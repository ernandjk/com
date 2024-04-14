package com.onesignal;

import java.util.List;
import java.util.Iterator;
import com.onesignal.influence.domain.OSInfluence;
import java.util.HashMap;

class OSFocusTimeProcessorFactory
{
    private final HashMap<String, FocusTimeController.FocusTimeProcessorBase> focusTimeProcessors;
    
    public OSFocusTimeProcessorFactory() {
        final HashMap focusTimeProcessors = new HashMap();
        (this.focusTimeProcessors = (HashMap<String, FocusTimeController.FocusTimeProcessorBase>)focusTimeProcessors).put((Object)FocusTimeController$FocusTimeProcessorUnattributed.class.getName(), (Object)new FocusTimeController$FocusTimeProcessorUnattributed());
        focusTimeProcessors.put((Object)FocusTimeController$FocusTimeProcessorAttributed.class.getName(), (Object)new FocusTimeController$FocusTimeProcessorAttributed());
    }
    
    private FocusTimeController.FocusTimeProcessorBase getAttributedProcessor() {
        return (FocusTimeController.FocusTimeProcessorBase)this.focusTimeProcessors.get((Object)FocusTimeController$FocusTimeProcessorAttributed.class.getName());
    }
    
    private FocusTimeController.FocusTimeProcessorBase getUnattributedProcessor() {
        return (FocusTimeController.FocusTimeProcessorBase)this.focusTimeProcessors.get((Object)FocusTimeController$FocusTimeProcessorUnattributed.class.getName());
    }
    
    FocusTimeController.FocusTimeProcessorBase getTimeProcessorSaved() {
        final FocusTimeController.FocusTimeProcessorBase attributedProcessor = this.getAttributedProcessor();
        final Iterator iterator = attributedProcessor.getInfluences().iterator();
        while (iterator.hasNext()) {
            if (((OSInfluence)iterator.next()).getInfluenceType().isAttributed()) {
                return attributedProcessor;
            }
        }
        return this.getUnattributedProcessor();
    }
    
    FocusTimeController.FocusTimeProcessorBase getTimeProcessorWithInfluences(final List<OSInfluence> list) {
        final Iterator iterator = list.iterator();
        while (true) {
            while (iterator.hasNext()) {
                if (((OSInfluence)iterator.next()).getInfluenceType().isAttributed()) {
                    final boolean b = true;
                    FocusTimeController.FocusTimeProcessorBase focusTimeProcessorBase;
                    if (b) {
                        focusTimeProcessorBase = this.getAttributedProcessor();
                    }
                    else {
                        focusTimeProcessorBase = this.getUnattributedProcessor();
                    }
                    return focusTimeProcessorBase;
                }
            }
            final boolean b = false;
            continue;
        }
    }
}
