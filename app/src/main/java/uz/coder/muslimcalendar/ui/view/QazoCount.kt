package uz.coder.muslimcalendar.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uz.coder.muslimcalendar.R
import uz.coder.muslimcalendar.ui.theme.Light_Blue

@Composable
fun QazoCount(modifier: Modifier = Modifier, text:String, count:Int, minus:()->Unit, plus:()->Unit, onClick:()->Unit) {
    Column {
        Text(text = text,
            modifier
                .fillMaxWidth()
                .padding(end = 10.dp), textAlign = TextAlign.Center)
        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = modifier.fillMaxWidth()) {
            IconButton(onClick = minus, colors = IconButtonDefaults.iconButtonColors(
                Light_Blue), modifier = modifier.size(50.dp)) {
                Icon(painterResource(R.drawable.minus), contentDescription = null, tint = White)
            }
            Box(modifier = modifier
                .clickable { onClick() }
                .height(50.dp)
                .width(120.dp)
                .background(
                    Light_Blue, CircleShape
                ), contentAlignment = Alignment.Center){
                Text(count.toString(), color =  White, fontSize =  25.sp)
            }
            IconButton(onClick = plus, colors = IconButtonDefaults.iconButtonColors(
                Light_Blue), modifier = modifier.size(50.dp)) {
                Icon(painterResource(R.drawable.plus), contentDescription = null, tint = White)
            }
        }
    }
}