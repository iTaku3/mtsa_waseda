package MTSSynthesis.controller.gr.time;

import MTSSynthesis.controller.gr.time.model.ChoiceType;
import MTSSynthesis.controller.gr.time.utils.TimeUtils;

import java.util.Set;

public class SchedulerUtils<A> {
	public ChoiceType getChoiceType(A label, Set<A> cActions) {
		if(cActions.contains(label)){
			return ChoiceType.CONTROLLABLE;
		}else{
			if(TimeUtils.isEnding(label.toString())){
				return ChoiceType.ENDS;
			}else{
				return ChoiceType.UNCONTROLLABLE;
			}
		}
	}
}
