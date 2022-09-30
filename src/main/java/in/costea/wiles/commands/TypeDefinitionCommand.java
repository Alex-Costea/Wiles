package in.costea.wiles.commands;

import in.costea.wiles.data.CompilationExceptionsCollection;
import in.costea.wiles.data.Token;
import in.costea.wiles.exceptions.CompilationException;
import in.costea.wiles.exceptions.TokenExpectedException;
import in.costea.wiles.services.TokenTransmitter;
import in.costea.wiles.statics.Constants;

import java.util.ArrayList;
import java.util.List;

import static in.costea.wiles.builders.ExpectParamsBuilder.tokenOf;
import static in.costea.wiles.statics.Constants.TYPES;


public class TypeDefinitionCommand extends AbstractCommand
{
    private final List<AbstractCommand> components = new ArrayList<>();
    private final CompilationExceptionsCollection exceptions = new CompilationExceptionsCollection();

    public TypeDefinitionCommand(TokenTransmitter transmitter)
    {
        super(transmitter);
    }

    @Override
    public Constants.SYNTAX_TYPE getType()
    {
        return Constants.SYNTAX_TYPE.TYPE;
    }

    @Override
    public List<? extends AbstractCommand> getComponents()
    {
        return components;
    }

    @Override
    public CompilationExceptionsCollection process()
    {
        try
        {
            Token token = transmitter.expect(tokenOf(TYPES::containsKey).withErrorMessage("Type expected!"));
            name = TYPES.get(token.content());
            assert name!=null;
        }
        catch (CompilationException e)
        {
            exceptions.add(e);
            if (e instanceof TokenExpectedException)
                if (!transmitter.tokensExhausted())
                    transmitter.removeToken();
        }
        return exceptions;
    }
}
