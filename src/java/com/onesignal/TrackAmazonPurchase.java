package com.onesignal;

import com.amazon.device.iap.model.UserDataResponse;
import com.amazon.device.iap.model.PurchaseUpdatesResponse;
import java.util.Set;
import java.util.HashSet;
import com.amazon.device.iap.model.PurchaseResponse$RequestStatus;
import com.amazon.device.iap.model.PurchaseResponse;
import java.util.Iterator;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import com.amazon.device.iap.model.Product;
import org.json.JSONArray;
import com.amazon.device.iap.model.ProductDataResponse;
import com.amazon.device.iap.model.RequestId;
import com.amazon.device.iap.PurchasingService;
import java.lang.reflect.InvocationTargetException;
import com.amazon.device.iap.PurchasingListener;
import java.lang.reflect.Field;
import android.content.Context;

class TrackAmazonPurchase
{
    private boolean canTrack;
    private Context context;
    private Field listenerHandlerField;
    private Object listenerHandlerObject;
    private OSPurchasingListener osPurchasingListener;
    private boolean registerListenerOnMainThread;
    
    TrackAmazonPurchase(Context forName) {
        this.canTrack = false;
        this.registerListenerOnMainThread = false;
        this.context = forName;
        try {
            forName = (Context)Class.forName("com.amazon.device.iap.internal.d");
            try {
                this.listenerHandlerObject = ((Class)forName).getMethod("d", (Class<?>[])new Class[0]).invoke((Object)null, new Object[0]);
            }
            catch (final NullPointerException ex) {
                this.listenerHandlerObject = ((Class)forName).getMethod("e", (Class<?>[])new Class[0]).invoke((Object)null, new Object[0]);
                this.registerListenerOnMainThread = true;
            }
            (this.listenerHandlerField = ((Class)forName).getDeclaredField("f")).setAccessible(true);
            final OSPurchasingListener osPurchasingListener = new OSPurchasingListener();
            this.osPurchasingListener = osPurchasingListener;
            osPurchasingListener.orgPurchasingListener = (PurchasingListener)this.listenerHandlerField.get(this.listenerHandlerObject);
            this.canTrack = true;
            this.setListener();
        }
        catch (final ClassCastException ex2) {
            logAmazonIAPListenerError((Exception)ex2);
        }
        catch (final NoSuchFieldException ex3) {
            logAmazonIAPListenerError((Exception)ex3);
        }
        catch (final NoSuchMethodException ex4) {
            logAmazonIAPListenerError((Exception)ex4);
        }
        catch (final InvocationTargetException ex5) {
            logAmazonIAPListenerError((Exception)ex5);
        }
        catch (final IllegalAccessException ex6) {
            logAmazonIAPListenerError((Exception)ex6);
        }
        catch (final ClassNotFoundException ex7) {
            logAmazonIAPListenerError((Exception)ex7);
        }
    }
    
    private static void logAmazonIAPListenerError(final Exception ex) {
        OneSignal.Log(OneSignal.LOG_LEVEL.ERROR, "Error adding Amazon IAP listener.", (Throwable)ex);
        ex.printStackTrace();
    }
    
    private void setListener() {
        if (this.registerListenerOnMainThread) {
            OSUtils.runOnMainUIThread((Runnable)new Runnable(this) {
                final TrackAmazonPurchase this$0;
                
                public void run() {
                    PurchasingService.registerListener(this.this$0.context, (PurchasingListener)this.this$0.osPurchasingListener);
                }
            });
        }
        else {
            PurchasingService.registerListener(this.context, (PurchasingListener)this.osPurchasingListener);
        }
    }
    
    void checkListener() {
        if (!this.canTrack) {
            return;
        }
        try {
            final PurchasingListener orgPurchasingListener = (PurchasingListener)this.listenerHandlerField.get(this.listenerHandlerObject);
            final OSPurchasingListener osPurchasingListener = this.osPurchasingListener;
            if (orgPurchasingListener != osPurchasingListener) {
                osPurchasingListener.orgPurchasingListener = orgPurchasingListener;
                this.setListener();
            }
        }
        catch (final IllegalAccessException ex) {
            ex.printStackTrace();
        }
    }
    
    private class OSPurchasingListener implements PurchasingListener
    {
        private String currentMarket;
        private RequestId lastRequestId;
        PurchasingListener orgPurchasingListener;
        final TrackAmazonPurchase this$0;
        
        private OSPurchasingListener(final TrackAmazonPurchase this$0) {
            this.this$0 = this$0;
        }
        
