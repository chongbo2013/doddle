package com.gurella.engine.graphics.vector;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;

public class PathShape extends Shape {
	private Array<PathCommand> commands = new Array<PathCommand>();
	
	public PathShape arcTo(float startX, float startY, float endX, float endY, float radius) {
		return arcTo(false, startX, startY, endX, endY, radius);
	}
	
	public PathShape arcToRel(float startX, float startY, float endX, float endY, float radius) {
		return arcTo(true, startX, startY, endX, endY, radius);
	}

	public PathShape arcTo(boolean relative, float startX, float startY, float endX, float endY, float radius) {
		ArcToCommand command = FastPools.obtainArcToCommand();
		command.relative = relative;
		command.startX = startX;
		command.startY = startY;
		command.endX = endX;
		command.endY = endY;
		command.radius = radius;
		commands.add(command);
		return this;
	}
	
	public PathShape arcTo(float radiusX, float radiusY, float angleDegrees, boolean largeArcFlag, boolean sweepFlag, float x, float y) {
		return arcTo(false, radiusX, radiusY, angleDegrees, largeArcFlag, sweepFlag, x, y);
	}
	
	public PathShape arcToRel(float radiusX, float radiusY, float angleDegrees, boolean largeArcFlag, boolean sweepFlag, float x, float y) {
		return arcTo(true, radiusX, radiusY, angleDegrees, largeArcFlag, sweepFlag, x, y);
	}
	
	public PathShape arcToRad(float radiusX, float radiusY, float angleRadians, boolean largeArcFlag, boolean sweepFlag, float x, float y) {
		return arcTo(false, radiusX, radiusY, MathUtils.radiansToDegrees * angleRadians, largeArcFlag, sweepFlag, x, y);
	}
	
	public PathShape arcToRadRel(float radiusX, float radiusY, float angleRadians, boolean largeArcFlag, boolean sweepFlag, float x, float y) {
		return arcTo(true, radiusX, radiusY, MathUtils.radiansToDegrees * angleRadians, largeArcFlag, sweepFlag, x, y);
	}
	
	public PathShape arcTo(boolean relative, float radiusX, float radiusY, float angleDegrees, boolean largeArcFlag, boolean sweepFlag, float x, float y) {
		SvgArcToCommand command = FastPools.obtainSvgArcToCommand();
		command.relative = relative;
		command.radiusX = radiusX;
		command.radiusY = radiusY;
		command.angleDegrees = angleDegrees;
		command.largeArcFlag = largeArcFlag;
		command.sweepFlag = sweepFlag;
		command.x = x;
		command.y = y;
		commands.add(command);
		return this;
	}

	public PathShape moveTo(float x, float y) {
		return moveTo(false, x, y);
	}
	
	public PathShape moveToRel(float x, float y) {
		return moveTo(true, x, y);
	}

	public PathShape moveTo(boolean relative, float x, float y) {
		MoveToCommand command = FastPools.obtainMoveToCommand();
		command.relative = relative;
		command.x = x;
		command.y = y;
		commands.add(command);
		return this;
	}
	
	public PathShape verticalMoveTo(float y) {
		return verticalMoveTo(false, y);
	}
	
	public PathShape verticalMoveToRel(float y) {
		return verticalMoveTo(true, y);
	}

	public PathShape verticalMoveTo(boolean relative, float y) {
		VerticalMoveToCommand command = FastPools.obtainVerticalMoveToCommand();
		command.relative = relative;
		command.y = y;
		commands.add(command);
		return this;
	}

	public PathShape horizontalMoveTo(float x) {
		return horizontalMoveTo(false, x);
	}
	

	public PathShape horizontalMoveToRel(float x) {
		return horizontalMoveTo(true, x);
	}

	public PathShape horizontalMoveTo(boolean relative, float x) {
		HorizontalMoveToCommand command = FastPools.obtainHorizontalMoveToCommand();
		command.relative = relative;
		command.x = x;
		commands.add(command);
		return this;
	}

	public PathShape lineTo(float x, float y) {
		return lineTo(false, x, y);
	}
	
	public PathShape lineToRel(float x, float y) {
		return lineTo(true, x, y);
	}

	private PathShape lineTo(boolean relative, float x, float y) {
		LineToCommand command = FastPools.obtainLineToCommand();
		command.relative = relative;
		command.x = x;
		command.y = y;
		commands.add(command);
		return this;
	}

	public PathShape verticalLineTo(float y) {
		return verticalLineTo(false, y);
	}
	
	public PathShape verticalLineToRel(float y) {
		return verticalLineTo(true, y);
	}

	public PathShape verticalLineTo(boolean relative, float y) {
		VerticalLineToCommand command = FastPools.obtainVerticalLineToCommand();
		command.relative = relative;
		command.y = y;
		commands.add(command);
		return this;
	}

	public PathShape horizontalLineTo(float x) {
		return horizontalLineTo(false, x);
	}
	

