package com.onesignal.shortcutbadger;

import java.util.Iterator;
import android.content.pm.ResolveInfo;
import com.onesignal.shortcutbadger.impl.DefaultBadger;
import android.content.Intent;
import android.os.Build;
import android.app.Notification;
import android.util.Log;
import android.content.Context;
import com.onesignal.shortcutbadger.impl.EverythingMeHomeBadger;
import com.onesignal.shortcutbadger.impl.VivoHomeBadger;
import com.onesignal.shortcutbadger.impl.ZukHomeBadger;
import com.onesignal.shortcutbadger.impl.SamsungHomeBadger;
import com.onesignal.shortcutbadger.impl.OPPOHomeBader;
import com.onesignal.shortcutbadger.impl.HuaweiHomeBadger;
import com.onesignal.shortcutbadger.impl.AsusHomeBadger;
import com.onesignal.shortcutbadger.impl.SonyHomeBadger;
import com.onesignal.shortcutbadger.impl.NovaHomeBadger;
import com.onesignal.shortcutbadger.impl.NewHtcHomeBadger;
import com.onesignal.shortcutbadger.impl.ApexHomeBadger;
import com.onesignal.shortcutbadger.impl.AdwHomeBadger;
import java.util.LinkedList;
import android.content.ComponentName;
import java.util.List;

public final class ShortcutBadger
{
    private static final List<Class<? extends Badger>> BADGERS;
    private static final String LOG_TAG = "ShortcutBadger";
    private static final int SUPPORTED_CHECK_ATTEMPTS = 3;
    private static ComponentName sComponentName;
    private static final Object sCounterSupportedLock;
    private static volatile Boolean sIsBadgeCounterSupported;
    private static Badger sShortcutBadger;
    
    static {
        final List<Class<? extends Badger>> list = (List<Class<? extends Badger>>)(BADGERS = (List)new LinkedList());
        sCounterSupportedLock = new Object();
        list.add((Object)AdwHomeBadger.class);
        list.add((Object)ApexHomeBadger.class);
        list.add((Object)NewHtcHomeBadger.class);
        list.add((Object)NovaHomeBadger.class);
        list.add((Object)SonyHomeBadger.class);
        list.add((Object)AsusHomeBadger.class);
        list.add((Object)HuaweiHomeBadger.class);
        list.add((Object)OPPOHomeBader.class);
        list.add((Object)SamsungHomeBadger.class);
        list.add((Object)ZukHomeBadger.class);
        list.add((Object)VivoHomeBadger.class);
        list.add((Object)EverythingMeHomeBadger.class);
    }
    
    private ShortcutBadger() {
    }
    
    public static boolean applyCount(final Context context, final int n) {
        try {
            applyCountOrThrow(context, n);
            return true;
        }
        catch (final ShortcutBadgeException ex) {
            if (Log.isLoggable("ShortcutBadger", 3)) {
                Log.d("ShortcutBadger", "Unable to execute badge", (Throwable)ex);
            }
            return false;
        }
    }
    
    public static void applyCountOrThrow(final Context context, final int n) throws ShortcutBadgeException {
        if (ShortcutBadger.sShortcutBadger == null) {
            if (!initBadger(context)) {
                throw new ShortcutBadgeException("No default launcher available");
            }
        }
        try {
            ShortcutBadger.sShortcutBadger.executeBadge(context, ShortcutBadger.sComponentName, n);
        }
        catch (final Exception ex) {
            throw new ShortcutBadgeException("Unable to execute badge", ex);
        }
    }
    
    public static void applyNotification(final Context context, final Notification notification, final int n) {
        if (Build.MANUFACTURER.equalsIgnoreCase("Xiaomi")) {
            try {
                final Object value = notification.getClass().getDeclaredField("extraNotification").get((Object)notification);
                value.getClass().getDeclaredMethod("setMessageCount", Integer.TYPE).invoke(value, new Object[] { n });
            }
            catch (final Exception ex) {
                if (Log.isLoggable("ShortcutBadger", 3)) {
                    Log.d("ShortcutBadger", "Unable to execute badge", (Throwable)ex);
                }
            }
        }
    }
    
