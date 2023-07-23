package WilesWebBackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import wiles.Main;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;

@SpringBootApplication
@RestController
public class WilesWebBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(WilesWebBackendApplication.class, args);
	}


	@RequestMapping(value = "/run", method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public CompilationResponse compile(@RequestBody Map<String, Object> payload) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream outputStream = new PrintStream(baos);
		System.setOut(outputStream);
		String[] args = {"--nofile",payload.get("code").toString()};
		Main.main(args);
		System.setOut(System.out);
		return new CompilationResponse(baos.toString("UTF-8"));
	}
}
