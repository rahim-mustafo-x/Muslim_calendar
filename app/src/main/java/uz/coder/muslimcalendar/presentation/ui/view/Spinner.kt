package uz.coder.muslimcalendar.presentation.ui.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import uz.coder.muslimcalendar.domain.model.SpinnerModel

@Composable
fun Spinner(
    modifier: Modifier = Modifier,
    value: String,
    list: List<SpinnerModel>,
    onSelected: (SpinnerModel) -> Unit
) {
    var showSpinner by remember { mutableStateOf(false) }
    
    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = {},
        readOnly = true,
        trailingIcon = {
            IconButton(onClick = { showSpinner = !showSpinner }) {
                Icon(
                    if (showSpinner) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            DropdownMenu(
                expanded = showSpinner,
                onDismissRequest = { showSpinner = false }
            ) {
                list.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item.spinnerValue) },
                        onClick = {
                            showSpinner = false
                            onSelected(item)
                        }
                    )
                }
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        )
    )
}