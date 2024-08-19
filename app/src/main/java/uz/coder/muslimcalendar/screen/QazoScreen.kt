package uz.coder.muslimcalendar.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
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
import uz.coder.muslimcalendar.ui.theme.Light_Blue
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
        .background(White)
        .padding(paddingValues), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        QazoCount(text = stringResource(R.string.bomdod), count = bomdod, minus = { viewModel.setBomdod(if (bomdod <=0){ 0 } else --bomdod) }, plus = { viewModel.setBomdod(++bomdod) }) {
            showDialog = true
            id = 1
        }
        QazoCount(text = stringResource(R.string.peshin), count = peshin, minus = { viewModel.setPeshin(if (peshin <=0){ 0 } else --peshin) }, plus = { viewModel.setPeshin(++peshin) }) {
            showDialog = true
            id = 2
        }
        QazoCount(text = stringResource(R.string.asr), count = asr, minus = { viewModel.setAsr(if (asr <=0){ 0 } else --asr) }, plus = { viewModel.setAsr(++asr) }) {
            showDialog = true
            id = 3
        }
        QazoCount(text = stringResource(R.string.shom), count = shom, minus = { viewModel.setShom(if (shom <=0){ 0 } else --shom) }, plus = { viewModel.setShom(++shom) }) {
            showDialog = true
            id = 4
        }
        QazoCount(text = stringResource(R.string.xufton), count = xufton, minus = { viewModel.setXufton(if (xufton <=0){ 0 } else --xufton) }, plus = { viewModel.setXufton(++xufton) }) {
            showDialog = true
            id = 5
        }
        QazoCount(text = stringResource(R.string.vitr), count = vitr, minus = { viewModel.setVitr(if (vitr <=0){ 0 } else --vitr) }, plus = { viewModel.setVitr(++vitr) }) {
            showDialog = true
            id = 6
        }
    }
    QazoDialog(viewModel = viewModel)
    BackHandler {
        viewModel.saveInt(BOMDOD, bomdod)
        viewModel.saveInt(PESHIN, peshin)
        viewModel.saveInt(ASR, asr)
        viewModel.saveInt(SHOM, shom)
        viewModel.saveInt(XUFTON, xufton)
        viewModel.saveInt(VITR, vitr)
        controller.popBackStack()
    }
}

private var showDialog by
    mutableStateOf(false)


@Composable
fun QazoDialog(modifier: Modifier = Modifier, viewModel: CalendarViewModel) {
    var numberOfQazo by remember {
        mutableIntStateOf(0)
    }

    if (showDialog){
        Dialog(onDismissRequest = { showDialog = false }) {
            OutlinedCard(modifier, colors = CardDefaults.cardColors(White), elevation = CardDefaults.cardElevation(10.dp)) {
                Column {
                    OutlinedTextField(value = numberOfQazo.toString(), onValueChange = { numberOfQazo = try {
                        it.toInt()
                    }catch (_:Exception){ 0 } }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = modifier
                        .fillMaxWidth()
                        .padding(5.dp, 2.5.dp))
                    Row(modifier.fillMaxWidth()) {
                        OutlinedButton(onClick = { showDialog =false; id = -1; numberOfQazo = 0 }, modifier = modifier
                            .fillMaxWidth()
                            .weight(1f), colors = ButtonDefaults.buttonColors(Light_Blue)) {
                            Box(modifier = modifier.fillMaxWidth()){
                                Text(stringResource(R.string.cancel), color = White, fontSize = 20.sp)
                            }
                        }
                        OutlinedButton(onClick = { try{ showDialog =false; buttonClicked(numberOfQazo, viewModel); id = -1; numberOfQazo = 0 }catch (_:Exception){  } }, modifier = modifier
                            .fillMaxWidth()
                            .weight(1f), colors = ButtonDefaults.buttonColors(Light_Blue)) {
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

fun buttonClicked(numberOfQazo: Int, viewModel: CalendarViewModel) {
    if (id !=-1){
        when(id){
            1->{
                bomdod = numberOfQazo
                viewModel.setBomdod(numberOfQazo)
            }
            2->{
                peshin = numberOfQazo
                viewModel.setPeshin(numberOfQazo)
            }
            3->{
                asr = numberOfQazo
                viewModel.setAsr(numberOfQazo)
            }
            4->{
                shom = numberOfQazo
                viewModel.setShom(numberOfQazo)
            }
            5->{
                xufton = numberOfQazo
                viewModel.setXufton(numberOfQazo)
            }
            6->{
                vitr = numberOfQazo
                viewModel.setVitr(numberOfQazo)
            }
        }
    }
    else{
        bomdod = numberOfQazo
        viewModel.setBomdod(numberOfQazo)
        peshin = numberOfQazo
        viewModel.setPeshin(numberOfQazo)
        asr = numberOfQazo
        viewModel.setAsr(numberOfQazo)
        shom = numberOfQazo
        viewModel.setShom(numberOfQazo)
        xufton = numberOfQazo
        viewModel.setXufton(numberOfQazo)
        vitr = numberOfQazo
        viewModel.setVitr(numberOfQazo)
    }
}