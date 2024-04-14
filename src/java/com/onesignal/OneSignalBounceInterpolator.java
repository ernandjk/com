package com.onesignal;

import android.view.animation.Interpolator;

class OneSignalBounceInterpolator implements Interpolator
{
    private double mAmplitude;
    private double mFrequency;
    
    OneSignalBounceInterpolator(final double mAmplitude, final double mFrequency) {
        this.mAmplitude = mAmplitude;
        this.mFrequency = mFrequency;
    }
    
    public float getInterpolation(final float n) {
        return (float)(Math.pow(2.718281828459045, -n / this.mAmplitude) * -1.0 * Math.cos(this.mFrequency * n) + 1.0);
    }
}
