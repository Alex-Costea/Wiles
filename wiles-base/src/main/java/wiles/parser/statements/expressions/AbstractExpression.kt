package wiles.parser.statements.expressions

import wiles.parser.builders.ExpectParamsBuilder.Companion.tokenOf
import wiles.parser.builders.ParserContext
import wiles.parser.builders.StatementFactory
import wiles.parser.enums.ExpectNext
import wiles.parser.enums.StatementFactoryTypes
import wiles.parser.exceptions.UnexpectedEndException
import wiles.parser.services.PrecedenceProcessor
import wiles.parser.statements.AbstractStatement
import wiles.parser.statements.MethodCallStatement
import wiles.parser.statements.TokenStatement
import wiles.shared.AbstractCompilationException
import wiles.shared.CompilationExceptionsCollection
import wiles.shared.SyntaxType
import wiles.shared.Token
import wiles.shared.constants.ErrorMessages.EXPRESSION_UNFINISHED_ERROR
import wiles.shared.constants.ErrorMessages.UNEXPECTED_OPENING_PAREN_ERROR
import wiles.shared.constants.Predicates.EXPECT_OPERATOR
import wiles.shared.constants.Predicates.EXPECT_TOKEN
import wiles.shared.constants.Predicates.FINALIZE_EXPRESSION
import wiles.shared.constants.Predicates.IS_CONTAINED_IN
import wiles.shared.constants.Predicates.STARTS_AS_TOKEN
import wiles.shared.constants.Predicates.START_OF_EXPRESSION
import wiles.shared.constants.Tokens.APPLY_ID
import wiles.shared.constants.Tokens.INFIX_OPERATORS
import wiles.shared.constants.Tokens.KEYWORDS_INDICATING_NEW_EXPRESSION
import wiles.shared.constants.Tokens.PAREN_END_ID
import wiles.shared.constants.Tokens.PAREN_START_ID
import wiles.shared.constants.Tokens.STARTING_OPERATORS
import wiles.shared.constants.Tokens.SUFFIX_OPERATORS
import wiles.shared.constants.Tokens.UNARY_ID
import java.util.*

