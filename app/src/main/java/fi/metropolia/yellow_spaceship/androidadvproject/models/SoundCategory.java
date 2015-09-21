package fi.metropolia.yellow_spaceship.androidadvproject.models;

/**
 * Enumeration for DAM's "Category" property.
 */
public enum SoundCategory {
    NATURE,
    HUMAN,
    MACHINE,
    STORY,
    UNKNOWN,;

    public static SoundCategory fromApi(String category) {
        if ("nature".equals(category)) {
            return NATURE;
        } else if ("human".equals(category)) {
            return HUMAN;
        } else if ("machine".equals(category)) {
            return MACHINE;
        } else if ("story".equals(category)) {
            return STORY;
        } else {
            return UNKNOWN;
        }
    }

    public String getDescription() {
        switch (this) {
            case NATURE:
                return "Nature sound";
            case HUMAN:
                return "Human sound";
            case MACHINE:
                return "Machine sound";
            case STORY:
                return "Story sound";
            case UNKNOWN:
            default:
                return "Unknown sound";
        }
    }

    public String toString() {
        switch (this) {
            case NATURE:
                return "nature";
            case HUMAN:
                return "human";
            case MACHINE:
                return "machine";
            case STORY:
                return "story";
            case UNKNOWN:
            default:
                return null;
        }
    }

    public String menuCaption() {
        switch (this) {
            case NATURE:
                return "Nature";
            case HUMAN:
                return "Human";
            case MACHINE:
                return "Machine";
            case STORY:
                return "Story";
            case UNKNOWN:
            default:
                return "Unknown";
        }
    }
}
