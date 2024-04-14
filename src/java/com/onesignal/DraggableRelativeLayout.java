package com.onesignal;

import android.content.res.Resources;
import android.view.MotionEvent;
import android.view.View;
import androidx.core.view.ViewCompat;
import androidx.customview.widget.ViewDragHelper$Callback;
import android.view.ViewGroup;
import android.content.Context;
import androidx.customview.widget.ViewDragHelper;
import android.widget.RelativeLayout;

class DraggableRelativeLayout extends RelativeLayout
{
    private static final int EXTRA_PX_DISMISS;
    private static final int MARGIN_PX_SIZE;
    private boolean dismissing;
    private boolean draggingDisabled;
    private ViewDragHelper mDragHelper;
    private DraggableListener mListener;
    private Params params;
    
    static {
        MARGIN_PX_SIZE = OSViewUtils.dpToPx(28);
        EXTRA_PX_DISMISS = OSViewUtils.dpToPx(64);
    }
    
    public DraggableRelativeLayout(final Context context) {
        super(context);
        this.setClipChildren(false);
        this.createDragHelper();
    }
    
    private void createDragHelper() {
        this.mDragHelper = ViewDragHelper.create((ViewGroup)this, 1.0f, (ViewDragHelper$Callback)new DraggableRelativeLayout$1(this));
    }
    
    public void computeScroll() {
        super.computeScroll();
        if (this.mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation((View)this);
        }
    }
    
    public void dismiss() {
        this.dismissing = true;
        this.mDragHelper.smoothSlideViewTo((View)this, this.getLeft(), this.params.offScreenYPos);
        ViewCompat.postInvalidateOnAnimation((View)this);
    }
    
    public boolean onInterceptTouchEvent(final MotionEvent motionEvent) {
        if (this.dismissing) {
            return true;
        }
        final int action = motionEvent.getAction();
        if (action == 0 || action == 5) {
            final DraggableListener mListener = this.mListener;
            if (mListener != null) {
                mListener.onDragEnd();
            }
        }
        this.mDragHelper.processTouchEvent(motionEvent);
        return false;
    }
    
    void setListener(final DraggableListener mListener) {
        this.mListener = mListener;
    }
    
    void setParams(final Params params) {
        (this.params = params).offScreenYPos = params.messageHeight + params.posY + (Resources.getSystem().getDisplayMetrics().heightPixels - params.messageHeight - params.posY) + DraggableRelativeLayout.EXTRA_PX_DISMISS;
        params.dismissingYVelocity = OSViewUtils.dpToPx(3000);
        if (params.dragDirection == 0) {
            params.offScreenYPos = -params.messageHeight - DraggableRelativeLayout.MARGIN_PX_SIZE;
            params.dismissingYVelocity = -params.dismissingYVelocity;
            params.dismissingYPos = params.offScreenYPos / 3;
        }
        else {
            params.dismissingYPos = params.messageHeight / 3 + params.maxYPos * 2;
        }
    }
    
    interface DraggableListener
    {
        void onDismiss();
        
        void onDragEnd();
        
        void onDragStart();
    }
    
    static class Params
    {
        static final int DRAGGABLE_DIRECTION_DOWN = 1;
        static final int DRAGGABLE_DIRECTION_UP = 0;
        private int dismissingYPos;
        private int dismissingYVelocity;
        int dragDirection;
        int dragThresholdY;
        boolean draggingDisabled;
        int height;
        int maxXPos;
        int maxYPos;
        int messageHeight;
        private int offScreenYPos;
        int posY;
    }
}
