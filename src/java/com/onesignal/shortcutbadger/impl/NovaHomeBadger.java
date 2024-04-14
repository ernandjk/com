package com.onesignal.shortcutbadger.impl;

import java.util.Arrays;
import java.util.List;
import com.onesignal.shortcutbadger.ShortcutBadgeException;
import android.net.Uri;
import android.content.ContentValues;
import android.content.ComponentName;
import android.content.Context;
import com.onesignal.shortcutbadger.Badger;

public class NovaHomeBadger implements Badger
{
    private static final String CONTENT_URI = "content://com.teslacoilsw.notifier/unread_count";
    private static final String COUNT = "count";
    private static final String TAG = "tag";
    
    @Override
    public void executeBadge(final Context context, final ComponentName componentName, final int n) throws ShortcutBadgeException {
        final ContentValues contentValues = new ContentValues();
        final StringBuilder sb = new StringBuilder();
        sb.append(componentName.getPackageName());
        sb.append("/");
        sb.append(componentName.getClassName());
        contentValues.put("tag", sb.toString());
        contentValues.put("count", Integer.valueOf(n));
        context.getContentResolver().insert(Uri.parse("content://com.teslacoilsw.notifier/unread_count"), contentValues);
    }
    
    @Override
    public List<String> getSupportLaunchers() {
        return (List<String>)Arrays.asList((Object[])new String[] { "com.teslacoilsw.launcher" });
    }
}
