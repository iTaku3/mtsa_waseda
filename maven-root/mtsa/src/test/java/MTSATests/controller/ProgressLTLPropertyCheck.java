package MTSATests.controller;

import java.io.IOException;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ProgressLTLPropertyCheck extends LTSLTLPropertyTest {
	@DataProvider(name = "progressLTLPropertyTest")
	public Object[][] safeGameParameters() {
		return new Object[][] {
		{"../ltsa/dist/examples/LTLProperties/Tests/biscotti-idealised.lts", new String[]{"SuccessfullyCookedTwice"}}
		};
	}	
	
	@Test(dataProvider = "progressLTLPropertyTest")
	private void ProgressLTLPropertyTest(String filename, String[] properties) throws Exception, IOException {
		run(filename, "C", properties);
	}
}
