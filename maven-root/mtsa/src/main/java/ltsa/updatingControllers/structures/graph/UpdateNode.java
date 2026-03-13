package ltsa.updatingControllers.structures.graph;

import MTSTools.ac.ic.doc.mtstools.model.MTS;

import java.util.Set;

/**
 * Created by Victor Wjugow on 27/05/15.
 */
public class UpdateNode {

	private String goalName;
	private MTS<Long, String> environment;
	private MTS<Long, String> controller$environment;
	private Set<String> controllableActions;

	public MTS<Long, String> getEnvironment() {
		return environment;
	}

	public void setEnvironment(MTS<Long, String> environment) {
		this.environment = environment;
	}

	public MTS<Long, String> getController$environment() {
		return controller$environment;
	}

	public void setController$environment(MTS<Long, String> controller$environment) {
		this.controller$environment = controller$environment;
	}

	public String getGoalName() {
		return goalName;
	}

	public void setGoalName(String goalName) {
		this.goalName = goalName;
	}

	public Set<String> getControllableActions() {
		return controllableActions;
	}

	public void setControllableActions(Set<String> controllableActions) {
		this.controllableActions = controllableActions;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		UpdateNode that = (UpdateNode) o;
		if (goalName != null ? !goalName.equals(that.goalName) : that.goalName != null) {
			return false;
		}
		return !(environment != null ? !environment.equals(that.environment) : that.environment != null);
	}

	@Override
	public int hashCode() {
		int result = goalName != null ? goalName.hashCode() : 0;
		result = 31 * result + (environment != null ? environment.hashCode() : 0);
		return result;
	}
}