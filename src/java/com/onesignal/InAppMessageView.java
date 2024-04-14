package com.onesignal;

import android.view.ViewGroup;
import androidx.core.widget.PopupWindowCompat;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ColorDrawable;
import android.view.ViewGroup$LayoutParams;
import android.os.Build$VERSION;
import androidx.cardview.widget.CardView;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.Animation$AnimationListener;
import android.animation.ValueAnimator;
import android.animation.Animator$AnimatorListener;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.widget.RelativeLayout$LayoutParams;
import android.content.Context;
import android.view.View;
import android.graphics.Color;
import android.webkit.WebView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.os.Handler;
import android.app.Activity;

class InAppMessageView
{
    private static final int ACTIVITY_BACKGROUND_COLOR_EMPTY;
    private static final int ACTIVITY_BACKGROUND_COLOR_FULL;
    private static final int ACTIVITY_FINISH_AFTER_DISMISS_DELAY_MS = 600;
    private static final int ACTIVITY_INIT_DELAY = 200;
    private static final int DRAG_THRESHOLD_PX_SIZE;
    private static final int IN_APP_BACKGROUND_ANIMATION_DURATION_MS = 400;
    private static final int IN_APP_BANNER_ANIMATION_DURATION_MS = 1000;
    private static final int IN_APP_CENTER_ANIMATION_DURATION_MS = 1000;
    private static final String IN_APP_MESSAGE_CARD_VIEW_TAG = "IN_APP_MESSAGE_CARD_VIEW_TAG";
    private Activity currentActivity;
    private boolean disableDragDismiss;
    private double displayDuration;
    private WebViewManager$Position displayLocation;
    private DraggableRelativeLayout draggableRelativeLayout;
    private final Handler handler;
    private boolean hasBackground;
    private boolean isDragging;
    private int marginPxSizeBottom;
    private int marginPxSizeLeft;
    private int marginPxSizeRight;
    private int marginPxSizeTop;
    private OSInAppMessageContent messageContent;
    private InAppMessageViewListener messageController;
    private int pageHeight;
    private int pageWidth;
    private RelativeLayout parentRelativeLayout;
    private PopupWindow popupWindow;
    private Runnable scheduleDismissRunnable;
    private boolean shouldDismissWhenActive;
    private WebView webView;
    
    static {
        ACTIVITY_BACKGROUND_COLOR_EMPTY = Color.parseColor("#00000000");
        ACTIVITY_BACKGROUND_COLOR_FULL = Color.parseColor("#BB000000");
        DRAG_THRESHOLD_PX_SIZE = OSViewUtils.dpToPx(4);
    }
    
    InAppMessageView(final WebView webView, final OSInAppMessageContent messageContent, final boolean disableDragDismiss) {
        this.handler = new Handler();
        this.marginPxSizeLeft = OSViewUtils.dpToPx(24);
        this.marginPxSizeRight = OSViewUtils.dpToPx(24);
        this.marginPxSizeTop = OSViewUtils.dpToPx(24);
        this.marginPxSizeBottom = OSViewUtils.dpToPx(24);
        this.shouldDismissWhenActive = false;
        this.isDragging = false;
        this.disableDragDismiss = false;
        this.webView = webView;
        this.displayLocation = messageContent.getDisplayLocation();
        this.pageHeight = messageContent.getPageHeight();
        this.pageWidth = -1;
        double doubleValue;
        if (messageContent.getDisplayDuration() == null) {
            doubleValue = 0.0;
        }
        else {
            doubleValue = messageContent.getDisplayDuration();
        }
        this.displayDuration = doubleValue;
        this.hasBackground = (this.displayLocation.isBanner() ^ true);
        this.disableDragDismiss = disableDragDismiss;
        this.setMarginsFromContent(this.messageContent = messageContent);
    }
    
    private void animateAndDismissLayout(final View view, final WebViewManager$OneSignalGenericCallback webViewManager$OneSignalGenericCallback) {
        this.animateBackgroundColor(view, 400, InAppMessageView.ACTIVITY_BACKGROUND_COLOR_FULL, InAppMessageView.ACTIVITY_BACKGROUND_COLOR_EMPTY, (Animator$AnimatorListener)new AnimatorListenerAdapter(this, webViewManager$OneSignalGenericCallback) {
            final InAppMessageView this$0;
            final WebViewManager$OneSignalGenericCallback val$callback;
            
            public void onAnimationEnd(final Animator animator) {
                this.this$0.cleanupViewsAfterDismiss();
                final WebViewManager$OneSignalGenericCallback val$callback = this.val$callback;
                if (val$callback != null) {
                    val$callback.onComplete();
                }
            }
        }).start();
    }
    
