package in.costea.wiles;

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
        try(InputStream is = classloader.getResourceAsStream("input.wiles")) {
            Objects.requireNonNull(is);
            String input = new BufferedReader(new InputStreamReader(is))
                    .lines().collect(Collectors.joining("\n"));
            wilesToJava(input);

            //Print exceptions
            if(exceptions.size()>0)
                throw new CompilationFailedException(exceptions);
        }
        catch (NullPointerException | IOException ex)
        {
            System.out.println("IO exception!");
        }
    }

    public static void wilesToJava(String input)
    {
        var converter=new TokensConverter(input);
        List<String> tokens= converter.convert();
        System.out.println(tokens);
        exceptions.add(converter.getExceptions());
    }
}