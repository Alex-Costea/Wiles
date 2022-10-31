package in.costea.wiles.statements.expressions;

import in.costea.wiles.builders.Context;
import in.costea.wiles.data.Token;
import org.jetbrains.annotations.NotNull;

import static in.costea.wiles.constants.Tokens.BRACKET_END_ID;

public class InnerExpression extends AbstractExpression {
    public InnerExpression(@NotNull Context context) {
        super(context);
    }

    @Override
    protected boolean handleToken(@NotNull Token token) {
        return token.getContent().equals(BRACKET_END_ID);
    }

}
