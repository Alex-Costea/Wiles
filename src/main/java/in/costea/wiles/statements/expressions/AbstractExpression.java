package in.costea.wiles.statements.expressions;

import in.costea.wiles.builders.IsWithin;
import in.costea.wiles.data.CompilationExceptionsCollection;
import in.costea.wiles.data.Token;
import in.costea.wiles.enums.ExpectNext;
import in.costea.wiles.enums.SyntaxType;
import in.costea.wiles.enums.WhenRemoveToken;
import in.costea.wiles.exceptions.AbstractCompilationException;
import in.costea.wiles.exceptions.InternalErrorException;
import in.costea.wiles.exceptions.UnexpectedEndException;
import in.costea.wiles.exceptions.UnexpectedTokenException;
import in.costea.wiles.services.PrecedenceProcessor;
import in.costea.wiles.services.TokenTransmitter;
import in.costea.wiles.statements.AbstractStatement;
import in.costea.wiles.statements.TokenStatement;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static in.costea.wiles.builders.ExpectParamsBuilder.tokenOf;
import static in.costea.wiles.constants.ErrorMessages.*;
import static in.costea.wiles.constants.Predicates.*;
import static in.costea.wiles.constants.Tokens.*;

public abstract class AbstractExpression extends AbstractStatement {
    @NotNull
    protected final CompilationExceptionsCollection exceptions = new CompilationExceptionsCollection();
    protected AbstractStatement left = null;
    protected TokenStatement operation = null;
    protected AbstractStatement right = null;

    protected AbstractExpression(@NotNull TokenTransmitter transmitter, @NotNull IsWithin within) {
        super(transmitter,within);
    }

    @Override
    public final @NotNull SyntaxType getType() {
        return SyntaxType.EXPRESSION;
    }

    @Override
    public final @NotNull List<AbstractStatement> getComponents() {
        var components = new ArrayList<AbstractStatement>();
        if (left != null) components.add(left);
        if (operation != null) components.add(operation);
        else
            assert left == null;
        if(right != null)
            components.add(right);
        return components;
    }

    protected Optional<AbstractStatement> handleSpecialStatements() {
        return Optional.empty();
    }

    protected boolean handleToken(@NotNull Token token) throws AbstractCompilationException {
        if (token.getContent().equals(ROUND_BRACKET_END_ID))
            throw new UnexpectedTokenException(UNEXPECTED_CLOSING_BRACKET_ERROR, token.getLocation());
        return STATEMENT_START_KEYWORDS.contains(token.getContent());
    }

    protected void setComponents(@NotNull PrecedenceProcessor precedenceProcessor)
    {
        @NotNull final AbstractStatement result = precedenceProcessor.getResult();
        if (result instanceof final AbstractExpression expression) {
            this.left = expression.left;
            this.operation = expression.operation;
            this.right = expression.right;
            //Right cannot be null. If left is null, operation must also be null
            assert operation != null || left == null;
            assert right != null;
            return;
        }
        //Is not flattenable
        this.right = result;
    }

