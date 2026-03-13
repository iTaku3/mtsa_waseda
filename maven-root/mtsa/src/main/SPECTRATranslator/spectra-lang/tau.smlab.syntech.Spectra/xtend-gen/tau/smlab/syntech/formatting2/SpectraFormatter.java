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
package tau.smlab.syntech.formatting2;

import com.google.common.base.Objects;
import java.util.Arrays;
import javax.inject.Inject;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.formatting2.AbstractFormatter2;
import org.eclipse.xtext.formatting2.IFormattableDocument;
import org.eclipse.xtext.formatting2.IHiddenRegionFormatter;
import org.eclipse.xtext.formatting2.regionaccess.ISemanticRegion;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import tau.smlab.syntech.services.SpectraGrammarAccess;
import tau.smlab.syntech.spectra.Decl;
import tau.smlab.syntech.spectra.Define;
import tau.smlab.syntech.spectra.DefineArray;
import tau.smlab.syntech.spectra.DefineDecl;
import tau.smlab.syntech.spectra.Import;
import tau.smlab.syntech.spectra.LTLAsm;
import tau.smlab.syntech.spectra.LTLGar;
import tau.smlab.syntech.spectra.Model;
import tau.smlab.syntech.spectra.Pattern;
import tau.smlab.syntech.spectra.PatternParam;
import tau.smlab.syntech.spectra.PatternParamList;
import tau.smlab.syntech.spectra.Predicate;
import tau.smlab.syntech.spectra.Subrange;
import tau.smlab.syntech.spectra.TemporalAdditiveExpr;
import tau.smlab.syntech.spectra.TemporalAndExpr;
import tau.smlab.syntech.spectra.TemporalBinaryExpr;
import tau.smlab.syntech.spectra.TemporalExpression;
import tau.smlab.syntech.spectra.TemporalIffExpr;
import tau.smlab.syntech.spectra.TemporalImpExpr;
import tau.smlab.syntech.spectra.TemporalMultiplicativeExpr;
import tau.smlab.syntech.spectra.TemporalOrExpr;
import tau.smlab.syntech.spectra.TemporalPrimaryExpr;
import tau.smlab.syntech.spectra.TemporalRelationalExpr;
import tau.smlab.syntech.spectra.TemporalRemainderExpr;
import tau.smlab.syntech.spectra.TemporalUnaryExpr;
import tau.smlab.syntech.spectra.TypeConstant;
import tau.smlab.syntech.spectra.TypeDef;
import tau.smlab.syntech.spectra.TypedParam;
import tau.smlab.syntech.spectra.TypedParamList;
import tau.smlab.syntech.spectra.Var;
import tau.smlab.syntech.spectra.VarDecl;
import tau.smlab.syntech.spectra.VarOwner;
import tau.smlab.syntech.spectra.VarType;
import tau.smlab.syntech.spectra.WeightDef;

@SuppressWarnings("all")
public class SpectraFormatter extends AbstractFormatter2 {
  @Inject
  private SpectraGrammarAccess grammarAccess;
  
  protected void _format(final Model model, @Extension final IFormattableDocument document) {
    final Procedure1<IHiddenRegionFormatter> _function = new Procedure1<IHiddenRegionFormatter>() {
      public void apply(final IHiddenRegionFormatter it) {
        it.setNewLines(0, 0, 1);
        it.noSpace();
      }
    };
    final Procedure1<IHiddenRegionFormatter> _function_1 = new Procedure1<IHiddenRegionFormatter>() {
      public void apply(final IHiddenRegionFormatter it) {
        it.newLine();
      }
    };
    document.<Model>append(document.<Model>prepend(model, _function), _function_1);
    final Procedure1<IHiddenRegionFormatter> _function_2 = new Procedure1<IHiddenRegionFormatter>() {
      public void apply(final IHiddenRegionFormatter it) {
        it.oneSpace();
      }
    };
    document.append(this.textRegionExtensions.regionFor(model).keyword(this.grammarAccess.getModelAccess().getSpecKeyword_1_1().getValue()), _function_2);
    EList<Import> _imports = model.getImports();
    for (final Import imports : _imports) {
      document.<Import>format(imports);
    }
    EList<Decl> _elements = model.getElements();
    for (final Decl elements : _elements) {
      document.<Decl>format(elements);
    }
  }
  
  protected void _format(final Import _import, @Extension final IFormattableDocument document) {
    final Procedure1<IHiddenRegionFormatter> _function = new Procedure1<IHiddenRegionFormatter>() {
      public void apply(final IHiddenRegionFormatter it) {
        it.noSpace();
      }
    };
    final Procedure1<IHiddenRegionFormatter> _function_1 = new Procedure1<IHiddenRegionFormatter>() {
      public void apply(final IHiddenRegionFormatter it) {
        it.oneSpace();
      }
    };
    document.append(document.prepend(this.textRegionExtensions.regionFor(_import).keyword(this.grammarAccess.getImportAccess().getImportKeyword_0().getValue()), _function), _function_1);
  }
  
  protected void _format(final Var _var, @Extension final IFormattableDocument document) {
    document.<VarOwner>format(_var.getKind());
    document.<VarDecl>format(_var.getVar());
  }
  
