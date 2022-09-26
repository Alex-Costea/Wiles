package in.costea.wiles.commands;

import in.costea.wiles.data.CompilationExceptionsCollection;
import in.costea.wiles.data.Token;
import in.costea.wiles.exceptions.CompilationException;
import in.costea.wiles.services.TokenTransmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static in.costea.wiles.statics.Constants.*;

public class MethodCommand extends SyntaxTree
{
    private final List<SyntaxTree> components = new ArrayList<>();
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
    public List<SyntaxTree> getComponents()
    {
        return components;
    }

    public void addNothingReturnType()
    {
        var typeDefinitionCommand = new TypeDefinitionCommand(transmitter);
        typeDefinitionCommand.name=NOTHING_ID;
        components.add(0, typeDefinitionCommand);
    }

    @Override
    public CompilationExceptionsCollection process()
    {
        try
        {
            name = expect(x -> x.length() > 1 && x.startsWith(IDENTIFIER_START), "Expected method name!").
                    content().substring(1);

            //Parameters list
            expect(ROUND_BRACKET_START_ID);
            Optional<Token> maybeToken;
            while ((maybeToken = expectMaybe(x -> x.startsWith(IDENTIFIER_START))).isPresent())
            {
                var parameterCommand = new ParameterCommand(transmitter, maybeToken.get());
                exceptions.add(parameterCommand.process());
                components.add(parameterCommand);
                if (expectMaybe("COMMA").isEmpty())
                    break;
            }
            expect(ROUND_BRACKET_END_ID);

            //Return type
            if (expectMaybe(COLON_ID).isPresent())
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
            expect(START_BLOCK_ID);
            var MethodBodyCommand = new CodeBlockCommand(transmitter, false);
            exceptions.add(MethodBodyCommand.process());
            components.add(MethodBodyCommand);
            expect(END_BLOCK_ID);
        }
        catch (CompilationException ex)
        {
            exceptions.add(ex);
            readUntilIgnoringErrors(x -> x.equals(END_BLOCK_ID));
            if (!transmitter.tokensExhausted())
                transmitter.removeToken();
        }
        return exceptions;
    }
}
