package com.onesignal.influence.data;

import org.json.JSONException;
import com.onesignal.influence.domain.OSInfluenceChannel;
import com.onesignal.influence.domain.OSInfluence;
import org.json.JSONObject;
import kotlin.jvm.internal.Intrinsics;
import com.onesignal.OSTime;
import com.onesignal.OSLogger;
import com.onesignal.influence.domain.OSInfluenceType;
import org.json.JSONArray;
import kotlin.Metadata;

@Metadata(bv = { 1, 0, 3 }, d1 = { "\u0000d\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u000e\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\f\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000e\b&\u0018\u00002\u00020\u0001B\u001f\b\u0000\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0002\u0010\bJ\u0018\u0010<\u001a\u00020=2\u0006\u0010>\u001a\u00020?2\u0006\u0010@\u001a\u00020\u0012H&J\b\u0010A\u001a\u00020=H&J\u0013\u0010B\u001a\u0002002\b\u0010C\u001a\u0004\u0018\u00010\u0001H\u0096\u0002J\u0012\u0010D\u001a\u00020$2\b\u0010E\u001a\u0004\u0018\u00010\u001aH&J\b\u0010F\u001a\u00020\nH\u0016J\b\u0010G\u001a\u00020=H&J\u0006\u0010H\u001a\u00020=J\u0010\u0010I\u001a\u00020=2\u0006\u0010J\u001a\u00020$H&J\u0010\u0010K\u001a\u00020=2\b\u0010E\u001a\u0004\u0018\u00010\u001aJ\b\u0010L\u001a\u00020\u001aH\u0016R\u0012\u0010\t\u001a\u00020\nX¦\u0004¢\u0006\u0006\u001a\u0004\b\u000b\u0010\fR\u0012\u0010\r\u001a\u00020\u000eX¦\u0004¢\u0006\u0006\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0011\u001a\u00020\u00128F¢\u0006\u0006\u001a\u0004\b\u0013\u0010\u0014R\u001a\u0010\u0002\u001a\u00020\u0003X\u0084\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0015\u0010\u0016\"\u0004\b\u0017\u0010\u0018R\u001c\u0010\u0019\u001a\u0004\u0018\u00010\u001aX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001b\u0010\u001c\"\u0004\b\u001d\u0010\u001eR\u0012\u0010\u001f\u001a\u00020\u001aX¦\u0004¢\u0006\u0006\u001a\u0004\b \u0010\u001cR\u0012\u0010!\u001a\u00020\nX¦\u0004¢\u0006\u0006\u001a\u0004\b\"\u0010\fR\u001c\u0010#\u001a\u0004\u0018\u00010$X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b%\u0010&\"\u0004\b'\u0010(R\u001c\u0010)\u001a\u0004\u0018\u00010*X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b+\u0010,\"\u0004\b-\u0010.R\u0014\u0010/\u001a\u0002008BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b/\u00101R\u0014\u00102\u001a\u0002008BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b2\u00101R\u0014\u00103\u001a\u0002008BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b3\u00101R\u0014\u00104\u001a\u00020$8fX¦\u0004¢\u0006\u0006\u001a\u0004\b5\u0010&R\u0011\u00106\u001a\u00020$8F¢\u0006\u0006\u001a\u0004\b7\u0010&R\u001a\u0010\u0004\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b8\u00109\"\u0004\b:\u0010;R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006M" }, d2 = { "Lcom/onesignal/influence/data/OSChannelTracker;", "", "dataRepository", "Lcom/onesignal/influence/data/OSInfluenceDataRepository;", "logger", "Lcom/onesignal/OSLogger;", "timeProvider", "Lcom/onesignal/OSTime;", "(Lcom/onesignal/influence/data/OSInfluenceDataRepository;Lcom/onesignal/OSLogger;Lcom/onesignal/OSTime;)V", "channelLimit", "", "getChannelLimit", "()I", "channelType", "Lcom/onesignal/influence/domain/OSInfluenceChannel;", "getChannelType", "()Lcom/onesignal/influence/domain/OSInfluenceChannel;", "currentSessionInfluence", "Lcom/onesignal/influence/domain/OSInfluence;", "getCurrentSessionInfluence", "()Lcom/onesignal/influence/domain/OSInfluence;", "getDataRepository", "()Lcom/onesignal/influence/data/OSInfluenceDataRepository;", "setDataRepository", "(Lcom/onesignal/influence/data/OSInfluenceDataRepository;)V", "directId", "", "getDirectId", "()Ljava/lang/String;", "setDirectId", "(Ljava/lang/String;)V", "idTag", "getIdTag", "indirectAttributionWindow", "getIndirectAttributionWindow", "indirectIds", "Lorg/json/JSONArray;", "getIndirectIds", "()Lorg/json/JSONArray;", "setIndirectIds", "(Lorg/json/JSONArray;)V", "influenceType", "Lcom/onesignal/influence/domain/OSInfluenceType;", "getInfluenceType", "()Lcom/onesignal/influence/domain/OSInfluenceType;", "setInfluenceType", "(Lcom/onesignal/influence/domain/OSInfluenceType;)V", "isDirectSessionEnabled", "", "()Z", "isIndirectSessionEnabled", "isUnattributedSessionEnabled", "lastChannelObjects", "getLastChannelObjects", "lastReceivedIds", "getLastReceivedIds", "getLogger", "()Lcom/onesignal/OSLogger;", "setLogger", "(Lcom/onesignal/OSLogger;)V", "addSessionData", "", "jsonObject", "Lorg/json/JSONObject;", "influence", "cacheState", "equals", "other", "getLastChannelObjectsReceivedByNewId", "id", "hashCode", "initInfluencedTypeFromCache", "resetAndInitInfluence", "saveChannelObjects", "channelObjects", "saveLastId", "toString", "onesignal_release" }, k = 1, mv = { 1, 4, 2 })
public abstract class OSChannelTracker
{
    private OSInfluenceDataRepository dataRepository;
    private String directId;
    private JSONArray indirectIds;
    private OSInfluenceType influenceType;
    private OSLogger logger;
    private OSTime timeProvider;
    
