package wiles.parser.statements;

import org.jetbrains.annotations.NotNull;
import wiles.parser.builders.Context;
import wiles.shared.CompilationExceptionsCollection;
import wiles.shared.SyntaxType;

import java.util.List;

public class BreakStatement extends AbstractStatement{
    public BreakStatement(@NotNull Context context) {
        super(context);
    }

    @NotNull
    @Override
    public SyntaxType getSyntaxType() {
        return SyntaxType.BREAK;
    }

    @Override
    public @NotNull List<AbstractStatement> getComponents() {
        return List.of();
    }

    @NotNull
    @Override
    public CompilationExceptionsCollection process() {
        return new CompilationExceptionsCollection();
    }
}
