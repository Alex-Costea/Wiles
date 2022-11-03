package wiles.parser.statements.expressions

import wiles.parser.builders.Context
import wiles.parser.constants.ErrorMessages.CANNOT_BE_PROCESSED_ERROR
import wiles.parser.data.CompilationExceptionsCollection
import wiles.parser.statements.AbstractStatement
import wiles.parser.statements.TokenStatement

class BinaryExpression(
    operation: TokenStatement?,
    left: AbstractStatement?,
    right: AbstractStatement,
    context: Context
) : AbstractExpression(context) {
    init {
        this.left=left
        this.operation=operation
        this.right=right
    }

    override fun process(): CompilationExceptionsCollection {
        throw wiles.parser.exceptions.InternalErrorException(CANNOT_BE_PROCESSED_ERROR)
    }
}