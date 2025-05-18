package uz.coder.muslimcalendar.models.model;

import androidx.annotation.NonNull;

public class Menu {
    private int img;
    private String text;
    private MenuSetting menu;

    public Menu(int img, String text, MenuSetting menu) {
        this.img = img;
        this.text = text;
        this.menu = menu;
    }

    public Menu(int img, MenuSetting menu) {
        this.img = img;
        this.menu = menu;
    }

    public Menu() {
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public MenuSetting getMenu() {
        return menu;
    }

    public void setMenu(MenuSetting menu) {
        this.menu = menu;
    }

    @NonNull
    @Override
    public String toString() {
        return "Menu{" +
                "img=" + img +
                ", text='" + text + '\'' +
                ", menu=" + menu +
                '}';
    }
}
