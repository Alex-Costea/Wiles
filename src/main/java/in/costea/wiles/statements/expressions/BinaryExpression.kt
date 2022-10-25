package `in`.costea.wiles.statements.expressions

import `in`.costea.wiles.builders.Context
import `in`.costea.wiles.constants.ErrorMessages.CANNOT_BE_PROCESSED_ERROR
import `in`.costea.wiles.data.CompilationExceptionsCollection
import `in`.costea.wiles.exceptions.InternalErrorException
import `in`.costea.wiles.statements.AbstractStatement
import `in`.costea.wiles.statements.TokenStatement

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
        throw InternalErrorException(CANNOT_BE_PROCESSED_ERROR)
    }
}