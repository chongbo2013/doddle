package com.dodles.gdx.dodleengine;

/**
 * Logger to use in mobile interop scenarios that will relay errors to the host for
 * transmission to the cloud logging service.
 */
public class InteropLogger extends DefaultLogger {
    private final HostErrorLogger hostLogger;
    
    public InteropLogger(HostErrorLogger hostLogger) {
        this.hostLogger = hostLogger;
    }
    
    @Override
    public final void error(String tag, String message, Throwable t) {
        super.error(tag, message, t);
        String stackTrace = null;
        
        if (t != null) {
            stackTrace = t.toString();
            for (StackTraceElement element : t.getStackTrace()) {
                stackTrace += "\n" + element;
            }
        }
        
        hostLogger.logError(tag + ": " + message, stackTrace);
    }
    
    /**
     * Provides the ability to send errors to the host to be logged.
     */
    public interface HostErrorLogger {
        /**
         * Logs an error with the host.
         */
        void logError(String message, String stackTrace);
    }
}
