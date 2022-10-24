package in.costea.wiles.exceptions;

import static in.costea.wiles.constants.ErrorMessages.INTERNAL_ERROR;

public class InternalErrorException extends RuntimeException{
    public InternalErrorException(String message) {
        super(INTERNAL_ERROR + message);
    }

    public InternalErrorException() {
        super(INTERNAL_ERROR);
    }
}
