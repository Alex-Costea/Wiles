package in.costea.wiles;

import in.costea.wiles.exceptions.ComponentException;

import java.util.List;

import static in.costea.wiles.statics.Constants.SYNTAX_TYPE;

public abstract class SyntaxTree {

    public abstract SYNTAX_TYPE getType();
    public abstract List<? extends SyntaxTree> getComponents();

    public abstract void setComponents(List<? extends SyntaxTree> components) throws ComponentException;

    protected void fail(String reason) throws ComponentException
    {
        throw new ComponentException(reason);
    }


    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder();
        sb.append(getType());
        if(getComponents().size()>0)
        {
            sb.append("(");
            for (SyntaxTree component : getComponents())
            {
                sb.append(component.toString());
                sb.append(";");
            }
            sb.append(")");
        }
        return sb.toString();
    }
}
