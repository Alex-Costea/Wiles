package wiles.parser.statements.expressions;

import org.jetbrains.annotations.NotNull;
import wiles.parser.builders.Context;
import wiles.parser.builders.ExpectParamsBuilder;
import wiles.parser.data.Token;
import wiles.parser.exceptions.AbstractCompilationException;
import wiles.parser.services.PrecedenceProcessor;
import wiles.parser.statements.TokenStatement;

import static wiles.parser.constants.Tokens.*;

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
        if(isAssignment)
            checkLeftIsOneIdentifier();
    }

    @Override
    protected boolean handleToken(@NotNull Token token) throws AbstractCompilationException {
        if(token.getContent().equals(PAREN_END_ID))
            lastExpression = true;
        if (token.getContent().equals(ASSIGN_ID)) {
            operation = new TokenStatement(transmitter.expect(ExpectParamsBuilder.tokenOf(ASSIGN_ID)), getContext());
            var new_right = new InnerDefaultExpression(getContext());
            exceptions.addAll(new_right.process());
            right = new_right;
            isAssignment = true;
            return true;
        }
        return super.handleToken(token);
    }
}
