package com.onesignal;

import java.security.SecureRandom;
import android.net.Uri;
import org.json.JSONObject;
import android.content.Context;

public class OSNotificationGenerationJob
{
    private Context context;
    private boolean isNotificationToDisplay;
    private JSONObject jsonPayload;
    private OSNotification notification;
    private Integer orgFlags;
    private Uri orgSound;
    private CharSequence overriddenBodyFromExtender;
    private Integer overriddenFlags;
    private Uri overriddenSound;
    private CharSequence overriddenTitleFromExtender;
    private boolean restoring;
    private Long shownTimeStamp;
    
    OSNotificationGenerationJob(final Context context) {
        this.context = context;
    }
    
    OSNotificationGenerationJob(final Context context, final OSNotification notification, final JSONObject jsonPayload) {
        this.context = context;
        this.jsonPayload = jsonPayload;
        this.setNotification(notification);
    }
    
    OSNotificationGenerationJob(final Context context, final JSONObject jsonObject) {
        this(context, new OSNotification(jsonObject), jsonObject);
    }
    
    JSONObject getAdditionalData() {
        JSONObject additionalData;
        if (this.notification.getAdditionalData() != null) {
            additionalData = this.notification.getAdditionalData();
        }
        else {
            additionalData = new JSONObject();
        }
        return additionalData;
    }
    
    Integer getAndroidId() {
        return this.notification.getAndroidNotificationId();
    }
    
    String getApiNotificationId() {
        return OneSignal.getNotificationIdFromFCMJson(this.jsonPayload);
    }
    
    CharSequence getBody() {
        final CharSequence overriddenBodyFromExtender = this.overriddenBodyFromExtender;
        if (overriddenBodyFromExtender != null) {
            return overriddenBodyFromExtender;
        }
        return (CharSequence)this.notification.getBody();
    }
    
    public Context getContext() {
        return this.context;
    }
    
    public JSONObject getJsonPayload() {
        return this.jsonPayload;
    }
    
    public OSNotification getNotification() {
        return this.notification;
    }
    
    public Integer getOrgFlags() {
        return this.orgFlags;
    }
    
    public Uri getOrgSound() {
        return this.orgSound;
    }
    
    public CharSequence getOverriddenBodyFromExtender() {
        return this.overriddenBodyFromExtender;
    }
    
    public Integer getOverriddenFlags() {
        return this.overriddenFlags;
    }
    
    public Uri getOverriddenSound() {
        return this.overriddenSound;
    }
    
    public CharSequence getOverriddenTitleFromExtender() {
        return this.overriddenTitleFromExtender;
    }
    
    public Long getShownTimeStamp() {
        return this.shownTimeStamp;
    }
    
    CharSequence getTitle() {
        final CharSequence overriddenTitleFromExtender = this.overriddenTitleFromExtender;
        if (overriddenTitleFromExtender != null) {
            return overriddenTitleFromExtender;
        }
        return (CharSequence)this.notification.getTitle();
    }
    
    boolean hasExtender() {
        return this.notification.getNotificationExtender() != null;
    }
    
    boolean isNotificationToDisplay() {
        return this.isNotificationToDisplay;
    }
    
    public boolean isRestoring() {
        return this.restoring;
    }
    
    public void setContext(final Context context) {
        this.context = context;
    }
    
    void setIsNotificationToDisplay(final boolean isNotificationToDisplay) {
        this.isNotificationToDisplay = isNotificationToDisplay;
    }
    
    public void setJsonPayload(final JSONObject jsonPayload) {
        this.jsonPayload = jsonPayload;
    }
    
    public void setNotification(final OSNotification notification) {
        if (notification != null && !notification.hasNotificationId()) {
            final OSNotification notification2 = this.notification;
            if (notification2 != null && notification2.hasNotificationId()) {
                notification.setAndroidNotificationId(this.notification.getAndroidNotificationId());
            }
            else {
                notification.setAndroidNotificationId(new SecureRandom().nextInt());
            }
        }
        this.notification = notification;
    }
    
    public void setOrgFlags(final Integer orgFlags) {
        this.orgFlags = orgFlags;
    }
    
    public void setOrgSound(final Uri orgSound) {
        this.orgSound = orgSound;
    }
    
    public void setOverriddenBodyFromExtender(final CharSequence overriddenBodyFromExtender) {
        this.overriddenBodyFromExtender = overriddenBodyFromExtender;
    }
    
    public void setOverriddenFlags(final Integer overriddenFlags) {
        this.overriddenFlags = overriddenFlags;
    }
    
    public void setOverriddenSound(final Uri overriddenSound) {
        this.overriddenSound = overriddenSound;
    }
    
    public void setOverriddenTitleFromExtender(final CharSequence overriddenTitleFromExtender) {
        this.overriddenTitleFromExtender = overriddenTitleFromExtender;
    }
    
    public void setRestoring(final boolean restoring) {
        this.restoring = restoring;
    }
    
    public void setShownTimeStamp(final Long shownTimeStamp) {
        this.shownTimeStamp = shownTimeStamp;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("OSNotificationGenerationJob{jsonPayload=");
        sb.append((Object)this.jsonPayload);
        sb.append(", isRestoring=");
        sb.append(this.restoring);
        sb.append(", isNotificationToDisplay=");
        sb.append(this.isNotificationToDisplay);
        sb.append(", shownTimeStamp=");
        sb.append((Object)this.shownTimeStamp);
        sb.append(", overriddenBodyFromExtender=");
        sb.append((Object)this.overriddenBodyFromExtender);
        sb.append(", overriddenTitleFromExtender=");
        sb.append((Object)this.overriddenTitleFromExtender);
        sb.append(", overriddenSound=");
        sb.append((Object)this.overriddenSound);
        sb.append(", overriddenFlags=");
        sb.append((Object)this.overriddenFlags);
        sb.append(", orgFlags=");
        sb.append((Object)this.orgFlags);
        sb.append(", orgSound=");
        sb.append((Object)this.orgSound);
        sb.append(", notification=");
        sb.append((Object)this.notification);
        sb.append('}');
        return sb.toString();
    }
}
