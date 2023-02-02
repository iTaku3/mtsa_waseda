package MTSSynthesis.ar.dc.uba.model.lsc;


/**
 * Default implementation.
 * The name don't take into
 * account the instances affected
 * by the location
 * @author gsibay
 *
 */
public class LocationNamingStrategyImpl implements LocationNamingStrategy {

	@Override
	public String calculateName(Interaction interaction) {
		return interaction.getMessage();
	}

	@Override
	public String calculateName(ConditionLocation conditionLocation) {
		return conditionLocation.getConditionName();
	}


}
