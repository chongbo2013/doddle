package com.gurella.engine.graphics.vector.svg.property.parser;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.gurella.engine.graphics.vector.svg.property.PropertyParser;

public class FloatParser implements PropertyParser<Float> {
	public static final FloatParser instance = new FloatParser();

	private FloatParser() {
	}

	@Override
	public Float parse(String strValue) {
		return Float.valueOf(parseFloat(strValue));
	}

	public static synchronized float parseFloat(String strValue) {
		try {
			final RegExp regExp = RegExp.compile("([-+]?((\\d*\\.\\d+)|(\\d+))([eE][+-]?\\d+)?)(\\%|in|cm|mm|pt|pc|px|em|ex)?", "g");
			MatchResult matcher;

			matcher = regExp.exec(strValue);
			float value = Float.parseFloat(matcher.getGroup(1));
			String units = matcher.getGroup(6);
			return "%".equals(units) ? value / 100 : value;
		} catch (Exception e) {
			return 0;
		}
	}
}
