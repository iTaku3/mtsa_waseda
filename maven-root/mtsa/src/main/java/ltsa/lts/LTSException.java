package ltsa.lts;

public class LTSException extends RuntimeException {

    public Object marker;

    public LTSException (String errorMsg) {
	    super (errorMsg);
	    this.marker = null;
    }

    public LTSException (String errorMsg, Object marker) {
	    super (errorMsg);
    	this.marker = marker;
    }

}