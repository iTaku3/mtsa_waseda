package MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.js;

import static MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.js.JsCamera.IMG_SIZE;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.js.JsCamera.ImageReceivedEvent;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.js.util.Mats;
import nu.pattern.OpenCV;

/**
 * Processes images from the drone's {@link JsCamera camera}.
 * Finds target locations in the image.
 * A target is identified through a color range.
 * 
 * @author Timo G&uuml;nther
 */
public class JsVision {

	static {
		OpenCV.loadShared();
	}

	/**
	 * Listens to {@link JsVision image processing}.
	 * 
	 * @author Timo G&uuml;nther
	 */
	public interface Listener {

		/**
		 * Called when an image was received but ignored (i.e., not processed) due to time constraints.
		 * @param e event
		 */
		public default void onImageIgnored(ImageIgnoredEvent e) {}

		/**
		 * Called when an image was processed.
		 * @param e event
		 */
		public void onImageProcessed(ImageProcessedEvent e);
	}

	/**
	 * An event fired by {@link JsVision}.
	 * 
	 * @author Timo G&uuml;nther
	 */
	public abstract class Event extends JsEvent<JsVision> {

		/** The event that caused this event. */
		private final ImageReceivedEvent imageReceivedEvent;

		/**
		 * Constructs a new instance of this class.
		 * @param time the time in milliseconds when the event was fired
		 * @param imageReceivedEvent the event that caused this event
		 */
		public Event(long time, ImageReceivedEvent imageReceivedEvent) {
			super(JsVision.this, time);
			this.imageReceivedEvent = imageReceivedEvent;
		}

		/**
		 * Returns the event that caused this event.
		 * @return the event that caused this event
		 */
		public ImageReceivedEvent getImageReceivedEvent() {
			return imageReceivedEvent;
		}
	}

	/**
	 * An event indicating that an image was ignored due to time constraints.
	 * 
	 * @author Timo G&uuml;nther
	 */
	public class ImageIgnoredEvent extends Event {

		/**
		 * Constructs a new instance of this class.
		 * @param time the time in milliseconds when the image was ignored
		 * @param imageReceivedEvent the event that caused this event
		 */
		public ImageIgnoredEvent(long time, ImageReceivedEvent imageReceivedEvent) {
			super(time, imageReceivedEvent);
		}
	}

	/**
	 * An event indicating that an image was processed.
	 * 
	 * @author Timo G&uuml;nther
	 */
	public class ImageProcessedEvent extends Event {

		/** The processed output image. */
		private final Mat imgOut;
		/** The position of the target in the image. */
		private final Point2D.Double targetImgPos;
		/** The position of the target relative to the drone in meters. */
		private final Point2D.Double targetPos;
		/** The yaw to the target. */
		private final double targetYaw;
		/** The distance to the target in meters. */
		private final double targetDistance;

		/**
		 * Constructs a new instance of this class.
		 * @param time the time in milliseconds when the image was processed
		 * @param imageReceivedEvent the event that caused this event
		 * @param imgOut the processed output image
		 * @param targetImgPos the position of the target in the image
		 */
		public ImageProcessedEvent(
				long time,
				ImageReceivedEvent imageReceivedEvent,
				Mat imgOut,
				Point2D.Double targetImgPos) {
			super(time, imageReceivedEvent);
			this.imgOut = imgOut;
			this.targetImgPos = targetImgPos;
			if (targetImgPos == null) {
				this.targetPos = null;
				this.targetYaw = Double.NaN;
				this.targetDistance = Double.NaN;
			} else {
				final double scale = 0.001; // millimeters to meters
				this.targetPos = new Point2D.Double(
						(targetImgPos.x - DRONE_ORIGIN.x) * scale,
						-(targetImgPos.y - DRONE_ORIGIN.y) * scale);
				this.targetYaw = Math.atan(targetPos.x / targetPos.y);
				this.targetDistance = Point2D.distance(0, 0, targetPos.x, targetPos.y);
			}
		}

		/**
		 * Returns the processed output image.
		 * @return the processed output image
		 */
		public Mat getOutputImage() {
			return imgOut;
		}

		/**
		 * Returns the position of the target in the output image.
		 * @return the position of the target in the output image
		 */
		public Point2D.Double getTargetImagePosition() {
			return targetImgPos;
		}

		/**
		 * Returns the position of the target relative to the drone in meters.
		 * @return the position of the target relative to the drone in meters
		 */
		public Point2D.Double getTargetPosition() {
			return targetPos;
		}

