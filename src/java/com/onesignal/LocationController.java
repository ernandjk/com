package com.onesignal;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Build$VERSION;
import java.math.RoundingMode;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import android.content.pm.PackageManager$NameNotFoundException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import android.location.Location;
import android.content.Context;

class LocationController
{
    static final long BACKGROUND_UPDATE_TIME_MS = 570000L;
    static final long FOREGROUND_UPDATE_TIME_MS = 270000L;
    private static final long TIME_BACKGROUND_SEC = 600L;
    private static final long TIME_FOREGROUND_SEC = 300L;
    static Context classContext;
    static Thread fallbackFailThread;
    static Location lastLocation;
    private static boolean locationCoarse;
    private static LocationHandlerThread locationHandlerThread;
    private static ConcurrentHashMap<PermissionType, LocationHandler> locationHandlers;
    private static final List<LocationController.LocationController$LocationPromptCompletionHandler> promptHandlers;
    static String requestPermission;
    static final Object syncLock;
    
    static {
        promptHandlers = (List)new ArrayList();
        LocationController.locationHandlers = (ConcurrentHashMap<PermissionType, LocationHandler>)new ConcurrentHashMap();
        syncLock = new Object() {};
    }
    
    private static void addPromptHandlerIfAvailable(final LocationHandler locationHandler) {
        if (locationHandler instanceof LocationController.LocationController$LocationPromptCompletionHandler) {
            final List<LocationController.LocationController$LocationPromptCompletionHandler> promptHandlers = LocationController.promptHandlers;
            synchronized (promptHandlers) {
                promptHandlers.add((Object)locationHandler);
            }
        }
    }
    
    private static void backgroundLocationPermissionLogic(final Context context, final boolean b, final boolean b2) {
        try {
            if (Arrays.asList((Object[])context.getPackageManager().getPackageInfo(context.getPackageName(), 4096).requestedPermissions).contains((Object)"android.permission.ACCESS_BACKGROUND_LOCATION")) {
                LocationController.requestPermission = "android.permission.ACCESS_BACKGROUND_LOCATION";
            }
            if (LocationController.requestPermission != null && b) {
                LocationPermissionController.INSTANCE.prompt(b2, LocationController.requestPermission);
            }
            else {
                sendAndClearPromptHandlers(b, OneSignal.PromptActionResult.PERMISSION_GRANTED);
                startGetLocation();
            }
        }
        catch (final PackageManager$NameNotFoundException ex) {
            sendAndClearPromptHandlers(b, OneSignal.PromptActionResult.ERROR);
            ex.printStackTrace();
        }
    }
    
    private static void fireComplete(final LocationPoint locationPoint) {
        final HashMap hashMap = new HashMap();
        synchronized (LocationController.class) {
            hashMap.putAll((Map)LocationController.locationHandlers);
            LocationController.locationHandlers.clear();
            final Thread fallbackFailThread = LocationController.fallbackFailThread;
            monitorexit(LocationController.class);
            final Iterator iterator = hashMap.keySet().iterator();
            while (iterator.hasNext()) {
                ((LocationHandler)hashMap.get((Object)iterator.next())).onComplete(locationPoint);
            }
            if (fallbackFailThread != null && !Thread.currentThread().equals(fallbackFailThread)) {
                fallbackFailThread.interrupt();
            }
            if (fallbackFailThread == LocationController.fallbackFailThread) {
                synchronized (LocationController.class) {
                    if (fallbackFailThread == LocationController.fallbackFailThread) {
                        LocationController.fallbackFailThread = null;
                    }
                }
            }
            setLastLocationTime(OneSignal.getTime().getCurrentTimeMillis());
        }
    }
    
