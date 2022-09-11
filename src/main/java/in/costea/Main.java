package in.costea;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static in.costea.Utils.*;

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
        List<String> identifiers=wilesToIdentifiers(input);
        System.out.println(identifiers);
    }

    private static List<String> wilesToIdentifiers(String input) {
        var arrayChars=input.toCharArray();
        var identifiers=new ArrayList<String>();
        int i;
        for(i=0;i<arrayChars.length;i++)
        {
            if(arrayChars[i]=='"') //string literal
            {
                int j=i+1;
                StringBuilder sb=new StringBuilder("@");
                while(arrayChars[j]!='"')
                {
                    sb.append(arrayChars[j]);
                    j++;
                    if(j>=input.length())
                        throw new CompilationException("String unfinished: "+input.substring(i));
                }
                i=j;
                identifiers.add(sb.toString());
            }
            else if(isAlphanumeric(arrayChars[i]))
            {
                if(isAlphabetic(arrayChars[i])) //identifier
                {
                    int j = i;
                    StringBuilder sb = new StringBuilder();
                    while (j<input.length() && isAlphanumeric(arrayChars[j])) {
                        sb.append(arrayChars[j]);
                        j++;
                    }
                    i = j-1;
                    String id = Constants.keywords.getOrDefault(sb.toString(), sb.toString());
                    identifiers.add(id);
                }
                else //numeral literal
                {
                    int j = i;
                    StringBuilder sb = new StringBuilder("#");
                    while (j<input.length() && isDigit(arrayChars[j])) {
                        sb.append(arrayChars[j]);
                        j++;
                    }
                    i = j-1;
                    identifiers.add(sb.toString());
                }
            }
            else //operator
            {
                int j=i,maxJ=i;
                StringBuilder sb=new StringBuilder();
                String id=null;
                while (j<input.length() && !isAlphanumeric(arrayChars[j])) {
                    sb.append(arrayChars[j]);
                    String tempId = Constants.operators.get(sb.toString());
                    if(tempId!=null)
                    {
                        id=tempId;
                        maxJ=j;
                    }
                    j++;
                }
                if(id==null)
                    throw new CompilationException("Operator unknown: "+input.substring(i,j));
                i = maxJ;
                if(!id.equals("SPACE")) identifiers.add(id);
            }
        }
        return identifiers;
    }
}