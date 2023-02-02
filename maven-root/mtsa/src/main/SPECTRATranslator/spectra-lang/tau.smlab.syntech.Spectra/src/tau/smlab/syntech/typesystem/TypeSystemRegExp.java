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

import tau.smlab.syntech.spectra.RegExp;
import tau.smlab.syntech.spectra.SpectraPackage;
import tau.smlab.syntech.spectra.UnaryRegExp;
import tau.smlab.syntech.typesystem.TypeSystemUtils.NotArithmeticExpressionException;

public class TypeSystemRegExp {

	private TypeSystemRegExp() {}

	public static TypeCheckIssue typeCheck(RegExp regExp){
		if(regExp instanceof UnaryRegExp) {
			UnaryRegExp unaryRegExp = (UnaryRegExp) regExp;
			if(unaryRegExp.isHaveRange()) {
				int from = unaryRegExp.getFrom();
				int to = unaryRegExp.getTo();
				
				try {
					if (unaryRegExp.getFromDefine() != null) {						
						from = TypeSystemUtils.calcArithmeticExpression(unaryRegExp.getFromDefine().getSimpleExpr()); 
					}
				} catch (NotArithmeticExpressionException e) {
					return new TypeCheckError(SpectraPackage.Literals.UNARY_REG_EXP__FROM_DEFINE, IssueMessages.REGEXP_INVALID_RANGE_QUANTIFIER_NOT_A_NUMBER);
				}
				
				try {					
					if (unaryRegExp.getToDefine() != null) {
						to = TypeSystemUtils.calcArithmeticExpression(unaryRegExp.getToDefine().getSimpleExpr()); 
					}							
				} catch (NotArithmeticExpressionException e) {
					return new TypeCheckError(SpectraPackage.Literals.UNARY_REG_EXP__TO_DEFINE, IssueMessages.REGEXP_INVALID_RANGE_QUANTIFIER_NOT_A_NUMBER);
				}
				
				if(from > to) {
					return new TypeCheckError(SpectraPackage.Literals.UNARY_REG_EXP__HAVE_RANGE, IssueMessages.REGEXP_INVALID_RANGE_QUANTIFIER);
				}
			}
		}
		/*
		 * Uncomment to disable regular expressions with nested negation (complement) operators.
		 */
//		if(regExp instanceof CompRegExp) {
//			if(regExp.eContainer() instanceof RegExp) {
//				return new TypeCheckError(SpectraPackage.Literals.COMP_REG_EXP__COMP, IssueMessages.REGEXP_CANT_HAVE_NESTED_COMP);
//			}
//		}
		return null;
	}

}
