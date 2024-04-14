package com.onesignal;

import java.util.Random;
import android.service.notification.StatusBarNotification;
import java.util.ArrayList;
import android.R$drawable;
import java.net.URL;
import android.content.res.AssetManager;
import java.util.Iterator;
import java.util.Arrays;
import android.graphics.BitmapFactory;
import java.math.BigInteger;
import android.content.ContentValues;
import android.net.Uri;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.NotificationCompat$InboxStyle;
import java.security.SecureRandom;
import java.lang.reflect.Field;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import org.json.JSONException;
import org.json.JSONArray;
import android.graphics.Bitmap;
import androidx.core.app.NotificationCompat$Style;
import android.widget.RemoteViews;
import android.os.Build$VERSION;
import androidx.core.app.NotificationCompat$Builder;
import java.util.List;
import org.json.JSONObject;
import android.content.Context;
import android.content.res.Resources;

class GenerateNotification
{
    public static final String BUNDLE_KEY_ACTION_ID = "actionId";
    public static final String BUNDLE_KEY_ANDROID_NOTIFICATION_ID = "androidNotificationId";
    public static final String BUNDLE_KEY_ONESIGNAL_DATA = "onesignalData";
    public static final String OS_SHOW_NOTIFICATION_THREAD = "OS_SHOW_NOTIFICATION_THREAD";
    private static Resources contextResources;
    private static Context currentContext;
    private static Integer groupAlertBehavior;
    private static Class<?> notificationDismissedClass;
    private static Class<?> notificationOpenedClass;
    private static String packageName;
    
    static {
        GenerateNotification.notificationOpenedClass = NotificationOpenedReceiver.class;
        GenerateNotification.notificationDismissedClass = NotificationDismissReceiver.class;
        GenerateNotification.contextResources = null;
        GenerateNotification.currentContext = null;
        GenerateNotification.packageName = null;
        GenerateNotification.groupAlertBehavior = null;
    }
    
    private static void addAlertButtons(final Context context, final JSONObject jsonObject, final List<String> list, final List<String> list2) {
        try {
            addCustomAlertButtons(jsonObject, list, list2);
        }
        finally {
            final Throwable t;
            OneSignal.Log(OneSignal.LOG_LEVEL.ERROR, "Failed to parse JSON for custom buttons for alert dialog.", t);
        }
        if (list.size() == 0 || list.size() < 3) {
            list.add((Object)OSUtils.getResourceString(context, "onesignal_in_app_alert_ok_button_text", "Ok"));
            list2.add((Object)"__DEFAULT__");
        }
    }
    
    private static void addBackgroundImage(final JSONObject jsonObject, final NotificationCompat$Builder notificationCompat$Builder) throws Throwable {
        if (Build$VERSION.SDK_INT >= 31) {
            final OneSignal.LOG_LEVEL verbose = OneSignal.LOG_LEVEL.VERBOSE;
            final StringBuilder sb = new StringBuilder("Cannot use background images in notifications for device on version: ");
            sb.append(Build$VERSION.SDK_INT);
            OneSignal.Log(verbose, sb.toString());
            return;
        }
        final String optString = jsonObject.optString("bg_img", (String)null);
        JSONObject jsonObject2;
        Bitmap bitmap;
        if (optString != null) {
            jsonObject2 = new JSONObject(optString);
            bitmap = getBitmap(jsonObject2.optString("img", (String)null));
        }
        else {
            bitmap = null;
            jsonObject2 = null;
        }
        Bitmap bitmapFromAssetsOrResourceName = bitmap;
        if (bitmap == null) {
            bitmapFromAssetsOrResourceName = getBitmapFromAssetsOrResourceName("onesignal_bgimage_default_image");
        }
        if (bitmapFromAssetsOrResourceName != null) {
            final RemoteViews content = new RemoteViews(GenerateNotification.currentContext.getPackageName(), R.layout.onesignal_bgimage_notif_layout);
            content.setTextViewText(R.id.os_bgimage_notif_title, getTitle(jsonObject));
            content.setTextViewText(R.id.os_bgimage_notif_body, (CharSequence)jsonObject.optString("alert"));
            setTextColor(content, jsonObject2, R.id.os_bgimage_notif_title, "tc", "onesignal_bgimage_notif_title_color");
            setTextColor(content, jsonObject2, R.id.os_bgimage_notif_body, "bc", "onesignal_bgimage_notif_body_color");
            String s;
            if (jsonObject2 != null && jsonObject2.has("img_align")) {
                s = jsonObject2.getString("img_align");
            }
            else {
                final int identifier = GenerateNotification.contextResources.getIdentifier("onesignal_bgimage_notif_image_align", "string", GenerateNotification.packageName);
                if (identifier != 0) {
                    s = GenerateNotification.contextResources.getString(identifier);
                }
                else {
                    s = null;
                }
            }
            if ("right".equals((Object)s)) {
                content.setViewPadding(R.id.os_bgimage_notif_bgimage_align_layout, -5000, 0, 0, 0);
                content.setImageViewBitmap(R.id.os_bgimage_notif_bgimage_right_aligned, bitmapFromAssetsOrResourceName);
                content.setViewVisibility(R.id.os_bgimage_notif_bgimage_right_aligned, 0);
                content.setViewVisibility(R.id.os_bgimage_notif_bgimage, 2);
            }
            else {
                content.setImageViewBitmap(R.id.os_bgimage_notif_bgimage, bitmapFromAssetsOrResourceName);
            }
            notificationCompat$Builder.setContent(content);
            notificationCompat$Builder.setStyle((NotificationCompat$Style)null);
        }
    }
    
    private static void addCustomAlertButtons(JSONObject jsonObject, final List<String> list, final List<String> list2) throws JSONException {
        jsonObject = new JSONObject(jsonObject.optString("custom"));
        if (!jsonObject.has("a")) {
            return;
        }
        jsonObject = jsonObject.getJSONObject("a");
        if (!jsonObject.has("actionButtons")) {
            return;
        }
        final JSONArray optJSONArray = jsonObject.optJSONArray("actionButtons");
        for (int i = 0; i < optJSONArray.length(); ++i) {
            final JSONObject jsonObject2 = optJSONArray.getJSONObject(i);
            list.add((Object)jsonObject2.optString("text"));
            list2.add((Object)jsonObject2.optString("id"));
        }
    }
    
    private static void addNotificationActionButtons(final JSONObject jsonObject, final IntentGeneratorForAttachingToNotifications intentGeneratorForAttachingToNotifications, final NotificationCompat$Builder notificationCompat$Builder, final int n, final String s) {
        try {
            final JSONObject jsonObject2 = new JSONObject(jsonObject.optString("custom"));
            if (!jsonObject2.has("a")) {
                return;
            }
            final JSONObject jsonObject3 = jsonObject2.getJSONObject("a");
            if (!jsonObject3.has("actionButtons")) {
                return;
            }
            final JSONArray jsonArray = jsonObject3.getJSONArray("actionButtons");
            for (int i = 0; i < jsonArray.length(); ++i) {
                final JSONObject optJSONObject = jsonArray.optJSONObject(i);
                final JSONObject jsonObject4 = new JSONObject(jsonObject.toString());
                final Intent newBaseIntent = intentGeneratorForAttachingToNotifications.getNewBaseIntent(n);
                final StringBuilder sb = new StringBuilder();
                sb.append("");
                sb.append(i);
                newBaseIntent.setAction(sb.toString());
                newBaseIntent.putExtra("action_button", true);
                jsonObject4.put("actionId", (Object)optJSONObject.optString("id"));
                newBaseIntent.putExtra("onesignalData", jsonObject4.toString());
                if (s != null) {
                    newBaseIntent.putExtra("summary", s);
                }
                else if (jsonObject.has("grp")) {
                    newBaseIntent.putExtra("grp", jsonObject.optString("grp"));
                }
                final PendingIntent newActionPendingIntent = intentGeneratorForAttachingToNotifications.getNewActionPendingIntent(n, newBaseIntent);
                int resourceIcon;
                if (optJSONObject.has("icon")) {
                    resourceIcon = getResourceIcon(optJSONObject.optString("icon"));
                }
                else {
                    resourceIcon = 0;
                }
                notificationCompat$Builder.addAction(resourceIcon, (CharSequence)optJSONObject.optString("text"), newActionPendingIntent);
            }
        }
        finally {
            final Throwable t;
            t.printStackTrace();
        }
    }
    
    private static void addXiaomiSettings(final OneSignalNotificationBuilder oneSignalNotificationBuilder, final Notification notification) {
        if (!oneSignalNotificationBuilder.hasLargeIcon) {
            return;
        }
        try {
            final Object instance = Class.forName("android.app.MiuiNotification").newInstance();
            final Field declaredField = instance.getClass().getDeclaredField("customizedIcon");
            declaredField.setAccessible(true);
            declaredField.set(instance, (Object)true);
            final Field field = notification.getClass().getField("extraNotification");
            field.setAccessible(true);
            field.set((Object)notification, instance);
        }
        finally {}
    }
    
