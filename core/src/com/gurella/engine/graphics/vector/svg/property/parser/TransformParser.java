package com.gurella.engine.graphics.vector.svg.property.parser;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.FloatArray;
import com.dodles.gdx.dodleengine.DodleEngine;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.gurella.engine.graphics.vector.AffineTransform;
import com.gurella.engine.graphics.vector.svg.property.PropertyParser;

public class TransformParser implements PropertyParser<AffineTransform> {
	public static final TransformParser instance = new TransformParser();

	private final RegExp regExp = RegExp.compile("\\w+\\([^)]*\\)", "g");
	private final RegExp xformRegExp = RegExp.compile("[-.\\w]+", "g");
	private MatchResult matcher;
	private MatchResult xformMatcher;

	private final FloatArray transformTerms = new FloatArray();
	private final AffineTransform tempXform1 = AffineTransform.obtain();
	private final AffineTransform tempXform2 = AffineTransform.obtain();

	private TransformParser() {
	}

	@Override
	public synchronized AffineTransform parse(String strValue) {
		AffineTransform xform = AffineTransform.obtain();

		for (matcher = regExp.exec(strValue); matcher != null; matcher = regExp.exec(strValue)) {
			xform.mulLeft(parseTransform(matcher.getGroup(0)));
		}

		return xform;
	}
	
	
	private AffineTransform parseTransform(String val) {
		tempXform1.idt();

		String function = xformRegExp.exec(val).getGroup(0);

		if (function == null) {
			return tempXform1;
		}

		transformTerms.clear();

		for (xformMatcher = xformRegExp.exec(val); xformMatcher != null; xformMatcher = xformRegExp.exec(val)) {
			transformTerms.add(Float.parseFloat(xformMatcher.getGroup(0)));
		}

		if (function.equals("matrix")) {
			return tempXform1.set(transformTerms.get(0), transformTerms.get(1), transformTerms.get(2), transformTerms.get(3), transformTerms.get(4), transformTerms.get(5));
		} else if (function.equals("translate")) {
			if (transformTerms.size == 1) {
				return tempXform1.setToTranslation(transformTerms.get(0), 0);
			} else {
				return tempXform1.setToTranslation(transformTerms.get(0), transformTerms.get(1));
			}
		} else if (function.equals("scale")) {
			if (transformTerms.size > 1) {
				return tempXform1.setToScaling(transformTerms.get(0), transformTerms.get(1));
			} else {
				return tempXform1.setToScaling(transformTerms.get(0), transformTerms.get(0));
			}
		} else if (function.equals("rotate")) {
			if (transformTerms.size > 2) {
				float translateX = transformTerms.get(1);
				float translateY = transformTerms.get(2);
				
				tempXform1.setToTranslation(translateX, translateY);
				tempXform2.setToRotation(transformTerms.get(0));
				tempXform1.mul(tempXform2);
				tempXform2.setToTranslation(-translateX, -translateY);
				return tempXform1.mul(tempXform2);
			} else {
				return tempXform1.setToRotation(transformTerms.get(0));
			}
		} else if (function.equals("skewx")) {
			return tempXform1.setToSkewX(transformTerms.get(0));
		} else if (function.equals("skewy")) {
			return tempXform1.setToSkewY(transformTerms.get(0));
		} else {
			DodleEngine.getLogger().debug("SvgParseUtil", "Unknown transform type");
		}

		return tempXform1;
	}
}
