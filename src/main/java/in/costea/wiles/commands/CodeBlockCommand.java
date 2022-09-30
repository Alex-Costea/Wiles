package in.costea.wiles.commands;

import in.costea.wiles.data.CompilationExceptionsCollection;
import in.costea.wiles.data.Token;
import in.costea.wiles.exceptions.CompilationException;
import in.costea.wiles.exceptions.UnexpectedTokenException;
import in.costea.wiles.services.TokenTransmitter;
import in.costea.wiles.statics.Constants;

import java.util.ArrayList;
import java.util.List;

import static in.costea.wiles.builders.ExpectParamsBuilder.*;
import static in.costea.wiles.statics.Constants.*;

public class CodeBlockCommand extends AbstractCommand
{
    private final List<OperationCommand> components = new ArrayList<>();
    private final CompilationExceptionsCollection exceptions = new CompilationExceptionsCollection();
    private final boolean standAlone;

    public CodeBlockCommand(TokenTransmitter transmitter, boolean standAlone)
    {
        super(transmitter);
        this.standAlone = standAlone;
    }

    @Override
    public Constants.SYNTAX_TYPE getType()
    {
        return Constants.SYNTAX_TYPE.CODE_BLOCK;
    }

    @Override
    public List<OperationCommand> getComponents()
    {
        return components;
    }

    private void readRestOfLineIgnoringErrors()
    {
        final boolean stopAtEndBlock = !standAlone;
        transmitter.readUntilIgnoringErrors(x -> STATEMENT_ENDERS.contains(x) || (stopAtEndBlock && x.equals(END_BLOCK_ID)));
    }

    @Override
    public CompilationExceptionsCollection process()
    {
        while (!transmitter.tokensExhausted())
        {
            try
            {
                Token token = transmitter.expect(REQUEST_FIRST_TOKEN);
                if (token.content().equals(END_BLOCK_ID) && !standAlone)
                    break;
                transmitter.removeToken();
                if (STATEMENT_ENDERS.contains(token.content()))
                    continue;
                OperationCommand operationCommand;
                boolean innerOperation=false;
                if (token.content().equals(ROUND_BRACKET_START_ID))
                {
                    token = transmitter.expect(tokenOf(ANYTHING).withErrorMessage("Unexpected operation end!"));
                    innerOperation=true;
                }
                if (UNARY_OPERATORS.contains(token.content()) || !TOKENS.containsValue(token.content()))
                {
                    operationCommand = new OperationCommand(token, transmitter, innerOperation);
                }
                else if (standAlone && token.content().equals(DECLARE_METHOD_ID))
                    throw new UnexpectedTokenException("Cannot declare method in body-only mode!", token.location());
                else throw new UnexpectedTokenException(TOKENS_INVERSE.get(token.content()), token.location());
                CompilationExceptionsCollection newExceptions = operationCommand.process();
                exceptions.add(newExceptions);
                components.add(operationCommand);
                if (newExceptions.size() > 0)
                    readRestOfLineIgnoringErrors();
            }
            catch (CompilationException ex)
            {
                exceptions.add(ex);
                readRestOfLineIgnoringErrors();
            }
        }
        return exceptions;
    }
}
