package uz.coder.muslimcalendar.models.model;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Time {
    private Item item;
    private int hour;
    private int minute;
    private int resId;
    private int soundId;

    public Time() {
    }

    public Time(Item item, int hour, int minute, int resId, int soundId) {
        this.item = item;
        this.hour = hour;
        this.minute = minute;
        this.resId = resId;
        this.soundId = soundId;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public int getSoundId() {
        return soundId;
    }

    public Item getItem() {
        return item;
    }

    public int getResId() {
        return resId;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Time time = (Time) object;
        return hour == time.hour && minute == time.minute && resId == time.resId && soundId == time.soundId && Objects.equals(item, time.item);
    }

    @Override
    public int hashCode() {
        return Objects.hash(item, hour, minute, resId, soundId);
    }

    @NotNull
    @Override
    public String toString() {
        return "Time{" +
                "item=" + item +
                ", hour=" + hour +
                ", minute=" + minute +
                ", resId=" + resId +
                ", soundId=" + soundId +
                '}';
    }
}