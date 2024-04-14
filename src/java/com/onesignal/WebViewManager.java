package com.onesignal;

import android.webkit.JavascriptInterface;
import android.os.Handler;
import android.os.Looper;
import android.os.Build$VERSION;
import android.content.Context;
import java.io.UnsupportedEncodingException;
import android.util.Base64;
import org.json.JSONException;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import org.json.JSONObject;
import android.app.Activity;

class WebViewManager extends ActivityLifecycleHandler$ActivityAvailableListener
{
    private static final int IN_APP_MESSAGE_INIT_DELAY = 200;
    private static final int MARGIN_PX_SIZE;
    private static final String TAG = "com.onesignal.WebViewManager";
    protected static WebViewManager lastInstance;
    private Activity activity;
    private boolean closing;
    private String currentActivityName;
    private boolean dismissFired;
    private Integer lastPageHeight;
    private OSInAppMessageInternal message;
    private OSInAppMessageContent messageContent;
    private InAppMessageView messageView;
    private final Object messageViewSyncLock;
    private OSWebView webView;
    
    static {
        MARGIN_PX_SIZE = OSViewUtils.dpToPx(24);
        WebViewManager.lastInstance = null;
    }
    
    protected WebViewManager(final OSInAppMessageInternal message, final Activity activity, final OSInAppMessageContent messageContent) {
        this.messageViewSyncLock = new Object() {
            final WebViewManager this$0;
        };
        this.currentActivityName = null;
        this.lastPageHeight = null;
        this.dismissFired = false;
        this.closing = false;
        this.message = message;
        this.activity = activity;
        this.messageContent = messageContent;
    }
    
    private void blurryRenderingWebViewForKitKatWorkAround(final WebView webView) {
    }
    
    private void calculateHeightAndShowWebViewAfterNewActivity() {
        final InAppMessageView messageView = this.messageView;
        if (messageView == null) {
            return;
        }
        if (messageView.getDisplayPosition() == Position.FULL_SCREEN && !this.messageContent.isFullBleed()) {
            this.showMessageView(null);
            return;
        }
        OneSignal.Log(OneSignal$LOG_LEVEL.DEBUG, "In app message new activity, calculate height and show ");
        OSViewUtils.decorViewReady(this.activity, (Runnable)new Runnable(this) {
            final WebViewManager this$0;
            
            public void run() {
                final WebViewManager this$0 = this.this$0;
                this$0.setWebViewToMaxSize(this$0.activity);
                if (this.this$0.messageContent.isFullBleed()) {
                    this.this$0.updateSafeAreaInsets();
                }
                this.this$0.webView.evaluateJavascript("getPageMetaData()", (ValueCallback)new ValueCallback<String>(this) {
                    final WebViewManager$6 this$1;
                    
                    public void onReceiveValue(final String s) {
                        try {
                            this.this$1.this$0.showMessageView(this.this$1.this$0.pageRectToViewHeight(this.this$1.this$0.activity, new JSONObject(s)));
                        }
                        catch (final JSONException ex) {
                            ex.printStackTrace();
                        }
                    }
                });
            }
        });
    }
    
    private void createNewInAppMessageView(final boolean b) {
        this.lastPageHeight = this.messageContent.getPageHeight();
        this.setMessageView(new InAppMessageView((WebView)this.webView, this.messageContent, b));
        this.messageView.setMessageController((InAppMessageView$InAppMessageViewListener)new InAppMessageView$InAppMessageViewListener(this) {
            final WebViewManager this$0;
            
            public void onMessageWasDismissed() {
                OneSignal.getInAppMessageController().messageWasDismissed(this.this$0.message);
                this.this$0.removeActivityListener();
            }
            
            public void onMessageWasShown() {
                OneSignal.getInAppMessageController().onMessageWasShown(this.this$0.message);
            }
            
            public void onMessageWillDismiss() {
                OneSignal.getInAppMessageController().onMessageWillDismiss(this.this$0.message);
            }
        });
        final ActivityLifecycleHandler activityLifecycleHandler = ActivityLifecycleListener.getActivityLifecycleHandler();
        if (activityLifecycleHandler != null) {
            final StringBuilder sb = new StringBuilder();
            sb.append(WebViewManager.TAG);
            sb.append(this.message.messageId);
            activityLifecycleHandler.addActivityAvailableListener(sb.toString(), (ActivityLifecycleHandler.ActivityLifecycleHandler$ActivityAvailableListener)this);
        }
    }
    
