package uz.coder.muslimcalendar.models.model;

import androidx.compose.ui.graphics.Color;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Calendar {
    private String text;
    private Color color;
    private Color backgroundColor;

    public Calendar(String text, Color color, Color backgroundColor) {
        this.text = text;
        this.color = color;
        this.backgroundColor = backgroundColor;
    }

    public Calendar() {
    }

    public String getText() {
        return text;
    }

    public Color getColor() {
        return color;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Calendar calendar = (Calendar) object;
        return Objects.equals(text, calendar.text) && Objects.equals(color, calendar.color) && Objects.equals(backgroundColor, calendar.backgroundColor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, color, backgroundColor);
    }

    @Override
    @NotNull
    public String toString() {
        return "Calendar{" +
                "text='" + text + '\'' +
                ", color=" + color +
                ", backgroundColor=" + backgroundColor +
                '}';
    }
}