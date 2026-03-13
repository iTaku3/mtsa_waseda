package MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.js;

/**
 * Records and schedules the movement of the drone over time.
 * 
 * @author Timo G&uuml;nther
 */
public interface JsSpeedometer {

	/**
	 * Decorates {@link JsSpeedometer}.
	 * 
	 * @author Timo G&uuml;nther
	 */
	public static abstract class Decorator implements JsSpeedometer {

		/**
		 * Returns the decorated instance.
		 * @return the decorated instance
		 */
		protected JsSpeedometer getDecorated() {
			return getDecorated(System.currentTimeMillis());
		}

		/**
		 * Returns the decorated instance responsible for the given time.
		 * @param t absolute time in milliseconds
		 * @return the decorated instance
		 */
		protected abstract JsSpeedometer getDecorated(long t);

		@Override
		public double getSpeed(long t) {
			return getDecorated(t).getSpeed(t);
		}

		@Override
		public double getAcceleration(long t) {
			return getDecorated(t).getAcceleration(t);
		}

		@Override
		public double getVelocity(long t) {
			return getDecorated(t).getVelocity(t);
		}

		@Override
		public double getDistance(long t) {
			return getDecorated(t).getDistance(t);
		}

		@Override
		public long getTimeVelocityReached(double v) {
			return getDecorated().getTimeVelocityReached(v);
		}
	}

	/**
	 * Decorates {@link JsSpeedometer} with synchronization on every method.
	 * 
	 * @author Timo G&uuml;nther
	 */
	public static abstract class SynchronizedDecorator extends Decorator {

		/**
		 * Returns the lock for synchronization.
		 * @return the lock for synchronization
		 */
		protected abstract Object getLock();

		@Override
		public double getSpeed() {
			synchronized (getLock()) {
				return super.getSpeed();
			}
		}

		@Override
		public double getSpeed(long t) {
			synchronized (getLock()) {
				return super.getSpeed(t);
			}
		}

		@Override
		public double getAcceleration() {
			synchronized (getLock()) {
				return super.getAcceleration();
			}
		}

		@Override
		public double getAcceleration(long t) {
			synchronized (getLock()) {
				return super.getAcceleration(t);
			}
		}

		@Override
		public double getVelocity() {
			synchronized (getLock()) {
				return super.getVelocity();
			}
		}

		@Override
		public double getVelocity(long t) {
			synchronized (getLock()) {
				return super.getVelocity(t);
			}
		}

		@Override
		public double getDistance() {
			synchronized (getLock()) {
				return super.getDistance();
			}
		}

		@Override
		public double getDistance(long t) {
			synchronized (getLock()) {
				return super.getDistance(t);
			}
		}

		@Override
		public long getTimeVelocityReached(double v) {
			synchronized (getLock()) {
				return super.getTimeVelocityReached(v);
			}
		}
	}

	/**
	 * Returns the current longitudinal speed of the drone.
	 * @return the longitudinal speed; in [-1, 1]
	 */
	public default double getSpeed() {
		return getSpeed(System.currentTimeMillis());
	}

	/**
	 * Returns the longitudinal speed of the drone at the given point in time.
	 * @param t absolute time in milliseconds
	 * @return the longitudinal speed; in [-1, 1]
	 */
	public double getSpeed(long t);

	/**
	 * Returns the current acceleration of the drone.
	 * @return acceleration in meters per second squared
	 */
	public default double getAcceleration() {
		return getAcceleration(System.currentTimeMillis());
	}

	/**
	 * Returns the acceleration of the drone at the given point in time.
	 * @param t absolute time in milliseconds
	 * @return acceleration in meters per second squared
	 */
	public double getAcceleration(long t);

	/**
	 * Returns the current velocity of the drone.
	 * @return velocity in meters per second
	 */
	public default double getVelocity() {
		return getVelocity(System.currentTimeMillis());
	}

	/**
	 * Returns the velocity of the drone at the given point in time.
	 * @param t absolute time in milliseconds
	 * @return velocity in meters per second
	 */
	public double getVelocity(long t);

	/**
	 * Returns the distance the drone has travelled so far.
	 * @return distance in meters
	 */
	public default double getDistance() {
		return getDistance(System.currentTimeMillis());
	}

	/**
	 * Returns the distance the drone has travelled or will have travelled at the given point in time.
	 * @param t absolute time in milliseconds
	 * @return distance in meters
	 */
	public double getDistance(long t);

	/**
	 * Returns the point in time when the given velocity has been or will be reached.
	 * @param v velocity in meters per second
	 * @return absolute time in milliseconds
	 */
	public long getTimeVelocityReached(double v);
}
