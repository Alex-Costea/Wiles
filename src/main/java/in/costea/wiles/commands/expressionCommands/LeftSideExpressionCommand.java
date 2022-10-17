package in.costea.wiles.commands.expressionCommands;

import in.costea.wiles.data.Token;
import in.costea.wiles.services.TokenTransmitter;
import org.jetbrains.annotations.NotNull;

public class LeftSideExpressionCommand extends AbstractExpressionCommand {

    public LeftSideExpressionCommand(@NotNull TokenTransmitter transmitter) {
        super(transmitter);
    }

    private void checkValid()
    {
        //TODO: check if valid LeftSide
    }

    public LeftSideExpressionCommand(@NotNull TokenTransmitter transmitter, AssignableExpressionCommand assignableExpressionCommand) {
        super(transmitter);
        components.addAll(assignableExpressionCommand.components);
        exceptions.addAll(assignableExpressionCommand.exceptions);
        checkValid();
    }

    @Override
    protected boolean handleAssignTokenReceived(Token token) {
        checkValid();
        return true;
    }
}
