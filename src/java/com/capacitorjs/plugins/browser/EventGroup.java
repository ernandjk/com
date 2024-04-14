package com.capacitorjs.plugins.browser;

class EventGroup
{
    private EventGroupCompletion completion;
    private int count;
    private boolean isComplete;
    
    public EventGroup(final EventGroupCompletion completion) {
        this.count = 0;
        this.isComplete = false;
        this.completion = completion;
    }
    
    private void checkForCompletion() {
        if (this.count <= 0) {
            if (!this.isComplete) {
                final EventGroupCompletion completion = this.completion;
                if (completion != null) {
                    completion.onGroupCompletion();
                }
            }
            this.isComplete = true;
        }
    }
    
    public void enter() {
        ++this.count;
    }
    
    public void leave() {
        --this.count;
        this.checkForCompletion();
    }
    
    public void reset() {
        this.count = 0;
        this.isComplete = false;
    }
    
    interface EventGroupCompletion
    {
        void onGroupCompletion();
    }
}
