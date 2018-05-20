package com.gurella.engine.graphics.vector;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.gurella.engine.graphics.vector.FastPool.ObjectFactory;
import com.gurella.engine.graphics.vector.svg.SvgReader;
import java.util.HashMap;

public final class FastPools {
    private static final HashMap<String, FastPool> pools = new HashMap<String, FastPool>();
    
    private static final String AFFINE_XFORM_POOL = "affinexform";
    private static final String ARC_TO_COMMAND_POOL = "arctocommand";
    private static final String CANVAS_POOL = "canvas";
    private static final String CANVAS_LAYER_POOL = "canvaslayer";
    private static final String CANVAS_STATE_POOL = "canvasstate";
    private static final String CLIP_POOL = "clip";
    private static final String CLOSE_COMMAND_POOL = "closecommand";
    private static final String CUBIC_TO_COMMAND_POOL = "cubictocommand";
    private static final String CUBIC_SMOOTH_TO_COMMAND_POOL = "cubicsmoothtocommand";
    private static final String GLCALL_POOL = "glcall";
    private static final String GLPATHCOMPONENT_POOL = "glpathcomponent";
    private static final String GLUNIFORMS_POOL = "gluniforms";
    private static final String GRADIENT_POOL = "gradient";
    private static final String HORIZONTAL_LINE_TO_COMMAND_POOL = "horizontallinetocommand";
    private static final String HORIZONTAL_MOVE_TO_COMMAND_POOL = "horizontalmovetocommand";
    private static final String IMAGE_POOL = "image";
    private static final String LAYER_RENDER_NODE_POOL = "layerrendernode";
    private static final String LINE_TO_COMMAND_POOL = "linetocommand";
    private static final String MOVE_TO_COMMAND_POOL = "movetocommand";
    private static final String PAINT_POOL = "paint";
    private static final String PATH_POOL = "path";
    private static final String PATH_COMPONENT_POOL = "pathcomponent";
    private static final String PATH_MESH_POOL = "pathmesh";
    private static final String POINT_POOL = "point";
    private static final String QUAD_TO_COMMAND_POOL = "quadtocommand";
    private static final String QUAD_SMOOTH_TO_COMMAND_POOL = "quadsmoothtocommand";
    private static final String RECTANGLE_POOL = "rectangle";
    private static final String SIMPLE_RENDER_NODE_POOL = "simplerendernode";
    private static final String SVG_ARC_TO_COMMAND_POOL = "svgarctocommand";
    private static final String SVG_READER_POOL = "svgreader";
    private static final String TRIANGLES_MESH_POOL = "trianglesmesh";
    private static final String VECTOR2_POOL = "vector2";
    private static final String VERTEX_POOL = "vertex";
    private static final String VERTICAL_LINE_TO_COMMAND_POOL = "verticallinetocommand";
    private static final String VERTICAL_MOVE_TO_COMMAND_POOL = "verticalmovetocommand";
    private static final String WINDING_COMMAND_POOL = "windingcommand";
    
    
    static {
        pools.put(AFFINE_XFORM_POOL, new FastPool(new ObjectFactory<AffineTransform>() {
            @Override
            public AffineTransform create() {
                return new AffineTransform();
            }
        }));
        
        pools.put(ARC_TO_COMMAND_POOL, new FastPool(new ObjectFactory<PathShape.ArcToCommand>() {
            @Override
            public PathShape.ArcToCommand create() {
                return new PathShape.ArcToCommand();
            }
        }));
        
        pools.put(CANVAS_POOL, new FastPool(new ObjectFactory<Canvas>() {
            @Override
            public Canvas create() {
                return new Canvas();
            }
        }));
        
        pools.put(CANVAS_LAYER_POOL, new FastPool(new ObjectFactory<CanvasLayer>() {
            @Override
            public CanvasLayer create() {
                return new CanvasLayer();
            }
        }));
        
        pools.put(CANVAS_STATE_POOL, new FastPool(new ObjectFactory<CanvasState>() {
            @Override
            public CanvasState create() {
                return new CanvasState();
            }
        }));
        
        pools.put(CLIP_POOL, new FastPool(new ObjectFactory<Clip>() {
            @Override
            public Clip create() {
                return new Clip();
            }
        }));
        
        pools.put(CLOSE_COMMAND_POOL, new FastPool(new ObjectFactory<PathShape.CloseCommand>() {
            @Override
            public PathShape.CloseCommand create() {
                return new PathShape.CloseCommand();
            }
        }));
        
        pools.put(CUBIC_TO_COMMAND_POOL, new FastPool(new ObjectFactory<PathShape.CubicToCommand>() {
            @Override
            public PathShape.CubicToCommand create() {
                return new PathShape.CubicToCommand();
            }
        }));
        
        pools.put(CUBIC_SMOOTH_TO_COMMAND_POOL, new FastPool(new ObjectFactory<PathShape.CubicSmoothToCommand>() {
            @Override
            public PathShape.CubicSmoothToCommand create() {
                return new PathShape.CubicSmoothToCommand();
            }
        }));
        
        pools.put(GLCALL_POOL, new FastPool(new ObjectFactory<GlCall>() {
            @Override
            public GlCall create() {
                return new GlCall();
            }
        }));
        
        pools.put(GLPATHCOMPONENT_POOL, new FastPool(new ObjectFactory<GlPathComponent>() {
            @Override
            public GlPathComponent create() {
                return new GlPathComponent();
            }
        }));
        
        pools.put(GLUNIFORMS_POOL, new FastPool(new ObjectFactory<GlUniforms>() {
            @Override
            public GlUniforms create() {
                return new GlUniforms();
            }
        }));
        
        pools.put(GRADIENT_POOL, new FastPool(new ObjectFactory<Gradient>() {
            @Override
            public Gradient create() {
                return new Gradient();
            }
        }));
        
        pools.put(HORIZONTAL_LINE_TO_COMMAND_POOL, new FastPool(new ObjectFactory<PathShape.HorizontalLineToCommand>() {
            @Override
            public PathShape.HorizontalLineToCommand create() {
                return new PathShape.HorizontalLineToCommand();
            }
        }));
        
        pools.put(HORIZONTAL_MOVE_TO_COMMAND_POOL, new FastPool(new ObjectFactory<PathShape.HorizontalMoveToCommand>() {
            @Override
            public PathShape.HorizontalMoveToCommand create() {
                return new PathShape.HorizontalMoveToCommand();
            }
        }));
        
        pools.put(IMAGE_POOL, new FastPool(new ObjectFactory<Image>() {
            @Override
            public Image create() {
                return new Image();
            }
        }));
        
        pools.put(LAYER_RENDER_NODE_POOL, new FastPool(new ObjectFactory<RenderGraph.LayerRenderNode>() {
            @Override
            public RenderGraph.LayerRenderNode create() {
                return new RenderGraph.LayerRenderNode();
            }
        }));
        
        pools.put(LINE_TO_COMMAND_POOL, new FastPool(new ObjectFactory<PathShape.LineToCommand>() {
            @Override
            public PathShape.LineToCommand create() {
                return new PathShape.LineToCommand();
            }
        }));
        
        pools.put(MOVE_TO_COMMAND_POOL, new FastPool(new ObjectFactory<PathShape.MoveToCommand>() {
            @Override
            public PathShape.MoveToCommand create() {
                return new PathShape.MoveToCommand();
            }
        }));
        
        pools.put(PAINT_POOL, new FastPool(new ObjectFactory<Paint>() {
            @Override
            public Paint create() {
                return new Paint();
            }
        }));
        
        pools.put(PATH_POOL, new FastPool(new ObjectFactory<Path>() {
            @Override
            public Path create() {
                return new Path();
            }
        }));
        
        pools.put(PATH_COMPONENT_POOL, new FastPool(new ObjectFactory<PathComponent>() {
            @Override
            public PathComponent create() {
                return new PathComponent();
            }
        }));
        
        pools.put(PATH_MESH_POOL, new FastPool(new ObjectFactory<PathMesh>() {
            @Override
            public PathMesh create() {
                return new PathMesh();
            }
        }));
        
        pools.put(POINT_POOL, new FastPool(new ObjectFactory<Point>() {
            @Override
            public Point create() {
                return new Point();
            }
        }));
        
        pools.put(QUAD_TO_COMMAND_POOL, new FastPool(new ObjectFactory<PathShape.QuadToCommand>() {
            @Override
            public PathShape.QuadToCommand create() {
                return new PathShape.QuadToCommand();
            }
        }));
        
        pools.put(QUAD_SMOOTH_TO_COMMAND_POOL, new FastPool(new ObjectFactory<PathShape.QuadSmoothToCommand>() {
            @Override
            public PathShape.QuadSmoothToCommand create() {
                return new PathShape.QuadSmoothToCommand();
            }
        }));
        
        pools.put(RECTANGLE_POOL, new FastPool(new ObjectFactory<Rectangle>() {
            @Override
            public Rectangle create() {
                return new Rectangle();
            }
        }));
        
        pools.put(SIMPLE_RENDER_NODE_POOL, new FastPool(new ObjectFactory<RenderGraph.SimpleRenderNode>() {
            @Override
            public RenderGraph.SimpleRenderNode create() {
                return new RenderGraph.SimpleRenderNode();
            }
        }));
        
        pools.put(SVG_ARC_TO_COMMAND_POOL, new FastPool(new ObjectFactory<PathShape.SvgArcToCommand>() {
            @Override
            public PathShape.SvgArcToCommand create() {
                return new PathShape.SvgArcToCommand();
            }
        }));
        
        pools.put(SVG_READER_POOL, new FastPool(new ObjectFactory<SvgReader>() {
            @Override
            public SvgReader create() {
                return new SvgReader();
            }
        }));
        
        pools.put(TRIANGLES_MESH_POOL, new FastPool(new ObjectFactory<TrianglesMesh>() {
            @Override
            public TrianglesMesh create() {
                return new TrianglesMesh();
            }
        }));
        
        pools.put(VECTOR2_POOL, new FastPool(new ObjectFactory<Vector2>() {
            @Override
            public Vector2 create() {
                return new Vector2();
            }
        }));
        
        pools.put(VERTEX_POOL, new FastPool(new ObjectFactory<Vertex>() {
            @Override
            public Vertex create() {
                return new Vertex();
            }
        }));
        
        pools.put(VERTICAL_LINE_TO_COMMAND_POOL, new FastPool(new ObjectFactory<PathShape.VerticalLineToCommand>() {
            @Override
            public PathShape.VerticalLineToCommand create() {
                return new PathShape.VerticalLineToCommand();
            }
        }));
        
        pools.put(VERTICAL_MOVE_TO_COMMAND_POOL, new FastPool(new ObjectFactory<PathShape.VerticalMoveToCommand>() {
            @Override
            public PathShape.VerticalMoveToCommand create() {
                return new PathShape.VerticalMoveToCommand();
            }
        }));
        
        pools.put(WINDING_COMMAND_POOL, new FastPool(new ObjectFactory<PathShape.WindingCommand>() {
            @Override
            public PathShape.WindingCommand create() {
                return new PathShape.WindingCommand();
            }
        }));
    }
    
