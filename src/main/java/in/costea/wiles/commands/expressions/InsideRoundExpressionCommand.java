package in.costea.wiles.commands.expressions;

import in.costea.wiles.commands.AbstractCommand;
import in.costea.wiles.data.TokenLocation;
import in.costea.wiles.exceptions.UnexpectedEndException;
import in.costea.wiles.services.TokenTransmitter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static in.costea.wiles.statics.Constants.ROUND_BRACKET_END_ID;

public class InsideRoundExpressionCommand extends AbstractExpressionCommand {
    public InsideRoundExpressionCommand(@NotNull TokenTransmitter transmitter) {
        super(transmitter);
        name = "ROUND";
    }

    public InsideRoundExpressionCommand(@NotNull TokenTransmitter transmitter, @NotNull List<AbstractCommand> components) {
        this(transmitter);
        this.components.addAll(components);
    }

    @Override
    protected boolean handleBracketsCloseTokenFound(@NotNull String content, TokenLocation location) {
        return content.equals(ROUND_BRACKET_END_ID);
    }

    @Override
    protected void checkBracketsCloseProperlyAtEnd(@NotNull String content, TokenLocation location) throws UnexpectedEndException {

        if (!content.equals(ROUND_BRACKET_END_ID))
            throw new UnexpectedEndException("Closing brackets expected", location);
    }
}
