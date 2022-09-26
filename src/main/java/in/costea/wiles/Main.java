package in.costea.wiles;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.json.JsonMapper;
import in.costea.wiles.commands.SyntaxTree;
import in.costea.wiles.converters.InputToTokensConverter;
import in.costea.wiles.converters.TokensToSyntaxTreeConverter;
import in.costea.wiles.data.CompilationExceptionsCollection;
import in.costea.wiles.data.Token;
import in.costea.wiles.exceptions.CompilationFailedException;

import java.io.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Main
{

    private static final CompilationExceptionsCollection exceptions = new CompilationExceptionsCollection();

    private Main()
    {
    }

    public static void main(String[] args) throws IOException
    {
        String input = loadFile();
        List<Token> tokens = sourceToTokens(input);
        System.out.print("Tokens: ");
        System.out.println(tokens.stream().map(Token::content).toList());
        SyntaxTree syntaxTree = tokensToAST(tokens);
        JsonMapper mapper = JsonMapper.builder().disable(MapperFeature.AUTO_DETECT_CREATORS).
                disable(MapperFeature.AUTO_DETECT_FIELDS).disable(MapperFeature.AUTO_DETECT_GETTERS).
                disable(MapperFeature.AUTO_DETECT_IS_GETTERS).build();

        String JSON=mapper.writerWithDefaultPrettyPrinter().writeValueAsString(syntaxTree);
        System.out.println("Syntax tree:");
        System.out.println(JSON);

        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        writer.writeValue(new File("syntaxtree.json"),syntaxTree);

        //Print exceptions
        if (exceptions.size() > 0)
            throw new CompilationFailedException(exceptions);
    }

    private static String loadFile()
    {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        String input;
        try (InputStream is = classloader.getResourceAsStream("input.wiles"))
        {
            Objects.requireNonNull(is);
            input = new BufferedReader(new InputStreamReader(is))
                    .lines().collect(Collectors.joining("\n"));
        }
        catch (NullPointerException | IOException ex)
        {
            throw new Error("Error loading input file!");
        }
        return input;
    }

    public static List<Token> sourceToTokens(String input)
    {
        var converter = new InputToTokensConverter(input);
        List<Token> tokens = converter.convert();
        exceptions.add(converter.getExceptions());
        return tokens;
    }

    public static SyntaxTree tokensToAST(List<Token> tokens)
    {
        var converter = new TokensToSyntaxTreeConverter(tokens);
        SyntaxTree syntaxTree = converter.convert();
        exceptions.add(converter.getExceptions());
        return syntaxTree;
    }
}