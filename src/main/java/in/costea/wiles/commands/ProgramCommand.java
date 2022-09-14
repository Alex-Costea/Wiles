package in.costea.wiles.commands;

import in.costea.wiles.converters.TokensToSyntaxTreeConverter;
import in.costea.wiles.data.CompilationExceptionsCollection;
import in.costea.wiles.data.Token;
import in.costea.wiles.exceptions.CompilationException;
import in.costea.wiles.exceptions.NonMethodInProgramException;
import in.costea.wiles.statics.Constants.SYNTAX_TYPE;

import java.util.ArrayList;
import java.util.List;

import static in.costea.wiles.statics.Constants.METHOD_DECLARATION_ID;
import static in.costea.wiles.statics.Constants.NEWLINE_ID;

public class ProgramCommand extends SyntaxTree {
    private final List<MethodCommand> components=new ArrayList<>();
    CompilationExceptionsCollection exceptions;

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
        exceptions=new CompilationExceptionsCollection();
        Token token;
        try
        {
            while(!converter.tokensExhausted())
            {
                token=converter.requestToken();
                converter.removeToken();

                if(token.content().equals(NEWLINE_ID))
                    continue;
                if(!token.content().equals(METHOD_DECLARATION_ID))
                    throw new NonMethodInProgramException("Method declaration expected!",token.location());

                var methodCommand=new MethodCommand(converter);
                exceptions.add(methodCommand.process());
                components.add(methodCommand);
            }
        }
        catch (CompilationException ex)
        {
            exceptions.add(ex);
        }
        return exceptions;
    }
}
