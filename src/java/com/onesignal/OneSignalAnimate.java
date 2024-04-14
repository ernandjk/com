package com.onesignal;

import android.view.animation.ScaleAnimation;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import android.animation.TypeEvaluator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.animation.Animator$AnimatorListener;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation$AnimationListener;
import android.view.animation.Interpolator;
import android.view.View;

class OneSignalAnimate
{
    static Animation animateViewByTranslation(final View view, final float n, final float n2, final int n3, final Interpolator interpolator, final Animation$AnimationListener animationListener) {
        final TranslateAnimation animation = new TranslateAnimation(0.0f, 0.0f, n, n2);
        ((Animation)animation).setDuration((long)n3);
        ((Animation)animation).setInterpolator(interpolator);
        if (animationListener != null) {
            ((Animation)animation).setAnimationListener(animationListener);
        }
        view.setAnimation((Animation)animation);
        return (Animation)animation;
    }
    
    static ValueAnimator animateViewColor(final View view, final int n, final int n2, final int n3, final Animator$AnimatorListener animator$AnimatorListener) {
        final ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setDuration((long)n);
        valueAnimator.setIntValues(new int[] { n2, n3 });
        valueAnimator.setEvaluator((TypeEvaluator)new ArgbEvaluator());
        valueAnimator.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new ValueAnimator$AnimatorUpdateListener(view) {
            final View val$view;
            
            public void onAnimationUpdate(final ValueAnimator valueAnimator) {
                this.val$view.setBackgroundColor((int)valueAnimator.getAnimatedValue());
            }
        });
        if (animator$AnimatorListener != null) {
            valueAnimator.addListener(animator$AnimatorListener);
        }
        return valueAnimator;
    }
    
    static Animation animateViewSmallToLarge(final View view, final int n, final Interpolator interpolator, final Animation$AnimationListener animationListener) {
        final ScaleAnimation animation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, 1, 0.5f, 1, 0.5f);
        ((Animation)animation).setDuration((long)n);
        ((Animation)animation).setInterpolator(interpolator);
        if (animationListener != null) {
            ((Animation)animation).setAnimationListener(animationListener);
        }
        view.setAnimation((Animation)animation);
        return (Animation)animation;
    }
}
