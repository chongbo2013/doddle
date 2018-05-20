package com.dodles.gdx.dodleengine.util;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import java.util.ArrayList;

/**
 * Utility functions for reading/writing JSON.
 */
public final class JsonUtility {
    private JsonUtility() {
    }
    
    /**
     * Reads an array of vectors.
     */
    public static ArrayList<Vector2> readVectorArray(JsonValue jsonValue) {
        ArrayList<Vector2> result = new ArrayList<Vector2>();
        
        for (int i = 0; i < jsonValue.size; i++) {
            JsonValue curValue = jsonValue.get(i);
            result.add(readVector(curValue));
        }
        
        return result;
    }
    
    /**
     * Reads a Vector from JSON without using reflection.
     */
    public static Vector2 readVector(JsonValue jsonValue) {
        return new Vector2(jsonValue.getFloat("x", 0), jsonValue.getFloat("y", 0));
    }
    
    /**
     * Writes a vector array without using reflection.
     */
    public static void writeVectorArray(ArrayList<Vector2> vectors, Json json, String name) {
        json.writeArrayStart(name);
        
        for (Vector2 vector : vectors) {
            json.writeObjectStart();
            writeVector(vector, json);
            json.writeObjectEnd();
        }
        
        json.writeArrayEnd();
    }
    
    /**
     * Writes a vector.
     */
    public static void writeVector(Vector2 vector, Json json) {
        json.writeValue("x", vector.x);
        json.writeValue("y", vector.y);
    }
    
    /**
     * Returns the value of the json object as an untyped Object.
     */
    public static Object getObject(JsonValue value) {
        if (value.isNumber()) {
            return value.asFloat();
        } else if (value.isString()) {
            return value.asString();
        } else {
            throw new GdxRuntimeException("Not Currently Supported.");
        }
    }
    
    /**
     * Reads an array of strings.
     */
    public static ArrayList<String> readStringArray(JsonValue jsonValue) {
        ArrayList<String> result = new ArrayList<String>();
        
        for (int i = 0; i < jsonValue.size; i++) {
            JsonValue curValue = jsonValue.get(i);
            if (curValue.isObject()) {
                // Handle horrible libgdx serialization method
                result.add(curValue.getString("value"));
            } else {
                result.add(curValue.asString());
            }
        }
        
        return result;
    }
}
