package in.costea.wiles.commands.expressionCommands;

import in.costea.wiles.data.TokenLocation;
import in.costea.wiles.exceptions.UnexpectedEndException;
import in.costea.wiles.services.TokenTransmitter;
import org.jetbrains.annotations.NotNull;

import static in.costea.wiles.statics.Constants.SQUARE_BRACKET_END_ID;

public class InsideSquareBracketsExpressionCommand extends ExpressionCommand {
    public InsideSquareBracketsExpressionCommand(@NotNull TokenTransmitter transmitter) {
        super(transmitter);
        name="SQUARE";
    }

    @Override
    protected boolean handleBracketsCloseTokenFound(String content, TokenLocation location) {
        return content.equals(SQUARE_BRACKET_END_ID);
    }

    @Override
    protected void checkBracketsCloseProperlyAtEnd(String content, TokenLocation location) throws UnexpectedEndException {

        if (!content.equals(SQUARE_BRACKET_END_ID))
            throw new UnexpectedEndException("Closing parentheses expected", location);
    }
}