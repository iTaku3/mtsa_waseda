package MTSTools.ac.ic.doc.commons.collections;

import java.util.HashMap;
import java.util.Map;


/** This class implements a Map that initializes values with a factory method. */
public class InitMap<K, V> extends HashMap<K, V> implements Map<K, V> {

	private static final long serialVersionUID = -1160554492338063507L;

	/** Internal factory instance. */
	private Factory<V> factory;
	
	
	/** Constructor for an InitMap with a given factory. */ 
	public InitMap(Factory<V> factory) {
		this.factory = factory;
	}
	
	
	/** Consturctor for an InitMap with the default class factory. */
	public InitMap(Class<?> valueClass) {
		this((Factory<V>)new ClassFactory<>(valueClass));
	}
	
	
	/** Returns the value associated to the given key (initializes the value with the factory if not set). */
	@Override
	public V get(Object key) {
		V result = super.get(key);
		if (result == null && key != null)
			put((K)key, result = factory.newInstance());
		return result;
	}
	
	
	/** Interface for an InitMap.Factory. */
	public interface Factory<V> {
		public V newInstance();
	}
	
	
	/** Default class factory. */
	public static class ClassFactory<V> implements Factory<V> {

		/** Class of the values to instanciate. */
		private Class<V> valueClass;
		
		/** Constructor for a ClassFactory. */
		public ClassFactory(Class<?> valueClass) {
			this.valueClass = (Class<V>)valueClass;
		}
		
		/** Returns a new instance of V.
		 *  @throws RuntimeException when an InstantiationException occurs. */
		@Override
		public V newInstance() {
			V result = null;
			try { result = valueClass.newInstance(); }
			catch (InstantiationException | IllegalAccessException e) {
				throw new RuntimeException(e.getMessage());
			}
			return result;
		}
		
	}
	
}