abstract class AbstractExpression protected constructor(context: ParserContext) :
    AbstractStatement(context) {
    @JvmField
    protected val exceptions: CompilationExceptionsCollection = CompilationExceptionsCollection()
    @JvmField
    var left: AbstractStatement? = null
    @JvmField
    var operation: TokenStatement? = null
    @JvmField
    var right: AbstractStatement? = null
    private val specialStatementFactory = StatementFactory().setContext(context)
        .addType(StatementFactoryTypes.LIST_STATEMENT).addType(StatementFactoryTypes.DICT_STATEMENT)
        .addType(StatementFactoryTypes.DATA_STATEMENT).addType(StatementFactoryTypes.TYPE_LITERAL)
        .addType(StatementFactoryTypes.FUNC_STATEMENT)
    @JvmField
    protected var isInner: Boolean = false

    override val syntaxType: SyntaxType
        get() = SyntaxType.EXPRESSION

    override fun getComponents(): MutableList<AbstractStatement> {
        val components = ArrayList<AbstractStatement>()
        if (left != null) components.add(left!!)
        if (operation != null) components.add(operation!!)
        else assert(left == null)
        if (right != null) components.add(right!!)
        return components
    }

    @Throws(AbstractCompilationException::class)
    protected open fun handleToken(token: Token): Boolean {
        return KEYWORDS_INDICATING_NEW_EXPRESSION.contains(token.content)
    }

    protected open fun setComponents(precedenceProcessor: PrecedenceProcessor) {
        val result = precedenceProcessor.getResult()
        if (result is AbstractExpression) {
            this.left = result.left
            this.operation = result.operation
            this.right = result.right
            //Right cannot be null. If left is null, operation must also be null
            assert(operation != null || left == null)
            checkNotNull(right)
            return
        }
        //Is not flattenable
        this.right = result
    }

    private fun handleSpecialStatements(): Optional<AbstractStatement> {
        return try {
            Optional.of(specialStatementFactory.create())
        } catch (e: AbstractCompilationException) {
            Optional.empty()
        }
    }

    override fun process(): CompilationExceptionsCollection {
        try {
            var mainCurrentToken = transmitter.expect(START_OF_EXPRESSION)
            val precedenceProcessor = PrecedenceProcessor(context)
            var maybeTempToken: Optional<Token>
            val content = mainCurrentToken.content

            //Decide what token to expect first
            var expectNext: ExpectNext
            expectNext = if (STARTS_AS_TOKEN.test(content)) ExpectNext.TOKEN
            else ExpectNext.OPERATOR

            while (!transmitter.tokensExhausted()) {
                //Finalize expression
                //It finalizes on keywords that correspond to the start of the next statement for better error messages
                if ((expectNext == ExpectNext.OPERATOR)) {
                    maybeTempToken = transmitter.expectMaybe(FINALIZE_EXPRESSION)
                    if (maybeTempToken.isPresent) {
                        mainCurrentToken = maybeTempToken.get()
                        if (handleToken(mainCurrentToken)) break
                    }
                }

                //Handle method calls and inner expressions
                maybeTempToken = transmitter.expectMaybe(tokenOf(PAREN_START_ID))
                if (maybeTempToken.isPresent) {
                    if (expectNext == ExpectNext.OPERATOR) { //Method call
                        precedenceProcessor.add(
                            TokenStatement(
                                Token(
                                    APPLY_ID, maybeTempToken.get()
                                        .location
                                ), context
                            )
                        )
                        val newExpression = MethodCallStatement(context)
                        newExpression.process().throwFirstIfExists()
                        precedenceProcessor.add(newExpression)
                        continue
                    }
                    //Inner expressions
                    val newExpression = InnerExpression(context)
                    newExpression.process().throwFirstIfExists()
                    precedenceProcessor.add(newExpression)
                    expectNext = ExpectNext.OPERATOR
                    continue
                }

                //Special statements
                if (expectNext == ExpectNext.TOKEN) {
                    val maybeStatement = handleSpecialStatements()
                    if (maybeStatement.isPresent) {
                        val statement = maybeStatement.get()
                        exceptions.addAll(statement.process())
                        precedenceProcessor.add(statement)
                        expectNext = ExpectNext.OPERATOR
                        continue
                    }
                }

                //Handle suffix unary operators
                if(expectNext == ExpectNext.OPERATOR)
                {
                    maybeTempToken = transmitter.expectMaybe(tokenOf(IS_CONTAINED_IN.invoke(SUFFIX_OPERATORS)))
                    if(maybeTempToken.isPresent) {
                        mainCurrentToken = maybeTempToken.get()
                        precedenceProcessor.add(TokenStatement(mainCurrentToken, context))
                        continue
                    }
                }

                //Handle prefix unary operators
                if (expectNext == ExpectNext.TOKEN) {
                    maybeTempToken = transmitter.expectMaybe(tokenOf(IS_CONTAINED_IN.invoke(STARTING_OPERATORS)))
                    if (maybeTempToken.isPresent) {
                        mainCurrentToken = maybeTempToken.get()
                        if (INFIX_OPERATORS.contains(mainCurrentToken.content)) mainCurrentToken = Token(
                            UNARY_ID + mainCurrentToken.content,
                            mainCurrentToken.location
                        )
                        precedenceProcessor.add(TokenStatement(mainCurrentToken, context))
                        continue
                    }
                }

                //Expect the next token
                mainCurrentToken = if (expectNext == ExpectNext.OPERATOR) transmitter.expect(EXPECT_OPERATOR)
                else transmitter.expect(EXPECT_TOKEN)

                //Add token and change next expected token
                precedenceProcessor.add(TokenStatement(mainCurrentToken, context))
                expectNext = if (expectNext == ExpectNext.OPERATOR) ExpectNext.TOKEN else ExpectNext.OPERATOR
            }

            //Final processing
            if (expectNext == ExpectNext.TOKEN) throw UnexpectedEndException(
                EXPRESSION_UNFINISHED_ERROR,
                mainCurrentToken.location
            )
            if (this is InnerExpression && mainCurrentToken.content != PAREN_END_ID) throw UnexpectedEndException(
                UNEXPECTED_OPENING_PAREN_ERROR,
                transmitter.lastLocation
            )
            setComponents(precedenceProcessor)
        } catch (ex: AbstractCompilationException) {
            exceptions.add(ex)
        }
        return exceptions
    }
}
