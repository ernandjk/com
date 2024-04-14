package com.onesignal;

import org.json.JSONException;
import org.json.JSONObject;
import com.onesignal.outcomes.domain.OSOutcomeSource;
import com.onesignal.outcomes.domain.OSOutcomeEventParams;
import com.onesignal.influence.domain.OSInfluenceType;
import org.json.JSONArray;

public class OSOutcomeEvent
{
    private static final String NOTIFICATION_IDS = "notification_ids";
    private static final String OUTCOME_ID = "id";
    private static final String SESSION = "session";
    private static final String TIMESTAMP = "timestamp";
    private static final String WEIGHT = "weight";
    private String name;
    private JSONArray notificationIds;
    private OSInfluenceType session;
    private long timestamp;
    private Float weight;
    
    public OSOutcomeEvent(final OSInfluenceType session, final JSONArray notificationIds, final String name, final long timestamp, final float n) {
        this.session = session;
        this.notificationIds = notificationIds;
        this.name = name;
        this.timestamp = timestamp;
        this.weight = n;
    }
    
    public static OSOutcomeEvent fromOutcomeEventParamsV2toOutcomeEventV1(final OSOutcomeEventParams osOutcomeEventParams) {
        OSInfluenceType osInfluenceType = OSInfluenceType.UNATTRIBUTED;
        if (osOutcomeEventParams.getOutcomeSource() != null) {
            final OSOutcomeSource outcomeSource = osOutcomeEventParams.getOutcomeSource();
            if (outcomeSource.getDirectBody() != null && outcomeSource.getDirectBody().getNotificationIds() != null && outcomeSource.getDirectBody().getNotificationIds().length() > 0) {
                osInfluenceType = OSInfluenceType.DIRECT;
                final JSONArray jsonArray = outcomeSource.getDirectBody().getNotificationIds();
                return new OSOutcomeEvent(osInfluenceType, jsonArray, osOutcomeEventParams.getOutcomeId(), osOutcomeEventParams.getTimestamp(), osOutcomeEventParams.getWeight());
            }
            if (outcomeSource.getIndirectBody() != null && outcomeSource.getIndirectBody().getNotificationIds() != null && outcomeSource.getIndirectBody().getNotificationIds().length() > 0) {
                osInfluenceType = OSInfluenceType.INDIRECT;
                final JSONArray jsonArray = outcomeSource.getIndirectBody().getNotificationIds();
                return new OSOutcomeEvent(osInfluenceType, jsonArray, osOutcomeEventParams.getOutcomeId(), osOutcomeEventParams.getTimestamp(), osOutcomeEventParams.getWeight());
            }
        }
        final JSONArray jsonArray = null;
        return new OSOutcomeEvent(osInfluenceType, jsonArray, osOutcomeEventParams.getOutcomeId(), osOutcomeEventParams.getTimestamp(), osOutcomeEventParams.getWeight());
    }
    
    @Override
    public boolean equals(final Object o) {
        boolean b = true;
        if (this == o) {
            return true;
        }
        if (o != null && this.getClass() == o.getClass()) {
            final OSOutcomeEvent osOutcomeEvent = (OSOutcomeEvent)o;
            if (!this.session.equals((Object)osOutcomeEvent.session) || !this.notificationIds.equals((Object)osOutcomeEvent.notificationIds) || !this.name.equals((Object)osOutcomeEvent.name) || this.timestamp != osOutcomeEvent.timestamp || !this.weight.equals((Object)osOutcomeEvent.weight)) {
                b = false;
            }
            return b;
        }
        return false;
    }
    
    public String getName() {
        return this.name;
    }
    
    public JSONArray getNotificationIds() {
        return this.notificationIds;
    }
    
    public OSInfluenceType getSession() {
        return this.session;
    }
    
    public long getTimestamp() {
        return this.timestamp;
    }
    
    public float getWeight() {
        return this.weight;
    }
    
    @Override
    public int hashCode() {
        final OSInfluenceType session = this.session;
        final JSONArray notificationIds = this.notificationIds;
        int n = 1;
        final String name = this.name;
        final long timestamp = this.timestamp;
        final Float weight = this.weight;
        for (int i = 0; i < 5; ++i) {
            final Object o = (new Object[] { session, notificationIds, name, timestamp, weight })[i];
            int hashCode;
            if (o == null) {
                hashCode = 0;
            }
            else {
                hashCode = o.hashCode();
            }
            n = n * 31 + hashCode;
        }
        return n;
    }
    
    public JSONObject toJSONObject() throws JSONException {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("session", (Object)this.session);
        jsonObject.put("notification_ids", (Object)this.notificationIds);
        jsonObject.put("id", (Object)this.name);
        jsonObject.put("timestamp", this.timestamp);
        jsonObject.put("weight", (Object)this.weight);
        return jsonObject;
    }
    
    public JSONObject toJSONObjectForMeasure() throws JSONException {
        final JSONObject jsonObject = new JSONObject();
        final JSONArray notificationIds = this.notificationIds;
        if (notificationIds != null && notificationIds.length() > 0) {
            jsonObject.put("notification_ids", (Object)this.notificationIds);
        }
        jsonObject.put("id", (Object)this.name);
        if (this.weight > 0.0f) {
            jsonObject.put("weight", (Object)this.weight);
        }
        final long timestamp = this.timestamp;
        if (timestamp > 0L) {
            jsonObject.put("timestamp", timestamp);
        }
        return jsonObject;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("OutcomeEvent{session=");
        sb.append((Object)this.session);
        sb.append(", notificationIds=");
        sb.append((Object)this.notificationIds);
        sb.append(", name='");
        sb.append(this.name);
        sb.append("', timestamp=");
        sb.append(this.timestamp);
        sb.append(", weight=");
        sb.append((Object)this.weight);
        sb.append('}');
        return sb.toString();
    }
}