        private String marketToCurrencyCode(final String s) {
            s.hashCode();
            final int hashCode = s.hashCode();
            int n = -1;
            switch (hashCode) {
                case 2718: {
                    if (!s.equals((Object)"US")) {
                        break;
                    }
                    n = 9;
                    break;
                }
                case 2374: {
                    if (!s.equals((Object)"JP")) {
                        break;
                    }
                    n = 8;
                    break;
                }
                case 2347: {
                    if (!s.equals((Object)"IT")) {
                        break;
                    }
                    n = 7;
                    break;
                }
                case 2267: {
                    if (!s.equals((Object)"GB")) {
                        break;
                    }
                    n = 6;
                    break;
                }
                case 2252: {
                    if (!s.equals((Object)"FR")) {
                        break;
                    }
                    n = 5;
                    break;
                }
                case 2222: {
                    if (!s.equals((Object)"ES")) {
                        break;
                    }
                    n = 4;
                    break;
                }
                case 2177: {
                    if (!s.equals((Object)"DE")) {
                        break;
                    }
                    n = 3;
                    break;
                }
                case 2142: {
                    if (!s.equals((Object)"CA")) {
                        break;
                    }
                    n = 2;
                    break;
                }
                case 2128: {
                    if (!s.equals((Object)"BR")) {
                        break;
                    }
                    n = 1;
                    break;
                }
                case 2100: {
                    if (!s.equals((Object)"AU")) {
                        break;
                    }
                    n = 0;
                    break;
                }
            }
            switch (n) {
                default: {
                    return "";
                }
                case 9: {
                    return "USD";
                }
                case 8: {
                    return "JPY";
                }
                case 6: {
                    return "GBP";
                }
                case 3:
                case 4:
                case 5:
                case 7: {
                    return "EUR";
                }
                case 2: {
                    return "CDN";
                }
                case 1: {
                    return "BRL";
                }
                case 0: {
                    return "AUD";
                }
            }
        }
        
        public void onProductDataResponse(final ProductDataResponse productDataResponse) {
            final RequestId lastRequestId = this.lastRequestId;
            if (lastRequestId != null && lastRequestId.toString().equals((Object)productDataResponse.getRequestId().toString())) {
                try {
                    if (TrackAmazonPurchase$2.$SwitchMap$com$amazon$device$iap$model$ProductDataResponse$RequestStatus[productDataResponse.getRequestStatus().ordinal()] == 1) {
                        final JSONArray jsonArray = new JSONArray();
                        final Map productData = productDataResponse.getProductData();
                        final Iterator iterator = productData.keySet().iterator();
                        while (iterator.hasNext()) {
                            final Product product = (Product)productData.get((Object)iterator.next());
                            final JSONObject jsonObject = new JSONObject();
                            jsonObject.put("sku", (Object)product.getSku());
                            jsonObject.put("iso", (Object)this.marketToCurrencyCode(this.currentMarket));
                            String s2;
                            final String s = s2 = product.getPrice();
                            if (!s.matches("^[0-9]")) {
                                s2 = s.substring(1);
                            }
                            jsonObject.put("amount", (Object)s2);
                            jsonArray.put((Object)jsonObject);
                        }
                        OneSignal.sendPurchases(jsonArray, false, null);
                    }
                }
                catch (final JSONException ex) {
                    ex.printStackTrace();
                }
            }
            else {
                final PurchasingListener orgPurchasingListener = this.orgPurchasingListener;
                if (orgPurchasingListener != null) {
                    orgPurchasingListener.onProductDataResponse(productDataResponse);
                }
            }
        }
        
        public void onPurchaseResponse(final PurchaseResponse purchaseResponse) {
            if (purchaseResponse.getRequestStatus() == PurchaseResponse$RequestStatus.SUCCESSFUL) {
                this.currentMarket = purchaseResponse.getUserData().getMarketplace();
                final HashSet set = new HashSet();
                ((Set)set).add((Object)purchaseResponse.getReceipt().getSku());
                this.lastRequestId = PurchasingService.getProductData((Set)set);
            }
            final PurchasingListener orgPurchasingListener = this.orgPurchasingListener;
            if (orgPurchasingListener != null) {
                orgPurchasingListener.onPurchaseResponse(purchaseResponse);
            }
        }
        
        public void onPurchaseUpdatesResponse(final PurchaseUpdatesResponse purchaseUpdatesResponse) {
            final PurchasingListener orgPurchasingListener = this.orgPurchasingListener;
            if (orgPurchasingListener != null) {
                orgPurchasingListener.onPurchaseUpdatesResponse(purchaseUpdatesResponse);
            }
        }
        
        public void onUserDataResponse(final UserDataResponse userDataResponse) {
            final PurchasingListener orgPurchasingListener = this.orgPurchasingListener;
            if (orgPurchasingListener != null) {
                orgPurchasingListener.onUserDataResponse(userDataResponse);
            }
        }
    }
}
