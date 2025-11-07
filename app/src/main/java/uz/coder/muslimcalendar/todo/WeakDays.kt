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
fun String.toWeakDays(): String{
    return uzbekDays.getValue(this)
}