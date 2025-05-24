package uz.coder.muslimcalendar.models.model;

import androidx.annotation.NonNull;

public class Notification {
    private String name;
    private int icon;

    public Notification(String name, int icon) {
        this.name = name;
        this.icon = icon;
    }

    public Notification() {}

    public String getName() {
        return name;
    }

    public int getIcon() {
        return icon;
    }

    @NonNull
    @Override
    public String toString() {
        return "Notification{" +
                "name=" + name +
                ", icon=" + icon +
                '}';
    }
}