		/**
		 * Returns the yaw to the target.
		 * @return the yaw to the target
		 */
		public double getTargetYaw() {
			return targetYaw;
		}

		/**
		 * Returns the distance to the target in meters.
		 * @return the distance to the target in meters
		 */
		public double getTargetDistance() {
			return targetDistance;
		}

		/**
		 * Returns true iff the target was found.
		 * @return true iff the target was found
		 */
		public boolean isTargetFound() {
			return targetPos != null;
		}
	}

	/** The listeners to notify. */
	private final List<Listener> listeners = new LinkedList<>();

	// perspective
	/** The origin of the drone in the bird's eye view image. */
	private static final Point2D.Double DRONE_ORIGIN = new Point2D.Double(IMG_SIZE.getWidth() / 2, IMG_SIZE.getHeight() + 70);
	/**
	 * The transformation matrix defining the homography between image plane and bird's eye view.
	 * Each pixel in the bird's eye view has a width and height of 1 millimeter.
	 * Thus, the bird's eye view can be used to measure distances and angles in physical relationship to the drone.
	 */
	private static final Mat HOMOGRAPHY;
	static {
		final Mat homography = Mat.ones(3, 3, CvType.CV_64F);
		homography.put(0, 0, -0.22211974108450958);
		homography.put(0, 1, -0.04196213522710946);
		homography.put(0, 2, 80.76137619413272);
		homography.put(1, 0, 0.0029084348927548836);
		homography.put(1, 1, 0.012063817238358912);
		homography.put(1, 2, 56.992613333692134);
		homography.put(2, 0, 3.572001877049044E-5);
		homography.put(2, 1, -0.004026644174618831);
		final Mat translate = Mat.zeros(3, 3, CvType.CV_64F); // shifts drone position in image from image origin to drone origin
		translate.put(0, 0, 1);
		translate.put(1, 1, 1);
		translate.put(2, 2, 1);
		translate.put(0, 2, DRONE_ORIGIN.x);
		translate.put(1, 2, DRONE_ORIGIN.y);
		HOMOGRAPHY = Mats.mul(translate, homography);
	}
	/** The inverse homography. */
	private static final Mat INV_HOMOGRAPHY = HOMOGRAPHY.inv();

	// input: image
	/** The unprocessed image from the camera. */
	private Mat img = new Mat();

	// image processing
	/** The width of the vertical strip in the center of the image to be considered for target detection. */
	private static final int IMG_CROP_WIDTH = 500;
	/** Rectangle for cropping the image so that only a narrow vertical strip is considered for target detection. */
	private static final Rect IMG_CROP = new Rect(
			(int) (DRONE_ORIGIN.x - IMG_CROP_WIDTH / 2),
			0,
			IMG_CROP_WIDTH,
			(int) IMG_SIZE.getHeight());
	/** The kernel used for morphological opening and closing. */
	private static final Mat MORPH_KERNEL = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
	/** The camera image in bird's eye view. */
	private Mat imgWarp = new Mat();
	/** The camera image in HSV (hue, saturation, value). */
	private Mat imgHsv = new Mat();
	/** The thresholded image (white if the pixel is within the color range, black otherwise). */
	private Mat imgRange = new Mat();
	/** The image after morphological opening and closing. */
	private Mat imgMorph = new Mat();
	/** The image labeled with connected components. */
	private Mat imgLabels = new Mat();
	/** The minimum color of the color range to detect. */
	private Scalar minColor = new Scalar(0);
	/** The maximum color of the color range to detect. */
	private Scalar maxColor = new Scalar(0);

	// output: target
	/** The position of the target. */
	private Point2D.Double targetImgPos;

	// rate limiting
	/** The lock for accessing the processing flag. */
	private final Object processingLock = new Object();
	/** True iff this is busy processing an image, causing incoming frames to be dropped. */
	private volatile boolean processing = false;

	/**
	 * Constructs a new instance of this class.
	 * @param cam camera
	 */
	public JsVision(JsCamera cam) {
		cam.addListener(e -> {
			synchronized (processingLock) {
				if (processing) { // Already busy processing, so drop the frame.
					notifyImageIgnored(new ImageIgnoredEvent(System.currentTimeMillis(), e));
					return;
				}
				processing = true;
			}
			new Thread(() -> {
				try {
					processImage(e);
				} finally {
					synchronized (processingLock) {
						processing = false;
					}
				}
			}, "Image Processor").start(); // Process without blocking.
		});
	}

