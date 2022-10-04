package in.costea.wiles.commands;

import in.costea.wiles.data.CompilationExceptionsCollection;
import in.costea.wiles.data.Token;
import in.costea.wiles.enums.SyntaxType;
import in.costea.wiles.exceptions.AbstractCompilationException;
import in.costea.wiles.services.TokenTransmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static in.costea.wiles.builders.ExpectParamsBuilder.tokenOf;
import static in.costea.wiles.statics.Constants.*;

public class MethodCommand extends AbstractCommand {
    private final List<ParameterCommand> parameters = new ArrayList<>();
    private final CompilationExceptionsCollection exceptions = new CompilationExceptionsCollection();
    //private final List<AbstractCommand> components = new ArrayList<>();
    private TypeDefinitionCommand returnType;
    private CodeBlockCommand methodBody;

    public MethodCommand(TokenTransmitter transmitter) {
        super(transmitter);
        returnType = new TypeDefinitionCommand(transmitter);
        methodBody = new CodeBlockCommand(transmitter, false);
        returnType.name = NOTHING_ID;
    }

    public void setMethodName(String methodName) {
        name = methodName;
    }

    @Override
    public SyntaxType getType() {
        return SyntaxType.METHOD;
    }

    public void setMethodBody(CodeBlockCommand methodBody) {
        this.methodBody = methodBody;
    }

    @Override
    public List<AbstractCommand> getComponents() {
        final ArrayList<AbstractCommand> components = new ArrayList<>();
        components.add(returnType);
        components.addAll(parameters);
        components.add(methodBody);
        return components;
    }

    @Override
    public CompilationExceptionsCollection process() {
        try {
            name = transmitter.expect(tokenOf(IS_IDENTIFIER).withErrorMessage("Expected method name!"))
                    .getContent().substring(1);

            //Parameters list
            transmitter.expect(tokenOf(ROUND_BRACKET_START_ID));
            Optional<Token> maybeToken;
            while ((maybeToken = transmitter.expectMaybe(tokenOf(IS_IDENTIFIER))).isPresent()) {
                var parameterCommand = new ParameterCommand(transmitter, maybeToken.get());
                exceptions.addAll(parameterCommand.process());
                parameters.add(parameterCommand);
                if (transmitter.expectMaybe(tokenOf(COMMA_ID)).isEmpty())
                    break;
            }
            transmitter.expect(tokenOf(ROUND_BRACKET_END_ID));

            //Return type
            if (transmitter.expectMaybe(tokenOf(COLON_ID)).isPresent()) {
                returnType = new TypeDefinitionCommand(transmitter);
                exceptions.addAll(returnType.process());
            }

            //Read body
            exceptions.addAll(methodBody.process());
        } catch (AbstractCompilationException ex) {
            exceptions.add(ex);
        }
        return exceptions;
    }
}