    static void dismissCurrentInAppMessage() {
        final OneSignal$LOG_LEVEL debug = OneSignal$LOG_LEVEL.DEBUG;
        final StringBuilder sb = new StringBuilder("WebViewManager IAM dismissAndAwaitNextMessage lastInstance: ");
        sb.append((Object)WebViewManager.lastInstance);
        OneSignal.onesignalLog(debug, sb.toString());
        final WebViewManager lastInstance = WebViewManager.lastInstance;
        if (lastInstance != null) {
            lastInstance.dismissAndAwaitNextMessage(null);
        }
    }
    
    private static void enableWebViewRemoteDebugging() {
        if (OneSignal.atLogLevel(OneSignal$LOG_LEVEL.DEBUG)) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
    }
    
    private int getWebViewMaxSizeX(final Activity activity) {
        if (this.messageContent.isFullBleed()) {
            return OSViewUtils.getFullbleedWindowWidth(activity);
        }
        return OSViewUtils.getWindowWidth(activity) - WebViewManager.MARGIN_PX_SIZE * 2;
    }
    
    private int getWebViewMaxSizeY(final Activity activity) {
        int n;
        if (this.messageContent.isFullBleed()) {
            n = 0;
        }
        else {
            n = WebViewManager.MARGIN_PX_SIZE * 2;
        }
        return OSViewUtils.getWindowHeight(activity) - n;
    }
    
    private static void initInAppMessage(final Activity activity, final OSInAppMessageInternal osInAppMessageInternal, final OSInAppMessageContent osInAppMessageContent) {
        if (osInAppMessageContent.isFullBleed()) {
            setContentSafeAreaInsets(osInAppMessageContent, activity);
        }
        try {
            OSUtils.runOnMainUIThread((Runnable)new Runnable(WebViewManager.lastInstance = new WebViewManager(osInAppMessageInternal, activity, osInAppMessageContent), activity, Base64.encodeToString(osInAppMessageContent.getContentHtml().getBytes("UTF-8"), 2), osInAppMessageContent) {
                final String val$base64Str;
                final OSInAppMessageContent val$content;
                final Activity val$currentActivity;
                final WebViewManager val$webViewManager;
                
                public void run() {
                    try {
                        this.val$webViewManager.setupWebView(this.val$currentActivity, this.val$base64Str, this.val$content.isFullBleed());
                    }
                    catch (final Exception ex) {
                        if (ex.getMessage() == null || !ex.getMessage().contains((CharSequence)"No WebView installed")) {
                            throw ex;
                        }
                        OneSignal.Log(OneSignal$LOG_LEVEL.ERROR, "Error setting up WebView: ", (Throwable)ex);
                    }
                }
            });
        }
        catch (final UnsupportedEncodingException ex) {
            OneSignal.Log(OneSignal$LOG_LEVEL.ERROR, "Catch on initInAppMessage: ", (Throwable)ex);
            ex.printStackTrace();
        }
    }
    
