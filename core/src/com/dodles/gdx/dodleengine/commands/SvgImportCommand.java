package com.dodles.gdx.dodleengine.commands;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.geometry.GeometryRegistry;
import com.dodles.gdx.dodleengine.geometry.GeometryRenderState;
import com.dodles.gdx.dodleengine.geometry.HandleHook;
import com.dodles.gdx.dodleengine.geometry.circle.CircleGeometry;
import com.dodles.gdx.dodleengine.geometry.circle.CircleGeometryConfig;
import com.dodles.gdx.dodleengine.geometry.polygon.PolygonGeometry;
import com.dodles.gdx.dodleengine.geometry.polygon.PolygonGeometryConfig;
import com.dodles.gdx.dodleengine.scenegraph.BaseDodlesViewGroup;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActor;
import com.dodles.gdx.dodleengine.scenegraph.DodlesGroup;
import com.dodles.gdx.dodleengine.scenegraph.Shape;
import com.dodles.gdx.dodleengine.scenegraph.StrokeConfig;
import com.dodles.gdx.dodleengine.scenegraph.Transform;
import com.dodles.gdx.dodleengine.scenegraph.graphics.FrameBufferAtlasManager;
import com.dodles.gdx.dodleengine.util.JsonUtility;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.gurella.engine.graphics.vector.AffineTransform;
import com.gurella.engine.graphics.vector.Path;
import com.gurella.engine.graphics.vector.svg.Svg;
import com.gurella.engine.graphics.vector.svg.SvgReader;
import com.gurella.engine.graphics.vector.svg.element.CircleElement;
import com.gurella.engine.graphics.vector.svg.element.Element;
import com.gurella.engine.graphics.vector.svg.element.EllipseElement;
import com.gurella.engine.graphics.vector.svg.element.GroupElement;
import com.gurella.engine.graphics.vector.svg.element.PathElement;
import com.gurella.engine.graphics.vector.svg.element.PolygonElement;
import com.gurella.engine.graphics.vector.svg.element.PolylineElement;
import com.gurella.engine.graphics.vector.svg.element.RectElement;
import com.gurella.engine.graphics.vector.svg.element.ShapeElement;
import com.gurella.engine.graphics.vector.svg.element.TextSequenceElement;
import com.gurella.engine.graphics.vector.svg.property.PropertyType;
import com.gurella.engine.graphics.vector.svg.property.value.Length;
import de.hypergraphs.hyena.core.shared.data.UUID;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

/**
 * A command that flips an object.
 */
public class SvgImportCommand implements Command, Importable {
    public static final String COMMAND_NAME = "svg";

    private final ObjectManager objectManager;
    private final GroupHelper groupHelper;
    private final FrameBufferAtlasManager atlasManager;
    private final GeometryRegistry geometryRegistry;

    private ArrayList<String> ids = new ArrayList<String>();
    private HashMap<String, StrokeConfig> strokeConfigs = new HashMap<String, StrokeConfig>();
    private HashMap<Element, DodlesActor> elements = new HashMap<Element, DodlesActor>();
    private StrokeConfig globalStrokeConfig = new StrokeConfig() { {
        setColor(new Color());
        setSize(-1);
        setFill(new Color());
    } };
    private String file;
    private String groupID;
    private String phaseID;
    private String rootGroupID;
    private int counter;

    private static final HashMap<String, Color> SVG_COLORS = new HashMap<String, Color>() { {
        put("CLEAR", new Color(0, 0, 0, 0));
        put("BLACK", new Color(0, 0, 0, 1));
        put("WHITE", new Color(0xffffffff));
        put("LIGHT_GRAY", new Color(0xbfbfbfff));
        put("GRAY", new Color(0x7f7f7fff));
        put("DARK_GRAY", new Color(0x3f3f3fff));

        put("BLUE", new Color(0, 0, 1, 1));
        put("NAVY", new Color(0, 0, 0.5f, 1));
        put("ROYAL", new Color(0x4169e1ff));
        put("SLATE", new Color(0x708090ff));
        put("SKY", new Color(0x87ceebff));
        put("CYAN", new Color(0, 1, 1, 1));
        put("TEAL", new Color(0, 0.5f, 0.5f, 1));

        put("GREEN", new Color(0x00ff00ff));
        put("CHARTREUSE", new Color(0x7fff00ff));
        put("LIME", new Color(0x32cd32ff));
        put("FOREST", new Color(0x228b22ff));
        put("OLIVE", new Color(0x6b8e23ff));

        put("YELLOW", new Color(0xffff00ff));
        put("GOLD", new Color(0xffd700ff));
        put("GOLDENROD", new Color(0xdaa520ff));
        put("ORANGE", new Color(0xffa500ff));

        put("BROWN", new Color(0x8b4513ff));
        put("TAN", new Color(0xd2b48cff));
        put("FIREBRICK", new Color(0xb22222ff));

        put("RED", new Color(0xff0000ff));
        put("SCARLET", new Color(0xff341cff));
        put("CORAL", new Color(0xff7f50ff));
        put("SALMON", new Color(0xfa8072ff));
        put("PINK", new Color(0xff69b4ff));
        put("MAGENTA", new Color(1, 0, 1, 1));

        put("PURPLE", new Color(0xa020f0ff));
        put("VIOLET", new Color(0xee82eeff));
        put("MAROON", new Color(0xb03060ff));
    } };

