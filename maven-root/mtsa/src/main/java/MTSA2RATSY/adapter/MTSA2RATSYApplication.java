package MTSA2RATSY.adapter;

public class MTSA2RATSYApplication {

	public static void main(String[] args) {
		MTSAWrapper wrapper		= new MTSAWrapper("target/classes/ltsa/dist/examples/2012-Biscotti.lts", "C", "G1");
		SymbolicBuilder builder	= new SymbolicBuilder(wrapper);
		builder.buildSpecification();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		builder.saveSpecification("target/classes/MTSA2RATSY/results/biscotti.xml");
	}

}
