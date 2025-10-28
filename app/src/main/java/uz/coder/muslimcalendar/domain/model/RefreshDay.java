package uz.coder.muslimcalendar.domain.model;

import androidx.annotation.NonNull;

import java.util.Objects;

public class RefreshDay {
    private int day;
    private int month;

    public RefreshDay(int day, int month) {
        this.day = day;
        this.month = month;
    }

    public RefreshDay() {
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        RefreshDay that = (RefreshDay) object;
        return day == that.day && month == that.month;
    }

    @Override
    public int hashCode() {
        return Objects.hash(day, month);
    }

    @NonNull
    @Override
    public String toString() {
        return "RefreshDay{" +
                "day=" + day +
                ", month=" + month +
                '}';
    }
}