    @Inject
    public SvgImportCommand(ObjectManager objectManager, FrameBufferAtlasManager atlasManager, GroupHelper groupHelper, GeometryRegistry geometryRegistry) {
        this.objectManager = objectManager;
        this.atlasManager = atlasManager;
        this.groupHelper = groupHelper;
        this.geometryRegistry = geometryRegistry;
    }

    /**
     * Init the command.
     */
    public final void init(String pGroupID, String pPhaseID, String pFile) {
        this.groupID = pGroupID;
        this.phaseID = pPhaseID;
        this.file = pFile;
    }

    @Override
    public final String getName() {
        return COMMAND_NAME;
    }

    @Override
    public final void execute() {
        counter = 0;
        SvgReader reader = new SvgReader();
        Svg svg = reader.parse(this.file);

        //long startTime = System.nanoTime();
        handleGroupElement(svg.getRoot(), null);
        //com.badlogic.gdx.Gdx.app.log("import", "" + (System.nanoTime() - startTime) / 1000000);
        
        // EXPERIMENTAL: Cache the phase after import is complete
        //((DodlesGroup) objectManager.getActor(rootGroupID)).getVisiblePhase().setDisplayMode(Phase.DisplayMode.CACHED);
    }

    @Override
    public final void undo() {
        groupHelper.removeChildFromGroup(objectManager.getActor(ids.get(0)));
    }

    /**
     * Get the dodle container that the svg is held in.
     */
    private String getId() {
        if (ids.size() <= counter) {
         ids.add(UUID.uuid());
        }

        return ids.get(counter++);
    }

    /**
     * Draw the given element, and then draw it's children.
     */
    public final DodlesActor drawElement(Element element, DodlesGroup newGroup) {

        DodlesActor actor = null;

        if (element instanceof TextSequenceElement) {
            actor = handleTextSequenceElement((TextSequenceElement) element);
        } else if (element instanceof GroupElement) {
            actor = handleGroupElement(element, newGroup);
        } else if (element instanceof EllipseElement) {
            actor = drawEllipse(element);
        } else if (element instanceof CircleElement) {
            actor = drawCircle(element);
        } else if (element instanceof PathElement || element instanceof PolylineElement) {
            actor = drawPath(element);
        } else if (element instanceof RectElement) {
            actor = drawRect(element);
        } else if (element instanceof PolygonElement) {
            actor = drawPolygon(element);
        } else {
            for (Element child : element.getChildren()) {
                DodlesActor newActor = drawElement(child, newGroup);
                if (newActor != null) {
                    groupHelper.addChildToGroup(newGroup.getName(), null, newActor);
                    newActor.updateOrigin();
                }
            }

            newGroup.updateOrigin();
        }

        if (actor != null) {
            actor.updateOrigin();
        }

        return actor;
    }

    private DodlesActor drawPolygon(Element element) {
        PolygonElement polygonElement = (PolygonElement) element;

        FloatArray svgPoints = polygonElement.getPoints();
        Vector2[] points = new Vector2[svgPoints.size / 2];
        Vector2[] pointsOut;
        float totalX = 0;
        float totalY = 0;
        
        int index = 0;
        for (int i = 0; i < svgPoints.size; i++) {
            points[index] = new Vector2(svgPoints.get(i), svgPoints.get(++i));
            totalX += points[index].x;
            totalY += points[index].y;
            index++;
        }
        if (points[0].equals(points[(svgPoints.size / 2) - 1])) {
            pointsOut = new Vector2[(svgPoints.size / 2) - 1];
            System.arraycopy(points, 0, pointsOut, 0, points.length - 1);
            points = pointsOut;
        }
        
        Shape shape = getShape(element);
        PolygonGeometryConfig geometryConfig = new PolygonGeometryConfig();
        shape.setCustomConfig(geometryConfig);        
        geometryConfig.setNumPoints(points.length);
        geometryConfig.setPoint(new Vector2(totalX / index, totalY / index));
        
        PolygonGeometry geometry = (PolygonGeometry) geometryRegistry.getGeometry(PolygonGeometry.GEOMETRY_NAME);
        geometry.init(shape);
        
        List<HandleHook> handleHooks = ((GeometryRenderState) shape.getRenderState()).getHandleHooks();
        geometry.generateHandleHooks(shape, handleHooks);
        
        for (int i = 0; i < handleHooks.size(); i++) {
            handleHooks.get(i).setPosition(points[i]);
        }

        return shape;
    }