    /*-------*/
    
    public static AffineTransform obtainAffineTransform() {
        return (AffineTransform) pools.get(AFFINE_XFORM_POOL).obtain();
    }
    
    public static void free(AffineTransform affineTransform) {
        pools.get(AFFINE_XFORM_POOL).free(affineTransform);
    }
    
    /*-------*/
    
    public static PathShape.ArcToCommand obtainArcToCommand() {
        return (PathShape.ArcToCommand) pools.get(ARC_TO_COMMAND_POOL).obtain();
    }
    
    public static void free(PathShape.ArcToCommand arcToCommand) {
        pools.get(ARC_TO_COMMAND_POOL).free(arcToCommand);
    }
    
    /*-------*/
    
    public static Canvas obtainCanvas() {
        return (Canvas) pools.get(CANVAS_POOL).obtain();
    }
    
    public static void free(Canvas canvas) {
        pools.get(CANVAS_POOL).free(canvas);
    }
    
    /*-------*/
    
    public static CanvasLayer obtainCanvasLayer() {
        return (CanvasLayer) pools.get(CANVAS_LAYER_POOL).obtain();
    }
    
    public static void free(CanvasLayer canvasLayer) {
        pools.get(CANVAS_LAYER_POOL).free(canvasLayer);
    }
    