    public OSChannelTracker(final OSInfluenceDataRepository dataRepository, final OSLogger logger, final OSTime timeProvider) {
        Intrinsics.checkNotNullParameter((Object)dataRepository, "dataRepository");
        Intrinsics.checkNotNullParameter((Object)logger, "logger");
        Intrinsics.checkNotNullParameter((Object)timeProvider, "timeProvider");
        this.dataRepository = dataRepository;
        this.logger = logger;
        this.timeProvider = timeProvider;
    }
    
    private final boolean isDirectSessionEnabled() {
        return this.dataRepository.isDirectInfluenceEnabled();
    }
    
    private final boolean isIndirectSessionEnabled() {
        return this.dataRepository.isIndirectInfluenceEnabled();
    }
    
    private final boolean isUnattributedSessionEnabled() {
        return this.dataRepository.isUnattributedInfluenceEnabled();
    }
    
    public abstract void addSessionData(final JSONObject p0, final OSInfluence p1);
    
    public abstract void cacheState();
    
    @Override
    public boolean equals(final Object o) {
        final OSChannelTracker osChannelTracker = this;
        boolean b = true;
        if (this == o) {
            return true;
        }
        if (o != null && !(Intrinsics.areEqual((Object)this.getClass(), (Object)o.getClass()) ^ true)) {
            final OSChannelTracker osChannelTracker2 = (OSChannelTracker)o;
            if (this.influenceType != osChannelTracker2.influenceType || !Intrinsics.areEqual((Object)osChannelTracker2.getIdTag(), (Object)this.getIdTag())) {
                b = false;
            }
            return b;
        }
        return false;
    }
    