	public PathShape horizontalLineToRel(float x) {
		return horizontalLineTo(true, x);
	}

	public PathShape horizontalLineTo(boolean relative, float x) {
		HorizontalLineToCommand command = FastPools.obtainHorizontalLineToCommand();
		command.relative = relative;
		command.x = x;
		commands.add(command);
		return this;
	}

	public PathShape quadTo(float controlX, float controlY, float x, float y) {
		return quadTo(false, controlX, controlY, x, y);
	}
	
	public PathShape quadToRel(float controlX, float controlY, float x, float y) {
		return quadTo(true, controlX, controlY, x, y);
	}

	public PathShape quadTo(boolean relative, float controlX, float controlY, float x, float y) {
		QuadToCommand command = FastPools.obtainQuadToCommand();
		command.relative = relative;
		command.controlX = controlX;
		command.controlY = controlY;
		command.x = x;
		command.y = y;
		commands.add(command);
		return this;
	}

	public PathShape quadSmoothTo(float x, float y) {
		return quadSmoothTo(false, x, y);
	}
	
	public PathShape quadSmoothToRel(float x, float y) {
		return quadSmoothTo(true, x, y);
	}

	public PathShape quadSmoothTo(boolean relative, float x, float y) {
		QuadSmoothToCommand command = FastPools.obtainQuadSmoothToCommand();
		command.relative = relative;
		command.x = x;
		command.y = y;
		commands.add(command);
		return this;
	}

	public PathShape cubicTo(float controlX1, float controlY1, float controlX2, float controlY2, float x, float y) {
		return cubicTo(false, controlX1, controlY1, controlX2, controlY2, x, y);
	}
	
	public PathShape cubicToRel(float controlX1, float controlY1, float controlX2, float controlY2, float x, float y) {
		return cubicTo(true, controlX1, controlY1, controlX2, controlY2, x, y);
	}

	public PathShape cubicTo(boolean relative, float controlX1, float controlY1, float controlX2, float controlY2, float x, float y) {
		CubicToCommand command = FastPools.obtainCubicToCommand();
		command.relative = relative;
		command.controlX1 = controlX1;
		command.controlY1 = controlY1;
		command.controlX2 = controlX2;
		command.controlY2 = controlY2;
		command.x = x;
		command.y = y;
		commands.add(command);
		return this;
	}

	public PathShape cubicSmoothTo(float controlX2, float controlY2, float x, float y) {
		return cubicSmoothTo(false, controlX2, controlY2, x, y);
	}
	
	public PathShape cubicSmoothToRel(float controlX2, float controlY2, float x, float y) {
		return cubicSmoothTo(true, controlX2, controlY2, x, y);
	}

	public PathShape cubicSmoothTo(boolean relative, float controlX2, float controlY2, float x, float y) {
		CubicSmoothToCommand command = FastPools.obtainCubicSmoothToCommand();
		command.relative = relative;
		command.controlX2 = controlX2;
		command.controlY2 = controlY2;
		command.x = x;
		command.y = y;
		commands.add(command);
		return this;
	}

	public PathShape closePath() {
		commands.add(FastPools.obtainCloseCommand());
		return this;
	}

	public PathShape winding(Winding winding) {
		WindingCommand command = FastPools.obtainWindingCommand();
		command.winding = winding;
		commands.add(command);
		return this;
	}

	@Override
	public void reset() {
		super.reset();
                FastPools.resetPathCommands(commands);
	}

	@Override
	protected void initPath(Path path) {
		for(int i = 0; i < commands.size; i++) {
			PathCommand command = commands.get(i);
			command.appendPath(path);
		}
	}
	
	static abstract class PathCommand {
		protected boolean relative;

		protected abstract void appendPath(Path path);
	}

	static class SvgArcToCommand extends PathCommand {
		private float radiusX; 
		private float radiusY; 
		private float angleDegrees;
		private boolean largeArcFlag;
		private boolean sweepFlag;
		private float x;
		private float y;

		@Override
		protected void appendPath(Path path) {
			if(relative) {
				path.arcToRel(radiusX, radiusY, angleDegrees, largeArcFlag, sweepFlag, radiusX, radiusY);
			} else {
				path.arcTo(radiusX, radiusY, angleDegrees, largeArcFlag, sweepFlag, radiusX, radiusY);
			}
		}

		@Override
		public String toString() {
			String command = relative ? "a " : "A ";
			return command + radiusX + " " + radiusY + " " + angleDegrees + " " + largeArcFlag
					+ " " + sweepFlag + " " + x + " " + y;
		}
	}

	static class ArcToCommand extends PathCommand {
		private float startX;
		private float startY;
		private float endX;
		private float endY;
		private float radius;

		@Override
		protected void appendPath(Path path) {
			if(relative) {
				path.arcToRel(startX, startY, endX, endY, radius);
			} else {
				path.arcTo(startX, startY, endX, endY, radius);
			}
		}

