package lu.die.mundo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

public abstract class SamplePermissionDetection {
    /**
     * Check if the project has embedded with the permission pack
     * If your app will not be uploaded to the app market like Play Store,
     * you have to integrate with the permission pack.
     * @param context app context (activity, application...)
     * @throws RuntimeException When the permission pack is not be included,
     * a {@link RuntimeException} will be thrown
     */
    public static void detectPermission(final Context context) throws RuntimeException
    {
        int result = PackageManager.PERMISSION_DENIED;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            result = context.checkCallingOrSelfPermission(Manifest.permission.DETECT_SCREEN_CAPTURE);
        }
        if(result != PackageManager.PERMISSION_GRANTED)
        {
            result = context.checkCallingOrSelfPermission(Manifest.permission.VIBRATE);
        }
        if(result != PackageManager.PERMISSION_GRANTED)
        {
            result = context.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE);
        }
        if(result != PackageManager.PERMISSION_GRANTED)
        {
            throw new RuntimeException("Permission pack must be included");
        }
    }
}
