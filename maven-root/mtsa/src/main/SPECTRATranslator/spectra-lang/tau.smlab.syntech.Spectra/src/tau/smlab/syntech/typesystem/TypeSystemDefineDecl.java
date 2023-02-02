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
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.EcoreUtil2;

import tau.smlab.syntech.spectra.Constant;
import tau.smlab.syntech.spectra.DefineArray;
import tau.smlab.syntech.spectra.DefineDecl;
import tau.smlab.syntech.spectra.Pattern;
import tau.smlab.syntech.spectra.SizeDefineDecl;
import tau.smlab.syntech.spectra.SpectraPackage;
import tau.smlab.syntech.spectra.TemporalExpression;
import tau.smlab.syntech.spectra.TemporalPrimaryExpr;

public class TypeSystemDefineDecl {

	public static TypeCheckIssue typeCheck(DefineDecl defineDecl) {

		if (defineDecl.getSimpleExpr() != null) {
			TypeCheckError error = isContainsCycles(defineDecl.getSimpleExpr(), defineDecl.getName(), new ArrayList<String>());
			if (error != null) {
				return error;
			}
			
			if (defineDecl.getSimpleExpr() instanceof TemporalPrimaryExpr) {
				TemporalPrimaryExpr temporalPrimaryExpr = (TemporalPrimaryExpr) defineDecl.getSimpleExpr();
				if (temporalPrimaryExpr.getPredPatt() != null && temporalPrimaryExpr.getPredPatt() instanceof Pattern) {
					return new TypeCheckError(SpectraPackage.Literals.DEFINE_DECL__SIMPLE_EXPR,
							IssueMessages.CANT_CALL_A_PATTERN_FROM_HERE);
				}
			}
			
		} else {
			
			for(SizeDefineDecl sdd : defineDecl.getDimensions()) {
				if(TypeSystemUtils.sizeDefineToInt(sdd) <= 0) {
					return new TypeCheckError(SpectraPackage.Literals.DEFINE_DECL__DIMENSIONS,
							IssueMessages.NON_INT_DEFINE_DIMEMSION);
				}
			}
			
			TypeCheckError error = checkValidityAndDimensions(defineDecl.getInnerArray(), TypeSystemUtils.sizeDefineToInt(defineDecl.getDimensions()), 0);
			if (error != null) {
				return error;
			}
			
			error = checkCycles(defineDecl.getInnerArray(), defineDecl.getName());
			if (error != null) {
				return error;
			}
			
			if (DefineType.BOTH.equals(checkType(defineDecl.getInnerArray()))) {
				return new TypeCheckError(SpectraPackage.Literals.DEFINE_DECL__SIMPLE_EXPR,
						IssueMessages.DEFINEDECL_DIFFERENT_TYPES);
			}
		}

		// check if defineDecl isn't used
		// NOTE: Assuming no other files can acces defineDecl
		Collection<Setting> references = EcoreUtil.UsageCrossReferencer.find(defineDecl, defineDecl.eResource());
		if (references == null || references.size() == 0) {
			return new TypeCheckWarning(SpectraPackage.Literals.DEFINE_DECL.getEIDAttribute(),
					IssueMessages.DEFINEDECL_NOT_USED + ": " + defineDecl.getName());
		}
		return null;
	}
	
	private enum DefineType {
		NUMERIC, BOOLEAN, BOTH;
	}
	
	
	private static DefineType checkType(DefineArray defArray) {

		DefineType returnType = null;
		for (DefineArray innerArray : defArray.getInnerArrays()) {
			DefineType type = checkType(innerArray);
			
			if (DefineType.BOTH.equals(type) || 
					(DefineType.BOOLEAN.equals(type) && DefineType.NUMERIC.equals(returnType)) ||
					(DefineType.NUMERIC.equals(type) && DefineType.BOOLEAN.equals(returnType))) {
				return DefineType.BOTH;
			}
			
			returnType = type;
		}
		
		for (TemporalExpression exp : defArray.getSimpleExprs()) {
			BooleanAndString isBoolean = TypeSystemUtils.isBooleanExpression(exp);
			if (isBoolean.getBoolean()) {
				if (DefineType.NUMERIC.equals(returnType)) {
					return DefineType.BOTH;
				}
				
				returnType = DefineType.BOOLEAN;	
			}
			BooleanAndString isNumeric = TypeSystemUtils.isNumericExpression(exp);
			if (isNumeric.getBoolean()) {
				if (DefineType.BOOLEAN.equals(returnType)) {
					return DefineType.BOTH;
				}
					
				returnType = DefineType.NUMERIC;
			}
		}

		return returnType;
	}
	
