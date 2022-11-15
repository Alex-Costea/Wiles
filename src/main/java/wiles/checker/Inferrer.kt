package wiles.checker

import wiles.checker.exceptions.TypeInferenceException
import wiles.parser.data.Token
import wiles.parser.statements.AbstractStatement
import wiles.parser.statements.TokenStatement
import wiles.parser.statements.expressions.AbstractExpression
import wiles.shared.InternalErrorException
import wiles.shared.TokenLocation
import wiles.shared.constants.Chars.DECIMAL_DELIMITER
import wiles.shared.constants.ErrorMessages.INFERENCE_ERROR
import wiles.shared.constants.Predicates.IS_IDENTIFIER
import wiles.shared.constants.Predicates.IS_NUMBER_LITERAL
import wiles.shared.constants.Predicates.IS_TEXT_LITERAL
import wiles.shared.constants.Types.DOUBLE_ID
import wiles.shared.constants.Types.INT64_ID
import wiles.shared.constants.Types.STRING_ID


//NOTE: this needs to be run even if types don't necessitate inferring, as it also checks whether identifiers are valid
class Inferrer(private val checker: Checker){

    private fun fromToken(token : Token) : TypeDefinition
    {
        val name = token.content
        if (IS_TEXT_LITERAL.test(name))
            return TypeDefinition(STRING_ID)
        if (IS_NUMBER_LITERAL.test(name))
        {
            if(name.contains(DECIMAL_DELIMITER))
                return TypeDefinition(DOUBLE_ID)
            return TypeDefinition(INT64_ID)
        }
        if(IS_IDENTIFIER.test(name))
            return checker.getTypeOfIdentifier(token)
        throw TypeInferenceException(INFERENCE_ERROR, token.location)
    }

    fun fromExpression(expression: AbstractStatement?): TypeDefinition? {
        if(expression == null)
            return null
        val newLocation : TokenLocation
        when (expression.getComponents().size)
        {
            0 -> return fromToken((expression as TokenStatement).token)
            1 -> {
                val component = expression.getComponents()[0]
                if(component is TokenStatement)
                    return fromToken(component.token)
                TODO()
            }
            2, 3 -> {
                if(expression !is AbstractExpression)
                    throw InternalErrorException()
                newLocation = expression.operation.token.location
                val resultInferrer = ResultInferrer(newLocation)
                return resultInferrer.results(
                    fromExpression(expression.left),
                    expression.operation.name,
                    fromExpression(expression.right)!!
                )
            }
            else -> throw InternalErrorException()
        }
    }
}