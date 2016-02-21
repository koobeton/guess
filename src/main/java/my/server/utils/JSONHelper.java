package my.server.utils;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonWriter;
import java.io.Reader;
import java.io.StringWriter;

public class JSONHelper {

    public static int readInt(String key, Reader reader) {

        JsonReader jsonReader = Json.createReader(reader);
        JsonObject jsonObject = jsonReader.readObject();
        int value = jsonObject.getInt(key);
        jsonReader.close();

        return value;
    }

    public static String toJSONString(String key, String value) {

        JsonObject jsonObject = Json.createObjectBuilder().add(key, value).build();

        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = Json.createWriter(stringWriter);
        jsonWriter.writeObject(jsonObject);
        jsonWriter.close();

        return stringWriter.toString();
    }
}
