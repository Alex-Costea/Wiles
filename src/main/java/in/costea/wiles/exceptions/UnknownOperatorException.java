package in.costea.wiles.exceptions;

public class UnknownOperatorException extends CompilationException {
    public UnknownOperatorException(String s,int line,int lineIndex) {
        super("Operator unknown: "+s,line,lineIndex);
    }
}
