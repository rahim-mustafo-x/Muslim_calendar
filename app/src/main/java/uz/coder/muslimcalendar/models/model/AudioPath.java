package uz.coder.muslimcalendar.models.model;

import androidx.annotation.NonNull;

public class AudioPath {
    private final String path;
    private final String sura;
    public AudioPath(String path, String sura) {
        this.path = path;
        this.sura = sura;
    }
    public String getPath() {
        return path;
    }

    public String getSura() {
        return sura;
    }

    @NonNull
    @Override
    public String toString() {
        return "AudioPath{" +
                "path='" + path + '\'' +
                ", sura='" + sura + '\'' +
                '}';
    }
}
