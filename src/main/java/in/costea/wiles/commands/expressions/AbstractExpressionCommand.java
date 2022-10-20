package in.costea.wiles.commands.expressions;

import in.costea.wiles.builders.ExpectParamsBuilder;
import in.costea.wiles.commands.AbstractCommand;
import in.costea.wiles.commands.TokenCommand;
import in.costea.wiles.data.CompilationExceptionsCollection;
import in.costea.wiles.data.Token;
import in.costea.wiles.data.TokenLocation;
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
        if(left!=null) components.add(left);
        if(operation!=null) components.add(operation);
        assert(right!=null);
        components.add(right);
        return components;
    }

    private void addInnerExpression(PrecedenceProcessor precedenceProcessor) throws AbstractCompilationException {
        var newExpression = new InnerExpressionCommand(transmitter);
        @NotNull final var newExceptions = newExpression.process();
        if (newExceptions.size() > 0)
            throw newExceptions.get(0);
        precedenceProcessor.add(newExpression);
    }

    private @NotNull ExpectNext firstExpectNext(@NotNull String content) {
        if (IS_LITERAL.test(content) || ROUND_BRACKETS.contains(content) || STARTING_OPERATORS.contains(content))
            return ExpectNext.TOKEN;
        return ExpectNext.OPERATOR;
    }

    private @NotNull Token getNextToken(@NotNull ExpectNext expectNext) throws AbstractCompilationException {
        if (expectNext == ExpectNext.OPERATOR) return transmitter.expect(tokenOf(isContainedIn(ROUND_BRACKETS))
                .or(isContainedIn(INFIX_OPERATORS)).withErrorMessage("Operator expected!"));

        return transmitter.expect(tokenOf(isContainedIn(ROUND_BRACKETS)).or(isContainedIn(STARTING_OPERATORS))
                .or(IS_LITERAL).withErrorMessage("Identifier or unary operator expected!"));
    }

    private void flatten(AbstractCommand component) {
        if(this instanceof AssignableExpressionCommand command && command.isAssignment) {
            this.left = component;
        }
        else if (component instanceof final BinaryExpressionCommand command) {
            this.left = command.left;
            this.operation = command.operation;
            this.right = command.right;
            assert right != null;
        }
        else if (component instanceof final InnerExpressionCommand command) {
            this.left=command.left;
            this.operation=command.operation;
            this.right=command.right;
        }
        else this.right=component;
    }

    @Override
    public @NotNull CompilationExceptionsCollection process() {
        try {
            @NotNull Token mainToken = transmitter.expect(START_OF_EXPRESSION);
            @NotNull ExpectNext expectNext;
            @NotNull var content = mainToken.getContent();
            @NotNull var precedenceProcessor=new PrecedenceProcessor(transmitter);
            TokenLocation location = mainToken.getLocation();
            expectNext = firstExpectNext(content);

            while (!transmitter.tokensExhausted()) {
                // finalize expression if correctly finalized
                if ((expectNext == ExpectNext.OPERATOR))
                    if (checkExpressionFinalized())
                        break;

                // handle end token
                @NotNull final var tempToken = transmitter.expectMaybe(tokenOf(END_BLOCK_ID).removeWhen(WhenRemoveToken.Never));
                if (tempToken.isPresent())
                    if (handleEndTokenReceived(tempToken.get().getLocation()))
                        break;

                //handle assignment token
                final var tempToken2 = transmitter.expectMaybe(tokenOf(ASSIGN_ID).removeWhen(WhenRemoveToken.Never));
                if (tempToken2.isPresent())
                    if (handleAssignTokenReceived(tempToken2.get().getLocation(),precedenceProcessor))
                        break;

                // expect the next correct token
                mainToken = getNextToken(expectNext);
                content = mainToken.getContent();
                location = mainToken.getLocation();

                //handle closing brackets token
                if (content.equals(ROUND_BRACKET_END_ID))
                    if (handleBracketsCloseTokenFound(content, location))
                        break;

                //method call
                if (expectNext == ExpectNext.OPERATOR && content.equals(ROUND_BRACKET_START_ID)) {
                    todo("Method call");
                }

                // switch expecting operator or token next
                if (expectNext == ExpectNext.TOKEN && STARTING_OPERATORS.contains(content)) {
                    if (INFIX_OPERATORS.contains(content))
                        mainToken=new Token(UNARY_ID+content,location);
                }
                else expectNext = expectNext == ExpectNext.OPERATOR ? ExpectNext.TOKEN : ExpectNext.OPERATOR;

                // add inner expression
                if (content.equals(ROUND_BRACKET_START_ID))
                    addInnerExpression(precedenceProcessor);
                else precedenceProcessor.add(new TokenCommand(transmitter, mainToken));
            }

            checkBracketsCloseProperlyAtEnd(content, location);
            if (expectNext == ExpectNext.TOKEN && exceptions.size() == 0)
                throw new UnexpectedEndException("Expression unfinished!", location);

            //Set order of operations and flatten
            flatten(precedenceProcessor.getResult());
        } catch (AbstractCompilationException ex) {
            exceptions.add(ex);
        }
        return exceptions;
    }

    protected boolean handleAssignTokenReceived(TokenLocation location, PrecedenceProcessor precedenceProcessor) throws AbstractCompilationException {
        throw new UnexpectedTokenException("Assignment not allowed here!", location);
    }

    protected void checkBracketsCloseProperlyAtEnd(@NotNull String content, TokenLocation location) throws UnexpectedEndException {
        //by default, there is no check
    }

    protected boolean handleEndTokenReceived(TokenLocation location) throws UnexpectedTokenException {
        throw new UnexpectedTokenException("End token not allowed here!", location);
    }

    protected boolean handleBracketsCloseTokenFound(@NotNull String content, TokenLocation location) throws UnexpectedTokenException {
        throw new UnexpectedTokenException("Brackets don't close properly", location);
    }

    protected boolean checkExpressionFinalized() {
        return false;
    }

    private enum ExpectNext {
        OPERATOR,
        TOKEN
    }
}
