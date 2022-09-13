package in.costea.wiles.factories;

import in.costea.wiles.SyntaxTree;
import in.costea.wiles.commands.MethodCommand;
import in.costea.wiles.commands.ProgramCommand;
import in.costea.wiles.statics.Constants.*;

public class SyntaxTreeFactory {
    private SyntaxTreeFactory() {}

    public static SyntaxTree of(SYNTAX_TYPE type)  {
        return switch (type) {
            case PROGRAM -> new ProgramCommand();
            case METHOD -> new MethodCommand();
            default -> throw new Error("Not yet implemented!");
        };
    }
}
