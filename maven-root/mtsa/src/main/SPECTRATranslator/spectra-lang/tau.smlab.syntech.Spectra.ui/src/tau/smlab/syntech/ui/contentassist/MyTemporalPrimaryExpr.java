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

package tau.smlab.syntech.ui.contentassist;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;

import tau.smlab.syntech.spectra.DefineRegExpDecl;
import tau.smlab.syntech.spectra.PredicateOrPatternReferrable;
import tau.smlab.syntech.spectra.Referrable;
import tau.smlab.syntech.spectra.RegExp;
import tau.smlab.syntech.spectra.TemporalExpression;
import tau.smlab.syntech.spectra.TemporalPrimaryExpr;
import tau.smlab.syntech.spectra.TypeConstant;
import tau.smlab.syntech.spectra.VarDecl;

public class MyTemporalPrimaryExpr implements TemporalPrimaryExpr{

  private Referrable pointer;

  @Override
  public EClass eClass() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Resource eResource() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public EObject eContainer() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public EStructuralFeature eContainingFeature() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public EReference eContainmentFeature() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public EList<EObject> eContents() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public TreeIterator<EObject> eAllContents() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean eIsProxy() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public EList<EObject> eCrossReferences() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object eGet(EStructuralFeature feature) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object eGet(EStructuralFeature feature, boolean resolve) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void eSet(EStructuralFeature feature, Object newValue) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public boolean eIsSet(EStructuralFeature feature) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void eUnset(EStructuralFeature feature) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public Object eInvoke(EOperation operation, EList<?> arguments) throws InvocationTargetException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public EList<Adapter> eAdapters() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean eDeliver() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void eSetDeliver(boolean deliver) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void eNotify(Notification notification) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public PredicateOrPatternReferrable getPredPatt() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setPredPatt(PredicateOrPatternReferrable value) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public EList<TemporalExpression> getPredPattParams() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getOperator() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setOperator(String value) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public TemporalExpression getTpe() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setTpe(TemporalExpression value) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public Referrable getPointer() {
    return this.pointer;
  }
  public VarDecl getPointerVarDecl() {
    return (VarDecl) this.pointer;
  }
  
  
  public TypeConstant getPointerTypeConstant() {
    return (TypeConstant) this.pointer;
  }
  @Override
  public void setPointer(Referrable value) {
    // TODO Auto-generated method stub
    
  }

  public void setPointer(VarDecl varDecl) {
    pointer = (Referrable) varDecl;
  }
  public void setPointer(TypeConstant tc) {
    pointer = (Referrable) tc;
  }

  @Override
  public TemporalExpression getTemporalExpression() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setTemporalExpression(TemporalExpression value) {
    // TODO Auto-generated method stub
    
  }

@Override
public EList<TemporalExpression> getIndex() {
	// TODO Auto-generated method stub
	return null;
}

@Override
public RegExp getRegexp() {
	// TODO Auto-generated method stub
	return null;
}

@Override
public void setRegexp(RegExp value) {
	// TODO Auto-generated method stub
	
}

@Override
public DefineRegExpDecl getRegexpPointer() {
	// TODO Auto-generated method stub
	return null;
}

@Override
public void setRegexpPointer(DefineRegExpDecl value) {
	// TODO Auto-generated method stub
	
}

}
