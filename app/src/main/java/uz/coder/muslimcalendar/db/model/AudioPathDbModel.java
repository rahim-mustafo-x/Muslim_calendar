package uz.coder.muslimcalendar.db.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import org.jetbrains.annotations.NotNull;

@Entity(
        tableName = "audioPath",
        indices = {@Index(value = {"sura"}, unique = true)}  // bu yerda sura unique qilindi
)
public class AudioPathDbModel {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @NotNull
    private String sura;
    @NotNull
    private String audioPath;

    public AudioPathDbModel(@NonNull String sura, @NonNull String audioPath) {
        this.sura = sura;
        this.audioPath = audioPath;
    }

    public AudioPathDbModel() {
        sura = "";
        audioPath = "";
    }

    public int getId() {
        return id;
    }

    @NotNull
    public String getSura() {
        return sura;
    }

    @NotNull
    public String getAudioPath() {
        return audioPath;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSura(@NotNull String sura) {
        this.sura = sura;
    }

    public void setAudioPath(@NotNull String audioPath) {
        this.audioPath = audioPath;
    }

    @NonNull
    @Override
    public String toString() {
        return "AudioPathDbModel{" +
                "id=" + id +
                ", sura='" + sura + '\'' +
                ", audioPath='" + audioPath + '\'' +
                '}';
    }
}
