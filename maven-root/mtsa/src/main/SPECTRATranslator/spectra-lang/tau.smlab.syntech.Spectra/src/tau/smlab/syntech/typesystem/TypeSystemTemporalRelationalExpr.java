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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.xtext.EcoreUtil2;

import tau.smlab.syntech.spectra.Constant;
import tau.smlab.syntech.spectra.DomainVarDecl;
import tau.smlab.syntech.spectra.PatternParam;
import tau.smlab.syntech.spectra.SizeDefineDecl;
import tau.smlab.syntech.spectra.SpectraPackage;
import tau.smlab.syntech.spectra.TemporalExpression;
import tau.smlab.syntech.spectra.TemporalPrimaryExpr;
import tau.smlab.syntech.spectra.TemporalRelationalExpr;
import tau.smlab.syntech.spectra.TypeConstant;
import tau.smlab.syntech.spectra.TypedParam;
import tau.smlab.syntech.spectra.VarDecl;
import tau.smlab.syntech.spectra.VarType;

public class TypeSystemTemporalRelationalExpr {

	//TODO temporary implementation. change to grammarAccess injection
	private static final String CAPITAL_TRUE = "TRUE";
	private static final String CAPITAL_FALSE = "FALSE";
	private static final String BOOLEAN = "boolean";

	private static final String REPRESENTATION_ENUM = "REP_ENUM";
	private static final String REPRESENTATION_SUBRANGE = "REP_SUBRANGE";
	public static TypeCheckIssue typeCheck(TemporalRelationalExpr temporalRelationalExpr) {
		if (temporalRelationalExpr.getRight() == null)
		{
			// not supposed to happen
			return null;
		}
		TypeCheckIssue issue = null;
		TemporalExpression leftTemporalExpression = temporalRelationalExpr.getLeft();
		TemporalExpression rightTemporalExpression = temporalRelationalExpr.getRight();

		issue = typeCheckVarDeclvsVarDecl(leftTemporalExpression, rightTemporalExpression);
		if (issue != null)
		{
			return issue;
		}

		issue = typeCheckTypeConstantvsTypeConstant(leftTemporalExpression, rightTemporalExpression);
		if (issue != null)
		{
			return issue;
		}

		issue = typeCheckVarDeclVsTypeConstant(leftTemporalExpression, rightTemporalExpression);
		if (issue != null)
		{
			return issue;
		}
		//check the other direction
		issue = typeCheckVarDeclVsTypeConstant(rightTemporalExpression, leftTemporalExpression);
		if (issue != null)
		{
			return issue;
		}

		// NOTE: Constant, not TypeConstant
		issue = typeCheckVarDeclVsConstant(leftTemporalExpression, rightTemporalExpression);
		if (issue != null)
		{
			return issue;
		}
		// check the other direction
		issue = typeCheckVarDeclVsConstant(rightTemporalExpression, leftTemporalExpression);
		if (issue != null)
		{
			return issue;
		}

		issue = typeCheckTypeConstantVsConstant(leftTemporalExpression, rightTemporalExpression);
		if (issue != null)
		{
			return issue;
		}
		// check the other direction
		issue = typeCheckTypeConstantVsConstant(rightTemporalExpression, leftTemporalExpression);
		if (issue != null)
		{
			return issue;
		}

		issue = typeCheckPatternParam(rightTemporalExpression, leftTemporalExpression);
		if (issue != null)
		{
			return issue;
		}
		//check the other direction
		issue = typeCheckPatternParam(leftTemporalExpression, rightTemporalExpression);
		if (issue != null)
		{
			return issue;
		}

		issue = typeCheckBothSidesBoolean(leftTemporalExpression, rightTemporalExpression);
		if (issue != null)
		{
			return issue;
		}

		issue = typeCheckBothSidesNumeric(leftTemporalExpression, rightTemporalExpression);
		if (issue != null)
		{
			return issue;
		}

//		issue = TypeSystemUtils.typeCheckCorrectWayOfAccessingArray(leftTemporalExpression, rightTemporalExpression, SpectraPackage.Literals.TEMPORAL_RELATIONAL_EXPR__OPERATOR);
//		if (issue != null)
//		{
//			return issue;
//		}
		return null;
	}



