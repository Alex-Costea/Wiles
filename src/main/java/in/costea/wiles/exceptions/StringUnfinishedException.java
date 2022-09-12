package in.costea.wiles.exceptions;


public class StringUnfinishedException extends CompilationException {

    public StringUnfinishedException(String s) {
        super("String unfinished: "+s);
    }
}
