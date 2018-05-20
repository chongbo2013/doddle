package com.gurella.engine.graphics.vector.svg.property.parser;

import com.badlogic.gdx.utils.FloatArray;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.gurella.engine.graphics.vector.svg.property.PropertyParser;

public class FloatArrayParser implements PropertyParser<FloatArray> {
	public static final FloatArrayParser instance = new FloatArrayParser();
	
	private final RegExp regExp = RegExp.compile("([-+]?((\\d*\\.\\d+)|(\\d+))([eE][+-]?\\d+)?)(\\%|in|cm|mm|pt|pc|px|em|ex)?", "g");
	private MatchResult matcher;

	private FloatArrayParser() {
	}
	
	@Override
	public synchronized FloatArray parse(String strValue) {
		FloatArray floatArray = new FloatArray();

		for (matcher = regExp.exec(strValue); matcher != null; matcher = regExp.exec(strValue)) {
			float value = Float.parseFloat(matcher.getGroup(1));
			String units = matcher.getGroup(6);
			floatArray.add("%".equals(units) ? value / 100 : value);
		}

		return floatArray;
	}
}
