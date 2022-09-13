package in.costea.wiles.exceptions;

import in.costea.wiles.data.CompilationExceptionsCollection;
import org.jetbrains.annotations.NotNull;

public class CompilationFailedException extends RuntimeException{
    public CompilationFailedException(@NotNull CompilationExceptionsCollection exceptionsCollection)
    {
        super(exceptionsCollection.getExceptionsString());
    }
}

