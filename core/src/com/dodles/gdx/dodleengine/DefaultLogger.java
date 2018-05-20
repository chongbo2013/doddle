package com.dodles.gdx.dodleengine;

import com.badlogic.gdx.Gdx;

/**
 * Default logger implementation that uses the default GDX logger.
 */
public class DefaultLogger implements Logger {
    // CHECKSTYLE.OFF: DesignForExtension - need to extend this class, and no need to make it fancy...
    @Override
    public void debug(String tag, String message) {
        Gdx.app.debug(tag, message);
    }

    @Override
    public void log(String tag, String message) {
        System.out.println("Gdx App is " + (Gdx.app == null ? "null" : Gdx.app.toString() ));
        Gdx.app.log(tag, message);
    }

    @Override
    public void error(String tag, String message) {
        error(tag, message, null);
    }

    @Override
    public void error(String tag, String message, Throwable t) {
        if (t != null) {
            Gdx.app.error(tag, message, t);
        } else {
            Gdx.app.error(tag, message);
        }
    }
    // CHECKSTYLE.ON: DesignForExtension
}
