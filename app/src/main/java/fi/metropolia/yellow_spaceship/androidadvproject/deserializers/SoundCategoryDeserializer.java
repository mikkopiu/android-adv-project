package fi.metropolia.yellow_spaceship.androidadvproject.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.Type;

import fi.metropolia.yellow_spaceship.androidadvproject.models.SoundCategory;

/**
 * Deserialize JSON's "Category" property into enum SoundCategory.
 */
public class SoundCategoryDeserializer implements JsonDeserializer<SoundCategory> {
    @Override
    public SoundCategory deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {

        // Get the property value as a string, for the enum
        JsonPrimitive json = (JsonPrimitive) jsonElement;
        String categoryString = json.getAsString();

        // Map and return
        return SoundCategory.fromApi(categoryString);
    }
}
