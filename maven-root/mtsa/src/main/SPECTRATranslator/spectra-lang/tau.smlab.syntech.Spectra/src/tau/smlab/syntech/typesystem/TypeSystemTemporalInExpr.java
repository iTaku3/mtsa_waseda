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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import tau.smlab.syntech.spectra.DomainVarDecl;
import tau.smlab.syntech.spectra.Monitor;
import tau.smlab.syntech.spectra.SpectraPackage;
import tau.smlab.syntech.spectra.TemporalInExpr;
import tau.smlab.syntech.spectra.TemporalPrimaryExpr;
import tau.smlab.syntech.spectra.TypedParam;
import tau.smlab.syntech.spectra.ValueInRange;
import tau.smlab.syntech.spectra.VarDecl;
import tau.smlab.syntech.spectra.VarType;
import tau.smlab.syntech.spectra.impl.TemporalPrimaryExprImpl;


public class TypeSystemTemporalInExpr {
	
	private static final String CAPITAL_TRUE = "TRUE";
	private static final String CAPITAL_FALSE = "FALSE";
	
	public static TypeCheckIssue typeCheck(TemporalInExpr temporalInlExpr) {
		if(temporalInlExpr.getLeft() != null && 
				temporalInlExpr.getLeft() instanceof TemporalPrimaryExprImpl &&
				((TemporalPrimaryExprImpl)temporalInlExpr.getLeft()).getPointer() != null &&						
				temporalInlExpr.getValues() != null) {
			TypeCheckIssue issues = null;
			
			VarType varType = getVarType((TemporalPrimaryExpr) temporalInlExpr.getLeft());
			
			issues = validateLeftIntegerTypedVariable(temporalInlExpr, varType);
			
			if (issues != null) {				
				return issues;
			}
			
			issues = validateLeftEnumTypedVariable(temporalInlExpr, varType);
			
			if (issues != null) {				
				return issues;
			}
				
			issues = validateLeftBooleanTypedVariable(temporalInlExpr);
				
			if (issues != null) {				
				return issues;
			}			
		}
		
		return null;
	}
	
	private static TypeCheckIssue validateLeftBooleanTypedVariable(TemporalInExpr temporalInlExpr) {
			BooleanAndString isCurrentTemporalExpressionBoolean = TypeSystemUtils.isBooleanExpression(temporalInlExpr.getLeft());
		    if (isCurrentTemporalExpressionBoolean.getBoolean())
		    {
				Set<String> seenVals = new HashSet<>();
				for(ValueInRange value : temporalInlExpr.getValues()) {
					if(value.getBooleanValue() == null || (!CAPITAL_TRUE.equalsIgnoreCase(value.getBooleanValue()) &&
							!CAPITAL_FALSE.equalsIgnoreCase(value.getBooleanValue()))) {
						//A value that is NOT a constant Boolean value
						return new TypeCheckError(SpectraPackage.Literals.TEMPORAL_IN_EXPR__VALUES,
								IssueMessages.IN_LEFT_VAR_DOM);
					}
					if(seenVals.contains(value.getBooleanValue())) {
						return new TypeCheckError(SpectraPackage.Literals.TEMPORAL_IN_EXPR__VALUES,
								IssueMessages.IN_LEFT_VAR_DOM + ": " + value.getBooleanValue() + " appears more than once");
					}
					seenVals.add(value.getBooleanValue());
				}
				if(seenVals.size() == 2) {
					return handleTrivialRangePredicate(temporalInlExpr);
				}
		    }
		
		return null;
	}

	private static TypeCheckIssue validateLeftEnumTypedVariable(TemporalInExpr temporalInlExpr, VarType leftVarType) {
		Set<String> seenVals;
		if(leftVarType != null && leftVarType.getConst() != null && !leftVarType.getConst().isEmpty()) {
			List<String> domainValues = TypeSystemUtils.getPermittedTypeConstants(leftVarType);
			seenVals = new HashSet<>();
			for(ValueInRange value : temporalInlExpr.getValues()) {
				if(value.getConst() == null || !domainValues.contains(value.getConst().getName())) {
					//A value NOT in the domain of the enum variable
					return new TypeCheckError(SpectraPackage.Literals.TEMPORAL_IN_EXPR__VALUES,
							IssueMessages.IN_LEFT_VAR_DOM);
				}
				if(seenVals.contains(value.getConst().getName())) {
					return new TypeCheckError(SpectraPackage.Literals.TEMPORAL_IN_EXPR__VALUES,
							IssueMessages.IN_LEFT_VAR_DOM + ": " + value.getConst().getName() + " appears more than once");
				}
				seenVals.add(value.getConst().getName());
			}
			if(seenVals.size() == domainValues.size()) {
				return handleTrivialRangePredicate(temporalInlExpr);
			}
		}		
		return null;
	}

