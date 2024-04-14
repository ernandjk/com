package com.onesignal;

import org.json.JSONObject;

public class OSNotificationAction
{
    private final String actionId;
    private final ActionType type;
    
    public OSNotificationAction(final ActionType type, final String actionId) {
        this.type = type;
        this.actionId = actionId;
    }
    
    public String getActionId() {
        return this.actionId;
    }
    
    public ActionType getType() {
        return this.type;
    }
    
    public JSONObject toJSONObject() {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type", this.type.ordinal());
            jsonObject.put("actionId", (Object)this.actionId);
        }
        finally {
            final Throwable t;
            t.printStackTrace();
        }
        return jsonObject;
    }
    
    public enum ActionType
    {
        private static final ActionType[] $VALUES;
        
        ActionTaken, 
        Opened;
    }
}
