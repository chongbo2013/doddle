package com.dodles.gdx.dodleengine.scenegraph.graphics;

import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import java.util.Stack;

/**
 * We can't have multiple framebuffers open at once - this class manages a framebuffer
 * stack so we can re-enable previous framebuffers if we need nested framebuffers.
 * http://stackoverflow.com/questions/25471727/libgdx-nested-framebuffer
 */
public final class FrameBufferStack {
    private static final FrameBufferStack INSTANCE = new FrameBufferStack();
    private final Stack<FrameBuffer> stack = new Stack<FrameBuffer>();
    
    private FrameBufferStack() {    
    }
    
    /**
     * Returns the reference to global instance of the FrameBufferStack.
     */
    public static FrameBufferStack instance() {
        return INSTANCE;
    }
    
    /**
     * Begins use of the given FrameBuffer, disabling any other currently active framebuffers.
     */
    public void begin(FrameBuffer buffer) {
        if (!stack.isEmpty()) {
            stack.peek().end();
        }
        stack.push(buffer).begin();
    }

    /**
     * Ends use of the FrameBuffer and re-enables the previous FrameBuffer.
     */
    public void end() {
        stack.pop().end();
        if (!stack.isEmpty()) {
            stack.peek().begin();
        }
    }
}
