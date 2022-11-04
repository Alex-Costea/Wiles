package wiles.parser.statements.expressions;

import org.jetbrains.annotations.NotNull;
import wiles.parser.builders.Context;
import wiles.parser.builders.ExpectParamsBuilder;
import wiles.parser.constants.ErrorMessages;
import wiles.parser.constants.Tokens;
import wiles.parser.data.CompilationExceptionsCollection;
import wiles.parser.data.Token;
import wiles.parser.enums.ExpectNext;
import wiles.parser.enums.SyntaxType;
import wiles.parser.enums.WhenRemoveToken;
import wiles.parser.exceptions.AbstractCompilationException;
import wiles.parser.exceptions.UnexpectedEndException;
import wiles.parser.exceptions.UnexpectedTokenException;
import wiles.parser.services.PrecedenceProcessor;
import wiles.parser.statements.AbstractStatement;
import wiles.parser.statements.MethodCallStatement;
import wiles.parser.statements.TokenStatement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static wiles.parser.builders.ExpectParamsBuilder.tokenOf;
import static wiles.parser.constants.Predicates.*;

public abstract class AbstractExpression extends AbstractStatement {
    @NotNull
    protected final CompilationExceptionsCollection exceptions = new CompilationExceptionsCollection();
    protected AbstractStatement left = null;
    protected TokenStatement operation = null;
    protected AbstractStatement right = null;

    protected AbstractExpression(@NotNull Context context) {
        super(context);
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

    protected boolean handleToken(@NotNull Token token) throws AbstractCompilationException {
        if (token.getContent().equals(Tokens.PAREN_END_ID))
            throw new UnexpectedTokenException(ErrorMessages.UNEXPECTED_CLOSING_BRACKET_ERROR, token.getLocation());
        return Tokens.STATEMENT_START_KEYWORDS.contains(token.getContent());
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

    protected void checkValid() throws AbstractCompilationException {
        //Nothing to check by default
    }

    //TODO: read list literals and function declarations properly
    @Override
    public @NotNull CompilationExceptionsCollection process() {
        try {
            @NotNull Token mainCurrentToken = transmitter.expect(START_OF_EXPRESSION);
            @NotNull var precedenceProcessor = new PrecedenceProcessor(getContext());
            @NotNull Optional<Token> maybeTempToken;
            @NotNull String content = mainCurrentToken.getContent();

            //Decide what token to expect first
            @NotNull ExpectNext expectNext;
            if (IS_LITERAL.test(content) || Tokens.PARENS.contains(content) || Tokens.STARTING_OPERATORS.contains(content))
                expectNext = ExpectNext.TOKEN;
            else
                expectNext = ExpectNext.OPERATOR;

            while (!transmitter.tokensExhausted()) {
                //Finalize expression
                //It finalizes on keywords that correspond to the start of the next statement for better error messages
                if ((expectNext == ExpectNext.OPERATOR)) {
                    maybeTempToken = transmitter.expectMaybe(tokenOf(IS_CONTAINED_IN.invoke(Tokens.TERMINATORS)).or(Tokens.ASSIGN_ID)
                            .or(IS_CONTAINED_IN.invoke(Tokens.STATEMENT_START_KEYWORDS)).or(Tokens.SEPARATOR_ID).dontIgnoreNewLine()
                            .removeWhen(WhenRemoveToken.Never));
                    if (maybeTempToken.isPresent())
                        if (handleToken(maybeTempToken.get()))
                            break;
                }

                //Handle method calls and inner expressions
                maybeTempToken =transmitter.expectMaybe(ExpectParamsBuilder.tokenOf(Tokens.PAREN_START_ID));
                if (maybeTempToken.isPresent()) {
                    if (expectNext == ExpectNext.OPERATOR) { //Method call
                        precedenceProcessor.add(new TokenStatement(new Token(Tokens.APPLY_ID,maybeTempToken.get()
                                .getLocation()),getContext()));
                        var newExpression = new MethodCallStatement(getContext());
                        newExpression.process().throwFirstIfExists();
                        precedenceProcessor.add(newExpression);
                        continue;
                    }
                    //Inner expressions
                    var newExpression = new InnerExpression(getContext());
                    newExpression.process().throwFirstIfExists();
                    precedenceProcessor.add(newExpression);
                    expectNext = ExpectNext.OPERATOR;
                    continue;
                }

                //Handle closing brackets token
                maybeTempToken = transmitter.expectMaybe(ExpectParamsBuilder.tokenOf(Tokens.PAREN_END_ID));
                if (maybeTempToken.isPresent()) {
                    mainCurrentToken = maybeTempToken.get();
                    if (handleToken(mainCurrentToken))
                        break;
                }

                //Handle unary operators
                if (expectNext == ExpectNext.TOKEN) {
                    maybeTempToken = transmitter.expectMaybe(tokenOf(IS_CONTAINED_IN.invoke(Tokens.STARTING_OPERATORS)));
                    if (maybeTempToken.isPresent()) {
                        mainCurrentToken = maybeTempToken.get();
                        if (Tokens.INFIX_OPERATORS.contains(mainCurrentToken.getContent()))
                            mainCurrentToken = new Token(Tokens.UNARY_ID + mainCurrentToken.getContent(),
                                    mainCurrentToken.getLocation());
                        precedenceProcessor.add(new TokenStatement(mainCurrentToken, getContext()));
                        continue;
                    }
                }

                //Expect the next token
                if (expectNext == ExpectNext.OPERATOR)
                    mainCurrentToken = transmitter.expect(tokenOf(IS_CONTAINED_IN.invoke(Tokens.INFIX_OPERATORS))
                            .withErrorMessage(ErrorMessages.INVALID_EXPRESSION_ERROR));
                else
                    mainCurrentToken = transmitter.expect(tokenOf(IS_CONTAINED_IN.invoke(Tokens.STARTING_OPERATORS))
                            .or(IS_LITERAL).withErrorMessage(ErrorMessages.INVALID_EXPRESSION_ERROR));

                //Add token and change next expected token
                precedenceProcessor.add(new TokenStatement(mainCurrentToken, getContext()));
                expectNext = (expectNext == ExpectNext.OPERATOR) ? ExpectNext.TOKEN : ExpectNext.OPERATOR;
            }

            //Final processing
            if (expectNext == ExpectNext.TOKEN)
                throw new UnexpectedEndException(ErrorMessages.EXPRESSION_UNFINISHED_ERROR, mainCurrentToken.getLocation());
            if (this instanceof InnerExpression && !mainCurrentToken.getContent().equals(Tokens.PAREN_END_ID))
                throw new UnexpectedEndException(ErrorMessages.UNEXPECTED_OPENING_BRACKET_ERROR, transmitter.getLastLocation());
            setComponents(precedenceProcessor);
            checkValid();
        } catch (AbstractCompilationException ex) {
            exceptions.add(ex);
        }
        return exceptions;
    }
}
