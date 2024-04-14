package com.onesignal;

import org.json.JSONException;
import org.json.JSONObject;

public class OSInAppMessage
{
    public static final String IAM_ID = "messageId";
    protected String messageId;
    
    OSInAppMessage(final String messageId) {
        this.messageId = messageId;
    }
    
    public String getMessageId() {
        return this.messageId;
    }
    
    public JSONObject toJSONObject() {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("messageId", (Object)this.messageId);
        }
        catch (final JSONException ex) {
            ex.printStackTrace();
        }
        return jsonObject;
    }
}
