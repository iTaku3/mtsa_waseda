/*
Copyright (c) since 2015, Tel Aviv University and Software Modeling Lab

All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of Tel Aviv University and Software Modeling Lab nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL Tel Aviv University and Software Modeling Lab 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT 
LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
*/

package tau.smlab.syntech.typesystem;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.xtext.EcoreUtil2;

import tau.smlab.syntech.spectra.Constant;
import tau.smlab.syntech.spectra.Counter;
import tau.smlab.syntech.spectra.DefineDecl;
import tau.smlab.syntech.spectra.DomainVarDecl;
import tau.smlab.syntech.spectra.LTLAsm;
import tau.smlab.syntech.spectra.LTLGar;
import tau.smlab.syntech.spectra.Monitor;
import tau.smlab.syntech.spectra.Pattern;
import tau.smlab.syntech.spectra.PatternParam;
import tau.smlab.syntech.spectra.PatternParamList;
import tau.smlab.syntech.spectra.Predicate;
import tau.smlab.syntech.spectra.SpectraPackage;
import tau.smlab.syntech.spectra.TemporalExpression;
import tau.smlab.syntech.spectra.TemporalPrimaryExpr;
import tau.smlab.syntech.spectra.TypeConstant;
import tau.smlab.syntech.spectra.TypedParam;
import tau.smlab.syntech.spectra.TypedParamList;
import tau.smlab.syntech.spectra.VarDecl;
import tau.smlab.syntech.spectra.VarType;

public class TypeSystemTemporalPrimaryExpr {

	// TODO temporary implementation. replace with grammarAccess injection
	private static final String NOT = "!";
	private static final String MINUS = "-";
	private static final String NEXT = "next";

