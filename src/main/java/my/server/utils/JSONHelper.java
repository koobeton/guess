package my.server.utils;

import javax.json.*;
import java.io.StringReader;
import java.io.StringWriter;

public class JSONHelper {

    private JsonObjectBuilder builder = Json.createObjectBuilder();

    public static Integer readInt(String key, String jsonString) {

        JsonNumber jsonNumber = getJsonObject(jsonString).getJsonNumber(key);
        return jsonNumber != null ? jsonNumber.intValue() : null;
    }

    public static String readString(String key, String jsonString) {

        return getJsonObject(jsonString).getString(key, null);
    }

    private static JsonObject getJsonObject(String jsonString) {

        JsonReader jsonReader = Json.createReader(new StringReader(jsonString));
        JsonObject jsonObject = jsonReader.readObject();
        jsonReader.close();
        return jsonObject;
    }

    public JSONHelper put(String key, String value) {
        builder.add(key, value);
        return this;
    }

    public JSONHelper put(String key, int value) {
        builder.add(key, value);
        return this;
    }

    public JSONHelper put(String key, boolean value) {
        builder.add(key, value);
        return this;
    }

    public String toJSONString() {

        JsonObject jsonObject = builder.build();

        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = Json.createWriter(stringWriter);
        jsonWriter.writeObject(jsonObject);
        jsonWriter.close();

        return stringWriter.toString();
    }
}
