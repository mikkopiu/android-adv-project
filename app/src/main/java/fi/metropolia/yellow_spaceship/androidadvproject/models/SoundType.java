package fi.metropolia.yellow_spaceship.androidadvproject.models;

import com.google.gson.annotations.SerializedName;

/**
 * Enumeration for DAM's "Sound type" property
 */
public enum SoundType {

    @SerializedName("soundscapes")
    SOUNDSCAPE,

    @SerializedName("ambience")
    AMBIENCE,

    @SerializedName("effects")
    EFFECT,

    @SerializedName("")
    UNKNOWN;

    public static SoundType fromApi(String typeString) {
        if ("soundscapes".equals(typeString)) {
            return SOUNDSCAPE;
        } else if ("ambience".equals(typeString)) {
            return AMBIENCE;
        } else if ("effects".equals(typeString)) {
            return EFFECT;
        } else {
            return UNKNOWN;
        }
    }

    public String toString() {
        switch (this) {
            case SOUNDSCAPE:
                return "soundscapes";
            case AMBIENCE:
                return "ambience";
            case EFFECT:
                return "effects";
            case UNKNOWN:
            default:
                return "";
        }
    }
}
