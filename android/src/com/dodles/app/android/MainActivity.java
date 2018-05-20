package com.dodles.app.android;

import com.badlogic.gdx.backends.android.AndroidFragmentApplication;
import com.facebook.react.ReactFragmentActivity;

public class MainActivity extends ReactFragmentActivity implements AndroidFragmentApplication.Callbacks {

    /**
     * Returns the name of the main component registered from JavaScript.
     * This is used to schedule rendering of the component.
     */
    @Override
    protected String getMainComponentName() {
        return "mainactivity";
    }

    /**
     * Implementation of AndroidFragmentApplication.Callbacks
     */
    @Override
    public void exit() {

    }
}
