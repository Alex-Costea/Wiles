package in.costea.wiles.statements.expressions;

import in.costea.wiles.builders.Context;
import in.costea.wiles.data.Token;
import in.costea.wiles.exceptions.AbstractCompilationException;
import in.costea.wiles.exceptions.InvalidStatementException;
import in.costea.wiles.services.PrecedenceProcessor;
import in.costea.wiles.statements.AbstractStatement;
import in.costea.wiles.statements.TokenStatement;
import org.jetbrains.annotations.NotNull;

import static in.costea.wiles.builders.ExpectParamsBuilder.tokenOf;
import static in.costea.wiles.constants.ErrorMessages.IDENTIFIER_EXPECTED_ERROR;
import static in.costea.wiles.constants.Predicates.IS_IDENTIFIER;
import static in.costea.wiles.constants.Tokens.*;

public class InsideMethodCallExpression extends AbstractExpression{

    protected boolean isAssignment=false;
    private boolean lastExpression=false;

    public boolean isLastExpression() {
        return lastExpression;
    }

    public InsideMethodCallExpression(@NotNull Context context) {
        super(context);
    }

    protected void setComponents(@NotNull PrecedenceProcessor precedenceProcessor) {
        if(isAssignment)
            this.left = precedenceProcessor.getResult();
        else super.setComponents(precedenceProcessor);
    }

    @Override
    protected void checkValid() throws AbstractCompilationException {
        if(isAssignment) {
            if (left.getComponents().size() != 1)
                throw new InvalidStatementException(IDENTIFIER_EXPECTED_ERROR, operation.getToken().getLocation());
            AbstractStatement first = left.getComponents().get(0);
            if (!(first instanceof TokenStatement) || !IS_IDENTIFIER.test(first.name))
                throw new InvalidStatementException(IDENTIFIER_EXPECTED_ERROR, operation.getToken().getLocation());
        }
    }

    @Override
    protected boolean handleToken(@NotNull Token token) throws AbstractCompilationException {
        if(token.getContent().equals(PAREN_END_ID))
            return lastExpression = true;
        if(token.getContent().equals(SEPARATOR_ID))
            return true;
        if (token.getContent().equals(ASSIGN_ID)) {
            operation = new TokenStatement(transmitter.expect(tokenOf(ASSIGN_ID)), getContext());
            right = new EndAtSeparatorExpression(getContext());
            exceptions.addAll(right.process());
            isAssignment = true;
            return true;
        }
        return super.handleToken(token);
    }
}
