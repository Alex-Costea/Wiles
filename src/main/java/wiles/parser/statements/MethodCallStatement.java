package wiles.parser.statements;

import wiles.parser.builders.Context;
import wiles.shared.CompilationExceptionsCollection;
import wiles.parser.enums.SyntaxType;
import wiles.parser.enums.WhenRemoveToken;
import wiles.shared.AbstractCompilationException;
import wiles.parser.statements.expressions.InsideMethodCallExpression;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static wiles.parser.builders.ExpectParamsBuilder.tokenOf;
import static wiles.shared.constants.Tokens.PAREN_END_ID;
import static wiles.shared.constants.Tokens.SEPARATOR_ID;

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
            while (transmitter.expectMaybe(tokenOf(PAREN_END_ID).removeWhen(WhenRemoveToken.Never)).isEmpty()) {
                InsideMethodCallExpression newComp = new InsideMethodCallExpression(getContext());
                exceptions.addAll(newComp.process());
                components.add(newComp);
                if (!newComp.isLastExpression())
                    transmitter.expect(tokenOf(SEPARATOR_ID));
                else break;
            }
            transmitter.expect(tokenOf(PAREN_END_ID));
        }
        catch (AbstractCompilationException ex)
        {
            exceptions.add(ex);
        }
        return exceptions;
    }
}
