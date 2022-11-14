package wiles.parser.exceptions;

import wiles.shared.TokenLocation;
import org.jetbrains.annotations.NotNull;
import wiles.shared.AbstractCompilationException;

public class InvalidStatementException extends AbstractCompilationException {
    public InvalidStatementException(@NotNull String s, @NotNull TokenLocation tokenLocation) {
        super(s, tokenLocation);
    }
}
