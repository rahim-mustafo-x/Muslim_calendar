package uz.coder.muslimcalendar.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import uz.coder.muslimcalendar.R
import uz.coder.muslimcalendar.models.model.Menu
import uz.coder.muslimcalendar.models.model.MenuScreen
import uz.coder.muslimcalendar.todo.ASR
import uz.coder.muslimcalendar.todo.BOMDOD
import uz.coder.muslimcalendar.todo.PESHIN
import uz.coder.muslimcalendar.todo.SHOM
import uz.coder.muslimcalendar.todo.VITR
import uz.coder.muslimcalendar.todo.XUFTON
import uz.coder.muslimcalendar.ui.theme.Dark_Green
import uz.coder.muslimcalendar.ui.view.CalendarTopBar
import uz.coder.muslimcalendar.ui.view.QazoCount
import uz.coder.muslimcalendar.viewModel.CalendarViewModel

@Composable
fun QazoScreen(modifier: Modifier = Modifier, controller: NavHostController) {
    val viewModel = viewModel<CalendarViewModel>()
    viewModel.fromPreferencesQazo()
    Scaffold(topBar = { CalendarTopBar(
        list = listOf(
            Menu(
                R.drawable.setting,
                MenuScreen.QazoSetting
            )
        )
    ) {
        when(it){
            MenuScreen.QazoSetting->{
                showDialog = true
            }
            else->{}
        }
    } }) {
        Qazo(modifier = modifier, controller = controller, paddingValues = it, viewModel = viewModel)
    }
}
var bomdod by
    mutableIntStateOf(0)
var peshin by
    mutableIntStateOf(0)
var asr by
    mutableIntStateOf(0)
var shom by
    mutableIntStateOf(0)
var xufton by
    mutableIntStateOf(0)
var vitr by
    mutableIntStateOf(0)
var id by mutableIntStateOf(-1)
@Composable
fun Qazo(modifier: Modifier = Modifier, controller: NavHostController, paddingValues: PaddingValues, viewModel: CalendarViewModel) {
    LaunchedEffect(true) {
        bomdod = viewModel.bomdod.value
        peshin = viewModel.peshin.value
        asr = viewModel.asr.value
        shom = viewModel.shom.value
        xufton = viewModel.xufton.value
        vitr = viewModel.vitr.value
    }
    Column(modifier = modifier
        .fillMaxSize()
        .padding(paddingValues), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        QazoCount(text = stringResource(R.string.bomdod), count = bomdod, minus = { viewModel.setBomdod(if (bomdod<0){ 0 } else --bomdod) }, plus = { ++bomdod }) {
            showDialog = true
            id = 1
        }
        QazoCount(text = stringResource(R.string.peshin), count = peshin, minus = { viewModel.setPeshin(if (peshin<0){ 0 } else --peshin) }, plus = { ++peshin }) {
            showDialog = true
            id = 2
        }
        QazoCount(text = stringResource(R.string.asr), count = asr, minus = { viewModel.setAsr(if (asr<0){ 0 } else --asr) }, plus = { ++asr }) {
            showDialog = true
            id = 3
        }
        QazoCount(text = stringResource(R.string.shom), count = shom, minus = { viewModel.setShom(if (shom<0){ 0 } else --shom) }, plus = { ++shom }) {
            showDialog = true
            id = 4
        }
        QazoCount(text = stringResource(R.string.xufton), count = xufton, minus = { viewModel.setXufton(if (xufton<0){ 0 } else --xufton) }, plus = { ++xufton }) {
            showDialog = true
            id = 5
        }
        QazoCount(text = stringResource(R.string.vitr), count = vitr, minus = { viewModel.setVitr(if (vitr<0){ 0 } else --vitr) }, plus = { ++vitr }) {
            showDialog = true
            id = 6
        }
    }
    QazoDialog()
    BackHandler {
        viewModel.saveTime(BOMDOD, bomdod)
        viewModel.saveTime(PESHIN, peshin)
        viewModel.saveTime(ASR, asr)
        viewModel.saveTime(SHOM, shom)
        viewModel.saveTime(XUFTON, xufton)
        viewModel.saveTime(VITR, vitr)
        controller.popBackStack()
    }
}

private var showDialog by
    mutableStateOf(false)


@Composable
fun QazoDialog(modifier: Modifier = Modifier) {
    var numberOfQazo by remember {
        mutableStateOf("0")
    }
    var error by remember {
        mutableStateOf(numberOfQazo.toInt()<0 || numberOfQazo.isEmpty() || numberOfQazo.isBlank())
    }
    if (showDialog){
        Dialog(onDismissRequest = { showDialog = false }) {
            OutlinedCard(modifier, colors = CardDefaults.cardColors(White), elevation = CardDefaults.cardElevation(10.dp)) {
                Column {
                    OutlinedTextField(value = numberOfQazo, onValueChange = { numberOfQazo = it; error = false }, isError = error, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), supportingText =
                    { if (error){ Text(stringResource(R.string.errorMessage, MaterialTheme.colorScheme.error)) } }, trailingIcon =
                    {
                        if (error){ Icon(Icons.Default.Info, null, tint =  MaterialTheme.colorScheme.error) }
                    }, modifier = modifier
                        .fillMaxWidth()
                        .padding(5.dp, 2.5.dp))
                    Row(modifier.fillMaxWidth()) {
                        OutlinedButton(onClick = { showDialog=false; id = -1; numberOfQazo = "0" }, modifier = modifier
                            .fillMaxWidth()
                            .weight(1f), colors = ButtonDefaults.buttonColors(Dark_Green)) {
                            Box(modifier = modifier.fillMaxWidth()){
                                Text(stringResource(R.string.cancel), color = White, fontSize = 20.sp)
                            }
                        }
                        OutlinedButton(onClick = { try{ showDialog=false; buttonClicked(numberOfQazo.toInt(), id); id = -1; numberOfQazo = "0" }catch (_:Exception){ error = true } }, modifier = modifier
                            .fillMaxWidth()
                            .weight(1f), colors = ButtonDefaults.buttonColors(Dark_Green)) {
                            Box(modifier = modifier.fillMaxWidth()){
                                Text(stringResource(R.string.save), color = White, fontSize = 20.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

fun buttonClicked(numberOfQazo: Int, id: Int) {
    if (id!=-1){
        when(id){
            1->{
                bomdod = numberOfQazo
            }
            2->{
                peshin = numberOfQazo
            }
            3->{
                asr = numberOfQazo
            }
            4->{
                shom = numberOfQazo
            }
            5->{
                xufton = numberOfQazo
            }
            6->{
                vitr = numberOfQazo
            }
        }
    }
    else{
        bomdod = numberOfQazo
        peshin = numberOfQazo
        asr = numberOfQazo
        shom = numberOfQazo
        xufton = numberOfQazo
        vitr = numberOfQazo
    }
}
