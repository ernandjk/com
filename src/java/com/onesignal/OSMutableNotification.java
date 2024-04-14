package com.onesignal;

import androidx.core.app.NotificationCompat$Extender;

public class OSMutableNotification extends OSNotification
{
    OSMutableNotification(final OSNotification osNotification) {
        super(osNotification);
    }
    
    public void setAndroidNotificationId(final int androidNotificationId) {
        super.setAndroidNotificationId(androidNotificationId);
    }
    
    public void setExtender(final NotificationCompat$Extender notificationExtender) {
        this.setNotificationExtender(notificationExtender);
    }
}
