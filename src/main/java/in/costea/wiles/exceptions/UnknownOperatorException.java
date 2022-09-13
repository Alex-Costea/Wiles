package in.costea.wiles.exceptions;

import in.costea.wiles.data.TokenLocation;
import org.jetbrains.annotations.NotNull;

public class UnknownOperatorException extends CompilationException {
    public UnknownOperatorException(@NotNull String s, int line, int lineIndex) {
        super("Operator unknown: "+s, new TokenLocation(line,lineIndex));
    }
}
