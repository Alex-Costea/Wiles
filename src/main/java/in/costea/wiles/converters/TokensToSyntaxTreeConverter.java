package in.costea.wiles.converters;

import in.costea.wiles.commands.CodeBlockCommand;
import in.costea.wiles.commands.MethodCommand;
import in.costea.wiles.commands.ProgramCommand;
import in.costea.wiles.commands.SyntaxTree;
import in.costea.wiles.data.CompilationExceptionsCollection;
import in.costea.wiles.data.Token;
import in.costea.wiles.services.TokenTransmitter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static in.costea.wiles.statics.Constants.*;

public class TokensToSyntaxTreeConverter
{
    private final SyntaxTree syntaxTree;
    private final CompilationExceptionsCollection exceptions;
    private final boolean bodyOnlyMode;
    private final TokenTransmitter tokenTransmitter;

    public TokensToSyntaxTreeConverter(@NotNull List<Token> tokens)
    {
        SyntaxTree syntaxTree;
        tokenTransmitter = new TokenTransmitter(tokens);
        exceptions = new CompilationExceptionsCollection();
        while (!tokenTransmitter.tokensExhausted() && tokenTransmitter.requestTokenAssertNotEmpty().content().equals(NEWLINE_ID))
            tokenTransmitter.removeToken();
        bodyOnlyMode = tokenTransmitter.tokensExhausted() || !tokenTransmitter.requestTokenAssertNotEmpty().content().equals(DECLARE_METHOD_ID);
        if (bodyOnlyMode)
            syntaxTree = new CodeBlockCommand(tokenTransmitter, true);
        else syntaxTree = new ProgramCommand(tokenTransmitter);
        this.syntaxTree = syntaxTree;
    }

    public SyntaxTree convert()
    {
        if (bodyOnlyMode)
        {
            var programCommand = new ProgramCommand(tokenTransmitter);
            var methodCommand = new MethodCommand(tokenTransmitter);
            methodCommand.setMethodName(MAIN_METHOD_NAME);
            var methodBodyCommand = new CodeBlockCommand(tokenTransmitter, true);
            methodCommand.getComponents().add(methodBodyCommand);
            methodCommand.addNothingReturnType();
            programCommand.getComponents().add(methodCommand);
            exceptions.add(methodBodyCommand.process());
            return programCommand;
        }
        else exceptions.add(syntaxTree.process());
        return syntaxTree;
    }

    public CompilationExceptionsCollection getExceptions()
    {
        return exceptions;
    }
}
