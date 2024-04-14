package com.capacitorjs.plugins.statusbar;

public class StatusBarInfo
{
    private String color;
    private boolean overlays;
    private String style;
    private boolean visible;
    
    public String getColor() {
        return this.color;
    }
    
    public String getStyle() {
        return this.style;
    }
    
    public boolean isOverlays() {
        return this.overlays;
    }
    
    public boolean isVisible() {
        return this.visible;
    }
    
    public void setColor(final String color) {
        this.color = color;
    }
    
    public void setOverlays(final boolean overlays) {
        this.overlays = overlays;
    }
    
    public void setStyle(final String style) {
        this.style = style;
    }
    
    public void setVisible(final boolean visible) {
        this.visible = visible;
    }
}
