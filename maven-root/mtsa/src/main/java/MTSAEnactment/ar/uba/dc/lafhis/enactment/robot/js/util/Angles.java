package MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.js.util;

/**
 * Utilities for dealing with angles.
 * 
 * @author Timo G&uuml;nther
 */
public abstract class Angles {

	/** Multiply to convert radians to degrees. */
	public static final double DEGREES_TO_RADIANS = Math.PI / 180;
	/** Multiply to convert degrees to radians. */
	public static final double RADIANS_TO_DEGREES = 180 / Math.PI;

	/**
	 * Converts the given angle from degrees to radians.
	 * @param a angle in degrees
	 * @return angle in radians
	 */
	public static double degreesToRadians(double a) {
		return a * DEGREES_TO_RADIANS;
	}

	/**
	 * Converts the given angle from radians to degrees.
	 * @param a angle in radians
	 * @return angle in degrees
	 */
	public static double radiansToDegrees(double a) {
		return a * RADIANS_TO_DEGREES;
	}

	/**
	 * Converts the given angle from radians to degrees.
	 * @param a angle in radians
	 * @return angle in degrees as integer
	 */
	public static int radiansToDegreesInt(double a) {
		return (int) Math.round(radiansToDegrees(a));
	}
}
