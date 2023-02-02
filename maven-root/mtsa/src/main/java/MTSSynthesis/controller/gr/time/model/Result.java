package MTSSynthesis.controller.gr.time.model;

public enum Result {
	EQUALLYGOOD, INCOMPARABLES, BETTER, WORSE, EXCEPTION, EQUALLYGOOD_WARNING(true), BETTER_WARNING(true), WORSE_WARNING(true);
	
	private boolean warning;
	
	Result() {
		this.warning = false;
	}
	
	Result(boolean warning) {
		this.warning = warning;
	}
	
	public boolean isWarning() {
		return warning;
	}
	
}
