package uz.coder.muslimcalendar.domain.model;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Duo {
    private final String name;
    private final String duo;

    public String getName() {
        return name;
    }

    public String getDuo() {
        return duo;
    }

    public Duo(String name, String duo) {
        this.name = name;
        this.duo = duo;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Duo duo1 = (Duo) object;
        return Objects.equals(name, duo1.name) && Objects.equals(duo, duo1.duo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, duo);
    }

    @Override
    @NotNull
    public String toString() {
        return "Duo{" +
                "name='" + name + '\'' +
                ", duo='" + duo + '\'' +
                '}';
    }
}