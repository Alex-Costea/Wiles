package in.costea.wiles.commands;

import in.costea.wiles.data.CompilationExceptionsCollection;
import in.costea.wiles.data.Token;
import in.costea.wiles.enums.SyntaxType;
import in.costea.wiles.exceptions.AbstractCompilationException;
import in.costea.wiles.services.TokenTransmitter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static in.costea.wiles.builders.ExpectParamsBuilder.isContainedIn;
import static in.costea.wiles.builders.ExpectParamsBuilder.tokenOf;
import static in.costea.wiles.statics.Constants.TYPES;


public class TypeDefinitionCommand extends AbstractCommand {
    private final CompilationExceptionsCollection exceptions = new CompilationExceptionsCollection();

    public TypeDefinitionCommand(TokenTransmitter transmitter) {
        super(transmitter);
    }

    @Override
    public @NotNull SyntaxType getType() {
        return SyntaxType.TYPE;
    }

    @Override
    public @NotNull List<AbstractCommand> getComponents() {
        return new ArrayList<>();
    }

    @Override
    public @NotNull CompilationExceptionsCollection process() {
        try {
            Token token = transmitter.expect(tokenOf(isContainedIn(TYPES.keySet())).withErrorMessage("Type expected!"));
            name = TYPES.get(token.getContent());
            assert name != null;
        } catch (AbstractCompilationException e) {
            exceptions.add(e);
        }
        return exceptions;
    }
}
