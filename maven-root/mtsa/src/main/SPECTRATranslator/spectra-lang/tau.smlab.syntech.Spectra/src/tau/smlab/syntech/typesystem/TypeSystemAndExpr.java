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
import tau.smlab.syntech.spectra.TemporalAndExpr;
import tau.smlab.syntech.spectra.TemporalExpression;

public class TypeSystemAndExpr {

  public static TypeCheckIssue typeCheck(TemporalAndExpr temporalAndExpr) {
    for (TemporalExpression temporalExpression : temporalAndExpr.getElements())
    {
      BooleanAndString isCurrentTemporalExpressionBoolean = TypeSystemUtils.isBooleanExpression(temporalExpression);
      if (! isCurrentTemporalExpressionBoolean.getBoolean())
      {
        return new TypeCheckError(SpectraPackage.Literals.TEMPORAL_AND_EXPR__OPERATOR, IssueMessages.AND_EXPR_ARGUMENTS_MUST_BE_BOOLEANS + ": " + isCurrentTemporalExpressionBoolean.getString());        
      }
    }
    
//    TypeCheckIssue issue = TypeSystemUtils.typeCheckCorrectWayOfAccessingArray(temporalAndExpr.getElements().get(0), temporalAndExpr.getElements().get(1), SpectraPackage.Literals.TEMPORAL_AND_EXPR__OPERATOR);
//    if (issue != null)
//    {
//    	return issue;
//    }
    
    return null;
  }

}
