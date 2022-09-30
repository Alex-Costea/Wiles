package in.costea.wiles.converters;

import in.costea.wiles.data.CompilationExceptionsCollection;
import in.costea.wiles.data.Token;
import in.costea.wiles.exceptions.CompilationException;
import in.costea.wiles.exceptions.StringUnfinishedException;
import in.costea.wiles.exceptions.UnknownOperatorException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static in.costea.wiles.statics.Constants.*;
import static in.costea.wiles.statics.Utils.*;

public class InputToTokensConverter
{

    private final char[] arrayChars;
    private final CompilationExceptionsCollection exceptions = new CompilationExceptionsCollection();
    private int originalIndex;
    private int index;
    private int lineIndex = -1; //character at index -1 can be considered equivalent to newline
    private int line = 1;

    public InputToTokensConverter(@NotNull String input)
    {
        arrayChars = input.toCharArray();
    }

    public List<Token> convert()
    {
        var tokens = new ArrayList<Token>();
        for (index = 0; index < arrayChars.length; index++)
        {
            try
            {
                originalIndex = index;
                if (arrayChars[index] == STRING_DELIMITER) //string literal
                {
                    tokens.add(createToken(readStringLiteral()));
                }
                else if (isAlphabetic(arrayChars[index])) //identifier
                {
                    tokens.add(createToken(readIdentifier()));
                }
                else if (isDigit(arrayChars[index])) //numeral literal
                {
                    tokens.add(createToken(readNumeralLiteral()));
                }
                else if (arrayChars[index] == COMMENT_START) //operator
                {
                    readComment();
                }
                else
                {
                    String id = readOperator();
                    int size = tokens.size();
                    if (size > 0 && id.equals(NEWLINE_ID) && tokens.get(size - 1).content().equals(CONTINUE_LINE_ID))
                    {
                        tokens.remove(size - 1);
                        addNewLine();
                        continue; // backslash followed by newline is ignored
                    }
                    if (!id.equals(SPACE_ID))
                        tokens.add(createToken(id));
                    if (id.equals(NEWLINE_ID))
                        addNewLine();
                }
            }
            catch (CompilationException ex)
            {
                exceptions.add(ex);
                tokens.add(createToken(UNKNOWN_TOKEN));
            }
        }
        return tokens;
    }

    private String readStringLiteral() throws StringUnfinishedException
    {
        int currentIndex = index + 1;
        try
        {
            if (currentIndex >= arrayChars.length)
                throw new StringUnfinishedException("", line, getIndexOnCurrentLine());

            StringBuilder sb = new StringBuilder();
            char lastNonSpaceCharacter = 0;
            int lastNonSpaceCharacterIndex = -1;
            while (arrayChars[currentIndex] != STRING_DELIMITER)
            {
                if (arrayChars[currentIndex] == NEWLINE)
                {
                    if (lastNonSpaceCharacter == CONTINUE_LINE)
                        sb.setLength(lastNonSpaceCharacterIndex - 1);
                    else break;
                }
                else if (arrayChars[currentIndex] != SPACE)
                {
                    lastNonSpaceCharacterIndex = currentIndex;
                    lastNonSpaceCharacter = arrayChars[currentIndex];
                }
                sb.append(arrayChars[currentIndex]);
                if (currentIndex + 1 == arrayChars.length)
                    break;
                currentIndex++;
            }
            if (arrayChars[currentIndex] == STRING_DELIMITER)
                return STRING_START + sb;

            //String not properly finished
            if (arrayChars[currentIndex] == NEWLINE) //add the newline token regardless
                currentIndex--;
            throw new StringUnfinishedException(sb.toString(), line, getIndexOnCurrentLine());
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
        while (currentIndex < arrayChars.length && isAlphanumeric(arrayChars[currentIndex]))
        {
            sb.append(arrayChars[currentIndex]);
            currentIndex++;
        }
        index = currentIndex - 1;
        return KEYWORDS.getOrDefault(sb.toString(), IDENTIFIER_START + sb);
    }

    private String readNumeralLiteral()
    {
        int currentIndex = index;
        StringBuilder sb = new StringBuilder(NUM_START);
        boolean delimiterAlreadyFound = false;
        while (currentIndex < arrayChars.length && (isDigit(arrayChars[currentIndex]) ||
                //first delimiter found, and not as the last digit
                (!delimiterAlreadyFound && arrayChars[currentIndex] == DECIMAL_DELIMITER &&
                        currentIndex + 1 < arrayChars.length && isDigit(arrayChars[currentIndex + 1]))))
        {
            sb.append(arrayChars[currentIndex]);
            if (arrayChars[currentIndex] == DECIMAL_DELIMITER)
                delimiterAlreadyFound = true;
            currentIndex++;
        }
        index = currentIndex - 1;
        return sb.toString();
    }

    private String readOperator() throws UnknownOperatorException
    {
        int currentIndex = index;
        int operatorFoundIndex = index;
        StringBuilder sb = new StringBuilder();
        String token = null;
        while (!isAlphanumeric(arrayChars[currentIndex]) && currentIndex - index < MAX_OPERATOR_LENGTH)
        {
            sb.append(arrayChars[currentIndex]);
            String tempId = OPERATORS.get(sb.toString());
            if (tempId != null)
            {
                token = tempId;
                operatorFoundIndex = currentIndex;
            }
            currentIndex++;
            if (currentIndex == arrayChars.length || arrayChars[currentIndex] == SPACE || arrayChars[currentIndex] == NEWLINE)
                break;
        }
        index = operatorFoundIndex;
        if (token == null)
        {
            index = currentIndex - 1;
            throw new UnknownOperatorException(sb.toString(), line, getIndexOnCurrentLine());
        }
        return token;
    }

    private void readComment()
    {
        int currentIndex = index;
        while (currentIndex < arrayChars.length && arrayChars[currentIndex] != NEWLINE)
        {
            currentIndex++;
        }
        index = currentIndex - 1;
    }

    private Token createToken(String token)
    {
        return new Token(token, line, getIndexOnCurrentLine());
    }

    private void addNewLine()
    {
        line++;
        lineIndex = index;
    }

    private int getIndexOnCurrentLine()
    {
        return originalIndex - lineIndex;
    }


    public void throwExceptionIfExists(int exceptionIndex) throws CompilationException
    {
        if (exceptions.size() > exceptionIndex)
            throw exceptions.get(exceptionIndex);
    }

    public CompilationExceptionsCollection getExceptions()
    {
        return new CompilationExceptionsCollection(exceptions);
    }
}
