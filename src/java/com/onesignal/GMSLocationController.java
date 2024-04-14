package com.onesignal;

import com.google.android.gms.common.ConnectionResult;
import android.os.Bundle;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.api.GoogleApiClient$OnConnectionFailedListener;
import com.google.android.gms.common.api.GoogleApiClient$ConnectionCallbacks;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.common.api.GoogleApiClient$Builder;

class GMSLocationController extends LocationController
{
    static final int API_FALLBACK_TIME = 30000;
    private static GoogleApiClientCompatProxy googleApiClient;
    static GMSLocationController$LocationUpdateListener locationUpdateListener;
    
    static void fireFailedComplete() {
        final Object syncLock = GMSLocationController.syncLock;
        synchronized (syncLock) {
            final GoogleApiClientCompatProxy googleApiClient = GMSLocationController.googleApiClient;
            if (googleApiClient != null) {
                googleApiClient.disconnect();
            }
            GMSLocationController.googleApiClient = null;
        }
    }
    
    private static int getApiFallbackWait() {
        return 30000;
    }
    
    private static void initGoogleLocation() {
        if (GMSLocationController.fallbackFailThread != null) {
            return;
        }
        final Object syncLock = GMSLocationController.syncLock;
        synchronized (syncLock) {
            startFallBackThread();
            if (GMSLocationController.googleApiClient != null && GMSLocationController.lastLocation != null) {
                fireCompleteForLocation(GMSLocationController.lastLocation);
            }
            else {
                final GoogleApiClientListener googleApiClientListener = new GoogleApiClientListener(null);
                (GMSLocationController.googleApiClient = new GoogleApiClientCompatProxy(new GoogleApiClient$Builder(GMSLocationController.classContext).addApi(LocationServices.API).addConnectionCallbacks((GoogleApiClient$ConnectionCallbacks)googleApiClientListener).addOnConnectionFailedListener((GoogleApiClient$OnConnectionFailedListener)googleApiClientListener).setHandler(getLocationHandlerThread().mHandler).build())).connect();
            }
        }
    }
    
    static void onFocusChange() {
        final Object syncLock = GMSLocationController.syncLock;
        synchronized (syncLock) {
            OneSignal.Log(OneSignal$LOG_LEVEL.DEBUG, "GMSLocationController onFocusChange!");
            final GoogleApiClientCompatProxy googleApiClient = GMSLocationController.googleApiClient;
            if (googleApiClient != null && googleApiClient.realInstance().isConnected()) {
                final GoogleApiClientCompatProxy googleApiClient2 = GMSLocationController.googleApiClient;
                if (googleApiClient2 != null) {
                    final GoogleApiClient realInstance = googleApiClient2.realInstance();
                    if (GMSLocationController.locationUpdateListener != null) {
                        LocationServices.FusedLocationApi.removeLocationUpdates(realInstance, (LocationListener)GMSLocationController.locationUpdateListener);
                    }
                    GMSLocationController.locationUpdateListener = new GMSLocationController$LocationUpdateListener(realInstance);
                }
            }
        }
    }
    
    private static void startFallBackThread() {
        (GMSLocationController.fallbackFailThread = new Thread((Runnable)new GMSLocationController$1(), "OS_GMS_LOCATION_FALLBACK")).start();
    }
    
    static void startGetLocation() {
        initGoogleLocation();
    }
    
    private static class GoogleApiClientListener implements GoogleApiClient$ConnectionCallbacks, GoogleApiClient$OnConnectionFailedListener
    {
        public void onConnected(final Bundle bundle) {
            final Object syncLock = LocationController.syncLock;
            synchronized (syncLock) {
                if (GMSLocationController.googleApiClient != null && GMSLocationController.googleApiClient.realInstance() != null) {
                    final OneSignal$LOG_LEVEL debug = OneSignal$LOG_LEVEL.DEBUG;
                    final StringBuilder sb = new StringBuilder("GMSLocationController GoogleApiClientListener onConnected lastLocation: ");
                    sb.append((Object)LocationController.lastLocation);
                    OneSignal.Log(debug, sb.toString());
                    if (LocationController.lastLocation == null) {
                        LocationController.lastLocation = GMSLocationController$FusedLocationApiWrapper.getLastLocation(GMSLocationController.googleApiClient.realInstance());
                        final OneSignal$LOG_LEVEL debug2 = OneSignal$LOG_LEVEL.DEBUG;
                        final StringBuilder sb2 = new StringBuilder("GMSLocationController GoogleApiClientListener lastLocation: ");
                        sb2.append((Object)LocationController.lastLocation);
                        OneSignal.Log(debug2, sb2.toString());
                        if (LocationController.lastLocation != null) {
                            LocationController.fireCompleteForLocation(LocationController.lastLocation);
                        }
                    }
                    GMSLocationController.locationUpdateListener = new GMSLocationController$LocationUpdateListener(GMSLocationController.googleApiClient.realInstance());
                    return;
                }
                OneSignal.Log(OneSignal$LOG_LEVEL.DEBUG, "GMSLocationController GoogleApiClientListener onConnected googleApiClient not available, returning");
            }
        }
        
        public void onConnectionFailed(final ConnectionResult connectionResult) {
            final OneSignal$LOG_LEVEL debug = OneSignal$LOG_LEVEL.DEBUG;
            final StringBuilder sb = new StringBuilder("GMSLocationController GoogleApiClientListener onConnectionSuspended connectionResult: ");
            sb.append((Object)connectionResult);
            OneSignal.Log(debug, sb.toString());
            GMSLocationController.fireFailedComplete();
        }
        
        public void onConnectionSuspended(final int n) {
            final OneSignal$LOG_LEVEL debug = OneSignal$LOG_LEVEL.DEBUG;
            final StringBuilder sb = new StringBuilder("GMSLocationController GoogleApiClientListener onConnectionSuspended i: ");
            sb.append(n);
            OneSignal.Log(debug, sb.toString());
            GMSLocationController.fireFailedComplete();
        }
    }
}