    public abstract int getChannelLimit();
    
    public abstract OSInfluenceChannel getChannelType();
    
    public final OSInfluence getCurrentSessionInfluence() {
        final OSInfluence osInfluence = new OSInfluence(this.getChannelType(), OSInfluenceType.DISABLED, null);
        if (this.influenceType == null) {
            this.initInfluencedTypeFromCache();
        }
        OSInfluenceType osInfluenceType = this.influenceType;
        if (osInfluenceType == null) {
            osInfluenceType = OSInfluenceType.DISABLED;
        }
        if (osInfluenceType.isDirect()) {
            if (this.isDirectSessionEnabled()) {
                osInfluence.setIds(new JSONArray().put((Object)this.directId));
                osInfluence.setInfluenceType(OSInfluenceType.DIRECT);
            }
        }
        else if (osInfluenceType.isIndirect()) {
            if (this.isIndirectSessionEnabled()) {
                osInfluence.setIds(this.indirectIds);
                osInfluence.setInfluenceType(OSInfluenceType.INDIRECT);
            }
        }
        else if (this.isUnattributedSessionEnabled()) {
            osInfluence.setInfluenceType(OSInfluenceType.UNATTRIBUTED);
        }
        return osInfluence;
    }
    
    protected final OSInfluenceDataRepository getDataRepository() {
        return this.dataRepository;
    }
    
    public final String getDirectId() {
        return this.directId;
    }
    
    public abstract String getIdTag();
    
    public abstract int getIndirectAttributionWindow();
    
    public final JSONArray getIndirectIds() {
        return this.indirectIds;
    }
    
    public final OSInfluenceType getInfluenceType() {
        return this.influenceType;
    }
    
    public abstract JSONArray getLastChannelObjects() throws JSONException;
    
    public abstract JSONArray getLastChannelObjectsReceivedByNewId(final String p0);
    
    public final JSONArray getLastReceivedIds() {
        final JSONArray jsonArray = new JSONArray();
        try {
            final JSONArray lastChannelObjects = this.getLastChannelObjects();
            final OSLogger logger = this.logger;
            final StringBuilder sb = new StringBuilder("OneSignal ChannelTracker getLastReceivedIds lastChannelObjectReceived: ");
            sb.append((Object)lastChannelObjects);
            logger.debug(sb.toString());
            final long n = this.getIndirectAttributionWindow() * 60;
            final long currentTimeMillis = this.timeProvider.getCurrentTimeMillis();
            for (int length = lastChannelObjects.length(), i = 0; i < length; ++i) {
                final JSONObject jsonObject = lastChannelObjects.getJSONObject(i);
                if (currentTimeMillis - jsonObject.getLong("time") <= n * 1000L) {
                    jsonArray.put((Object)jsonObject.getString(this.getIdTag()));
                }
            }
        }
        catch (final JSONException ex) {
            this.logger.error("Generating tracker getLastReceivedIds JSONObject ", (Throwable)ex);
        }
        return jsonArray;
    }
    
    public final OSLogger getLogger() {
        return this.logger;
    }
    
    @Override
    public int hashCode() {
        final OSInfluenceType influenceType = this.influenceType;
        int hashCode;
        if (influenceType != null) {
            hashCode = influenceType.hashCode();
        }
        else {
            hashCode = 0;
        }
        return hashCode * 31 + this.getIdTag().hashCode();
    }
    
    public abstract void initInfluencedTypeFromCache();
    
    public final void resetAndInitInfluence() {
        final String s = null;
        this.directId = null;
        final JSONArray lastReceivedIds = this.getLastReceivedIds();
        this.indirectIds = lastReceivedIds;
        int length;
        if (lastReceivedIds != null) {
            length = lastReceivedIds.length();
        }
        else {
            length = 0;
        }
        OSInfluenceType influenceType;
        if (length > 0) {
            influenceType = OSInfluenceType.INDIRECT;
        }
        else {
            influenceType = OSInfluenceType.UNATTRIBUTED;
        }
        this.influenceType = influenceType;
        this.cacheState();
        final OSLogger logger = this.logger;
        final StringBuilder sb = new StringBuilder("OneSignal OSChannelTracker resetAndInitInfluence: ");
        sb.append(this.getIdTag());
        sb.append(" finish with influenceType: ");
        sb.append((Object)this.influenceType);
        logger.debug(sb.toString());
    }
    
