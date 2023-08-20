package WilesWebBackend;

import kotlin.Pair;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

@SpringBootApplication
@RestController
@ComponentScan(basePackages = "WilesWebBackend")
public class WilesWebBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(WilesWebBackendApplication.class, args);
	}

	@RequestMapping(value = "/getcsfr")
	public ResponseEntity<String> getCsfr() {
		return ResponseEntity.ok().body("{}");
	}

	@RequestMapping(value = "/run", method = RequestMethod.PUT,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CompilationResponse> compile(@RequestBody Map<String, Object> payload) {
		String code = (String) payload.getOrDefault("code",null);
		if(code == null)
			return ResponseEntity.badRequest().body(new CompilationResponse(null,"Code not provided!"));
		String input = (String) payload.getOrDefault("input","");

		ArrayList<String> args = new ArrayList<>();
		args.add("--code="+code);
		args.add("--input="+input);

		ExecutorService executor = Executors.newSingleThreadExecutor();
		Future<Pair<String, String>> future = executor.submit(new WilesTask(args));
		try
		{
			var result = future.get(10, TimeUnit.SECONDS);
			String outputText = result.getFirst();
			String errorsText = result.getSecond();
			if(Objects.equals(errorsText, ""))
				errorsText = null;
			return ResponseEntity.ok(new CompilationResponse(outputText, errorsText));
		}
		catch (TimeoutException | InterruptedException | ExecutionException e)
		{
			return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
					.body(new CompilationResponse("", "Interpreter timed out!"));
		}
		finally {
			executor.shutdownNow();
		}
	}
}
