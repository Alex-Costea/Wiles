package wiles.checker

import wiles.checker.exceptions.TypeInferenceException
import wiles.parser.data.Token
import wiles.parser.statements.AbstractStatement
import wiles.parser.statements.TokenStatement
import wiles.shared.TokenLocation
import wiles.shared.constants.Chars.DECIMAL_DELIMITER
import wiles.shared.constants.ErrorMessages.INFERENCE_ERROR
import wiles.shared.constants.Predicates.IS_NUMBER_LITERAL
import wiles.shared.constants.Predicates.IS_TEXT_LITERAL
import wiles.shared.constants.Tokens.ERROR_TOKEN
import wiles.shared.constants.Types.DOUBLE_ID
import wiles.shared.constants.Types.INT64_ID
import wiles.shared.constants.Types.STRING_ID

class Inferrer(private val checker: Checker){

    private fun fromToken(token : Token) : String
    {
        val name = token.content
        if (IS_TEXT_LITERAL.test(name))
            return STRING_ID
        if (IS_NUMBER_LITERAL.test(name))
        {
            if(name.contains(DECIMAL_DELIMITER))
                return DOUBLE_ID
            return INT64_ID
        }
        return checker.getTypeOfIdentifier(token)
    }

    fun fromExpression(expression: AbstractStatement?, location: TokenLocation): String {
        if(expression == null)
            return ERROR_TOKEN
        val newLocation : TokenLocation
        when (expression.getComponents().size)
        {
            0 ->{
                return fromToken((expression as TokenStatement).token)
            }
            1 -> {
                val component = expression.getComponents()[0]
                if(component is TokenStatement)
                    return fromToken(component.token)
            }
            2 -> {
                newLocation = (expression.getComponents()[0] as TokenStatement).token.location
                val resultInferrer = ResultInferrer(newLocation)
                return resultInferrer.results(
                    null,
                    expression.getComponents()[0].name,
                    fromExpression(expression.getComponents()[1], newLocation)
                )
            }
            3 -> {
                newLocation = (expression.getComponents()[1] as TokenStatement).token.location
                val resultInferrer = ResultInferrer(newLocation)
                return resultInferrer.results(
                    fromExpression(expression.getComponents()[0], newLocation),
                    expression.getComponents()[1].name,
                    fromExpression(expression.getComponents()[2], newLocation)
                )
            }
            else -> throw InternalError()
        }
        throw TypeInferenceException(INFERENCE_ERROR, location)
    }
}