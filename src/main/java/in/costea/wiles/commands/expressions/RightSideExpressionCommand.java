package in.costea.wiles.commands.expressions;

import in.costea.wiles.data.Token;
import in.costea.wiles.exceptions.AbstractCompilationException;
import in.costea.wiles.services.PrecedenceProcessor;
import in.costea.wiles.services.TokenTransmitter;
import org.jetbrains.annotations.NotNull;

import static in.costea.wiles.builders.ExpectParamsBuilder.isContainedIn;
import static in.costea.wiles.statics.Constants.END_BLOCK_ID;
import static in.costea.wiles.statics.Constants.TERMINATORS;

public class RightSideExpressionCommand extends AbstractExpressionCommand {
    public RightSideExpressionCommand(@NotNull TokenTransmitter transmitter) {
        super(transmitter);
    }

    @Override
    protected boolean shouldBreakOnToken(@NotNull Token token, @NotNull PrecedenceProcessor precedenceProcessor) throws AbstractCompilationException {
        if(isContainedIn(TERMINATORS).test(token.getContent()))
            return true;
        if(token.getContent().equals(END_BLOCK_ID))
            return true;
        return super.shouldBreakOnToken(token,precedenceProcessor);
    }

}
