package com.onesignal;

import android.os.Handler;
import android.content.Intent;
import android.content.Context;
import android.os.Build$VERSION;
import android.os.Bundle;
import java.util.HashMap;
import android.app.Activity;

public class PermissionsActivity extends Activity
{
    private static final int DELAY_TIME_CALLBACK_CALL = 500;
    private static final String INTENT_EXTRA_ANDROID_PERMISSION_STRING = "INTENT_EXTRA_ANDROID_PERMISSION_STRING";
    private static final String INTENT_EXTRA_CALLBACK_CLASS = "INTENT_EXTRA_CALLBACK_CLASS";
    private static final String INTENT_EXTRA_PERMISSION_TYPE = "INTENT_EXTRA_PERMISSION_TYPE";
    private static final int ONESIGNAL_PERMISSION_REQUEST_CODE = 2;
    private static final int REQUEST_SETTINGS = 3;
    private static final String TAG = "com.onesignal.PermissionsActivity";
    private static ActivityLifecycleHandler.ActivityAvailableListener activityAvailableListener;
    private static final HashMap<String, PermissionCallback> callbackMap;
    private static boolean fallbackToSettings;
    private static boolean neverAskAgainClicked;
    private static boolean waiting;
    private String androidPermissionString;
    private String permissionRequestType;
    
    static {
        callbackMap = new HashMap();
    }
    
    private void handleBundleParams(final Bundle bundle) {
        if (Build$VERSION.SDK_INT < 23) {
            this.finish();
            this.overridePendingTransition(R.anim.onesignal_fade_in, R.anim.onesignal_fade_out);
            return;
        }
        this.reregisterCallbackHandlers(bundle);
        this.permissionRequestType = bundle.getString("INTENT_EXTRA_PERMISSION_TYPE");
        this.requestPermission(this.androidPermissionString = bundle.getString("INTENT_EXTRA_ANDROID_PERMISSION_STRING"));
    }
    
    public static void registerAsCallback(final String s, final PermissionCallback permissionCallback) {
        PermissionsActivity.callbackMap.put((Object)s, (Object)permissionCallback);
    }
    
    private void requestPermission(final String s) {
        if (!PermissionsActivity.waiting) {
            PermissionsActivity.waiting = true;
            PermissionsActivity.neverAskAgainClicked = (true ^ AndroidSupportV4Compat.ActivityCompat.shouldShowRequestPermissionRationale(this, s));
            AndroidSupportV4Compat.ActivityCompat.requestPermissions(this, new String[] { s }, 2);
        }
    }
    
    private void reregisterCallbackHandlers(Bundle string) {
        string = (Bundle)string.getString("INTENT_EXTRA_CALLBACK_CLASS");
        try {
            Class.forName((String)string);
        }
        catch (final ClassNotFoundException ex) {
            final StringBuilder sb = new StringBuilder("Could not find callback class for PermissionActivity: ");
            sb.append((String)string);
            throw new RuntimeException(sb.toString());
        }
    }
    
    private boolean shouldShowSettings() {
        return PermissionsActivity.fallbackToSettings && PermissionsActivity.neverAskAgainClicked && !AndroidSupportV4Compat.ActivityCompat.shouldShowRequestPermissionRationale(this, this.androidPermissionString);
    }
    
    static void startPrompt(final boolean fallbackToSettings, final String s, final String s2, final Class<?> clazz) {
        if (PermissionsActivity.waiting) {
            return;
        }
        PermissionsActivity.fallbackToSettings = fallbackToSettings;
        PermissionsActivity.activityAvailableListener = (ActivityLifecycleHandler.ActivityAvailableListener)new PermissionsActivity$2(s, s2, (Class)clazz);
        final ActivityLifecycleHandler activityLifecycleHandler = ActivityLifecycleListener.getActivityLifecycleHandler();
        if (activityLifecycleHandler != null) {
            activityLifecycleHandler.addActivityAvailableListener(PermissionsActivity.TAG, PermissionsActivity.activityAvailableListener);
        }
    }
    
    protected void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        OneSignal.initWithContext((Context)this);
        this.handleBundleParams(this.getIntent().getExtras());
    }
    
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
        this.handleBundleParams(intent.getExtras());
    }
    
    public void onRequestPermissionsResult(final int n, final String[] array, final int[] array2) {
        PermissionsActivity.waiting = false;
        if (n == 2) {
            new Handler().postDelayed((Runnable)new Runnable(this, array2) {
                final PermissionsActivity this$0;
                final int[] val$grantResults;
                
                public void run() {
                    final int[] val$grantResults = this.val$grantResults;
                    final int length = val$grantResults.length;
                    int n = 0;
                    if (length > 0) {
                        n = n;
                        if (val$grantResults[0] == 0) {
                            n = 1;
                        }
                    }
                    final PermissionCallback permissionCallback = (PermissionCallback)PermissionsActivity.callbackMap.get((Object)this.this$0.permissionRequestType);
                    if (permissionCallback != null) {
                        if (n != 0) {
                            permissionCallback.onAccept();
                        }
                        else {
                            permissionCallback.onReject(this.this$0.shouldShowSettings());
                        }
                        return;
                    }
                    final StringBuilder sb = new StringBuilder("Missing handler for permissionRequestType: ");
                    sb.append(this.this$0.permissionRequestType);
                    throw new RuntimeException(sb.toString());
                }
            }, 500L);
        }
        final ActivityLifecycleHandler activityLifecycleHandler = ActivityLifecycleListener.getActivityLifecycleHandler();
        if (activityLifecycleHandler != null) {
            activityLifecycleHandler.removeActivityAvailableListener(PermissionsActivity.TAG);
        }
        this.finish();
        this.overridePendingTransition(R.anim.onesignal_fade_in, R.anim.onesignal_fade_out);
    }
    
    interface PermissionCallback
    {
        void onAccept();
        
        void onReject(final boolean p0);
    }
}
