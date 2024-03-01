package WilesWebBackend;

import java.util.List;

public record CompilationResponse(String response, String errors, List<Error> errorList) {

}
