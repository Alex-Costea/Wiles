package in.costea.wiles;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.json.JsonMapper;
import in.costea.wiles.commands.ProgramCommand;
import in.costea.wiles.converters.InputToTokensConverter;
import in.costea.wiles.converters.TokensToSyntaxTreeConverter;
import in.costea.wiles.data.CompilationExceptionsCollection;
import in.costea.wiles.data.Token;
import in.costea.wiles.exceptions.CompilationFailed;

import java.io.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Main {

    private static final CompilationExceptionsCollection exceptions = new CompilationExceptionsCollection();

    private Main() {
    }

    public static void main(String[] args) throws IOException {
        String input = loadFile();
        List<Token> tokens = sourceToTokens(input);
        System.out.print("Tokens: ");
        System.out.println(tokens.stream().map(Token::getContent).toList());
        ProgramCommand AST = tokensToAST(tokens);
        JsonMapper mapper = JsonMapper.builder().disable(MapperFeature.AUTO_DETECT_CREATORS).
                disable(MapperFeature.AUTO_DETECT_FIELDS).disable(MapperFeature.AUTO_DETECT_GETTERS).
                disable(MapperFeature.AUTO_DETECT_IS_GETTERS).build();

        System.out.print("Syntax tree: ");
        System.out.println(AST);

        //Print exceptions
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        writer.writeValue(new File("syntaxtree.json"), AST);
        if (exceptions.size() > 0)
            throw new CompilationFailed(exceptions);
    }

    private static String loadFile() {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        String input;
        try (InputStream is = classloader.getResourceAsStream("input.wiles")) {
            Objects.requireNonNull(is);
            input = new BufferedReader(new InputStreamReader(is))
                    .lines().collect(Collectors.joining("\n"));
        } catch (NullPointerException | IOException ex) {
            throw new Error("Error loading input file!");
        }
        return input;
    }

    private static List<Token> sourceToTokens(String input) {
        var converter = new InputToTokensConverter(input);
        List<Token> tokens = converter.convert();
        exceptions.addAll(converter.getExceptions());
        return tokens;
    }

    private static ProgramCommand tokensToAST(List<Token> tokens) {
        var converter = new TokensToSyntaxTreeConverter(tokens);
        ProgramCommand programCommand = converter.convert();
        exceptions.addAll(converter.getExceptions());
        programCommand.setCompiledSuccessfully(exceptions.size() == 0);
        return programCommand;
    }
}