package ltsa.lts.chart;

import org.apache.commons.collections15.Predicate;

/**
 * A Chart location
 * @author gsibay
 *
 */
public interface Location {

	public static final Predicate<Location> isConditionPredicate = new Predicate<Location>() {

		public boolean evaluate(Location location) {
			return location instanceof ConditionLocation;
		}
		
	};
}
