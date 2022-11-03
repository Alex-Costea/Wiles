package wiles.parser.statements.expressions;

import wiles.parser.builders.Context;
import wiles.parser.data.Token;
import org.jetbrains.annotations.NotNull;

import static wiles.parser.constants.Tokens.PAREN_END_ID;

public class InnerExpression extends AbstractExpression {
    public InnerExpression(@NotNull Context context) {
        super(context);
    }

    @Override
    protected boolean handleToken(@NotNull Token token) {
        return token.getContent().equals(PAREN_END_ID);
    }

}
