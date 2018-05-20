package com.dodles.gdx.dodleengine.editor;

import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.events.EventBus;
import com.dodles.gdx.dodleengine.events.EventData;
import com.dodles.gdx.dodleengine.events.EventSubscriber;
import com.dodles.gdx.dodleengine.events.EventTopic;
import com.dodles.gdx.dodleengine.events.EventType;
import de.hypergraphs.hyena.core.shared.data.UUID;

import javax.inject.Inject;
import java.util.ArrayList;

/**
 * Manages the editor ok/cancel stack.
 */
@PerDodleEngine
public class OkCancelStackManager {
    private final ArrayList<OkCancelStackLayer> layers = new ArrayList<OkCancelStackLayer>();
    private final EventBus eventBus;
    
    @Inject
    public OkCancelStackManager(EventBus eventBus) {
        this.eventBus = eventBus;

        this.eventBus.addSubscriber(new EventSubscriber(EventTopic.EDITOR) {
            @Override
            public void listen(EventTopic eventTopic, EventType eventType, EventData data) {
                switch(eventType) {
                    case OK_CANCEL_STACK_POP_CANCEL:
                        popCancel();
                        break;
                    case OK_CANCEL_STACK_POP_OK:
                        popOk();
                        break;
                }
            }
        });
        reset();
    }
    
    /**
     * Adds a new "layer" to the ok/cancel stack.
     */
    public final void nextLayer() {
        layers.add(new OkCancelStackLayer());
    }
    
    /**
     * Returns the id of the current "layer".
     */
    public final String getLayerID() {
        return layers.get(layers.size() - 1).id;
    }
    
    /**
     * Returns the top frame on the externally visible stack.
     */
    public final OkCancelStackFrame getCurFrame() {
        if (size() > 0) {
            for (int i = layers.size() - 1; i >= 0; i--) {
                OkCancelStackLayer curLayer = layers.get(i);
                
                if (curLayer.size() > 0) {
                    return curLayer.get(curLayer.size() - 1);
                }
            }
        }
        
        return null;
    }

    /**
     * Returns true if there is an okay action in the top frame
     */
    public final boolean getOkEnabled() {
        OkCancelStackFrame frame = getCurFrame();
        return frame != null && frame.hasOk();
    }

    /**
     * Returns true if there is a cancel action in the top frame
     */
    public final boolean getCancelEnabled() {
        OkCancelStackFrame frame = getCurFrame();
        return frame != null && frame.hasCancel();
    }

    /**
     * Returns the top frame's type, if there is one
     */
    public final String getCurrentFrameType() {
        OkCancelStackFrame frame = getCurFrame();
        return ((frame != null) ? frame.getType() : null);
    }
    
    /**
     * Returns the current size of the externally visible stack.
     */
    public final int size() {
        int count = 0;
        
        for (ArrayList layer : layers) {
            if (layer != null) {
                count += layer.size();
            }
        }
        
        return count;
    }
    
    /**
     * Resets the stack to empty.
     */
    public final void reset() {
        layers.clear();
        nextLayer();
        sendStackChangedEvent();
    }
    
    /**
     * Pushes a new frame on the stack with only an OK action.
     */
    public final void push(Runnable ok) {
        push(ok, null);
    }
    
    /**
     * Pushes a new frame on the stack with an OK action, optionally in the front of the current layer.
     */
    public final void push(Runnable ok, boolean front) {
        push(ok, null, front, null);
    }
    
    /**
     * Pushes a new frame on the stack with an OK and cancel action.
     */
    public final void push(Runnable ok, Runnable cancel) {
        push(ok, cancel, false, null);
    }

    /**
     * Pushes a new frame on the stack with an OK and cancel action, optionally in the front of the current layer.
     */
    public final void push(Runnable ok, Runnable cancel, boolean front, String type) {
        push(new OkCancelStackFrame(ok, cancel, type), front);
    }

    /**
     * Pushes a new frame on the stack with an OK and cancel action, optionally in the front of the current layer.
     */
    public final void push(OkCancelStackFrame okCancelFrame) {
        push(okCancelFrame, false);
    }

    /**
     * Pushes a new frame on the stack with an OK and cancel action, optionally in the front of the current layer.
     */
    public final void push(OkCancelStackFrame okCancelFrame, boolean front) {
        ArrayList<OkCancelStackFrame> stack = layers.get(layers.size() - 1);
        int index = stack.size();

        if (front) {
            index = 0;
        }

        stack.add(index, okCancelFrame);
        sendStackChangedEvent();
    }
    
    /**
     * Pops the top frame off the stack, optionally not firing the stack changed event.
     */
    public final OkCancelStackFrame pop(boolean dontFireEvent) {
        while (size() > 0) {
            ArrayList<OkCancelStackFrame> curStack = layers.get(layers.size() - 1);
            
            if (curStack.size() == 0) {
                layers.remove(layers.size() - 1);
            } else {
                OkCancelStackFrame frame = curStack.remove(curStack.size() - 1);
                
                if (!dontFireEvent) {
                    sendStackChangedEvent();
                }

                return frame;
            }
        }
        
        return null;
    }

    /**
     * Pops all frames off of the ok/cancel stack, until it pops the desired layered.
     * Optionally, tries to cancel if available.
     */
    public final void popThroughLayer(String layerID, boolean cancelIfAvailable) {
        String previousLayerId = "";
        while (size() > 0 && !layerID.equals(previousLayerId)) {
            popLayer(cancelIfAvailable);

            OkCancelStackLayer layer = layers.get(layers.size() - 1);

            if (layers.size() > 1) {
                layers.remove(layer);
            }

            previousLayerId = layer.id;
        }
    }

    /**
     * Pops all frames off of the ok/cancel stack, optionally trying to cancel if available.
     */
    public final void popAll(boolean cancelIfAvailable) {
        while (size() > 0) {
            popLayer(cancelIfAvailable);
            
            OkCancelStackLayer layer = layers.get(layers.size() - 1);
            
            if (layers.size() > 1) {
                layers.remove(layer);
            }
        }
    }
    
    /**
     * Pops all the frames of the current "layer" off of the ok/cancel stack, optionally trying to cancel if available.
     * NOTE: doesn't necessarily remove the layer from the stack, just the
     */
    public final void popLayer(boolean cancelIfAvailable) {
        OkCancelStackLayer layer = layers.get(layers.size() - 1);
        
        while (layer.size() > 0) {
            OkCancelStackFrame frame = pop(false);
            
            if (cancelIfAvailable && frame.hasCancel()) {
                frame.cancel();
            } else {
                frame.execute();
            }
        }
    }
    
    /**
     * Pops the top frame off the stack, executing the OK action.
     */
    public final void popOk() {
        OkCancelStackFrame frame = pop(true);
        frame.execute();
        sendStackChangedEvent();
    }
    
    /**
     * Pops the top frame off the stack, executing the cancel action.
     */
    public final void popCancel() {
        OkCancelStackFrame frame = pop(true);
        frame.cancel();
        sendStackChangedEvent();
    }

    /**
     * Sends event to notify listeners that the Ok/Cancel Stack State has changed
     */
    private void sendStackChangedEvent() {
        eventBus.publish(EventTopic.DEFAULT, EventType.OK_CANCEL_STACK_CHANGED, Integer.toString(size()),
                Boolean.toString(getOkEnabled()), Boolean.toString(getCancelEnabled()), getCurrentFrameType());
    }
    
    /**
     * Adds an id to the layer frame list.
     */
    private class OkCancelStackLayer extends ArrayList<OkCancelStackFrame> {
        private final String id = UUID.uuid();
    }

}
