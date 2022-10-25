package in.costea.wiles.statements;

import in.costea.wiles.builders.Context;
import in.costea.wiles.data.CompilationExceptionsCollection;
import in.costea.wiles.enums.SyntaxType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ForStatement extends AbstractStatement{
    public ForStatement(@NotNull Context oldContext) {
        super(oldContext.setWithinLoop());
    }

    @NotNull
    @Override
    public SyntaxType getType() {
        return SyntaxType.FOR;
    }

    @NotNull
    @Override
    public List<AbstractStatement> getComponents() {
        return List.of();
    }

    @NotNull
    @Override
    public CompilationExceptionsCollection process() {
        return new CompilationExceptionsCollection();
    }
}
