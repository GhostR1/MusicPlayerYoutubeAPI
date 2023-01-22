package nataliia.semenova.musicplayeryoutubeapi.utils;

import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import nataliia.semenova.musicplayeryoutubeapi.ui.MusicPlayerActivity;

public class PermissionHelper {
    public static boolean checkPermission(String permission, int requestCode, MusicPlayerActivity musicPlayerActivity) {
        if (ContextCompat.checkSelfPermission(musicPlayerActivity, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(musicPlayerActivity, new String[] { permission }, requestCode);
            return false;
        } else {
            return true;
        }
    }
}
