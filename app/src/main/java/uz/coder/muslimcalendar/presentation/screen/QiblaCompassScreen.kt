package uz.coder.muslimcalendar.presentation.screen

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
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
    val activity = context as? Activity
    val lifecycleOwner = LocalLifecycleOwner.current
    var azimuth by remember { mutableFloatStateOf(0f) }
    var qiblaDirection by remember { mutableFloatStateOf(0f) }
    var isCalibrated by remember { mutableStateOf(false) }
    var hasLocationPermission by remember { mutableStateOf(context.hasLocationPermission()) }
    var showPermissionRationale by remember { mutableStateOf(false) }
    var showSettingsExplanation by remember { mutableStateOf(false) }
    var gpsResolutionVersion by remember { mutableIntStateOf(0) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (!hasLocationPermission) {
            val canAskAgain = activity?.let {
                ActivityCompat.shouldShowRequestPermissionRationale(it, Manifest.permission.ACCESS_FINE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(it, Manifest.permission.ACCESS_COARSE_LOCATION)
            } == true
            showPermissionRationale = canAskAgain
            showSettingsExplanation = !canAskAgain
        }
    }
    val gpsResolutionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        // Re-check only after the user enabled location; cancelling must not reopen the dialog.
        if (result.resultCode == Activity.RESULT_OK) gpsResolutionVersion++
    }

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    DisposableEffect(lifecycleOwner, context) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                hasLocationPermission = context.hasLocationPermission()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    
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

    /*
     * Qibla needs a current position, but location updates are deliberately scoped to
     * this composable. Disposing the screen always removes the callback, so this is
     * never background location tracking.
     */
    DisposableEffect(hasLocationPermission, gpsResolutionVersion) {
        if (!hasLocationPermission) {
            onDispose { }
        } else {
            val locationClient = LocationServices.getFusedLocationProviderClient(context)
            val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5_000L)
                .setMinUpdateIntervalMillis(2_000L)
                .setWaitForAccurateLocation(true)
                .build()
            val settingsRequest = LocationSettingsRequest.Builder()
                .addLocationRequest(request)
                .setAlwaysShow(true)
                .build()
            var isScreenActive = true
            val callback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    val location = result.lastLocation ?: return
                    if (!isScreenActive) return
                    SharedPref(context).apply {
                        saveValue("saved_latitude", location.latitude.toFloat())
                        saveValue("saved_longitude", location.longitude.toFloat())
                    }
                    qiblaDirection = calculateQiblaDirection(location.latitude, location.longitude)
                }
            }

            LocationServices.getSettingsClient(context).checkLocationSettings(settingsRequest)
                .addOnSuccessListener {
                    if (isScreenActive) {
                        locationClient.requestLocationUpdates(request, callback, context.mainLooper)
                    }
                }
                .addOnFailureListener { error ->
                    if (isScreenActive && error is ResolvableApiException) {
                        gpsResolutionLauncher.launch(
                            IntentSenderRequest.Builder(error.resolution).build()
                        )
                    }
                }

            onDispose {
                isScreenActive = false
                locationClient.removeLocationUpdates(callback)
            }
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
            if (!hasLocationPermission) {
                Card(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Text(
                        text = "Aniq qibla yo'nalishi uchun joylashuv ruxsati kerak.",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
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

    if (showPermissionRationale) {
        AlertDialog(
            onDismissRequest = { showPermissionRationale = false },
            title = { Text("Joylashuv ruxsati kerak") },
            text = { Text("Joylashuv faqat Qibla yo'nalishi oynasi ochiq paytda olinadi va oynadan chiqqaningizda kuzatuv to'xtaydi.") },
            confirmButton = {
                TextButton(onClick = {
                    showPermissionRationale = false
                    permissionLauncher.launch(
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                    )
                }) { Text("Ruxsat berish") }
            },
            dismissButton = { TextButton(onClick = { showPermissionRationale = false }) { Text("Hozir emas") } }
        )
    }

    if (showSettingsExplanation) {
        AlertDialog(
            onDismissRequest = { showSettingsExplanation = false },
            title = { Text("Ruxsat sozlamalarda o'chirilgan") },
            text = { Text("Qibla yo'nalishini aniqlash uchun ilova sozlamalaridan Joylashuv ruxsatini yoqing.") },
            confirmButton = {
                TextButton(onClick = {
                    showSettingsExplanation = false
                    context.startActivity(
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                    )
                }) { Text("Sozlamalarni ochish") }
            },
            dismissButton = { TextButton(onClick = { showSettingsExplanation = false }) { Text("Bekor qilish") } }
        )
    }
}

private fun Context.hasLocationPermission(): Boolean =
    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

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
