package in.costea.wiles.commands;

import in.costea.wiles.statics.Constants.SYNTAX_TYPE;

import java.util.ArrayList;
import java.util.List;

public class MethodCommand extends SyntaxTree {
    private final List<SyntaxTree> components=new ArrayList<>();
    @Override
    public SYNTAX_TYPE getType() {
        return SYNTAX_TYPE.METHOD;
    }

    @Override
    public List<SyntaxTree> getComponents() {
        return components;
    }

    @Override
    public void setComponents(List<? extends SyntaxTree> components) {

    }
}
