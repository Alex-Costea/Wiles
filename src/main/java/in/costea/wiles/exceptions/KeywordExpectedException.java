package in.costea.wiles.exceptions;

import in.costea.wiles.data.TokenLocation;
import org.jetbrains.annotations.NotNull;

public class KeywordExpectedException extends CompilationException{

    public KeywordExpectedException(@NotNull String s, TokenLocation tokenLocation) {
        super(s, tokenLocation);
    }
}