    private static boolean initBadger(final Context context) {
        final Intent launchIntentForPackage = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        if (launchIntentForPackage == null) {
            final StringBuilder sb = new StringBuilder("Unable to find launch intent for package ");
            sb.append(context.getPackageName());
            Log.e("ShortcutBadger", sb.toString());
            return false;
        }
        ShortcutBadger.sComponentName = launchIntentForPackage.getComponent();
        final Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        final ResolveInfo resolveActivity = context.getPackageManager().resolveActivity(intent, 65536);
        if (resolveActivity != null && !resolveActivity.activityInfo.name.toLowerCase().contains((CharSequence)"resolver")) {
            final String packageName = resolveActivity.activityInfo.packageName;
            for (final Class clazz : ShortcutBadger.BADGERS) {
                Badger sShortcutBadger;
                try {
                    sShortcutBadger = clazz.newInstance();
                }
                catch (final Exception ex) {
                    sShortcutBadger = null;
                }
                if (sShortcutBadger != null && sShortcutBadger.getSupportLaunchers().contains((Object)packageName)) {
                    ShortcutBadger.sShortcutBadger = sShortcutBadger;
                    break;
                }
            }
            if (ShortcutBadger.sShortcutBadger == null) {
                if (Build.MANUFACTURER.equalsIgnoreCase("ZUK")) {
                    ShortcutBadger.sShortcutBadger = new ZukHomeBadger();
                }
                else if (Build.MANUFACTURER.equalsIgnoreCase("OPPO")) {
                    ShortcutBadger.sShortcutBadger = new OPPOHomeBader();
                }
                else if (Build.MANUFACTURER.equalsIgnoreCase("VIVO")) {
                    ShortcutBadger.sShortcutBadger = new VivoHomeBadger();
                }
                else {
                    ShortcutBadger.sShortcutBadger = new DefaultBadger();
                }
            }
            return true;
        }
        return false;
    }
    
    public static boolean isBadgeCounterSupported(final Context context) {
        if (ShortcutBadger.sIsBadgeCounterSupported == null) {
            final Object sCounterSupportedLock = ShortcutBadger.sCounterSupportedLock;
            synchronized (sCounterSupportedLock) {
                if (ShortcutBadger.sIsBadgeCounterSupported == null) {
                    String message = null;
                    for (int i = 0; i < 3; ++i) {
                        try {
                            final StringBuilder sb = new StringBuilder();
                            sb.append("Checking if platform supports badge counters, attempt ");
                            sb.append(String.format("%d/%d.", new Object[] { i + 1, 3 }));
                            Log.i("ShortcutBadger", sb.toString());
                            if (initBadger(context)) {
                                ShortcutBadger.sShortcutBadger.executeBadge(context, ShortcutBadger.sComponentName, 0);
                                ShortcutBadger.sIsBadgeCounterSupported = true;
                                Log.i("ShortcutBadger", "Badge counter is supported in this platform.");
                                break;
                            }
                            message = "Failed to initialize the badge counter.";
                        }
                        catch (final Exception ex) {
                            message = ex.getMessage();
                        }
                    }
                    if (ShortcutBadger.sIsBadgeCounterSupported == null) {
                        final StringBuilder sb2 = new StringBuilder();
                        sb2.append("Badge counter seems not supported for this platform: ");
                        sb2.append(message);
                        Log.w("ShortcutBadger", sb2.toString());
                        ShortcutBadger.sIsBadgeCounterSupported = false;
                    }
                }
            }
        }
        return ShortcutBadger.sIsBadgeCounterSupported;
    }
    
    public static boolean removeCount(final Context context) {
        return applyCount(context, 0);
    }
    
    public static void removeCountOrThrow(final Context context) throws ShortcutBadgeException {
        applyCountOrThrow(context, 0);
    }
}
