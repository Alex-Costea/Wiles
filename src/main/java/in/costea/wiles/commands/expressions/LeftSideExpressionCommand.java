package in.costea.wiles.commands.expressions;

import in.costea.wiles.data.Token;
import in.costea.wiles.exceptions.AbstractCompilationException;
import in.costea.wiles.services.PrecedenceProcessor;
import in.costea.wiles.services.TokenTransmitter;
import org.jetbrains.annotations.NotNull;

import static in.costea.wiles.statics.Constants.ASSIGN_ID;

public class LeftSideExpressionCommand extends AbstractExpressionCommand {


    public LeftSideExpressionCommand(@NotNull TokenTransmitter transmitter) {
        super(transmitter);
        checkValid();
    }

    private void checkValid() {
        //TODO: check if valid LeftSide
    }

    @Override
    protected boolean shouldBreakOnToken(@NotNull Token token, @NotNull PrecedenceProcessor precedenceProcessor) throws AbstractCompilationException {
        if(token.getContent().equals(ASSIGN_ID))
        {
            checkValid();
            return true;
        }
        return super.shouldBreakOnToken(token,precedenceProcessor);
    }

}
