package com.onesignal.shortcutbadger.util;

import java.util.Iterator;
import com.onesignal.shortcutbadger.ShortcutBadgeException;
import java.util.Collections;
import android.content.pm.ResolveInfo;
import java.util.List;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.content.Context;

public class BroadcastHelper
{
    public static boolean canResolveBroadcast(final Context context, final Intent intent) {
        final PackageManager packageManager = context.getPackageManager();
        final boolean b = false;
        final List queryBroadcastReceivers = packageManager.queryBroadcastReceivers(intent, 0);
        boolean b2 = b;
        if (queryBroadcastReceivers != null) {
            b2 = b;
            if (queryBroadcastReceivers.size() > 0) {
                b2 = true;
            }
        }
        return b2;
    }
    
    public static List<ResolveInfo> resolveBroadcast(final Context context, final Intent intent) {
        List list = context.getPackageManager().queryBroadcastReceivers(intent, 0);
        if (list == null) {
            list = Collections.emptyList();
        }
        return (List<ResolveInfo>)list;
    }
    
    public static void sendIntentExplicitly(final Context context, final Intent intent) throws ShortcutBadgeException {
        final List<ResolveInfo> resolveBroadcast = resolveBroadcast(context, intent);
        if (resolveBroadcast.size() != 0) {
            for (final ResolveInfo resolveInfo : resolveBroadcast) {
                final Intent intent2 = new Intent(intent);
                if (resolveInfo != null) {
                    intent2.setPackage(resolveInfo.resolvePackageName);
                    context.sendBroadcast(intent2);
                }
            }
            return;
        }
        final StringBuilder sb = new StringBuilder("unable to resolve intent: ");
        sb.append(intent.toString());
        throw new ShortcutBadgeException(sb.toString());
    }
}
