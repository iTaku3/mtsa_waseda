package MTSSynthesis.ar.dc.uba.model.lsc;

import java.util.Set;

import org.apache.commons.collections15.Predicate;



/**
 * A chart location
 * 
 * @author gsibay
 *
 */
public interface Location {

	public static final Predicate<Location> isConditionPredicate = new Predicate<Location>() {

		public boolean evaluate(Location location) {
			return location instanceof ConditionLocation;
		}
		
	};
	
	String getName(LocationNamingStrategy locationNamingStrategy);
	
	/**
	 * Returns the instances affected by this location
	 * @return
	 */
	Set<String> getInstances();
}