	private static TypeCheckIssue typeCheckBothSidesNumeric(TemporalExpression leftTemporalExpression,
			TemporalExpression rightTemporalExpression) {
		BooleanAndString leftBooAndString = TypeSystemUtils.isNumericExpression(leftTemporalExpression);
		BooleanAndString rightBooAndString = TypeSystemUtils.isNumericExpression(rightTemporalExpression);
		boolean isLeftNumeric = leftBooAndString.getBoolean();
		boolean isRightNumeric = rightBooAndString.getBoolean();

		if (isLeftNumeric ^ isRightNumeric)
		{
			String issueMessage = IssueMessages.CANT_COMPARE_BETWEEN_NUMERIC_AND_NON_NUMERIC;
			if (! isLeftNumeric)
			{
				issueMessage += ": Left";
			}
			else
			{
				issueMessage += ": Right";
			}
			issueMessage += " side is not numeric";
			return new TypeCheckError(SpectraPackage.Literals.TEMPORAL_RELATIONAL_EXPR__OPERATOR, issueMessage);
		}
		return null;
	}

	private static TypeCheckIssue typeCheckBothSidesBoolean(TemporalExpression leftTemporalExpression,
			TemporalExpression rightTemporalExpression) {
		BooleanAndString leftBooAndString = TypeSystemUtils.isBooleanExpression(leftTemporalExpression);
		BooleanAndString rightBooAndString = TypeSystemUtils.isBooleanExpression(rightTemporalExpression);
		boolean isLeftBoolean = leftBooAndString.getBoolean();
		boolean isRightBoolean = rightBooAndString.getBoolean();

		if (isLeftBoolean ^ isRightBoolean)
		{
			String issueMessage = IssueMessages.CANT_COMPARE_BETWEEN_BOOLEAN_AND_NON_BOOLEAN;
			if (! isLeftBoolean)
			{
				issueMessage += ": Left";
			}
			else
			{
				issueMessage += ": Right";
			}
			issueMessage += " side is not boolean";
			return new TypeCheckError(SpectraPackage.Literals.TEMPORAL_RELATIONAL_EXPR__OPERATOR, issueMessage);
		}
		return null;
	}

	private static TypeCheckIssue typeCheckPatternParam(TemporalExpression expectedPatternParam,
			TemporalExpression temporalExpression) {
		if (expectedPatternParam instanceof TemporalPrimaryExpr)
		{
			TemporalPrimaryExpr temporalPrimaryExpr = (TemporalPrimaryExpr)expectedPatternParam;
			PatternParam patternParam = TypeSystemUtils.extractPatternParamFromTemporalPrimaryExpr(temporalPrimaryExpr);
			if (patternParam != null)
			{
				BooleanAndString isBoolean = TypeSystemUtils.isBooleanExpression(temporalExpression);
				if (! isBoolean.getBoolean())
				{
					return new TypeCheckError(SpectraPackage.Literals.TEMPORAL_RELATIONAL_EXPR__OPERATOR, IssueMessages.CANT_COMPARE_BOOLEAN_PATT_PARAM_TO_NON_BOOLEAN_EXPR + ": " + isBoolean.getString());
				}
			}
		}
		return null;
	}

	private static TypeCheckIssue typeCheckTypeConstantVsConstant(TemporalExpression expectedTypeConstant,
			TemporalExpression expectedConstant) {

		if (expectedConstant instanceof Constant && expectedTypeConstant instanceof TemporalPrimaryExpr)
		{
			TemporalPrimaryExpr temporalPrimaryExprTypeConstant = (TemporalPrimaryExpr)expectedTypeConstant;
			TypeConstant typeConstant = TypeSystemUtils.extractTypeConstantFromTemporalPrimaryExpr(temporalPrimaryExprTypeConstant);
			if (typeConstant != null)
			{
				return new TypeCheckError(SpectraPackage.Literals.TEMPORAL_RELATIONAL_EXPR__OPERATOR, IssueMessages.CANT_COMPARE_TYPE_CONSTANT_AND_CONSTANT);
			}
		}
		return null;
	}

