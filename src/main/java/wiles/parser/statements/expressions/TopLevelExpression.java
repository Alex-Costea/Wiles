package wiles.parser.statements.expressions;

import wiles.parser.builders.Context;
import wiles.parser.data.Token;
import wiles.parser.exceptions.AbstractCompilationException;
import wiles.parser.exceptions.InvalidStatementException;
import wiles.parser.services.PrecedenceProcessor;
import wiles.parser.statements.TokenStatement;
import org.jetbrains.annotations.NotNull;
import wiles.parser.constants.ErrorMessages;

import static wiles.parser.builders.ExpectParamsBuilder.tokenOf;
import static wiles.parser.constants.Tokens.*;

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
        else throw new InvalidStatementException(ErrorMessages.INVALID_LEFT_EXCEPTION, exp.operation.getToken().getLocation());
    }

    @Override
    protected void checkValid() throws InvalidStatementException {
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
        if (token.getContent().equals(ASSIGN_ID)) {
            operation = new TokenStatement(transmitter.expect(tokenOf(ASSIGN_ID)), getContext());
            var new_right = new DefaultExpression(getContext());
            exceptions.addAll(new_right.process());
            right = new_right;
            isAssignment = true;
            return true;
        }
        return super.handleToken(token);
    }
}
