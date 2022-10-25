package in.costea.wiles.statements;

import in.costea.wiles.builders.IsWithin;
import in.costea.wiles.data.CompilationExceptionsCollection;
import in.costea.wiles.enums.SyntaxType;
import in.costea.wiles.services.TokenTransmitter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BreakStatement extends AbstractStatement{
    public BreakStatement(@NotNull TokenTransmitter transmitter,@NotNull IsWithin within) {
        super(transmitter,within);
    }

    @NotNull
    @Override
    public SyntaxType getType() {
        return SyntaxType.BREAK;
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