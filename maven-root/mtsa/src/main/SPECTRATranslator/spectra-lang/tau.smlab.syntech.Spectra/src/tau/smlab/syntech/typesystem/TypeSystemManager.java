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

import org.eclipse.emf.ecore.EObject;

import tau.smlab.syntech.spectra.BooleanTerm;
import tau.smlab.syntech.spectra.DefineDecl;
import tau.smlab.syntech.spectra.EXGar;
import tau.smlab.syntech.spectra.Import;
import tau.smlab.syntech.spectra.LTLAsm;
import tau.smlab.syntech.spectra.LTLGar;
import tau.smlab.syntech.spectra.Pattern;
import tau.smlab.syntech.spectra.Predicate;
import tau.smlab.syntech.spectra.QuantifierExpr;
import tau.smlab.syntech.spectra.RegExp;
import tau.smlab.syntech.spectra.Subrange;
import tau.smlab.syntech.spectra.TemporalAdditiveExpr;
import tau.smlab.syntech.spectra.TemporalAndExpr;
import tau.smlab.syntech.spectra.TemporalBinaryExpr;
import tau.smlab.syntech.spectra.TemporalIffExpr;
import tau.smlab.syntech.spectra.TemporalImpExpr;
import tau.smlab.syntech.spectra.TemporalInExpr;
import tau.smlab.syntech.spectra.TemporalMultiplicativeExpr;
import tau.smlab.syntech.spectra.TemporalOrExpr;
import tau.smlab.syntech.spectra.TemporalPrimaryExpr;
import tau.smlab.syntech.spectra.TemporalRelationalExpr;
import tau.smlab.syntech.spectra.TemporalRemainderExpr;
import tau.smlab.syntech.spectra.TemporalUnaryExpr;
import tau.smlab.syntech.spectra.TypeConstant;
import tau.smlab.syntech.spectra.TypeDef;
import tau.smlab.syntech.spectra.VarDecl;
import tau.smlab.syntech.spectra.VarType;


public class TypeSystemManager {
  public static TypeCheckIssue typeCheck(EObject eobject) {

    if (eobject instanceof TemporalRelationalExpr)
    {
      return TypeSystemTemporalRelationalExpr.typeCheck((TemporalRelationalExpr)eobject);
    }
    else if (eobject instanceof TemporalInExpr)
    {
      return TypeSystemTemporalInExpr.typeCheck((TemporalInExpr)eobject);
    }
    else if (eobject instanceof TemporalPrimaryExpr)
    {
      return TypeSystemTemporalPrimaryExpr.typeCheck((TemporalPrimaryExpr)eobject);
    }
    else if (eobject instanceof Subrange)
    {
      return TypeSystemSubrange.typeCheck((Subrange)eobject);
    }
    else if (eobject instanceof TemporalImpExpr)
    {
      return TypeSystemImplExpr.typeCheck((TemporalImpExpr)eobject);
    }
    else if (eobject instanceof TemporalUnaryExpr)
    {
      return TypeSystemTemporalUnaryExpr.typeCheck((TemporalUnaryExpr) eobject);
    }
    else if (eobject instanceof TemporalBinaryExpr)
    {
      return TypeSystemTemporalBinaryExpr.typeCheck((TemporalBinaryExpr) eobject);
    }
    else if (eobject instanceof TemporalIffExpr)
    { 
      return TypeSystemTemporlIffExpr.typeCheck((TemporalIffExpr)eobject); 
    }
    else if (eobject instanceof TemporalOrExpr)
    {
      return TypeSystemOrExpr.typeCheck((TemporalOrExpr)eobject);
    }
    else if (eobject instanceof TemporalAndExpr)
    {
      return TypeSystemAndExpr.typeCheck((TemporalAndExpr)eobject);
    }
    else if (eobject instanceof TemporalRemainderExpr)
    {
      return TypeSystemTemporalRemainderExpr.typeCheck((TemporalRemainderExpr)eobject);
    }
    else if (eobject instanceof TemporalAdditiveExpr)
    {
      return TypeSystemTemporalAdditiveExpr.typeCheck((TemporalAdditiveExpr)eobject);
    }
    else if (eobject instanceof TemporalMultiplicativeExpr)
    {
      return TypeSystemTemporalMultiplicativeExpr.typeCheck((TemporalMultiplicativeExpr)eobject);
    }
    else if (eobject instanceof VarDecl)
    {
      return TypeSystemVarDecl.typeCheck((VarDecl)eobject);
    }
    else if (eobject instanceof TypeConstant)
    {
      return TypeSystemTypeConstant.typeCheck((TypeConstant)eobject);
    }
    else if (eobject instanceof DefineDecl)
    {
      return TypeSystemDefineDecl.typeCheck((DefineDecl)eobject);
    }
    else if (eobject instanceof Predicate)
    {
      return TypeSystemPredicate.typeCheck((Predicate)eobject);
    }
    else if (eobject instanceof Pattern)
    {
      return TypeSystemPattern.typeCheck((Pattern)eobject);
    }
    else if (eobject instanceof LTLGar)
    {
      return TypeSystemLTLGar.typeCheck((LTLGar)eobject);
    }
    else if (eobject instanceof EXGar)
    {
    	return TypeSystemEXGar.typeCheck((EXGar)eobject);
    }
    else if (eobject instanceof RegExp) {
    	return TypeSystemRegExp.typeCheck((RegExp)eobject);
    }
    else if(eobject instanceof BooleanTerm) {
    	return TypeSystemBooleanTerm.typeCheck((BooleanTerm)eobject);
    }
    else if (eobject instanceof LTLAsm)
    {
      return TypeSystemLTLAsm.typeCheck((LTLAsm)eobject);
    }
    else if (eobject instanceof TypeDef)
    {
      return TypeSystemTypeDef.typeCheck((TypeDef)eobject);
    }
    else if (eobject instanceof Import)
    {
    	return TypeSystemImport.typeCheck((Import)eobject);
    }
    else if (eobject instanceof QuantifierExpr)
    {
        return TypeSystemQuantifierExpr.typeCheck((QuantifierExpr)eobject);
    }
    else if (eobject instanceof VarType)
    {
        return TypeSystemVarType.typeCheck((VarType)eobject);
    }

    return null;
  }
}
