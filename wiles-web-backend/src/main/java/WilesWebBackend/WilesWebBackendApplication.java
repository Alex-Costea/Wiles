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
import wiles.shared.CompilationExceptionsCollection;
import wiles.shared.JSONStatement;
import wiles.shared.OutputData;

import java.util.*;
import java.util.concurrent.*;

@SpringBootApplication
@RestController
public class WilesWebBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(WilesWebBackendApplication.class, args);
	}


	private List<Error> convertToErrorList(CompilationExceptionsCollection exceptions)
	{
		return exceptions.stream().map(it -> new Error(it.getMessage(), it.tokenLocation)).toList();
	}

	private String getType(JSONStatement statement)
	{
		String syntaxName = Objects.requireNonNull(statement.getSyntaxType()).name();
		if(syntaxName.equals("TOKEN"))
		{
			if(statement.getName().startsWith("!"))
				return "IDENTIFIER";
			if(statement.getName().startsWith("@"))
				return "STRING";
			else if(statement.getName().startsWith("#"))
				return "NUMBER";
			else if (statement.getName().contains("APPLY"))
				return "NONE";
			else return "TOKEN";
		}
		else return syntaxName;
	}

	private List<SyntaxOutput> getSyntaxOutput(JSONStatement statement)
	{
		final var list = new ArrayList<SyntaxOutput>(List.of());
		if(statement.getLocation() != null)
		{
			list.add(new SyntaxOutput(getType(statement), statement.getLocation()));
		}
		for(var subStatement : statement.components)
		{
			list.addAll(getSyntaxOutput(subStatement));
		}
		return list;
	}

	@RequestMapping(value = "/syntax", method=RequestMethod.PUT,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<SyntaxOutput>> getSyntax(@RequestBody Map<String, Object> payload)
	{
		String code = (String) payload.getOrDefault("code","");
		ArrayList<String> args = new ArrayList<>();
		args.add("--code="+code);
		args.add("--syntax");

		ExecutorService executor = Executors.newSingleThreadExecutor();
		Future<OutputData> future = executor.submit(new WilesTask(args));

		try
		{
			var result = future.get(10, TimeUnit.SECONDS);
			var syntax = result.getSyntax();
			if(!result.getExceptions().isEmpty())
				return ResponseEntity.ok(List.of());
            assert syntax != null;
            var syntaxOutput = getSyntaxOutput(syntax);
			syntaxOutput.sort((o1, o2) ->
            {
                if (o1.location().getLine() < o2.location().getLine())
                    return -1;
                else if (o1.location().getLine() > o2.location().getLine())
                    return 1;
                else return Integer.compare(o1.location().getLineIndex(), o2.location().getLineIndex());
            });
            return ResponseEntity.ok(syntaxOutput);
		}
		catch (TimeoutException | InterruptedException | ExecutionException e)
		{
			System.out.println(e.getMessage());
			System.out.println(Arrays.toString(e.getStackTrace()));
			return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
					.body(null);
		}
		finally {
			executor.shutdownNow();
		}
	}

	@RequestMapping(value = "/run", method = RequestMethod.PUT,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CompilationResponse> compile(@RequestBody Map<String, Object> payload) {
		String code = (String) payload.getOrDefault("code",null);
		if(code == null)
			return ResponseEntity.badRequest().body(new CompilationResponse(
					null,"Code not provided!", List.of()));
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
			var errorList = result.getExceptions();
			return ResponseEntity.ok(new CompilationResponse(outputText, errorsText, convertToErrorList(errorList)));
		}
		catch (TimeoutException | InterruptedException | ExecutionException e)
		{
			System.out.println(e.getMessage());
			System.out.println(Arrays.toString(e.getStackTrace()));
			return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
					.body(new CompilationResponse("",
							"Interpreter timed out! Reason:" + e.getMessage(),
							List.of()));
		}
		finally {
			executor.shutdownNow();
		}
	}
}
