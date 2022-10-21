package in.costea.wiles.commands.expressions;

import in.costea.wiles.data.Token;
import in.costea.wiles.exceptions.AbstractCompilationException;
import in.costea.wiles.services.TokenTransmitter;
import org.jetbrains.annotations.NotNull;

import static in.costea.wiles.statics.Constants.ASSIGN_ID;

//TODO: check if left side is correct
public class LeftSideExpressionCommand extends AbstractExpressionCommand {

    public LeftSideExpressionCommand(@NotNull TokenTransmitter transmitter) {
        super(transmitter);
    }

    @Override
    protected boolean handleToken(@NotNull Token token) throws AbstractCompilationException {
        if(token.getContent().equals(ASSIGN_ID))
            return true;
        return super.handleToken(token);
    }

}
