package uz.coder.muslimcalendar.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.URL;

public class TranslateToUzbek {

    private TranslateToUzbek() {}

    // 📌 Arabchadan o'zbekchaga tarjima
    @NonNull
    public static String translateArabicToUzbek(@NonNull String text) {
        return translate(text, "ar");
    }

    // 📌 Inglizchadan o'zbekchaga tarjima
    @NonNull
    public static String translateEnglishToUzbek(@NonNull String text) {
        return translate(text, "en");
    }

    // 🔧 Ichki method: translate.googleapis.com ga so'rov yuboradi
    private static String translate(String text, String fromLang) {
        try {
            String encodedText = URLEncoder.encode(text, "UTF-8");
            String urlStr = "https://translate.googleapis.com/translate_a/single?client=gtx&sl="
                    + fromLang + "&tl=" + "uz" + "&dt=t&q=" + encodedText;

            String result = getString(urlStr);
            int start = result.indexOf("\"") + 1;
            int end = result.indexOf("\"", start);
            return result.substring(start, end);

        } catch (Exception e) {
            Log.e(TAG, "translate: tarjimada xato", e);
            return "Tarjima xatosi";
        }
    }

    @NonNull
    private static String getString(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");

        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream())
        );

        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        in.close();

        // JSON javobdan tarjima qilingan matnni ajratamiz
        return response.toString();
    }

    private static final String TAG = "TranslateToUzbek";
}