	//VarDecl or TypedParam
	private static TypeCheckIssue typeCheckVarDeclVsConstant(TemporalExpression expectedVarDeclOrTypedParam,
			TemporalExpression expectedConstant) {
		boolean isConstantBoolean;
		boolean isConstantCapitalBoolean = false;;
		if (expectedConstant instanceof Constant)
		{
			Constant constant = (Constant)expectedConstant;
			if (constant.getBooleanValue() != null)
			{
				if (constant.getBooleanValue().equals(CAPITAL_TRUE) || constant.getBooleanValue().equals(CAPITAL_FALSE))
				{
					isConstantCapitalBoolean = true;
				}
				isConstantBoolean = true;
			}
			else
			{
				isConstantBoolean = false;
			}
		}
		else {return null;}

		if (expectedVarDeclOrTypedParam instanceof TemporalPrimaryExpr)
		{
			TemporalPrimaryExpr temporalPrimaryExp = (TemporalPrimaryExpr)expectedVarDeclOrTypedParam;
			VarType varType = null;
			DomainVarDecl dvd = TypeSystemUtils.extractDomainVarDeclFromTemporalPrimaryExpr(temporalPrimaryExp);
			if (dvd != null)
			{
				varType = dvd.getDomainType();
			}
			else 
			{
				// no VarDecl
				TypedParam typedParam = TypeSystemUtils.extractTypedParamFromTemporalPrimaryExpr(temporalPrimaryExp);
				if (typedParam == null) {return null;} //no VarDecl and no TypedParam
				else
				{
					varType = typedParam.getType();
				}
			}
			String typeOfVarType = getTypeOfVarType(varType);
			if (typeOfVarType == null)
			{
				// not a boolean and not an integer
				return new TypeCheckError(SpectraPackage.Literals.TEMPORAL_RELATIONAL_EXPR__OPERATOR, IssueMessages.ILLEGAL_COMPARISON_BETWEEN_VAR_AND_CONSTANT);
			}
			else if (isConstantBoolean && !typeOfVarType.equals("boolean") || (!isConstantBoolean && !typeOfVarType.equals("integer")))
			{
				return new TypeCheckError(SpectraPackage.Literals.TEMPORAL_RELATIONAL_EXPR__OPERATOR, IssueMessages.ILLEGAL_COMPARISON_BETWEEN_VAR_AND_CONSTANT);        
			}
		}
		else {return null;}

		if (isConstantCapitalBoolean)
		{
			return new TypeCheckError(SpectraPackage.Literals.TEMPORAL_RELATIONAL_EXPR__OPERATOR, IssueMessages.CANT_USE_CAPITAL_BOOLEAN_FOR_VARIABLES);      
		}
		return null;
	}

	private static String getTypeOfVarType(VarType varType)
	{
		return getTypeOfVarType(varType, new HashSet<Integer>());
	}
	private static String getTypeOfVarType(VarType varType, Set<Integer> seenTypes) {
		int hashCode = varType.hashCode();
		if (seenTypes.contains(hashCode))
		{
			// in case of cycles, prevent stack overflow (cycle handled somewhere else)
			return null;
		}
		if (varType.getName() != null && varType.getName().equals(BOOLEAN))
		{
			return "boolean";
		}
		if (varType.getSubr() != null)
		{
			return "integer";
		}
		if (varType.getType() != null)
		{
			VarType typeDefVarType = varType.getType().getType();
			seenTypes.add(hashCode);
			return getTypeOfVarType(typeDefVarType, seenTypes);
		}
		return null;
	}

