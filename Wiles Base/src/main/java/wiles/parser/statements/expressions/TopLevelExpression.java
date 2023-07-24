package wiles.parser.statements.expressions;

import org.jetbrains.annotations.NotNull;
import wiles.parser.builders.ParserContext;
import wiles.parser.services.PrecedenceProcessor;
import wiles.parser.statements.TokenStatement;
import wiles.shared.AbstractCompilationException;
import wiles.shared.Token;

import static wiles.parser.builders.ExpectParamsBuilder.tokenOf;
import static wiles.shared.constants.ErrorMessages.INTERNAL_ERROR;
import static wiles.shared.constants.Tokens.ASSIGN_ID;

public class TopLevelExpression extends AbstractExpression {
    public boolean isAssignment=false;

    public TopLevelExpression(@NotNull ParserContext context) {
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
        @NotNull String content = token.getContent();
        if (content.equals(ASSIGN_ID)) {
            operation = new TokenStatement(transmitter.expect(
                    tokenOf(ASSIGN_ID).withErrorMessage(INTERNAL_ERROR)), getContext());
            var new_right = new DefaultExpression(getContext());
            exceptions.addAll(new_right.process());
            right = new_right;
            isAssignment = true;
            return true;
        }
        return super.handleToken(token);
    }
}
