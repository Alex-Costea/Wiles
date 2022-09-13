package in.costea.wiles.converters;

import in.costea.wiles.SyntaxTree;
import in.costea.wiles.data.CompilationExceptionsCollection;
import in.costea.wiles.data.Token;
import in.costea.wiles.factories.SyntaxTreeFactory;
import in.costea.wiles.statics.Constants.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TokensToSyntaxTreeConverter {
    SyntaxTree syntaxTree= SyntaxTreeFactory.of(SYNTAX_TYPE.PROGRAM);

    private final List<Token> tokens;
    public TokensToSyntaxTreeConverter(@NotNull List<Token> tokens)
    {
        this.tokens=tokens;
    }
    public SyntaxTree convert() {
        return syntaxTree;
    }

    public CompilationExceptionsCollection getExceptions() {
        return new CompilationExceptionsCollection();
    }
}
