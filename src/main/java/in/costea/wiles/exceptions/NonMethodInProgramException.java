package in.costea.wiles.exceptions;

import in.costea.wiles.data.TokenLocation;
import org.jetbrains.annotations.NotNull;

public class NonMethodInProgramException extends CompilationException{
    public NonMethodInProgramException(@NotNull String s, TokenLocation tokenLocation) {
        super(s, tokenLocation);
    }
}
