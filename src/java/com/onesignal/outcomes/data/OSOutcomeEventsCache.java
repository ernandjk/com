package com.onesignal.outcomes.data;

import android.content.ContentValues;
import java.util.Set;
import java.util.Iterator;
import com.onesignal.influence.domain.OSInfluence;
import kotlin.jvm.internal.DefaultConstructorMarker;
import android.database.Cursor;
import java.util.ArrayList;
import com.onesignal.outcomes.domain.OSOutcomeEventParams;
import java.util.Locale;
import com.onesignal.outcomes.domain.OSOutcomeSource;
import com.onesignal.influence.domain.OSInfluenceType;
import com.onesignal.outcomes.domain.OSOutcomeSourceBody;
import org.json.JSONException;
import com.onesignal.influence.domain.OSInfluenceChannel;
import org.json.JSONArray;
import com.onesignal.outcomes.domain.OSCachedUniqueOutcome;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;
import com.onesignal.OSSharedPreferences;
import com.onesignal.OSLogger;
import com.onesignal.OneSignalDb;
import kotlin.Metadata;

@Metadata(bv = { 1, 0, 3 }, d1 = { "\u0000z\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\"\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u000b\b\u0000\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0002\u0010\bJ(\u0010\u0011\u001a\u00020\u00122\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00150\u00142\b\u0010\u0016\u001a\u0004\u0018\u00010\u00172\u0006\u0010\u0018\u001a\u00020\u0019H\u0002J \u0010\u001a\u001a\u00020\u00122\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00150\u00142\b\u0010\u001b\u001a\u0004\u0018\u00010\u001cH\u0002J\u0018\u0010\u001d\u001a\u00020\u00122\u0006\u0010\u001e\u001a\u00020\u000e2\u0006\u0010\u001f\u001a\u00020\u000eH\u0007J\u0010\u0010 \u001a\u00020\u00122\u0006\u0010!\u001a\u00020\"H\u0007J\u000e\u0010#\u001a\b\u0012\u0004\u0012\u00020\"0$H\u0007J4\u0010%\u001a\u0004\u0018\u00010&2\u0006\u0010'\u001a\u00020(2\u0006\u0010)\u001a\u00020\u001c2\u0006\u0010*\u001a\u00020\u001c2\u0006\u0010+\u001a\u00020\u000e2\b\u0010,\u001a\u0004\u0018\u00010&H\u0002J$\u0010-\u001a\b\u0012\u0004\u0012\u00020.0$2\u0006\u0010/\u001a\u00020\u000e2\f\u00100\u001a\b\u0012\u0004\u0012\u00020.0$H\u0007J*\u00101\u001a\u0004\u0018\u00010&2\u0006\u00102\u001a\u00020(2\u0006\u0010)\u001a\u00020\u001c2\u0006\u0010*\u001a\u00020\u001c2\u0006\u00103\u001a\u00020\u000eH\u0002J\u0010\u00104\u001a\u00020\u00122\u0006\u00105\u001a\u00020\"H\u0007J\u0018\u00106\u001a\u00020\u00122\u0010\u00107\u001a\f\u0012\u0006\u0012\u0004\u0018\u00010\u000e\u0018\u00010\rJ\u0010\u00108\u001a\u00020\u00122\u0006\u00105\u001a\u00020\"H\u0007R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u0011\u0010\t\u001a\u00020\n8F¢\u0006\u0006\u001a\u0004\b\t\u0010\u000bR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u0019\u0010\f\u001a\n\u0012\u0004\u0012\u00020\u000e\u0018\u00010\r8F¢\u0006\u0006\u001a\u0004\b\u000f\u0010\u0010¨\u00069" }, d2 = { "Lcom/onesignal/outcomes/data/OSOutcomeEventsCache;", "", "logger", "Lcom/onesignal/OSLogger;", "dbHelper", "Lcom/onesignal/OneSignalDb;", "preferences", "Lcom/onesignal/OSSharedPreferences;", "(Lcom/onesignal/OSLogger;Lcom/onesignal/OneSignalDb;Lcom/onesignal/OSSharedPreferences;)V", "isOutcomesV2ServiceEnabled", "", "()Z", "unattributedUniqueOutcomeEventsSentByChannel", "", "", "getUnattributedUniqueOutcomeEventsSentByChannel", "()Ljava/util/Set;", "addIdToListFromChannel", "", "cachedUniqueOutcomes", "", "Lcom/onesignal/outcomes/domain/OSCachedUniqueOutcome;", "channelIds", "Lorg/json/JSONArray;", "channel", "Lcom/onesignal/influence/domain/OSInfluenceChannel;", "addIdsToListFromSource", "sourceBody", "Lcom/onesignal/outcomes/domain/OSOutcomeSourceBody;", "cleanCachedUniqueOutcomeEventNotifications", "notificationTableName", "notificationIdColumnName", "deleteOldOutcomeEvent", "event", "Lcom/onesignal/outcomes/domain/OSOutcomeEventParams;", "getAllEventsToSend", "", "getIAMInfluenceSource", "Lcom/onesignal/outcomes/domain/OSOutcomeSource;", "iamInfluenceType", "Lcom/onesignal/influence/domain/OSInfluenceType;", "directSourceBody", "indirectSourceBody", "iamIds", "source", "getNotCachedUniqueInfluencesForOutcome", "Lcom/onesignal/influence/domain/OSInfluence;", "name", "influences", "getNotificationInfluenceSource", "notificationInfluenceType", "notificationIds", "saveOutcomeEvent", "eventParams", "saveUnattributedUniqueOutcomeEventsSentByChannel", "unattributedUniqueOutcomeEvents", "saveUniqueOutcomeEventParams", "onesignal_release" }, k = 1, mv = { 1, 4, 2 })
public final class OSOutcomeEventsCache
{
    private final OneSignalDb dbHelper;
    private final OSLogger logger;
    private final OSSharedPreferences preferences;
    
