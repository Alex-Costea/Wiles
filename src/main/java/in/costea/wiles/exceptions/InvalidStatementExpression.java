package in.costea.wiles.exceptions;

import in.costea.wiles.data.TokenLocation;
import org.jetbrains.annotations.NotNull;

public class InvalidStatementExpression extends  AbstractCompilationException{
    public InvalidStatementExpression(@NotNull String s, @NotNull TokenLocation tokenLocation) {
        super(s, tokenLocation);
    }
}
