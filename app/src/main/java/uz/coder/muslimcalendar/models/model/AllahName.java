package uz.coder.muslimcalendar.models.model;

import androidx.annotation.NonNull;

public class AllahName {
    private String name;
    private String meaning;

    public AllahName() {
    }

    public AllahName(String name, String meaning) {
        this.name = name;
        this.meaning = meaning;
    }

    public String getName() {
        return name;
    }

    public String getMeaning() {
        return meaning;
    }

    @NonNull
    @Override
    public String toString() {
        return "AllahName{" +
                "name='" + name + '\'' +
                ", meaning='" + meaning + '\'' +
                '}';
    }
}
