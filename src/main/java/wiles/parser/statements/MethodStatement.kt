package wiles.parser.statements

import wiles.parser.builders.Context
import wiles.parser.builders.ExpectParamsBuilder.Companion.tokenOf
import wiles.parser.enums.WhenRemoveToken
import wiles.shared.AbstractCompilationException
import wiles.shared.CompilationExceptionsCollection
import wiles.shared.SyntaxType
import wiles.shared.constants.Predicates.IS_IDENTIFIER
import wiles.shared.constants.Tokens.ANON_ARG_ID
import wiles.shared.constants.Tokens.DO_ID
import wiles.shared.constants.Tokens.METHOD_ID
import wiles.shared.constants.Tokens.PAREN_END_ID
import wiles.shared.constants.Tokens.PAREN_START_ID
import wiles.shared.constants.Tokens.RIGHT_ARROW_ID
import wiles.shared.constants.Tokens.SEPARATOR_ID
import wiles.shared.constants.Tokens.START_BLOCK_ID

class MethodStatement(oldContext : Context, private val isTypeDeclaration: Boolean = false)
    : AbstractStatement(oldContext.setWithinMethod(true)) {

    private val parameters: MutableList<DeclarationStatement> = ArrayList()
    private val exceptions: CompilationExceptionsCollection = CompilationExceptionsCollection()

    private var returnType: TypeDefinitionStatement? = null
    private var methodBody: CodeBlockStatement = CodeBlockStatement(context)

    override val type: SyntaxType
        get() = SyntaxType.METHOD

    override fun getComponents(): MutableList<AbstractStatement> {
        val components = ArrayList<AbstractStatement>()
        if(returnType != null)
            components.add(returnType!!)
        components.addAll(parameters)
        if(!isTypeDeclaration)
            components.add(methodBody)
        return components
    }

    private fun readParams()
    {
        while(transmitter.expectMaybe(tokenOf(IS_IDENTIFIER).or(ANON_ARG_ID)
                .removeWhen(WhenRemoveToken.Never)).isPresent) {
            val parameterStatement = DeclarationStatement(context,true)
            exceptions.addAll(parameterStatement.process())
            parameters.add(parameterStatement)
            if (transmitter.expectMaybe(tokenOf(SEPARATOR_ID)).isEmpty) break
        }
        if(!isTypeDeclaration)
            transmitter.expect(tokenOf(PAREN_END_ID))
    }

    override fun process(): CompilationExceptionsCollection {
        try {
            val startWithCodeBlock = transmitter.expectMaybe(tokenOf(DO_ID).or(START_BLOCK_ID).removeWhen(WhenRemoveToken.Never))
            if(startWithCodeBlock.isEmpty) {
                if(!isTypeDeclaration)
                    transmitter.expect(tokenOf(METHOD_ID))

                //Params
                if(!isTypeDeclaration) {
                    transmitter.expect(tokenOf(PAREN_START_ID))
                    readParams()
                }
                else {
                    readParams()
                }

                //Return type
                if (transmitter.expectMaybe(tokenOf(RIGHT_ARROW_ID).dontIgnoreNewLine()).isPresent) {
                    returnType = TypeDefinitionStatement(context)
                    exceptions.addAll(returnType!!.process())
                }
            }
            else location = startWithCodeBlock.get().location
            //Read body
            if(!isTypeDeclaration)
                exceptions.addAll(methodBody.process())
        } catch (ex: AbstractCompilationException) {
            exceptions.add(ex)
        }
        return exceptions
    }
}