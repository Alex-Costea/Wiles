package wiles.parser.statements;

import wiles.parser.builders.ParserContext;
import wiles.shared.AbstractStatement;
import wiles.shared.WilesExceptionsCollection;
import wiles.shared.SyntaxType;
import wiles.parser.enums.WhenRemoveToken;
import wiles.shared.WilesException;
import wiles.parser.statements.expressions.InsideMethodCallExpression;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static wiles.parser.builders.ExpectParamsBuilder.tokenOf;
import static wiles.shared.constants.Tokens.PAREN_END_ID;
import static wiles.shared.constants.Tokens.SEPARATOR_ID;

public class MethodCallStatement extends AbstractStatement {
    ArrayList<AbstractStatement> components = new ArrayList<>();

    public MethodCallStatement(@NotNull ParserContext context) {
        super(context);
    }

    @NotNull
    @Override
    public SyntaxType getSyntaxType() {
        return SyntaxType.FUNC_CALL;
    }

    @Override
    public @NotNull ArrayList<AbstractStatement> getComponents() {
        return components;
    }

    @NotNull
    @Override
    public WilesExceptionsCollection process() {
        var exceptions = new WilesExceptionsCollection();
        try
        {
            while (transmitter.expectMaybe(tokenOf(PAREN_END_ID).removeWhen(WhenRemoveToken.Never)).isEmpty()) {
                InsideMethodCallExpression newComp = new InsideMethodCallExpression(getContext());
                exceptions.addAll(newComp.process());
                components.add(newComp);
                if (transmitter.expectMaybe(tokenOf(SEPARATOR_ID)).isEmpty()) break;
            }
            transmitter.expect(tokenOf(PAREN_END_ID));
        }
        catch (WilesException ex)
        {
            exceptions.add(ex);
        }
        return exceptions;
    }
}
