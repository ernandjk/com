package com.onesignal;

import com.google.firebase.FirebaseOptions$Builder;
import com.google.android.gms.tasks.Task;
import java.util.concurrent.ExecutionException;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.messaging.FirebaseMessaging;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import com.google.firebase.FirebaseApp;
import android.content.Context;

class PushRegistratorFCM extends PushRegistratorAbstractGoogle
{
    private static final String FCM_APP_NAME = "ONESIGNAL_SDK_FCM_APP_NAME";
    private final Context context;
    private FirebaseApp firebaseApp;
    private final PushRegistratorFCM.PushRegistratorFCM$Params params;
    
    PushRegistratorFCM(final Context context, final PushRegistratorFCM.PushRegistratorFCM$Params params) {
        this.context = context;
        if (params == null) {
            this.params = new PushRegistratorFCM.PushRegistratorFCM$Params();
        }
        else {
            this.params = params;
        }
    }
    
    @Deprecated
    private String getTokenWithClassFirebaseInstanceId(String o) throws IOException {
        try {
            final Object invoke = Class.forName("com.google.firebase.iid.FirebaseInstanceId").getMethod("getInstance", FirebaseApp.class).invoke((Object)null, new Object[] { this.firebaseApp });
            o = invoke.getClass().getMethod("getToken", String.class, String.class).invoke(invoke, new Object[] { o, "FCM" });
            return (String)o;
        }
        catch (final InvocationTargetException o) {}
        catch (final IllegalAccessException o) {}
        catch (final NoSuchMethodException o) {}
        catch (final ClassNotFoundException ex) {}
        throw new Error("Reflection error on FirebaseInstanceId.getInstance(firebaseApp).getToken(senderId, FirebaseMessaging.INSTANCE_ID_SCOPE)", (Throwable)o);
    }
    
    private String getTokenWithClassFirebaseMessaging() throws Exception {
        final Task token = ((FirebaseMessaging)this.firebaseApp.get((Class)FirebaseMessaging.class)).getToken();
        try {
            return (String)Tasks.await(token);
        }
        catch (final ExecutionException ex) {
            throw token.getException();
        }
    }
    
    private void initFirebaseApp(final String gcmSenderId) {
        if (this.firebaseApp != null) {
            return;
        }
        this.firebaseApp = FirebaseApp.initializeApp(this.context, new FirebaseOptions$Builder().setGcmSenderId(gcmSenderId).setApplicationId(PushRegistratorFCM.PushRegistratorFCM$Params.access$200(this.params)).setApiKey(PushRegistratorFCM.PushRegistratorFCM$Params.access$100(this.params)).setProjectId(PushRegistratorFCM.PushRegistratorFCM$Params.access$000(this.params)).build(), "ONESIGNAL_SDK_FCM_APP_NAME");
    }
    
    @Override
    String getProviderName() {
        return "FCM";
    }
    
    @Override
    String getToken(final String s) throws Exception {
        this.initFirebaseApp(s);
        try {
            return this.getTokenWithClassFirebaseMessaging();
        }
        catch (final NoClassDefFoundError | NoSuchMethodError noClassDefFoundError | NoSuchMethodError) {
            OneSignal.Log(OneSignal$LOG_LEVEL.INFO, "FirebaseMessaging.getToken not found, attempting to use FirebaseInstanceId.getToken");
            return this.getTokenWithClassFirebaseInstanceId(s);
        }
    }
}
