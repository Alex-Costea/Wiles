package in.costea.wiles.converters;

import in.costea.wiles.commands.CodeBlockCommand;
import in.costea.wiles.commands.MethodCommand;
import in.costea.wiles.commands.ProgramCommand;
import in.costea.wiles.data.CompilationExceptionsCollection;
import in.costea.wiles.data.Token;
import in.costea.wiles.exceptions.CompilationException;
import in.costea.wiles.services.RemoveTokenEnum;
import in.costea.wiles.services.TokenTransmitter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static in.costea.wiles.statics.Constants.DECLARE_METHOD_ID;
import static in.costea.wiles.statics.Constants.MAIN_METHOD_NAME;

public class TokensToSyntaxTreeConverter
{
    private final CompilationExceptionsCollection exceptions;
    private final boolean bodyOnlyMode;
    private final TokenTransmitter tokenTransmitter;

    public TokensToSyntaxTreeConverter(@NotNull List<Token> tokens)
    {
        tokenTransmitter = new TokenTransmitter(tokens);
        exceptions = new CompilationExceptionsCollection();

        boolean bodyOnlyMode;
        try {
            tokenTransmitter.expect(x->x.equals(DECLARE_METHOD_ID),"", RemoveTokenEnum.Never);
            bodyOnlyMode = false;
        } catch (CompilationException e) {
            bodyOnlyMode = true;
        }
        this.bodyOnlyMode = bodyOnlyMode;
    }

    public ProgramCommand convert()
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
        else
        {
            ProgramCommand syntaxTree = new ProgramCommand(tokenTransmitter);
            exceptions.add(syntaxTree.process());
            return syntaxTree;
        }
    }

    public CompilationExceptionsCollection getExceptions()
    {
        return exceptions;
    }
}
