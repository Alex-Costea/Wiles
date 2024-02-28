package wiles.interpreter.exceptions;

import org.jetbrains.annotations.NotNull;

public class PanicException extends Exception
{
    public PanicException(@NotNull String s)
    {
        super(s);
    }

    public PanicException()
    {
        super();
    }
}