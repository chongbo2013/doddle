package com.dodles.gdx.dodleengine;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import de.hypergraphs.hyena.core.shared.data.UUID;
import java.util.Collection;
import java.util.HashMap;

/**
 * Assists in mapping IDs between copied objects.
 */
public class IdDatabase {
    private HashMap<String, String> oldToNewIDs = new HashMap<String, String>();
    private HashMap<String, String> newToOldIDs = new HashMap<String, String>();
    
    public IdDatabase() {
        this(new HashMap<String, String>());
    }
    
    public IdDatabase(HashMap<String, String> db) {
        this.oldToNewIDs = oldToNewIDs;
        
        for (String key : db.keySet()) {
            newToOldIDs.put(db.get(key), key);
        }
    }
    
    public IdDatabase(JsonValue json) {
        for (JsonValue t : json.iterator()) {
            oldToNewIDs.put(t.name(), t.asString());
            newToOldIDs.put(t.asString(), t.name());
        }
    }
    
    /**
     * Returns a new id that maps to the passed in id.
     */
    public final String getNewID(String oldID) {
        if (oldID == null) {
            return null;
        }
        
        if (!oldToNewIDs.containsKey(oldID)) {
            String newUUID = UUID.uuid().toString();
            oldToNewIDs.put(oldID, newUUID);
            newToOldIDs.put(newUUID, oldID);
        }
        
        return oldToNewIDs.get(oldID);
    }
    
    /**
     * Returns the old ID of the given ID.
     */
    public final String getOldID(String newID) {
        return newToOldIDs.get(newID);
    }
    
    /**
     * Returns a new map with new IDs for all entries in the map.
     */
    public final HashMap<String, String> getNewMap(HashMap<String, String> map) {
        HashMap<String, String> result = new HashMap<String, String>();
        
        for (String key : map.keySet()) {
            result.put(getNewID(key), getNewID(map.get(key)));
        }
        
        return result;
    }

    /**
     * returns the DB in libgdx json form for serialization -- avoid the "db" in the idDB that libgdx wants to pu
     * into the output.
     * @return
     */
    public final Json writeConfig(Json json) {
        for (String key : oldToNewIDs.keySet()) {
            json.writeValue(key, oldToNewIDs.get(key));
        }
        return json;
    }
    
    /**
     * Returns the new IDs in the database.
     */
    public final Collection<String> getNewIDs() {
        return oldToNewIDs.values();
    }
}
