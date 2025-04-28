package uz.coder.muslimcalendar.ui.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import uz.coder.muslimcalendar.models.model.quran.Sura

@Composable
fun SuraItem(modifier: Modifier = Modifier, sura: Sura, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = modifier.padding(16.dp)) {
            Text(
                text = sura.name,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = modifier.height(4.dp))
            Text(
                text = "${sura.englishName} • ${sura.englishNameTranslation}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Revelation: ${sura.revelationType}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
