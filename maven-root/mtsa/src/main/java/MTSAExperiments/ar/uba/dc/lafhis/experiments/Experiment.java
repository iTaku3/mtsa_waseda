package MTSAExperiments.ar.uba.dc.lafhis.experiments;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Enumeration;

import org.apache.commons.lang.NullArgumentException;
import org.json.simple.JSONObject;

import MTSAExperiments.ar.uba.dc.lafhis.experiments.exchange.JSONCompatible;

public abstract class Experiment implements JSONCompatible {
	
	protected boolean	isOver;
	protected String	name;
	protected Dictionary<String, JSONCompatible> results;
	
	public Experiment(String name){
		this.name	= name;
		isOver		= false;
	}
	
	/***
	 * It will run the experiment with given parameters and then store the results as a json object in the provided destination.
	 * After running the experiment the results dictionary will be populated with proper values.
	 * @param parameters
	 * @param resultsDestination
	 */
	public void runExperiment(Dictionary<String, JSONCompatible> parameters, String resultsDestination){
		results	= primitiveRunExperiment(parameters);
		//save json results
		BufferedWriter writer = null;
		try
		{
			JSONObject compositeObject = new JSONObject();
			Enumeration<String> itKeys = results.keys();
			while(itKeys.hasMoreElements()){
				String key = itKeys.nextElement();
				compositeObject.put(key, results.get(key));
			}
		    writer = new BufferedWriter( new FileWriter( resultsDestination));
		    writer.write( compositeObject.toJSONString());

		}
		catch ( IOException e)
		{
//			System.out.println("problem writing results");
		}
		finally
		{
		    try
		    {
		        if ( writer != null)
		        writer.close( );
		    }
		    catch ( IOException e)
		    {
		    }
		}
		isOver	= true;
	}
	
	public abstract Dictionary<String, JSONCompatible> primitiveRunExperiment(Dictionary<String, JSONCompatible> parameters)  throws NullArgumentException;
}
