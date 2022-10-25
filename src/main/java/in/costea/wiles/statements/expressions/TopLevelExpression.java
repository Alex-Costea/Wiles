package in.costea.wiles.statements.expressions;

import in.costea.wiles.builders.Context;
import in.costea.wiles.data.Token;
import in.costea.wiles.exceptions.AbstractCompilationException;
import in.costea.wiles.services.PrecedenceProcessor;
import in.costea.wiles.statements.TokenStatement;
import org.jetbrains.annotations.NotNull;

import static in.costea.wiles.builders.ExpectParamsBuilder.tokenOf;
import static in.costea.wiles.constants.Tokens.ASSIGN_ID;
import static in.costea.wiles.constants.Tokens.TERMINATORS;

public class TopLevelExpression extends AbstractExpression {
    protected boolean isAssignment=false;

    public TopLevelExpression(@NotNull Context context) {
        super(context);
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
