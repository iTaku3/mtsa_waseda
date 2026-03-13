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
package tau.smlab.syntech.scoping;

import com.google.common.base.Objects;
import com.google.inject.Provider;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.Scopes;
import org.eclipse.xtext.util.IResourceScopeCache;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import tau.smlab.syntech.spectra.Counter;
import tau.smlab.syntech.spectra.Decl;
import tau.smlab.syntech.spectra.Define;
import tau.smlab.syntech.spectra.DefineArray;
import tau.smlab.syntech.spectra.DefineDecl;
import tau.smlab.syntech.spectra.DefineRegExp;
import tau.smlab.syntech.spectra.DomainVarDecl;
import tau.smlab.syntech.spectra.LTLAsm;
import tau.smlab.syntech.spectra.LTLGar;
import tau.smlab.syntech.spectra.Model;
import tau.smlab.syntech.spectra.Monitor;
import tau.smlab.syntech.spectra.Pattern;
import tau.smlab.syntech.spectra.PatternParamList;
import tau.smlab.syntech.spectra.Predicate;
import tau.smlab.syntech.spectra.QuantifierExpr;
import tau.smlab.syntech.spectra.SpectraPackage;
import tau.smlab.syntech.spectra.TemporalAndExpr;
import tau.smlab.syntech.spectra.TemporalExpression;
import tau.smlab.syntech.spectra.TemporalIffExpr;
import tau.smlab.syntech.spectra.TemporalImpExpr;
import tau.smlab.syntech.spectra.TemporalOrExpr;
import tau.smlab.syntech.spectra.TypeConstant;
import tau.smlab.syntech.spectra.TypeDef;
import tau.smlab.syntech.spectra.TypedParamList;
import tau.smlab.syntech.spectra.Var;
import tau.smlab.syntech.spectra.VarDecl;
import tau.smlab.syntech.spectra.VarType;

/**
 * This class contains custom scoping description.
 * 
 * See https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#scoping
 * on how and when to use it.
 */
@SuppressWarnings("all")
public class SpectraScopeProvider extends AbstractSpectraScopeProvider {
  @Inject
  private IResourceScopeCache cache;
  
  /**
   * select elements in the scope of the <code>reference</code> element
   */
  public IScope getScope(final EObject context, final EReference reference) {
    boolean _equals = Objects.equal(reference, SpectraPackage.Literals.TEMPORAL_PRIMARY_EXPR__POINTER);
    if (_equals) {
      final Decl contextDecl = EcoreUtil2.<Decl>getContainerOfType(context, Decl.class);
      final Model root = EcoreUtil2.<Model>getContainerOfType(context, Model.class);
      final EList<Decl> elements = root.getElements();
      if ((contextDecl instanceof Predicate)) {
        return this.getScopeForTemporalExpressionInPredicate(context, elements);
      } else {
        if ((contextDecl instanceof Pattern)) {
          return this.getScopeForTemporalExpressionInPattern(context, elements);
        } else {
          if ((contextDecl instanceof LTLGar)) {
            return this.getScopeForTemporalExpressionInLTLGar(context, elements);
          } else {
            if ((contextDecl instanceof LTLAsm)) {
              return this.getScopeForTemporalExpressionInLTLAsm(context, elements);
            } else {
              if ((contextDecl instanceof Define)) {
                return this.getScopeForTemporalExpressionInDefine(context, elements);
              } else {
                return this.getScopeForTemporalExpressionInOtherContext(context, elements);
              }
            }
          }
        }
      }
    } else {
      boolean _equals_1 = Objects.equal(reference, SpectraPackage.Literals.TEMPORAL_PRIMARY_EXPR__INDEX);
      if (_equals_1) {
        final Decl contextDecl_1 = EcoreUtil2.<Decl>getContainerOfType(context, Decl.class);
        final Model root_1 = EcoreUtil2.<Model>getContainerOfType(context, Model.class);
        final EList<Decl> elements_1 = root_1.getElements();
        if ((contextDecl_1 instanceof Predicate)) {
          return this.getScopeForIndexInPredicate(context, elements_1);
        } else {
          if ((contextDecl_1 instanceof LTLGar)) {
            return this.getScopeForIndexInLTLGar(context, elements_1);
          } else {
            if ((contextDecl_1 instanceof LTLAsm)) {
              return this.getScopeForIndexInLTLAsm(context, elements_1);
            } else {
              return this.getScopeForIndexInOtherContext(context, elements_1);
            }
          }
        }
      }
    }
    return super.getScope(context, reference);
  }
  
