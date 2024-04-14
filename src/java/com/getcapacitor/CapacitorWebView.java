package com.getcapacitor;

import android.view.View;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.EditorInfo;
import android.webkit.ValueCallback;
import android.view.KeyEvent;
import android.util.AttributeSet;
import android.content.Context;
import android.view.inputmethod.BaseInputConnection;
import android.webkit.WebView;

public class CapacitorWebView extends WebView
{
    private Bridge bridge;
    private BaseInputConnection capInputConnection;
    
    public CapacitorWebView(final Context context, final AttributeSet set) {
        super(context, set);
    }
    
    public boolean dispatchKeyEvent(final KeyEvent keyEvent) {
        if (keyEvent.getAction() == 2) {
            final StringBuilder sb = new StringBuilder("document.activeElement.value = document.activeElement.value + '");
            sb.append(keyEvent.getCharacters());
            sb.append("';");
            this.evaluateJavascript(sb.toString(), (ValueCallback)null);
            return false;
        }
        return super.dispatchKeyEvent(keyEvent);
    }
    
    public InputConnection onCreateInputConnection(final EditorInfo editorInfo) {
        final Bridge bridge = this.bridge;
        CapConfig capConfig;
        if (bridge != null) {
            capConfig = bridge.getConfig();
        }
        else {
            capConfig = CapConfig.loadDefault(this.getContext());
        }
        if (capConfig.isInputCaptured()) {
            if (this.capInputConnection == null) {
                this.capInputConnection = new BaseInputConnection((View)this, false);
            }
            return (InputConnection)this.capInputConnection;
        }
        return super.onCreateInputConnection(editorInfo);
    }
    
    public void setBridge(final Bridge bridge) {
        this.bridge = bridge;
    }
}
