package uz.coder.muslimcalendar.models.model;

import androidx.annotation.NonNull;

public class SpinnerModel {
    private String spinnerValue;
    private String sign;

    public SpinnerModel(String spinnerValue, String sign) {
        this.spinnerValue = spinnerValue;
        this.sign = sign;
    }

    public String getSpinnerValue() {
        return spinnerValue;
    }

    public String getSign() {
        return sign;
    }

    @NonNull
    @Override
    public String toString() {
        return "SpinnerModel{" +
                "spinnerValue='" + spinnerValue + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
