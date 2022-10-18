package in.costea.wiles.commands.expressions;

import in.costea.wiles.data.TokenLocation;
import in.costea.wiles.services.TokenTransmitter;
import org.jetbrains.annotations.NotNull;

import static in.costea.wiles.builders.ExpectParamsBuilder.isContainedIn;
import static in.costea.wiles.builders.ExpectParamsBuilder.tokenOf;
import static in.costea.wiles.statics.Constants.TERMINATORS;

public class RightSideExpressionCommand extends AbstractExpressionCommand {
    public RightSideExpressionCommand(@NotNull TokenTransmitter transmitter) {
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
}