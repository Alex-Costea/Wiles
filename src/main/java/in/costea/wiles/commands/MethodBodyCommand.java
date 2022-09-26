package in.costea.wiles.commands;

import in.costea.wiles.data.CompilationExceptionsCollection;
import in.costea.wiles.exceptions.CompilationException;
import in.costea.wiles.exceptions.UnexpectedTokenException;
import in.costea.wiles.services.TokenTransmitter;
import in.costea.wiles.statics.Constants;

import java.util.ArrayList;
import java.util.List;

import static in.costea.wiles.statics.Constants.*;

public class MethodBodyCommand extends SyntaxTree
{
    private final List<SyntaxTree> components = new ArrayList<>();
    private final CompilationExceptionsCollection exceptions = new CompilationExceptionsCollection();
    private final boolean standAlone;

    public MethodBodyCommand(TokenTransmitter transmitter, boolean standAlone)
    {
        super(transmitter);
        this.standAlone = standAlone;
    }

    @Override
    public Constants.SYNTAX_TYPE getType()
    {
        return Constants.SYNTAX_TYPE.METHOD_BODY;
    }

    @Override
    public List<? extends SyntaxTree> getComponents()
    {
        return components;
    }

    @Override
    public CompilationExceptionsCollection process()
    {
        while (!transmitter.tokensExhausted())
        {
            try
            {
                var token = transmitter.requestToken("");
                if (token.content().equals(END_BLOCK_ID) && !standAlone)
                    break;
                transmitter.removeToken();
                if (token.content().equals(NEWLINE_ID) || token.content().equals(FINISH_STATEMENT))
                    continue;
                OperationCommand operationCommand;
                if (unaryOperators.contains(token.content()) || !TOKENS.containsValue(token.content()))
                {
                    operationCommand = new OperationCommand(token, transmitter, false);
                }
                else if (standAlone && token.content().equals(DECLARE_METHOD_ID))
                    throw new UnexpectedTokenException("Cannot declare method in body-only mode!", token.location());
                else throw new UnexpectedTokenException(TOKENS_INVERSE.get(token.content()), token.location());
                CompilationExceptionsCollection newExceptions = operationCommand.process();
                exceptions.add(newExceptions);
                components.add(operationCommand);
                if (newExceptions.size() > 0)
                    readRestOfLineIgnoringErrors(!standAlone);
            }
            catch (CompilationException ex)
            {
                exceptions.add(ex);
                readRestOfLineIgnoringErrors(!standAlone);
            }
        }
        return exceptions;
    }
}
