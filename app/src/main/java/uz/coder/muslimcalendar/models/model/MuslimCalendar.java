package uz.coder.muslimcalendar.models.model;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MuslimCalendar {
    private int day;
    private int hijriDay;
    private String hijriMonth;
    private int month;
    private String region;
    private String weekday;
    private String asr;
    private String hufton;
    private String peshin;
    private String quyosh;
    private String shomIftor;
    private String tongSaharlik;

    public MuslimCalendar(int day, int hijriDay, String hijriMonth, int month, String region, String weekday, String asr, String hufton, String peshin, String quyosh, String shomIftor, String tongSaharlik) {
        this.day = day;
        this.hijriDay = hijriDay;
        this.hijriMonth = hijriMonth;
        this.month = month;
        this.region = region;
        this.weekday = weekday;
        this.asr = asr;
        this.hufton = hufton;
        this.peshin = peshin;
        this.quyosh = quyosh;
        this.shomIftor = shomIftor;
        this.tongSaharlik = tongSaharlik;
    }

    public MuslimCalendar() {
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHijriDay() {
        return hijriDay;
    }

    @NotNull
    public String getHijriMonth() {
        return hijriMonth;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    @NotNull
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }


    @NotNull
    public String getWeekday() {
        return weekday;
    }

    @NotNull
    public String getAsr() {
        return asr;
    }

    public void setAsr(String asr) {
        this.asr = asr;
    }

    @NotNull
    public String getHufton() {
        return hufton;
    }


    @NotNull
    public String getPeshin() {
        return peshin;
    }

    public void setPeshin(String peshin) {
        this.peshin = peshin;
    }

    @NotNull
    public String getQuyosh() {
        return quyosh;
    }

    @NotNull
    public String getShomIftor() {
        return shomIftor;
    }

    @NotNull
    public String getTongSaharlik() {
        return tongSaharlik;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        MuslimCalendar that = (MuslimCalendar) object;
        return day == that.day && hijriDay == that.hijriDay && month == that.month && Objects.equals(hijriMonth, that.hijriMonth) && Objects.equals(region, that.region) && Objects.equals(weekday, that.weekday) && Objects.equals(asr, that.asr) && Objects.equals(hufton, that.hufton) && Objects.equals(peshin, that.peshin) && Objects.equals(quyosh, that.quyosh) && Objects.equals(shomIftor, that.shomIftor) && Objects.equals(tongSaharlik, that.tongSaharlik);
    }

    @Override
    public int hashCode() {
        return Objects.hash(day, hijriDay, hijriMonth, month, region, weekday, asr, hufton, peshin, quyosh, shomIftor, tongSaharlik);
    }

    @NotNull
    @Override
    public String toString() {
        return "MuslimCalendar{" +
                "day=" + day +
                ", hijriDay=" + hijriDay +
                ", hijriMonth='" + hijriMonth + '\'' +
                ", month=" + month +
                ", region='" + region + '\'' +
                ", weekday='" + weekday + '\'' +
                ", asr='" + asr + '\'' +
                ", hufton='" + hufton + '\'' +
                ", peshin='" + peshin + '\'' +
                ", quyosh='" + quyosh + '\'' +
                ", shomIftor='" + shomIftor + '\'' +
                ", tongSaharlik='" + tongSaharlik + '\'' +
                '}';
    }
}