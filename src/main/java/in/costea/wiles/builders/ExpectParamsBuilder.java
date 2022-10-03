package in.costea.wiles.builders;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;

import static in.costea.wiles.statics.Constants.*;

public class ExpectParamsBuilder
{

    private enum WhenRemoveToken {
        Always,
        WhenFound,
        Never
    }
    public static final WhenRemoveToken ALWAYS=WhenRemoveToken.Always;
    public static final WhenRemoveToken WHEN_FOUND=WhenRemoveToken.WhenFound;
    public static final WhenRemoveToken NEVER=WhenRemoveToken.Never;
    public static final Predicate<String> ANYTHING = (x) -> true;

    public static Predicate<String> isContainedIn(Collection<String> set)
    {
        return set::contains;
    }

    public static ExpectParamsBuilder tokenOf(String expectedToken)
    {
        return new ExpectParamsBuilder(x -> Objects.equals(x, expectedToken))
                .withErrorMessage("Token \""+TOKENS_INVERSE.get(expectedToken)+"\" expected!");
    }

    public static ExpectParamsBuilder tokenOf(Predicate<String> found)
    {
        return new ExpectParamsBuilder(found);
    }



    private Predicate<String> foundTest;
    private String errorMessage = "Shouldn't happen";
    private WhenRemoveToken when = WHEN_FOUND;
    private boolean ignoringNewLine = true;

    private ExpectParamsBuilder(Predicate<String> foundTest)
    {
        this.foundTest = foundTest;
    }

    public ExpectParamsBuilder withErrorMessage(String message)
    {
        this.errorMessage = message;
        return this;
    }

    public ExpectParamsBuilder removeTokenWhen(WhenRemoveToken when)
    {
        this.when = when;
        return this;
    }

    public ExpectParamsBuilder dontIgnoreNewLine()
    {
        ignoringNewLine = false;
        return this;
    }

    public ExpectParamsBuilder or(Predicate<String> otherTest)
    {
        foundTest = foundTest.or(otherTest);
        return this;
    }

    @SuppressWarnings("unused")
    public ExpectParamsBuilder or(String token)
    {
        foundTest = foundTest.or(x->x.equals(token));
        return this;
    }

    public Predicate<String> getFoundTest() {
        return foundTest;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public WhenRemoveToken getWhenRemoveToken() {
        return when;
    }

    public boolean isIgnoringNewLine() {
        return ignoringNewLine;
    }
}
