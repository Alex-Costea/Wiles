package WilesWebBackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import wiles.Main;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

@SpringBootApplication
@RestController
public class WilesWebBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(WilesWebBackendApplication.class, args);
	}

	private final PrintStream systemOut = System.out;
	private final PrintStream systemErr = System.err;

	@RequestMapping(value = "/run", method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public CompilationResponse compile(@RequestBody Map<String, Object> payload) throws IOException {
		String code = (String) payload.getOrDefault("code",null);
		if(code == null)
			return new CompilationResponse(null,"Code not provided!");
		String input = (String) payload.getOrDefault("input","");

		ArrayList<String> args = new ArrayList<>();
		args.add("--code="+code);
		args.add("--input="+input);

		//sysout
		ByteArrayOutputStream outputBaos = new ByteArrayOutputStream();
		PrintStream outputStream = new PrintStream(outputBaos);
		System.setOut(outputStream);

		//syserr
		ByteArrayOutputStream errorBaos = new ByteArrayOutputStream();
		PrintStream errorStream = new PrintStream(errorBaos);
		System.setErr(errorStream);

		Main.main(args.toArray(String[]::new));
		System.setOut(systemOut);
		System.setErr(systemErr);
		String errorsText = errorBaos.toString(StandardCharsets.UTF_8);
		if(Objects.equals(errorsText, ""))
			errorsText = null;
		return new CompilationResponse(outputBaos.toString(StandardCharsets.UTF_8),
				errorsText);
	}
}
