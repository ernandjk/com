package com.getcapacitor;

import java.util.List;
import java.io.IOException;
import android.content.Context;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collection;
import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;

public class JSExport
{
    private static String CALLBACK_PARAM = "_callback";
    private static String CATCHALL_OPTIONS_PARAM = "_options";
    
    private static JSONObject createPluginHeader(final PluginHandle pluginHandle) {
        final JSONObject jsonObject = new JSONObject();
        final Collection<PluginMethodHandle> methods = pluginHandle.getMethods();
        try {
            final String id = pluginHandle.getId();
            final JSONArray jsonArray = new JSONArray();
            jsonObject.put("name", (Object)id);
            final Iterator iterator = methods.iterator();
            while (iterator.hasNext()) {
                jsonArray.put((Object)createPluginMethodHeader((PluginMethodHandle)iterator.next()));
            }
            jsonObject.put("methods", (Object)jsonArray);
            return jsonObject;
        }
        catch (final JSONException ex) {
            return jsonObject;
        }
    }
    
    private static JSONObject createPluginMethodHeader(final PluginMethodHandle pluginMethodHandle) {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", (Object)pluginMethodHandle.getName());
            if (!pluginMethodHandle.getReturnType().equals((Object)"none")) {
                jsonObject.put("rtype", (Object)pluginMethodHandle.getReturnType());
            }
            return jsonObject;
        }
        catch (final JSONException ex) {
            return jsonObject;
        }
    }
    
    private static String generateMethodJS(final PluginHandle pluginHandle, final PluginMethodHandle pluginMethodHandle) {
        final ArrayList list = new ArrayList();
        final ArrayList list2 = new ArrayList();
        ((List)list2).add((Object)JSExport.CATCHALL_OPTIONS_PARAM);
        final String returnType = pluginMethodHandle.getReturnType();
        if (returnType.equals((Object)"callback")) {
            ((List)list2).add((Object)JSExport.CALLBACK_PARAM);
        }
        final StringBuilder sb = new StringBuilder("t['");
        sb.append(pluginMethodHandle.getName());
        sb.append("'] = function(");
        sb.append(TextUtils.join((CharSequence)", ", (Iterable)list2));
        sb.append(") {");
        ((List)list).add((Object)sb.toString());
        returnType.hashCode();
        final int hashCode = returnType.hashCode();
        int n = -1;
        switch (hashCode) {
            case 3387192: {
                if (!returnType.equals((Object)"none")) {
                    break;
                }
                n = 2;
                break;
            }
            case -172220347: {
                if (!returnType.equals((Object)"callback")) {
                    break;
                }
                n = 1;
                break;
            }
            case -309216997: {
                if (!returnType.equals((Object)"promise")) {
                    break;
                }
                n = 0;
                break;
            }
        }
        switch (n) {
            case 2: {
                final StringBuilder sb2 = new StringBuilder("return w.Capacitor.nativeCallback('");
                sb2.append(pluginHandle.getId());
                sb2.append("', '");
                sb2.append(pluginMethodHandle.getName());
                sb2.append("', ");
                sb2.append(JSExport.CATCHALL_OPTIONS_PARAM);
                sb2.append(")");
                ((List)list).add((Object)sb2.toString());
                break;
            }
            case 1: {
                final StringBuilder sb3 = new StringBuilder("return w.Capacitor.nativeCallback('");
                sb3.append(pluginHandle.getId());
                sb3.append("', '");
                sb3.append(pluginMethodHandle.getName());
                sb3.append("', ");
                sb3.append(JSExport.CATCHALL_OPTIONS_PARAM);
                sb3.append(", ");
                sb3.append(JSExport.CALLBACK_PARAM);
                sb3.append(")");
                ((List)list).add((Object)sb3.toString());
                break;
            }
            case 0: {
                final StringBuilder sb4 = new StringBuilder("return w.Capacitor.nativePromise('");
                sb4.append(pluginHandle.getId());
                sb4.append("', '");
                sb4.append(pluginMethodHandle.getName());
                sb4.append("', ");
                sb4.append(JSExport.CATCHALL_OPTIONS_PARAM);
                sb4.append(")");
                ((List)list).add((Object)sb4.toString());
                break;
            }
        }
        ((List)list).add((Object)"}");
        return TextUtils.join((CharSequence)"\n", (Iterable)list);
    }
    
    public static String getBridgeJS(final Context context) throws JSExportException {
        return getFilesContent(context, "native-bridge.js");
    }
    
    public static String getCordovaJS(final Context context) {
        String fileFromAssets;
        try {
            fileFromAssets = FileUtils.readFileFromAssets(context.getAssets(), "public/cordova.js");
        }
        catch (final IOException ex) {
            Logger.error("Unable to read public/cordova.js file, Cordova plugins will not work");
            fileFromAssets = "";
        }
        return fileFromAssets;
    }
    
    public static String getCordovaPluginJS(final Context context) {
        return getFilesContent(context, "public/plugins");
    }
    
    public static String getCordovaPluginsFileJS(final Context context) {
        String fileFromAssets;
        try {
            fileFromAssets = FileUtils.readFileFromAssets(context.getAssets(), "public/cordova_plugins.js");
        }
        catch (final IOException ex) {
            Logger.error("Unable to read public/cordova_plugins.js file, Cordova plugins will not work");
            fileFromAssets = "";
        }
        return fileFromAssets;
    }
    
    public static String getFilesContent(final Context context, final String s) {
        final StringBuilder sb = new StringBuilder();
        try {
            final String[] list = context.getAssets().list(s);
            if (list.length <= 0) {
                return FileUtils.readFileFromAssets(context.getAssets(), s);
            }
            for (final String s2 : list) {
                if (!s2.endsWith(".map")) {
                    final StringBuilder sb2 = new StringBuilder();
                    sb2.append(s);
                    sb2.append("/");
                    sb2.append(s2);
                    sb.append(getFilesContent(context, sb2.toString()));
                }
            }
        }
        catch (final IOException ex) {
            final StringBuilder sb3 = new StringBuilder("Unable to read file at path ");
            sb3.append(s);
            Logger.warn(sb3.toString());
        }
        return sb.toString();
    }
    
    public static String getGlobalJS(final Context context, final boolean b, final boolean b2) {
        final StringBuilder sb = new StringBuilder("window.Capacitor = { DEBUG: ");
        sb.append(b2);
        sb.append(", isLoggingEnabled: ");
        sb.append(b);
        sb.append(", Plugins: {} };");
        return sb.toString();
    }
    
    public static String getPluginJS(final Collection<PluginHandle> collection) {
        final ArrayList list = new ArrayList();
        final JSONArray jsonArray = new JSONArray();
        ((List)list).add((Object)"// Begin: Capacitor Plugin JS");
        for (final PluginHandle pluginHandle : collection) {
            final StringBuilder sb = new StringBuilder("(function(w) {\nvar a = (w.Capacitor = w.Capacitor || {});\nvar p = (a.Plugins = a.Plugins || {});\nvar t = (p['");
            sb.append(pluginHandle.getId());
            sb.append("'] = {});\nt.addListener = function(eventName, callback) {\n  return w.Capacitor.addListener('");
            sb.append(pluginHandle.getId());
            sb.append("', eventName, callback);\n}");
            ((List)list).add((Object)sb.toString());
            for (final PluginMethodHandle pluginMethodHandle : pluginHandle.getMethods()) {
                if (!pluginMethodHandle.getName().equals((Object)"addListener")) {
                    if (pluginMethodHandle.getName().equals((Object)"removeListener")) {
                        continue;
                    }
                    ((List)list).add((Object)generateMethodJS(pluginHandle, pluginMethodHandle));
                }
            }
            ((List)list).add((Object)"})(window);\n");
            jsonArray.put((Object)createPluginHeader(pluginHandle));
        }
        final StringBuilder sb2 = new StringBuilder();
        sb2.append(TextUtils.join((CharSequence)"\n", (Iterable)list));
        sb2.append("\nwindow.Capacitor.PluginHeaders = ");
        sb2.append(jsonArray.toString());
        sb2.append(";");
        return sb2.toString();
    }
}
