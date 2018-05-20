package com.dodles.mobileinterop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Base64Coder;
import com.dodles.gdx.dodleengine.DodleEngine;
import com.dodles.gdx.dodleengine.DodleEngine.ScreenshotCallback;
import com.dodles.gdx.dodleengine.events.EventData;
import com.dodles.gdx.dodleengine.events.EventSubscriber;
import com.dodles.gdx.dodleengine.events.EventTopic;
import com.dodles.gdx.dodleengine.events.EventType;

/**
 * Editor persistence helper methods for android/ios interop.
 */
public class InteropEditorPersistence {
    private final DodleEngine engine;

    private static final String PERSISTENCE_PREFS_NAME = "editorpersistence";
    private static final String ACTIVE_DODLE_ID_PROPERTY = "dodleid";
    private static final String ACTIVE_DODLE_JSON_PROPERTY = "dodlejson";

    public InteropEditorPersistence(final DodleEngine engine) {
        this.engine = engine;

        engine.getEventBus().addSubscriber(new EventSubscriber(EventTopic.EDITOR) {
            @Override
            public void listen(EventTopic eventTopic, EventType eventType, EventData data) {
                switch (eventType) {
                    case SAVE_DODLE:
                        saveDodle();
                        break;
                    case LOAD_DODLE:
                        loadDodle(data.getFirstStringParam());
                        break;
                    case DODLE_CREATE_NEW:
                        System.out.println("InteropEditorPersistence::DODLE_CREATE_NEW - not yet implemented");
                        break;
                }
            }
        });

        engine.getEventBus().addSubscriber(new EventSubscriber(EventTopic.DEFAULT) {
            @Override
            public void listen(EventTopic topic, EventType eventType, final EventData data) {
                switch (eventType) {
                    case HOST_ENGINE_TRASH_STATE:
                        Preferences prefs = Gdx.app.getPreferences(PERSISTENCE_PREFS_NAME);
                        prefs.clear();
                        prefs.flush();
                        break;
                    case COMMAND_STACK_CHANGED:
                        writePref(ACTIVE_DODLE_JSON_PROPERTY, engine.getStateManager().exportGraphJson());
                        break;
                    case  INTERNAL_LOAD_SUCCESS:
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                loadDodle(data.getFirstStringParam(), data.getParameters().get(1));
                            }
                        });
                        break;
                    case PRINT_JSON:
                        String jsonExport = engine.getStateManager().exportGraphJson();
                        System.out.println(jsonExport);
                }
            }
        });

        loadDodle(readPref(ACTIVE_DODLE_ID_PROPERTY), readPref(ACTIVE_DODLE_JSON_PROPERTY));
    }

    /**
     * Saves a dodle.
     */
    public final void saveDodle() {
        engine.takeScreenshot(new ScreenshotCallback() {
            @Override
            public void call(String screenshotData) {
                FileHandle fileHandle;
                String dodleJson = engine.getStateManager().exportGraphJson();
                String id = engine.getObjectManager().getTrackingID();

                fileHandle = Gdx.files.local(getDodleFilename(id, ".png"));
                // TODO: eliminate the encoding / decoding from this step (in PixmapScreenGrabber). - CAD 2017.09.25
                fileHandle.writeBytes(Base64Coder.decode(screenshotData), false);

                fileHandle = Gdx.files.local(getDodleFilename(id, ".json"));
                fileHandle.writeString(dodleJson, false);

                writePref(ACTIVE_DODLE_ID_PROPERTY, id);

                engine.getEventBus().publish(EventTopic.DEFAULT, EventType.DODLE_SAVE_SUCCESS, id);
            }
        });
    }

    /**
     * Loads a dodle.
     */
    public final void loadDodle(String dodleID) {
        FileHandle fileHandle = Gdx.files.local(getDodleFilename(dodleID, ".json"));
        if (fileHandle.exists()) {
            String json = fileHandle.readString();
            loadDodle(dodleID, json);
        } else {
            engine.getEventBus().publish(EventTopic.DEFAULT, EventType.DODLE_LOAD_FAILURE, dodleID);
        }
    }

    public final void loadDodle(String dodleID, String json) {
        writePref(ACTIVE_DODLE_ID_PROPERTY, dodleID);

        if (json == null || json.isEmpty()) {
            engine.getStateManager().resetState();
        } else {
            engine.getStateManager().importJson(json);
        }

        engine.getEventBus().publish(EventTopic.DEFAULT, EventType.DODLE_LOAD_SUCCESS, dodleID);
    }

    private String getDodleFilename(String dodleID, String fileExtension) {
        return "saves/" + dodleID + "/" + dodleID + fileExtension;
    }

    private String readPref(String prefName) {
        Preferences pref = Gdx.app.getPreferences(PERSISTENCE_PREFS_NAME);

        if (pref == null) {
            return null;
        }

        return pref.getString(prefName);
    }

    private void writePref(String prefName, String value) {
        Preferences pref = Gdx.app.getPreferences(PERSISTENCE_PREFS_NAME);
        pref.putString(prefName, value);
        pref.flush();
    }
}
