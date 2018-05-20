package com.gurella.engine.graphics.vector;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;

class CanvasUtils {
	private CanvasUtils() {
	}

	static float sqrtf(float a) {
		return (float) Math.sqrt(a);
	}

	static float tanf(float a) {
		return (float) Math.tan(a);
	}

	static float acosf(float a) {
		return (float) Math.acos(a);
	}

	static float sign(float a) {
		return a >= 0.0f ? 1.0f : -1.0f;
	}

	static float cross(float dx0, float dy0, float dx1, float dy1) {
		return dx1 * dy0 - dx0 * dy1;
	}

	static float normalize(Vector2 point) {
		float length = sqrtf(point.x * point.x + point.y * point.y);
		if (length > 1e-6f) {
			float inversedLength = 1.0f / length;
			point.x *= inversedLength;
			point.y *= inversedLength;
		}

		return length;
	}

	static String arrayToString(Array<?> values) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");

		for (Object object : values) {
			builder.append("\n\t\t");
			builder.append(object);
		}

		builder.append("\n\t\t]");
		return builder.toString();
	}
}
