package uz.coder.muslimcalendar.models.model;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Date {
    private int day;
    private int month;
    private String weekDay;
    private int hijriDay;
    private String hijriMonth;
    private String region;

    public Date() {
    }

    public Date(int day, int month, String weekDay, int hijriDay, String hijriMonth, String region) {
        this.day = day;
        this.month = month;
        this.weekDay = weekDay;
        this.hijriDay = hijriDay;
        this.hijriMonth = hijriMonth;
        this.region = region;
    }

    public String getRegion() {
        return region;
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
        return day == date.day && month == date.month && hijriDay == date.hijriDay && Objects.equals(weekDay, date.weekDay) && Objects.equals(hijriMonth, date.hijriMonth) && Objects.equals(region, date.region);
    }

    @Override
    public int hashCode() {
        return Objects.hash(day, month, weekDay, hijriDay, hijriMonth, region);
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
                ", region='" + region + '\'' +
                '}';
    }
}