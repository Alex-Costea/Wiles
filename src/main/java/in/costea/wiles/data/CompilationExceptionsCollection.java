package in.costea.wiles.data;

import in.costea.wiles.exceptions.CompilationException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public class CompilationExceptionsCollection extends ArrayList<CompilationException> {

    public CompilationExceptionsCollection(CompilationExceptionsCollection exceptions) {
        super(exceptions);
    }

    public CompilationExceptionsCollection()
    {
        super();
    }

    public void add(@NotNull CompilationExceptionsCollection objToAdd) {
        this.addAll(objToAdd);
    }

    public String getExceptionsString() {
        var optional= stream().map((Exception x)->"\n    "+x.getMessage()).reduce((a,b)->a+b);
        if(optional.isEmpty())
            throw new IllegalStateException();
        return optional.get();
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof CompilationExceptionsCollection obj)
        {
            if(size()!=obj.size())
                return false;
            for(int i=0;i<size();i++)
                if(!Objects.equals(get(i).getMessage(), obj.get(i).getMessage()))
                    return false;
            return true;
        }
        return false;
    }
}
