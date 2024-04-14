package com.onesignal.influence.data;

import kotlin.Unit;
import org.json.JSONException;
import org.json.JSONArray;
import com.onesignal.influence.domain.OSInfluenceChannel;
import com.onesignal.influence.domain.OSInfluenceType;
import com.onesignal.influence.domain.OSInfluence;
import org.json.JSONObject;
import kotlin.jvm.internal.Intrinsics;
import com.onesignal.OSTime;
import com.onesignal.OSLogger;
import kotlin.Metadata;

@Metadata(bv = { 1, 0, 3 }, d1 = { "\u0000R\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\b\u0000\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0002\u0010\bJ\u0018\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020 H\u0016J\b\u0010!\u001a\u00020\u001cH\u0016J\u0012\u0010\"\u001a\u00020\u00182\b\u0010#\u001a\u0004\u0018\u00010\u0012H\u0016J\b\u0010$\u001a\u00020\u001cH\u0016J\u0010\u0010%\u001a\u00020\u001c2\u0006\u0010&\u001a\u00020\u0018H\u0016R\u0014\u0010\t\u001a\u00020\n8VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u000b\u0010\fR\u0014\u0010\r\u001a\u00020\u000e8VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u000f\u0010\u0010R\u0014\u0010\u0011\u001a\u00020\u00128VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u0013\u0010\u0014R\u0014\u0010\u0015\u001a\u00020\n8VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u0016\u0010\fR\u0014\u0010\u0017\u001a\u00020\u00188VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u0019\u0010\u001a¨\u0006'" }, d2 = { "Lcom/onesignal/influence/data/OSInAppMessageTracker;", "Lcom/onesignal/influence/data/OSChannelTracker;", "dataRepository", "Lcom/onesignal/influence/data/OSInfluenceDataRepository;", "logger", "Lcom/onesignal/OSLogger;", "timeProvider", "Lcom/onesignal/OSTime;", "(Lcom/onesignal/influence/data/OSInfluenceDataRepository;Lcom/onesignal/OSLogger;Lcom/onesignal/OSTime;)V", "channelLimit", "", "getChannelLimit", "()I", "channelType", "Lcom/onesignal/influence/domain/OSInfluenceChannel;", "getChannelType", "()Lcom/onesignal/influence/domain/OSInfluenceChannel;", "idTag", "", "getIdTag", "()Ljava/lang/String;", "indirectAttributionWindow", "getIndirectAttributionWindow", "lastChannelObjects", "Lorg/json/JSONArray;", "getLastChannelObjects", "()Lorg/json/JSONArray;", "addSessionData", "", "jsonObject", "Lorg/json/JSONObject;", "influence", "Lcom/onesignal/influence/domain/OSInfluence;", "cacheState", "getLastChannelObjectsReceivedByNewId", "id", "initInfluencedTypeFromCache", "saveChannelObjects", "channelObjects", "onesignal_release" }, k = 1, mv = { 1, 4, 2 })
public final class OSInAppMessageTracker extends OSChannelTracker
{
    public OSInAppMessageTracker(final OSInfluenceDataRepository osInfluenceDataRepository, final OSLogger osLogger, final OSTime osTime) {
        Intrinsics.checkNotNullParameter((Object)osInfluenceDataRepository, "dataRepository");
        Intrinsics.checkNotNullParameter((Object)osLogger, "logger");
        Intrinsics.checkNotNullParameter((Object)osTime, "timeProvider");
        super(osInfluenceDataRepository, osLogger, osTime);
    }
    
    @Override
    public void addSessionData(final JSONObject jsonObject, final OSInfluence osInfluence) {
        Intrinsics.checkNotNullParameter((Object)jsonObject, "jsonObject");
        Intrinsics.checkNotNullParameter((Object)osInfluence, "influence");
    }
    
    @Override
    public void cacheState() {
        OSInfluenceType osInfluenceType = this.getInfluenceType();
        if (osInfluenceType == null) {
            osInfluenceType = OSInfluenceType.UNATTRIBUTED;
        }
        final OSInfluenceDataRepository dataRepository = this.getDataRepository();
        OSInfluenceType indirect = osInfluenceType;
        if (osInfluenceType == OSInfluenceType.DIRECT) {
            indirect = OSInfluenceType.INDIRECT;
        }
        dataRepository.cacheIAMInfluenceType(indirect);
    }
    
    @Override
    public int getChannelLimit() {
        return this.getDataRepository().getIamLimit();
    }
    
    @Override
    public OSInfluenceChannel getChannelType() {
        return OSInfluenceChannel.IAM;
    }
    
    @Override
    public String getIdTag() {
        return "iam_id";
    }
    
    @Override
    public int getIndirectAttributionWindow() {
        return this.getDataRepository().getIamIndirectAttributionWindow();
    }
    
    @Override
    public JSONArray getLastChannelObjects() throws JSONException {
        return this.getDataRepository().getLastIAMsReceivedData();
    }
    
    @Override
    public JSONArray getLastChannelObjectsReceivedByNewId(final String s) {
        try {
            final JSONArray lastChannelObjects = this.getLastChannelObjects();
            JSONArray jsonArray2;
            try {
                final JSONArray jsonArray = new JSONArray();
                for (int length = lastChannelObjects.length(), i = 0; i < length; ++i) {
                    if (Intrinsics.areEqual((Object)s, (Object)lastChannelObjects.getJSONObject(i).getString(this.getIdTag())) ^ true) {
                        jsonArray.put((Object)lastChannelObjects.getJSONObject(i));
                    }
                }
                jsonArray2 = jsonArray;
            }
            catch (final JSONException ex) {
                this.getLogger().error("Generating tracker lastChannelObjectReceived get JSONObject ", (Throwable)ex);
                jsonArray2 = lastChannelObjects;
            }
            return jsonArray2;
        }
        catch (final JSONException ex2) {
            this.getLogger().error("Generating IAM tracker getLastChannelObjects JSONObject ", (Throwable)ex2);
            return new JSONArray();
        }
    }
    
    @Override
    public void initInfluencedTypeFromCache() {
        final OSInfluenceType iamCachedInfluenceType = this.getDataRepository().getIamCachedInfluenceType();
        if (iamCachedInfluenceType.isIndirect()) {
            this.setIndirectIds(this.getLastReceivedIds());
        }
        final Unit instance = Unit.INSTANCE;
        this.setInfluenceType(iamCachedInfluenceType);
        final OSLogger logger = this.getLogger();
        final StringBuilder sb = new StringBuilder("OneSignal InAppMessageTracker initInfluencedTypeFromCache: ");
        sb.append((Object)this);
        logger.debug(sb.toString());
    }
    
    @Override
    public void saveChannelObjects(final JSONArray jsonArray) {
        Intrinsics.checkNotNullParameter((Object)jsonArray, "channelObjects");
        this.getDataRepository().saveIAMs(jsonArray);
    }
}