	public static TypeCheckIssue typeCheck(TemporalPrimaryExpr temporalPrimaryExpr) {
		TypeCheckIssue issue = null;
		if (temporalPrimaryExpr.getPredPatt() != null) {
			issue = chekNumberOfArgumentsMatch(temporalPrimaryExpr);
			if (issue != null) {
				return issue;
			}
			if (temporalPrimaryExpr.getPredPatt() instanceof Predicate) {
				issue = checkLegalParamsOfPredicate(temporalPrimaryExpr);
				if (issue != null) {
					return issue;
				}
			}
			if (temporalPrimaryExpr.getPredPatt() instanceof Pattern) {
				issue = checkLegalParamsOfPattern(temporalPrimaryExpr);
				if (issue != null) {
					return issue;
				}
				issue = checkPatternIsAloneInFormula(temporalPrimaryExpr);
				if (issue != null) {
					return issue;
				}
			}
		}

		if (temporalPrimaryExpr.getPredPattParams() != null) {
			issue = checkNoCallToPattern(temporalPrimaryExpr);
			if (issue != null) {
				return issue;
			}
		}

		if (temporalPrimaryExpr.getOperator() != null && temporalPrimaryExpr.getOperator().equals(NOT)) {
			BooleanAndString isTemporalExpressionWithinNotBoolean = TypeSystemUtils
					.isBooleanExpression(temporalPrimaryExpr.getTpe());
			if (!isTemporalExpressionWithinNotBoolean.getBoolean()) {
				return new TypeCheckError(SpectraPackage.Literals.TEMPORAL_PRIMARY_EXPR__OPERATOR,
						IssueMessages.NOT_EXPR_ARGUMENT_MUST_BE_BOOLEAN + ": "
								+ isTemporalExpressionWithinNotBoolean.getString());
			}
		}

		if (temporalPrimaryExpr.getOperator() != null && temporalPrimaryExpr.getOperator().equals(MINUS)) {
			BooleanAndString isTemporalExpressionWithinMinusNumeric = TypeSystemUtils
					.isNumericExpression(temporalPrimaryExpr.getTpe());
			if (!isTemporalExpressionWithinMinusNumeric.getBoolean()) {
				return new TypeCheckError(SpectraPackage.Literals.TEMPORAL_PRIMARY_EXPR__OPERATOR,
						IssueMessages.MINUS_EXPR_ARGUMENT_MUST_BE_NUMERIC + ": "
								+ isTemporalExpressionWithinMinusNumeric.getString());
			}
		}
		if (temporalPrimaryExpr.getOperator() != null && temporalPrimaryExpr.getOperator().equals(NEXT)) {
			if (isHasNestedNextExpression(temporalPrimaryExpr.getTemporalExpression())) {
				return new TypeCheckError(SpectraPackage.Literals.TEMPORAL_PRIMARY_EXPR__OPERATOR,
						IssueMessages.CANT_PRIME_MORE_THAN_ONCE);
			}
		}
		if (temporalPrimaryExpr.getPointer() != null && temporalPrimaryExpr.getIndex() != null
				&& temporalPrimaryExpr.getIndex().size() > 0) {
			// a pointer + [] found
			if (temporalPrimaryExpr.getPointer() instanceof TypeConstant
					|| temporalPrimaryExpr.getPointer() instanceof PatternParam
					|| temporalPrimaryExpr.getPointer() instanceof Monitor
					|| temporalPrimaryExpr.getPointer() instanceof Counter) {
				return new TypeCheckError(SpectraPackage.Literals.TEMPORAL_PRIMARY_EXPR__POINTER,
						IssueMessages.NOT_AN_ARRAY);
			} else if (temporalPrimaryExpr.getPointer() instanceof VarDecl) {
				VarDecl varDecl = (VarDecl) temporalPrimaryExpr.getPointer();
				VarType varType = TypeSystemUtils.getVarType(varDecl.getType());
				if (varType.getDimensions() == null || varType.getDimensions().size() == 0) {
					return new TypeCheckError(SpectraPackage.Literals.TEMPORAL_PRIMARY_EXPR__POINTER,
							IssueMessages.NOT_AN_ARRAY);
				}
			} else if (temporalPrimaryExpr.getPointer() instanceof TypedParam) {
				TypedParam typedParam = (TypedParam) temporalPrimaryExpr.getPointer();
				VarType varType = TypeSystemUtils.getVarType(typedParam.getType());
				if (varType.getDimensions() == null || varType.getDimensions().size() == 0) {
					return new TypeCheckError(SpectraPackage.Literals.TEMPORAL_PRIMARY_EXPR__POINTER,
							IssueMessages.NOT_AN_ARRAY);
				}
			} else if (temporalPrimaryExpr.getPointer() instanceof DefineDecl) {
				DefineDecl defineDecl = (DefineDecl) temporalPrimaryExpr.getPointer();
				if (defineDecl.getInnerArray() == null) {
					return new TypeCheckError(SpectraPackage.Literals.TEMPORAL_PRIMARY_EXPR__POINTER,
							IssueMessages.NOT_AN_ARRAY);
				}
			}
		}
		
		if (temporalPrimaryExpr.getOperator() != null && TypeSystemUtils.ARRAY_FUNCTIONS.contains(temporalPrimaryExpr.getOperator())) {
			
			VarDecl varDecl = (VarDecl) temporalPrimaryExpr.getPointer();
			VarType varType = TypeSystemUtils.getVarType(varDecl.getType());
			
			if (varType.getDimensions() == null || varType.getDimensions().size() == 0) {
				return new TypeCheckError(SpectraPackage.Literals.TEMPORAL_PRIMARY_EXPR__OPERATOR,
						IssueMessages.FUNCTION_APPLY_ONLY_ON_ARRAY);
			} else if (TypeSystemUtils.isVarTypeBoolean(varType) && !TypeSystemUtils.BOOLEAN_ARRAY_FUNCTIONS.contains(temporalPrimaryExpr.getOperator())) {
				return new TypeCheckError(SpectraPackage.Literals.TEMPORAL_PRIMARY_EXPR__OPERATOR,
						IssueMessages.FUNCTION_CANT_APPLY_ON_BOOLEAN_ARRAY);
			} else if (!TypeSystemUtils.isVarTypeBoolean(varType) && !TypeSystemUtils.NUMERIC_ARRAY_FUNCTIONS.contains(temporalPrimaryExpr.getOperator())) {
				return new TypeCheckError(SpectraPackage.Literals.TEMPORAL_PRIMARY_EXPR__OPERATOR,
						IssueMessages.FUNCTION_CAN_APPLY_ONlY_ON_BOOLEAN_ARRAY);
			}
			
		} 

		issue = checkDimensionsMatch(temporalPrimaryExpr);
		if (issue != null) {
			return issue;
		}

		issue = TypeSystemUtils.typeCheckCorrectWayOfAccessingArray(temporalPrimaryExpr,
				SpectraPackage.Literals.TEMPORAL_PRIMARY_EXPR__POINTER);

		return issue;
	}

