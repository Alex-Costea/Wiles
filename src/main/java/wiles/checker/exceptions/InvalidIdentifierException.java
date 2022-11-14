package wiles.checker.exceptions;

import org.jetbrains.annotations.NotNull;
import wiles.shared.AbstractCompilationException;
import wiles.shared.TokenLocation;

public class InvalidIdentifierException extends AbstractCompilationException {
    public InvalidIdentifierException(@NotNull String s, @NotNull TokenLocation tokenLocation) {
        super(s, tokenLocation);
    }
}