	/**
	 * Processes an image from the video stream.
	 * @param imageReceivedEvent the event that caused the image processing to activate
	 */
	private void processImage(ImageReceivedEvent imageReceivedEvent) {
		final byte[] imgData = imageReceivedEvent.getImageData();
		img = Imgcodecs.imdecode(new MatOfByte(imgData), Imgcodecs.IMREAD_COLOR);
		targetImgPos = findTargetImagePos();
		notifyImageProcessed(new ImageProcessedEvent(
				System.currentTimeMillis(),
				imageReceivedEvent,
				imgMorph,
				targetImgPos));
	}

	/**
	 * Finds the pixel coordinates of the target in the image.
	 * @return target image position; null if not found
	 */
	private Point2D.Double findTargetImagePos() {
		// Warp the perspective to a bird's eye view for easier processing.
		Imgproc.warpPerspective(img, imgWarp, INV_HOMOGRAPHY, img.size(), Imgproc.CV_WARP_INVERSE_MAP);

		// Threshold in HSV.
		Imgproc.cvtColor(imgWarp, imgHsv, Imgproc.COLOR_BGR2HSV);
		Core.inRange(imgHsv, minColor, maxColor, imgRange);

		// Do morphological opening.
		Imgproc.erode(imgRange, imgMorph, MORPH_KERNEL);
		Imgproc.dilate(imgMorph, imgMorph, MORPH_KERNEL);
		
		// Do morphological closing.
		Imgproc.dilate(imgMorph, imgMorph, MORPH_KERNEL);
		Imgproc.erode(imgMorph, imgMorph, MORPH_KERNEL);

		// Crop.
		final Mat imgCrop = imgMorph.submat(IMG_CROP);

		// Find the connected components.
		final Mat stats = new Mat();
		final Mat centroids = new Mat();
		final int labelCount = Imgproc.connectedComponentsWithStats(imgCrop, imgLabels, stats, centroids);

		// Return the most likely component.
		Point2D.Double target = null;
		double targetScore = 0;
		for (int label = 1; label < labelCount; label++) {
			final double area = stats.get(label, Imgproc.CC_STAT_AREA)[0];
			if (area < 100) {
				continue;
			}
			final Point2D.Double centroid = new Point2D.Double(
					centroids.get(label, 0)[0] + IMG_CROP.x,
					centroids.get(label, 1)[0] + IMG_CROP.y);
			final double distance = DRONE_ORIGIN.distance(centroid);
			final double score = area / distance;
			if (target == null || score > targetScore) {
				target = centroid;
				targetScore = score;
			}
		}
		return target;
	}

	/**
	 * Returns the minimum color of the color range to detect.
	 * @return the minimum color
	 */
	public Color getMinColor() {
		return Mats.scalarHsvToColorRgb(minColor);
	}

	/**
	 * Sets the minimum color of the color range to detect.
	 * @param minColor the minimum color
	 */
	public void setMinColor(Color minColor) {
		this.minColor = Mats.colorRgbToScalarHsv(minColor);
	}

	/**
	 * Returns the maximum color of the color range to detect.
	 * @param the maximum color
	 */
	public Color getMaxColor() {
		return Mats.scalarHsvToColorRgb(maxColor);
	}

	/**
	 * Sets the maximum color of the color range to detect.
	 * @param maxColor the maximum color
	 */
	public void setMaxColor(Color maxColor) {
		this.maxColor = Mats.colorRgbToScalarHsv(maxColor);
	}

	/**
	 * Adds the given listener.
	 * @param l listener to add
	 */
	public void addListener(Listener l) {
		synchronized (listeners) {
			listeners.add(l);
		}
	}

	/**
	 * Removes the given listener.
	 * @param l listener to remove
	 * @return true iff removed
	 */
	public boolean removeListener(Listener l) {
		synchronized (listeners) {
			return listeners.remove(l);
		}
	}

	/**
	 * Notifies each listener that an image was ignored.
	 * @param e event
	 */
	protected void notifyImageIgnored(ImageIgnoredEvent e) {
		synchronized (listeners) {
			for (final Listener l : listeners) {
				l.onImageIgnored(e);
			}
		}
	}

	/**
	 * Notifies each listener of the results of image processing.
	 * @param e event
	 */
	protected void notifyImageProcessed(ImageProcessedEvent e) {
		synchronized (listeners) {
			for (final Listener l : listeners) {
				l.onImageProcessed(e);
			}
		}
	}
}
