package com.getcapacitor.util;

import com.getcapacitor.Bridge$$ExternalSyntheticApiModelOutline0;
import android.os.Build$VERSION;
import android.content.pm.PackageManager$NameNotFoundException;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class InternalUtils
{
    public static PackageInfo getPackageInfo(final PackageManager packageManager, final String s) throws PackageManager$NameNotFoundException {
        return getPackageInfo(packageManager, s, 0L);
    }
    
    public static PackageInfo getPackageInfo(final PackageManager packageManager, final String s, final long n) throws PackageManager$NameNotFoundException {
        if (Build$VERSION.SDK_INT >= 33) {
            return Bridge$$ExternalSyntheticApiModelOutline0.m(packageManager, s, Bridge$$ExternalSyntheticApiModelOutline0.m(n));
        }
        return getPackageInfoLegacy(packageManager, s, (int)n);
    }
    
    private static PackageInfo getPackageInfoLegacy(final PackageManager packageManager, final String s, final long n) throws PackageManager$NameNotFoundException {
        return packageManager.getPackageInfo(s, (int)n);
    }
}