    private ValueAnimator animateBackgroundColor(final View view, final int n, final int n2, final int n3, final Animator$AnimatorListener animator$AnimatorListener) {
        return OneSignalAnimate.animateViewColor(view, n, n2, n3, animator$AnimatorListener);
    }
    
    private void animateBottom(final View view, final int n, final Animation$AnimationListener animation$AnimationListener) {
        OneSignalAnimate.animateViewByTranslation(view, (float)(n + this.marginPxSizeBottom), 0.0f, 1000, (Interpolator)new OneSignalBounceInterpolator(0.1, 8.0), animation$AnimationListener).start();
    }
    
    private void animateCenter(final View view, final View view2, final Animation$AnimationListener animation$AnimationListener, final Animator$AnimatorListener animator$AnimatorListener) {
        final Animation animateViewSmallToLarge = OneSignalAnimate.animateViewSmallToLarge(view, 1000, (Interpolator)new OneSignalBounceInterpolator(0.1, 8.0), animation$AnimationListener);
        final ValueAnimator animateBackgroundColor = this.animateBackgroundColor(view2, 400, InAppMessageView.ACTIVITY_BACKGROUND_COLOR_EMPTY, InAppMessageView.ACTIVITY_BACKGROUND_COLOR_FULL, animator$AnimatorListener);
        animateViewSmallToLarge.start();
        animateBackgroundColor.start();
    }
    
    private void animateInAppMessage(final WebViewManager$Position webViewManager$Position, final View view, final View view2) {
        final CardView cardView = (CardView)view.findViewWithTag((Object)"IN_APP_MESSAGE_CARD_VIEW_TAG");
        final Animation$AnimationListener animationListener = this.createAnimationListener(cardView);
        final int n = InAppMessageView$9.$SwitchMap$com$onesignal$WebViewManager$Position[webViewManager$Position.ordinal()];
        if (n != 1) {
            if (n != 2) {
                if (n == 3 || n == 4) {
                    this.animateCenter(view, view2, animationListener, null);
                }
            }
            else {
                this.animateBottom((View)cardView, this.webView.getHeight(), animationListener);
            }
        }
        else {
            this.animateTop((View)cardView, this.webView.getHeight(), animationListener);
        }
    }
    
    private void animateTop(final View view, final int n, final Animation$AnimationListener animation$AnimationListener) {
        OneSignalAnimate.animateViewByTranslation(view, (float)(-n - this.marginPxSizeTop), 0.0f, 1000, (Interpolator)new OneSignalBounceInterpolator(0.1, 8.0), animation$AnimationListener).start();
    }
    
    private void cleanupViewsAfterDismiss() {
        this.removeAllViews();
        final InAppMessageViewListener messageController = this.messageController;
        if (messageController != null) {
            messageController.onMessageWasDismissed();
        }
    }
    
    private Animation$AnimationListener createAnimationListener(final CardView cardView) {
        return (Animation$AnimationListener)new Animation$AnimationListener(this, cardView) {
            final InAppMessageView this$0;
            final CardView val$messageViewCardView;
            
            public void onAnimationEnd(final Animation animation) {
                if (Build$VERSION.SDK_INT == 23) {
                    this.val$messageViewCardView.setCardElevation((float)OSViewUtils.dpToPx(5));
                }
                if (this.this$0.messageController != null) {
                    this.this$0.messageController.onMessageWasShown();
                }
            }
            
            public void onAnimationRepeat(final Animation animation) {
            }
            
            public void onAnimationStart(final Animation animation) {
            }
        };
    }
    