    protected static void fireCompleteForLocation(final Location location) {
        final OneSignal.LOG_LEVEL debug = OneSignal.LOG_LEVEL.DEBUG;
        final StringBuilder sb = new StringBuilder("LocationController fireCompleteForLocation with location: ");
        sb.append((Object)location);
        OneSignal.Log(debug, sb.toString());
        final LocationPoint locationPoint = new LocationPoint();
        locationPoint.accuracy = location.getAccuracy();
        locationPoint.bg = (OneSignal.isInForeground() ^ true);
        locationPoint.type = ((LocationController.locationCoarse ^ true) ? 1 : 0);
        locationPoint.timeStamp = location.getTime();
        if (LocationController.locationCoarse) {
            locationPoint.lat = new BigDecimal(location.getLatitude()).setScale(7, RoundingMode.HALF_UP).doubleValue();
            locationPoint.log = new BigDecimal(location.getLongitude()).setScale(7, RoundingMode.HALF_UP).doubleValue();
        }
        else {
            locationPoint.lat = location.getLatitude();
            locationPoint.log = location.getLongitude();
        }
        fireComplete(locationPoint);
        scheduleUpdate(LocationController.classContext);
    }
    
    static void fireFailedComplete() {
        final Object syncLock = LocationController.syncLock;
        synchronized (syncLock) {
            if (isGooglePlayServicesAvailable()) {
                GMSLocationController.fireFailedComplete();
            }
            else if (isHMSAvailable()) {
                HMSLocationController.fireFailedComplete();
            }
            monitorexit(syncLock);
            fireComplete(null);
        }
    }
    
    private static long getLastLocationTime() {
        return OneSignalPrefs.getLong(OneSignalPrefs.PREFS_ONESIGNAL, "OS_LAST_LOCATION_TIME", -600000L);
    }
    
    static void getLocation(final Context classContext, final boolean b, final boolean b2, final LocationHandler locationHandler) {
        addPromptHandlerIfAvailable(locationHandler);
        LocationController.classContext = classContext;
        LocationController.locationHandlers.put((Object)locationHandler.getType(), (Object)locationHandler);
        if (!OneSignal.isLocationShared()) {
            sendAndClearPromptHandlers(b, OneSignal.PromptActionResult.ERROR);
            fireFailedComplete();
            return;
        }
        final int checkSelfPermission = AndroidSupportV4Compat.ContextCompat.checkSelfPermission(classContext, "android.permission.ACCESS_FINE_LOCATION");
        int checkSelfPermission2 = -1;
        int checkSelfPermission3;
        if (checkSelfPermission == -1) {
            checkSelfPermission3 = AndroidSupportV4Compat.ContextCompat.checkSelfPermission(classContext, "android.permission.ACCESS_COARSE_LOCATION");
            LocationController.locationCoarse = true;
        }
        else {
            checkSelfPermission3 = -1;
        }
        if (Build$VERSION.SDK_INT >= 29) {
            checkSelfPermission2 = AndroidSupportV4Compat.ContextCompat.checkSelfPermission(classContext, "android.permission.ACCESS_BACKGROUND_LOCATION");
        }
        if (Build$VERSION.SDK_INT < 23) {
            if (checkSelfPermission != 0 && checkSelfPermission3 != 0) {
                sendAndClearPromptHandlers(b, OneSignal.PromptActionResult.LOCATION_PERMISSIONS_MISSING_MANIFEST);
                locationHandler.onComplete(null);
                return;
            }
            sendAndClearPromptHandlers(b, OneSignal.PromptActionResult.PERMISSION_GRANTED);
            startGetLocation();
        }
        else if (checkSelfPermission != 0) {
            try {
                final List list = Arrays.asList((Object[])classContext.getPackageManager().getPackageInfo(classContext.getPackageName(), 4096).requestedPermissions);
                final OneSignal.PromptActionResult permission_DENIED = OneSignal.PromptActionResult.PERMISSION_DENIED;
                Enum<OneSignal.PromptActionResult> location_PERMISSIONS_MISSING_MANIFEST;
                if (list.contains((Object)"android.permission.ACCESS_FINE_LOCATION")) {
                    LocationController.requestPermission = "android.permission.ACCESS_FINE_LOCATION";
                    location_PERMISSIONS_MISSING_MANIFEST = permission_DENIED;
                }
                else if (list.contains((Object)"android.permission.ACCESS_COARSE_LOCATION")) {
                    if (checkSelfPermission3 != 0) {
                        LocationController.requestPermission = "android.permission.ACCESS_COARSE_LOCATION";
                        location_PERMISSIONS_MISSING_MANIFEST = permission_DENIED;
                    }
                    else {
                        location_PERMISSIONS_MISSING_MANIFEST = permission_DENIED;
                        if (Build$VERSION.SDK_INT >= 29) {
                            location_PERMISSIONS_MISSING_MANIFEST = permission_DENIED;
                            if (list.contains((Object)"android.permission.ACCESS_BACKGROUND_LOCATION")) {
                                LocationController.requestPermission = "android.permission.ACCESS_BACKGROUND_LOCATION";
                                location_PERMISSIONS_MISSING_MANIFEST = permission_DENIED;
                            }
                        }
                    }
                }
                else {
                    OneSignal.onesignalLog(OneSignal.LOG_LEVEL.INFO, "Location permissions not added on AndroidManifest file");
                    location_PERMISSIONS_MISSING_MANIFEST = OneSignal.PromptActionResult.LOCATION_PERMISSIONS_MISSING_MANIFEST;
                }
                if (LocationController.requestPermission != null && b) {
                    LocationPermissionController.INSTANCE.prompt(b2, LocationController.requestPermission);
                }
                else if (checkSelfPermission3 == 0) {
                    sendAndClearPromptHandlers(b, OneSignal.PromptActionResult.PERMISSION_GRANTED);
                    startGetLocation();
                }
                else {
                    sendAndClearPromptHandlers(b, (OneSignal.PromptActionResult)location_PERMISSIONS_MISSING_MANIFEST);
                    fireFailedComplete();
                }
            }
            catch (final PackageManager$NameNotFoundException ex) {
                sendAndClearPromptHandlers(b, OneSignal.PromptActionResult.ERROR);
                ex.printStackTrace();
            }
        }
        else if (Build$VERSION.SDK_INT >= 29 && checkSelfPermission2 != 0) {
            backgroundLocationPermissionLogic(classContext, b, b2);
        }
        else {
            sendAndClearPromptHandlers(b, OneSignal.PromptActionResult.PERMISSION_GRANTED);
            startGetLocation();
        }
    }
    
