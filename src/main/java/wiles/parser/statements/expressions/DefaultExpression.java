package wiles.parser.statements.expressions;

import org.jetbrains.annotations.NotNull;
import wiles.parser.builders.Context;
import wiles.parser.constants.Tokens;
import wiles.parser.data.Token;
import wiles.parser.exceptions.AbstractCompilationException;

public class DefaultExpression extends AbstractExpression {
    public DefaultExpression(@NotNull Context context) {
        super(context);
    }

    @Override
    protected boolean handleToken(@NotNull Token token) throws AbstractCompilationException {
        if(Tokens.TERMINATORS.contains(token.getContent()))
            return true;
        return super.handleToken(token);
    }

}
