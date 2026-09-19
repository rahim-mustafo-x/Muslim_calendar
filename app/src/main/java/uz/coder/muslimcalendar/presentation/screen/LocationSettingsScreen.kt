package uz.coder.muslimcalendar.presentation.screen

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel
import uz.coder.muslimcalendar.presentation.viewModel.LocationSettingsViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationSettingsScreen(
    controller: NavHostController,
    viewModel: LocationSettingsViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    var city by remember { mutableStateOf("") }
    var lookupError by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    fun detectLocation() {
        viewModel.setLocating(true)
        val client = LocationServices.getFusedLocationProviderClient(context)
        try {
            client.lastLocation.addOnSuccessListener { location ->
                if (location == null) {
                    viewModel.setLocating(false)
                    lookupError = "Joylashuv topilmadi. Shahar nomini kiriting."
                    return@addOnSuccessListener
                }
                val name = try {
                    val address = Geocoder(context, Locale.forLanguageTag("uz"))
                        .getFromLocation(location.latitude, location.longitude, 1)
                        ?.firstOrNull()
                    address?.locality ?: address?.subAdminArea ?: "Tanlangan joylashuv"
                } catch (_: Exception) {
                    "Tanlangan joylashuv"
                }
                viewModel.updateDetectedLocation(name, location.latitude, location.longitude)
            }.addOnFailureListener {
                viewModel.setLocating(false)
                lookupError = "Joylashuvni olib bo'lmadi. Shahar nomini kiriting."
            }
        } catch (e: SecurityException) {
            viewModel.setLocating(false)
            lookupError = "Joylashuv ruxsati yo'q."
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            detectLocation()
        } else {
            lookupError = "Ruxsat berilmadi. Shahar nomini qo'lda kiriting."
        }
    }

    LaunchedEffect(Unit) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
        if (hasPermission) {
            detectLocation()
        } else {
            permissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            // Always return to the existing home entry. A plain pop can race with the
            // initial location redirect and briefly show the settings screen again.
            controller.popBackStack("home", inclusive = false)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Namoz vaqti joylashuvi") },
                navigationIcon = {
                    IconButton(onClick = { controller.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Orqaga")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp).padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.LocationOn, null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(16.dp))
            Text("Joylashuvingizni tanlang", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(8.dp))
            Text(
                "Joylashuv faqat siz tanlaganingizda olinadi va keyingi tashriflarda saqlangan ma'lumot ishlatiladi.",
                style = MaterialTheme.typography.bodyMedium
            )
            
            state.detectedCity?.let { cityName ->
                Spacer(Modifier.height(24.dp))
                Text("Aniqlangan joylashuv:", style = MaterialTheme.typography.labelLarge)
                Text(cityName, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
            }

            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    val lat = state.detectedLatitude
                    val lon = state.detectedLongitude
                    val name = state.detectedCity
                    if (lat != null && lon != null && name != null) {
                        viewModel.saveLocation(name, lat, lon)
                    } else {
                        detectLocation()
                    }
                },
                enabled = !state.isSaving && !state.isLocating,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (state.detectedCity != null) "Shu joylashuvni saqlash" else "Hozirgi joylashuvimdan foydalanish")
            }
            Spacer(Modifier.height(20.dp))
            OutlinedTextField(
                value = city,
                onValueChange = { city = it; lookupError = null },
                label = { Text("Shahar yoki tuman nomi") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = {
                    viewModel.setLocating(true)
                    lookupError = null
                    scope.launch {
                        val address = withContext(Dispatchers.IO) {
                            try { Geocoder(context, Locale.forLanguageTag("uz")).getFromLocationName(city, 1)?.firstOrNull() }
                            catch (_: Exception) { null }
                        }
                        viewModel.setLocating(false)
                        if (address == null) {
                            lookupError = "Shahar topilmadi. Nomini aniqroq yozing."
                        } else {
                            viewModel.saveLocation(
                                address.locality ?: city,
                                address.latitude,
                                address.longitude
                            )
                        }
                    }
                },
                enabled = city.isNotBlank() && !state.isSaving && !state.isLocating,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Shaharni tanlash") }
            if (state.isLocating || state.isSaving) {
                Spacer(Modifier.height(20.dp))
                CircularProgressIndicator()
            }
            (lookupError ?: state.error)?.let {
                Spacer(Modifier.height(16.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
