package in.costea.wiles.commands.expressions;

import in.costea.wiles.commands.TokenCommand;
import in.costea.wiles.data.Token;
import in.costea.wiles.exceptions.AbstractCompilationException;
import in.costea.wiles.services.PrecedenceProcessor;
import in.costea.wiles.services.TokenTransmitter;
import org.jetbrains.annotations.NotNull;

import static in.costea.wiles.builders.ExpectParamsBuilder.isContainedIn;
import static in.costea.wiles.builders.ExpectParamsBuilder.tokenOf;
import static in.costea.wiles.statics.Constants.*;

public class AssignableExpressionCommand extends AbstractExpressionCommand {
    protected boolean isAssignment=false;

    public AssignableExpressionCommand(@NotNull TokenTransmitter transmitter) {
        super(transmitter);
    }

    @Override
    protected void setComponents(@NotNull PrecedenceProcessor precedenceProcessor) {
        if(isAssignment)
            this.left = precedenceProcessor.getResult();
        else super.setComponents(precedenceProcessor);
    }

    @Override
    protected boolean handleToken(@NotNull Token token) throws AbstractCompilationException {
        if(isContainedIn(TERMINATORS).test(token.getContent()))
            return true;
        if(token.getContent().equals(END_BLOCK_ID))
            return true;
        if (token.getContent().equals(ASSIGN_ID)) {
            operation = new TokenCommand(transmitter, transmitter.expect(tokenOf(ASSIGN_ID)));
            right = new RightSideExpressionCommand(transmitter);
            exceptions.addAll(right.process());
            isAssignment = true;
            return true;
        }
        return super.handleToken(token);
    }
}
