package WilesWebBackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import wiles.shared.OutputData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

@SpringBootApplication
@RestController
public class WilesWebBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(WilesWebBackendApplication.class, args);
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
		Future<OutputData> future = executor.submit(new WilesTask(args));
		try
		{
			var result = future.get(10, TimeUnit.SECONDS);
			String outputText = result.getOutput();
			String errorsText = result.getExceptionsString();
			if(!Objects.equals(errorsText, ""))
				errorsText = errorsText.substring(5, errorsText.length()-4);
			return ResponseEntity.ok(new CompilationResponse(outputText, errorsText));
		}
		catch (TimeoutException | InterruptedException | ExecutionException e)
		{
			System.out.println(e.getMessage());
			System.out.println(Arrays.toString(e.getStackTrace()));
			return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
					.body(new CompilationResponse("",
							"Interpreter timed out! Reason:" + e.getMessage()));
		}
		finally {
			executor.shutdownNow();
		}
	}
}
