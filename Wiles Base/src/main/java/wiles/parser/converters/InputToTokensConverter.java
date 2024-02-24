package wiles.parser.converters;

import org.jetbrains.annotations.NotNull;
import wiles.parser.exceptions.StringInvalidException;
import wiles.shared.AbstractCompilationException;
import wiles.shared.CompilationExceptionsCollection;
import wiles.shared.Token;
import wiles.shared.TokenLocation;
import wiles.shared.constants.ErrorMessages;
import wiles.shared.constants.Settings;
import wiles.shared.constants.Tokens;
import wiles.shared.constants.Utils;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.text.StringEscapeUtils.unescapeHtml4;
import static wiles.shared.constants.Chars.*;

public class InputToTokensConverter {
    private final int[] arrayChars;
    @NotNull
    private final CompilationExceptionsCollection exceptions = new CompilationExceptionsCollection();
    private int originalIndex;
    private int index;
    private int lineIndex = -1; //character at index -1 can be considered equivalent to newline
    private int line = 1;

    public InputToTokensConverter(@NotNull String input) {
        arrayChars = input.codePoints().toArray();
    }

    public InputToTokensConverter(@NotNull String input, int additionalLines) {
        arrayChars = input.codePoints().toArray();
        line -= additionalLines;
    }

    @NotNull
    public List<Token> convert() {
        @NotNull
        var tokens = new ArrayList<Token>();
        for (index = 0; index < arrayChars.length; index++) {
            try {
                originalIndex = index;
                if (arrayChars[index] == STRING_DELIMITER) //string literal
                {
                    try {
                        tokens.add(createToken(unescapeHtml4(readStringLiteral())));
                    }
                    catch(IllegalArgumentException ex)
                    {
                        throw new StringInvalidException(ErrorMessages.STRING_ESCAPE_INVALID_ERROR, line, getIndexOnCurrentLine());
                    }
                } else if (Utils.isAlphabetic(arrayChars[index])) //identifier
                {
                    tokens.add(createToken(readIdentifier()));
                } else if (Utils.isDigit(arrayChars[index])) //numeral literal
                {
                    tokens.add(createToken(readNumeralLiteral()));
                } else if (arrayChars[index] == COMMENT_START) //operator
                {
                    readComment();
                } else {
                    String id = readSymbol();
                    int size = tokens.size();
                    if (size > 0 && id.equals(Tokens.NEWLINE_ID) && tokens.get(size - 1).getContent().equals(Tokens.CONTINUE_LINE_ID)) {
                        tokens.remove(size - 1);
                        addNewLine();
                        continue; // backslash followed by newline is ignored
                    }
                    if (!id.isBlank())
                        tokens.add(createToken(id));
                    if (id.equals(Tokens.NEWLINE_ID))
                        addNewLine();
                }
            } catch (AbstractCompilationException ex) {
                exceptions.add(ex);
                tokens.add(createToken(Tokens.ERROR_TOKEN));
            }
        }
        return tokens;
    }

    public StringBuilder createString(boolean isNotComment) {
        int currentIndex = index + 1;
        @NotNull
        StringBuilder sb = new StringBuilder();
        while (currentIndex < arrayChars.length) {
            if (arrayChars[currentIndex] == '\n') {
                if (!isNotComment)
                    currentIndex--;
                break;
            }
            if (isNotComment && arrayChars[currentIndex] == STRING_DELIMITER)
                break;
            sb.appendCodePoint(arrayChars[currentIndex]);
            if (currentIndex + 1 == arrayChars.length)
                break;
            currentIndex++;
        }
        index = currentIndex;
        return sb;
    }

    @NotNull
    private String readStringLiteral() throws StringInvalidException {
        if (index >= arrayChars.length)
            throw new StringInvalidException(ErrorMessages.STRING_UNFINISHED_ERROR, line, getIndexOnCurrentLine());
        StringBuilder sb = createString(true);
        if (index < arrayChars.length && arrayChars[index] == STRING_DELIMITER)
            return Tokens.STRING_START + sb;

        //String not properly finished at this point
        if (index < arrayChars.length && arrayChars[index] == '\n') //of the newline token regardless
            index--;
        throw new StringInvalidException(ErrorMessages.STRING_UNFINISHED_ERROR, line, getIndexOnCurrentLine());
    }

    @NotNull
    private String readIdentifier() {
        int currentIndex = index;
        @NotNull
        StringBuilder sb = new StringBuilder();
        while (currentIndex < arrayChars.length && Utils.isAlphanumeric(arrayChars[currentIndex])) {
            sb.appendCodePoint(arrayChars[currentIndex]);
            currentIndex++;
        }
        index = currentIndex - 1;
        return Tokens.TOKENS.getOrDefault(sb.toString(), Tokens.IDENTIFIER_START + sb);
    }

    @NotNull
    private String readNumeralLiteral() {
        int currentIndex = index;
        @NotNull
        StringBuilder sb = new StringBuilder(Tokens.NUM_START);
        boolean delimiterAlreadyFound = false;
        while (currentIndex < arrayChars.length && (Utils.isDigit(arrayChars[currentIndex]) ||
                //first delimiter found, and not as the last digit
                (!delimiterAlreadyFound && arrayChars[currentIndex] == DECIMAL_DELIMITER &&
                        currentIndex + 1 < arrayChars.length && Utils.isDigit(arrayChars[currentIndex + 1])))) {
            sb.appendCodePoint(arrayChars[currentIndex]);
            if (arrayChars[currentIndex] == DECIMAL_DELIMITER)
                delimiterAlreadyFound = true;
            currentIndex++;
        }
        index = currentIndex - 1;
        return sb.toString();
    }

    @NotNull
    private String readSymbol() {
        int currentIndex = index;
        int operatorFoundIndex = index;
        @NotNull
        StringBuilder sb = new StringBuilder();
        String token = null;
        while (!Utils.isAlphanumeric(arrayChars[currentIndex]) && currentIndex - index < Settings.MAX_SYMBOL_LENGTH) {
            sb.appendCodePoint(arrayChars[currentIndex]);
            String tempId = Tokens.TOKENS.get(sb.toString());
            if (tempId != null) {
                token = tempId;
                operatorFoundIndex = currentIndex;
            }
            currentIndex++;
            if (Utils.isWhitespace(arrayChars[currentIndex-1]) ||
                    currentIndex == arrayChars.length ||
                    arrayChars[currentIndex] == '\n')
                break;
        }
        index = operatorFoundIndex;
        if (token == null) {
            index = currentIndex - 1;
            token = sb.toString();
        }
        return token;
    }

    private void readComment() {
        createString(false);
    }

    @NotNull
    private Token createToken(String token) {
        return new Token(token, new TokenLocation(line, getIndexOnCurrentLine()));
    }

    private void addNewLine() {
        line++;
        lineIndex = index;
    }

    private int getIndexOnCurrentLine() {
        return originalIndex - lineIndex;
    }


    public void throwExceptionIfExists(int exceptionIndex) throws AbstractCompilationException {
        if (exceptions.size() > exceptionIndex)
            throw exceptions.get(exceptionIndex);
    }

    @NotNull
    public CompilationExceptionsCollection getExceptions() {
        return (CompilationExceptionsCollection) exceptions.clone();
    }
}