    public abstract void saveChannelObjects(final JSONArray p0);
    
    public final void saveLastId(String s) {
        final OSLogger logger = this.logger;
        final StringBuilder sb = new StringBuilder("OneSignal OSChannelTracker for: ");
        sb.append(this.getIdTag());
        sb.append(" saveLastId: ");
        sb.append(s);
        logger.debug(sb.toString());
        if (s != null) {
            if (((CharSequence)s).length() != 0) {
                final JSONArray lastChannelObjectsReceivedByNewId = this.getLastChannelObjectsReceivedByNewId(s);
                final OSLogger logger2 = this.logger;
                final StringBuilder sb2 = new StringBuilder("OneSignal OSChannelTracker for: ");
                sb2.append(this.getIdTag());
                sb2.append(" saveLastId with lastChannelObjectsReceived: ");
                sb2.append((Object)lastChannelObjectsReceivedByNewId);
                logger2.debug(sb2.toString());
                try {
                    lastChannelObjectsReceivedByNewId.put((Object)new JSONObject().put(this.getIdTag(), (Object)s).put("time", this.timeProvider.getCurrentTimeMillis()));
                    s = (String)lastChannelObjectsReceivedByNewId;
                    if (lastChannelObjectsReceivedByNewId.length() > this.getChannelLimit()) {
                        int i = lastChannelObjectsReceivedByNewId.length() - this.getChannelLimit();
                        s = (String)new JSONArray();
                        while (i < lastChannelObjectsReceivedByNewId.length()) {
                            try {
                                ((JSONArray)s).put(lastChannelObjectsReceivedByNewId.get(i));
                            }
                            catch (final JSONException ex) {
                                this.logger.error("Generating tracker lastChannelObjectsReceived get JSONObject ", (Throwable)ex);
                            }
                            ++i;
                        }
                    }
                    final OSLogger logger3 = this.logger;
                    final StringBuilder sb3 = new StringBuilder("OneSignal OSChannelTracker for: ");
                    sb3.append(this.getIdTag());
                    sb3.append(" with channelObjectToSave: ");
                    sb3.append((Object)s);
                    logger3.debug(sb3.toString());
                    this.saveChannelObjects((JSONArray)s);
                }
                catch (final JSONException ex2) {
                    this.logger.error("Generating tracker newInfluenceId JSONObject ", (Throwable)ex2);
                }
            }
        }
    }
    
    protected final void setDataRepository(final OSInfluenceDataRepository dataRepository) {
        Intrinsics.checkNotNullParameter((Object)dataRepository, "<set-?>");
        this.dataRepository = dataRepository;
    }
    
    public final void setDirectId(final String directId) {
        this.directId = directId;
    }
    
    public final void setIndirectIds(final JSONArray indirectIds) {
        this.indirectIds = indirectIds;
    }
    
    public final void setInfluenceType(final OSInfluenceType influenceType) {
        this.influenceType = influenceType;
    }
    
    public final void setLogger(final OSLogger logger) {
        Intrinsics.checkNotNullParameter((Object)logger, "<set-?>");
        this.logger = logger;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("OSChannelTracker{tag=");
        sb.append(this.getIdTag());
        sb.append(", influenceType=");
        sb.append((Object)this.influenceType);
        sb.append(", indirectIds=");
        sb.append((Object)this.indirectIds);
        sb.append(", directId=");
        sb.append(this.directId);
        sb.append('}');
        return sb.toString();
    }
}
