package in.costea.wiles.statements;

import in.costea.wiles.builders.Context;
import in.costea.wiles.data.CompilationExceptionsCollection;
import in.costea.wiles.enums.SyntaxType;
import in.costea.wiles.exceptions.InternalErrorException;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static in.costea.wiles.constants.ErrorMessages.NOT_YET_IMPLEMENTED_ERROR;

public class WhileStatement extends AbstractStatement{
    public WhileStatement(@NotNull Context oldContext) {
        super(oldContext.setWithinLoop(true));
    }

    @NotNull
    @Override
    public SyntaxType getType() {
        return SyntaxType.WHILE;
    }

    @NotNull
    @Override
    public List<AbstractStatement> getComponents() {
        throw new InternalErrorException(NOT_YET_IMPLEMENTED_ERROR);
    }

    @NotNull
    @Override
    public CompilationExceptionsCollection process() {
        throw new InternalErrorException(NOT_YET_IMPLEMENTED_ERROR);
    }
}