    @Override
    public @NotNull CompilationExceptionsCollection process() {
        try {
            @NotNull Token mainCurrentToken = transmitter.expect(START_OF_EXPRESSION);
            @NotNull var precedenceProcessor = new PrecedenceProcessor(transmitter,getWithin());
            @NotNull Optional<Token> maybeTempToken;
            @NotNull String content = mainCurrentToken.getContent();

            //Decide what token to expect first
            @NotNull ExpectNext expectNext;
            if (IS_LITERAL.test(content) || ROUND_BRACKETS.contains(content) || STARTING_OPERATORS.contains(content))
                expectNext = ExpectNext.TOKEN;
            else
                expectNext = ExpectNext.OPERATOR;

            while (!transmitter.tokensExhausted()) {
                //Finalize expression
                //It finalizes on keywords that correspond to the start of the next statement for better error messages
                if ((expectNext == ExpectNext.OPERATOR)) {
                    maybeTempToken = transmitter.expectMaybe(tokenOf(IS_CONTAINED_IN.invoke(TERMINATORS)).or(ASSIGN_ID)
                            .or(IS_CONTAINED_IN.invoke(STATEMENT_START_KEYWORDS)).dontIgnoreNewLine()
                            .removeWhen(WhenRemoveToken.Never));
                    if (maybeTempToken.isPresent())
                        if (handleToken(maybeTempToken.get()))
                            break;
                }

                //Handle method calls and inner expressions
                if (transmitter.expectMaybe(tokenOf(ROUND_BRACKET_START_ID)).isPresent()) {
                    if (expectNext == ExpectNext.OPERATOR) //Method call
                        throw new InternalErrorException(NOT_YET_IMPLEMENTED_ERROR);
                    else { //Inner expressions
                        var newExpression = new InnerExpression(transmitter,getWithin());
                        newExpression.process().throwFirstIfExists();
                        precedenceProcessor.add(newExpression);
                        expectNext = ExpectNext.OPERATOR;
                        continue;
                    }
                }

                //Handle closing brackets token
                maybeTempToken = transmitter.expectMaybe(tokenOf(ROUND_BRACKET_END_ID));
                if (maybeTempToken.isPresent()) {
                    mainCurrentToken = maybeTempToken.get();
                    if (handleToken(mainCurrentToken))
                        break;
                }

                //Special statements
                if (expectNext == ExpectNext.TOKEN) {
                    Optional<AbstractStatement> maybeStatement;
                    if ((maybeStatement = handleSpecialStatements()).isPresent()) {
                        AbstractStatement statement = maybeStatement.get();
                        exceptions.addAll(statement.process());
                        precedenceProcessor.add(maybeStatement.get());
                        expectNext = ExpectNext.OPERATOR;
                        continue;
                    }
                }

                //Handle unary operators
                if (expectNext == ExpectNext.TOKEN) {
                    maybeTempToken = transmitter.expectMaybe(tokenOf(IS_CONTAINED_IN.invoke(STARTING_OPERATORS)));
                    if (maybeTempToken.isPresent()) {
                        mainCurrentToken = maybeTempToken.get();
                        if (INFIX_OPERATORS.contains(mainCurrentToken.getContent()))
                            mainCurrentToken = new Token(UNARY_ID + mainCurrentToken.getContent(),
                                    mainCurrentToken.getLocation());
                        precedenceProcessor.add(new TokenStatement(transmitter, mainCurrentToken,getWithin()));
                        continue;
                    }
                }

                //Expect the next token
                if (expectNext == ExpectNext.OPERATOR)
                    mainCurrentToken = transmitter.expect(tokenOf(IS_CONTAINED_IN.invoke(INFIX_OPERATORS))
                            .withErrorMessage(INVALID_EXPRESSION_ERROR));
                else
                    mainCurrentToken = transmitter.expect(tokenOf(IS_CONTAINED_IN.invoke(STARTING_OPERATORS))
                            .or(IS_LITERAL).withErrorMessage(INVALID_EXPRESSION_ERROR));

                //Add token and change next expected token
                precedenceProcessor.add(new TokenStatement(transmitter, mainCurrentToken,getWithin()));
                expectNext = (expectNext == ExpectNext.OPERATOR) ? ExpectNext.TOKEN : ExpectNext.OPERATOR;
            }

            //Final processing
            if (expectNext == ExpectNext.TOKEN)
                throw new UnexpectedEndException(EXPRESSION_UNFINISHED_ERROR, mainCurrentToken.getLocation());
            if (this instanceof InnerExpression && !mainCurrentToken.getContent().equals(ROUND_BRACKET_END_ID))
                throw new UnexpectedEndException(UNEXPECTED_OPENING_BRACKET_ERROR, transmitter.getLastLocation());
            setComponents(precedenceProcessor);
        } catch (AbstractCompilationException ex) {
            exceptions.add(ex);
        }
        return exceptions;
    }
}
