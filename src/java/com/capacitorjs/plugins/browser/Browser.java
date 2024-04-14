package com.capacitorjs.plugins.browser;

import android.content.ServiceConnection;
import android.content.Intent;
import androidx.browser.customtabs.CustomTabsIntent;
import android.os.Parcelable;
import androidx.browser.customtabs.CustomTabColorSchemeParams$Builder;
import androidx.browser.customtabs.CustomTabsIntent$Builder;
import android.net.Uri;
import java.util.List;
import androidx.browser.customtabs.CustomTabsCallback;
import androidx.browser.customtabs.CustomTabsClient;
import android.content.Context;
import androidx.browser.customtabs.CustomTabsServiceConnection;
import androidx.browser.customtabs.CustomTabsSession;

public class Browser
{
    public static final int BROWSER_FINISHED = 2;
    public static final int BROWSER_LOADED = 1;
    private static final String FALLBACK_CUSTOM_TAB_PACKAGE_NAME = "com.android.chrome";
    private BrowserEventListener browserEventListener;
    private CustomTabsSession browserSession;
    private CustomTabsServiceConnection connection;
    private Context context;
    private CustomTabsClient customTabsClient;
    private EventGroup group;
    private boolean isInitialLoad;
    
    public Browser(final Context context) {
        this.isInitialLoad = false;
        this.connection = (CustomTabsServiceConnection)new Browser$1(this);
        this.context = context;
        this.group = new EventGroup((EventGroup.EventGroupCompletion)new Browser$$ExternalSyntheticLambda0(this));
    }
    
    private CustomTabsSession getCustomTabsSession() {
        final CustomTabsClient customTabsClient = this.customTabsClient;
        if (customTabsClient == null) {
            return null;
        }
        if (this.browserSession == null) {
            this.browserSession = customTabsClient.newSession((CustomTabsCallback)new Browser$2(this));
        }
        return this.browserSession;
    }
    
    private void handleGroupCompletion() {
        final BrowserEventListener browserEventListener = this.browserEventListener;
        if (browserEventListener != null) {
            browserEventListener.onBrowserEvent(2);
        }
    }
    
    private void handledNavigationEvent(final int n) {
        if (n != 2) {
            if (n != 5) {
                if (n == 6) {
                    this.group.leave();
                }
            }
            else {
                this.group.enter();
            }
        }
        else if (this.isInitialLoad) {
            final BrowserEventListener browserEventListener = this.browserEventListener;
            if (browserEventListener != null) {
                browserEventListener.onBrowserEvent(1);
            }
            this.isInitialLoad = false;
        }
    }
    
    public boolean bindService() {
        String packageName;
        if ((packageName = CustomTabsClient.getPackageName(this.context, (List)null)) == null) {
            packageName = "com.android.chrome";
        }
        final boolean bindCustomTabsService = CustomTabsClient.bindCustomTabsService(this.context, packageName, this.connection);
        this.group.leave();
        return bindCustomTabsService;
    }
    
    public BrowserEventListener getBrowserEventListenerListener() {
        return this.browserEventListener;
    }
    
    public void open(final Uri uri) {
        this.open(uri, null);
    }
    
    public void open(final Uri uri, final Integer n) {
        final CustomTabsIntent$Builder customTabsIntent$Builder = new CustomTabsIntent$Builder(this.getCustomTabsSession());
        customTabsIntent$Builder.setShareState(1);
        if (n != null) {
            customTabsIntent$Builder.setDefaultColorSchemeParams(new CustomTabColorSchemeParams$Builder().setToolbarColor((int)n).build());
        }
        final CustomTabsIntent build = customTabsIntent$Builder.build();
        final Intent intent = build.intent;
        final StringBuilder sb = new StringBuilder("2//");
        sb.append(this.context.getPackageName());
        intent.putExtra("android.intent.extra.REFERRER", (Parcelable)Uri.parse(sb.toString()));
        this.isInitialLoad = true;
        this.group.reset();
        build.launchUrl(this.context, uri);
    }
    
    public void setBrowserEventListener(final BrowserEventListener browserEventListener) {
        this.browserEventListener = browserEventListener;
    }
    
    public void unbindService() {
        this.context.unbindService((ServiceConnection)this.connection);
        this.group.enter();
    }
    
    interface BrowserEventListener
    {
        void onBrowserEvent(final int p0);
    }
}
