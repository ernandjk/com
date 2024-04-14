package com.onesignal;

import java.util.List;
import java.util.Iterator;
import android.database.Cursor;
import java.util.ArrayList;
import android.database.SQLException;
import android.content.ContentValues;
import android.database.sqlite.SQLiteException;
import android.os.SystemClock;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase$CursorFactory;
import android.content.Context;
import com.onesignal.outcomes.data.OSOutcomeTableProvider;
import android.database.sqlite.SQLiteOpenHelper;

class OneSignalDbHelper extends SQLiteOpenHelper implements OneSignalDb
{
    private static final String COMMA_SEP = ",";
    private static final String DATABASE_NAME = "OneSignal.db";
    static final int DATABASE_VERSION = 8;
    private static final int DB_OPEN_RETRY_BACKOFF = 400;
    private static final int DB_OPEN_RETRY_MAX = 5;
    private static final String FLOAT_TYPE = " FLOAT";
    private static final String INTEGER_PRIMARY_KEY_TYPE = " INTEGER PRIMARY KEY";
    private static final String INT_TYPE = " INTEGER";
    private static final Object LOCK;
    protected static final String SQL_CREATE_ENTRIES = "CREATE TABLE notification (_id INTEGER PRIMARY KEY,notification_id TEXT,android_notification_id INTEGER,group_id TEXT,collapse_id TEXT,is_summary INTEGER DEFAULT 0,opened INTEGER DEFAULT 0,dismissed INTEGER DEFAULT 0,title TEXT,message TEXT,full_data TEXT,created_time TIMESTAMP DEFAULT (strftime('%s', 'now')),expire_time TIMESTAMP);";
    private static final String SQL_CREATE_IN_APP_MESSAGE_ENTRIES = "CREATE TABLE in_app_message (_id INTEGER PRIMARY KEY,display_quantity INTEGER,last_display INTEGER,message_id TEXT,displayed_in_session INTEGER,click_ids TEXT);";
    protected static final String[] SQL_INDEX_ENTRIES;
    private static final String TEXT_TYPE = " TEXT";
    private static final String TIMESTAMP_TYPE = " TIMESTAMP";
    private static OSLogger logger;
    private static OSOutcomeTableProvider outcomeTableProvider;
    private static OneSignalDbHelper sInstance;
    
    static {
        LOCK = new Object();
        SQL_INDEX_ENTRIES = new String[] { "CREATE INDEX notification_notification_id_idx ON notification(notification_id); ", "CREATE INDEX notification_android_notification_id_idx ON notification(android_notification_id); ", "CREATE INDEX notification_group_id_idx ON notification(group_id); ", "CREATE INDEX notification_collapse_id_idx ON notification(collapse_id); ", "CREATE INDEX notification_created_time_idx ON notification(created_time); ", "CREATE INDEX notification_expire_time_idx ON notification(expire_time); " };
        OneSignalDbHelper.logger = (OSLogger)new OSLogWrapper();
        OneSignalDbHelper.outcomeTableProvider = new OSOutcomeTableProvider();
    }
    
    OneSignalDbHelper(final Context context) {
        super(context, "OneSignal.db", (SQLiteDatabase$CursorFactory)null, getDbVersion());
    }
    
    private static int getDbVersion() {
        return 8;
    }
    
    public static OneSignalDbHelper getInstance(final Context context) {
        if (OneSignalDbHelper.sInstance == null) {
            final Object lock = OneSignalDbHelper.LOCK;
            synchronized (lock) {
                if (OneSignalDbHelper.sInstance == null) {
                    OneSignalDbHelper.sInstance = new OneSignalDbHelper(context.getApplicationContext());
                }
            }
        }
        return OneSignalDbHelper.sInstance;
    }
    
    private SQLiteDatabase getSQLiteDatabase() {
        final Object lock;
        monitorenter(lock = OneSignalDbHelper.LOCK);
        try {
            try {
                final SQLiteDatabase writableDatabase = this.getWritableDatabase();
                monitorexit(lock);
                return writableDatabase;
            }
            finally {
                monitorexit(lock);
            }
        }
        catch (final SQLiteDatabaseLockedException ex) {}
        catch (final SQLiteCantOpenDatabaseException ex2) {}
    }
    
    private SQLiteDatabase getSQLiteDatabaseWithRetries() {
        final Object lock;
        monitorenter(lock = OneSignalDbHelper.LOCK);
        Object o = null;
        int n = 0;
        try {
        Label_0044_Outer:
            while (true) {
                try {
                    final SQLiteDatabase sqLiteDatabase = this.getSQLiteDatabase();
                    monitorexit(lock);
                    return sqLiteDatabase;
                }
                finally {
                    monitorexit(lock);
                    Object o2 = o;
                    iftrue(Label_0044:)(o != null);
                Block_6:
                    while (true) {
                        Block_5: {
                            break Block_5;
                            iftrue(Label_0067:)(++n >= 5);
                            break Block_6;
                        }
                        final Throwable t;
                        o2 = t;
                        continue;
                    }
                    SystemClock.sleep((long)(n * 400));
                    o = o2;
                    continue Label_0044_Outer;
                    Label_0067:;
                }
                break;
            }
        }
        catch (final SQLiteDatabaseLockedException ex) {}
        catch (final SQLiteCantOpenDatabaseException ex2) {}
    }
    
    private void internalOnUpgrade(final SQLiteDatabase sqLiteDatabase, final int n) {
        if (n < 2) {
            upgradeToV2(sqLiteDatabase);
        }
        if (n < 3) {
            upgradeToV3(sqLiteDatabase);
        }
        if (n < 4) {
            upgradeToV4(sqLiteDatabase);
        }
        if (n < 5) {
            upgradeToV5(sqLiteDatabase);
        }
        if (n == 5) {
            upgradeFromV5ToV6(sqLiteDatabase);
        }
        if (n < 7) {
            upgradeToV7(sqLiteDatabase);
        }
        if (n < 8) {
            this.upgradeToV8(sqLiteDatabase);
        }
    }
    
    static StringBuilder recentUninteractedWithNotificationsWhere() {
        final long n = OneSignal.getTime().getCurrentTimeMillis() / 1000L;
        final StringBuilder sb = new StringBuilder("created_time > ");
        sb.append(n - 604800L);
        sb.append(" AND dismissed = 0 AND opened = 0 AND is_summary = 0");
        final StringBuilder sb2 = new StringBuilder(sb.toString());
        if (OneSignal.getRemoteParamController().isRestoreTTLFilterActive()) {
            final StringBuilder sb3 = new StringBuilder(" AND expire_time > ");
            sb3.append(n);
            sb2.append(sb3.toString());
        }
        return sb2;
    }
    
    private static void safeExecSQL(final SQLiteDatabase sqLiteDatabase, final String s) {
        try {
            sqLiteDatabase.execSQL(s);
        }
        catch (final SQLiteException ex) {
            ex.printStackTrace();
        }
    }
    
