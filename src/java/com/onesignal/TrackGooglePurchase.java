package com.onesignal;

import java.util.Map;
import android.content.Intent;
import android.content.ComponentName;
import java.util.Iterator;
import java.util.Collection;
import java.math.BigDecimal;
import java.util.HashMap;
import android.os.IBinder;
import org.json.JSONObject;
import android.os.Bundle;
import org.json.JSONException;
import org.json.JSONArray;
import java.util.ArrayList;
import android.content.ServiceConnection;
import java.lang.reflect.Method;
import android.content.Context;

class TrackGooglePurchase
{
    private static Class<?> IInAppBillingServiceClass;
    private static int iapEnabled = -99;
    private Context appContext;
    private Method getPurchasesMethod;
    private Method getSkuDetailsMethod;
    private boolean isWaitingForPurchasesRequest;
    private Object mIInAppBillingService;
    private ServiceConnection mServiceConn;
    private boolean newAsExisting;
    private ArrayList<String> purchaseTokens;
    
    TrackGooglePurchase(final Context appContext) {
        this.newAsExisting = true;
        boolean newAsExisting = false;
        this.isWaitingForPurchasesRequest = false;
        this.appContext = appContext;
        this.purchaseTokens = (ArrayList<String>)new ArrayList();
        try {
            final JSONArray jsonArray = new JSONArray(OneSignalPrefs.getString("GTPlayerPurchases", "purchaseTokens", "[]"));
            for (int i = 0; i < jsonArray.length(); ++i) {
                this.purchaseTokens.add((Object)jsonArray.get(i).toString());
            }
            if (jsonArray.length() == 0) {
                newAsExisting = true;
            }
            this.newAsExisting = newAsExisting;
            if (newAsExisting) {
                this.newAsExisting = OneSignalPrefs.getBool("GTPlayerPurchases", "ExistingPurchases", true);
            }
        }
        catch (final JSONException ex) {
            ex.printStackTrace();
        }
        this.trackIAP();
    }
    
    static boolean CanTrack(final Context context) {
        if (TrackGooglePurchase.iapEnabled == -99) {
            TrackGooglePurchase.iapEnabled = context.checkCallingOrSelfPermission("com.android.vending.BILLING");
        }
        boolean b = false;
        try {
            if (TrackGooglePurchase.iapEnabled == 0) {
                TrackGooglePurchase.IInAppBillingServiceClass = Class.forName("com.android.vending.billing.IInAppBillingService");
            }
            if (TrackGooglePurchase.iapEnabled == 0) {
                b = true;
            }
            return b;
        }
        finally {
            TrackGooglePurchase.iapEnabled = 0;
            return false;
        }
    }
    
    private void QueryBoughtItems() {
        if (this.isWaitingForPurchasesRequest) {
            return;
        }
        new Thread((Runnable)new Runnable(this) {
            final TrackGooglePurchase this$0;
            
            public void run() {
                this.this$0.isWaitingForPurchasesRequest = true;
                try {
                    if (this.this$0.getPurchasesMethod == null) {
                        this.this$0.getPurchasesMethod = getGetPurchasesMethod(TrackGooglePurchase.IInAppBillingServiceClass);
                        this.this$0.getPurchasesMethod.setAccessible(true);
                    }
                    final Bundle bundle = (Bundle)this.this$0.getPurchasesMethod.invoke(this.this$0.mIInAppBillingService, new Object[] { 3, this.this$0.appContext.getPackageName(), "inapp", null });
                    if (bundle.getInt("RESPONSE_CODE") == 0) {
                        final ArrayList list = new ArrayList();
                        final ArrayList list2 = new ArrayList();
                        final ArrayList stringArrayList = bundle.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
                        final ArrayList stringArrayList2 = bundle.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
                        for (int i = 0; i < stringArrayList2.size(); ++i) {
                            final String s = (String)stringArrayList2.get(i);
                            final String s2 = (String)stringArrayList.get(i);
                            final String string = new JSONObject(s).getString("purchaseToken");
                            if (!this.this$0.purchaseTokens.contains((Object)string) && !list2.contains((Object)string)) {
                                list2.add((Object)string);
                                list.add((Object)s2);
                            }
                        }
                        if (list.size() > 0) {
                            this.this$0.sendPurchases((ArrayList<String>)list, (ArrayList<String>)list2);
                        }
                        else if (stringArrayList2.size() == 0) {
                            this.this$0.newAsExisting = false;
                            OneSignalPrefs.saveBool("GTPlayerPurchases", "ExistingPurchases", false);
                        }
                    }
                }
                finally {
                    final Throwable t;
                    t.printStackTrace();
                }
                this.this$0.isWaitingForPurchasesRequest = false;
            }
        }).start();
    }
    
    private static Method getAsInterfaceMethod(final Class clazz) {
        for (final Method method : clazz.getMethods()) {
            final Class[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length == 1 && parameterTypes[0] == IBinder.class) {
                return method;
            }
        }
        return null;
    }
    
