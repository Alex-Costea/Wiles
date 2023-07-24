package WilesWebBackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import wiles.WilesCompiler;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

@SpringBootApplication
@RestController
public class WilesWebBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(WilesWebBackendApplication.class, args);
	}

	@RequestMapping(value = "/run", method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public CompilationResponse compile(@RequestBody Map<String, Object> payload) {
		String code = (String) payload.getOrDefault("code",null);
		if(code == null)
			return new CompilationResponse(null,"Code not provided!");
		String input = (String) payload.getOrDefault("input","");

		ArrayList<String> args = new ArrayList<>();
		args.add("--code="+code);
		args.add("--input="+input);

		var result = WilesCompiler.getOutput(args.toArray(String[]::new));
		String outputText = result.getFirst();
		String errorsText = result.getSecond();

		if(Objects.equals(errorsText, ""))
			errorsText = null;
		return new CompilationResponse(outputText, errorsText);
	}
}
