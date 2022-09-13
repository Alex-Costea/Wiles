package in.costea.wiles.commands;

import in.costea.wiles.converters.TokensToSyntaxTreeConverter;
import in.costea.wiles.data.CompilationExceptionsCollection;
import in.costea.wiles.data.Token;
import in.costea.wiles.exceptions.IdentifierExpectedException;
import in.costea.wiles.exceptions.KeywordExpectedException;
import in.costea.wiles.exceptions.UnexpectedEndException;
import in.costea.wiles.statics.Constants.SYNTAX_TYPE;

import java.util.ArrayList;
import java.util.List;

public class MethodCommand extends SyntaxTree {
    private final List<SyntaxTree> components=new ArrayList<>();
    private String methodName;

    public MethodCommand(TokensToSyntaxTreeConverter converter) {
        super(converter);
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
        CompilationExceptionsCollection exceptions=new CompilationExceptionsCollection();
        Token token;
        try {
            token = converter.requestToken();
            if(token.content().length()<2 || token.content().charAt(0)!='!')
            {
                exceptions.add(new IdentifierExpectedException("Expected method name!",token.location()));
                return exceptions;
            }
            converter.removeToken();
            methodName=token.content().substring(1);

            //TODO: method body

            token = converter.requestToken();
            if(!token.content().equals("START_BLOCK"))
            {
                exceptions.add(new KeywordExpectedException("Start block expected!",token.location()));
                return exceptions;
            }
            converter.removeToken();

            //TODO: things between begin and end

            while(!(token = converter.requestToken()).content().equals("END_BLOCK"))
            {
                components.add(new Identifier(token.content(),converter));
                converter.removeToken();
            }
            converter.removeToken();
        }
        catch (UnexpectedEndException ex)
        {
            exceptions.add(ex);
        }
        return exceptions;
    }

    @Override
    public String toString() {
        return super.toString(methodName!=null?" "+methodName+" ":"");
    }
}
