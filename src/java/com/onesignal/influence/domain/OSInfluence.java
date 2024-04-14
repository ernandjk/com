package com.onesignal.influence.domain;

import org.json.JSONException;
import org.json.JSONObject;
import kotlin.jvm.internal.Intrinsics;
import org.json.JSONArray;
import kotlin.Metadata;

@Metadata(bv = { 1, 0, 3 }, d1 = { "\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0011\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u000f\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004B!\b\u0016\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\b\u0010\t\u001a\u0004\u0018\u00010\n¢\u0006\u0002\u0010\u000bJ\u0006\u0010\u001a\u001a\u00020\u0000J\u0013\u0010\u001b\u001a\u00020\u001c2\b\u0010\u001d\u001a\u0004\u0018\u00010\u0001H\u0096\u0002J\b\u0010\u001e\u001a\u00020\u001fH\u0016J\u0006\u0010 \u001a\u00020\u0003J\b\u0010!\u001a\u00020\u0003H\u0016R\u0013\u0010\f\u001a\u0004\u0018\u00010\u00038F¢\u0006\u0006\u001a\u0004\b\r\u0010\u000eR\u001c\u0010\t\u001a\u0004\u0018\u00010\nX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000f\u0010\u0010\"\u0004\b\u0011\u0010\u0012R\u001e\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0013\u001a\u00020\u0006@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u001a\u0010\u0007\u001a\u00020\bX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0016\u0010\u0017\"\u0004\b\u0018\u0010\u0019¨\u0006\"" }, d2 = { "Lcom/onesignal/influence/domain/OSInfluence;", "", "jsonString", "", "(Ljava/lang/String;)V", "influenceChannel", "Lcom/onesignal/influence/domain/OSInfluenceChannel;", "influenceType", "Lcom/onesignal/influence/domain/OSInfluenceType;", "ids", "Lorg/json/JSONArray;", "(Lcom/onesignal/influence/domain/OSInfluenceChannel;Lcom/onesignal/influence/domain/OSInfluenceType;Lorg/json/JSONArray;)V", "directId", "getDirectId", "()Ljava/lang/String;", "getIds", "()Lorg/json/JSONArray;", "setIds", "(Lorg/json/JSONArray;)V", "<set-?>", "getInfluenceChannel", "()Lcom/onesignal/influence/domain/OSInfluenceChannel;", "getInfluenceType", "()Lcom/onesignal/influence/domain/OSInfluenceType;", "setInfluenceType", "(Lcom/onesignal/influence/domain/OSInfluenceType;)V", "copy", "equals", "", "o", "hashCode", "", "toJSONString", "toString", "onesignal_release" }, k = 1, mv = { 1, 4, 2 })
public final class OSInfluence
{
    private JSONArray ids;
    private OSInfluenceChannel influenceChannel;
    private OSInfluenceType influenceType;
    
    public OSInfluence(final OSInfluenceChannel influenceChannel, final OSInfluenceType influenceType, final JSONArray ids) {
        Intrinsics.checkNotNullParameter((Object)influenceChannel, "influenceChannel");
        Intrinsics.checkNotNullParameter((Object)influenceType, "influenceType");
        this.influenceChannel = influenceChannel;
        this.influenceType = influenceType;
        this.ids = ids;
    }
    
    public OSInfluence(String string) throws JSONException {
        Intrinsics.checkNotNullParameter((Object)string, "jsonString");
        final JSONObject jsonObject = new JSONObject(string);
        final String string2 = jsonObject.getString("influence_channel");
        string = jsonObject.getString("influence_type");
        final String string3 = jsonObject.getString("influence_ids");
        this.influenceChannel = OSInfluenceChannel.Companion.fromString(string2);
        this.influenceType = OSInfluenceType.Companion.fromString(string);
        Intrinsics.checkNotNullExpressionValue((Object)string3, "ids");
        JSONArray ids;
        if (((CharSequence)string3).length() == 0) {
            ids = null;
        }
        else {
            ids = new JSONArray(string3);
        }
        this.ids = ids;
    }
    
    public final OSInfluence copy() {
        return new OSInfluence(this.influenceChannel, this.influenceType, this.ids);
    }
    
    @Override
    public boolean equals(final Object o) {
        final OSInfluence osInfluence = this;
        boolean b = true;
        if (this == o) {
            return true;
        }
        if (o != null && !(Intrinsics.areEqual((Object)this.getClass(), (Object)o.getClass()) ^ true)) {
            final OSInfluence osInfluence2 = (OSInfluence)o;
            if (this.influenceChannel != osInfluence2.influenceChannel || this.influenceType != osInfluence2.influenceType) {
                b = false;
            }
            return b;
        }
        return false;
    }
    
    public final String getDirectId() throws JSONException {
        final JSONArray ids = this.ids;
        String string = null;
        if (ids != null) {
            string = string;
            if (ids.length() > 0) {
                string = ids.getString(0);
            }
        }
        return string;
    }
    
    public final JSONArray getIds() {
        return this.ids;
    }
    
    public final OSInfluenceChannel getInfluenceChannel() {
        return this.influenceChannel;
    }
    
    public final OSInfluenceType getInfluenceType() {
        return this.influenceType;
    }
    
    @Override
    public int hashCode() {
        return this.influenceChannel.hashCode() * 31 + this.influenceType.hashCode();
    }
    
    public final void setIds(final JSONArray ids) {
        this.ids = ids;
    }
    
    public final void setInfluenceType(final OSInfluenceType influenceType) {
        Intrinsics.checkNotNullParameter((Object)influenceType, "<set-?>");
        this.influenceType = influenceType;
    }
    
    public final String toJSONString() throws JSONException {
        final JSONObject put = new JSONObject().put("influence_channel", (Object)this.influenceChannel.toString()).put("influence_type", (Object)this.influenceType.toString());
        final JSONArray ids = this.ids;
        String value;
        if (ids != null) {
            value = String.valueOf((Object)ids);
        }
        else {
            value = "";
        }
        final String string = put.put("influence_ids", (Object)value).toString();
        Intrinsics.checkNotNullExpressionValue((Object)string, "JSONObject()\n        .pu\u2026e \"\")\n        .toString()");
        return string;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SessionInfluence{influenceChannel=");
        sb.append((Object)this.influenceChannel);
        sb.append(", influenceType=");
        sb.append((Object)this.influenceType);
        sb.append(", ids=");
        sb.append((Object)this.ids);
        sb.append('}');
        return sb.toString();
    }
}
