package uz.coder.muslimcalendar.todo;

import android.app.Application;

import uz.coder.muslimcalendar.repository.CalendarRepositoryImpl;

public class App extends Application {
    private static Application application;
    @Override
    public void onCreate() {
        application = this;
        super.onCreate();
    }
    public static Application getApplication(){
        return application;
    }
}
