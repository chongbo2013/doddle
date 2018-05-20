package com.dodles.gdx.dodleengine;

/**
 * Logging interface.
 */
public interface Logger {
    /**
     * Logs at debug level.
     */
    void debug(String tag, String message);
    
    /**
     * Logs a message.
     */
    void log(String tag, String message);
    
    /**
     * Logs at error level.
     */
    void error(String tag, String message);
    
    /**
     * Logs at error level with a stacktrace from an exception.
     */
    void error(String tag, String message, Throwable t);
}
