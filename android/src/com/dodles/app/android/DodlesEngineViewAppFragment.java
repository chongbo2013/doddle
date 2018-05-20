package com.dodles.app.android;


import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidFragmentApplication;
import com.dodles.gdx.dodleengine.DodleEngine;
import com.dodles.gdx.dodleengine.DodleEngineConfig;
import com.dodles.gdx.dodleengine.events.EventBus;
import com.dodles.gdx.dodleengine.events.EventData;
import com.dodles.gdx.dodleengine.events.EventSubscriber;
import com.dodles.gdx.dodleengine.events.EventTopic;
import com.dodles.gdx.dodleengine.events.EventType;
import com.dodles.mobileinterop.DaggerInteropDodleEngineComponent;
import com.dodles.mobileinterop.InteropDodleEngineComponent;
import com.dodles.mobileinterop.InteropEditorPersistence;

import java.util.EnumSet;

public class DodlesEngineViewAppFragment extends AndroidFragmentApplication {

    // todo: additional integration from the previous implementation of AndroidLauncher.java - Clint 2017.07.19
    // - functions for handling file download and management events (probably shouldn't be in this class)
    // - functionality for launching from a URL? wouldn't be added back to this class, and possibly not needed with
    //   new react based implementation, but it might be useful for launching the app from a custom URL scheme
    // - getter() for the event bus - do we still need it? who actually uses it?

    //region Properties & Variables

    private DodleEngine engine;
    private EventBus eventBus;
    private InteropEditorPersistence persistence;
    private EventSubscriber reactEventSubscriber;

    //endregion Properties & Variables


    //region Fragment Overrides

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Android Application Setup
        // todo: determine where these belong MainActivity / MainApplication class - Clint 2017.07.19
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        // libGDX Application Configuration
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        // todo: this line causes the emulator to crash, but seems to be needed for shape rendering - Clint 2017.07.19
        if (!isEmulator()) {
            config.stencil = 8;
        }
        config.hideStatusBar = true;
        config.useImmersiveMode = true;

        // Initialize the Dodle Engine
        // todo: the dagger2 integration seems non-standard compared to the way I've seen it online. We may want to
        //       refactor to make it easier to maintain - Clint 2017.07.19
        InteropDodleEngineComponent engineComponent = DaggerInteropDodleEngineComponent.create();
        engine = engineComponent.engine();

        // Initial height and width configuration
        // todo: Without this, the DodleEngine throws a GdxRuntimeException in the create(). However, this shouldn't be
        //       necessary for the libGDX application, as the view is going to be resized soon anyways, and all the
        //       engine creation code shouldn't be so tightly coupled on having the fixed frame dimensions set up
        //       initially. Initial tests (android, ios) seem to indicate that its safe to remove that exception, but I
        //       haven't tested the full app, and haven't tested other platforms. - Clint 2017.07.19
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        engine.resize(width, height);

        // todo: Set Logger
        //       The previous implementation used the InteropLogger in order to pass the log messages to the Webview
        //       implementation (and maybe then to the server?). Their whole system seemed over-engineered, but the
        //       there still might be value in creating our logger class in order to pass messages up to the react
        //       code, if the react code can expose that in debug mode or if its going to send that to the server
        //       - Clint 2017.09.17
        // DodleEngine.setLogger(new InteropLogger(this));

        // Save reference to Event Bus
        eventBus = engine.getEventBus();

