package com.onesignal;

import org.json.JSONObject;

public class OSPermissionStateChanges
{
    private OSPermissionState from;
    private OSPermissionState to;
    
    public OSPermissionStateChanges(final OSPermissionState from, final OSPermissionState to) {
        this.from = from;
        this.to = to;
    }
    
    public OSPermissionState getFrom() {
        return this.from;
    }
    
    public OSPermissionState getTo() {
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
