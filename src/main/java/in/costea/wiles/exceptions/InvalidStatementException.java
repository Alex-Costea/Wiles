package in.costea.wiles.exceptions;

import in.costea.wiles.data.TokenLocation;
import org.jetbrains.annotations.NotNull;

public class InvalidStatementException extends  AbstractCompilationException{
    public InvalidStatementException(@NotNull String s, @NotNull TokenLocation tokenLocation) {
        super(s, tokenLocation);
    }
}