    public OSOutcomeEventsCache(final OSLogger logger, final OneSignalDb dbHelper, final OSSharedPreferences preferences) {
        Intrinsics.checkNotNullParameter((Object)logger, "logger");
        Intrinsics.checkNotNullParameter((Object)dbHelper, "dbHelper");
        Intrinsics.checkNotNullParameter((Object)preferences, "preferences");
        this.logger = logger;
        this.dbHelper = dbHelper;
        this.preferences = preferences;
    }
    
    private final void addIdToListFromChannel(final List<OSCachedUniqueOutcome> list, final JSONArray jsonArray, final OSInfluenceChannel osInfluenceChannel) {
        if (jsonArray != null) {
            for (int length = jsonArray.length(), i = 0; i < length; ++i) {
                try {
                    final String string = jsonArray.getString(i);
                    Intrinsics.checkNotNullExpressionValue((Object)string, "influenceId");
                    list.add((Object)new OSCachedUniqueOutcome(string, osInfluenceChannel));
                }
                catch (final JSONException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    
    private final void addIdsToListFromSource(final List<OSCachedUniqueOutcome> list, final OSOutcomeSourceBody osOutcomeSourceBody) {
        if (osOutcomeSourceBody != null) {
            final JSONArray inAppMessagesIds = osOutcomeSourceBody.getInAppMessagesIds();
            final JSONArray notificationIds = osOutcomeSourceBody.getNotificationIds();
            this.addIdToListFromChannel(list, inAppMessagesIds, OSInfluenceChannel.IAM);
            this.addIdToListFromChannel(list, notificationIds, OSInfluenceChannel.NOTIFICATION);
        }
    }
    
    private final OSOutcomeSource getIAMInfluenceSource(final OSInfluenceType osInfluenceType, final OSOutcomeSourceBody directBody, final OSOutcomeSourceBody indirectBody, final String s, OSOutcomeSource o) {
        final int n = OSOutcomeEventsCache$WhenMappings.$EnumSwitchMapping$1[osInfluenceType.ordinal()];
        if (n != 1) {
            if (n == 2) {
                indirectBody.setInAppMessagesIds(new JSONArray(s));
                if (o != null) {
                    o = ((OSOutcomeSource)o).setIndirectBody(indirectBody);
                    if (o != null) {
                        return (OSOutcomeSource)o;
                    }
                }
                o = new OSOutcomeSource(null, indirectBody);
            }
        }
        else {
            directBody.setInAppMessagesIds(new JSONArray(s));
            if (o != null) {
                o = ((OSOutcomeSource)o).setDirectBody(directBody);
                if (o != null) {
                    return (OSOutcomeSource)o;
                }
            }
            o = new OSOutcomeSource(directBody, null);
        }
        return (OSOutcomeSource)o;
    }
    
    private final OSOutcomeSource getNotificationInfluenceSource(final OSInfluenceType osInfluenceType, final OSOutcomeSourceBody osOutcomeSourceBody, final OSOutcomeSourceBody osOutcomeSourceBody2, final String s) {
        final int n = OSOutcomeEventsCache$WhenMappings.$EnumSwitchMapping$0[osInfluenceType.ordinal()];
        OSOutcomeSource osOutcomeSource = null;
        if (n != 1) {
            if (n == 2) {
                osOutcomeSourceBody2.setNotificationIds(new JSONArray(s));
                osOutcomeSource = new OSOutcomeSource(null, osOutcomeSourceBody2);
            }
        }
        else {
            osOutcomeSourceBody.setNotificationIds(new JSONArray(s));
            osOutcomeSource = new OSOutcomeSource(osOutcomeSourceBody, null);
        }
        return osOutcomeSource;
    }
    
    public final void cleanCachedUniqueOutcomeEventNotifications(String s, String string) {
        synchronized (this) {
            Intrinsics.checkNotNullParameter((Object)s, "notificationTableName");
            Intrinsics.checkNotNullParameter((Object)string, "notificationIdColumnName");
            final StringBuilder sb = new StringBuilder("NOT EXISTS(SELECT NULL FROM ");
            sb.append(s);
            sb.append(" n WHERE n.");
            sb.append(string);
            sb.append(" = channel_influence_id AND channel_type = \"");
            string = OSInfluenceChannel.NOTIFICATION.toString();
            final Locale root = Locale.ROOT;
            Intrinsics.checkNotNullExpressionValue((Object)root, "Locale.ROOT");
            if (string != null) {
                s = string.toLowerCase(root);
                Intrinsics.checkNotNullExpressionValue((Object)s, "(this as java.lang.String).toLowerCase(locale)");
                sb.append(s);
                sb.append("\")");
                s = sb.toString();
                this.dbHelper.delete("cached_unique_outcome", s, (String[])null);
                return;
            }
            throw new NullPointerException("null cannot be cast to non-null type java.lang.String");
        }
    }
    
    public final void deleteOldOutcomeEvent(final OSOutcomeEventParams osOutcomeEventParams) {
        synchronized (this) {
            Intrinsics.checkNotNullParameter((Object)osOutcomeEventParams, "event");
            this.dbHelper.delete("outcome", "timestamp = ?", new String[] { String.valueOf(osOutcomeEventParams.getTimestamp()) });
        }
    }
    
    public final List<OSOutcomeEventParams> getAllEventsToSend() {
        synchronized (this) {
            final List list = (List)new ArrayList();
            Object o = null;
            final Cursor cursor = null;
            try {
                final Cursor query = this.dbHelper.query("outcome", (String[])null, (String)null, (String[])null, (String)null, (String)null, (String)null);
                try {
                    if (query.moveToFirst()) {
                        do {
                            o = query.getString(query.getColumnIndex("notification_influence_type"));
                            final OSInfluenceType fromString = OSInfluenceType.Companion.fromString((String)o);
                            o = query.getString(query.getColumnIndex("iam_influence_type"));
                            final OSInfluenceType fromString2 = OSInfluenceType.Companion.fromString((String)o);
                            o = query.getString(query.getColumnIndex("notification_ids"));
                            if (o == null) {
                                o = "[]";
                            }
                            String string = query.getString(query.getColumnIndex("iam_ids"));
                            if (string == null) {
                                string = "[]";
                            }
                            final String string2 = query.getString(query.getColumnIndex("name"));
                            final float float1 = query.getFloat(query.getColumnIndex("weight"));
                            final long long1 = query.getLong(query.getColumnIndex("timestamp"));
                            try {
                                final OSOutcomeSourceBody osOutcomeSourceBody = new OSOutcomeSourceBody(null, null, 3, null);
                                final OSOutcomeSourceBody osOutcomeSourceBody2 = new OSOutcomeSourceBody(null, null, 3, null);
                                o = this.getNotificationInfluenceSource(fromString, osOutcomeSourceBody, osOutcomeSourceBody2, (String)o);
                                this.getIAMInfluenceSource(fromString2, osOutcomeSourceBody, osOutcomeSourceBody2, string, (OSOutcomeSource)o);
                                if (o == null) {
                                    o = new OSOutcomeSource(null, null);
                                }
                                Intrinsics.checkNotNullExpressionValue((Object)string2, "name");
                                list.add((Object)new OSOutcomeEventParams(string2, (OSOutcomeSource)o, float1, long1));
                            }
                            catch (final JSONException o) {
                                this.logger.error("Generating JSONArray from notifications ids outcome:JSON Failed.", (Throwable)o);
                            }
                        } while (query.moveToNext());
                    }
                    if (query != null && !query.isClosed()) {
                        query.close();
                    }
                    return (List<OSOutcomeEventParams>)list;
                }
                finally {
                    o = query;
                }
            }
            finally {}
            if (o != null && !((Cursor)o).isClosed()) {
                ((Cursor)o).close();
            }
        }
    }
    
    public final List<OSInfluence> getNotCachedUniqueInfluencesForOutcome(final String s, final List<OSInfluence> list) {
        synchronized (this) {
            Intrinsics.checkNotNullParameter((Object)s, "name");
            Intrinsics.checkNotNullParameter((Object)list, "influences");
            final List list2 = (List)new ArrayList();
            Cursor query = null;
            Cursor cursor = null;
            final Cursor cursor2 = null;
            final Cursor cursor3 = null;
            try {
                try {
                    final Iterator iterator = list.iterator();
                    Cursor cursor4 = cursor2;
                    while (true) {
                        query = cursor4;
                        cursor = cursor4;
                        if (!iterator.hasNext()) {
                            break;
                        }
                        query = cursor4;
                        cursor = cursor4;
                        final OSInfluence osInfluence = (OSInfluence)iterator.next();
                        query = cursor4;
                        cursor = cursor4;
                        query = cursor4;
                        cursor = cursor4;
                        final JSONArray ids = new JSONArray();
                        query = cursor4;
                        cursor = cursor4;
                        final JSONArray ids2 = osInfluence.getIds();
                        if (ids2 == null) {
                            continue;
                        }
                        query = cursor4;
                        cursor = cursor4;
                        final int length = ids2.length();
                        int i = 0;
                        Cursor cursor5 = cursor4;
                        while (i < length) {
                            query = cursor5;
                            cursor = cursor5;
                            final String string = ids2.getString(i);
                            query = cursor5;
                            cursor = cursor5;
                            final String string2 = osInfluence.getInfluenceChannel().toString();
                            query = cursor5;
                            cursor = cursor5;
                            cursor5 = (cursor = (query = this.dbHelper.query("cached_unique_outcome", new String[0], "channel_influence_id = ? AND channel_type = ? AND name = ?", new String[] { string, string2, s }, (String)null, (String)null, (String)null, "1")));
                            Intrinsics.checkNotNullExpressionValue((Object)cursor5, "cursor");
                            query = cursor5;
                            cursor = cursor5;
                            if (cursor5.getCount() == 0) {
                                query = cursor5;
                                cursor = cursor5;
                                ids.put((Object)string);
                            }
                            ++i;
                        }
                        cursor4 = cursor5;
                        query = cursor5;
                        cursor = cursor5;
                        if (ids.length() <= 0) {
                            continue;
                        }
                        query = cursor5;
                        cursor = cursor5;
                        final OSInfluence copy = osInfluence.copy();
                        query = cursor5;
                        cursor = cursor5;
                        copy.setIds(ids);
                        query = cursor5;
                        cursor = cursor5;
                        list2.add((Object)copy);
                        cursor4 = cursor5;
                    }
                    if (cursor4 != null && !cursor4.isClosed()) {
                        cursor4.close();
                        return (List<OSInfluence>)list2;
                    }
                    return (List<OSInfluence>)list2;
                }
                finally {
                    if (query != null && !query.isClosed()) {
                        query.close();
                    }
                    iftrue(Label_0429:)(cursor.isClosed());
                    Block_15: {
                        break Block_15;
                        Label_0429: {
                            return (List<OSInfluence>)list2;
                        }
                    }
                    final Cursor cursor4 = cursor;
                }
            }
            catch (final JSONException ex) {}
        }
    }
    
    public final Set<String> getUnattributedUniqueOutcomeEventsSentByChannel() {
        final OSSharedPreferences preferences = this.preferences;
        return (Set<String>)preferences.getStringSet(preferences.getPreferencesName(), "PREFS_OS_UNATTRIBUTED_UNIQUE_OUTCOME_EVENTS_SENT", (Set)null);
    }
    
    public final boolean isOutcomesV2ServiceEnabled() {
        final OSSharedPreferences preferences = this.preferences;
        return preferences.getBool(preferences.getPreferencesName(), this.preferences.getOutcomesV2KeyName(), false);
    }
    
    public final void saveOutcomeEvent(final OSOutcomeEventParams osOutcomeEventParams) {
        synchronized (this) {
            Intrinsics.checkNotNullParameter((Object)osOutcomeEventParams, "eventParams");
            final JSONArray jsonArray = new JSONArray();
            final JSONArray jsonArray2 = new JSONArray();
            final OSInfluenceType unattributed = OSInfluenceType.UNATTRIBUTED;
            final OSInfluenceType unattributed2 = OSInfluenceType.UNATTRIBUTED;
            final OSOutcomeSource outcomeSource = osOutcomeEventParams.getOutcomeSource();
            JSONArray jsonArray3 = jsonArray;
            JSONArray jsonArray4 = jsonArray2;
            OSInfluenceType osInfluenceType = unattributed;
            OSInfluenceType direct = unattributed2;
            if (outcomeSource != null) {
                final OSOutcomeSourceBody directBody = outcomeSource.getDirectBody();
                jsonArray3 = jsonArray;
                jsonArray4 = jsonArray2;
                osInfluenceType = unattributed;
                direct = unattributed2;
                if (directBody != null) {
                    final JSONArray notificationIds = directBody.getNotificationIds();
                    JSONArray jsonArray5 = jsonArray;
                    OSInfluenceType direct2 = unattributed;
                    if (notificationIds != null) {
                        jsonArray5 = jsonArray;
                        direct2 = unattributed;
                        if (notificationIds.length() > 0) {
                            direct2 = OSInfluenceType.DIRECT;
                            jsonArray5 = notificationIds;
                        }
                    }
                    final JSONArray inAppMessagesIds = directBody.getInAppMessagesIds();
                    jsonArray3 = jsonArray5;
                    jsonArray4 = jsonArray2;
                    osInfluenceType = direct2;
                    direct = unattributed2;
                    if (inAppMessagesIds != null) {
                        jsonArray3 = jsonArray5;
                        jsonArray4 = jsonArray2;
                        osInfluenceType = direct2;
                        direct = unattributed2;
                        if (inAppMessagesIds.length() > 0) {
                            direct = OSInfluenceType.DIRECT;
                            jsonArray4 = inAppMessagesIds;
                            osInfluenceType = direct2;
                            jsonArray3 = jsonArray5;
                        }
                    }
                }
            }
            final OSOutcomeSource outcomeSource2 = osOutcomeEventParams.getOutcomeSource();
            JSONArray jsonArray6 = jsonArray3;
            JSONArray jsonArray7 = jsonArray4;
            OSInfluenceType osInfluenceType2 = osInfluenceType;
            OSInfluenceType indirect = direct;
            if (outcomeSource2 != null) {
                final OSOutcomeSourceBody indirectBody = outcomeSource2.getIndirectBody();
                jsonArray6 = jsonArray3;
                jsonArray7 = jsonArray4;
                osInfluenceType2 = osInfluenceType;
                indirect = direct;
                if (indirectBody != null) {
                    final JSONArray notificationIds2 = indirectBody.getNotificationIds();
                    JSONArray jsonArray8 = jsonArray3;
                    OSInfluenceType indirect2 = osInfluenceType;
                    if (notificationIds2 != null) {
                        jsonArray8 = jsonArray3;
                        indirect2 = osInfluenceType;
                        if (notificationIds2.length() > 0) {
                            indirect2 = OSInfluenceType.INDIRECT;
                            jsonArray8 = notificationIds2;
                        }
                    }
                    final JSONArray inAppMessagesIds2 = indirectBody.getInAppMessagesIds();
                    jsonArray6 = jsonArray8;
                    jsonArray7 = jsonArray4;
                    osInfluenceType2 = indirect2;
                    indirect = direct;
                    if (inAppMessagesIds2 != null) {
                        jsonArray6 = jsonArray8;
                        jsonArray7 = jsonArray4;
                        osInfluenceType2 = indirect2;
                        indirect = direct;
                        if (inAppMessagesIds2.length() > 0) {
                            indirect = OSInfluenceType.INDIRECT;
                            jsonArray7 = inAppMessagesIds2;
                            osInfluenceType2 = indirect2;
                            jsonArray6 = jsonArray8;
                        }
                    }
                }
            }
            final ContentValues contentValues = new ContentValues();
            contentValues.put("notification_ids", jsonArray6.toString());
            contentValues.put("iam_ids", jsonArray7.toString());
            final String string = osInfluenceType2.toString();
            if (string == null) {
                throw new NullPointerException("null cannot be cast to non-null type java.lang.String");
            }
            final String lowerCase = string.toLowerCase();
            Intrinsics.checkNotNullExpressionValue((Object)lowerCase, "(this as java.lang.String).toLowerCase()");
            contentValues.put("notification_influence_type", lowerCase);
            final String string2 = indirect.toString();
            if (string2 != null) {
                final String lowerCase2 = string2.toLowerCase();
                Intrinsics.checkNotNullExpressionValue((Object)lowerCase2, "(this as java.lang.String).toLowerCase()");
                contentValues.put("iam_influence_type", lowerCase2);
                contentValues.put("name", osOutcomeEventParams.getOutcomeId());
                contentValues.put("weight", Float.valueOf(osOutcomeEventParams.getWeight()));
                contentValues.put("timestamp", Long.valueOf(osOutcomeEventParams.getTimestamp()));
                this.dbHelper.insert("outcome", (String)null, contentValues);
                return;
            }
            throw new NullPointerException("null cannot be cast to non-null type java.lang.String");
        }
    }
    
    public final void saveUnattributedUniqueOutcomeEventsSentByChannel(final Set<String> set) {
        final OSSharedPreferences preferences = this.preferences;
        final String preferencesName = preferences.getPreferencesName();
        Intrinsics.checkNotNull((Object)set);
        preferences.saveStringSet(preferencesName, "PREFS_OS_UNATTRIBUTED_UNIQUE_OUTCOME_EVENTS_SENT", (Set)set);
    }
    
    public final void saveUniqueOutcomeEventParams(final OSOutcomeEventParams osOutcomeEventParams) {
        synchronized (this) {
            Intrinsics.checkNotNullParameter((Object)osOutcomeEventParams, "eventParams");
            final OSLogger logger = this.logger;
            final StringBuilder sb = new StringBuilder("OneSignal saveUniqueOutcomeEventParams: ");
            sb.append((Object)osOutcomeEventParams);
            logger.debug(sb.toString());
            final String outcomeId = osOutcomeEventParams.getOutcomeId();
            final List list = (List)new ArrayList();
            final OSOutcomeSource outcomeSource = osOutcomeEventParams.getOutcomeSource();
            OSOutcomeSourceBody directBody;
            if (outcomeSource != null) {
                directBody = outcomeSource.getDirectBody();
            }
            else {
                directBody = null;
            }
            final OSOutcomeSource outcomeSource2 = osOutcomeEventParams.getOutcomeSource();
            OSOutcomeSourceBody indirectBody;
            if (outcomeSource2 != null) {
                indirectBody = outcomeSource2.getIndirectBody();
            }
            else {
                indirectBody = null;
            }
            this.addIdsToListFromSource((List<OSCachedUniqueOutcome>)list, directBody);
            this.addIdsToListFromSource((List<OSCachedUniqueOutcome>)list, indirectBody);
            for (final OSCachedUniqueOutcome osCachedUniqueOutcome : list) {
                final ContentValues contentValues = new ContentValues();
                contentValues.put("channel_influence_id", osCachedUniqueOutcome.getInfluenceId());
                contentValues.put("channel_type", osCachedUniqueOutcome.getChannel().toString());
                contentValues.put("name", outcomeId);
                this.dbHelper.insert("cached_unique_outcome", (String)null, contentValues);
            }
        }
    }
}
