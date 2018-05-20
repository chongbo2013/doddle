package com.dodles.gdx.dodleengine.logging;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * Reimplementation of libgdx FPSLogger, providing additional options on how the Frames Per Second calculation is
 * is accessed.
 */
public final class DodlesFPSLogger {
    private long startTime;
    private int lastFPS = -1;
    private boolean active = false;
    private boolean logFPS = false;

    public DodlesFPSLogger(boolean active, boolean logFPS) {
        this.active = active;
        this.logFPS = logFPS;
        startTime = TimeUtils.nanoTime();
    }

    /** Logs the current frames per second to the console. */
    public int updateFPS() {
        if (active) {
            if (TimeUtils.nanoTime() - startTime > 1000000000) /* 1,000,000,000ns == one second */ {
                lastFPS = Gdx.graphics.getFramesPerSecond();
                if (logFPS) {
                    Gdx.app.log("DodlesFPSLogger", "fps: " + Gdx.graphics.getFramesPerSecond());
                }
                startTime = TimeUtils.nanoTime();
            }
        }
        return lastFPS;
    }

    /**
     * Returns the last computed FPS calculation.
     * @return the last computed FPS calculation.
     */
    public int getLastFPS() {
        return lastFPS;
    }

    /**
     * Returns boolean indicating whether the calculated FPS should be logged to the libgdx logger.
     * @return boolean indicator of whether the calculated FPS should be automatically logged.
     */
    public boolean isLogging() {
        return logFPS;
    }

    /**
     * Enables or disables automatic logging of the FPS each time it is calculated.
     * @param logging boolean indicator of whether to log FPS to the libgdx logger.
     */
    public void setLogging(boolean logging) {
        this.logFPS = logging;
    }

    /**
     * Returns boolean indicating whether the logger is actively computing the FPS.
     * @return boolean indicator of whether the logger is activately computing the FPS.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Enables or disables FPS calculation.
     * @param active boolean indicator of whether the FPS is currently being calculated.
     */
    public void setActive(boolean active) {
        this.active = active;
        if (active) {
            lastFPS = -1;
            startTime = TimeUtils.nanoTime();
        }
    }
}
