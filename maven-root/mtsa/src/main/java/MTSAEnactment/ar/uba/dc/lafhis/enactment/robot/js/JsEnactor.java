package MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.js;

import java.awt.Color;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

import MTSAEnactment.ar.uba.dc.lafhis.enactment.Enactor;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.TransitionEvent;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.js.JsVisionMovement.ArrivedAtTargetEvent;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.js.JsVisionMovement.FacingTargetEvent;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.js.JsVisionMovement.TargetLostEvent;
import de.devoxx4kids.dronecontroller.network.ConnectionException;
import de.devoxx4kids.dronecontroller.network.DroneConnection;
import de.devoxx4kids.dronecontroller.network.WirelessLanDroneConnection;

/**
 * <p>
 * {@link Enactor} for a Parrot Jumping Sumo drone.
 * The drone {@link JsMovement moves} along a 2D grid.
 * {@link JsVision Processes images} from the {@link JsCamera camera} to detect target positions.
 * Then {@link JsVisionMovement moves to the target}.
 * </p>
 * 
 * <p>
 * The enactor may be configured using a properties file called <code>JumpingSumo.properties</code>.
 * It supports the following entries:
 * </p>
 * 
 * <table>
 * <tr>
 * <th>Key</th>
 * <th>Default Value</th>
 * <th>Description</th>
 * </tr>
 * <tr>
 * <td><code>conn.ip</code></td>
 * <td><code>192.168.2.1</code></td>
 * <td>IP address of the drone.</td>
 * </tr>
 * <tr>
 * <td><code>conn.port</code></td>
 * <td><code>44444</code></td>
 * <td>Port through which to communicate with the drone.</td>
 * </tr>
 * <tr>
 * <td><code>conn.name</code></td>
 * <td><code>JumpingSumo</code></td>
 * <td>Name of the drone's WLAN network.</td>
 * </tr>
 * <tr>
 * <td><code>cam.outDir</code></td>
 * <td></td>
 * <td>Directory to store frames from the drone's video stream in. If not set, the images are not written to files at all.</td>
 * </tr>
 * <tr>
 * <td><code>img.minColor</code></td>
 * <td><code>#efdae8</code></td>
 * <td>Lower bound of the color range to detect using the camera.</td>
 * </tr>
 * <tr>
 * <td><code>img.maxColor</code></td>
 * <td><code>#ff000c</code></td>
 * <td>Upper bound of the color range to detect using the camera.</td>
 * </tr>
 * </table>
 * 
 * @param <S> state
 * @param <A> action
 * @author Timo G&uuml;nther
 */
public class JsEnactor<S, A> extends Enactor<S, A> {

	/**
	 * A direction in which the drone can move.
	 * The direction in which the drone is facing at the start defines {@link #NORTH North}.
	 * After that, the compass stays the same independently of the drone.
	 * 
	 * @author Timo G&uuml;nther
	 */
	private enum Direction {
		NORTH, EAST, SOUTH, WEST;

		/**
		 * <p>
		 * Returns the heading from this direction to another.
		 * That is how many directions one would have to turn clockwise to get from the former to the latter.
		 * For example, the heading from {@link #SOUTH East} to {@link #SOUTH South} is 1.
		 * </p>
		 * 
		 * <p>
		 * The returned value is normalized.
		 * For example, the heading from {@link #NORTH North} to {@link #WEST West} is not 3 but -1.
		 * That prevents unnecessary turning.
		 * </p>
		 * @param towards direction to compare to
		 * @return heading in steps between directions; in [-2, 2)
		 */
		public int getHeading(Direction towards) {
			int heading = this.ordinal() - towards.ordinal();
			final int full = values().length;
			final int half = full / 2;
			if (heading >= half) {
				heading -= full;
			} else if (heading < -half) {
				heading += full;
			}
			return heading;
		}
	}

	// controllable actions
	private final A goE, goW, goN, goS;
	// uncontrollable actions
	private final A arrive, obstacle;

	// 2D grid logic
	/** The direction the drone is currently facing.*/
	private Direction facing = Direction.NORTH;

	// drone connection
	/** The IP for connecting with the drone. */
	private String ip;
	/** The port for connecting with the drone. */
	private int port;
	/** The network name for connecting with the drone. */
	private String name;
	/** The connection to the drone. */
	private DroneConnection conn;

	// drone movement
	/** The lock for drone movement. */
	private final Object moveLock = new Object();
	/** True iff moving towards a target is done. */
	private volatile boolean moveDone;
	/** The lock for drone turning. */
	private final Object turnLock = new Object();
	/** True iff turning towards a target is done. */
	private volatile boolean turnDone;
	/** True iff the target could not be found. */
	private volatile boolean targetLost;
	/** Controls the drone's movements. */
	private JsMovement move;
	/** The thread for {@link #move}. */
	private Thread moveThread;

	// drone camera
	/** Deals with the drone's video stream. */
	private JsCamera cam;
	/** The thread for {@link #cam}. */
	private Thread camThread;
	/** Processes the images from the drone's video stream. */
	private JsVision vis;
	/** For moving towards the target. */
	private JsVisionMovement visMove;

	// GUI
	/** The user interface. */
	private JsEnactorGui gui;

