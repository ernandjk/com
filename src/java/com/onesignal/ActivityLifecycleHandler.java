package com.onesignal;

import android.content.res.Configuration;
import android.view.ViewTreeObserver;
import java.util.Iterator;
import android.view.ViewTreeObserver$OnGlobalLayoutListener;
import java.util.Map$Entry;
import java.util.concurrent.ConcurrentHashMap;
import android.app.Activity;
import java.util.Map;

class ActivityLifecycleHandler implements OSSystemConditionController$OSSystemConditionHandler
{
    private static final String FOCUS_LOST_WORKER_TAG = "FOCUS_LOST_WORKER_TAG";
    private static final int SYNC_AFTER_BG_DELAY_MS = 2000;
    private static final Map<String, ActivityLifecycleHandler.ActivityLifecycleHandler$ActivityAvailableListener> sActivityAvailableListeners;
    private static final Map<String, ActivityLifecycleHandler.ActivityLifecycleHandler$KeyboardListener> sKeyboardListeners;
    private static final Map<String, OSSystemConditionController$OSSystemConditionObserver> sSystemConditionObservers;
    private Activity curActivity;
    private final OSFocusHandler focusHandler;
    private boolean nextResumeIsFirstActivity;
    
    static {
        sActivityAvailableListeners = (Map)new ConcurrentHashMap();
        sSystemConditionObservers = (Map)new ConcurrentHashMap();
        sKeyboardListeners = (Map)new ConcurrentHashMap();
    }
    
    public ActivityLifecycleHandler(final OSFocusHandler focusHandler) {
        this.curActivity = null;
        this.nextResumeIsFirstActivity = false;
        this.focusHandler = focusHandler;
    }
    
    private void handleFocus() {
        final OneSignal$LOG_LEVEL debug = OneSignal$LOG_LEVEL.DEBUG;
        final StringBuilder sb = new StringBuilder("ActivityLifecycleHandler handleFocus, nextResumeIsFirstActivity: ");
        sb.append(this.nextResumeIsFirstActivity);
        OneSignal.onesignalLog(debug, sb.toString());
        if (!this.focusHandler.hasBackgrounded() && !this.nextResumeIsFirstActivity) {
            OneSignal.onesignalLog(OneSignal$LOG_LEVEL.DEBUG, "ActivityLifecycleHandler cancel background lost focus worker");
            this.focusHandler.cancelOnLostFocusWorker("FOCUS_LOST_WORKER_TAG", OneSignal.appContext);
        }
        else {
            OneSignal.onesignalLog(OneSignal$LOG_LEVEL.DEBUG, "ActivityLifecycleHandler reset background state, call app focus");
            this.nextResumeIsFirstActivity = false;
            this.focusHandler.startOnFocusWork();
        }
    }
    
    private void handleLostFocus() {
        OneSignal.onesignalLog(OneSignal$LOG_LEVEL.DEBUG, "ActivityLifecycleHandler Handling lost focus");
        final OSFocusHandler focusHandler = this.focusHandler;
        if (focusHandler != null) {
            if (!focusHandler.hasBackgrounded() || this.focusHandler.hasCompleted()) {
                OneSignal.getFocusTimeController().appStopped();
                this.focusHandler.startOnLostFocusWorker("FOCUS_LOST_WORKER_TAG", 2000L, OneSignal.appContext);
            }
        }
    }
    
    private void logCurActivity() {
        final OneSignal$LOG_LEVEL debug = OneSignal$LOG_LEVEL.DEBUG;
        final StringBuilder sb = new StringBuilder("curActivity is NOW: ");
        String string;
        if (this.curActivity != null) {
            final StringBuilder sb2 = new StringBuilder("");
            sb2.append(this.curActivity.getClass().getName());
            sb2.append(":");
            sb2.append((Object)this.curActivity);
            string = sb2.toString();
        }
        else {
            string = "null";
        }
        sb.append(string);
        OneSignal.Log(debug, sb.toString());
    }
    
    private void logOrientationChange(final int n, final Activity activity) {
        if (n == 2) {
            final OneSignal$LOG_LEVEL debug = OneSignal$LOG_LEVEL.DEBUG;
            final StringBuilder sb = new StringBuilder("Configuration Orientation Change: LANDSCAPE (");
            sb.append(n);
            sb.append(") on activity: ");
            sb.append((Object)activity);
            OneSignal.onesignalLog(debug, sb.toString());
        }
        else if (n == 1) {
            final OneSignal$LOG_LEVEL debug2 = OneSignal$LOG_LEVEL.DEBUG;
            final StringBuilder sb2 = new StringBuilder("Configuration Orientation Change: PORTRAIT (");
            sb2.append(n);
            sb2.append(") on activity: ");
            sb2.append((Object)activity);
            OneSignal.onesignalLog(debug2, sb2.toString());
        }
    }
    
