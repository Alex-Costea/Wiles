package in.costea.wiles.commands.expressionCommands;

import in.costea.wiles.builders.ExpectParamsBuilder;
import in.costea.wiles.enums.WhenRemoveToken;
import in.costea.wiles.services.TokenTransmitter;
import org.jetbrains.annotations.NotNull;

import static in.costea.wiles.builders.ExpectParamsBuilder.tokenOf;
import static in.costea.wiles.statics.Constants.ASSIGN_ID;

public class LeftSideExpressionCommand extends ExpressionCommand {
    @Override
    protected ExpectParamsBuilder expressionFinalized() {
        return tokenOf(ASSIGN_ID).removeWhen(WhenRemoveToken.Never);
    }

    public LeftSideExpressionCommand(@NotNull TokenTransmitter transmitter) {
        super(transmitter);
    }
}
