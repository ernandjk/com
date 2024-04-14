package com.onesignal;

import java.io.File;

class RootToolsInternalMethods
{
    static boolean isRooted() {
        int n = 0;
        while (true) {
            if (n >= 8) {
                return false;
            }
            try {
                final String s = (new String[] { "/sbin/", "/system/bin/", "/system/xbin/", "/data/local/xbin/", "/data/local/bin/", "/system/sd/xbin/", "/system/bin/failsafe/", "/data/local/" })[n];
                final StringBuilder sb = new StringBuilder();
                sb.append(s);
                sb.append("su");
                if (new File(sb.toString()).exists()) {
                    return true;
                }
                ++n;
                continue;
            }
            finally {
                return false;
            }
        }
    }
}
