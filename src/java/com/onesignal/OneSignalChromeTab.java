package com.onesignal;

import androidx.browser.customtabs.CustomTabsServiceConnection;
import androidx.browser.customtabs.CustomTabsClient;

class OneSignalChromeTab
{
    private static boolean hasChromeTabLibrary() {
        return true;
    }
    
    protected static boolean open(final String s, final boolean b) {
        return hasChromeTabLibrary() && CustomTabsClient.bindCustomTabsService(OneSignal.appContext, "com.android.chrome", (CustomTabsServiceConnection)new OneSignalChromeTab.OneSignalChromeTab$OneSignalCustomTabsServiceConnection(s, b));
    }
}