    private CardView createCardView(final Context context) {
        final CardView cardView = new CardView(context);
        int n;
        if (this.displayLocation == WebViewManager$Position.FULL_SCREEN) {
            n = -1;
        }
        else {
            n = -2;
        }
        final RelativeLayout$LayoutParams layoutParams = new RelativeLayout$LayoutParams(-1, n);
        layoutParams.addRule(13);
        cardView.setLayoutParams((ViewGroup$LayoutParams)layoutParams);
        if (Build$VERSION.SDK_INT == 23) {
            cardView.setCardElevation(0.0f);
        }
        else {
            cardView.setCardElevation((float)OSViewUtils.dpToPx(5));
        }
        cardView.setRadius((float)OSViewUtils.dpToPx(8));
        cardView.setClipChildren(false);
        cardView.setClipToPadding(false);
        cardView.setPreventCornerOverlap(false);
        cardView.setCardBackgroundColor(0);
        return cardView;
    }
    
    private DraggableRelativeLayout.Params createDraggableLayoutParams(int dragDirection, final WebViewManager$Position webViewManager$Position, final boolean draggingDisabled) {
        final DraggableRelativeLayout.Params params = new DraggableRelativeLayout.Params();
        params.maxXPos = this.marginPxSizeRight;
        params.maxYPos = this.marginPxSizeTop;
        params.draggingDisabled = draggingDisabled;
        params.messageHeight = dragDirection;
        params.height = this.getDisplayYSize();
        final int n = InAppMessageView$9.$SwitchMap$com$onesignal$WebViewManager$Position[webViewManager$Position.ordinal()];
        final int n2 = 1;
        Label_0185: {
            if (n != 1) {
                if (n != 2) {
                    if (n != 3) {
                        if (n != 4) {
                            break Label_0185;
                        }
                        dragDirection = this.getDisplayYSize() - (this.marginPxSizeBottom + this.marginPxSizeTop);
                        params.messageHeight = dragDirection;
                    }
                    dragDirection = this.getDisplayYSize() / 2 - dragDirection / 2;
                    params.dragThresholdY = InAppMessageView.DRAG_THRESHOLD_PX_SIZE + dragDirection;
                    params.maxYPos = dragDirection;
                    params.posY = dragDirection;
                }
                else {
                    params.posY = this.getDisplayYSize() - dragDirection;
                    params.dragThresholdY = this.marginPxSizeBottom + InAppMessageView.DRAG_THRESHOLD_PX_SIZE;
                }
            }
            else {
                params.dragThresholdY = this.marginPxSizeTop - InAppMessageView.DRAG_THRESHOLD_PX_SIZE;
            }
        }
        dragDirection = n2;
        if (webViewManager$Position == WebViewManager$Position.TOP_BANNER) {
            dragDirection = 0;
        }
        params.dragDirection = dragDirection;
        return params;
    }
    
    private RelativeLayout$LayoutParams createParentRelativeLayoutParams() {
        final RelativeLayout$LayoutParams relativeLayout$LayoutParams = new RelativeLayout$LayoutParams(this.pageWidth, -1);
        final int n = InAppMessageView$9.$SwitchMap$com$onesignal$WebViewManager$Position[this.displayLocation.ordinal()];
        if (n != 1) {
            if (n != 2) {
                if (n == 3 || n == 4) {
                    relativeLayout$LayoutParams.addRule(13);
                }
            }
            else {
                relativeLayout$LayoutParams.addRule(12);
                relativeLayout$LayoutParams.addRule(14);
            }
        }
        else {
            relativeLayout$LayoutParams.addRule(10);
            relativeLayout$LayoutParams.addRule(14);
        }
        return relativeLayout$LayoutParams;
    }
    
    private void createPopupWindow(final RelativeLayout relativeLayout) {
        final boolean hasBackground = this.hasBackground;
        int n = -1;
        int pageWidth;
        if (hasBackground) {
            pageWidth = -1;
        }
        else {
            pageWidth = this.pageWidth;
        }
        if (!hasBackground) {
            n = -2;
        }
        final int n2 = 1;
        (this.popupWindow = new PopupWindow((View)relativeLayout, pageWidth, n, true)).setBackgroundDrawable((Drawable)new ColorDrawable(0));
        this.popupWindow.setTouchable(true);
        this.popupWindow.setClippingEnabled(false);
        int n4 = 0;
        Label_0144: {
            if (!this.hasBackground) {
                final int n3 = InAppMessageView$9.$SwitchMap$com$onesignal$WebViewManager$Position[this.displayLocation.ordinal()];
                if (n3 == 1) {
                    n4 = 49;
                    break Label_0144;
                }
                if (n3 == 2) {
                    n4 = 81;
                    break Label_0144;
                }
                n4 = n2;
                if (n3 == 3) {
                    break Label_0144;
                }
                n4 = n2;
                if (n3 == 4) {
                    break Label_0144;
                }
            }
            n4 = 0;
        }
        int n5;
        if (this.messageContent.isFullBleed()) {
            n5 = 1000;
        }
        else {
            n5 = 1003;
        }
        PopupWindowCompat.setWindowLayoutType(this.popupWindow, n5);
        this.popupWindow.showAtLocation(this.currentActivity.getWindow().getDecorView().getRootView(), n4, 0, 0);
    }
    
