package uz.coder.muslimcalendar.todo

import com.michaeltroger.latintocyrillic.Alphabet
import com.michaeltroger.latintocyrillic.LatinCyrillicFactory


suspend fun String.cyrillicToLatin(): String {
    val latinCyrillic = LatinCyrillicFactory.create(Alphabet.Serbian)
    return latinCyrillic.cyrillicToLatin(this)
}