  protected void _format(final VarOwner varOwner, @Extension final IFormattableDocument document) {
  }
  
  protected void _format(final VarDecl varDecl, @Extension final IFormattableDocument document) {
    document.<VarType>format(varDecl.getType());
  }
  
  protected void _format(final VarType varType, @Extension final IFormattableDocument document) {
    final Procedure1<IHiddenRegionFormatter> _function = new Procedure1<IHiddenRegionFormatter>() {
      public void apply(final IHiddenRegionFormatter it) {
        it.oneSpace();
      }
    };
    final Procedure1<IHiddenRegionFormatter> _function_1 = new Procedure1<IHiddenRegionFormatter>() {
      public void apply(final IHiddenRegionFormatter it) {
        it.noSpace();
      }
    };
    document.append(document.prepend(this.textRegionExtensions.regionFor(varType).keyword(this.grammarAccess.getVarTypeAccess().getLeftCurlyBracketKeyword_0_2_0().getValue()), _function), _function_1);
    final Procedure1<IHiddenRegionFormatter> _function_2 = new Procedure1<IHiddenRegionFormatter>() {
      public void apply(final IHiddenRegionFormatter it) {
        it.noSpace();
      }
    };
    final Procedure1<IHiddenRegionFormatter> _function_3 = new Procedure1<IHiddenRegionFormatter>() {
      public void apply(final IHiddenRegionFormatter it) {
        it.noSpace();
      }
    };
    document.append(document.prepend(this.textRegionExtensions.regionFor(varType).keyword(this.grammarAccess.getVarTypeAccess().getRightCurlyBracketKeyword_0_2_3().getValue()), _function_2), _function_3);
    final Procedure1<IHiddenRegionFormatter> _function_4 = new Procedure1<IHiddenRegionFormatter>() {
      public void apply(final IHiddenRegionFormatter it) {
        it.noSpace();
      }
    };
    final Procedure1<IHiddenRegionFormatter> _function_5 = new Procedure1<IHiddenRegionFormatter>() {
      public void apply(final IHiddenRegionFormatter it) {
        it.oneSpace();
      }
    };
    document.append(document.prepend(this.textRegionExtensions.regionFor(varType).keyword(this.grammarAccess.getVarTypeAccess().getCommaKeyword_0_2_2_0().getValue()), _function_4), _function_5);
    document.<Subrange>format(varType.getSubr());
    document.<TypeDef>format(varType.getType());
  }
  
  protected void _format(final Subrange subrange, @Extension final IFormattableDocument document) {
    final Procedure1<IHiddenRegionFormatter> _function = new Procedure1<IHiddenRegionFormatter>() {
      public void apply(final IHiddenRegionFormatter it) {
        it.noSpace();
      }
    };
    final Procedure1<IHiddenRegionFormatter> _function_1 = new Procedure1<IHiddenRegionFormatter>() {
      public void apply(final IHiddenRegionFormatter it) {
        it.noSpace();
      }
    };
    document.append(document.prepend(this.textRegionExtensions.regionFor(subrange).keyword(this.grammarAccess.getSubrangeAccess().getFullStopFullStopKeyword_1().getValue()), _function), _function_1);
  }
  
  protected void _format(final TypeConstant typeConstant, @Extension final IFormattableDocument document) {
  }
  
  protected void _format(final TypeDef typeDef, @Extension final IFormattableDocument document) {
    final Procedure1<IHiddenRegionFormatter> _function = new Procedure1<IHiddenRegionFormatter>() {
      public void apply(final IHiddenRegionFormatter it) {
        it.setNewLines(0, 0, 2);
      }
    };
    final Procedure1<IHiddenRegionFormatter> _function_1 = new Procedure1<IHiddenRegionFormatter>() {
      public void apply(final IHiddenRegionFormatter it) {
        it.oneSpace();
      }
    };
    document.append(document.prepend(this.textRegionExtensions.regionFor(typeDef).keyword(this.grammarAccess.getTypeDefAccess().getTypeKeyword_0().getValue()), _function), _function_1);
    document.<VarType>format(typeDef.getType());
  }
  
  protected void _format(final Define define, @Extension final IFormattableDocument document) {
    final Procedure1<IHiddenRegionFormatter> _function = new Procedure1<IHiddenRegionFormatter>() {
      public void apply(final IHiddenRegionFormatter it) {
        it.setNewLines(0, 0, 2);
      }
    };
    final Procedure1<IHiddenRegionFormatter> _function_1 = new Procedure1<IHiddenRegionFormatter>() {
      public void apply(final IHiddenRegionFormatter it) {
        it.oneSpace();
      }
    };
    document.append(document.prepend(this.textRegionExtensions.regionFor(define).keyword(this.grammarAccess.getDefineAccess().getDefineKeyword_0().getValue()), _function), _function_1);
    EList<DefineDecl> _defineList = define.getDefineList();
    for (final DefineDecl defineDecl : _defineList) {
      document.<DefineDecl>format(defineDecl);
    }
    final Procedure1<IHiddenRegionFormatter> _function_2 = new Procedure1<IHiddenRegionFormatter>() {
      public void apply(final IHiddenRegionFormatter it) {
        it.setNewLines(1, 1, 1);
      }
    };
    document.append(this.textRegionExtensions.regionFor(define).keyword(this.grammarAccess.getTOK_SEMIAccess().getSemicolonKeyword().getValue()), _function_2);
  }
  
