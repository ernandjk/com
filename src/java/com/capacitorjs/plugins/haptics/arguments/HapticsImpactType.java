package com.capacitorjs.plugins.haptics.arguments;

public enum HapticsImpactType implements HapticsVibrationType
{
    private static final HapticsImpactType[] $VALUES;
    
    HEAVY("HEAVY", new long[] { 0L, 60L }, new int[] { 0, 255 }, new long[] { 0L, 61L }), 
    LIGHT("LIGHT", new long[] { 0L, 50L }, new int[] { 0, 110 }, new long[] { 0L, 20L }), 
    MEDIUM("MEDIUM", new long[] { 0L, 43L }, new int[] { 0, 180 }, new long[] { 0L, 43L });
    
    private final int[] amplitudes;
    private final long[] oldSDKPattern;
    private final long[] timings;
    private final String type;
    
    private static /* synthetic */ HapticsImpactType[] $values() {
        return new HapticsImpactType[] { HapticsImpactType.LIGHT, HapticsImpactType.MEDIUM, HapticsImpactType.HEAVY };
    }
    
    static {
        $VALUES = $values();
    }
    
    private HapticsImpactType(final String type, final long[] timings, final int[] amplitudes, final long[] oldSDKPattern) {
        this.type = type;
        this.timings = timings;
        this.amplitudes = amplitudes;
        this.oldSDKPattern = oldSDKPattern;
    }
    
    public static HapticsImpactType fromString(final String s) {
        for (final HapticsImpactType hapticsImpactType : values()) {
            if (hapticsImpactType.type.equals((Object)s)) {
                return hapticsImpactType;
            }
        }
        return HapticsImpactType.HEAVY;
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
