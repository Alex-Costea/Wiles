package in.costea.wiles.commands;

import in.costea.wiles.enums.ExpressionType;
import in.costea.wiles.services.TokenTransmitter;
import org.jetbrains.annotations.NotNull;

public class LeftSideExpressionCommand extends ExpressionCommand{
    public LeftSideExpressionCommand(@NotNull TokenTransmitter transmitter) {
        super(transmitter, ExpressionType.LEFT_SIDE);
    }
}
