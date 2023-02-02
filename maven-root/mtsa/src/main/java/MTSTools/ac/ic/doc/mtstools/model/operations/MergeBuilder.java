package MTSTools.ac.ic.doc.mtstools.model.operations;

import MTSTools.ac.ic.doc.mtstools.model.MTS;

public interface MergeBuilder {

	public abstract <S1,S2,A> MTS<?,A> merge(MTS<S1,A> mtsA, MTS<S2, A> mtsB);
		
}
