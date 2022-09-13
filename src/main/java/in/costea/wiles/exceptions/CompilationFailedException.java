package in.costea.wiles.exceptions;

import in.costea.wiles.data.CompilationExceptionsCollection;

public class CompilationFailedException extends RuntimeException{
    public CompilationFailedException(CompilationExceptionsCollection exceptionsCollection)
    {
        super(exceptionsCollection.getExceptionsString());
    }
}