    private void onOrientationChanged(final Activity activity) {
        this.handleLostFocus();
        final Iterator iterator = ActivityLifecycleHandler.sActivityAvailableListeners.entrySet().iterator();
        while (iterator.hasNext()) {
            ((ActivityLifecycleHandler.ActivityLifecycleHandler$ActivityAvailableListener)((Map$Entry)iterator.next()).getValue()).stopped(activity);
        }
        final Iterator iterator2 = ActivityLifecycleHandler.sActivityAvailableListeners.entrySet().iterator();
        while (iterator2.hasNext()) {
            ((ActivityLifecycleHandler.ActivityLifecycleHandler$ActivityAvailableListener)((Map$Entry)iterator2.next()).getValue()).available(this.curActivity);
        }
        final ViewTreeObserver viewTreeObserver = this.curActivity.getWindow().getDecorView().getViewTreeObserver();
        for (final Map$Entry map$Entry : ActivityLifecycleHandler.sSystemConditionObservers.entrySet()) {
            final ActivityLifecycleHandler.ActivityLifecycleHandler$KeyboardListener activityLifecycleHandler$KeyboardListener = new ActivityLifecycleHandler.ActivityLifecycleHandler$KeyboardListener((OSSystemConditionController$OSSystemConditionHandler)this, (OSSystemConditionController$OSSystemConditionObserver)map$Entry.getValue(), (String)map$Entry.getKey(), (ActivityLifecycleHandler$1)null);
            viewTreeObserver.addOnGlobalLayoutListener((ViewTreeObserver$OnGlobalLayoutListener)activityLifecycleHandler$KeyboardListener);
            ActivityLifecycleHandler.sKeyboardListeners.put((Object)map$Entry.getKey(), (Object)activityLifecycleHandler$KeyboardListener);
        }
        this.handleFocus();
    }
    
    void addActivityAvailableListener(final String s, final ActivityLifecycleHandler.ActivityLifecycleHandler$ActivityAvailableListener activityLifecycleHandler$ActivityAvailableListener) {
        ActivityLifecycleHandler.sActivityAvailableListeners.put((Object)s, (Object)activityLifecycleHandler$ActivityAvailableListener);
        final Activity curActivity = this.curActivity;
        if (curActivity != null) {
            activityLifecycleHandler$ActivityAvailableListener.available(curActivity);
        }
    }
    
    void addSystemConditionObserver(final String s, final OSSystemConditionController$OSSystemConditionObserver osSystemConditionController$OSSystemConditionObserver) {
        final Activity curActivity = this.curActivity;
        if (curActivity != null) {
            final ViewTreeObserver viewTreeObserver = curActivity.getWindow().getDecorView().getViewTreeObserver();
            final ActivityLifecycleHandler.ActivityLifecycleHandler$KeyboardListener activityLifecycleHandler$KeyboardListener = new ActivityLifecycleHandler.ActivityLifecycleHandler$KeyboardListener((OSSystemConditionController$OSSystemConditionHandler)this, osSystemConditionController$OSSystemConditionObserver, s, (ActivityLifecycleHandler$1)null);
            viewTreeObserver.addOnGlobalLayoutListener((ViewTreeObserver$OnGlobalLayoutListener)activityLifecycleHandler$KeyboardListener);
            ActivityLifecycleHandler.sKeyboardListeners.put((Object)s, (Object)activityLifecycleHandler$KeyboardListener);
        }
        ActivityLifecycleHandler.sSystemConditionObservers.put((Object)s, (Object)osSystemConditionController$OSSystemConditionObserver);
    }
    
    public Activity getCurActivity() {
        return this.curActivity;
    }
    
    void onActivityCreated(final Activity activity) {
    }
    
    void onActivityDestroyed(final Activity activity) {
        final OneSignal$LOG_LEVEL debug = OneSignal$LOG_LEVEL.DEBUG;
        final StringBuilder sb = new StringBuilder("onActivityDestroyed: ");
        sb.append((Object)activity);
        OneSignal.Log(debug, sb.toString());
        ActivityLifecycleHandler.sKeyboardListeners.clear();
        if (activity == this.curActivity) {
            this.curActivity = null;
            this.handleLostFocus();
        }
        this.logCurActivity();
    }
    