  protected void _format(final DefineDecl defineDecl, @Extension final IFormattableDocument document) {
    final Procedure1<IHiddenRegionFormatter> _function = new Procedure1<IHiddenRegionFormatter>() {
      public void apply(final IHiddenRegionFormatter it) {
        it.oneSpace();
      }
    };
    document.surround(this.textRegionExtensions.regionFor(defineDecl).keyword(this.grammarAccess.getDefineDeclAccess().getColonEqualsSignKeyword_0_0_1().getValue()), _function);
    document.<TemporalExpression>format(defineDecl.getSimpleExpr());
    final Procedure1<IHiddenRegionFormatter> _function_1 = new Procedure1<IHiddenRegionFormatter>() {
      public void apply(final IHiddenRegionFormatter it) {
        it.oneSpace();
      }
    };
    document.surround(this.textRegionExtensions.regionFor(defineDecl).keyword(this.grammarAccess.getDefineDeclAccess().getColonEqualsSignKeyword_0_1_2().getValue()), _function_1);
    this.format(defineDecl.getInnerArray(), document);
  }
  
  protected void _format(final DefineArray defineArray, @Extension final IFormattableDocument document) {
    EList<DefineArray> _innerArrays = defineArray.getInnerArrays();
    for (final DefineArray innerArray : _innerArrays) {
      this.format(innerArray, document);
    }
    EList<TemporalExpression> _simpleExprs = defineArray.getSimpleExprs();
    for (final TemporalExpression temporalExpr : _simpleExprs) {
      document.<TemporalExpression>format(temporalExpr);
    }
  }
  
  protected void _format(final TemporalImpExpr temporalImpExpr, @Extension final IFormattableDocument document) {
    document.<TemporalExpression>format(temporalImpExpr.getLeft());
    final Procedure1<IHiddenRegionFormatter> _function = new Procedure1<IHiddenRegionFormatter>() {
      public void apply(final IHiddenRegionFormatter it) {
        it.oneSpace();
      }
    };
    document.surround(this.textRegionExtensions.regionFor(temporalImpExpr).keyword(
      this.grammarAccess.getTemporalImpExprAccess().getOperatorHyphenMinusGreaterThanSignKeyword_1_1_0_0().getValue()), _function);
    final Procedure1<IHiddenRegionFormatter> _function_1 = new Procedure1<IHiddenRegionFormatter>() {
      public void apply(final IHiddenRegionFormatter it) {
        it.oneSpace();
      }
    };
    document.surround(this.textRegionExtensions.regionFor(temporalImpExpr).keyword(
      this.grammarAccess.getTemporalImpExprAccess().getOperatorImpliesKeyword_1_1_0_1().getValue()), _function_1);
    document.<TemporalExpression>format(temporalImpExpr.getImplication());
  }
  
  protected void _format(final TemporalIffExpr temporalIffExpr, @Extension final IFormattableDocument document) {
    EList<TemporalExpression> _elements = temporalIffExpr.getElements();
    for (final TemporalExpression temporalOrExpr : _elements) {
      document.<TemporalExpression>format(temporalOrExpr);
    }
  }
  
  protected void _format(final TemporalOrExpr temporalOrExpr, @Extension final IFormattableDocument document) {
    EList<String> _operator = temporalOrExpr.getOperator();
    for (final String op : _operator) {
      {
        final Procedure1<IHiddenRegionFormatter> _function = new Procedure1<IHiddenRegionFormatter>() {
          public void apply(final IHiddenRegionFormatter it) {
            it.oneSpace();
          }
        };
        document.surround(this.textRegionExtensions.regionFor(temporalOrExpr).keyword(op), _function);
        final Procedure1<IHiddenRegionFormatter> _function_1 = new Procedure1<IHiddenRegionFormatter>() {
          public void apply(final IHiddenRegionFormatter it) {
            it.setNewLines(0, 0, 1);
          }
        };
        final Procedure1<IHiddenRegionFormatter> _function_2 = new Procedure1<IHiddenRegionFormatter>() {
          public void apply(final IHiddenRegionFormatter it) {
            it.oneSpace();
          }
        };
        document.prepend(document.append(this.textRegionExtensions.regionFor(temporalOrExpr).keyword(
          this.grammarAccess.getTemporalOrExprAccess().getOperatorVerticalLineKeyword_1_1_0_0().getValue()), _function_1), _function_2);
      }
    }
    EList<TemporalExpression> _elements = temporalOrExpr.getElements();
    for (final TemporalExpression temporalAndExpr : _elements) {
      document.<TemporalExpression>format(temporalAndExpr);
    }
  }
  
