package MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.js.util;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

import nu.pattern.OpenCV;

/**
 * Utilities for {@link Mat matrices} and {@link Scalar scalars}.
 * 
 * @author Timo G&uuml;nther
 */
public abstract class Mats {

	static {
		OpenCV.loadShared();
	}

	/** An empty matrix. */
	private static final Mat EMPTY_MAT = new Mat();

	/**
	 * Performs matrix multiplication.
	 * @param a left operand
	 * @param b right operand
	 * @return multiplication result
	 */
	public static Mat mul(Mat a, Mat b) {
		final Mat c = new Mat();
		Core.gemm(a, b, 1, EMPTY_MAT, 0, c);
		return c;
	}

	/**
	 * Copies the given matrix to the given buffered image.
	 * @param m source matrix
	 * @param bi target buffered image
	 * @return target buffered image for convenience
	 */
	public static BufferedImage copyToBufferedImage(Mat m, BufferedImage bi) {
		m.get(0, 0, ((DataBufferByte) bi.getRaster().getDataBuffer()).getData());
		return bi;
	}

	/**
	 * Converts the given color in RGB to a scalar in HSV.
	 * @param c color in RGB
	 * @return scalar in HSV
	 */
	public static Scalar colorRgbToScalarHsv(Color c) {
		final float[] hsv = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
		return new Scalar(hsv[0] * 179, hsv[1] * 255, hsv[2] * 255);
	}

	/**
	 * Converts the given scalar in HSV to a color in RGB.
	 * @param s scalar in HSV
	 * @return color in RGB
	 */
	public static Color scalarHsvToColorRgb(Scalar s) {
		return Color.getHSBColor((float) s.val[0] / 179, (float) s.val[1] / 255, (float) s.val[2] / 255);
	}
}
