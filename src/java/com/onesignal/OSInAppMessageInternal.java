package com.onesignal;

import org.json.JSONArray;
import java.util.Iterator;
import java.text.ParseException;
import org.json.JSONException;
import java.util.HashSet;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

class OSInAppMessageInternal extends OSInAppMessage
{
    private static final String DISPLAY_DURATION = "displayDuration";
    private static final String END_TIME = "end_time";
    private static final String HAS_LIQUID = "has_liquid";
    private static final String IAM_ID = "messageId";
    private static final String IAM_REDISPLAY_STATS = "redisplay";
    private static final String IAM_TRIGGERS = "triggers";
    private static final String IAM_VARIANTS = "variants";
    private static final String ID = "id";
    private boolean actionTaken;
    private Set<String> clickedClickIds;
    private double displayDuration;
    private boolean displayedInSession;
    private Date endTime;
    private boolean hasLiquid;
    boolean isPreview;
    private OSInAppMessageRedisplayStats redisplayStats;
    private boolean triggerChanged;
    public ArrayList<ArrayList<OSTrigger>> triggers;
    public HashMap<String, HashMap<String, String>> variants;
    
    OSInAppMessageInternal(final String s, final Set<String> clickedClickIds, final boolean displayedInSession, final OSInAppMessageRedisplayStats redisplayStats) {
        super(s);
        new OSInAppMessageRedisplayStats();
        this.triggerChanged = false;
        this.clickedClickIds = clickedClickIds;
        this.displayedInSession = displayedInSession;
        this.redisplayStats = redisplayStats;
    }
    
    OSInAppMessageInternal(final JSONObject jsonObject) throws JSONException {
        super(jsonObject.getString("id"));
        this.redisplayStats = new OSInAppMessageRedisplayStats();
        this.displayedInSession = false;
        this.triggerChanged = false;
        this.variants = this.parseVariants(jsonObject.getJSONObject("variants"));
        this.triggers = this.parseTriggerJson(jsonObject.getJSONArray("triggers"));
        this.clickedClickIds = (Set<String>)new HashSet();
        this.endTime = this.parseEndTimeJson(jsonObject);
        if (jsonObject.has("has_liquid")) {
            this.hasLiquid = jsonObject.getBoolean("has_liquid");
        }
        if (jsonObject.has("redisplay")) {
            this.redisplayStats = new OSInAppMessageRedisplayStats(jsonObject.getJSONObject("redisplay"));
        }
    }
    
    OSInAppMessageInternal(final boolean isPreview) {
        super("");
        this.redisplayStats = new OSInAppMessageRedisplayStats();
        this.displayedInSession = false;
        this.triggerChanged = false;
        this.isPreview = isPreview;
    }
    
    private Date parseEndTimeJson(final JSONObject jsonObject) {
        try {
            final String string = jsonObject.getString("end_time");
            if (string.equals((Object)"null")) {
                return null;
            }
            try {
                return OneSignalSimpleDateFormat.iso8601Format().parse(string);
            }
            catch (final ParseException ex) {
                ex.printStackTrace();
            }
            return null;
        }
        catch (final JSONException ex2) {
            return null;
        }
    }
    
    private HashMap<String, HashMap<String, String>> parseVariants(final JSONObject jsonObject) throws JSONException {
        final HashMap hashMap = new HashMap();
        final Iterator keys = jsonObject.keys();
        while (keys.hasNext()) {
            final String s = (String)keys.next();
            final JSONObject jsonObject2 = jsonObject.getJSONObject(s);
            final HashMap hashMap2 = new HashMap();
            final Iterator keys2 = jsonObject2.keys();
            while (keys2.hasNext()) {
                final String s2 = (String)keys2.next();
                hashMap2.put((Object)s2, (Object)jsonObject2.getString(s2));
            }
            hashMap.put((Object)s, (Object)hashMap2);
        }
        return (HashMap<String, HashMap<String, String>>)hashMap;
    }
    
    void addClickId(final String s) {
        this.clickedClickIds.add((Object)s);
    }
    
    void clearClickIds() {
        this.clickedClickIds.clear();
    }
    
    public boolean equals(final Object o) {
        return this == o || (o != null && this.getClass() == o.getClass() && this.messageId.equals((Object)((OSInAppMessageInternal)o).messageId));
    }
    
    Set<String> getClickedClickIds() {
        return this.clickedClickIds;
    }
    
    double getDisplayDuration() {
        return this.displayDuration;
    }
    
    boolean getHasLiquid() {
        return this.hasLiquid;
    }
    
