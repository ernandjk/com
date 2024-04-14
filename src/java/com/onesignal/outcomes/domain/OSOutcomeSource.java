package com.onesignal.outcomes.domain;

import org.json.JSONException;
import org.json.JSONObject;
import kotlin.Metadata;

@Metadata(bv = { 1, 0, 3 }, d1 = { "\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\u0018\u00002\u00020\u0001B\u0019\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0003¢\u0006\u0002\u0010\u0005J\u0010\u0010\b\u001a\u00020\u00002\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003J\u0010\u0010\u000b\u001a\u00020\u00002\b\u0010\u0004\u001a\u0004\u0018\u00010\u0003J\u0006\u0010\f\u001a\u00020\rJ\b\u0010\u000e\u001a\u00020\u000fH\u0016R\u001c\u0010\u0002\u001a\u0004\u0018\u00010\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0006\u0010\u0007\"\u0004\b\b\u0010\tR\u001c\u0010\u0004\u001a\u0004\u0018\u00010\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\n\u0010\u0007\"\u0004\b\u000b\u0010\t¨\u0006\u0010" }, d2 = { "Lcom/onesignal/outcomes/domain/OSOutcomeSource;", "", "directBody", "Lcom/onesignal/outcomes/domain/OSOutcomeSourceBody;", "indirectBody", "(Lcom/onesignal/outcomes/domain/OSOutcomeSourceBody;Lcom/onesignal/outcomes/domain/OSOutcomeSourceBody;)V", "getDirectBody", "()Lcom/onesignal/outcomes/domain/OSOutcomeSourceBody;", "setDirectBody", "(Lcom/onesignal/outcomes/domain/OSOutcomeSourceBody;)V", "getIndirectBody", "setIndirectBody", "toJSONObject", "Lorg/json/JSONObject;", "toString", "", "onesignal_release" }, k = 1, mv = { 1, 4, 2 })
public final class OSOutcomeSource
{
    private OSOutcomeSourceBody directBody;
    private OSOutcomeSourceBody indirectBody;
    
    public OSOutcomeSource(final OSOutcomeSourceBody directBody, final OSOutcomeSourceBody indirectBody) {
        this.directBody = directBody;
        this.indirectBody = indirectBody;
    }
    
    public final OSOutcomeSourceBody getDirectBody() {
        return this.directBody;
    }
    
    public final OSOutcomeSourceBody getIndirectBody() {
        return this.indirectBody;
    }
    
    public final OSOutcomeSource setDirectBody(final OSOutcomeSourceBody directBody) {
        final OSOutcomeSource osOutcomeSource = this;
        this.directBody = directBody;
        return this;
    }
    
    public final void setDirectBody(final OSOutcomeSourceBody directBody) {
        this.directBody = directBody;
    }
    
    public final OSOutcomeSource setIndirectBody(final OSOutcomeSourceBody indirectBody) {
        final OSOutcomeSource osOutcomeSource = this;
        this.indirectBody = indirectBody;
        return this;
    }
    
    public final void setIndirectBody(final OSOutcomeSourceBody indirectBody) {
        this.indirectBody = indirectBody;
    }
    
    public final JSONObject toJSONObject() throws JSONException {
        final JSONObject jsonObject = new JSONObject();
        final OSOutcomeSourceBody directBody = this.directBody;
        if (directBody != null) {
            jsonObject.put("direct", (Object)directBody.toJSONObject());
        }
        final OSOutcomeSourceBody indirectBody = this.indirectBody;
        if (indirectBody != null) {
            jsonObject.put("indirect", (Object)indirectBody.toJSONObject());
        }
        return jsonObject;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("OSOutcomeSource{directBody=");
        sb.append((Object)this.directBody);
        sb.append(", indirectBody=");
        sb.append((Object)this.indirectBody);
        sb.append('}');
        return sb.toString();
    }
}