    private Shape getShape(Element element) {
        StrokeConfig pathStrokeConfig = globalStrokeConfig.cpy();

        if (element.getProperty(PropertyType.cssClass) != null) {
            pathStrokeConfig = mergeIntoGlobalStrokeConfig(strokeConfigs.get(element.getProperty(PropertyType.cssClass)));
        }

        Shape shape = new Shape(getId(), objectManager.getTrackingID(), atlasManager, setStrokeConfigValues(element, pathStrokeConfig));

        objectManager.addActor(shape);

        if (element.getProperty(PropertyType.transform) != null) {
            applySvgTransform((AffineTransform) element.getProperty(PropertyType.transform), shape);
        }
        return shape;
    }

    private DodlesActor drawCircle(Element element) {
        final float cx = ((Length) element.getProperty(PropertyType.cx)).getPixels();
        final float cy = ((Length) element.getProperty(PropertyType.cy)).getPixels();
        final float r = ((Length) element.getProperty(PropertyType.r)).getPixels();
        
        Shape shape = getShape(element);
        CircleGeometryConfig geometryConfig = new CircleGeometryConfig();
        shape.setCustomConfig(geometryConfig);        
        geometryConfig.setPoint(new Vector2(cx, cy));
        geometryConfig.setSize(r);
        
        CircleGeometry geometry = (CircleGeometry) geometryRegistry.getGeometry(CircleGeometry.GEOMETRY_NAME);
        geometry.init(shape);

        return shape;
    }

    private DodlesActor drawEllipse(Element element) {
        final float cx = ((Length) element.getProperty(PropertyType.cx)).getPixels();
        final float cy = ((Length) element.getProperty(PropertyType.cy)).getPixels();
        final float rx = ((Length) element.getProperty(PropertyType.rx)).getPixels();
        final float ry = ((Length) element.getProperty(PropertyType.ry)).getPixels();

        Shape shape = getShape(element);
        CircleGeometryConfig geometryConfig = new CircleGeometryConfig();
        shape.setCustomConfig(geometryConfig);        
        geometryConfig.setPoint(new Vector2(cx, cy));
        geometryConfig.setSize(rx + ry / 2);
        
        geometryConfig.setAxisRatio(CircleGeometryConfig.Axis.XPOS, rx / geometryConfig.getSize());
        geometryConfig.setAxisRatio(CircleGeometryConfig.Axis.XNEG, rx / geometryConfig.getSize());
        geometryConfig.setAxisRatio(CircleGeometryConfig.Axis.YPOS, ry / geometryConfig.getSize());
        geometryConfig.setAxisRatio(CircleGeometryConfig.Axis.YNEG, ry / geometryConfig.getSize());
        
        CircleGeometry geometry = (CircleGeometry) geometryRegistry.getGeometry(CircleGeometry.GEOMETRY_NAME);
        geometry.init(shape);

        return shape;
    }

    private DodlesActor drawRect(Element element) {
        RectElement rectElement = (RectElement) element;

        final float x = rectElement.getX();
        final float y = rectElement.getY();
        final float width = rectElement.getWidth();
        final float height = rectElement.getHeight();
        
        Shape shape = getShape(element);
        PolygonGeometryConfig geometryConfig = new PolygonGeometryConfig();
        shape.setCustomConfig(geometryConfig);        
        geometryConfig.setNumPoints(4);
        geometryConfig.setPoint(new Vector2(x + width / 2f, y + height / 2f));
        geometryConfig.setCornerRadius(rectElement.getRx()); // TODO: currently only supporting rx...
        
        PolygonGeometry geometry = (PolygonGeometry) geometryRegistry.getGeometry(PolygonGeometry.GEOMETRY_NAME);
        geometry.init(shape);
        
        List<HandleHook> handleHooks = ((GeometryRenderState) shape.getRenderState()).getHandleHooks();
        geometry.generateHandleHooks(shape, handleHooks);
        
        handleHooks.get(0).setPosition(new Vector2(x, y));
        handleHooks.get(1).setPosition(new Vector2(x + width, y));
        handleHooks.get(2).setPosition(new Vector2(x + width, y + height));
        handleHooks.get(3).setPosition(new Vector2(x, y + height));

        return shape;
    }

