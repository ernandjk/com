package com.onesignal;

import android.os.Bundle;
import org.json.JSONException;
import org.json.JSONObject;
import com.huawei.hms.push.RemoteMessage;
import android.content.Context;
import java.util.concurrent.atomic.AtomicBoolean;

public class OneSignalHmsEventBridge
{
    public static final String HMS_SENT_TIME_KEY = "hms.sent_time";
    public static final String HMS_TTL_KEY = "hms.ttl";
    private static final AtomicBoolean firstToken;
    
    static {
        firstToken = new AtomicBoolean(true);
    }
    
    public static void onMessageReceived(final Context context, final RemoteMessage remoteMessage) {
        final String data = remoteMessage.getData();
        String string;
        try {
            final JSONObject jsonObject = new JSONObject(remoteMessage.getData());
            if (remoteMessage.getTtl() == 0) {
                jsonObject.put("hms.ttl", 259200);
            }
            else {
                jsonObject.put("hms.ttl", remoteMessage.getTtl());
            }
            if (remoteMessage.getSentTime() == 0L) {
                jsonObject.put("hms.sent_time", OneSignal.getTime().getCurrentTimeMillis());
            }
            else {
                jsonObject.put("hms.sent_time", remoteMessage.getSentTime());
            }
            string = jsonObject.toString();
        }
        catch (final JSONException ex) {
            OneSignal.Log(OneSignal.LOG_LEVEL.ERROR, "OneSignalHmsEventBridge error when trying to create RemoteMessage data JSON");
            string = data;
        }
        NotificationPayloadProcessorHMS.processDataMessageReceived(context, string);
    }
    
    @Deprecated
    public static void onNewToken(final Context context, final String s) {
        onNewToken(context, s, null);
    }
    
    public static void onNewToken(final Context context, final String s, final Bundle bundle) {
        if (OneSignalHmsEventBridge.firstToken.compareAndSet(true, false)) {
            final OneSignal.LOG_LEVEL info = OneSignal.LOG_LEVEL.INFO;
            final StringBuilder sb = new StringBuilder("OneSignalHmsEventBridge onNewToken - HMS token: ");
            sb.append(s);
            sb.append(" Bundle: ");
            sb.append((Object)bundle);
            OneSignal.Log(info, sb.toString());
            PushRegistratorHMS.fireCallback(s);
        }
        else {
            final OneSignal.LOG_LEVEL info2 = OneSignal.LOG_LEVEL.INFO;
            final StringBuilder sb2 = new StringBuilder("OneSignalHmsEventBridge ignoring onNewToken - HMS token: ");
            sb2.append(s);
            sb2.append(" Bundle: ");
            sb2.append((Object)bundle);
            OneSignal.Log(info2, sb2.toString());
        }
    }
}
