package wiles.checker.exceptions;

import org.jetbrains.annotations.NotNull;
import wiles.shared.AbstractCompilationException;
import wiles.shared.TokenLocation;

public class TypeInferenceException extends AbstractCompilationException {
    public TypeInferenceException(@NotNull String s, @NotNull TokenLocation tokenLocation) {
        super(s, tokenLocation);
    }
}
