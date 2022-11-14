package wiles.checker.exceptions;

import org.jetbrains.annotations.NotNull;
import wiles.shared.AbstractCompilationException;
import wiles.shared.TokenLocation;

import static wiles.shared.constants.ErrorMessages.INVALID_OPERATION_ERROR;

public class InvalidOperationException extends AbstractCompilationException {

    public InvalidOperationException(@NotNull TokenLocation tokenLocation) {
        super(INVALID_OPERATION_ERROR, tokenLocation);
    }
}