    public static void resetCanvasLayers(Array<CanvasLayer> canvasLayers) {
        resetArray(CANVAS_LAYER_POOL, canvasLayers);
    }
    
    /*-------*/
    
    public static CanvasState obtainCanvasState() {
        return (CanvasState) pools.get(CANVAS_STATE_POOL).obtain();
    }
    
    public static void free(CanvasState canvasState) {
        pools.get(CANVAS_STATE_POOL).free(canvasState);
    }
    
    public static void resetCanvasStates(Array<CanvasState> canvasStates) {
        resetArray(CANVAS_STATE_POOL, canvasStates);
    }
    
    /*-------*/
    
    public static Clip obtainClip() {
        return (Clip) pools.get(CLIP_POOL).obtain();
    }
    
    public static void free(Clip clip) {
        pools.get(CLIP_POOL).free(clip);
    }
    
    public static void resetClips(Array<Clip> clips) {
        resetArray(CLIP_POOL, clips);
    }
    
    /*-------*/
    
    public static PathShape.CloseCommand obtainCloseCommand() {
        return (PathShape.CloseCommand) pools.get(CLOSE_COMMAND_POOL).obtain();
    }
    
    public static void free(PathShape.CloseCommand closeCommand) {
        pools.get(CLOSE_COMMAND_POOL).free(closeCommand);
    }
    
