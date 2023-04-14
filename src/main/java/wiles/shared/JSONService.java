package wiles.shared;

import com.eclipsesource.json.*;
import org.apache.commons.lang3.NotImplementedException;

import java.io.*;
import java.util.Objects;

public final class JSONService {
    private JSONService(){}
    public static JSONStatement readValueAsJSONStatement(File file) {
        try(BufferedReader br = new BufferedReader(new FileReader(file)))
        {
            StringBuilder text = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
            }
            return readValueAsJSONStatement(text.toString());
        }
        catch (IOException ex)
        {
            throw new InternalErrorException(ex.toString());
        }
    }

    public static void writeValue(File file, StatementInterface statement) {
        try(FileWriter writer = new FileWriter(file)) {
            writer.append(writeValueAsString(statement));
        }
        catch (IOException ex){
            throw new InternalErrorException(ex.toString());
        }
    }

    public static JSONStatement readValueAsJSONStatement(String text) {
        throw new NotImplementedException("read JSON " + text);
    }

    private static JsonObject getLocation(TokenLocation location)
    {
        JsonObject value = Json.object();
        value.add("line",location.getLine());
        value.add("lineIndex",location.getLineIndex());
        return value;
    }

    private static JsonObject getJsonObjectFromStatement(StatementInterface statement)
    {
        JsonObject value = Json.object();

        Boolean parsed = statement.getParsed();
        if (parsed != null)
            value.add("parsed", parsed);

        String name = statement.getName();
        if (!name.equals(""))
            value.add("name", name);

        String type = Objects.requireNonNull(statement.getSyntaxType()).toString();
        value.add("type", type);

        TokenLocation location = statement.getLocation();
        if (location != null)
        {
            value.add("location",getLocation(location));
        }

        var components = statement.getComponents();
        if(!components.isEmpty())
        {
            JsonArray array = new JsonArray();
            for(var component : components)
            {
                array.add(getJsonObjectFromStatement(component));
            }
            value.add("components",array);
        }

        return value;
    }

    public static String writeValueAsString(StatementInterface statement) {
        return getJsonObjectFromStatement(statement).toString(WriterConfig.PRETTY_PRINT);
    }
}