	private static VarType getVarType(TemporalPrimaryExpr temporalPrimaryExpr) {
		if (temporalPrimaryExpr.getPointer() instanceof VarDecl) {
			VarDecl varDecl = (VarDecl) temporalPrimaryExpr.getPointer();
			return varDecl.getType();
		}
		
		if (temporalPrimaryExpr.getPointer() instanceof Monitor) {
			Monitor mon = (Monitor) temporalPrimaryExpr.getPointer();
			return mon.getType();
		}
		
		if (temporalPrimaryExpr.getPointer() instanceof TypedParam) {
			TypedParam typedParam = (TypedParam) temporalPrimaryExpr.getPointer();
			return typedParam.getType();
		}
		
		if (temporalPrimaryExpr.getPointer() instanceof DomainVarDecl) {
			DomainVarDecl domainVarDecl = (DomainVarDecl) temporalPrimaryExpr.getPointer();
			return domainVarDecl.getDomainType();
		}
		
		return null;
	}
	
	private static TypeCheckIssue validateLeftIntegerTypedVariable(TemporalInExpr temporalInlExpr, VarType leftVarType) {
		BooleanAndString isCurrentTemporalExpressionNumeric = TypeSystemUtils.isNumericExpression(temporalInlExpr.getLeft());
	    if (isCurrentTemporalExpressionNumeric.getBoolean())
	    {
	    	if(leftVarType != null && leftVarType.getSubr() != null) {
				int from = TypeSystemUtils.sizeDefineToInt(leftVarType.getSubr().getFrom());
				int to = TypeSystemUtils.sizeDefineToInt(leftVarType.getSubr().getTo());
				if(from < 0 || from > to) {
					//Should not happen
					return null;
				}
				Set<Integer> seenIntVals = new HashSet<>();
				int specifiedFrom = -1, specifiedTo = -1;
				for(ValueInRange value : temporalInlExpr.getValues()) {
					if(value.isMulti()) {
						specifiedFrom = value.getFrom();
						specifiedTo = value.getTo();
						if(specifiedTo < specifiedFrom) {
							return new TypeCheckError(SpectraPackage.Literals.TEMPORAL_IN_EXPR__VALUES,
									IssueMessages.IN_LEFT_INVALID_VAL_RANGE + ": " + specifiedFrom + "-" + specifiedTo);
						}
						if(specifiedTo > to || specifiedFrom < from) {
							return new TypeCheckError(SpectraPackage.Literals.TEMPORAL_IN_EXPR__VALUES,
									IssueMessages.IN_LEFT_VAR_DOM);
						}
					}
					else if(value.getConst() != null || value.getBooleanValue() != null || value.getInt() < from ||
							value.getInt() > to) {
						//A value NOT in the domain of the integer variable
						return new TypeCheckError(SpectraPackage.Literals.TEMPORAL_IN_EXPR__VALUES,
								IssueMessages.IN_LEFT_VAR_DOM);
					}

					if(value.isMulti()) {
						for(int intValue = specifiedFrom; intValue <= specifiedTo; intValue++) {
							if(seenIntVals.contains(intValue)) {
								return new TypeCheckError(SpectraPackage.Literals.TEMPORAL_IN_EXPR__VALUES,
										IssueMessages.IN_LEFT_VAL_AT_MOST_ONCE + ": " + intValue + " appears more than once");
							}
							seenIntVals.add(intValue);
						}
					}
					else {
						if(seenIntVals.contains(value.getInt())) {
							return new TypeCheckError(SpectraPackage.Literals.TEMPORAL_IN_EXPR__VALUES,
									IssueMessages.IN_LEFT_VAL_AT_MOST_ONCE + ": " + value.getInt() + " appears more than once");
						}
						seenIntVals.add(value.getInt());
					}
				}
				if(seenIntVals.size() == (to-from+1)) {
					return handleTrivialRangePredicate(temporalInlExpr);
				}
			}
	    }
		return null;
	}
	
	private static TypeCheckIssue handleTrivialRangePredicate(TemporalInExpr temporalInlExpr) {
		if(temporalInlExpr.isNot()) {
			return new TypeCheckError(SpectraPackage.Literals.TEMPORAL_IN_EXPR__VALUES,
					IssueMessages.IN_LEFT_VAL_SET_EQUALS_DOM + ". The domain of " +((TemporalPrimaryExprImpl)temporalInlExpr.getLeft()).getPointer().getName() + " minus the value set is an EMPTY SET (trivially evaluates to false)");
		}
		return new TypeCheckWarning(SpectraPackage.Literals.TEMPORAL_IN_EXPR__VALUES,
				IssueMessages.IN_LEFT_VAL_SET_EQUALS_DOM + ". This set equals the domain of " + ((TemporalPrimaryExprImpl)temporalInlExpr.getLeft()).getPointer().getName() + " (trivially evaluates to true)");
	}
}
