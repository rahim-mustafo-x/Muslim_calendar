package uz.coder.muslimcalendar.ui.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.coder.muslimcalendar.models.model.quran.Sura
import uz.coder.muslimcalendar.R

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
                text = String.format(stringResource(R.string.revelation), sura.revelationType),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
