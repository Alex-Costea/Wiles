package wiles.parser.statements.expressions;

import wiles.parser.builders.Context;
import wiles.shared.Token;
import org.jetbrains.annotations.NotNull;
import wiles.parser.exceptions.TokenExpectedException;
import wiles.parser.exceptions.UnexpectedEndException;

import static wiles.parser.builders.ExpectParamsBuilder.tokenOf;
import static wiles.shared.constants.ErrorMessages.INTERNAL_ERROR;
import static wiles.shared.constants.Tokens.PAREN_END_ID;

public class InnerExpression extends AbstractExpression {
    public InnerExpression(@NotNull Context oldContext) {
        super(oldContext.setWithinInnerExpression(true));
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
