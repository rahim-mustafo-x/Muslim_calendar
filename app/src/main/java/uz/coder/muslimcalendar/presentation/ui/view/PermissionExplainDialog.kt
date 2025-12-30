package uz.coder.muslimcalendar.presentation.ui.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PermissionExplainDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            Text(
                "Davom etish",
                modifier = Modifier
                    .padding(8.dp)
                    .clickable { onConfirm() }
            )
        },
        dismissButton = {
            Text(
                "Bekor qilish",
                modifier = Modifier
                    .padding(8.dp)
                    .clickable { onDismiss() }
            )
        }
    )
}
