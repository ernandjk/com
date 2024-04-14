package com.capacitorjs.plugins.camera;

import androidx.coordinatorlayout.widget.CoordinatorLayout$Behavior;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import androidx.coordinatorlayout.widget.CoordinatorLayout$LayoutParams;
import android.view.View$OnClickListener;
import android.graphics.Color;
import android.widget.TextView;
import android.widget.LinearLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import java.util.List;
import com.google.android.material.bottomsheet.BottomSheetBehavior$BottomSheetCallback;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class CameraBottomSheetDialogFragment extends BottomSheetDialogFragment
{
    private CameraBottomSheetDialogFragment.CameraBottomSheetDialogFragment$BottomSheetOnCanceledListener canceledListener;
    private BottomSheetBehavior$BottomSheetCallback mBottomSheetBehaviorCallback;
    private List<String> options;
    private CameraBottomSheetDialogFragment.CameraBottomSheetDialogFragment$BottomSheetOnSelectedListener selectedListener;
    private String title;
    
    public CameraBottomSheetDialogFragment() {
        this.mBottomSheetBehaviorCallback = new BottomSheetBehavior$BottomSheetCallback() {
            final CameraBottomSheetDialogFragment this$0;
            
            public void onSlide(final View view, final float n) {
            }
            
            public void onStateChanged(final View view, final int n) {
                if (n == 5) {
                    this.this$0.dismiss();
                }
            }
        };
    }
    
    public void onCancel(final DialogInterface dialogInterface) {
        super.onCancel(dialogInterface);
        final CameraBottomSheetDialogFragment.CameraBottomSheetDialogFragment$BottomSheetOnCanceledListener canceledListener = this.canceledListener;
        if (canceledListener != null) {
            canceledListener.onCanceled();
        }
    }
    
    void setOptions(final List<String> options, final CameraBottomSheetDialogFragment.CameraBottomSheetDialogFragment$BottomSheetOnSelectedListener selectedListener, final CameraBottomSheetDialogFragment.CameraBottomSheetDialogFragment$BottomSheetOnCanceledListener canceledListener) {
        this.options = options;
        this.selectedListener = selectedListener;
        this.canceledListener = canceledListener;
    }
    
    void setTitle(final String title) {
        this.title = title;
    }
    
    public void setupDialog(final Dialog dialog, int i) {
        super.setupDialog(dialog, i);
        final List<String> options = this.options;
        if (options != null) {
            if (options.size() != 0) {
                dialog.getWindow();
                final float density = this.getResources().getDisplayMetrics().density;
                final int n = (int)(16.0f * density + 0.5f);
                final int n2 = (int)(12.0f * density + 0.5f);
                i = (int)(density * 8.0f + 0.5f);
                final CoordinatorLayout coordinatorLayout = new CoordinatorLayout(this.getContext());
                final LinearLayout linearLayout = new LinearLayout(this.getContext());
                linearLayout.setOrientation(1);
                linearLayout.setPadding(n, n, n, n);
                final TextView textView = new TextView(this.getContext());
                textView.setTextColor(Color.parseColor("#757575"));
                textView.setPadding(i, i, i, i);
                textView.setText((CharSequence)this.title);
                linearLayout.addView((View)textView);
                TextView textView2;
                for (i = 0; i < this.options.size(); ++i) {
                    textView2 = new TextView(this.getContext());
                    textView2.setTextColor(Color.parseColor("#000000"));
                    textView2.setPadding(n2, n2, n2, n2);
                    textView2.setText((CharSequence)this.options.get(i));
                    textView2.setOnClickListener((View$OnClickListener)new CameraBottomSheetDialogFragment$$ExternalSyntheticLambda0(this, i));
                    linearLayout.addView((View)textView2);
                }
                coordinatorLayout.addView(linearLayout.getRootView());
                dialog.setContentView(coordinatorLayout.getRootView());
                final CoordinatorLayout$Behavior behavior = ((CoordinatorLayout$LayoutParams)((View)coordinatorLayout.getParent()).getLayoutParams()).getBehavior();
                if (behavior != null && behavior instanceof BottomSheetBehavior) {
                    ((BottomSheetBehavior)behavior).addBottomSheetCallback(this.mBottomSheetBehaviorCallback);
                }
            }
        }
    }
}
