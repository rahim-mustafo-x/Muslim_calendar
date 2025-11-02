#include <jni.h>
#include <string>
using namespace std;

// --- Base URLs ---
string getPrayerTimeUrl() {
    return "https://api.aladhan.com/";
}

string getQuranUzbekUrl() {
    return "https://quranenc.com/";
}

string getQuranArabUrl() {
    return "https://api.alquran.cloud/";
}

// --- JNI Functions ---

extern "C"
JNIEXPORT jstring JNICALL
Java_uz_coder_muslimcalendar_data_network_ApiClient_getPrayerTimeUrl(JNIEnv *env, jobject /* this */) {
    return env->NewStringUTF(getPrayerTimeUrl().c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_uz_coder_muslimcalendar_data_network_ApiClient_getQuranUzbekUrl(JNIEnv *env, jobject /* this */) {
    return env->NewStringUTF(getQuranUzbekUrl().c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_uz_coder_muslimcalendar_data_network_ApiClient_getQuranArabUrl(JNIEnv *env, jobject /* this */) {
    return env->NewStringUTF(getQuranArabUrl().c_str());
}
