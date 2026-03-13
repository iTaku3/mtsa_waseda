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

package tau.smlab.syntech.ui.hover;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.ui.editor.hover.html.DefaultEObjectHoverProvider;

import tau.smlab.syntech.spectra.DefineDecl;
import tau.smlab.syntech.spectra.DefineRegExpDecl;
import tau.smlab.syntech.spectra.LTLAsm;
import tau.smlab.syntech.spectra.LTLGar;
import tau.smlab.syntech.spectra.Model;
import tau.smlab.syntech.spectra.Monitor;
import tau.smlab.syntech.spectra.Pattern;
import tau.smlab.syntech.spectra.PatternParam;
import tau.smlab.syntech.spectra.Predicate;
import tau.smlab.syntech.spectra.TypeConstant;
import tau.smlab.syntech.spectra.TypeDef;
import tau.smlab.syntech.spectra.TypedParam;
import tau.smlab.syntech.spectra.Var;
import tau.smlab.syntech.spectra.VarDecl;
import tau.smlab.syntech.spectra.VarType;
import tau.smlab.syntech.spectra.WeightDef;

public class SpectraEObjectHoverProvider extends DefaultEObjectHoverProvider {
  @Override
  protected String getFirstLine(EObject o) {
    if (o instanceof LTLGar) {
          LTLGar gar = (LTLGar)o;
          String garName = (gar.getName() != null ? gar.getName() : "");
          if (gar.getSafety() != null)
          {
            return "Safety Guarantee <b>" + garName + "</b>";
          }
          else if (gar.getJustice() != null)
          {
            return "Justice Guarantee <b>" + garName + "</b>";
          }
          else if(gar.getTrig() != null) {
        	return "Trigger Guarantee <b>" + garName + "</b>";
          }
          else {
            return "INI Guarantee <b>" + garName + "</b>";
          }
      }
      if (o instanceof LTLAsm)
      {
        LTLAsm asm = (LTLAsm)o;
        String asmName = (asm.getName() != null ? asm.getName() : "");
        if (asm.getSafety() != null)
        {
          return "Safety Assumption <b>" + asmName + "</b>";
        }
        else if (asm.getJustice() != null)
        {
          return "Justice Assumption <b>" + asmName + "</b>";
        }
        else if (asm.getTrig() != null)
        {
          return "Trigger Assumption <b>" + asmName + "</b>";
        }
        else {
          return "INI Assumption <b>" + asmName + "</b>";
        }
      }
      if (o instanceof Predicate)
      {
        return "Predicate <b>" + ((Predicate)o).getName() + "</b>";
      }
      if (o instanceof Pattern)
      {
        return "Pattern <b>" + ((Pattern)o).getName() + "</b>";
      }
      if (o instanceof TypeDef)
      {
        return "TypeDef <b>" + ((TypeDef)o).getName() + "</b>";
      }
      if (o instanceof VarDecl)
      {
    	VarDecl varDecl = (VarDecl)o;
    	Var var = EcoreUtil2.getContainerOfType(varDecl, Var.class);	
    	VarType varType = varDecl.getType();
    	INode inode = NodeModelUtils.findActualNodeFor(varType);
    	String type = NodeModelUtils.getTokenText(inode);
        return "Variable " + var.getKind().getName()  + " " + type + " <b>"  + ((VarDecl)o).getName() + "</b>";
      }
      if (o instanceof TypeConstant)
      {
        return "TypeConstant <b>" + ((TypeConstant)o).getName() + "</b>";
      }
      if (o instanceof Model)
      {
        return "Module <b>" + ((Model)o).getName() + "</b>";
      }
      if (o instanceof DefineDecl)
      {
        return "DefineDecl <b>" + ((DefineDecl)o).getName() + "</b>";
      }
      if (o instanceof TypedParam)
      {
        return "TypedParam <b>" + ((TypedParam)o).getName() + "</b>";
      }
      if (o instanceof PatternParam)
      {
        return "PatternParam <b>" + ((PatternParam)o).getName() + "</b>";
      }
      if (o instanceof WeightDef)
      {
        return "WeightDef <b>" + ((WeightDef)o).getName() + "</b>";
      }
      if (o instanceof Monitor)
      {
        return "Monitor <b>" + ((Monitor)o).getName() + "</b>";
      }
      if (o instanceof DefineRegExpDecl) {
    	  return "RegExpDecl <b>" + ((DefineRegExpDecl)o).getName() + "</b>";
      }
      return super.getFirstLine(o);
  }
}
