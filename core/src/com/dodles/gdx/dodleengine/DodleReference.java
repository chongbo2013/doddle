package com.dodles.gdx.dodleengine;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import java.util.HashMap;
import java.util.HashSet;

/**
 * A structure mapping an external dodleID to it's internal tracking ID.
 */
public class DodleReference {
    private String trackingID;
    private String dodleID;
    private HashSet<String> references = new HashSet<String>();
    
    public DodleReference(String trackingID, String dodleID) {
        this.trackingID = trackingID;
        this.dodleID = dodleID;
    }
    
    public DodleReference(JsonValue value) {
        this(value.getString("trackingID"), value.getString("dodleID"));
    }
    
    /**
     * Loads the json references into a hashmap.
     */
    public static HashMap<String, DodleReference> loadReferences(JsonValue json) {
        HashMap<String, DodleReference> result = new HashMap<String, DodleReference>();
        
        if (json.has("references")) {
            JsonValue referencesJson = json.get("references");
            
            for (int i = 0; i < referencesJson.size; i++) {
                DodleReference newRef = new DodleReference(referencesJson.get(i));
                result.put(newRef.getTrackingID(), newRef);
            }
        }
        
        return result;
    }
    
    /**
     * Returns the internal tracking ID.
     */
    public final String getTrackingID() {
        return trackingID;
    }
    
    /**
     * Returns the external DodleID.
     */
    public final String getDodleID() {
        return dodleID;
    }
    
    /**
     * Returns the number of references to this dodle in the current dodle.
     */
    public final int getRefCount() {
        return references.size();
    }
    
    /**
     * Adds a reference to the counter.
     */
    public final void addRef(String id) {
        references.add(id);
    }
    
    /**
     * Removes a reference from the counter.
     */
    public final void decRef(String id) {
        references.remove(id);
    }
    
    /**
     * Writes the dodle reference to json.
     */
    public final void writeConfig(Json json) {
        json.writeValue("trackingID", trackingID);
        json.writeValue("dodleID", dodleID);
    } 
}