    static LocationHandlerThread getLocationHandlerThread() {
        if (LocationController.locationHandlerThread == null) {
            final Object syncLock = LocationController.syncLock;
            synchronized (syncLock) {
                if (LocationController.locationHandlerThread == null) {
                    LocationController.locationHandlerThread = new LocationHandlerThread();
                }
            }
        }
        return LocationController.locationHandlerThread;
    }
    
    private static boolean hasLocationPermission(final Context context) {
        return AndroidSupportV4Compat.ContextCompat.checkSelfPermission(context, "android.permission.ACCESS_FINE_LOCATION") == 0 || AndroidSupportV4Compat.ContextCompat.checkSelfPermission(context, "android.permission.ACCESS_COARSE_LOCATION") == 0;
    }
    
    static boolean isGooglePlayServicesAvailable() {
        return OSUtils.isAndroidDeviceType() && OSUtils.hasGMSLocationLibrary();
    }
    
    static boolean isHMSAvailable() {
        return OSUtils.isHuaweiDeviceType() && OSUtils.hasHMSLocationLibrary();
    }
    
    static void onFocusChange() {
        final Object syncLock = LocationController.syncLock;
        synchronized (syncLock) {
            if (isGooglePlayServicesAvailable()) {
                GMSLocationController.onFocusChange();
                return;
            }
            if (isHMSAvailable()) {
                HMSLocationController.onFocusChange();
            }
        }
    }
    
