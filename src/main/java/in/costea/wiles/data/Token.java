package in.costea.wiles.data;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record Token(@NotNull String content, TokenLocation location)
{

    public Token(String content, int line, int lineIndex)
    {
        this(content, new TokenLocation(line, lineIndex));
    }

    public Token(String content)
    {
        this(content, null);
    }

    @Contract(pure = true)
    @Override
    public @NotNull String toString()
    {
        if (location == null)
            return content;
        return "%s at line: %d index: %d".formatted(content, location().line(), location().lineIndex());
    }

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof Token tokenObj)
        {
            if (tokenObj.location() == null)
                return Objects.equals(content, tokenObj.content);
            return Objects.equals(content, tokenObj.content) && Objects.equals(location(), tokenObj.location());
        }
        return false;
    }
}
