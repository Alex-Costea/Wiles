package wiles.parser.statements.expressions;

import wiles.parser.builders.Context;
import wiles.parser.data.Token;
import wiles.parser.exceptions.AbstractCompilationException;
import org.jetbrains.annotations.NotNull;
import wiles.parser.constants.Tokens;

public class RightSideInMethodCallExpression extends AbstractExpression {
    public RightSideInMethodCallExpression(@NotNull Context context) {
        super(context);
    }

    @Override
    protected boolean handleToken(@NotNull Token token) throws AbstractCompilationException {
        if(token.getContent().equals(Tokens.SEPARATOR_ID) || token.getContent().equals(Tokens.PAREN_END_ID))
            return true;
        return super.handleToken(token);
    }

}
