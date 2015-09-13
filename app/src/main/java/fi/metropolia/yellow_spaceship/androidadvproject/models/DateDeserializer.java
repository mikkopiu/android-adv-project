package fi.metropolia.yellow_spaceship.androidadvproject.models;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Deserialize the "Creation date" property from DAM into a Date.
 */
public class DateDeserializer implements JsonDeserializer<Date> {

                                                   // "2015-09-01 13:00:05"
    final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

    @Override
    public Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonPrimitive json = (JsonPrimitive) jsonElement;
        String creationDateString = json.getAsString();

        Date parsedDate = null;

        // SimpleDateFormat is not threadsafe, so it needs to be synchronized
        synchronized (sdf) {
            try {
                parsedDate = sdf.parse(creationDateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return parsedDate;
    }
}
