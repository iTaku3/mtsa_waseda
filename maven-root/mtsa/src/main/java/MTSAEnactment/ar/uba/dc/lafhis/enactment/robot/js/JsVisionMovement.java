package MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.js;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.js.JsVision.ImageProcessedEvent;

/**
 * {@link JsMovement Moves} the drone to the target {@link JsVision located using the camera}.
 * 
 * @author Timo G&uuml;nther
 */
public class JsVisionMovement {

	/**
	 * Listens to {@link JsVisionMovement}.
	 * 
	 * @author Timo G&uuml;nther
	 */
	public interface Listener {

		/**
		 * Called when the drone has arrived at the target.
		 * @param e event
		 */
		public void onArrivedAtTarget(ArrivedAtTargetEvent e);

		/**
		 * Called when the drone has turned towards the target.
		 * @param e event
		 */
		public void onFacingTarget(FacingTargetEvent e);

		/**
		 * Called when the target could not be found for a while.
		 * @param e event
		 */
		public void onTargetLost(TargetLostEvent e);
	}

	/**
	 * An event fired by {@link JsVisionMovement}.
	 * 
	 * @author Timo G&uuml;nther
	 */
	public abstract class Event extends JsEvent<JsVisionMovement> {

		/**
		 * Constructs a new instance of this class.
		 * @param time the time in milliseconds when the event was fired
		 */
		public Event(long time) {
			super(JsVisionMovement.this, time);
		}
	}

	/**
	 * An event indicating arrival at a target.
	 * 
	 * @author Timo G&uuml;nther
	 */
	public class ArrivedAtTargetEvent extends Event {

		/**
		 * Constructs a new instance of this class.
		 * @param time the time in milliseconds of the arrival
		 */
		public ArrivedAtTargetEvent(long time) {
			super(time);
		}
	}

	/**
	 * An event indicating that the drone is facing the target.
	 * 
	 * @author Timo G&uuml;nther
	 */
	public class FacingTargetEvent extends Event {

		/**
		 * Constructs a new instance of this class.
		 * @param time the time in milliseconds when the drone faced the target
		 */
		public FacingTargetEvent(long time) {
			super(time);
		}
	}

	/**
	 * An event indicating that the target could not be found for a while.
	 * 
	 * @author Timo G&uuml;nther
	 */
	public class TargetLostEvent extends Event {

		/**
		 * Constructs a new instance of this class.
		 * @param time the time in milliseconds when the target was lost
		 */
		public TargetLostEvent(long time) {
			super(time);
		}
	}

	/** The speed at which the drone is to travel. */
	private static final double SPEED = 0.25;
	/** The yaw that needs to be achieved (in either direction) to be considered on target. */
	private static final double TARGET_YAW = Math.PI / 128;
	/** The distance in meters that needs to be achieved to be considered close to the target. */
	private static final double TARGET_DISTANCE = 0.2;
	/** The number of consecutive processed frames without target found until the target is considered lost. */
	private static final int TARGET_LOST = 15;

	/** The listeners to notify. */
	private final List<Listener> listeners = new LinkedList<>();

	/** Lower-level drone movement. */
	private final JsMovement move;
	
	/** The lock for {@link #faceTarget}. */
	private final Object faceTargetLock = new Object();
	/** True to use the camera to fine adjust the target. */
	private volatile boolean faceTarget;
	/** The lock for {@link #moveToTarget}. */
	private final Object moveToTargetLock = new Object();
	/** True to move towards the target. */
	private volatile boolean moveToTarget;
	/** Whether the drone is close to the target. */
	private volatile boolean closeToTarget;
	/** Whether the drone has started moving towards the target yet. */
	private volatile boolean startedMovingToTarget;

	/** The number of consecutive processed frames that need to have similar angles to the target in order to commit to turning. */
	private static final int TARGET_YAW_AGREEMENT_COUNT = 3;
	/** The maximum distance between two angles to be considered similar. */
	private static final double TARGET_YAW_AGREEMENT_MARGIN = Math.PI / 32;
	/** The lock for {@link #onTargetFound(ImageProcessedEvent)} and {@link #onTargetNotFound(ImageProcessedEvent)}. */
	private final Object targetLock = new Object();
	/** The yaws of the last few consecutive targets found. */
	private final List<Double> targetYaws = new ArrayList<>(TARGET_YAW_AGREEMENT_COUNT);
	/** The number of consecutive processed frames with a target if positive or no target if negative. */
	private volatile int targetFrames;

