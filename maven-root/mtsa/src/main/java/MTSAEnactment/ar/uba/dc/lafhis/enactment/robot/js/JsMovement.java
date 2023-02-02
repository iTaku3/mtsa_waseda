package MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.js;

import java.util.LinkedList;
import java.util.List;

import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.js.util.Angles;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.js.util.Maths;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.js.util.TimeUtils;
import de.devoxx4kids.dronecontroller.command.movement.Pcmd;
import de.devoxx4kids.dronecontroller.network.DroneConnection;

/**
 * Handles the physical movement of the drone.
 * 
 * @author Timo G&uuml;nther
 */
public class JsMovement extends JsSpeedometer.SynchronizedDecorator implements Runnable {

	/**
	 * Listens to {@link JsMovement}.
	 * 
	 * @author Timo G&uuml;nther
	 */
	public interface Listener {

		/**
		 * Called when a movement was finished.
		 * @param e event
		 */
		public void onMovementDone(MovementDoneEvent e);
	}

	/**
	 * An event fired by {@link JsMovement}.
	 * 
	 * @author Timo G&uuml;nther
	 */
	public abstract class Event extends JsEvent<JsMovement> {

		/**
		 * Constructs a new instance of this class.
		 * @param time the time in milliseconds when the event was fired
		 */
		public Event(long time) {
			super(JsMovement.this, time);
		}
	}

	/**
	 * An event indicating that a movement was finished.
	 * 
	 * @author Timo G&uuml;nther
	 */
	public class MovementDoneEvent extends Event {

		/**
		 * Constructs a new instance of this class.
		 * @param time the time in milliseconds when the movement was finished
		 * @param distance the distance reached in meters
		 */
		public MovementDoneEvent(long time) {
			super(time);
		}
	}

	/**
	 * <p>
	 * Models a change in the movement function of the drone.
	 * Each node allows interpolating the movement properties over time up until the newer node.
	 * </p>
	 * 
	 * <p>
	 * Assumes that the acceleration per node is constant.
	 * As a result, the velocity is linear.
	 * In turn, the distance over time can be deduced.
	 * </p>
	 * @author Timo G&uuml;nther
	 */
	private static class SpeedNode implements JsSpeedometer {
		/** The starting time of the node in milliseconds. */
		private final long t;
		/** The speed at which the drone is to travel during this segment in [-1, 1]. */
		private final double speed;
		/** The acceleration valid throughout the entirety of this segment in meters per second squared. */
		private final double a;
		/** The velocity at the starting time of the node in meters per second. */
		private final double v;
		/** The distance at the starting time of the node in meters. */
		private final double d;

		/** The previous node in time. */
		private final SpeedNode older;

		/**
		 * Creates a new segment of no movement during every point in time.
		 */
		public SpeedNode() {
			this(Long.MIN_VALUE, 0, 0, 0, 0, null);
		}

		/**
		 * Creates a new change of speed at the given point in time based on the previous movement.
		 * @param t time
		 * @param speed speed
		 * @param a acceleration
		 * @param older the previous node in time
		 */
		public SpeedNode(
				long t,
				double speed,
				double a,
				SpeedNode older) {
			this(t, speed, a, older.getVelocity(t), older.getDistance(t), older);
		}

		/**
		 * Constructs a new instance of this class.
		 * @param t time
		 * @param speed speed
		 * @param a acceleration
		 * @param v velocity
		 * @param d distance
		 * @param older the previous node in time
		 */
		private SpeedNode(
				long t,
				double speed,
				double a,
				double v,
				double d,
				SpeedNode older) {
			this.t = t;
			this.speed = speed;
			this.a = a;
			this.v = v;
			this.d = d;
			this.older = older;
		}

		@Override
		public double getSpeed(long t) {
			return speed;
		}

		@Override
		public double getAcceleration(long t) {
			return a;
		}

		@Override
		public double getVelocity(long t) {
			final double dt = TimeUtils.msToS(t - this.t);
			return v + a * dt;
		}

		@Override
		public double getDistance(long t) {
			final double dt = TimeUtils.msToS(t - this.t);
			return d + v * dt + 0.5 * a * dt * dt;
		}

