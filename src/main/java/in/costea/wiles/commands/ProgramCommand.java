package in.costea.wiles.commands;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import in.costea.wiles.data.CompilationExceptionsCollection;
import in.costea.wiles.enums.SyntaxType;
import in.costea.wiles.exceptions.AbstractCompilationException;
import in.costea.wiles.services.TokenTransmitter;

import java.util.ArrayList;
import java.util.List;

import static in.costea.wiles.builders.ExpectParamsBuilder.tokenOf;
import static in.costea.wiles.statics.Constants.DECLARE_METHOD_ID;

public class ProgramCommand extends AbstractCommand {
    private final List<MethodCommand> components = new ArrayList<>();
    private final CompilationExceptionsCollection exceptions = new CompilationExceptionsCollection();
    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected Boolean compiledSuccessfully = null;

    public ProgramCommand(TokenTransmitter transmitter) {
        super(transmitter);
    }

    @Override
    public SyntaxType getType() {
        return SyntaxType.PROGRAM;
    }

    @Override
    public List<MethodCommand> getComponents() {
        return components;
    }

    public void addMethod(MethodCommand command) {
        components.add(command);
    }

    @Override
    public CompilationExceptionsCollection process() {
        try {
            while (!transmitter.tokensExhausted()) {
                transmitter.expect(tokenOf(DECLARE_METHOD_ID));
                var methodCommand = new MethodCommand(transmitter);
                exceptions.addAll(methodCommand.process());
                components.add(methodCommand);
            }
        } catch (AbstractCompilationException ex) {
            exceptions.add(ex);
        }
        return exceptions;
    }

    public void setCompiledSuccessfully(boolean compiledSuccessfully) {
        this.compiledSuccessfully = compiledSuccessfully;
    }
}