	//VarDecl or TypedParam
	public static TypeCheckIssue typeCheckVarDeclVsTypeConstant(TemporalExpression expectedVarDecl,
			TemporalExpression expectedTypeConstant) {
		TypeCheckIssue issue = null;
		DomainVarDecl dvd = null;
		VarDecl varDecl = null;
		TypedParam typedParam = null;
		TypeConstant typeConstant = null;

		if (expectedVarDecl instanceof TemporalPrimaryExpr)
		{
			TemporalPrimaryExpr temporalPrimaryExpr = (TemporalPrimaryExpr)expectedVarDecl;
			dvd = TypeSystemUtils.extractDomainVarDeclFromTemporalPrimaryExpr(temporalPrimaryExpr);
			if (dvd == null) {
				// no DomainVarDecl so we look for VarDecl
				varDecl = TypeSystemUtils.extractVarDeclFromTemporalPrimaryExpr(temporalPrimaryExpr);
				if(varDecl == null) { 
					typedParam = TypeSystemUtils.extractTypedParamFromTemporalPrimaryExpr(temporalPrimaryExpr);
					if (typedParam == null) {return null;} // no DomainVarDel, VarDecl, and no TypedParm
				}
			}  
		}
		else {return null;}

		if (expectedTypeConstant instanceof TemporalPrimaryExpr)
		{
			TemporalPrimaryExpr temporalPrimaryExpr = (TemporalPrimaryExpr)expectedTypeConstant;
			typeConstant = TypeSystemUtils.extractTypeConstantFromTemporalPrimaryExpr(temporalPrimaryExpr);
			if (typeConstant == null) {return null;} // no TypeConstant
		}
		else {return null;}

		issue = checkEnums(dvd, varDecl, typedParam, typeConstant);
		if (issue != null)
		{
			return issue;
		}

		return null;
	}



	private static TypeCheckIssue typeCheckTypeConstantvsTypeConstant(TemporalExpression leftTemporalExpression,
			TemporalExpression rightTemporalExpression) {
		TypeCheckIssue issue = null;
		TypeConstant leftTypeConstant = null;
		TypeConstant rightTypeConstant = null;

		if (leftTemporalExpression instanceof TemporalPrimaryExpr)
		{
			TemporalPrimaryExpr leftTemporalPrimaryExpr = (TemporalPrimaryExpr)leftTemporalExpression;
			leftTypeConstant = TypeSystemUtils.extractTypeConstantFromTemporalPrimaryExpr(leftTemporalPrimaryExpr);
			if (leftTypeConstant == null) {return null;} // no left type constant
		}
		else {return null;}

		if (rightTemporalExpression instanceof TemporalPrimaryExpr)
		{
			TemporalPrimaryExpr rightTemporalPrimaryExpr = (TemporalPrimaryExpr)rightTemporalExpression;
			rightTypeConstant = TypeSystemUtils.extractTypeConstantFromTemporalPrimaryExpr(rightTemporalPrimaryExpr);
			if (rightTypeConstant == null) {return null;} // no right type constant
		}
		else {return null;}

		issue = checkEnumsValues(leftTypeConstant, rightTypeConstant);
		if (issue != null)
		{
			return issue;
		}
		return null;
	}

	private static TypeCheckIssue checkEnumsValues(TypeConstant leftTypeConstant,
			TypeConstant rightTypeConstant) {
		if (leftTypeConstant.getName().equals(rightTypeConstant.getName()))
		{
			return new TypeCheckWarning(SpectraPackage.Literals.TEMPORAL_RELATIONAL_EXPR__OPERATOR, IssueMessages.TYPE_CONSTANTS_COMPARISON_EVALUATES_TO_TRUE);
		}

		VarType leftVarTypeParent = EcoreUtil2.getContainerOfType(leftTypeConstant, VarType.class);
		VarType rightVarTypeParent = EcoreUtil2.getContainerOfType(rightTypeConstant, VarType.class);
		if (isBothEnums(leftVarTypeParent, rightVarTypeParent))
		{
			return new TypeCheckWarning(SpectraPackage.Literals.TEMPORAL_RELATIONAL_EXPR__OPERATOR, IssueMessages.TYPE_CONSTANTS_COMPARISON_EVALUATES_TO_FALSE);                 	
		}
		return null;
	}

	private static boolean isBothEnums(VarType leftVarTypeParent, VarType rightVarTypeParent) {
		String leftRep = getTypeRepresentation(leftVarTypeParent);
		String rightRep = getTypeRepresentation(rightVarTypeParent);
		if (leftRep == null || rightRep == null)
		{
			return false;
		}
		if (! leftRep.contains(REPRESENTATION_ENUM))
		{
			return false;
		}
		if (! rightRep.contains(REPRESENTATION_ENUM))
		{
			return false;
		}
		return true;
	}

	//checks if the input is a VarDecl object
	//private static boolean isVarDecl (ReferrableIndex ri) {
	//    return (ri instanceof VarDecl);
	//}
	//
	//private static VarType getReferrableIndexType (ReferrableIndex ri) {   
	//    VarType varType = null;
	//     
	//    if(isVarDecl(ri)) {
	//        varType = ((VarDecl)ri).getType();
	//    } else if(ri instanceof DomainVarDecl) {
	//        varType = ((DomainVarDecl)ri).getDomainType();
	//    }
	//    //return TypeSystemUtils.getVarType(varType);
	//    return varType;
	//}