	private static boolean isHasNestedNextExpression(TemporalExpression temporalExpression) {
		if (temporalExpression instanceof TemporalPrimaryExpr) {
			TemporalPrimaryExpr temporalPrimaryExpr = (TemporalPrimaryExpr) temporalExpression;
			if (temporalPrimaryExpr.getOperator() != null && temporalPrimaryExpr.getOperator().equals(NEXT)) {
				return true;
			}
		}
		List<TemporalPrimaryExpr> tpeChildList = EcoreUtil2.getAllContentsOfType(temporalExpression,
				TemporalPrimaryExpr.class);
		if (tpeChildList != null && tpeChildList.size() > 0) {
			for (TemporalPrimaryExpr tpe : tpeChildList) {
				if (tpe.getOperator() != null && tpe.getOperator().equals(NEXT)) {
					return true;
				}
			}
		}
		return false;
	}

	private static TypeCheckIssue checkNoCallToPattern(TemporalPrimaryExpr temporalPrimaryExpr) {
		List<TemporalExpression> temporalExpressionsList = temporalPrimaryExpr.getPredPattParams();
		for (TemporalExpression temporalExpression : temporalExpressionsList) {
			if (temporalExpression instanceof TemporalPrimaryExpr) {
				TemporalPrimaryExpr temporalPE = (TemporalPrimaryExpr) temporalExpression;
				if (temporalPE.getPredPatt() != null && temporalPE.getPredPatt() instanceof Pattern) {
					return new TypeCheckError(SpectraPackage.Literals.TEMPORAL_PRIMARY_EXPR__PRED_PATT_PARAMS,
							IssueMessages.CANT_CALL_A_PATTERN_FROM_HERE);
				}
			}
		}
		return null;
	}

	private static TypeCheckIssue checkDimensionsMatch(TemporalPrimaryExpr temporalPrimaryExpr) {
		List<TemporalExpression> specifiedLocations = temporalPrimaryExpr.getIndex();
//		List<List<Integer>> specifiedLocations;
//		try {
//			specifiedLocations = TypeSystemUtils.calcArithmeticExpressions(temporalPrimaryExpr.getIndex());
//		} catch (NotArithmeticExpressionException e) {
//			return new TypeCheckError(SpectraPackage.Literals.TEMPORAL_PRIMARY_EXPR__INDEX,
//					IssueMessages.INVALID_INDEX_EXP);
//		}
		VarType varType = null;
		List<Integer> varDimensions = null;
		if (temporalPrimaryExpr.getPointer() instanceof VarDecl) {
			VarDecl varDecl = (VarDecl) temporalPrimaryExpr.getPointer();
			varType = TypeSystemUtils.getVarType(varDecl.getType());
			varDimensions = TypeSystemUtils.sizeDefineToInt(varType.getDimensions());
		} else if (temporalPrimaryExpr.getPointer() instanceof TypedParam) {
			TypedParam typedParam = (TypedParam) temporalPrimaryExpr.getPointer();
			varType = TypeSystemUtils.getVarType(typedParam.getType());
			varDimensions = TypeSystemUtils.sizeDefineToInt(varType.getDimensions());
		} else if (temporalPrimaryExpr.getPointer() instanceof DefineDecl) {
			DefineDecl defineDecl = (DefineDecl) temporalPrimaryExpr.getPointer();
			varDimensions = TypeSystemUtils.sizeDefineToInt(defineDecl.getDimensions());
		}

		
		// declared var is an array
		if (varDimensions != null && varDimensions.size() > 0 && specifiedLocations != null
				&& specifiedLocations.size() > 0) {
			if (specifiedLocations.size() != varDimensions.size()) {
				return new TypeCheckError(SpectraPackage.Literals.TEMPORAL_PRIMARY_EXPR__INDEX,
						IssueMessages.DIMENSIONS_DONT_MATCH);
			}
			for(int i = 0; i < specifiedLocations.size(); i++) {
				if (specifiedLocations.get(i) instanceof Constant) {
					Constant c = (Constant) specifiedLocations.get(i);
					
					// Basic bounds check if index is constant
					if(c.getIntegerValue() < 0 || c.getIntegerValue() >= varDimensions.get(i)) {
						return new TypeCheckError(SpectraPackage.Literals.TEMPORAL_PRIMARY_EXPR__INDEX,
								IssueMessages.INDEX_OUT_OF_BOUNDS);
					}
				}
//				if(specifiedLocations.get(i).get(0) < 0 || specifiedLocations.get(i).get(0) >= varDimensions.get(i)) {
//					return new TypeCheckError(SpectraPackage.Literals.TEMPORAL_PRIMARY_EXPR__INDEX,
//							IssueMessages.INDEX_OUT_OF_BOUNDS);
//				}
//				if(specifiedLocations.get(i).size() == 2) { //we have an index which is a reference to an integer variable, so we check that
//					//the upper bound of its domain is valid w.r.t. the size of i-th dimension of the array
//					if(specifiedLocations.get(i).get(1) >= varDimensions.get(i)) {
//						return new TypeCheckError(SpectraPackage.Literals.TEMPORAL_PRIMARY_EXPR__INDEX,
//								IssueMessages.INDEX_OUT_OF_BOUNDS);
//					}
//				}
			}
		}
		return null;
	}

