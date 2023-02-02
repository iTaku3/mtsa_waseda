package MTSSynthesis.ar.dc.uba.model.lsc;


/**
 * Strategy for calculating a name for 
 * a location.
 * 
 * @author gsibay
 *
 */
public interface LocationNamingStrategy {

	public String calculateName(Interaction interaction);
	public String calculateName(ConditionLocation conditionLocation);
}
