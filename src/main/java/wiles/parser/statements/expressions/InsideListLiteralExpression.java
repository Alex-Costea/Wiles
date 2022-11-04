package wiles.parser.statements.expressions;

import wiles.parser.builders.Context;
import wiles.parser.data.Token;
import wiles.parser.exceptions.AbstractCompilationException;
import org.jetbrains.annotations.NotNull;

import static wiles.parser.constants.Tokens.BRACKET_END_ID;
import static wiles.parser.constants.Tokens.SEPARATOR_ID;

public class InsideListLiteralExpression extends AbstractExpression {
    public InsideListLiteralExpression(@NotNull Context oldContext) {
        super(oldContext.setWithinInnerExpression(true));
    }

    {
        isInner = true;
    }
    @Override
    protected boolean handleToken(@NotNull Token token) throws AbstractCompilationException {
        if(token.getContent().equals(SEPARATOR_ID) || token.getContent().equals(BRACKET_END_ID))
            return true;
        return super.handleToken(token);
    }

}
