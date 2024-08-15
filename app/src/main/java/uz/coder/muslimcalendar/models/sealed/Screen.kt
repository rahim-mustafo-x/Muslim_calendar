package uz.coder.muslimcalendar.models.sealed

sealed class Screen(val route:String) {
    data object Home: Screen(HOME)
    data object Tasbeh: Screen(TASBEH)
    data object Duo: Screen(DUO)
    data object DuoMeaning: Screen(DUO_MEANING)
    data object Namoz: Screen(NAMOZ)
    data object NamozMeaning: Screen(NAMOZ_MEANING)
    data object Calendar: Screen(CALENDAR)
    data object Qazo: Screen(QAZO)
    data object ChooseRegion: Screen(CHOOSE_REGION)
    data object About: Screen(ABOUT)
    data object AllahName: Screen(ALLAH_NAME)
    data object AllahNameMeaning: Screen(ALLAH_NAME_MEANING)

    companion object{
        private const val HOME = "home"
        private const val TASBEH = "tasbeh"
        private const val DUO = "duo"
        private const val DUO_MEANING = "duo_meaning"
        private const val NAMOZ = "namoz"
        private const val NAMOZ_MEANING = "namoz_meaning"
        private const val CALENDAR = "calendar"
        private const val QAZO = "qazo"
        private const val CHOOSE_REGION = "choose_region"
        private const val ABOUT = "about"
        private const val ALLAH_NAME = "allah_name"
        private const val ALLAH_NAME_MEANING = "allah_name_meaning"
    }

}