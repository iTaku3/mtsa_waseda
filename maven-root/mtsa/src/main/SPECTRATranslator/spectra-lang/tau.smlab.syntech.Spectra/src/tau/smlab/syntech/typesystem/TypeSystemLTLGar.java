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

import tau.smlab.syntech.spectra.LTLGar;
import tau.smlab.syntech.spectra.QuantifierExpr;
import tau.smlab.syntech.spectra.SpectraPackage;
import tau.smlab.syntech.spectra.TemporalExpression;
import tau.smlab.syntech.spectra.TemporalPrimaryExpr;
import tau.smlab.syntech.spectra.VarDecl;

public class TypeSystemLTLGar {

	public static TypeCheckIssue typeCheck(LTLGar gar) {
		TemporalExpression temporalExpression = (TemporalExpression) gar.getTemporalExpr();
		if (temporalExpression != null) {
			List<TemporalPrimaryExpr> temporalPrimaryExprsList;

			// Need to peel down quantifier layers to see if inner expr has primed vars
			while(temporalExpression instanceof QuantifierExpr) {
				temporalExpression = ((QuantifierExpr)temporalExpression).getTemporalExpr();
			}

			BooleanAndString boolAndString = TypeSystemUtils.isBooleanExpression(temporalExpression);
			if(!boolAndString.getBoolean()) {
				return new TypeCheckError(SpectraPackage.Literals.LTL_GAR__TEMPORAL_EXPR,
						IssueMessages.GAR_MUST_BE_BOOLEAN);
			}

			if (temporalExpression instanceof TemporalPrimaryExpr) {
				temporalPrimaryExprsList = new ArrayList<>();
				temporalPrimaryExprsList.add((TemporalPrimaryExpr) temporalExpression);
			} else {
				temporalPrimaryExprsList = EcoreUtil2.getAllContentsOfType(temporalExpression, TemporalPrimaryExpr.class);
			}
			List<VarDecl> primedVarDecls = TypeSystemUtils.getPrimedVarDecls(temporalPrimaryExprsList);

			if(gar.getJustice() != null) {
				if (primedVarDecls != null && primedVarDecls.size() > 0) {
					return new TypeCheckError(SpectraPackage.Literals.LTL_GAR__TEMPORAL_EXPR,
							IssueMessages.GAR_JUSTICE_CANT_HAVE_PRIMED_VARS);
				}  
			}	else if (gar.getSafety() == null && gar.getStateInv() == null) {
				//INI
				if (primedVarDecls != null && primedVarDecls.size() > 0) {
					return new TypeCheckError(SpectraPackage.Literals.LTL_GAR__TEMPORAL_EXPR,
							IssueMessages.GAR_INI_CANT_HAVE_PRIMED_VARS);
				}
			}
		}
		return null;
	}

}
