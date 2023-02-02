package MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.js.util;

import java.awt.Graphics;

/**
 * Utilities for dealing with {@link Graphics}.
 * 
 * @author Timo G&uuml;nther
 */
public abstract class GraphicsUtils {

	/**
	 * Draws a crosshair.
	 * @param g graphics to paint with
	 * @param x horizontal position
	 * @param y vertical position
	 * @param r radius
	 */
	public static void drawCrosshair(Graphics g, int x, int y, int r) {
		g.drawLine(x - r, y, x + r, y);
		g.drawLine(x, y - r, x, y + r);
	}
}