    private static void applyNotificationExtender(final OSNotificationGenerationJob osNotificationGenerationJob, final NotificationCompat$Builder notificationCompat$Builder) {
        if (!osNotificationGenerationJob.hasExtender()) {
            return;
        }
        try {
            final Field declaredField = NotificationCompat$Builder.class.getDeclaredField("mNotification");
            declaredField.setAccessible(true);
            final Notification notification = (Notification)declaredField.get((Object)notificationCompat$Builder);
            osNotificationGenerationJob.setOrgFlags(notification.flags);
            osNotificationGenerationJob.setOrgSound(notification.sound);
            notificationCompat$Builder.extend(osNotificationGenerationJob.getNotification().getNotificationExtender());
            final Notification notification2 = (Notification)declaredField.get((Object)notificationCompat$Builder);
            final Field declaredField2 = NotificationCompat$Builder.class.getDeclaredField("mContentText");
            declaredField2.setAccessible(true);
            final CharSequence overriddenBodyFromExtender = (CharSequence)declaredField2.get((Object)notificationCompat$Builder);
            final Field declaredField3 = NotificationCompat$Builder.class.getDeclaredField("mContentTitle");
            declaredField3.setAccessible(true);
            final CharSequence overriddenTitleFromExtender = (CharSequence)declaredField3.get((Object)notificationCompat$Builder);
            osNotificationGenerationJob.setOverriddenBodyFromExtender(overriddenBodyFromExtender);
            osNotificationGenerationJob.setOverriddenTitleFromExtender(overriddenTitleFromExtender);
            if (!osNotificationGenerationJob.isRestoring()) {
                osNotificationGenerationJob.setOverriddenFlags(notification2.flags);
                osNotificationGenerationJob.setOverriddenSound(notification2.sound);
            }
        }
        finally {
            final Throwable t;
            t.printStackTrace();
        }
    }
    
    private static int convertOSToAndroidPriority(final int n) {
        if (n > 9) {
            return 2;
        }
        if (n > 7) {
            return 1;
        }
        if (n > 4) {
            return 0;
        }
        if (n > 2) {
            return -1;
        }
        return -2;
    }
    
    private static Intent createBaseSummaryIntent(final int n, final IntentGeneratorForAttachingToNotifications intentGeneratorForAttachingToNotifications, final JSONObject jsonObject, final String s) {
        return intentGeneratorForAttachingToNotifications.getNewBaseIntent(n).putExtra("onesignalData", jsonObject.toString()).putExtra("summary", s);
    }
    
    private static void createGenericPendingIntentsForGroup(final NotificationCompat$Builder notificationCompat$Builder, final IntentGeneratorForAttachingToNotifications intentGeneratorForAttachingToNotifications, final JSONObject jsonObject, final String group, final int n) {
        final SecureRandom secureRandom = new SecureRandom();
        notificationCompat$Builder.setContentIntent(intentGeneratorForAttachingToNotifications.getNewActionPendingIntent(((Random)secureRandom).nextInt(), intentGeneratorForAttachingToNotifications.getNewBaseIntent(n).putExtra("onesignalData", jsonObject.toString()).putExtra("grp", group)));
        notificationCompat$Builder.setDeleteIntent(getNewDismissActionPendingIntent(((Random)secureRandom).nextInt(), getNewBaseDismissIntent(n).putExtra("grp", group)));
        notificationCompat$Builder.setGroup(group);
        try {
            notificationCompat$Builder.setGroupAlertBehavior((int)GenerateNotification.groupAlertBehavior);
        }
        finally {}
    }
    
    private static Notification createGenericPendingIntentsForNotif(final NotificationCompat$Builder notificationCompat$Builder, final IntentGeneratorForAttachingToNotifications intentGeneratorForAttachingToNotifications, final JSONObject jsonObject, final int n) {
        final SecureRandom secureRandom = new SecureRandom();
        notificationCompat$Builder.setContentIntent(intentGeneratorForAttachingToNotifications.getNewActionPendingIntent(((Random)secureRandom).nextInt(), intentGeneratorForAttachingToNotifications.getNewBaseIntent(n).putExtra("onesignalData", jsonObject.toString())));
        notificationCompat$Builder.setDeleteIntent(getNewDismissActionPendingIntent(((Random)secureRandom).nextInt(), getNewBaseDismissIntent(n)));
        return notificationCompat$Builder.build();
    }
    
    private static void createGrouplessSummaryNotification(final OSNotificationGenerationJob osNotificationGenerationJob, IntentGeneratorForAttachingToNotifications compatBuilder, final int number) {
        final JSONObject jsonPayload = osNotificationGenerationJob.getJsonPayload();
        final SecureRandom secureRandom = new SecureRandom();
        final String grouplessSummaryKey = OneSignalNotificationManager.getGrouplessSummaryKey();
        final StringBuilder sb = new StringBuilder();
        sb.append(number);
        sb.append(" new messages");
        final String string = sb.toString();
        final int grouplessSummaryId = OneSignalNotificationManager.getGrouplessSummaryId();
        createSummaryIdDatabaseEntry(OneSignalDbHelper.getInstance(GenerateNotification.currentContext), grouplessSummaryKey, grouplessSummaryId);
        final PendingIntent newActionPendingIntent = compatBuilder.getNewActionPendingIntent(secureRandom.nextInt(), createBaseSummaryIntent(grouplessSummaryId, compatBuilder, jsonPayload, grouplessSummaryKey));
        final PendingIntent newDismissActionPendingIntent = getNewDismissActionPendingIntent(secureRandom.nextInt(), getNewBaseDismissIntent(0).putExtra("summary", grouplessSummaryKey));
        compatBuilder = (IntentGeneratorForAttachingToNotifications)getBaseOneSignalNotificationBuilder(osNotificationGenerationJob).compatBuilder;
        if (osNotificationGenerationJob.getOverriddenSound() != null) {
            ((NotificationCompat$Builder)compatBuilder).setSound(osNotificationGenerationJob.getOverriddenSound());
        }
        if (osNotificationGenerationJob.getOverriddenFlags() != null) {
            ((NotificationCompat$Builder)compatBuilder).setDefaults((int)osNotificationGenerationJob.getOverriddenFlags());
        }
        ((NotificationCompat$Builder)compatBuilder).setContentIntent(newActionPendingIntent).setDeleteIntent(newDismissActionPendingIntent).setContentTitle(GenerateNotification.currentContext.getPackageManager().getApplicationLabel(GenerateNotification.currentContext.getApplicationInfo())).setContentText((CharSequence)string).setNumber(number).setSmallIcon(getDefaultSmallIconId()).setLargeIcon(getDefaultLargeIcon()).setOnlyAlertOnce(true).setAutoCancel(false).setGroup(grouplessSummaryKey).setGroupSummary(true);
        while (true) {
            try {
                ((NotificationCompat$Builder)compatBuilder).setGroupAlertBehavior((int)GenerateNotification.groupAlertBehavior);
                final NotificationCompat$InboxStyle style = new NotificationCompat$InboxStyle();
                style.setBigContentTitle((CharSequence)string);
                ((NotificationCompat$Builder)compatBuilder).setStyle((NotificationCompat$Style)style);
                NotificationManagerCompat.from(GenerateNotification.currentContext).notify(grouplessSummaryId, ((NotificationCompat$Builder)compatBuilder).build());
            }
            finally {
                continue;
            }
            break;
        }
    }
    
    private static Notification createSingleNotificationBeforeSummaryBuilder(final OSNotificationGenerationJob osNotificationGenerationJob, final NotificationCompat$Builder notificationCompat$Builder) {
        final boolean b = Build$VERSION.SDK_INT < 24 && !osNotificationGenerationJob.isRestoring();
        if (b && osNotificationGenerationJob.getOverriddenSound() != null && !osNotificationGenerationJob.getOverriddenSound().equals((Object)osNotificationGenerationJob.getOrgSound())) {
            notificationCompat$Builder.setSound((Uri)null);
        }
        final Notification build = notificationCompat$Builder.build();
        if (b) {
            notificationCompat$Builder.setSound(osNotificationGenerationJob.getOverriddenSound());
        }
        return build;
    }
    
    private static void createSummaryIdDatabaseEntry(final OneSignalDbHelper oneSignalDbHelper, final String s, final int n) {
        final ContentValues contentValues = new ContentValues();
        contentValues.put("android_notification_id", Integer.valueOf(n));
        contentValues.put("group_id", s);
        contentValues.put("is_summary", Integer.valueOf(1));
        oneSignalDbHelper.insertOrThrow("notification", (String)null, contentValues);
    }
    
