/**
 * 
 */
package ltsa.enactment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import MTSTools.ac.ic.doc.mtstools.model.LTS;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.BaseController;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.TakeFirstController;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.RandomController;


/**
 * Maintain a list of available schedellers and create instances 
 *  
 * @author Julio
 *
 */
public class SchedulerFactory<State, Action> {
	
	
	private Set<BaseController<State, Action>> schedulers;
	
	
	public Set<BaseController<State, Action>> getSchedulers()
	{
		return schedulers;		
	}
	
	
	/**
	 * @return the list of scheduler names
	 */
	public List<String> getSchedulersList()
	{
		List<String> ret = new ArrayList<String>();
		for (BaseController<State, Action> scheduler : this.getSchedulers())
		{
			ret.add(scheduler.getName().toString());
		}
		return ret;
	}
	
	@Autowired
	public void setSchedulers(Set<BaseController<State, Action>> schedulers)
	{
		this.schedulers = schedulers;		
	}
	
	

	public SchedulerFactory()
	{

	}
	
	public BaseController<State, Action> getScheduler(String scheduler, LTS<State, Action> lts, Set<Action> controllableActions) throws Exception
	{
		if (scheduler != null && this.schedulers != null)
		{						
			for(BaseController<State, Action> item : this.schedulers)
			{
				if (item.getName().equals(scheduler))
				{
					item.removeAllTransitionEventListener();
					//item.removeAllTransitionDispatcher();
					item.setLts(lts);
					item.setControllableActions(controllableActions);
					item.setUp();
					return item;
				}
			}
			}
		
		throw new Exception("Scheduler not found");
	}


}
