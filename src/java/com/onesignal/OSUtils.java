package com.onesignal;

import java.util.UUID;
import android.net.NetworkInfo;
import android.net.ConnectivityManager;
import android.telephony.TelephonyManager;
import android.os.Handler;
import android.content.Intent;
import java.util.HashSet;
import org.json.JSONArray;
import java.util.Map;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.regex.Pattern;
import android.text.TextUtils;
import android.os.Looper;
import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.location.LocationCallback;
import com.huawei.hms.api.HuaweiApiAvailability;
import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.google.android.gms.location.LocationListener;
import com.google.firebase.messaging.FirebaseMessaging;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.content.res.Resources;
import java.util.Random;
import android.content.pm.PackageManager$NameNotFoundException;
import android.os.Bundle;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;
import android.os.Build$VERSION;
import androidx.core.app.NotificationManagerCompat;
import android.content.Context;

class OSUtils
{
    private static final int HMS_AVAILABLE_SUCCESSFUL = 0;
    private static final String HMS_CORE_SERVICES_PACKAGE = "com.huawei.hwid";
    public static int MAX_NETWORK_REQUEST_ATTEMPT_COUNT = 3;
    static final int[] NO_RETRY_NETWROK_REQUEST_STATUS_CODES;
    public static final int UNINITIALIZABLE_STATUS = -999;
    
    static {
        NO_RETRY_NETWROK_REQUEST_STATUS_CODES = new int[] { 401, 402, 403, 404, 410 };
    }
    
    static boolean areNotificationsEnabled(final Context context) {
        try {
            return NotificationManagerCompat.from(OneSignal.appContext).areNotificationsEnabled();
        }
        finally {
            return true;
        }
    }
    
    private Integer checkAndroidSupportLibrary(final Context context) {
        final boolean hasWakefulBroadcastReceiver = hasWakefulBroadcastReceiver();
        final boolean hasNotificationManagerCompat = hasNotificationManagerCompat();
        if (!hasWakefulBroadcastReceiver && !hasNotificationManagerCompat) {
            OneSignal.Log(OneSignal.LOG_LEVEL.FATAL, "Could not find the Android Support Library. Please make sure it has been correctly added to your project.");
            return -3;
        }
        if (!hasWakefulBroadcastReceiver || !hasNotificationManagerCompat) {
            OneSignal.Log(OneSignal.LOG_LEVEL.FATAL, "The included Android Support Library is to old or incomplete. Please update to the 26.0.0 revision or newer.");
            return -5;
        }
        if (Build$VERSION.SDK_INT >= 26 && getTargetSdkVersion(context) >= 26 && !hasJobIntentService()) {
            OneSignal.Log(OneSignal.LOG_LEVEL.FATAL, "The included Android Support Library is to old or incomplete. Please update to the 26.0.0 revision or newer.");
            return -5;
        }
        return null;
    }
    
    static Collection<String> extractStringsFromCollection(final Collection<Object> collection) {
        final ArrayList list = new ArrayList();
        if (collection == null) {
            return (Collection<String>)list;
        }
        for (final Object next : collection) {
            if (next instanceof String) {
                ((Collection)list).add((Object)next);
            }
        }
        return (Collection<String>)list;
    }
    
    static String getManifestMeta(final Context context, final String s) {
        final Bundle manifestMetaBundle = getManifestMetaBundle(context);
        if (manifestMetaBundle != null) {
            return manifestMetaBundle.getString(s);
        }
        return null;
    }
    
    static boolean getManifestMetaBoolean(final Context context, final String s) {
        final Bundle manifestMetaBundle = getManifestMetaBundle(context);
        return manifestMetaBundle != null && manifestMetaBundle.getBoolean(s);
    }
    
    static Bundle getManifestMetaBundle(final Context context) {
        try {
            return context.getPackageManager().getApplicationInfo(context.getPackageName(), 128).metaData;
        }
        catch (final PackageManager$NameNotFoundException ex) {
            OneSignal.Log(OneSignal.LOG_LEVEL.ERROR, "Manifest application info not found", (Throwable)ex);
            return null;
        }
    }
    
    static int getRandomDelay(final int n, final int n2) {
        return new Random().nextInt(n2 + 1 - n) + n;
    }
    
    static String getResourceString(final Context context, final String s, final String s2) {
        final Resources resources = context.getResources();
        final int identifier = resources.getIdentifier(s, "string", context.getPackageName());
        if (identifier != 0) {
            return resources.getString(identifier);
        }
        return s2;
    }
    
