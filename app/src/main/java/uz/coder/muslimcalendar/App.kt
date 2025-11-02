package uz.coder.muslimcalendar

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        application = this
        super.onCreate()
    }

    companion object {
        lateinit var application: Application
            private set
    }
}