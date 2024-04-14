package com.getcapacitor;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import org.json.JSONException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.json.JSONArray;
import android.content.res.AssetManager;

public class PluginManager
{
    private final AssetManager assetManager;
    
    public PluginManager(final AssetManager assetManager) {
        this.assetManager = assetManager;
    }
    
    private JSONArray parsePluginsJSON() throws PluginLoadException {
        try {
            final BufferedReader bufferedReader = new BufferedReader((Reader)new InputStreamReader(this.assetManager.open("capacitor.plugins.json")));
            try {
                final StringBuilder sb = new StringBuilder();
                while (true) {
                    final String line = bufferedReader.readLine();
                    if (line == null) {
                        break;
                    }
                    sb.append(line);
                }
                final JSONArray jsonArray = new JSONArray(sb.toString());
                bufferedReader.close();
                return jsonArray;
            }
            finally {
                try {
                    bufferedReader.close();
                }
                finally {
                    final Throwable t;
                    final Throwable t2;
                    t.addSuppressed(t2);
                }
            }
        }
        catch (final JSONException ex) {
            throw new PluginLoadException("Could not parse capacitor.plugins.json as JSON");
        }
        catch (final IOException ex2) {
            throw new PluginLoadException("Could not load capacitor.plugins.json");
        }
    }
    
    public List<Class<? extends Plugin>> loadPluginClasses() throws PluginLoadException {
        final JSONArray pluginsJSON = this.parsePluginsJSON();
        final ArrayList list = new ArrayList();
        try {
            for (int length = pluginsJSON.length(), i = 0; i < length; ++i) {
                list.add((Object)Class.forName(pluginsJSON.getJSONObject(i).getString("classpath")).asSubclass(Plugin.class));
            }
            return (List<Class<? extends Plugin>>)list;
        }
        catch (final ClassNotFoundException ex) {
            final StringBuilder sb = new StringBuilder("Could not find class by class path: ");
            sb.append(ex.getMessage());
            throw new PluginLoadException(sb.toString());
        }
        catch (final JSONException ex2) {
            throw new PluginLoadException("Could not parse capacitor.plugins.json as JSON");
        }
    }
}