	private static TypeCheckError checkCycles(DefineArray defArray, String name) {
		
		int index = 0;
		for (DefineArray innerArray : defArray.getInnerArrays()) {
			TypeCheckError error = checkCycles(innerArray, name + "[" + index + "]");
			if (error != null) {
				return error;
			}
			index++;
		}
		
		index = 0;
		for (TemporalExpression exp : defArray.getSimpleExprs()) {
			TypeCheckError error = isContainsCycles(exp, name + "[" + index + "]", new ArrayList<String>());
			if (error != null) {
				return error;
			}
			index++;
		}
		
		return null;
	}
	
	private static TypeCheckError checkValidityAndDimensions(DefineArray defArray, List<Integer> dimensions, int dimIndex) {
		
		if (dimIndex == dimensions.size() - 1) {
			
			if (defArray.getSimpleExprs() == null || defArray.getSimpleExprs().size() == 0) {
				return new TypeCheckError(SpectraPackage.Literals.DEFINE_DECL.getEIDAttribute(),
						IssueMessages.DEFINEDECL_INDEXES_DONT_MATCH);
			}
			
			if (defArray.getSimpleExprs().size() != dimensions.get(dimIndex)) {
				return new TypeCheckError(SpectraPackage.Literals.DEFINE_DECL.getEIDAttribute(),
						IssueMessages.DEFINEDECL_INDEXES_DONT_MATCH);
			}
			
			for (TemporalExpression exp : defArray.getSimpleExprs()) {
				if (exp instanceof TemporalPrimaryExpr) {
					TemporalPrimaryExpr temporalPrimaryExpr = (TemporalPrimaryExpr) exp;
					if (temporalPrimaryExpr.getPredPatt() != null && temporalPrimaryExpr.getPredPatt() instanceof Pattern) {
						return new TypeCheckError(SpectraPackage.Literals.DEFINE_DECL.getEIDAttribute(),
								IssueMessages.CANT_CALL_A_PATTERN_FROM_HERE);
					}
				}
			}
			
		} else {
			
			if (defArray.getInnerArrays() == null || defArray.getInnerArrays().size() == 0) {
				return new TypeCheckError(SpectraPackage.Literals.DEFINE_DECL.getEIDAttribute(),
						IssueMessages.DEFINEDECL_INDEXES_DONT_MATCH);
			}
			
			if (defArray.getInnerArrays().size() != dimensions.get(dimIndex)) {
				return new TypeCheckError(SpectraPackage.Literals.DEFINE_DECL.getEIDAttribute(),
						IssueMessages.DEFINEDECL_INDEXES_DONT_MATCH);
			}
			
			for (DefineArray innerArray : defArray.getInnerArrays()) {
				TypeCheckError error = checkValidityAndDimensions(innerArray, dimensions, dimIndex + 1);
				if (error != null) {
					return error;
				}
			}
		}
		
		return null;
	}
	

