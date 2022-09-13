package in.costea.wiles.exceptions;


import in.costea.wiles.data.TokenLocation;
import org.jetbrains.annotations.NotNull;

public class StringUnfinishedException extends CompilationException {

    public StringUnfinishedException(@NotNull String s, int line, int lineIndex) {
        super("String unfinished: \""+s, new TokenLocation(line,lineIndex));
    }
}
