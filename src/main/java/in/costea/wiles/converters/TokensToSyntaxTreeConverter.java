package in.costea.wiles.converters;

import in.costea.wiles.commands.MethodBodyCommand;
import in.costea.wiles.commands.ProgramCommand;
import in.costea.wiles.commands.SyntaxTree;
import in.costea.wiles.data.CompilationExceptionsCollection;
import in.costea.wiles.data.Token;
import in.costea.wiles.services.TokenTransmitter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static in.costea.wiles.statics.Constants.DECLARE_METHOD_ID;
import static in.costea.wiles.statics.Constants.NEWLINE_ID;

public class TokensToSyntaxTreeConverter
{
    private final SyntaxTree syntaxTree;
    private final CompilationExceptionsCollection exceptions;

    public TokensToSyntaxTreeConverter(@NotNull List<Token> tokens)
    {
        SyntaxTree syntaxTree;
        TokenTransmitter tokenTransmitter = new TokenTransmitter(tokens);
        exceptions = new CompilationExceptionsCollection();
        while (!tokenTransmitter.tokensExhausted() && tokenTransmitter.requestTokenAssertNotEmpty().content().equals(NEWLINE_ID))
            tokenTransmitter.removeToken();
        if (!tokenTransmitter.tokensExhausted() && tokenTransmitter.requestTokenAssertNotEmpty().content().equals(DECLARE_METHOD_ID))
            syntaxTree = new ProgramCommand(tokenTransmitter);
        else
        {
            syntaxTree = new MethodBodyCommand(tokenTransmitter, true);
        }
        this.syntaxTree = syntaxTree;
    }

    public SyntaxTree convert()
    {
        exceptions.add(syntaxTree.process());
        return syntaxTree;
    }

    public CompilationExceptionsCollection getExceptions()
    {
        return exceptions;
    }
}
