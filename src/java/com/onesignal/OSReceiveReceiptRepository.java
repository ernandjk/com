package com.onesignal;

import org.json.JSONException;
import org.json.JSONObject;

class OSReceiveReceiptRepository
{
    private static final String APP_ID = "app_id";
    private static final String DEVICE_TYPE = "device_type";
    private static final String PLAYER_ID = "player_id";
    
    void sendReceiveReceipt(final String s, final String s2, final Integer n, final String s3, final OneSignalRestClient.ResponseHandler responseHandler) {
        try {
            final JSONObject put = new JSONObject().put("app_id", (Object)s).put("player_id", (Object)s2);
            if (n != null) {
                put.put("device_type", (Object)n);
            }
            final StringBuilder sb = new StringBuilder("notifications/");
            sb.append(s3);
            sb.append("/report_received");
            OneSignalRestClient.put(sb.toString(), put, responseHandler);
        }
        catch (final JSONException ex) {
            OneSignal.Log(OneSignal.LOG_LEVEL.ERROR, "Generating direct receive receipt:JSON Failed.", (Throwable)ex);
        }
    }
}
