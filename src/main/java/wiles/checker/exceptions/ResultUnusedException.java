package wiles.checker.exceptions;

import org.jetbrains.annotations.NotNull;
import wiles.shared.AbstractCompilationException;
import wiles.shared.TokenLocation;

import static wiles.shared.constants.ErrorMessages.RESULT_UNUSED_ERROR;

public class ResultUnusedException extends AbstractCompilationException {
    public ResultUnusedException(@NotNull TokenLocation tokenLocation) {
        super(RESULT_UNUSED_ERROR, tokenLocation);
    }
}