    private int pageRectToViewHeight(final Activity activity, final JSONObject jsonObject) {
        try {
            final int dpToPx = OSViewUtils.dpToPx(jsonObject.getJSONObject("rect").getInt("height"));
            final OneSignal$LOG_LEVEL debug = OneSignal$LOG_LEVEL.DEBUG;
            final StringBuilder sb = new StringBuilder("getPageHeightData:pxHeight: ");
            sb.append(dpToPx);
            OneSignal.onesignalLog(debug, sb.toString());
            final int webViewMaxSizeY = this.getWebViewMaxSizeY(activity);
            int n = dpToPx;
            if (dpToPx > webViewMaxSizeY) {
                final OneSignal$LOG_LEVEL debug2 = OneSignal$LOG_LEVEL.DEBUG;
                final StringBuilder sb2 = new StringBuilder("getPageHeightData:pxHeight is over screen max: ");
                sb2.append(webViewMaxSizeY);
                OneSignal.Log(debug2, sb2.toString());
                n = webViewMaxSizeY;
            }
            return n;
        }
        catch (final JSONException ex) {
            OneSignal.Log(OneSignal$LOG_LEVEL.ERROR, "pageRectToViewHeight could not get page height", (Throwable)ex);
            return -1;
        }
    }
    
    private void removeActivityListener() {
        final ActivityLifecycleHandler activityLifecycleHandler = ActivityLifecycleListener.getActivityLifecycleHandler();
        if (activityLifecycleHandler != null) {
            final StringBuilder sb = new StringBuilder();
            sb.append(WebViewManager.TAG);
            sb.append(this.message.messageId);
            activityLifecycleHandler.removeActivityAvailableListener(sb.toString());
        }
    }
    
    private static void setContentSafeAreaInsets(final OSInAppMessageContent osInAppMessageContent, final Activity activity) {
        final String contentHtml = osInAppMessageContent.getContentHtml();
        final int[] cutoutAndStatusBarInsets = OSViewUtils.getCutoutAndStatusBarInsets(activity);
        final String format = String.format("\n\n<script>\n    setSafeAreaInsets(%s);\n</script>", new Object[] { String.format("{\n   top: %d,\n   bottom: %d,\n   right: %d,\n   left: %d,\n}", new Object[] { cutoutAndStatusBarInsets[0], cutoutAndStatusBarInsets[1], cutoutAndStatusBarInsets[2], cutoutAndStatusBarInsets[3] }) });
        final StringBuilder sb = new StringBuilder();
        sb.append(contentHtml);
        sb.append(format);
        osInAppMessageContent.setContentHtml(sb.toString());
    }
    
    private void setMessageView(final InAppMessageView messageView) {
        final Object messageViewSyncLock = this.messageViewSyncLock;
        synchronized (messageViewSyncLock) {
            this.messageView = messageView;
        }
    }
    
    private void setWebViewToMaxSize(final Activity activity) {
        this.webView.layout(0, 0, this.getWebViewMaxSizeX(activity), this.getWebViewMaxSizeY(activity));
    }
    
    private void setupWebView(final Activity activity, final String s, final boolean b) {
        enableWebViewRemoteDebugging();
        (this.webView = new OSWebView((Context)activity)).setOverScrollMode(2);
        this.webView.setVerticalScrollBarEnabled(false);
        this.webView.setHorizontalScrollBarEnabled(false);
        this.webView.getSettings().setJavaScriptEnabled(true);
        this.webView.addJavascriptInterface((Object)new OSJavaScriptInterface(), "OSAndroid");
        if (b) {
            this.webView.setSystemUiVisibility(3074);
            if (Build$VERSION.SDK_INT >= 30) {
                this.webView.setFitsSystemWindows(false);
            }
        }
        this.blurryRenderingWebViewForKitKatWorkAround((WebView)this.webView);
        OSViewUtils.decorViewReady(activity, (Runnable)new Runnable(this, activity, s) {
            final WebViewManager this$0;
            final String val$base64Message;
            final Activity val$currentActivity;
            
            public void run() {
                this.this$0.setWebViewToMaxSize(this.val$currentActivity);
                this.this$0.webView.loadData(this.val$base64Message, "text/html; charset=utf-8", "base64");
            }
        });
    }
    
