package wiles.shared;

import org.apache.commons.lang3.NotImplementedException;

import java.io.*;

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
        throw new NotImplementedException("read JSON");
    }

    public static String writeValueAsString(StatementInterface statement)
    {
        //("parsed", "name", "type", "location", "components")
        throw new NotImplementedException("write JSON " + statement);
    }
}