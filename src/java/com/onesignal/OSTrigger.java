package com.onesignal;

import org.json.JSONException;
import org.json.JSONObject;

class OSTrigger
{
    public OSTriggerKind kind;
    public OSTriggerOperator operatorType;
    public String property;
    String triggerId;
    public Object value;
    
    OSTrigger(final JSONObject jsonObject) throws JSONException {
        this.triggerId = jsonObject.getString("id");
        this.kind = OSTriggerKind.fromString(jsonObject.getString("kind"));
        this.property = jsonObject.optString("property", (String)null);
        this.operatorType = OSTriggerOperator.fromString(jsonObject.getString("operator"));
        this.value = jsonObject.opt("value");
    }
    
    public JSONObject toJSONObject() {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", (Object)this.triggerId);
            jsonObject.put("kind", (Object)this.kind);
            jsonObject.put("property", (Object)this.property);
            jsonObject.put("operator", (Object)this.operatorType.toString());
            jsonObject.put("value", this.value);
        }
        catch (final JSONException ex) {
            ex.printStackTrace();
        }
        return jsonObject;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("OSTrigger{triggerId='");
        sb.append(this.triggerId);
        sb.append("', kind=");
        sb.append((Object)this.kind);
        sb.append(", property='");
        sb.append(this.property);
        sb.append("', operatorType=");
        sb.append((Object)this.operatorType);
        sb.append(", value=");
        sb.append(this.value);
        sb.append('}');
        return sb.toString();
    }
    
    public enum OSTriggerKind
    {
        private static final OSTriggerKind[] $VALUES;
        
        CUSTOM("custom"), 
        SESSION_TIME("session_time"), 
        TIME_SINCE_LAST_IN_APP("min_time_since"), 
        UNKNOWN("unknown");
        
        private String value;
        
        private OSTriggerKind(final String value) {
            this.value = value;
        }
        
        public static OSTriggerKind fromString(final String s) {
            for (final OSTriggerKind osTriggerKind : values()) {
                if (osTriggerKind.value.equalsIgnoreCase(s)) {
                    return osTriggerKind;
                }
            }
            return OSTriggerKind.UNKNOWN;
        }
        
        public String toString() {
            return this.value;
        }
    }
    
    public enum OSTriggerOperator
    {
        private static final OSTriggerOperator[] $VALUES;
        
        CONTAINS("in"), 
        EQUAL_TO("equal"), 
        EXISTS("exists"), 
        GREATER_THAN("greater"), 
        GREATER_THAN_OR_EQUAL_TO("greater_or_equal"), 
        LESS_THAN("less"), 
        LESS_THAN_OR_EQUAL_TO("less_or_equal"), 
        NOT_EQUAL_TO("not_equal"), 
        NOT_EXISTS("not_exists");
        
        private String text;
        
        private OSTriggerOperator(final String text) {
            this.text = text;
        }
        
        public static OSTriggerOperator fromString(final String s) {
            for (final OSTriggerOperator osTriggerOperator : values()) {
                if (osTriggerOperator.text.equalsIgnoreCase(s)) {
                    return osTriggerOperator;
                }
            }
            return OSTriggerOperator.EQUAL_TO;
        }
        
        public boolean checksEquality() {
            return this == OSTriggerOperator.EQUAL_TO || this == OSTriggerOperator.NOT_EQUAL_TO;
        }
        
        public String toString() {
            return this.text;
        }
    }
}
