package in.costea.wiles.commands.expressions;

import in.costea.wiles.builders.ExpectParamsBuilder;
import in.costea.wiles.commands.AbstractCommand;
import in.costea.wiles.commands.TokenCommand;
import in.costea.wiles.data.CompilationExceptionsCollection;
import in.costea.wiles.data.Token;
import in.costea.wiles.enums.ExpectNext;
import in.costea.wiles.enums.SyntaxType;
import in.costea.wiles.enums.WhenRemoveToken;
import in.costea.wiles.exceptions.AbstractCompilationException;
import in.costea.wiles.exceptions.UnexpectedEndException;
import in.costea.wiles.exceptions.UnexpectedTokenException;
import in.costea.wiles.services.PrecedenceProcessor;
import in.costea.wiles.services.TokenTransmitter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static in.costea.wiles.builders.ExpectParamsBuilder.isContainedIn;
import static in.costea.wiles.builders.ExpectParamsBuilder.tokenOf;
import static in.costea.wiles.statics.Constants.*;
import static in.costea.wiles.statics.Utils.todo;

public abstract class AbstractExpressionCommand extends AbstractCommand {
    public static final ExpectParamsBuilder START_OF_EXPRESSION =
            tokenOf(isContainedIn(STARTING_OPERATORS)).or(IS_LITERAL).or(ROUND_BRACKET_START_ID)
                    .withErrorMessage("Expected expression!").removeWhen(WhenRemoveToken.Never);
    @NotNull
    protected final CompilationExceptionsCollection exceptions = new CompilationExceptionsCollection();
    protected AbstractCommand left = null;
    protected TokenCommand operation = null;
    protected AbstractCommand right = null;

    protected AbstractExpressionCommand(@NotNull TokenTransmitter transmitter) {
        super(transmitter);
    }

    @Override
    public final @NotNull SyntaxType getType() {
        return SyntaxType.EXPRESSION;
    }

    @Override
    public final @NotNull List<AbstractCommand> getComponents() {
        var components = new ArrayList<AbstractCommand>();
        if (left != null) components.add(left);
        if (operation != null) components.add(operation);
        else
            assert left == null;
        assert right != null;
        components.add(right);
        return components;
    }

    protected Optional<AbstractCommand> handleSpecialCommands() {
        return Optional.empty();
    }

    protected boolean handleToken(@NotNull Token token) throws AbstractCompilationException {
        if (token.getContent().equals(ROUND_BRACKET_END_ID))
            throw new UnexpectedTokenException("Brackets don't close properly", token.getLocation());
        return false;
    }

    protected void setComponents(@NotNull PrecedenceProcessor precedenceProcessor)
    {
        @NotNull final AbstractCommand result = precedenceProcessor.getResult();
        if (result instanceof final AbstractExpressionCommand command) {
            this.left = command.left;
            this.operation = command.operation;
            this.right = command.right;
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
            @NotNull var precedenceProcessor = new PrecedenceProcessor(transmitter);
            @NotNull Optional<Token> maybeTempToken;
            @NotNull String content = mainCurrentToken.getContent();

            //Decide what token to expect first
            @NotNull ExpectNext expectNext;
            if (IS_LITERAL.test(content) || ROUND_BRACKETS.contains(content) || STARTING_OPERATORS.contains(content))
                expectNext = ExpectNext.TOKEN;
            else
                expectNext = ExpectNext.OPERATOR;

            while (!transmitter.tokensExhausted()) {
                //Stop parsing expression if correctly finalized
                if ((expectNext == ExpectNext.OPERATOR)) {
                    maybeTempToken = transmitter.expectMaybe(tokenOf(isContainedIn(TERMINATORS)).dontIgnoreNewLine());
                    if (maybeTempToken.isPresent())
                        if (handleToken(maybeTempToken.get()))
                            break;
                }

                //Handle end and assign tokens
                maybeTempToken = transmitter.expectMaybe(tokenOf(END_BLOCK_ID).or(ASSIGN_ID)
                        .removeWhen(WhenRemoveToken.Never));
                if (maybeTempToken.isPresent())
                    if (handleToken(maybeTempToken.get()))
                        break;

                //Handle method calls and inner expressions
                if (transmitter.expectMaybe(tokenOf(ROUND_BRACKET_START_ID)).isPresent()) {
                    if (expectNext == ExpectNext.OPERATOR) //Method call
                        todo("Method call");
                    else { //Inner expressions
                        var newExpression = new InnerExpressionCommand(this.transmitter);
                        @NotNull final CompilationExceptionsCollection newExceptions = newExpression.process();
                        if (newExceptions.size() > 0)
                            throw newExceptions.get(0);
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

                //Special commands
                if (expectNext == ExpectNext.TOKEN) {
                    Optional<AbstractCommand> maybeCommand;
                    if ((maybeCommand = handleSpecialCommands()).isPresent()) {
                        AbstractCommand command = maybeCommand.get();
                        CompilationExceptionsCollection exceptions = command.process();
                        if (!exceptions.isEmpty())
                            throw exceptions.get(0);
                        precedenceProcessor.add(maybeCommand.get());
                        expectNext = ExpectNext.OPERATOR;
                        continue;
                    }
                }

                //Handle unary operators
                if (expectNext == ExpectNext.TOKEN) {
                    maybeTempToken = transmitter.expectMaybe(tokenOf(isContainedIn(STARTING_OPERATORS)));
                    if (maybeTempToken.isPresent()) {
                        mainCurrentToken = maybeTempToken.get();
                        if (INFIX_OPERATORS.contains(mainCurrentToken.getContent()))
                            mainCurrentToken = new Token(UNARY_ID + mainCurrentToken.getContent(),
                                    mainCurrentToken.getLocation());
                        precedenceProcessor.add(new TokenCommand(transmitter, mainCurrentToken));
                        continue;
                    }
                }

                //Expect the next token
                if (expectNext == ExpectNext.OPERATOR)
                    mainCurrentToken = transmitter.expect(tokenOf(isContainedIn(INFIX_OPERATORS))
                            .withErrorMessage("Operator expected!"));
                else
                    mainCurrentToken = transmitter.expect(tokenOf(isContainedIn(STARTING_OPERATORS)).or(IS_LITERAL)
                            .withErrorMessage("Identifier or unary operator expected!"));

                //Add token and change next expected token
                precedenceProcessor.add(new TokenCommand(transmitter, mainCurrentToken));
                expectNext = (expectNext == ExpectNext.OPERATOR) ? ExpectNext.TOKEN : ExpectNext.OPERATOR;
            }

            //Final processing
            if (expectNext == ExpectNext.TOKEN)
                throw new UnexpectedEndException("Expression unfinished!", mainCurrentToken.getLocation());
            if (this instanceof InnerExpressionCommand && !mainCurrentToken.getContent().equals(ROUND_BRACKET_END_ID))
                throw new UnexpectedEndException("Closing brackets expected", mainCurrentToken.getLocation());
            setComponents(precedenceProcessor);
        } catch (AbstractCompilationException ex) {
            exceptions.add(ex);
        }
        return exceptions;
    }
}
