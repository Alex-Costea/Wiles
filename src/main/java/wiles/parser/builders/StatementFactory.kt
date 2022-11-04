package wiles.parser.builders

import wiles.parser.builders.ExpectParamsBuilder.Companion.tokenOf
import wiles.parser.constants.ErrorMessages.INTERNAL_ERROR
import wiles.parser.constants.ErrorMessages.INVALID_STATEMENT_ERROR
import wiles.parser.constants.ErrorMessages.NOT_YET_IMPLEMENTED_ERROR
import wiles.parser.constants.Predicates.ANYTHING
import wiles.parser.constants.Predicates.START_OF_EXPRESSION_NO_CODE_BLOCK
import wiles.parser.constants.Tokens.BRACKET_START_ID
import wiles.parser.constants.Tokens.BREAK_ID
import wiles.parser.constants.Tokens.CONTINUE_ID
import wiles.parser.constants.Tokens.DECLARE_ID
import wiles.parser.constants.Tokens.DO_ID
import wiles.parser.constants.Tokens.METHOD_ID
import wiles.parser.constants.Tokens.RETURN_ID
import wiles.parser.constants.Tokens.START_BLOCK_ID
import wiles.parser.constants.Tokens.WHEN_ID
import wiles.parser.constants.Tokens.WHILE_ID
import wiles.parser.enums.WhenRemoveToken
import wiles.parser.exceptions.AbstractCompilationException
import wiles.parser.exceptions.UnexpectedTokenException
import wiles.parser.services.TokenTransmitter
import wiles.parser.statements.*
import wiles.parser.statements.expressions.DefaultExpression
import wiles.parser.statements.expressions.TopLevelExpression
import java.util.function.Function

class StatementFactory {
    private val statements: MutableSet<Class<out AbstractStatement>> = HashSet()
    private lateinit var transmitter: TokenTransmitter
    private lateinit var context: Context
    fun addType(statement: Class<out AbstractStatement>): StatementFactory {
        if (!params.containsKey(statement)) throw wiles.parser.exceptions.InternalErrorException(
            NOT_YET_IMPLEMENTED_ERROR
        )
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
            params[TopLevelExpression::class.java] = START_OF_EXPRESSION_NO_CODE_BLOCK
            params[DefaultExpression::class.java] = START_OF_EXPRESSION_NO_CODE_BLOCK
            params[DeclarationStatement::class.java] = tokenOf(DECLARE_ID)
            params[MethodStatement::class.java] = tokenOf(METHOD_ID)
                .or(DO_ID).or(START_BLOCK_ID).removeWhen(WhenRemoveToken.Never)
            params[ReturnStatement::class.java] = tokenOf(RETURN_ID)
            params[WhenStatement::class.java] = tokenOf(WHEN_ID)
            params[WhileStatement::class.java] = tokenOf(WHILE_ID)
            params[BreakStatement::class.java] = tokenOf(BREAK_ID)
            params[ContinueStatement::class.java] = tokenOf(CONTINUE_ID)
            params[ListStatement::class.java] = tokenOf(BRACKET_START_ID)
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
            createObject[WhenStatement::class.java] =
                Function { context: Context -> WhenStatement(context) }
            createObject[WhileStatement::class.java] =
                Function { context: Context -> WhileStatement(context) }
            createObject[BreakStatement::class.java] =
                Function { context: Context -> BreakStatement(context) }
            createObject[ContinueStatement::class.java] =
                Function { context: Context -> ContinueStatement(context) }
            createObject[ListStatement::class.java] =
                Function { context : Context -> ListStatement(context) }
        }
    }
}