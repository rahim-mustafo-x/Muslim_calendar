package uz.coder.muslimcalendar.domain.model

data class Menu(
    val img: Int = 0,
    val text: String = "",
    val menu: MenuSetting = MenuSetting.About
) {
    constructor(img: Int, menu: MenuSetting) : this(img, "", menu)
}