		@Override
		public long getTimeVelocityReached(double v) {
			return t + TimeUtils.sToMs((v - this.v) / a);
		}

		@Override
		public String toString() {
			return String.format("(%d ms, %f, %f m/s\u00B2, %f m/s, %f m)", t, speed, a, v, d);
		}
	}

	/** The time between commands being sent in milliseconds. */
	private static final long COMMAND_INTERVAL = 10;
	/** Maximum velocity of the drone in meters per second. */
	private static final double MAX_VELOCITY = 1.7; // measured
	/** Maximum acceleration of the drone in meters per second squared. */
	private static final double MAX_ACCELERATION = 1.5; // measured
	/** Maximum deceleration of the drone in meters per second squared. */
	private static final double MAX_DECELERATION = 4; // estimated
	/** Minimum yaw to turn by to prevent the drone from ignoring tiny angles. */
	private static final double MIN_YAW = Math.PI / 32;

	/** The listeners to notify. */
	private final List<Listener> listeners = new LinkedList<>();

	/** The connection to the drone. */
	private final DroneConnection conn;

	/** The lock for movement. */
	private final Object moveLock = new Object();
	/** The yaw to turn by in radians. */
	private volatile double turn;
	/** The speed node with the highest time. */
	private SpeedNode newest = new SpeedNode();
	/** The distance at which the command to move to a destination was issued in meters. */
	private double dStart = Double.NaN;
	/** The destination to move to in meters. */
	private double dEnd = Double.NaN;
	/** The speed at the start of travelling to the destination in [0, 1]. */
	private double speedStart = Double.NaN;
	/** The speed at the end of travelling to the destination in [0, 1]. */
	private double speedEnd = Double.NaN;

	/**
	 * Constructs a new instance of this class.
	 * @param conn connection to the drone
	 */
	public JsMovement(DroneConnection conn) {
		this.conn = conn;
	}

	/**
	 * Sends movement commands to the drone in regular intervals.
	 * This is necessary as otherwise the drone interprets the lack of movement commands as a drop in bandwidth,
	 * causing the frame rate of the {@link JsCamera video stream} to drop.
	 */
	@Override
	public void run() {
		try {
			long next = System.currentTimeMillis();
			while (!Thread.interrupted()) {
				synchronized (moveLock) {
					final long t = System.currentTimeMillis();

					// Approach the destination.
					boolean destinationReached = false;
					if (!Double.isNaN(dEnd)) {
						destinationReached = setSpeedFromDestination(t);
					}

					// Send a command.
					final double speed = getSpeed(t);
					conn.sendCommand(Pcmd.pcmd(
							(byte) Math.round(speed * (speed < 0 ? Byte.MIN_VALUE : Byte.MAX_VALUE)),
							Angles.radiansToDegreesInt(turn),
							0));

					// Notify the listeners.
					if (destinationReached) {
						resetDestination();
						notifyMovementDone(new MovementDoneEvent(t));
					}
				}
				next += COMMAND_INTERVAL;
				Thread.sleep(Math.max(0, next - System.currentTimeMillis()));
			}
		} catch (InterruptedException e) {}
	}

	/**
	 * Sets the longitudinal speed of the drone.
	 * <ul>
	 * <li>A value of 0 causes the drone to stop moving longitudinally.</li>
	 * <li>A value greater than 0 causes the drone to move forward, with maximum speed at 1.</li>
	 * <li>A value smaller than 0 causes the drone to move backward, with maximum speed at -1.</li>
	 * </ul>
	 * @param speed longitudinal speed; automatically clamped to be in [-1, 1]
	 */
	public void setSpeed(double speed) {
		synchronized (moveLock) {
			setSpeed(System.currentTimeMillis(), speed);
		}
	}

