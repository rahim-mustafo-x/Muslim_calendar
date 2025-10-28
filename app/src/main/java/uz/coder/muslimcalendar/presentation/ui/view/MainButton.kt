package uz.coder.muslimcalendar.presentation.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uz.coder.muslimcalendar.presentation.ui.theme.Light_Blue

@Composable
fun MainButton(modifier: Modifier = Modifier, resId: Int, text:String, onClick:()->Unit) {
    OutlinedCard(onClick = onClick, modifier = modifier
        .padding(5.dp), colors = CardDefaults.cardColors(White), shape = CircleShape) {
        Column(modifier.size(120.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(
                painterResource(resId), contentDescription = null, modifier = modifier
                    .size(80.dp)
                    .padding(5.dp), tint = Light_Blue)
            Text(text = text, color = Light_Blue, fontSize = 20.sp)
        }
    }
}