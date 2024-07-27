package uz.coder.muslimcalendar.todo

import android.app.Application

class App : Application() {
    override fun onCreate() {
        application = this
        super.onCreate()
    }

    companion object {
        var application: Application? = null
            private set
    }
}
