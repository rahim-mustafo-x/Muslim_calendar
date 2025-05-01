package uz.coder.muslimcalendar.todo

val uzbekDays = mapOf<String,String>(
    "Monday" to  "Dushanba",
    "Tuesday" to  "Seshanba",
    "Wednesday" to  "Chorshana",
    "Thursday" to  "Payshanba",
    "Friday" to  "Juma",
    "Saturday" to  "Shanba",
    "Sunday" to  "Yakshanba"

)
val russianDays = mapOf<String,String>(
    "Monday" to "Понедельник",
    "Tuesday" to "Вторник",
    "Wednesday" to "Среда",
    "Thursday" to "Четверг",
    "Friday" to "Пятница",
    "Saturday" to "Суббота",
    "Sunday" to "Воскресенье"

)
fun String.toWeakDays(): String{
    return when(appLanguage){
        UZB -> uzbekDays.getValue(this)
        RUS -> russianDays.getValue(this)
        ENG -> this
        else -> "Error"
    }
}