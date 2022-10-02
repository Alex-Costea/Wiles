package in.costea.wiles.exceptions;

import in.costea.wiles.data.TokenLocation;
import org.jetbrains.annotations.NotNull;

public class UnexpectedTokenException extends AbstractCompilationException
{
    public UnexpectedTokenException(@NotNull String s, TokenLocation where)
    {
        super("Unexpected token: " + s, where);
    }
}