	private static TypeCheckError isContainsCycles(TemporalExpression exp, String name, ArrayList<String> alreadySeenDefines) {
		if (alreadySeenDefines.contains(name)) {
			return new TypeCheckError(SpectraPackage.Literals.DEFINE_DECL.getEIDAttribute(),
					IssueMessages.DEFINEDECL_CONTAINS_CYCLES);
		}
		alreadySeenDefines.add(name); // add the current one

		// case1: this define is a simple expression
		if (exp instanceof TemporalPrimaryExpr) {
			TemporalPrimaryExpr temporalPrimaryExpr = (TemporalPrimaryExpr) exp;
			// check only the case where we directly reference a define
			if (temporalPrimaryExpr.getPointer() != null && temporalPrimaryExpr.getPointer() instanceof DefineDecl) {
				DefineDecl nestedDefineDecl = (DefineDecl) temporalPrimaryExpr.getPointer();
				
				if (nestedDefineDecl.getSimpleExpr() != null) {
					return isContainsCycles(nestedDefineDecl.getSimpleExpr(), nestedDefineDecl.getName(), alreadySeenDefines);
				} else {
					
					// Assume there is index
					// Index dimensions matching is checked somewhere else
					if (temporalPrimaryExpr.getIndex() != null && temporalPrimaryExpr.getIndex().size() > 0) {
						StringBuilder newName = new StringBuilder(nestedDefineDecl.getName());
						TempExpOrError expOrError = checkDefineReference(nestedDefineDecl, temporalPrimaryExpr.getIndex(), newName);
						if (expOrError.error != null) {
							return expOrError.error;
						}
						return isContainsCycles(expOrError.exp, newName.toString(), alreadySeenDefines);
					}
				}
			}
		}


		// case2: check all subexpressions (inefficient)? Hopefully only the topmost.
		List<TemporalPrimaryExpr> temporalsPEnestedInExpression = EcoreUtil2.getAllContentsOfType(exp, TemporalPrimaryExpr.class);
		if (temporalsPEnestedInExpression != null && temporalsPEnestedInExpression.size() > 0) {
			for (TemporalPrimaryExpr tpe : temporalsPEnestedInExpression) {
				// for every define continue cycle chek
				if (tpe.getPointer() != null && tpe.getPointer() instanceof DefineDecl) {
					DefineDecl nestedDefineDecl = (DefineDecl) tpe.getPointer();
					// here we need to clone to descend current branch only
					ArrayList<String> copy = new ArrayList<>();
					copy.addAll(alreadySeenDefines);
					
					
					if (nestedDefineDecl.getSimpleExpr() != null) {
						TypeCheckError error = isContainsCycles(nestedDefineDecl.getSimpleExpr(), nestedDefineDecl.getName(), copy);
						if (error != null) {
							return error;
						}
					} else {
						
						// Assume there is index
						// Index dimensions matching is checked somewhere else
						if (tpe.getIndex() != null && tpe.getIndex().size() > 0) {
							StringBuilder newName = new StringBuilder(nestedDefineDecl.getName());
							TempExpOrError expOrError = checkDefineReference(nestedDefineDecl, tpe.getIndex(), newName);
							if (expOrError.error != null) {
								return expOrError.error;
							}
							TypeCheckError error = isContainsCycles(expOrError.exp, newName.toString(), copy);
							if (error != null) {
								return error;
							}
						}
					}
				}
			}
		}

		return null;
	}
	
	private static TempExpOrError checkDefineReference(DefineDecl defineDecl, List<TemporalExpression> indexes, StringBuilder name) {
		DefineArray innerArray = defineDecl.getInnerArray();
		int indexOfIndexes = 0;
		TemporalExpression exp = null;
		
		for (TemporalExpression indExp : indexes) {
			if (!(indExp instanceof Constant)) {
				return new TempExpOrError(new TypeCheckError(SpectraPackage.Literals.DEFINE_DECL.getEIDAttribute(),
						IssueMessages.DEFINEDECL_NON_CONSTANT_INDEX));
			}
			
			int indValue = ((Constant)indexes.get(indexOfIndexes)).getIntegerValue();
			
			if (innerArray.getInnerArrays() != null && innerArray.getInnerArrays().size() > 0) {
				if (innerArray.getInnerArrays().size() <= indValue || indValue < 0) {
					return new TempExpOrError(new TypeCheckError(SpectraPackage.Literals.DEFINE_DECL.getEIDAttribute(),
							IssueMessages.INDEX_OUT_OF_BOUNDS));
				}
				
				innerArray = innerArray.getInnerArrays().get(indValue);
			} else {
				if (indexOfIndexes < indexes.size() - 1) {
					return new TempExpOrError(new TypeCheckError(SpectraPackage.Literals.DEFINE_DECL.getEIDAttribute(),
							IssueMessages.DIMENSIONS_DONT_MATCH));
				}
				if (innerArray.getSimpleExprs().size() <= indValue || indValue < 0) {
					return new TempExpOrError(new TypeCheckError(SpectraPackage.Literals.DEFINE_DECL.getEIDAttribute(),
							IssueMessages.INDEX_OUT_OF_BOUNDS));
				}
				exp = innerArray.getSimpleExprs().get(indValue);
			}
			name.append("[" + indValue + "]");
			indexOfIndexes++;
		}
		
		if (exp == null) {
			return new TempExpOrError(new TypeCheckError(SpectraPackage.Literals.DEFINE_DECL.getEIDAttribute(),
					IssueMessages.DIMENSIONS_DONT_MATCH));
		}
		
		return new TempExpOrError(exp);
	}
	
	private static class TempExpOrError {
		TypeCheckError error;
		TemporalExpression exp;
		
		TempExpOrError(TypeCheckError error) {
			this.error = error;
		}
		
		TempExpOrError(TemporalExpression exp) {
			this.exp = exp;
		}
	}
}