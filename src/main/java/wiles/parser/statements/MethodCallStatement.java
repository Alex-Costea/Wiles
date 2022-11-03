package wiles.parser.statements;

import wiles.parser.builders.Context;
import wiles.parser.data.CompilationExceptionsCollection;
import wiles.parser.enums.SyntaxType;
import wiles.parser.exceptions.AbstractCompilationException;
import wiles.parser.statements.expressions.InsideMethodCallExpression;
import org.jetbrains.annotations.NotNull;
import wiles.parser.builders.ExpectParamsBuilder;
import wiles.parser.constants.Tokens;

import java.util.ArrayList;

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
            while (transmitter.expectMaybe(ExpectParamsBuilder.tokenOf(Tokens.PAREN_END_ID)).isEmpty()) {
                InsideMethodCallExpression newComp = new InsideMethodCallExpression(getContext());
                exceptions.addAll(newComp.process());
                components.add(newComp);
                if (!newComp.isLastExpression())
                    transmitter.expect(ExpectParamsBuilder.tokenOf(Tokens.SEPARATOR_ID));
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
