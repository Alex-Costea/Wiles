package in.costea.wiles.exceptions;

public class UnknownOperatorException extends CompilationException {
    public UnknownOperatorException(String s) {
        super("Operator unknown: "+s);
    }
}
