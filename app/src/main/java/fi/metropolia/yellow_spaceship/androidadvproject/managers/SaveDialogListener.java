package fi.metropolia.yellow_spaceship.androidadvproject.managers;

import android.support.annotation.Nullable;

import fi.metropolia.yellow_spaceship.androidadvproject.models.SoundCategory;

public interface SaveDialogListener {
    void onDialogSave(String title, @Nullable SoundCategory category);

    void onDialogCancel();
}
