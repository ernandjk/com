package com.onesignal;

import org.json.JSONException;
import org.json.JSONObject;

public class OSSMSSubscriptionStateChanges
{
    private OSSMSSubscriptionState from;
    private OSSMSSubscriptionState to;
    
    public OSSMSSubscriptionStateChanges(final OSSMSSubscriptionState from, final OSSMSSubscriptionState to) {
        this.from = from;
        this.to = to;
    }
    
    public OSSMSSubscriptionState getFrom() {
        return this.from;
    }
    
    public OSSMSSubscriptionState getTo() {
        return this.to;
    }
    
    public JSONObject toJSONObject() {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("from", (Object)this.from.toJSONObject());
            jsonObject.put("to", (Object)this.to.toJSONObject());
        }
        catch (final JSONException ex) {
            ex.printStackTrace();
        }
        return jsonObject;
    }
    
    @Override
    public String toString() {
        return this.toJSONObject().toString();
    }
}
