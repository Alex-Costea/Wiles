package wiles.checker;

import wiles.parser.statements.TypeDefinitionStatement;

public class TypeDefinition {
    private final String name;
    public TypeDefinition(String name)
    {
        this.name = name;
    }

    public TypeDefinition(TypeDefinitionStatement statement)
    {
        this.name = statement.name;
    }

    public String getName() {
        return name;
    }
}
