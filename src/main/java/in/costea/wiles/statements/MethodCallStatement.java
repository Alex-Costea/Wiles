package in.costea.wiles.statements;

import in.costea.wiles.builders.Context;
import in.costea.wiles.data.CompilationExceptionsCollection;
import in.costea.wiles.enums.SyntaxType;
import in.costea.wiles.exceptions.AbstractCompilationException;
import in.costea.wiles.statements.expressions.InsideMethodCallExpression;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static in.costea.wiles.builders.ExpectParamsBuilder.tokenOf;
import static in.costea.wiles.constants.Tokens.PAREN_END_ID;
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
        try
        {
            while (transmitter.expectMaybe(tokenOf(PAREN_END_ID)).isEmpty()) {
                InsideMethodCallExpression newComp = new InsideMethodCallExpression(getContext());
                exceptions.addAll(newComp.process());
                components.add(newComp);
                if (!newComp.isLastExpression())
                    transmitter.expect(tokenOf(SEPARATOR_ID));
                else break;
            }

        }
        catch (AbstractCompilationException ex)
        {
            exceptions.add(ex);
        }
        return exceptions;
    }
}
