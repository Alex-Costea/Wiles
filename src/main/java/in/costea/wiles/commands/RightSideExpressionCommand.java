package in.costea.wiles.commands;

import in.costea.wiles.enums.ExpressionType;
import in.costea.wiles.services.TokenTransmitter;
import org.jetbrains.annotations.NotNull;

public class RightSideExpressionCommand extends ExpressionCommand{
    public RightSideExpressionCommand(@NotNull TokenTransmitter transmitter) {
        super(transmitter, ExpressionType.RIGHT_SIDE);
    }
}
