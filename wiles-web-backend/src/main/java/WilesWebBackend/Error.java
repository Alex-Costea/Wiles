package WilesWebBackend;

import wiles.shared.TokenLocation;

public record Error(String message, TokenLocation location) {
}
