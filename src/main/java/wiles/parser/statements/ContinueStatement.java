package wiles.parser.statements;

import wiles.parser.builders.Context;
import wiles.parser.data.CompilationExceptionsCollection;
import wiles.parser.enums.SyntaxType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ContinueStatement extends AbstractStatement{
    public ContinueStatement(@NotNull Context context) {
        super(context);
    }

    @NotNull
    @Override
    public SyntaxType getType() {
        return SyntaxType.CONTINUE;
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