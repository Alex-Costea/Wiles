package wiles.parser.statements.expressions;

import wiles.parser.builders.Context;
import wiles.parser.data.Token;
import org.jetbrains.annotations.NotNull;
import wiles.parser.exceptions.TokenExpectedException;
import wiles.parser.exceptions.UnexpectedEndException;

import static wiles.parser.builders.ExpectParamsBuilder.tokenOf;
import static wiles.parser.constants.ErrorMessages.INTERNAL_ERROR;
import static wiles.parser.constants.Tokens.PAREN_END_ID;

public class InnerExpression extends AbstractExpression {
    public InnerExpression(@NotNull Context context) {
        super(context);
    }

    {
        isInner = true;
    }

    @Override
    protected boolean handleToken(@NotNull Token token) throws TokenExpectedException, UnexpectedEndException {
        if(token.getContent().equals(PAREN_END_ID)) {
            transmitter.expect(tokenOf(PAREN_END_ID).withErrorMessage(INTERNAL_ERROR));
            return true;
        }
        return false;
    }

}