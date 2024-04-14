package com.onesignal;

import org.json.JSONObject;

public class OSSubscriptionStateChanges
{
    private OSSubscriptionState from;
    private OSSubscriptionState to;
    
    public OSSubscriptionStateChanges(final OSSubscriptionState from, final OSSubscriptionState to) {
        this.from = from;
        this.to = to;
    }
    
    public OSSubscriptionState getFrom() {
        return this.from;
    }
    
    public OSSubscriptionState getTo() {
        return this.to;
    }
    
    public JSONObject toJSONObject() {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("from", (Object)this.from.toJSONObject());
            jsonObject.put("to", (Object)this.to.toJSONObject());
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
