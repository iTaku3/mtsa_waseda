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

import tau.smlab.syntech.spectra.SpectraPackage;
import tau.smlab.syntech.spectra.TemporalExpression;
import tau.smlab.syntech.spectra.TemporalPrimaryExpr;
import tau.smlab.syntech.spectra.VarType;
import tau.smlab.syntech.spectra.DomainVarDecl;
import tau.smlab.syntech.spectra.Pattern;
import tau.smlab.syntech.spectra.PredicateOrPatternReferrable;
import tau.smlab.syntech.spectra.QuantifierExpr;



public class TypeSystemQuantifierExpr 
{

  public static TypeCheckIssue typeCheck(QuantifierExpr quantifierExpr)
  {
    TypeCheckIssue issue = null;
    
    issue = checkDomainType(quantifierExpr);
    if (issue != null)
    {
    	return issue;
    }

    issue = checkIfPatternIsTheExpression(quantifierExpr);
    if(issue != null)
    {
    	return issue;
    }
    return null;
  }


  	//get a quantifierExpr
  	//checks the domain type of the domain var of the input quantifierExpr
  /**
   * 
   * @param quantifierExpr
   * 
   * @return error if the domain type of the domain var of the input is wrong
   */
	private static TypeCheckIssue checkDomainType(QuantifierExpr quantifierExpr) 
	{
		if(quantifierExpr!=null)
		{
			DomainVarDecl dvd=quantifierExpr.getDomainVar();
			VarType vt = TypeSystemUtils.getVarType(dvd.getDomainType());
			
			//checks if the domain type is a domain type of array
			if(vt.getDimensions() != null && vt.getDimensions().size() > 0)
			{
				return new TypeCheckError(SpectraPackage.Literals.QUANTIFIER_EXPR__DOMAIN_VAR, IssueMessages.DOMAIN_TYPE_IS_NOT_ARRAY);
			}
			
			//checks if the domain type is a type with the shape - {Const1,Const2,...}
			if(dvd.getDomainType().getConst() != null && dvd.getDomainType().getConst().size() > 0)
			{
				return new TypeCheckError(SpectraPackage.Literals.QUANTIFIER_EXPR__DOMAIN_VAR, IssueMessages.DOMAIN_TYPE_WRONG_FORM);
			}
		}
		return null;
	}
	
  /**
   * 
   * @param quantifierExpr
   * 
   * @return error if the temporal expression of the quantifierExpr calls to a pattern
   */
	private static TypeCheckIssue checkIfPatternIsTheExpression(QuantifierExpr quantifierExpr)
	{
		TemporalExpression tmpr = quantifierExpr.getTemporalExpr();
		while(tmpr instanceof QuantifierExpr)
		{
			tmpr=((QuantifierExpr)tmpr).getTemporalExpr();
		}
		if(tmpr instanceof TemporalPrimaryExpr)
		{
			PredicateOrPatternReferrable predOrPatt = ((TemporalPrimaryExpr)tmpr).getPredPatt();
			if((predOrPatt!=null) && predOrPatt instanceof Pattern)
			{
				return new TypeCheckError(SpectraPackage.Literals.QUANTIFIER_EXPR__TEMPORAL_EXPR, IssueMessages.PATTERN_CANT_BE_IN_QUANTIFIER_EXPR);
			}
		}
		return null;
	}

	
}
