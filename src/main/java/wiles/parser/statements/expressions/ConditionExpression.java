package wiles.parser.statements.expressions;

import org.jetbrains.annotations.NotNull;
import wiles.parser.builders.Context;
import wiles.parser.builders.ExpectParamsBuilder;
import wiles.shared.Token;
import wiles.shared.AbstractCompilationException;
import wiles.parser.services.PrecedenceProcessor;
import wiles.parser.statements.TokenStatement;
import wiles.parser.statements.TypeDefinitionStatement;

import static wiles.shared.constants.Tokens.IS_ID;

public class ConditionExpression extends AbstractExpression{

    protected boolean isTypeCheck = false;

    public ConditionExpression(@NotNull Context oldContext) {
        super(oldContext);
    }

    protected void setComponents(@NotNull PrecedenceProcessor precedenceProcessor) {
        if(isTypeCheck)
            this.left = precedenceProcessor.getResult();
        else super.setComponents(precedenceProcessor);
    }

    @Override
    protected boolean handleToken(@NotNull Token token) throws AbstractCompilationException
    {
        if(token.getContent().equals(IS_ID))
        {
            operation = new TokenStatement(transmitter.expect(ExpectParamsBuilder.tokenOf(IS_ID)), getContext());
            var new_right = new TypeDefinitionStatement(getContext());
            exceptions.addAll(new_right.process());
            right = new_right;
            isTypeCheck = true;
            return true;
        }
        return super.handleToken(token);
    }
}