    private static void createSummaryNotification(final OSNotificationGenerationJob p0, final OneSignalNotificationBuilder p1) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     3: astore          11
        //     5: ldc_w           "title"
        //     8: astore          9
        //    10: ldc_w           "is_summary"
        //    13: astore          10
        //    15: aload_0        
        //    16: invokevirtual   com/onesignal/OSNotificationGenerationJob.isRestoring:()Z
        //    19: istore_3       
        //    20: aload_0        
        //    21: invokevirtual   com/onesignal/OSNotificationGenerationJob.getJsonPayload:()Lorg/json/JSONObject;
        //    24: astore          8
        //    26: new             Lcom/onesignal/IntentGeneratorForAttachingToNotifications;
        //    29: dup            
        //    30: getstatic       com/onesignal/GenerateNotification.currentContext:Landroid/content/Context;
        //    33: invokespecial   com/onesignal/IntentGeneratorForAttachingToNotifications.<init>:(Landroid/content/Context;)V
        //    36: astore          18
        //    38: aload           8
        //    40: ldc_w           "grp"
        //    43: aconst_null    
        //    44: invokevirtual   org/json/JSONObject.optString:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
        //    47: astore          16
        //    49: new             Ljava/security/SecureRandom;
        //    52: dup            
        //    53: invokespecial   java/security/SecureRandom.<init>:()V
        //    56: astore          20
        //    58: aload           20
        //    60: invokevirtual   java/security/SecureRandom.nextInt:()I
        //    63: iconst_0       
        //    64: invokestatic    com/onesignal/GenerateNotification.getNewBaseDismissIntent:(I)Landroid/content/Intent;
        //    67: ldc_w           "summary"
        //    70: aload           16
        //    72: invokevirtual   android/content/Intent.putExtra:(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;
        //    75: invokestatic    com/onesignal/GenerateNotification.getNewDismissActionPendingIntent:(ILandroid/content/Intent;)Landroid/app/PendingIntent;
        //    78: astore          17
        //    80: getstatic       com/onesignal/GenerateNotification.currentContext:Landroid/content/Context;
        //    83: invokestatic    com/onesignal/OneSignalDbHelper.getInstance:(Landroid/content/Context;)Lcom/onesignal/OneSignalDbHelper;
        //    86: astore          19
        //    88: iload_3        
        //    89: ifne            131
        //    92: new             Ljava/lang/StringBuilder;
        //    95: astore          5
        //    97: aload           5
        //    99: ldc_w           "group_id = ? AND dismissed = 0 AND opened = 0 AND android_notification_id <> "
        //   102: invokespecial   java/lang/StringBuilder.<init>:(Ljava/lang/String;)V
        //   105: aload           5
        //   107: aload_0        
        //   108: invokevirtual   com/onesignal/OSNotificationGenerationJob.getAndroidId:()Ljava/lang/Integer;
        //   111: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   114: pop            
        //   115: aload           5
        //   117: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   120: astore          5
        //   122: goto            136
        //   125: astore_0       
        //   126: aconst_null    
        //   127: astore_1       
        //   128: goto            1247
        //   131: ldc_w           "group_id = ? AND dismissed = 0 AND opened = 0"
        //   134: astore          5
        //   136: aload           19
        //   138: ldc_w           "notification"
        //   141: iconst_5       
        //   142: anewarray       Ljava/lang/String;
        //   145: dup            
        //   146: iconst_0       
        //   147: ldc_w           "android_notification_id"
        //   150: aastore        
        //   151: dup            
        //   152: iconst_1       
        //   153: ldc_w           "full_data"
        //   156: aastore        
        //   157: dup            
        //   158: iconst_2       
        //   159: ldc_w           "is_summary"
        //   162: aastore        
        //   163: dup            
        //   164: iconst_3       
        //   165: ldc_w           "title"
        //   168: aastore        
        //   169: dup            
        //   170: iconst_4       
        //   171: ldc_w           "message"
        //   174: aastore        
        //   175: aload           5
        //   177: iconst_1       
        //   178: anewarray       Ljava/lang/String;
        //   181: dup            
        //   182: iconst_0       
        //   183: aload           16
        //   185: aastore        
        //   186: aconst_null    
        //   187: aconst_null    
        //   188: ldc_w           "_id DESC"
        //   191: invokevirtual   com/onesignal/OneSignalDbHelper.query:(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
        //   194: astore          14
        //   196: aload           14
        //   198: invokeinterface android/database/Cursor.moveToFirst:()Z
        //   203: istore          4
        //   205: ldc_w           ""
        //   208: astore          13
        //   210: iload           4
        //   212: ifeq            557
        //   215: new             Ljava/util/ArrayList;
        //   218: astore          15
        //   220: aload           15
        //   222: invokespecial   java/util/ArrayList.<init>:()V
        //   225: aconst_null    
        //   226: astore          5
        //   228: aconst_null    
        //   229: astore          12
        //   231: aload           14
        //   233: aload           14
        //   235: aload           10
        //   237: invokeinterface android/database/Cursor.getColumnIndex:(Ljava/lang/String;)I
        //   242: invokeinterface android/database/Cursor.getInt:(I)I
        //   247: iconst_1       
        //   248: if_icmpne       280
        //   251: aload           14
        //   253: aload           14
        //   255: ldc_w           "android_notification_id"
        //   258: invokeinterface android/database/Cursor.getColumnIndex:(Ljava/lang/String;)I
        //   263: invokeinterface android/database/Cursor.getInt:(I)I
        //   268: invokestatic    java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
        //   271: astore          6
        //   273: aload           5
        //   275: astore          7
        //   277: goto            486
        //   280: aload           14
        //   282: aload           14
        //   284: aload           9
        //   286: invokeinterface android/database/Cursor.getColumnIndex:(Ljava/lang/String;)I
        //   291: invokeinterface android/database/Cursor.getString:(I)Ljava/lang/String;
        //   296: astore          7
        //   298: aload           7
        //   300: ifnonnull       311
        //   303: ldc_w           ""
        //   306: astore          6
        //   308: goto            345
        //   311: new             Ljava/lang/StringBuilder;
        //   314: astore          6
        //   316: aload           6
        //   318: invokespecial   java/lang/StringBuilder.<init>:()V
        //   321: aload           6
        //   323: aload           7
        //   325: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   328: pop            
        //   329: aload           6
        //   331: ldc_w           " "
        //   334: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   337: pop            
        //   338: aload           6
        //   340: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   343: astore          6
        //   345: aload           14
        //   347: aload           14
        //   349: aload           11
        //   351: invokeinterface android/database/Cursor.getColumnIndex:(Ljava/lang/String;)I
        //   356: invokeinterface android/database/Cursor.getString:(I)Ljava/lang/String;
        //   361: astore          22
        //   363: new             Landroid/text/SpannableString;
        //   366: astore          7
        //   368: new             Ljava/lang/StringBuilder;
        //   371: astore          21
        //   373: aload           21
        //   375: invokespecial   java/lang/StringBuilder.<init>:()V
        //   378: aload           21
        //   380: aload           6
        //   382: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   385: pop            
        //   386: aload           21
        //   388: aload           22
        //   390: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   393: pop            
        //   394: aload           7
        //   396: aload           21
        //   398: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   401: invokespecial   android/text/SpannableString.<init>:(Ljava/lang/CharSequence;)V
        //   404: aload           6
        //   406: invokevirtual   java/lang/String.length:()I
        //   409: ifle            437
        //   412: new             Landroid/text/style/StyleSpan;
        //   415: astore          21
        //   417: aload           21
        //   419: iconst_1       
        //   420: invokespecial   android/text/style/StyleSpan.<init>:(I)V
        //   423: aload           7
        //   425: aload           21
        //   427: iconst_0       
        //   428: aload           6
        //   430: invokevirtual   java/lang/String.length:()I
        //   433: iconst_0       
        //   434: invokevirtual   android/text/SpannableString.setSpan:(Ljava/lang/Object;III)V
        //   437: aload           15
        //   439: aload           7
        //   441: invokeinterface java/util/Collection.add:(Ljava/lang/Object;)Z
        //   446: pop            
        //   447: aload           5
        //   449: astore          7
        //   451: aload           12
        //   453: astore          6
        //   455: aload           5
        //   457: ifnonnull       277
        //   460: aload           14
        //   462: aload           14
        //   464: ldc_w           "full_data"
        //   467: invokeinterface android/database/Cursor.getColumnIndex:(Ljava/lang/String;)I
        //   472: invokeinterface android/database/Cursor.getString:(I)Ljava/lang/String;
        //   477: astore          7
        //   479: aload           12
        //   481: astore          6
        //   483: goto            277
        //   486: aload           14
        //   488: invokeinterface android/database/Cursor.moveToNext:()Z
        //   493: istore          4
        //   495: iload           4
        //   497: ifne            546
        //   500: iload_3        
        //   501: ifeq            535
        //   504: aload           7
        //   506: ifnull          535
        //   509: new             Lorg/json/JSONObject;
        //   512: astore          5
        //   514: aload           5
        //   516: aload           7
        //   518: invokespecial   org/json/JSONObject.<init>:(Ljava/lang/String;)V
        //   521: aload           15
        //   523: astore          8
        //   525: goto            567
        //   528: astore          5
        //   530: aload           5
        //   532: invokevirtual   org/json/JSONException.printStackTrace:()V
        //   535: aload           8
        //   537: astore          5
        //   539: aload           15
        //   541: astore          8
        //   543: goto            567
        //   546: aload           7
        //   548: astore          5
        //   550: aload           6
        //   552: astore          12
        //   554: goto            231
        //   557: aload           8
        //   559: astore          5
        //   561: aconst_null    
        //   562: astore          8
        //   564: aconst_null    
        //   565: astore          6
        //   567: aload           14
        //   569: ifnull          589
        //   572: aload           14
        //   574: invokeinterface android/database/Cursor.isClosed:()Z
        //   579: ifne            589
        //   582: aload           14
        //   584: invokeinterface android/database/Cursor.close:()V
        //   589: aload           6
        //   591: astore          7
        //   593: aload           6
        //   595: ifnonnull       620
        //   598: aload           20
        //   600: invokevirtual   java/security/SecureRandom.nextInt:()I
        //   603: invokestatic    java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
        //   606: astore          7
        //   608: aload           19
        //   610: aload           16
        //   612: aload           7
        //   614: invokevirtual   java/lang/Integer.intValue:()I
        //   617: invokestatic    com/onesignal/GenerateNotification.createSummaryIdDatabaseEntry:(Lcom/onesignal/OneSignalDbHelper;Ljava/lang/String;I)V
        //   620: aload           18
        //   622: aload           20
        //   624: invokevirtual   java/security/SecureRandom.nextInt:()I
        //   627: aload           7
        //   629: invokevirtual   java/lang/Integer.intValue:()I
        //   632: aload           18
        //   634: aload           5
        //   636: aload           16
        //   638: invokestatic    com/onesignal/GenerateNotification.createBaseSummaryIntent:(ILcom/onesignal/IntentGeneratorForAttachingToNotifications;Lorg/json/JSONObject;Ljava/lang/String;)Landroid/content/Intent;
        //   641: invokevirtual   com/onesignal/IntentGeneratorForAttachingToNotifications.getNewActionPendingIntent:(ILandroid/content/Intent;)Landroid/app/PendingIntent;
        //   644: astore          9
        //   646: aload           8
        //   648: ifnull          1144
        //   651: iload_3        
        //   652: ifeq            666
        //   655: aload           8
        //   657: invokeinterface java/util/Collection.size:()I
        //   662: iconst_1       
        //   663: if_icmpgt       680
        //   666: iload_3        
        //   667: ifne            1144
        //   670: aload           8
        //   672: invokeinterface java/util/Collection.size:()I
        //   677: ifle            1144
        //   680: aload           8
        //   682: invokeinterface java/util/Collection.size:()I
        //   687: iload_3        
        //   688: iconst_1       
        //   689: ixor           
        //   690: iadd           
        //   691: istore_2       
        //   692: aload           5
        //   694: ldc_w           "grp_msg"
        //   697: aconst_null    
        //   698: invokevirtual   org/json/JSONObject.optString:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
        //   701: astore_1       
        //   702: aload_1        
        //   703: ifnonnull       736
        //   706: new             Ljava/lang/StringBuilder;
        //   709: dup            
        //   710: invokespecial   java/lang/StringBuilder.<init>:()V
        //   713: astore_1       
        //   714: aload_1        
        //   715: iload_2        
        //   716: invokevirtual   java/lang/StringBuilder.append:(I)Ljava/lang/StringBuilder;
        //   719: pop            
        //   720: aload_1        
        //   721: ldc_w           " new messages"
        //   724: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   727: pop            
        //   728: aload_1        
        //   729: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   732: astore_1       
        //   733: goto            768
        //   736: new             Ljava/lang/StringBuilder;
        //   739: dup            
        //   740: ldc_w           ""
        //   743: invokespecial   java/lang/StringBuilder.<init>:(Ljava/lang/String;)V
        //   746: astore          5
        //   748: aload           5
        //   750: iload_2        
        //   751: invokevirtual   java/lang/StringBuilder.append:(I)Ljava/lang/StringBuilder;
        //   754: pop            
        //   755: aload_1        
        //   756: ldc_w           "$[notif_count]"
        //   759: aload           5
        //   761: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   764: invokevirtual   java/lang/String.replace:(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;
        //   767: astore_1       
        //   768: aload_0        
        //   769: invokestatic    com/onesignal/GenerateNotification.getBaseOneSignalNotificationBuilder:(Lcom/onesignal/OSNotificationGenerationJob;)Lcom/onesignal/GenerateNotification$OneSignalNotificationBuilder;
        //   772: getfield        com/onesignal/GenerateNotification$OneSignalNotificationBuilder.compatBuilder:Landroidx/core/app/NotificationCompat$Builder;
        //   775: astore          6
        //   777: iload_3        
        //   778: ifeq            789
        //   781: aload           6
        //   783: invokestatic    com/onesignal/GenerateNotification.removeNotifyOptions:(Landroidx/core/app/NotificationCompat$Builder;)V
        //   786: goto            826
        //   789: aload_0        
        //   790: invokevirtual   com/onesignal/OSNotificationGenerationJob.getOverriddenSound:()Landroid/net/Uri;
        //   793: ifnull          806
        //   796: aload           6
        //   798: aload_0        
        //   799: invokevirtual   com/onesignal/OSNotificationGenerationJob.getOverriddenSound:()Landroid/net/Uri;
        //   802: invokevirtual   androidx/core/app/NotificationCompat$Builder.setSound:(Landroid/net/Uri;)Landroidx/core/app/NotificationCompat$Builder;
        //   805: pop            
        //   806: aload_0        
        //   807: invokevirtual   com/onesignal/OSNotificationGenerationJob.getOverriddenFlags:()Ljava/lang/Integer;
        //   810: ifnull          826
        //   813: aload           6
        //   815: aload_0        
        //   816: invokevirtual   com/onesignal/OSNotificationGenerationJob.getOverriddenFlags:()Ljava/lang/Integer;
        //   819: invokevirtual   java/lang/Integer.intValue:()I
        //   822: invokevirtual   androidx/core/app/NotificationCompat$Builder.setDefaults:(I)Landroidx/core/app/NotificationCompat$Builder;
        //   825: pop            
        //   826: aload           6
        //   828: aload           9
        //   830: invokevirtual   androidx/core/app/NotificationCompat$Builder.setContentIntent:(Landroid/app/PendingIntent;)Landroidx/core/app/NotificationCompat$Builder;
        //   833: aload           17
        //   835: invokevirtual   androidx/core/app/NotificationCompat$Builder.setDeleteIntent:(Landroid/app/PendingIntent;)Landroidx/core/app/NotificationCompat$Builder;
        //   838: getstatic       com/onesignal/GenerateNotification.currentContext:Landroid/content/Context;
        //   841: invokevirtual   android/content/Context.getPackageManager:()Landroid/content/pm/PackageManager;
        //   844: getstatic       com/onesignal/GenerateNotification.currentContext:Landroid/content/Context;
        //   847: invokevirtual   android/content/Context.getApplicationInfo:()Landroid/content/pm/ApplicationInfo;
        //   850: invokevirtual   android/content/pm/PackageManager.getApplicationLabel:(Landroid/content/pm/ApplicationInfo;)Ljava/lang/CharSequence;
        //   853: invokevirtual   androidx/core/app/NotificationCompat$Builder.setContentTitle:(Ljava/lang/CharSequence;)Landroidx/core/app/NotificationCompat$Builder;
        //   856: aload_1        
        //   857: invokevirtual   androidx/core/app/NotificationCompat$Builder.setContentText:(Ljava/lang/CharSequence;)Landroidx/core/app/NotificationCompat$Builder;
        //   860: iload_2        
        //   861: invokevirtual   androidx/core/app/NotificationCompat$Builder.setNumber:(I)Landroidx/core/app/NotificationCompat$Builder;
        //   864: invokestatic    com/onesignal/GenerateNotification.getDefaultSmallIconId:()I
        //   867: invokevirtual   androidx/core/app/NotificationCompat$Builder.setSmallIcon:(I)Landroidx/core/app/NotificationCompat$Builder;
        //   870: invokestatic    com/onesignal/GenerateNotification.getDefaultLargeIcon:()Landroid/graphics/Bitmap;
        //   873: invokevirtual   androidx/core/app/NotificationCompat$Builder.setLargeIcon:(Landroid/graphics/Bitmap;)Landroidx/core/app/NotificationCompat$Builder;
        //   876: iload_3        
        //   877: invokevirtual   androidx/core/app/NotificationCompat$Builder.setOnlyAlertOnce:(Z)Landroidx/core/app/NotificationCompat$Builder;
        //   880: iconst_0       
        //   881: invokevirtual   androidx/core/app/NotificationCompat$Builder.setAutoCancel:(Z)Landroidx/core/app/NotificationCompat$Builder;
        //   884: aload           16
        //   886: invokevirtual   androidx/core/app/NotificationCompat$Builder.setGroup:(Ljava/lang/String;)Landroidx/core/app/NotificationCompat$Builder;
        //   889: iconst_1       
        //   890: invokevirtual   androidx/core/app/NotificationCompat$Builder.setGroupSummary:(Z)Landroidx/core/app/NotificationCompat$Builder;
        //   893: pop            
        //   894: aload           6
        //   896: getstatic       com/onesignal/GenerateNotification.groupAlertBehavior:Ljava/lang/Integer;
        //   899: invokevirtual   java/lang/Integer.intValue:()I
        //   902: invokevirtual   androidx/core/app/NotificationCompat$Builder.setGroupAlertBehavior:(I)Landroidx/core/app/NotificationCompat$Builder;
        //   905: pop            
        //   906: goto            911
        //   909: astore          5
        //   911: iload_3        
        //   912: ifne            922
        //   915: aload           6
        //   917: aload_1        
        //   918: invokevirtual   androidx/core/app/NotificationCompat$Builder.setTicker:(Ljava/lang/CharSequence;)Landroidx/core/app/NotificationCompat$Builder;
        //   921: pop            
        //   922: new             Landroidx/core/app/NotificationCompat$InboxStyle;
        //   925: dup            
        //   926: invokespecial   androidx/core/app/NotificationCompat$InboxStyle.<init>:()V
        //   929: astore          9
        //   931: iload_3        
        //   932: ifne            1085
        //   935: aload_0        
        //   936: invokevirtual   com/onesignal/OSNotificationGenerationJob.getTitle:()Ljava/lang/CharSequence;
        //   939: ifnull          956
        //   942: aload_0        
        //   943: invokevirtual   com/onesignal/OSNotificationGenerationJob.getTitle:()Ljava/lang/CharSequence;
        //   946: invokeinterface java/lang/CharSequence.toString:()Ljava/lang/String;
        //   951: astore          5
        //   953: goto            959
        //   956: aconst_null    
        //   957: astore          5
        //   959: aload           5
        //   961: ifnonnull       971
        //   964: aload           13
        //   966: astore          5
        //   968: goto            1004
        //   971: new             Ljava/lang/StringBuilder;
        //   974: dup            
        //   975: invokespecial   java/lang/StringBuilder.<init>:()V
        //   978: astore          10
        //   980: aload           10
        //   982: aload           5
        //   984: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   987: pop            
        //   988: aload           10
        //   990: ldc_w           " "
        //   993: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   996: pop            
        //   997: aload           10
        //   999: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //  1002: astore          5
        //  1004: aload_0        
        //  1005: invokevirtual   com/onesignal/OSNotificationGenerationJob.getBody:()Ljava/lang/CharSequence;
        //  1008: invokeinterface java/lang/CharSequence.toString:()Ljava/lang/String;
        //  1013: astore_0       
        //  1014: new             Ljava/lang/StringBuilder;
        //  1017: dup            
        //  1018: invokespecial   java/lang/StringBuilder.<init>:()V
        //  1021: astore          10
        //  1023: aload           10
        //  1025: aload           5
        //  1027: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //  1030: pop            
        //  1031: aload           10
        //  1033: aload_0        
        //  1034: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //  1037: pop            
        //  1038: new             Landroid/text/SpannableString;
        //  1041: dup            
        //  1042: aload           10
        //  1044: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //  1047: invokespecial   android/text/SpannableString.<init>:(Ljava/lang/CharSequence;)V
        //  1050: astore_0       
        //  1051: aload           5
        //  1053: invokevirtual   java/lang/String.length:()I
        //  1056: ifle            1078
        //  1059: aload_0        
        //  1060: new             Landroid/text/style/StyleSpan;
        //  1063: dup            
        //  1064: iconst_1       
        //  1065: invokespecial   android/text/style/StyleSpan.<init>:(I)V
        //  1068: iconst_0       
        //  1069: aload           5
        //  1071: invokevirtual   java/lang/String.length:()I
        //  1074: iconst_0       
        //  1075: invokevirtual   android/text/SpannableString.setSpan:(Ljava/lang/Object;III)V
        //  1078: aload           9
        //  1080: aload_0        
        //  1081: invokevirtual   androidx/core/app/NotificationCompat$InboxStyle.addLine:(Ljava/lang/CharSequence;)Landroidx/core/app/NotificationCompat$InboxStyle;
        //  1084: pop            
        //  1085: aload           8
        //  1087: invokeinterface java/util/Collection.iterator:()Ljava/util/Iterator;
        //  1092: astore_0       
        //  1093: aload_0        
        //  1094: invokeinterface java/util/Iterator.hasNext:()Z
        //  1099: ifeq            1120
        //  1102: aload           9
        //  1104: aload_0        
        //  1105: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
        //  1110: checkcast       Landroid/text/SpannableString;
        //  1113: invokevirtual   androidx/core/app/NotificationCompat$InboxStyle.addLine:(Ljava/lang/CharSequence;)Landroidx/core/app/NotificationCompat$InboxStyle;
        //  1116: pop            
        //  1117: goto            1093
        //  1120: aload           9
        //  1122: aload_1        
        //  1123: invokevirtual   androidx/core/app/NotificationCompat$InboxStyle.setBigContentTitle:(Ljava/lang/CharSequence;)Landroidx/core/app/NotificationCompat$InboxStyle;
        //  1126: pop            
        //  1127: aload           6
        //  1129: aload           9
        //  1131: invokevirtual   androidx/core/app/NotificationCompat$Builder.setStyle:(Landroidx/core/app/NotificationCompat$Style;)Landroidx/core/app/NotificationCompat$Builder;
        //  1134: pop            
        //  1135: aload           6
        //  1137: invokevirtual   androidx/core/app/NotificationCompat$Builder.build:()Landroid/app/Notification;
        //  1140: astore_0       
        //  1141: goto            1221
        //  1144: aload_1        
        //  1145: getfield        com/onesignal/GenerateNotification$OneSignalNotificationBuilder.compatBuilder:Landroidx/core/app/NotificationCompat$Builder;
        //  1148: astore_0       
        //  1149: aload_0        
        //  1150: getfield        androidx/core/app/NotificationCompat$Builder.mActions:Ljava/util/ArrayList;
        //  1153: invokevirtual   java/util/ArrayList.clear:()V
        //  1156: aload           5
        //  1158: aload           18
        //  1160: aload_0        
        //  1161: aload           7
        //  1163: invokevirtual   java/lang/Integer.intValue:()I
        //  1166: aload           16
        //  1168: invokestatic    com/onesignal/GenerateNotification.addNotificationActionButtons:(Lorg/json/JSONObject;Lcom/onesignal/IntentGeneratorForAttachingToNotifications;Landroidx/core/app/NotificationCompat$Builder;ILjava/lang/String;)V
        //  1171: aload_0        
        //  1172: aload           9
        //  1174: invokevirtual   androidx/core/app/NotificationCompat$Builder.setContentIntent:(Landroid/app/PendingIntent;)Landroidx/core/app/NotificationCompat$Builder;
        //  1177: aload           17
        //  1179: invokevirtual   androidx/core/app/NotificationCompat$Builder.setDeleteIntent:(Landroid/app/PendingIntent;)Landroidx/core/app/NotificationCompat$Builder;
        //  1182: iload_3        
        //  1183: invokevirtual   androidx/core/app/NotificationCompat$Builder.setOnlyAlertOnce:(Z)Landroidx/core/app/NotificationCompat$Builder;
        //  1186: iconst_0       
        //  1187: invokevirtual   androidx/core/app/NotificationCompat$Builder.setAutoCancel:(Z)Landroidx/core/app/NotificationCompat$Builder;
        //  1190: aload           16
        //  1192: invokevirtual   androidx/core/app/NotificationCompat$Builder.setGroup:(Ljava/lang/String;)Landroidx/core/app/NotificationCompat$Builder;
        //  1195: iconst_1       
        //  1196: invokevirtual   androidx/core/app/NotificationCompat$Builder.setGroupSummary:(Z)Landroidx/core/app/NotificationCompat$Builder;
        //  1199: pop            
        //  1200: aload_0        
        //  1201: getstatic       com/onesignal/GenerateNotification.groupAlertBehavior:Ljava/lang/Integer;
        //  1204: invokevirtual   java/lang/Integer.intValue:()I
        //  1207: invokevirtual   androidx/core/app/NotificationCompat$Builder.setGroupAlertBehavior:(I)Landroidx/core/app/NotificationCompat$Builder;
        //  1210: pop            
        //  1211: aload_0        
        //  1212: invokevirtual   androidx/core/app/NotificationCompat$Builder.build:()Landroid/app/Notification;
        //  1215: astore_0       
        //  1216: aload_1        
        //  1217: aload_0        
        //  1218: invokestatic    com/onesignal/GenerateNotification.addXiaomiSettings:(Lcom/onesignal/GenerateNotification$OneSignalNotificationBuilder;Landroid/app/Notification;)V
        //  1221: getstatic       com/onesignal/GenerateNotification.currentContext:Landroid/content/Context;
        //  1224: invokestatic    androidx/core/app/NotificationManagerCompat.from:(Landroid/content/Context;)Landroidx/core/app/NotificationManagerCompat;
        //  1227: aload           7
        //  1229: invokevirtual   java/lang/Integer.intValue:()I
        //  1232: aload_0        
        //  1233: invokevirtual   androidx/core/app/NotificationManagerCompat.notify:(ILandroid/app/Notification;)V
        //  1236: return         
        //  1237: astore_0       
        //  1238: aload           14
        //  1240: astore_1       
        //  1241: goto            1247
        //  1244: astore_0       
        //  1245: aconst_null    
        //  1246: astore_1       
        //  1247: aload_1        
        //  1248: ifnull          1266
        //  1251: aload_1        
        //  1252: invokeinterface android/database/Cursor.isClosed:()Z
        //  1257: ifne            1266
        //  1260: aload_1        
        //  1261: invokeinterface android/database/Cursor.close:()V
        //  1266: aload_0        
        //  1267: athrow         
        //  1268: astore          5
        //  1270: goto            1211
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                    
        //  -----  -----  -----  -----  ------------------------
        //  92     122    125    131    Any
        //  136    196    1244   1247   Any
        //  196    205    1237   1244   Any
        //  215    225    1237   1244   Any
        //  231    273    1237   1244   Any
        //  280    298    1237   1244   Any
        //  311    345    1237   1244   Any
        //  345    437    1237   1244   Any
        //  437    447    1237   1244   Any
        //  460    479    1237   1244   Any
        //  486    495    1237   1244   Any
        //  509    521    528    535    Lorg/json/JSONException;
        //  509    521    1237   1244   Any
        //  530    535    1237   1244   Any
        //  894    906    909    911    Any
        //  1200   1211   1268   1273   Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_1211:
        //     at w5.m.a(SourceFile:20)
        //     at w5.f.o(SourceFile:122)
        //     at w5.f.r(SourceFile:571)
        //     at w5.f.q(SourceFile:3)
        //     at a6.j.j(SourceFile:32)
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
    
