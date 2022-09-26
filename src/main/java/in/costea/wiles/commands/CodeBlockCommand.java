package in.costea.wiles.commands;

import in.costea.wiles.data.CompilationExceptionsCollection;
import in.costea.wiles.data.Token;
import in.costea.wiles.exceptions.CompilationException;
import in.costea.wiles.exceptions.UnexpectedTokenException;
import in.costea.wiles.services.TokenTransmitter;
import in.costea.wiles.statics.Constants;

import java.util.ArrayList;
import java.util.List;

import static in.costea.wiles.statics.Constants.*;

public class CodeBlockCommand extends SyntaxTree
{
    private final List<SyntaxTree> components = new ArrayList<>();
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
    public List<? extends SyntaxTree> getComponents()
    {
        return components;
    }

    private void readRestOfLineIgnoringErrors()
    {
        final boolean stopAtEndBlock = !standAlone;
        readUntilIgnoringErrors(x -> STATEMENT_ENDERS.contains(x) || (stopAtEndBlock && x.equals(END_BLOCK_ID)));
    }

    @Override
    public CompilationExceptionsCollection process()
    {
        while (!transmitter.tokensExhausted())
        {
            try
            {
                Token token = transmitter.requestToken("");
                if (token.content().equals(END_BLOCK_ID) && !standAlone)
                    break;
                transmitter.removeToken();
                if (STATEMENT_ENDERS.contains(token.content()))
                    continue;
                OperationCommand operationCommand;
                if (UNARY_OPERATORS.contains(token.content()) || !TOKENS.containsValue(token.content()))
                {
                    operationCommand = new OperationCommand(token, transmitter, false);
                }
                else if (token.content().equals(ROUND_BRACKET_START_ID))
                {
                    Token newToken = expect((x) -> true, "Unexpected operation end!");
                    operationCommand = new OperationCommand(newToken, transmitter, true);
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
