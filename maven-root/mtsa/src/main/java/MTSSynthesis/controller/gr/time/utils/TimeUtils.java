package MTSSynthesis.controller.gr.time.utils;

import java.util.Set;

import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;

public abstract class TimeUtils {
	
	public static boolean isEnding(String action) {
		String actionNoId = removeInicialIds(action);
		return actionNoId.startsWith("e");
	}
	
	public static boolean isStarting(String action) {
		String actionNoId = removeInicialIds(action);
		return actionNoId.startsWith("s");
	}
	
	
	public static String getTimeLabel(String label){
			return "T" + label;
	}
	
	public static <S> String getLabelFromState(S state){
		if(state instanceof Pair){
			return "ST"+((Pair<?,?>)state).getFirst().toString();
		}
		return "ST"+state.toString();
	}
	
	
	public static <A,S> boolean isTransient(S s, MTS<S, A> result, Set<A> controllableActions){
		for (Pair<A, S> pair : result.getTransitions(s, TransitionType.REQUIRED)) {
			A transition = pair.getFirst();
			if(controllableActions.contains(transition)){
				return true;
			}
		}
		return false;
	}
	
	public static <A,S> boolean isFinal(MTS<S, A> result, S s){
		return result.getTransitions(s, TransitionType.REQUIRED).isEmpty();
	}
	
	public static String getTimer(String action){
		return getTimeLabel(action).replaceFirst("T", "t");
	}
	
	public static String getTimeFromTimer(String timer){
		return timer.replaceFirst("t","T");
	}
	
	public static String getTimerFromTime(String timer){
		return timer.replaceFirst("T","t");
	}
	
	private static String removeInicialIds(String string) {
		return string.replaceFirst("\\d+.", "");
	}

}