    static boolean displayIAMPreviewNotification(final OSNotificationGenerationJob osNotificationGenerationJob) {
        setStatics(osNotificationGenerationJob.getContext());
        return showNotification(osNotificationGenerationJob);
    }
    
    static boolean displayNotification(final OSNotificationGenerationJob osNotificationGenerationJob) {
        setStatics(osNotificationGenerationJob.getContext());
        isRunningOnMainThreadCheck();
        initGroupAlertBehavior();
        return showNotification(osNotificationGenerationJob);
    }
    
    static BigInteger getAccentColor(final JSONObject p0) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: ldc_w           "bgac"
        //     4: invokevirtual   org/json/JSONObject.has:(Ljava/lang/String;)Z
        //     7: ifeq            30
        //    10: new             Ljava/math/BigInteger;
        //    13: dup            
        //    14: aload_0        
        //    15: ldc_w           "bgac"
        //    18: aconst_null    
        //    19: invokevirtual   org/json/JSONObject.optString:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
        //    22: bipush          16
        //    24: invokespecial   java/math/BigInteger.<init>:(Ljava/lang/String;I)V
        //    27: astore_0       
        //    28: aload_0        
        //    29: areturn        
        //    30: getstatic       com/onesignal/OneSignal.appContext:Landroid/content/Context;
        //    33: ldc_w           "onesignal_notification_accent_color"
        //    36: aconst_null    
        //    37: invokestatic    com/onesignal/OSUtils.getResourceString:(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
        //    40: astore_0       
        //    41: aload_0        
        //    42: ifnull          58
        //    45: new             Ljava/math/BigInteger;
        //    48: dup            
        //    49: aload_0        
        //    50: bipush          16
        //    52: invokespecial   java/math/BigInteger.<init>:(Ljava/lang/String;I)V
        //    55: astore_0       
        //    56: aload_0        
        //    57: areturn        
        //    58: getstatic       com/onesignal/OneSignal.appContext:Landroid/content/Context;
        //    61: ldc_w           "com.onesignal.NotificationAccentColor.DEFAULT"
        //    64: invokestatic    com/onesignal/OSUtils.getManifestMeta:(Landroid/content/Context;Ljava/lang/String;)Ljava/lang/String;
        //    67: astore_0       
        //    68: aload_0        
        //    69: ifnull          85
        //    72: new             Ljava/math/BigInteger;
        //    75: dup            
        //    76: aload_0        
        //    77: bipush          16
        //    79: invokespecial   java/math/BigInteger.<init>:(Ljava/lang/String;I)V
        //    82: astore_0       
        //    83: aload_0        
        //    84: areturn        
        //    85: aconst_null    
        //    86: areturn        
        //    87: astore_0       
        //    88: goto            30
        //    91: astore_0       
        //    92: goto            58
        //    95: astore_0       
        //    96: goto            85
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type
        //  -----  -----  -----  -----  ----
        //  0      28     87     91     Any
        //  30     41     91     95     Any
        //  45     56     91     95     Any
        //  58     68     95     99     Any
        //  72     83     95     99     Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IndexOutOfBoundsException: Index 52 out of bounds for length 52
        //     at jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:64)
        //     at jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:70)
        //     at jdk.internal.util.Preconditions.checkIndex(Preconditions.java:266)
        //     at java.util.Objects.checkIndex(Objects.java:359)
        //     at java.util.ArrayList.get(ArrayList.java:434)
        //     at w5.a.o(SourceFile:31)
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
    