    /**
     * Draw the path attribute.
     */
    private DodlesActor drawPath(Element element) {
        Path path = ((ShapeElement) element).getPath();
        
        Shape shape = getShape(element);
        shape.setCustomConfig(new PathConfig(path.toPathData()));
        PathConfig.init(shape);

        return shape;
    }

    /**
     * Set up group variables such as stroke config.
     */
    private DodlesActor handleGroupElement(Element element, DodlesGroup group) {
        globalStrokeConfig = new StrokeConfig() { {
            setColor(new Color());
            setSize(-1);
            setFill(new Color());
        } };

        if (element.getProperty(PropertyType.cssClass) != null) {
            globalStrokeConfig = strokeConfigs.get(element.getProperty(PropertyType.cssClass));
        }

        setStrokeConfigValues(element, globalStrokeConfig);

        String newGroupID = null;

        if (group == null) {
            group = new DodlesGroup(getId(), objectManager.getTrackingID());
            
            BaseDodlesViewGroup hostGroup = (BaseDodlesViewGroup) objectManager.getActor(groupID);
            hostGroup.addActor(group, phaseID);
            objectManager.addActor(group);
            newGroupID = group.getName();
            rootGroupID = newGroupID;
        } else {
            DodlesGroup newGroup = new DodlesGroup(getId(), objectManager.getTrackingID());

            groupHelper.addChildToGroup(group.getName(), null, newGroup);
            objectManager.addActor(newGroup);
            group = newGroup;
            newGroupID = group.getName();
        }

        if (element.getProperty(PropertyType.transform) != null) {
            applySvgTransform((AffineTransform) element.getProperty(PropertyType.transform), group);
        }
        
        ArrayList<DodlesActor> newChildren = new ArrayList<DodlesActor>();

        for (Element child : element.getChildren()) {
            DodlesActor actor = drawElement(child, group);
            
            if (actor != null) {
                newChildren.add(actor);
            }
        }
        
        if (!newChildren.isEmpty()) {
            groupHelper.addChildrenToGroup(newGroupID, null, newChildren);
        }

        group.updateOrigin();

        return group;
    }

    /**
     * Apply the svg transform to the given actor.
     */
    private void applySvgTransform(final AffineTransform transform, DodlesActor shape) {
        final Vector2 translation = transform.getTranslation(new Vector2());
        shape.updateBaseTransform(new Transform() {
            {
                setX(translation.x);
                setY(translation.y);
                setScaleX(transform.getScaleX());
                setScaleY(transform.getScaleY());
                setRotation(transform.getRotation());
            }
        });
    }

    /**
     * Possibly create the strokeconfig collection by keys/css class names.
     */
    private DodlesActor handleTextSequenceElement(TextSequenceElement element) {
        final RegExp regExp = RegExp.compile("(?:\\:|\\{|\\}|;|)([a-z\\d#-\\.]+)", "g");
        MatchResult matcher;

        ArrayList<String> styleValues = new ArrayList<String>();
        String text = element.getText();
        for (matcher = regExp.exec(text); matcher != null; matcher = regExp.exec(text)) {
            styleValues.add(matcher.getGroup(1));
        }

        Map.Entry<String, StrokeConfig> entry = null;
        for (int i = 0; i < styleValues.size(); i++) {
            String style = styleValues.get(i);
            if (style.indexOf(".") == 0) {
                entry = new DodleStyleEntry<String, StrokeConfig>(style.substring(1), new StrokeConfig() { {
                    setColor(new Color());
                    setSize(-1);
                    setFill(new Color());
                } });
                continue;
            }

            if (entry != null) {
                StrokeConfig current = entry.getValue();

                if (style.equals("fill") || style.equals("stroke")) {
                    String strColor = styleValues.get(++i);
                    Color color = new Color();
                    if (strColor.equals("none")) {
                        color = Color.valueOf("#000000");
                    }
                    if (strColor.length() < 5 && strColor.charAt(0) == '#') {
                        color = Color.valueOf("#" + strColor.charAt(1) + strColor.charAt(1) + strColor.charAt(2) + strColor.charAt(2) + strColor.charAt(3) + strColor.charAt(3));
                    }
                    if (SVG_COLORS.containsKey(strColor.toUpperCase())) {
                        color = SVG_COLORS.get(strColor.toUpperCase());
                    }
                    if (color.equals(new Color())) {
                        color = Color.valueOf(strColor);
                    }
                    if (style.equals("fill")) {
                        current.setFill(color);
                    } else if (style.equals("stroke")) {
                        current.setColor(color);
                    }
                } else if (style.equals("stroke-width")) {
                    String rawValue = styleValues.get(++i);
                    current.setSize(Float.parseFloat(rawValue.replaceAll("[a-zA-Z]", "")));
                    
                    // This doesn't work in HTML... :(
                    //String pattern = "((?<=[a-zA-Z])(?=[0-9]))|((?<=[0-9])(?=[a-zA-Z]))";
                    //String[] value = rawValue.split(pattern);
                    //current.setSize(Float.parseFloat(value[0]));
                }

                entry.setValue(current.cpy());

                strokeConfigs.put(entry.getKey(), entry.getValue());
            }
        }

        return null;
    }

