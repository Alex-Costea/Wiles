package wiles.parser.builders

import wiles.parser.builders.ExpectParamsBuilder.Companion.tokenOf
import wiles.parser.enums.StatementFactoryTypes
import wiles.parser.enums.WhenRemoveToken
import wiles.parser.exceptions.UnexpectedTokenException
import wiles.parser.services.TokenTransmitter
import wiles.parser.statements.*
import wiles.parser.statements.expressions.DefaultExpression
import wiles.parser.statements.expressions.TopLevelExpression
import wiles.shared.AbstractCompilationException
import wiles.shared.InternalErrorException
import wiles.shared.constants.ErrorMessages.INTERNAL_ERROR
import wiles.shared.constants.ErrorMessages.INVALID_STATEMENT_ERROR
import wiles.shared.constants.ErrorMessages.NOT_YET_IMPLEMENTED_ERROR
import wiles.shared.constants.Predicates.ANYTHING
import wiles.shared.constants.Predicates.START_OF_EXPRESSION
import wiles.shared.constants.Tokens.DATA_START_ID
import wiles.shared.constants.Tokens.DICT_START_ID
import wiles.shared.constants.Tokens.BRACKET_START_ID
import wiles.shared.constants.Tokens.BREAK_ID
import wiles.shared.constants.Tokens.CONTINUE_ID
import wiles.shared.constants.Tokens.DECLARE_ID
import wiles.shared.constants.Tokens.DO_ID
import wiles.shared.constants.Tokens.FOR_ID
import wiles.shared.constants.Tokens.FUNC_ID
import wiles.shared.constants.Tokens.IF_ID
import wiles.shared.constants.Tokens.RETURN_ID
import wiles.shared.constants.Tokens.START_BLOCK_ID
import wiles.shared.constants.Tokens.TYPE_ID
import wiles.shared.constants.Tokens.WHILE_ID
import java.util.function.Function

class StatementFactory {
    private val statements = LinkedHashSet<StatementFactoryTypes>()
    private lateinit var transmitter: TokenTransmitter
    private lateinit var context: ParserContext
    fun addType(statement: StatementFactoryTypes): StatementFactory {
        if (!params.containsKey(statement)) throw InternalErrorException(
            NOT_YET_IMPLEMENTED_ERROR
        )
        statements.add(statement)
        return this
    }

    fun setContext(context: ParserContext) : StatementFactory
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
            if (!context.isWithinLoop && statement == StatementFactoryTypes.BREAK_STATEMENT) continue
            if (transmitter.expectMaybe(params[statement]!!).isPresent) return createObject[statement]!!.apply(context)
        }

        //Expression not found
        val paramsBuilder = tokenOf(ANYTHING).removeWhen(WhenRemoveToken.Never)
            .withErrorMessage(errorMessage)
        val (_, location) = transmitter.expect(paramsBuilder)
        throw UnexpectedTokenException(INVALID_STATEMENT_ERROR, location)
    }

    companion object {
        private val params = LinkedHashMap<StatementFactoryTypes, ExpectParamsBuilder>()
        private val createObject = LinkedHashMap<StatementFactoryTypes, Function<ParserContext, AbstractStatement>>()

        init {
            params[StatementFactoryTypes.TOP_LEVEL_EXPRESSION] = START_OF_EXPRESSION
            params[StatementFactoryTypes.DECLARATION_STATEMENT] = tokenOf(DECLARE_ID)
            params[StatementFactoryTypes.FUNC_STATEMENT] = tokenOf(FUNC_ID).or(DO_ID).or(START_BLOCK_ID)
                .removeWhen(WhenRemoveToken.Never)
            params[StatementFactoryTypes.RETURN_STATEMENT] = tokenOf(RETURN_ID)
            params[StatementFactoryTypes.IF_STATEMENT] = tokenOf(IF_ID).removeWhen(WhenRemoveToken.Never)
            params[StatementFactoryTypes.WHILE_STATEMENT] = tokenOf(WHILE_ID)
            params[StatementFactoryTypes.BREAK_STATEMENT] = tokenOf(BREAK_ID)
            params[StatementFactoryTypes.CONTINUE_STATEMENT] = tokenOf(CONTINUE_ID)
            params[StatementFactoryTypes.LIST_STATEMENT] = tokenOf(BRACKET_START_ID)
            params[StatementFactoryTypes.FOR_STATEMENT] = tokenOf(FOR_ID)
            params[StatementFactoryTypes.DICT_STATEMENT] = tokenOf(DICT_START_ID)
            params[StatementFactoryTypes.DATA_STATEMENT] = tokenOf(DATA_START_ID)
            params[StatementFactoryTypes.TYPE_LITERAL] = tokenOf(TYPE_ID)
            createObject[StatementFactoryTypes.TOP_LEVEL_EXPRESSION] =
                Function { context: ParserContext -> TopLevelExpression(context) }
            createObject[StatementFactoryTypes.DEFAULT_EXPRESSION_NO_CODE_BLOCK] =
                Function { context: ParserContext -> DefaultExpression(context) }
            createObject[StatementFactoryTypes.DECLARATION_STATEMENT] =
                Function { context: ParserContext -> DeclarationStatement(context) }
            createObject[StatementFactoryTypes.FUNC_STATEMENT] =
                Function { context: ParserContext -> MethodStatement(context) }
            createObject[StatementFactoryTypes.RETURN_STATEMENT] =
                Function { context: ParserContext -> ReturnStatement(context) }
            createObject[StatementFactoryTypes.IF_STATEMENT] =
                Function { context: ParserContext -> IfStatement(context) }
            createObject[StatementFactoryTypes.WHILE_STATEMENT] =
                Function { context: ParserContext -> WhileStatement(context) }
            createObject[StatementFactoryTypes.BREAK_STATEMENT] =
                Function { context: ParserContext -> BreakStatement(context) }
            createObject[StatementFactoryTypes.CONTINUE_STATEMENT] =
                Function { context: ParserContext -> ContinueStatement(context) }
            createObject[StatementFactoryTypes.LIST_STATEMENT] =
                Function { context : ParserContext -> ListStatement(context) }
            createObject[StatementFactoryTypes.FOR_STATEMENT] =
                Function { context : ParserContext -> ForStatement(context) }
            createObject[StatementFactoryTypes.DICT_STATEMENT] =
                Function { context : ParserContext -> DictStatement(context) }
            createObject[StatementFactoryTypes.DATA_STATEMENT] =
                Function { context : ParserContext -> DataStatement(context) }
            createObject[StatementFactoryTypes.TYPE_LITERAL] =
                Function { context : ParserContext -> TypeLiteralStatement(context) }
        }
    }
}