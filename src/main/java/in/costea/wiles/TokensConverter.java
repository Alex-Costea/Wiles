package in.costea.wiles;

import java.util.ArrayList;
import java.util.List;

import static in.costea.wiles.Utils.*;

public class TokensConverter {
    public TokensConverter(String input) {
        this.input=input;
        arrayChars=input.toCharArray();
    }

    private int i;
    private final String input;
    private final char[] arrayChars;

    private String readStringLiteral()
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
        return sb.toString();
    }

    private String readIdentifier()
    {
        int j = i;
        StringBuilder sb = new StringBuilder();
        while (j<arrayChars.length && isAlphanumeric(arrayChars[j])) {
            sb.append(arrayChars[j]);
            j++;
        }
        i = j-1;
        return Constants.KEYWORDS.getOrDefault(sb.toString(), sb.toString());
    }

    private String readNumeralLiteral()
    {
        int j = i;
        StringBuilder sb = new StringBuilder("#");
        boolean periodFound=false;
        while (j<input.length() && (isDigit(arrayChars[j]) ||
                //first period found, and not as the last digit
                (!periodFound && arrayChars[j]=='.' && j+1<input.length() && isDigit(arrayChars[j+1])))) {
            sb.append(arrayChars[j]);
            if(arrayChars[j]=='.')
                periodFound=true;
            j++;
        }
        i = j-1;
        return sb.toString();
    }

    private String readOperator()
    {
        int j=i,maxJ=i;
        StringBuilder sb=new StringBuilder();
        String token=null;
        while (!isAlphanumeric(arrayChars[j])) {
            sb.append(arrayChars[j]);
            String tempId = Constants.OPERATORS.get(sb.toString());
            if(tempId!=null)
            {
                token=tempId;
                maxJ=j;
            }
            j++;
            if(j == input.length() || arrayChars[j]==' ')
                break;
        }
        if(token==null)
            throw new CompilationException("Operator unknown: "+input.substring(i,j));
        i = maxJ;
        return token;
    }

    public List<String> convert() {
        var tokens=new ArrayList<String>();
        for(i=0;i<arrayChars.length;i++)
        {
            if(arrayChars[i]=='"') //string literal
            {
                tokens.add(readStringLiteral());
            }
            else if(isAlphabetic(arrayChars[i])) //identifier
            {
                tokens.add(readIdentifier());
            }
            else if(isDigit(arrayChars[i])) //numeral literal
            {
                tokens.add(readNumeralLiteral());
            }
            else //operator
            {
                String id=readOperator();
                if(!id.equals("SPACE"))
                    tokens.add(id);
            }
        }
        return tokens;
    }
}