    private static void upgradeFromV5ToV6(final SQLiteDatabase sqLiteDatabase) {
        OneSignalDbHelper.outcomeTableProvider.upgradeOutcomeTableRevision1To2(sqLiteDatabase);
    }
    
    private static void upgradeToV2(final SQLiteDatabase sqLiteDatabase) {
        safeExecSQL(sqLiteDatabase, "ALTER TABLE notification ADD COLUMN collapse_id TEXT;");
        safeExecSQL(sqLiteDatabase, "CREATE INDEX notification_group_id_idx ON notification(group_id); ");
    }
    
    private static void upgradeToV3(final SQLiteDatabase sqLiteDatabase) {
        safeExecSQL(sqLiteDatabase, "ALTER TABLE notification ADD COLUMN expire_time TIMESTAMP;");
        safeExecSQL(sqLiteDatabase, "UPDATE notification SET expire_time = created_time + 259200;");
        safeExecSQL(sqLiteDatabase, "CREATE INDEX notification_expire_time_idx ON notification(expire_time); ");
    }
    
    private static void upgradeToV4(final SQLiteDatabase sqLiteDatabase) {
        safeExecSQL(sqLiteDatabase, "CREATE TABLE outcome (_id INTEGER PRIMARY KEY,notification_ids TEXT,name TEXT,session TEXT,params TEXT,timestamp TIMESTAMP);");
    }
    
    private static void upgradeToV5(final SQLiteDatabase sqLiteDatabase) {
        safeExecSQL(sqLiteDatabase, "CREATE TABLE cached_unique_outcome_notification (_id INTEGER PRIMARY KEY,notification_id TEXT,name TEXT);");
        upgradeFromV5ToV6(sqLiteDatabase);
    }
    
    private static void upgradeToV7(final SQLiteDatabase sqLiteDatabase) {
        safeExecSQL(sqLiteDatabase, "CREATE TABLE in_app_message (_id INTEGER PRIMARY KEY,display_quantity INTEGER,last_display INTEGER,message_id TEXT,displayed_in_session INTEGER,click_ids TEXT);");
    }
    
    private void upgradeToV8(final SQLiteDatabase sqLiteDatabase) {
        OneSignalDbHelper.outcomeTableProvider.upgradeOutcomeTableRevision2To3(sqLiteDatabase);
        OneSignalDbHelper.outcomeTableProvider.upgradeCacheOutcomeTableRevision1To2(sqLiteDatabase);
    }
    
