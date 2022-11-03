package wiles.parser.exceptions;

import wiles.parser.data.TokenLocation;
import org.jetbrains.annotations.NotNull;

public class InvalidStatementException extends  AbstractCompilationException{
    public InvalidStatementException(@NotNull String s, @NotNull TokenLocation tokenLocation) {
        super(s, tokenLocation);
    }
}
