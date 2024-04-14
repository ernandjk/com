package com.onesignal.outcomes.data;

import android.database.sqlite.SQLiteException;
import com.onesignal.influence.domain.OSInfluenceChannel;
import kotlin.jvm.internal.Intrinsics;
import android.database.sqlite.SQLiteDatabase;
import kotlin.Metadata;

@Metadata(bv = { 1, 0, 3 }, d1 = { "\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u000e\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006J\u000e\u0010\u0007\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006J\u000e\u0010\b\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006¨\u0006\t" }, d2 = { "Lcom/onesignal/outcomes/data/OSOutcomeTableProvider;", "", "()V", "upgradeCacheOutcomeTableRevision1To2", "", "db", "Landroid/database/sqlite/SQLiteDatabase;", "upgradeOutcomeTableRevision1To2", "upgradeOutcomeTableRevision2To3", "onesignal_release" }, k = 1, mv = { 1, 4, 2 })
public final class OSOutcomeTableProvider
{
    public final void upgradeCacheOutcomeTableRevision1To2(final SQLiteDatabase sqLiteDatabase) {
        Intrinsics.checkNotNullParameter((Object)sqLiteDatabase, "db");
        try {
            try {
                sqLiteDatabase.execSQL("BEGIN TRANSACTION;");
                sqLiteDatabase.execSQL("CREATE TABLE cached_unique_outcome (_id INTEGER PRIMARY KEY,channel_influence_id TEXT,channel_type TEXT,name TEXT);");
                sqLiteDatabase.execSQL("INSERT INTO cached_unique_outcome(_id,name,channel_influence_id) SELECT _id,name,notification_id FROM cached_unique_outcome_notification;");
                final StringBuilder sb = new StringBuilder("UPDATE cached_unique_outcome SET channel_type = '");
                sb.append(OSInfluenceChannel.NOTIFICATION.toString());
                sb.append("';");
                sqLiteDatabase.execSQL(sb.toString());
                sqLiteDatabase.execSQL("DROP TABLE cached_unique_outcome_notification;");
            }
            finally {}
        }
        catch (final SQLiteException ex) {
            ex.printStackTrace();
        }
        sqLiteDatabase.execSQL("COMMIT;");
        return;
        sqLiteDatabase.execSQL("COMMIT;");
    }
    
    public final void upgradeOutcomeTableRevision1To2(final SQLiteDatabase sqLiteDatabase) {
        Intrinsics.checkNotNullParameter((Object)sqLiteDatabase, "db");
        try {
            try {
                sqLiteDatabase.execSQL("BEGIN TRANSACTION;");
                sqLiteDatabase.execSQL("CREATE TEMPORARY TABLE outcome_backup(_id,session,notification_ids,name,timestamp);");
                sqLiteDatabase.execSQL("INSERT INTO outcome_backup SELECT _id,session,notification_ids,name,timestamp FROM outcome;");
                sqLiteDatabase.execSQL("DROP TABLE outcome;");
                sqLiteDatabase.execSQL("CREATE TABLE outcome (_id INTEGER PRIMARY KEY,session TEXT,notification_ids TEXT,name TEXT,timestamp TIMESTAMP,weight FLOAT);");
                sqLiteDatabase.execSQL("INSERT INTO outcome (_id,session,notification_ids,name,timestamp, weight) SELECT _id,session,notification_ids,name,timestamp, 0 FROM outcome_backup;");
                sqLiteDatabase.execSQL("DROP TABLE outcome_backup;");
            }
            finally {}
        }
        catch (final SQLiteException ex) {
            ex.printStackTrace();
        }
        sqLiteDatabase.execSQL("COMMIT;");
        return;
        sqLiteDatabase.execSQL("COMMIT;");
    }
    
    public final void upgradeOutcomeTableRevision2To3(final SQLiteDatabase sqLiteDatabase) {
        Intrinsics.checkNotNullParameter((Object)sqLiteDatabase, "db");
        try {
            try {
                sqLiteDatabase.execSQL("BEGIN TRANSACTION;");
                sqLiteDatabase.execSQL("ALTER TABLE outcome RENAME TO outcome_aux;");
                sqLiteDatabase.execSQL("CREATE TABLE outcome (_id INTEGER PRIMARY KEY,notification_influence_type TEXT,iam_influence_type TEXT,notification_ids TEXT,iam_ids TEXT,name TEXT,timestamp TIMESTAMP,weight FLOAT);");
                sqLiteDatabase.execSQL("INSERT INTO outcome(_id,name,timestamp,notification_ids,weight,notification_influence_type) SELECT _id,name,timestamp,notification_ids,weight,session FROM outcome_aux;");
                sqLiteDatabase.execSQL("DROP TABLE outcome_aux;");
            }
            finally {}
        }
        catch (final SQLiteException ex) {
            ex.printStackTrace();
        }
        sqLiteDatabase.execSQL("COMMIT;");
        return;
        sqLiteDatabase.execSQL("COMMIT;");
    }
}
