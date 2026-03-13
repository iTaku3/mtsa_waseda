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

import tau.smlab.syntech.spectra.Pattern;
import tau.smlab.syntech.spectra.Predicate;
import tau.smlab.syntech.spectra.SpectraPackage;
import tau.smlab.syntech.spectra.TemporalPrimaryExpr;

public class TypeSystemPredicate {

	public static TypeCheckIssue typeCheck(Predicate predicate) {
//		List<Pattern> patternList = null;
//		if (predicate.getBody() instanceof TemporalPrimaryExpr) {
//			TemporalPrimaryExpr temporalPrimaryExpr = (TemporalPrimaryExpr)predicate.getBody();
//				if (temporalPrimaryExpr.getPredPatt() instanceof Pattern) {
//					patternList = new ArrayList<>();
//					patternList.add((Pattern)temporalPrimaryExpr.getPredPatt());
//				}
//		}
//		else {
//			patternList = EcoreUtil2.getAllContentsOfType(predicate.getBody(), Pattern.class);
//		}
//		if(patternList != null && !patternList.isEmpty()) {
//			return new TypeCheckError(SpectraPackage.Literals.PREDICATE__BODY, IssueMessages.CANT_CALL_A_PATTERN_FROM_HERE);
//		}
		
		TypeCheckIssue issue = isPredicatesCycle(predicate, new ArrayList<String>());
		if (issue != null)
		{
			return issue;
		}

		//We have no cycles thus we can check that the body of the predicate is indeed boolean

		BooleanAndString isPredicateBodyBoolean = TypeSystemUtils.isBooleanExpression(predicate.getBody());
		if (!isPredicateBodyBoolean.getBoolean()) {
			return new TypeCheckError(SpectraPackage.Literals.PREDICATE__BODY, IssueMessages.PREDICATES_MUST_BE_BOOLEAN);
		}

		return null;
	}

	private static TypeCheckIssue isPredicatesCycle(Predicate currPredicate, List<String> predicatesNamesSeen) {
		TypeCheckIssue issue = null;
		if (predicatesNamesSeen.contains(currPredicate.getName())) {
			return new TypeCheckError(SpectraPackage.Literals.PREDICATE__BODY, IssueMessages.PREDICATES_CYCLE);
		}
		predicatesNamesSeen.add(currPredicate.getName());

		List<TemporalPrimaryExpr> temporalPrimaryExprsList = EcoreUtil2.getAllContentsOfType(currPredicate,
				TemporalPrimaryExpr.class);
		if (temporalPrimaryExprsList == null) {
			return null;
		}
		for (TemporalPrimaryExpr tpe : temporalPrimaryExprsList) {
			if (tpe.getPredPatt() != null && tpe.getPredPatt() instanceof Predicate) {
				Predicate referencedPred = (Predicate) tpe.getPredPatt();
				List<String> copy = new ArrayList<>();
				copy.addAll(predicatesNamesSeen);
				if ((issue = isPredicatesCycle(referencedPred, copy)) != null) {
					return issue;
				}
			}
		}

		return null;
	}
}
