package in.costea.wiles.builders;

import in.costea.wiles.commands.AbstractCommand;
import in.costea.wiles.commands.DeclarationCommand;
import in.costea.wiles.commands.MethodCommand;
import in.costea.wiles.commands.expressions.AssignableExpressionCommand;
import in.costea.wiles.commands.expressions.RightSideExpressionCommand;
import in.costea.wiles.data.Token;
import in.costea.wiles.enums.WhenRemoveToken;
import in.costea.wiles.exceptions.AbstractCompilationException;
import in.costea.wiles.exceptions.TokenExpectedException;
import in.costea.wiles.exceptions.UnexpectedEndException;
import in.costea.wiles.exceptions.UnexpectedTokenException;
import in.costea.wiles.services.TokenTransmitter;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static in.costea.wiles.builders.ExpectParamsBuilder.ANYTHING;
import static in.costea.wiles.builders.ExpectParamsBuilder.tokenOf;
import static in.costea.wiles.commands.expressions.AbstractExpressionCommand.START_OF_EXPRESSION;
import static in.costea.wiles.statics.Constants.*;

public class CommandFactory {
    @NotNull
    private final Set<Class<? extends AbstractCommand>> commands=new HashSet<>();
    @NotNull private final TokenTransmitter transmitter;
    public CommandFactory(@NotNull TokenTransmitter transmitter){
        this.transmitter=transmitter;
    }

    public @NotNull CommandFactory of(@NotNull Class<? extends AbstractCommand> command)
    {
        commands.add(command);
        return this;
    }

    public @NotNull AbstractCommand create(@NotNull String errorMessage) throws TokenExpectedException, UnexpectedEndException, UnexpectedTokenException {
        if(commands.contains(AssignableExpressionCommand.class))
            if (transmitter.expectMaybe(START_OF_EXPRESSION).isPresent())
                return new AssignableExpressionCommand(transmitter);

        if(commands.contains(RightSideExpressionCommand.class))
            if (transmitter.expectMaybe(START_OF_EXPRESSION).isPresent())
                return new RightSideExpressionCommand(transmitter);

        if(commands.contains(DeclarationCommand.class))
            if(transmitter.expectMaybe(tokenOf(DECLARE_ID)).isPresent())
                return new DeclarationCommand(transmitter);

        if(commands.contains(MethodCommand.class))
            if(transmitter.expectMaybe(tokenOf(METHOD_ID)).isPresent())
                return new MethodCommand(transmitter);

        //Expression not found
        ExpectParamsBuilder paramsBuilder = tokenOf(ANYTHING).removeWhen(WhenRemoveToken.Never).withErrorMessage(errorMessage);
        Token newToken = transmitter.expect(paramsBuilder);
        String content = TOKENS_INVERSE.getOrDefault(newToken.getContent(),newToken.getContent());
        throw new UnexpectedTokenException(content, newToken.getLocation());
    }

    public @NotNull AbstractCommand create() throws TokenExpectedException, UnexpectedTokenException, UnexpectedEndException {
        return create(INTERNAL_ERROR);
    }

    @SuppressWarnings("unused")
    public @NotNull Optional<AbstractCommand> createMaybe()
    {
        try
        {
            return Optional.of(create(INTERNAL_ERROR));
        } catch (AbstractCompilationException e) {
            return Optional.empty();
        }
    }
}
