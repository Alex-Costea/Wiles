package in.costea.wiles.exceptions;

import java.util.List;

public class CompilationFailedException extends RuntimeException{
    public CompilationFailedException(List<? extends Exception> exceptions)
    {
        super(exceptions.stream().map((Exception x)->"\n"+x.getMessage()).reduce((a,b)->a+b)
                .orElseThrow(()->new IllegalArgumentException("Exception list must not be empty!")));
    }
}

