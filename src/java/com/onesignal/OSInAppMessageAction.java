package com.onesignal;

import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import org.json.JSONObject;
import java.util.List;

public class OSInAppMessageAction
{
    private static final String CLICK_NAME = "click_name";
    private static final String CLICK_URL = "click_url";
    private static final String CLOSE = "close";
    private static final String CLOSES_MESSAGE = "closes_message";
    private static final String FIRST_CLICK = "first_click";
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String OUTCOMES = "outcomes";
    private static final String PAGE_ID = "pageId";
    private static final String PROMPTS = "prompts";
    private static final String TAGS = "tags";
    private static final String URL = "url";
    private static final String URL_TARGET = "url_target";
    private String clickId;
    private String clickName;
    private String clickUrl;
    private boolean closesMessage;
    private boolean firstClick;
    private List<OSInAppMessageOutcome> outcomes;
    private String pageId;
    private List<OSInAppMessagePrompt> prompts;
    private OSInAppMessageTag tags;
    private OSInAppMessageActionUrlType urlTarget;
    
    OSInAppMessageAction(final JSONObject jsonObject) throws JSONException {
        this.outcomes = (List<OSInAppMessageOutcome>)new ArrayList();
        this.prompts = (List<OSInAppMessagePrompt>)new ArrayList();
        this.clickId = jsonObject.optString("id", (String)null);
        this.clickName = jsonObject.optString("name", (String)null);
        this.clickUrl = jsonObject.optString("url", (String)null);
        this.pageId = jsonObject.optString("pageId", (String)null);
        final OSInAppMessageActionUrlType fromString = OSInAppMessageActionUrlType.fromString(jsonObject.optString("url_target", (String)null));
        this.urlTarget = fromString;
        if (fromString == null) {
            this.urlTarget = OSInAppMessageActionUrlType.IN_APP_WEBVIEW;
        }
        this.closesMessage = jsonObject.optBoolean("close", true);
        if (jsonObject.has("outcomes")) {
            this.parseOutcomes(jsonObject);
        }
        if (jsonObject.has("tags")) {
            this.tags = new OSInAppMessageTag(jsonObject.getJSONObject("tags"));
        }
        if (jsonObject.has("prompts")) {
            this.parsePrompts(jsonObject);
        }
    }
    
    private void parseOutcomes(final JSONObject jsonObject) throws JSONException {
        final JSONArray jsonArray = jsonObject.getJSONArray("outcomes");
        for (int i = 0; i < jsonArray.length(); ++i) {
            this.outcomes.add((Object)new OSInAppMessageOutcome((JSONObject)jsonArray.get(i)));
        }
    }
    
    private void parsePrompts(final JSONObject jsonObject) throws JSONException {
        final JSONArray jsonArray = jsonObject.getJSONArray("prompts");
        for (int i = 0; i < jsonArray.length(); ++i) {
            final String string = jsonArray.getString(i);
            string.hashCode();
            if (!string.equals((Object)"push")) {
                if (string.equals((Object)"location")) {
                    this.prompts.add((Object)new OSInAppMessageLocationPrompt());
                }
            }
            else {
                this.prompts.add((Object)new OSInAppMessagePushPrompt());
            }
        }
    }
    
    public boolean doesCloseMessage() {
        return this.closesMessage;
    }
    
    String getClickId() {
        return this.clickId;
    }
    
    public String getClickName() {
        return this.clickName;
    }
    
    public String getClickUrl() {
        return this.clickUrl;
    }
    
    public List<OSInAppMessageOutcome> getOutcomes() {
        return this.outcomes;
    }
    
    String getPageId() {
        return this.pageId;
    }
    
    public List<OSInAppMessagePrompt> getPrompts() {
        return this.prompts;
    }
    
    public OSInAppMessageTag getTags() {
        return this.tags;
    }
    
    public OSInAppMessageActionUrlType getUrlTarget() {
        return this.urlTarget;
    }
    
    public boolean isFirstClick() {
        return this.firstClick;
    }
    
    void setFirstClick(final boolean firstClick) {
        this.firstClick = firstClick;
    }
    
    public JSONObject toJSONObject() {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("click_name", (Object)this.clickName);
            jsonObject.put("click_url", (Object)this.clickUrl);
            jsonObject.put("first_click", this.firstClick);
            jsonObject.put("closes_message", this.closesMessage);
            final JSONArray jsonArray = new JSONArray();
            final Iterator iterator = this.outcomes.iterator();
            while (iterator.hasNext()) {
                jsonArray.put((Object)((OSInAppMessageOutcome)iterator.next()).toJSONObject());
            }
            jsonObject.put("outcomes", (Object)jsonArray);
            final OSInAppMessageTag tags = this.tags;
            if (tags != null) {
                jsonObject.put("tags", (Object)tags.toJSONObject());
            }
        }
        catch (final JSONException ex) {
            ex.printStackTrace();
        }
        return jsonObject;
    }
    
    public enum OSInAppMessageActionUrlType
    {
        private static final OSInAppMessageActionUrlType[] $VALUES;
        
        BROWSER("browser"), 
        IN_APP_WEBVIEW("webview"), 
        REPLACE_CONTENT("replacement");
        
        private String text;
        
        private OSInAppMessageActionUrlType(final String text) {
            this.text = text;
        }
        
        public static OSInAppMessageActionUrlType fromString(final String s) {
            for (final OSInAppMessageActionUrlType osInAppMessageActionUrlType : values()) {
                if (osInAppMessageActionUrlType.text.equalsIgnoreCase(s)) {
                    return osInAppMessageActionUrlType;
                }
            }
            return null;
        }
        
        public JSONObject toJSONObject() {
            final JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("url_type", (Object)this.text);
            }
            catch (final JSONException ex) {
                ex.printStackTrace();
            }
            return jsonObject;
        }
        
        public String toString() {
            return this.text;
        }
    }
}