	/**
	 * Constructs a new instance of this class
	 * @param vis drone vision
	 * @param move drone movement
	 */
	public JsVisionMovement(JsVision vis, JsMovement move) {
		this.move = move;
		vis.addListener(e -> {
			synchronized (targetLock) {
				if (e.isTargetFound()) {
					onTargetFound(e);
				} else {
					onTargetNotFound(e);
				}
			}
		});
		move.addListener(e -> {
			synchronized (targetLock) {
				synchronized (moveToTargetLock) {
					if (!moveToTarget) {
						return;
					}
					setMoveToTarget(false);
					notifyArrivedAtTarget(new ArrivedAtTargetEvent(System.currentTimeMillis()));
				}
			}
		});
	}

	/**
	 * Called when a target was found.
	 * @param e event
	 */
	private void onTargetFound(ImageProcessedEvent e) {
		targetFrames = Math.max(0, targetFrames) + 1;
		synchronized (faceTargetLock) {
			if (faceTarget) {
				final double targetYaw = e.getTargetYaw();
				if (isTargetYawAgreement(targetYaw)) {
					if (Math.abs(targetYaw) <= TARGET_YAW) { // on target
						move.setTurn(0);
						setFaceTarget(false);
						notifyFacingTarget(new FacingTargetEvent(System.currentTimeMillis()));
					} else { // not on target
						move.setTurn(targetYaw); // turn towards target
					}
				} else {
					move.setTurn(0);
				}
			}
		}
		synchronized (moveToTargetLock) {
			if (moveToTarget) {
				final double ds = e.getTargetDistance();
				closeToTarget |= ds < TARGET_DISTANCE; // if close, stop considering camera input to avoid skipping over the target
				if (!closeToTarget || !startedMovingToTarget) {
					final long t = e.getImageReceivedEvent().getTime();
					final double s = move.getDistance(t) + ds;
					move.setDestination(s, SPEED, 0); // move to target
					startedMovingToTarget = true;
				}
			}
		}
	}

	/**
	 * Called when a target was not found.
	 * @param e event
	 */
	private void onTargetNotFound(ImageProcessedEvent e) {
		synchronized (faceTargetLock) {
			if (faceTarget) {
				move.setTurn(0);
				targetFrames = Math.min(0, targetFrames) - 1;
				if (Math.abs(targetFrames) < TARGET_LOST) {
					return;
				}
				setFaceTarget(false);
				notifyTargetLost(new TargetLostEvent(System.currentTimeMillis()));
			}
		}
	}

	/**
	 * Returns true iff there is an agreement regarding the yaw of the last few consecutive targets.
	 * This is the case iff all of them are similar.
	 * If false, the yaw cannot be relied upon due to noise or being in the middle of turning.
	 * @param targetYaw yaw to the target
	 * @return true iff there is an agreement
	 */
	private boolean isTargetYawAgreement(double targetYaw) {
		boolean agree = true;
		for (final double oldYaw : targetYaws) {
			if (Math.abs(targetYaw - oldYaw) >= TARGET_YAW_AGREEMENT_MARGIN) {
				agree = false;
				break;
			}
		}
		if (!agree) {
			targetYaws.clear();
			return false;
		}
		targetYaws.add(targetYaw);
		if (targetYaws.size() < TARGET_YAW_AGREEMENT_COUNT) {
			return false;
		}
		targetYaws.remove(0);
		return true;
	}

	/**
	 * Moves the drone to the target straight ahead of the drone.
	 * @param moveToTarget true to move
	 */
	public void setMoveToTarget(boolean moveToTarget) {
		synchronized (moveToTargetLock) {
			this.moveToTarget = moveToTarget;
			this.closeToTarget = false;
			this.startedMovingToTarget = false;
			this.targetFrames = 0;
		}
	}

	/**
	 * Turns the drone towards the target.
	 * @param faceTarget true to turn
	 */
	public void setFaceTarget(boolean faceTarget) {
		synchronized (faceTargetLock) {
			this.faceTarget = faceTarget;
			this.targetFrames = 0;
			this.targetYaws.clear();
		}
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
	 * Notifies each listener of arrival at the target.
	 * @param e event
	 */
	protected void notifyArrivedAtTarget(ArrivedAtTargetEvent e) {
		synchronized (listeners) {
			for (final Listener l : listeners) {
				l.onArrivedAtTarget(e);
			}
		}
	}

	/**
	 * Notifies each listener that the drone is facing the target.
	 * @param e event
	 */
	protected void notifyFacingTarget(FacingTargetEvent e) {
		synchronized (listeners) {
			for (final Listener l : listeners) {
				l.onFacingTarget(e);
			}
		}
	}

	/**
	 * Notifies each listener that the target could not be found for a while.
	 * @param e event
	 */
	protected void notifyTargetLost(TargetLostEvent e) {
		synchronized (listeners) {
			for (final Listener l : listeners) {
				l.onTargetLost(e);
			}
		}
	}
}
