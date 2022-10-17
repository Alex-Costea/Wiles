package in.costea.wiles.commands.expressionCommands;

import in.costea.wiles.data.TokenLocation;
import in.costea.wiles.exceptions.UnexpectedEndException;
import in.costea.wiles.services.TokenTransmitter;
import org.jetbrains.annotations.NotNull;

import static in.costea.wiles.statics.Constants.SQUARE_BRACKET_END_ID;

public class InsideSquareExpressionCommand extends AbstractExpressionCommand {
    public InsideSquareExpressionCommand(@NotNull TokenTransmitter transmitter) {
        super(transmitter);
        name = "SQUARE";
    }

    @Override
    protected boolean handleBracketsCloseTokenFound(@NotNull String content, TokenLocation location) {
        return content.equals(SQUARE_BRACKET_END_ID);
    }

    @Override
    protected void checkBracketsCloseProperlyAtEnd(@NotNull String content, TokenLocation location) throws UnexpectedEndException {

        if (!content.equals(SQUARE_BRACKET_END_ID))
            throw new UnexpectedEndException("Closing brackets expected", location);
    }
}
