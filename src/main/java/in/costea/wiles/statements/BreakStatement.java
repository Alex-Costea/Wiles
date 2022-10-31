package in.costea.wiles.statements;

import in.costea.wiles.builders.Context;
import in.costea.wiles.data.CompilationExceptionsCollection;
import in.costea.wiles.enums.SyntaxType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BreakStatement extends AbstractStatement{
    public BreakStatement(@NotNull Context context) {
        super(context);
    }

    @NotNull
    @Override
    public SyntaxType getType() {
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
