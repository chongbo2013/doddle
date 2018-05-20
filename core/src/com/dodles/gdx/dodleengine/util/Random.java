package com.dodles.gdx.dodleengine.util;

import com.badlogic.gdx.math.RandomXS128;
import java.util.List;

/**
 * Abstraction for pseudo random number handling.
 */
public class Random {
    private RandomXS128 rng;
    
    public Random(long seed) {
        rng = new RandomXS128(seed);
    }
    
    /**
     * Returns a random integer less than max.
     */
    public final int getRandomInt(int max) {
        return getRandomInt(0, max);
    }
    
    /**
     * Returns a random integer between min and max.
     */
    public final int getRandomInt(int min, int max) {
        return rng.nextInt(max - min) + min;
    }
    
    /**
     * Returns a random float less than max.
     */
    public final float getRandom(float max) {
        return getRandom(0, max);
    }
    
    /**
     * Returns a random float between min and max.
     */
    public final float getRandom(float min, float max) {
        return (rng.nextFloat() * (max - min)) + min;
    }
    
    /**
     * Shuffles the provided list.
     */
    public final void shuffle(List list) {
        int index = list.size();
        
        while (index != 0) {
            int randomIndex = rng.nextInt(index);
            index--;
            
            Object temp = list.get(index);
            list.set(index, list.get(randomIndex));
            list.set(randomIndex, temp);
        }
    }
}