  protected void _format(final TemporalAndExpr temporalAndExpr, @Extension final IFormattableDocument document) {
    final Procedure1<IHiddenRegionFormatter> _function = new Procedure1<IHiddenRegionFormatter>() {
      public void apply(final IHiddenRegionFormatter it) {
        it.oneSpace();
      }
    };
    document.surround(this.textRegionExtensions.regionFor(temporalAndExpr).keyword(
      this.grammarAccess.getTemporalAndExprAccess().getOperatorAmpersandKeyword_1_1_0_0().getValue()), _function);
    final Procedure1<IHiddenRegionFormatter> _function_1 = new Procedure1<IHiddenRegionFormatter>() {
      public void apply(final IHiddenRegionFormatter it) {
        it.oneSpace();
      }
    };
    document.surround(this.textRegionExtensions.regionFor(temporalAndExpr).keyword(this.grammarAccess.getTemporalAndExprAccess().getOperatorAndKeyword_1_1_0_1().getValue()), _function_1);
    EList<TemporalExpression> _elements = temporalAndExpr.getElements();
    for (final TemporalExpression temporalRelationalExpr : _elements) {
      document.<TemporalExpression>format(temporalRelationalExpr);
    }
  }
  
  protected void _format(final TemporalRelationalExpr temporalRelationalExpr, @Extension final IFormattableDocument document) {
    final Procedure1<IHiddenRegionFormatter> _function = new Procedure1<IHiddenRegionFormatter>() {
      public void apply(final IHiddenRegionFormatter it) {
        it.oneSpace();
      }
    };
    document.surround(this.textRegionExtensions.regionFor(temporalRelationalExpr).keyword(temporalRelationalExpr.getOperator()), _function);
    document.<TemporalExpression>format(temporalRelationalExpr.getLeft());
    document.<TemporalExpression>format(temporalRelationalExpr.getRight());
  }
  
  protected void _format(final TemporalRemainderExpr temporalRemainderExpr, @Extension final IFormattableDocument document) {
    document.<TemporalExpression>format(temporalRemainderExpr.getLeft());
    document.<TemporalExpression>format(temporalRemainderExpr.getRight());
  }
  
  protected void _format(final TemporalAdditiveExpr temporalAdditiveExpr, @Extension final IFormattableDocument document) {
    EList<TemporalExpression> _elements = temporalAdditiveExpr.getElements();
    for (final TemporalExpression temporalMultiplicativeExpr : _elements) {
      document.<TemporalExpression>format(temporalMultiplicativeExpr);
    }
  }
  
  protected void _format(final TemporalMultiplicativeExpr temporalMultiplicativeExpr, @Extension final IFormattableDocument document) {
    EList<TemporalExpression> _elements = temporalMultiplicativeExpr.getElements();
    for (final TemporalExpression temporalBinaryExpr : _elements) {
      document.<TemporalExpression>format(temporalBinaryExpr);
    }
  }
  
  protected void _format(final TemporalBinaryExpr temporalBinaryExpr, @Extension final IFormattableDocument document) {
  }
  
  protected void _format(final TemporalUnaryExpr temporalUnaryExpr, @Extension final IFormattableDocument document) {
    document.<TemporalExpression>format(temporalUnaryExpr.getTue());
  }
  
  protected void _format(final TemporalPrimaryExpr temporalPrimaryExpr, @Extension final IFormattableDocument document) {
    final Procedure1<IHiddenRegionFormatter> _function = new Procedure1<IHiddenRegionFormatter>() {
      public void apply(final IHiddenRegionFormatter it) {
        it.oneSpace();
      }
    };
    final Procedure1<IHiddenRegionFormatter> _function_1 = new Procedure1<IHiddenRegionFormatter>() {
      public void apply(final IHiddenRegionFormatter it) {
        it.noSpace();
      }
    };
    document.append(document.prepend(this.textRegionExtensions.regionFor(temporalPrimaryExpr).keyword(
      this.grammarAccess.getTemporalPrimaryExprAccess().getLeftParenthesisKeyword_2_1_3_1().getValue()), _function), _function_1);
    final Procedure1<IHiddenRegionFormatter> _function_2 = new Procedure1<IHiddenRegionFormatter>() {
      public void apply(final IHiddenRegionFormatter it) {
        it.noSpace();
      }
    };
    final Procedure1<IHiddenRegionFormatter> _function_3 = new Procedure1<IHiddenRegionFormatter>() {
      public void apply(final IHiddenRegionFormatter it) {
        it.oneSpace();
      }
    };
    document.append(document.prepend(this.textRegionExtensions.regionFor(temporalPrimaryExpr).keyword(
      this.grammarAccess.getTemporalPrimaryExprAccess().getRightParenthesisKeyword_2_1_3_3().getValue()), _function_2), _function_3);
    final Procedure1<IHiddenRegionFormatter> _function_4 = new Procedure1<IHiddenRegionFormatter>() {
      public void apply(final IHiddenRegionFormatter it) {
        it.noSpace();
      }
    };
    final Procedure1<IHiddenRegionFormatter> _function_5 = new Procedure1<IHiddenRegionFormatter>() {
      public void apply(final IHiddenRegionFormatter it) {
        it.oneSpace();
      }
    };
    document.append(document.prepend(this.textRegionExtensions.regionFor(temporalPrimaryExpr).keyword(
      this.grammarAccess.getTemporalPrimaryExprAccess().getCommaKeyword_2_1_0_1_0_2_0().getValue()), _function_4), _function_5);
    final ISemanticRegion open = this.textRegionExtensions.regionFor(temporalPrimaryExpr).keyword("{");
    final ISemanticRegion close = this.textRegionExtensions.regionFor(temporalPrimaryExpr).keyword("}");
    final Procedure1<IHiddenRegionFormatter> _function_6 = new Procedure1<IHiddenRegionFormatter>() {
      public void apply(final IHiddenRegionFormatter it) {
        it.indent();
      }
    };
    document.<ISemanticRegion, ISemanticRegion>interior(open, close, _function_6);
    this.format(temporalPrimaryExpr.getTpe(), document);
  }
  
