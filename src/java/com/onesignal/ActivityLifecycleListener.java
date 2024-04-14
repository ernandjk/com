package com.onesignal;

import android.os.Bundle;
import android.app.Activity;
import android.content.res.Configuration;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.app.Application$ActivityLifecycleCallbacks;

class ActivityLifecycleListener implements Application$ActivityLifecycleCallbacks
{
    private static ActivityLifecycleHandler activityLifecycleHandler;
    private static ComponentCallbacks configuration;
    private static ActivityLifecycleListener instance;
    
    public static ActivityLifecycleHandler getActivityLifecycleHandler() {
        return ActivityLifecycleListener.activityLifecycleHandler;
    }
    
    static void registerActivityLifecycleCallbacks(final Application application) {
        if (ActivityLifecycleListener.instance == null) {
            application.registerActivityLifecycleCallbacks((Application$ActivityLifecycleCallbacks)(ActivityLifecycleListener.instance = new ActivityLifecycleListener()));
        }
        if (ActivityLifecycleListener.activityLifecycleHandler == null) {
            ActivityLifecycleListener.activityLifecycleHandler = new ActivityLifecycleHandler(new OSFocusHandler());
        }
        if (ActivityLifecycleListener.configuration == null) {
            application.registerComponentCallbacks(ActivityLifecycleListener.configuration = (ComponentCallbacks)new ComponentCallbacks() {
                public void onConfigurationChanged(final Configuration configuration) {
                    ActivityLifecycleListener.activityLifecycleHandler.onConfigurationChanged(configuration, ActivityLifecycleListener.activityLifecycleHandler.getCurActivity());
                }
                
                public void onLowMemory() {
                }
            });
        }
    }
    
    public void onActivityCreated(final Activity activity, final Bundle bundle) {
        final ActivityLifecycleHandler activityLifecycleHandler = ActivityLifecycleListener.activityLifecycleHandler;
        if (activityLifecycleHandler != null) {
            activityLifecycleHandler.onActivityCreated(activity);
        }
    }
    
    public void onActivityDestroyed(final Activity activity) {
        final ActivityLifecycleHandler activityLifecycleHandler = ActivityLifecycleListener.activityLifecycleHandler;
        if (activityLifecycleHandler != null) {
            activityLifecycleHandler.onActivityDestroyed(activity);
        }
    }
    
    public void onActivityPaused(final Activity activity) {
        final ActivityLifecycleHandler activityLifecycleHandler = ActivityLifecycleListener.activityLifecycleHandler;
        if (activityLifecycleHandler != null) {
            activityLifecycleHandler.onActivityPaused(activity);
        }
    }
    
    public void onActivityResumed(final Activity activity) {
        final ActivityLifecycleHandler activityLifecycleHandler = ActivityLifecycleListener.activityLifecycleHandler;
        if (activityLifecycleHandler != null) {
            activityLifecycleHandler.onActivityResumed(activity);
        }
    }
    
    public void onActivitySaveInstanceState(final Activity activity, final Bundle bundle) {
    }
    
    public void onActivityStarted(final Activity activity) {
        final ActivityLifecycleHandler activityLifecycleHandler = ActivityLifecycleListener.activityLifecycleHandler;
        if (activityLifecycleHandler != null) {
            activityLifecycleHandler.onActivityStarted(activity);
        }
    }
    
    public void onActivityStopped(final Activity activity) {
        final ActivityLifecycleHandler activityLifecycleHandler = ActivityLifecycleListener.activityLifecycleHandler;
        if (activityLifecycleHandler != null) {
            activityLifecycleHandler.onActivityStopped(activity);
        }
    }
}
