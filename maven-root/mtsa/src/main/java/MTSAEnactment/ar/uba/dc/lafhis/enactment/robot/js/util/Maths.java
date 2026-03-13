package MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.js.util;

/**
 * Mathematical utilities.
 * 
 * @author Timo G&uuml;nther
 */
public abstract class Maths {

	/**
	 * Clamps the given value to be in [min, max].
	 * @param a value to clamp
	 * @param min minimum
	 * @param max maximum
	 * @return clamped value
	 */
	public static double clamp(double a, double min, double max) {
		return Math.max(min, Math.min(max, a));
	}

	/**
	 * Maps the given value from the given source range to the given target range.
	 * @param x value to map
	 * @param xMax lower bound of the source range
	 * @param xMin upper bound of the source range
	 * @param yMin lower bound of the target range
	 * @param yMax upper bound of the target range
	 * @return mapped value
	 * @throws IllegalArgumentException if the source range is a singularity, i.e., both of its bounds are equal
	 */
	public static double mapLinear(double x, double xMax, double xMin, double yMin, double yMax)
			throws IllegalArgumentException {
		return scaleLinear(normalizeLinear(x, xMin, xMax), yMin, yMax);
	}

	/**
	 * Normalizes the given value from the given range.
	 * In other words, evaluates the linear mapping from the given range to the unit range for the given value.
	 * A value equal to the lower bound is mapped to 0.
	 * A value equal to the upper bound is mapped to 1.
	 * All other values are mapped linearly between the bounds.
	 * @param x value to normalize
	 * @param xMin lower bound of the range
	 * @param xMax upper bound of the range
	 * @return normalized value
	 * @throws IllegalArgumentException if the range is a singularity, i.e., both of its bounds are equal
	 */
	public static double normalizeLinear(double x, double xMin, double xMax)
			throws IllegalArgumentException {
		final double dx = xMax - xMin;
		if (dx == 0) {
			throw new IllegalArgumentException("Range is singularity");
		}
		return (x - xMin) / dx;
	}

	/**
	 * Scales the given value to the given range.
	 * In other words, evaluates the linear mapping from the unit range to the given range for the given value.
	 * A value of 0 is mapped to the lower bound.
	 * A value of 1 is mapped to the upper bound.
	 * All other values are mapped linearly between the bounds.
	 * @param x value to scale
	 * @param yMin lower bound of the range
	 * @param yMax upper bound of the range
	 * @return scaled value
	 */
	public static double scaleLinear(double x, double yMin, double yMax) {
		return yMin + (yMax - yMin) * x;
	}
}
