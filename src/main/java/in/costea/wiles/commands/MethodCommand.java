package in.costea.wiles.commands;

import in.costea.wiles.data.CompilationExceptionsCollection;
import in.costea.wiles.data.Token;
import in.costea.wiles.exceptions.CompilationException;
import in.costea.wiles.services.TokenTransmitter;

import java.util.ArrayList;
import java.util.List;

import static in.costea.wiles.statics.Constants.*;

public class MethodCommand extends SyntaxTree {
    private final List<SyntaxTree> components=new ArrayList<>();
    private String methodName;
    CompilationExceptionsCollection exceptions=new CompilationExceptionsCollection();

    public MethodCommand(TokenTransmitter transmitter) {
        super(transmitter);
    }

    @Override
    public SYNTAX_TYPE getType() {
        return SYNTAX_TYPE.METHOD;
    }

    @Override
    public List<SyntaxTree> getComponents() {
        return components;
    }

    @Override
    public CompilationExceptionsCollection process() {
        Token token;
        try {
            token = expect(x->x.length()>1 && x.startsWith(IDENTIFIER_START),"Expected method name!");
            methodName=token.content().substring(1);
            expect(x->x.equals(ROUND_BRACKET_START_ID),"Parenthesis expected!");
            //TODO: method declaration
            expect(x->x.equals(ROUND_BRACKET_END_ID),"Parenthesis expected!");
            expect(x->x.equals(START_BLOCK_ID),"Start block expected!");
            while(!(token = transmitter.requestToken()).content().equals(END_BLOCK_ID))
            {
                //TODO: method body
                components.add(new Identifier(token.content(), transmitter));
                transmitter.removeToken();
            }
            transmitter.removeToken();
        }
        catch (CompilationException ex)
        {
            exceptions.add(ex);
        }
        return exceptions;
    }

    @Override
    public String toString() {
        return super.toString(methodName!=null?methodName:"");
    }
}
