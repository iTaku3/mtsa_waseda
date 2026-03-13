package MTSSynthesis.ar.dc.uba.parser;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

import MTSSynthesis.ar.dc.uba.model.lsc.Interaction;
import MTSSynthesis.ar.dc.uba.model.lsc.Location;

/**
 * @author gsibay
 *
 */
public class InteractionParser implements LocationParser {

	public static InteractionParser instance = new InteractionParser();
	
	public static InteractionParser getInstance() {
		return instance;
	}
	
	/* (non-Javadoc)
	 * @see ar.dc.uba.parser.LocationParser#parseLocation(java.lang.String)
	 */
	@Override
	public Location parseLocation(String locationAsStr) {
		String[] interactionValues = StringUtils.split(locationAsStr, "->");
		Validate.isTrue(interactionValues.length == 3);
		
		return new Interaction(StringUtils.trim(interactionValues[0]), 
				StringUtils.trim(interactionValues[1]), 
					StringUtils.trim(interactionValues[2]));
	}

}
