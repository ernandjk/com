package com.onesignal.outcomes.domain;

import org.json.JSONException;
import kotlin.jvm.internal.Intrinsics;
import org.json.JSONObject;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.json.JSONArray;
import kotlin.Metadata;

@Metadata(bv = { 1, 0, 3 }, d1 = { "\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\u0018\u00002\u00020\u0001B\u001f\b\u0007\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0003¢\u0006\u0002\u0010\u0005J\u0006\u0010\f\u001a\u00020\rJ\b\u0010\u000e\u001a\u00020\u000fH\u0016R\u001c\u0010\u0004\u001a\u0004\u0018\u00010\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0006\u0010\u0007\"\u0004\b\b\u0010\tR\u001c\u0010\u0002\u001a\u0004\u0018\u00010\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\n\u0010\u0007\"\u0004\b\u000b\u0010\t¨\u0006\u0010" }, d2 = { "Lcom/onesignal/outcomes/domain/OSOutcomeSourceBody;", "", "notificationIds", "Lorg/json/JSONArray;", "inAppMessagesIds", "(Lorg/json/JSONArray;Lorg/json/JSONArray;)V", "getInAppMessagesIds", "()Lorg/json/JSONArray;", "setInAppMessagesIds", "(Lorg/json/JSONArray;)V", "getNotificationIds", "setNotificationIds", "toJSONObject", "Lorg/json/JSONObject;", "toString", "", "onesignal_release" }, k = 1, mv = { 1, 4, 2 })
public final class OSOutcomeSourceBody
{
    private JSONArray inAppMessagesIds;
    private JSONArray notificationIds;
    
    public OSOutcomeSourceBody() {
        this(null, null, 3, null);
    }
    
    public OSOutcomeSourceBody(final JSONArray jsonArray) {
        this(jsonArray, null, 2, null);
    }
    
    public OSOutcomeSourceBody(final JSONArray notificationIds, final JSONArray inAppMessagesIds) {
        this.notificationIds = notificationIds;
        this.inAppMessagesIds = inAppMessagesIds;
    }
    
    public final JSONArray getInAppMessagesIds() {
        return this.inAppMessagesIds;
    }
    
    public final JSONArray getNotificationIds() {
        return this.notificationIds;
    }
    
    public final void setInAppMessagesIds(final JSONArray inAppMessagesIds) {
        this.inAppMessagesIds = inAppMessagesIds;
    }
    
    public final void setNotificationIds(final JSONArray notificationIds) {
        this.notificationIds = notificationIds;
    }
    
    public final JSONObject toJSONObject() throws JSONException {
        final JSONObject put = new JSONObject().put("notification_ids", (Object)this.notificationIds).put("in_app_message_ids", (Object)this.inAppMessagesIds);
        Intrinsics.checkNotNullExpressionValue((Object)put, "JSONObject()\n        .pu\u2026AM_IDS, inAppMessagesIds)");
        return put;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("OSOutcomeSourceBody{notificationIds=");
        sb.append((Object)this.notificationIds);
        sb.append(", inAppMessagesIds=");
        sb.append((Object)this.inAppMessagesIds);
        sb.append('}');
        return sb.toString();
    }
}
