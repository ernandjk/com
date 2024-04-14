package com.capacitorjs.plugins.haptics.arguments;

public enum HapticsNotificationType implements HapticsVibrationType
{
    private static final HapticsNotificationType[] $VALUES;
    
    ERROR("ERROR", new long[] { 0L, 27L, 45L, 50L }, new int[] { 0, 120, 0, 250 }, new long[] { 0L, 27L, 45L, 50L }), 
    SUCCESS("SUCCESS", new long[] { 0L, 35L, 65L, 21L }, new int[] { 0, 250, 0, 180 }, new long[] { 0L, 35L, 65L, 21L }), 
    WARNING("WARNING", new long[] { 0L, 30L, 40L, 30L, 50L, 60L }, new int[] { 255, 255, 255, 255, 255, 255 }, new long[] { 0L, 30L, 40L, 30L, 50L, 60L });
    
    private final int[] amplitudes;
    private final long[] oldSDKPattern;
    private final long[] timings;
    private final String type;
    
    private static /* synthetic */ HapticsNotificationType[] $values() {
        return new HapticsNotificationType[] { HapticsNotificationType.SUCCESS, HapticsNotificationType.WARNING, HapticsNotificationType.ERROR };
    }
    
    static {
        $VALUES = $values();
    }
    
    private HapticsNotificationType(final String type, final long[] timings, final int[] amplitudes, final long[] oldSDKPattern) {
        this.type = type;
        this.timings = timings;
        this.amplitudes = amplitudes;
        this.oldSDKPattern = oldSDKPattern;
    }
    
    public static HapticsNotificationType fromString(final String s) {
        for (final HapticsNotificationType hapticsNotificationType : values()) {
            if (hapticsNotificationType.type.equals((Object)s)) {
                return hapticsNotificationType;
            }
        }
        return HapticsNotificationType.SUCCESS;
    }
    
    public int[] getAmplitudes() {
        return this.amplitudes;
    }
    
    public long[] getOldSDKPattern() {
        return this.oldSDKPattern;
    }
    
    public long[] getTimings() {
        return this.timings;
    }
}
