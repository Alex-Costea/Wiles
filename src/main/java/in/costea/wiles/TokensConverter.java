package in.costea.wiles;

import in.costea.wiles.exceptions.CompilationException;
import in.costea.wiles.exceptions.StringUnfinishedException;
import in.costea.wiles.exceptions.UnknownOperatorException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static in.costea.wiles.Constants.*;
import static in.costea.wiles.Utils.*;

public class TokensConverter {

    public TokensConverter(@NotNull String input) {
        arrayChars=input.toCharArray();
    }

    private int index;
    private int lineIndex=-1; //character at index -1 can be considered equivalent to newline
    private final char[] arrayChars;
    private final CompilationExceptionsCollection exceptions=new CompilationExceptionsCollection();
    private int line=1;

    private String readStringLiteral() throws StringUnfinishedException {
        int currentIndex= index +1;
        try
        {
            if (currentIndex >= arrayChars.length)
                throw new StringUnfinishedException("",line,getIndexOnCurrentLine());

            StringBuilder sb = new StringBuilder();
            while (arrayChars[currentIndex] != STRING_DELIMITER && arrayChars[currentIndex] != '\n') {
                sb.append(arrayChars[currentIndex]);
                currentIndex++;
                if (currentIndex >= arrayChars.length)
                    throw new StringUnfinishedException(sb.toString(),line,getIndexOnCurrentLine());
            }
            if(arrayChars[currentIndex] == '\n')
            {
                currentIndex--;
                throw new StringUnfinishedException(sb.toString(), line, getIndexOnCurrentLine());
            }
            return STRING_START + sb;
        }
        finally
        {
            index = currentIndex;
        }
    }

    private String readIdentifier()
    {
        int currentIndex = index;
        StringBuilder sb = new StringBuilder();
        while (currentIndex<arrayChars.length && isAlphanumeric(arrayChars[currentIndex])) {
            sb.append(arrayChars[currentIndex]);
            currentIndex++;
        }
        index = currentIndex-1;
        return KEYWORDS.getOrDefault(sb.toString(), IDENTIFIER_START + sb);
    }

    private String readNumeralLiteral()
    {
        int currentIndex = index;
        StringBuilder sb = new StringBuilder(NUM_START);
        boolean delimiterAlreadyFound=false;
        while (currentIndex<arrayChars.length && (isDigit(arrayChars[currentIndex]) ||
                //first delimiter found, and not as the last digit
                (!delimiterAlreadyFound && arrayChars[currentIndex] == DECIMAL_DELIMITER &&
                        currentIndex+1 < arrayChars.length && isDigit(arrayChars[currentIndex+1])))) {
            sb.append(arrayChars[currentIndex]);
            if(arrayChars[currentIndex]== DECIMAL_DELIMITER)
                delimiterAlreadyFound=true;
            currentIndex++;
        }
        index = currentIndex-1;
        return sb.toString();
    }

    private String readOperator() throws UnknownOperatorException {
        int currentIndex= index;
        int operatorFoundIndex= index;
        StringBuilder sb=new StringBuilder();
        String token=null;
        while (!isAlphanumeric(arrayChars[currentIndex]) && currentIndex - index < MAX_OPERATOR_LENGTH) {
            sb.append(arrayChars[currentIndex]);
            String tempId = OPERATORS.get(sb.toString());
            if(tempId!=null)
            {
                token=tempId;
                operatorFoundIndex=currentIndex;
            }
            currentIndex++;
            if(currentIndex == arrayChars.length || arrayChars[currentIndex]==' ' || arrayChars[currentIndex]=='\n')
                break;
        }
        index = operatorFoundIndex;
        if(token==null)
        {
            index=currentIndex-1;
            throw new UnknownOperatorException(sb.toString(), line, getIndexOnCurrentLine());
        }
        return token;
    }

    private void readComment()
    {
        int currentIndex = index;
        while(currentIndex<arrayChars.length && arrayChars[currentIndex]!=COMMENT_END)
        {
            currentIndex++;
        }
        index = currentIndex-1;
    }

    public List<String> convert() {
        var tokens=new ArrayList<String>();
        for(index =0; index <arrayChars.length; index++)
        {
            try
            {
                if (arrayChars[index] == STRING_DELIMITER) //string literal
                {
                    tokens.add(readStringLiteral());
                }
                else if (isAlphabetic(arrayChars[index])) //identifier
                {
                    tokens.add(readIdentifier());
                }
                else if (isDigit(arrayChars[index])) //numeral literal
                {
                    tokens.add(readNumeralLiteral());
                }
                else if (arrayChars[index] == COMMENT_START) //operator
                {
                    readComment();
                }
                else
                {
                    String id = readOperator();
                    if(id.equals(NEWLINE_ID))
                        newLine();
                    if (!id.equals(SPACE_ID))
                        tokens.add(id);
                }
            }
            catch (CompilationException ex)
            {
                exceptions.add(ex);
                tokens.add(UNKNOWN_TOKEN);
            }
        }
        return tokens;
    }

    private void newLine()
    {
        line++;
        lineIndex=index;
    }

    private int getIndexOnCurrentLine()
    {
        return index-lineIndex;
    }


    public void throwFirstExceptionIfExists() throws CompilationException
    {
        if(exceptions.size()>0)
            throw exceptions.get(0);
    }

    public CompilationExceptionsCollection getExceptions() {
        return exceptions;
    }
}
