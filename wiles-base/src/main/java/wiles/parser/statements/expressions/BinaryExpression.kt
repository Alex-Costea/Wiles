package wiles.parser.statements.expressions

import wiles.parser.builders.ParserContext
import wiles.shared.constants.ErrorMessages.CANNOT_BE_PROCESSED_ERROR
import wiles.shared.WilesExceptionsCollection
import wiles.shared.InternalErrorException
import wiles.shared.AbstractStatement
import wiles.parser.statements.TokenStatement

class BinaryExpression(
    operation: TokenStatement?,
    left: AbstractStatement?,
    right: AbstractStatement,
    context: ParserContext
) : AbstractExpression(context) {
    init {
        this.left=left
        this.operation=operation
        this.right=right
    }

    override fun process(): WilesExceptionsCollection {
        throw InternalErrorException(CANNOT_BE_PROCESSED_ERROR)
    }
}