package uz.coder.muslimcalendar.ui.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import uz.coder.muslimcalendar.ui.theme.Dark_Green

@Composable
fun MainButton(modifier: Modifier = Modifier, resId: Int, onClick:()->Unit) {
    Card(onClick = onClick, shape = CardDefaults.elevatedShape, modifier = modifier
        .size(80.dp)
        .padding(5.dp), colors = CardDefaults.cardColors(
        Dark_Green
    )) {
        Image(
            painterResource(resId), contentDescription = null, modifier = modifier
            .fillMaxSize()
            .padding(2.5.dp), contentScale = ContentScale.Crop)
    }
}