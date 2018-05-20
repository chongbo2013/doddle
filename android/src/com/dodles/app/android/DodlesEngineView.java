package com.dodles.app.android;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.FragmentManager;
import android.widget.FrameLayout;
import com.dodles.app.R;
import com.dodles.gdx.dodleengine.events.EventData;
import com.dodles.gdx.dodleengine.events.EventSubscriber;
import com.dodles.gdx.dodleengine.events.EventTopic;
import com.dodles.gdx.dodleengine.events.EventType;
import com.facebook.react.ReactFragmentActivity;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.RCTEventEmitter;

/**
 * Custom View that exposes the libGDX-based Dodles Engine view.
 */
public class DodlesEngineView extends FrameLayout {

    private DodlesEngineViewAppFragment dodlesEngineViewAppFragment;

    public DodlesEngineView(Context context) {
        super(context);
        // todo: set background to loading animation until the engine is loaded up
        setBackgroundColor(Color.YELLOW);
        init();
    }

    public ReactContext getReactContext() {
        return (ReactContext) getContext();
    }

    public void init() {
        inflate(getContext(), R.layout.customview_layout, this);
        dodlesEngineViewAppFragment = new DodlesEngineViewAppFragment();

        // Listen for engine events, and propogate them to the react code
        dodlesEngineViewAppFragment.setReactEventSubscriber(new EventSubscriber() {
            @Override
            public void listen(EventTopic eventTopic, EventType eventType, EventData data) {
                WritableMap event = Arguments.createMap();
                event.putString("topic", eventTopic.toString());
                event.putString("type", eventType.toString());
                event.putString("data", ((data != null && data.getParameters().size() > 0) ? data.forReact() : ""));
                ReactContext reactContext = (ReactContext)getContext();
                reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "onEngineEvent", event);
            }
        });
    }

    @Override
    protected void onAttachedToWindow() {
        FragmentManager fragmentManager =
                ((ReactFragmentActivity) getReactContext().getCurrentActivity()).getSupportFragmentManager();
        fragmentManager.beginTransaction().add(getId(), dodlesEngineViewAppFragment).commit();
        playerViewDidBecomeReady();
    }

    @Override
    protected void onDetachedFromWindow() {
        if (getReactContext().getCurrentActivity() != null) {
            FragmentManager fragmentManager =
                    ((ReactFragmentActivity) getReactContext().getCurrentActivity()).getSupportFragmentManager();
            if (dodlesEngineViewAppFragment != null) {
                fragmentManager.beginTransaction().remove(dodlesEngineViewAppFragment).commit();
            }
        }
        super.onDetachedFromWindow();
    }

    public void playerViewDidBecomeReady() {
        WritableMap event = Arguments.createMap();
        event.putInt("target", getId());
        ReactContext reactContext = (ReactContext) getContext();
        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "ready", event);
    }

    public void sendEventToEngine(String topic, String type, String data) {
        dodlesEngineViewAppFragment.sendEventToEngine(topic, type, data);
    }

}
