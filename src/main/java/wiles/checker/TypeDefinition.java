package wiles.checker;

import wiles.parser.statements.TypeDefinitionStatement;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeDefinition that = (TypeDefinition) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
