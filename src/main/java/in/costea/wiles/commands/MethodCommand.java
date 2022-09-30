package in.costea.wiles.commands;

import in.costea.wiles.data.CompilationExceptionsCollection;
import in.costea.wiles.data.Token;
import in.costea.wiles.exceptions.CompilationException;
import in.costea.wiles.services.TokenTransmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static in.costea.wiles.builders.ExpectParamsBuilder.tokenOf;
import static in.costea.wiles.statics.Constants.*;

public class MethodCommand extends AbstractCommand
{
    private final List<AbstractCommand> components = new ArrayList<>();
    private final CompilationExceptionsCollection exceptions = new CompilationExceptionsCollection();

    public MethodCommand(TokenTransmitter transmitter)
    {
        super(transmitter);
    }

    public void setMethodName(String methodName)
    {
        name = methodName;
    }

    @Override
    public SYNTAX_TYPE getType()
    {
        return SYNTAX_TYPE.METHOD;
    }

    @Override
    public List<AbstractCommand> getComponents()
    {
        return components;
    }

    public void addNothingReturnType()
    {
        if (components.size() == 0 || components.get(0).getType() != SYNTAX_TYPE.TYPE)
        {
            var typeDefinitionCommand = new TypeDefinitionCommand(transmitter);
            typeDefinitionCommand.name = NOTHING_ID;
            components.add(0, typeDefinitionCommand);
        }
    }

    @Override
    public CompilationExceptionsCollection process()
    {
        try
        {
            name = transmitter.expect(tokenOf(IS_IDENTIFIER).withErrorMessage("Expected method name!")).
                    content().substring(1);

            //Parameters list
            transmitter.expect(tokenOf(ROUND_BRACKET_START_ID));
            Optional<Token> maybeToken;
            while ((maybeToken = transmitter.expectMaybe(tokenOf(IS_IDENTIFIER))).isPresent())
            {
                var parameterCommand = new ParameterCommand(transmitter, maybeToken.get());
                exceptions.add(parameterCommand.process());
                components.add(parameterCommand);
                if (transmitter.expectMaybe(tokenOf(COMMA_ID)).isEmpty())
                    break;
            }
            transmitter.expect(tokenOf(ROUND_BRACKET_END_ID));

            //Return type
            if (transmitter.expectMaybe(tokenOf(COLON_ID)).isPresent())
            {
                var typeDefinitionCommand = new TypeDefinitionCommand(transmitter);
                exceptions.add(typeDefinitionCommand.process());
                components.add(0, typeDefinitionCommand);
            }
            else
            {
                addNothingReturnType();
            }

            //Method body
            if (transmitter.expectMaybe(tokenOf(NOTHING_ID)).isPresent())
            {
                var MethodBodyCommand = new CodeBlockCommand(transmitter, false);
                components.add(MethodBodyCommand);
                return exceptions;
            }
            transmitter.expect(tokenOf(START_BLOCK_ID));
            var MethodBodyCommand = new CodeBlockCommand(transmitter, false);
            exceptions.add(MethodBodyCommand.process());
            components.add(MethodBodyCommand);
            transmitter.expect(tokenOf(END_BLOCK_ID));
        }
        catch (CompilationException ex)
        {
            exceptions.add(ex);
            transmitter.readUntilIgnoringErrors(x -> x.equals(END_BLOCK_ID));
            if (!transmitter.tokensExhausted())
                transmitter.removeToken();
        }
        return exceptions;
    }
}
