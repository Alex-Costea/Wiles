package in.costea.wiles.builders;

import in.costea.wiles.statements.*;
import in.costea.wiles.statements.expressions.AssignableExpression;
import in.costea.wiles.statements.expressions.DefaultExpression;
import in.costea.wiles.data.Token;
import in.costea.wiles.enums.WhenRemoveToken;
import in.costea.wiles.exceptions.AbstractCompilationException;
import in.costea.wiles.exceptions.UnexpectedTokenException;
import in.costea.wiles.services.TokenTransmitter;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static in.costea.wiles.builders.ExpectParamsBuilder.ANYTHING;
import static in.costea.wiles.builders.ExpectParamsBuilder.tokenOf;
import static in.costea.wiles.constants.ErrorMessages.UNEXPECTED_TOKEN_ERROR;
import static in.costea.wiles.statements.expressions.AbstractExpression.START_OF_EXPRESSION;
import static in.costea.wiles.constants.Tokens.*;
import static in.costea.wiles.constants.ErrorMessages.INTERNAL_ERROR;

public class StatementFactory {
    @NotNull
    private final Set<Class<? extends AbstractStatement>> statement =new HashSet<>();
    @NotNull private final TokenTransmitter transmitter;
    public StatementFactory(@NotNull TokenTransmitter transmitter){
        this.transmitter=transmitter;
    }

    public @NotNull StatementFactory addType(@NotNull Class<? extends AbstractStatement> statement)
    {
        this.statement.add(statement);
        return this;
    }

    public @NotNull AbstractStatement create(@NotNull String errorMessage) throws AbstractCompilationException {
        if(statement.contains(AssignableExpression.class))
            if (transmitter.expectMaybe(START_OF_EXPRESSION).isPresent())
                return new AssignableExpression(transmitter);

        if(statement.contains(DefaultExpression.class))
            if (transmitter.expectMaybe(START_OF_EXPRESSION).isPresent())
                return new DefaultExpression(transmitter);

        if(statement.contains(DeclarationStatement.class))
            if(transmitter.expectMaybe(tokenOf(DECLARE_ID)).isPresent())
                return new DeclarationStatement(transmitter);

        if(statement.contains(MethodStatement.class))
            if(transmitter.expectMaybe(tokenOf(METHOD_ID)).isPresent())
                return new MethodStatement(transmitter);

        if(statement.contains(ReturnStatement.class))
            if(transmitter.expectMaybe(tokenOf(RETURN_ID)).isPresent())
                return new ReturnStatement(transmitter);

        //Expression not found
        ExpectParamsBuilder paramsBuilder = tokenOf(ANYTHING).removeWhen(WhenRemoveToken.Never)
                .withErrorMessage(errorMessage);
        Token newToken = transmitter.expect(paramsBuilder);
        throw new UnexpectedTokenException(UNEXPECTED_TOKEN_ERROR, newToken.getLocation());
    }

    public @NotNull AbstractStatement create() throws AbstractCompilationException {
        return create(INTERNAL_ERROR);
    }

    @SuppressWarnings("unused")
    public @NotNull Optional<AbstractStatement> createMaybe()
    {
        try
        {
            return Optional.of(create(INTERNAL_ERROR));
        } catch (AbstractCompilationException e) {
            return Optional.empty();
        }
    }
}
