package com.gurella.engine.graphics.vector;

import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.Pool.Poolable;

public abstract class Shape implements Poolable {
	private final Path path = Path.obtain();
	private PathMesh cachedPathMesh;

	private final FillState fillState = new FillState();
	private GlCall fillCall;

	private final StrokeState strokeState = new StrokeState();
	private GlCall strokeCall;

	GlCall getFillCall(Canvas canvas) {
		updateFillState(canvas);
		if (fillCall == null) {
			PathMesh pathMesh = getPath(canvas);
			fillCall = pathMesh.createFillCall();
		}
		return fillCall;
	}

	private void updateFillState(Canvas canvas) {
		if (fillState.isChanged(canvas)) {
			freeFillCall();
			fillState.update(canvas);
		}
	}

	GlCall getStrokeCall(Canvas canvas) {
		updateStrokeState(canvas);
		if (strokeCall == null) {
			PathMesh pathMesh = getPath(canvas);
			strokeCall = pathMesh.createStrokeCall();
		}
		return strokeCall;
	}

	private void updateStrokeState(Canvas canvas) {
		if (strokeState.isChanged(canvas)) {
			freeStrokeCall();
			strokeState.update(canvas);
		}
	}

	protected void markDataChanged() {
		freePath();
		freeFillCall();
		freeStrokeCall();
	}

	private void freePath() {
		if (cachedPathMesh != null) {
			cachedPathMesh.free();
			cachedPathMesh = null;
		}
	}

	private void freeFillCall() {
		if (fillCall != null) {
			fillCall.free();
			fillCall = null;
		}
	}

	private void freeStrokeCall() {
		if (strokeCall != null) {
			strokeCall.free();
			strokeCall = null;
		}
	}

	private PathMesh getPath(Canvas canvas) {
		if (cachedPathMesh == null) {
			path.reset();
			initPath(path);
			cachedPathMesh = PathMesh.obtain(canvas, path);
		}

		return cachedPathMesh;
	}

	protected abstract void initPath(Path path);

	@Override
	public void reset() {
		path.reset();
		freePath();
		freeFillCall();
		freeStrokeCall();
	}

	private static class FillState {
		private float devicePixelRatio;
		private AffineTransform xform = AffineTransform.obtain();

		boolean isChanged(Canvas canvas) {
			//TODO clips and uniforms are not checked
			CanvasState canvasState = canvas.currentState;
			return devicePixelRatio != canvas.devicePixelRatio || !xform.equals(canvasState.xform);
		}

		void update(Canvas canvas) {
			CanvasState canvasState = canvas.currentState;
			devicePixelRatio = canvas.devicePixelRatio;
			xform.set(canvasState.xform);
		}
	}
	
	private static class StrokeState {
		private float devicePixelRatio;
		private float strokeWidth = 1.0f;
		private float miterLimit = 10.0f;
		private LineJoin lineJoin = LineJoin.miter;
		private LineCap lineCap = LineCap.butt;
		private float dashOffset;
		private final FloatArray dashes = new FloatArray();
		private AffineTransform xform = AffineTransform.obtain();
		
		boolean isChanged(Canvas canvas) {
			CanvasState canvasState = canvas.currentState;
			return devicePixelRatio != canvas.devicePixelRatio
					|| strokeWidth != canvasState.strokeWidth
					|| miterLimit != canvasState.miterLimit
					|| lineJoin != canvasState.lineJoin
					|| lineCap != canvasState.lineCap
					|| dashOffset != canvasState.dashOffset
					|| !dashes.equals(canvasState.dashArray)
					|| !xform.equals(canvasState.xform);
		}
		
		void update(Canvas canvas) {
			CanvasState canvasState = canvas.currentState;
			devicePixelRatio = canvas.devicePixelRatio;
			strokeWidth = canvasState.strokeWidth;
			miterLimit = canvasState.miterLimit;
			lineJoin = canvasState.lineJoin;
			lineCap = canvasState.lineCap;
			dashOffset = canvasState.dashOffset;
			dashes.clear();
			dashes.addAll(canvasState.dashArray);
			xform.set(canvasState.xform);
		}
	}
}