    private static OneSignalNotificationBuilder getBaseOneSignalNotificationBuilder(final OSNotificationGenerationJob p0) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: invokevirtual   com/onesignal/OSNotificationGenerationJob.getJsonPayload:()Lorg/json/JSONObject;
        //     4: astore_3       
        //     5: new             Lcom/onesignal/GenerateNotification$OneSignalNotificationBuilder;
        //     8: dup            
        //     9: aconst_null    
        //    10: invokespecial   com/onesignal/GenerateNotification$OneSignalNotificationBuilder.<init>:(Lcom/onesignal/GenerateNotification$1;)V
        //    13: astore          4
        //    15: aload_0        
        //    16: invokestatic    com/onesignal/NotificationChannelManager.createNotificationChannel:(Lcom/onesignal/OSNotificationGenerationJob;)Ljava/lang/String;
        //    19: astore          5
        //    21: new             Landroidx/core/app/NotificationCompat$Builder;
        //    24: astore_2       
        //    25: aload_2        
        //    26: getstatic       com/onesignal/GenerateNotification.currentContext:Landroid/content/Context;
        //    29: aload           5
        //    31: invokespecial   androidx/core/app/NotificationCompat$Builder.<init>:(Landroid/content/Context;Ljava/lang/String;)V
        //    34: goto            49
        //    37: astore_2       
        //    38: new             Landroidx/core/app/NotificationCompat$Builder;
        //    41: dup            
        //    42: getstatic       com/onesignal/GenerateNotification.currentContext:Landroid/content/Context;
        //    45: invokespecial   androidx/core/app/NotificationCompat$Builder.<init>:(Landroid/content/Context;)V
        //    48: astore_2       
        //    49: aload_3        
        //    50: ldc             "alert"
        //    52: aconst_null    
        //    53: invokevirtual   org/json/JSONObject.optString:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
        //    56: astore          5
        //    58: aload_2        
        //    59: iconst_1       
        //    60: invokevirtual   androidx/core/app/NotificationCompat$Builder.setAutoCancel:(Z)Landroidx/core/app/NotificationCompat$Builder;
        //    63: aload_3        
        //    64: invokestatic    com/onesignal/GenerateNotification.getSmallIconId:(Lorg/json/JSONObject;)I
        //    67: invokevirtual   androidx/core/app/NotificationCompat$Builder.setSmallIcon:(I)Landroidx/core/app/NotificationCompat$Builder;
        //    70: new             Landroidx/core/app/NotificationCompat$BigTextStyle;
        //    73: dup            
        //    74: invokespecial   androidx/core/app/NotificationCompat$BigTextStyle.<init>:()V
        //    77: aload           5
        //    79: invokevirtual   androidx/core/app/NotificationCompat$BigTextStyle.bigText:(Ljava/lang/CharSequence;)Landroidx/core/app/NotificationCompat$BigTextStyle;
        //    82: invokevirtual   androidx/core/app/NotificationCompat$Builder.setStyle:(Landroidx/core/app/NotificationCompat$Style;)Landroidx/core/app/NotificationCompat$Builder;
        //    85: aload           5
        //    87: invokevirtual   androidx/core/app/NotificationCompat$Builder.setContentText:(Ljava/lang/CharSequence;)Landroidx/core/app/NotificationCompat$Builder;
        //    90: aload           5
        //    92: invokevirtual   androidx/core/app/NotificationCompat$Builder.setTicker:(Ljava/lang/CharSequence;)Landroidx/core/app/NotificationCompat$Builder;
        //    95: pop            
        //    96: getstatic       android/os/Build$VERSION.SDK_INT:I
        //    99: bipush          24
        //   101: if_icmplt       120
        //   104: aload_3        
        //   105: ldc_w           "title"
        //   108: invokevirtual   org/json/JSONObject.optString:(Ljava/lang/String;)Ljava/lang/String;
        //   111: ldc_w           ""
        //   114: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
        //   117: ifne            129
        //   120: aload_2        
        //   121: aload_3        
        //   122: invokestatic    com/onesignal/GenerateNotification.getTitle:(Lorg/json/JSONObject;)Ljava/lang/CharSequence;
        //   125: invokevirtual   androidx/core/app/NotificationCompat$Builder.setContentTitle:(Ljava/lang/CharSequence;)Landroidx/core/app/NotificationCompat$Builder;
        //   128: pop            
        //   129: aload_3        
        //   130: invokestatic    com/onesignal/GenerateNotification.getAccentColor:(Lorg/json/JSONObject;)Ljava/math/BigInteger;
        //   133: astore          6
        //   135: aload           6
        //   137: ifnull          150
        //   140: aload_2        
        //   141: aload           6
        //   143: invokevirtual   java/math/BigInteger.intValue:()I
        //   146: invokevirtual   androidx/core/app/NotificationCompat$Builder.setColor:(I)Landroidx/core/app/NotificationCompat$Builder;
        //   149: pop            
        //   150: aload_3        
        //   151: ldc_w           "vis"
        //   154: invokevirtual   org/json/JSONObject.has:(Ljava/lang/String;)Z
        //   157: ifeq            174
        //   160: aload_3        
        //   161: ldc_w           "vis"
        //   164: invokevirtual   org/json/JSONObject.optString:(Ljava/lang/String;)Ljava/lang/String;
        //   167: invokestatic    java/lang/Integer.parseInt:(Ljava/lang/String;)I
        //   170: istore_1       
        //   171: goto            176
        //   174: iconst_1       
        //   175: istore_1       
        //   176: aload_2        
        //   177: iload_1        
        //   178: invokevirtual   androidx/core/app/NotificationCompat$Builder.setVisibility:(I)Landroidx/core/app/NotificationCompat$Builder;
        //   181: pop            
        //   182: goto            187
        //   185: astore          6
        //   187: aload_3        
        //   188: invokestatic    com/onesignal/GenerateNotification.getLargeIcon:(Lorg/json/JSONObject;)Landroid/graphics/Bitmap;
        //   191: astore          6
        //   193: aload           6
        //   195: ifnull          211
        //   198: aload           4
        //   200: iconst_1       
        //   201: putfield        com/onesignal/GenerateNotification$OneSignalNotificationBuilder.hasLargeIcon:Z
        //   204: aload_2        
        //   205: aload           6
        //   207: invokevirtual   androidx/core/app/NotificationCompat$Builder.setLargeIcon:(Landroid/graphics/Bitmap;)Landroidx/core/app/NotificationCompat$Builder;
        //   210: pop            
        //   211: aload_3        
        //   212: ldc_w           "bicon"
        //   215: aconst_null    
        //   216: invokevirtual   org/json/JSONObject.optString:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
        //   219: invokestatic    com/onesignal/GenerateNotification.getBitmap:(Ljava/lang/String;)Landroid/graphics/Bitmap;
        //   222: astore          6
        //   224: aload           6
        //   226: ifnull          251
        //   229: aload_2        
        //   230: new             Landroidx/core/app/NotificationCompat$BigPictureStyle;
        //   233: dup            
        //   234: invokespecial   androidx/core/app/NotificationCompat$BigPictureStyle.<init>:()V
        //   237: aload           6
        //   239: invokevirtual   androidx/core/app/NotificationCompat$BigPictureStyle.bigPicture:(Landroid/graphics/Bitmap;)Landroidx/core/app/NotificationCompat$BigPictureStyle;
        //   242: aload           5
        //   244: invokevirtual   androidx/core/app/NotificationCompat$BigPictureStyle.setSummaryText:(Ljava/lang/CharSequence;)Landroidx/core/app/NotificationCompat$BigPictureStyle;
        //   247: invokevirtual   androidx/core/app/NotificationCompat$Builder.setStyle:(Landroidx/core/app/NotificationCompat$Style;)Landroidx/core/app/NotificationCompat$Builder;
        //   250: pop            
        //   251: aload_0        
        //   252: invokevirtual   com/onesignal/OSNotificationGenerationJob.getShownTimeStamp:()Ljava/lang/Long;
        //   255: ifnull          274
        //   258: aload_2        
        //   259: aload_0        
        //   260: invokevirtual   com/onesignal/OSNotificationGenerationJob.getShownTimeStamp:()Ljava/lang/Long;
        //   263: invokevirtual   java/lang/Long.longValue:()J
        //   266: ldc2_w          1000
        //   269: lmul           
        //   270: invokevirtual   androidx/core/app/NotificationCompat$Builder.setWhen:(J)Landroidx/core/app/NotificationCompat$Builder;
        //   273: pop            
        //   274: aload_3        
        //   275: aload_2        
        //   276: invokestatic    com/onesignal/GenerateNotification.setAlertnessOptions:(Lorg/json/JSONObject;Landroidx/core/app/NotificationCompat$Builder;)V
        //   279: aload           4
        //   281: aload_2        
        //   282: putfield        com/onesignal/GenerateNotification$OneSignalNotificationBuilder.compatBuilder:Landroidx/core/app/NotificationCompat$Builder;
        //   285: aload           4
        //   287: areturn        
        //   288: astore          6
        //   290: goto            150
        //   293: astore_0       
        //   294: goto            274
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type
        //  -----  -----  -----  -----  ----
        //  15     34     37     49     Any
        //  129    135    288    293    Any
        //  140    150    288    293    Any
        //  150    171    185    187    Any
        //  176    182    185    187    Any
        //  258    274    293    297    Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0274:
        //     at w5.m.a(SourceFile:20)
        //     at w5.f.o(SourceFile:122)
        //     at w5.f.r(SourceFile:571)
        //     at w5.f.q(SourceFile:3)
        //     at a6.j.j(SourceFile:32)
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
    
