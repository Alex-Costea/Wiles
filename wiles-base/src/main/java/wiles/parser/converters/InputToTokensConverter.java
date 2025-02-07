package wiles.parser.converters;

import org.jetbrains.annotations.NotNull;
import org.unbescape.html.HtmlEscape;
import wiles.parser.exceptions.StringInvalidException;
import wiles.shared.AbstractCompilationException;
import wiles.shared.CompilationExceptionsCollection;
import wiles.shared.Token;
import wiles.shared.TokenLocation;
import wiles.shared.constants.ErrorMessages;
import wiles.shared.constants.Settings;
import wiles.shared.constants.Tokens;
import wiles.shared.constants.Utils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static wiles.shared.constants.Chars.*;
import static wiles.shared.constants.Utils.isAlphanumeric;
import static wiles.shared.constants.Utils.isWhitespace;

public class InputToTokensConverter {
    private final int[] arrayChars;
    @NotNull
    private final CompilationExceptionsCollection exceptions = new CompilationExceptionsCollection();
    private int originalIndex;
    private int index;
    private int lineIndex = -1; //character at index -1 can be considered equivalent to newline
    private int line = 1;
    private final TokenLocation lastLocation;

    private static final HashMap<String, String> ESCAPE_SEQUENCES = new HashMap<>();

    static {
        ESCAPE_SEQUENCES.put("\\q", "\"");
        ESCAPE_SEQUENCES.put("\\n", "\n");
        ESCAPE_SEQUENCES.put("\\b", "\\");
        ESCAPE_SEQUENCES.put("\\d", "$");
        ESCAPE_SEQUENCES.put("\\s", ";");
    }

    public InputToTokensConverter(@NotNull String input, @NotNull TokenLocation lastLocation) {
        arrayChars = input.codePoints().toArray();
        this.lastLocation = lastLocation;
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
                        var string = readStringLiteral();
                        var unescaped = unescape(string);
                        tokens.add(createToken(unescaped));
                    }
                    catch(IllegalArgumentException ex)
                    {
                        throw new StringInvalidException(ErrorMessages.STRING_ESCAPE_INVALID_ERROR,
                                new TokenLocation( line, getIndexOnCurrentLine(), -1, -1));
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
                    else tokens.add(createToken(""));
                    if (id.equals(Tokens.NEWLINE_ID))
                        addNewLine();
                }
            } catch (AbstractCompilationException ex) {
                exceptions.add(ex);
                tokens.add(createToken(Tokens.ERROR_TOKEN));
            }
        }
        return removeNull(addLocationEnd(tokens));
    }

    private @NotNull ArrayList<Token> addLocationEnd(ArrayList<Token> tokens)
    {
        ArrayList<Token> newTokens = new ArrayList<>();
        for(int i = 0; i < tokens.size(); i++)
        {
            Token token = tokens.get(i);
            TokenLocation nextLocation;
            if(i != tokens.size() - 1)
            {
                nextLocation = tokens.get(i+1).getLocation();
            }
            else{
                nextLocation = lastLocation;
            }
            TokenLocation location = token.getLocation();
            newTokens.add(new Token(token.getContent(),
                    new TokenLocation(location.getLine(), location.getLineIndex(),
                            nextLocation.getLine(), nextLocation.getLineIndex())));
        }
        return newTokens;
    }

    private List<Token> removeNull(ArrayList<Token> tokens)
    {
        return tokens.stream().filter(token -> !token.component1().isEmpty()).toList();
    }

    private @NotNull String unescapeGroup(@NotNull String match)
    {
        int lastCharIndex = match.length() - 1;
        if(match.charAt(lastCharIndex) == ';')
            match = match.substring(0, lastCharIndex);
        if(!ESCAPE_SEQUENCES.containsKey(match)) {
            if(match.length() == 2 && match.charAt(0) == '\\') {
                var myChar = match.charAt(1);
                if(isAlphanumeric(myChar) || isWhitespace(myChar))
                    throw new IllegalArgumentException();
                return String.valueOf(myChar);
            }
            match = "&" + match.substring(1) + ";";
            var htmlMatch = HtmlEscape.unescapeHtml(match);
            if(htmlMatch.length() > 1)
                throw new IllegalArgumentException();
            return htmlMatch;
        }
        return ESCAPE_SEQUENCES.get(match);

    }

    private @NotNull String unescape(@NotNull String s) {
        Pattern pattern = Pattern.compile("\\$.*?;|\\\\#?\\w*?;|\\\\\\w");
        Matcher matcher = pattern.matcher(s);
        return matcher.replaceAll((matchResult ->
                Matcher.quoteReplacement(unescapeGroup(matcher.group()))));
    }

    public StringBuilder createString(boolean isNotComment) {
        int currentIndex = index + 1;
        @NotNull
        StringBuilder sb = new StringBuilder();
        while (currentIndex < arrayChars.length) {
            if (!isNotComment & arrayChars[currentIndex] == '\n') {
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
            throw new StringInvalidException(ErrorMessages.STRING_UNFINISHED_ERROR,
                    new TokenLocation( line, getIndexOnCurrentLine(), -1, -1));
        StringBuilder sb = createString(true);
        if (index < arrayChars.length && arrayChars[index] == STRING_DELIMITER)
            return Tokens.STRING_START + sb;

        //String not properly finished at this point
        if (index < arrayChars.length && arrayChars[index] == '\n') //of the newline token regardless
            index--;
        throw new StringInvalidException(ErrorMessages.STRING_UNFINISHED_ERROR,
                new TokenLocation( line, getIndexOnCurrentLine(), -1, -1));
    }

    @NotNull
    private String readIdentifier() {
        int currentIndex = index;
        @NotNull
        StringBuilder sb = new StringBuilder();
        while (currentIndex < arrayChars.length && isAlphanumeric(arrayChars[currentIndex])) {
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
        while (!isAlphanumeric(arrayChars[currentIndex]) && currentIndex - index < Settings.MAX_SYMBOL_LENGTH) {
            sb.appendCodePoint(arrayChars[currentIndex]);
            String tempId = Tokens.TOKENS.get(sb.toString());
            if (tempId != null) {
                token = tempId;
                operatorFoundIndex = currentIndex;
            }
            currentIndex++;
            if (isWhitespace(arrayChars[currentIndex-1]) ||
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
        return new Token(token, new TokenLocation(line, getIndexOnCurrentLine(), -1, -1));
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
