package uz.coder.muslimcalendar.models.model;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Objects;

public class MuslimCalendar {
    private int day;
    private int hijriDay;
    private String hijriMonth;
    private int month;
    private String weekday;
    private String asr;
    private String hufton;
    private String peshin;
    private String shomIftor;
    private String tongSaharlik;
    private String sunRise;

    public MuslimCalendar(int day, int hijriDay, String hijriMonth, int month, String weekday, String asr, String hufton, String peshin, String shomIftor, String tongSaharlik, String sunRise) {
        this.day = day;
        this.hijriDay = hijriDay;
        this.hijriMonth = hijriMonth;
        this.month = month;
        this.weekday = weekday;
        this.asr = asr;
        this.hufton = hufton;
        this.peshin = peshin;
        this.shomIftor = shomIftor;
        this.tongSaharlik = tongSaharlik;
        this.sunRise = sunRise;
    }

    public MuslimCalendar() {
    }

    public int getDay() {
        return day;
    }

    public int getHijriDay() {
        return hijriDay;
    }

    public String getHijriMonth() {
        return hijriMonth;
    }

    public int getMonth() {
        return month;
    }

    public String getWeekday() {
        return weekday;
    }

    public String getAsr() {
        return asr;
    }

    public String getHufton() {
        return hufton;
    }

    public String getPeshin() {
        return peshin;
    }

    public String getShomIftor() {
        return shomIftor;
    }

    public String getTongSaharlik() {
        return tongSaharlik;
    }

    public String getSunRise() {
        return sunRise;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MuslimCalendar that = (MuslimCalendar) o;
        return day == that.day && hijriDay == that.hijriDay && month == that.month && Objects.equals(hijriMonth, that.hijriMonth) && Objects.equals(weekday, that.weekday) && Objects.equals(asr, that.asr) && Objects.equals(hufton, that.hufton) && Objects.equals(peshin, that.peshin) && Objects.equals(shomIftor, that.shomIftor) && Objects.equals(tongSaharlik, that.tongSaharlik) && Objects.equals(sunRise, that.sunRise);
    }

    @Override
    public int hashCode() {
        return Objects.hash(day, hijriDay, hijriMonth, month, weekday, asr, hufton, peshin, shomIftor, tongSaharlik, sunRise);
    }
    public List<String> getItem(){
        return List.of(tongSaharlik, sunRise, peshin, asr, shomIftor, hufton);
    }

    @NonNull
    @Override
    public String toString() {
        return "MuslimCalendar{" +
                "day=" + day +
                ", hijriDay=" + hijriDay +
                ", hijriMonth='" + hijriMonth + '\'' +
                ", month=" + month +
                ", weekday='" + weekday + '\'' +
                ", asr='" + asr + '\'' +
                ", hufton='" + hufton + '\'' +
                ", peshin='" + peshin + '\'' +
                ", shomIftor='" + shomIftor + '\'' +
                ", tongSaharlik='" + tongSaharlik + '\'' +
                ", sunRise='" + sunRise + '\'' +
                '}';
    }
}