    static String getRootCauseMessage(final Throwable t) {
        return getRootCauseThrowable(t).getMessage();
    }
    
    static Throwable getRootCauseThrowable(Throwable cause) {
        while (cause.getCause() != null && cause.getCause() != cause) {
            cause = cause.getCause();
        }
        return cause;
    }
    
    static Uri getSoundUri(final Context context, final String s) {
        final Resources resources = context.getResources();
        final String packageName = context.getPackageName();
        if (isValidResourceName(s)) {
            final int identifier = resources.getIdentifier(s, "raw", packageName);
            if (identifier != 0) {
                final StringBuilder sb = new StringBuilder("android.resource://");
                sb.append(packageName);
                sb.append("/");
                sb.append(identifier);
                return Uri.parse(sb.toString());
            }
        }
        final int identifier2 = resources.getIdentifier("onesignal_default_sound", "raw", packageName);
        if (identifier2 != 0) {
            final StringBuilder sb2 = new StringBuilder("android.resource://");
            sb2.append(packageName);
            sb2.append("/");
            sb2.append(identifier2);
            return Uri.parse(sb2.toString());
        }
        return null;
    }
    
    static int getTargetSdkVersion(final Context context) {
        final String packageName = context.getPackageName();
        final PackageManager packageManager = context.getPackageManager();
        try {
            return packageManager.getApplicationInfo(packageName, 0).targetSdkVersion;
        }
        catch (final PackageManager$NameNotFoundException ex) {
            ex.printStackTrace();
            return 15;
        }
    }
    
    static boolean hasAllHMSLibrariesForPushKit() {
        return hasHMSAGConnectLibrary() && hasHMSPushKitLibrary();
    }
    
    static boolean hasConfigChangeFlag(final Activity activity, final int n) {
        boolean b = false;
        try {
            if ((activity.getPackageManager().getActivityInfo(activity.getComponentName(), 0).configChanges & n) != 0x0) {
                b = true;
            }
        }
        catch (final PackageManager$NameNotFoundException ex) {
            ex.printStackTrace();
        }
        return b;
    }
    
    static boolean hasFCMLibrary() {
        try {
            return opaqueHasClass(FirebaseMessaging.class);
        }
        catch (final NoClassDefFoundError noClassDefFoundError) {
            return false;
        }
    }
    
    static boolean hasGMSLocationLibrary() {
        try {
            return opaqueHasClass(LocationListener.class);
        }
        catch (final NoClassDefFoundError noClassDefFoundError) {
            return false;
        }
    }
    
    private static boolean hasHMSAGConnectLibrary() {
        try {
            return opaqueHasClass(AGConnectServicesConfig.class);
        }
        catch (final NoClassDefFoundError noClassDefFoundError) {
            return false;
        }
    }
    
    private static boolean hasHMSAvailabilityLibrary() {
        try {
            return opaqueHasClass(HuaweiApiAvailability.class);
        }
        catch (final NoClassDefFoundError noClassDefFoundError) {
            return false;
        }
    }
    
    static boolean hasHMSLocationLibrary() {
        try {
            return opaqueHasClass(LocationCallback.class);
        }
        catch (final NoClassDefFoundError noClassDefFoundError) {
            return false;
        }
    }
    
    private static boolean hasHMSPushKitLibrary() {
        try {
            return opaqueHasClass(HmsInstanceId.class);
        }
        catch (final NoClassDefFoundError noClassDefFoundError) {
            return false;
        }
    }
    
    private static boolean hasJobIntentService() {
        return true;
    }
    
    private static boolean hasNotificationManagerCompat() {
        return true;
    }
    
    private static boolean hasWakefulBroadcastReceiver() {
        return true;
    }
    
    static boolean isAndroidDeviceType() {
        final int deviceType = new OSUtils().getDeviceType();
        boolean b = true;
        if (deviceType != 1) {
            b = false;
        }
        return b;
    }
    
    static boolean isFireOSDeviceType() {
        return new OSUtils().getDeviceType() == 2;
    }
    
    static boolean isGMSInstalledAndEnabled() {
        return packageInstalledAndEnabled("com.google.android.gms");
    }
    
    private static boolean isHMSCoreInstalledAndEnabled() {
        return HuaweiApiAvailability.getInstance().isHuaweiMobileServicesAvailable(OneSignal.appContext) == 0;
    }
    
    private static boolean isHMSCoreInstalledAndEnabledFallback() {
        return packageInstalledAndEnabled("com.huawei.hwid");
    }
    
