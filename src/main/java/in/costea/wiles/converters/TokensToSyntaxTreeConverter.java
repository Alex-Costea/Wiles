package in.costea.wiles.converters;

import in.costea.wiles.commands.SyntaxTree;
import in.costea.wiles.data.CompilationExceptionsCollection;
import in.costea.wiles.data.Token;
import in.costea.wiles.factories.SyntaxTreeFactory;
import in.costea.wiles.services.TokenTransmitter;
import in.costea.wiles.statics.Constants.SYNTAX_TYPE;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TokensToSyntaxTreeConverter {
    private final SyntaxTree syntaxTree;
    private final CompilationExceptionsCollection exceptions;

    public TokensToSyntaxTreeConverter(@NotNull List<Token> tokens)
    {
        TokenTransmitter tokenTransmitter = new TokenTransmitter(tokens);
        if(tokens.get(0).equals(new Token("DECLARE_METHOD")))
            syntaxTree =  SyntaxTreeFactory.of(SYNTAX_TYPE.PROGRAM, tokenTransmitter);
        else
        {
            //TODO: implement
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
}
