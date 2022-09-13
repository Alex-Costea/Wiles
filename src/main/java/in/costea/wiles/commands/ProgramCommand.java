package in.costea.wiles.commands;

import in.costea.wiles.converters.TokensToSyntaxTreeConverter;
import in.costea.wiles.data.CompilationExceptionsCollection;
import in.costea.wiles.data.Token;
import in.costea.wiles.exceptions.CompilationException;
import in.costea.wiles.exceptions.NonMethodInProgramException;
import in.costea.wiles.exceptions.UnexpectedEndException;
import in.costea.wiles.statics.Constants.SYNTAX_TYPE;

import java.util.ArrayList;
import java.util.List;

public class ProgramCommand extends SyntaxTree {
    private final List<MethodCommand> components=new ArrayList<>();

    public ProgramCommand(TokensToSyntaxTreeConverter converter) {
        super(converter);
    }

    @Override
    public SYNTAX_TYPE getType() {
        return SYNTAX_TYPE.PROGRAM;
    }

    @Override
    public List<MethodCommand> getComponents() {
        return components;
    }

    @Override
    public CompilationExceptionsCollection process() {
        CompilationExceptionsCollection exceptions=new CompilationExceptionsCollection();
        while(true)
        {
            try
            {
                if(converter.tokensExhausted())
                    break;

                Token currentToken=converter.requestToken();
                converter.removeToken();

                if(currentToken.content().equals("NEWLINE"))
                    continue;
                if(!currentToken.content().equals("DECLARE_METHOD"))
                    throw new NonMethodInProgramException("Method declaration expected!",currentToken.location());

                var methodCommand=new MethodCommand(converter);
                exceptions.add(methodCommand.process());
                components.add(methodCommand);
            }
            catch (CompilationException ex)
            {
                exceptions.add(ex);
                if(ex instanceof UnexpectedEndException)
                    break;
            }
        }
        return exceptions;
    }
}
