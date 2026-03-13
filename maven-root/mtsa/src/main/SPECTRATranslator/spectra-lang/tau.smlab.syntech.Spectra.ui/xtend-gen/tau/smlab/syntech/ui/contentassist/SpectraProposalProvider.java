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
package tau.smlab.syntech.ui.contentassist;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.AbstractElement;
import org.eclipse.xtext.Assignment;
import org.eclipse.xtext.CrossReference;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.RuleCall;
import org.eclipse.xtext.ui.editor.contentassist.ContentAssistContext;
import org.eclipse.xtext.ui.editor.contentassist.ICompletionProposalAcceptor;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.IntegerRange;
import tau.smlab.syntech.services.SpectraGrammarAccess;
import tau.smlab.syntech.spectra.Counter;
import tau.smlab.syntech.spectra.Decl;
import tau.smlab.syntech.spectra.Define;
import tau.smlab.syntech.spectra.DefineDecl;
import tau.smlab.syntech.spectra.DomainVarDecl;
import tau.smlab.syntech.spectra.Model;
import tau.smlab.syntech.spectra.Monitor;
import tau.smlab.syntech.spectra.Pattern;
import tau.smlab.syntech.spectra.PatternParam;
import tau.smlab.syntech.spectra.PatternParamList;
import tau.smlab.syntech.spectra.Predicate;
import tau.smlab.syntech.spectra.PredicateOrPatternReferrable;
import tau.smlab.syntech.spectra.Referrable;
import tau.smlab.syntech.spectra.TemporalExpression;
import tau.smlab.syntech.spectra.TemporalPrimaryExpr;
import tau.smlab.syntech.spectra.TemporalRelationalExpr;
import tau.smlab.syntech.spectra.TypeConstant;
import tau.smlab.syntech.spectra.TypeDef;
import tau.smlab.syntech.spectra.TypedParam;
import tau.smlab.syntech.spectra.TypedParamList;
import tau.smlab.syntech.spectra.Var;
import tau.smlab.syntech.spectra.VarDecl;
import tau.smlab.syntech.spectra.VarType;
import tau.smlab.syntech.typesystem.TypeCheckIssue;
import tau.smlab.syntech.typesystem.TypeSystemTemporalRelationalExpr;
import tau.smlab.syntech.typesystem.TypeSystemUtils;

/**
 * See https://www.eclipse.org/Xtext/documentation/304_ide_concepts.html#content-assist
 * on how to customize the content assistant.
 */
@SuppressWarnings("all")
public class SpectraProposalProvider extends AbstractSpectraProposalProvider {
  @Inject
  private SpectraGrammarAccess grammarAccess;
  
  public void complete_Model(final EObject model, final RuleCall ruleCall, final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
    acceptor.accept(this.createCompletionProposal(this.grammarAccess.getModelAccess().getSpecKeyword_1_1().getValue(), context));
    super.complete_Model(model, ruleCall, context, acceptor);
  }
  
  public void complete_Import(final EObject model, final RuleCall ruleCall, final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
    acceptor.accept(this.createCompletionProposal(this.grammarAccess.getImportAccess().getImportKeyword_0().getValue(), context));
    super.complete_Import(model, ruleCall, context, acceptor);
  }
  
  public void completeVar_Kind(final EObject model, final Assignment assignment, final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
    acceptor.accept(this.createCompletionProposal(this.grammarAccess.getVarOwnerAccess().getSYSOutputKeyword_0_0().getValue(), context));
    acceptor.accept(this.createCompletionProposal(this.grammarAccess.getVarOwnerAccess().getSYSOutKeyword_1_0().getValue(), context));
    acceptor.accept(this.createCompletionProposal(this.grammarAccess.getVarOwnerAccess().getSYSSysvarKeyword_2_0().getValue(), context));
    acceptor.accept(this.createCompletionProposal(this.grammarAccess.getVarOwnerAccess().getSYSSysKeyword_3_0().getValue(), context));
    acceptor.accept(this.createCompletionProposal(this.grammarAccess.getVarOwnerAccess().getENVInputKeyword_4_0().getValue(), context));
    acceptor.accept(this.createCompletionProposal(this.grammarAccess.getVarOwnerAccess().getENVInKeyword_5_0().getValue(), context));
    acceptor.accept(this.createCompletionProposal(this.grammarAccess.getVarOwnerAccess().getENVEnvvarKeyword_6_0().getValue(), context));
    acceptor.accept(this.createCompletionProposal(this.grammarAccess.getVarOwnerAccess().getENVEnvKeyword_7_0().getValue(), context));
    acceptor.accept(this.createCompletionProposal(this.grammarAccess.getVarOwnerAccess().getAUXAuxvarKeyword_8_0().getValue(), context));
    acceptor.accept(
      this.createCompletionProposal(
        this.grammarAccess.getVarOwnerAccess().getAUXAuxKeyword_9_0().getValue(), context));
  }
  
