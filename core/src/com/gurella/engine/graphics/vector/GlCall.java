package com.gurella.engine.graphics.vector;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.Pool.Poolable;

public class GlCall implements Poolable {
	CallType callType = CallType.none;
	BlendMode blendMode = BlendMode.over;
	
	final Array<GlUniforms> uniforms = new Array<GlUniforms>();
	final Array<GlPathComponent> components = new Array<GlPathComponent>();

	int triangleVerticesOffset = -1;
	final Array<Vertex> triangleVertices = new Array<Vertex>();
	VertexMode vertexMode = VertexMode.triangles;
	
	final Array<Clip> clips = new Array<Clip>();
	
	static GlCall obtain() {
		return FastPools.obtainGlCall();
	}
	
	static GlCall obtainFrameBufferRenderCall() {
		GlCall call = FastPools.obtainGlCall();
		call.callType = CallType.triangles;
		
		GlUniforms uniform = GlUniforms.newInstance();
		uniform.initForFrameBuffer();
		call.uniforms.add(uniform);
		return call;
	}

	void newTriangleVertex(float x, float y, float u, float v) {
		triangleVertices.add(Vertex.obtain(x, y, u, v));
	}
	
	GlUniforms newUniform(AffineTransform globalXform, float globalAlpha, Scissor scissor, Paint paint, float width, float fringe, float strokeThr){
		return newUniform(globalXform, globalAlpha, scissor, paint, width, fringe, strokeThr, false);
	}
	
	GlUniforms newUniform(AffineTransform globalXform, float globalAlpha, Scissor scissor, Paint paint, float width, float fringe, float strokeThr, boolean texturedVertices){
		GlUniforms uniform = GlUniforms.newInstance();
		uniform.init(globalXform, globalAlpha, paint, scissor, width, fringe, strokeThr, texturedVertices);
		uniforms.add(uniform);
		return uniform;
	}

	GlUniforms getUniforms(int i) {
		return uniforms.get(i);
	}

	@Override
	public void reset() {
		callType = CallType.none;
		blendMode = BlendMode.over;
                FastPools.resetGlUniforms(uniforms);
		triangleVerticesOffset = -1;
                FastPools.resetVertices(triangleVertices);
		vertexMode = VertexMode.triangles;
                FastPools.resetGlPathComponents(components);
		clips.clear();
	}
	
	void free() {
		FastPools.free(this);
	}
	
	enum CallType {
		none, fill, convexFill, stroke, triangles;
	}
}
