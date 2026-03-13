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
package tau.smlab.syntech.validation;

import com.google.common.collect.Iterables;
import java.util.HashSet;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.CheckType;
import tau.smlab.syntech.spectra.Counter;
import tau.smlab.syntech.spectra.Define;
import tau.smlab.syntech.spectra.DefineDecl;
import tau.smlab.syntech.spectra.DefineRegExp;
import tau.smlab.syntech.spectra.DefineRegExpDecl;
import tau.smlab.syntech.spectra.EXGar;
import tau.smlab.syntech.spectra.LTLAsm;
import tau.smlab.syntech.spectra.LTLGar;
import tau.smlab.syntech.spectra.Model;
import tau.smlab.syntech.spectra.Monitor;
import tau.smlab.syntech.spectra.Pattern;
import tau.smlab.syntech.spectra.Predicate;
import tau.smlab.syntech.spectra.QuantifierExpr;
import tau.smlab.syntech.spectra.SpectraPackage;
import tau.smlab.syntech.spectra.TemporalExpression;
import tau.smlab.syntech.spectra.TypedParam;
import tau.smlab.syntech.spectra.TypedParamList;
import tau.smlab.syntech.spectra.Var;
import tau.smlab.syntech.spectra.VarDecl;
import tau.smlab.syntech.spectra.WeightDef;
import tau.smlab.syntech.typesystem.TypeCheckIssue;
import tau.smlab.syntech.typesystem.TypeSystemManager;

/**
 * This class contains custom validation rules.
 * 
 * See https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#validation
 */
@SuppressWarnings("all")
public class SpectraValidator extends AbstractSpectraValidator {
  public static final String INVALID_NAME = "invalidName";
  
