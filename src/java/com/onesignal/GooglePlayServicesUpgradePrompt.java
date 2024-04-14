package com.onesignal;

import android.content.DialogInterface;
import android.content.DialogInterface$OnClickListener;
import android.app.AlertDialog$Builder;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager$NameNotFoundException;
import android.app.PendingIntent;
import android.app.PendingIntent$CanceledException;
import android.content.Context;
import com.google.android.gms.common.GoogleApiAvailability;
import android.app.Activity;

class GooglePlayServicesUpgradePrompt
{
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    
    private static void OpenPlayStoreToApp(final Activity activity) {
        try {
            final GoogleApiAvailability instance = GoogleApiAvailability.getInstance();
            final PendingIntent errorResolutionPendingIntent = instance.getErrorResolutionPendingIntent((Context)activity, instance.isGooglePlayServicesAvailable(OneSignal.appContext), 9000);
            if (errorResolutionPendingIntent != null) {
                errorResolutionPendingIntent.send();
            }
        }
        catch (final PendingIntent$CanceledException ex) {
            ex.printStackTrace();
        }
    }
    
    private static boolean isGooglePlayStoreInstalled() {
        try {
            final PackageManager packageManager = OneSignal.appContext.getPackageManager();
            return ((String)packageManager.getPackageInfo("com.google.android.gms", 128).applicationInfo.loadLabel(packageManager)).equals((Object)"Market") ^ true;
        }
        catch (final PackageManager$NameNotFoundException ex) {
            return false;
        }
    }
    
    static void showUpdateGPSDialog() {
        if (!OSUtils.isAndroidDeviceType()) {
            return;
        }
        if (isGooglePlayStoreInstalled()) {
            if (!OneSignal.getDisableGMSMissingPrompt()) {
                if (OneSignalPrefs.getBool(OneSignalPrefs.PREFS_ONESIGNAL, "GT_DO_NOT_SHOW_MISSING_GPS", false)) {
                    return;
                }
                OSUtils.runOnMainUIThread((Runnable)new Runnable() {
                    public void run() {
                        final Activity currentActivity = OneSignal.getCurrentActivity();
                        if (currentActivity == null) {
                            return;
                        }
                        new AlertDialog$Builder((Context)currentActivity).setMessage((CharSequence)OSUtils.getResourceString((Context)currentActivity, "onesignal_gms_missing_alert_text", "To receive push notifications please press 'Update' to enable 'Google Play services'.")).setPositiveButton((CharSequence)OSUtils.getResourceString((Context)currentActivity, "onesignal_gms_missing_alert_button_update", "Update"), (DialogInterface$OnClickListener)new DialogInterface$OnClickListener(this, currentActivity) {
                            final GooglePlayServicesUpgradePrompt$1 this$0;
                            final Activity val$activity;
                            
                            public void onClick(final DialogInterface dialogInterface, final int n) {
                                OpenPlayStoreToApp(this.val$activity);
                            }
                        }).setNegativeButton((CharSequence)OSUtils.getResourceString((Context)currentActivity, "onesignal_gms_missing_alert_button_skip", "Skip"), (DialogInterface$OnClickListener)new DialogInterface$OnClickListener(this) {
                            final GooglePlayServicesUpgradePrompt$1 this$0;
                            
                            public void onClick(final DialogInterface dialogInterface, final int n) {
                                OneSignalPrefs.saveBool(OneSignalPrefs.PREFS_ONESIGNAL, "GT_DO_NOT_SHOW_MISSING_GPS", true);
                            }
                        }).setNeutralButton((CharSequence)OSUtils.getResourceString((Context)currentActivity, "onesignal_gms_missing_alert_button_close", "Close"), (DialogInterface$OnClickListener)null).create().show();
                    }
                });
            }
        }
    }
}
