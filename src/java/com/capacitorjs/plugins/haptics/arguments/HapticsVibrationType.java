package com.capacitorjs.plugins.haptics.arguments;

public interface HapticsVibrationType
{
    int[] getAmplitudes();
    
    long[] getOldSDKPattern();
    
    long[] getTimings();
}
