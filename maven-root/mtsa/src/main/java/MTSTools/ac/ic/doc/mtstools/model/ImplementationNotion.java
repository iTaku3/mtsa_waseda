package MTSTools.ac.ic.doc.mtstools.model;


public interface ImplementationNotion {

	public <S1,S2,A> boolean isAnImplementation(MTS<S1,A> mts, LTS<S2,A> lts);
	
}
