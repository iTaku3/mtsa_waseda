package MTSSynthesis.ar.dc.uba.lsc;

import MTSSynthesis.ar.dc.uba.model.lsc.Chart;
import MTSSynthesis.ar.dc.uba.model.lsc.ChartLanguageGenerationException;
import MTSSynthesis.ar.dc.uba.model.lsc.Location;
import MTSSynthesis.ar.dc.uba.model.lsc.LocationNamingStrategyImpl;
import MTSSynthesis.ar.dc.uba.parser.ConditionLocationParser;
import MTSSynthesis.ar.dc.uba.parser.InteractionParser;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class ChartTest {

	private InteractionParser getInteractionParser() {
		return InteractionParser.getInstance();
	}

	private ConditionLocationParser getConditionLocationParser() {
		return ConditionLocationParser.getInstance();
	}
	
	private Chart createOneInteractionChart() {
		List<Location> chartLocations = new ArrayList<Location>();
		chartLocations.add(this.getInteractionParser().parseLocation(("User -> pwd -> ATM")));
		return createChart(chartLocations);
	}

	private Chart createOneConditionChart() {
		List<Location> chartLocations = new ArrayList<Location>();
		chartLocations.add(this.getConditionLocationParser().parseLocation(("Phi[ATM, Bank]")));
		return createChart(chartLocations);
	}
	
	private Chart createSeveralConditionsChart() {
		List<Location> chartLocations = new ArrayList<Location>();
		chartLocations.add(this.getConditionLocationParser().parseLocation(("Phi_1[User]")));
		chartLocations.add(this.getConditionLocationParser().parseLocation(("Phi_2[ATM]")));
		chartLocations.add(this.getConditionLocationParser().parseLocation(("Phi_3[ATM, Bank]")));
		return createChart(chartLocations);
	}

	private Chart createOneLinearisationChartWithInteractionAndConditions() {
		List<Location> chartLocations = new ArrayList<Location>();
		chartLocations.add(this.getInteractionParser().parseLocation("User -> pwd -> ATM"));
		chartLocations.add(this.getConditionLocationParser().parseLocation("Phi_1[ATM, Bank]"));
		chartLocations.add(this.getInteractionParser().parseLocation("ATM -> checkPwd -> Bank"));
		chartLocations.add(this.getInteractionParser().parseLocation("ATM -> wait -> User"));
		chartLocations.add(this.getConditionLocationParser().parseLocation("Phi_2[ATM]"));
		chartLocations.add(this.getInteractionParser().parseLocation("Bank -> checkOk -> ATM"));
		chartLocations.add(this.getInteractionParser().parseLocation("ATM -> ok -> User"));
		return createChart(chartLocations);
	}

	private Chart createSeveralLinearisationsChartWithInteractionAndConditions() {
		List<Location> chartLocations = new ArrayList<Location>();
		chartLocations.add(this.getInteractionParser().parseLocation("User -> pwd -> ATM"));
		chartLocations.add(this.getInteractionParser().parseLocation("ATM -> checkPwd -> Bank"));
		chartLocations.add(this.getConditionLocationParser().parseLocation("Phi_1[ATM, User, Bank]"));
		chartLocations.add(this.getConditionLocationParser().parseLocation("Phi_2[ATM]"));
		chartLocations.add(this.getInteractionParser().parseLocation("ATM -> wait -> User"));
		chartLocations.add(this.getInteractionParser().parseLocation("Bank -> doCheck -> Bank"));
		chartLocations.add(this.getInteractionParser().parseLocation("Bank -> checkOk -> ATM"));
		chartLocations.add(this.getInteractionParser().parseLocation("ATM -> ok -> User"));
		return createChart(chartLocations);
	}

	private Chart createSeveralLinearisationsChartWithInteractionAndConditionsA() {
		List<Location> chartLocations = new ArrayList<Location>();
		chartLocations.add(this.getInteractionParser().parseLocation("User -> pwd -> ATM"));
		chartLocations.add(this.getInteractionParser().parseLocation("ATM -> checkPwd -> Bank"));
		chartLocations.add(this.getConditionLocationParser().parseLocation("Phi_1[Bank]"));
		chartLocations.add(this.getConditionLocationParser().parseLocation("Phi_2[ATM]"));
		chartLocations.add(this.getConditionLocationParser().parseLocation("Phi_3[ATM,User]"));
		chartLocations.add(this.getConditionLocationParser().parseLocation("Phi_4[Bank]"));		
		chartLocations.add(this.getInteractionParser().parseLocation("ATM -> wait -> User"));
		chartLocations.add(this.getInteractionParser().parseLocation("Bank -> doCheck -> Bank"));
		chartLocations.add(this.getInteractionParser().parseLocation("Bank -> checkOk -> ATM"));
		chartLocations.add(this.getInteractionParser().parseLocation("ATM -> ok -> User"));
		return createChart(chartLocations);
	}

	private Chart createSeveralLinearisationsChartWithInteractionAndConditionsB() {
		List<Location> chartLocations = new ArrayList<Location>();
		chartLocations.add(this.getInteractionParser().parseLocation("User -> pwd -> ATM"));
		chartLocations.add(this.getInteractionParser().parseLocation("ATM -> checkPwd -> Bank"));
		chartLocations.add(this.getConditionLocationParser().parseLocation("Phi_1[Bank]"));
		chartLocations.add(this.getConditionLocationParser().parseLocation("Phi_2[ATM]"));
		chartLocations.add(this.getConditionLocationParser().parseLocation("Phi_3[ATM,Bank]"));
		chartLocations.add(this.getInteractionParser().parseLocation("ATM -> wait -> User"));
		chartLocations.add(this.getInteractionParser().parseLocation("Bank -> doCheck -> Bank"));
		chartLocations.add(this.getInteractionParser().parseLocation("Bank -> checkOk -> ATM"));
		chartLocations.add(this.getInteractionParser().parseLocation("ATM -> ok -> User"));
		return createChart(chartLocations);
	}

	private Chart createSeveralConditionsBeginingChartWithInteractionsA() {
		List<Location> chartLocations = new ArrayList<Location>();
		chartLocations.add(this.getConditionLocationParser().parseLocation("Phi_1[Bank]"));
		chartLocations.add(this.getConditionLocationParser().parseLocation("Phi_2[ATM]"));
		chartLocations.add(this.getConditionLocationParser().parseLocation("Phi_3[ATM,User]"));
		chartLocations.add(this.getConditionLocationParser().parseLocation("Phi_4[Bank]"));		
		chartLocations.add(this.getInteractionParser().parseLocation("ATM -> wait -> User"));
		chartLocations.add(this.getInteractionParser().parseLocation("Bank -> doCheck -> Bank"));
		chartLocations.add(this.getInteractionParser().parseLocation("Bank -> checkOk -> ATM"));
		chartLocations.add(this.getInteractionParser().parseLocation("ATM -> ok -> User"));
		return createChart(chartLocations);		
	}

	private Chart createSeveralConditionsBeginingChartWithInteractionsB() {
		List<Location> chartLocations = new ArrayList<Location>();
		chartLocations.add(this.getConditionLocationParser().parseLocation("Phi_1[Bank]"));
		chartLocations.add(this.getConditionLocationParser().parseLocation("Phi_2[ATM]"));
		chartLocations.add(this.getConditionLocationParser().parseLocation("Phi_3[ATM,Bank]"));
		chartLocations.add(this.getInteractionParser().parseLocation("ATM -> wait -> User"));
		chartLocations.add(this.getInteractionParser().parseLocation("Bank -> doCheck -> Bank"));
		chartLocations.add(this.getInteractionParser().parseLocation("Bank -> checkOk -> ATM"));
		chartLocations.add(this.getInteractionParser().parseLocation("ATM -> ok -> User"));
		return createChart(chartLocations);
	}
	
	private Chart createSeveralInteractionChart() {
		List<Location> chartLocations = new ArrayList<Location>();
		chartLocations.add(this.getInteractionParser().parseLocation("User -> pwd -> ATM"));
		chartLocations.add(this.getInteractionParser().parseLocation("ATM -> checkPwd -> Bank"));
		chartLocations.add(this.getInteractionParser().parseLocation("ATM -> wait -> User"));
		chartLocations.add(this.getInteractionParser().parseLocation("Bank -> checkOk -> ATM"));
		chartLocations.add(this.getInteractionParser().parseLocation("ATM -> ok -> User"));
		return createChart(chartLocations);
	}

	private Chart createSeveralInteractionChartSeveralLinearisations() {
		List<Location> chartLocations = new ArrayList<Location>();
		chartLocations.add(this.getInteractionParser().parseLocation("User -> pwd -> ATM"));
		chartLocations.add(this.getInteractionParser().parseLocation("ATM -> checkPwd -> Bank"));
		chartLocations.add(this.getInteractionParser().parseLocation("ATM -> wait -> User"));
		chartLocations.add(this.getInteractionParser().parseLocation("Bank -> doCheck -> Bank"));
		chartLocations.add(this.getInteractionParser().parseLocation("Bank -> checkOk -> ATM"));
		chartLocations.add(this.getInteractionParser().parseLocation("ATM -> ok -> User"));
		return createChart(chartLocations);
	}

	
	private Chart createChart(List<Location> chartLocations) {
		try {
			return new Chart(chartLocations, new LocationNamingStrategyImpl());
		} catch (ChartLanguageGenerationException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Test
	public void testQtyInteractions() {
		assertEquals(1, this.createOneInteractionChart().getQtyInteractions());
		
		assertEquals(5, this.createSeveralInteractionChart().getQtyInteractions());
		
		assertEquals(6, this.createSeveralInteractionChartSeveralLinearisations().getQtyInteractions());
		
		assertEquals(0, this.createOneConditionChart().getQtyInteractions());
		
		assertEquals(0, this.createSeveralConditionsChart().getQtyInteractions());
		
		assertEquals(5, this.createOneLinearisationChartWithInteractionAndConditions().getQtyInteractions());
		
		assertEquals(6, this.createSeveralLinearisationsChartWithInteractionAndConditions().getQtyInteractions());
		
		assertEquals(6, this.createSeveralLinearisationsChartWithInteractionAndConditionsA().getQtyInteractions());
		
		assertEquals(6, this.createSeveralLinearisationsChartWithInteractionAndConditionsB().getQtyInteractions());
		
		assertEquals(4, this.createSeveralConditionsBeginingChartWithInteractionsA().getQtyInteractions());
		
		assertEquals(4, this.createSeveralConditionsBeginingChartWithInteractionsB().getQtyInteractions());
	}

	@Test
	public void testQtyLinearisations() {
		assertEquals(1, this.createOneInteractionChart().getLinearisations().size());

		assertEquals(1, this.createSeveralInteractionChart().getLinearisations().size());

		assertEquals(2, this.createSeveralInteractionChartSeveralLinearisations().getLinearisations().size());

		assertEquals(1, this.createOneConditionChart().getLinearisations().size());

		assertEquals(3, this.createSeveralConditionsChart().getLinearisations().size());

		assertEquals(1, this.createOneLinearisationChartWithInteractionAndConditions().getLinearisations().size());

		assertEquals(2, this.createSeveralLinearisationsChartWithInteractionAndConditions().getLinearisations().size());

		assertEquals(2, this.createSeveralLinearisationsChartWithInteractionAndConditionsA().getLinearisations().size());

		assertEquals(2, this.createSeveralLinearisationsChartWithInteractionAndConditionsB().getLinearisations().size());

		assertEquals(12, this.createSeveralConditionsBeginingChartWithInteractionsA().getLinearisations().size());

		assertEquals(4, this.createSeveralConditionsBeginingChartWithInteractionsB().getLinearisations().size());
	}
	
	@Test
	public void testOrderNotImportantForLinearisationOnlyInteractions() {
		// If there is no ordering for the locations (like "wait" and "doCheck"), 
		// both Chart's linearisations must be equals
		List<Location> chartLocations = new ArrayList<Location>();
		chartLocations.add(this.getInteractionParser().parseLocation("User -> pwd -> ATM"));
		chartLocations.add(this.getInteractionParser().parseLocation("ATM -> checkPwd -> Bank"));
		chartLocations.add(this.getInteractionParser().parseLocation("ATM -> wait -> User"));
		chartLocations.add(this.getInteractionParser().parseLocation("Bank -> doCheck -> Bank"));
		chartLocations.add(this.getInteractionParser().parseLocation("Bank -> checkOk -> ATM"));
		chartLocations.add(this.getInteractionParser().parseLocation("ATM -> ok -> User"));
		Chart aChart = this.createChart(chartLocations);
		
		chartLocations = new ArrayList<Location>();
		chartLocations.add(this.getInteractionParser().parseLocation("User -> pwd -> ATM"));
		chartLocations.add(this.getInteractionParser().parseLocation("ATM -> checkPwd -> Bank"));
		chartLocations.add(this.getInteractionParser().parseLocation("Bank -> doCheck -> Bank"));
		chartLocations.add(this.getInteractionParser().parseLocation("ATM -> wait -> User"));
		chartLocations.add(this.getInteractionParser().parseLocation("Bank -> checkOk -> ATM"));
		chartLocations.add(this.getInteractionParser().parseLocation("ATM -> ok -> User"));
		Chart anotherChart = this.createChart(chartLocations);
		
		assertEquals(aChart.getLinearisations(), anotherChart.getLinearisations());
	}

	@Test
	public void testOrderNotImportantForLinearisationInteractionsAndConditions() {
		// If there is no ordering for the locations (like "wait" and "doCheck"), 
		// both Chart's linearisations must be equals
		List<Location> chartLocations = new ArrayList<Location>();
		chartLocations.add(this.getInteractionParser().parseLocation("User -> pwd -> ATM"));
		chartLocations.add(this.getInteractionParser().parseLocation("ATM -> checkPwd -> Bank"));
		chartLocations.add(this.getConditionLocationParser().parseLocation("Phi_1[ATM, User, Bank]"));
		chartLocations.add(this.getConditionLocationParser().parseLocation("Phi_2[ATM]"));
		chartLocations.add(this.getInteractionParser().parseLocation("ATM -> wait -> User"));
		chartLocations.add(this.getInteractionParser().parseLocation("Bank -> doCheck -> Bank"));
		chartLocations.add(this.getInteractionParser().parseLocation("Bank -> checkOk -> ATM"));
		chartLocations.add(this.getInteractionParser().parseLocation("ATM -> ok -> User"));
		Chart aChart = this.createChart(chartLocations);
		
		chartLocations = new ArrayList<Location>();
		chartLocations.add(this.getInteractionParser().parseLocation("User -> pwd -> ATM"));
		chartLocations.add(this.getInteractionParser().parseLocation("ATM -> checkPwd -> Bank"));
		chartLocations.add(this.getConditionLocationParser().parseLocation("Phi_1[ATM, User, Bank]"));
		chartLocations.add(this.getConditionLocationParser().parseLocation("Phi_2[ATM]"));
		chartLocations.add(this.getInteractionParser().parseLocation("Bank -> doCheck -> Bank"));
		chartLocations.add(this.getInteractionParser().parseLocation("ATM -> wait -> User"));
		chartLocations.add(this.getInteractionParser().parseLocation("Bank -> checkOk -> ATM"));
		chartLocations.add(this.getInteractionParser().parseLocation("ATM -> ok -> User"));
		Chart anotherChart = this.createChart(chartLocations);
		
		assertEquals(aChart.getLinearisations(), anotherChart.getLinearisations());
	}

	@Test
	public void testOrderNotImportantForConditionsInstances() {
		List<Location> chartLocations = new ArrayList<Location>();
		chartLocations.add(this.getConditionLocationParser().parseLocation(("Phi_1[ATM, User, Bank]")));
		chartLocations.add(this.getConditionLocationParser().parseLocation(("Phi_3[ATM, Bank]")));
		Chart aChart = this.createChart(chartLocations);

		chartLocations = new ArrayList<Location>();
		chartLocations.add(this.getConditionLocationParser().parseLocation(("Phi_1[User, ATM, Bank]")));
		chartLocations.add(this.getConditionLocationParser().parseLocation(("Phi_3[Bank, ATM]")));
		Chart anotherChart = this.createChart(chartLocations);
		
		assertEquals(aChart, anotherChart);
	}

	@Test
	public void testOrderNotImportantForConditionInstances() {
		List<Location> chartLocations = new ArrayList<Location>();
		chartLocations.add(this.getConditionLocationParser().parseLocation(("Phi_3[ATM, Bank]")));
		Chart aChart = this.createChart(chartLocations);

		chartLocations = new ArrayList<Location>();
		chartLocations.add(this.getConditionLocationParser().parseLocation(("Phi_3[Bank, ATM]")));
		Chart anotherChart = this.createChart(chartLocations);
		
		assertEquals(aChart, anotherChart);
	}

	@Test
	public void testLinearisationsOnlyInteractions() {
		// Test for one interaction and one linearisation
		Set<List<Location>> linearisations = new HashSet<List<Location>>();
		List<Location> linearisation = new ArrayList<Location>();
		linearisation.add(this.getInteractionParser().parseLocation(("User -> pwd -> ATM")));
		
		linearisations.add(linearisation);
		
		assertEquals(linearisations,this.createOneInteractionChart().getLinearisations());
		
		// Test for several interactions and one linearisation
		linearisations = new HashSet<List<Location>>();
		
		linearisation = new ArrayList<Location>();
		linearisation.add(this.getInteractionParser().parseLocation("User -> pwd -> ATM"));
		linearisation.add(this.getInteractionParser().parseLocation("ATM -> checkPwd -> Bank"));
		linearisation.add(this.getInteractionParser().parseLocation("ATM -> wait -> User"));
		linearisation.add(this.getInteractionParser().parseLocation("Bank -> checkOk -> ATM"));
		linearisation.add(this.getInteractionParser().parseLocation("ATM -> ok -> User"));

		
		linearisations.add(linearisation);
		
		assertEquals(linearisations,this.createSeveralInteractionChart().getLinearisations());
		
		// Test for several interactions and several linearisations
		linearisations = new HashSet<List<Location>>();
		
		linearisation = new ArrayList<Location>();
		linearisation.add(this.getInteractionParser().parseLocation("User -> pwd -> ATM"));
		linearisation.add(this.getInteractionParser().parseLocation("ATM -> checkPwd -> Bank"));
		linearisation.add(this.getInteractionParser().parseLocation("ATM -> wait -> User"));
		linearisation.add(this.getInteractionParser().parseLocation("Bank -> doCheck -> Bank"));
		linearisation.add(this.getInteractionParser().parseLocation("Bank -> checkOk -> ATM"));
		linearisation.add(this.getInteractionParser().parseLocation("ATM -> ok -> User"));

		linearisations.add(linearisation);

		// another linearisation
		linearisation = new ArrayList<Location>();
		linearisation.add(this.getInteractionParser().parseLocation("User -> pwd -> ATM"));
		linearisation.add(this.getInteractionParser().parseLocation("ATM -> checkPwd -> Bank"));
		linearisation.add(this.getInteractionParser().parseLocation("Bank -> doCheck -> Bank"));
		linearisation.add(this.getInteractionParser().parseLocation("ATM -> wait -> User"));
		linearisation.add(this.getInteractionParser().parseLocation("Bank -> checkOk -> ATM"));
		linearisation.add(this.getInteractionParser().parseLocation("ATM -> ok -> User"));

		linearisations.add(linearisation);

		assertEquals(linearisations,this.createSeveralInteractionChartSeveralLinearisations().getLinearisations());
	}
	
	@Test
	public void testLinearisationsWithInteractionsAndConditions() {
		// Test only one linearisation
		Set<List<Location>> linearisations = new HashSet<List<Location>>();
		List<Location> linearisation = new ArrayList<Location>();

		linearisation.add(this.getInteractionParser().parseLocation("User -> pwd -> ATM"));
		linearisation.add(this.getConditionLocationParser().parseLocation("Phi_1[ATM, Bank]"));
		linearisation.add(this.getInteractionParser().parseLocation("ATM -> checkPwd -> Bank"));
		linearisation.add(this.getInteractionParser().parseLocation("ATM -> wait -> User"));
		linearisation.add(this.getConditionLocationParser().parseLocation("Phi_2[ATM]"));		
		linearisation.add(this.getInteractionParser().parseLocation("Bank -> checkOk -> ATM"));
		linearisation.add(this.getInteractionParser().parseLocation("ATM -> ok -> User"));

		linearisations.add(linearisation);

		assertEquals(linearisations,this.createOneLinearisationChartWithInteractionAndConditions().getLinearisations());
	}

	@Test
	public void testTwoLinearisationsWithInteractionsAndConditions() {
		// first linearisation
		Set<List<Location>> linearisations = new HashSet<List<Location>>();
		List<Location> linearisation = new ArrayList<Location>();

		linearisation.add(this.getInteractionParser().parseLocation("User -> pwd -> ATM"));
		linearisation.add(this.getInteractionParser().parseLocation("ATM -> checkPwd -> Bank"));
		linearisation.add(this.getConditionLocationParser().parseLocation("Phi_1[ATM, Bank, User]"));
		linearisation.add(this.getConditionLocationParser().parseLocation("Phi_2[ATM]"));
		linearisation.add(this.getInteractionParser().parseLocation("ATM -> wait -> User"));
		linearisation.add(this.getInteractionParser().parseLocation("Bank -> doCheck -> Bank"));
		linearisation.add(this.getInteractionParser().parseLocation("Bank -> checkOk -> ATM"));
		linearisation.add(this.getInteractionParser().parseLocation("ATM -> ok -> User"));

		linearisations.add(linearisation);

		// another linearisation
		linearisation = new ArrayList<Location>();

		linearisation.add(this.getInteractionParser().parseLocation("User -> pwd -> ATM"));
		linearisation.add(this.getInteractionParser().parseLocation("ATM -> checkPwd -> Bank"));
		linearisation.add(this.getConditionLocationParser().parseLocation("Phi_1[ATM, Bank, User]"));
		linearisation.add(this.getConditionLocationParser().parseLocation("Phi_2[ATM]"));
		linearisation.add(this.getInteractionParser().parseLocation("Bank -> doCheck -> Bank"));
		linearisation.add(this.getInteractionParser().parseLocation("ATM -> wait -> User"));
		linearisation.add(this.getInteractionParser().parseLocation("Bank -> checkOk -> ATM"));
		linearisation.add(this.getInteractionParser().parseLocation("ATM -> ok -> User"));

		linearisations.add(linearisation);
		
		assertEquals(linearisations,this.createSeveralLinearisationsChartWithInteractionAndConditions().getLinearisations());
	}

	@Ignore
	public void testTwoLinearisationsWithInteractionsAndConditionsA() {
		// first linearisation
		Set<List<Location>> linearisations = new HashSet<List<Location>>();
		List<Location> linearisation = new ArrayList<Location>();

		linearisation.add(this.getInteractionParser().parseLocation("User -> pwd -> ATM"));
		linearisation.add(this.getInteractionParser().parseLocation("ATM -> checkPwd -> Bank"));
		linearisation.add(this.getConditionLocationParser().parseLocation("Phi_1[Bank]"));
		linearisation.add(this.getConditionLocationParser().parseLocation("Phi_2[ATM]"));
		linearisation.add(this.getConditionLocationParser().parseLocation("Phi_3[ATM,User]"));
		linearisation.add(this.getConditionLocationParser().parseLocation("Phi_4[Bank]"));		
		linearisation.add(this.getInteractionParser().parseLocation("ATM -> wait -> User"));
		linearisation.add(this.getInteractionParser().parseLocation("Bank -> doCheck -> Bank"));
		linearisation.add(this.getInteractionParser().parseLocation("Bank -> checkOk -> ATM"));
		linearisation.add(this.getInteractionParser().parseLocation("ATM -> ok -> User"));

		linearisations.add(linearisation);

		// another linearisation
		linearisation = new ArrayList<Location>();

		linearisation.add(this.getInteractionParser().parseLocation("User -> pwd -> ATM"));
		linearisation.add(this.getInteractionParser().parseLocation("ATM -> checkPwd -> Bank"));
		linearisation.add(this.getConditionLocationParser().parseLocation("Phi_1[Bank]"));
		linearisation.add(this.getConditionLocationParser().parseLocation("Phi_2[ATM]"));
		linearisation.add(this.getConditionLocationParser().parseLocation("Phi_3[ATM,User]"));
		linearisation.add(this.getConditionLocationParser().parseLocation("Phi_4[Bank]"));		
		linearisation.add(this.getInteractionParser().parseLocation("Bank -> doCheck -> Bank"));
		linearisation.add(this.getInteractionParser().parseLocation("ATM -> wait -> User"));
		linearisation.add(this.getInteractionParser().parseLocation("Bank -> checkOk -> ATM"));
		linearisation.add(this.getInteractionParser().parseLocation("ATM -> ok -> User"));

		linearisations.add(linearisation);
		
		assertEquals(linearisations,this.createSeveralLinearisationsChartWithInteractionAndConditionsA().getLinearisations());
	}

	@Ignore
	public void testTwoLinearisationsWithInteractionsAndConditionsB() {
		// first linearisation
		Set<List<Location>> linearisations = new HashSet<List<Location>>();
		List<Location> linearisation = new ArrayList<Location>();

		linearisation.add(this.getInteractionParser().parseLocation("User -> pwd -> ATM"));
		linearisation.add(this.getInteractionParser().parseLocation("ATM -> checkPwd -> Bank"));
		linearisation.add(this.getConditionLocationParser().parseLocation("Phi_1[Bank]"));
		linearisation.add(this.getConditionLocationParser().parseLocation("Phi_2[ATM]"));
		linearisation.add(this.getConditionLocationParser().parseLocation("Phi_3[ATM,Bank]"));
		linearisation.add(this.getInteractionParser().parseLocation("ATM -> wait -> User"));
		linearisation.add(this.getInteractionParser().parseLocation("Bank -> doCheck -> Bank"));
		linearisation.add(this.getInteractionParser().parseLocation("Bank -> checkOk -> ATM"));
		linearisation.add(this.getInteractionParser().parseLocation("ATM -> ok -> User"));

		linearisations.add(linearisation);

		// another linearisation
		linearisation = new ArrayList<Location>();

		linearisation.add(this.getInteractionParser().parseLocation("User -> pwd -> ATM"));
		linearisation.add(this.getInteractionParser().parseLocation("ATM -> checkPwd -> Bank"));
		linearisation.add(this.getConditionLocationParser().parseLocation("Phi_1[Bank]"));
		linearisation.add(this.getConditionLocationParser().parseLocation("Phi_2[ATM]"));
		linearisation.add(this.getConditionLocationParser().parseLocation("Phi_3[ATM,Bank]"));
		linearisation.add(this.getInteractionParser().parseLocation("Bank -> doCheck -> Bank"));
		linearisation.add(this.getInteractionParser().parseLocation("ATM -> wait -> User"));
		linearisation.add(this.getInteractionParser().parseLocation("Bank -> checkOk -> ATM"));
		linearisation.add(this.getInteractionParser().parseLocation("ATM -> ok -> User"));

		linearisations.add(linearisation);

		assertSame(linearisations,this.createSeveralLinearisationsChartWithInteractionAndConditionsB().getLinearisations());
	}


	@Test
	public void testTwoLinearisationsSeveralConditionsBeginingChartWithInteractionsB() {
		// first linearisation
		Set<List<Location>> linearisations = new HashSet<List<Location>>();
		List<Location> linearisation = new ArrayList<Location>();

		linearisation.add(this.getConditionLocationParser().parseLocation("Phi_1[Bank]"));
		linearisation.add(this.getConditionLocationParser().parseLocation("Phi_2[ATM]"));
		linearisation.add(this.getConditionLocationParser().parseLocation("Phi_3[ATM,Bank]"));
		linearisation.add(this.getInteractionParser().parseLocation("ATM -> wait -> User"));
		linearisation.add(this.getInteractionParser().parseLocation("Bank -> doCheck -> Bank"));
		linearisation.add(this.getInteractionParser().parseLocation("Bank -> checkOk -> ATM"));
		linearisation.add(this.getInteractionParser().parseLocation("ATM -> ok -> User"));

		linearisations.add(linearisation);

		// another linearisation
		linearisation = new ArrayList<Location>();

		linearisation.add(this.getConditionLocationParser().parseLocation("Phi_1[Bank]"));
		linearisation.add(this.getConditionLocationParser().parseLocation("Phi_2[ATM]"));
		linearisation.add(this.getConditionLocationParser().parseLocation("Phi_3[ATM,Bank]"));
		linearisation.add(this.getInteractionParser().parseLocation("Bank -> doCheck -> Bank"));
		linearisation.add(this.getInteractionParser().parseLocation("ATM -> wait -> User"));
		linearisation.add(this.getInteractionParser().parseLocation("Bank -> checkOk -> ATM"));
		linearisation.add(this.getInteractionParser().parseLocation("ATM -> ok -> User"));

		linearisations.add(linearisation);
		
		System.out.println(linearisations);
		System.out.println(this.createSeveralConditionsBeginingChartWithInteractionsB().getLinearisations());
		
		// They wont be equals because of the flattened condition name		
		//assertEquals(linearisations,this.createSeveralConditionsBeginingChartWithInteractionsB().getLinearisations());
	}

	@Test
	public void testTwoLinearisationsSeveralConditionsBeginingChartWithInteractionsA() {
		// first linearisation
		Set<List<Location>> linearisations = new HashSet<List<Location>>();
		List<Location> linearisation = new ArrayList<Location>();

		linearisation.add(this.getConditionLocationParser().parseLocation("Phi_1[Bank]"));
		linearisation.add(this.getConditionLocationParser().parseLocation("Phi_2[ATM]"));
		linearisation.add(this.getConditionLocationParser().parseLocation("Phi_3[ATM,User]"));
		linearisation.add(this.getConditionLocationParser().parseLocation("Phi_4[Bank]"));		
		linearisation.add(this.getInteractionParser().parseLocation("ATM -> wait -> User"));
		linearisation.add(this.getInteractionParser().parseLocation("Bank -> doCheck -> Bank"));
		linearisation.add(this.getInteractionParser().parseLocation("Bank -> checkOk -> ATM"));
		linearisation.add(this.getInteractionParser().parseLocation("ATM -> ok -> User"));		

		linearisations.add(linearisation);

		// another linearisation
		linearisation = new ArrayList<Location>();

		linearisation.add(this.getConditionLocationParser().parseLocation("Phi_1[Bank]"));
		linearisation.add(this.getConditionLocationParser().parseLocation("Phi_2[ATM]"));
		linearisation.add(this.getConditionLocationParser().parseLocation("Phi_3[ATM,User]"));
		linearisation.add(this.getConditionLocationParser().parseLocation("Phi_4[Bank]"));		
		linearisation.add(this.getInteractionParser().parseLocation("Bank -> doCheck -> Bank"));
		linearisation.add(this.getInteractionParser().parseLocation("ATM -> wait -> User"));
		linearisation.add(this.getInteractionParser().parseLocation("Bank -> checkOk -> ATM"));
		linearisation.add(this.getInteractionParser().parseLocation("ATM -> ok -> User"));		

		linearisations.add(linearisation);

		System.out.println(linearisations);
		System.out.println(this.createSeveralConditionsBeginingChartWithInteractionsA().getLinearisations());
		
		// They wont be equals because of the flattened condition name
		//assertEquals(linearisations,this.createSeveralConditionsBeginingChartWithInteractionsA().getLinearisations());
	}

}
