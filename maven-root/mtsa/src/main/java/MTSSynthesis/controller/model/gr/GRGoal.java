package MTSSynthesis.controller.model.gr;

import java.util.Set;

import MTSSynthesis.controller.model.Assume;
import MTSSynthesis.controller.model.Assumptions;
import MTSSynthesis.controller.model.Guarantee;
import MTSSynthesis.controller.model.Guarantees;
import MTSSynthesis.controller.model.Goal;

public class GRGoal<State> implements Goal {

    private Set<State> failures;
    private Guarantees<State> guarantees;
    private Assumptions<State> assumptions;
    private boolean permissiveStrategy;

    public GRGoal(Guarantees<State> guarantees, Assumptions<State> assumptions,
                  Set<State> faults, boolean permissive) {
        this.setFailures(faults);
        this.setGuarantees(guarantees);
        this.addAssumptions(assumptions);
        this.permissiveStrategy = permissive;
    }

    private void addAssumptions(Assumptions<State> assumptions) {
        this.assumptions = assumptions;
    }

    private void setGuarantees(Guarantees<State> guarantees) {
        this.guarantees = guarantees;
    }

    //TODO this methods shouldn't be public
    public void setFailures(Set<State> faults) {
        this.failures = faults;
    }

    public Guarantee<State> getGuarantee(int guaranteeId) {
        return this.guarantees.getGuarantee(guaranteeId);
    }

    public Assume<State> getAssumption(int assumeId) {
        return this.assumptions.getAssume(assumeId);
    }

    public int getGuaranteesQuantity() {
        return this.guarantees.size();
    }

    public int getAssumptionsQuantity() {
        return this.assumptions.getSize();
    }

    public Guarantees<State> guarantees() {
        return this.guarantees;
    }

    public Guarantees<State> getGuarantees() {
        return this.guarantees;
    }

    public Assumptions<State> getAssumptions() {
        return this.assumptions;
    }

    /* (non-Javadoc)
     * @see controller.game.model.TrueGame#toString()
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer()
                .append("Guarantees: ").append(this.getGuarantees())
                .append("Assumptions: ").append(this.getAssumptions())
                .append("Faults: ").append(this.getFailures())
                .append(permissiveStrategy ? "Permissive." : "Not permissive.");
        return sb.toString();
    }

    public Set<State> getFailures() {
        return failures;
    }

    public boolean buildPermissiveStrategy() {
        return this.permissiveStrategy;
    }

    public void setPermissiveStrategy(boolean permissive) {
        this.permissiveStrategy = permissive;
    }


}
