package in.costea.wiles.commands;

import in.costea.wiles.enums.ExpressionType;
import in.costea.wiles.services.TokenTransmitter;
import org.jetbrains.annotations.NotNull;

public class InsideRoundBracketsExpressionCommand extends ExpressionCommand{
    public InsideRoundBracketsExpressionCommand(@NotNull TokenTransmitter transmitter) {
        super(transmitter, ExpressionType.INSIDE_ROUND);
    }
}
