package in.costea.wiles.commands;

import in.costea.wiles.data.CompilationExceptionsCollection;
import in.costea.wiles.enums.WhenRemoveToken;
import in.costea.wiles.exceptions.AbstractCompilationException;
import in.costea.wiles.exceptions.UnexpectedTokenException;
import in.costea.wiles.services.TokenTransmitter;
import in.costea.wiles.statics.Constants;

import java.util.ArrayList;
import java.util.List;

import static in.costea.wiles.builders.ExpectParamsBuilder.*;
import static in.costea.wiles.commands.ExpressionCommand.*;
import static in.costea.wiles.statics.Constants.*;

public class CodeBlockCommand extends AbstractCommand
{
    private final List<ExpressionCommand> components = new ArrayList<>();
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
    public List<ExpressionCommand> getComponents()
    {
        return components;
    }

    private void readOneStatement() throws AbstractCompilationException {
        if (transmitter.expectMaybe(tokenOf(isContainedIn(STATEMENT_TERMINATORS)).dontIgnoreNewLine()).isPresent())
            return;
        ExpressionCommand expressionCommand;
        boolean innerExpression = transmitter.expectMaybe(tokenOf(ROUND_BRACKET_START_ID)).isPresent();

        var optionalToken=transmitter.expectMaybe(tokenOf(isContainedIn(UNARY_OPERATORS)).or(IS_LITERAL));
        if (optionalToken.isPresent())
            expressionCommand = new ExpressionCommand(optionalToken.get(), transmitter, innerExpression?INSIDE_ROUND:REGULAR);
        else
        {
            optionalToken=transmitter.expectMaybe(tokenOf(DECLARE_METHOD_ID));
            if (standAlone && optionalToken.isPresent())
                throw new UnexpectedTokenException("Cannot declare method in body-only mode!", optionalToken.get().location());
            else
            {
                var token=transmitter.expect(tokenOf(ANYTHING));
                throw new UnexpectedTokenException(TOKENS_INVERSE.get(token.content()), token.location());
            }
        }

        CompilationExceptionsCollection newExceptions = expressionCommand.process();
        if(newExceptions.size()>0)
            throw newExceptions.get(0);

        components.add(expressionCommand);
    }

    @Override
    public CompilationExceptionsCollection process()
    {
        try
        {
            if (!standAlone && transmitter.expectMaybe(tokenOf(DO_ID)).isPresent())
            {
                if(transmitter.expectMaybe(tokenOf(NOTHING_ID)).isEmpty())
                {
                    readOneStatement();
                }
                return exceptions;
            }
            else
            {
                if(!standAlone)
                    transmitter.expect(tokenOf(START_BLOCK_ID));
                while(!transmitter.tokensExhausted())
                {
                    if(!standAlone && transmitter.expectMaybe(tokenOf(END_BLOCK_ID).removeTokenWhen(WhenRemoveToken.Never)).isPresent())
                        break;
                    readOneStatement();
                }
                if(!standAlone) transmitter.expect(tokenOf(END_BLOCK_ID));
            }
        }
        catch (AbstractCompilationException ex)
        {
            exceptions.add(ex);
        }
        return exceptions;
    }
}
