package com.onesignal;

import android.view.Window;
import android.util.DisplayMetrics;
import java.lang.ref.WeakReference;
import android.os.IBinder;
import android.view.WindowInsets;
import android.graphics.Point;
import android.view.DisplayCutout;
import android.view.View;
import android.graphics.Rect;
import android.os.Build$VERSION;
import android.content.res.Resources;
import android.app.Activity;

class OSViewUtils
{
    private static final int MARGIN_ERROR_PX_SIZE;
    
    static {
        MARGIN_ERROR_PX_SIZE = dpToPx(24);
    }
    
    static void decorViewReady(final Activity activity, final Runnable runnable) {
        final StringBuilder sb = new StringBuilder("decorViewReady:");
        sb.append((Object)runnable);
        activity.getWindow().getDecorView().post((Runnable)new Runnable(sb.toString(), runnable) {
            final String val$listenerKey;
            final Runnable val$runnable;
            
            public void run() {
                final ActivityLifecycleHandler activityLifecycleHandler = ActivityLifecycleListener.getActivityLifecycleHandler();
                if (activityLifecycleHandler != null) {
                    activityLifecycleHandler.addActivityAvailableListener(this.val$listenerKey, (ActivityLifecycleHandler.ActivityAvailableListener)new OSViewUtils$1$1(this, activityLifecycleHandler));
                }
            }
        });
    }
    
    static int dpToPx(final int n) {
        return (int)(n * Resources.getSystem().getDisplayMetrics().density);
    }
    
    static int[] getCutoutAndStatusBarInsets(final Activity activity) {
        final Rect windowVisibleDisplayFrame = getWindowVisibleDisplayFrame(activity);
        final View viewById = activity.getWindow().findViewById(16908290);
        final float n = (windowVisibleDisplayFrame.top - viewById.getTop()) / Resources.getSystem().getDisplayMetrics().density;
        final float n2 = (viewById.getBottom() - windowVisibleDisplayFrame.bottom) / Resources.getSystem().getDisplayMetrics().density;
        if (Build$VERSION.SDK_INT == 29) {
            final DisplayCutout m = OneSignal$$ExternalSyntheticApiModelOutline0.m(activity.getWindowManager().getDefaultDisplay());
            if (m != null) {
                final float n3 = OneSignal$$ExternalSyntheticApiModelOutline0.m(m) / Resources.getSystem().getDisplayMetrics().density;
                final float n4 = OneSignal$$ExternalSyntheticApiModelOutline0.m$1(m) / Resources.getSystem().getDisplayMetrics().density;
                return new int[] { Math.round(n), Math.round(n2), Math.round(n3), Math.round(n4) };
            }
        }
        final float n3 = 0.0f;
        final float n4 = 0.0f;
        return new int[] { Math.round(n), Math.round(n2), Math.round(n3), Math.round(n4) };
    }
    
    private static int getDisplaySizeY(final Activity activity) {
        final Point point = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(point);
        return point.y;
    }
    
    static int getFullbleedWindowWidth(final Activity activity) {
        if (Build$VERSION.SDK_INT >= 23) {
            return activity.getWindow().getDecorView().getWidth();
        }
        return getWindowWidth(activity);
    }
    
    static int getWindowHeight(final Activity activity) {
        if (Build$VERSION.SDK_INT >= 23) {
            return getWindowHeightAPI23Plus(activity);
        }
        return getWindowHeightLollipop(activity);
    }
    
    private static int getWindowHeightAPI23Plus(final Activity activity) {
        final View decorView = activity.getWindow().getDecorView();
        final WindowInsets m = OneSignal$$ExternalSyntheticApiModelOutline0.m(decorView);
        if (m == null) {
            return decorView.getHeight();
        }
        return decorView.getHeight() - m.getStableInsetBottom() - m.getStableInsetTop();
    }
    
    private static int getWindowHeightLollipop(final Activity activity) {
        if (activity.getResources().getConfiguration().orientation == 2) {
            return getWindowVisibleDisplayFrame(activity).height();
        }
        return getDisplaySizeY(activity);
    }
    
    private static Rect getWindowVisibleDisplayFrame(final Activity activity) {
        final Rect rect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        return rect;
    }
    
    static int getWindowWidth(final Activity activity) {
        return getWindowVisibleDisplayFrame(activity).width();
    }
    
    static boolean isActivityFullyReady(final Activity activity) {
        final IBinder applicationWindowToken = activity.getWindow().getDecorView().getApplicationWindowToken();
        final boolean b = true;
        final boolean b2 = applicationWindowToken != null;
        if (Build$VERSION.SDK_INT < 23) {
            return b2;
        }
        final boolean b3 = OneSignal$$ExternalSyntheticApiModelOutline0.m(activity.getWindow().getDecorView()) != null;
        return b2 && b3 && b;
    }
    
    static boolean isKeyboardUp(final WeakReference<Activity> weakReference) {
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        final Rect rect = new Rect();
        View decorView;
        if (weakReference.get() != null) {
            final Window window = ((Activity)weakReference.get()).getWindow();
            decorView = window.getDecorView();
            decorView.getWindowVisibleDisplayFrame(rect);
            window.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        }
        else {
            decorView = null;
        }
        boolean b = false;
        if (decorView != null) {
            b = b;
            if (displayMetrics.heightPixels - rect.bottom > OSViewUtils.MARGIN_ERROR_PX_SIZE) {
                b = true;
            }
        }
        return b;
    }
}