	/**
	 * Sets the longitudinal speed of the drone starting at the given time.
	 * @param t time at which to change the speed
	 * @param speed longitudinal speed; automatically clamped to be in [-1, 1]
	 */
	public void setSpeed(long t, double speed) {
		synchronized (moveLock) {
			speed = Maths.clamp(speed, -1, 1);

			SpeedNode old = getDecorated(t);

			if (speed == old.speed) { // no change
				return;
			}

			double a;
			if (speed > 0 && old.speed < 0 || speed < 0 && old.speed > 0) { // reverse direction
				a = speed > 0 ? MAX_DECELERATION : -MAX_DECELERATION;
				old = new SpeedNode(t, 0, a, old); // brake before accelerating in opposite of current direction
				t = old.getTimeVelocityReached(0);
			}

			a = Math.abs(speed) > Math.abs(old.speed) ? MAX_ACCELERATION : -MAX_DECELERATION;
			if (speed < 0) {
				a = -a;
			}
			old = new SpeedNode(t, speed, a, old); // start accelerating or decelerating towards new speed

			t = old.getTimeVelocityReached(getVelocityFromSpeed(speed));
			old = new SpeedNode(t, speed, 0, old); // hold new speed

			newest = old;
		}
	}

	/**
	 * Yaws the drone clockwise by the given angle.
	 * @param turn angle in radians to turn
	 */
	public void setTurn(double turn) {
		synchronized (moveLock) {
			if (Math.abs(turn) < MIN_YAW) {
				turn = MIN_YAW * Math.signum(turn);
			}
			this.turn = turn;
		}
	}

	/**
	 * Returns the yaw to turn by in radians.
	 * @return the yaw to turn by in radians
	 */
	public double getTurn() {
		synchronized (moveLock) {
			return turn;
		}
	}

	/**
	 * Moves the drone to the given destination.
	 * @param dEnd the absolute distance of the destination in meters
	 * @param speedStart the speed at which to travel at the start in [0, 1]
	 * @param speedEnd the speed at which to travel at the end (close to the destination) in [0, 1]
	 */
	public void setDestination(double dEnd, double speedStart, double speedEnd) {
		synchronized (moveLock) {
			this.dStart = getDistance();
			this.dEnd = dEnd;
			speedStart = Math.abs(Maths.clamp(speedStart, -1, 1));
			speedEnd = Math.abs(Maths.clamp(speedEnd, -1, 1));
			if (dEnd < dStart) { // move backwards
				speedStart = -speedStart;
				speedEnd = -speedEnd;
			}
			this.speedStart = speedStart;
			this.speedEnd = speedEnd;
		}
	}

	/**
	 * Resets the destination.
	 */
	private void resetDestination() {
		dStart = Double.NaN;
		dEnd = Double.NaN;
		speedStart = Double.NaN;
		speedEnd = Double.NaN;
	}

	/**
	 * Sets the speed based on the remaining distance to the destination.
	 * @param t absolute time in milliseconds
	 * @return true iff the destination was reached
	 */
	private boolean setSpeedFromDestination(long t) {
		final double progress = Maths.normalizeLinear(getDistance(t), dStart, dEnd);
		final boolean destinationReached = progress > 0.95;
		final double speed = destinationReached ? speedEnd : Maths.scaleLinear(progress, speedStart, speedEnd);
		setSpeed(t, speed);
		return destinationReached;
	}

	/**
	 * Returns the physical velocity that would be reached after setting the drone to the given speed for long enough.
	 * @param speed speed of the drone; in [-1, 1]
	 * @return physical velocity in meters per second
	 */
	public static double getVelocityFromSpeed(double speed) {
		return speed * MAX_VELOCITY;
	}

	/**
	 * Returns the speed that the drone would need to be set to eventually reach the given physical velocity.
	 * @param velocity physical velocity in meters per second
	 * @return speed of the drone
	 */
	public static double getSpeedFromVelocity(double velocity) {
		return velocity / MAX_VELOCITY;
	}

	@Override
	protected SpeedNode getDecorated(long t) {
		synchronized (moveLock) {
			SpeedNode old = newest;
			while (old.t > t) {
				old = old.older;
			}
			return old;
		}
	}

	@Override
	protected Object getLock() {
		return moveLock;
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
	 * Notifies each listener that a distance of interest was reached.
	 * @param e event
	 */
	protected void notifyMovementDone(MovementDoneEvent e) {
		synchronized (listeners) {
			for (final Listener l : listeners) {
				l.onMovementDone(e);
			}
		}
	}
}