    /*-------*/
    
    public static PathShape.CubicToCommand obtainCubicToCommand() {
        return (PathShape.CubicToCommand) pools.get(CUBIC_TO_COMMAND_POOL).obtain();
    }
    
    public static void free(PathShape.CubicToCommand cubicToCommand) {
        pools.get(CUBIC_TO_COMMAND_POOL).free(cubicToCommand);
    }
    
    /*-------*/
    
    public static PathShape.CubicSmoothToCommand obtainCubicSmoothToCommand() {
        return (PathShape.CubicSmoothToCommand) pools.get(CUBIC_SMOOTH_TO_COMMAND_POOL).obtain();
    }
    
    public static void free(PathShape.CubicSmoothToCommand cubicSmoothToCommand) {
        pools.get(CUBIC_SMOOTH_TO_COMMAND_POOL).free(cubicSmoothToCommand);
    }
    
    /*-------*/
    
    public static GlCall obtainGlCall() {
        return (GlCall) pools.get(GLCALL_POOL).obtain();
    }
    
    public static void free(GlCall glCall) {
        pools.get(GLCALL_POOL).free(glCall);
    }
    
    public static void resetGlCalls(Array<GlCall> glCalls) {
        resetArray(GLCALL_POOL, glCalls);
    }
    
    /*-------*/
    
    public static GlPathComponent obtainGlPathComponent() {
        return (GlPathComponent) pools.get(GLPATHCOMPONENT_POOL).obtain();
    }
    
    public static void free(GlPathComponent glPathComponent) {
        pools.get(GLPATHCOMPONENT_POOL).free(glPathComponent);
    }
    
    public static void resetGlPathComponents(Array<GlPathComponent> glPathComponents) {
        resetArray(GLPATHCOMPONENT_POOL, glPathComponents);
    }
    
    /*-------*/
    
    public static GlUniforms obtainGlUniforms() {
        return (GlUniforms) pools.get(GLUNIFORMS_POOL).obtain();
    }
    
    public static void free(GlUniforms glUniforms) {
        pools.get(GLUNIFORMS_POOL).free(glUniforms);
    }
    
    public static void resetGlUniforms(Array<GlUniforms> glUniforms) {
        resetArray(GLUNIFORMS_POOL, glUniforms);
    }
    
    /*-------*/
    