	private static TypeCheckIssue checkEnums(DomainVarDecl dvd, VarDecl varDecl, TypedParam typedParam, TypeConstant typeConstant) {
		VarType varType = null;
		if (dvd != null) {
			varType = dvd.getDomainType();
		}
		else if(varDecl != null) {
			varType = varDecl.getType();
		}
		else {
			varType = typedParam.getType();
		}
		List<String> permittedTypeConstantsForVarDecl = TypeSystemUtils.getPermittedTypeConstants(varType);
		if (permittedTypeConstantsForVarDecl == null || permittedTypeConstantsForVarDecl.size() == 0) {
			//varDecl/typedParam isn't an enum
			return new TypeCheckError(SpectraPackage.Literals.TEMPORAL_RELATIONAL_EXPR__OPERATOR, IssueMessages.VAR_ISNT_ENUM);
		} 
		if (! permittedTypeConstantsForVarDecl.contains(typeConstant.getName()))
		{
			return new TypeCheckError(SpectraPackage.Literals.TEMPORAL_RELATIONAL_EXPR__OPERATOR, IssueMessages.VAR_DECL_AND_TYPE_CONSTANT_COME_FROM_DIFFERENT_ENUMS);               
		}

		return null;
	}

	// VarDecl or TypedParam or DomainVarDecl
	public static TypeCheckIssue typeCheckVarDeclvsVarDecl(TemporalExpression leftTemporalExpression,
			TemporalExpression rightTemporalExpression) {
		TypeCheckIssue issue = null;
		DomainVarDecl leftDvd = null;
		VarDecl leftVarDecl = null;
		TypedParam leftTypedParam = null;
		DomainVarDecl rightDvd = null;
		VarDecl rightVarDecl = null;
		TypedParam rightTypedParam = null;
		if (leftTemporalExpression instanceof TemporalPrimaryExpr)
		{
			TemporalPrimaryExpr leftTemporalPrimaryExpr = (TemporalPrimaryExpr)leftTemporalExpression;
			leftDvd = TypeSystemUtils.extractDomainVarDeclFromTemporalPrimaryExpr(leftTemporalPrimaryExpr);

			if (leftDvd == null) {
				//no left domain var
				leftVarDecl = TypeSystemUtils.extractVarDeclFromTemporalPrimaryExpr(leftTemporalPrimaryExpr);
				if(leftVarDecl == null) {
					//no left var
					leftTypedParam = TypeSystemUtils.extractTypedParamFromTemporalPrimaryExpr(leftTemporalPrimaryExpr);
					if (leftTypedParam == null) {return null;} //no left DomainVarDecl/VarDecl/TypedParam
				}
			}
		}
		else {return null;}
		if (rightTemporalExpression instanceof TemporalPrimaryExpr)
		{
			TemporalPrimaryExpr rightTemporayPrimaryExpr = (TemporalPrimaryExpr)rightTemporalExpression;
			rightDvd = TypeSystemUtils.extractDomainVarDeclFromTemporalPrimaryExpr(rightTemporayPrimaryExpr);
			if (rightDvd == null) {
				//no right domain var
				rightVarDecl = TypeSystemUtils.extractVarDeclFromTemporalPrimaryExpr(rightTemporayPrimaryExpr);
				if(rightVarDecl == null) {
					rightTypedParam = TypeSystemUtils.extractTypedParamFromTemporalPrimaryExpr(rightTemporayPrimaryExpr);
					if (rightTypedParam == null) {return null;} //no right VarDecl/TypedParam

				}
			} 
		}
		else {return null;}

		issue = checkDomains(leftDvd, leftVarDecl, leftTypedParam, rightDvd, rightVarDecl, rightTypedParam);
		if (issue != null)
		{
			return issue;
		}

		return null;
	}

