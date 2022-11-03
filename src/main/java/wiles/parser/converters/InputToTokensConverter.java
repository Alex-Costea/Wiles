package wiles.parser.converters;

import org.jetbrains.annotations.NotNull;
import wiles.parser.constants.ErrorMessages;
import wiles.parser.constants.Settings;
import wiles.parser.constants.Tokens;
import wiles.parser.constants.Utils;
import wiles.parser.data.CompilationExceptionsCollection;
import wiles.parser.data.Token;
import wiles.parser.data.TokenLocation;
import wiles.parser.exceptions.AbstractCompilationException;
import wiles.parser.exceptions.StringUnfinishedException;

import java.util.ArrayList;
import java.util.List;

import static wiles.parser.constants.Chars.*;

public class InputToTokensConverter {
    private final char[] arrayChars;
    @NotNull
    private final CompilationExceptionsCollection exceptions = new CompilationExceptionsCollection();
    private int originalIndex;
    private int index;
    private int lineIndex = -1; //character at index -1 can be considered equivalent to newline
    private int line = 1;

    public InputToTokensConverter(@NotNull String input) {
        arrayChars = input.toCharArray();
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
                    tokens.add(createToken(readStringLiteral()));
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
                    if (!id.equals(Tokens.SPACE_ID))
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

    public StringBuilder createString(boolean isComment) {
        int currentIndex = index + 1;
        @NotNull
        StringBuilder sb = new StringBuilder();
        char lastNonSpaceCharacter = 0;
        int lastNonSpaceCharacterIndex = -1;
        while (currentIndex < arrayChars.length) {
            if (arrayChars[currentIndex] == '\n') {
                if (lastNonSpaceCharacter == CONTINUE_LINE)
                    sb.setLength(lastNonSpaceCharacterIndex - index - 1);
                else {
                    //TODO: ugly hack i don't understand
                    if (!isComment)
                        currentIndex--;
                    break;
                }
            } else if (arrayChars[currentIndex] != ' ') {
                lastNonSpaceCharacterIndex = currentIndex;
                lastNonSpaceCharacter = arrayChars[currentIndex];
            }
            if (isComment && arrayChars[currentIndex] == STRING_DELIMITER)
                break;
            sb.append(arrayChars[currentIndex]);
            if (currentIndex + 1 == arrayChars.length)
                break;
            currentIndex++;
        }
        index = currentIndex;
        return sb;
    }

    @NotNull
    private String readStringLiteral() throws StringUnfinishedException {
        if (index >= arrayChars.length)
            throw new StringUnfinishedException(ErrorMessages.STRING_UNFINISHED_ERROR, line, getIndexOnCurrentLine());
        StringBuilder sb = createString(true);
        if (index < arrayChars.length && arrayChars[index] == STRING_DELIMITER)
            return Tokens.STRING_START + sb;

        //String not properly finished at this point
        if (index < arrayChars.length && arrayChars[index] == '\n') //of the newline token regardless
            index--;
        throw new StringUnfinishedException(ErrorMessages.STRING_UNFINISHED_ERROR, line, getIndexOnCurrentLine());
    }

    @NotNull
    private String readIdentifier() {
        int currentIndex = index;
        @NotNull
        StringBuilder sb = new StringBuilder();
        while (currentIndex < arrayChars.length && Utils.isAlphanumeric(arrayChars[currentIndex])) {
            sb.append(arrayChars[currentIndex]);
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
            sb.append(arrayChars[currentIndex]);
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
            sb.append(arrayChars[currentIndex]);
            String tempId = Tokens.TOKENS.get(sb.toString());
            if (tempId != null) {
                token = tempId;
                operatorFoundIndex = currentIndex;
            }
            currentIndex++;
            if (currentIndex == arrayChars.length || arrayChars[currentIndex] == ' ' || arrayChars[currentIndex] == '\n')
                break;
        }
        index = operatorFoundIndex;
        if (token == null) {
            index = currentIndex - 1;
            token = sb.toString();
            //throw new UnknownTokenException(sb.toString(), line, getIndexOnCurrentLine());
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
