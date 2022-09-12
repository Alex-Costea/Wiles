package in.costea.wiles.exceptions;

import in.costea.wiles.ExceptionsList;

public class CompilationFailedException extends RuntimeException{
    public CompilationFailedException(ExceptionsList exceptionsList)
    {
        super(exceptionsList.getExceptionsString());
    }
}

