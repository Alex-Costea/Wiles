package wiles.parser.statements.expressions;

import wiles.parser.builders.Context;
import wiles.parser.data.Token;
import wiles.parser.exceptions.AbstractCompilationException;
import wiles.parser.exceptions.InvalidStatementException;
import wiles.parser.services.PrecedenceProcessor;
import wiles.parser.statements.AbstractStatement;
import wiles.parser.statements.TokenStatement;
import org.jetbrains.annotations.NotNull;
import wiles.parser.builders.ExpectParamsBuilder;
import wiles.parser.constants.ErrorMessages;
import wiles.parser.constants.Predicates;
import wiles.parser.constants.Tokens;

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
        if(isAssignment) {
            if (left.getComponents().size() != 1)
                throw new InvalidStatementException(ErrorMessages.IDENTIFIER_EXPECTED_ERROR, operation.getToken().getLocation());
            AbstractStatement first = left.getComponents().get(0);
            if (!(first instanceof TokenStatement) || !Predicates.IS_IDENTIFIER.test(first.name))
                throw new InvalidStatementException(ErrorMessages.IDENTIFIER_EXPECTED_ERROR, operation.getToken().getLocation());
        }
    }

    @Override
    protected boolean handleToken(@NotNull Token token) throws AbstractCompilationException {
        if(token.getContent().equals(Tokens.PAREN_END_ID))
            return lastExpression = true;
        if(token.getContent().equals(Tokens.SEPARATOR_ID))
            return true;
        if (token.getContent().equals(Tokens.ASSIGN_ID)) {
            operation = new TokenStatement(transmitter.expect(ExpectParamsBuilder.tokenOf(Tokens.ASSIGN_ID)), getContext());
            var new_right = new RightSideInMethodCallExpression(getContext());
            exceptions.addAll(new_right.process());
            handledEOL = new_right.handledEOL;
            right = new_right;
            return true;
        }
        return super.handleToken(token);
    }
}
