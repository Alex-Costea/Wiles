package `in`.costea.wiles.builders

import `in`.costea.wiles.builders.ExpectParamsBuilder.Companion.tokenOf
import `in`.costea.wiles.constants.ErrorMessages.INTERNAL_ERROR
import `in`.costea.wiles.constants.ErrorMessages.INVALID_STATEMENT_ERROR
import `in`.costea.wiles.constants.ErrorMessages.NOT_YET_IMPLEMENTED_ERROR
import `in`.costea.wiles.constants.Predicates.ANYTHING
import `in`.costea.wiles.constants.Predicates.START_OF_EXPRESSION
import `in`.costea.wiles.constants.Tokens.BREAK_ID
import `in`.costea.wiles.constants.Tokens.CONTINUE_ID
import `in`.costea.wiles.constants.Tokens.DECLARE_ID
import `in`.costea.wiles.constants.Tokens.DO_ID
import `in`.costea.wiles.constants.Tokens.IF_ID
import `in`.costea.wiles.constants.Tokens.METHOD_ID
import `in`.costea.wiles.constants.Tokens.RETURN_ID
import `in`.costea.wiles.constants.Tokens.START_BLOCK_ID
import `in`.costea.wiles.constants.Tokens.WHILE_ID
import `in`.costea.wiles.enums.WhenRemoveToken
import `in`.costea.wiles.exceptions.AbstractCompilationException
import `in`.costea.wiles.exceptions.InternalErrorException
import `in`.costea.wiles.exceptions.UnexpectedTokenException
import `in`.costea.wiles.services.TokenTransmitter
import `in`.costea.wiles.statements.*
import `in`.costea.wiles.statements.expressions.DefaultExpression
import `in`.costea.wiles.statements.expressions.TopLevelExpression
import java.util.function.Function

class StatementFactory {
    private val statements: MutableSet<Class<out AbstractStatement>> = HashSet()
    private lateinit var transmitter: TokenTransmitter
    private lateinit var context: Context
    fun addType(statement: Class<out AbstractStatement>): StatementFactory {
        if (!params.containsKey(statement)) throw InternalErrorException(NOT_YET_IMPLEMENTED_ERROR)
        statements.add(statement)
        return this
    }

    fun setContext(context: Context) : StatementFactory
    {
        this.context=context
        this.transmitter=context.transmitter
        return this
    }

    @JvmOverloads
    @Throws(AbstractCompilationException::class)
    fun create(errorMessage: String = INTERNAL_ERROR): AbstractStatement {
        for (statement in statements) {
            if (!context.isWithinMethod && statement == ReturnStatement::class.java) continue
            if (!context.isWithinLoop && statement == ContinueStatement::class.java) continue
            if (transmitter.expectMaybe(params[statement]!!).isPresent) return createObject[statement]!!
                .apply(context)
        }

        //Expression not found
        val paramsBuilder = tokenOf(ANYTHING).removeWhen(WhenRemoveToken.Never)
            .withErrorMessage(errorMessage)
        val (_, location) = transmitter.expect(paramsBuilder)
        throw UnexpectedTokenException(INVALID_STATEMENT_ERROR, location)
    }

    companion object {
        private val params: HashMap<Class<out AbstractStatement>, ExpectParamsBuilder> =
            HashMap()
        private val createObject: HashMap<Class<out AbstractStatement>, Function<Context, AbstractStatement>> =
            HashMap()

        init {
            params[TopLevelExpression::class.java] = START_OF_EXPRESSION
            params[DefaultExpression::class.java] = START_OF_EXPRESSION
            params[DeclarationStatement::class.java] = tokenOf(DECLARE_ID)
            params[MethodStatement::class.java] = tokenOf(METHOD_ID)
            params[ReturnStatement::class.java] = tokenOf(RETURN_ID)
            params[IfStatement::class.java] = tokenOf(IF_ID)
            params[WhileStatement::class.java] = tokenOf(WHILE_ID)
            params[BreakStatement::class.java] = tokenOf(BREAK_ID)
            params[ContinueStatement::class.java] = tokenOf(CONTINUE_ID)
            params[CodeBlockStatement::class.java] = tokenOf(DO_ID).or(START_BLOCK_ID).removeWhen(WhenRemoveToken.Never)
            createObject[TopLevelExpression::class.java] =
                Function { context: Context -> TopLevelExpression(context) }
            createObject[DefaultExpression::class.java] =
                Function { context: Context -> DefaultExpression(context) }
            createObject[DeclarationStatement::class.java] =
                Function { context: Context -> DeclarationStatement(context) }
            createObject[MethodStatement::class.java] =
                Function { context: Context -> MethodStatement(context) }
            createObject[ReturnStatement::class.java] =
                Function { context: Context -> ReturnStatement(context) }
            createObject[IfStatement::class.java] =
                Function { context: Context -> IfStatement(context) }
            createObject[WhileStatement::class.java] =
                Function { context: Context -> WhileStatement(context) }
            createObject[BreakStatement::class.java] =
                Function { context: Context -> BreakStatement(context) }
            createObject[ContinueStatement::class.java] =
                Function { context: Context -> ContinueStatement(context) }
            createObject[CodeBlockStatement::class.java] =
                Function { context : Context -> CodeBlockStatement(context) }
        }
    }
}