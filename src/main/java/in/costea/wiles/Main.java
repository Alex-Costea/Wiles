package in.costea.wiles;

import in.costea.wiles.converters.InputToTokensConverter;
import in.costea.wiles.converters.TokensToSyntaxTreeConverter;
import in.costea.wiles.data.CompilationExceptionsCollection;
import in.costea.wiles.data.Token;
import in.costea.wiles.exceptions.CompilationFailedException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Main {

    private static final CompilationExceptionsCollection exceptions=new CompilationExceptionsCollection();
    public static void main(String[] args) {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        String input=null;
        try(InputStream is = classloader.getResourceAsStream("input.wiles")) {
            Objects.requireNonNull(is);
            input = new BufferedReader(new InputStreamReader(is))
                    .lines().collect(Collectors.joining("\n"));
        }
        catch (NullPointerException | IOException ex)
        {
            System.out.println("IO exception!");
        }

        List<Token> tokens = sourceToTokens(input);
        for(Token token:tokens)
            System.out.println(token);

        SyntaxTree syntaxTree =tokensToAST(tokens);
        System.out.println(syntaxTree);

        //Print exceptions
        if(exceptions.size()>0)
            throw new CompilationFailedException(exceptions);
    }

    public static List<Token> sourceToTokens(String input)
    {
        var converter=new InputToTokensConverter(input);
        List<Token> tokens= converter.convert();
        exceptions.add(converter.getExceptions());
        return tokens;
    }

    public static SyntaxTree tokensToAST(List<Token> tokens)
    {
        var converter=new TokensToSyntaxTreeConverter(tokens);
        SyntaxTree syntaxTree = converter.convert();
        exceptions.add(converter.getExceptions());
        return syntaxTree;
    }
}