    private void delayShowUntilAvailable(final Activity activity) {
        if (OSViewUtils.isActivityFullyReady(activity) && this.parentRelativeLayout == null) {
            this.showInAppMessageView(activity);
            return;
        }
        new Handler().postDelayed((Runnable)new Runnable(this, activity) {
            final InAppMessageView this$0;
            final Activity val$currentActivity;
            
            public void run() {
                this.this$0.delayShowUntilAvailable(this.val$currentActivity);
            }
        }, 200L);
    }
    
    private void dereferenceViews() {
        this.parentRelativeLayout = null;
        this.draggableRelativeLayout = null;
        this.webView = null;
    }
    
    private void finishAfterDelay(final WebViewManager$OneSignalGenericCallback webViewManager$OneSignalGenericCallback) {
        OSUtils.runOnMainThreadDelayed((Runnable)new Runnable(this, webViewManager$OneSignalGenericCallback) {
            final InAppMessageView this$0;
            final WebViewManager$OneSignalGenericCallback val$callback;
            
            public void run() {
                if (this.this$0.hasBackground && this.this$0.parentRelativeLayout != null) {
                    final InAppMessageView this$0 = this.this$0;
                    this$0.animateAndDismissLayout((View)this$0.parentRelativeLayout, this.val$callback);
                }
                else {
                    this.this$0.cleanupViewsAfterDismiss();
                    final WebViewManager$OneSignalGenericCallback val$callback = this.val$callback;
                    if (val$callback != null) {
                        val$callback.onComplete();
                    }
                }
            }
        }, 600);
    }
    
    private int getDisplayYSize() {
        return OSViewUtils.getWindowHeight(this.currentActivity);
    }
    
    private void setMarginsFromContent(final OSInAppMessageContent osInAppMessageContent) {
        final boolean useHeightMargin = osInAppMessageContent.getUseHeightMargin();
        final int n = 0;
        int dpToPx;
        if (useHeightMargin) {
            dpToPx = OSViewUtils.dpToPx(24);
        }
        else {
            dpToPx = 0;
        }
        this.marginPxSizeTop = dpToPx;
        int dpToPx2;
        if (osInAppMessageContent.getUseHeightMargin()) {
            dpToPx2 = OSViewUtils.dpToPx(24);
        }
        else {
            dpToPx2 = 0;
        }
        this.marginPxSizeBottom = dpToPx2;
        int dpToPx3;
        if (osInAppMessageContent.getUseWidthMargin()) {
            dpToPx3 = OSViewUtils.dpToPx(24);
        }
        else {
            dpToPx3 = 0;
        }
        this.marginPxSizeLeft = dpToPx3;
        int dpToPx4 = n;
        if (osInAppMessageContent.getUseWidthMargin()) {
            dpToPx4 = OSViewUtils.dpToPx(24);
        }
        this.marginPxSizeRight = dpToPx4;
    }
    
    private void setUpDraggableLayout(final Context context, final RelativeLayout$LayoutParams layoutParams, final DraggableRelativeLayout.Params params) {
        final DraggableRelativeLayout draggableRelativeLayout = new DraggableRelativeLayout(context);
        this.draggableRelativeLayout = draggableRelativeLayout;
        if (layoutParams != null) {
            draggableRelativeLayout.setLayoutParams((ViewGroup$LayoutParams)layoutParams);
        }
        this.draggableRelativeLayout.setParams(params);
        this.draggableRelativeLayout.setListener((DraggableRelativeLayout.DraggableListener)new InAppMessageView$3(this));
        if (this.webView.getParent() != null) {
            ((ViewGroup)this.webView.getParent()).removeAllViews();
        }
        final CardView cardView = this.createCardView(context);
        cardView.setTag((Object)"IN_APP_MESSAGE_CARD_VIEW_TAG");
        cardView.addView((View)this.webView);
        this.draggableRelativeLayout.setPadding(this.marginPxSizeLeft, this.marginPxSizeTop, this.marginPxSizeRight, this.marginPxSizeBottom);
        this.draggableRelativeLayout.setClipChildren(false);
        this.draggableRelativeLayout.setClipToPadding(false);
        this.draggableRelativeLayout.addView((View)cardView);
    }
    