        // Subscribe to Engine Events
        if (reactEventSubscriber != null) {
            eventBus.addSubscriber(reactEventSubscriber);
        }
        eventBus.addSubscriber(new EventSubscriber(EventTopic.DEFAULT) {
            @Override
            public void listen(EventTopic topic, EventType eventType, EventData data) {

                switch (eventType) {
                    case STATE_GO:
                        error("Deprecated", "The EventType 'STATE_GO' has been deprecated. - Clint 2017.09.19");
                        break;
                    case ENGINE_INITIALIZED:
                        log("Integration Incomplete", "The EventType 'ENGINE_INITIALIZED' may not have been fully integrated. - Clint 2017.09.19");
                        engine.getEngineConfig().setOptions(EnumSet.of(
                                //DodleEngineConfig.Options.FULL_EDITOR,
                                DodleEngineConfig.Options.USER_MOVE_VIEWPORT
                        ));
                        persistence = new InteropEditorPersistence(engine);
                        //requestLoadWhenReady(dodleIdToLoadOnInit);
                        //dodleIdToLoadOnInit = null;
                        break;
                    case TOGGLE_SLIDE_OUT:
                        error("Integration Incomplete", "The EventType 'TOGGLE_SLIDE_OUT' has not been fully integrated");
                        //openDrawer();
                        break;
                    case INTERNAL_LOAD_SUCCESS:
                    case CALLBACK_IMPORT_DODLE:
                    case LAUNCH_EDITOR:
                        error("Integration Incomplete", "The EventType '" + eventType.name() + "' has not been fully integrated");
                        //if (EventType.INTERNAL_LOAD_SUCCESS.equals(eventType)) {
                        //    makeToast("Your dodle has been loaded.");
                        //}
                        //runOnUiThread(new Runnable() {
                        //    @Override
                        //    public void run() {
                        //        flipper.setDisplayedChild(0);
                        //        resetToolBar();
                        //    }
                        //});
                        break;
                    case KICK_OFF_IMPORT:
                        error("Integration Incomplete", "The EventType 'KICK_OFF_IMPORT' has not been fully integrated");
                        //launchFilePicker();
                        break;
                    case MAKE_TOAST:
                        error("Integration Incomplete", "The EventType 'MAKE_TOAST' has not been fully integrated");
                        //makeToast(data.getSecondStringParam());
                        break;
                    case UPDATE_BEARER_TOKEN:
                        error("Integration Incomplete", "The EventType 'UPDATE_BEARER_TOKEN' has not been fully integrated");
                        //sendMessageToEventBus("publishEventFromRemote", EventTopic.DEFAULT.name(), eventType.name(), data.getFirstStringParam(), null, null, null);
                        //ApplicationState.setBearerToken(data.getFirstStringParam());
                        break;
                    case SHARE_VIA_TWITTER:
                        error("Integration Incomplete", "The EventType 'SHARE_VIA_TWITTER' has not been fully integrated");
                        //openActivity(data.getFirstStringParam(), data.getSecondStringParam(), "com.twitter.android", "Twitter");
                        break;
                    case SHARE_VIA_INSTAGRAM:
                        error("Integration Incomplete", "The EventType 'SHARE_VIA_INSTAGRAM' has not been fully integrated");
                        //openActivity(data.getFirstStringParam(), data.getSecondStringParam(), "com.instagram.android", "Instagram");
                        break;
                    case SHARE_VIA_SMS:
                        error("Integration Incomplete", "The EventType 'SHARE_VIA_SMS' has not been fully integrated");
                        //Intent intent = new Intent(Intent.ACTION_SEND);
                        //intent.putExtra(Intent.EXTRA_TEXT, data.getFirstStringParam());
                        //intent.setData(Uri.parse("sms:"));
                        //intent.putExtra("sms_body", data.getFirstStringParam());
                        //intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(getBitmapFromURL(data.getSecondStringParam())));
                        //intent.setType("image/png");
                        //startActivity(intent);
                        break;
                    case SHARE_VIA_EMAIL:
                        error("Integration Incomplete", "The EventType 'SHARE_VIA_EMAIL' has not been fully integrated");
                        //Intent intent = new Intent(Intent.ACTION_SEND);
                        //intent.putExtra(Intent.EXTRA_SUBJECT, data.getFirstStringParam());
                        //intent.putExtra(Intent.EXTRA_TEXT, data.getSecondStringParam());
                        //intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(getBitmapFromURL(data.getThirdStringParam())));
                        //intent.setType("string/text");
                        //startActivity(intent);
                        break;
                }

            }
        });

        log("Initialization", "complete");

        // Return created view
        return initializeForView(engine, config);
    }

    //endregion Fragment Overrides


    //region Getters & Setters

    public EventSubscriber getReactEventSubscriber() {
        return reactEventSubscriber;
    }

    public void setReactEventSubscriber(EventSubscriber reactEventSubscriber) {
        this.reactEventSubscriber = reactEventSubscriber;
    }

    //endregion Getters & Setters


    //region Event Passing

    public void sendEventToEngine(final String topic, final String type, final String data) {

        // Run on the main rendering thread, so that any resultant state changes run on the core loop
        this.postRunnable(new Runnable() {
            @Override
            public void run() {
                eventBus.publish(EventTopic.valueOf(topic), EventType.valueOf(type), data);
            }
        });

    }

    //endregion Event Passing

    //region Private Helpers
    private static boolean isEmulator() {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
    }
}
