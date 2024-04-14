package com.getcapacitor;

import com.getcapacitor.android.R$layout;
import com.getcapacitor.android.R$style;
import android.os.Bundle;
import android.content.res.Configuration;
import android.content.Intent;
import java.util.ArrayList;
import java.util.List;
import androidx.appcompat.app.AppCompatActivity;

public class BridgeActivity extends AppCompatActivity
{
    protected int activityDepth;
    protected Bridge bridge;
    protected final Bridge$Builder bridgeBuilder;
    protected CapConfig config;
    protected List<Class<? extends Plugin>> initialPlugins;
    protected boolean keepRunning;
    
    public BridgeActivity() {
        this.keepRunning = true;
        this.activityDepth = 0;
        this.initialPlugins = (List<Class<? extends Plugin>>)new ArrayList();
        this.bridgeBuilder = new Bridge$Builder((AppCompatActivity)this);
    }
    
    public Bridge getBridge() {
        return this.bridge;
    }
    
    protected void load() {
        Logger.debug("Starting BridgeActivity");
        final Bridge create = this.bridgeBuilder.addPlugins((List)this.initialPlugins).setConfig(this.config).create();
        this.bridge = create;
        this.keepRunning = create.shouldKeepRunning();
        this.onNewIntent(this.getIntent());
    }
    
    protected void onActivityResult(final int n, final int n2, final Intent intent) {
        final Bridge bridge = this.bridge;
        if (bridge == null) {
            return;
        }
        if (!bridge.onActivityResult(n, n2, intent)) {
            super.onActivityResult(n, n2, intent);
        }
    }
    
    public void onConfigurationChanged(final Configuration configuration) {
        super.onConfigurationChanged(configuration);
        final Bridge bridge = this.bridge;
        if (bridge == null) {
            return;
        }
        bridge.onConfigurationChanged(configuration);
    }
    
    protected void onCreate(final Bundle instanceState) {
        super.onCreate(instanceState);
        this.bridgeBuilder.setInstanceState(instanceState);
        this.getApplication().setTheme(R$style.AppTheme_NoActionBar);
        this.setTheme(R$style.AppTheme_NoActionBar);
        this.setContentView(R$layout.bridge_layout_main);
        final PluginManager pluginManager = new PluginManager(this.getAssets());
        try {
            this.bridgeBuilder.addPlugins(pluginManager.loadPluginClasses());
        }
        catch (final PluginLoadException ex) {
            Logger.error("Error loading plugins.", (Throwable)ex);
        }
        this.load();
    }
    
    public void onDestroy() {
        super.onDestroy();
        this.bridge.onDestroy();
        Logger.debug("App destroyed");
    }
    
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.bridge.onDetachedFromWindow();
    }
    
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
        final Bridge bridge = this.bridge;
        if (bridge != null) {
            if (intent != null) {
                bridge.onNewIntent(intent);
            }
        }
    }
    
    public void onPause() {
        super.onPause();
        this.bridge.onPause();
        Logger.debug("App paused");
    }
    
    public void onRequestPermissionsResult(final int n, final String[] array, final int[] array2) {
        final Bridge bridge = this.bridge;
        if (bridge == null) {
            return;
        }
        if (!bridge.onRequestPermissionsResult(n, array, array2)) {
            super.onRequestPermissionsResult(n, array, array2);
        }
    }
    
    public void onRestart() {
        super.onRestart();
        this.bridge.onRestart();
        Logger.debug("App restarted");
    }
    
    public void onResume() {
        super.onResume();
        this.bridge.getApp().fireStatusChange(true);
        this.bridge.onResume();
        Logger.debug("App resumed");
    }
    
    public void onSaveInstanceState(final Bundle bundle) {
        super.onSaveInstanceState(bundle);
        this.bridge.saveInstanceState(bundle);
    }
    
    public void onStart() {
        super.onStart();
        ++this.activityDepth;
        this.bridge.onStart();
        Logger.debug("App started");
    }
    
    public void onStop() {
        super.onStop();
        final int max = Math.max(0, this.activityDepth - 1);
        this.activityDepth = max;
        if (max == 0) {
            this.bridge.getApp().fireStatusChange(false);
        }
        this.bridge.onStop();
        Logger.debug("App stopped");
    }
    
    public void registerPlugin(final Class<? extends Plugin> clazz) {
        this.bridgeBuilder.addPlugin((Class)clazz);
    }
    
    public void registerPlugins(final List<Class<? extends Plugin>> list) {
        this.bridgeBuilder.addPlugins((List)list);
    }
}
