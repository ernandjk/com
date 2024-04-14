package com.getcapacitor;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import com.getcapacitor.annotation.CapacitorPlugin;

public class PluginHandle
{
    private final Bridge bridge;
    private Plugin instance;
    private NativePlugin legacyPluginAnnotation;
    private CapacitorPlugin pluginAnnotation;
    private final Class<? extends Plugin> pluginClass;
    private final String pluginId;
    private final Map<String, PluginMethodHandle> pluginMethods;
    
    public PluginHandle(final Bridge bridge, final Plugin plugin) throws InvalidPluginException {
        this(plugin.getClass(), bridge);
        this.loadInstance(plugin);
    }
    
    public PluginHandle(final Bridge bridge, final Class<? extends Plugin> clazz) throws InvalidPluginException, PluginLoadException {
        this(clazz, bridge);
        this.load();
    }
    
    private PluginHandle(final Class<? extends Plugin> pluginClass, final Bridge bridge) throws InvalidPluginException {
        this.pluginMethods = (Map<String, PluginMethodHandle>)new HashMap();
        this.bridge = bridge;
        this.pluginClass = pluginClass;
        final CapacitorPlugin pluginAnnotation = pluginClass.getAnnotation(CapacitorPlugin.class);
        if (pluginAnnotation == null) {
            final NativePlugin legacyPluginAnnotation = pluginClass.getAnnotation(NativePlugin.class);
            if (legacyPluginAnnotation == null) {
                final StringBuilder sb = new StringBuilder("No @CapacitorPlugin annotation found for plugin ");
                sb.append(pluginClass.getName());
                throw new InvalidPluginException(sb.toString());
            }
            if (!legacyPluginAnnotation.name().equals((Object)"")) {
                this.pluginId = legacyPluginAnnotation.name();
            }
            else {
                this.pluginId = pluginClass.getSimpleName();
            }
            this.legacyPluginAnnotation = legacyPluginAnnotation;
        }
        else {
            if (!pluginAnnotation.name().equals((Object)"")) {
                this.pluginId = pluginAnnotation.name();
            }
            else {
                this.pluginId = pluginClass.getSimpleName();
            }
            this.pluginAnnotation = pluginAnnotation;
        }
        this.indexMethods(pluginClass);
    }
    
    private void indexMethods(final Class<? extends Plugin> clazz) {
        for (final Method method : this.pluginClass.getMethods()) {
            final PluginMethod pluginMethod = (PluginMethod)method.getAnnotation((Class)PluginMethod.class);
            if (pluginMethod != null) {
                this.pluginMethods.put((Object)method.getName(), (Object)new PluginMethodHandle(method, pluginMethod));
            }
        }
    }
    
    public String getId() {
        return this.pluginId;
    }
    
    public Plugin getInstance() {
        return this.instance;
    }
    
    public NativePlugin getLegacyPluginAnnotation() {
        return this.legacyPluginAnnotation;
    }
    
    public Collection<PluginMethodHandle> getMethods() {
        return (Collection<PluginMethodHandle>)this.pluginMethods.values();
    }
    
    public CapacitorPlugin getPluginAnnotation() {
        return this.pluginAnnotation;
    }
    
    public Class<? extends Plugin> getPluginClass() {
        return this.pluginClass;
    }
    
    public void invoke(final String s, final PluginCall pluginCall) throws PluginLoadException, InvalidPluginMethodException, InvocationTargetException, IllegalAccessException {
        if (this.instance == null) {
            this.load();
        }
        final PluginMethodHandle pluginMethodHandle = (PluginMethodHandle)this.pluginMethods.get((Object)s);
        if (pluginMethodHandle != null) {
            pluginMethodHandle.getMethod().invoke((Object)this.instance, new Object[] { pluginCall });
            return;
        }
        final StringBuilder sb = new StringBuilder("No method ");
        sb.append(s);
        sb.append(" found for plugin ");
        sb.append(this.pluginClass.getName());
        throw new InvalidPluginMethodException(sb.toString());
    }
    
    public Plugin load() throws PluginLoadException {
        final Plugin instance = this.instance;
        if (instance != null) {
            return instance;
        }
        try {
            final Plugin instance2 = (Plugin)this.pluginClass.newInstance();
            this.instance = instance2;
            return this.loadInstance(instance2);
        }
        catch (final InstantiationException | IllegalAccessException ex) {
            throw new PluginLoadException("Unable to load plugin instance. Ensure plugin is publicly accessible");
        }
    }
    
    public Plugin loadInstance(final Plugin instance) {
        (this.instance = instance).setPluginHandle(this);
        this.instance.setBridge(this.bridge);
        this.instance.load();
        this.instance.initializeActivityLaunchers();
        return this.instance;
    }
}
