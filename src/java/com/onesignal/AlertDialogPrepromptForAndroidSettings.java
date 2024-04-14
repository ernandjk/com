package com.onesignal;

import android.content.DialogInterface$OnClickListener;
import android.app.AlertDialog$Builder;
import android.content.Context;
import java.util.Arrays;
import kotlin.jvm.internal.Intrinsics;
import android.app.Activity;
import kotlin.Metadata;

@Metadata(bv = { 1, 0, 3 }, d1 = { "\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0001\fB\u0007\b\u0002¢\u0006\u0002\u0010\u0002J&\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\b2\u0006\u0010\n\u001a\u00020\u000b¨\u0006\r" }, d2 = { "Lcom/onesignal/AlertDialogPrepromptForAndroidSettings;", "", "()V", "show", "", "activity", "Landroid/app/Activity;", "titlePrefix", "", "previouslyDeniedPostfix", "callback", "Lcom/onesignal/AlertDialogPrepromptForAndroidSettings$Callback;", "Callback", "onesignal_release" }, k = 1, mv = { 1, 4, 2 })
public final class AlertDialogPrepromptForAndroidSettings
{
    public static final AlertDialogPrepromptForAndroidSettings INSTANCE;
    
    static {
        INSTANCE = new AlertDialogPrepromptForAndroidSettings();
    }
    
    private AlertDialogPrepromptForAndroidSettings() {
    }
    
    public final void show(final Activity activity, String format, String format2, final Callback callback) {
        Intrinsics.checkNotNullParameter((Object)activity, "activity");
        Intrinsics.checkNotNullParameter((Object)format, "titlePrefix");
        Intrinsics.checkNotNullParameter((Object)format2, "previouslyDeniedPostfix");
        Intrinsics.checkNotNullParameter((Object)callback, "callback");
        final String string = activity.getString(R.string.permission_not_available_title);
        Intrinsics.checkNotNullExpressionValue((Object)string, "activity.getString(R.str\u2026sion_not_available_title)");
        format = String.format(string, Arrays.copyOf(new Object[] { format }, 1));
        Intrinsics.checkNotNullExpressionValue((Object)format, "java.lang.String.format(this, *args)");
        final String string2 = activity.getString(R.string.permission_not_available_message);
        Intrinsics.checkNotNullExpressionValue((Object)string2, "activity.getString(R.str\u2026on_not_available_message)");
        format2 = String.format(string2, Arrays.copyOf(new Object[] { format2 }, 1));
        Intrinsics.checkNotNullExpressionValue((Object)format2, "java.lang.String.format(this, *args)");
        new AlertDialog$Builder((Context)activity).setTitle((CharSequence)format).setMessage((CharSequence)format2).setPositiveButton(R.string.permission_not_available_open_settings_option, (DialogInterface$OnClickListener)new AlertDialogPrepromptForAndroidSettings$show.AlertDialogPrepromptForAndroidSettings$show$1(callback)).setNegativeButton(17039369, (DialogInterface$OnClickListener)new AlertDialogPrepromptForAndroidSettings$show.AlertDialogPrepromptForAndroidSettings$show$2(callback)).show();
    }
    
    @Metadata(bv = { 1, 0, 3 }, d1 = { "\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H&J\b\u0010\u0004\u001a\u00020\u0003H&¨\u0006\u0005" }, d2 = { "Lcom/onesignal/AlertDialogPrepromptForAndroidSettings$Callback;", "", "onAccept", "", "onDecline", "onesignal_release" }, k = 1, mv = { 1, 4, 2 })
    public interface Callback
    {
        void onAccept();
        
        void onDecline();
    }
}