  @Check
  public void checkIfReferenceableNamesAreUnique(final Model m) {
    final HashSet<String> seenNames = new HashSet<String>();
    final HashSet<String> domainVarsNames = new HashSet<String>();
    Iterable<Predicate> _filter = Iterables.<Predicate>filter(m.getElements(), Predicate.class);
    for (final Predicate e : _filter) {
      String _name = e.getName();
      boolean _tripleNotEquals = (_name != null);
      if (_tripleNotEquals) {
        boolean _contains = seenNames.contains(e.getName());
        if (_contains) {
          this.error("Predicate names have to be unique", e, SpectraPackage.Literals.PREDICATE__NAME);
        }
        seenNames.add(e.getName());
      }
    }
    Iterable<Var> _filter_1 = Iterables.<Var>filter(m.getElements(), Var.class);
    for (final Var spectraVar : _filter_1) {
      {
        VarDecl e_1 = spectraVar.getVar();
        String _name_1 = e_1.getName();
        boolean _tripleNotEquals_1 = (_name_1 != null);
        if (_tripleNotEquals_1) {
          boolean _contains_1 = seenNames.contains(e_1.getName());
          if (_contains_1) {
            this.error("Variable names have to be unique", e_1, SpectraPackage.Literals.VAR_DECL.getEIDAttribute());
          }
          seenNames.add(e_1.getName());
        }
      }
    }
    Iterable<DefineRegExp> _filter_2 = Iterables.<DefineRegExp>filter(m.getElements(), DefineRegExp.class);
    for (final DefineRegExp defineRegExp : _filter_2) {
      EList<DefineRegExpDecl> _defineRegsList = defineRegExp.getDefineRegsList();
      for (final DefineRegExpDecl regExpDecl : _defineRegsList) {
        String _name_1 = regExpDecl.getName();
        boolean _tripleNotEquals_1 = (_name_1 != null);
        if (_tripleNotEquals_1) {
          boolean _contains_1 = seenNames.contains(regExpDecl.getName());
          if (_contains_1) {
            this.error("Regular expressions\' names have to be unique", regExpDecl, SpectraPackage.Literals.DEFINE_REG_EXP_DECL.getEIDAttribute());
          }
          seenNames.add(regExpDecl.getName());
        }
      }
    }
    Iterable<Define> _filter_3 = Iterables.<Define>filter(m.getElements(), Define.class);
    for (final Define define : _filter_3) {
      EList<DefineDecl> _defineList = define.getDefineList();
      for (final DefineDecl defineDecl : _defineList) {
        String _name_2 = defineDecl.getName();
        boolean _tripleNotEquals_2 = (_name_2 != null);
        if (_tripleNotEquals_2) {
          boolean _contains_2 = seenNames.contains(defineDecl.getName());
          if (_contains_2) {
            this.error("Define names have to be unique", defineDecl, 
              SpectraPackage.Literals.DEFINE_DECL.getEIDAttribute());
          }
          seenNames.add(defineDecl.getName());
        }
      }
    }
    Iterable<Pattern> _filter_4 = Iterables.<Pattern>filter(m.getElements(), Pattern.class);
    for (final Pattern e_1 : _filter_4) {
      String _name_3 = e_1.getName();
      boolean _tripleNotEquals_3 = (_name_3 != null);
      if (_tripleNotEquals_3) {
        boolean _contains_3 = seenNames.contains(e_1.getName());
        if (_contains_3) {
          this.error("Pattern names have to be unique", e_1, SpectraPackage.Literals.PATTERN__NAME);
        }
        seenNames.add(e_1.getName());
      }
    }
    Iterable<EXGar> _filter_5 = Iterables.<EXGar>filter(m.getElements(), EXGar.class);
    for (final EXGar e_2 : _filter_5) {
      String _name_4 = e_2.getName();
      boolean _tripleNotEquals_4 = (_name_4 != null);
      if (_tripleNotEquals_4) {
        boolean _contains_4 = seenNames.contains(e_2.getName());
        if (_contains_4) {
          this.error("Existential guarantee names have to be unique", e_2, SpectraPackage.Literals.EX_GAR__NAME);
        }
        seenNames.add(e_2.getName());
      }
    }
    Iterable<LTLGar> _filter_6 = Iterables.<LTLGar>filter(m.getElements(), LTLGar.class);
    for (final LTLGar e_3 : _filter_6) {
      {
        String _name_5 = e_3.getName();
        boolean _tripleNotEquals_5 = (_name_5 != null);
        if (_tripleNotEquals_5) {
          boolean _contains_5 = seenNames.contains(e_3.getName());
          if (_contains_5) {
            this.error("Guarantee names have to be unique", e_3, SpectraPackage.Literals.LTL_GAR__NAME);
          }
          seenNames.add(e_3.getName());
        }
        TemporalExpression tmpExpr = e_3.getTemporalExpr();
        while ((tmpExpr instanceof QuantifierExpr)) {
          {
            boolean _contains_6 = domainVarsNames.contains(((QuantifierExpr)tmpExpr).getDomainVar().getName());
            if (_contains_6) {
              this.error("Domain variables names have to be unique in scope", e_3, SpectraPackage.Literals.LTL_GAR__TEMPORAL_EXPR);
            }
            domainVarsNames.add(((QuantifierExpr)tmpExpr).getDomainVar().getName());
            tmpExpr = ((QuantifierExpr)tmpExpr).getTemporalExpr();
          }
        }
        domainVarsNames.clear();
      }
    }
    Iterable<LTLAsm> _filter_7 = Iterables.<LTLAsm>filter(m.getElements(), LTLAsm.class);
    for (final LTLAsm e_4 : _filter_7) {
      {
        String _name_5 = e_4.getName();
        boolean _tripleNotEquals_5 = (_name_5 != null);
        if (_tripleNotEquals_5) {
          boolean _contains_5 = seenNames.contains(e_4.getName());
          if (_contains_5) {
            this.error("Assumption names have to be unique", e_4, SpectraPackage.Literals.LTL_ASM__NAME);
          }
          seenNames.add(e_4.getName());
        }
        TemporalExpression tmpExpr = e_4.getTemporalExpr();
        while ((tmpExpr instanceof QuantifierExpr)) {
          {
            boolean _contains_6 = domainVarsNames.contains(((QuantifierExpr)tmpExpr).getDomainVar().getName());
            if (_contains_6) {
              this.error("DomainVar Name must be unique in scope", e_4, SpectraPackage.Literals.LTL_ASM__TEMPORAL_EXPR);
            }
            domainVarsNames.add(((QuantifierExpr)tmpExpr).getDomainVar().getName());
            tmpExpr = ((QuantifierExpr)tmpExpr).getTemporalExpr();
          }
        }
        domainVarsNames.clear();
      }
    }
    Iterable<Monitor> _filter_8 = Iterables.<Monitor>filter(m.getElements(), Monitor.class);
    for (final Monitor monitor : _filter_8) {
      {
        boolean _contains_5 = seenNames.contains(monitor.getName());
        if (_contains_5) {
          this.error("Monitor names have to be unique", monitor, SpectraPackage.Literals.MONITOR.getEIDAttribute());
        }
        seenNames.add(monitor.getName());
      }
    }
    Iterable<WeightDef> _filter_9 = Iterables.<WeightDef>filter(m.getElements(), WeightDef.class);
    for (final WeightDef weight : _filter_9) {
      String _name_5 = weight.getName();
      boolean _tripleNotEquals_5 = (_name_5 != null);
      if (_tripleNotEquals_5) {
        boolean _contains_5 = seenNames.contains(weight.getName());
        if (_contains_5) {
          this.error("Weight names have to be unique", weight, SpectraPackage.Literals.WEIGHT_DEF__NAME);
        }
        seenNames.add(weight.getName());
      }
    }
  }
  
