package in.costea.wiles.converters;

import in.costea.wiles.commands.SyntaxTree;
import in.costea.wiles.data.CompilationExceptionsCollection;
import in.costea.wiles.data.Token;
import in.costea.wiles.exceptions.UnexpectedEndException;
import in.costea.wiles.factories.SyntaxTreeFactory;
import in.costea.wiles.statics.Constants.SYNTAX_TYPE;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public class TokensToSyntaxTreeConverter {
    private final SyntaxTree syntaxTree;
    private final CompilationExceptionsCollection exceptions;
    private final LinkedList<Token> tokens;
    public TokensToSyntaxTreeConverter(@NotNull List<Token> tokens)
    {
        this.tokens=new LinkedList<>(tokens);
        if(tokens.get(0).equals(new Token("DECLARE_METHOD")))
            syntaxTree =  SyntaxTreeFactory.of(SYNTAX_TYPE.PROGRAM,this);
        else
        {
            throw new Error("Body-only mode not yet implemented!");
        }
        exceptions=new CompilationExceptionsCollection();
    }
    public SyntaxTree convert() {
        exceptions.add(syntaxTree.process());
        return syntaxTree;
    }

    public CompilationExceptionsCollection getExceptions() {
        return exceptions;
    }

    public Token requestToken()  throws UnexpectedEndException
    {
        if(tokensExhausted()) throw new UnexpectedEndException("Input ended unexpectedly!");
        return tokens.getFirst();
    }

    public void removeToken()  throws UnexpectedEndException
    {
        if(tokensExhausted()) throw new UnexpectedEndException("Input ended unexpectedly!");
        tokens.pop();
    }

    public boolean tokensExhausted(){return tokens.isEmpty();}
}
