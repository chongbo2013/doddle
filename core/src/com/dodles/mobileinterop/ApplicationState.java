package com.dodles.mobileinterop;

/**
 * Stores state of the application that needs to be shared between the webview and libgdx.
 */
public final class ApplicationState {
    private static String bearerToken;
    
    private ApplicationState() {
    }

    /**
     * Gets the current bearer token for HTTP requests.
     */
    public static String getBearerToken() {
        return bearerToken;
    }

    /**
     * Sets the bearer token.
     */
    public static void setBearerToken(String token) {
        bearerToken = token;
    }
}