	private static TypeCheckIssue checkLegalParamsOfPattern(TemporalPrimaryExpr temporalPrimaryExpr) {
		// check if all passed params are booleans
		int argNumber = 1;
		for (TemporalExpression temporalExpression : temporalPrimaryExpr.getPredPattParams()) {
			BooleanAndString isTemporalExpressionBoolean = TypeSystemUtils.isBooleanExpression(temporalExpression);
			if (!isTemporalExpressionBoolean.getBoolean()) {
				return new TypeCheckError(SpectraPackage.Literals.TEMPORAL_PRIMARY_EXPR__PRED_PATT,
						IssueMessages.PATTERN_PARAMS_ARE_BOOLEAN + ": " + isTemporalExpressionBoolean.getString()
						+ " in argument number: " + argNumber);
			}
			argNumber++;
		}
		return null;
	}

	private static TypeCheckIssue checkPatternIsAloneInFormula(TemporalPrimaryExpr temporalPrimaryExpression) {
		if (!(temporalPrimaryExpression.eContainer() instanceof LTLAsm)
				&& !(temporalPrimaryExpression.eContainer() instanceof LTLGar)) {
			return new TypeCheckError(SpectraPackage.Literals.TEMPORAL_PRIMARY_EXPR__PRED_PATT,
					IssueMessages.PATTERN_MUST_BE_ALONE);

		}
		return null;
	}

