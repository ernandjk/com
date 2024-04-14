package com.onesignal;

import com.huawei.hms.location.LocationCallback;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hms.location.LocationServices;
import com.huawei.hms.location.FusedLocationProviderClient;

class HMSLocationController extends LocationController
{
    private static FusedLocationProviderClient hmsFusedLocationClient;
    static HMSLocationController.HMSLocationController$LocationUpdateListener locationUpdateListener;
    
    static void fireFailedComplete() {
        final Object syncLock = HMSLocationController.syncLock;
        synchronized (syncLock) {
            HMSLocationController.hmsFusedLocationClient = null;
        }
    }
    
    private static void initHuaweiLocation() {
        final Object syncLock = HMSLocationController.syncLock;
        synchronized (syncLock) {
            if (HMSLocationController.hmsFusedLocationClient == null) {
                try {
                    HMSLocationController.hmsFusedLocationClient = LocationServices.getFusedLocationProviderClient(HMSLocationController.classContext);
                }
                catch (final Exception ex) {
                    final OneSignal$LOG_LEVEL error = OneSignal$LOG_LEVEL.ERROR;
                    final StringBuilder sb = new StringBuilder("Huawei LocationServices getFusedLocationProviderClient failed! ");
                    sb.append((Object)ex);
                    OneSignal.Log(error, sb.toString());
                    fireFailedComplete();
                    return;
                }
            }
            if (HMSLocationController.lastLocation != null) {
                fireCompleteForLocation(HMSLocationController.lastLocation);
            }
            else {
                HMSLocationController.hmsFusedLocationClient.getLastLocation().addOnSuccessListener((OnSuccessListener)new HMSLocationController$2()).addOnFailureListener((OnFailureListener)new HMSLocationController$1());
            }
        }
    }
    
    static void onFocusChange() {
        final Object syncLock = HMSLocationController.syncLock;
        synchronized (syncLock) {
            OneSignal.Log(OneSignal$LOG_LEVEL.DEBUG, "HMSLocationController onFocusChange!");
            if (isHMSAvailable() && HMSLocationController.hmsFusedLocationClient == null) {
                return;
            }
            final FusedLocationProviderClient hmsFusedLocationClient = HMSLocationController.hmsFusedLocationClient;
            if (hmsFusedLocationClient != null) {
                final HMSLocationController.HMSLocationController$LocationUpdateListener locationUpdateListener = HMSLocationController.locationUpdateListener;
                if (locationUpdateListener != null) {
                    hmsFusedLocationClient.removeLocationUpdates((LocationCallback)locationUpdateListener);
                }
                HMSLocationController.locationUpdateListener = new HMSLocationController.HMSLocationController$LocationUpdateListener(HMSLocationController.hmsFusedLocationClient);
            }
        }
    }
    
    static void startGetLocation() {
        initHuaweiLocation();
    }
}