  public IScope getScopeForTemporalExpressionInPredicate(final EObject context, final EList<Decl> elements) {
    final Decl contextDecl = EcoreUtil2.<Decl>getContainerOfType(context, Decl.class);
    final List<EObject> EObjectsInScope = CollectionLiterals.<EObject>newArrayList();
    Resource _eResource = contextDecl.eResource();
    return this.cache.<IScope>get(contextDecl, _eResource, new Provider<IScope>() {
      public IScope get() {
        final ArrayList<TypeConstant> consts = CollectionLiterals.<TypeConstant>newArrayList();
        final ArrayList<VarDecl> varDecls = CollectionLiterals.<VarDecl>newArrayList();
        final Predicate pred = ((Predicate) contextDecl);
        for (final Decl d : elements) {
          if ((d instanceof TypeDef)) {
            TypeDef typedef = ((TypeDef) d);
            VarType _type = typedef.getType();
            boolean _tripleNotEquals = (_type != null);
            if (_tripleNotEquals) {
              consts.addAll(typedef.getType().getConst());
              if (((typedef.getType().getType() != null) && (typedef.getType().getType().getType() != null))) {
                consts.addAll(typedef.getType().getType().getType().getConst());
              }
            }
          } else {
            if ((d instanceof Var)) {
              Var v = ((Var) d);
              VarType _type_1 = v.getVar().getType();
              boolean _tripleNotEquals_1 = (_type_1 != null);
              if (_tripleNotEquals_1) {
                consts.addAll(v.getVar().getType().getConst());
                if (((v.getVar().getType().getType() != null) && (v.getVar().getType().getType().getType() != null))) {
                  consts.addAll(v.getVar().getType().getType().getType().getConst());
                }
              }
              varDecls.add(((Var)d).getVar());
            } else {
              if ((d instanceof Define)) {
                Define define = ((Define) d);
                EObjectsInScope.addAll(define.getDefineList());
              }
            }
          }
        }
        TypedParamList _params = pred.getParams();
        boolean _tripleNotEquals_2 = (_params != null);
        if (_tripleNotEquals_2) {
          EObjectsInScope.addAll(pred.getParams().getParams());
        }
        TemporalExpression _body = pred.getBody();
        if ((_body instanceof TemporalExpression)) {
          TemporalExpression _body_1 = pred.getBody();
          TemporalExpression tmpExpr = ((TemporalExpression) _body_1);
          SpectraScopeProvider.this.addDomainVars(EObjectsInScope, tmpExpr);
        }
        EObjectsInScope.addAll(consts);
        EObjectsInScope.addAll(varDecls);
        return Scopes.scopeFor(EObjectsInScope);
      }
    });
  }
  
  public IScope getScopeForTemporalExpressionInPattern(final EObject context, final EList<Decl> elements) {
    final Decl contextDecl = EcoreUtil2.<Decl>getContainerOfType(context, Decl.class);
    final List<EObject> EObjectsInScope = CollectionLiterals.<EObject>newArrayList();
    Resource _eResource = contextDecl.eResource();
    return this.cache.<IScope>get(contextDecl, _eResource, new Provider<IScope>() {
      public IScope get() {
        final Pattern patt = ((Pattern) contextDecl);
        final ArrayList<TypeConstant> consts = CollectionLiterals.<TypeConstant>newArrayList();
        for (final Decl d : elements) {
          if ((d instanceof TypeDef)) {
            TypeDef typedef = ((TypeDef) d);
            VarType _type = typedef.getType();
            boolean _tripleNotEquals = (_type != null);
            if (_tripleNotEquals) {
              if (((typedef.getType().getType() != null) && (typedef.getType().getType().getType() != null))) {
                consts.addAll(typedef.getType().getType().getType().getConst());
              }
              consts.addAll(typedef.getType().getConst());
            }
          } else {
            if ((d instanceof Counter)) {
              EObjectsInScope.add(d);
            }
          }
        }
        EList<VarDecl> _varDeclList = patt.getVarDeclList();
        final EList<VarDecl> varDeclList = ((EList<VarDecl>) _varDeclList);
        for (final VarDecl d_1 : varDeclList) {
          VarType _type_1 = d_1.getType();
          boolean _tripleNotEquals_1 = (_type_1 != null);
          if (_tripleNotEquals_1) {
            consts.addAll(d_1.getType().getConst());
          }
        }
        EObjectsInScope.addAll(consts);
        EObjectsInScope.addAll(patt.getVarDeclList());
        PatternParamList _params = patt.getParams();
        boolean _tripleNotEquals_2 = (_params != null);
        if (_tripleNotEquals_2) {
          EObjectsInScope.addAll(patt.getParams().getParams());
        }
        return Scopes.scopeFor(EObjectsInScope);
      }
    });
  }
  
