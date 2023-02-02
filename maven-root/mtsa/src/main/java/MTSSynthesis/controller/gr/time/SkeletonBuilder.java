package MTSSynthesis.controller.gr.time;

import java.util.Map;

public interface SkeletonBuilder<S,A,D> {
	GenericChooser<S, A, D> build(Map<S, Integer> skeleton);
}