		@Override
		public String toString() {
			String command = relative ? "a " : "A ";
			return command + startX + " " + startY + " " + endX + " " + endY + " " + radius;
		}
	}

	static class CubicToCommand extends PathCommand {
		private float controlX1;
		private float controlY1;
		private float controlX2;
		private float controlY2;
		private float x;
		private float y;

		@Override
		protected void appendPath(Path path) {
			if(relative) {
				path.cubicTo(controlX1, controlY1, controlX2, controlY2, x, y);
			} else {
				path.cubicToRel(controlX1, controlY1, controlX2, controlY2, x, y);
			}
		}

		@Override
		public String toString() {
			String command = relative ? "c " : "C ";
			return command + controlX1 + " " + controlY1 + " " + controlX2 + " " + controlY2 + " " + x + " " + y;
		}
	}

	static class CubicSmoothToCommand extends PathCommand {
		private float controlX2;
		private float controlY2;
		float x;
		float y;

		@Override
		protected void appendPath(Path path) {
			if(relative) {
				path.cubicSmoothTo(controlX2, controlY2, x, y);
			} else {
				path.cubicSmoothToRel(controlX2, controlY2, x, y);
			}
		}

		@Override
		public String toString() {
			String command = relative ? "s " : "S ";
			return command + " " + controlX2 + " " + controlY2 + " " + x + " " + y;
		}
	}

	static class QuadToCommand extends PathCommand {
		private float controlX;
		private float controlY;
		private float x;
		private float y;

		@Override
		protected void appendPath(Path path) {
			if(relative) {
				path.quadToRel(controlX, controlY, x, y);
			} else {
				path.quadTo(controlX, controlY, x, y);
			}
		}

		@Override
		public String toString() {
			String command = relative ? "q " : "Q ";
			return command + controlX + " " + controlY + " " + x + " " + y;
		}
	}

	static class QuadSmoothToCommand extends PathCommand {
		float x;
		float y;

		@Override
		protected void appendPath(Path path) {
			if(relative) {
				path.quadSmoothToRel(x, y);
			} else {
				path.quadSmoothTo(x, y);
			}
		}

		@Override
		public String toString() {
			String command = relative ? "t " : "T ";
			return command + x + " " + y;
		}
	}

	static class MoveToCommand extends PathCommand {
		float x;
		float y;

		@Override
		protected void appendPath(Path path) {
			if(relative) {
				path.moveToRel(x, y);
			} else {
				path.moveTo(x, y);
			}
		}

		@Override
		public String toString() {
			String command = relative ? "m " : "M ";
			return command + x + " " + y;
		}
	}

	static class VerticalMoveToCommand extends PathCommand {
		float y;

		@Override
		protected void appendPath(Path path) {
			if(relative) {
				path.verticalMoveToRel(y);
			} else {
				path.verticalMoveTo(y);
			}
		}

		@Override
		public String toString() {
			String command = relative ? "vm " : "VM ";
			return command + y;
		}
	}

	static class HorizontalMoveToCommand extends PathCommand {
		float x;

		@Override
		protected void appendPath(Path path) {
			if(relative) {
				path.horizontalMoveToRel(x);
			} else {
				path.horizontalMoveTo(x);
			}
		}

		@Override
		public String toString() {
			String command = relative ? "hm " : "HM ";
			return command + x;
		}
	}

	static class CloseCommand extends PathCommand {
		@Override
		protected void appendPath(Path path) {
			path.close();
		}

		@Override
		public String toString() {
			return "Z";
		}
	}

	public static class LineToCommand extends PathCommand {
		float x;
		float y;

		@Override
		protected void appendPath(Path path) {
			if(relative) {
				path.lineToRel(x, y);
			} else {
				path.lineTo(x, y);
			}
		}

		@Override
		public String toString() {
			String command = relative ? "l " : "L ";
			return command + x + " " + y;
		}
	}

	static class VerticalLineToCommand extends PathCommand {
		float y;

		@Override
		protected void appendPath(Path path) {
			if(relative) {
				path.verticalLineToRel(y);
			} else {
				path.verticalLineTo(y);
			}
		}

		@Override
		public String toString() {
			String command = relative ? "v " : "V ";
			return command + y;
		}
	}

	static class HorizontalLineToCommand extends PathCommand {
		float x;

		@Override
		protected void appendPath(Path path) {
			if(relative) {
				path.horizontalLineToRel(x);
			} else {
				path.horizontalLineTo(x);
			}
		}

		@Override
		public String toString() {
			String command = relative ? "h " : "H ";
			return command + x;
		}
	}

	static class WindingCommand extends PathCommand {
		Winding winding;

		@Override
		protected void appendPath(Path path) {
			path.winding(winding);
		}

		@Override
		public String toString() {
			return "W " + winding.name();
		}
	}
}
