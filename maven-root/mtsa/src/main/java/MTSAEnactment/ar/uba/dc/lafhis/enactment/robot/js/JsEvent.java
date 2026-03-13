package MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.js;

/**
 * An event fired by an object that can be listened to.
 * 
 * @param <O> origin
 * @author Timo G&uuml;nther
 */
public abstract class JsEvent<O> {

	/** The object that fired the event. */
	private final O origin;
	/** The time in milliseconds when the event was fired. */
	private final long time;

	/**
	 * Constructs a new instance of this class.
	 * @param origin the object that fired the event
	 * @param time the time in milliseconds when the event was fired
	 */
	public JsEvent(O origin, long time) {
		this.origin = origin;
		this.time = time;
	}

	/**
	 * Returns the object that fired the event.
	 * @return the object that fired the event
	 */
	public O getOrigin() {
		return origin;
	}

	/**
	 * Returns the time in milliseconds when the event was fired.
	 * @return the time in milliseconds when the event was fired
	 */
	public long getTime() {
		return time;
	}
}
