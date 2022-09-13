package in.costea.wiles.exceptions;


public class StringUnfinishedException extends CompilationException {

    public StringUnfinishedException(String s,int line,int lineIndex) {
        super("String unfinished: \""+s,line,lineIndex);
    }
}
