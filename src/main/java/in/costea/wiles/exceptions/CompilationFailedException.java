package in.costea.wiles.exceptions;

import in.costea.wiles.ExceptionsCollection;

public class CompilationFailedException extends RuntimeException{
    public CompilationFailedException(ExceptionsCollection exceptionsCollection)
    {
        super(exceptionsCollection.getExceptionsString());
    }
}