  protected void _format(final Predicate predicate, @Extension final IFormattableDocument document) {
    final Procedure1<IHiddenRegionFormatter> _function = new Procedure1<IHiddenRegionFormatter>() {
      public void apply(final IHiddenRegionFormatter it) {
        it.setNewLines(0, 0, 2);
      }
    };
    final Procedure1<IHiddenRegionFormatter> _function_1 = new Procedure1<IHiddenRegionFormatter>() {
      public void apply(final IHiddenRegionFormatter it) {
        it.oneSpace();
      }
    };
    document.append(document.prepend(this.textRegionExtensions.regionFor(predicate).keyword(this.grammarAccess.getPredicateAccess().getPredicateKeyword_0().getValue()), _function), _function_1);
    document.<TypedParamList>format(predicate.getParams());
    document.<TemporalExpression>format(predicate.getBody());
    final ISemanticRegion open = this.textRegionExtensions.regionFor(predicate).keyword(this.grammarAccess.getPredicateAccess().getLeftCurlyBracketKeyword_3_1_0().getValue());
    final ISemanticRegion close = this.textRegionExtensions.regionFor(predicate).keyword(this.grammarAccess.getPredicateAccess().getRightCurlyBracketKeyword_3_1_2().getValue());
    final Procedure1<IHiddenRegionFormatter> _function_2 = new Procedure1<IHiddenRegionFormatter>() {
      public void apply(final IHiddenRegionFormatter it) {
        it.indent();
      }
    };
    document.<ISemanticRegion, ISemanticRegion>interior(open, close, _function_2);
  }
  
  protected void _format(final TypedParamList typedParamList, @Extension final IFormattableDocument document) {
    EList<TypedParam> _params = typedParamList.getParams();
    for (final TypedParam typeParam : _params) {
      document.<TypedParam>format(typeParam);
    }
  }
  
  protected void _format(final TypedParam typedParam, @Extension final IFormattableDocument document) {
    document.<VarType>format(typedParam.getType());
  }
  
