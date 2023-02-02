package MTSSynthesis.ar.dc.uba.model.condition;

/**
 * 
 * @author gsibay
 *
 */
public interface Formula {

	public boolean evaluate(Valuation valuation);
	
	public static Formula TRUE_FORMULA = new Formula() {

		@Override
		public boolean evaluate(Valuation valuation) {
			return true;
		}
		
		public String toString() {
			return "True";
		}
	};

	public static Formula FALSE_FORMULA = new Formula() {

		@Override
		public boolean evaluate(Valuation valuation) {
			return false;
		}

		public String toString() {
			return "False";
		}
		
	};

}
