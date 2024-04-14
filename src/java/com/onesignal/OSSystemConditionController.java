package com.onesignal;

import android.app.Activity;
import java.lang.ref.WeakReference;
import java.util.List;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager$FragmentLifecycleCallbacks;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;

class OSSystemConditionController
{
    private static final String TAG = "com.onesignal.OSSystemConditionController";
    private final OSSystemConditionObserver systemConditionObserver;
    
    OSSystemConditionController(final OSSystemConditionObserver systemConditionObserver) {
        this.systemConditionObserver = systemConditionObserver;
    }
    
    boolean isDialogFragmentShowing(final Context context) throws NoClassDefFoundError {
        final boolean b = context instanceof AppCompatActivity;
        boolean b3;
        final boolean b2 = b3 = false;
        if (b) {
            final FragmentManager supportFragmentManager = ((AppCompatActivity)context).getSupportFragmentManager();
            supportFragmentManager.registerFragmentLifecycleCallbacks((FragmentManager$FragmentLifecycleCallbacks)new OSSystemConditionController$1(this, supportFragmentManager), true);
            final List fragments = supportFragmentManager.getFragments();
            final int size = fragments.size();
            b3 = b2;
            if (size > 0) {
                final Fragment fragment = (Fragment)fragments.get(size - 1);
                b3 = b2;
                if (fragment.isVisible()) {
                    b3 = b2;
                    if (fragment instanceof DialogFragment) {
                        b3 = true;
                    }
                }
            }
        }
        return b3;
    }
    
    boolean systemConditionsAvailable() {
        if (OneSignal.getCurrentActivity() == null) {
            OneSignal.onesignalLog(OneSignal.LOG_LEVEL.WARN, "OSSystemConditionObserver curActivity null");
            return false;
        }
        try {
            if (this.isDialogFragmentShowing((Context)OneSignal.getCurrentActivity())) {
                OneSignal.onesignalLog(OneSignal.LOG_LEVEL.WARN, "OSSystemConditionObserver dialog fragment detected");
                return false;
            }
        }
        catch (final NoClassDefFoundError noClassDefFoundError) {
            final OneSignal.LOG_LEVEL info = OneSignal.LOG_LEVEL.INFO;
            final StringBuilder sb = new StringBuilder("AppCompatActivity is not used in this app, skipping 'isDialogFragmentShowing' check: ");
            sb.append((Object)noClassDefFoundError);
            OneSignal.onesignalLog(info, sb.toString());
        }
        final ActivityLifecycleHandler activityLifecycleHandler = ActivityLifecycleListener.getActivityLifecycleHandler();
        final boolean keyboardUp = OSViewUtils.isKeyboardUp((WeakReference<Activity>)new WeakReference((Object)OneSignal.getCurrentActivity()));
        if (keyboardUp && activityLifecycleHandler != null) {
            activityLifecycleHandler.addSystemConditionObserver(OSSystemConditionController.TAG, this.systemConditionObserver);
            OneSignal.onesignalLog(OneSignal.LOG_LEVEL.WARN, "OSSystemConditionObserver keyboard up detected");
        }
        return keyboardUp ^ true;
    }
    
    interface OSSystemConditionHandler
    {
        void removeSystemConditionObserver(final String p0, final ActivityLifecycleHandler.KeyboardListener p1);
    }
    
    interface OSSystemConditionObserver
    {
        void systemConditionChanged();
    }
}
