package in.costea.wiles.commands;

import in.costea.wiles.data.CompilationExceptionsCollection;
import in.costea.wiles.data.Token;
import in.costea.wiles.exceptions.CompilationException;
import in.costea.wiles.services.TokenTransmitter;
import in.costea.wiles.statics.Constants;

import java.util.ArrayList;
import java.util.List;

import static in.costea.wiles.statics.Constants.*;

public class OperationCommand extends SyntaxTree {
    private final List<SyntaxTree> components=new ArrayList<>();
    private final CompilationExceptionsCollection exceptions=new CompilationExceptionsCollection();
    private boolean expectOperatorNext;

    public OperationCommand(Token firstToken, TokenTransmitter transmitter,boolean expectOperatorNext) {
        super(transmitter);
        components.add(new TokenCommand(transmitter,firstToken));
        this.expectOperatorNext=expectOperatorNext;
    }

    @Override
    public Constants.SYNTAX_TYPE getType() {
        return SYNTAX_TYPE.OPERATION;
    }

    @Override
    public List<SyntaxTree> getComponents() {
        return components;
    }

    @Override
    public CompilationExceptionsCollection process() {
        while(!transmitter.tokensExhausted())
        {
            try
            {
                Token token=transmitter.requestToken("Token expected!");
                String content=token.content();
                if (content.equals(END_BLOCK_ID))
                    break;
                if(expectOperatorNext && (content.equals(FINISH_STATEMENT) || content.equals(NEWLINE_ID)))
                    break;
                if(content.equals(ROUND_BRACKET_START_ID))
                {
                    //TODO: implement
                    throw new Error("Not yet implemented!");
                }
                if (expectOperatorNext)
                {
                    token = expect(OPERATORS::containsValue, "Operator expected!");
                    expectOperatorNext = false;
                }
                else
                {
                    token = expect((String x) -> x.startsWith("!") || x.startsWith("@") || x.startsWith("#"), "Identifier expected!");
                    expectOperatorNext = true;
                }
                components.add(new TokenCommand(transmitter,token));
            }
            catch(CompilationException ex)
            {
                exceptions.add(ex);
                break;
            }
            //TODO: process order of operations
        }
        return exceptions;
    }
}
