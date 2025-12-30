package uz.coder.muslimcalendar

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import uz.coder.muslimcalendar.presentation.navigation.CalendarNavigation
import uz.coder.muslimcalendar.presentation.ui.theme.LightBlueStatusBar
import uz.coder.muslimcalendar.presentation.ui.theme.MuslimCalendarTheme
import uz.coder.muslimcalendar.presentation.viewModel.HomeViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val viewModel: HomeViewModel by viewModels()

    private var locationPermissionRequestCount = 0

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (fineGranted || coarseGranted) {
            locationPermissionRequestCount = 0
            getLocation()
        } else {
            locationPermissionRequestCount++
            handleLocationPermissionDenied()
        }
    }

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d(TAG, "Notification permission granted")
        } else {
            Log.d(TAG, "Notification permission denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        enableEdgeToEdge(SystemBarStyle.dark(LightBlueStatusBar))

        checkAndShowPermissionIntro()
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                Log.d(TAG, "Exact alarm permission still not granted")
            }
        }
    }

    private fun checkAndShowPermissionIntro() {
        val fineGranted = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val notificationGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else true

        if (!fineGranted && !coarseGranted) {
            showLocationIntroDialog(notificationGranted)
        } else {
            getLocation()
            checkNotificationPermissionIfNeeded(notificationGranted)
        }
    }

    private fun showLocationIntroDialog(notificationGranted: Boolean) {
        AlertDialog.Builder(this)
            .setTitle("Lokatsiya ruxsati kerak")
            .setMessage("Sizning turgan joyingizga qarab namoz vaqtini ko‘rsatish uchun lokatsiya ruxsati kerak bo‘ladi.")
            .setPositiveButton("Ruxsat berish") { _, _ ->
                checkLocationPermission()
                checkNotificationPermissionIfNeeded(notificationGranted)
            }
            .setNegativeButton("Keyinroq") { dialog, _ ->
                dialog.dismiss()
                checkNotificationPermissionIfNeeded(notificationGranted)
            }
            .setCancelable(false)
            .show()
    }

    private fun checkNotificationPermissionIfNeeded(notificationGranted: Boolean) {
        if (!notificationGranted) {
            showNotificationIntroDialog()
        } else {
            checkExactAlarmPermission()
            setContentUI()
        }
    }

    private fun showNotificationIntroDialog() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            AlertDialog.Builder(this)
                .setTitle("Bildirishnomalar ruxsati kerak")
                .setMessage("Namoz vaqtlarini bildirishnomalarda ko‘rsatish uchun ruxsat bering.")
                .setPositiveButton("Ruxsat berish") { _, _ ->
                    checkNotificationPermission()
                    checkExactAlarmPermission()
                    setContentUI()
                }
                .setNegativeButton("Keyinroq") { dialog, _ ->
                    dialog.dismiss()
                    checkExactAlarmPermission()
                    setContentUI()
                }
                .setCancelable(false)
                .show()
        } else {
            checkExactAlarmPermission()
            setContentUI()
        }
    }

    private fun setContentUI() {
        setContent {
            MuslimCalendarTheme(darkTheme = false) {
                Greeting()
            }
        }
    }

    private fun checkLocationPermission() {
        val fineLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarseLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)

        if (fineLocation != PackageManager.PERMISSION_GRANTED || coarseLocation != PackageManager.PERMISSION_GRANTED) {
            if (locationPermissionRequestCount == 0) {
                locationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                showRationaleDialog()
            } else {
                showSettingsDialogForLocation()
            }
        } else {
            getLocation()
        }
    }

    private fun handleLocationPermissionDenied() {
        when (locationPermissionRequestCount) {
            1 -> Log.d(TAG, "Location permission denied first time")
            2 -> Log.d(TAG, "Location permission denied second time")
            else -> showSettingsDialogForLocation()
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val notificationPermission = ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            )
            if (notificationPermission != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun checkExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                showExactAlarmPermissionDialog()
            }
        }
    }

    private fun showExactAlarmPermissionDialog() {
        AlertDialog.Builder(this)
            .setTitle("Namoz vaqtlari uchun ruxsat")
            .setMessage("Namoz vaqtlarini aniq vaqtida bildirish uchun, ilova sozlamalaridan 'Aniq signallar va eslatmalar' ruxsatini bering.")
            .setPositiveButton("Sozlamalarga o'tish") { _, _ ->
                requestExactAlarmPermission()
            }
            .setNegativeButton("Keyinroq") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    @SuppressLint("UseKtx")
    private fun requestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = "package:$packageName".toUri()
                }
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = "package:$packageName".toUri()
                    }
                    startActivity(intent)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                Log.d(TAG, "Location: ${location.latitude}, ${location.longitude}")
                viewModel.loadInformationFromInternet(location.latitude, location.longitude)
            } else {
                Log.d(TAG, "Location is null. Trying again...")
            }
        }
    }

    private fun showRationaleDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.permission_title))
            .setMessage(getString(R.string.permission_location_message))
            .setPositiveButton(getString(R.string.access)) { _, _ ->
                locationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    private fun showSettingsDialogForLocation() {
        AlertDialog.Builder(this)
            .setTitle("Lokatsiya ruxsati kerak")
            .setMessage("Ilova to'g'ri ishlashi uchun lokatsiya ruxsati zarur. Iltimos, sozlamalardan ruxsat bering.")
            .setPositiveButton("Sozlamalarga o'tish") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton("Keyinroq") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    @Composable
    fun Greeting(modifier: Modifier = Modifier) {
        Box(modifier = modifier.fillMaxSize()) {
            CalendarNavigation()
        }
    }
}

private const val TAG = "MainActivity"