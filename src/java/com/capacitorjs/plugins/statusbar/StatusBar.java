package com.capacitorjs.plugins.statusbar;

import android.view.View;
import androidx.core.view.WindowInsetsCompat;
import android.view.Window;
import androidx.core.view.WindowInsetsCompat$Type;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.appcompat.app.AppCompatActivity;

public class StatusBar
{
    private final AppCompatActivity activity;
    private int currentStatusBarColor;
    private final String defaultStyle;
    
    public StatusBar(final AppCompatActivity activity) {
        this.activity = activity;
        this.currentStatusBarColor = activity.getWindow().getStatusBarColor();
        this.defaultStyle = this.getStyle();
    }
    
    private boolean getIsOverlaid() {
        return (this.activity.getWindow().getDecorView().getSystemUiVisibility() & 0x400) == 0x400;
    }
    
    private String getStyle() {
        String s;
        if (WindowCompat.getInsetsController(this.activity.getWindow(), this.activity.getWindow().getDecorView()).isAppearanceLightStatusBars()) {
            s = "LIGHT";
        }
        else {
            s = "DARK";
        }
        return s;
    }
    
    public StatusBarInfo getInfo() {
        final Window window = this.activity.getWindow();
        final WindowInsetsCompat rootWindowInsets = ViewCompat.getRootWindowInsets(window.getDecorView());
        final boolean visible = rootWindowInsets != null && rootWindowInsets.isVisible(WindowInsetsCompat$Type.statusBars());
        final StatusBarInfo statusBarInfo = new StatusBarInfo();
        statusBarInfo.setStyle(this.getStyle());
        statusBarInfo.setOverlays(this.getIsOverlaid());
        statusBarInfo.setVisible(visible);
        statusBarInfo.setColor(String.format("#%06X", new Object[] { window.getStatusBarColor() & 0xFFFFFF }));
        return statusBarInfo;
    }
    
    public void hide() {
        WindowCompat.getInsetsController(this.activity.getWindow(), this.activity.getWindow().getDecorView()).hide(WindowInsetsCompat$Type.statusBars());
    }
    
    public void setBackgroundColor(final int n) {
        final Window window = this.activity.getWindow();
        window.clearFlags(67108864);
        window.addFlags(Integer.MIN_VALUE);
        window.setStatusBarColor(n);
        this.currentStatusBarColor = n;
    }
    
    public void setOverlaysWebView(final Boolean b) {
        final View decorView = this.activity.getWindow().getDecorView();
        final int systemUiVisibility = decorView.getSystemUiVisibility();
        if (b) {
            decorView.setSystemUiVisibility(systemUiVisibility | 0x100 | 0x400);
            this.currentStatusBarColor = this.activity.getWindow().getStatusBarColor();
            this.activity.getWindow().setStatusBarColor(0);
        }
        else {
            decorView.setSystemUiVisibility(systemUiVisibility & 0xFFFFFEFF & 0xFFFFFBFF);
            this.activity.getWindow().setStatusBarColor(this.currentStatusBarColor);
        }
    }
    
    public void setStyle(final String s) {
        final Window window = this.activity.getWindow();
        final View decorView = window.getDecorView();
        String defaultStyle = s;
        if (s.equals((Object)"DEFAULT")) {
            defaultStyle = this.defaultStyle;
        }
        WindowCompat.getInsetsController(window, decorView).setAppearanceLightStatusBars(defaultStyle.equals((Object)"DARK") ^ true);
    }
    
    public void show() {
        WindowCompat.getInsetsController(this.activity.getWindow(), this.activity.getWindow().getDecorView()).show(WindowInsetsCompat$Type.statusBars());
    }
}
