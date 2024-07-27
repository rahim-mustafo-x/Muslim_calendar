package uz.coder.muslimcalendar.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.Color.Companion.Yellow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import uz.coder.muslimcalendar.models.model.Item

@Composable
fun TimeSettingView(modifier: Modifier = Modifier, item: Item, onClick:(String)->Unit, resId:Int) {
    Column(modifier = modifier
        .fillMaxWidth()
        .background(White)
        .height(80.dp)
        .clickable { onClick(item.name) }){
        Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier
                    .height(80.dp)
                    .fillMaxWidth()
                    .weight(4f), contentAlignment = Alignment.Center){
                Text(item.name)
            }
            Box(
                modifier
                    .height(80.dp)
                    .fillMaxWidth()
                    .weight(2.5f), contentAlignment = Alignment.Center){
                Text(item.time)
            }
            Icon(painterResource(resId), null,
                modifier
                    .size(20.dp)
                    .weight(1.5f))
        }
    }
    HorizontalDivider()
}
