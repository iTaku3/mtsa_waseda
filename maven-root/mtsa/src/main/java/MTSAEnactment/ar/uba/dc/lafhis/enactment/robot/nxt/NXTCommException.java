/**
 * 
 */
package MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.nxt;

/**
 * @author Julio
 *
 */
public class NXTCommException extends Exception {
	
	private int relatedResponse = 0;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	NXTCommException()
	{
		
	}
	
	NXTCommException(String message)
	{
		super(message);
	}
	NXTCommException(String message, int relatedResponse)
	{
		super(message);
		this.setRelatedResponse(relatedResponse);
	}
	NXTCommException(int relatedResponse)
	{
		this.setRelatedResponse(relatedResponse);
	}
	/**
	 * @return the relatedResponse
	 */
	public int getRelatedResponse() {
		return relatedResponse;
	}

	/**
	 * @param relatedResponse the relatedResponse to set
	 */
	public void setRelatedResponse(int relatedResponse) {
		this.relatedResponse = relatedResponse;
	}
}
