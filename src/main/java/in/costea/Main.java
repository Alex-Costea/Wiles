package in.costea;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.stream.Collectors;

public class Main {


    public static void main(String[] args) {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        try(InputStream is = classloader.getResourceAsStream("input.wiles")) {
            Objects.requireNonNull(is);
            String input = new BufferedReader(new InputStreamReader(is))
                    .lines().collect(Collectors.joining("\n"));
            wilesToJava(input);
        }
        catch (NullPointerException | IOException ex)
        {
            System.out.println("IO exception!");
        }
    }

    public static void wilesToJava(String input)
    {
        System.out.println(input);
    }
}