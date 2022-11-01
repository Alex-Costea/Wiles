package in.costea.wiles.statements.expressions;

import in.costea.wiles.builders.Context;
import in.costea.wiles.data.CompilationExceptionsCollection;
import in.costea.wiles.data.Token;
import in.costea.wiles.enums.ExpectNext;
import in.costea.wiles.enums.SyntaxType;
import in.costea.wiles.enums.WhenRemoveToken;
import in.costea.wiles.exceptions.AbstractCompilationException;
import in.costea.wiles.exceptions.UnexpectedEndException;
import in.costea.wiles.exceptions.UnexpectedTokenException;
import in.costea.wiles.services.PrecedenceProcessor;
import in.costea.wiles.statements.AbstractStatement;
import in.costea.wiles.statements.MethodCallStatement;
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
        if (token.getContent().equals(PAREN_END_ID))
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

    protected void checkValid() throws AbstractCompilationException {
        //Nothing to check by default
    }

    @Override
    public @NotNull CompilationExceptionsCollection process() {
        try {
            @NotNull Token mainCurrentToken = transmitter.expect(START_OF_EXPRESSION);
            @NotNull var precedenceProcessor = new PrecedenceProcessor(getContext());
            @NotNull Optional<Token> maybeTempToken;
            @NotNull String content = mainCurrentToken.getContent();

            //Decide what token to expect first
            @NotNull ExpectNext expectNext;
            if (IS_LITERAL.test(content) || PARENS.contains(content) || STARTING_OPERATORS.contains(content))
                expectNext = ExpectNext.TOKEN;
            else
                expectNext = ExpectNext.OPERATOR;

            while (!transmitter.tokensExhausted()) {
                //Finalize expression
                //It finalizes on keywords that correspond to the start of the next statement for better error messages
                if ((expectNext == ExpectNext.OPERATOR)) {
                    maybeTempToken = transmitter.expectMaybe(tokenOf(IS_CONTAINED_IN.invoke(TERMINATORS)).or(ASSIGN_ID)
                            .or(IS_CONTAINED_IN.invoke(STATEMENT_START_KEYWORDS)).or(SEPARATOR_ID).dontIgnoreNewLine()
                            .removeWhen(WhenRemoveToken.Never));
                    if (maybeTempToken.isPresent())
                        if (handleToken(maybeTempToken.get()))
                            break;
                }

                //Handle method calls and inner expressions
                maybeTempToken =transmitter.expectMaybe(tokenOf(PAREN_START_ID));
                if (maybeTempToken.isPresent()) {
                    if (expectNext == ExpectNext.OPERATOR) { //Method call
                        precedenceProcessor.add(new TokenStatement(new Token(APPLY_ID,maybeTempToken.get()
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
                maybeTempToken = transmitter.expectMaybe(tokenOf(PAREN_END_ID));
                if (maybeTempToken.isPresent()) {
                    mainCurrentToken = maybeTempToken.get();
                    if (handleToken(mainCurrentToken))
                        break;
                }

                //Handle unary operators
                if (expectNext == ExpectNext.TOKEN) {
                    maybeTempToken = transmitter.expectMaybe(tokenOf(IS_CONTAINED_IN.invoke(STARTING_OPERATORS)));
                    if (maybeTempToken.isPresent()) {
                        mainCurrentToken = maybeTempToken.get();
                        if (INFIX_OPERATORS.contains(mainCurrentToken.getContent()))
                            mainCurrentToken = new Token(UNARY_ID + mainCurrentToken.getContent(),
                                    mainCurrentToken.getLocation());
                        precedenceProcessor.add(new TokenStatement(mainCurrentToken, getContext()));
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
                precedenceProcessor.add(new TokenStatement(mainCurrentToken, getContext()));
                expectNext = (expectNext == ExpectNext.OPERATOR) ? ExpectNext.TOKEN : ExpectNext.OPERATOR;
            }

            //Final processing
            if (expectNext == ExpectNext.TOKEN)
                throw new UnexpectedEndException(EXPRESSION_UNFINISHED_ERROR, mainCurrentToken.getLocation());
            if (this instanceof InnerExpression && !mainCurrentToken.getContent().equals(PAREN_END_ID))
                throw new UnexpectedEndException(UNEXPECTED_OPENING_BRACKET_ERROR, transmitter.getLastLocation());
            setComponents(precedenceProcessor);
            checkValid();
        } catch (AbstractCompilationException ex) {
            exceptions.add(ex);
        }
        return exceptions;
    }
}
