package in.costea.wiles.exceptions;

import org.jetbrains.annotations.NotNull;

public class UnexpectedEndException extends CompilationException
{
    public UnexpectedEndException(@NotNull String s)
    {
        super(s);
    }
}
