package uz.coder.muslimcalendar.domain.model;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Namoz {
    private final String name;
    private final String namoz;

    public Namoz(String name, String namoz) {
        this.name = name;
        this.namoz = namoz;
    }

    public String getName() {
        return name;
    }

    public String getNamoz() {
        return namoz;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Namoz namoz1 = (Namoz) object;
        return Objects.equals(name, namoz1.name) && Objects.equals(namoz, namoz1.namoz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, namoz);
    }

    @Override
    @NotNull
    public String toString() {
        return "Namoz{" +
                "name='" + name + '\'' +
                ", namoz='" + namoz + '\'' +
                '}';
    }
}