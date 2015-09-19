package fi.metropolia.yellow_spaceship.androidadvproject.api;


import retrofit.mime.TypedByteArray;

/**
 * Used to add the "filename" attribute to our multi-part upload
 */
public class TypedByteArrayWithFilename extends TypedByteArray {

    private final String fileName;

    public TypedByteArrayWithFilename(String mimeType, byte[] bytes, String fileName) {
        super(mimeType, bytes);
        this.fileName = fileName;
    }

    @Override public String fileName() {
        return fileName;
    }
}
