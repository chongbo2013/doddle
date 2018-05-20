package com.dodles.app.android;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;

import javax.annotation.Nullable;
import java.util.Map;

public class DodlesEngineViewManager extends SimpleViewManager<DodlesEngineView> {

    public static final String REACT_CLASS = "DodlesEngineView";
    public static final int COMMAND_SEND_EVENT_TO_ENGINE = 1;

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    protected DodlesEngineView createViewInstance(ThemedReactContext reactContext) {
        return new DodlesEngineView(reactContext);
    }

    @Override
    public @Nullable Map <String,Object> getExportedCustomDirectEventTypeConstants() {
        return MapBuilder.of(
            "ready",
            (Object) MapBuilder.of("registrationName", "onReady"),
            "onEngineEvent",
            (Object) MapBuilder.of("registrationName", "onEngineEvent")
        );
    }

    @Override
    public Map<String, Integer> getCommandsMap() {
        return MapBuilder.of(
                "sendEventToEngine",
                COMMAND_SEND_EVENT_TO_ENGINE
        );
    }

    @Override
    public void receiveCommand(DodlesEngineView dodleEngineView, int commandId, @Nullable ReadableArray args) {
        switch(commandId) {
            case COMMAND_SEND_EVENT_TO_ENGINE:
                dodleEngineView.sendEventToEngine(args.getString(0), args.getString(1), args.getString(2));
        }
    }
}
