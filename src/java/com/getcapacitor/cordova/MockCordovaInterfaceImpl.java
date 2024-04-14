package com.getcapacitor.cordova;

import org.json.JSONException;
import android.util.Pair;
import org.apache.cordova.CordovaPlugin;
import java.util.concurrent.Executors;
import androidx.appcompat.app.AppCompatActivity;
import org.apache.cordova.CordovaInterfaceImpl;

public class MockCordovaInterfaceImpl extends CordovaInterfaceImpl
{
    public MockCordovaInterfaceImpl(final AppCompatActivity appCompatActivity) {
        super(appCompatActivity, Executors.newCachedThreadPool());
    }
    
    public CordovaPlugin getActivityResultCallback() {
        return this.activityResultCallback;
    }
    
    public boolean handlePermissionResult(final int n, final String[] array, final int[] array2) throws JSONException {
        final Pair andRemoveCallback = this.permissionResultCallbacks.getAndRemoveCallback(n);
        if (andRemoveCallback != null) {
            ((CordovaPlugin)andRemoveCallback.first).onRequestPermissionResult((int)andRemoveCallback.second, array, array2);
            return true;
        }
        return false;
    }
}
