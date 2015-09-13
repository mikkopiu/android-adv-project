package fi.metropolia.yellow_spaceship.androidadvproject.models;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Deserialize JSON's "Sound type" property into enum SoundType.
 */
public class IntegerDeserializer implements JsonDeserializer<Integer> {
    @Override
    public Integer deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        return jsonElement.getAsInt();
    }
}
