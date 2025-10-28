package uz.coder.muslimcalendar.presentation.ui.view

import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
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
import uz.coder.muslimcalendar.presentation.ui.theme.Light_Blue

@Composable
fun Spinner(modifier: Modifier = Modifier, value: String, list: List<SpinnerModel>, onSelected:(SpinnerModel)-> Unit) {
    var showSpinner by remember { mutableStateOf(false) }
    OutlinedTextField(modifier = modifier, value = value, onValueChange = {}, readOnly = true, trailingIcon = {
        IconButton(onClick = {
            showSpinner = !showSpinner
        }) {
            Icon(if (showSpinner) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, null, tint = Light_Blue)
        }
        DropdownMenu(expanded = showSpinner, onDismissRequest = { showSpinner = false }) {
            list.forEach {item ->
                DropdownMenuItem({ Text(item.spinnerValue) }, onClick = {
                        showSpinner = false
                        onSelected(item)
                })
            }
        }
    }, colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Light_Blue, unfocusedTextColor = Light_Blue))
}