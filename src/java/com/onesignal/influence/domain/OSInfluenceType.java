package com.onesignal.influence.domain;

import kotlin.text.StringsKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.Metadata;

@Metadata(bv = { 1, 0, 3 }, d1 = { "\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\n\b\u0086\u0001\u0018\u0000 \r2\b\u0012\u0004\u0012\u00020\u00000\u0001:\u0001\rB\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0006\u0010\u0003\u001a\u00020\u0004J\u0006\u0010\u0005\u001a\u00020\u0004J\u0006\u0010\u0006\u001a\u00020\u0004J\u0006\u0010\u0007\u001a\u00020\u0004J\u0006\u0010\b\u001a\u00020\u0004j\u0002\b\tj\u0002\b\nj\u0002\b\u000bj\u0002\b\f¨\u0006\u000e" }, d2 = { "Lcom/onesignal/influence/domain/OSInfluenceType;", "", "(Ljava/lang/String;I)V", "isAttributed", "", "isDirect", "isDisabled", "isIndirect", "isUnattributed", "DIRECT", "INDIRECT", "UNATTRIBUTED", "DISABLED", "Companion", "onesignal_release" }, k = 1, mv = { 1, 4, 2 })
public enum OSInfluenceType
{
    private static final OSInfluenceType[] $VALUES;
    public static final Companion Companion;
    
    DIRECT, 
    DISABLED, 
    INDIRECT, 
    UNATTRIBUTED;
    
    static {
        Companion = new Companion(null);
    }
    
    @JvmStatic
    public static final OSInfluenceType fromString(final String s) {
        return OSInfluenceType.Companion.fromString(s);
    }
    
    public final boolean isAttributed() {
        return this.isDirect() || this.isIndirect();
    }
    
    public final boolean isDirect() {
        final OSInfluenceType osInfluenceType = this;
        return this == OSInfluenceType.DIRECT;
    }
    
    public final boolean isDisabled() {
        final OSInfluenceType osInfluenceType = this;
        return this == OSInfluenceType.DISABLED;
    }
    
    public final boolean isIndirect() {
        final OSInfluenceType osInfluenceType = this;
        return this == OSInfluenceType.INDIRECT;
    }
    
    public final boolean isUnattributed() {
        final OSInfluenceType osInfluenceType = this;
        return this == OSInfluenceType.UNATTRIBUTED;
    }
    
    @Metadata(bv = { 1, 0, 3 }, d1 = { "\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0012\u0010\u0003\u001a\u00020\u00042\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006H\u0007¨\u0006\u0007" }, d2 = { "Lcom/onesignal/influence/domain/OSInfluenceType$Companion;", "", "()V", "fromString", "Lcom/onesignal/influence/domain/OSInfluenceType;", "value", "", "onesignal_release" }, k = 1, mv = { 1, 4, 2 })
    public static final class Companion
    {
        private Companion() {
        }
        
        @JvmStatic
        public final OSInfluenceType fromString(final String s) {
            if (s != null) {
                final OSInfluenceType[] values = OSInfluenceType.values();
                int length = values.length;
                while (true) {
                    while (--length >= 0) {
                        final OSInfluenceType osInfluenceType = values[length];
                        if (StringsKt.equals(osInfluenceType.name(), s, true)) {
                            final OSInfluenceType unattributed = osInfluenceType;
                            if (unattributed != null) {
                                return unattributed;
                            }
                            return OSInfluenceType.UNATTRIBUTED;
                        }
                    }
                    final OSInfluenceType unattributed = null;
                    continue;
                }
            }
            return OSInfluenceType.UNATTRIBUTED;
        }
    }
}
