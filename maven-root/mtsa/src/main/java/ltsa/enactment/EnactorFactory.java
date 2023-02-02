/**
 * 
 */
package ltsa.enactment;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import MTSAEnactment.ar.uba.dc.lafhis.enactment.TakeFirstController;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.Enactor;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.RobotAdapter;

/**
 * Enactor Factory
 * @author Julio
 *
 */
public class EnactorFactory<State, Action> {
	private Set<Enactor<State, Action>> enactors;

	/**
	 * @return the enactors
	 */
	private Set<Enactor<State, Action>> getEnactors() {
		return enactors;
	}

	/**
	 * @param robots the robots to set
	 */
	@Autowired
	private void setEnactors(Set<Enactor<State, Action>> enactors) {
		this.enactors = enactors;
	}
	
	public List<String> getEnactorNames() {
		List<String> ret = new ArrayList<String>();
		for (Enactor<State, Action> enactor : this.getEnactors())
		{
			if(!(enactor instanceof TakeFirstController))
				ret.add(enactor.getName());
		}
		return ret;
	}
	
	/**
	 * Returns an instance of the enactor by name  
	 * @param name of the enactor
	 * @return instance of enactor
	 * @throws Exception
	 */
	public Enactor<State, Action> getEnactor(String name) throws Exception
	{
		for (Enactor<State, Action> enactor : this.getEnactors())
		{
			if (enactor.getName().equals(name))
			{
				enactor.removeAllTransitionEventListener();
				enactor.setUp();
				return enactor;
			}			
		}
		throw new Exception("Enactor not found");		
	}
}
