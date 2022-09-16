package in.costea.wiles.commands;

import in.costea.wiles.data.CompilationExceptionsCollection;
import in.costea.wiles.data.Token;
import in.costea.wiles.exceptions.CompilationException;
import in.costea.wiles.exceptions.TokenExpectedException;
import in.costea.wiles.services.TokenTransmitter;
import in.costea.wiles.statics.Constants;

import java.util.ArrayList;
import java.util.List;

import static in.costea.wiles.statics.Constants.OPERATORS;

public class BinaryOperationCommand extends SyntaxTree {
    private final List<SyntaxTree> components=new ArrayList<>();
    private final Token token1,token2;
    private String id1,operator,id2;
    private final CompilationExceptionsCollection exceptions=new CompilationExceptionsCollection();

    public BinaryOperationCommand(Token token1, Token token2, TokenTransmitter transmitter) {
        super(transmitter);
        this.token1=token1;
        this.token2=token2;
    }

    @Override
    public Constants.SYNTAX_TYPE getType() {
        return Constants.SYNTAX_TYPE.IDENTIFIER;
    }

    @Override
    public List<SyntaxTree> getComponents() {
        return components;
    }

    @Override
    public CompilationExceptionsCollection process() {
        try {
            this.id1=token1.content();
            this.operator = token2.content();
            if (!OPERATORS.containsValue(operator))
                throw new TokenExpectedException("Operator expected!", token2.location());
            //TODO: order of operations
            Token token3=expect((String x)->x.startsWith("!") || x.startsWith("@") || x.startsWith("#"),"Identifier expected!");
            id2=token3.content();
        }
        catch(CompilationException ex)
        {
            exceptions.add(ex);
        }
        return exceptions;
    }

    @Override
    public String toString() {
        return id1+" "+operator+" "+id2;
    }
}
