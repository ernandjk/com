package com.onesignal.outcomes.domain;

import org.json.JSONException;
import org.json.JSONObject;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.Metadata;

@Metadata(bv = { 1, 0, 3 }, d1 = { "\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\t\n\u0002\b\u000e\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B)\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\t¢\u0006\u0002\u0010\nJ\u0006\u0010\u0017\u001a\u00020\u0018J\u0006\u0010\u0019\u001a\u00020\u001aJ\b\u0010\u001b\u001a\u00020\u0003H\u0016R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0005¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u001a\u0010\b\u001a\u00020\tX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000f\u0010\u0010\"\u0004\b\u0011\u0010\u0012R\u001a\u0010\u0006\u001a\u00020\u0007X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0013\u0010\u0014\"\u0004\b\u0015\u0010\u0016¨\u0006\u001c" }, d2 = { "Lcom/onesignal/outcomes/domain/OSOutcomeEventParams;", "", "outcomeId", "", "outcomeSource", "Lcom/onesignal/outcomes/domain/OSOutcomeSource;", "weight", "", "timestamp", "", "(Ljava/lang/String;Lcom/onesignal/outcomes/domain/OSOutcomeSource;FJ)V", "getOutcomeId", "()Ljava/lang/String;", "getOutcomeSource", "()Lcom/onesignal/outcomes/domain/OSOutcomeSource;", "getTimestamp", "()J", "setTimestamp", "(J)V", "getWeight", "()F", "setWeight", "(F)V", "isUnattributed", "", "toJSONObject", "Lorg/json/JSONObject;", "toString", "onesignal_release" }, k = 1, mv = { 1, 4, 2 })
public final class OSOutcomeEventParams
{
    private final String outcomeId;
    private final OSOutcomeSource outcomeSource;
    private long timestamp;
    private float weight;
    
    public OSOutcomeEventParams(final String outcomeId, final OSOutcomeSource outcomeSource, final float weight, final long timestamp) {
        Intrinsics.checkNotNullParameter((Object)outcomeId, "outcomeId");
        this.outcomeId = outcomeId;
        this.outcomeSource = outcomeSource;
        this.weight = weight;
        this.timestamp = timestamp;
    }
    
    public final String getOutcomeId() {
        return this.outcomeId;
    }
    
    public final OSOutcomeSource getOutcomeSource() {
        return this.outcomeSource;
    }
    
    public final long getTimestamp() {
        return this.timestamp;
    }
    
    public final float getWeight() {
        return this.weight;
    }
    
    public final boolean isUnattributed() {
        final OSOutcomeSource outcomeSource = this.outcomeSource;
        return outcomeSource == null || (outcomeSource.getDirectBody() == null && this.outcomeSource.getIndirectBody() == null);
    }
    
    public final void setTimestamp(final long timestamp) {
        this.timestamp = timestamp;
    }
    
    public final void setWeight(final float weight) {
        this.weight = weight;
    }
    
    public final JSONObject toJSONObject() throws JSONException {
        final JSONObject put = new JSONObject().put("id", (Object)this.outcomeId);
        final OSOutcomeSource outcomeSource = this.outcomeSource;
        if (outcomeSource != null) {
            put.put("sources", (Object)outcomeSource.toJSONObject());
        }
        final float weight = this.weight;
        if (weight > 0) {
            put.put("weight", (Object)weight);
        }
        final long timestamp = this.timestamp;
        if (timestamp > 0L) {
            put.put("timestamp", timestamp);
        }
        Intrinsics.checkNotNullExpressionValue((Object)put, "json");
        return put;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("OSOutcomeEventParams{outcomeId='");
        sb.append(this.outcomeId);
        sb.append("', outcomeSource=");
        sb.append((Object)this.outcomeSource);
        sb.append(", weight=");
        sb.append(this.weight);
        sb.append(", timestamp=");
        sb.append(this.timestamp);
        sb.append('}');
        return sb.toString();
    }
}