  protected void _format(final Pattern pattern, @Extension final IFormattableDocument document) {
    final Procedure1<IHiddenRegionFormatter> _function = new Procedure1<IHiddenRegionFormatter>() {
      public void apply(final IHiddenRegionFormatter it) {
        it.setNewLines(0, 0, 2);
      }
    };
    final Procedure1<IHiddenRegionFormatter> _function_1 = new Procedure1<IHiddenRegionFormatter>() {
      public void apply(final IHiddenRegionFormatter it) {
        it.oneSpace();
      }
    };
    document.append(document.prepend(this.textRegionExtensions.regionFor(pattern).keyword(this.grammarAccess.getPatternAccess().getPatternKeyword_0().getValue()), _function), _function_1);
    document.<PatternParamList>format(pattern.getParams());
    EList<VarDecl> _varDeclList = pattern.getVarDeclList();
    for (final VarDecl varDecl : _varDeclList) {
      document.<VarDecl>format(varDecl);
    }
    EList<TemporalExpression> _initial = pattern.getInitial();
    for (final TemporalExpression initial : _initial) {
      {
        document.<TemporalExpression>format(initial);
        final Procedure1<IHiddenRegionFormatter> _function_2 = new Procedure1<IHiddenRegionFormatter>() {
          public void apply(final IHiddenRegionFormatter it) {
            it.setNewLines(0, 0, 1);
          }
        };
        final Procedure1<IHiddenRegionFormatter> _function_3 = new Procedure1<IHiddenRegionFormatter>() {
          public void apply(final IHiddenRegionFormatter it) {
            it.oneSpace();
          }
        };
        document.prepend(document.append(this.textRegionExtensions.regionFor(initial).keyword("|"), _function_2), _function_3);
      }
    }
    EList<TemporalExpression> _safety = pattern.getSafety();
    for (final TemporalExpression safety : _safety) {
      {
        document.<TemporalExpression>format(safety);
        final Procedure1<IHiddenRegionFormatter> _function_2 = new Procedure1<IHiddenRegionFormatter>() {
          public void apply(final IHiddenRegionFormatter it) {
            it.setNewLines(0, 0, 1);
          }
        };
        final Procedure1<IHiddenRegionFormatter> _function_3 = new Procedure1<IHiddenRegionFormatter>() {
          public void apply(final IHiddenRegionFormatter it) {
            it.oneSpace();
          }
        };
        document.prepend(document.append(this.textRegionExtensions.regionFor(safety).keyword("|"), _function_2), _function_3);
      }
    }
    EList<TemporalExpression> _justice = pattern.getJustice();
    for (final TemporalExpression justice : _justice) {
      {
        document.<TemporalExpression>format(justice);
        final Procedure1<IHiddenRegionFormatter> _function_2 = new Procedure1<IHiddenRegionFormatter>() {
          public void apply(final IHiddenRegionFormatter it) {
            it.setNewLines(0, 0, 1);
          }
        };
        final Procedure1<IHiddenRegionFormatter> _function_3 = new Procedure1<IHiddenRegionFormatter>() {
          public void apply(final IHiddenRegionFormatter it) {
            it.oneSpace();
          }
        };
        document.prepend(document.append(this.textRegionExtensions.regionFor(justice).keyword("|"), _function_2), _function_3);
      }
    }
    final Procedure1<IHiddenRegionFormatter> _function_2 = new Procedure1<IHiddenRegionFormatter>() {
      public void apply(final IHiddenRegionFormatter it) {
        it.oneSpace();
      }
    };
    final Procedure1<IHiddenRegionFormatter> _function_3 = new Procedure1<IHiddenRegionFormatter>() {
      public void apply(final IHiddenRegionFormatter it) {
        it.setNewLines(1, 1, 1);
      }
    };
    document.prepend(document.append(this.textRegionExtensions.regionFor(pattern).keyword(this.grammarAccess.getPatternAccess().getGKeyword_3_2_0_1_0_0().getValue()), _function_2), _function_3);
    final Procedure1<IHiddenRegionFormatter> _function_4 = new Procedure1<IHiddenRegionFormatter>() {
      public void apply(final IHiddenRegionFormatter it) {
        it.oneSpace();
      }
    };
    final Procedure1<IHiddenRegionFormatter> _function_5 = new Procedure1<IHiddenRegionFormatter>() {
      public void apply(final IHiddenRegionFormatter it) {
        it.setNewLines(1, 1, 1);
      }
    };
    document.prepend(document.append(this.textRegionExtensions.regionFor(pattern).keyword(this.grammarAccess.getPatternAccess().getGFKeyword_3_2_0_3_0_0().getValue()), _function_4), _function_5);
    final ISemanticRegion open = this.textRegionExtensions.regionFor(pattern).keyword(this.grammarAccess.getPatternAccess().getLeftCurlyBracketKeyword_3_0().getValue());
    final ISemanticRegion close = this.textRegionExtensions.regionFor(pattern).keyword(this.grammarAccess.getPatternAccess().getRightCurlyBracketKeyword_3_3().getValue());
    final Procedure1<IHiddenRegionFormatter> _function_6 = new Procedure1<IHiddenRegionFormatter>() {
      public void apply(final IHiddenRegionFormatter it) {
        it.indent();
      }
    };
    document.<ISemanticRegion, ISemanticRegion>interior(open, close, _function_6);
  }
  
  protected void _format(final PatternParamList patternParamList, @Extension final IFormattableDocument document) {
    EList<PatternParam> _params = patternParamList.getParams();
    for (final PatternParam patternParam : _params) {
      {
        document.<PatternParam>format(patternParam);
        final Procedure1<IHiddenRegionFormatter> _function = new Procedure1<IHiddenRegionFormatter>() {
          public void apply(final IHiddenRegionFormatter it) {
            it.oneSpace();
          }
        };
        document.surround(this.textRegionExtensions.regionFor(patternParam).keyword("="), _function);
      }
    }
  }
  
  protected void _format(final PatternParam patternParam, @Extension final IFormattableDocument document) {
  }
  
  protected void _format(final WeightDef weightDef, @Extension final IFormattableDocument document) {
    document.<TemporalExpression>format(weightDef.getDefinition());
  }
  