    private void setUpParentRelativeLayout(final Context context) {
        (this.parentRelativeLayout = new RelativeLayout(context)).setBackgroundDrawable((Drawable)new ColorDrawable(0));
        this.parentRelativeLayout.setClipChildren(false);
        this.parentRelativeLayout.setClipToPadding(false);
        this.parentRelativeLayout.addView((View)this.draggableRelativeLayout);
    }
    
    private void showDraggableView(final WebViewManager$Position webViewManager$Position, final RelativeLayout$LayoutParams relativeLayout$LayoutParams, final RelativeLayout$LayoutParams relativeLayout$LayoutParams2, final DraggableRelativeLayout.Params params) {
        OSUtils.runOnMainUIThread((Runnable)new Runnable(this, relativeLayout$LayoutParams, relativeLayout$LayoutParams2, params, webViewManager$Position) {
            final InAppMessageView this$0;
            final WebViewManager$Position val$displayLocation;
            final RelativeLayout$LayoutParams val$draggableRelativeLayoutParams;
            final RelativeLayout$LayoutParams val$relativeLayoutParams;
            final DraggableRelativeLayout.Params val$webViewLayoutParams;
            
            public void run() {
                if (this.this$0.webView == null) {
                    return;
                }
                this.this$0.webView.setLayoutParams((ViewGroup$LayoutParams)this.val$relativeLayoutParams);
                final Context applicationContext = this.this$0.currentActivity.getApplicationContext();
                this.this$0.setUpDraggableLayout(applicationContext, this.val$draggableRelativeLayoutParams, this.val$webViewLayoutParams);
                this.this$0.setUpParentRelativeLayout(applicationContext);
                final InAppMessageView this$0 = this.this$0;
                this$0.createPopupWindow(this$0.parentRelativeLayout);
                if (this.this$0.messageController != null) {
                    final InAppMessageView this$2 = this.this$0;
                    this$2.animateInAppMessage(this.val$displayLocation, (View)this$2.draggableRelativeLayout, (View)this.this$0.parentRelativeLayout);
                }
                this.this$0.startDismissTimerIfNeeded();
            }
        });
    }
    
    private void startDismissTimerIfNeeded() {
        if (this.displayDuration <= 0.0) {
            return;
        }
        if (this.scheduleDismissRunnable != null) {
            return;
        }
        final Runnable scheduleDismissRunnable = (Runnable)new Runnable(this) {
            final InAppMessageView this$0;
            
            public void run() {
                if (this.this$0.messageController != null) {
                    this.this$0.messageController.onMessageWillDismiss();
                }
                if (this.this$0.currentActivity != null) {
                    this.this$0.dismissAndAwaitNextMessage(null);
                    this.this$0.scheduleDismissRunnable = null;
                }
                else {
                    this.this$0.shouldDismissWhenActive = true;
                }
            }
        };
        this.scheduleDismissRunnable = (Runnable)scheduleDismissRunnable;
        this.handler.postDelayed((Runnable)scheduleDismissRunnable, (long)this.displayDuration * 1000L);
    }
    
    void checkIfShouldDismiss() {
        if (this.shouldDismissWhenActive) {
            this.shouldDismissWhenActive = false;
            this.finishAfterDelay(null);
        }
    }
    
    void dismissAndAwaitNextMessage(final WebViewManager$OneSignalGenericCallback webViewManager$OneSignalGenericCallback) {
        final DraggableRelativeLayout draggableRelativeLayout = this.draggableRelativeLayout;
        if (draggableRelativeLayout == null) {
            OneSignal.Log(OneSignal.LOG_LEVEL.ERROR, "No host presenter to trigger dismiss animation, counting as dismissed already", new Throwable());
            this.dereferenceViews();
            if (webViewManager$OneSignalGenericCallback != null) {
                webViewManager$OneSignalGenericCallback.onComplete();
            }
            return;
        }
        draggableRelativeLayout.dismiss();
        this.finishAfterDelay(webViewManager$OneSignalGenericCallback);
    }
    
