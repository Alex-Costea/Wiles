package WilesWebBackend;

import org.jetbrains.annotations.NotNull;
import wiles.shared.TokenLocation;

public record SyntaxOutput(@NotNull String type, @NotNull TokenLocation location) {
}
