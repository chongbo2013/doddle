package com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.objects.PhaseSchema;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.objects.PhaseAttribute;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.objects.PhaseAttributeType;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.objects.PhaseStep;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.objects.PhaseStepType;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.objects.PhaseType;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Constants for the phase interface.
 */
public final class PhaseConstants {
    public static final float PANEL_INSTANCE_DURATION = 0f;
    public static final float PANEL_EASE_DURATION = 0.25f;

    public static final PhaseSchema MOUTH_PHASE = new PhaseSchema(PhaseType.MOUTH) { {
        setAttributeList(new ArrayList<PhaseAttribute>(Arrays.asList(
                new PhaseAttribute(PhaseAttributeType.ANGLE) { {
                    setPhaseSteps(PhaseStepType.getByAttributeType(PhaseAttributeType.ANGLE));
                } },
                new PhaseAttribute(PhaseAttributeType.EMOTION) { {
                    setPhaseSteps(PhaseStepType.getByAttributeType(PhaseAttributeType.EMOTION));
                } },
                new PhaseAttribute(PhaseAttributeType.TALK) { {
                    setPhaseSteps(PhaseStepType.getByAttributeType(PhaseAttributeType.TALK));
                } }
        )));
    } };

    public static final PhaseSchema GENERIC_PHASE = new PhaseSchema(PhaseType.GENERIC) { {
        setAttributeList(new ArrayList<PhaseAttribute>(Arrays.asList(
            new PhaseAttribute(PhaseAttributeType.ANGLE) { {
                setPhaseSteps(PhaseStepType.getByAttributeType(PhaseAttributeType.ANGLE));
            } }
        )));
    } };

    public static final PhaseSchema CAR_PHASE = new PhaseSchema(PhaseType.CAR) { {
        setAttributeList(new ArrayList<PhaseAttribute>(Arrays.asList(
            new PhaseAttribute(PhaseAttributeType.ANGLE) { {
                setPhaseSteps(PhaseStepType.getByAttributeType(PhaseAttributeType.ANGLE));
            } }
        )));
    } };

    public static final PhaseSchema EMPTY_PHASE = new PhaseSchema(PhaseType.EMPTY);

    public static final ArrayList<PhaseSchema> PHASES = new ArrayList<PhaseSchema>(Arrays.asList(EMPTY_PHASE, MOUTH_PHASE, GENERIC_PHASE, CAR_PHASE));

    private PhaseConstants() {

    }

    /**
     * Just output our own PhaseSchema JSON for now.
     */
    public static String exportJson(PhaseSchema p) {
        StringWriter writer = new StringWriter();
        Json json = new Json(JsonWriter.OutputType.json);
        json.setWriter(writer);
        json.setQuoteLongValues(true);

        json.writeObjectStart();
        p.writeConfig(json);
        json.writeObjectEnd();

        // because we supplied out own JsonWriter / StringWriter, we need to reach deep into the
        // object model of libgdx.Json
        String retVal = json.getWriter().getWriter().toString();

        //have to close the Writer ourselves;
        try {
            json.getWriter().close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return retVal;
    }

    /**
     * Build the hierarchy.
     */
    public static void buildHierarchy(PhaseSchema phaseSchema, ArrayList<PHNode> nodes, int depth, PHNode node) {
        if (depth == phaseSchema.getAttributeList().size()) {
            // at the bottom of the hierarchy
            return;
        }

        for (int i = 0; i < phaseSchema.getAttributeList().get(depth).getPhaseSteps().size(); i++) {
            PhaseStep ps = phaseSchema.getAttributeList().get(depth).getPhaseSteps().get(i);

            PHNode n = new PHNode();
            n.setType(phaseSchema.getPhaseType());
            n.setAttributeType(phaseSchema.getAttributeList().get(depth).getPhaseAttributeType());
            n.setStepType(ps.getPhaseStepType());
            nodes.add(n);

            buildHierarchy(phaseSchema, n.getChildren(), depth + 1, n);
        }
    }

    /**
     * Export the hierarchy.
     */
    public static String exportHierarchy(PHNode node) {
        StringWriter writer = new StringWriter();
        Json json = new Json(JsonWriter.OutputType.json);
        json.setWriter(writer);
        json.setQuoteLongValues(true);

        json.writeObjectStart();
        node.writeConfig(json);
        json.writeObjectEnd();

        // because we supplied out own JsonWriter / StringWriter, we need to reach deep into the
        // object model of libgdx.Json
        String retVal = json.getWriter().getWriter().toString();

        //have to close the Writer ourselves;
        try {
            json.getWriter().close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return retVal;
    }
}
