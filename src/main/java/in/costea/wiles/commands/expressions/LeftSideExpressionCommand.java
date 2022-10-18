package in.costea.wiles.commands.expressions;

import in.costea.wiles.data.TokenLocation;
import in.costea.wiles.services.TokenTransmitter;
import org.jetbrains.annotations.NotNull;

public class LeftSideExpressionCommand extends AbstractExpressionCommand {

    public LeftSideExpressionCommand(@NotNull TokenTransmitter transmitter) {
        super(transmitter);
    }

    public LeftSideExpressionCommand(@NotNull TokenTransmitter transmitter, AssignableExpressionCommand assignableExpressionCommand) {
        super(transmitter);
        components.addAll(assignableExpressionCommand.components);
        exceptions.addAll(assignableExpressionCommand.exceptions);
        checkValid();
    }

    private void checkValid() {
        //TODO: check if valid LeftSide
    }

    @Override
    protected boolean handleAssignTokenReceived(TokenLocation location) {
        checkValid();
        return true;
    }
}