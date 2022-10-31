package in.costea.wiles.statements.expressions;

import in.costea.wiles.builders.Context;
import in.costea.wiles.data.Token;
import in.costea.wiles.exceptions.AbstractCompilationException;
import org.jetbrains.annotations.NotNull;

import static in.costea.wiles.constants.Tokens.ROUND_BRACKET_END_ID;
import static in.costea.wiles.constants.Tokens.SEPARATOR_ID;

public class EndAtSeparatorExpression extends AbstractExpression {
    public EndAtSeparatorExpression(@NotNull Context context) {
        super(context);
    }

    @Override
    protected boolean handleToken(@NotNull Token token) throws AbstractCompilationException {
        if(token.getContent().equals(SEPARATOR_ID) || token.getContent().equals(ROUND_BRACKET_END_ID))
            return true;
        return super.handleToken(token);
    }

}
