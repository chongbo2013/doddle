package com.gurella.engine.graphics.vector;

import com.badlogic.gdx.utils.Pool;

public class FastPool<T> extends Pool<T> {
    private ObjectFactory<T> factory;
    
    public FastPool(ObjectFactory<T> factory) {
        super(16, Integer.MAX_VALUE);
        
        this.factory = factory;
    }
    
    @Override
    protected T newObject() {
        return factory.create();
    }
    
    public interface ObjectFactory<TObj> {
        TObj create();
    }
}
