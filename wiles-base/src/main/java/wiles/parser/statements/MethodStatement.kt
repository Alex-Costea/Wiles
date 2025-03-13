package wiles.parser.statements

import wiles.parser.builders.ExpectParamsBuilder.Companion.tokenOf
import wiles.parser.builders.ParserContext
import wiles.parser.enums.WhenRemoveToken
import wiles.parser.statements.expressions.TypeDefExpression
import wiles.shared.abstracts.AbstractStatement
import wiles.shared.constants.Predicates.IS_IDENTIFIER
import wiles.shared.constants.Tokens.ANON_ARG_ID
import wiles.shared.constants.Tokens.CONST_ID
import wiles.shared.constants.Tokens.DO_ID
import wiles.shared.constants.Tokens.FUNC_ID
import wiles.shared.constants.Tokens.PAREN_END_ID
import wiles.shared.constants.Tokens.PAREN_START_ID
import wiles.shared.constants.Tokens.PURE_ID
import wiles.shared.constants.Tokens.SEPARATOR_ID
import wiles.shared.constants.Tokens.START_BLOCK_ID
import wiles.shared.constants.Tokens.YIELDS_ID
import wiles.shared.data.WilesExceptionsCollection
import wiles.shared.enums.DeclarationType
import wiles.shared.enums.SyntaxType
import wiles.shared.errors.WilesException

class MethodStatement(oldContext : ParserContext)
    : AbstractStatement(oldContext.setWithinMethod(true)) {

    private val parameters: MutableList<DeclarationStatement> = ArrayList()
    private val exceptions: WilesExceptionsCollection = WilesExceptionsCollection()

    private var returnType: TypeDefExpression? = null
    private val methodBody: CodeBlockStatement = CodeBlockStatement(context)
    private var isTypeDefinition = false

    override val syntaxType: SyntaxType
        get() = SyntaxType.FUNC

    override fun getComponents(): MutableList<AbstractStatement> {
        val components = ArrayList<AbstractStatement>()
        if(returnType != null)
            components.add(returnType!!)
        components.addAll(parameters)
        if(!isTypeDefinition)
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

    override fun process(): WilesExceptionsCollection {
        try {
            transmitter.expectMaybe(tokenOf(DO_ID).or(START_BLOCK_ID).removeWhen(WhenRemoveToken.Never))
            location = transmitter.expect(tokenOf(FUNC_ID)).location

            val isPure = transmitter.expectMaybe(tokenOf(PURE_ID).dontskipNewLine())
            if(isPure.isPresent)
                name = PURE_ID

            //Params
            val parenStart = transmitter.expectMaybe(tokenOf(PAREN_START_ID).dontskipNewLine())
            if(parenStart.isPresent)
               readParams()

            //Return type
            if (transmitter.expectMaybe(tokenOf(YIELDS_ID).dontskipNewLine()).isPresent) {
                returnType = TypeDefExpression(context)
                exceptions.addAll(returnType!!.process())
            }

            //Read body
            if(transmitter.expectMaybe(tokenOf(DO_ID).or(START_BLOCK_ID).removeWhen(WhenRemoveToken.Never)
                .dontskipNewLine()).isPresent)
                exceptions.addAll(methodBody.process())
            else isTypeDefinition = true
        } catch (ex: WilesException) {
            exceptions.add(ex)
        }
        return exceptions
    }
}