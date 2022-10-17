package in.costea.wiles.commands.expressionCommands;

import in.costea.wiles.data.TokenLocation;
import in.costea.wiles.enums.SyntaxType;
import in.costea.wiles.exceptions.TokenExpectedException;
import in.costea.wiles.exceptions.UnexpectedEndException;
import in.costea.wiles.services.TokenTransmitter;
import org.jetbrains.annotations.NotNull;

import static in.costea.wiles.builders.ExpectParamsBuilder.isContainedIn;
import static in.costea.wiles.builders.ExpectParamsBuilder.tokenOf;
import static in.costea.wiles.statics.Constants.ASSIGN_ID;
import static in.costea.wiles.statics.Constants.STATEMENT_TERMINATORS;

public class AssignableExpressionCommand extends AbstractExpressionCommand {

    private boolean isAssignment = false;

    public AssignableExpressionCommand(@NotNull TokenTransmitter transmitter) {
        super(transmitter);
    }

    @Override
    public @NotNull SyntaxType getType() {
        if (isAssignment) return SyntaxType.ASSIGNMENT;
        return SyntaxType.EXPRESSION;
    }

    @Override
    protected boolean checkExpressionFinalized() {
        return transmitter.expectMaybe(tokenOf(isContainedIn(STATEMENT_TERMINATORS)).dontIgnoreNewLine()).isPresent();
    }

    @Override
    protected boolean handleEndTokenReceived(TokenLocation location) {
        return true;
    }

    @Override
    protected boolean handleAssignTokenReceived(TokenLocation location) throws TokenExpectedException, UnexpectedEndException {
        transmitter.expect(tokenOf(ASSIGN_ID));
        isAssignment = true;
        LeftSideExpressionCommand leftSide = new LeftSideExpressionCommand(transmitter, this);
        components.clear();
        RightSideExpressionCommand rightSide = new RightSideExpressionCommand(transmitter);
        exceptions.addAll(rightSide.process());
        components.add(leftSide);
        components.add(rightSide);
        return true;
    }
}
