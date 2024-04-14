package com.onesignal;

import org.json.JSONObject;

public class OSPermissionState implements Cloneable
{
    private static final String ARE_NOTIFICATION_ENABLED_KEY = "areNotificationsEnabled";
    private static final String CHANGED_KEY = "changed";
    private boolean notificationsEnabled;
    private OSObservable<Object, OSPermissionState> observable;
    
    OSPermissionState(final boolean b) {
        this.observable = new OSObservable<Object, OSPermissionState>("changed", false);
        if (b) {
            this.notificationsEnabled = OneSignalPrefs.getBool(OneSignalPrefs.PREFS_ONESIGNAL, "ONESIGNAL_ACCEPTED_NOTIFICATION_LAST", false);
        }
        else {
            this.refreshAsTo();
        }
    }
    
    private void setNotificationsEnabled(final boolean notificationsEnabled) {
        final boolean b = this.notificationsEnabled != notificationsEnabled;
        this.notificationsEnabled = notificationsEnabled;
        if (b) {
            this.observable.notifyChange(this);
        }
    }
    
    public boolean areNotificationsEnabled() {
        return this.notificationsEnabled;
    }
    
    @Override
    protected Object clone() {
        try {
            return super.clone();
        }
        finally {
            return null;
        }
    }
    
    boolean compare(final OSPermissionState osPermissionState) {
        return this.notificationsEnabled != osPermissionState.notificationsEnabled;
    }
    
    public OSObservable<Object, OSPermissionState> getObservable() {
        return this.observable;
    }
    
    void persistAsFrom() {
        OneSignalPrefs.saveBool(OneSignalPrefs.PREFS_ONESIGNAL, "ONESIGNAL_ACCEPTED_NOTIFICATION_LAST", this.notificationsEnabled);
    }
    
    void refreshAsTo() {
        this.setNotificationsEnabled(OSUtils.areNotificationsEnabled(OneSignal.appContext));
    }
    
    public JSONObject toJSONObject() {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("areNotificationsEnabled", this.notificationsEnabled);
        }
        finally {
            final Throwable t;
            t.printStackTrace();
        }
        return jsonObject;
    }
    
    @Override
    public String toString() {
        return this.toJSONObject().toString();
    }
}
