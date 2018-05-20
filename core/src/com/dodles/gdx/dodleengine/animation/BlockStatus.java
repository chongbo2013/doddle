package com.dodles.gdx.dodleengine.animation;

/**
 * Stores information about the animation status of a block.
 */
public class BlockStatus {
    private final Block block;
    
    private boolean playing = false;
    private int executionCount = 0;
    private float curTime;
    
    public BlockStatus(Block block) {
        this.block = block;
    }
    
    /**
     * Returns the block this status applies to.
     */
    public final Block getBlock() {
        return block;
    }

    /**
     * Returns a value indicating if the block is currently playing.
     */
    public final boolean isPlaying() {
        return playing;
    }

    /**
     * Sets a value indicating if the block is currently playing.
     * @param playing 
     */
    public final void setPlaying(boolean playing) {
        this.playing = playing;
        
        if (playing) {
            this.curTime = 0;
        }
    }
    
    /**
     * Returns the current time in the block.
     */
    public final float getCurTime() {
        return curTime;
    }
    
    /**
     * Increments the current time in the block.
     */
    public final void incrementCurTime(float deltaTime) {
        curTime += deltaTime;
    }

    /**
     * Returns the number of executions of the block.
     */
    public final int getExecutionCount() {
        return executionCount;
    }

    /**
     * Increments the block execution counter.
     */
    public final void incrementExecutionCount() {
        this.executionCount++;
    }
}
