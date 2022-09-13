package in.costea.wiles.exceptions;

import in.costea.wiles.data.TokenLocation;
import org.jetbrains.annotations.NotNull;

@Deprecated()
public class GenericCompilationException extends CompilationException{
    @SuppressWarnings("unused")
    public GenericCompilationException(@NotNull String s, TokenLocation tokenLocation) {
        super(s, tokenLocation);
    }

    @SuppressWarnings("unused")
    public GenericCompilationException(@NotNull String s) {
        super(s);
    }
}
