package in.costea.wiles.exceptions;

import in.costea.wiles.data.CompilationExceptionsCollection;
import org.jetbrains.annotations.NotNull;

public class CompilationFailed extends RuntimeException {
    public CompilationFailed(@NotNull CompilationExceptionsCollection exceptionsCollection) {
        super(exceptionsCollection.getExceptionsString());
    }
}

