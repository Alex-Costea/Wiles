package in.costea.wiles.commands;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import in.costea.wiles.data.CompilationExceptionsCollection;
import in.costea.wiles.enums.SyntaxType;
import in.costea.wiles.services.TokenTransmitter;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

@JsonPropertyOrder({"compiledSuccessfully", "name", "type", "components"})
public abstract class AbstractCommand {
    protected final TokenTransmitter transmitter;
    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @NotNull
    protected String name = "";

    public AbstractCommand(TokenTransmitter transmitter) {
        this.transmitter = transmitter;
    }

    @JsonProperty
    public abstract SyntaxType getType();

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public abstract List<? extends AbstractCommand> getComponents();

    public abstract CompilationExceptionsCollection process();

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getType());
        if (!Objects.equals(name, ""))
            sb.append(" ").append(name);
        if (getComponents().size() > 0) {
            sb.append("(");
            int i = 0;
            for (AbstractCommand component : getComponents()) {
                sb.append(component.toString());
                if (i < getComponents().size() - 1)
                    sb.append("; ");
                i++;
            }
            sb.append(")");
        }
        return sb.toString();
    }

}
