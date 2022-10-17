package in.costea.wiles.commands;

import in.costea.wiles.enums.ExpressionType;
import in.costea.wiles.services.TokenTransmitter;
import org.jetbrains.annotations.NotNull;

public class InsideSquareBracketsExpressionCommand extends ExpressionCommand{
    public InsideSquareBracketsExpressionCommand(@NotNull TokenTransmitter transmitter) {
        super(transmitter, ExpressionType.INSIDE_SQUARE);
    }
}
