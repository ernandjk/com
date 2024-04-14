package com.onesignal;

import org.json.JSONException;
import org.json.JSONObject;

class OSInAppMessageRedisplayStats
{
    private static final String DISPLAY_DELAY = "delay";
    private static final String DISPLAY_LIMIT = "limit";
    private long displayDelay;
    private int displayLimit;
    private int displayQuantity;
    private long lastDisplayTime;
    private boolean redisplayEnabled;
    
    OSInAppMessageRedisplayStats() {
        this.lastDisplayTime = -1L;
        this.displayQuantity = 0;
        this.displayLimit = 1;
        this.displayDelay = 0L;
        this.redisplayEnabled = false;
    }
    
    OSInAppMessageRedisplayStats(final int displayQuantity, final long lastDisplayTime) {
        this.displayLimit = 1;
        this.displayDelay = 0L;
        this.redisplayEnabled = false;
        this.displayQuantity = displayQuantity;
        this.lastDisplayTime = lastDisplayTime;
    }
    
    OSInAppMessageRedisplayStats(final JSONObject jsonObject) throws JSONException {
        this.lastDisplayTime = -1L;
        this.displayQuantity = 0;
        this.displayLimit = 1;
        this.displayDelay = 0L;
        this.redisplayEnabled = true;
        final Object value = jsonObject.get("limit");
        final Object value2 = jsonObject.get("delay");
        if (value instanceof Integer) {
            this.displayLimit = (int)value;
        }
        if (value2 instanceof Long) {
            this.displayDelay = (long)value2;
        }
        else if (value2 instanceof Integer) {
            this.displayDelay = (int)value2;
        }
    }
    
    long getDisplayDelay() {
        return this.displayDelay;
    }
    
    int getDisplayLimit() {
        return this.displayLimit;
    }
    
    int getDisplayQuantity() {
        return this.displayQuantity;
    }
    
    long getLastDisplayTime() {
        return this.lastDisplayTime;
    }
    
    void incrementDisplayQuantity() {
        ++this.displayQuantity;
    }
    
    boolean isDelayTimeSatisfied() {
        final long lastDisplayTime = this.lastDisplayTime;
        boolean b = true;
        if (lastDisplayTime < 0L) {
            return true;
        }
        final long n = OneSignal.getTime().getCurrentTimeMillis() / 1000L;
        final long n2 = n - this.lastDisplayTime;
        final OneSignal.LOG_LEVEL debug = OneSignal.LOG_LEVEL.DEBUG;
        final StringBuilder sb = new StringBuilder("OSInAppMessage lastDisplayTime: ");
        sb.append(this.lastDisplayTime);
        sb.append(" currentTimeInSeconds: ");
        sb.append(n);
        sb.append(" diffInSeconds: ");
        sb.append(n2);
        sb.append(" displayDelay: ");
        sb.append(this.displayDelay);
        OneSignal.Log(debug, sb.toString());
        if (n2 < this.displayDelay) {
            b = false;
        }
        return b;
    }
    
    public boolean isRedisplayEnabled() {
        return this.redisplayEnabled;
    }
    
    void setDisplayDelay(final long displayDelay) {
        this.displayDelay = displayDelay;
    }
    
    void setDisplayLimit(final int displayLimit) {
        this.displayLimit = displayLimit;
    }
    
    void setDisplayQuantity(final int displayQuantity) {
        this.displayQuantity = displayQuantity;
    }
    
    void setDisplayStats(final OSInAppMessageRedisplayStats osInAppMessageRedisplayStats) {
        this.setLastDisplayTime(osInAppMessageRedisplayStats.getLastDisplayTime());
        this.setDisplayQuantity(osInAppMessageRedisplayStats.getDisplayQuantity());
    }
    
    void setLastDisplayTime(final long lastDisplayTime) {
        this.lastDisplayTime = lastDisplayTime;
    }
    
    boolean shouldDisplayAgain() {
        final boolean b = this.displayQuantity < this.displayLimit;
        final OneSignal.LOG_LEVEL debug = OneSignal.LOG_LEVEL.DEBUG;
        final StringBuilder sb = new StringBuilder("OSInAppMessage shouldDisplayAgain: ");
        sb.append(b);
        OneSignal.Log(debug, sb.toString());
        return b;
    }
    
    JSONObject toJSONObject() {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("limit", this.displayLimit);
            jsonObject.put("delay", this.displayDelay);
        }
        catch (final JSONException ex) {
            ex.printStackTrace();
        }
        return jsonObject;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("OSInAppMessageDisplayStats{lastDisplayTime=");
        sb.append(this.lastDisplayTime);
        sb.append(", displayQuantity=");
        sb.append(this.displayQuantity);
        sb.append(", displayLimit=");
        sb.append(this.displayLimit);
        sb.append(", displayDelay=");
        sb.append(this.displayDelay);
        sb.append('}');
        return sb.toString();
    }
}
