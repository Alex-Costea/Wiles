package in.costea.wiles;

import in.costea.wiles.exceptions.CompilationException;

import java.util.ArrayList;
import java.util.List;

public class ExceptionsCollection {
    private final List<CompilationException> exceptionList=new ArrayList<>();
    public int size() {
        return exceptionList.size();
    }

    public void add(ExceptionsCollection exceptions) {
        exceptionList.addAll(exceptions.getList());
    }

    public void add(CompilationException ex) {
        exceptionList.add(ex);
    }

    public CompilationException get(int i) {
        return exceptionList.get(i);
    }

    private List<CompilationException> getList() {
        return exceptionList;
    }

    public String getExceptionsString() {
        return exceptionList.stream().map((Exception x)->"\n"+x.getMessage()).reduce((a,b)->a+b)
                .orElseThrow(()->new IllegalStateException("Exception list must not be empty!"));
    }
}
