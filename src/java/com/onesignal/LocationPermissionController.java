package com.onesignal;

import android.app.Activity;
import kotlin.jvm.internal.Intrinsics;
import kotlin.Metadata;

@Metadata(bv = { 1, 0, 3 }, d1 = { "\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\b\u0010\u0005\u001a\u00020\u0006H\u0016J\u0010\u0010\u0007\u001a\u00020\u00062\u0006\u0010\b\u001a\u00020\tH\u0016J\u0010\u0010\n\u001a\u00020\u00062\u0006\u0010\u000b\u001a\u00020\fH\u0002J\u0016\u0010\r\u001a\u00020\u00062\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\u000e\u001a\u00020\u0004J\b\u0010\u000f\u001a\u00020\u0006H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n\u0000¨\u0006\u0010" }, d2 = { "Lcom/onesignal/LocationPermissionController;", "Lcom/onesignal/PermissionsActivity$PermissionCallback;", "()V", "PERMISSION_TYPE", "", "onAccept", "", "onReject", "fallbackToSettings", "", "onResponse", "result", "Lcom/onesignal/OneSignal$PromptActionResult;", "prompt", "androidPermissionString", "showFallbackAlertDialog", "onesignal_release" }, k = 1, mv = { 1, 4, 2 })
public final class LocationPermissionController implements PermissionsActivity$PermissionCallback
{
    public static final LocationPermissionController INSTANCE;
    private static final String PERMISSION_TYPE = "LOCATION";
    
    static {
        PermissionsActivity.registerAsCallback("LOCATION", (PermissionsActivity$PermissionCallback)(INSTANCE = new LocationPermissionController()));
    }
    
    private LocationPermissionController() {
    }
    
    private final void onResponse(final OneSignal$PromptActionResult oneSignal$PromptActionResult) {
        LocationController.sendAndClearPromptHandlers(true, oneSignal$PromptActionResult);
    }
    
    private final void showFallbackAlertDialog() {
        final Activity currentActivity = OneSignal.getCurrentActivity();
        if (currentActivity != null) {
            Intrinsics.checkNotNullExpressionValue((Object)currentActivity, "OneSignal.getCurrentActivity() ?: return");
            final AlertDialogPrepromptForAndroidSettings instance = AlertDialogPrepromptForAndroidSettings.INSTANCE;
            final String string = currentActivity.getString(R$string.location_permission_name_for_title);
            Intrinsics.checkNotNullExpressionValue((Object)string, "activity.getString(R.str\u2026ermission_name_for_title)");
            final String string2 = currentActivity.getString(R$string.location_permission_settings_message);
            Intrinsics.checkNotNullExpressionValue((Object)string2, "activity.getString(R.str\u2026mission_settings_message)");
            instance.show(currentActivity, string, string2, (AlertDialogPrepromptForAndroidSettings$Callback)new LocationPermissionController$showFallbackAlertDialog.LocationPermissionController$showFallbackAlertDialog$1(currentActivity));
        }
    }
    
    public void onAccept() {
        this.onResponse(OneSignal$PromptActionResult.PERMISSION_GRANTED);
        LocationController.startGetLocation();
    }
    
    public void onReject(final boolean b) {
        this.onResponse(OneSignal$PromptActionResult.PERMISSION_DENIED);
        if (b) {
            this.showFallbackAlertDialog();
        }
        LocationController.fireFailedComplete();
    }
    
    public final void prompt(final boolean b, final String s) {
        Intrinsics.checkNotNullParameter((Object)s, "androidPermissionString");
        PermissionsActivity.startPrompt(b, "LOCATION", s, (Class)this.getClass());
    }
}
