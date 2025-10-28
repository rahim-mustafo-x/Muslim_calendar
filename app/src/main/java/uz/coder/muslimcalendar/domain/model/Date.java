package uz.coder.muslimcalendar.domain.model;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Date {
    private int day;
    private int month;
    private String weekDay;
    private int hijriDay;
    private String hijriMonth;

    public Date() {
    }

    public Date(int day, int month, String weekDay, int hijriDay, String hijriMonth) {
        this.day = day;
        this.month = month;
        this.weekDay = weekDay;
        this.hijriDay = hijriDay;
        this.hijriMonth = hijriMonth;
    }


    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public String getWeekDay() {
        return weekDay;
    }

    public int getHijriDay() {
        return hijriDay;
    }

    public String getHijriMonth() {
        return hijriMonth;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Date date = (Date) object;
        return day == date.day && month == date.month && hijriDay == date.hijriDay && Objects.equals(weekDay, date.weekDay) && Objects.equals(hijriMonth, date.hijriMonth);
    }

    @Override
    public int hashCode() {
        return Objects.hash(day, month, weekDay, hijriDay, hijriMonth);
    }

    @Override
    @NotNull
    public String toString() {
        return "Date{" +
                "day=" + day +
                ", month=" + month +
                ", weekDay='" + weekDay + '\'' +
                ", hijriDay=" + hijriDay +
                ", hijriMonth='" + hijriMonth + '\'' +
                '}';
    }
}