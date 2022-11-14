package wiles.checker

import wiles.checker.exceptions.TypeInferenceException
import wiles.parser.statements.DeclarationStatement
import wiles.shared.TokenLocation
import wiles.shared.constants.Chars.DECIMAL_DELIMITER
import wiles.shared.constants.ErrorMessages.INFERENCE_ERROR
import wiles.shared.constants.Predicates
import wiles.shared.constants.Tokens
import wiles.shared.constants.Types

object Inferrer {
    fun fromDeclaration(component: DeclarationStatement, location: TokenLocation): String {
        val right = component.right ?: return Tokens.ERROR_TOKEN
        if(right.getComponents().size == 1) {
            val name = right.getComponents()[0].name
            if (Predicates.IS_TEXT_LITERAL.test(name))
                return Types.STRING_ID
            if(Predicates.IS_NUMBER_LITERAL.test(name))
            {
                if(name.contains(DECIMAL_DELIMITER))
                    return Types.DOUBLE_ID
                return Types.INT64_ID
            }
        }
        throw TypeInferenceException(INFERENCE_ERROR, location)
    }
}