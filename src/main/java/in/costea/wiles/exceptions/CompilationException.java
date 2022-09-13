package in.costea.wiles.exceptions;

import in.costea.wiles.data.TokenLocation;
import org.jetbrains.annotations.NotNull;

public abstract class CompilationException extends Exception {

    private final TokenLocation tokenLocation;
    public CompilationException(@NotNull String s, TokenLocation tokenLocation)
    {
        super("Line "+tokenLocation.line()+", character "+tokenLocation.lineIndex()+": "+s);
        this.tokenLocation=tokenLocation;
    }

    public CompilationException(@NotNull String s)
    {
        super(s);
        this.tokenLocation=null;
    }

    public int getLine() {
        return tokenLocation.line();
    }
}
