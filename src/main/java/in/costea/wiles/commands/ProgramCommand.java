package in.costea.wiles.commands;

import in.costea.wiles.SyntaxTree;
import in.costea.wiles.statics.Constants.SYNTAX_TYPE;

import java.util.ArrayList;
import java.util.List;

public class ProgramCommand extends SyntaxTree {
    private final List<MethodCommand> components=new ArrayList<>();
    @Override
    public SYNTAX_TYPE getType() {
        return SYNTAX_TYPE.PROGRAM;
    }

    @Override
    public List<MethodCommand> getComponents() {
        return components;
    }

    @Override
    public void setComponents(List<? extends SyntaxTree> components) {

    }
}
