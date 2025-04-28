package uz.coder.muslimcalendar.ui.view

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Card
import androidx.compose.ui.unit.sp
import uz.coder.muslimcalendar.models.model.quran.SurahList
import uz.coder.muslimcalendar.todo.toArabicNumbers

@Composable
fun AyahArabicSection(ayahList: List<SurahList>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        val finalText = ayahList.joinToString("\n\n") {
            "${it.arabicText} ﴿${it.aya}﴾".toArabicNumbers()
        }

        Text(
            text = finalText,
            fontSize = 24.sp,
            textAlign = TextAlign.End,
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
        )
    }
}