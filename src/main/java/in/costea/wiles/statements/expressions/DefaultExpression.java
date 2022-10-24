package in.costea.wiles.statements.expressions;

import in.costea.wiles.data.Token;
import in.costea.wiles.exceptions.AbstractCompilationException;
import in.costea.wiles.services.TokenTransmitter;
import org.jetbrains.annotations.NotNull;

import static in.costea.wiles.constants.Tokens.END_BLOCK_ID;
import static in.costea.wiles.constants.Tokens.TERMINATORS;

public class DefaultExpression extends AbstractExpression {
    public DefaultExpression(@NotNull TokenTransmitter transmitter) {
        super(transmitter);
    }

    @Override
    protected boolean handleToken(@NotNull Token token) throws AbstractCompilationException {
        if(TERMINATORS.contains(token.getContent()))
            return true;
        if(token.getContent().equals(END_BLOCK_ID))
            return true;
        return super.handleToken(token);
    }

}