package com.onesignal;

import org.json.JSONException;
import org.json.JSONObject;

public class OSInAppMessageOutcome
{
    private static final String OUTCOME_NAME = "name";
    private static final String OUTCOME_UNIQUE = "unique";
    private static final String OUTCOME_WEIGHT = "weight";
    private String name;
    private boolean unique;
    private float weight;
    
    OSInAppMessageOutcome(final JSONObject jsonObject) throws JSONException {
        this.name = jsonObject.getString("name");
        float weight;
        if (jsonObject.has("weight")) {
            weight = (float)jsonObject.getDouble("weight");
        }
        else {
            weight = 0.0f;
        }
        this.weight = weight;
        this.unique = (jsonObject.has("unique") && jsonObject.getBoolean("unique"));
    }
    
    public String getName() {
        return this.name;
    }
    
    public float getWeight() {
        return this.weight;
    }
    
    public boolean isUnique() {
        return this.unique;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public void setUnique(final boolean unique) {
        this.unique = unique;
    }
    
    public void setWeight(final float weight) {
        this.weight = weight;
    }
    
    public JSONObject toJSONObject() {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", (Object)this.name);
            jsonObject.put("weight", (double)this.weight);
            jsonObject.put("unique", this.unique);
        }
        catch (final JSONException ex) {
            ex.printStackTrace();
        }
        return jsonObject;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("OSInAppMessageOutcome{name='");
        sb.append(this.name);
        sb.append("', weight=");
        sb.append(this.weight);
        sb.append(", unique=");
        sb.append(this.unique);
        sb.append('}');
        return sb.toString();
    }
}
