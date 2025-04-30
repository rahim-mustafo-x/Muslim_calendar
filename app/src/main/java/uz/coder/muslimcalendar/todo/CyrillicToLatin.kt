package uz.coder.muslimcalendar.todo

import kotlin.concurrent.thread


val cyrillicToLatinMap = mapOf<Char, String>(
    'А' to "A",
    'Б' to "B",
    'В' to "V",
    'Г' to "G",
    'Д' to "D",
    'Е' to "E",
    'Ё' to "Yo",
    'Ж' to "J",
    'З' to "Z",
    'И' to "I",
    'Й' to "Y",
    'К' to "K",
    'Л' to "L",
    'М' to "M",
    'Н' to "N",
    'О' to "O",
    'П' to "P",
    'Р' to "R",
    'С' to "S",
    'Т' to "T",
    'У' to "U",
    'Ф' to "F",
    'Х' to "Kh",
    'Ц' to "Ts",
    'Ч' to "Ch",
    'Ш' to "Sh",
    'Щ' to "Shch",
    'Ъ' to "",
    'Ы' to "Y",
    'Ь' to "",
    'Э' to "E",
    'Ю' to "Yu",
    'Я' to "Ya",

    // Lowercase mappings
    'а' to "a",
    'б' to "b",
    'в' to "v",
    'г' to "g",
    'д' to "d",
    'е' to "e",
    'ё' to "yo",
    'ж' to "j",
    'з' to "z",
    'и' to "i",
    'й' to "y",
    'к' to "k",
    'л' to "l",
    'м' to "m",
    'н' to "n",
    'о' to "o",
    'п' to "p",
    'р' to "r",
    'с' to "s",
    'т' to "t",
    'у' to "u",
    'ф' to "f",
    'х' to "kh",
    'ц' to "ts",
    'ч' to "ch",
    'ш' to "sh",
    'щ' to "shch",
    'ъ' to "",
    'ы' to "y",
    'ь' to "",
    'э' to "e",
    'ю' to "yu",
    'я' to "ya"
)

fun String.cyrillicToLatin(): String {
    val builder = StringBuilder()
    thread(start = true) {
        this.forEach {
            builder.append(cyrillicToLatinMap[it] ?: it)
        }
    }.join()
    return builder.toString()
}


