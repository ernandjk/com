package com.getcapacitor;

import com.getcapacitor.android.R$styleable;
import android.util.AttributeSet;
import android.content.Context;
import com.getcapacitor.android.R$layout;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.List;
import androidx.fragment.app.Fragment;

public class BridgeFragment extends Fragment
{
    private static final String ARG_START_DIR = "startDir";
    protected Bridge bridge;
    private CapConfig config;
    private final List<Class<? extends Plugin>> initialPlugins;
    protected boolean keepRunning;
    private final List<WebViewListener> webViewListeners;
    
    public BridgeFragment() {
        this.keepRunning = true;
        this.initialPlugins = (List<Class<? extends Plugin>>)new ArrayList();
        this.config = null;
        this.webViewListeners = (List<WebViewListener>)new ArrayList();
    }
    
    public static BridgeFragment newInstance(final String s) {
        final BridgeFragment bridgeFragment = new BridgeFragment();
        final Bundle arguments = new Bundle();
        arguments.putString("startDir", s);
        bridgeFragment.setArguments(arguments);
        return bridgeFragment;
    }
    
    public void addPlugin(final Class<? extends Plugin> clazz) {
        this.initialPlugins.add((Object)clazz);
    }
    
    public void addWebViewListener(final WebViewListener webViewListener) {
        this.webViewListeners.add((Object)webViewListener);
    }
    
    public Bridge getBridge() {
        return this.bridge;
    }
    
    protected void load(final Bundle instanceState) {
        Logger.debug("Loading Bridge with BridgeFragment");
        String string;
        if (this.getArguments() != null) {
            string = this.getArguments().getString("startDir");
        }
        else {
            string = null;
        }
        final Bridge create = new Bridge$Builder((Fragment)this).setInstanceState(instanceState).setPlugins((List)this.initialPlugins).setConfig(this.config).addWebViewListeners((List)this.webViewListeners).create();
        this.bridge = create;
        if (string != null) {
            create.setServerAssetPath(string);
        }
        this.keepRunning = this.bridge.shouldKeepRunning();
    }
    
    public void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
    }
    
    public View onCreateView(final LayoutInflater layoutInflater, final ViewGroup viewGroup, final Bundle bundle) {
        return layoutInflater.inflate(R$layout.fragment_bridge, viewGroup, false);
    }
    
    public void onDestroy() {
        super.onDestroy();
        final Bridge bridge = this.bridge;
        if (bridge != null) {
            bridge.onDestroy();
        }
    }
    
    public void onInflate(final Context context, final AttributeSet set, final Bundle bundle) {
        super.onInflate(context, set, bundle);
        final String string = context.obtainStyledAttributes(set, R$styleable.bridge_fragment).getString(R$styleable.bridge_fragment_start_dir);
        if (string != null) {
            final String string2 = ((CharSequence)string).toString();
            final Bundle arguments = new Bundle();
            arguments.putString("startDir", string2);
            this.setArguments(arguments);
        }
    }
    
    public void onViewCreated(final View view, final Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.load(bundle);
    }
    
    public void setConfig(final CapConfig config) {
        this.config = config;
    }
}
