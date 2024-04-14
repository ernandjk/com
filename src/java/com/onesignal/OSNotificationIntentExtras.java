package com.onesignal;

import kotlin.jvm.internal.Intrinsics;
import org.json.JSONObject;
import org.json.JSONArray;
import kotlin.Metadata;

@Metadata(bv = { 1, 0, 3 }, d1 = { "\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\r\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u0019\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005¢\u0006\u0002\u0010\u0006J\u000b\u0010\u000f\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u0010\u0010\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J!\u0010\u0011\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005H\u00c6\u0001J\u0013\u0010\u0012\u001a\u00020\u00132\b\u0010\u0014\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0015\u001a\u00020\u0016H\u00d6\u0001J\t\u0010\u0017\u001a\u00020\u0018H\u00d6\u0001R\u001c\u0010\u0002\u001a\u0004\u0018\u00010\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0007\u0010\b\"\u0004\b\t\u0010\nR\u001c\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000e¨\u0006\u0019" }, d2 = { "Lcom/onesignal/OSNotificationIntentExtras;", "", "dataArray", "Lorg/json/JSONArray;", "jsonData", "Lorg/json/JSONObject;", "(Lorg/json/JSONArray;Lorg/json/JSONObject;)V", "getDataArray", "()Lorg/json/JSONArray;", "setDataArray", "(Lorg/json/JSONArray;)V", "getJsonData", "()Lorg/json/JSONObject;", "setJsonData", "(Lorg/json/JSONObject;)V", "component1", "component2", "copy", "equals", "", "other", "hashCode", "", "toString", "", "onesignal_release" }, k = 1, mv = { 1, 4, 2 })
public final class OSNotificationIntentExtras
{
    private JSONArray dataArray;
    private JSONObject jsonData;
    
    public OSNotificationIntentExtras(final JSONArray dataArray, final JSONObject jsonData) {
        this.dataArray = dataArray;
        this.jsonData = jsonData;
    }
    
    public final JSONArray component1() {
        return this.dataArray;
    }
    
    public final JSONObject component2() {
        return this.jsonData;
    }
    
    public final OSNotificationIntentExtras copy(final JSONArray jsonArray, final JSONObject jsonObject) {
        return new OSNotificationIntentExtras(jsonArray, jsonObject);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this != o) {
            if (o instanceof OSNotificationIntentExtras) {
                final OSNotificationIntentExtras osNotificationIntentExtras = (OSNotificationIntentExtras)o;
                if (Intrinsics.areEqual((Object)this.dataArray, (Object)osNotificationIntentExtras.dataArray) && Intrinsics.areEqual((Object)this.jsonData, (Object)osNotificationIntentExtras.jsonData)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }
    
    public final JSONArray getDataArray() {
        return this.dataArray;
    }
    
    public final JSONObject getJsonData() {
        return this.jsonData;
    }
    
    @Override
    public int hashCode() {
        final JSONArray dataArray = this.dataArray;
        int hashCode = 0;
        int hashCode2;
        if (dataArray != null) {
            hashCode2 = dataArray.hashCode();
        }
        else {
            hashCode2 = 0;
        }
        final JSONObject jsonData = this.jsonData;
        if (jsonData != null) {
            hashCode = jsonData.hashCode();
        }
        return hashCode2 * 31 + hashCode;
    }
    
    public final void setDataArray(final JSONArray dataArray) {
        this.dataArray = dataArray;
    }
    
    public final void setJsonData(final JSONObject jsonData) {
        this.jsonData = jsonData;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("OSNotificationIntentExtras(dataArray=");
        sb.append((Object)this.dataArray);
        sb.append(", jsonData=");
        sb.append((Object)this.jsonData);
        sb.append(")");
        return sb.toString();
    }
}
