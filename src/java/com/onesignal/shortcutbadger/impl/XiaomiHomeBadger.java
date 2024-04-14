package com.onesignal.shortcutbadger.impl;

import java.util.Arrays;
import java.util.List;
import java.lang.reflect.Field;
import android.os.Build;
import com.onesignal.shortcutbadger.util.BroadcastHelper;
import android.content.ComponentName;
import android.app.Notification;
import com.onesignal.shortcutbadger.ShortcutBadgeException;
import android.app.Notification$Builder;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.Context;
import android.content.pm.ResolveInfo;
import com.onesignal.shortcutbadger.Badger;

@Deprecated
public class XiaomiHomeBadger implements Badger
{
    public static final String EXTRA_UPDATE_APP_COMPONENT_NAME = "android.intent.extra.update_application_component_name";
    public static final String EXTRA_UPDATE_APP_MSG_TEXT = "android.intent.extra.update_application_message_text";
    public static final String INTENT_ACTION = "android.intent.action.APPLICATION_MESSAGE_UPDATE";
    private ResolveInfo resolveInfo;
    
    private void tryNewMiuiBadge(final Context context, final int n) throws ShortcutBadgeException {
        if (this.resolveInfo == null) {
            final Intent intent = new Intent("android.intent.action.MAIN");
            intent.addCategory("android.intent.category.HOME");
            this.resolveInfo = context.getPackageManager().resolveActivity(intent, 65536);
        }
        if (this.resolveInfo != null) {
            final NotificationManager notificationManager = (NotificationManager)context.getSystemService("notification");
            final Notification build = new Notification$Builder(context).setContentTitle((CharSequence)"").setContentText((CharSequence)"").setSmallIcon(this.resolveInfo.getIconResource()).build();
            try {
                final Object value = build.getClass().getDeclaredField("extraNotification").get((Object)build);
                value.getClass().getDeclaredMethod("setMessageCount", Integer.TYPE).invoke(value, new Object[] { n });
                notificationManager.notify(0, build);
            }
            catch (final Exception ex) {
                throw new ShortcutBadgeException("not able to set badge", ex);
            }
        }
    }
    
    @Override
    public void executeBadge(final Context context, final ComponentName componentName, final int n) throws ShortcutBadgeException {
        final String s = "";
        try {
            final Object instance = Class.forName("android.app.MiuiNotification").newInstance();
            final Field declaredField = instance.getClass().getDeclaredField("messageCount");
            declaredField.setAccessible(true);
            Label_0049: {
                if (n == 0) {
                    final Object value = "";
                    break Label_0049;
                }
                try {
                    final Object value = n;
                    declaredField.set(instance, (Object)String.valueOf(value));
                }
                catch (final Exception ex) {
                    declaredField.set(instance, (Object)n);
                }
            }
        }
        catch (final Exception ex2) {
            final Intent intent = new Intent("android.intent.action.APPLICATION_MESSAGE_UPDATE");
            final StringBuilder sb = new StringBuilder();
            sb.append(componentName.getPackageName());
            sb.append("/");
            sb.append(componentName.getClassName());
            intent.putExtra("android.intent.extra.update_application_component_name", sb.toString());
            Object value2;
            if (n == 0) {
                value2 = s;
            }
            else {
                value2 = n;
            }
            intent.putExtra("android.intent.extra.update_application_message_text", String.valueOf(value2));
            if (BroadcastHelper.canResolveBroadcast(context, intent)) {
                context.sendBroadcast(intent);
            }
        }
        if (Build.MANUFACTURER.equalsIgnoreCase("Xiaomi")) {
            this.tryNewMiuiBadge(context, n);
        }
    }
    
    @Override
    public List<String> getSupportLaunchers() {
        return (List<String>)Arrays.asList((Object[])new String[] { "com.miui.miuilite", "com.miui.home", "com.miui.miuihome", "com.miui.miuihome2", "com.miui.mihome", "com.miui.mihome2", "com.i.miui.launcher" });
    }
}
