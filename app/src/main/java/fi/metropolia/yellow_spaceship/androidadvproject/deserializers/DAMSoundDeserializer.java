package fi.metropolia.yellow_spaceship.androidadvproject.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import fi.metropolia.yellow_spaceship.androidadvproject.models.DAMSound;
import fi.metropolia.yellow_spaceship.androidadvproject.models.SoundCategory;
import fi.metropolia.yellow_spaceship.androidadvproject.models.SoundType;

/**
 * Deserialize JSON's "Category" property into enum SoundCategory.
 */
public class DAMSoundDeserializer implements JsonDeserializer<DAMSound> {
    @Override
    public DAMSound deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        DAMSound sound = new DAMSound();
        JsonObject json = (JsonObject) jsonElement;

        Set<Map.Entry<String, JsonElement>> entrySet = json.entrySet();
        for(Map.Entry<String,JsonElement> entry : entrySet){
            JsonElement val = entry.getValue();
            switch (entry.getKey()) {
                case "Title":
                    sound.setTitle(val.getAsString());
                    break;
                case "Description":
                    sound.setDescription(val.getAsString());
                    break;
                case "Original filename":
                    sound.setOriginalFilename(val.getAsString());
                    break;
                case "Category":
                    sound.setCategory((new SoundCategoryDeserializer())
                                    .deserialize(val.getAsJsonPrimitive(),
                                            SoundCategory.class, context
                                    )
                    );
                    break;
                case "Sound Type":
                    sound.setSoundType((new SoundTypeDeserializer())
                                    .deserialize(val.getAsJsonPrimitive(),
                                            SoundType.class, context
                                    )
                    );
                    break;
                case "Length (sec)":
                    if (!"".equals(val.getAsString())) {
                        sound.setLengthSec(val.getAsInt());
                    }
                    break;
                case "Creation date":
                    sound.setCreationDate((new DateDeserializer())
                                    .deserialize(val.getAsJsonPrimitive(),
                                            Date.class, context
                                    )
                    );
                    break;
                case "File extension":
                    sound.setFileExtension(val.getAsString());
                    break;
                case "File size(KB)":
                    if (!"".equals(val.getAsString())) {
                        sound.setFileSizeKB(val.getAsFloat());
                    }
                    break;
                case "Created by":
                    sound.setCreatedBy(val.getAsString());
                    break;
                case "Collection name":
                    sound.setCollectionName(val.getAsString());
                    break;
                case "Collection ID":
                    if (!"".equals(val.getAsString())) {
                        sound.setCollectionID(val.getAsInt());
                    }
                    break;
                case "Download link":
                    sound.setDownloadLink(val.getAsString());
                    break;
                default:
                    sound.setAdditionalProperty(entry.getKey(), val);
            }
        }

        return sound;
    }
}
