package ltsa.lts;

import ltsa.lts.ltl.AssertDefinition;
import ltsa.lts.ltl.FormulaSyntax;
import ltsa.updatingControllers.UpdateConstants;
import ltsa.control.ControllerGoalDefinition;

public class UpdatingControllersGoalsMaker {

	/**
	 * Saves each old safety goals as (!StopOldSpec -> OLD)
	 *
	 * @param formulaName
	 * @param cgd
	 */
	public static void addOldGoals(Symbol formulaName, ControllerGoalDefinition cgd) {
		// getting elements that I need to build the formula
		Symbol arrow = new Symbol(Symbol.ARROW);
		Symbol not = new Symbol(Symbol.PLING);
		ActionName stopOldSpecActionName = new ActionName(new Symbol(123, "StopOldSpec"));
		FormulaSyntax stopOldSpecFormula = FormulaSyntax.make(stopOldSpecActionName);
		FormulaSyntax dontDo = FormulaSyntax.make(null, not, stopOldSpecFormula);
		FormulaSyntax originalFormula = obtainFormula(formulaName);
		// building formula
		FormulaSyntax finalFormula = FormulaSyntax.make(dontDo, arrow, originalFormula);
		// saving formula
		addFormula(cgd, formulaName.toString(), finalFormula, UpdateConstants.OLD_SUFFIX);
		
	}

	/**
	 * Saves each new safety goals as (StartNewSpec -> NEW)
	 *
	 * @param formulaName
	 * @param cgd
	 */
	public static void addImplyUpdatingGoal(Symbol formulaName, ControllerGoalDefinition cgd) {
		// getting elements that I need to build the formula
		Symbol arrow = new Symbol(Symbol.ARROW);
		ActionName startNewSpecActionName = new ActionName(new Symbol(123, "StartNewSpec"));
		FormulaSyntax startNewSpecFormula = FormulaSyntax.make(startNewSpecActionName);
		FormulaSyntax originalFormula = obtainFormula(formulaName);
		// building formula
		FormulaSyntax finalFormula = FormulaSyntax.make(startNewSpecFormula, arrow, originalFormula);
		// saving formula
		addFormula(cgd, formulaName.toString(), finalFormula, UpdateConstants.NEW_SUFFIX);
	}

	/**
	 * @param formulaName
	 * @return
	 */
	private static FormulaSyntax obtainFormula(Symbol formulaName) {
		//TODO: Also consider that instead of a formula we could get a machine / lts
		AssertDefinition def = AssertDefinition.getConstraint(formulaName.getName());
		if (def == null) {
			throw new RuntimeException("ltl_property " + formulaName + " not found");
		}
		return def.getLTLFormula().removeLeftTemporalOperators();
	}

	private static void addFormula(ControllerGoalDefinition cgd, String name, FormulaSyntax formula, String suffix) {
		Symbol finalFormulaName = new Symbol(123, name + suffix);
		if (AssertDefinition.getConstraint(finalFormulaName.toString()) == null) {
			//TODO display a warning about possible duplicate property
			AssertDefinition.put(finalFormulaName, formula, null, null, null, true, false);
		}
		cgd.addSafetyDefinition(finalFormulaName);
	}


}