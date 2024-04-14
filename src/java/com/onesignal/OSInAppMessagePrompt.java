package com.onesignal;

public abstract class OSInAppMessagePrompt
{
    private boolean prompted;
    
    OSInAppMessagePrompt() {
        this.prompted = false;
    }
    
    abstract String getPromptKey();
    
    abstract void handlePrompt(final OneSignal.OSPromptActionCompletionCallback p0);
    
    boolean hasPrompted() {
        return this.prompted;
    }
    
    void setPrompted(final boolean prompted) {
        this.prompted = prompted;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("OSInAppMessagePrompt{key=");
        sb.append(this.getPromptKey());
        sb.append(" prompted=");
        sb.append(this.prompted);
        sb.append('}');
        return sb.toString();
    }
}
