package in.costea.wiles.statements.expressions;

import in.costea.wiles.builders.Context;
import in.costea.wiles.data.Token;
import in.costea.wiles.exceptions.AbstractCompilationException;
import in.costea.wiles.exceptions.InvalidStatementException;
import in.costea.wiles.services.PrecedenceProcessor;
import in.costea.wiles.statements.TokenStatement;
import org.jetbrains.annotations.NotNull;

import static in.costea.wiles.builders.ExpectParamsBuilder.tokenOf;
import static in.costea.wiles.constants.ErrorMessages.INVALID_LEFT_EXCEPTION;
import static in.costea.wiles.constants.Tokens.*;

public class TopLevelExpression extends AbstractExpression {
    protected boolean isAssignment=false;

    public TopLevelExpression(@NotNull Context context) {
        super(context);
    }

    private void checkValid(AbstractExpression exp) throws InvalidStatementException
    {
        if(exp.operation == null)
            return;
        if(exp.operation.name.equals(ACCESS_ID) || exp.operation.name.equals(ELEM_ACCESS_ID)) {
            if(exp.left instanceof AbstractExpression)
                checkValid((AbstractExpression) exp.left);
            if(exp.operation.name.equals(ACCESS_ID) && exp.right instanceof AbstractExpression)
                checkValid((AbstractExpression) exp.right);
        }
        else throw new InvalidStatementException(INVALID_LEFT_EXCEPTION, exp.operation.getToken().getLocation());
    }

    @Override
    protected void checkLeft() throws InvalidStatementException {
        if(!isAssignment)
            return;
        checkValid((AbstractExpression) this.left);
    }

    @Override
    protected void setComponents(@NotNull PrecedenceProcessor precedenceProcessor) {
        if(isAssignment)
            this.left = precedenceProcessor.getResult();
        else super.setComponents(precedenceProcessor);
    }

    @Override
    protected boolean handleToken(@NotNull Token token) throws AbstractCompilationException {
        if(TERMINATORS.contains(token.getContent()))
            return true;
        if (token.getContent().equals(ASSIGN_ID)) {
            operation = new TokenStatement(transmitter.expect(tokenOf(ASSIGN_ID)), getContext());
            right = new DefaultExpression(getContext());
            exceptions.addAll(right.process());
            isAssignment = true;
            return true;
        }
        return super.handleToken(token);
    }
}
