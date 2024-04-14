package com.onesignal.shortcutbadger.impl;

import java.util.Collections;
import java.util.List;
import android.net.Uri;
import android.os.Bundle;
import com.onesignal.shortcutbadger.ShortcutBadgeException;
import com.onesignal.shortcutbadger.util.BroadcastHelper;
import android.content.Intent;
import android.content.ComponentName;
import android.content.Context;
import com.onesignal.shortcutbadger.Badger;

public class OPPOHomeBader implements Badger
{
    private static final String INTENT_ACTION = "com.oppo.unsettledevent";
    private static final String INTENT_EXTRA_BADGEUPGRADE_COUNT = "app_badge_count";
    private static final String INTENT_EXTRA_BADGE_COUNT = "number";
    private static final String INTENT_EXTRA_BADGE_UPGRADENUMBER = "upgradeNumber";
    private static final String INTENT_EXTRA_PACKAGENAME = "pakeageName";
    private static final String PROVIDER_CONTENT_URI = "content://com.android.badge/badge";
    private int mCurrentTotalCount;
    
    public OPPOHomeBader() {
        this.mCurrentTotalCount = -1;
    }
    
    private void executeBadgeByBroadcast(final Context context, final ComponentName componentName, final int n) throws ShortcutBadgeException {
        int n2 = n;
        if (n == 0) {
            n2 = -1;
        }
        final Intent intent = new Intent("com.oppo.unsettledevent");
        intent.putExtra("pakeageName", componentName.getPackageName());
        intent.putExtra("number", n2);
        intent.putExtra("upgradeNumber", n2);
        BroadcastHelper.sendIntentExplicitly(context, intent);
    }
    
    private void executeBadgeByContentProvider(final Context context, final int n) throws ShortcutBadgeException {
        try {
            final Bundle bundle = new Bundle();
            bundle.putInt("app_badge_count", n);
            context.getContentResolver().call(Uri.parse("content://com.android.badge/badge"), "setAppBadgeCount", (String)null, bundle);
        }
        finally {
            throw new ShortcutBadgeException("Unable to execute Badge By Content Provider");
        }
    }
    
    @Override
    public void executeBadge(final Context context, final ComponentName componentName, final int mCurrentTotalCount) throws ShortcutBadgeException {
        if (this.mCurrentTotalCount == mCurrentTotalCount) {
            return;
        }
        this.executeBadgeByContentProvider(context, this.mCurrentTotalCount = mCurrentTotalCount);
    }
    
    @Override
    public List<String> getSupportLaunchers() {
        return (List<String>)Collections.singletonList((Object)"com.oppo.launcher");
    }
}
