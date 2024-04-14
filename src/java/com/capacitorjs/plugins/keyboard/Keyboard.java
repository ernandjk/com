package com.capacitorjs.plugins.keyboard;

import android.view.inputmethod.InputMethodManager;
import android.view.Display;
import com.getcapacitor.Logger;
import com.getcapacitor.Bridge$$ExternalSyntheticApiModelOutline0;
import android.os.Build$VERSION;
import android.graphics.Rect;
import android.widget.FrameLayout;
import android.view.WindowInsets;
import android.graphics.Point;
import android.view.View;
import android.view.ViewTreeObserver$OnGlobalLayoutListener;
import android.widget.FrameLayout$LayoutParams;
import androidx.appcompat.app.AppCompatActivity;

public class Keyboard
{
    static final String EVENT_KB_DID_HIDE = "keyboardDidHide";
    static final String EVENT_KB_DID_SHOW = "keyboardDidShow";
    static final String EVENT_KB_WILL_HIDE = "keyboardWillHide";
    static final String EVENT_KB_WILL_SHOW = "keyboardWillShow";
    private AppCompatActivity activity;
    private FrameLayout$LayoutParams frameLayoutParams;
    private KeyboardEventListener keyboardEventListener;
    private ViewTreeObserver$OnGlobalLayoutListener list;
    private View mChildOfContent;
    private View rootView;
    private int usableHeightPrevious;
    
    public Keyboard(final AppCompatActivity activity, final boolean b) {
        this.activity = activity;
        final float density = activity.getResources().getDisplayMetrics().density;
        final FrameLayout frameLayout = (FrameLayout)activity.getWindow().getDecorView().findViewById(16908290);
        this.rootView = frameLayout.getRootView();
        this.list = (ViewTreeObserver$OnGlobalLayoutListener)new ViewTreeObserver$OnGlobalLayoutListener(this, density, b, activity) {
            int previousHeightDiff = 0;
            final Keyboard this$0;
            final AppCompatActivity val$activity;
            final float val$density;
            final boolean val$resizeOnFullScreen;
            
            private int computeUsableHeight() {
                final Rect rect = new Rect();
                this.this$0.mChildOfContent.getWindowVisibleDisplayFrame(rect);
                int n;
                if (this.isOverlays()) {
                    n = rect.bottom;
                }
                else {
                    n = rect.height();
                }
                return n;
            }
            
            private boolean isOverlays() {
                return (this.val$activity.getWindow().getDecorView().getSystemUiVisibility() & 0x400) == 0x400;
            }
            
            private void possiblyResizeChildOfContent(final boolean b) {
                int computeUsableHeight;
                if (b) {
                    computeUsableHeight = this.computeUsableHeight();
                }
                else {
                    computeUsableHeight = -1;
                }
                if (this.this$0.usableHeightPrevious != computeUsableHeight) {
                    this.this$0.frameLayoutParams.height = computeUsableHeight;
                    this.this$0.mChildOfContent.requestLayout();
                    Keyboard.-$$Nest$fputusableHeightPrevious(this.this$0, computeUsableHeight);
                }
            }
            
            public void onGlobalLayout() {
                final Rect rect = new Rect();
                this.this$0.rootView.getWindowVisibleDisplayFrame(rect);
                final int height = this.this$0.rootView.getRootView().getHeight();
                int bottom = rect.bottom;
                int y = 0;
                Label_0122: {
                    int n;
                    if (Build$VERSION.SDK_INT >= 30) {
                        n = Bridge$$ExternalSyntheticApiModelOutline0.m(Bridge$$ExternalSyntheticApiModelOutline0.m(Bridge$$ExternalSyntheticApiModelOutline0.m(this.this$0.rootView), Bridge$$ExternalSyntheticApiModelOutline0.m()));
                    }
                    else {
                        if (Build$VERSION.SDK_INT < 23) {
                            y = this.this$0.getLegacySizePoint().y;
                            break Label_0122;
                        }
                        n = this.this$0.getLegacyStableInsetBottom(Bridge$$ExternalSyntheticApiModelOutline0.m(this.this$0.rootView));
                    }
                    bottom += n;
                    y = height;
                }
                final int previousHeightDiff = (int)((y - bottom) / this.val$density);
                if (previousHeightDiff > 100 && previousHeightDiff != this.previousHeightDiff) {
                    if (this.val$resizeOnFullScreen) {
                        this.possiblyResizeChildOfContent(true);
                    }
                    if (this.this$0.keyboardEventListener != null) {
                        this.this$0.keyboardEventListener.onKeyboardEvent("keyboardWillShow", previousHeightDiff);
                        this.this$0.keyboardEventListener.onKeyboardEvent("keyboardDidShow", previousHeightDiff);
                    }
                    else {
                        Logger.warn("Native Keyboard Event Listener not found");
                    }
                }
                else {
                    final int previousHeightDiff2 = this.previousHeightDiff;
                    if (previousHeightDiff != previousHeightDiff2 && previousHeightDiff2 - previousHeightDiff > 100) {
                        if (this.val$resizeOnFullScreen) {
                            this.possiblyResizeChildOfContent(false);
                        }
                        if (this.this$0.keyboardEventListener != null) {
                            this.this$0.keyboardEventListener.onKeyboardEvent("keyboardWillHide", 0);
                            this.this$0.keyboardEventListener.onKeyboardEvent("keyboardDidHide", 0);
                        }
                        else {
                            Logger.warn("Native Keyboard Event Listener not found");
                        }
                    }
                }
                this.previousHeightDiff = previousHeightDiff;
            }
        };
        this.mChildOfContent = frameLayout.getChildAt(0);
        this.rootView.getViewTreeObserver().addOnGlobalLayoutListener(this.list);
        this.frameLayoutParams = (FrameLayout$LayoutParams)this.mChildOfContent.getLayoutParams();
    }
    
    private Point getLegacySizePoint() {
        final Display defaultDisplay = this.activity.getWindowManager().getDefaultDisplay();
        final Point point = new Point();
        defaultDisplay.getSize(point);
        return point;
    }
    
    private int getLegacyStableInsetBottom(final WindowInsets windowInsets) {
        return windowInsets.getStableInsetBottom();
    }
    
    public KeyboardEventListener getKeyboardEventListener() {
        return this.keyboardEventListener;
    }
    
    public boolean hide() {
        final InputMethodManager inputMethodManager = (InputMethodManager)this.activity.getSystemService("input_method");
        final View currentFocus = this.activity.getCurrentFocus();
        if (currentFocus == null) {
            return false;
        }
        inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 2);
        return true;
    }
    
    public void setKeyboardEventListener(final KeyboardEventListener keyboardEventListener) {
        this.keyboardEventListener = keyboardEventListener;
    }
    
    public void show() {
        ((InputMethodManager)this.activity.getSystemService("input_method")).showSoftInput(this.activity.getCurrentFocus(), 0);
    }
    
    interface KeyboardEventListener
    {
        void onKeyboardEvent(final String p0, final int p1);
    }
}
