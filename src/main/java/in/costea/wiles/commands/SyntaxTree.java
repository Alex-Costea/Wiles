package in.costea.wiles.commands;

import in.costea.wiles.converters.TokensToSyntaxTreeConverter;
import in.costea.wiles.data.CompilationExceptionsCollection;

import java.util.List;
import java.util.Objects;

import static in.costea.wiles.statics.Constants.SYNTAX_TYPE;

public abstract class SyntaxTree {
    protected TokensToSyntaxTreeConverter converter;

    public SyntaxTree(TokensToSyntaxTreeConverter converter)
    {
        this.converter=converter;

    }
    public abstract SYNTAX_TYPE getType();
    public abstract List<? extends SyntaxTree> getComponents();

    public abstract CompilationExceptionsCollection process();

    @Override
    public String toString() {
        return toString("");
    }

    public final String toString(String inside)
    {
        StringBuilder sb=new StringBuilder();
        sb.append(getType());
        if(!Objects.equals(inside, ""))
            sb.append(" ").append(inside).append(" ");
        if(getComponents().size()>0)
        {
            sb.append("(");
            int i=0;
            for (SyntaxTree component : getComponents())
            {
                sb.append(component.toString());
                if(i<getComponents().size()-1)
                    sb.append("; ");
                i++;
            }
            sb.append(")");
        }
        return sb.toString();
    }
}
