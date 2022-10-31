package in.costea.wiles.statements;

import in.costea.wiles.builders.Context;
import in.costea.wiles.data.CompilationExceptionsCollection;
import in.costea.wiles.enums.SyntaxType;
import in.costea.wiles.statements.expressions.InsideMethodCallExpression;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static in.costea.wiles.builders.ExpectParamsBuilder.tokenOf;
import static in.costea.wiles.constants.Tokens.BRACKET_END_ID;
import static in.costea.wiles.constants.Tokens.SEPARATOR_ID;

public class MethodCallStatement extends AbstractStatement{
    ArrayList<AbstractStatement> components = new ArrayList<>();

    public MethodCallStatement(@NotNull Context context) {
        super(context);
    }

    @NotNull
    @Override
    public SyntaxType getType() {
        return SyntaxType.METHOD_CALL;
    }

    @Override
    public @NotNull ArrayList<AbstractStatement> getComponents() {
        return components;
    }

    @NotNull
    @Override
    public CompilationExceptionsCollection process() {
        var exceptions = new CompilationExceptionsCollection();
        InsideMethodCallExpression newComp;
        if(transmitter.expectMaybe(tokenOf(BRACKET_END_ID)).isEmpty())
        {
            newComp =  new InsideMethodCallExpression(getContext());
            while (components.size() == 0 || !newComp.isLastExpression()) {
                exceptions.addAll(newComp.process());
                components.add(newComp);
                if (transmitter.expectMaybe(tokenOf(SEPARATOR_ID)).isEmpty()) break;
                newComp = new InsideMethodCallExpression(getContext());
            }
        }
        return exceptions;
    }
}