    static boolean isHuaweiDeviceType() {
        return new OSUtils().getDeviceType() == 13;
    }
    
    static boolean isRunningOnMainThread() {
        return Thread.currentThread().equals(Looper.getMainLooper().getThread());
    }
    
    static boolean isStringNotEmpty(final String s) {
        return TextUtils.isEmpty((CharSequence)s) ^ true;
    }
    
    static boolean isValidEmail(final String s) {
        return s != null && Pattern.compile("^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$").matcher((CharSequence)s).matches();
    }
    
    static boolean isValidResourceName(final String s) {
        return s != null && !s.matches("^[0-9]");
    }
    
    static Bundle jsonStringToBundle(String s) {
        try {
            final JSONObject jsonObject = new JSONObject(s);
            final Bundle bundle = new Bundle();
            final Iterator keys = jsonObject.keys();
            while (keys.hasNext()) {
                s = (String)keys.next();
                bundle.putString(s, jsonObject.getString(s));
            }
            return bundle;
        }
        catch (final JSONException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    static <T> Set<T> newConcurrentSet() {
        return (Set<T>)Collections.newSetFromMap((Map)new ConcurrentHashMap());
    }
    
    static Set<String> newStringSetFromJSONArray(final JSONArray jsonArray) throws JSONException {
        final HashSet set = new HashSet();
        for (int i = 0; i < jsonArray.length(); ++i) {
            ((Set)set).add((Object)jsonArray.getString(i));
        }
        return (Set<String>)set;
    }
    
    private static boolean opaqueHasClass(final Class<?> clazz) {
        return true;
    }
    
    private static void openURLInBrowser(final Uri uri) {
        OneSignal.appContext.startActivity(openURLInBrowserIntent(uri));
    }
    
    static void openURLInBrowser(final String s) {
        openURLInBrowser(Uri.parse(s.trim()));
    }
    
    static Intent openURLInBrowserIntent(final Uri uri) {
        SchemaType fromString;
        if (uri.getScheme() != null) {
            fromString = SchemaType.fromString(uri.getScheme());
        }
        else {
            fromString = null;
        }
        SchemaType http = fromString;
        Uri parse = uri;
        if (fromString == null) {
            http = SchemaType.HTTP;
            parse = uri;
            if (!uri.toString().contains((CharSequence)"://")) {
                final StringBuilder sb = new StringBuilder("http://");
                sb.append(uri.toString());
                parse = Uri.parse(sb.toString());
                http = http;
            }
        }
        Intent mainSelectorActivity;
        if (OSUtils$1.$SwitchMap$com$onesignal$OSUtils$SchemaType[http.ordinal()] != 1) {
            mainSelectorActivity = new Intent("android.intent.action.VIEW", parse);
        }
        else {
            mainSelectorActivity = Intent.makeMainSelectorActivity("android.intent.action.MAIN", "android.intent.category.APP_BROWSER");
            mainSelectorActivity.setData(parse);
        }
        mainSelectorActivity.addFlags(268435456);
        return mainSelectorActivity;
    }
    
    private static boolean packageInstalledAndEnabled(final String s) {
        try {
            return OneSignal.appContext.getPackageManager().getPackageInfo(s, 128).applicationInfo.enabled;
        }
        catch (final PackageManager$NameNotFoundException ex) {
            return false;
        }
    }
    
    static long[] parseVibrationPattern(final JSONObject jsonObject) {
        try {
            final Object opt = jsonObject.opt("vib_pt");
            JSONArray jsonArray;
            if (opt instanceof String) {
                jsonArray = new JSONArray((String)opt);
            }
            else {
                jsonArray = (JSONArray)opt;
            }
            final long[] array = new long[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); ++i) {
                array[i] = jsonArray.optLong(i);
            }
            return array;
        }
        catch (final JSONException ex) {
            return null;
        }
    }
    
    static void runOnMainThreadDelayed(final Runnable runnable, final int n) {
        new Handler(Looper.getMainLooper()).postDelayed(runnable, (long)n);
    }
    
    static void runOnMainUIThread(final Runnable runnable) {
        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            runnable.run();
        }
        else {
            new Handler(Looper.getMainLooper()).post(runnable);
        }
    }
    
    static boolean shouldLogMissingAppIdError(final String s) {
        if (s != null) {
            return false;
        }
        OneSignal.Log(OneSignal.LOG_LEVEL.INFO, "OneSignal was not initialized, ensure to always initialize OneSignal from the onCreate of your Application class.");
        return true;
    }
    
