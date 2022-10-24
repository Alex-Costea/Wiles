package in.costea.wiles.statements.expressions;

import in.costea.wiles.data.Token;
import in.costea.wiles.services.TokenTransmitter;
import org.jetbrains.annotations.NotNull;

import static in.costea.wiles.constants.Tokens.ROUND_BRACKET_END_ID;

public class InnerExpression extends AbstractExpression {
    public InnerExpression(@NotNull TokenTransmitter transmitter) {
        super(transmitter);
    }

    @Override
    protected boolean handleToken(@NotNull Token token) {

        return token.getContent().equals(ROUND_BRACKET_END_ID);
    }

}
