package MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.js.util;

/**
 * Utilities for dealing with time.
 * 
 * @author Timo G&uuml;nther
 */
public abstract class TimeUtils {

	/** Multiply to turn seconds to milliseconds. */
	public static final double S_TO_MS = 1000;
	/** Multiply to turn milliseconds to seconds. */
	public static final double MS_TO_S = 0.001;

	/**
	 * Converts the given time from seconds to milliseconds.
	 * @param t time in seconds
	 * @return time in milliseconds
	 */
	public static long sToMs(double t) {
		return Math.round(t * S_TO_MS);
	}

	/**
	 * Converts the given time from milliseconds to seconds.
	 * @param t time in milliseconds
	 * @return time in seconds
	 */
	public static double msToS(long t) {
		return t * MS_TO_S;
	}
}
