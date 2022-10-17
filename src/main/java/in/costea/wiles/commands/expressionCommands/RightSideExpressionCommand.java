package in.costea.wiles.commands.expressionCommands;

import in.costea.wiles.builders.ExpectParamsBuilder;
import in.costea.wiles.data.Token;
import in.costea.wiles.services.TokenTransmitter;
import org.jetbrains.annotations.NotNull;

import static in.costea.wiles.builders.ExpectParamsBuilder.isContainedIn;
import static in.costea.wiles.builders.ExpectParamsBuilder.tokenOf;
import static in.costea.wiles.statics.Constants.STATEMENT_TERMINATORS;

public class RightSideExpressionCommand extends ExpressionCommand {
    @Override
    protected ExpectParamsBuilder expressionFinalized() {
        return tokenOf(isContainedIn(STATEMENT_TERMINATORS)).dontIgnoreNewLine();
    }

    public RightSideExpressionCommand(@NotNull TokenTransmitter transmitter) {
        super(transmitter);
    }

    @Override
    protected boolean handleEndTokenReceived(Token token) {
        return true;
    }
}