    public static boolean shouldRetryNetworkRequest(final int n) {
        final int[] no_RETRY_NETWROK_REQUEST_STATUS_CODES = OSUtils.NO_RETRY_NETWROK_REQUEST_STATUS_CODES;
        for (int length = no_RETRY_NETWROK_REQUEST_STATUS_CODES.length, i = 0; i < length; ++i) {
            if (n == no_RETRY_NETWROK_REQUEST_STATUS_CODES[i]) {
                return false;
            }
        }
        return true;
    }
    
    static void sleep(final int n) {
        final long n2 = n;
        try {
            Thread.sleep(n2);
        }
        catch (final InterruptedException ex) {
            ex.printStackTrace();
        }
    }
    
    private boolean supportsADM() {
        try {
            Class.forName("com.amazon.device.messaging.ADM");
            return true;
        }
        catch (final ClassNotFoundException ex) {
            return false;
        }
    }
    
    private boolean supportsGooglePush() {
        return hasFCMLibrary() && isGMSInstalledAndEnabled();
    }
    
    private boolean supportsHMS() {
        return hasHMSAvailabilityLibrary() && hasAllHMSLibrariesForPushKit() && isHMSCoreInstalledAndEnabled();
    }
    
    Integer checkForGooglePushLibrary() {
        if (!hasFCMLibrary()) {
            OneSignal.Log(OneSignal.LOG_LEVEL.FATAL, "The Firebase FCM library is missing! Please make sure to include it in your project.");
            return -4;
        }
        return null;
    }
    
    String getCarrierName() {
        String s = null;
        try {
            final String networkOperatorName = ((TelephonyManager)OneSignal.appContext.getSystemService("phone")).getNetworkOperatorName();
            if (!"".equals((Object)networkOperatorName)) {
                s = networkOperatorName;
            }
            return s;
        }
        finally {
            final Throwable t;
            t.printStackTrace();
            return null;
        }
    }
    
    int getDeviceType() {
        if (this.supportsADM()) {
            return 2;
        }
        if (this.supportsGooglePush()) {
            return 1;
        }
        if (this.supportsHMS()) {
            return 13;
        }
        if (isGMSInstalledAndEnabled()) {
            return 1;
        }
        if (isHMSCoreInstalledAndEnabledFallback()) {
            return 13;
        }
        return 1;
    }
    
    Integer getNetType() {
        final NetworkInfo activeNetworkInfo = ((ConnectivityManager)OneSignal.appContext.getSystemService("connectivity")).getActiveNetworkInfo();
        if (activeNetworkInfo == null) {
            return null;
        }
        final int type = activeNetworkInfo.getType();
        if (type != 1 && type != 9) {
            return 1;
        }
        return 0;
    }
    
    int initializationChecker(final Context context, final String s) {
        final int deviceType = this.getDeviceType();
        try {
            UUID.fromString(s);
            if ("b2f7f966-d8cc-11e4-bed1-df8f05be55ba".equals((Object)s) || "5eb5a37e-b458-11e3-ac11-000c2940e62c".equals((Object)s)) {
                OneSignal.Log(OneSignal.LOG_LEVEL.ERROR, "OneSignal Example AppID detected, please update to your app's id found on OneSignal.com");
            }
            int n = 1;
            if (deviceType == 1) {
                final Integer checkForGooglePushLibrary = this.checkForGooglePushLibrary();
                n = n;
                if (checkForGooglePushLibrary != null) {
                    n = checkForGooglePushLibrary;
                }
            }
            final Integer checkAndroidSupportLibrary = this.checkAndroidSupportLibrary(context);
            if (checkAndroidSupportLibrary != null) {
                n = checkAndroidSupportLibrary;
            }
            return n;
        }
        finally {
            final Throwable t;
            OneSignal.Log(OneSignal.LOG_LEVEL.FATAL, "OneSignal AppId format is invalid.\nExample: 'b2f7f966-d8cc-11e4-bed1-df8f05be55ba'\n", t);
            return -999;
        }
    }
    
    public enum SchemaType
    {
        private static final SchemaType[] $VALUES;
        
        DATA("data"), 
        HTTP("http"), 
        HTTPS("https");
        
        private final String text;
        
        private SchemaType(final String text) {
            this.text = text;
        }
        
        public static SchemaType fromString(final String s) {
            for (final SchemaType schemaType : values()) {
                if (schemaType.text.equalsIgnoreCase(s)) {
                    return schemaType;
                }
            }
            return null;
        }
    }
}
