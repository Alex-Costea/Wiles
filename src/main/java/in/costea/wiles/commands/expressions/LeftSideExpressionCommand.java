package in.costea.wiles.commands.expressions;

import in.costea.wiles.data.TokenLocation;
import in.costea.wiles.services.PrecedenceProcessor;
import in.costea.wiles.services.TokenTransmitter;
import org.jetbrains.annotations.NotNull;

public class LeftSideExpressionCommand extends AbstractExpressionCommand {


    public LeftSideExpressionCommand(@NotNull TokenTransmitter transmitter) {
        super(transmitter);
        checkValid();
    }

    private void checkValid() {
        //TODO: check if valid LeftSide
    }

    @Override
    protected boolean handleAssignTokenReceived(TokenLocation location, PrecedenceProcessor precedenceProcessor) {
        checkValid();
        return true;
    }
}