    public static Gradient obtainGradient() {
        return (Gradient) pools.get(GRADIENT_POOL).obtain();
    }
    
    public static void free(Gradient gradient) {
        pools.get(GRADIENT_POOL).free(gradient);
    }
    
    /*-------*/
    
    public static PathShape.HorizontalLineToCommand obtainHorizontalLineToCommand() {
        return (PathShape.HorizontalLineToCommand) pools.get(HORIZONTAL_LINE_TO_COMMAND_POOL).obtain();
    }
    
    public static void free(PathShape.HorizontalLineToCommand horizontalLineToCommand) {
        pools.get(HORIZONTAL_LINE_TO_COMMAND_POOL).free(horizontalLineToCommand);
    }
    
    /*-------*/
    
    public static PathShape.HorizontalMoveToCommand obtainHorizontalMoveToCommand() {
        return (PathShape.HorizontalMoveToCommand) pools.get(HORIZONTAL_MOVE_TO_COMMAND_POOL).obtain();
    }
    
    public static void free(PathShape.HorizontalMoveToCommand horizontalMoveToCommand) {
        pools.get(HORIZONTAL_MOVE_TO_COMMAND_POOL).free(horizontalMoveToCommand);
    }
    
    /*-------*/
    
    public static Image obtainImage() {
        return (Image) pools.get(IMAGE_POOL).obtain();
    }
    
    public static void free(Image image) {
        pools.get(IMAGE_POOL).free(image);
    }
    
    /*-------*/
    
    public static RenderGraph.LayerRenderNode obtainLayerRenderNode() {
        return (RenderGraph.LayerRenderNode) pools.get(LAYER_RENDER_NODE_POOL).obtain();
    }
    
    public static void free(RenderGraph.LayerRenderNode layerRenderNode) {
        pools.get(LAYER_RENDER_NODE_POOL).free(layerRenderNode);
    }
    
    /*-------*/
    
    public static PathShape.LineToCommand obtainLineToCommand() {
        return (PathShape.LineToCommand) pools.get(LINE_TO_COMMAND_POOL).obtain();
    }
    
    public static void free(PathShape.LineToCommand lineToCommand) {
        pools.get(LINE_TO_COMMAND_POOL).free(lineToCommand);
    }
    
    /*-------*/
    
    public static PathShape.MoveToCommand obtainMoveToCommand() {
        return (PathShape.MoveToCommand) pools.get(MOVE_TO_COMMAND_POOL).obtain();
    }
    
    public static void free(PathShape.MoveToCommand moveToCommand) {
        pools.get(MOVE_TO_COMMAND_POOL).free(moveToCommand);
    }
    
    /*-------*/
    
    public static Paint obtainPaint() {
        return (Paint) pools.get(PAINT_POOL).obtain();
    }
    
    public static void free(Paint paint) {
        pools.get(PAINT_POOL).free(paint);
    }
    
    /*-------*/
    
    public static Path obtainPath() {
        return (Path) pools.get(PATH_POOL).obtain();
    }
    
    public static void free(Path path) {
        pools.get(PATH_POOL).free(path);
    }
    
    /*-------*/
    
    public static PathComponent obtainPathComponent() {
        return (PathComponent) pools.get(PATH_COMPONENT_POOL).obtain();
    }
    
    public static void free(PathComponent pathComponent) {
        pools.get(PATH_COMPONENT_POOL).free(pathComponent);
    }
    
    public static void resetPathComponents(Array<PathComponent> pathComponents) {
        resetArray(PATH_COMPONENT_POOL, pathComponents);
    }
    
    /*-------*/
    
    public static PathMesh obtainPathMesh() {
        return (PathMesh) pools.get(PATH_MESH_POOL).obtain();
    }
    
    public static void free(PathMesh pathMesh) {
        pools.get(PATH_MESH_POOL).free(pathMesh);
    }
    
    /*-------*/
    
    public static Point obtainPoint() {
        return (Point) pools.get(POINT_POOL).obtain();
    }
    
    public static void free(Point point) {
        pools.get(POINT_POOL).free(point);
    }
    