  @Check
  public void checkIfPredicateParamNamesAreUnique(final Predicate p) {
    final HashSet<String> seenNames = new HashSet<String>();
    TypedParamList _params = p.getParams();
    boolean _tripleNotEquals = (_params != null);
    if (_tripleNotEquals) {
      EList<TypedParam> _params_1 = p.getParams().getParams();
      for (final TypedParam e : _params_1) {
        {
          boolean _contains = seenNames.contains(e.getName());
          if (_contains) {
            this.error("Parameter names have to be unique", p, SpectraPackage.Literals.PREDICATE__PARAMS);
          }
          seenNames.add(e.getName());
        }
      }
    }
  }
  
  @Check
  public void checkCounterPartsUnique(final Counter c) {
    if (((c.getInitial() != null) && (c.getInitial().size() > 1))) {
      this.error("Cannot define multiple initial constraints in a counter", c, SpectraPackage.Literals.COUNTER__INITIAL);
    }
    if (((c.getDecPred() != null) && (c.getDecPred().size() > 1))) {
      this.error("Cannot define multiple decrement constraints in a counter", c, SpectraPackage.Literals.COUNTER__DEC_PRED);
    }
    if (((c.getIncPred() != null) && (c.getIncPred().size() > 1))) {
      this.error("Cannot define multiple increment constraints in a counter", c, SpectraPackage.Literals.COUNTER__INC_PRED);
    }
    if (((c.getOverflowMethod() != null) && (c.getOverflowMethod().size() > 1))) {
      this.error("Cannot define multiple overflow methods for a counter", c, SpectraPackage.Literals.COUNTER__OVERFLOW_METHOD);
    }
  }
  
  @Check
  public void checkModuleNameStartsWithCapital(final Model m) {
    boolean _isUpperCase = Character.isUpperCase(m.getName().charAt(0));
    boolean _not = (!_isUpperCase);
    if (_not) {
      this.warning("Module name should start with a capital", SpectraPackage.Literals.MODEL__NAME, SpectraValidator.INVALID_NAME);
    }
  }
  
  @Check(CheckType.NORMAL)
  public void typeCheck(final EObject eobject) {
    TypeCheckIssue _typeCheck = TypeSystemManager.typeCheck(eobject);
    final TypeCheckIssue issue = ((TypeCheckIssue) _typeCheck);
    if ((issue != null)) {
      boolean _hasEAttribute = issue.hasEAttribute();
      if (_hasEAttribute) {
        boolean _isError = issue.isError();
        if (_isError) {
          this.error(issue.getIssueMessage(), issue.getEAttribute());
        } else {
          boolean _isWarning = issue.isWarning();
          if (_isWarning) {
            this.warning(issue.getIssueMessage(), issue.getEAttribute());
          }
        }
      } else {
        boolean _hasEReference = issue.hasEReference();
        if (_hasEReference) {
          boolean _isError_1 = issue.isError();
          if (_isError_1) {
            this.error(issue.getIssueMessage(), issue.getEReference());
          } else {
            boolean _isWarning_1 = issue.isWarning();
            if (_isWarning_1) {
              this.warning(issue.getIssueMessage(), issue.getEReference());
            }
          }
        }
      }
    }
  }
}
