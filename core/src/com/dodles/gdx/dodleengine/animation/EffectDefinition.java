package com.dodles.gdx.dodleengine.animation;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.assets.SpeechAssets;
import static com.dodles.gdx.dodleengine.assets.StringAssets.ANIMATION_DEFAULT_EFFECT_DEFINITIONS;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.objects.PhaseAttributeType;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.objects.PhaseStepType;
import com.dodles.gdx.dodleengine.util.JsonUtility;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Definition format for effects.
 */
public class EffectDefinition {
    public static final String JSON_NAME = "effectDefinition";
    private String displayName;
    private EffectOperation operation = EffectOperation.NONE;
    private ArrayList<BaseKeyframe> keyframes = new ArrayList<BaseKeyframe>();
    private HashMap<EffectParameterType, Object> defaultParameters = new HashMap<EffectParameterType, Object>();
    
    public EffectDefinition(JsonValue json) {
        displayName = json.getString("displayName");
        
        JsonValue jsonKeyframes = json.get("keyframes");
        if (jsonKeyframes != null) {
            for (int i = 0; i < jsonKeyframes.size; i++) {
                keyframes.add(BaseKeyframe.parseKeyframe(jsonKeyframes.get(i)));
            }
        }
        
        JsonValue jsonOperation = json.get("operation");
        if (jsonOperation != null) {
            operation = EffectOperation.valueOf(jsonOperation.asString());
        }
        
        JsonValue jsonDefaultParameters = json.get("defaultParameters");
        for (int i = 0; i < jsonDefaultParameters.size; i++) {
            EffectParameterType ept = EffectParameterType.getByJsonKey(jsonDefaultParameters.get(i).name);
            defaultParameters.put(ept, JsonUtility.getObject(jsonDefaultParameters.get(i)));
        }
    }
    
    public EffectDefinition(String displayname, List<BaseKeyframe> keyframes, EffectOperation operation, HashMap<EffectParameterType, Object> defaultParameters) {
        this.displayName = displayname;
        this.operation = operation;
        
        if (keyframes != null) {
            this.keyframes = new ArrayList<BaseKeyframe>(keyframes);
        }
        
        if (defaultParameters != null) {
            this.defaultParameters = defaultParameters;
        }
    }
    
    /**
     * Returns the default effect definitions.
     */
    public static Map<EffectType, Map<String, EffectDefinition>> getDefaultDefinitions(AssetProvider assetProvider) {
        HashMap<EffectType, Map<String, EffectDefinition>> result = new HashMap<EffectType, Map<String, EffectDefinition>>();
        JsonValue types = new JsonReader().parse(assetProvider.getString(ANIMATION_DEFAULT_EFFECT_DEFINITIONS));
        
        for (EffectType effectType : EffectType.values()) {
            LinkedHashMap<String, EffectDefinition> curDefinitions = new LinkedHashMap<String, EffectDefinition>();
            result.put(effectType, curDefinitions);
                
            if (!effectType.isDynamic()) {
                JsonValue definitions = types.get(effectType.name());    

                for (JsonValue definition : definitions.iterator()) {
                    curDefinitions.put(definition.name, new EffectDefinition(definition));
                }
            } else {
                switch (effectType) {
                    case EMOTION:
                    case ANGLE:
                        // TODO: Refactor this out into some sort of provider model, especially if we have more than one "dynamic" class going forward?
                        PhaseAttributeType attr = PhaseAttributeType.EMOTION;

                        if (effectType != EffectType.EMOTION) {
                            attr = PhaseAttributeType.ANGLE;
                        }

                        for (PhaseStepType stepType : PhaseStepType.getByAttributeType(attr)) {
                            HashMap<EffectParameterType, Object> parameters = new HashMap<EffectParameterType, Object>();

                            parameters.put(EffectParameterType.PHASE_ATTRIBUTE, attr.name());
                            parameters.put(EffectParameterType.PHASE_STEP, stepType.name());

                            curDefinitions.put(stepType.name(), new EffectDefinition(stepType.getDescription(), null, EffectOperation.CHANGE_PHASE_ATTRIBUTE, parameters));
                        }
                        break;
                        
                    case AUDIO:
                        for (SpeechAssets sound : SpeechAssets.values()) {
                            HashMap<EffectParameterType, Object> parameters = new HashMap<EffectParameterType, Object>();

                            parameters.put(EffectParameterType.SOUND_ASSET, sound.name());
                            parameters.put(EffectParameterType.LENGTH, sound.getLength());
                            
                            ArrayList<PhonemeTiming> phonemeTimings = new ArrayList<PhonemeTiming>();
                            
                            JsonValue data = new JsonReader().parse(assetProvider.getPhonemeJson(sound));
                            for (JsonValue entry : data.iterator()) {
                                phonemeTimings.add(new PhonemeTiming(entry));
                            }
                            
                            ArrayList<BaseKeyframe> visemeKeyframes = new ArrayList<BaseKeyframe>();
                            Viseme curViseme = Viseme.M_B_P;
                            boolean lastWasDifferent = false;
                            
                            for (PhonemeTiming timing : phonemeTimings) {
                                lastWasDifferent = false;
                                
                                if (curViseme == null || curViseme != timing.phoneme.getViseme()) {
                                    visemeKeyframes.add(new VisemeKeyframe(sound.getLength() * 100, timing.start, curViseme));
                                    curViseme = timing.phoneme.getViseme();
                                    lastWasDifferent = true;
                                }
                            }
                            
                            PhonemeTiming lastTiming = phonemeTimings.get(phonemeTimings.size() - 1);
                            
                            if (lastWasDifferent) {
                                visemeKeyframes.add(new VisemeKeyframe(sound.getLength() * 100, lastTiming.end, lastTiming.phoneme.getViseme()));
                            }
                            
                            // Add explicit rest viseme if not in rest state at end...
                            if (lastTiming.phoneme.getViseme() != Viseme.M_B_P) {
                                visemeKeyframes.add(new VisemeKeyframe(sound.getLength() * 100, lastTiming.end + 1, Viseme.M_B_P));
                            }
                            
                            curDefinitions.put(sound.name(), new EffectDefinition(sound.getDisplayName(), visemeKeyframes, EffectOperation.PLAY_AUDIO, parameters));
                        }
                        break;
                        
                    default:
                        throw new GdxRuntimeException("Unrecognized effect type: " + effectType.name());
                }
            }
        }
        
        return result;
    }
    