  public void complete_LTLGar(final EObject model, final RuleCall ruleCall, final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
    super.complete_LTLGar(model, ruleCall, context, acceptor);
    acceptor.accept(this.createCompletionProposal(this.grammarAccess.getLTLGarAccess().getIniKeyword_2_0_0_0().getValue(), context));
    acceptor.accept(this.createCompletionProposal(this.grammarAccess.getLTLGarAccess().getSafetyTransKeyword_2_0_0_2_0_1().getValue(), context));
    acceptor.accept(this.createCompletionProposal(this.grammarAccess.getLTLGarAccess().getJusticeAlwEvKeyword_2_0_0_4_0_1().getValue(), context));
    acceptor.accept(this.createCompletionProposal(this.grammarAccess.getLTLGarAccess().getStateInvAlwKeyword_2_0_0_3_0_1().getValue(), context));
  }
  
  public void complete_LTLAsm(final EObject model, final RuleCall ruleCall, final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
    super.complete_LTLAsm(model, ruleCall, context, acceptor);
    acceptor.accept(this.createCompletionProposal(this.grammarAccess.getLTLAsmAccess().getIniKeyword_2_0_0_0().getValue(), context));
    acceptor.accept(this.createCompletionProposal(this.grammarAccess.getLTLAsmAccess().getSafetyTransKeyword_2_0_0_2_0_1().getValue(), context));
    acceptor.accept(this.createCompletionProposal(this.grammarAccess.getLTLAsmAccess().getJusticeAlwEvKeyword_2_0_0_4_0_1().getValue(), context));
    acceptor.accept(this.createCompletionProposal(this.grammarAccess.getLTLAsmAccess().getStateInvAlwKeyword_2_0_0_3_0_1().getValue(), context));
  }
  
  public void completeTypedParam_Type(final EObject model, final Assignment assignment, final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
    AbstractElement _terminal = assignment.getTerminal();
    this.lookupCrossReference(((CrossReference) _terminal), context, acceptor);
  }
  
