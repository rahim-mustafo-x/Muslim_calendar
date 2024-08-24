# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

-dontwarn org.slf4j.impl.StaticLoggerBinder
# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
-keepclassmembers class uz.coder.muslimcalendar.models.internet.HijriDate, uz.coder.muslimcalendar.models.internet.PrayerTime, uz.coder.muslimcalendar.models.internet.Times, uz.coder.muslimcalendar.models.db.MuslimCalendarDbModel, uz.coder.muslimcalendar.models.model.RefreshDay {
   public *;
}
# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile