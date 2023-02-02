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

package tau.smlab.syntech.resources;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.impl.DefaultResourceDescriptionStrategy;
import org.eclipse.xtext.util.IAcceptor;

import tau.smlab.syntech.spectra.Define;
import tau.smlab.syntech.spectra.DefineDecl;
import tau.smlab.syntech.spectra.Model;
import tau.smlab.syntech.spectra.TypeConstant;
import tau.smlab.syntech.spectra.TypeDef;
import tau.smlab.syntech.spectra.Var;
import tau.smlab.syntech.spectra.VarType;

/**
 * Exporting only the most important elements of Spectra documents.
 *
 */
public class SpectraResourceDescriptionStrategy extends DefaultResourceDescriptionStrategy {
	
  @Override
  public boolean createEObjectDescriptions(EObject o, IAcceptor<IEObjectDescription> acceptor) {
    if (o instanceof Model) {
      // register the model 
      super.createEObjectDescriptions(o, acceptor);
      // register top level elements of the model
      for (EObject e : ((Model) o).getElements()) {
        if (e instanceof Var) {
          super.createEObjectDescriptions(((Var) e).getVar(), acceptor);
          createEObjectDescriptions(((Var) e).getVar().getType(), acceptor);
        } else if (e instanceof Define) {
          for (DefineDecl d : ((Define) e).getDefineList()) {
            super.createEObjectDescriptions(d, acceptor);
          }
        } else {
          super.createEObjectDescriptions(e, acceptor);
          createEObjectDescriptions(e, acceptor);
        }
      }
    } else if (o instanceof VarType) {
      EList<TypeConstant> consts = ((VarType) o).getConst();
      if (consts != null) {
        for (TypeConstant c : consts) {
          super.createEObjectDescriptions(c, acceptor);
        }
      }
    } else if (o instanceof TypeDef) {
      TypeDef td = (TypeDef)o;
      if (td.getType()!=null) {
        EList<TypeConstant> consts = td.getType().getConst();
        if (consts != null) {
          for (TypeConstant c : consts) {
            super.createEObjectDescriptions(c, acceptor);
          }
        }
        
      }
    }
    return false;
  }
}