    static boolean scheduleUpdate(final Context context) {
        if (!hasLocationPermission(context)) {
            OneSignal.onesignalLog(OneSignal.LOG_LEVEL.DEBUG, "LocationController scheduleUpdate not possible, location permission not enabled");
            return false;
        }
        if (!OneSignal.isLocationShared()) {
            OneSignal.onesignalLog(OneSignal.LOG_LEVEL.DEBUG, "LocationController scheduleUpdate not possible, location shared not enabled");
            return false;
        }
        final long n = OneSignal.getTime().getCurrentTimeMillis() - getLastLocationTime();
        long n2;
        if (OneSignal.isInForeground()) {
            n2 = 300L;
        }
        else {
            n2 = 600L;
        }
        final long n3 = n2 * 1000L;
        final OneSignal.LOG_LEVEL debug = OneSignal.LOG_LEVEL.DEBUG;
        final StringBuilder sb = new StringBuilder("LocationController scheduleUpdate lastTime: ");
        sb.append(n);
        sb.append(" minTime: ");
        sb.append(n3);
        OneSignal.onesignalLog(debug, sb.toString());
        OSSyncService.getInstance().scheduleLocationUpdateTask(context, n3 - n);
        return true;
    }
    
    static void sendAndClearPromptHandlers(final boolean b, final OneSignal.PromptActionResult promptActionResult) {
        if (!b) {
            OneSignal.onesignalLog(OneSignal.LOG_LEVEL.DEBUG, "LocationController sendAndClearPromptHandlers from non prompt flow");
            return;
        }
        final List<LocationController.LocationController$LocationPromptCompletionHandler> promptHandlers = LocationController.promptHandlers;
        synchronized (promptHandlers) {
            OneSignal.onesignalLog(OneSignal.LOG_LEVEL.DEBUG, "LocationController calling prompt handlers");
            final Iterator iterator = promptHandlers.iterator();
            while (iterator.hasNext()) {
                ((LocationController.LocationController$LocationPromptCompletionHandler)iterator.next()).onAnswered(promptActionResult);
            }
            LocationController.promptHandlers.clear();
        }
    }
    
    private static void setLastLocationTime(final long n) {
        OneSignalPrefs.saveLong(OneSignalPrefs.PREFS_ONESIGNAL, "OS_LAST_LOCATION_TIME", n);
    }
    
    static void startGetLocation() {
        final OneSignal.LOG_LEVEL debug = OneSignal.LOG_LEVEL.DEBUG;
        final StringBuilder sb = new StringBuilder("LocationController startGetLocation with lastLocation: ");
        sb.append((Object)LocationController.lastLocation);
        OneSignal.Log(debug, sb.toString());
        try {
            if (isGooglePlayServicesAvailable()) {
                GMSLocationController.startGetLocation();
            }
            else if (isHMSAvailable()) {
                HMSLocationController.startGetLocation();
            }
            else {
                OneSignal.Log(OneSignal.LOG_LEVEL.WARN, "LocationController startGetLocation not possible, no location dependency found");
                fireFailedComplete();
            }
        }
        finally {
            final Throwable t;
            OneSignal.Log(OneSignal.LOG_LEVEL.WARN, "Location permission exists but there was an error initializing: ", t);
            fireFailedComplete();
        }
    }
    
    interface LocationHandler
    {
        PermissionType getType();
        
        void onComplete(final LocationPoint p0);
    }
    
    protected static class LocationHandlerThread extends HandlerThread
    {
        Handler mHandler;
        
        LocationHandlerThread() {
            super("OSH_LocationHandlerThread");
            this.start();
            this.mHandler = new Handler(this.getLooper());
        }
    }
    
    static class LocationPoint
    {
        Float accuracy;
        Boolean bg;
        Double lat;
        Double log;
        Long timeStamp;
        Integer type;
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("LocationPoint{lat=");
            sb.append((Object)this.lat);
            sb.append(", log=");
            sb.append((Object)this.log);
            sb.append(", accuracy=");
            sb.append((Object)this.accuracy);
            sb.append(", type=");
            sb.append((Object)this.type);
            sb.append(", bg=");
            sb.append((Object)this.bg);
            sb.append(", timeStamp=");
            sb.append((Object)this.timeStamp);
            sb.append('}');
            return sb.toString();
        }
    }
    
    enum PermissionType
    {
        private static final PermissionType[] $VALUES;
        
        PROMPT_LOCATION, 
        STARTUP, 
        SYNC_SERVICE;
    }
}
