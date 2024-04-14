package com.onesignal;

import android.os.Build$VERSION;
import android.util.Log;
import android.os.Process;
import android.content.Context;
import androidx.core.app.ActivityCompat;
import android.app.Activity;

class AndroidSupportV4Compat
{
    static class ActivityCompat
    {
        static void requestPermissions(final Activity activity, final String[] array, final int n) {
            ActivityCompatApi23.requestPermissions(activity, array, n);
        }
        
        static boolean shouldShowRequestPermissionRationale(final Activity activity, final String s) {
            return ActivityCompatApi23.shouldShowRequestPermissionRationale(activity, s);
        }
    }
    
    static class ActivityCompatApi23
    {
        static void requestPermissions(final Activity activity, final String[] array, final int n) {
            if (activity instanceof RequestPermissionsRequestCodeValidator) {
                ((RequestPermissionsRequestCodeValidator)activity).validateRequestPermissionsRequestCode(n);
            }
            OneSignal$$ExternalSyntheticApiModelOutline0.m(activity, array, n);
        }
        
        static boolean shouldShowRequestPermissionRationale(final Activity activity, final String s) {
            return androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale(activity, s);
        }
    }
    
    static class ContextCompat
    {
        static int checkSelfPermission(final Context context, final String s) {
            try {
                return context.checkPermission(s, Process.myPid(), Process.myUid());
            }
            finally {
                Log.e("OneSignal", "checkSelfPermission failed, returning PERMISSION_DENIED");
                return -1;
            }
        }
        
        static int getColor(final Context context, final int n) {
            if (Build$VERSION.SDK_INT > 22) {
                return OneSignal$$ExternalSyntheticApiModelOutline0.m(context, n);
            }
            return context.getResources().getColor(n);
        }
    }
    
    interface RequestPermissionsRequestCodeValidator
    {
        void validateRequestPermissionsRequestCode(final int p0);
    }
}