    WebViewManager$Position getDisplayPosition() {
        return this.displayLocation;
    }
    
    boolean isDragging() {
        return this.isDragging;
    }
    
    void removeAllViews() {
        OneSignal.onesignalLog(OneSignal.LOG_LEVEL.DEBUG, "InAppMessageView removing views");
        final Runnable scheduleDismissRunnable = this.scheduleDismissRunnable;
        if (scheduleDismissRunnable != null) {
            this.handler.removeCallbacks(scheduleDismissRunnable);
            this.scheduleDismissRunnable = null;
        }
        final DraggableRelativeLayout draggableRelativeLayout = this.draggableRelativeLayout;
        if (draggableRelativeLayout != null) {
            draggableRelativeLayout.removeAllViews();
        }
        final PopupWindow popupWindow = this.popupWindow;
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
        this.dereferenceViews();
    }
    
    void setMessageController(final InAppMessageViewListener messageController) {
        this.messageController = messageController;
    }
    
    void setWebView(final WebView webView) {
        (this.webView = webView).setBackgroundColor(0);
    }
    
    void showInAppMessageView(final Activity currentActivity) {
        this.currentActivity = currentActivity;
        final RelativeLayout$LayoutParams relativeLayout$LayoutParams = new RelativeLayout$LayoutParams(-1, this.pageHeight);
        relativeLayout$LayoutParams.addRule(13);
        RelativeLayout$LayoutParams parentRelativeLayoutParams;
        if (this.hasBackground) {
            parentRelativeLayoutParams = this.createParentRelativeLayoutParams();
        }
        else {
            parentRelativeLayoutParams = null;
        }
        final WebViewManager$Position displayLocation = this.displayLocation;
        this.showDraggableView(displayLocation, relativeLayout$LayoutParams, parentRelativeLayoutParams, this.createDraggableLayoutParams(this.pageHeight, displayLocation, this.disableDragDismiss));
    }
    
    void showView(final Activity activity) {
        this.delayShowUntilAvailable(activity);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("InAppMessageView{currentActivity=");
        sb.append((Object)this.currentActivity);
        sb.append(", pageWidth=");
        sb.append(this.pageWidth);
        sb.append(", pageHeight=");
        sb.append(this.pageHeight);
        sb.append(", displayDuration=");
        sb.append(this.displayDuration);
        sb.append(", hasBackground=");
        sb.append(this.hasBackground);
        sb.append(", shouldDismissWhenActive=");
        sb.append(this.shouldDismissWhenActive);
        sb.append(", isDragging=");
        sb.append(this.isDragging);
        sb.append(", disableDragDismiss=");
        sb.append(this.disableDragDismiss);
        sb.append(", displayLocation=");
        sb.append((Object)this.displayLocation);
        sb.append(", webView=");
        sb.append((Object)this.webView);
        sb.append('}');
        return sb.toString();
    }
    
    void updateHeight(final int pageHeight) {
        this.pageHeight = pageHeight;
        OSUtils.runOnMainUIThread((Runnable)new Runnable(this, pageHeight) {
            final InAppMessageView this$0;
            final int val$pageHeight;
            
            public void run() {
                if (this.this$0.webView == null) {
                    OneSignal.onesignalLog(OneSignal.LOG_LEVEL.WARN, "WebView height update skipped, new height will be used once it is displayed.");
                    return;
                }
                final ViewGroup$LayoutParams layoutParams = this.this$0.webView.getLayoutParams();
                if (layoutParams == null) {
                    OneSignal.onesignalLog(OneSignal.LOG_LEVEL.WARN, "WebView height update skipped because of null layoutParams, new height will be used once it is displayed.");
                    return;
                }
                layoutParams.height = this.val$pageHeight;
                this.this$0.webView.setLayoutParams(layoutParams);
                if (this.this$0.draggableRelativeLayout != null) {
                    final DraggableRelativeLayout access$100 = this.this$0.draggableRelativeLayout;
                    final InAppMessageView this$0 = this.this$0;
                    access$100.setParams(this$0.createDraggableLayoutParams(this.val$pageHeight, this$0.displayLocation, this.this$0.disableDragDismiss));
                }
            }
        });
    }
    
    interface InAppMessageViewListener
    {
        void onMessageWasDismissed();
        
        void onMessageWasShown();
        
        void onMessageWillDismiss();
    }
}
