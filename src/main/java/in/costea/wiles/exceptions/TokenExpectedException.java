package in.costea.wiles.exceptions;

import in.costea.wiles.data.TokenLocation;
import org.jetbrains.annotations.NotNull;

public class TokenExpectedException extends CompilationException{

    public TokenExpectedException(@NotNull String s, TokenLocation tokenLocation) {
        super(s, tokenLocation);
    }
}
