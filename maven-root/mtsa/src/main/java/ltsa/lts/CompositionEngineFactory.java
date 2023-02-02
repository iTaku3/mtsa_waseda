package ltsa.lts;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class CompositionEngineFactory {
	public static CompositionEngine createCompositionEngine(String engineClassName, StateCodec coder) {
		Object obj= null;
		CompositionEngine result= null;
		try {
			Constructor constructor= Class.forName(engineClassName).getDeclaredConstructor(StateCodec.class);
			obj= constructor.newInstance(coder);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			 result= ((CompositionEngine) obj);
		} catch (ClassCastException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}
}