    static void showMessageContent(final OSInAppMessageInternal osInAppMessageInternal, final OSInAppMessageContent osInAppMessageContent) {
        final Activity currentActivity = OneSignal.getCurrentActivity();
        final OneSignal$LOG_LEVEL debug = OneSignal$LOG_LEVEL.DEBUG;
        final StringBuilder sb = new StringBuilder("in app message showMessageContent on currentActivity: ");
        sb.append((Object)currentActivity);
        OneSignal.onesignalLog(debug, sb.toString());
        if (currentActivity != null) {
            if (WebViewManager.lastInstance != null && osInAppMessageInternal.isPreview) {
                WebViewManager.lastInstance.dismissAndAwaitNextMessage((OneSignalGenericCallback)new OneSignalGenericCallback(currentActivity, osInAppMessageInternal, osInAppMessageContent) {
                    final OSInAppMessageContent val$content;
                    final Activity val$currentActivity;
                    final OSInAppMessageInternal val$message;
                    
                    @Override
                    public void onComplete() {
                        WebViewManager.lastInstance = null;
                        initInAppMessage(this.val$currentActivity, this.val$message, this.val$content);
                    }
                });
            }
            else {
                initInAppMessage(currentActivity, osInAppMessageInternal, osInAppMessageContent);
            }
            return;
        }
        Looper.prepare();
        new Handler().postDelayed((Runnable)new Runnable(osInAppMessageInternal, osInAppMessageContent) {
            final OSInAppMessageContent val$content;
            final OSInAppMessageInternal val$message;
            
            public void run() {
                WebViewManager.showMessageContent(this.val$message, this.val$content);
            }
        }, 200L);
    }
    
    private void showMessageView(final Integer lastPageHeight) {
        final Object messageViewSyncLock = this.messageViewSyncLock;
        synchronized (messageViewSyncLock) {
            if (this.messageView == null) {
                OneSignal.Log(OneSignal$LOG_LEVEL.WARN, "No messageView found to update a with a new height.");
                return;
            }
            final OneSignal$LOG_LEVEL debug = OneSignal$LOG_LEVEL.DEBUG;
            final StringBuilder sb = new StringBuilder("In app message, showing first one with height: ");
            sb.append((Object)lastPageHeight);
            OneSignal.Log(debug, sb.toString());
            this.messageView.setWebView((WebView)this.webView);
            if (lastPageHeight != null) {
                this.lastPageHeight = lastPageHeight;
                this.messageView.updateHeight((int)lastPageHeight);
            }
            this.messageView.showView(this.activity);
            this.messageView.checkIfShouldDismiss();
        }
    }
    
    private void updateSafeAreaInsets() {
        OSUtils.runOnMainUIThread((Runnable)new Runnable(this) {
            final WebViewManager this$0;
            
            public void run() {
                final int[] cutoutAndStatusBarInsets = OSViewUtils.getCutoutAndStatusBarInsets(this.this$0.activity);
                this.this$0.webView.evaluateJavascript(String.format("setSafeAreaInsets(%s)", new Object[] { String.format("{\n   top: %d,\n   bottom: %d,\n   right: %d,\n   left: %d,\n}", new Object[] { cutoutAndStatusBarInsets[0], cutoutAndStatusBarInsets[1], cutoutAndStatusBarInsets[2], cutoutAndStatusBarInsets[3] }) }), (ValueCallback)null);
            }
        });
    }
    
    void available(final Activity activity) {
        final String currentActivityName = this.currentActivityName;
        this.activity = activity;
        this.currentActivityName = activity.getLocalClassName();
        final OneSignal$LOG_LEVEL debug = OneSignal$LOG_LEVEL.DEBUG;
        final StringBuilder sb = new StringBuilder("In app message activity available currentActivityName: ");
        sb.append(this.currentActivityName);
        sb.append(" lastActivityName: ");
        sb.append(currentActivityName);
        OneSignal.Log(debug, sb.toString());
        if (currentActivityName == null) {
            this.showMessageView(null);
        }
        else if (!currentActivityName.equals((Object)this.currentActivityName)) {
            if (!this.closing) {
                final InAppMessageView messageView = this.messageView;
                if (messageView != null) {
                    messageView.removeAllViews();
                }
                this.showMessageView(this.lastPageHeight);
            }
        }
        else {
            this.calculateHeightAndShowWebViewAfterNewActivity();
        }
    }
    
