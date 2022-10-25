package in.costea.wiles.statements.expressions;

import in.costea.wiles.builders.Context;
import in.costea.wiles.data.Token;
import in.costea.wiles.exceptions.AbstractCompilationException;
import org.jetbrains.annotations.NotNull;

import static in.costea.wiles.constants.Tokens.TERMINATORS;

public class DefaultExpression extends AbstractExpression {
    public DefaultExpression(@NotNull Context context) {
        super(context);
    }

    @Override
    protected boolean handleToken(@NotNull Token token) throws AbstractCompilationException {
        if(TERMINATORS.contains(token.getContent()))
            return true;
        return super.handleToken(token);
    }

}
