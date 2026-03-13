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

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;

import tau.smlab.syntech.spectra.SizeDefineDecl;
import tau.smlab.syntech.spectra.Subrange;
import tau.smlab.syntech.spectra.TypeConstant;
import tau.smlab.syntech.spectra.TypeDef;
import tau.smlab.syntech.spectra.VarType;

public class CustomizedVarType implements VarType {

	VarType originalVarType;
	List<SizeDefineDecl> customizedDimensions;
	
	public CustomizedVarType(VarType varType, List<SizeDefineDecl> dimensions) {
		this.originalVarType = varType;
		this.customizedDimensions = dimensions;
	}

	@Override
	public EClass eClass() {
		return originalVarType.eClass();
	}

	@Override
	public Resource eResource() {
		return originalVarType.eResource();
	}

	@Override
	public EObject eContainer() {
		return originalVarType.eContainer();
	}

	@Override
	public EStructuralFeature eContainingFeature() {
		return originalVarType.eContainingFeature();
	}

	@Override
	public EReference eContainmentFeature() {
		return originalVarType.eContainmentFeature();
	}

	@Override
	public EList<EObject> eContents() {
		return originalVarType.eContents();
	}

	@Override
	public TreeIterator<EObject> eAllContents() {
		return originalVarType.eAllContents();
	}

	@Override
	public boolean eIsProxy() {
		return originalVarType.eIsProxy();
	}

	@Override
	public EList<EObject> eCrossReferences() {
		return originalVarType.eCrossReferences();
	}

	@Override
	public Object eGet(EStructuralFeature feature) {
		return originalVarType.eGet(feature);
	}

	@Override
	public Object eGet(EStructuralFeature feature, boolean resolve) {
		return originalVarType.eGet(feature, resolve);
	}

	@Override
	public void eSet(EStructuralFeature feature, Object newValue) {
		originalVarType.eSet(feature, newValue);
	}

	@Override
	public boolean eIsSet(EStructuralFeature feature) {
		return originalVarType.eIsSet(feature);
	}

	@Override
	public void eUnset(EStructuralFeature feature) {
		originalVarType.eUnset(feature);
	}

	@Override
	public Object eInvoke(EOperation operation, EList<?> arguments) throws InvocationTargetException {
		return originalVarType.eInvoke(operation, arguments);
	}

	@Override
	public EList<Adapter> eAdapters() {
		return originalVarType.eAdapters();
	}

	@Override
	public boolean eDeliver() {
		return originalVarType.eDeliver();
	}

	@Override
	public void eSetDeliver(boolean deliver) {
		originalVarType.eSetDeliver(deliver);
	}

	@Override
	public void eNotify(Notification notification) {
		originalVarType.eNotify(notification);
	}

	@Override
	public String getName() {
		return originalVarType.getName();
	}

	@Override
	public void setName(String value) {
		originalVarType.setName(value);
	}

	@Override
	public Subrange getSubr() {
		return originalVarType.getSubr();
	}

	@Override
	public void setSubr(Subrange value) {
		originalVarType.setSubr(value);
	}

	@Override
	public EList<TypeConstant> getConst() {
		return originalVarType.getConst();
	}

	@Override
	public TypeDef getType() {
		return originalVarType.getType();
	}

	@Override
	public void setType(TypeDef value) {
		originalVarType.setType(value);
	}

	@Override
	public EList<SizeDefineDecl> getDimensions() {
		EList<SizeDefineDecl> list = new BasicEList<SizeDefineDecl>();
		list.addAll(customizedDimensions);
		return list;
	}

}
