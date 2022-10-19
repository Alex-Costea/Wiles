package in.costea.wiles.commands.expressions;

import in.costea.wiles.commands.TokenCommand;
import in.costea.wiles.data.TokenLocation;
import in.costea.wiles.exceptions.TokenExpectedException;
import in.costea.wiles.exceptions.UnexpectedEndException;
import in.costea.wiles.services.TokenTransmitter;
import org.jetbrains.annotations.NotNull;

import static in.costea.wiles.builders.ExpectParamsBuilder.isContainedIn;
import static in.costea.wiles.builders.ExpectParamsBuilder.tokenOf;
import static in.costea.wiles.statics.Constants.ASSIGN_ID;
import static in.costea.wiles.statics.Constants.TERMINATORS;

public class AssignableExpressionCommand extends AbstractExpressionCommand {

    public AssignableExpressionCommand(@NotNull TokenTransmitter transmitter) {
        super(transmitter);
    }

    @Override
    protected boolean checkExpressionFinalized() {
        return transmitter.expectMaybe(tokenOf(isContainedIn(TERMINATORS)).dontIgnoreNewLine()).isPresent();
    }

    @Override
    protected boolean handleEndTokenReceived(TokenLocation location) {
        return true;
    }

    @Override
    protected boolean handleAssignTokenReceived(TokenLocation location) throws TokenExpectedException, UnexpectedEndException {
        operation = new TokenCommand(transmitter,transmitter.expect(tokenOf(ASSIGN_ID)));
        left = new LeftSideExpressionCommand(transmitter, this);
        right = new RightSideExpressionCommand(transmitter);
        components.clear();
        exceptions.addAll(right.process());
        shouldDoOrderOfOperations=false;
        return true;
    }
}