  public void completeTemporalPrimaryExpr_PredPattParams(final EObject model, final Assignment assignment, final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
    final Model root = EcoreUtil2.<Model>getContainerOfType(model, Model.class);
    final ArrayList<VarDecl> theLegalParamsVar = CollectionLiterals.<VarDecl>newArrayList();
    EObject _currentModel = context.getCurrentModel();
    TemporalPrimaryExpr tpe = ((TemporalPrimaryExpr) _currentModel);
    if (((tpe.getPredPatt() != null) && (tpe.getPredPatt() instanceof Predicate))) {
      PredicateOrPatternReferrable _predPatt = tpe.getPredPatt();
      Predicate predicate = ((Predicate) _predPatt);
      TypedParamList _params = predicate.getParams();
      TypedParamList predicateParamsList = ((TypedParamList) _params);
      EList<TypedParam> _params_1 = predicateParamsList.getParams();
      List<TypedParam> predicatesParamParam = ((List<TypedParam>) _params_1);
      final EList<Decl> elements = root.getElements();
      final List<VarDecl> varDecls = CollectionLiterals.<VarDecl>newArrayList();
      for (final Decl elem : elements) {
        if ((elem instanceof Var)) {
          Var var1 = ((Var) elem);
          varDecls.add(var1.getVar());
        }
      }
      for (final TypedParam param : predicatesParamParam) {
        for (final VarDecl vd : varDecls) {
          boolean _isSameVarType = TypeSystemUtils.isSameVarType(vd.getType(), param.getType());
          if (_isSameVarType) {
            theLegalParamsVar.add(vd);
          }
        }
      }
    }
    if (((tpe.getPredPatt() != null) && (tpe.getPredPatt() instanceof Pattern))) {
      final List<VarDecl> varDecls_1 = CollectionLiterals.<VarDecl>newArrayList();
      final EList<Decl> elements_1 = root.getElements();
      for (final Decl elem_1 : elements_1) {
        if ((elem_1 instanceof Var)) {
          Var var1_1 = ((Var) elem_1);
          varDecls_1.add(var1_1.getVar());
        }
      }
      for (final VarDecl vd_1 : varDecls_1) {
        if (((vd_1.getType() != null) && (vd_1.getType().getName() != null))) {
          boolean _equals = vd_1.getType().getName().equals(this.grammarAccess.getVarTypeAccess().getNameBooleanKeyword_0_0_0().getValue());
          if (_equals) {
            theLegalParamsVar.add(vd_1);
          }
        }
      }
    }
    int _size = theLegalParamsVar.size();
    int _minus = (_size - 1);
    IntegerRange _upTo = new IntegerRange(0, _minus);
    for (final Integer i : _upTo) {
      acceptor.accept(this.createCompletionProposal(theLegalParamsVar.get((i).intValue()).getName(), context));
    }
    AbstractElement _terminal = assignment.getTerminal();
    this.completeRuleCall(((RuleCall) _terminal), context, acceptor);
  }
  
