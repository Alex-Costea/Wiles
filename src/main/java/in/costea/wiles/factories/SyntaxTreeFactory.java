package in.costea.wiles.factories;

import in.costea.wiles.commands.MethodCommand;
import in.costea.wiles.commands.ProgramCommand;
import in.costea.wiles.commands.SyntaxTree;
import in.costea.wiles.services.TokenTransmitter;
import in.costea.wiles.statics.Constants.SYNTAX_TYPE;

public class SyntaxTreeFactory {
    private SyntaxTreeFactory() {}

    public static SyntaxTree of(SYNTAX_TYPE type, TokenTransmitter transmitter)  {
        return switch (type) {
            case PROGRAM -> new ProgramCommand(transmitter);
            case METHOD -> new MethodCommand(transmitter);
            default -> throw new Error("Not yet implemented!");
        };
    }
}