    void onActivityPaused(final Activity activity) {
        final OneSignal$LOG_LEVEL debug = OneSignal$LOG_LEVEL.DEBUG;
        final StringBuilder sb = new StringBuilder("onActivityPaused: ");
        sb.append((Object)activity);
        OneSignal.Log(debug, sb.toString());
        if (activity == this.curActivity) {
            this.curActivity = null;
            this.handleLostFocus();
        }
        this.logCurActivity();
    }
    
    void onActivityResumed(final Activity curActivity) {
        final OneSignal$LOG_LEVEL debug = OneSignal$LOG_LEVEL.DEBUG;
        final StringBuilder sb = new StringBuilder("onActivityResumed: ");
        sb.append((Object)curActivity);
        OneSignal.Log(debug, sb.toString());
        this.setCurActivity(curActivity);
        this.logCurActivity();
        this.handleFocus();
    }
    
    void onActivityStarted(final Activity activity) {
        this.focusHandler.startOnStartFocusWork();
    }
    
    void onActivityStopped(final Activity activity) {
        final OneSignal$LOG_LEVEL debug = OneSignal$LOG_LEVEL.DEBUG;
        final StringBuilder sb = new StringBuilder("onActivityStopped: ");
        sb.append((Object)activity);
        OneSignal.Log(debug, sb.toString());
        if (activity == this.curActivity) {
            this.curActivity = null;
            this.handleLostFocus();
        }
        final Iterator iterator = ActivityLifecycleHandler.sActivityAvailableListeners.entrySet().iterator();
        while (iterator.hasNext()) {
            ((ActivityLifecycleHandler.ActivityLifecycleHandler$ActivityAvailableListener)((Map$Entry)iterator.next()).getValue()).stopped(activity);
        }
        this.logCurActivity();
        if (this.curActivity == null) {
            this.focusHandler.startOnStopFocusWork();
        }
    }
    
    void onConfigurationChanged(final Configuration configuration, final Activity activity) {
        final Activity curActivity = this.curActivity;
        if (curActivity != null && OSUtils.hasConfigChangeFlag(curActivity, 128)) {
            this.logOrientationChange(configuration.orientation, activity);
            this.onOrientationChanged(activity);
        }
    }
    
    void removeActivityAvailableListener(final String s) {
        ActivityLifecycleHandler.sActivityAvailableListeners.remove((Object)s);
    }
    
    public void removeSystemConditionObserver(final String s, final ActivityLifecycleHandler.ActivityLifecycleHandler$KeyboardListener activityLifecycleHandler$KeyboardListener) {
        final Activity curActivity = this.curActivity;
        if (curActivity != null) {
            curActivity.getWindow().getDecorView().getViewTreeObserver().removeOnGlobalLayoutListener((ViewTreeObserver$OnGlobalLayoutListener)activityLifecycleHandler$KeyboardListener);
        }
        ActivityLifecycleHandler.sKeyboardListeners.remove((Object)s);
        ActivityLifecycleHandler.sSystemConditionObservers.remove((Object)s);
    }
    
    public void setCurActivity(final Activity curActivity) {
        this.curActivity = curActivity;
        final Iterator iterator = ActivityLifecycleHandler.sActivityAvailableListeners.entrySet().iterator();
        while (iterator.hasNext()) {
            ((ActivityLifecycleHandler.ActivityLifecycleHandler$ActivityAvailableListener)((Map$Entry)iterator.next()).getValue()).available(this.curActivity);
        }
        try {
            final ViewTreeObserver viewTreeObserver = this.curActivity.getWindow().getDecorView().getViewTreeObserver();
            for (final Map$Entry map$Entry : ActivityLifecycleHandler.sSystemConditionObservers.entrySet()) {
                final ActivityLifecycleHandler.ActivityLifecycleHandler$KeyboardListener activityLifecycleHandler$KeyboardListener = new ActivityLifecycleHandler.ActivityLifecycleHandler$KeyboardListener((OSSystemConditionController$OSSystemConditionHandler)this, (OSSystemConditionController$OSSystemConditionObserver)map$Entry.getValue(), (String)map$Entry.getKey(), (ActivityLifecycleHandler$1)null);
                viewTreeObserver.addOnGlobalLayoutListener((ViewTreeObserver$OnGlobalLayoutListener)activityLifecycleHandler$KeyboardListener);
                ActivityLifecycleHandler.sKeyboardListeners.put((Object)map$Entry.getKey(), (Object)activityLifecycleHandler$KeyboardListener);
            }
        }
        catch (final RuntimeException ex) {
            ex.printStackTrace();
        }
    }
    
    void setNextResumeIsFirstActivity(final boolean nextResumeIsFirstActivity) {
        this.nextResumeIsFirstActivity = nextResumeIsFirstActivity;
    }
}