    protected void dismissAndAwaitNextMessage(final OneSignalGenericCallback oneSignalGenericCallback) {
        final InAppMessageView messageView = this.messageView;
        if (messageView != null && !this.dismissFired) {
            if (this.message != null && messageView != null) {
                OneSignal.getInAppMessageController().onMessageWillDismiss(this.message);
            }
            this.messageView.dismissAndAwaitNextMessage((OneSignalGenericCallback)new OneSignalGenericCallback(this, oneSignalGenericCallback) {
                final WebViewManager this$0;
                final OneSignalGenericCallback val$callback;
                
                @Override
                public void onComplete() {
                    this.this$0.dismissFired = false;
                    this.this$0.setMessageView(null);
                    final OneSignalGenericCallback val$callback = this.val$callback;
                    if (val$callback != null) {
                        val$callback.onComplete();
                    }
                }
            });
            this.dismissFired = true;
            return;
        }
        if (oneSignalGenericCallback != null) {
            oneSignalGenericCallback.onComplete();
        }
    }
    
    void stopped(final Activity activity) {
        final OneSignal$LOG_LEVEL debug = OneSignal$LOG_LEVEL.DEBUG;
        final StringBuilder sb = new StringBuilder("In app message activity stopped, cleaning views, currentActivityName: ");
        sb.append(this.currentActivityName);
        sb.append("\nactivity: ");
        sb.append((Object)this.activity);
        sb.append("\nmessageView: ");
        sb.append((Object)this.messageView);
        OneSignal.Log(debug, sb.toString());
        if (this.messageView != null && activity.getLocalClassName().equals((Object)this.currentActivityName)) {
            this.messageView.removeAllViews();
        }
    }
    
    class OSJavaScriptInterface
    {
        static final String EVENT_TYPE_ACTION_TAKEN = "action_taken";
        static final String EVENT_TYPE_KEY = "type";
        static final String EVENT_TYPE_PAGE_CHANGE = "page_change";
        static final String EVENT_TYPE_RENDERING_COMPLETE = "rendering_complete";
        static final String EVENT_TYPE_RESIZE = "resize";
        static final String GET_PAGE_META_DATA_JS_FUNCTION = "getPageMetaData()";
        static final String IAM_DISPLAY_LOCATION_KEY = "displayLocation";
        static final String IAM_DRAG_TO_DISMISS_DISABLED_KEY = "dragToDismissDisabled";
        static final String IAM_PAGE_META_DATA_KEY = "pageMetaData";
        static final String JS_OBJ_NAME = "OSAndroid";
        static final String SAFE_AREA_JS_OBJECT = "{\n   top: %d,\n   bottom: %d,\n   right: %d,\n   left: %d,\n}";
        static final String SET_SAFE_AREA_INSETS_JS_FUNCTION = "setSafeAreaInsets(%s)";
        static final String SET_SAFE_AREA_INSETS_SCRIPT = "\n\n<script>\n    setSafeAreaInsets(%s);\n</script>";
        final WebViewManager this$0;
        
        OSJavaScriptInterface(final WebViewManager this$0) {
            this.this$0 = this$0;
        }
        
        private Position getDisplayLocation(final JSONObject jsonObject) {
            Enum<Position> enum1;
            final Position position = (Position)(enum1 = Position.FULL_SCREEN);
            try {
                if (jsonObject.has("displayLocation")) {
                    enum1 = position;
                    if (!jsonObject.get("displayLocation").equals("")) {
                        enum1 = Position.valueOf(jsonObject.optString("displayLocation", "FULL_SCREEN").toUpperCase());
                    }
                }
            }
            catch (final JSONException ex) {
                ex.printStackTrace();
                enum1 = position;
            }
            return (Position)enum1;
        }
        
        private boolean getDragToDismissDisabled(final JSONObject jsonObject) {
            try {
                return jsonObject.getBoolean("dragToDismissDisabled");
            }
            catch (final JSONException ex) {
                return false;
            }
        }
        
