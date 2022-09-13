package in.costea.wiles.converters;

import in.costea.wiles.SyntaxTree;
import in.costea.wiles.data.CompilationExceptionsCollection;
import in.costea.wiles.data.Token;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TokensToASTConverter {
    private final List<Token> tokens;
    public TokensToASTConverter(@NotNull List<Token> tokens)
    {
        this.tokens=tokens;
    }
    public SyntaxTree convert() {
        return null;
    }

    public CompilationExceptionsCollection getExceptions() {
        return new CompilationExceptionsCollection();
    }
}
