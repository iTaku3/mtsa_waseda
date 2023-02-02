package MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.js;

import java.awt.Dimension;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;
import java.util.List;

import de.devoxx4kids.dronecontroller.command.PacketType;
import de.devoxx4kids.dronecontroller.command.multimedia.VideoStreaming;
import de.devoxx4kids.dronecontroller.listener.EventListener;
import de.devoxx4kids.dronecontroller.network.DroneConnection;
import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;

/**
 * The camera of the drone.
 * Provides images in regular intervals.
 * 
 * @author Timo G&uuml;nther
 */
public class JsCamera implements Runnable {

	/**
	 * Listens to the {@link JsCamera camera}.
	 * 
	 * @author Timo G&uuml;nther
	 */
	public interface Listener {

		/**
		 * Called when an image should have been received by now but that did not happen.
		 * @param e event
		 */
		public default void onImageMissed(ImageMissedEvent e) {}

		/**
		 * Called when an image was received.
		 * @param e event
		 */
		public void onImageReceived(ImageReceivedEvent e);
	}

	/**
	 * An event fired by {@link JsCamera}.
	 * 
	 * @author Timo G&uuml;nther
	 */
	public abstract class Event extends JsEvent<JsCamera> {

		/**
		 * Constructs a new instance of this class.
		 * @param time the time in milliseconds when the event was fired
		 */
		public Event(long time) {
			super(JsCamera.this, time);
		}
	}

	/**
	 * An event indicating that an image was not received in time.
	 * 
	 * @author Timo G&uuml;nther
	 */
	public class ImageMissedEvent extends Event {

		/**
		 * Constructs a new instance of this class.
		 * @param time the time in milliseconds when the image should have been received but was not
		 */
		public ImageMissedEvent(long time) {
			super(time);
		}
	}

	/**
	 * An event indicating that an image was received.
	 * 
	 * @author Timo G&uuml;nther
	 */
	public class ImageReceivedEvent extends Event {

		/** The image data (JPG). */
		private final byte[] imgData;

		/**
		 * Constructs a new instance of this class.
		 * @param time the time in milliseconds when the image was received
		 * @param imgData the image data (JPG)
		 */
		public ImageReceivedEvent(long time, byte[] imgData) {
			super(time);
			this.imgData = imgData;
		}

		/**
		 * Returns the image data (JPG).
		 * @return the image data (JPG)
		 */
		public byte[] getImageData() {
			return imgData;
		}
	}

	/** The listeners to notify. */
	private final List<Listener> listeners = new LinkedList<>();

	/** Size of the received images. */
	public static final Dimension IMG_SIZE = new Dimension(640, 480);
	/** The expected number of frames per second the drone's video stream contains. */
	public static final double FPS = 15;
	/** The expected time in milliseconds between two consecutive frames of the drone's video stream. */
	public static final long FRAME_RATE = Math.round(TimeUnit.SECONDS.toMillis(1) / FPS);

	/** The lock for {@link #nextImage}. */
	private final Object nextImageLock = new Object();
	/** The last time an image was received. */
	private volatile long nextImage;

	/**
	 * Constructs a new instance of this class.
	 * @param conn drone connection
	 */
	public JsCamera(DroneConnection conn) {
		conn.addEventListener(new EventListener() {
			@Override
			public void consume(byte[] data) {
				final long time = System.currentTimeMillis();
				final byte[] imgData = new byte[getSize(data)];
				System.arraycopy(data, 12, imgData, 0, imgData.length);
				onImageReceived(time, imgData);
			}

			@Override
			public boolean test(byte[] data) {
				return data[0] == PacketType.DATA_LOW_LATENCY.toByte()
						&& data[1] == 125
						&& data[12] == -1
						&& data[13] == -40;
			}

			/**
			 * Returns the size of the payload of the given packet.
			 * @param data packet data
			 * @return payload byte size
			 */
			private int getSize(byte[] data) {
				return Math.min(data.length - 12, ByteBuffer.wrap(data, 3, 4).order(ByteOrder.LITTLE_ENDIAN).getInt());
			}
		});
		conn.sendCommand(VideoStreaming.enableVideoStreaming());
	}

	/**
	 * Called when an image is received.
	 * @param time the time in milliseconds when the image was received
	 * @param imgData image data
	 */
	private void onImageReceived(long time, byte[] imgData) {
		synchronized (nextImageLock) {
			if (nextImage == 0) {
				nextImage = System.currentTimeMillis();
			}
			nextImage += FRAME_RATE;
			nextImageLock.notifyAll();
		}
		notifyImageReceived(new ImageReceivedEvent(time, imgData));
	}

	@Override
	public void run() {
		try {
			// Wait for the first frame.
			synchronized (nextImageLock) {
				while (nextImage == 0) {
					nextImageLock.wait();
				}
			}

			// Wait for each next frame and notify the listeners if it does not occur.
			final long extraWait = FRAME_RATE / 2; // in case the frame is just a little late
			while (!Thread.interrupted()) {
				long miss;
				long untilMiss;
				synchronized (nextImageLock) {
					while ((untilMiss = (miss = nextImage + extraWait) - System.currentTimeMillis()) > 0) {
						nextImageLock.wait(untilMiss);
					}
					nextImage += FRAME_RATE;
				}
				notifyImageMissed(new ImageMissedEvent(miss));
			}
		} catch (InterruptedException e) {}
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
	 * Notifies each listener that an image was missed.
	 * @param e event
	 */
	protected void notifyImageMissed(ImageMissedEvent e) {
		synchronized (listeners) {
			for (final Listener l : listeners) {
				l.onImageMissed(e);
			}
		}
	}

	/**
	 * Notifies each listener that an image was received.
	 * @param event e
	 */
	protected void notifyImageReceived(ImageReceivedEvent e) {
		synchronized (listeners) {
			for (final Listener l : listeners) {
				l.onImageReceived(e);
			}
		}
	}
}
