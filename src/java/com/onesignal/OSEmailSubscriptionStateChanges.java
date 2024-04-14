package com.onesignal;

import org.json.JSONException;
import org.json.JSONObject;

public class OSEmailSubscriptionStateChanges
{
    private OSEmailSubscriptionState from;
    private OSEmailSubscriptionState to;
    
    public OSEmailSubscriptionStateChanges(final OSEmailSubscriptionState from, final OSEmailSubscriptionState to) {
        this.from = from;
        this.to = to;
    }
    
    public OSEmailSubscriptionState getFrom() {
        return this.from;
    }
    
    public OSEmailSubscriptionState getTo() {
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
