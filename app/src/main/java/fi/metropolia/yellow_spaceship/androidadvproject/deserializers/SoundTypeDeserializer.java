package fi.metropolia.yellow_spaceship.androidadvproject.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.Type;

import fi.metropolia.yellow_spaceship.androidadvproject.models.SoundType;

/**
 * Deserialize JSON's "Sound type" property into enum SoundType.
 */
public class SoundTypeDeserializer implements JsonDeserializer<SoundType> {
    @Override
    public SoundType deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {

        // Get the property value as a string, for the enum
        JsonPrimitive json = (JsonPrimitive) jsonElement;
        String categoryString = json.getAsString();

        // Map and return
        return SoundType.fromApi(categoryString);
    }
}
