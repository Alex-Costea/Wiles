package wiles.checker;

import org.jetbrains.annotations.NotNull;
import wiles.shared.TokenLocation;
import wiles.shared.AbstractCompilationException;

public class IdentifierExistsException extends AbstractCompilationException {
    public IdentifierExistsException(@NotNull String s, @NotNull TokenLocation tokenLocation) {
        super(s, tokenLocation);
    }
}
