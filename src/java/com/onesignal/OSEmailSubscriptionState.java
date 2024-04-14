package com.onesignal;

import org.json.JSONObject;

public class OSEmailSubscriptionState implements Cloneable
{
    private static final String CHANGED_KEY = "changed";
    private String emailAddress;
    private String emailUserId;
    private OSObservable<Object, OSEmailSubscriptionState> observable;
    
    OSEmailSubscriptionState(final boolean b) {
        this.observable = new OSObservable<Object, OSEmailSubscriptionState>("changed", false);
        if (b) {
            this.emailUserId = OneSignalPrefs.getString(OneSignalPrefs.PREFS_ONESIGNAL, "PREFS_ONESIGNAL_EMAIL_ID_LAST", null);
            this.emailAddress = OneSignalPrefs.getString(OneSignalPrefs.PREFS_ONESIGNAL, "PREFS_ONESIGNAL_EMAIL_ADDRESS_LAST", null);
        }
        else {
            this.emailUserId = OneSignal.getEmailId();
            this.emailAddress = OneSignalStateSynchronizer.getEmailStateSynchronizer().getRegistrationId();
        }
    }
    
    void clearEmailAndId() {
        final boolean b = this.emailUserId != null || this.emailAddress != null;
        this.emailUserId = null;
        this.emailAddress = null;
        if (b) {
            this.observable.notifyChange(this);
        }
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
    
    boolean compare(final OSEmailSubscriptionState osEmailSubscriptionState) {
        String emailUserId = this.emailUserId;
        final String s = "";
        if (emailUserId == null) {
            emailUserId = "";
        }
        String emailUserId2 = osEmailSubscriptionState.emailUserId;
        if (emailUserId2 == null) {
            emailUserId2 = "";
        }
        if (emailUserId.equals((Object)emailUserId2)) {
            String emailAddress = this.emailAddress;
            if (emailAddress == null) {
                emailAddress = "";
            }
            final String emailAddress2 = osEmailSubscriptionState.emailAddress;
            String s2 = s;
            if (emailAddress2 != null) {
                s2 = emailAddress2;
            }
            if (emailAddress.equals((Object)s2)) {
                return false;
            }
        }
        return true;
    }
    
    public String getEmailAddress() {
        return this.emailAddress;
    }
    
    public String getEmailUserId() {
        return this.emailUserId;
    }
    
    public OSObservable<Object, OSEmailSubscriptionState> getObservable() {
        return this.observable;
    }
    
    public boolean isSubscribed() {
        return this.emailUserId != null && this.emailAddress != null;
    }
    
    void persistAsFrom() {
        OneSignalPrefs.saveString(OneSignalPrefs.PREFS_ONESIGNAL, "PREFS_ONESIGNAL_EMAIL_ID_LAST", this.emailUserId);
        OneSignalPrefs.saveString(OneSignalPrefs.PREFS_ONESIGNAL, "PREFS_ONESIGNAL_EMAIL_ADDRESS_LAST", this.emailAddress);
    }
    
    void setEmailAddress(final String emailAddress) {
        final boolean equals = emailAddress.equals((Object)this.emailAddress);
        this.emailAddress = emailAddress;
        if (equals ^ true) {
            this.observable.notifyChange(this);
        }
    }
    
    void setEmailUserId(final String emailUserId) {
        boolean b = true;
        Label_0032: {
            if (emailUserId == null) {
                if (this.emailUserId != null) {
                    break Label_0032;
                }
            }
            else if (!emailUserId.equals((Object)this.emailUserId)) {
                break Label_0032;
            }
            b = false;
        }
        this.emailUserId = emailUserId;
        if (b) {
            this.observable.notifyChange(this);
        }
    }
    
    public JSONObject toJSONObject() {
        final JSONObject jsonObject = new JSONObject();
        try {
            final String emailUserId = this.emailUserId;
            if (emailUserId != null) {
                jsonObject.put("emailUserId", (Object)emailUserId);
            }
            else {
                jsonObject.put("emailUserId", JSONObject.NULL);
            }
            final String emailAddress = this.emailAddress;
            if (emailAddress != null) {
                jsonObject.put("emailAddress", (Object)emailAddress);
            }
            else {
                jsonObject.put("emailAddress", JSONObject.NULL);
            }
            jsonObject.put("isSubscribed", this.isSubscribed());
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