    private static Bitmap getBitmap(final String s) {
        if (s == null) {
            return null;
        }
        final String trim = s.trim();
        if (!trim.startsWith("http://") && !trim.startsWith("https://")) {
            return getBitmapFromAssetsOrResourceName(s);
        }
        return getBitmapFromURL(trim);
    }
    
    private static Bitmap getBitmapFromAssetsOrResourceName(final String s) {
        Bitmap bitmap;
        try {
            BitmapFactory.decodeStream(GenerateNotification.currentContext.getAssets().open(s));
        }
        finally {
            bitmap = null;
        }
        if (bitmap != null) {
            return bitmap;
        }
        try {
            for (final String s2 : Arrays.asList((Object[])new String[] { ".png", ".webp", ".jpg", ".gif", ".bmp" })) {
                Bitmap bitmap2 = null;
                try {
                    final AssetManager assets = GenerateNotification.currentContext.getAssets();
                    final StringBuilder sb = new StringBuilder();
                    sb.append(s);
                    sb.append(s2);
                    BitmapFactory.decodeStream(assets.open(sb.toString()));
                }
                finally {
                    bitmap2 = bitmap;
                }
                bitmap = bitmap2;
                if (bitmap2 != null) {
                    return bitmap2;
                }
            }
            final int resourceIcon = getResourceIcon(s);
            if (resourceIcon != 0) {
                return BitmapFactory.decodeResource(GenerateNotification.contextResources, resourceIcon);
            }
            return null;
        }
        finally {
            return null;
        }
    }
    
