package MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.js;

import static MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.js.JsCamera.IMG_SIZE;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.js.util.GraphicsUtils;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.js.util.Mats;

/**
 * The graphical user interface for the {@link JsEnactor enactor}.
 * Shows the drone's {@link JsCamera video stream} and the result of the {@link JsVision image processing}.
 * Also allows choosing the {@link JsCamera#setMinColor(Color) minimum} and {@link JsCamera#setMaxColor(Color) maximum color}.
 * 
 * @author Timo G&uuml;nther
 */
public class JsEnactorGui extends JFrame {

	/**
	 * A panel showing an image.
	 * Functions as a video display by repeatedly updating the image and then repainting.
	 * 
	 * @author Timo G&uuml;ther
	 */
	private class ImagePanel extends JPanel {

		private static final long serialVersionUID = 8582828288222L;

		/** The image to display. */
		private BufferedImage img;
		/** The position to highlight. */
		private Point2D.Double pos;

		/**
		 * Constructs a new instance of this class.
		 * @param img image to display
		 */
		public ImagePanel(BufferedImage img) {
			this.img = img;
		}

		@Override
		protected void paintComponent(Graphics g) {
			final Dimension size = getSize();
			g.drawImage(img, 0, 0, size.width, size.height, null);
			if (pos != null) {
				final int x = (int) Math.round(pos.x);
				final int y = (int) Math.round(pos.y);
				final int r = 5;
				g.setColor(Color.RED);
				GraphicsUtils.drawCrosshair(g, x, y, r);
			}
		}
	
		@Override
		public Dimension getMinimumSize() {
			return getPreferredSize();
		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(img.getWidth(), img.getHeight());
		}

		@Override
		public Dimension getMaximumSize() {
			return getPreferredSize();
		}
	}

	/** Serial version UID. */
	private static final long serialVersionUID = 222967928351624L;

	/** The image panel for the color image. */
	private final ImagePanel imgPanelIn;
	/** The image panel for the gray image. */
	private final ImagePanel imgPanelOut;
	
	/** The panel holding the two color choosers. */
	private final JFrame colorFrame = new JFrame();
	/** The color chooser for the minimum color. */
	private final JColorChooser minColorChooser = new JColorChooser();
	/** The color chooser for the maximum color. */
	private final JColorChooser maxColorChooser = new JColorChooser();

	/**
	 * Constructs a new instance of this class.
	 * @param cam drone camera
	 * @param vis drone vision
	 */
	public JsEnactorGui(JsCamera cam, JsVision vis) {
		// Create the components.
		imgPanelIn = new ImagePanel(new BufferedImage(IMG_SIZE.width, IMG_SIZE.height, BufferedImage.TYPE_3BYTE_BGR));
		imgPanelOut = new ImagePanel(new BufferedImage(IMG_SIZE.width, IMG_SIZE.height, BufferedImage.TYPE_BYTE_GRAY));

		// Create the color frame.
		colorFrame.setTitle("Target Color");
		colorFrame.getContentPane().setLayout(new BoxLayout(colorFrame.getContentPane(), BoxLayout.X_AXIS));
		colorFrame.getContentPane().add(minColorChooser);
		colorFrame.getContentPane().add(maxColorChooser);
		colorFrame.pack();
		colorFrame.setMinimumSize(colorFrame.getSize());

		// Create the menu.
		setJMenuBar(new JMenuBar());
		JMenu menu = new JMenu("Settings");
		getJMenuBar().add(menu);
		JMenuItem menuItem = new JMenuItem("Target Color");
		menu.add(menuItem);
		menuItem.addActionListener(e -> colorFrame.setVisible(true));

		// Add the components.
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));
		getContentPane().add(imgPanelIn);
		getContentPane().add(imgPanelOut);

		// Listen to the camera.
		cam.addListener(e -> {
			try {
				imgPanelIn.img = ImageIO.read(new ByteArrayInputStream(e.getImageData()));
				SwingUtilities.invokeLater(() -> imgPanelIn.repaint());
			} catch (IOException ex) {}
		});

		// Listen to the image processor.
		vis.addListener(e -> {
			Mats.copyToBufferedImage(e.getOutputImage(), imgPanelOut.img);
			imgPanelOut.pos = e.getTargetImagePosition();
			SwingUtilities.invokeLater(() -> imgPanelOut.repaint());
		});

		// Link the color choosers to the image processor.
		minColorChooser.setColor(vis.getMinColor());
		minColorChooser.getSelectionModel().addChangeListener(e -> {
			vis.setMinColor(minColorChooser.getColor());
		});
		maxColorChooser.setColor(vis.getMaxColor());
		maxColorChooser.getSelectionModel().addChangeListener(e -> {
			vis.setMaxColor(maxColorChooser.getColor());
		});

		// Set the size.
		pack();
		setMinimumSize(getSize());
	}
}
