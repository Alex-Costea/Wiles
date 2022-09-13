package in.costea.wiles.exceptions;

import org.jetbrains.annotations.NotNull;

public class UnknownOperatorException extends CompilationException {
    public UnknownOperatorException(@NotNull String s, int line, int lineIndex) {
        super("Operator unknown: "+s,line,lineIndex);
    }
}
