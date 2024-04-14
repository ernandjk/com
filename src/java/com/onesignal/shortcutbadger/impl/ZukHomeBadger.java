package com.onesignal.shortcutbadger.impl;

import java.util.Collections;
import java.util.List;
import com.onesignal.shortcutbadger.ShortcutBadgeException;
import android.os.Bundle;
import android.content.ComponentName;
import android.content.Context;
import android.net.Uri;
import com.onesignal.shortcutbadger.Badger;

public class ZukHomeBadger implements Badger
{
    private final Uri CONTENT_URI;
    
    public ZukHomeBadger() {
        this.CONTENT_URI = Uri.parse("content://com.android.badge/badge");
    }
    
    @Override
    public void executeBadge(final Context context, final ComponentName componentName, final int n) throws ShortcutBadgeException {
        final Bundle bundle = new Bundle();
        bundle.putInt("app_badge_count", n);
        context.getContentResolver().call(this.CONTENT_URI, "setAppBadgeCount", (String)null, bundle);
    }
    
    @Override
    public List<String> getSupportLaunchers() {
        return (List<String>)Collections.singletonList((Object)"com.zui.launcher");
    }
}
