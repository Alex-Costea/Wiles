package in.costea.wiles.commands.expressions;

import in.costea.wiles.data.TokenLocation;
import in.costea.wiles.exceptions.UnexpectedEndException;
import in.costea.wiles.services.TokenTransmitter;
import org.jetbrains.annotations.NotNull;

import static in.costea.wiles.statics.Constants.ROUND_BRACKET_END_ID;

public class InnerExpressionCommand extends AbstractExpressionCommand {
    public InnerExpressionCommand(@NotNull TokenTransmitter transmitter) {
        super(transmitter);
        shouldFlatten=true;
    }

    @Override
    protected boolean handleBracketsCloseTokenFound(@NotNull String content, TokenLocation location) {
        return true;
    }

    @Override
    protected void checkBracketsCloseProperlyAtEnd(@NotNull String content, TokenLocation location) throws UnexpectedEndException {

        if (!content.equals(ROUND_BRACKET_END_ID))
            throw new UnexpectedEndException("Closing brackets expected", location);
    }
}
