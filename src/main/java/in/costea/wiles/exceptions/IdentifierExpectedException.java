package in.costea.wiles.exceptions;

import in.costea.wiles.data.TokenLocation;
import org.jetbrains.annotations.NotNull;

public class IdentifierExpectedException extends CompilationException{
    public IdentifierExpectedException(@NotNull String s, TokenLocation tokenLocation) {
        super(s, tokenLocation);
    }
}
