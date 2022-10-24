package in.costea.wiles.builders;

import in.costea.wiles.data.Token;
import in.costea.wiles.enums.WhenRemoveToken;
import in.costea.wiles.exceptions.AbstractCompilationException;
import in.costea.wiles.exceptions.InternalErrorException;
import in.costea.wiles.exceptions.UnexpectedTokenException;
import in.costea.wiles.services.TokenTransmitter;
import in.costea.wiles.statements.AbstractStatement;
import in.costea.wiles.statements.DeclarationStatement;
import in.costea.wiles.statements.MethodStatement;
import in.costea.wiles.statements.ReturnStatement;
import in.costea.wiles.statements.expressions.AssignableExpression;
import in.costea.wiles.statements.expressions.DefaultExpression;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import static in.costea.wiles.builders.ExpectParamsBuilder.tokenOf;
import static in.costea.wiles.constants.ErrorMessages.*;
import static in.costea.wiles.constants.Predicates.ANYTHING;
import static in.costea.wiles.constants.Predicates.START_OF_EXPRESSION;
import static in.costea.wiles.constants.Tokens.*;

public class StatementFactory {
    @NotNull
    private final Set<Class<? extends AbstractStatement>> statements =new HashSet<>();
    @NotNull private final TokenTransmitter transmitter;
    private static final HashMap<Class<? extends AbstractStatement>,ExpectParamsBuilder> params = new HashMap<>();
    private static final HashMap<Class<? extends AbstractStatement>, Function<TokenTransmitter,AbstractStatement>>
            createObject = new HashMap<>();
    static {
        params.put(AssignableExpression.class, START_OF_EXPRESSION);
        params.put(DefaultExpression.class, START_OF_EXPRESSION);
        params.put(DeclarationStatement.class, tokenOf(DECLARE_ID));
        params.put(MethodStatement.class, tokenOf(METHOD_ID));
        params.put(ReturnStatement.class, tokenOf(RETURN_ID));
        createObject.put(AssignableExpression.class, AssignableExpression::new);
        createObject.put(DefaultExpression.class, DefaultExpression::new);
        createObject.put(DeclarationStatement.class, DeclarationStatement::new);
        createObject.put(MethodStatement.class, MethodStatement::new);
        createObject.put(ReturnStatement.class, ReturnStatement::new);
    }
    public StatementFactory(@NotNull TokenTransmitter transmitter){
        this.transmitter=transmitter;
    }

    public @NotNull StatementFactory addType(@NotNull Class<? extends AbstractStatement> statement)
    {
        if(!params.containsKey(statement))
            throw new InternalErrorException(NOT_YET_IMPLEMENTED_ERROR);
        this.statements.add(statement);
        return this;
    }

    public @NotNull AbstractStatement create(@NotNull String errorMessage) throws AbstractCompilationException {
        for(var statement:statements)
            if(transmitter.expectMaybe(params.get(statement)).isPresent())
                return createObject.get(statement).apply(transmitter);

        //Expression not found
        ExpectParamsBuilder paramsBuilder = tokenOf(ANYTHING).removeWhen(WhenRemoveToken.Never)
                .withErrorMessage(errorMessage);
        Token newToken = transmitter.expect(paramsBuilder);
        throw new UnexpectedTokenException(INVALID_STATEMENT_ERROR, newToken.getLocation());
    }

    public @NotNull AbstractStatement create() throws AbstractCompilationException {
        return create(INTERNAL_ERROR);
    }
}
