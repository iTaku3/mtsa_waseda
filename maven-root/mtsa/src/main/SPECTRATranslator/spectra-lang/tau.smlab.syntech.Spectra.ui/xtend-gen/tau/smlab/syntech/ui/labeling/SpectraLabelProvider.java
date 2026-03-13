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
package tau.smlab.syntech.ui.labeling;

import com.google.inject.Inject;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.xtext.ui.label.DefaultEObjectLabelProvider;
import tau.smlab.syntech.spectra.Counter;
import tau.smlab.syntech.spectra.Define;
import tau.smlab.syntech.spectra.DefineRegExp;
import tau.smlab.syntech.spectra.EXGar;
import tau.smlab.syntech.spectra.LTLAsm;
import tau.smlab.syntech.spectra.LTLGar;
import tau.smlab.syntech.spectra.Monitor;
import tau.smlab.syntech.spectra.Pattern;
import tau.smlab.syntech.spectra.Predicate;
import tau.smlab.syntech.spectra.TypeDef;
import tau.smlab.syntech.spectra.Var;
import tau.smlab.syntech.spectra.VarDecl;
import tau.smlab.syntech.spectra.VarOwner;

/**
 * Provides labels for EObjects.
 * 
 * See https://www.eclipse.org/Xtext/documentation/304_ide_concepts.html#label-provider
 */
@SuppressWarnings("all")
public class SpectraLabelProvider extends DefaultEObjectLabelProvider {
  @Inject
  public SpectraLabelProvider(final AdapterFactoryLabelProvider delegate) {
    super(delegate);
  }
  
  public String text(final Var ele) {
    String _text = this.getText(ele.getKind());
    String _plus = (_text + " ");
    String _name = ele.getVar().getName();
    return (_plus + _name);
  }
  
  public String text(final VarOwner ele) {
    if (ele != null) {
      switch (ele) {
        case SYS:
          return "SYS";
        case ENV:
          return "ENV";
        case AUX:
          return "AUX";
        default:
          break;
      }
    }
    return null;
  }
  
  public String text(final VarDecl ele) {
    return ele.getName();
  }
  
  public String text(final LTLGar ele) {
    String name = ele.getName();
    if ((name == null)) {
      name = "";
    }
    return ("guarantee " + name);
  }
  
  public String text(final LTLAsm ele) {
    String name = ele.getName();
    if ((name == null)) {
      name = "";
    }
    return ("assumption " + name);
  }
  
  public String text(final Define ele) {
    String _name = ele.getDefineList().get(0).getName();
    return ("define " + _name);
  }
  
  public String text(final TypeDef ele) {
    String _name = ele.getName();
    return ("type " + _name);
  }
  
  public String text(final Predicate ele) {
    String _name = ele.getName();
    return ("predicate " + _name);
  }
  
  public String text(final Pattern ele) {
    String _name = ele.getName();
    return ("pattern " + _name);
  }
  
  public String text(final Monitor ele) {
    String _name = ele.getName();
    return ("monitor " + _name);
  }
  
  public String text(final Counter ele) {
    String _name = ele.getName();
    return ("counter " + _name);
  }
  
  public String text(final DefineRegExp ele) {
    String _name = ele.getDefineRegsList().get(0).getName();
    return ("regexp " + _name);
  }
  
  public String text(final EXGar ele) {
    String _name = ele.getName();
    boolean _tripleEquals = (_name == null);
    if (_tripleEquals) {
      return "Existential guarantee";
    }
    String _name_1 = ele.getName();
    return ("Existential guarantee " + _name_1);
  }
  
  public String image(final Var ele) {
    return "package.JPG";
  }
  
  public String image(final LTLGar ele) {
    return "LTLGar.gif";
  }
}
