package in.costea.wiles.dataclasses;

import in.costea.wiles.exceptions.CompilationException;

import java.util.ArrayList;

public class CompilationExceptionsCollection extends ArrayList<CompilationException> {

    public CompilationExceptionsCollection(CompilationExceptionsCollection exceptions) {
        super(exceptions);
    }

    public CompilationExceptionsCollection()
    {
        super();
    }

    public void add(CompilationExceptionsCollection ex) {
        this.addAll(ex);
    }

    public String getExceptionsString() {
        var optional= stream().map((Exception x)->"\n    "+x.getMessage()).reduce((a,b)->a+b);
        if(optional.isEmpty())
            throw new IllegalStateException();
        return optional.get();
    }
}
