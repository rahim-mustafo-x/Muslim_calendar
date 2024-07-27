package uz.coder.muslimcalendar.models.sealed

sealed class Screen(val route:String) {
    data object Home:Screen(HOME)
    data object TimeSetting:Screen(TIME_SETTING)
    data object OneTimeSetting:Screen(ONE_TIME_SETTING)
    data object Qazo:Screen(QAZO)
    data object ChooseRegion:Screen(CHOOSE_REGION)
    data object About:Screen(ABOUT)
    data object AllahName:Screen(ALLAH_NAME)
    data object AllahNameMeaning:Screen(ALLAH_NAME_MEANING)

    companion object{
        private const val HOME = "home"
        private const val TIME_SETTING = "time_setting"
        private const val ONE_TIME_SETTING = "one_time_setting"
        private const val QAZO = "qazo"
        private const val CHOOSE_REGION = "choose_region"
        private const val ABOUT = "about"
        private const val ALLAH_NAME = "allah_name"
        private const val ALLAH_NAME_MEANING = "allah_name_meaning"
    }

}