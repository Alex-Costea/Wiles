package wiles.shared;

import com.eclipsesource.json.*;

import java.io.*;
import java.util.Objects;

public final class JSONService {
    private static final String PARSED = "parsed";
    private static final String NAME = "name";
    private static final String TYPE = "type";
    private static final String LOCATION = "location";
    private static final String COMPONENTS = "components";
    private static final String LINE = "line";
    private static final String LINE_INDEX = "lineIndex";

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
        JSONStatement statement = new JSONStatement();
        JsonObject object = Json.parse(text).asObject();

        JsonValue name = object.get(NAME);
        if(name != null)
            statement.setName(name.asString());

        JsonValue parsed = object.get(PARSED);
        if(parsed != null)
            statement.setParsed(parsed.asBoolean());
        else statement.setParsed(null);

        JsonValue type = Objects.requireNonNull(object.get(TYPE));
        statement.setSyntaxType(SyntaxType.valueOf(type.asString()));

        JsonValue location = object.get(LOCATION);
        if(location != null)
        {
            JsonObject locationObject = location.asObject();
            int line = Objects.requireNonNull(locationObject.get(LINE)).asInt();
            int lineIndex = Objects.requireNonNull(locationObject.get(LINE_INDEX)).asInt();
            TokenLocation tokenLocation = new TokenLocation(line, lineIndex);
            statement.setLocation(tokenLocation);
        }

        JsonValue components = object.get(COMPONENTS);
        if(components != null)
        {
            JsonArray array = components.asArray();
            for(var component : array.values())
            {
                var newComponent = readValueAsJSONStatement(component.toString());
                statement.components.add(newComponent);
            }
        }

        return statement;
    }

    private static JsonObject getJsonObjectFromStatement(StatementInterface statement)
    {
        JsonObject value = Json.object();

        Boolean parsed = statement.getParsed();
        if (parsed != null)
            value.add(PARSED, parsed);

        String name = statement.getName();
        if (!name.equals(""))
            value.add(NAME, name);

        String type = Objects.requireNonNull(statement.getSyntaxType()).toString();
        value.add(TYPE, type);

        TokenLocation location = statement.getLocation();
        if (location != null)
        {
            JsonObject object = Json.object();
            object.add(LINE, location.getLine());
            object.add(LINE_INDEX, location.getLineIndex());
            value.add(LOCATION, object);
        }

        var components = statement.getComponents();
        if(!components.isEmpty())
        {
            JsonArray array = new JsonArray();
            for(var component : components)
            {
                array.add(getJsonObjectFromStatement(component));
            }
            value.add(COMPONENTS, array);
        }

        return value;
    }

    public static String writeValueAsString(StatementInterface statement) {
        return getJsonObjectFromStatement(statement).toString(WriterConfig.PRETTY_PRINT);
    }
}