	/**
	 * @pre has a pointer to a predicate
	 */
	private static TypeCheckIssue checkLegalParamsOfPredicate(TemporalPrimaryExpr temporalPrimaryExpr) {
		Predicate referencedPredicate = (Predicate) temporalPrimaryExpr.getPredPatt();
		List<TypedParam> refPredSignatureParamas = referencedPredicate.getParams().getParams();
		List<TemporalExpression> passedParams = temporalPrimaryExpr.getPredPattParams();
		if (refPredSignatureParamas.size() != passedParams.size()) {
			return null;
		}
		for (int i = 0; i < passedParams.size(); i++) {
			TypedParam currSignatureParam = refPredSignatureParamas.get(i);
			VarType currSignatureParamType = TypeSystemUtils.getVarType(currSignatureParam.getType());
			TemporalExpression currPassedParam = passedParams.get(i);

			if (currPassedParam instanceof Constant) {
				Constant constant = (Constant) currPassedParam;
				if (constant.getBooleanValue() != null) {
					// boolean
					if (!TypeSystemUtils.isVarTypeBoolean(currSignatureParamType)) {
						return new TypeCheckError(SpectraPackage.Literals.TEMPORAL_PRIMARY_EXPR__PRED_PATT,
								IssueMessages.WRONG_PARAM_TYPE + " of argument in location: " + (i + 1));
					}
				} else {
					// numeric
					if (!TypeSystemUtils.isVarTypeNumeric(currSignatureParamType)) {
						return new TypeCheckError(SpectraPackage.Literals.TEMPORAL_PRIMARY_EXPR__PRED_PATT,
								IssueMessages.WRONG_PARAM_TYPE + " of argument in location: " + (i + 1));
					}
				}
			}

			List<TemporalPrimaryExpr> tpesListCurrPassedParam = TypeSystemUtils
					.getAllTemporaryPrimaryExprs(currPassedParam, true);
			for (TemporalPrimaryExpr tpeWithPointerNotNull : tpesListCurrPassedParam) {
				if (tpeWithPointerNotNull.getPointer() instanceof VarDecl) {
					VarType currPassedVarType = TypeSystemUtils
							.getVarType(((VarDecl) tpeWithPointerNotNull.getPointer()).getType());
					if (tpeWithPointerNotNull.getIndex() != null && tpeWithPointerNotNull.getIndex().size() > 0) {
						// need to update the dimensions. i.e. if arr[2][4] is accessed as arr[0],
						// dimensions need to be {4} and not {2,4}
						currPassedVarType = TypeSystemUtils.updateDimensionsAccordingToTheWayArrIsAccessed(
								currPassedVarType, tpeWithPointerNotNull.getIndex());
					}
					if (!TypeSystemUtils.isSameVarType(currPassedVarType, currSignatureParamType)) {
						return new TypeCheckError(SpectraPackage.Literals.TEMPORAL_PRIMARY_EXPR__PRED_PATT,
								IssueMessages.WRONG_PARAM_TYPE + " of argument in location: " + (i + 1));
					}
				}

				else if (tpeWithPointerNotNull.getPointer() instanceof DomainVarDecl) {
					VarType currPassedVarType = TypeSystemUtils
							.getVarType(((DomainVarDecl) tpeWithPointerNotNull.getPointer()).getDomainType());
					if (!TypeSystemUtils.isSameVarType(currPassedVarType, currSignatureParamType)) {
						return new TypeCheckError(SpectraPackage.Literals.TEMPORAL_PRIMARY_EXPR__PRED_PATT,
								IssueMessages.WRONG_PARAM_TYPE + " of argument in location: " + (i + 1));
					}
				}

				else if (tpeWithPointerNotNull.getPointer() instanceof DefineDecl) {
					// defines are booleans
					if (!TypeSystemUtils.isVarTypeBoolean(currSignatureParamType)) {
						return new TypeCheckError(SpectraPackage.Literals.TEMPORAL_PRIMARY_EXPR__PRED_PATT,
								IssueMessages.WRONG_PARAM_TYPE + " of argument in location: " + (i + 1));
					}
				} else if (tpeWithPointerNotNull.getPointer() instanceof TypedParam) {
					VarType currPassedVarType = TypeSystemUtils
							.getVarType(((TypedParam) tpeWithPointerNotNull.getPointer()).getType());
					if (!TypeSystemUtils.isSameVarType(currPassedVarType, currSignatureParamType)) {
						return new TypeCheckError(SpectraPackage.Literals.TEMPORAL_PRIMARY_EXPR__PRED_PATT,
								IssueMessages.WRONG_PARAM_TYPE + " of argument in location: " + (i + 1));
					}
				} else if (tpeWithPointerNotNull.getPointer() instanceof TypeConstant) {
					if (currSignatureParamType.getConst() != null) // check if the signature type is an enum
					{
						List<String> permittedTypeConstantsNames = new ArrayList<>();
						for (TypeConstant tc : currSignatureParamType.getConst()) {
							permittedTypeConstantsNames.add(tc.getName());
						}
						TypeConstant passedTypeConstant = (TypeConstant) tpeWithPointerNotNull.getPointer();
						if (!permittedTypeConstantsNames.contains(passedTypeConstant.getName())) {
							return new TypeCheckError(SpectraPackage.Literals.TEMPORAL_PRIMARY_EXPR__PRED_PATT,
									IssueMessages.WRONG_PARAM_TYPE + " of argument in location: " + (i + 1));
						}
					}
				}
			}
		}
		return null;
	}

	private static TypeCheckIssue chekNumberOfArgumentsMatch(TemporalPrimaryExpr temporalPrimaryExpr) {
		int numOfPassedArguments;
		if(temporalPrimaryExpr.getPredPattParams() != null) {
			numOfPassedArguments = temporalPrimaryExpr.getPredPattParams().size();			
		}
		else {
			numOfPassedArguments = 0;
		}

		int numOfArgumentsInSignature = -1;

		if (temporalPrimaryExpr.getPredPatt() instanceof Predicate) {
			Predicate predicate = (Predicate) temporalPrimaryExpr.getPredPatt();
			TypedParamList predicateParamsList = predicate.getParams();
			if(predicateParamsList != null) {
				numOfArgumentsInSignature = predicateParamsList.getParams().size();
			}
			else {
				numOfArgumentsInSignature = 0;
			}
		} else if (temporalPrimaryExpr.getPredPatt() instanceof Pattern) {
			Pattern pattern = (Pattern) temporalPrimaryExpr.getPredPatt();
			PatternParamList patternParamsList = pattern.getParams();
			numOfArgumentsInSignature = patternParamsList.getParams().size();
		}

		if (numOfArgumentsInSignature == -1) {
			// Shouldn't happen
			return null;
		}

		if (numOfPassedArguments != numOfArgumentsInSignature) {
			return new TypeCheckError(SpectraPackage.Literals.TEMPORAL_PRIMARY_EXPR__PRED_PATT,
					IssueMessages.NUM_ARGS_DOESNT_MATCH);
		} else {
			return null;
		}
	}

}
