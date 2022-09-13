package in.costea.wiles.factories;

import in.costea.wiles.commands.SyntaxTree;
import in.costea.wiles.commands.MethodCommand;
import in.costea.wiles.commands.ProgramCommand;
import in.costea.wiles.converters.TokensToSyntaxTreeConverter;
import in.costea.wiles.statics.Constants.*;

public class SyntaxTreeFactory {
    private SyntaxTreeFactory() {}

    public static SyntaxTree of(SYNTAX_TYPE type, TokensToSyntaxTreeConverter converter)  {
        return switch (type) {
            case PROGRAM -> new ProgramCommand(converter);
            case METHOD -> new MethodCommand(converter);
            default -> throw new Error("Not yet implemented!");
        };
    }
}