    public static void resetPoints(Array<Point> points) {
        resetArray(POINT_POOL, points);
    }
    
    /*-------*/
    
    public static PathShape.QuadToCommand obtainQuadToCommand() {
        return (PathShape.QuadToCommand) pools.get(QUAD_TO_COMMAND_POOL).obtain();
    }
    
    public static void free(PathShape.QuadToCommand quadToCommand) {
        pools.get(QUAD_TO_COMMAND_POOL).free(quadToCommand);
    }
    
    /*-------*/
    
    public static PathShape.QuadSmoothToCommand obtainQuadSmoothToCommand() {
        return (PathShape.QuadSmoothToCommand) pools.get(QUAD_SMOOTH_TO_COMMAND_POOL).obtain();
    }
    
    public static void free(PathShape.QuadSmoothToCommand quadSmoothToCommand) {
        pools.get(QUAD_SMOOTH_TO_COMMAND_POOL).free(quadSmoothToCommand);
    }
    
    /*-------*/
    
    public static Rectangle obtainRectangle() {
        return (Rectangle) pools.get(RECTANGLE_POOL).obtain();
    }
    
    public static void free(Rectangle rectangle) {
        pools.get(RECTANGLE_POOL).free(rectangle);
    }
    
    /*-------*/
    
    public static RenderGraph.SimpleRenderNode obtainSimpleRenderNode() {
        return (RenderGraph.SimpleRenderNode) pools.get(SIMPLE_RENDER_NODE_POOL).obtain();
    }
    
    public static void free(RenderGraph.SimpleRenderNode simpleRenderNode) {
        pools.get(SIMPLE_RENDER_NODE_POOL).free(simpleRenderNode);
    }
    
    /*-------*/
    
    public static PathShape.SvgArcToCommand obtainSvgArcToCommand() {
        return (PathShape.SvgArcToCommand) pools.get(SVG_ARC_TO_COMMAND_POOL).obtain();
    }
    
    public static void free(PathShape.SvgArcToCommand svgArcToCommand) {
        pools.get(SVG_ARC_TO_COMMAND_POOL).free(svgArcToCommand);
    }
    
    /*-------*/
    
    public static SvgReader obtainSvgReader() {
        return (SvgReader) pools.get(SVG_READER_POOL).obtain();
    }
    
    public static void free(SvgReader svgReader) {
        pools.get(SVG_READER_POOL).free(svgReader);
    }
    
    /*-------*/
    
    public static TrianglesMesh obtainTrianglesMesh() {
        return (TrianglesMesh) pools.get(TRIANGLES_MESH_POOL).obtain();
    }
    
    public static void free(TrianglesMesh trianglesMesh) {
        pools.get(TRIANGLES_MESH_POOL).free(trianglesMesh);
    }
    
    /*-------*/
    
    public static Vector2 obtainVector2() {
        return (Vector2) pools.get(VECTOR2_POOL).obtain();
    }
    
    public static void free(Vector2 vector2) {
        pools.get(VECTOR2_POOL).free(vector2);
    }
    
    /*-------*/
    
    public static Vertex obtainVertex() {
        return (Vertex) pools.get(VERTEX_POOL).obtain();
    }
    
    public static void free(Vertex vertex) {
        pools.get(VERTEX_POOL).free(vertex);
    }
    
    public static void resetVertices(Array<Vertex> vertices) {
        resetArray(VERTEX_POOL, vertices);
    }
    
    /*-------*/
    
    public static PathShape.VerticalLineToCommand obtainVerticalLineToCommand() {
        return (PathShape.VerticalLineToCommand) pools.get(VERTICAL_LINE_TO_COMMAND_POOL).obtain();
    }
    
    public static void free(PathShape.VerticalLineToCommand verticalLineToCommand) {
        pools.get(VERTICAL_LINE_TO_COMMAND_POOL).free(verticalLineToCommand);
    }
    
    /*-------*/
    
    public static PathShape.VerticalMoveToCommand obtainVerticalMoveToCommand() {
        return (PathShape.VerticalMoveToCommand) pools.get(VERTICAL_MOVE_TO_COMMAND_POOL).obtain();
    }
    
