package fi.metropolia.yellow_spaceship.androidadvproject.models;

/**
 * Enumeration for DAM's "Sound type" property
 */
public enum SoundType {
    SOUNDSCAPE,
    AMBIENCE,
    EFFECT,
    UNKNOWN,;

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

    public String getDescription() {
        switch (this) {
            case SOUNDSCAPE:
                return "Soundscape";
            case AMBIENCE:
                return "Ambient sound";
            case EFFECT:
                return "Effect sound";
            case UNKNOWN:
            default:
                return "Unknown sound type";
        }
    }
}
