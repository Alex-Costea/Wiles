package in.costea.wiles.commands;

import in.costea.wiles.data.CompilationExceptionsCollection;
import in.costea.wiles.data.Token;
import in.costea.wiles.enums.SyntaxType;
import in.costea.wiles.exceptions.AbstractCompilationException;
import in.costea.wiles.services.TokenTransmitter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static in.costea.wiles.builders.ExpectParamsBuilder.tokenOf;
import static in.costea.wiles.statics.Constants.COLON_ID;

public class ParameterCommand extends AbstractCommand {
    private final TokenCommand tokenCommand;
    private final CompilationExceptionsCollection exceptions = new CompilationExceptionsCollection();
    private TypeDefinitionCommand typeDefinition;

    public ParameterCommand(TokenTransmitter transmitter, Token firstToken) {
        super(transmitter);
        tokenCommand = new TokenCommand(transmitter, firstToken);
    }

    @Override
    public @NotNull SyntaxType getType() {
        return SyntaxType.DECLARATION;
    }

    @Override
    public @NotNull List<AbstractCommand> getComponents() {
        return List.of(tokenCommand, typeDefinition);
    }

    @Override
    public @NotNull CompilationExceptionsCollection process() {
        try {
            transmitter.expect(tokenOf(COLON_ID));
            typeDefinition = new TypeDefinitionCommand(transmitter);
            exceptions.addAll(typeDefinition.process());
        } catch (AbstractCompilationException e) {
            exceptions.add(e);
        }
        return exceptions;
    }
}
