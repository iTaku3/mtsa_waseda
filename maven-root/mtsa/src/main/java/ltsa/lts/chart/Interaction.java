package ltsa.lts.chart;


/**
 * @author gsibay
 *
 */
public class Interaction implements Location {

	private String source;
	private String target;
	private String message;
	
	public Interaction(String source, String message, String target) {
		this.source = source;
		this.target = target;
		this.message = message;
	}
	
	public String getSource() {
		return source;
	}

	public String getTarget() {
		return target;
	}

	public String getMessage() {
		return message;
	}
	
	public String toString() {
		return this.getSource() + " -> " + this.getMessage() + " -> " + this.getTarget();
	}
}
