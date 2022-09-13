package in.costea.wiles.exceptions;

public abstract class CompilationException extends Exception {
    private final int line;
    private final int index;
    public CompilationException(String s,int line,int lineIndex)
    {
        super("Line "+line+", character "+lineIndex+": "+s);
        this.line=line;
        this.index=lineIndex;
    }

    public int getLine() {
        return line;
    }

    public int getIndex() {
        return index;
    }
}
