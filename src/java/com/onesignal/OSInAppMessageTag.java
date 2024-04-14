package com.onesignal;

import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;

public class OSInAppMessageTag
{
    private static final String ADD_TAGS = "adds";
    private static final String REMOVE_TAGS = "removes";
    private JSONObject tagsToAdd;
    private JSONArray tagsToRemove;
    
    OSInAppMessageTag(final JSONObject jsonObject) throws JSONException {
        final boolean has = jsonObject.has("adds");
        final JSONArray jsonArray = null;
        JSONObject jsonObject2;
        if (has) {
            jsonObject2 = jsonObject.getJSONObject("adds");
        }
        else {
            jsonObject2 = null;
        }
        this.tagsToAdd = jsonObject2;
        JSONArray jsonArray2 = jsonArray;
        if (jsonObject.has("removes")) {
            jsonArray2 = jsonObject.getJSONArray("removes");
        }
        this.tagsToRemove = jsonArray2;
    }
    
    public JSONObject getTagsToAdd() {
        return this.tagsToAdd;
    }
    
    public JSONArray getTagsToRemove() {
        return this.tagsToRemove;
    }
    
    public void setTagsToAdd(final JSONObject tagsToAdd) {
        this.tagsToAdd = tagsToAdd;
    }
    
    public void setTagsToRemove(final JSONArray tagsToRemove) {
        this.tagsToRemove = tagsToRemove;
    }
    
    public JSONObject toJSONObject() {
        final JSONObject jsonObject = new JSONObject();
        try {
            final JSONObject tagsToAdd = this.tagsToAdd;
            if (tagsToAdd != null) {
                jsonObject.put("adds", (Object)tagsToAdd);
            }
            final JSONArray tagsToRemove = this.tagsToRemove;
            if (tagsToRemove != null) {
                jsonObject.put("removes", (Object)tagsToRemove);
            }
        }
        catch (final JSONException ex) {
            ex.printStackTrace();
        }
        return jsonObject;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("OSInAppMessageTag{adds=");
        sb.append((Object)this.tagsToAdd);
        sb.append(", removes=");
        sb.append((Object)this.tagsToRemove);
        sb.append('}');
        return sb.toString();
    }
}
