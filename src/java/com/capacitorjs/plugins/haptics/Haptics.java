package com.capacitorjs.plugins.haptics;

import com.capacitorjs.plugins.haptics.arguments.HapticsSelectionType;
import com.capacitorjs.plugins.haptics.arguments.HapticsVibrationType;
import com.getcapacitor.Bridge$$ExternalSyntheticApiModelOutline0;
import android.os.Build$VERSION;
import android.os.Vibrator;
import android.content.Context;

public class Haptics
{
    private Context context;
    private boolean selectionStarted;
    private final Vibrator vibrator;
    
    Haptics(final Context context) {
        this.selectionStarted = false;
        this.context = context;
        if (Build$VERSION.SDK_INT >= 31) {
            this.vibrator = Bridge$$ExternalSyntheticApiModelOutline0.m(Bridge$$ExternalSyntheticApiModelOutline0.m(context.getSystemService("vibrator_manager")));
        }
        else {
            this.vibrator = this.getDeprecatedVibrator(context);
        }
    }
    
    private Vibrator getDeprecatedVibrator(final Context context) {
        return (Vibrator)context.getSystemService("vibrator");
    }
    
    private void vibratePre26(final int n) {
        this.vibrator.vibrate((long)n);
    }
    
    private void vibratePre26(final long[] array, final int n) {
        this.vibrator.vibrate(array, n);
    }
    
    public void performHaptics(final HapticsVibrationType hapticsVibrationType) {
        if (Build$VERSION.SDK_INT >= 26) {
            Bridge$$ExternalSyntheticApiModelOutline0.m(this.vibrator, Bridge$$ExternalSyntheticApiModelOutline0.m(hapticsVibrationType.getTimings(), hapticsVibrationType.getAmplitudes(), -1));
        }
        else {
            this.vibratePre26(hapticsVibrationType.getOldSDKPattern(), -1);
        }
    }
    
    public void selectionChanged() {
        if (this.selectionStarted) {
            this.performHaptics((HapticsVibrationType)new HapticsSelectionType());
        }
    }
    
    public void selectionEnd() {
        this.selectionStarted = false;
    }
    
    public void selectionStart() {
        this.selectionStarted = true;
    }
    
    public void vibrate(final int n) {
        if (Build$VERSION.SDK_INT >= 26) {
            Bridge$$ExternalSyntheticApiModelOutline0.m(this.vibrator, Bridge$$ExternalSyntheticApiModelOutline0.m((long)n, -1));
        }
        else {
            this.vibratePre26(n);
        }
    }
}
