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
import wiles.parser.constants.Tokens.FOR_ID
import wiles.parser.constants.Tokens.METHOD_ID
import wiles.parser.constants.Tokens.RETURN_ID
import wiles.parser.constants.Tokens.START_BLOCK_ID
import wiles.parser.constants.Tokens.WHEN_ID
import wiles.parser.constants.Tokens.WHILE_ID
import wiles.parser.enums.StatementFactoryTypes
import wiles.parser.enums.WhenRemoveToken
import wiles.parser.exceptions.AbstractCompilationException
import wiles.parser.exceptions.InternalErrorException
import wiles.parser.exceptions.UnexpectedTokenException
import wiles.parser.services.TokenTransmitter
import wiles.parser.statements.*
import wiles.parser.statements.expressions.DefaultExpression
import wiles.parser.statements.expressions.TopLevelExpression
import java.util.function.Function

class StatementFactory {
    private val statements: MutableSet<StatementFactoryTypes> = HashSet()
    private lateinit var transmitter: TokenTransmitter
    private lateinit var context: Context
    fun addType(statement: StatementFactoryTypes): StatementFactory {
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
            if (!context.isWithinMethod && statement == StatementFactoryTypes.RETURN_STATEMENT) continue
            if (!context.isWithinLoop && statement == StatementFactoryTypes.CONTINUE_STATEMENT) continue
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
        private val params: HashMap<StatementFactoryTypes, ExpectParamsBuilder> = HashMap()
        private val createObject: HashMap<StatementFactoryTypes, Function<Context, AbstractStatement>> = HashMap()

        init {
            params[StatementFactoryTypes.TOP_LEVEL_EXPRESSION] = START_OF_EXPRESSION_NO_CODE_BLOCK
            params[StatementFactoryTypes.DECLARATION_STATEMENT] = tokenOf(DECLARE_ID)
            params[StatementFactoryTypes.METHOD_STATEMENT] = tokenOf(METHOD_ID).or(DO_ID).or(START_BLOCK_ID)
                .removeWhen(WhenRemoveToken.Never)
            params[StatementFactoryTypes.RETURN_STATEMENT] = tokenOf(RETURN_ID)
            params[StatementFactoryTypes.WHEN_STATEMENT] = tokenOf(WHEN_ID)
            params[StatementFactoryTypes.WHILE_STATEMENT] = tokenOf(WHILE_ID)
            params[StatementFactoryTypes.BREAK_STATEMENT] = tokenOf(BREAK_ID)
            params[StatementFactoryTypes.CONTINUE_STATEMENT] = tokenOf(CONTINUE_ID)
            params[StatementFactoryTypes.LIST_STATEMENT] = tokenOf(BRACKET_START_ID)
            params[StatementFactoryTypes.FOR_STATEMENT] = tokenOf(FOR_ID)
            createObject[StatementFactoryTypes.TOP_LEVEL_EXPRESSION] =
                Function { context: Context -> TopLevelExpression(context) }
            createObject[StatementFactoryTypes.DEFAULT_EXPRESSION_NO_CODE_BLOCK] =
                Function { context: Context -> DefaultExpression(context) }
            createObject[StatementFactoryTypes.DECLARATION_STATEMENT] =
                Function { context: Context -> DeclarationStatement(context) }
            createObject[StatementFactoryTypes.METHOD_STATEMENT] =
                Function { context: Context -> MethodStatement(context) }
            createObject[StatementFactoryTypes.RETURN_STATEMENT] =
                Function { context: Context -> ReturnStatement(context) }
            createObject[StatementFactoryTypes.WHEN_STATEMENT] =
                Function { context: Context -> WhenStatement(context) }
            createObject[StatementFactoryTypes.WHILE_STATEMENT] =
                Function { context: Context -> WhileStatement(context) }
            createObject[StatementFactoryTypes.BREAK_STATEMENT] =
                Function { context: Context -> BreakStatement(context) }
            createObject[StatementFactoryTypes.CONTINUE_STATEMENT] =
                Function { context: Context -> ContinueStatement(context) }
            createObject[StatementFactoryTypes.LIST_STATEMENT] =
                Function { context : Context -> ListStatement(context) }
            createObject[StatementFactoryTypes.FOR_STATEMENT] =
                Function { context : Context -> ForStatement(context) }
        }
    }
}