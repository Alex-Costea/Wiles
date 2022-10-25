package in.costea.wiles.statements;

import in.costea.wiles.builders.Context;
import in.costea.wiles.data.CompilationExceptionsCollection;
import in.costea.wiles.enums.SyntaxType;
import in.costea.wiles.exceptions.AbstractCompilationException;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static in.costea.wiles.builders.ExpectParamsBuilder.tokenOf;
import static in.costea.wiles.constants.Tokens.ROUND_BRACKET_END_ID;

public class MethodCallStatement extends AbstractStatement{
    public MethodCallStatement(@NotNull Context context) {
        super(context);
    }

    @NotNull
    @Override
    public SyntaxType getType() {
        return SyntaxType.METHOD_CALL;
    }

    @NotNull
    @Override
    public List<AbstractStatement> getComponents() {
        //TODO: complete
        return List.of();
    }

    @NotNull
    @Override
    public CompilationExceptionsCollection process() {
        //TODO: complete
        var exceptions = new CompilationExceptionsCollection();
        try{
            transmitter.expect(tokenOf(ROUND_BRACKET_END_ID));
        }
        catch (AbstractCompilationException ex)
        {
            exceptions.add(ex);
        }
        return exceptions;
    }
}
