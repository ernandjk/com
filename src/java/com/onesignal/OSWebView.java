package com.onesignal;

import android.content.Context;
import android.webkit.WebView;

public class OSWebView extends WebView
{
    public OSWebView(final Context context) {
        super(context);
    }
    
    public void computeScroll() {
    }
    
    public boolean overScrollBy(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8, final boolean b) {
        return false;
    }
    
    public void scrollTo(final int n, final int n2) {
    }
}