	//checks the domain of ReferrableIndexes (VardDecl and DomainVarDecl) and TypedParam
	private static TypeCheckIssue checkDomains(DomainVarDecl leftDvd, VarDecl leftVarDecl, TypedParam leftTypedParam, 
			DomainVarDecl rightDvd, VarDecl rightVarDecl, TypedParam rightTypedParam) {

		String leftVarDeclTypeRepresentation = null;
		String rightVarDeclTypeRepresentation = null;
		VarType varType;
		if (leftDvd != null) {
			varType = leftDvd.getDomainType();
		}
		else if(leftVarDecl != null) {
			varType = leftVarDecl.getType();
		}
		else {
			varType = leftTypedParam.getType();
		}

		leftVarDeclTypeRepresentation = getTypeRepresentation(varType);

		if (rightDvd != null) {
			varType = rightDvd.getDomainType();
		}
		else if(rightVarDecl != null) {
			varType = rightVarDecl.getType();
		}
		else {
			varType = rightTypedParam.getType();  
		}

		rightVarDeclTypeRepresentation = getTypeRepresentation(varType);



		if (leftVarDeclTypeRepresentation != null && rightVarDeclTypeRepresentation != null && ! isEqualRepresentation(leftVarDeclTypeRepresentation,rightVarDeclTypeRepresentation))
		{
			return new TypeCheckError(SpectraPackage.Literals.TEMPORAL_RELATIONAL_EXPR__OPERATOR, IssueMessages.VARS_HAVE_DIFFERENT_DOMAINS);
		}
		return null;
	}

	private static boolean isEqualRepresentation(String rep1, String rep2) {
		String[] repsArr = new String[]{rep1, rep2};
		fixRepresentationsIfExactlyOneIsAnArray(repsArr);  
		return repsArr[0].equals(repsArr[1]);
	}

	private static void fixRepresentationsIfExactlyOneIsAnArray(String[] repsArr) {
		String rep1 = repsArr[0];
		String rep2 = repsArr[1];
		if (rep1.contains("[") && rep1.contains("]") && ! rep2.contains("[") && ! rep2.contains("]"))
		{
			int firstOccurrence = rep1.indexOf("[");
			rep1 = rep1.substring(0, firstOccurrence);
		}
		else if (rep2.contains("[") && rep2.contains("]") && ! rep1.contains("[") && ! rep1.contains("]"))
		{
			int firstOccurrence = rep2.indexOf("[");
			rep2 = rep2.substring(0, firstOccurrence);		
		}
		repsArr[0] = rep1;
		repsArr[1] = rep2;
	}

	private static String getTypeRepresentation(VarType type) {
		return getTypeRepresentation(type, null);
	}

	private static String getTypeRepresentation(VarType varType, List<String> alreadySeenTypeDefs) {

		String typeRepresentation = null;

		if (varType.getName() != null)
		{
			typeRepresentation = varType.getName(); //boolean
		}
		else if (varType.getSubr() != null)
		{
			typeRepresentation = REPRESENTATION_SUBRANGE;
		}
		else if (varType.getConst() != null && varType.getConst().size() > 0)
		{
			typeRepresentation = REPRESENTATION_ENUM + "{";
			for (int i = 0; i < varType.getConst().size()-1; i++)
			{
				typeRepresentation += varType.getConst().get(i).getName() + ",";
			}
			typeRepresentation += varType.getConst().get(varType.getConst().size()-1).getName(); // last one
			typeRepresentation += "}";
		}
		else if (varType.getType() != null)
		{
			// cycles danger
			if (alreadySeenTypeDefs == null)
			{
				alreadySeenTypeDefs = new ArrayList<>();
			}
			else if (alreadySeenTypeDefs.contains(varType.getType().getName()))
			{
				return null; //to prevent stack overflow. we handle this error somewhere else so just return "no problem" here. 
			}
			alreadySeenTypeDefs.add(varType.getType().getName());
			VarType typeDefType = varType.getType().getType();
			return getTypeRepresentation(typeDefType, alreadySeenTypeDefs);
		}
		if (varType.getDimensions() != null && varType.getDimensions().size() > 0)
		{
			for (SizeDefineDecl sizeDefineDecl : varType.getDimensions())
			{
				int currDimension = TypeSystemUtils.sizeDefineToInt(sizeDefineDecl); 
				typeRepresentation += "[" + currDimension + "]";
			}

		}
		return typeRepresentation;
	}

}