    private static Method getGetPurchasesMethod(final Class clazz) {
        for (final Method method : clazz.getMethods()) {
            final Class[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length == 4 && parameterTypes[0] == Integer.TYPE && parameterTypes[1] == String.class && parameterTypes[2] == String.class && parameterTypes[3] == String.class) {
                return method;
            }
        }
        return null;
    }
    
    private static Method getGetSkuDetailsMethod(Class returnType) {
        for (final Method method : returnType.getMethods()) {
            final Class[] parameterTypes = method.getParameterTypes();
            returnType = method.getReturnType();
            if (parameterTypes.length == 4 && parameterTypes[0] == Integer.TYPE && parameterTypes[1] == String.class && parameterTypes[2] == String.class && parameterTypes[3] == Bundle.class && returnType == Bundle.class) {
                return method;
            }
        }
        return null;
    }
    
    private void sendPurchases(final ArrayList<String> list, final ArrayList<String> list2) {
        try {
            if (this.getSkuDetailsMethod == null) {
                (this.getSkuDetailsMethod = getGetSkuDetailsMethod(TrackGooglePurchase.IInAppBillingServiceClass)).setAccessible(true);
            }
            final Bundle bundle = new Bundle();
            bundle.putStringArrayList("ITEM_ID_LIST", (ArrayList)list);
            final Bundle bundle2 = (Bundle)this.getSkuDetailsMethod.invoke(this.mIInAppBillingService, new Object[] { 3, this.appContext.getPackageName(), "inapp", bundle });
            if (bundle2.getInt("RESPONSE_CODE") == 0) {
                final ArrayList stringArrayList = bundle2.getStringArrayList("DETAILS_LIST");
                final HashMap hashMap = new HashMap();
                final Iterator iterator = stringArrayList.iterator();
                while (iterator.hasNext()) {
                    final JSONObject jsonObject = new JSONObject((String)iterator.next());
                    final String string = jsonObject.getString("productId");
                    final BigDecimal divide = new BigDecimal(jsonObject.getString("price_amount_micros")).divide(new BigDecimal(1000000));
                    final JSONObject jsonObject2 = new JSONObject();
                    jsonObject2.put("sku", (Object)string);
                    jsonObject2.put("iso", (Object)jsonObject.getString("price_currency_code"));
                    jsonObject2.put("amount", (Object)divide.toString());
                    ((Map)hashMap).put((Object)string, (Object)jsonObject2);
                }
                final JSONArray jsonArray = new JSONArray();
                for (final String s : list) {
                    if (!((Map)hashMap).containsKey((Object)s)) {
                        continue;
                    }
                    jsonArray.put(((Map)hashMap).get((Object)s));
                }
                if (jsonArray.length() > 0) {
                    OneSignal.sendPurchases(jsonArray, this.newAsExisting, (OneSignalRestClient$ResponseHandler)new OneSignalRestClient$ResponseHandler(this, list2) {
                        final TrackGooglePurchase this$0;
                        final ArrayList val$newPurchaseTokens;
                        
                        public void onFailure(final int n, final JSONObject jsonObject, final Throwable t) {
                            OneSignal.Log(OneSignal$LOG_LEVEL.WARN, "HTTP sendPurchases failed to send.", t);
                            this.this$0.isWaitingForPurchasesRequest = false;
                        }
                        
                        public void onSuccess(final String s) {
                            this.this$0.purchaseTokens.addAll((Collection)this.val$newPurchaseTokens);
                            OneSignalPrefs.saveString("GTPlayerPurchases", "purchaseTokens", this.this$0.purchaseTokens.toString());
                            OneSignalPrefs.saveBool("GTPlayerPurchases", "ExistingPurchases", true);
                            this.this$0.newAsExisting = false;
                            this.this$0.isWaitingForPurchasesRequest = false;
                        }
                    });
                }
            }
        }
        finally {
            final Throwable t;
            OneSignal.Log(OneSignal$LOG_LEVEL.WARN, "Failed to track IAP purchases", t);
        }
    }
    
    void trackIAP() {
        if (this.mServiceConn == null) {
            this.mServiceConn = (ServiceConnection)new ServiceConnection(this) {
                final TrackGooglePurchase this$0;
                
                public void onServiceConnected(final ComponentName componentName, final IBinder binder) {
                    try {
                        final Method access$200 = getAsInterfaceMethod(Class.forName("com.android.vending.billing.IInAppBillingService$Stub"));
                        access$200.setAccessible(true);
                        this.this$0.mIInAppBillingService = access$200.invoke((Object)null, new Object[] { binder });
                        this.this$0.QueryBoughtItems();
                    }
                    finally {
                        final Throwable t;
                        t.printStackTrace();
                    }
                }
                
                public void onServiceDisconnected(final ComponentName componentName) {
                    TrackGooglePurchase.iapEnabled = -99;
                    this.this$0.mIInAppBillingService = null;
                }
            };
            final Intent intent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
            intent.setPackage("com.android.vending");
            this.appContext.bindService(intent, this.mServiceConn, 1);
        }
        else if (this.mIInAppBillingService != null) {
            this.QueryBoughtItems();
        }
    }
}