    private static Bitmap getBitmapFromURL(final String s) {
        try {
            return BitmapFactory.decodeStream(new URL(s).openConnection().getInputStream());
        }
        finally {
            final Throwable t;
            OneSignal.Log(OneSignal.LOG_LEVEL.WARN, "Could not download image!", t);
            return null;
        }
    }
    
    private static Bitmap getDefaultLargeIcon() {
        return resizeBitmapForLargeIconArea(getBitmapFromAssetsOrResourceName("ic_onesignal_large_icon_default"));
    }
    
    private static int getDefaultSmallIconId() {
        final int drawableId = getDrawableId("ic_stat_onesignal_default");
        if (drawableId != 0) {
            return drawableId;
        }
        final int drawableId2 = getDrawableId("corona_statusbar_icon_default");
        if (drawableId2 != 0) {
            return drawableId2;
        }
        final int drawableId3 = getDrawableId("ic_os_notification_fallback_white_24dp");
        if (drawableId3 != 0) {
            return drawableId3;
        }
        return 17301598;
    }
    
    private static int getDrawableId(final String s) {
        return GenerateNotification.contextResources.getIdentifier(s, "drawable", GenerateNotification.packageName);
    }
    
    private static Bitmap getLargeIcon(final JSONObject jsonObject) {
        Bitmap bitmap;
        if ((bitmap = getBitmap(jsonObject.optString("licon"))) == null) {
            bitmap = getBitmapFromAssetsOrResourceName("ic_onesignal_large_icon_default");
        }
        if (bitmap == null) {
            return null;
        }
        return resizeBitmapForLargeIconArea(bitmap);
    }
    
    private static Intent getNewBaseDismissIntent(final int n) {
        return new Intent(GenerateNotification.currentContext, (Class)GenerateNotification.notificationDismissedClass).putExtra("androidNotificationId", n).putExtra("dismissed", true);
    }
    
    private static PendingIntent getNewDismissActionPendingIntent(final int n, final Intent intent) {
        return PendingIntent.getBroadcast(GenerateNotification.currentContext, n, intent, 201326592);
    }
    
    private static int getResourceIcon(final String name) {
        if (name == null) {
            return 0;
        }
        final String trim = name.trim();
        if (!OSUtils.isValidResourceName(trim)) {
            return 0;
        }
        final int drawableId = getDrawableId(trim);
        if (drawableId != 0) {
            return drawableId;
        }
        try {
            return R$drawable.class.getField(name).getInt((Object)null);
        }
        finally {
            return 0;
        }
    }
    
    private static int getSmallIconId(final JSONObject jsonObject) {
        final int resourceIcon = getResourceIcon(jsonObject.optString("sicon", (String)null));
        if (resourceIcon != 0) {
            return resourceIcon;
        }
        return getDefaultSmallIconId();
    }
    
    private static CharSequence getTitle(final JSONObject jsonObject) {
        final String optString = jsonObject.optString("title", (String)null);
        if (optString != null) {
            return (CharSequence)optString;
        }
        return GenerateNotification.currentContext.getPackageManager().getApplicationLabel(GenerateNotification.currentContext.getApplicationInfo());
    }
    
