package in.costea.wiles.exceptions;

import in.costea.wiles.data.TokenLocation;
import org.jetbrains.annotations.NotNull;

public class UnexpectedEndException extends CompilationException
{
    public UnexpectedEndException(@NotNull String s, TokenLocation location)
    {
        super(s, location);
    }
}
