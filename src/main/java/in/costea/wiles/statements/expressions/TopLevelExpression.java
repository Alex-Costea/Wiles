package in.costea.wiles.statements.expressions;

import in.costea.wiles.builders.IsWithin;
import in.costea.wiles.statements.TokenStatement;
import in.costea.wiles.data.Token;
import in.costea.wiles.exceptions.AbstractCompilationException;
import in.costea.wiles.services.PrecedenceProcessor;
import in.costea.wiles.services.TokenTransmitter;
import org.jetbrains.annotations.NotNull;

import static in.costea.wiles.builders.ExpectParamsBuilder.tokenOf;
import static in.costea.wiles.constants.Tokens.*;

public class TopLevelExpression extends AbstractExpression {
    protected boolean isAssignment=false;

    public TopLevelExpression(@NotNull TokenTransmitter transmitter, @NotNull IsWithin within) {
        super(transmitter,within);
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
            operation = new TokenStatement(transmitter, transmitter.expect(tokenOf(ASSIGN_ID)),getWithin());
            right = new DefaultExpression(transmitter,getWithin());
            exceptions.addAll(right.process());
            isAssignment = true;
            return true;
        }
        return super.handleToken(token);
    }
}
