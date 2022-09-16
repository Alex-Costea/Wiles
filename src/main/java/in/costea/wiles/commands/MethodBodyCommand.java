package in.costea.wiles.commands;

import in.costea.wiles.data.CompilationExceptionsCollection;
import in.costea.wiles.exceptions.UnexpectedEndException;
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
            try {
                var token = transmitter.requestToken("Input ended unexpectedly!");
                if(token.content().equals(END_BLOCK_ID))
                    break;
                transmitter.removeToken();
                if(token.content().equals(NEWLINE_ID))
                    continue;
                if(token.content().startsWith(IDENTIFIER_START))
                {
                    var identifier=new Identifier(token.content(),transmitter);
                    exceptions.add(identifier.process());
                    components.add(identifier);
                }
            }
            catch(UnexpectedEndException ex)
            {
                exceptions.add(ex);
                break;
            }
        }
        return exceptions;
    }
}