    OSInAppMessageRedisplayStats getRedisplayStats() {
        return this.redisplayStats;
    }
    
    public int hashCode() {
        return this.messageId.hashCode();
    }
    
    boolean isClickAvailable(final String s) {
        return this.clickedClickIds.contains((Object)s) ^ true;
    }
    
    public boolean isDisplayedInSession() {
        return this.displayedInSession;
    }
    
    public boolean isFinished() {
        return this.endTime != null && this.endTime.before(new Date());
    }
    
    boolean isTriggerChanged() {
        return this.triggerChanged;
    }
    
    protected ArrayList<ArrayList<OSTrigger>> parseTriggerJson(final JSONArray jsonArray) throws JSONException {
        final ArrayList list = new ArrayList();
        for (int i = 0; i < jsonArray.length(); ++i) {
            final JSONArray jsonArray2 = jsonArray.getJSONArray(i);
            final ArrayList list2 = new ArrayList();
            for (int j = 0; j < jsonArray2.length(); ++j) {
                list2.add((Object)new OSTrigger(jsonArray2.getJSONObject(j)));
            }
            list.add((Object)list2);
        }
        return (ArrayList<ArrayList<OSTrigger>>)list;
    }
    
    void removeClickId(final String s) {
        this.clickedClickIds.remove((Object)s);
    }
    
    void setDisplayDuration(final double displayDuration) {
        this.displayDuration = displayDuration;
    }
    
    public void setDisplayedInSession(final boolean displayedInSession) {
        this.displayedInSession = displayedInSession;
    }
    
    void setHasLiquid(final boolean hasLiquid) {
        this.hasLiquid = hasLiquid;
    }
    
    void setRedisplayStats(final int n, final long n2) {
        this.redisplayStats = new OSInAppMessageRedisplayStats(n, n2);
    }
    
    void setTriggerChanged(final boolean triggerChanged) {
        this.triggerChanged = triggerChanged;
    }
    
    boolean takeActionAsUnique() {
        return !this.actionTaken && (this.actionTaken = true);
    }
    
    public JSONObject toJSONObject() {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("messageId", (Object)this.messageId);
            final JSONObject jsonObject2 = new JSONObject();
            for (final String s : this.variants.keySet()) {
                final HashMap hashMap = (HashMap)this.variants.get((Object)s);
                final JSONObject jsonObject3 = new JSONObject();
                for (final String s2 : hashMap.keySet()) {
                    jsonObject3.put(s2, hashMap.get((Object)s2));
                }
                jsonObject2.put(s, (Object)jsonObject3);
            }
            jsonObject.put("variants", (Object)jsonObject2);
            jsonObject.put("displayDuration", this.displayDuration);
            jsonObject.put("redisplay", (Object)this.redisplayStats.toJSONObject());
            final JSONArray jsonArray = new JSONArray();
            for (final ArrayList list : this.triggers) {
                final JSONArray jsonArray2 = new JSONArray();
                final Iterator iterator4 = list.iterator();
                while (iterator4.hasNext()) {
                    jsonArray2.put((Object)((OSTrigger)iterator4.next()).toJSONObject());
                }
                jsonArray.put((Object)jsonArray2);
            }
            jsonObject.put("triggers", (Object)jsonArray);
            if (this.endTime != null) {
                jsonObject.put("end_time", (Object)OneSignalSimpleDateFormat.iso8601Format().format(this.endTime));
            }
            jsonObject.put("has_liquid", this.hasLiquid);
        }
        catch (final JSONException ex) {
            ex.printStackTrace();
        }
        return jsonObject;
    }
    
    public String toString() {
        final StringBuilder sb = new StringBuilder("OSInAppMessage{messageId='");
        sb.append(this.messageId);
        sb.append("', variants=");
        sb.append((Object)this.variants);
        sb.append(", triggers=");
        sb.append((Object)this.triggers);
        sb.append(", clickedClickIds=");
        sb.append((Object)this.clickedClickIds);
        sb.append(", redisplayStats=");
        sb.append((Object)this.redisplayStats);
        sb.append(", displayDuration=");
        sb.append(this.displayDuration);
        sb.append(", displayedInSession=");
        sb.append(this.displayedInSession);
        sb.append(", triggerChanged=");
        sb.append(this.triggerChanged);
        sb.append(", actionTaken=");
        sb.append(this.actionTaken);
        sb.append(", isPreview=");
        sb.append(this.isPreview);
        sb.append(", endTime=");
        sb.append((Object)this.endTime);
        sb.append(", hasLiquid=");
        sb.append(this.hasLiquid);
        sb.append('}');
        return sb.toString();
    }
}
