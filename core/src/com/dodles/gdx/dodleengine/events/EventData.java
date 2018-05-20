package com.dodles.gdx.dodleengine.events;

import java.util.ArrayList;
import java.util.List;

/**
 * Describes an events parameters.
 */
public class EventData {
    private List<String> parameters;

    public EventData() {
        this(new ArrayList<String>());
    }

    public EventData(List<String> parameters) {
        this.parameters = parameters;
    }
    
    /**
     * Returns the event parameters.
     */
    public final List<String> getParameters() {
        return parameters;
    }

    /**
     * Returns a string parameter in the parameters array (if any).
     */
    public final String getStringParam(int i) {
        int index = i - 1;
        if (index > 0 && parameters.size() > index) {
            return parameters.get(index);
        }

        return null;
    }

    /**
     * Returns the first string parameter in the parameters array (if any).
     */
    public final String getFirstStringParam() {
        if (parameters.size() > 0) {
            return parameters.get(0);
        }
        
        return null;
    }

    /**
     * Returns the second string parameter.
     */
    public final String getSecondStringParam() {
        if (parameters.size() > 1) {
            return parameters.get(1);
        }
        return null;
    }

    /**
     * return the third string parameter or null.
     * @return
     */
    public final String getThirdStringParam() {
        if (parameters.size() > 2) {
            return parameters.get(2);
        }
        return null;
    }

    /**
     * return the fourth string parameter or null.
     * @return
     */
    public final String getFourthStringParam() {
        if (parameters.size() > 3) {
            return parameters.get(3);
        }
        return null;
    }

    @Override
    public final String toString() {
        int paramCt = 0;
        StringBuilder paramString = new StringBuilder();
        for (String param : parameters) {
            paramCt++;
            paramString.append(Integer.toString(paramCt)).append(": \"").append(param).append("\"");
            if (paramCt < parameters.size()) {
                paramString.append(", ");
            }
        }
        return paramString.toString();
    }

    public final String forReact() {
        StringBuilder paramString = new StringBuilder();
        boolean firstParam = true;
        for (String param : parameters) {
            if (!firstParam)
                paramString.append("|");
            else
                firstParam = false;
            paramString.append(param);
        }
        return paramString.toString();
    }
}