  public IScope getScopeForTemporalExpressionInLTLGar(final EObject context, final EList<Decl> elements) {
    final Decl contextDecl = EcoreUtil2.<Decl>getContainerOfType(context, Decl.class);
    final List<EObject> EObjectsInScope = CollectionLiterals.<EObject>newArrayList();
    Resource _eResource = contextDecl.eResource();
    return this.cache.<IScope>get(contextDecl, _eResource, new Provider<IScope>() {
      public IScope get() {
        final LTLGar ltlGar = ((LTLGar) contextDecl);
        final List<Pattern> PattList = CollectionLiterals.<Pattern>newArrayList();
        final List<Predicate> PredList = CollectionLiterals.<Predicate>newArrayList();
        TemporalExpression _temporalExpr = ltlGar.getTemporalExpr();
        boolean _tripleNotEquals = (_temporalExpr != null);
        if (_tripleNotEquals) {
          TemporalExpression _temporalExpr_1 = ltlGar.getTemporalExpr();
          TemporalExpression tmpExpr = ((TemporalExpression) _temporalExpr_1);
          SpectraScopeProvider.this.addDomainVars(EObjectsInScope, tmpExpr);
        }
        TypedParamList _params = ltlGar.getParams();
        boolean _tripleNotEquals_1 = (_params != null);
        if (_tripleNotEquals_1) {
          EObjectsInScope.addAll(ltlGar.getParams().getParams());
        }
        for (final Decl d : elements) {
          if ((d instanceof TypeDef)) {
            TypeDef typedef = ((TypeDef) d);
            if (((typedef.getType() != null) && (typedef.getType().getConst() != null))) {
              EObjectsInScope.addAll(typedef.getType().getConst());
            }
          } else {
            if ((d instanceof Var)) {
              Var v = ((Var) d);
              VarDecl _var = v.getVar();
              boolean _tripleNotEquals_2 = (_var != null);
              if (_tripleNotEquals_2) {
                VarType _type = v.getVar().getType();
                boolean _tripleNotEquals_3 = (_type != null);
                if (_tripleNotEquals_3) {
                  EObjectsInScope.addAll(v.getVar().getType().getConst());
                }
                EObjectsInScope.add(((Var)d).getVar());
              }
            } else {
              if ((d instanceof Define)) {
                EObjectsInScope.addAll(((Define)d).getDefineList());
              } else {
                if ((d instanceof Monitor)) {
                  EObjectsInScope.add(d);
                } else {
                  if ((d instanceof Pattern)) {
                    PattList.add(((Pattern) d));
                  } else {
                    if ((d instanceof Predicate)) {
                      PredList.add(((Predicate) d));
                    } else {
                      if ((d instanceof Counter)) {
                        EObjectsInScope.add(d);
                      } else {
                        if ((d instanceof DefineRegExp)) {
                          EObjectsInScope.addAll(((DefineRegExp)d).getDefineRegsList());
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
        EObjectsInScope.addAll(PattList);
        for (final Pattern p : PattList) {
          {
            final ArrayList<TypeConstant> consts = CollectionLiterals.<TypeConstant>newArrayList();
            EList<VarDecl> _varDeclList = p.getVarDeclList();
            final EList<VarDecl> varDeclList = ((EList<VarDecl>) _varDeclList);
            for (final VarDecl vd : varDeclList) {
              consts.addAll(vd.getType().getConst());
            }
            EObjectsInScope.removeAll(consts);
            EObjectsInScope.removeAll(p.getVarDeclList());
            PatternParamList _params_1 = p.getParams();
            boolean _tripleNotEquals_4 = (_params_1 != null);
            if (_tripleNotEquals_4) {
              EObjectsInScope.removeAll(p.getParams().getParams());
            }
          }
        }
        EObjectsInScope.addAll(PredList);
        for (final Predicate p_1 : PredList) {
          {
            TypedParamList _params_1 = p_1.getParams();
            boolean _tripleNotEquals_4 = (_params_1 != null);
            if (_tripleNotEquals_4) {
              EObjectsInScope.removeAll(p_1.getParams().getParams());
            }
            EObjectsInScope.removeAll(EcoreUtil2.<TypeConstant>getAllContentsOfType(p_1, TypeConstant.class));
          }
        }
        return Scopes.scopeFor(EObjectsInScope);
      }
    });
  }
  
  public IScope getScopeForTemporalExpressionInLTLAsm(final EObject context, final EList<Decl> elements) {
    final Decl contextDecl = EcoreUtil2.<Decl>getContainerOfType(context, Decl.class);
    final List<EObject> EObjectsInScope = CollectionLiterals.<EObject>newArrayList();
    Resource _eResource = contextDecl.eResource();
    return this.cache.<IScope>get(contextDecl, _eResource, new Provider<IScope>() {
      public IScope get() {
        final LTLAsm ltlAsm = ((LTLAsm) contextDecl);
        final List<Pattern> PattList = CollectionLiterals.<Pattern>newArrayList();
        final List<Predicate> PredList = CollectionLiterals.<Predicate>newArrayList();
        TemporalExpression _temporalExpr = ltlAsm.getTemporalExpr();
        boolean _tripleNotEquals = (_temporalExpr != null);
        if (_tripleNotEquals) {
          TemporalExpression _temporalExpr_1 = ltlAsm.getTemporalExpr();
          TemporalExpression tmpExpr = ((TemporalExpression) _temporalExpr_1);
          SpectraScopeProvider.this.addDomainVars(EObjectsInScope, tmpExpr);
        }
        TypedParamList _params = ltlAsm.getParams();
        boolean _tripleNotEquals_1 = (_params != null);
        if (_tripleNotEquals_1) {
          EObjectsInScope.addAll(ltlAsm.getParams().getParams());
        }
        for (final Decl d : elements) {
          if ((d instanceof TypeDef)) {
            TypeDef typedef = ((TypeDef) d);
            if (((typedef.getType() != null) && (typedef.getType().getConst() != null))) {
              EObjectsInScope.addAll(typedef.getType().getConst());
            }
          } else {
            if ((d instanceof Var)) {
              Var v = ((Var) d);
              VarDecl _var = v.getVar();
              boolean _tripleNotEquals_2 = (_var != null);
              if (_tripleNotEquals_2) {
                VarType _type = v.getVar().getType();
                boolean _tripleNotEquals_3 = (_type != null);
                if (_tripleNotEquals_3) {
                  EObjectsInScope.addAll(v.getVar().getType().getConst());
                }
                EObjectsInScope.add(((Var)d).getVar());
              }
            } else {
              if ((d instanceof Define)) {
                EObjectsInScope.addAll(((Define)d).getDefineList());
              } else {
                if ((d instanceof Monitor)) {
                  EObjectsInScope.add(d);
                } else {
                  if ((d instanceof Pattern)) {
                    PattList.add(((Pattern) d));
                  } else {
                    if ((d instanceof Predicate)) {
                      PredList.add(((Predicate) d));
                    } else {
                      if ((d instanceof Counter)) {
                        EObjectsInScope.add(d);
                      } else {
                        if ((d instanceof DefineRegExp)) {
                          EObjectsInScope.addAll(((DefineRegExp)d).getDefineRegsList());
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
        EObjectsInScope.addAll(PattList);
        for (final Pattern p : PattList) {
          {
            final ArrayList<TypeConstant> consts = CollectionLiterals.<TypeConstant>newArrayList();
            EList<VarDecl> _varDeclList = p.getVarDeclList();
            final EList<VarDecl> varDeclList = ((EList<VarDecl>) _varDeclList);
            for (final VarDecl vd : varDeclList) {
              consts.addAll(vd.getType().getConst());
            }
            EObjectsInScope.removeAll(consts);
            EObjectsInScope.removeAll(p.getVarDeclList());
            PatternParamList _params_1 = p.getParams();
            boolean _tripleNotEquals_4 = (_params_1 != null);
            if (_tripleNotEquals_4) {
              EObjectsInScope.removeAll(p.getParams().getParams());
            }
          }
        }
        EObjectsInScope.addAll(PredList);
        for (final Predicate p_1 : PredList) {
          {
            TypedParamList _params_1 = p_1.getParams();
            boolean _tripleNotEquals_4 = (_params_1 != null);
            if (_tripleNotEquals_4) {
              EObjectsInScope.removeAll(p_1.getParams().getParams());
            }
            EObjectsInScope.removeAll(EcoreUtil2.<TypeConstant>getAllContentsOfType(p_1, TypeConstant.class));
          }
        }
        return Scopes.scopeFor(EObjectsInScope);
      }
    });
  }
  
  public IScope getScopeForTemporalExpressionInDefine(final EObject context, final EList<Decl> elements) {
    final Decl contextDecl = EcoreUtil2.<Decl>getContainerOfType(context, Decl.class);
    final List<EObject> EObjectsInScope = CollectionLiterals.<EObject>newArrayList();
    Resource _eResource = contextDecl.eResource();
    return this.cache.<IScope>get(contextDecl, _eResource, new Provider<IScope>() {
      public IScope get() {
        final Define define = ((Define) contextDecl);
        final List<Pattern> PattList = CollectionLiterals.<Pattern>newArrayList();
        final List<Predicate> PredList = CollectionLiterals.<Predicate>newArrayList();
        for (final Decl d : elements) {
          if ((d instanceof TypeDef)) {
            TypeDef typedef = ((TypeDef) d);
            if (((typedef.getType() != null) && (typedef.getType().getConst() != null))) {
              EObjectsInScope.addAll(typedef.getType().getConst());
            }
          } else {
            if ((d instanceof Var)) {
              Var v = ((Var) d);
              VarDecl _var = v.getVar();
              boolean _tripleNotEquals = (_var != null);
              if (_tripleNotEquals) {
                VarType _type = v.getVar().getType();
                boolean _tripleNotEquals_1 = (_type != null);
                if (_tripleNotEquals_1) {
                  EObjectsInScope.addAll(v.getVar().getType().getConst());
                }
                EObjectsInScope.add(((Var)d).getVar());
              }
            } else {
              if ((d instanceof Define)) {
                EObjectsInScope.addAll(((Define)d).getDefineList());
              } else {
                if ((d instanceof Monitor)) {
                  EObjectsInScope.add(d);
                } else {
                  if ((d instanceof Counter)) {
                    EObjectsInScope.add(d);
                  } else {
                    if ((d instanceof Pattern)) {
                      PattList.add(((Pattern) d));
                    } else {
                      if ((d instanceof Predicate)) {
                        PredList.add(((Predicate) d));
                      } else {
                        if ((d instanceof DefineRegExp)) {
                          EObjectsInScope.addAll(((DefineRegExp)d).getDefineRegsList());
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
        EObjectsInScope.addAll(PattList);
        EList<DefineDecl> _defineList = define.getDefineList();
        for (final DefineDecl decl : _defineList) {
          TemporalExpression _simpleExpr = decl.getSimpleExpr();
          boolean _tripleNotEquals_2 = (_simpleExpr != null);
          if (_tripleNotEquals_2) {
            TemporalExpression _simpleExpr_1 = decl.getSimpleExpr();
            TemporalExpression tmpExpr = ((TemporalExpression) _simpleExpr_1);
            SpectraScopeProvider.this.addDomainVars(EObjectsInScope, tmpExpr);
          } else {
            SpectraScopeProvider.this.addDomainVarsForDefine(EObjectsInScope, decl.getInnerArray());
          }
        }
        for (final Pattern p : PattList) {
          {
            final ArrayList<TypeConstant> consts = CollectionLiterals.<TypeConstant>newArrayList();
            EList<VarDecl> _varDeclList = p.getVarDeclList();
            final EList<VarDecl> varDeclList = ((EList<VarDecl>) _varDeclList);
            for (final VarDecl vd : varDeclList) {
              consts.addAll(vd.getType().getConst());
            }
            EObjectsInScope.removeAll(consts);
            EObjectsInScope.removeAll(p.getVarDeclList());
            PatternParamList _params = p.getParams();
            boolean _tripleNotEquals_3 = (_params != null);
            if (_tripleNotEquals_3) {
              EObjectsInScope.removeAll(p.getParams().getParams());
            }
          }
        }
        EObjectsInScope.addAll(PredList);
        for (final Predicate p_1 : PredList) {
          {
            TypedParamList _params = p_1.getParams();
            boolean _tripleNotEquals_3 = (_params != null);
            if (_tripleNotEquals_3) {
              EObjectsInScope.removeAll(p_1.getParams().getParams());
            }
            EObjectsInScope.removeAll(EcoreUtil2.<TypeConstant>getAllContentsOfType(p_1, TypeConstant.class));
          }
        }
        return Scopes.scopeFor(EObjectsInScope);
      }
    });
  }
  
  public IScope getScopeForTemporalExpressionInOtherContext(final EObject context, final EList<Decl> elements) {
    final Model root = EcoreUtil2.<Model>getContainerOfType(context, Model.class);
    final List<EObject> EObjectsInScope = CollectionLiterals.<EObject>newArrayList();
    Resource _eResource = root.eResource();
    return this.cache.<IScope>get(root, _eResource, new Provider<IScope>() {
      public IScope get() {
        final List<Pattern> PattList = CollectionLiterals.<Pattern>newArrayList();
        final List<Predicate> PredList = CollectionLiterals.<Predicate>newArrayList();
        for (final Decl d : elements) {
          if ((d instanceof TypeDef)) {
            TypeDef typedef = ((TypeDef) d);
            if (((typedef.getType() != null) && (typedef.getType().getConst() != null))) {
              EObjectsInScope.addAll(typedef.getType().getConst());
            }
          } else {
            if ((d instanceof Var)) {
              Var v = ((Var) d);
              VarDecl _var = v.getVar();
              boolean _tripleNotEquals = (_var != null);
              if (_tripleNotEquals) {
                VarType _type = v.getVar().getType();
                boolean _tripleNotEquals_1 = (_type != null);
                if (_tripleNotEquals_1) {
                  EObjectsInScope.addAll(v.getVar().getType().getConst());
                }
                EObjectsInScope.add(((Var)d).getVar());
              }
            } else {
              if ((d instanceof Define)) {
                EObjectsInScope.addAll(((Define)d).getDefineList());
              } else {
                if ((d instanceof Monitor)) {
                  EObjectsInScope.add(d);
                } else {
                  if ((d instanceof Counter)) {
                    EObjectsInScope.add(d);
                  } else {
                    if ((d instanceof Pattern)) {
                      PattList.add(((Pattern) d));
                    } else {
                      if ((d instanceof Predicate)) {
                        PredList.add(((Predicate) d));
                      } else {
                        if ((d instanceof DefineRegExp)) {
                          EObjectsInScope.addAll(((DefineRegExp)d).getDefineRegsList());
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
        EObjectsInScope.addAll(PattList);
        for (final Pattern p : PattList) {
          {
            final ArrayList<TypeConstant> consts = CollectionLiterals.<TypeConstant>newArrayList();
            EList<VarDecl> _varDeclList = p.getVarDeclList();
            final EList<VarDecl> varDeclList = ((EList<VarDecl>) _varDeclList);
            for (final VarDecl vd : varDeclList) {
              consts.addAll(vd.getType().getConst());
            }
            EObjectsInScope.removeAll(consts);
            EObjectsInScope.removeAll(p.getVarDeclList());
            PatternParamList _params = p.getParams();
            boolean _tripleNotEquals_2 = (_params != null);
            if (_tripleNotEquals_2) {
              EObjectsInScope.removeAll(p.getParams().getParams());
            }
          }
        }
        EObjectsInScope.addAll(PredList);
        for (final Predicate p_1 : PredList) {
          {
            TypedParamList _params = p_1.getParams();
            boolean _tripleNotEquals_2 = (_params != null);
            if (_tripleNotEquals_2) {
              EObjectsInScope.removeAll(p_1.getParams().getParams());
            }
            EObjectsInScope.removeAll(EcoreUtil2.<TypeConstant>getAllContentsOfType(p_1, TypeConstant.class));
          }
        }
        return Scopes.scopeFor(EObjectsInScope);
      }
    });
  }
  
  public IScope getScopeForIndexInPredicate(final EObject context, final EList<Decl> elements) {
    final Decl contextDecl = EcoreUtil2.<Decl>getContainerOfType(context, Decl.class);
    final List<EObject> EObjectsInScope = CollectionLiterals.<EObject>newArrayList();
    Resource _eResource = contextDecl.eResource();
    return this.cache.<IScope>get(contextDecl, _eResource, new Provider<IScope>() {
      public IScope get() {
        final ArrayList<VarDecl> varDecls = CollectionLiterals.<VarDecl>newArrayList();
        final Predicate pred = ((Predicate) contextDecl);
        for (final Decl d : elements) {
          if ((d instanceof Var)) {
            varDecls.add(((Var)d).getVar());
          }
        }
        TypedParamList _params = pred.getParams();
        boolean _tripleNotEquals = (_params != null);
        if (_tripleNotEquals) {
          EObjectsInScope.addAll(pred.getParams().getParams());
        }
        EObjectsInScope.addAll(varDecls);
        return Scopes.scopeFor(EObjectsInScope);
      }
    });
  }
  
  public IScope getScopeForIndexInLTLGar(final EObject context, final EList<Decl> elements) {
    final Decl contextDecl = EcoreUtil2.<Decl>getContainerOfType(context, Decl.class);
    final List<EObject> EObjectsInScope = CollectionLiterals.<EObject>newArrayList();
    Resource _eResource = contextDecl.eResource();
    return this.cache.<IScope>get(contextDecl, _eResource, new Provider<IScope>() {
      public IScope get() {
        final LTLGar ltlGar = ((LTLGar) contextDecl);
        TemporalExpression _temporalExpr = ltlGar.getTemporalExpr();
        boolean _tripleNotEquals = (_temporalExpr != null);
        if (_tripleNotEquals) {
          TemporalExpression _temporalExpr_1 = ltlGar.getTemporalExpr();
          if ((_temporalExpr_1 instanceof QuantifierExpr)) {
            TemporalExpression _temporalExpr_2 = ltlGar.getTemporalExpr();
            QuantifierExpr qe = ((QuantifierExpr) _temporalExpr_2);
            DomainVarDecl _domainVar = qe.getDomainVar();
            boolean _tripleNotEquals_1 = (_domainVar != null);
            if (_tripleNotEquals_1) {
              EObjectsInScope.add(qe.getDomainVar());
            }
          }
        }
        for (final Decl d : elements) {
          if ((d instanceof Var)) {
            Var v = ((Var) d);
            VarDecl _var = v.getVar();
            boolean _tripleNotEquals_2 = (_var != null);
            if (_tripleNotEquals_2) {
              EObjectsInScope.add(((Var)d).getVar());
            }
          }
        }
        return Scopes.scopeFor(EObjectsInScope);
      }
    });
  }
  
  public IScope getScopeForIndexInLTLAsm(final EObject context, final EList<Decl> elements) {
    final Decl contextDecl = EcoreUtil2.<Decl>getContainerOfType(context, Decl.class);
    final List<EObject> EObjectsInScope = CollectionLiterals.<EObject>newArrayList();
    Resource _eResource = contextDecl.eResource();
    return this.cache.<IScope>get(contextDecl, _eResource, new Provider<IScope>() {
      public IScope get() {
        final LTLAsm ltlAsm = ((LTLAsm) contextDecl);
        TemporalExpression _temporalExpr = ltlAsm.getTemporalExpr();
        boolean _tripleNotEquals = (_temporalExpr != null);
        if (_tripleNotEquals) {
          TemporalExpression _temporalExpr_1 = ltlAsm.getTemporalExpr();
          if ((_temporalExpr_1 instanceof QuantifierExpr)) {
            TemporalExpression _temporalExpr_2 = ltlAsm.getTemporalExpr();
            QuantifierExpr qe = ((QuantifierExpr) _temporalExpr_2);
            DomainVarDecl _domainVar = qe.getDomainVar();
            boolean _tripleNotEquals_1 = (_domainVar != null);
            if (_tripleNotEquals_1) {
              EObjectsInScope.add(qe.getDomainVar());
            }
          }
        }
        for (final Decl d : elements) {
          if ((d instanceof Var)) {
            Var v = ((Var) d);
            VarDecl _var = v.getVar();
            boolean _tripleNotEquals_2 = (_var != null);
            if (_tripleNotEquals_2) {
              EObjectsInScope.add(((Var)d).getVar());
            }
          }
        }
        return Scopes.scopeFor(EObjectsInScope);
      }
    });
  }
  
  public IScope getScopeForIndexInOtherContext(final EObject context, final EList<Decl> elements) {
    final Model root = EcoreUtil2.<Model>getContainerOfType(context, Model.class);
    final List<EObject> EObjectsInScope = CollectionLiterals.<EObject>newArrayList();
    Resource _eResource = root.eResource();
    return this.cache.<IScope>get(root, _eResource, new Provider<IScope>() {
      public IScope get() {
        for (final Decl d : elements) {
          if ((d instanceof Var)) {
            Var v = ((Var) d);
            VarDecl _var = v.getVar();
            boolean _tripleNotEquals = (_var != null);
            if (_tripleNotEquals) {
              EObjectsInScope.add(((Var)d).getVar());
            }
          }
        }
        return Scopes.scopeFor(EObjectsInScope);
      }
    });
  }
  
  public void addDomainVarsForDefine(final List<EObject> EObjectsInScope, final DefineArray defArray) {
    EList<DefineArray> _innerArrays = defArray.getInnerArrays();
    for (final DefineArray innerArray : _innerArrays) {
      this.addDomainVarsForDefine(EObjectsInScope, innerArray);
    }
    EList<TemporalExpression> _simpleExprs = defArray.getSimpleExprs();
    for (final TemporalExpression exp : _simpleExprs) {
      this.addDomainVars(EObjectsInScope, exp);
    }
  }
  
  public void addDomainVars(final List<EObject> EObjectsInScope, final TemporalExpression tempExp) {
    if ((tempExp instanceof TemporalImpExpr)) {
      TemporalImpExpr imp = ((TemporalImpExpr) tempExp);
      this.addDomainVars(EObjectsInScope, imp.getLeft());
      this.addDomainVars(EObjectsInScope, imp.getImplication());
    } else {
      if ((tempExp instanceof TemporalIffExpr)) {
        TemporalIffExpr iff = ((TemporalIffExpr) tempExp);
        EList<TemporalExpression> _elements = iff.getElements();
        for (final TemporalExpression exp : _elements) {
          this.addDomainVars(EObjectsInScope, exp);
        }
      } else {
        if ((tempExp instanceof TemporalOrExpr)) {
          TemporalOrExpr or = ((TemporalOrExpr) tempExp);
          EList<TemporalExpression> _elements_1 = or.getElements();
          for (final TemporalExpression exp_1 : _elements_1) {
            this.addDomainVars(EObjectsInScope, exp_1);
          }
        } else {
          if ((tempExp instanceof TemporalAndExpr)) {
            TemporalAndExpr and = ((TemporalAndExpr) tempExp);
            EList<TemporalExpression> _elements_2 = and.getElements();
            for (final TemporalExpression exp_2 : _elements_2) {
              this.addDomainVars(EObjectsInScope, exp_2);
            }
          } else {
            if ((tempExp instanceof QuantifierExpr)) {
              QuantifierExpr qe = ((QuantifierExpr) tempExp);
              DomainVarDecl _domainVar = qe.getDomainVar();
              boolean _tripleNotEquals = (_domainVar != null);
              if (_tripleNotEquals) {
                EObjectsInScope.add(qe.getDomainVar());
              }
              this.addDomainVars(EObjectsInScope, qe.getTemporalExpr());
            }
          }
        }
      }
    }
  }
}