	/**
	 * Constructor hooked into <code>ltsa-context.xml</code>.
	 */
	public JsEnactor(String name,
			A goE, A goW, A goN, A goS,
			A arrive, A obstacle) {
		super(name);
		this.goE = goE;
		this.goW = goW;
		this.goN = goN;
		this.goS = goS;
		this.arrive = arrive;
		this.obstacle = obstacle;
	}

	@Override
	protected void primitiveHandleTransitionEvent(TransitionEvent<A> transitionEvent) throws Exception {
		final A a = transitionEvent.getAction();
		if (goE.equals(a)) {
			move(Direction.EAST);
		} else if (goW.equals(a)) {
			move(Direction.WEST);
		} else if (goN.equals(a)) {
			move(Direction.NORTH);
		} else if (goS.equals(a)) {
			move(Direction.SOUTH);
		}
	}

	@Override
	public void setUp() {
		// Read the properties.
		final Properties p = new Properties();
		try (final Reader in = new FileReader("JumpingSumo.properties")) {
			p.load(in);
		} catch (IOException e) {}
		ip = p.getProperty("conn.ip", "192.168.2.1");
		port = Integer.parseInt(p.getProperty("conn.port", "44444"));
		name = p.getProperty("conn.name", "JumpingSumo");
		final String outPath = p.getProperty("cam.outDir");
		final File outDir = outPath == null ? null : new File(outPath);
		final Color minColor = Color.decode(p.getProperty("img.minColor", "#efdae8"));
		final Color maxColor = Color.decode(p.getProperty("img.maxColor", "#ff000c"));

		//Connect to the drone;
		conn = new WirelessLanDroneConnection(ip, port, name);
		try {
			conn.connect();
		} catch (ConnectionException e) {
			throw new RuntimeException(e);
		}

		// Handle movement.
		move = new JsMovement(conn);
		moveThread = new Thread(move, String.format("%s Movement", name));
		moveThread.start();

		// Handle the camera.
		cam = new JsCamera(conn);
		camThread = new Thread(cam, String.format("%s Camera", name));
		camThread.start();
		if (outDir != null) {
			cam.addListener(new JsCameraRecorder(outDir));
		}

		// Process the video feed.
		vis = new JsVision(cam);
		vis.setMinColor(minColor);
		vis.setMaxColor(maxColor);

		// Move according to the processed video feed.
		visMove = new JsVisionMovement(vis, move);
		visMove.addListener(new JsVisionMovement.Listener() {
			
			@Override
			public void onFacingTarget(FacingTargetEvent e) {
				synchronized (turnLock) {
					turnDone = true;
					targetLost = false;
					turnLock.notifyAll();
				}
			}
			
			@Override
			public void onArrivedAtTarget(ArrivedAtTargetEvent e) {
				synchronized (moveLock) {
					moveDone = true;
					moveLock.notifyAll();
				}
			}

			@Override
			public void onTargetLost(TargetLostEvent e) {
				synchronized (turnLock) {
					turnDone = true;
					targetLost = true;
					turnLock.notifyAll();
				}
			}
		});

		// Create the GUI.
		gui = new JsEnactorGui(cam, vis);
		gui.setTitle(name);
		gui.setVisible(true);
	}

	@Override
	public void tearDown() {
		if (camThread != null) {
			camThread.interrupt();
		}
		if (moveThread != null) {
			moveThread.interrupt();
		}
		if (conn != null) {
			try {
				Thread.sleep(500); // Wait for commands to be executed before disconnecting.
			} catch (InterruptedException e) {}
			conn.disconnect();
			conn = null;
		}
		if (gui != null) {
			gui.setVisible(false);
			gui.dispose();
		}
	}

	/**
	 * Called when a move action occurs.
	 * @param direction direction of the movement
	 */
	private void move(Direction direction) {
		face(direction);
		if (targetLost) {
			try {
				fireTransitionEvent(obstacle);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			return;
		}
		synchronized (moveLock) {
			visMove.setMoveToTarget(true);
			moveDone = false;
			try {
				while (!moveDone) {
					moveLock.wait();
				}
			} catch (InterruptedException e) {}
		}
		try {
			fireTransitionEvent(arrive);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Ensures that the drone is facing the given direction.
	 * @param direction direction to face
	 */
	private void face(Direction direction) {
		// Roughly face the given direction.
		int turn = direction.getHeading(facing);
		while (turn != 0) {
			turn((turn < 0 ? -1 : 1) * 2 * Math.PI / Direction.values().length);
			if (turn < 0) {
				turn++;
			} else if (turn > 0) {
				turn--;
			}
			if (turn == 0) {
				break;
			}
			try {
				Thread.sleep(42);
			} catch (InterruptedException e) {}
		}
		facing = direction;

		// Make fine adjustments using the camera.
		synchronized (turnLock) {
			visMove.setFaceTarget(true);
			turnDone = false;
			try {
				while (!turnDone) {
					turnLock.wait();
				}
			} catch (InterruptedException e) {}
		}
	}

	/**
	 * Yaws the drone clockwise by the given angle.
	 * @param angle angle in radians to turn
	 */
	private void turn(double angle) {
		final long duration = (long) (Math.abs(angle) * 314); // estimate of the time it takes to turn
		move.setTurn(angle);
		try {
			Thread.sleep(duration);
		} catch (InterruptedException e) {}
		move.setTurn(0);
	}
}
