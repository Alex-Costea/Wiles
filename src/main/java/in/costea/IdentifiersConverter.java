package in.costea;

import java.util.ArrayList;
import java.util.List;

import static in.costea.Utils.*;
import static in.costea.Utils.isAlphanumeric;

public class IdentifiersConverter {
    public IdentifiersConverter(String input) {
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
        return Constants.keywords.getOrDefault(sb.toString(), sb.toString());
    }

    private String readNumeralLiteral()
    {
        int j = i;
        StringBuilder sb = new StringBuilder("#");
        boolean dotFound=false;
        while (j<input.length() && (isDigit(arrayChars[j]) || (!dotFound && arrayChars[j]=='.'))) {
            sb.append(arrayChars[j]);
            if(arrayChars[j]=='.')
                dotFound=true;
            j++;
        }
        i = j-1;
        return sb.toString();
    }

    private String readOperator()
    {
        int j=i,maxJ=i;
        StringBuilder sb=new StringBuilder();
        String id=null;
        while (!isAlphanumeric(arrayChars[j])) {
            sb.append(arrayChars[j]);
            String tempId = Constants.operators.get(sb.toString());
            if(tempId!=null)
            {
                id=tempId;
                maxJ=j;
            }
            j++;
            if(j == input.length() || arrayChars[j]==' ')
                break;
        }
        if(id==null)
            throw new CompilationException("Operator unknown: "+input.substring(i,j));
        i = maxJ;
        return id;
    }

    public List<String> convert() {
        var identifiers=new ArrayList<String>();
        for(i=0;i<arrayChars.length;i++)
        {
            if(arrayChars[i]=='"') //string literal
            {
                identifiers.add(readStringLiteral());
            }
            else if(isAlphabetic(arrayChars[i])) //identifier
            {
                identifiers.add(readIdentifier());
            }
            else if(isDigit(arrayChars[i])) //numeral literal
            {
                identifiers.add(readNumeralLiteral());
            }
            else //operator
            {
                String id=readOperator();
                if(!id.equals("SPACE"))
                    identifiers.add(id);
            }
        }
        return identifiers;
    }
}