    /**
     * Parses the JSON phoneme timing format.
     */
    private static class PhonemeTiming {
        private Phoneme phoneme;
        private float start;
        private float end;
        
        public PhonemeTiming(JsonValue data) {
            phoneme = Phoneme.valueOf(data.getString("phoneme").replace("+", ""));
            start = data.getFloat("start");
            end = data.getFloat("end");
        }
    }
    
    /**
     * write out a config snippet into the Json object.
     * @param json
     */
    public final void writeConfig(Json json) {
        json.writeObjectStart(JSON_NAME);
        json.writeValue("displayName", displayName);
        if (operation != null) {
            json.writeValue("operation", operation.name());
        }
        if (keyframes.size() > 0) {
            json.writeArrayStart("keyframes");
            for (BaseKeyframe frame : keyframes) {
                json.writeObjectStart();
                frame.writeConfig(json);
                json.writeObjectEnd();
            }
            json.writeArrayEnd();
        }
        if (!defaultParameters.isEmpty()) {
            json.writeObjectStart("defaultParameters");
            for (HashMap.Entry<EffectParameterType, Object> param : defaultParameters.entrySet()) {
                json.writeValue(param.getKey().getJsonKey(), param.getValue());
            }
            json.writeObjectEnd();
        }
        json.writeObjectEnd();
    }

    public final void writeConfigWithoutKeyFrames(Json json) {
        json.writeObjectStart(JSON_NAME);
        json.writeValue("displayName", displayName);
        if (operation != null) {
            json.writeValue("operation", operation.name());
        }
        if (!defaultParameters.isEmpty()) {
            json.writeObjectStart("defaultParameters");
            for (HashMap.Entry<EffectParameterType, Object> param : defaultParameters.entrySet()) {
                json.writeValue(param.getKey().getJsonKey(), param.getValue());
            }
            json.writeObjectEnd();
        }
        json.writeObjectEnd();
    }


    /**
     * Returns the display name of the effect.
     */
    public final String getDisplayName() {
        return displayName;
    }
    
    /**
     * Returns the keyframes of the effect.
     */
    public final List<BaseKeyframe> getKeyframes() {
        return keyframes;
    }
    
    /**
     * Returns the "operation" (if any) performed by this effect.
     */
    public final EffectOperation getOperation() {
        return operation;
    }

    /**
     * Returns the default parameters for this effect.
     */
    public final Map<EffectParameterType, Object> getDefaultParameters() {
        return defaultParameters;
    }
}