  public void completeTemporalPrimaryExpr_Pointer(final EObject model, final Assignment assignment, final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
    final ArrayList<Referrable> theLegalParamsVar = CollectionLiterals.<Referrable>newArrayList();
    final Model root = EcoreUtil2.<Model>getContainerOfType(model, Model.class);
    EList<Decl> _elements = root.getElements();
    List<Decl> declsList = ((List<Decl>) _elements);
    for (final Decl decl : declsList) {
      {
        if ((decl instanceof Define)) {
          Define define = ((Define) decl);
          EList<DefineDecl> _defineList = define.getDefineList();
          List<DefineDecl> defineDeclsList = ((List<DefineDecl>) _defineList);
          for (final DefineDecl defineDecl : defineDeclsList) {
            theLegalParamsVar.add(defineDecl);
          }
        }
        if ((decl instanceof Monitor)) {
          Monitor monitor = ((Monitor) decl);
          theLegalParamsVar.add(monitor);
        }
        if ((decl instanceof Counter)) {
          Counter counter = ((Counter) decl);
          theLegalParamsVar.add(counter);
        }
      }
    }
    EObject currModel = context.getCurrentModel();
    if ((currModel instanceof TemporalRelationalExpr)) {
      TemporalRelationalExpr tre = ((TemporalRelationalExpr) currModel);
      TemporalExpression _left = tre.getLeft();
      TemporalPrimaryExpr tpe = ((TemporalPrimaryExpr) _left);
      DomainVarDecl _extractDomainVarDeclFromTemporalPrimaryExpr = TypeSystemUtils.extractDomainVarDeclFromTemporalPrimaryExpr(tpe);
      boolean _tripleNotEquals = (_extractDomainVarDeclFromTemporalPrimaryExpr != null);
      if (_tripleNotEquals) {
        DomainVarDecl _extractDomainVarDeclFromTemporalPrimaryExpr_1 = TypeSystemUtils.extractDomainVarDeclFromTemporalPrimaryExpr(tpe);
        VarDecl vd = ((VarDecl) _extractDomainVarDeclFromTemporalPrimaryExpr_1);
        MyTemporalPrimaryExpr vd_mtpe = new MyTemporalPrimaryExpr();
        vd_mtpe.setPointer(vd);
        Pattern _containerOfType = EcoreUtil2.<Pattern>getContainerOfType(vd, Pattern.class);
        boolean _tripleEquals = (_containerOfType == null);
        if (_tripleEquals) {
          List<MyTemporalPrimaryExpr> varDeclsTPE = CollectionLiterals.<MyTemporalPrimaryExpr>newArrayList();
          varDeclsTPE.addAll(this.returnVarDeclsAsMyTemporalPrimaryExpr(root));
          for (final MyTemporalPrimaryExpr mtpe : varDeclsTPE) {
            TypeCheckIssue _typeCheckVarDeclvsVarDecl = TypeSystemTemporalRelationalExpr.typeCheckVarDeclvsVarDecl(vd_mtpe, mtpe);
            boolean _tripleEquals_1 = (_typeCheckVarDeclvsVarDecl == null);
            if (_tripleEquals_1) {
              theLegalParamsVar.add(mtpe.getPointerVarDecl());
            }
          }
        } else {
          Pattern _containerOfType_1 = EcoreUtil2.<Pattern>getContainerOfType(vd, Pattern.class);
          Pattern patt = ((Pattern) _containerOfType_1);
          EList<VarDecl> varDecls = patt.getVarDeclList();
          ArrayList<MyTemporalPrimaryExpr> varDeclsTpe = CollectionLiterals.<MyTemporalPrimaryExpr>newArrayList();
          for (final VarDecl vardec : varDecls) {
            {
              MyTemporalPrimaryExpr mytpe = new MyTemporalPrimaryExpr();
              mytpe.setPointer(vardec);
              varDeclsTpe.add(mytpe);
            }
          }
          for (final MyTemporalPrimaryExpr mtpe_1 : varDeclsTpe) {
            TypeCheckIssue _typeCheckVarDeclvsVarDecl_1 = TypeSystemTemporalRelationalExpr.typeCheckVarDeclvsVarDecl(vd_mtpe, mtpe_1);
            boolean _tripleEquals_2 = (_typeCheckVarDeclvsVarDecl_1 == null);
            if (_tripleEquals_2) {
              theLegalParamsVar.add(mtpe_1.getPointerVarDecl());
            }
          }
        }
        VarType _type = vd.getType();
        VarType vartype = ((VarType) _type);
        EList<TypeConstant> _const = vartype.getConst();
        boolean _tripleNotEquals_1 = (_const != null);
        if (_tripleNotEquals_1) {
          theLegalParamsVar.addAll(vartype.getConst());
        }
        TypeDef _type_1 = vartype.getType();
        if ((_type_1 instanceof TypeDef)) {
          TypeDef _type_2 = vartype.getType();
          TypeDef typedef = ((TypeDef) _type_2);
          VarType _type_3 = typedef.getType();
          boolean _tripleNotEquals_2 = (_type_3 != null);
          if (_tripleNotEquals_2) {
            VarType _type_4 = typedef.getType();
            VarType typeDefType = ((VarType) _type_4);
            theLegalParamsVar.addAll(typeDefType.getConst());
          }
        }
        if (((vartype.getName() != null) && vartype.getName().equals(this.grammarAccess.getVarTypeAccess().getNameBooleanKeyword_0_0_0().getValue()))) {
          acceptor.accept(
            this.createCompletionProposal(this.grammarAccess.getConstantAccess().getBooleanValueFalseKeyword_1_1_0().getValue(), context));
          acceptor.accept(
            this.createCompletionProposal(this.grammarAccess.getConstantAccess().getBooleanValueTrueKeyword_1_3_0().getValue(), context));
        }
      } else {
        TypedParam _extractTypedParamFromTemporalPrimaryExpr = TypeSystemUtils.extractTypedParamFromTemporalPrimaryExpr(tpe);
        boolean _tripleNotEquals_3 = (_extractTypedParamFromTemporalPrimaryExpr != null);
        if (_tripleNotEquals_3) {
          TypedParam _extractTypedParamFromTemporalPrimaryExpr_1 = TypeSystemUtils.extractTypedParamFromTemporalPrimaryExpr(tpe);
          TypedParam typeParam = ((TypedParam) _extractTypedParamFromTemporalPrimaryExpr_1);
          List<VarDecl> varDecls_1 = CollectionLiterals.<VarDecl>newArrayList();
          final EList<Decl> elements = root.getElements();
          for (final Decl elem : elements) {
            if ((elem instanceof Var)) {
              Var var1 = ((Var) elem);
              varDecls_1.add(var1.getVar());
            }
          }
          for (final VarDecl vd_1 : varDecls_1) {
            boolean _isSameVarType = TypeSystemUtils.isSameVarType(vd_1.getType(), typeParam.getType());
            if (_isSameVarType) {
              theLegalParamsVar.add(vd_1);
            }
          }
          theLegalParamsVar.add(typeParam);
        } else {
          TypeConstant _extractTypeConstantFromTemporalPrimaryExpr = TypeSystemUtils.extractTypeConstantFromTemporalPrimaryExpr(tpe);
          boolean _tripleNotEquals_4 = (_extractTypeConstantFromTemporalPrimaryExpr != null);
          if (_tripleNotEquals_4) {
            TypeConstant _extractTypeConstantFromTemporalPrimaryExpr_1 = TypeSystemUtils.extractTypeConstantFromTemporalPrimaryExpr(tpe);
            TypeConstant typeConst = ((TypeConstant) _extractTypeConstantFromTemporalPrimaryExpr_1);
            MyTemporalPrimaryExpr tc_mtpe = new MyTemporalPrimaryExpr();
            tc_mtpe.setPointer(typeConst);
            Pattern _containerOfType_2 = EcoreUtil2.<Pattern>getContainerOfType(typeConst, Pattern.class);
            boolean _tripleEquals_3 = (_containerOfType_2 == null);
            if (_tripleEquals_3) {
              List<MyTemporalPrimaryExpr> varDeclsTPE_1 = CollectionLiterals.<MyTemporalPrimaryExpr>newArrayList();
              varDeclsTPE_1.addAll(this.returnVarDeclsAsMyTemporalPrimaryExpr(root));
              for (final MyTemporalPrimaryExpr mtpe_2 : varDeclsTPE_1) {
                TypeCheckIssue _typeCheckVarDeclVsTypeConstant = TypeSystemTemporalRelationalExpr.typeCheckVarDeclVsTypeConstant(mtpe_2, tc_mtpe);
                boolean _tripleEquals_4 = (_typeCheckVarDeclVsTypeConstant == null);
                if (_tripleEquals_4) {
                  theLegalParamsVar.add(mtpe_2.getPointerVarDecl());
                }
              }
            } else {
              Pattern _containerOfType_3 = EcoreUtil2.<Pattern>getContainerOfType(typeConst, Pattern.class);
              Pattern patt_1 = ((Pattern) _containerOfType_3);
              EList<VarDecl> varDecls_2 = patt_1.getVarDeclList();
              ArrayList<MyTemporalPrimaryExpr> varDeclsTpe_1 = CollectionLiterals.<MyTemporalPrimaryExpr>newArrayList();
              for (final VarDecl vd_2 : varDecls_2) {
                {
                  MyTemporalPrimaryExpr mytpe = new MyTemporalPrimaryExpr();
                  mytpe.setPointer(vd_2);
                  varDeclsTpe_1.add(mytpe);
                }
              }
              for (final MyTemporalPrimaryExpr mtpe_3 : varDeclsTpe_1) {
                TypeCheckIssue _typeCheckVarDeclVsTypeConstant_1 = TypeSystemTemporalRelationalExpr.typeCheckVarDeclVsTypeConstant(mtpe_3, tc_mtpe);
                boolean _tripleEquals_5 = (_typeCheckVarDeclVsTypeConstant_1 == null);
                if (_tripleEquals_5) {
                  theLegalParamsVar.add(mtpe_3.getPointerVarDecl());
                }
              }
            }
          } else {
            PatternParam _extractPatternParamFromTemporalPrimaryExpr = TypeSystemUtils.extractPatternParamFromTemporalPrimaryExpr(tpe);
            boolean _tripleNotEquals_5 = (_extractPatternParamFromTemporalPrimaryExpr != null);
            if (_tripleNotEquals_5) {
              acceptor.accept(
                this.createCompletionProposal(this.grammarAccess.getConstantAccess().getBooleanValueFalseKeyword_1_1_0().getValue(), context));
              acceptor.accept(
                this.createCompletionProposal(this.grammarAccess.getConstantAccess().getBooleanValueTrueKeyword_1_3_0().getValue(), context));
            }
          }
        }
      }
    } else {
      Pattern _containerOfType_4 = EcoreUtil2.<Pattern>getContainerOfType(currModel, Pattern.class);
      boolean _tripleNotEquals_6 = (_containerOfType_4 != null);
      if (_tripleNotEquals_6) {
        Pattern _containerOfType_5 = EcoreUtil2.<Pattern>getContainerOfType(currModel, Pattern.class);
        Pattern patt_2 = ((Pattern) _containerOfType_5);
        EList<VarDecl> vardecls = patt_2.getVarDeclList();
        theLegalParamsVar.addAll(vardecls);
        for (final VarDecl vd_3 : vardecls) {
          EList<TypeConstant> _const_1 = vd_3.getType().getConst();
          boolean _tripleNotEquals_7 = (_const_1 != null);
          if (_tripleNotEquals_7) {
            theLegalParamsVar.addAll(vd_3.getType().getConst());
          }
        }
        PatternParamList _params = patt_2.getParams();
        boolean _tripleNotEquals_8 = (_params != null);
        if (_tripleNotEquals_8) {
          EList<PatternParam> _params_1 = patt_2.getParams().getParams();
          List<PatternParam> pattParam = ((List<PatternParam>) _params_1);
          for (final PatternParam pp : pattParam) {
            theLegalParamsVar.add(pp);
          }
        }
      } else {
        final EList<Decl> elements_1 = root.getElements();
        for (final Decl elem_1 : elements_1) {
          if ((elem_1 instanceof Var)) {
            Var var1_1 = ((Var) elem_1);
            theLegalParamsVar.add(var1_1.getVar());
            EList<TypeConstant> _const_2 = var1_1.getVar().getType().getConst();
            boolean _tripleNotEquals_9 = (_const_2 != null);
            if (_tripleNotEquals_9) {
              theLegalParamsVar.addAll(var1_1.getVar().getType().getConst());
            }
          }
        }
        Predicate _containerOfType_6 = EcoreUtil2.<Predicate>getContainerOfType(currModel, Predicate.class);
        boolean _tripleNotEquals_10 = (_containerOfType_6 != null);
        if (_tripleNotEquals_10) {
          Predicate pred = EcoreUtil2.<Predicate>getContainerOfType(currModel, Predicate.class);
          EList<TypedParam> _params_2 = pred.getParams().getParams();
          theLegalParamsVar.addAll(((List<TypedParam>) _params_2));
        }
      }
    }
    int _size = theLegalParamsVar.size();
    boolean _greaterThan = (_size > 0);
    if (_greaterThan) {
      int _size_1 = theLegalParamsVar.size();
      int _minus = (_size_1 - 1);
      IntegerRange _upTo = new IntegerRange(0, _minus);
      for (final Integer i : _upTo) {
        acceptor.accept(this.createCompletionProposal(theLegalParamsVar.get((i).intValue()).getName(), context));
      }
    }
  }
  
  public List<MyTemporalPrimaryExpr> returnVarDeclsAsMyTemporalPrimaryExpr(final Model root) {
    final EList<Decl> elements = root.getElements();
    List<MyTemporalPrimaryExpr> varDeclsTpe = CollectionLiterals.<MyTemporalPrimaryExpr>newArrayList();
    for (final Decl elem : elements) {
      if ((elem instanceof Var)) {
        Var var1 = ((Var) elem);
        MyTemporalPrimaryExpr mytpe = new MyTemporalPrimaryExpr();
        mytpe.setPointer(var1.getVar());
        varDeclsTpe.add(mytpe);
      }
    }
    return varDeclsTpe;
  }
  
  public void complete_INT(final EObject model, final RuleCall ruleCall, final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
  }
  
  public void completeKeyword(final Keyword keyword, final ContentAssistContext contentAssistContext, final ICompletionProposalAcceptor acceptor) {
  }
  
  public void complete_Counter(final EObject model, final RuleCall ruleCall, final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
  }
}
