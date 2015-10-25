package fi.metropolia.yellow_spaceship.androidadvproject.models;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * SoundsScapeProject represents the contents of the Create-view.
 * It contains a list of sounds to use and its name.
 */
public class SoundScapeProject implements Parcelable {
    private ArrayList<ProjectSound> sounds;
    private String name;

    /**
     * Constructor for a completely new project
     */
    public SoundScapeProject() {
        this(null, null);
    }

    /**
     * Constructor for a project with a name but without pre-existing sounds
     * @param name Name of the project
     */
    public SoundScapeProject(String name) {
        this(name, null);
    }

    public SoundScapeProject(Parcel in) {
        this(in.readString());

        in.readTypedList(this.sounds, ProjectSound.CREATOR);
    }

    /**
     * Constructor
     * @param name Name of the project
     * @param sounds A list of sounds in the project
     */
    public SoundScapeProject(String name, ArrayList<ProjectSound> sounds) {
        this.name = name;
        this.sounds = sounds;

        if (this.sounds == null) {
            this.sounds = new ArrayList<>();
        }
    }

    /**
     * Get project's name
     * @return Name of the project
     */
    public String getName() {
        return name;
    }

    /**
     * Set project's name
     * @param name New name
     */
    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<ProjectSound> getSounds() {
        return this.sounds;
    }

    /**
     * Get a sound by its index
     * @param ind Index to find
     * @return Found sound
     * @throws IndexOutOfBoundsException In case of trying to get a non-existing index
     */
    public ProjectSound getSound(int ind) throws IndexOutOfBoundsException {
        return this.sounds.get(ind);
    }

    /**
     * Add a sound to the project
     * @param sound Sound to add
     */
    public void addSound(ProjectSound sound) {
        this.sounds.add(sound);
    }

    /**
     * Add multiple sounds at the same time
     * @param sounds A list of sounds to add
     */
    public void addSounds(ArrayList<ProjectSound> sounds) {
        this.sounds.addAll(sounds);
    }

    /**
     * Remove a specific sounds by index (should match layout position in create-view)
     * @param ind Index to remove
     * @throws IndexOutOfBoundsException In case of trying to remove a non-existing sound
     */
    public void removeSound(int ind) throws IndexOutOfBoundsException {
        this.sounds.remove(ind);
    }

    /**
     * Remove all sounds
     */
    public void clearSounds() {
        this.sounds.clear();
    }

    /**
     * Replace all sounds with the specified ones
     * @param sounds New list of sounds to use
     */
    public void replaceSounds(ArrayList<ProjectSound> sounds) {
        this.clearSounds();
        this.addSounds(sounds);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.getName());
        dest.writeTypedList(this.getSounds());
    }

    public static final Parcelable.Creator<SoundScapeProject> CREATOR
            = new Parcelable.Creator<SoundScapeProject>() {

        public SoundScapeProject createFromParcel(Parcel in) {
            return new SoundScapeProject(in);
        }

        public SoundScapeProject[] newArray(int size) {
            return new SoundScapeProject[size];
        }
    };
}
