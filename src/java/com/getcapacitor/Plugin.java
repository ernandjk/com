package com.getcapacitor;

import java.util.Set;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import java.util.concurrent.CopyOnWriteArrayList;
import androidx.activity.result.contract.ActivityResultContracts$RequestMultiplePermissions;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts$StartActivityForResult;
import com.getcapacitor.annotation.ActivityCallback;
import androidx.core.app.ActivityCompat;
import com.getcapacitor.util.PermissionHelper;
import android.content.res.Configuration;
import android.content.Context;
import org.json.JSONException;
import androidx.appcompat.app.AppCompatActivity;
import com.getcapacitor.annotation.PermissionCallback;
import java.util.Map$Entry;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.CapacitorPlugin;
import java.util.Collection;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.ArrayList;
import java.util.HashMap;
import androidx.activity.result.ActivityResult;
import java.lang.reflect.Method;
import java.util.List;
import android.content.Intent;
import androidx.activity.result.ActivityResultLauncher;
import java.util.Map;

public class Plugin
{
    private static final String BUNDLE_PERSISTED_OPTIONS_JSON_KEY = "_json";
    private final Map<String, ActivityResultLauncher<Intent>> activityLaunchers;
    protected Bridge bridge;
    private final Map<String, List<PluginCall>> eventListeners;
    protected PluginHandle handle;
    private String lastPluginCallId;
    private final Map<String, ActivityResultLauncher<String[]>> permissionLaunchers;
    private final Map<String, List<JSObject>> retainedEventArguments;
    @Deprecated
    protected PluginCall savedLastCall;
    
    public Plugin() {
        this.activityLaunchers = (Map<String, ActivityResultLauncher<Intent>>)new HashMap();
        this.permissionLaunchers = (Map<String, ActivityResultLauncher<String[]>>)new HashMap();
        this.eventListeners = (Map<String, List<PluginCall>>)new HashMap();
        this.retainedEventArguments = (Map<String, List<JSObject>>)new HashMap();
    }
    
    private void addEventListener(final String s, final PluginCall pluginCall) {
        final List list = (List)this.eventListeners.get((Object)s);
        if (list != null && !list.isEmpty()) {
            list.add((Object)pluginCall);
        }
        else {
            final ArrayList list2 = new ArrayList();
            this.eventListeners.put((Object)s, (Object)list2);
            ((List)list2).add((Object)pluginCall);
            this.sendRetainedArgumentsForEvent(s);
        }
    }
    
    private ActivityResultLauncher<Intent> getActivityLauncherOrReject(final PluginCall pluginCall, String format) {
        final ActivityResultLauncher activityResultLauncher = (ActivityResultLauncher)this.activityLaunchers.get((Object)format);
        if (activityResultLauncher == null) {
            format = String.format(Locale.US, "There is no ActivityCallback method registered for the name: %s. Please define a callback method annotated with @ActivityCallback that receives arguments: (PluginCall, ActivityResult)", new Object[] { format });
            Logger.error(format);
            pluginCall.reject(format);
            return null;
        }
        return (ActivityResultLauncher<Intent>)activityResultLauncher;
    }
    
    private ActivityResultLauncher<String[]> getPermissionLauncherOrReject(final PluginCall pluginCall, String format) {
        final ActivityResultLauncher activityResultLauncher = (ActivityResultLauncher)this.permissionLaunchers.get((Object)format);
        if (activityResultLauncher == null) {
            format = String.format(Locale.US, "There is no PermissionCallback method registered for the name: %s. Please define a callback method annotated with @PermissionCallback that receives arguments: (PluginCall)", new Object[] { format });
            Logger.error(format);
            pluginCall.reject(format);
            return null;
        }
        return (ActivityResultLauncher<String[]>)activityResultLauncher;
    }
    
    private String[] getPermissionStringsForAliases(final String[] array) {
        final CapacitorPlugin pluginAnnotation = this.handle.getPluginAnnotation();
        final HashSet set = new HashSet();
        for (final Permission permission : pluginAnnotation.permissions()) {
            if (Arrays.asList((Object[])array).contains((Object)permission.alias())) {
                set.addAll((Collection)Arrays.asList((Object[])permission.strings()));
            }
        }
        return (String[])set.toArray((Object[])new String[0]);
    }
    
    private void handleLegacyPermission(final PluginCall pluginCall) {
        final NativePlugin legacyPluginAnnotation = this.handle.getLegacyPluginAnnotation();
        final String[] permissions = legacyPluginAnnotation.permissions();
        if (permissions.length > 0) {
            this.saveCall(pluginCall);
            this.pluginRequestPermissions(permissions, legacyPluginAnnotation.permissionRequestCode());
        }
        else {
            pluginCall.resolve();
        }
    }
    
