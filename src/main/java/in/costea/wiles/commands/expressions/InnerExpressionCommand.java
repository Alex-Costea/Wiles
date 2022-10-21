package in.costea.wiles.commands.expressions;

import in.costea.wiles.data.Token;
import in.costea.wiles.exceptions.UnexpectedTokenException;
import in.costea.wiles.services.TokenTransmitter;
import org.jetbrains.annotations.NotNull;

import static in.costea.wiles.statics.Constants.*;

public class InnerExpressionCommand extends AbstractExpressionCommand {
    public InnerExpressionCommand(@NotNull TokenTransmitter transmitter) {
        super(transmitter);
    }

    @Override
    protected boolean handleToken(@NotNull Token token) throws UnexpectedTokenException {
        if(token.getContent().equals(END_BLOCK_ID))
            throw new UnexpectedTokenException(END_TOKEN_NOT_ALLOWED_ERROR,token.getLocation());
        return token.getContent().equals(ROUND_BRACKET_END_ID);
    }

}
