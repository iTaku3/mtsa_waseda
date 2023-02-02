package MTSSynthesis.ar.dc.uba.parser;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

import MTSSynthesis.ar.dc.uba.model.condition.Formula;
import MTSSynthesis.ar.dc.uba.model.lsc.ConditionLocation;
import MTSSynthesis.ar.dc.uba.model.lsc.Location;

/**
 * @author gsibay
 *
 */
public class ConditionLocationParser implements LocationParser {

	public static ConditionLocationParser instance = new ConditionLocationParser();
	
	public static ConditionLocationParser getInstance() {
		return instance;
	}
	
	/**
	 * Condition location returned will have a
	 * True formula
	 */
	public Location parseLocation(String locationAsStr) {
		// a condition is like CondName[Instance_1, Instance_2, ..., Instance_n]
		String[] conditionValues = StringUtils.split(locationAsStr, "[]");
		Validate.isTrue(conditionValues.length == 2);
		
		String[] instancesArray = StringUtils.split(conditionValues[1], ",");
		Set<String>instances = new HashSet<String>();
		for (int i = 0; i < instancesArray.length; i++) {
			instances.add(StringUtils.trim(instancesArray[i]));
		}
		
		return new ConditionLocation(StringUtils.trim(conditionValues[0]), instances, Formula.TRUE_FORMULA);
	}

}
