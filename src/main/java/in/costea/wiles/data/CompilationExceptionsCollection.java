package in.costea.wiles.data;

import in.costea.wiles.exceptions.CompilationException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static in.costea.wiles.statics.Constants.DEBUG;

public class CompilationExceptionsCollection extends ArrayList<CompilationException>
{

    public CompilationExceptionsCollection(CompilationExceptionsCollection exceptions)
    {
        super(exceptions);
    }

    public CompilationExceptionsCollection()
    {
        super();
    }

    public void add(@NotNull CompilationException... exceptions)
    {
        this.addAll(List.of(exceptions));
    }

    public void add(@NotNull CompilationExceptionsCollection objToAdd)
    {
        this.addAll(objToAdd);
    }

    public String getExceptionsString()
    {
        var optional = stream().sorted((a, b) ->
                {
                    var loc1 = a.getLocation();
                    var loc2 = b.getLocation();
                    if (loc1 == null) return 1;
                    if (loc2 == null) return -1;
                    var comp = Integer.compare(loc1.line(), loc2.line());
                    if (comp != 0) return comp;
                    var comp2 = Integer.compare(loc1.lineIndex(), loc2.lineIndex());
                    if (comp2 != 0) return comp2;
                    return a.getMessage().compareTo(b.getMessage());
                }).
                map((Exception x) -> "\n    " + x.getMessage() + "\n" + (DEBUG ? Arrays.toString(x.getStackTrace()) : "")).
                reduce((a, b) -> a + b);
        if (optional.isEmpty())
            throw new IllegalStateException();
        return optional.get();
    }

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof CompilationExceptionsCollection obj)
        {
            if (size() != obj.size())
                return false;
            for (int i = 0; i < size(); i++)
                if (!Objects.equals(get(i).getMessage(), obj.get(i).getMessage()))
                    return false;
            return true;
        }
        return false;
    }
}
