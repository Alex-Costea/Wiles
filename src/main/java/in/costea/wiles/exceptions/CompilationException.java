package in.costea.wiles.exceptions;

public abstract class CompilationException extends Exception {
    public CompilationException(String s,int line,int lineIndex)
    {
        super("Line "+line+", character "+lineIndex+": "+s);
    }
}
