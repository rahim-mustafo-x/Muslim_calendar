package uz.coder.muslimcalendar.todo

private val pref = SharedPref.getInstance(App.application)
var language: String?
    set(value) = pref.saveValue<String>(LANGUAGE, value)
    get() = pref.getString(LANGUAGE, UZB)
/***
 * Bu yerga sen kelib tilingni sharedPrefga saqlaysan keyin uni olib tizim uchun ishlatasan
 * ***/
private const val LANGUAGE = "language"
const val UZB = "uzb"
const val ENG = "eng"
const val RUS = "rus"