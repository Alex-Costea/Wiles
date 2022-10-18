package in.costea.wiles.commands.expressions;

import in.costea.wiles.commands.AbstractCommand;
import in.costea.wiles.data.CompilationExceptionsCollection;
import in.costea.wiles.services.TokenTransmitter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BinaryExpressionCommand extends AbstractExpressionCommand{
    public BinaryExpressionCommand(@NotNull TokenTransmitter transmitter, @NotNull List<AbstractCommand> components) {
        super(transmitter);
        this.components.addAll(components);
    }

    @Override
    public @NotNull CompilationExceptionsCollection process() {
        throw new IllegalStateException("Cannot be processed!");
    }
}
