package uz.coder.muslimcalendar.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class VolumeButtonReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        // android.media.VOLUME_CHANGED_ACTION is the standard broadcast for volume changes
        if (intent?.action == "android.media.VOLUME_CHANGED_ACTION") {
            AlarmBroadCast.mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                    it.release()
                    AlarmBroadCast.mediaPlayer = null
                }
            }
        }
    }
}
