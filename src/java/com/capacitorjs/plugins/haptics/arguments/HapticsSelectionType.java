package com.capacitorjs.plugins.haptics.arguments;

public class HapticsSelectionType implements HapticsVibrationType
{
    private static final int[] amplitudes;
    private static final long[] oldSDKPattern;
    private static final long[] timings;
    
    static {
        timings = new long[] { 0L, 100L };
        amplitudes = new int[] { 0, 100 };
        oldSDKPattern = new long[] { 0L, 70L };
    }
    
    public int[] getAmplitudes() {
        return HapticsSelectionType.amplitudes;
    }
    
    public long[] getOldSDKPattern() {
        return HapticsSelectionType.oldSDKPattern;
    }
    
    public long[] getTimings() {
        return HapticsSelectionType.timings;
    }
}
