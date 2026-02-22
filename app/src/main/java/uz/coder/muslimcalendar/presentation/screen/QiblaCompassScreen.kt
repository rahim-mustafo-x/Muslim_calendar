package uz.coder.muslimcalendar.presentation.screen

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import uz.coder.muslimcalendar.SharedPref
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QiblaCompassScreen(
    controller: NavHostController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var azimuth by remember { mutableFloatStateOf(0f) }
    var qiblaDirection by remember { mutableFloatStateOf(0f) }
    var isCalibrated by remember { mutableStateOf(false) }
    
    val animatedAzimuth by animateFloatAsState(
        targetValue = azimuth,
        animationSpec = tween(durationMillis = 100),
        label = "azimuth"
    )

    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        
        if (accelerometer == null || magnetometer == null) {
            isCalibrated = false
            onDispose { }
        }
        
        val gravity = FloatArray(3)
        val geomagnetic = FloatArray(3)
        var lastUpdate = 0L
        
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastUpdate < 100) return
                lastUpdate = currentTime
                
                when (event.sensor.type) {
                    Sensor.TYPE_ACCELEROMETER -> event.values.copyInto(gravity)
                    Sensor.TYPE_MAGNETIC_FIELD -> event.values.copyInto(geomagnetic)
                }
                
                val R = FloatArray(9)
                val I = FloatArray(9)
                
                if (SensorManager.getRotationMatrix(R, I, gravity, geomagnetic)) {
                    val orientation = FloatArray(3)
                    SensorManager.getOrientation(R, orientation)
                    azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
                    isCalibrated = true
                }
            }
            
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                isCalibrated = accuracy >= SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM
            }
        }
        
        sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_UI)
        sensorManager.registerListener(listener, magnetometer, SensorManager.SENSOR_DELAY_UI)
        
        val sharedPref = SharedPref(context)
        val userLat = sharedPref.getFloat("saved_latitude", 41.2995f).toDouble()
        val userLon = sharedPref.getFloat("saved_longitude", 69.2401f).toDouble()
        qiblaDirection = calculateQiblaDirection(userLat, userLon)
        
        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Qibla yo'nalishi") },
                navigationIcon = {
                    IconButton(onClick = { controller.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Orqaga")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (!isCalibrated) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Telefonni kalibrlang",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "8-raqam shaklida harakatlantiring",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            } else {
                Text(
                    text = "Qiblaga yo'naltiring",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CompassView(
                    azimuth = animatedAzimuth,
                    qiblaDirection = qiblaDirection
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            val qiblaAngle = ((qiblaDirection - animatedAzimuth + 360) % 360).toInt()
            val isAligned = qiblaAngle in 355..360 || qiblaAngle in 0..5
            
            Card(
                modifier = Modifier.padding(horizontal = 32.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isAligned)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "$qiblaAngle°",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (isAligned)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isAligned) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(
                            text = if (isAligned) "To'g'ri yo'nalishdasiz!" else "Qiblaga buriling",
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (isAligned)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CompassView(azimuth: Float, qiblaDirection: Float) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val outlineColor = MaterialTheme.colorScheme.outline
    
    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2
        
        drawCircle(
            color = outlineColor.copy(alpha = 0.3f),
            radius = radius,
            center = center,
            style = Stroke(width = 4f)
        )
        
        rotate(-azimuth, center) {
            drawLine(
                color = Color.Red,
                start = Offset(center.x, center.y - radius + 20),
                end = Offset(center.x, center.y - radius + 60),
                strokeWidth = 8f
            )
            
            for (i in 0 until 360 step 30) {
                val angle = Math.toRadians(i.toDouble())
                val startRadius = if (i % 90 == 0) radius - 40 else radius - 20
                val endRadius = radius - 10
                
                drawLine(
                    color = outlineColor,
                    start = Offset(
                        center.x + (startRadius * sin(angle)).toFloat(),
                        center.y - (startRadius * cos(angle)).toFloat()
                    ),
                    end = Offset(
                        center.x + (endRadius * sin(angle)).toFloat(),
                        center.y - (endRadius * cos(angle)).toFloat()
                    ),
                    strokeWidth = 2f
                )
            }
        }
        
        rotate(qiblaDirection - azimuth, center) {
            val arrowPath = Path().apply {
                moveTo(center.x, center.y - radius + 80)
                lineTo(center.x - 20, center.y - radius + 140)
                lineTo(center.x, center.y - radius + 120)
                lineTo(center.x + 20, center.y - radius + 140)
                close()
            }
            drawPath(path = arrowPath, color = primaryColor)
        }
        
        drawCircle(color = primaryColor, radius = 10f, center = center)
    }
}

fun calculateQiblaDirection(lat: Double, lon: Double): Float {
    val kaabaLat = Math.toRadians(21.4225)
    val kaabaLon = Math.toRadians(39.8262)
    val userLat = Math.toRadians(lat)
    val userLon = Math.toRadians(lon)
    
    val dLon = kaabaLon - userLon
    val y = sin(dLon) * cos(kaabaLat)
    val x = cos(userLat) * sin(kaabaLat) - sin(userLat) * cos(kaabaLat) * cos(dLon)
    
    val bearing = Math.toDegrees(atan2(y, x))
    return ((bearing + 360) % 360).toFloat()
}