    /**
     * Merge element overridden strokeconfig values in.
     */
    private StrokeConfig mergeIntoGlobalStrokeConfig(StrokeConfig strokeConfig) {
        StrokeConfig temp = globalStrokeConfig.cpy();

        if (!strokeConfig.getColor().equals(new Color())) {
            temp.setColor(strokeConfig.getColor());
        }

        if (!strokeConfig.getFill().equals(new Color())) {
            temp.setFill(strokeConfig.getFill());
        }

        if (strokeConfig.getSize() != -1) {
            temp.setSize(strokeConfig.getSize());
        }

        return temp;
    }

    /**
     * Set the given strokeConfig values.
     */
    private StrokeConfig setStrokeConfigValues(Element element, StrokeConfig strokeConfigOverride) {
        if (element.getProperty(PropertyType.strokeWidth) != null) {
            strokeConfigOverride.setSize(Math.round(((Length) element.getProperty(PropertyType.strokeWidth)).getPixels()));
        }
        if (element.getProperty(PropertyType.fill) != null) {
            Color tempColor = new Color();
            if (element.getProperty(PropertyType.fill) instanceof com.gurella.engine.graphics.vector.svg.property.value.Color) {
                Color.argb8888ToColor(tempColor, ((com.gurella.engine.graphics.vector.svg.property.value.Color) element.getProperty(PropertyType.fill)).getColor());
                tempColor.a = 1;
            }
            strokeConfigOverride.setFill(new Color(tempColor));
        }
        if (element.getProperty(PropertyType.stroke) != null) {
            Color tempColor = new Color();
            if (element.getProperty(PropertyType.stroke) instanceof com.gurella.engine.graphics.vector.svg.property.value.Color) {
                Color.argb8888ToColor(tempColor, ((com.gurella.engine.graphics.vector.svg.property.value.Color) element.getProperty(PropertyType.stroke)).getColor());
                tempColor.a = 1;
            }
            strokeConfigOverride.setColor(new Color(tempColor));
        }

        return strokeConfigOverride;
    }

    @Override
    public final void writeConfig(Json json) {
        json.writeValue("ids", ids);
        json.writeValue("groupID", groupID);
        json.writeValue("phaseID", phaseID);
        json.writeValue("file", file);
    }

    @Override
    public final void loadConfig(JsonValue json, CommandFactory factory) {
        ids = JsonUtility.readStringArray(json.get("ids"));
        groupID = json.getString("groupID");
        phaseID = json.getString("phaseID");
        file = json.getString("file");
    }
    
    /**
     * Return the object id that should be selected upon import.
     */
    @Override
    public final String getObjectID() {
        return rootGroupID;
    }

    /**
     * Helper class to add entrys to the strokeconfig hashmap.
     */
    final class DodleStyleEntry<String, StrokeConfig> implements Map.Entry<String, StrokeConfig> {
        private final String key;
        private StrokeConfig value;

        /**
         * Create a new entry.
         */
        public DodleStyleEntry(String style, StrokeConfig strokeConfig) {
            this.key = style;
            this.value = strokeConfig;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public StrokeConfig getValue() {
            return value;
        }

        @Override
        public StrokeConfig setValue(StrokeConfig strokeConfig) {
            StrokeConfig old = value;
            value = strokeConfig;
            return old;
        }
    }
}
