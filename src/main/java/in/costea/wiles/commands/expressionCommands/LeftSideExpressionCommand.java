package in.costea.wiles.commands.expressionCommands;

import in.costea.wiles.data.Token;
import in.costea.wiles.services.TokenTransmitter;
import org.jetbrains.annotations.NotNull;

public class LeftSideExpressionCommand extends ExpressionCommand {

    public LeftSideExpressionCommand(@NotNull TokenTransmitter transmitter) {
        super(transmitter);
    }

    public LeftSideExpressionCommand(@NotNull TokenTransmitter transmitter, AssignableExpressionCommand assignableExpressionCommand) {
        super(transmitter);
        components.addAll(assignableExpressionCommand.components);
        exceptions.addAll(assignableExpressionCommand.exceptions);
        //TODO: check if valid LeftSide
    }

    @Override
    protected boolean handleAssignTokenReceived(Token token) {
        //TODO: check if valid LeftSide
        return true;
    }
}
