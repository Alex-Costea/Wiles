package wiles.parser.statements

import wiles.parser.builders.ExpectParamsBuilder.Companion.tokenOf
import wiles.parser.builders.ParserContext
import wiles.parser.enums.WhenRemoveToken
import wiles.parser.statements.expressions.TypeDefExpression
import wiles.shared.AbstractCompilationException
import wiles.shared.CompilationExceptionsCollection
import wiles.shared.DeclarationType
import wiles.shared.SyntaxType
import wiles.shared.constants.Predicates.IS_IDENTIFIER
import wiles.shared.constants.Tokens.ANON_ARG_ID
import wiles.shared.constants.Tokens.CONST_ID
import wiles.shared.constants.Tokens.DO_ID
import wiles.shared.constants.Tokens.FUNC_ID
import wiles.shared.constants.Tokens.PAREN_END_ID
import wiles.shared.constants.Tokens.PAREN_START_ID
import wiles.shared.constants.Tokens.SEPARATOR_ID
import wiles.shared.constants.Tokens.START_BLOCK_ID
import wiles.shared.constants.Tokens.TYPE_ID
import wiles.shared.constants.Tokens.YIELDS_ID

class MethodStatement(oldContext : ParserContext)
    : AbstractStatement(oldContext.setWithinMethod(true).setWithinLoop(false)) {

    private val parameters: MutableList<DeclarationStatement> = ArrayList()
    private val exceptions: CompilationExceptionsCollection = CompilationExceptionsCollection()

    private var returnType: TypeDefExpression? = null
    private val methodBody: CodeBlockStatement = CodeBlockStatement(context)

    override val syntaxType: SyntaxType
        get() = SyntaxType.FUNC

    override fun getComponents(): MutableList<AbstractStatement> {
        val components = ArrayList<AbstractStatement>()
        if(returnType != null)
            components.add(returnType!!)
        components.addAll(parameters)
        if(name != TYPE_ID)
            components.add(methodBody)
        return components
    }

    private fun readParams()
    {
        while(transmitter.expectMaybe(tokenOf(IS_IDENTIFIER).or(ANON_ARG_ID).or(CONST_ID)
                .removeWhen(WhenRemoveToken.Never)).isPresent) {
            val parameterStatement = DeclarationStatement(context, DeclarationType.FUNC_PARAM)
            exceptions.addAll(parameterStatement.process())
            parameters.add(parameterStatement)
            if (transmitter.expectMaybe(tokenOf(SEPARATOR_ID)).isEmpty) break
        }
        transmitter.expect(tokenOf(PAREN_END_ID))
    }

    override fun process(): CompilationExceptionsCollection {
        try {
            val startWithCodeBlock = transmitter.expectMaybe(tokenOf(DO_ID).or(START_BLOCK_ID).removeWhen(WhenRemoveToken.Never))
            if(startWithCodeBlock.isEmpty) {
                location = transmitter.expect(tokenOf(FUNC_ID)).location

                //Params
                transmitter.expect(tokenOf(PAREN_START_ID))
                readParams()

                //Return type
                if (transmitter.expectMaybe(tokenOf(YIELDS_ID).dontIgnoreNewLine()).isPresent) {
                    returnType = TypeDefExpression(context)
                    exceptions.addAll(returnType!!.process())
                }
            }
            else location = startWithCodeBlock.get().location
            //Read body
            if(transmitter.expectMaybe(tokenOf(DO_ID).or(START_BLOCK_ID).removeWhen(WhenRemoveToken.Never)).isPresent)
                exceptions.addAll(methodBody.process())
            else name = TYPE_ID
        } catch (ex: AbstractCompilationException) {
            exceptions.add(ex)
        }
        return exceptions
    }
}