    private void permissionActivityResult(final PluginCall pluginCall, final String[] array, final String s) {
        final ActivityResultLauncher<String[]> permissionLauncherOrReject = this.getPermissionLauncherOrReject(pluginCall, s);
        if (permissionLauncherOrReject == null) {
            return;
        }
        this.bridge.savePermissionCall(pluginCall);
        permissionLauncherOrReject.launch((Object)array);
    }
    
    private void removeEventListener(final String s, final PluginCall pluginCall) {
        final List list = (List)this.eventListeners.get((Object)s);
        if (list == null) {
            return;
        }
        list.remove((Object)pluginCall);
    }
    
    private void sendRetainedArgumentsForEvent(final String s) {
        final List list = (List)this.retainedEventArguments.get((Object)s);
        if (list == null) {
            return;
        }
        this.retainedEventArguments.remove((Object)s);
        final Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            this.notifyListeners(s, (JSObject)iterator.next());
        }
    }
    
    private void triggerActivityCallback(final Method ex, final ActivityResult activityResult) {
        PluginCall pluginCall;
        if ((pluginCall = this.bridge.getSavedCall(this.lastPluginCallId)) == null) {
            pluginCall = this.bridge.getPluginCallForLastActivity();
        }
        try {
            ((Method)ex).setAccessible(true);
            ((Method)ex).invoke((Object)this, new Object[] { pluginCall, activityResult });
            return;
        }
        catch (final InvocationTargetException ex) {}
        catch (final IllegalAccessException ex2) {}
        ((ReflectiveOperationException)ex).printStackTrace();
    }
    
    private void triggerPermissionCallback(final Method ex, final Map<String, Boolean> map) {
        final PluginCall permissionCall = this.bridge.getPermissionCall(this.handle.getId());
        if (this.bridge.validatePermissions(this, permissionCall, map)) {
            try {
                ((Method)ex).setAccessible(true);
                ((Method)ex).invoke((Object)this, new Object[] { permissionCall });
                return;
            }
            catch (final InvocationTargetException ex) {}
            catch (final IllegalAccessException ex2) {}
            ((ReflectiveOperationException)ex).printStackTrace();
        }
    }
    
    @PluginMethod(returnType = "none")
    public void addListener(final PluginCall pluginCall) {
        final String string = pluginCall.getString("eventName");
        pluginCall.setKeepAlive(true);
        this.addEventListener(string, pluginCall);
    }
    
    @PluginMethod
    @PermissionCallback
    public void checkPermissions(final PluginCall pluginCall) {
        final Map<String, PermissionState> permissionStates = this.getPermissionStates();
        if (permissionStates.size() == 0) {
            pluginCall.resolve();
        }
        else {
            final JSObject jsObject = new JSObject();
            for (final Map$Entry map$Entry : permissionStates.entrySet()) {
                jsObject.put((String)map$Entry.getKey(), map$Entry.getValue());
            }
            pluginCall.resolve(jsObject);
        }
    }
    
    public void execute(final Runnable runnable) {
        this.bridge.execute(runnable);
    }
    
    @Deprecated
    public void freeSavedCall() {
        this.savedLastCall.release(this.bridge);
        this.savedLastCall = null;
    }
    
    public AppCompatActivity getActivity() {
        return this.bridge.getActivity();
    }
    
    public String getAppId() {
        return this.getContext().getPackageName();
    }
    
    public Bridge getBridge() {
        return this.bridge;
    }
    
    public PluginConfig getConfig() {
        return this.bridge.getConfig().getPluginConfiguration(this.handle.getId());
    }
    
    @Deprecated
    public Object getConfigValue(final String s) {
        try {
            return this.getConfig().getConfigJSON().get(s);
        }
        catch (final JSONException ex) {
            return null;
        }
    }
    
    public Context getContext() {
        return this.bridge.getContext();
    }
    
    protected String getLogTag() {
        return Logger.tags(this.getClass().getSimpleName());
    }
    
    protected String getLogTag(final String... array) {
        return Logger.tags(array);
    }
    
    public PermissionState getPermissionState(final String s) {
        return (PermissionState)this.getPermissionStates().get((Object)s);
    }
    
    public Map<String, PermissionState> getPermissionStates() {
        return this.bridge.getPermissionStates(this);
    }
    
    public PluginHandle getPluginHandle() {
        return this.handle;
    }
    
    @Deprecated
    public PluginCall getSavedCall() {
        return this.savedLastCall;
    }
    
    @Deprecated
    protected void handleOnActivityResult(final int n, final int n2, final Intent intent) {
    }
    
    protected void handleOnConfigurationChanged(final Configuration configuration) {
    }
    
    protected void handleOnDestroy() {
    }
    
    protected void handleOnNewIntent(final Intent intent) {
    }
    
    protected void handleOnPause() {
    }
    
    protected void handleOnRestart() {
    }
    
    protected void handleOnResume() {
    }
    
    protected void handleOnStart() {
    }
    
    protected void handleOnStop() {
    }
    
    @Deprecated
    protected void handleRequestPermissionsResult(int i, final String[] array, final int[] array2) {
        if (!this.hasDefinedPermissions(array)) {
            final StringBuilder sb = new StringBuilder("Missing the following permissions in AndroidManifest.xml:\n");
            final String[] undefinedPermissions = PermissionHelper.getUndefinedPermissions(this.getContext(), array);
            int length;
            String s;
            StringBuilder sb2;
            for (length = undefinedPermissions.length, i = 0; i < length; ++i) {
                s = undefinedPermissions[i];
                sb2 = new StringBuilder();
                sb2.append(s);
                sb2.append("\n");
                sb.append(sb2.toString());
            }
            this.savedLastCall.reject(sb.toString());
            this.savedLastCall = null;
        }
    }
    
    @Deprecated
    public boolean hasDefinedPermissions(final String[] array) {
        for (int length = array.length, i = 0; i < length; ++i) {
            if (!PermissionHelper.hasDefinedPermission(this.getContext(), array[i])) {
                return false;
            }
        }
        return true;
    }
    
    @Deprecated
    public boolean hasDefinedRequiredPermissions() {
        final CapacitorPlugin pluginAnnotation = this.handle.getPluginAnnotation();
        if (pluginAnnotation == null) {
            return this.hasDefinedPermissions(this.handle.getLegacyPluginAnnotation().permissions());
        }
        final Permission[] permissions = pluginAnnotation.permissions();
        for (int length = permissions.length, i = 0; i < length; ++i) {
            final String[] strings = permissions[i].strings();
            for (int length2 = strings.length, j = 0; j < length2; ++j) {
                if (!PermissionHelper.hasDefinedPermission(this.getContext(), strings[j])) {
                    return false;
                }
            }
        }
        return true;
    }
    
    protected boolean hasListeners(final String s) {
        final List list = (List)this.eventListeners.get((Object)s);
        return list != null && (list.isEmpty() ^ true);
    }
    
    @Deprecated
    public boolean hasPermission(final String s) {
        return ActivityCompat.checkSelfPermission(this.getContext(), s) == 0;
    }
    
    @Deprecated
    public boolean hasRequiredPermissions() {
        final CapacitorPlugin pluginAnnotation = this.handle.getPluginAnnotation();
        if (pluginAnnotation == null) {
            final String[] permissions = this.handle.getLegacyPluginAnnotation().permissions();
            for (int length = permissions.length, i = 0; i < length; ++i) {
                if (ActivityCompat.checkSelfPermission(this.getContext(), permissions[i]) != 0) {
                    return false;
                }
            }
            return true;
        }
        final Permission[] permissions2 = pluginAnnotation.permissions();
        for (int length2 = permissions2.length, j = 0; j < length2; ++j) {
            final String[] strings = permissions2[j].strings();
            for (int length3 = strings.length, k = 0; k < length3; ++k) {
                if (ActivityCompat.checkSelfPermission(this.getContext(), strings[k]) != 0) {
                    return false;
                }
            }
        }
        return true;
    }
    
    void initializeActivityLaunchers() {
        final ArrayList list = new ArrayList();
        for (Class<? extends Plugin> clazz = this.getClass(); !clazz.getName().equals((Object)Object.class.getName()); clazz = (Class<? extends Plugin>)clazz.getSuperclass()) {
            ((List)list).addAll((Collection)Arrays.asList((Object[])clazz.getDeclaredMethods()));
        }
        for (final Method method : list) {
            if (method.isAnnotationPresent((Class)ActivityCallback.class)) {
                this.activityLaunchers.put((Object)method.getName(), (Object)this.bridge.registerForActivityResult((androidx.activity.result.contract.ActivityResultContract<Object, Object>)new ActivityResultContracts$StartActivityForResult(), (androidx.activity.result.ActivityResultCallback<Object>)new Plugin$$ExternalSyntheticLambda0(this, method)));
            }
            else {
                if (!method.isAnnotationPresent((Class)PermissionCallback.class)) {
                    continue;
                }
                this.permissionLaunchers.put((Object)method.getName(), (Object)this.bridge.registerForActivityResult((androidx.activity.result.contract.ActivityResultContract<Object, Object>)new ActivityResultContracts$RequestMultiplePermissions(), (androidx.activity.result.ActivityResultCallback<Object>)new Plugin$$ExternalSyntheticLambda1(this, method)));
            }
        }
    }
    
    public boolean isPermissionDeclared(final String s) {
        final CapacitorPlugin pluginAnnotation = this.handle.getPluginAnnotation();
        if (pluginAnnotation != null) {
            for (final Permission permission : pluginAnnotation.permissions()) {
                if (s.equalsIgnoreCase(permission.alias())) {
                    final String[] strings = permission.strings();
                    final int length2 = strings.length;
                    int j = 0;
                    boolean b = true;
                    while (j < length2) {
                        final String s2 = strings[j];
                        b = (b && PermissionHelper.hasDefinedPermission(this.getContext(), s2));
                        ++j;
                    }
                    return b;
                }
            }
        }
        Logger.error(String.format("isPermissionDeclared: No alias defined for %s or missing @CapacitorPlugin annotation.", new Object[] { s }));
        return false;
    }
    
    public void load() {
    }
    
    protected void notifyListeners(final String s, final JSObject jsObject) {
        this.notifyListeners(s, jsObject, false);
    }
    
    protected void notifyListeners(final String s, final JSObject jsObject, final boolean b) {
        final String logTag = this.getLogTag();
        final StringBuilder sb = new StringBuilder("Notifying listeners for event ");
        sb.append(s);
        Logger.verbose(logTag, sb.toString());
        final List list = (List)this.eventListeners.get((Object)s);
        if (list != null && !list.isEmpty()) {
            final Iterator iterator = new CopyOnWriteArrayList((Collection)list).iterator();
            while (iterator.hasNext()) {
                ((PluginCall)iterator.next()).resolve(jsObject);
            }
            return;
        }
        final String logTag2 = this.getLogTag();
        final StringBuilder sb2 = new StringBuilder("No listeners found for event ");
        sb2.append(s);
        Logger.debug(logTag2, sb2.toString());
        if (b) {
            Object o;
            if ((o = this.retainedEventArguments.get((Object)s)) == null) {
                o = new ArrayList();
            }
            ((List)o).add((Object)jsObject);
            this.retainedEventArguments.put((Object)s, o);
        }
    }
    
    @Deprecated
    public void pluginRequestAllPermissions() {
        final NativePlugin legacyPluginAnnotation = this.handle.getLegacyPluginAnnotation();
        ActivityCompat.requestPermissions((Activity)this.getActivity(), legacyPluginAnnotation.permissions(), legacyPluginAnnotation.permissionRequestCode());
    }
    
    @Deprecated
    public void pluginRequestPermission(final String s, final int n) {
        ActivityCompat.requestPermissions((Activity)this.getActivity(), new String[] { s }, n);
    }
    
    @Deprecated
    public void pluginRequestPermissions(final String[] array, final int n) {
        ActivityCompat.requestPermissions((Activity)this.getActivity(), array, n);
    }
    
    @PluginMethod(returnType = "promise")
    public void removeAllListeners(final PluginCall pluginCall) {
        this.eventListeners.clear();
        pluginCall.resolve();
    }
    
    @PluginMethod(returnType = "none")
    public void removeListener(PluginCall savedCall) {
        final String string = savedCall.getString("eventName");
        savedCall = this.bridge.getSavedCall(savedCall.getString("callbackId"));
        if (savedCall != null) {
            this.removeEventListener(string, savedCall);
            this.bridge.releaseCall(savedCall);
        }
    }
    
    protected void requestAllPermissions(final PluginCall pluginCall, final String s) {
        final CapacitorPlugin pluginAnnotation = this.handle.getPluginAnnotation();
        if (pluginAnnotation != null) {
            final HashSet set = new HashSet();
            final Permission[] permissions = pluginAnnotation.permissions();
            for (int length = permissions.length, i = 0; i < length; ++i) {
                set.addAll((Collection)Arrays.asList((Object[])permissions[i].strings()));
            }
            this.permissionActivityResult(pluginCall, (String[])set.toArray((Object[])new String[0]), s);
        }
    }
    
    protected void requestPermissionForAlias(final String s, final PluginCall pluginCall, final String s2) {
        this.requestPermissionForAliases(new String[] { s }, pluginCall, s2);
    }
    
    protected void requestPermissionForAliases(String[] permissionStringsForAliases, final PluginCall pluginCall, final String s) {
        if (permissionStringsForAliases.length == 0) {
            Logger.error("No permission alias was provided");
            return;
        }
        permissionStringsForAliases = this.getPermissionStringsForAliases(permissionStringsForAliases);
        if (permissionStringsForAliases.length > 0) {
            this.permissionActivityResult(pluginCall, permissionStringsForAliases, s);
        }
    }
    
    @PluginMethod
    public void requestPermissions(final PluginCall pluginCall) {
        final CapacitorPlugin pluginAnnotation = this.handle.getPluginAnnotation();
        if (pluginAnnotation == null) {
            this.handleLegacyPermission(pluginCall);
        }
        else {
            final HashSet set = new HashSet();
            final JSArray array = pluginCall.getArray("permissions");
            final String[] array2 = null;
            List list = null;
            Label_0063: {
                if (array != null) {
                    try {
                        list = array.toList();
                        break Label_0063;
                    }
                    catch (final JSONException ex) {}
                }
                list = null;
            }
            final HashSet set2 = new HashSet();
            String[] array3;
            if (list != null && !list.isEmpty()) {
                for (final Permission permission : pluginAnnotation.permissions()) {
                    if (list.contains((Object)permission.alias())) {
                        ((Set)set2).add((Object)permission.alias());
                    }
                }
                if (((Set)set2).isEmpty()) {
                    pluginCall.reject("No valid permission alias was requested of this plugin.");
                    array3 = array2;
                }
                else {
                    array3 = (String[])((Set)set2).toArray((Object[])new String[0]);
                }
            }
            else {
                for (final Permission permission2 : pluginAnnotation.permissions()) {
                    if (permission2.strings().length != 0 && (permission2.strings().length != 1 || !permission2.strings()[0].isEmpty())) {
                        ((Set)set2).add((Object)permission2.alias());
                    }
                    else if (!permission2.alias().isEmpty()) {
                        ((Set)set).add((Object)permission2.alias());
                    }
                }
                array3 = (String[])((Set)set2).toArray((Object[])new String[0]);
            }
            if (array3 != null && array3.length > 0) {
                this.requestPermissionForAliases(array3, pluginCall, "checkPermissions");
            }
            else if (!((Set)set).isEmpty()) {
                final JSObject jsObject = new JSObject();
                final Iterator iterator = ((Set)set).iterator();
                while (iterator.hasNext()) {
                    jsObject.put((String)iterator.next(), PermissionState.GRANTED.toString());
                }
                pluginCall.resolve(jsObject);
            }
            else {
                pluginCall.resolve();
            }
        }
    }
    
    protected void restoreState(final Bundle bundle) {
    }
    
    @Deprecated
    public void saveCall(final PluginCall savedLastCall) {
        this.savedLastCall = savedLastCall;
    }
    
    protected Bundle saveInstanceState() {
        final PluginCall savedCall = this.bridge.getSavedCall(this.lastPluginCallId);
        if (savedCall == null) {
            return null;
        }
        final Bundle bundle = new Bundle();
        final JSObject data = savedCall.getData();
        if (data != null) {
            bundle.putString("_json", data.toString());
        }
        return bundle;
    }
    
    public void setBridge(final Bridge bridge) {
        this.bridge = bridge;
    }
    
    public void setPluginHandle(final PluginHandle handle) {
        this.handle = handle;
    }
    
    public Boolean shouldOverrideLoad(final Uri uri) {
        return null;
    }
    
    @Deprecated
    protected void startActivityForResult(final PluginCall pluginCall, final Intent intent, final int n) {
        this.bridge.startActivityForPluginWithResult(pluginCall, intent, n);
    }
    
    public void startActivityForResult(final PluginCall pluginCallForLastActivity, final Intent intent, final String s) {
        final ActivityResultLauncher<Intent> activityLauncherOrReject = this.getActivityLauncherOrReject(pluginCallForLastActivity, s);
        if (activityLauncherOrReject == null) {
            return;
        }
        this.bridge.setPluginCallForLastActivity(pluginCallForLastActivity);
        this.lastPluginCallId = pluginCallForLastActivity.getCallbackId();
        this.bridge.saveCall(pluginCallForLastActivity);
        activityLauncherOrReject.launch((Object)intent);
    }
}