        private int getPageHeightData(final JSONObject jsonObject) {
            try {
                final WebViewManager this$0 = this.this$0;
                return this$0.pageRectToViewHeight(this$0.activity, jsonObject.getJSONObject("pageMetaData"));
            }
            catch (final JSONException ex) {
                return -1;
            }
        }
        
        private void handleActionTaken(final JSONObject jsonObject) throws JSONException {
            final JSONObject jsonObject2 = jsonObject.getJSONObject("body");
            final String optString = jsonObject2.optString("id", (String)null);
            this.this$0.closing = jsonObject2.getBoolean("close");
            if (this.this$0.message.isPreview) {
                OneSignal.getInAppMessageController().onMessageActionOccurredOnPreview(this.this$0.message, jsonObject2);
            }
            else if (optString != null) {
                OneSignal.getInAppMessageController().onMessageActionOccurredOnMessage(this.this$0.message, jsonObject2);
            }
            if (this.this$0.closing) {
                this.this$0.dismissAndAwaitNextMessage(null);
            }
        }
        
        private void handlePageChange(final JSONObject jsonObject) throws JSONException {
            OneSignal.getInAppMessageController().onPageChanged(this.this$0.message, jsonObject);
        }
        
        private void handleRenderComplete(final JSONObject jsonObject) {
            final Position displayLocation = this.getDisplayLocation(jsonObject);
            int pageHeightData;
            if (displayLocation == Position.FULL_SCREEN) {
                pageHeightData = -1;
            }
            else {
                pageHeightData = this.getPageHeightData(jsonObject);
            }
            final boolean dragToDismissDisabled = this.getDragToDismissDisabled(jsonObject);
            this.this$0.messageContent.setDisplayLocation(displayLocation);
            this.this$0.messageContent.setPageHeight(pageHeightData);
            this.this$0.createNewInAppMessageView(dragToDismissDisabled);
        }
        
        @JavascriptInterface
        public void postMessage(String string) {
            try {
                final OneSignal$LOG_LEVEL debug = OneSignal$LOG_LEVEL.DEBUG;
                final StringBuilder sb = new StringBuilder("OSJavaScriptInterface:postMessage: ");
                sb.append(string);
                OneSignal.onesignalLog(debug, sb.toString());
                final JSONObject jsonObject = new JSONObject(string);
                string = jsonObject.getString("type");
                int n = 0;
                Label_0157: {
                    switch (string.hashCode()) {
                        case 1851145598: {
                            if (string.equals((Object)"action_taken")) {
                                n = 1;
                                break Label_0157;
                            }
                            break;
                        }
                        case 42998156: {
                            if (string.equals((Object)"rendering_complete")) {
                                n = 0;
                                break Label_0157;
                            }
                            break;
                        }
                        case -934437708: {
                            if (string.equals((Object)"resize")) {
                                n = 2;
                                break Label_0157;
                            }
                            break;
                        }
                        case -1484226720: {
                            if (string.equals((Object)"page_change")) {
                                n = 3;
                                break Label_0157;
                            }
                            break;
                        }
                    }
                    n = -1;
                }
                if (n != 0) {
                    if (n != 1) {
                        if (n == 3) {
                            this.handlePageChange(jsonObject);
                        }
                    }
                    else if (!this.this$0.messageView.isDragging()) {
                        this.handleActionTaken(jsonObject);
                    }
                }
                else {
                    this.handleRenderComplete(jsonObject);
                }
            }
            catch (final JSONException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    interface OneSignalGenericCallback
    {
        void onComplete();
    }
    
    enum Position
    {
        private static final Position[] $VALUES;
        
        BOTTOM_BANNER, 
        CENTER_MODAL, 
        FULL_SCREEN, 
        TOP_BANNER;
        
        boolean isBanner() {
            final int n = WebViewManager$10.$SwitchMap$com$onesignal$WebViewManager$Position[this.ordinal()];
            return n == 1 || n == 2;
        }
    }
}
