/**
 * 
 */
package RobotProtocolClient.ar.uba.dc.lafhis.enactment;

import static org.junit.Assert.*;

import java.util.HashSet;

import MTSAEnactment.ar.uba.dc.lafhis.enactment.BaseController;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.CounterEnactor;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.Enactor;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.RandomController;
import org.junit.Before;
import org.junit.Test;

import MTSAClient.ac.ic.doc.mtsa.MTSCompiler;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;
import MTSTools.ac.ic.doc.mtstools.model.impl.LTSAdapter;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.NXTRobot;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.RobotAdapter;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.UIRobot;

/**
 * @author Julio
 *
 */
public class RandomSchedulerTest {

	BaseController<Long, String> scheduler;
	//String definition = "FOLLOW = (follow -> (success -> TURN | lost -> LOST)), TURN = (turnLeft -> (success -> FOLLOW | lost -> LOST)).";
	String definition = "FOLLOW = (follow -> (success -> TURN | lost -> LOST)), TURN = (turnLeft -> success -> FOLLOW | turnRight -> success -> FOLLOW | turnAround -> success -> FOLLOW | follow -> success -> FOLLOW).";
	//String definition = "FOLLOW = (follow -> (success -> TURN | lost -> LOST)), TURN = (turnAround -> success -> FOLLOW ).";
	Enactor<Long, String> enactor;
	CounterEnactor<Long, String> counterEnactor;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		LTSAdapter<Long, String> ltsAdapter	= new LTSAdapter<Long, String>(
				MTSCompiler.getInstance().compileMTS("controller", definition)
				, TransitionType.REQUIRED);
		HashSet<String> controllableActions = new HashSet<String>();
		controllableActions.add("follow");
		controllableActions.add("turnLeft");
		controllableActions.add("turnRight");
		controllableActions.add("turnAround");
		scheduler	= new RandomController<Long, String>( ltsAdapter, controllableActions);

		String bt_address = "0016531b6519"; 
		//enactor = new UIRobot<Long, String>("ui robot", "follow", "turnLeft", "turnRight", "turnAround", "success", "failure", "lost");
		enactor = new NXTRobot<Long, String>("nxt", "success", "fail", "lost", "follow", "turnLeft", "turnRight", "turnAround", "calibrar", bt_address, "");
		counterEnactor = new CounterEnactor<Long, String>("counter", "resetCounter");
		try {
			//scheduler.addTransitionDispatcher(enactor);
			scheduler.addTransitionEventListener(enactor);
			enactor.addTransitionEventListener(scheduler);
			scheduler.addTransitionEventListener(counterEnactor);
			
			scheduler.setUp();
			enactor.setUp();
			counterEnactor.setUp();
			
			scheduler.start();
			enactor.start();
			counterEnactor.start();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Test method for {@link MTSAEnactment.ar.uba.dc.lafhis.enactment.RandomController#takeNextAction()}.
	 */
	//@Test
	public void testTakeNextAction() {
		try {
			scheduler.takeNextAction();
		} catch (Exception e) {

			e.printStackTrace();
			fail(e.getMessage());
			
		}
		while (true)
		{
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
