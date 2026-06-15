package uz.coder.muslimcalendar.presentation.screen.permission

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import uz.coder.muslimcalendar.presentation.theme.AppTheme

@Composable
fun PermissionEducationScreen(
    onGrantClick: () -> Unit,
    onSkipClick: () -> Unit
) {
    AppTheme {
        Scaffold { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Namoz vaqtlarini o'tkazib yubormang",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Biz sizga har bir namoz vaqti bo'lganda eslatma yuboramiz. Shuningdek, Hijriy yangi kun va boshqa muhim islomiy voqealar haqida xabar beramiz.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(48.dp))
                Button(
                    onClick = onGrantClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large
                ) {
                    Text(text = "Bildirishnomalarni yoqish", modifier = Modifier.padding(vertical = 8.dp))
                }
                Spacer(modifier = Modifier.height(12.dp))
                TextButton(
                    onClick = onSkipClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Keyinroq", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
