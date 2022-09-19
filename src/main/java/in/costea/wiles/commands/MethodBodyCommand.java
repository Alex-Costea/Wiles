package in.costea.wiles.commands;

import in.costea.wiles.data.CompilationExceptionsCollection;
import in.costea.wiles.exceptions.CompilationException;
import in.costea.wiles.exceptions.TokenExpectedException;
import in.costea.wiles.services.TokenTransmitter;
import in.costea.wiles.statics.Constants;

import java.util.ArrayList;
import java.util.List;

import static in.costea.wiles.statics.Constants.*;

public class MethodBodyCommand extends SyntaxTree {
    private final List<SyntaxTree> components=new ArrayList<>();
    private final CompilationExceptionsCollection exceptions=new CompilationExceptionsCollection();
    public MethodBodyCommand(TokenTransmitter transmitter) {
        super(transmitter);
    }

    @Override
    public Constants.SYNTAX_TYPE getType() {
        return Constants.SYNTAX_TYPE.METHOD_BODY;
    }

    @Override
    public List<? extends SyntaxTree> getComponents() {
        return components;
    }

    @Override
    public CompilationExceptionsCollection process() {
        while(!transmitter.tokensExhausted())
        {
            try
            {
                var token = transmitter.requestToken("Input ended unexpectedly!");
                if (token.content().equals(END_BLOCK_ID))
                    break;
                transmitter.removeToken();
                if (token.content().equals(NEWLINE_ID) || token.content().equals(FINISH_STATEMENT))
                    continue;
                OperationCommand operationCommand;
                if (token.content().startsWith(IDENTIFIER_START)) {
                    operationCommand = new OperationCommand(token,transmitter,true);
                }
                else if(token.content().equals(PLUS)||token.content().equals(MINUS))
                {
                    operationCommand = new OperationCommand(token,transmitter,false);
                }
                else throw new TokenExpectedException("Identifier or unary operator expected!",token.location());
                CompilationExceptionsCollection newExceptions=operationCommand.process();
                exceptions.add(newExceptions);
                components.add(operationCommand);
                if(newExceptions.size()>0)
                    readRestOfLineIgnoringErrors();
            }
            catch(CompilationException ex)
            {
                exceptions.add(ex);
                readRestOfLineIgnoringErrors();
            }
        }
        return exceptions;
    }
}
