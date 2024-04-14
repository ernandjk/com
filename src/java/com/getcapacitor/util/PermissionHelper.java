package com.getcapacitor.util;

import androidx.core.app.ActivityCompat;
import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;
import android.content.pm.PackageInfo;
import android.content.Context;

public class PermissionHelper
{
    public static String[] getManifestPermissions(final Context context) {
        final String[] array = null;
        try {
            final PackageInfo packageInfo = InternalUtils.getPackageInfo(context.getPackageManager(), context.getPackageName(), 4096L);
            String[] requestedPermissions = array;
            if (packageInfo != null) {
                requestedPermissions = packageInfo.requestedPermissions;
            }
            return requestedPermissions;
        }
        catch (final Exception ex) {
            return array;
        }
    }
    
    public static String[] getUndefinedPermissions(final Context context, final String[] array) {
        final ArrayList list = new ArrayList();
        final String[] manifestPermissions = getManifestPermissions(context);
        if (manifestPermissions != null && manifestPermissions.length > 0) {
            final ArrayList list2 = new ArrayList((Collection)Arrays.asList((Object[])manifestPermissions));
            for (final String s : array) {
                if (!list2.contains((Object)s)) {
                    list.add((Object)s);
                }
            }
            return (String[])list.toArray((Object[])new String[list.size()]);
        }
        return array;
    }
    
    public static boolean hasDefinedPermission(final Context context, final String s) {
        final String[] manifestPermissions = getManifestPermissions(context);
        return manifestPermissions != null && manifestPermissions.length > 0 && new ArrayList((Collection)Arrays.asList((Object[])manifestPermissions)).contains((Object)s);
    }
    
    public static boolean hasDefinedPermissions(final Context context, final String[] array) {
        for (int length = array.length, i = 0; i < length; ++i) {
            if (!hasDefinedPermission(context, array[i])) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean hasPermissions(final Context context, final String[] array) {
        for (int length = array.length, i = 0; i < length; ++i) {
            if (ActivityCompat.checkSelfPermission(context, array[i]) != 0) {
                return false;
            }
        }
        return true;
    }
}