  protected void _format(final LTLGar ltlGar, @Extension final IFormattableDocument document) {
    String _name = ltlGar.getName();
    boolean _notEquals = (!Objects.equal(_name, null));
    if (_notEquals) {
      final Procedure1<IHiddenRegionFormatter> _function = new Procedure1<IHiddenRegionFormatter>() {
        public void apply(final IHiddenRegionFormatter it) {
          it.setNewLines(0, 0, 2);
        }
      };
      final Procedure1<IHiddenRegionFormatter> _function_1 = new Procedure1<IHiddenRegionFormatter>() {
        public void apply(final IHiddenRegionFormatter it) {
          it.oneSpace();
        }
      };
      document.append(document.prepend(this.textRegionExtensions.regionFor(ltlGar).keyword(this.grammarAccess.getLTLGarAccess().getGuaranteeKeyword_0_0().getValue()), _function), _function_1);
      final Procedure1<IHiddenRegionFormatter> _function_2 = new Procedure1<IHiddenRegionFormatter>() {
        public void apply(final IHiddenRegionFormatter it) {
          it.setNewLines(0, 0, 2);
        }
      };
      final Procedure1<IHiddenRegionFormatter> _function_3 = new Procedure1<IHiddenRegionFormatter>() {
        public void apply(final IHiddenRegionFormatter it) {
          it.oneSpace();
        }
      };
      document.append(document.prepend(this.textRegionExtensions.regionFor(ltlGar).keyword(this.grammarAccess.getLTLGarAccess().getGarKeyword_0_1().getValue()), _function_2), _function_3);
      final Procedure1<IHiddenRegionFormatter> _function_4 = new Procedure1<IHiddenRegionFormatter>() {
        public void apply(final IHiddenRegionFormatter it) {
          it.noSpace();
        }
      };
      final Procedure1<IHiddenRegionFormatter> _function_5 = new Procedure1<IHiddenRegionFormatter>() {
        public void apply(final IHiddenRegionFormatter it) {
          it.setNewLines(1, 1, 1);
        }
      };
      document.append(document.prepend(this.textRegionExtensions.regionFor(ltlGar).keyword(this.grammarAccess.getLTLGarAccess().getColonKeyword_1_2().getValue()), _function_4), _function_5);
    } else {
      final Procedure1<IHiddenRegionFormatter> _function_6 = new Procedure1<IHiddenRegionFormatter>() {
        public void apply(final IHiddenRegionFormatter it) {
          it.setNewLines(0, 0, 2);
        }
      };
      final Procedure1<IHiddenRegionFormatter> _function_7 = new Procedure1<IHiddenRegionFormatter>() {
        public void apply(final IHiddenRegionFormatter it) {
          it.setNewLines(1, 1, 1);
        }
      };
      document.append(document.prepend(this.textRegionExtensions.regionFor(ltlGar).keyword(this.grammarAccess.getLTLGarAccess().getGuaranteeKeyword_0_0().getValue()), _function_6), _function_7);
    }
    final Procedure1<IHiddenRegionFormatter> _function_8 = new Procedure1<IHiddenRegionFormatter>() {
      public void apply(final IHiddenRegionFormatter it) {
        it.oneSpace();
      }
    };
    final Procedure1<IHiddenRegionFormatter> _function_9 = new Procedure1<IHiddenRegionFormatter>() {
      public void apply(final IHiddenRegionFormatter it) {
        it.setSpace("  ");
      }
    };
    document.prepend(document.append(this.textRegionExtensions.regionFor(ltlGar).keyword(this.grammarAccess.getLTLGarAccess().getSafetyGKeyword_2_0_0_2_0_0().getValue()), _function_8), _function_9);
    final Procedure1<IHiddenRegionFormatter> _function_10 = new Procedure1<IHiddenRegionFormatter>() {
      public void apply(final IHiddenRegionFormatter it) {
        it.oneSpace();
      }
    };
    final Procedure1<IHiddenRegionFormatter> _function_11 = new Procedure1<IHiddenRegionFormatter>() {
      public void apply(final IHiddenRegionFormatter it) {
        it.setSpace("  ");
      }
    };
    document.prepend(document.append(this.textRegionExtensions.regionFor(ltlGar).keyword(this.grammarAccess.getLTLGarAccess().getJusticeGFKeyword_2_0_0_4_0_0().getValue()), _function_10), _function_11);
    this.format(ltlGar.getTemporalExpr(), document);
  }
  
  protected void _format(final LTLAsm ltlAsm, @Extension final IFormattableDocument document) {
    String _name = ltlAsm.getName();
    boolean _notEquals = (!Objects.equal(_name, null));
    if (_notEquals) {
      final Procedure1<IHiddenRegionFormatter> _function = new Procedure1<IHiddenRegionFormatter>() {
        public void apply(final IHiddenRegionFormatter it) {
          it.setNewLines(0, 0, 2);
        }
      };
      final Procedure1<IHiddenRegionFormatter> _function_1 = new Procedure1<IHiddenRegionFormatter>() {
        public void apply(final IHiddenRegionFormatter it) {
          it.oneSpace();
        }
      };
      document.append(document.prepend(this.textRegionExtensions.regionFor(ltlAsm).keyword(this.grammarAccess.getLTLAsmAccess().getAssumptionKeyword_0_0().getValue()), _function), _function_1);
      final Procedure1<IHiddenRegionFormatter> _function_2 = new Procedure1<IHiddenRegionFormatter>() {
        public void apply(final IHiddenRegionFormatter it) {
          it.setNewLines(0, 0, 2);
        }
      };
      final Procedure1<IHiddenRegionFormatter> _function_3 = new Procedure1<IHiddenRegionFormatter>() {
        public void apply(final IHiddenRegionFormatter it) {
          it.oneSpace();
        }
      };
      document.append(document.prepend(this.textRegionExtensions.regionFor(ltlAsm).keyword(this.grammarAccess.getLTLAsmAccess().getAsmKeyword_0_1()), _function_2), _function_3);
      final Procedure1<IHiddenRegionFormatter> _function_4 = new Procedure1<IHiddenRegionFormatter>() {
        public void apply(final IHiddenRegionFormatter it) {
          it.noSpace();
        }
      };
      final Procedure1<IHiddenRegionFormatter> _function_5 = new Procedure1<IHiddenRegionFormatter>() {
        public void apply(final IHiddenRegionFormatter it) {
          it.setNewLines(1, 1, 1);
        }
      };
      document.append(document.prepend(this.textRegionExtensions.regionFor(ltlAsm).keyword(this.grammarAccess.getLTLAsmAccess().getColonKeyword_1_2().getValue()), _function_4), _function_5);
    } else {
      final Procedure1<IHiddenRegionFormatter> _function_6 = new Procedure1<IHiddenRegionFormatter>() {
        public void apply(final IHiddenRegionFormatter it) {
          it.setNewLines(0, 0, 2);
        }
      };
      final Procedure1<IHiddenRegionFormatter> _function_7 = new Procedure1<IHiddenRegionFormatter>() {
        public void apply(final IHiddenRegionFormatter it) {
          it.setNewLines(1, 1, 1);
        }
      };
      document.append(document.prepend(this.textRegionExtensions.regionFor(ltlAsm).keyword(this.grammarAccess.getLTLAsmAccess().getAssumptionKeyword_0_0().getValue()), _function_6), _function_7);
    }
    final Procedure1<IHiddenRegionFormatter> _function_8 = new Procedure1<IHiddenRegionFormatter>() {
      public void apply(final IHiddenRegionFormatter it) {
        it.oneSpace();
      }
    };
    document.append(this.textRegionExtensions.regionFor(ltlAsm).keyword(this.grammarAccess.getLTLAsmAccess().getSafetyGKeyword_2_0_0_2_0_0().getValue()), _function_8);
    final Procedure1<IHiddenRegionFormatter> _function_9 = new Procedure1<IHiddenRegionFormatter>() {
      public void apply(final IHiddenRegionFormatter it) {
        it.oneSpace();
      }
    };
    document.append(this.textRegionExtensions.regionFor(ltlAsm).keyword(this.grammarAccess.getLTLAsmAccess().getJusticeGFKeyword_2_0_0_4_0_0().getValue()), _function_9);
    document.<TemporalExpression>format(ltlAsm.getTemporalExpr());
  }
  