    private static void initGroupAlertBehavior() {
        if (Build$VERSION.SDK_INT >= 24) {
            GenerateNotification.groupAlertBehavior = 2;
        }
        else {
            GenerateNotification.groupAlertBehavior = 1;
        }
    }
    
    static void isRunningOnMainThreadCheck() {
        if (!OSUtils.isRunningOnMainThread()) {
            return;
        }
        throw new OSThrowable.OSMainThreadException("Process for showing a notification should never been done on Main Thread!");
    }
    
    private static boolean isSoundEnabled(final JSONObject jsonObject) {
        final String optString = jsonObject.optString("sound", (String)null);
        return !"null".equals((Object)optString) && !"nil".equals((Object)optString);
    }
    
    private static void removeNotifyOptions(final NotificationCompat$Builder notificationCompat$Builder) {
        notificationCompat$Builder.setOnlyAlertOnce(true).setDefaults(0).setSound((Uri)null).setVibrate((long[])null).setTicker((CharSequence)null);
    }
    
    private static Bitmap resizeBitmapForLargeIconArea(final Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        try {
            int n = (int)GenerateNotification.contextResources.getDimension(17104902);
            final int n2 = (int)GenerateNotification.contextResources.getDimension(17104901);
            final int height = bitmap.getHeight();
            final int width = bitmap.getWidth();
            if (width <= n2) {
                final Bitmap scaledBitmap = bitmap;
                if (height <= n) {
                    return scaledBitmap;
                }
            }
            int n3;
            if (height > width) {
                n3 = (int)(n * (width / (float)height));
            }
            else {
                n3 = n2;
                if (width > height) {
                    n = (int)(n2 * (height / (float)width));
                    n3 = n2;
                }
            }
            return Bitmap.createScaledBitmap(bitmap, n3, n, true);
        }
        finally {
            return bitmap;
        }
    }
    
    private static Integer safeGetColorFromHex(final JSONObject jsonObject, final String s) {
        Label_0037: {
            if (jsonObject == null) {
                break Label_0037;
            }
            try {
                if (jsonObject.has(s)) {
                    return new BigInteger(jsonObject.optString(s), 16).intValue();
                }
                return null;
            }
            finally {
                return null;
            }
        }
    }
    
    private static void setAlertnessOptions(final JSONObject jsonObject, final NotificationCompat$Builder notificationCompat$Builder) {
        final int convertOSToAndroidPriority = convertOSToAndroidPriority(jsonObject.optInt("pri", 6));
        notificationCompat$Builder.setPriority(convertOSToAndroidPriority);
        int n = 0;
        if (convertOSToAndroidPriority < 0) {
            return;
        }
        Label_0101: {
            if (jsonObject.has("ledc") && jsonObject.optInt("led", 1) == 1) {
                try {
                    notificationCompat$Builder.setLights(new BigInteger(jsonObject.optString("ledc"), 16).intValue(), 2000, 5000);
                    break Label_0101;
                }
                finally {}
            }
            n = 4;
        }
        int n2 = n;
        if (jsonObject.optInt("vib", 1) == 1) {
            if (jsonObject.has("vib_pt")) {
                final long[] vibrationPattern = OSUtils.parseVibrationPattern(jsonObject);
                n2 = n;
                if (vibrationPattern != null) {
                    notificationCompat$Builder.setVibrate(vibrationPattern);
                    n2 = n;
                }
            }
            else {
                n2 = (n | 0x2);
            }
        }
        int defaults = n2;
        if (isSoundEnabled(jsonObject)) {
            final Uri soundUri = OSUtils.getSoundUri(GenerateNotification.currentContext, jsonObject.optString("sound", (String)null));
            if (soundUri != null) {
                notificationCompat$Builder.setSound(soundUri);
                defaults = n2;
            }
            else {
                defaults = (n2 | 0x1);
            }
        }
        notificationCompat$Builder.setDefaults(defaults);
    }
    
    private static void setStatics(final Context currentContext) {
        GenerateNotification.currentContext = currentContext;
        GenerateNotification.packageName = currentContext.getPackageName();
        GenerateNotification.contextResources = GenerateNotification.currentContext.getResources();
    }
    
    private static void setTextColor(final RemoteViews remoteViews, final JSONObject jsonObject, final int n, final String s, final String s2) {
        final Integer safeGetColorFromHex = safeGetColorFromHex(jsonObject, s);
        if (safeGetColorFromHex != null) {
            remoteViews.setTextColor(n, (int)safeGetColorFromHex);
        }
        else {
            final int identifier = GenerateNotification.contextResources.getIdentifier(s2, "color", GenerateNotification.packageName);
            if (identifier != 0) {
                remoteViews.setTextColor(n, AndroidSupportV4Compat.ContextCompat.getColor(GenerateNotification.currentContext, identifier));
            }
        }
    }
    
    private static boolean showNotification(final OSNotificationGenerationJob osNotificationGenerationJob) {
        final int intValue = osNotificationGenerationJob.getAndroidId();
        final JSONObject jsonPayload = osNotificationGenerationJob.getJsonPayload();
        final String optString = jsonPayload.optString("grp", (String)null);
        final IntentGeneratorForAttachingToNotifications intentGeneratorForAttachingToNotifications = new IntentGeneratorForAttachingToNotifications(GenerateNotification.currentContext);
        ArrayList list = new ArrayList();
        String grouplessSummaryKey = optString;
        if (Build$VERSION.SDK_INT >= 24) {
            final ArrayList<StatusBarNotification> activeGrouplessNotifications = OneSignalNotificationManager.getActiveGrouplessNotifications(GenerateNotification.currentContext);
            grouplessSummaryKey = optString;
            list = activeGrouplessNotifications;
            if (optString == null) {
                grouplessSummaryKey = optString;
                list = activeGrouplessNotifications;
                if (activeGrouplessNotifications.size() >= 3) {
                    grouplessSummaryKey = OneSignalNotificationManager.getGrouplessSummaryKey();
                    OneSignalNotificationManager.assignGrouplessNotifications(GenerateNotification.currentContext, activeGrouplessNotifications);
                    list = activeGrouplessNotifications;
                }
            }
        }
        final OneSignalNotificationBuilder baseOneSignalNotificationBuilder = getBaseOneSignalNotificationBuilder(osNotificationGenerationJob);
        final NotificationCompat$Builder compatBuilder = baseOneSignalNotificationBuilder.compatBuilder;
        addNotificationActionButtons(jsonPayload, intentGeneratorForAttachingToNotifications, compatBuilder, intValue, null);
        try {
            addBackgroundImage(jsonPayload, compatBuilder);
        }
        finally {
            final Throwable t;
            OneSignal.Log(OneSignal.LOG_LEVEL.ERROR, "Could not set background notification image!", t);
        }
        applyNotificationExtender(osNotificationGenerationJob, compatBuilder);
        if (osNotificationGenerationJob.isRestoring()) {
            removeNotifyOptions(compatBuilder);
        }
        int n;
        if (grouplessSummaryKey != null) {
            n = 2;
        }
        else {
            n = 1;
        }
        NotificationLimitManager.clearOldestOverLimit(GenerateNotification.currentContext, n);
        Notification genericPendingIntentsForNotif;
        if (grouplessSummaryKey != null) {
            createGenericPendingIntentsForGroup(compatBuilder, intentGeneratorForAttachingToNotifications, jsonPayload, grouplessSummaryKey, intValue);
            final Notification singleNotificationBeforeSummaryBuilder = createSingleNotificationBeforeSummaryBuilder(osNotificationGenerationJob, compatBuilder);
            if (Build$VERSION.SDK_INT >= 24 && grouplessSummaryKey.equals((Object)OneSignalNotificationManager.getGrouplessSummaryKey())) {
                createGrouplessSummaryNotification(osNotificationGenerationJob, intentGeneratorForAttachingToNotifications, list.size() + 1);
                genericPendingIntentsForNotif = singleNotificationBeforeSummaryBuilder;
            }
            else {
                createSummaryNotification(osNotificationGenerationJob, baseOneSignalNotificationBuilder);
                genericPendingIntentsForNotif = singleNotificationBeforeSummaryBuilder;
            }
        }
        else {
            genericPendingIntentsForNotif = createGenericPendingIntentsForNotif(compatBuilder, intentGeneratorForAttachingToNotifications, jsonPayload, intValue);
        }
        addXiaomiSettings(baseOneSignalNotificationBuilder, genericPendingIntentsForNotif);
        NotificationManagerCompat.from(GenerateNotification.currentContext).notify(intValue, genericPendingIntentsForNotif);
        return Build$VERSION.SDK_INT < 26 || OneSignalNotificationManager.areNotificationsEnabled(GenerateNotification.currentContext, OneSignal$$ExternalSyntheticApiModelOutline0.m(genericPendingIntentsForNotif));
    }
    
    static void updateSummaryNotification(final OSNotificationGenerationJob osNotificationGenerationJob) {
        setStatics(osNotificationGenerationJob.getContext());
        createSummaryNotification(osNotificationGenerationJob, null);
    }
    
    private static class OneSignalNotificationBuilder
    {
        NotificationCompat$Builder compatBuilder;
        boolean hasLargeIcon;
    }
}
