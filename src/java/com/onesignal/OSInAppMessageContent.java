package com.onesignal;

import kotlin.jvm.internal.Intrinsics;
import org.json.JSONObject;
import kotlin.Metadata;

@Metadata(bv = { 1, 0, 3 }, d1 = { "\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\u0006\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u000b\b\u0010\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004R\u001c\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0007\u0010\b\"\u0004\b\t\u0010\nR\u001e\u0010\u000b\u001a\u0004\u0018\u00010\fX\u0086\u000e¢\u0006\u0010\n\u0002\u0010\u0011\u001a\u0004\b\r\u0010\u000e\"\u0004\b\u000f\u0010\u0010R\u001c\u0010\u0012\u001a\u0004\u0018\u00010\u0013X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0014\u0010\u0015\"\u0004\b\u0016\u0010\u0017R\u001a\u0010\u0018\u001a\u00020\u0019X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0018\u0010\u001a\"\u0004\b\u001b\u0010\u001cR\u001a\u0010\u001d\u001a\u00020\u001eX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001f\u0010 \"\u0004\b!\u0010\"R\u001a\u0010#\u001a\u00020\u0019X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b$\u0010\u001a\"\u0004\b%\u0010\u001cR\u001a\u0010&\u001a\u00020\u0019X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b'\u0010\u001a\"\u0004\b(\u0010\u001c¨\u0006)" }, d2 = { "Lcom/onesignal/OSInAppMessageContent;", "", "jsonObject", "Lorg/json/JSONObject;", "(Lorg/json/JSONObject;)V", "contentHtml", "", "getContentHtml", "()Ljava/lang/String;", "setContentHtml", "(Ljava/lang/String;)V", "displayDuration", "", "getDisplayDuration", "()Ljava/lang/Double;", "setDisplayDuration", "(Ljava/lang/Double;)V", "Ljava/lang/Double;", "displayLocation", "Lcom/onesignal/WebViewManager$Position;", "getDisplayLocation", "()Lcom/onesignal/WebViewManager$Position;", "setDisplayLocation", "(Lcom/onesignal/WebViewManager$Position;)V", "isFullBleed", "", "()Z", "setFullBleed", "(Z)V", "pageHeight", "", "getPageHeight", "()I", "setPageHeight", "(I)V", "useHeightMargin", "getUseHeightMargin", "setUseHeightMargin", "useWidthMargin", "getUseWidthMargin", "setUseWidthMargin", "onesignal_release" }, k = 1, mv = { 1, 4, 2 })
public class OSInAppMessageContent
{
    private String contentHtml;
    private Double displayDuration;
    private WebViewManager$Position displayLocation;
    private boolean isFullBleed;
    private int pageHeight;
    private boolean useHeightMargin;
    private boolean useWidthMargin;
    
    public OSInAppMessageContent(JSONObject optJSONObject) {
        Intrinsics.checkNotNullParameter((Object)optJSONObject, "jsonObject");
        this.useHeightMargin = true;
        this.useWidthMargin = true;
        this.contentHtml = optJSONObject.optString("html");
        this.displayDuration = optJSONObject.optDouble("display_duration");
        optJSONObject = optJSONObject.optJSONObject("styles");
        final int n = 0;
        this.useHeightMargin = ((optJSONObject != null && optJSONObject.optBoolean("remove_height_margin", false)) ^ true);
        int optBoolean = n;
        if (optJSONObject != null) {
            optBoolean = (optJSONObject.optBoolean("remove_width_margin", false) ? 1 : 0);
        }
        this.useWidthMargin = ((optBoolean ^ 0x1) != 0x0);
        this.isFullBleed = (this.useHeightMargin ^ true);
    }
    
    public final String getContentHtml() {
        return this.contentHtml;
    }
    
    public final Double getDisplayDuration() {
        return this.displayDuration;
    }
    
    public final WebViewManager$Position getDisplayLocation() {
        return this.displayLocation;
    }
    
    public final int getPageHeight() {
        return this.pageHeight;
    }
    
    public final boolean getUseHeightMargin() {
        return this.useHeightMargin;
    }
    
    public final boolean getUseWidthMargin() {
        return this.useWidthMargin;
    }
    
    public final boolean isFullBleed() {
        return this.isFullBleed;
    }
    
    public final void setContentHtml(final String contentHtml) {
        this.contentHtml = contentHtml;
    }
    
    public final void setDisplayDuration(final Double displayDuration) {
        this.displayDuration = displayDuration;
    }
    
    public final void setDisplayLocation(final WebViewManager$Position displayLocation) {
        this.displayLocation = displayLocation;
    }
    
    public final void setFullBleed(final boolean isFullBleed) {
        this.isFullBleed = isFullBleed;
    }
    
    public final void setPageHeight(final int pageHeight) {
        this.pageHeight = pageHeight;
    }
    
    public final void setUseHeightMargin(final boolean useHeightMargin) {
        this.useHeightMargin = useHeightMargin;
    }
    
    public final void setUseWidthMargin(final boolean useWidthMargin) {
        this.useWidthMargin = useWidthMargin;
    }
}
