package uz.coder.muslimcalendar.models.model;

import androidx.annotation.NonNull;

public class Item {
    private String name;
    private String time;

    public Item(String name, String time) {
        this.name = name;
        this.time = time;
    }

    public Item() {}

    @NonNull
    public String getName() {
        return name;
    }
    @NonNull
    public String getTime() {
        return time;
    }

    @NonNull
    @Override
    public String toString() {
        return "Item{" +
                "name='" + name + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