    public void delete(final String p0, final String p1, final String[] p2) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     3: astore          4
        //     5: aload           4
        //     7: dup            
        //     8: astore          9
        //    10: monitorenter   
        //    11: aload_0        
        //    12: invokespecial   com/onesignal/OneSignalDbHelper.getSQLiteDatabaseWithRetries:()Landroid/database/sqlite/SQLiteDatabase;
        //    15: astore          5
        //    17: aload           5
        //    19: invokevirtual   android/database/sqlite/SQLiteDatabase.beginTransaction:()V
        //    22: aload           5
        //    24: aload_1        
        //    25: aload_2        
        //    26: aload_3        
        //    27: invokevirtual   android/database/sqlite/SQLiteDatabase.delete:(Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;)I
        //    30: pop            
        //    31: aload           5
        //    33: invokevirtual   android/database/sqlite/SQLiteDatabase.setTransactionSuccessful:()V
        //    36: aload           5
        //    38: ifnull          291
        //    41: aload           5
        //    43: invokevirtual   android/database/sqlite/SQLiteDatabase.endTransaction:()V
        //    46: goto            291
        //    49: astore_1       
        //    50: getstatic       com/onesignal/OneSignalDbHelper.logger:Lcom/onesignal/OSLogger;
        //    53: astore_2       
        //    54: aload_2        
        //    55: ldc             "Error closing transaction! "
        //    57: aload_1        
        //    58: invokeinterface com/onesignal/OSLogger.error:(Ljava/lang/String;Ljava/lang/Throwable;)V
        //    63: goto            291
        //    66: astore_1       
        //    67: getstatic       com/onesignal/OneSignalDbHelper.logger:Lcom/onesignal/OSLogger;
        //    70: astore_2       
        //    71: aload_2        
        //    72: ldc             "Error closing transaction! "
        //    74: aload_1        
        //    75: invokeinterface com/onesignal/OSLogger.error:(Ljava/lang/String;Ljava/lang/Throwable;)V
        //    80: goto            291
        //    83: astore_1       
        //    84: goto            295
        //    87: astore          6
        //    89: getstatic       com/onesignal/OneSignalDbHelper.logger:Lcom/onesignal/OSLogger;
        //    92: astore          7
        //    94: new             Ljava/lang/StringBuilder;
        //    97: astore          8
        //    99: aload           8
        //   101: ldc_w           "Error under delete transaction under table: "
        //   104: invokespecial   java/lang/StringBuilder.<init>:(Ljava/lang/String;)V
        //   107: aload           8
        //   109: aload_1        
        //   110: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   113: pop            
        //   114: aload           8
        //   116: ldc_w           " with whereClause: "
        //   119: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   122: pop            
        //   123: aload           8
        //   125: aload_2        
        //   126: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   129: pop            
        //   130: aload           8
        //   132: ldc_w           " and whereArgs: "
        //   135: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   138: pop            
        //   139: aload           8
        //   141: aload_3        
        //   142: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   145: pop            
        //   146: aload           7
        //   148: aload           8
        //   150: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   153: aload           6
        //   155: invokeinterface com/onesignal/OSLogger.error:(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   160: aload           5
        //   162: ifnull          291
        //   165: aload           5
        //   167: invokevirtual   android/database/sqlite/SQLiteDatabase.endTransaction:()V
        //   170: goto            291
        //   173: astore_1       
        //   174: getstatic       com/onesignal/OneSignalDbHelper.logger:Lcom/onesignal/OSLogger;
        //   177: astore_2       
        //   178: goto            54
        //   181: astore_1       
        //   182: getstatic       com/onesignal/OneSignalDbHelper.logger:Lcom/onesignal/OSLogger;
        //   185: astore_2       
        //   186: goto            71
        //   189: astore          8
        //   191: getstatic       com/onesignal/OneSignalDbHelper.logger:Lcom/onesignal/OSLogger;
        //   194: astore          6
        //   196: new             Ljava/lang/StringBuilder;
        //   199: astore          7
        //   201: aload           7
        //   203: ldc_w           "Error deleting on table: "
        //   206: invokespecial   java/lang/StringBuilder.<init>:(Ljava/lang/String;)V
        //   209: aload           7
        //   211: aload_1        
        //   212: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   215: pop            
        //   216: aload           7
        //   218: ldc_w           " with whereClause: "
        //   221: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   224: pop            
        //   225: aload           7
        //   227: aload_2        
        //   228: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   231: pop            
        //   232: aload           7
        //   234: ldc_w           " and whereArgs: "
        //   237: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   240: pop            
        //   241: aload           7
        //   243: aload_3        
        //   244: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   247: pop            
        //   248: aload           6
        //   250: aload           7
        //   252: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   255: aload           8
        //   257: invokeinterface com/onesignal/OSLogger.error:(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   262: aload           5
        //   264: ifnull          291
        //   267: aload           5
        //   269: invokevirtual   android/database/sqlite/SQLiteDatabase.endTransaction:()V
        //   272: goto            291
        //   275: astore_1       
        //   276: getstatic       com/onesignal/OneSignalDbHelper.logger:Lcom/onesignal/OSLogger;
        //   279: astore_2       
        //   280: goto            54
        //   283: astore_1       
        //   284: getstatic       com/onesignal/OneSignalDbHelper.logger:Lcom/onesignal/OSLogger;
        //   287: astore_2       
        //   288: goto            71
        //   291: aload           9
        //   293: monitorexit    
        //   294: return         
        //   295: aload           5
        //   297: ifnull          335
        //   300: aload           5
        //   302: invokevirtual   android/database/sqlite/SQLiteDatabase.endTransaction:()V
        //   305: goto            335
        //   308: astore_2       
        //   309: getstatic       com/onesignal/OneSignalDbHelper.logger:Lcom/onesignal/OSLogger;
        //   312: ldc             "Error closing transaction! "
        //   314: aload_2        
        //   315: invokeinterface com/onesignal/OSLogger.error:(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   320: goto            335
        //   323: astore_2       
        //   324: getstatic       com/onesignal/OneSignalDbHelper.logger:Lcom/onesignal/OSLogger;
        //   327: ldc             "Error closing transaction! "
        //   329: aload_2        
        //   330: invokeinterface com/onesignal/OSLogger.error:(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   335: aload_1        
        //   336: athrow         
        //   337: astore_1       
        //   338: aload           9
        //   340: monitorexit    
        //   341: aload_1        
        //   342: athrow         
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                                     
        //  -----  -----  -----  -----  -----------------------------------------
        //  11     17     337    343    Any
        //  17     36     189    291    Landroid/database/sqlite/SQLiteException;
        //  17     36     87     189    Ljava/lang/IllegalStateException;
        //  17     36     83     337    Any
        //  41     46     66     71     Ljava/lang/IllegalStateException;
        //  41     46     49     54     Landroid/database/sqlite/SQLiteException;
        //  41     46     337    343    Any
        //  50     54     337    343    Any
        //  54     63     337    343    Any
        //  67     71     337    343    Any
        //  71     80     337    343    Any
        //  89     160    83     337    Any
        //  165    170    181    189    Ljava/lang/IllegalStateException;
        //  165    170    173    181    Landroid/database/sqlite/SQLiteException;
        //  165    170    337    343    Any
        //  174    178    337    343    Any
        //  182    186    337    343    Any
        //  191    262    83     337    Any
        //  267    272    283    291    Ljava/lang/IllegalStateException;
        //  267    272    275    283    Landroid/database/sqlite/SQLiteException;
        //  267    272    337    343    Any
        //  276    280    337    343    Any
        //  284    288    337    343    Any
        //  291    294    337    343    Any
        //  300    305    323    335    Ljava/lang/IllegalStateException;
        //  300    305    308    323    Landroid/database/sqlite/SQLiteException;
        //  300    305    337    343    Any
        //  309    320    337    343    Any
        //  324    335    337    343    Any
        //  335    337    337    343    Any
        //  338    341    337    343    Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IndexOutOfBoundsException: Index 160 out of bounds for length 160
        //     at jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:64)
        //     at jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:70)
        //     at jdk.internal.util.Preconditions.checkIndex(Preconditions.java:266)
        //     at java.util.Objects.checkIndex(Objects.java:359)
        //     at java.util.ArrayList.get(ArrayList.java:434)
        //     at w5.a.o(SourceFile:31)
        //     at w5.a.o(SourceFile:733)
        //     at w5.a.o(SourceFile:323)
        //     at w5.a.j(SourceFile:218)
        //     at a6.j.j(SourceFile:23)
        //     at a6.j.i(SourceFile:28)
        //     at a6.i.n(SourceFile:7)
        //     at a6.i.m(SourceFile:174)
        //     at a6.i.c(SourceFile:67)
        //     at a6.i.r(SourceFile:328)
        //     at a6.i.s(SourceFile:17)
        //     at a6.i.q(SourceFile:29)
        //     at a6.i.b(SourceFile:33)
        //     at y5.d.e(SourceFile:6)
        //     at y5.d.b(SourceFile:1)
        //     at com.thesourceofcode.jadec.decompilers.JavaExtractionWorker.decompileWithProcyon(SourceFile:306)
        //     at com.thesourceofcode.jadec.decompilers.JavaExtractionWorker.doWork(SourceFile:131)
        //     at com.thesourceofcode.jadec.decompilers.BaseDecompiler.withAttempt(SourceFile:3)
        //     at com.thesourceofcode.jadec.workers.DecompilerWorker.d(SourceFile:53)
        //     at com.thesourceofcode.jadec.workers.DecompilerWorker.b(SourceFile:1)
        //     at e7.a.run(SourceFile:1)
        //     at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1145)
        //     at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:644)
        //     at java.lang.Thread.run(Thread.java:1012)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void insert(final String p0, final String p1, final ContentValues p2) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     3: astore          4
        //     5: aload           4
        //     7: dup            
        //     8: astore          9
        //    10: monitorenter   
        //    11: aload_0        
        //    12: invokespecial   com/onesignal/OneSignalDbHelper.getSQLiteDatabaseWithRetries:()Landroid/database/sqlite/SQLiteDatabase;
        //    15: astore          5
        //    17: aload           5
        //    19: invokevirtual   android/database/sqlite/SQLiteDatabase.beginTransaction:()V
        //    22: aload           5
        //    24: aload_1        
        //    25: aload_2        
        //    26: aload_3        
        //    27: invokevirtual   android/database/sqlite/SQLiteDatabase.insert:(Ljava/lang/String;Ljava/lang/String;Landroid/content/ContentValues;)J
        //    30: pop2           
        //    31: aload           5
        //    33: invokevirtual   android/database/sqlite/SQLiteDatabase.setTransactionSuccessful:()V
        //    36: aload           5
        //    38: ifnull          291
        //    41: aload           5
        //    43: invokevirtual   android/database/sqlite/SQLiteDatabase.endTransaction:()V
        //    46: goto            291
        //    49: astore_1       
        //    50: getstatic       com/onesignal/OneSignalDbHelper.logger:Lcom/onesignal/OSLogger;
        //    53: astore_2       
        //    54: aload_2        
        //    55: ldc             "Error closing transaction! "
        //    57: aload_1        
        //    58: invokeinterface com/onesignal/OSLogger.error:(Ljava/lang/String;Ljava/lang/Throwable;)V
        //    63: goto            291
        //    66: astore_1       
        //    67: getstatic       com/onesignal/OneSignalDbHelper.logger:Lcom/onesignal/OSLogger;
        //    70: astore_2       
        //    71: aload_2        
        //    72: ldc             "Error closing transaction! "
        //    74: aload_1        
        //    75: invokeinterface com/onesignal/OSLogger.error:(Ljava/lang/String;Ljava/lang/Throwable;)V
        //    80: goto            291
        //    83: astore_1       
        //    84: goto            295
        //    87: astore          7
        //    89: getstatic       com/onesignal/OneSignalDbHelper.logger:Lcom/onesignal/OSLogger;
        //    92: astore          8
        //    94: new             Ljava/lang/StringBuilder;
        //    97: astore          6
        //    99: aload           6
        //   101: ldc_w           "Error under inserting transaction under table: "
        //   104: invokespecial   java/lang/StringBuilder.<init>:(Ljava/lang/String;)V
        //   107: aload           6
        //   109: aload_1        
        //   110: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   113: pop            
        //   114: aload           6
        //   116: ldc_w           " with nullColumnHack: "
        //   119: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   122: pop            
        //   123: aload           6
        //   125: aload_2        
        //   126: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   129: pop            
        //   130: aload           6
        //   132: ldc_w           " and values: "
        //   135: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   138: pop            
        //   139: aload           6
        //   141: aload_3        
        //   142: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   145: pop            
        //   146: aload           8
        //   148: aload           6
        //   150: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   153: aload           7
        //   155: invokeinterface com/onesignal/OSLogger.error:(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   160: aload           5
        //   162: ifnull          291
        //   165: aload           5
        //   167: invokevirtual   android/database/sqlite/SQLiteDatabase.endTransaction:()V
        //   170: goto            291
        //   173: astore_1       
        //   174: getstatic       com/onesignal/OneSignalDbHelper.logger:Lcom/onesignal/OSLogger;
        //   177: astore_2       
        //   178: goto            54
        //   181: astore_1       
        //   182: getstatic       com/onesignal/OneSignalDbHelper.logger:Lcom/onesignal/OSLogger;
        //   185: astore_2       
        //   186: goto            71
        //   189: astore          7
        //   191: getstatic       com/onesignal/OneSignalDbHelper.logger:Lcom/onesignal/OSLogger;
        //   194: astore          8
        //   196: new             Ljava/lang/StringBuilder;
        //   199: astore          6
        //   201: aload           6
        //   203: ldc_w           "Error inserting on table: "
        //   206: invokespecial   java/lang/StringBuilder.<init>:(Ljava/lang/String;)V
        //   209: aload           6
        //   211: aload_1        
        //   212: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   215: pop            
        //   216: aload           6
        //   218: ldc_w           " with nullColumnHack: "
        //   221: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   224: pop            
        //   225: aload           6
        //   227: aload_2        
        //   228: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   231: pop            
        //   232: aload           6
        //   234: ldc_w           " and values: "
        //   237: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   240: pop            
        //   241: aload           6
        //   243: aload_3        
        //   244: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   247: pop            
        //   248: aload           8
        //   250: aload           6
        //   252: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   255: aload           7
        //   257: invokeinterface com/onesignal/OSLogger.error:(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   262: aload           5
        //   264: ifnull          291
        //   267: aload           5
        //   269: invokevirtual   android/database/sqlite/SQLiteDatabase.endTransaction:()V
        //   272: goto            291
        //   275: astore_1       
        //   276: getstatic       com/onesignal/OneSignalDbHelper.logger:Lcom/onesignal/OSLogger;
        //   279: astore_2       
        //   280: goto            54
        //   283: astore_1       
        //   284: getstatic       com/onesignal/OneSignalDbHelper.logger:Lcom/onesignal/OSLogger;
        //   287: astore_2       
        //   288: goto            71
        //   291: aload           9
        //   293: monitorexit    
        //   294: return         
        //   295: aload           5
        //   297: ifnull          335
        //   300: aload           5
        //   302: invokevirtual   android/database/sqlite/SQLiteDatabase.endTransaction:()V
        //   305: goto            335
        //   308: astore_2       
        //   309: getstatic       com/onesignal/OneSignalDbHelper.logger:Lcom/onesignal/OSLogger;
        //   312: ldc             "Error closing transaction! "
        //   314: aload_2        
        //   315: invokeinterface com/onesignal/OSLogger.error:(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   320: goto            335
        //   323: astore_2       
        //   324: getstatic       com/onesignal/OneSignalDbHelper.logger:Lcom/onesignal/OSLogger;
        //   327: ldc             "Error closing transaction! "
        //   329: aload_2        
        //   330: invokeinterface com/onesignal/OSLogger.error:(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   335: aload_1        
        //   336: athrow         
        //   337: astore_1       
        //   338: aload           9
        //   340: monitorexit    
        //   341: aload_1        
        //   342: athrow         
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                                     
        //  -----  -----  -----  -----  -----------------------------------------
        //  11     17     337    343    Any
        //  17     36     189    291    Landroid/database/sqlite/SQLiteException;
        //  17     36     87     189    Ljava/lang/IllegalStateException;
        //  17     36     83     337    Any
        //  41     46     66     71     Ljava/lang/IllegalStateException;
        //  41     46     49     54     Landroid/database/sqlite/SQLiteException;
        //  41     46     337    343    Any
        //  50     54     337    343    Any
        //  54     63     337    343    Any
        //  67     71     337    343    Any
        //  71     80     337    343    Any
        //  89     160    83     337    Any
        //  165    170    181    189    Ljava/lang/IllegalStateException;
        //  165    170    173    181    Landroid/database/sqlite/SQLiteException;
        //  165    170    337    343    Any
        //  174    178    337    343    Any
        //  182    186    337    343    Any
        //  191    262    83     337    Any
        //  267    272    283    291    Ljava/lang/IllegalStateException;
        //  267    272    275    283    Landroid/database/sqlite/SQLiteException;
        //  267    272    337    343    Any
        //  276    280    337    343    Any
        //  284    288    337    343    Any
        //  291    294    337    343    Any
        //  300    305    323    335    Ljava/lang/IllegalStateException;
        //  300    305    308    323    Landroid/database/sqlite/SQLiteException;
        //  300    305    337    343    Any
        //  309    320    337    343    Any
        //  324    335    337    343    Any
        //  335    337    337    343    Any
        //  338    341    337    343    Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IndexOutOfBoundsException: Index 160 out of bounds for length 160
        //     at jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:64)
        //     at jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:70)
        //     at jdk.internal.util.Preconditions.checkIndex(Preconditions.java:266)
        //     at java.util.Objects.checkIndex(Objects.java:359)
        //     at java.util.ArrayList.get(ArrayList.java:434)
        //     at w5.a.o(SourceFile:31)
        //     at w5.a.o(SourceFile:733)
        //     at w5.a.o(SourceFile:323)
        //     at w5.a.j(SourceFile:218)
        //     at a6.j.j(SourceFile:23)
        //     at a6.j.i(SourceFile:28)
        //     at a6.i.n(SourceFile:7)
        //     at a6.i.m(SourceFile:174)
        //     at a6.i.c(SourceFile:67)
        //     at a6.i.r(SourceFile:328)
        //     at a6.i.s(SourceFile:17)
        //     at a6.i.q(SourceFile:29)
        //     at a6.i.b(SourceFile:33)
        //     at y5.d.e(SourceFile:6)
        //     at y5.d.b(SourceFile:1)
        //     at com.thesourceofcode.jadec.decompilers.JavaExtractionWorker.decompileWithProcyon(SourceFile:306)
        //     at com.thesourceofcode.jadec.decompilers.JavaExtractionWorker.doWork(SourceFile:131)
        //     at com.thesourceofcode.jadec.decompilers.BaseDecompiler.withAttempt(SourceFile:3)
        //     at com.thesourceofcode.jadec.workers.DecompilerWorker.d(SourceFile:53)
        //     at com.thesourceofcode.jadec.workers.DecompilerWorker.b(SourceFile:1)
        //     at e7.a.run(SourceFile:1)
        //     at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1145)
        //     at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:644)
        //     at java.lang.Thread.run(Thread.java:1012)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void insertOrThrow(final String p0, final String p1, final ContentValues p2) throws SQLException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     3: astore          4
        //     5: aload           4
        //     7: dup            
        //     8: astore          9
        //    10: monitorenter   
        //    11: aload_0        
        //    12: invokespecial   com/onesignal/OneSignalDbHelper.getSQLiteDatabaseWithRetries:()Landroid/database/sqlite/SQLiteDatabase;
        //    15: astore          5
        //    17: aload           5
        //    19: invokevirtual   android/database/sqlite/SQLiteDatabase.beginTransaction:()V
        //    22: aload           5
        //    24: aload_1        
        //    25: aload_2        
        //    26: aload_3        
        //    27: invokevirtual   android/database/sqlite/SQLiteDatabase.insertOrThrow:(Ljava/lang/String;Ljava/lang/String;Landroid/content/ContentValues;)J
        //    30: pop2           
        //    31: aload           5
        //    33: invokevirtual   android/database/sqlite/SQLiteDatabase.setTransactionSuccessful:()V
        //    36: aload           5
        //    38: ifnull          291
        //    41: aload           5
        //    43: invokevirtual   android/database/sqlite/SQLiteDatabase.endTransaction:()V
        //    46: goto            291
        //    49: astore_1       
        //    50: getstatic       com/onesignal/OneSignalDbHelper.logger:Lcom/onesignal/OSLogger;
        //    53: astore_2       
        //    54: aload_2        
        //    55: ldc             "Error closing transaction! "
        //    57: aload_1        
        //    58: invokeinterface com/onesignal/OSLogger.error:(Ljava/lang/String;Ljava/lang/Throwable;)V
        //    63: goto            291
        //    66: astore_1       
        //    67: getstatic       com/onesignal/OneSignalDbHelper.logger:Lcom/onesignal/OSLogger;
        //    70: astore_2       
        //    71: aload_2        
        //    72: ldc             "Error closing transaction! "
        //    74: aload_1        
        //    75: invokeinterface com/onesignal/OSLogger.error:(Ljava/lang/String;Ljava/lang/Throwable;)V
        //    80: goto            291
        //    83: astore_1       
        //    84: goto            295
        //    87: astore          8
        //    89: getstatic       com/onesignal/OneSignalDbHelper.logger:Lcom/onesignal/OSLogger;
        //    92: astore          6
        //    94: new             Ljava/lang/StringBuilder;
        //    97: astore          7
        //    99: aload           7
        //   101: ldc_w           "Error under inserting or throw transaction under table: "
        //   104: invokespecial   java/lang/StringBuilder.<init>:(Ljava/lang/String;)V
        //   107: aload           7
        //   109: aload_1        
        //   110: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   113: pop            
        //   114: aload           7
        //   116: ldc_w           " with nullColumnHack: "
        //   119: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   122: pop            
        //   123: aload           7
        //   125: aload_2        
        //   126: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   129: pop            
        //   130: aload           7
        //   132: ldc_w           " and values: "
        //   135: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   138: pop            
        //   139: aload           7
        //   141: aload_3        
        //   142: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   145: pop            
        //   146: aload           6
        //   148: aload           7
        //   150: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   153: aload           8
        //   155: invokeinterface com/onesignal/OSLogger.error:(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   160: aload           5
        //   162: ifnull          291
        //   165: aload           5
        //   167: invokevirtual   android/database/sqlite/SQLiteDatabase.endTransaction:()V
        //   170: goto            291
        //   173: astore_1       
        //   174: getstatic       com/onesignal/OneSignalDbHelper.logger:Lcom/onesignal/OSLogger;
        //   177: astore_2       
        //   178: goto            54
        //   181: astore_1       
        //   182: getstatic       com/onesignal/OneSignalDbHelper.logger:Lcom/onesignal/OSLogger;
        //   185: astore_2       
        //   186: goto            71
        //   189: astore          8
        //   191: getstatic       com/onesignal/OneSignalDbHelper.logger:Lcom/onesignal/OSLogger;
        //   194: astore          7
        //   196: new             Ljava/lang/StringBuilder;
        //   199: astore          6
        //   201: aload           6
        //   203: ldc_w           "Error inserting or throw on table: "
        //   206: invokespecial   java/lang/StringBuilder.<init>:(Ljava/lang/String;)V
        //   209: aload           6
        //   211: aload_1        
        //   212: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   215: pop            
        //   216: aload           6
        //   218: ldc_w           " with nullColumnHack: "
        //   221: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   224: pop            
        //   225: aload           6
        //   227: aload_2        
        //   228: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   231: pop            
        //   232: aload           6
        //   234: ldc_w           " and values: "
        //   237: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   240: pop            
        //   241: aload           6
        //   243: aload_3        
        //   244: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   247: pop            
        //   248: aload           7
        //   250: aload           6
        //   252: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   255: aload           8
        //   257: invokeinterface com/onesignal/OSLogger.error:(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   262: aload           5
        //   264: ifnull          291
        //   267: aload           5
        //   269: invokevirtual   android/database/sqlite/SQLiteDatabase.endTransaction:()V
        //   272: goto            291
        //   275: astore_1       
        //   276: getstatic       com/onesignal/OneSignalDbHelper.logger:Lcom/onesignal/OSLogger;
        //   279: astore_2       
        //   280: goto            54
        //   283: astore_1       
        //   284: getstatic       com/onesignal/OneSignalDbHelper.logger:Lcom/onesignal/OSLogger;
        //   287: astore_2       
        //   288: goto            71
        //   291: aload           9
        //   293: monitorexit    
        //   294: return         
        //   295: aload           5
        //   297: ifnull          335
        //   300: aload           5
        //   302: invokevirtual   android/database/sqlite/SQLiteDatabase.endTransaction:()V
        //   305: goto            335
        //   308: astore_2       
        //   309: getstatic       com/onesignal/OneSignalDbHelper.logger:Lcom/onesignal/OSLogger;
        //   312: ldc             "Error closing transaction! "
        //   314: aload_2        
        //   315: invokeinterface com/onesignal/OSLogger.error:(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   320: goto            335
        //   323: astore_2       
        //   324: getstatic       com/onesignal/OneSignalDbHelper.logger:Lcom/onesignal/OSLogger;
        //   327: ldc             "Error closing transaction! "
        //   329: aload_2        
        //   330: invokeinterface com/onesignal/OSLogger.error:(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   335: aload_1        
        //   336: athrow         
        //   337: astore_1       
        //   338: aload           9
        //   340: monitorexit    
        //   341: aload_1        
        //   342: athrow         
        //    Exceptions:
        //  throws android.database.SQLException
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                                     
        //  -----  -----  -----  -----  -----------------------------------------
        //  11     17     337    343    Any
        //  17     36     189    291    Landroid/database/sqlite/SQLiteException;
        //  17     36     87     189    Ljava/lang/IllegalStateException;
        //  17     36     83     337    Any
        //  41     46     66     71     Ljava/lang/IllegalStateException;
        //  41     46     49     54     Landroid/database/sqlite/SQLiteException;
        //  41     46     337    343    Any
        //  50     54     337    343    Any
        //  54     63     337    343    Any
        //  67     71     337    343    Any
        //  71     80     337    343    Any
        //  89     160    83     337    Any
        //  165    170    181    189    Ljava/lang/IllegalStateException;
        //  165    170    173    181    Landroid/database/sqlite/SQLiteException;
        //  165    170    337    343    Any
        //  174    178    337    343    Any
        //  182    186    337    343    Any
        //  191    262    83     337    Any
        //  267    272    283    291    Ljava/lang/IllegalStateException;
        //  267    272    275    283    Landroid/database/sqlite/SQLiteException;
        //  267    272    337    343    Any
        //  276    280    337    343    Any
        //  284    288    337    343    Any
        //  291    294    337    343    Any
        //  300    305    323    335    Ljava/lang/IllegalStateException;
        //  300    305    308    323    Landroid/database/sqlite/SQLiteException;
        //  300    305    337    343    Any
        //  309    320    337    343    Any
        //  324    335    337    343    Any
        //  335    337    337    343    Any
        //  338    341    337    343    Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IndexOutOfBoundsException: Index 160 out of bounds for length 160
        //     at jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:64)
        //     at jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:70)
        //     at jdk.internal.util.Preconditions.checkIndex(Preconditions.java:266)
        //     at java.util.Objects.checkIndex(Objects.java:359)
        //     at java.util.ArrayList.get(ArrayList.java:434)
        //     at w5.a.o(SourceFile:31)
        //     at w5.a.o(SourceFile:733)
        //     at w5.a.o(SourceFile:323)
        //     at w5.a.j(SourceFile:218)
        //     at a6.j.j(SourceFile:23)
        //     at a6.j.i(SourceFile:28)
        //     at a6.i.n(SourceFile:7)
        //     at a6.i.m(SourceFile:174)
        //     at a6.i.c(SourceFile:67)
        //     at a6.i.r(SourceFile:328)
        //     at a6.i.s(SourceFile:17)
        //     at a6.i.q(SourceFile:29)
        //     at a6.i.b(SourceFile:33)
        //     at y5.d.e(SourceFile:6)
        //     at y5.d.b(SourceFile:1)
        //     at com.thesourceofcode.jadec.decompilers.JavaExtractionWorker.decompileWithProcyon(SourceFile:306)
        //     at com.thesourceofcode.jadec.decompilers.JavaExtractionWorker.doWork(SourceFile:131)
        //     at com.thesourceofcode.jadec.decompilers.BaseDecompiler.withAttempt(SourceFile:3)
        //     at com.thesourceofcode.jadec.workers.DecompilerWorker.d(SourceFile:53)
        //     at com.thesourceofcode.jadec.workers.DecompilerWorker.b(SourceFile:1)
        //     at e7.a.run(SourceFile:1)
        //     at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1145)
        //     at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:644)
        //     at java.lang.Thread.run(Thread.java:1012)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void onCreate(final SQLiteDatabase sqLiteDatabase) {
        final Object lock = OneSignalDbHelper.LOCK;
        synchronized (lock) {
            sqLiteDatabase.execSQL("CREATE TABLE notification (_id INTEGER PRIMARY KEY,notification_id TEXT,android_notification_id INTEGER,group_id TEXT,collapse_id TEXT,is_summary INTEGER DEFAULT 0,opened INTEGER DEFAULT 0,dismissed INTEGER DEFAULT 0,title TEXT,message TEXT,full_data TEXT,created_time TIMESTAMP DEFAULT (strftime('%s', 'now')),expire_time TIMESTAMP);");
            sqLiteDatabase.execSQL("CREATE TABLE outcome (_id INTEGER PRIMARY KEY,notification_influence_type TEXT,iam_influence_type TEXT,notification_ids TEXT,iam_ids TEXT,name TEXT,timestamp TIMESTAMP,weight FLOAT);");
            sqLiteDatabase.execSQL("CREATE TABLE cached_unique_outcome (_id INTEGER PRIMARY KEY,channel_influence_id TEXT,channel_type TEXT,name TEXT);");
            sqLiteDatabase.execSQL("CREATE TABLE in_app_message (_id INTEGER PRIMARY KEY,display_quantity INTEGER,last_display INTEGER,message_id TEXT,displayed_in_session INTEGER,click_ids TEXT);");
            final String[] sql_INDEX_ENTRIES = OneSignalDbHelper.SQL_INDEX_ENTRIES;
            for (int length = sql_INDEX_ENTRIES.length, i = 0; i < length; ++i) {
                sqLiteDatabase.execSQL(sql_INDEX_ENTRIES[i]);
            }
        }
    }
    
    public void onDowngrade(final SQLiteDatabase sqLiteDatabase, final int n, final int n2) {
        OneSignal.Log(OneSignal$LOG_LEVEL.WARN, "SDK version rolled back! Clearing OneSignal.db as it could be in an unexpected state.");
        final Object lock = OneSignalDbHelper.LOCK;
        synchronized (lock) {
            final Cursor rawQuery = sqLiteDatabase.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", (String[])null);
            try {
                final ArrayList list = new ArrayList(rawQuery.getCount());
                while (rawQuery.moveToNext()) {
                    ((List)list).add((Object)rawQuery.getString(0));
                }
                for (final String s : list) {
                    if (s.startsWith("sqlite_")) {
                        continue;
                    }
                    final StringBuilder sb = new StringBuilder();
                    sb.append("DROP TABLE IF EXISTS ");
                    sb.append(s);
                    sqLiteDatabase.execSQL(sb.toString());
                }
                rawQuery.close();
                this.onCreate(sqLiteDatabase);
            }
            finally {
                rawQuery.close();
            }
        }
    }
    
    public void onUpgrade(final SQLiteDatabase sqLiteDatabase, final int n, final int n2) {
        final OneSignal$LOG_LEVEL debug = OneSignal$LOG_LEVEL.DEBUG;
        final StringBuilder sb = new StringBuilder("OneSignal Database onUpgrade from: ");
        sb.append(n);
        sb.append(" to: ");
        sb.append(n2);
        OneSignal.Log(debug, sb.toString());
        final Object lock;
        monitorenter(lock = OneSignalDbHelper.LOCK);
        try {
            try {
                this.internalOnUpgrade(sqLiteDatabase, n);
            }
            finally {
                monitorexit(lock);
                monitorexit(lock);
            }
        }
        catch (final SQLiteException ex) {}
    }
    
    public Cursor query(final String s, final String[] array, final String s2, final String[] array2, final String s3, final String s4, final String s5) {
        final Object lock = OneSignalDbHelper.LOCK;
        synchronized (lock) {
            return this.getSQLiteDatabaseWithRetries().query(s, array, s2, array2, s3, s4, s5);
        }
    }
    
    public Cursor query(final String s, final String[] array, final String s2, final String[] array2, final String s3, final String s4, final String s5, final String s6) {
        final Object lock = OneSignalDbHelper.LOCK;
        synchronized (lock) {
            return this.getSQLiteDatabaseWithRetries().query(s, array, s2, array2, s3, s4, s5, s6);
        }
    }
    
    void setOutcomeTableProvider(final OSOutcomeTableProvider outcomeTableProvider) {
        OneSignalDbHelper.outcomeTableProvider = outcomeTableProvider;
    }
    
    public int update(final String p0, final ContentValues p1, final String p2, final String[] p3) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: istore          8
        //     3: iconst_0       
        //     4: istore          7
        //     6: aload_2        
        //     7: ifnull          441
        //    10: aload_2        
        //    11: invokevirtual   android/content/ContentValues.toString:()Ljava/lang/String;
        //    14: invokevirtual   java/lang/String.isEmpty:()Z
        //    17: ifeq            23
        //    20: goto            441
        //    23: getstatic       com/onesignal/OneSignalDbHelper.LOCK:Ljava/lang/Object;
        //    26: astore          9
        //    28: aload           9
        //    30: dup            
        //    31: astore          13
        //    33: monitorenter   
        //    34: aload_0        
        //    35: invokespecial   com/onesignal/OneSignalDbHelper.getSQLiteDatabaseWithRetries:()Landroid/database/sqlite/SQLiteDatabase;
        //    38: astore          10
        //    40: iload           7
        //    42: istore          5
        //    44: iload           8
        //    46: istore          6
        //    48: aload           10
        //    50: invokevirtual   android/database/sqlite/SQLiteDatabase.beginTransaction:()V
        //    53: iload           7
        //    55: istore          5
        //    57: iload           8
        //    59: istore          6
        //    61: aload           10
        //    63: aload_1        
        //    64: aload_2        
        //    65: aload_3        
        //    66: aload           4
        //    68: invokevirtual   android/database/sqlite/SQLiteDatabase.update:(Ljava/lang/String;Landroid/content/ContentValues;Ljava/lang/String;[Ljava/lang/String;)I
        //    71: istore          7
        //    73: iload           7
        //    75: istore          5
        //    77: iload           7
        //    79: istore          6
        //    81: aload           10
        //    83: invokevirtual   android/database/sqlite/SQLiteDatabase.setTransactionSuccessful:()V
        //    86: iload           7
        //    88: istore          8
        //    90: aload           10
        //    92: ifnull          387
        //    95: aload           10
        //    97: invokevirtual   android/database/sqlite/SQLiteDatabase.endTransaction:()V
        //   100: iload           7
        //   102: istore          8
        //   104: goto            387
        //   107: astore_1       
        //   108: getstatic       com/onesignal/OneSignalDbHelper.logger:Lcom/onesignal/OSLogger;
        //   111: astore_2       
        //   112: iload           7
        //   114: istore          5
        //   116: aload_2        
        //   117: ldc             "Error closing transaction! "
        //   119: aload_1        
        //   120: invokeinterface com/onesignal/OSLogger.error:(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   125: iload           5
        //   127: istore          8
        //   129: goto            387
        //   132: astore_1       
        //   133: getstatic       com/onesignal/OneSignalDbHelper.logger:Lcom/onesignal/OSLogger;
        //   136: astore_2       
        //   137: iload           7
        //   139: istore          5
        //   141: aload_2        
        //   142: ldc             "Error closing transaction! "
        //   144: aload_1        
        //   145: invokeinterface com/onesignal/OSLogger.error:(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   150: iload           5
        //   152: istore          8
        //   154: goto            387
        //   157: astore_1       
        //   158: goto            393
        //   161: astore_2       
        //   162: getstatic       com/onesignal/OneSignalDbHelper.logger:Lcom/onesignal/OSLogger;
        //   165: astore          11
        //   167: new             Ljava/lang/StringBuilder;
        //   170: astore          12
        //   172: aload           12
        //   174: ldc_w           "Error under update transaction under table: "
        //   177: invokespecial   java/lang/StringBuilder.<init>:(Ljava/lang/String;)V
        //   180: aload           12
        //   182: aload_1        
        //   183: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   186: pop            
        //   187: aload           12
        //   189: ldc_w           " with whereClause: "
        //   192: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   195: pop            
        //   196: aload           12
        //   198: aload_3        
        //   199: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   202: pop            
        //   203: aload           12
        //   205: ldc_w           " and whereArgs: "
        //   208: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   211: pop            
        //   212: aload           12
        //   214: aload           4
        //   216: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   219: pop            
        //   220: aload           11
        //   222: aload           12
        //   224: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   227: aload_2        
        //   228: invokeinterface com/onesignal/OSLogger.error:(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   233: iload           5
        //   235: istore          8
        //   237: aload           10
        //   239: ifnull          387
        //   242: aload           10
        //   244: invokevirtual   android/database/sqlite/SQLiteDatabase.endTransaction:()V
        //   247: iload           5
        //   249: istore          8
        //   251: goto            387
        //   254: astore_1       
        //   255: getstatic       com/onesignal/OneSignalDbHelper.logger:Lcom/onesignal/OSLogger;
        //   258: astore_2       
        //   259: goto            116
        //   262: astore_1       
        //   263: getstatic       com/onesignal/OneSignalDbHelper.logger:Lcom/onesignal/OSLogger;
        //   266: astore_2       
        //   267: goto            141
        //   270: astore_2       
        //   271: getstatic       com/onesignal/OneSignalDbHelper.logger:Lcom/onesignal/OSLogger;
        //   274: astore          11
        //   276: new             Ljava/lang/StringBuilder;
        //   279: astore          12
        //   281: aload           12
        //   283: ldc_w           "Error updating on table: "
        //   286: invokespecial   java/lang/StringBuilder.<init>:(Ljava/lang/String;)V
        //   289: aload           12
        //   291: aload_1        
        //   292: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   295: pop            
        //   296: aload           12
        //   298: ldc_w           " with whereClause: "
        //   301: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   304: pop            
        //   305: aload           12
        //   307: aload_3        
        //   308: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   311: pop            
        //   312: aload           12
        //   314: ldc_w           " and whereArgs: "
        //   317: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   320: pop            
        //   321: aload           12
        //   323: aload           4
        //   325: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   328: pop            
        //   329: aload           11
        //   331: aload           12
        //   333: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   336: aload_2        
        //   337: invokeinterface com/onesignal/OSLogger.error:(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   342: iload           6
        //   344: istore          8
        //   346: aload           10
        //   348: ifnull          387
        //   351: aload           10
        //   353: invokevirtual   android/database/sqlite/SQLiteDatabase.endTransaction:()V
        //   356: iload           6
        //   358: istore          8
        //   360: goto            387
        //   363: astore_1       
        //   364: getstatic       com/onesignal/OneSignalDbHelper.logger:Lcom/onesignal/OSLogger;
        //   367: astore_2       
        //   368: iload           6
        //   370: istore          5
        //   372: goto            116
        //   375: astore_1       
        //   376: getstatic       com/onesignal/OneSignalDbHelper.logger:Lcom/onesignal/OSLogger;
        //   379: astore_2       
        //   380: iload           6
        //   382: istore          5
        //   384: goto            141
        //   387: aload           13
        //   389: monitorexit    
        //   390: iload           8
        //   392: ireturn        
        //   393: aload           10
        //   395: ifnull          433
        //   398: aload           10
        //   400: invokevirtual   android/database/sqlite/SQLiteDatabase.endTransaction:()V
        //   403: goto            433
        //   406: astore_2       
        //   407: getstatic       com/onesignal/OneSignalDbHelper.logger:Lcom/onesignal/OSLogger;
        //   410: ldc             "Error closing transaction! "
        //   412: aload_2        
        //   413: invokeinterface com/onesignal/OSLogger.error:(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   418: goto            433
        //   421: astore_2       
        //   422: getstatic       com/onesignal/OneSignalDbHelper.logger:Lcom/onesignal/OSLogger;
        //   425: ldc             "Error closing transaction! "
        //   427: aload_2        
        //   428: invokeinterface com/onesignal/OSLogger.error:(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   433: aload_1        
        //   434: athrow         
        //   435: astore_1       
        //   436: aload           13
        //   438: monitorexit    
        //   439: aload_1        
        //   440: athrow         
        //   441: iconst_0       
        //   442: ireturn        
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                                     
        //  -----  -----  -----  -----  -----------------------------------------
        //  34     40     435    441    Any
        //  48     53     270    387    Landroid/database/sqlite/SQLiteException;
        //  48     53     161    270    Ljava/lang/IllegalStateException;
        //  48     53     157    435    Any
        //  61     73     270    387    Landroid/database/sqlite/SQLiteException;
        //  61     73     161    270    Ljava/lang/IllegalStateException;
        //  61     73     157    435    Any
        //  81     86     270    387    Landroid/database/sqlite/SQLiteException;
        //  81     86     161    270    Ljava/lang/IllegalStateException;
        //  81     86     157    435    Any
        //  95     100    132    141    Ljava/lang/IllegalStateException;
        //  95     100    107    116    Landroid/database/sqlite/SQLiteException;
        //  95     100    435    441    Any
        //  108    112    435    441    Any
        //  116    125    435    441    Any
        //  133    137    435    441    Any
        //  141    150    435    441    Any
        //  162    233    157    435    Any
        //  242    247    262    270    Ljava/lang/IllegalStateException;
        //  242    247    254    262    Landroid/database/sqlite/SQLiteException;
        //  242    247    435    441    Any
        //  255    259    435    441    Any
        //  263    267    435    441    Any
        //  271    342    157    435    Any
        //  351    356    375    387    Ljava/lang/IllegalStateException;
        //  351    356    363    375    Landroid/database/sqlite/SQLiteException;
        //  351    356    435    441    Any
        //  364    368    435    441    Any
        //  376    380    435    441    Any
        //  387    390    435    441    Any
        //  398    403    421    433    Ljava/lang/IllegalStateException;
        //  398    403    406    421    Landroid/database/sqlite/SQLiteException;
        //  398    403    435    441    Any
        //  407    418    435    441    Any
        //  422    433    435    441    Any
        //  433    435    435    441    Any
        //  436    439    435    441    Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IndexOutOfBoundsException: Index 211 out of bounds for length 211
        //     at jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:64)
        //     at jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:70)
        //     at jdk.internal.util.Preconditions.checkIndex(Preconditions.java:266)
        //     at java.util.Objects.checkIndex(Objects.java:359)
        //     at java.util.ArrayList.get(ArrayList.java:434)
        //     at w5.a.o(SourceFile:31)
        //     at w5.a.o(SourceFile:733)
        //     at w5.a.o(SourceFile:323)
        //     at w5.a.j(SourceFile:218)
        //     at a6.j.j(SourceFile:23)
        //     at a6.j.i(SourceFile:28)
        //     at a6.i.n(SourceFile:7)
        //     at a6.i.m(SourceFile:174)
        //     at a6.i.c(SourceFile:67)
        //     at a6.i.r(SourceFile:328)
        //     at a6.i.s(SourceFile:17)
        //     at a6.i.q(SourceFile:29)
        //     at a6.i.b(SourceFile:33)
        //     at y5.d.e(SourceFile:6)
        //     at y5.d.b(SourceFile:1)
        //     at com.thesourceofcode.jadec.decompilers.JavaExtractionWorker.decompileWithProcyon(SourceFile:306)
        //     at com.thesourceofcode.jadec.decompilers.JavaExtractionWorker.doWork(SourceFile:131)
        //     at com.thesourceofcode.jadec.decompilers.BaseDecompiler.withAttempt(SourceFile:3)
        //     at com.thesourceofcode.jadec.workers.DecompilerWorker.d(SourceFile:53)
        //     at com.thesourceofcode.jadec.workers.DecompilerWorker.b(SourceFile:1)
        //     at e7.a.run(SourceFile:1)
        //     at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1145)
        //     at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:644)
        //     at java.lang.Thread.run(Thread.java:1012)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
}
