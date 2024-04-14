package com.getcapacitor;

public class App
{
    private AppRestoredListener appRestoredListener;
    private boolean isActive;
    private AppStatusChangeListener statusChangeListener;
    
    public App() {
        this.isActive = false;
    }
    
    protected void fireRestoredResult(final PluginResult pluginResult) {
        final AppRestoredListener appRestoredListener = this.appRestoredListener;
        if (appRestoredListener != null) {
            appRestoredListener.onAppRestored(pluginResult);
        }
    }
    
    public void fireStatusChange(final boolean isActive) {
        this.isActive = isActive;
        final AppStatusChangeListener statusChangeListener = this.statusChangeListener;
        if (statusChangeListener != null) {
            statusChangeListener.onAppStatusChanged(isActive);
        }
    }
    
    public boolean isActive() {
        return this.isActive;
    }
    
    public void setAppRestoredListener(final AppRestoredListener appRestoredListener) {
        this.appRestoredListener = appRestoredListener;
    }
    
    public void setStatusChangeListener(final AppStatusChangeListener statusChangeListener) {
        this.statusChangeListener = statusChangeListener;
    }
    
    public interface AppRestoredListener
    {
        void onAppRestored(final PluginResult p0);
    }
    
    public interface AppStatusChangeListener
    {
        void onAppStatusChanged(final Boolean p0);
    }
}