  public void format(final Object define, final IFormattableDocument document) {
    if (define instanceof XtextResource) {
      _format((XtextResource)define, document);
      return;
    } else if (define instanceof Define) {
      _format((Define)define, document);
      return;
    } else if (define instanceof DefineDecl) {
      _format((DefineDecl)define, document);
      return;
    } else if (define instanceof LTLAsm) {
      _format((LTLAsm)define, document);
      return;
    } else if (define instanceof LTLGar) {
      _format((LTLGar)define, document);
      return;
    } else if (define instanceof Pattern) {
      _format((Pattern)define, document);
      return;
    } else if (define instanceof PatternParam) {
      _format((PatternParam)define, document);
      return;
    } else if (define instanceof Predicate) {
      _format((Predicate)define, document);
      return;
    } else if (define instanceof TemporalAdditiveExpr) {
      _format((TemporalAdditiveExpr)define, document);
      return;
    } else if (define instanceof TemporalAndExpr) {
      _format((TemporalAndExpr)define, document);
      return;
    } else if (define instanceof TemporalBinaryExpr) {
      _format((TemporalBinaryExpr)define, document);
      return;
    } else if (define instanceof TemporalIffExpr) {
      _format((TemporalIffExpr)define, document);
      return;
    } else if (define instanceof TemporalImpExpr) {
      _format((TemporalImpExpr)define, document);
      return;
    } else if (define instanceof TemporalMultiplicativeExpr) {
      _format((TemporalMultiplicativeExpr)define, document);
      return;
    } else if (define instanceof TemporalOrExpr) {
      _format((TemporalOrExpr)define, document);
      return;
    } else if (define instanceof TemporalPrimaryExpr) {
      _format((TemporalPrimaryExpr)define, document);
      return;
    } else if (define instanceof TemporalRelationalExpr) {
      _format((TemporalRelationalExpr)define, document);
      return;
    } else if (define instanceof TemporalRemainderExpr) {
      _format((TemporalRemainderExpr)define, document);
      return;
    } else if (define instanceof TemporalUnaryExpr) {
      _format((TemporalUnaryExpr)define, document);
      return;
    } else if (define instanceof TypeConstant) {
      _format((TypeConstant)define, document);
      return;
    } else if (define instanceof TypeDef) {
      _format((TypeDef)define, document);
      return;
    } else if (define instanceof TypedParam) {
      _format((TypedParam)define, document);
      return;
    } else if (define instanceof Var) {
      _format((Var)define, document);
      return;
    } else if (define instanceof VarDecl) {
      _format((VarDecl)define, document);
      return;
    } else if (define instanceof WeightDef) {
      _format((WeightDef)define, document);
      return;
    } else if (define instanceof DefineArray) {
      _format((DefineArray)define, document);
      return;
    } else if (define instanceof Import) {
      _format((Import)define, document);
      return;
    } else if (define instanceof Model) {
      _format((Model)define, document);
      return;
    } else if (define instanceof PatternParamList) {
      _format((PatternParamList)define, document);
      return;
    } else if (define instanceof Subrange) {
      _format((Subrange)define, document);
      return;
    } else if (define instanceof TypedParamList) {
      _format((TypedParamList)define, document);
      return;
    } else if (define instanceof VarOwner) {
      _format((VarOwner)define, document);
      return;
    } else if (define instanceof VarType) {
      _format((VarType)define, document);
      return;
    } else if (define instanceof EObject) {
      _format((EObject)define, document);
      return;
    } else if (define == null) {
      _format((Void)null, document);
      return;
    } else if (define != null) {
      _format(define, document);
      return;
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(define, document).toString());
    }
  }
}
