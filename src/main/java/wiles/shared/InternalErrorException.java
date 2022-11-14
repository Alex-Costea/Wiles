package wiles.shared;

import wiles.shared.constants.ErrorMessages;

public class InternalErrorException extends RuntimeException{
    public InternalErrorException(String message) {
        super(ErrorMessages.INTERNAL_ERROR + message);
    }

    public InternalErrorException() {
        super(ErrorMessages.INTERNAL_ERROR);
    }
}
