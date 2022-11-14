package wiles.parser.statements.expressions;

import org.jetbrains.annotations.NotNull;
import wiles.parser.builders.Context;
import wiles.parser.data.Token;
import wiles.parser.enums.WhenRemoveToken;
import wiles.shared.AbstractCompilationException;
import wiles.parser.services.PrecedenceProcessor;
import wiles.parser.statements.TokenStatement;

import static wiles.parser.builders.ExpectParamsBuilder.tokenOf;
import static wiles.shared.constants.Tokens.ASSIGN_ID;
import static wiles.shared.constants.Tokens.PAREN_END_ID;

public class InsideMethodCallExpression extends AbstractExpression{

    protected boolean isAssignment=false;
    private boolean lastExpression=false;

    public boolean isLastExpression() {
        return lastExpression;
    }

    public InsideMethodCallExpression(@NotNull Context oldContext) {
        super(oldContext.setWithinInnerExpression(true));
    }

    {
        isInner = true;
    }

    protected void setComponents(@NotNull PrecedenceProcessor precedenceProcessor) {
        if(isAssignment)
            this.left = precedenceProcessor.getResult();
        else super.setComponents(precedenceProcessor);
    }

    @Override
    protected void checkValid() throws AbstractCompilationException {
        lastExpression = transmitter.expectMaybe(tokenOf(PAREN_END_ID).removeWhen(WhenRemoveToken.Never)).isPresent();
        if(isAssignment)
            checkLeftIsOneIdentifier();
    }

    @Override
    protected boolean handleToken(@NotNull Token token) throws AbstractCompilationException {
        if (token.getContent().equals(ASSIGN_ID)) {
            operation = new TokenStatement(transmitter.expect(tokenOf(ASSIGN_ID)), getContext());
            var new_right = new InnerDefaultExpression(getContext());
            exceptions.addAll(new_right.process());
            right = new_right;
            isAssignment = true;
            return true;
        }
        return super.handleToken(token);
    }
}