    public static void free(PathShape.VerticalMoveToCommand verticalMoveToCommand) {
        pools.get(VERTICAL_MOVE_TO_COMMAND_POOL).free(verticalMoveToCommand);
    }
    
    /*-------*/
    
    public static PathShape.WindingCommand obtainWindingCommand() {
        return (PathShape.WindingCommand) pools.get(WINDING_COMMAND_POOL).obtain();
    }
    
    public static void free(PathShape.WindingCommand windingCommand) {
        pools.get(WINDING_COMMAND_POOL).free(windingCommand);
    }
    
    /*-------*/
    
    public static void resetPathCommands(Array<PathShape.PathCommand> poolables) {
        for (int i = 0; i < poolables.size; i++) {
            Object poolable = poolables.get(i);
            
            if (poolable instanceof PathShape.ArcToCommand) {
                pools.get(ARC_TO_COMMAND_POOL).free(poolable);
            } else if (poolable instanceof PathShape.CloseCommand) {
                pools.get(CLOSE_COMMAND_POOL).free(poolable);
            } else if (poolable instanceof PathShape.CubicSmoothToCommand) {
                pools.get(CUBIC_SMOOTH_TO_COMMAND_POOL).free(poolable);
            } else if (poolable instanceof PathShape.CubicToCommand) {
                pools.get(CUBIC_TO_COMMAND_POOL).free(poolable);
            } else if (poolable instanceof PathShape.HorizontalLineToCommand) {
                pools.get(HORIZONTAL_LINE_TO_COMMAND_POOL).free(poolable);
            } else if (poolable instanceof PathShape.HorizontalMoveToCommand) {
                pools.get(HORIZONTAL_MOVE_TO_COMMAND_POOL).free(poolable);
            } else if (poolable instanceof PathShape.LineToCommand) {
                pools.get(LINE_TO_COMMAND_POOL).free(poolable);
            } else if (poolable instanceof PathShape.MoveToCommand) {
                pools.get(MOVE_TO_COMMAND_POOL).free(poolable);
            } else if (poolable instanceof PathShape.QuadSmoothToCommand) {
                pools.get(QUAD_SMOOTH_TO_COMMAND_POOL).free(poolable);
            } else if (poolable instanceof PathShape.QuadToCommand) {
                pools.get(QUAD_TO_COMMAND_POOL).free(poolable);
            } else if (poolable instanceof PathShape.SvgArcToCommand) {
                pools.get(SVG_ARC_TO_COMMAND_POOL).free(poolable);
            } else if (poolable instanceof PathShape.VerticalLineToCommand) {
                pools.get(VERTICAL_LINE_TO_COMMAND_POOL).free(poolable);
            } else if (poolable instanceof PathShape.VerticalMoveToCommand) {
                pools.get(VERTICAL_MOVE_TO_COMMAND_POOL).free(poolable);
            } else if (poolable instanceof PathShape.WindingCommand) {
                pools.get(WINDING_COMMAND_POOL).free(poolable);
            } else {
                throw new GdxRuntimeException("Unrecognized command type: " + poolable.toString());
            }
        }
        
        poolables.clear();
    }
    
    public static void resetRenderNodes(Array<RenderGraph.RenderNode> poolables) {
        for (int i = 0; i < poolables.size; i++) {
            Object poolable = poolables.get(i);
            
            if (poolable instanceof RenderGraph.LayerRenderNode) {
                pools.get(LAYER_RENDER_NODE_POOL).free(poolable);
            } else if (poolable instanceof RenderGraph.SimpleRenderNode) {
                pools.get(SIMPLE_RENDER_NODE_POOL).free(poolable);
            } else {
                throw new GdxRuntimeException("Unrecognized render node type: " + poolable.toString());
            }
        }
        
        poolables.clear();
    }
    
    private static void resetArray(String key, Array<?> poolables) {
        for (int i = 0; i < poolables.size; i++) {
            Object poolable = poolables.get(i);
            pools.get(key).free(poolable);
        }
        poolables.clear();
    }
}
