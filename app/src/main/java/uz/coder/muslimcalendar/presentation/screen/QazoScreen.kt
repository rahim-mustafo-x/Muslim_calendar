package uz.coder.muslimcalendar.presentation.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import uz.coder.muslimcalendar.R
import uz.coder.muslimcalendar.domain.model.Menu
import uz.coder.muslimcalendar.domain.model.MenuSetting
import uz.coder.muslimcalendar.presentation.ui.view.CalendarTopBar
import uz.coder.muslimcalendar.presentation.ui.view.QazoCount
import uz.coder.muslimcalendar.presentation.viewModel.QazoViewModel

@Composable
fun QazoScreen(modifier: Modifier = Modifier, controller: NavHostController) {
    val viewModel: QazoViewModel = hiltViewModel()
    var showDialog by remember { mutableStateOf(false) }
    var selectedId by remember { mutableIntStateOf(-1) }

    Scaffold(topBar = {
        CalendarTopBar(
            list = listOf(Menu(R.drawable.ic_settings, "Sozlamalar", MenuSetting.QazoSetting))
        ) {
            if (it == MenuSetting.QazoSetting) showDialog = true
        }
    }) { padding ->
        QazoContent(
            modifier = modifier,
            paddingValues = padding,
            viewModel = viewModel,
            onShowDialog = { id ->
                selectedId = id
                showDialog = true
            }
        )
    }

    if (showDialog) {
        QazoDialog(
            id = selectedId,
            viewModel = viewModel,
            onDismiss = {
                showDialog = false
                selectedId = -1
            }
        )
    }

    BackHandler {
        controller.popBackStack()
    }
}

@Composable
fun QazoContent(
    modifier: Modifier,
    paddingValues: PaddingValues,
    viewModel: QazoViewModel,
    onShowDialog: (Int) -> Unit
) {
    val bomdod by viewModel.bomdod.collectAsStateWithLifecycle()
    val peshin by viewModel.peshin.collectAsStateWithLifecycle()
    val asr by viewModel.asr.collectAsStateWithLifecycle()
    val shom by viewModel.shom.collectAsStateWithLifecycle()
    val xufton by viewModel.xufton.collectAsStateWithLifecycle()
    val vitr by viewModel.vitr.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        QazoCount(text = stringResource(R.string.bomdod), count = bomdod, minus = { viewModel.setBomdod((bomdod - 1).coerceAtLeast(0)) }, plus = { viewModel.setBomdod(bomdod + 1) }) { onShowDialog(1) }
        QazoCount(text = stringResource(R.string.peshin), count = peshin, minus = { viewModel.setPeshin((peshin - 1).coerceAtLeast(0)) }, plus = { viewModel.setPeshin(peshin + 1) }) { onShowDialog(2) }
        QazoCount(text = stringResource(R.string.asr), count = asr, minus = { viewModel.setAsr((asr - 1).coerceAtLeast(0)) }, plus = { viewModel.setAsr(asr + 1) }) { onShowDialog(3) }
        QazoCount(text = stringResource(R.string.shom), count = shom, minus = { viewModel.setShom((shom - 1).coerceAtLeast(0)) }, plus = { viewModel.setShom(shom + 1) }) { onShowDialog(4) }
        QazoCount(text = stringResource(R.string.xufton), count = xufton, minus = { viewModel.setXufton((xufton - 1).coerceAtLeast(0)) }, plus = { viewModel.setXufton(xufton + 1) }) { onShowDialog(5) }
        QazoCount(text = stringResource(R.string.vitr), count = vitr, minus = { viewModel.setVitr((vitr - 1).coerceAtLeast(0)) }, plus = { viewModel.setVitr(vitr + 1) }) { onShowDialog(6) }
    }
}

@Composable
fun QazoDialog(id: Int, viewModel: QazoViewModel, onDismiss: () -> Unit) {
    var value by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        OutlinedCard(
            modifier = Modifier.padding(16.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Qazo sonini kiriting",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = value,
                    onValueChange = { if (it.all { char -> char.isDigit() }) value = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Miqdor") }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
                    Button(onClick = {
                        val num = value.toIntOrNull() ?: 0
                        when (id) {
                            1 -> viewModel.setBomdod(num)
                            2 -> viewModel.setPeshin(num)
                            3 -> viewModel.setAsr(num)
                            4 -> viewModel.setShom(num)
                            5 -> viewModel.setXufton(num)
                            6 -> viewModel.setVitr(num)
                            else -> {
                                viewModel.setBomdod(num)
                                viewModel.setPeshin(num)
                                viewModel.setAsr(num)
                                viewModel.setShom(num)
                                viewModel.setXufton(num)
                                viewModel.setVitr(num)
                            }
                        }
                        onDismiss()
                    }) { Text(stringResource(R.string.save)) }
                }
            }
        }
    }
}
