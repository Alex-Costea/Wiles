package in.costea.wiles.commands;

import in.costea.wiles.data.CompilationExceptionsCollection;
import in.costea.wiles.services.TokenTransmitter;
import in.costea.wiles.statics.Constants;

import java.util.ArrayList;
import java.util.List;

public class Identifier extends SyntaxTree {
    private final List<SyntaxTree> components=new ArrayList<>();
    private final String name;

    public Identifier(String name, TokenTransmitter transmitter) {
        super(transmitter);
        this.name=name;
    }

    @Override
    public Constants.SYNTAX_TYPE getType() {
        return Constants.SYNTAX_TYPE.IDENTIFIER;
    }

    @Override
    public List<SyntaxTree> getComponents() {
        return components;
    }

    @Override
    public CompilationExceptionsCollection process() {
        return new CompilationExceptionsCollection();
    }

    @Override
    public String toString() {
        return super.toString(name);
    }
}
