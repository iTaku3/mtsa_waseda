/**
 * Copyright (c) since 2015, Tel Aviv University and Software Modeling Lab
 * 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * Neither the name of Tel Aviv University and Software Modeling Lab nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL Tel Aviv University and Software Modeling Lab
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package tau.smlab.syntech.ui.outline;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.ui.editor.outline.impl.DefaultOutlineTreeProvider;
import org.eclipse.xtext.ui.editor.outline.impl.DocumentRootNode;
import tau.smlab.syntech.spectra.Counter;
import tau.smlab.syntech.spectra.Define;
import tau.smlab.syntech.spectra.DefineDecl;
import tau.smlab.syntech.spectra.DefineRegExp;
import tau.smlab.syntech.spectra.EXGar;
import tau.smlab.syntech.spectra.LTLAsm;
import tau.smlab.syntech.spectra.LTLGar;
import tau.smlab.syntech.spectra.Monitor;
import tau.smlab.syntech.spectra.Pattern;
import tau.smlab.syntech.spectra.PatternParamList;
import tau.smlab.syntech.spectra.Predicate;
import tau.smlab.syntech.spectra.TemporalExpression;
import tau.smlab.syntech.spectra.TemporalIffExpr;
import tau.smlab.syntech.spectra.TemporalOrExpr;
import tau.smlab.syntech.spectra.TemporalUnaryExpr;
import tau.smlab.syntech.spectra.TypedParamList;
import tau.smlab.syntech.spectra.Var;
import tau.smlab.syntech.spectra.VarDecl;
import tau.smlab.syntech.spectra.VarType;

/**
 * Customization of the default outline structure.
 * 
 * See https://www.eclipse.org/Xtext/documentation/304_ide_concepts.html#outline
 */
@SuppressWarnings("all")
public class SpectraOutlineTreeProvider extends DefaultOutlineTreeProvider {
  public void _createChildren(final DocumentRootNode parentNode, final EObject rootElement) {
    EList<EObject> _eContents = rootElement.eContents();
    for (final EObject content : _eContents) {
      boolean __isLeaf = this._isLeaf(content);
      boolean _not = (!__isLeaf);
      if (_not) {
        this.createNode(parentNode, content);
      }
    }
  }
  
  public boolean _isLeaf(final LTLGar g) {
    return true;
  }
  
  public boolean _isLeaf(final LTLAsm a) {
    return true;
  }
  
  public boolean _isLeaf(final TemporalUnaryExpr ele) {
    return true;
  }
  
  public boolean _isLeaf(final TemporalOrExpr ele) {
    return true;
  }
  
  public boolean _isLeaf(final TemporalIffExpr ele) {
    return true;
  }
  
  public boolean _isLeaf(final DefineDecl ele) {
    return true;
  }
  
  public boolean _isLeaf(final Define ele) {
    return true;
  }
  
  public boolean _isLeaf(final Monitor ele) {
    return true;
  }
  
  public boolean _isLeaf(final DefineRegExp ele) {
    return true;
  }
  
  public boolean _isLeaf(final EXGar ele) {
    return true;
  }
  
  public boolean _isLeaf(final Var ele) {
    return true;
  }
  
  public boolean _isLeaf(final TemporalExpression ele) {
    return true;
  }
  
  public boolean _isLeaf(final PatternParamList ele) {
    return true;
  }
  
  public boolean _isLeaf(final TypedParamList ele) {
    return true;
  }
  
  public boolean _isLeaf(final VarDecl ele) {
    return true;
  }
  
  public boolean _isLeaf(final VarType ele) {
    return true;
  }
  
  public boolean _isLeaf(final Predicate ele) {
    return true;
  }
  
  public boolean _isLeaf(final Pattern ele) {
    return true;
  }
  
  public boolean _isLeaf(final Counter ele) {
    return true;
  }
}
