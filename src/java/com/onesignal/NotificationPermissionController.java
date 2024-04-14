package com.onesignal;

import android.app.Activity;
import kotlin.jvm.internal.Intrinsics;
import java.util.Iterator;
import android.os.Build$VERSION;
import java.util.HashSet;
import java.util.Set;
import kotlin.Metadata;

@Metadata(bv = { 1, 0, 3 }, d1 = { "\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010#\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\n\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0010\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0007H\u0002J\b\u0010\u0011\u001a\u00020\u0007H\u0002J\b\u0010\u0012\u001a\u00020\u000fH\u0016J\u0006\u0010\u0013\u001a\u00020\u000fJ\u0010\u0010\u0014\u001a\u00020\u000f2\u0006\u0010\u0015\u001a\u00020\u0007H\u0016J\u0018\u0010\u0016\u001a\u00020\u000f2\u0006\u0010\u0015\u001a\u00020\u00072\b\u0010\u0017\u001a\u0004\u0018\u00010\nJ\b\u0010\u0018\u001a\u00020\u0007H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u0016\u0010\u000b\u001a\u00020\u00078\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\r¨\u0006\u0019" }, d2 = { "Lcom/onesignal/NotificationPermissionController;", "Lcom/onesignal/PermissionsActivity$PermissionCallback;", "()V", "ANDROID_PERMISSION_STRING", "", "PERMISSION_TYPE", "awaitingForReturnFromSystemSettings", "", "callbacks", "", "Lcom/onesignal/OneSignal$PromptForPushNotificationPermissionResponseHandler;", "supportsNativePrompt", "getSupportsNativePrompt", "()Z", "fireCallBacks", "", "accepted", "notificationsEnabled", "onAccept", "onAppForegrounded", "onReject", "fallbackToSettings", "prompt", "callback", "showFallbackAlertDialog", "onesignal_release" }, k = 1, mv = { 1, 4, 2 })
public final class NotificationPermissionController implements PermissionsActivity$PermissionCallback
{
    private static final String ANDROID_PERMISSION_STRING = "android.permission.POST_NOTIFICATIONS";
    public static final NotificationPermissionController INSTANCE;
    private static final String PERMISSION_TYPE = "NOTIFICATION";
    private static boolean awaitingForReturnFromSystemSettings;
    private static final Set<OneSignal$PromptForPushNotificationPermissionResponseHandler> callbacks;
    private static final boolean supportsNativePrompt;
    
    static {
        final NotificationPermissionController notificationPermissionController = INSTANCE = new NotificationPermissionController();
        callbacks = (Set)new HashSet();
        PermissionsActivity.registerAsCallback("NOTIFICATION", (PermissionsActivity$PermissionCallback)notificationPermissionController);
        supportsNativePrompt = (Build$VERSION.SDK_INT > 32 && OSUtils.getTargetSdkVersion(OneSignal.appContext) > 32);
    }
    
    private NotificationPermissionController() {
    }
    
    private final void fireCallBacks(final boolean b) {
        final Iterator iterator = ((Iterable)NotificationPermissionController.callbacks).iterator();
        while (iterator.hasNext()) {
            ((OneSignal$PromptForPushNotificationPermissionResponseHandler)iterator.next()).response(b);
        }
        NotificationPermissionController.callbacks.clear();
    }
    
    private final boolean notificationsEnabled() {
        return OSUtils.areNotificationsEnabled(OneSignal.appContext);
    }
    
    private final boolean showFallbackAlertDialog() {
        final Activity currentActivity = OneSignal.getCurrentActivity();
        if (currentActivity != null) {
            Intrinsics.checkNotNullExpressionValue((Object)currentActivity, "OneSignal.getCurrentActivity() ?: return false");
            final AlertDialogPrepromptForAndroidSettings instance = AlertDialogPrepromptForAndroidSettings.INSTANCE;
            final String string = currentActivity.getString(R$string.notification_permission_name_for_title);
            Intrinsics.checkNotNullExpressionValue((Object)string, "activity.getString(R.str\u2026ermission_name_for_title)");
            final String string2 = currentActivity.getString(R$string.notification_permission_settings_message);
            Intrinsics.checkNotNullExpressionValue((Object)string2, "activity.getString(R.str\u2026mission_settings_message)");
            instance.show(currentActivity, string, string2, (AlertDialogPrepromptForAndroidSettings$Callback)new NotificationPermissionController$showFallbackAlertDialog.NotificationPermissionController$showFallbackAlertDialog$1(currentActivity));
            return true;
        }
        return false;
    }
    
    public final boolean getSupportsNativePrompt() {
        return NotificationPermissionController.supportsNativePrompt;
    }
    
    public void onAccept() {
        OneSignal.refreshNotificationPermissionState();
        this.fireCallBacks(true);
    }
    
    public final void onAppForegrounded() {
        if (!NotificationPermissionController.awaitingForReturnFromSystemSettings) {
            return;
        }
        NotificationPermissionController.awaitingForReturnFromSystemSettings = false;
        this.fireCallBacks(this.notificationsEnabled());
    }
    
    public void onReject(final boolean b) {
        if (!b || !this.showFallbackAlertDialog()) {
            this.fireCallBacks(false);
        }
    }
    
    public final void prompt(final boolean b, final OneSignal$PromptForPushNotificationPermissionResponseHandler oneSignal$PromptForPushNotificationPermissionResponseHandler) {
        if (oneSignal$PromptForPushNotificationPermissionResponseHandler != null) {
            NotificationPermissionController.callbacks.add((Object)oneSignal$PromptForPushNotificationPermissionResponseHandler);
        }
        if (this.notificationsEnabled()) {
            this.fireCallBacks(true);
            return;
        }
        if (!NotificationPermissionController.supportsNativePrompt) {
            if (b) {
                this.showFallbackAlertDialog();
            }
            else {
                this.fireCallBacks(false);
            }
            return;
        }
        PermissionsActivity.startPrompt(b, "NOTIFICATION", "android.permission.POST_NOTIFICATIONS", (Class)this.getClass());
    }
}
