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
package tau.smlab.syntech.Spectra.ui.tests;

import com.google.inject.Inject;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.XtextRunner;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.eclipse.xtext.testing.validation.ValidationTestHelper;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Extension;
import org.junit.Test;
import org.junit.runner.RunWith;
import tau.smlab.syntech.spectra.Model;
import tau.smlab.syntech.spectra.SpectraPackage;
import tau.smlab.syntech.typesystem.IssueMessages;
import tau.smlab.syntech.ui.tests.SpectraUiInjectorProvider;

@RunWith(XtextRunner.class)
@InjectWith(SpectraUiInjectorProvider.class)
@SuppressWarnings("all")
public class SpectraTypeCheckTest {
  @Inject
  @Extension
  private ParseHelper<Model> _parseHelper;
  
  @Inject
  @Extension
  private ValidationTestHelper _validationTestHelper;
  
  @Test
  public void testBooleanVsNumeric() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("gar");
      _builder.newLine();
      _builder.append("5=true;");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getTemporalRelationalExpr(), null, 
        IssueMessages.CANT_COMPARE_BETWEEN_BOOLEAN_AND_NON_BOOLEAN);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testNestedPrimes() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("type FloorNum = Int(0..4);");
      _builder.newLine();
      _builder.append("predicate pred(FloorNum counter):");
      _builder.newLine();
      _builder.append("next(next(counter));");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getTemporalPrimaryExpr(), null, IssueMessages.CANT_PRIME_MORE_THAN_ONCE);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testArithmetics1() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("gar 3+true;");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getTemporalAdditiveExpr(), null, 
        IssueMessages.ADDITIVE_EXP_ARGUMENTS_MUST_BE_NUMERIC);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testArithmetics2() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("gar 3-true;");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getTemporalAdditiveExpr(), null, 
        IssueMessages.ADDITIVE_EXP_ARGUMENTS_MUST_BE_NUMERIC);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testArithmetics3() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("gar 3*true;");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getTemporalMultiplicativeExpr(), null, 
        IssueMessages.MULTIPLICATIVE_EXP_ARGUMENTS_MUST_BE_NUMERIC);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testArithmetics4() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("gar 3/true;");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getTemporalMultiplicativeExpr(), null, 
        IssueMessages.MULTIPLICATIVE_EXP_ARGUMENTS_MUST_BE_NUMERIC);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testArithmetics5() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("gar 3 mod true;");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getTemporalRemainderExpr(), null, 
        IssueMessages.MOD_EXP_ARGUMENTS_MUST_BE_NUMERIC);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testImplies1() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("gar");
      _builder.newLine();
      _builder.append("3 implies true;");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getTemporalImpExpr(), null, 
        IssueMessages.IMPLIES_EXPR_ARGUMENTS_MUST_BE_BOOLEANS);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testImplies2() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("gar");
      _builder.newLine();
      _builder.append("3 -> true;");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getTemporalImpExpr(), null, 
        IssueMessages.IMPLIES_EXPR_ARGUMENTS_MUST_BE_BOOLEANS);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testIff1() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("gar");
      _builder.newLine();
      _builder.append("3 iff true;");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getTemporalIffExpr(), null, 
        IssueMessages.IFF_EXPR_ARGUMENTS_MUST_BE_BOOLEANS);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testIff2() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("gar");
      _builder.newLine();
      _builder.append("3 <-> true;");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getTemporalIffExpr(), null, 
        IssueMessages.IFF_EXPR_ARGUMENTS_MUST_BE_BOOLEANS);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testOr1() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("gar");
      _builder.newLine();
      _builder.append("3 or true;");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getTemporalOrExpr(), null, 
        IssueMessages.OR_EXPR_ARGUMENTS_MUST_BE_BOOLEANS);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testOr2() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("gar");
      _builder.newLine();
      _builder.append("3 xor true;");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getTemporalOrExpr(), null, 
        IssueMessages.OR_EXPR_ARGUMENTS_MUST_BE_BOOLEANS);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testOr3() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("gar");
      _builder.newLine();
      _builder.append("3 | true;");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getTemporalOrExpr(), null, 
        IssueMessages.OR_EXPR_ARGUMENTS_MUST_BE_BOOLEANS);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testAnd1() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("gar");
      _builder.newLine();
      _builder.append("3 and true;");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getTemporalAndExpr(), null, 
        IssueMessages.AND_EXPR_ARGUMENTS_MUST_BE_BOOLEANS);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testAnd2() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("gar");
      _builder.newLine();
      _builder.append("3 & true;");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getTemporalAndExpr(), null, 
        IssueMessages.AND_EXPR_ARGUMENTS_MUST_BE_BOOLEANS);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testPatternParam() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("pattern patt(a) {");
      _builder.newLine();
      _builder.append("a=5;  ");
      _builder.newLine();
      _builder.append("}");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getTemporalRelationalExpr(), null, 
        IssueMessages.CANT_COMPARE_BOOLEAN_PATT_PARAM_TO_NON_BOOLEAN_EXPR);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testNotOperator() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("gar");
      _builder.newLine();
      _builder.append("!5;");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getTemporalPrimaryExpr(), null, 
        IssueMessages.NOT_EXPR_ARGUMENT_MUST_BE_BOOLEAN);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testMinusOperator() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("gar");
      _builder.newLine();
      _builder.append("-false;");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getTemporalPrimaryExpr(), null, 
        IssueMessages.MINUS_EXPR_ARGUMENT_MUST_BE_NUMERIC);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testVarDeclVsTypeConstant() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("output {UP, DOWN, STOP} motor;");
      _builder.newLine();
      _builder.append("output {UP2, DOWN2, STOP2} motor2;");
      _builder.newLine();
      _builder.append(" ");
      _builder.append("gar");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("G (motor!=UP2);");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getTemporalRelationalExpr(), null, 
        IssueMessages.VAR_DECL_AND_TYPE_CONSTANT_COME_FROM_DIFFERENT_ENUMS);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testUnaryLTL1() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("gar");
      _builder.newLine();
      _builder.append("PREV 5;");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getTemporalUnaryExpr(), null, 
        IssueMessages.PASTLTL_EXPR_ARGUMENTS_MUST_BE_BOOLEANS);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testUnaryLTL2() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("gar");
      _builder.newLine();
      _builder.append("H 5;");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getTemporalUnaryExpr(), null, 
        IssueMessages.PASTLTL_EXPR_ARGUMENTS_MUST_BE_BOOLEANS);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testUnaryLTL3() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("gar");
      _builder.newLine();
      _builder.append("ONCE 5;");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getTemporalUnaryExpr(), null, 
        IssueMessages.PASTLTL_EXPR_ARGUMENTS_MUST_BE_BOOLEANS);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testBinaryTL1() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("gar");
      _builder.newLine();
      _builder.append("5 SINCE 3;");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getTemporalBinaryExpr(), null, 
        IssueMessages.PASTLTL_EXPR_ARGUMENTS_MUST_BE_BOOLEANS);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testBinaryTL2() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("gar");
      _builder.newLine();
      _builder.append("false TRIGGERED 3;");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getTemporalBinaryExpr(), null, 
        IssueMessages.PASTLTL_EXPR_ARGUMENTS_MUST_BE_BOOLEANS);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testDomains1() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("output {UP, DOWN, STOP} motor;");
      _builder.newLine();
      _builder.append("output {UP2, DOWN2, STOP2} motor2;");
      _builder.newLine();
      _builder.append(" ");
      _builder.append("gar");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("G (motor!=motor2);");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getTemporalRelationalExpr(), null, 
        IssueMessages.VARS_HAVE_DIFFERENT_DOMAINS);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testDomains2() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("output boolean motor;");
      _builder.newLine();
      _builder.append("output {UP2, DOWN2, STOP2} motor2;");
      _builder.newLine();
      _builder.append(" ");
      _builder.append("gar");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("G (motor!=motor2);");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getTemporalRelationalExpr(), null, 
        IssueMessages.VARS_HAVE_DIFFERENT_DOMAINS);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testDomains3() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("output boolean motor;");
      _builder.newLine();
      _builder.append("output Int(0..4) motor2;");
      _builder.newLine();
      _builder.append(" ");
      _builder.append("gar");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("G (motor!=motor2);");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getTemporalRelationalExpr(), null, 
        IssueMessages.VARS_HAVE_DIFFERENT_DOMAINS);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testDomains4() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("output Int(6..8) motor;");
      _builder.newLine();
      _builder.append("output Int(0..4) motor2;");
      _builder.newLine();
      _builder.append(" ");
      _builder.append("gar");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("G (motor!=motor2);    ");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertNoErrors(value);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testDomains5() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("type myType = Int(0..4);");
      _builder.newLine();
      _builder.append("output myType motor;");
      _builder.newLine();
      _builder.append("output boolean motor2;");
      _builder.newLine();
      _builder.append(" ");
      _builder.append("gar");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("G (motor!=motor2); ");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getTemporalRelationalExpr(), null, 
        IssueMessages.VARS_HAVE_DIFFERENT_DOMAINS);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testDefineDeclCycles1() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("define a := a;");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getDefineDecl(), null, IssueMessages.DEFINEDECL_CONTAINS_CYCLES);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testDefineDeclCycles2() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("define a := b;");
      _builder.newLine();
      _builder.append("define b := a;");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getDefineDecl(), null, IssueMessages.DEFINEDECL_CONTAINS_CYCLES);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testTypeDefCycles1() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("type a = a;");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getTypeDef(), null, IssueMessages.TYPEDEF_CONTAINS_CYCLES);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testTypeDefCycles2() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("type a = b;");
      _builder.newLine();
      _builder.append("type b = a;");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getTypeDef(), null, IssueMessages.TYPEDEF_CONTAINS_CYCLES);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testPattParamsBooleans1() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("gar");
      _builder.newLine();
      _builder.append("patt(5);");
      _builder.newLine();
      _builder.newLine();
      _builder.append("pattern patt(a) {");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("G(a);");
      _builder.newLine();
      _builder.append("}");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getTemporalPrimaryExpr(), null, 
        IssueMessages.PATTERN_PARAMS_ARE_BOOLEAN);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testPattParamsBooleans2() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("gar");
      _builder.newLine();
      _builder.append("patt(false, 5);");
      _builder.newLine();
      _builder.newLine();
      _builder.append("pattern patt(a, b) {");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("G(a);");
      _builder.newLine();
      _builder.append("}");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getTemporalPrimaryExpr(), null, 
        IssueMessages.PATTERN_PARAMS_ARE_BOOLEAN);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testNumArgumentsDontMatch1() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("gar");
      _builder.newLine();
      _builder.append("patt(false);");
      _builder.newLine();
      _builder.newLine();
      _builder.append("pattern patt(a, b) {");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("G(a);");
      _builder.newLine();
      _builder.append("}");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getTemporalPrimaryExpr(), null, IssueMessages.NUM_ARGS_DOESNT_MATCH);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testNumArgumentsDontMatch2() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("gar");
      _builder.newLine();
      _builder.append("patt(false);");
      _builder.newLine();
      _builder.newLine();
      _builder.append("predicate patt(boolean a, Int(0..4) b) {");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("G(a);");
      _builder.newLine();
      _builder.append("}");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getTemporalPrimaryExpr(), null, IssueMessages.NUM_ARGS_DOESNT_MATCH);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testGarININoPrimes1() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("sysvar boolean x;");
      _builder.newLine();
      _builder.append("gar");
      _builder.newLine();
      _builder.append("next(x);");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getLTLGar(), null, IssueMessages.GAR_INI_CANT_HAVE_PRIMED_VARS);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testGarININoPrimes2() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("envvar boolean x;");
      _builder.newLine();
      _builder.append("gar");
      _builder.newLine();
      _builder.append("next(x);");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getLTLGar(), null, IssueMessages.GAR_INI_CANT_HAVE_PRIMED_VARS);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testGarININoPrimes3() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("auxvar boolean x;");
      _builder.newLine();
      _builder.append("gar");
      _builder.newLine();
      _builder.append("next(x);");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getLTLGar(), null, IssueMessages.GAR_INI_CANT_HAVE_PRIMED_VARS);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testGarJusticeNOPrimes1() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("sysvar boolean x;");
      _builder.newLine();
      _builder.append("gar");
      _builder.newLine();
      _builder.append("GF (next(x));");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getLTLGar(), null, IssueMessages.GAR_JUSTICE_CANT_HAVE_PRIMED_VARS);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testGarJusticeNOPrimes2() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("envvar boolean x;");
      _builder.newLine();
      _builder.append("gar");
      _builder.newLine();
      _builder.append("GF (next(x));");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getLTLGar(), null, IssueMessages.GAR_JUSTICE_CANT_HAVE_PRIMED_VARS);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testGarJusticeNOPrimes3() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("auxvar boolean x;");
      _builder.newLine();
      _builder.append("gar");
      _builder.newLine();
      _builder.append("GF (next(x));");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getLTLGar(), null, IssueMessages.GAR_JUSTICE_CANT_HAVE_PRIMED_VARS);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testAsmINIYesEnv() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("envvar boolean x;");
      _builder.newLine();
      _builder.append("asm");
      _builder.newLine();
      _builder.append("x;");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertNoErrors(value);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testAsmININoSys() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("sysvar boolean x;");
      _builder.newLine();
      _builder.append("asm");
      _builder.newLine();
      _builder.append("x;");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getLTLAsm(), null, IssueMessages.ASM_INI_CAN_ONLY_HAVE_ENV_VARS);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testAsmININoAUX() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("auxvar boolean x;");
      _builder.newLine();
      _builder.append("asm");
      _builder.newLine();
      _builder.append("x;");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getLTLAsm(), null, IssueMessages.ASM_INI_CAN_ONLY_HAVE_ENV_VARS);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testAsmININoPrimes1() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("envvar boolean x;");
      _builder.newLine();
      _builder.append("asm");
      _builder.newLine();
      _builder.append("next(x);");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getLTLAsm(), null, IssueMessages.ASM_INI_CANT_HAVE_PRIMED_VARS);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testAsmININoPrimes2() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("sysvar boolean x;");
      _builder.newLine();
      _builder.append("asm");
      _builder.newLine();
      _builder.append("next(x);");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getLTLAsm(), null, IssueMessages.ASM_INI_CANT_HAVE_PRIMED_VARS);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testAsmININoPrimes3() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("auxvar boolean x;");
      _builder.newLine();
      _builder.append("asm");
      _builder.newLine();
      _builder.append("next(x);");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getLTLAsm(), null, IssueMessages.ASM_INI_CANT_HAVE_PRIMED_VARS);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testAsmSafetyYesPrimedEnv() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("envvar boolean x;");
      _builder.newLine();
      _builder.append("asm");
      _builder.newLine();
      _builder.append("G (next(x));");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertNoErrors(value);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testAsmSafetyNoPrimedSys() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("sysvar boolean x;");
      _builder.newLine();
      _builder.append("asm");
      _builder.newLine();
      _builder.append("G (next(x));");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getLTLAsm(), null, IssueMessages.ASM_SAFETY_CAN_ONLY_HAVE_PRIMED_ENV);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testAsmSafetyNoPrimedAux() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("auxvar boolean x;");
      _builder.newLine();
      _builder.append("asm");
      _builder.newLine();
      _builder.append("G (next(x));");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getLTLAsm(), null, IssueMessages.ASM_SAFETY_CAN_ONLY_HAVE_PRIMED_ENV);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testAsmJusticeNoPrimes1() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("envvar boolean x;");
      _builder.newLine();
      _builder.append("asm");
      _builder.newLine();
      _builder.append("GF (next(x));");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getLTLAsm(), null, IssueMessages.ASM_JUSTICE_CANT_HAVE_PRIMED_VARS);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testAsmJusticeNoPrimes2() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("sysvar boolean x;");
      _builder.newLine();
      _builder.append("asm");
      _builder.newLine();
      _builder.append("GF (next(x));");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getLTLAsm(), null, IssueMessages.ASM_JUSTICE_CANT_HAVE_PRIMED_VARS);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testAsmJusticeNoPrimes3() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("auxvar boolean x;");
      _builder.newLine();
      _builder.append("asm");
      _builder.newLine();
      _builder.append("GF (next(x));");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getLTLAsm(), null, IssueMessages.ASM_JUSTICE_CANT_HAVE_PRIMED_VARS);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testTypeConstantVsNonEnumVarDecl() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("input {UP, DOWN, STOP} motor;");
      _builder.newLine();
      _builder.append("auxvar boolean x;");
      _builder.newLine();
      _builder.append("gar");
      _builder.newLine();
      _builder.append("STOP=x;");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getTemporalRelationalExpr(), null, IssueMessages.VAR_ISNT_ENUM);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testVarEnumVsVarEnum1() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("input {UP,DOWN} motor1;");
      _builder.newLine();
      _builder.append("input {UP,DOWN} motor2;");
      _builder.newLine();
      _builder.append("gar");
      _builder.newLine();
      _builder.append("motor1=motor2;");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertNoErrors(value);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testVarEnumVsVarEnum2() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("input {UP, DOWN} motor1;");
      _builder.newLine();
      _builder.append("input {UP} motor2;");
      _builder.newLine();
      _builder.append("gar");
      _builder.newLine();
      _builder.append("motor1=motor2;");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getTemporalRelationalExpr(), null, 
        IssueMessages.VARS_HAVE_DIFFERENT_DOMAINS);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testVarEnumAsTypeVsVarEnumAsType1() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("type Sometype = {UP, DOWN};");
      _builder.newLine();
      _builder.append("sysvar Sometype motor1;");
      _builder.newLine();
      _builder.append("sysvar Sometype motor2;");
      _builder.newLine();
      _builder.append("gar");
      _builder.newLine();
      _builder.append("motor1=motor2;  \t");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertNoErrors(value);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testVarEnumAsTypeVsVarEnumAsType2() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("type Sometype1 = {UP, DOWN};");
      _builder.newLine();
      _builder.append("type Sometype2 = {UP};");
      _builder.newLine();
      _builder.append("sysvar Sometype1 motor1;");
      _builder.newLine();
      _builder.append("sysvar Sometype2 motor2;");
      _builder.newLine();
      _builder.append("gar");
      _builder.newLine();
      _builder.append("motor1=motor2;  \t");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getTemporalRelationalExpr(), null, 
        IssueMessages.VARS_HAVE_DIFFERENT_DOMAINS);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testVarEnumAsTypeVsVarEnum() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("type Sometype = {UP};");
      _builder.newLine();
      _builder.append("sysvar {UP, DOWN} motor1;");
      _builder.newLine();
      _builder.append("sysvar Sometype motor2;");
      _builder.newLine();
      _builder.append("gar");
      _builder.newLine();
      _builder.append("motor1=motor2;  \t");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getTemporalRelationalExpr(), null, 
        IssueMessages.VARS_HAVE_DIFFERENT_DOMAINS);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testVarEnumAsTypeVsVarBoolean() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("type Sometype2 = boolean;");
      _builder.newLine();
      _builder.append("sysvar {UP, DOWN} motor1;");
      _builder.newLine();
      _builder.append("sysvar Sometype2 motor2;");
      _builder.newLine();
      _builder.append("gar");
      _builder.newLine();
      _builder.append("motor1=motor2;  \t");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getTemporalRelationalExpr(), null, 
        IssueMessages.VARS_HAVE_DIFFERENT_DOMAINS);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testVarEnumAsTypeVsVarBooleanAsType() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("type Sometype1 = {UP, DOWN};");
      _builder.newLine();
      _builder.append("type Sometype2 = boolean;");
      _builder.newLine();
      _builder.append("sysvar Sometype1 motor1;");
      _builder.newLine();
      _builder.append("sysvar Sometype2 motor2;");
      _builder.newLine();
      _builder.append("gar");
      _builder.newLine();
      _builder.append("motor1=motor2;  \t");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getTemporalRelationalExpr(), null, 
        IssueMessages.VARS_HAVE_DIFFERENT_DOMAINS);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testIntVarComparedToIntArrayLocation() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("sysvar Int(3..5) x;");
      _builder.newLine();
      _builder.append("sysvar Int(3..5)[3] arr;");
      _builder.newLine();
      _builder.append("gar");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("G (arr[1] = x);");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertNoErrors(value);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testComparisonWithNegativeValue() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("gar");
      _builder.newLine();
      _builder.append("5 = -5;");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertNoErrors(value);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testPredicateEnumParameterPassed() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("type Pos = {Left, Right};");
      _builder.newLine();
      _builder.append("predicate allItems(Pos p):");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("TRUE;");
      _builder.newLine();
      _builder.append("asm");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("allItems(Left);");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertNoErrors(value);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testDefineWithArithmeticExpression() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("define NUMBEROFDISKS := 5;");
      _builder.newLine();
      _builder.append("define NUMBEROFDISKSMINUSONE := NUMBEROFDISKS - 1;");
      _builder.newLine();
      _builder.append("sys Int(0..NUMBEROFDISKSMINUSONE) moveDiskNumber;\t\t\t  ");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertNoErrors(value);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testSubrangeWithArithmeticExpression() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("sys Int(0..(3 + 8 * 2)) a;\t\t\t  ");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertNoErrors(value);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testQuantifierInPredicate() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("define sizeOfIntArray := 7;");
      _builder.newLine();
      _builder.newLine();
      _builder.append("sys Int(0..3)[sizeOfIntArray] intArray;");
      _builder.newLine();
      _builder.newLine();
      _builder.append("predicate allGreaterThan (Int(0..3) number):");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("forall i in Int(0..sizeOfIntArray) . intArray[i]>number;");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertNoErrors(value);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testNestedQuantifierInPredicate() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("define sizeOfIntArray := 7;");
      _builder.newLine();
      _builder.newLine();
      _builder.append("sys Int(0..3)[sizeOfIntArray] intArray;");
      _builder.newLine();
      _builder.append("predicate allGreaterThan (Int(0..3) number):");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("forall i in Int(0..(sizeOfIntArray-1)) . ");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("exists j in Int(0..5) .");
      _builder.newLine();
      _builder.append("\t ");
      _builder.append("intArray[i] = j;");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertNoErrors(value);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testQuantifierInDefine() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("define sizeOfBoolArray := 4;");
      _builder.newLine();
      _builder.newLine();
      _builder.append("sys boolean[sizeOfBoolArray] boolArray;\t");
      _builder.newLine();
      _builder.append("define atLeastOneTrue := exists i in Int(0..(sizeOfBoolArray-1)) . boolArray[i];");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertNoErrors(value);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testNestedQuantifierInDefine() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("define sizeOfBoolArray := 4;");
      _builder.newLine();
      _builder.newLine();
      _builder.append("sys boolean[sizeOfBoolArray] boolArray;\t");
      _builder.newLine();
      _builder.append("define atLeastTwoTrue := exists i in Int(0..(sizeOfBoolArray-1)) . ");
      _builder.newLine();
      _builder.append("\t\t\t");
      _builder.append("exists j in Int(0..sizeOfBoolArray) . ");
      _builder.newLine();
      _builder.append("\t\t\t\t");
      _builder.append("j < i and boolArray[i] and boolArray[j];");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertNoErrors(value);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testMinFunctionOnIntArray() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("sys Int(0..5)[8] arr;");
      _builder.newLine();
      _builder.append("gar G arr.min < 10;");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertNoErrors(value);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testMaxFunctionOnIntArray() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("sys Int(0..5)[8] arr;");
      _builder.newLine();
      _builder.append("gar G arr.max < 10;");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertNoErrors(value);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testMinFunctionOnBooleanArray() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("sys boolean[8] arr;");
      _builder.newLine();
      _builder.append("gar G arr.min;");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getTemporalPrimaryExpr(), null, 
        IssueMessages.FUNCTION_CANT_APPLY_ON_BOOLEAN_ARRAY);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testMaxFunctionOnBooleanArray() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("sys boolean[8] arr;");
      _builder.newLine();
      _builder.append("gar G arr.max;");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getTemporalPrimaryExpr(), null, 
        IssueMessages.FUNCTION_CANT_APPLY_ON_BOOLEAN_ARRAY);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testParameterizedGar() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.newLine();
      _builder.append("sys boolean a;");
      _builder.newLine();
      _builder.append("gar b{Int(0..2) i}:");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("(i = 0 or i = 1 or i =2) and a; ");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertNoErrors(value);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testParameterizedAsm() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.newLine();
      _builder.append("env boolean a;");
      _builder.newLine();
      _builder.append("asm b{Int(0..2) i}:");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("(i = 0 or i = 1 or i =2) and a; ");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertNoErrors(value);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testTwoParameters() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.newLine();
      _builder.append("sys boolean a;");
      _builder.newLine();
      _builder.append("gar b{Int(0..2) i, boolean c}:");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("i = 0 or c; ");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertNoErrors(value);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testDefineArrayNonConstantIndex() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("define a1[4] := {0, 1, a1[ind], 2};");
      _builder.newLine();
      _builder.append("sys Int(0..8) ind;");
      _builder.newLine();
      _builder.append("gar G forall i in Int(0..3) . ind = a1[i];");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getDefineDecl(), null, 
        IssueMessages.DEFINEDECL_NON_CONSTANT_INDEX);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testDefineArrayBadSize1() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("define N := 4;");
      _builder.newLine();
      _builder.append("define a1[N] := {0, 2};");
      _builder.newLine();
      _builder.append("sys Int(0..8) ind;");
      _builder.newLine();
      _builder.append("gar G forall i in Int(0..3) . ind = a1[i];");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getDefineDecl(), null, 
        IssueMessages.DEFINEDECL_INDEXES_DONT_MATCH);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testDefineArrayBadSize2() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("define a1[4][2] := {{5,4}, {0,6,7,8}, {1,8}, {{1,2},{3,4}}};");
      _builder.newLine();
      _builder.append("sys Int(0..8) ind;");
      _builder.newLine();
      _builder.append("gar G forall i in Int(0..3) . ind = a1[i][i];");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getDefineDecl(), null, 
        IssueMessages.DEFINEDECL_INDEXES_DONT_MATCH);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testDefineArrayBadInnerDefine1() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("define a1[2] := {0, a1[0][2]};");
      _builder.newLine();
      _builder.append("sys Int(0..8) ind;");
      _builder.newLine();
      _builder.append("gar G forall i in Int(0..3) . ind = a1[i];");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getDefineDecl(), null, 
        IssueMessages.DIMENSIONS_DONT_MATCH);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testDefineArrayBadInnerDefine2() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("define a1[2] := {0, a1};");
      _builder.newLine();
      _builder.append("sys Int(0..8) ind;");
      _builder.newLine();
      _builder.append("gar G ind = a1[0];");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getTemporalPrimaryExpr(), null, 
        IssueMessages.ILLEGAL_ACCESS_TO_DEFINE_ARRAY);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testDefineArrayInnerOutOfBounds() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("define a1[2] := {0, a1[10]};");
      _builder.newLine();
      _builder.append("sys Int(0..8) ind;");
      _builder.newLine();
      _builder.append("gar G forall i in Int(0..3) . ind = a1[i];");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getDefineDecl(), null, 
        IssueMessages.INDEX_OUT_OF_BOUNDS);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testDefineArrayBadDimensions1() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("define a1[2] := {0, 2};");
      _builder.newLine();
      _builder.append("sys Int(0..8) ind;");
      _builder.newLine();
      _builder.append("gar G forall i in Int(0..3) . ind = a1[i][0][2];");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getTemporalPrimaryExpr(), null, 
        IssueMessages.DIMENSIONS_DONT_MATCH);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testDefineArrayBadDimensions2() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("define a1[2] := {0, 2};");
      _builder.newLine();
      _builder.append("sys Int(0..8) ind;");
      _builder.newLine();
      _builder.append("gar G ind = a1;");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getTemporalPrimaryExpr(), null, 
        IssueMessages.ILLEGAL_ACCESS_TO_DEFINE_ARRAY);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testDefineArrayOutOfBounds() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("define a1[2] := {0, a1[0]};");
      _builder.newLine();
      _builder.append("sys Int(0..8) ind;");
      _builder.newLine();
      _builder.append("gar G ind = a1[6];");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getTemporalPrimaryExpr(), null, 
        IssueMessages.INDEX_OUT_OF_BOUNDS);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testDefineArrayCycle1() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("define a1[4] := {0, 1, a1[2], 2};");
      _builder.newLine();
      _builder.append("sys Int(0..8) ind;");
      _builder.newLine();
      _builder.append("gar G ind = a1[1];");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getDefineDecl(), null, 
        IssueMessages.DEFINEDECL_CONTAINS_CYCLES);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testDefineArrayCycle2() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("define a0 := a2[0][0];");
      _builder.newLine();
      _builder.append("define a1[4] := {a0, 1, a1[1], 2};");
      _builder.newLine();
      _builder.append("define a2[2][2] := {{a1[0],a1[2]}, {2,4}, {ind,ind-2}};");
      _builder.newLine();
      _builder.append("sys Int(0..8) ind;");
      _builder.newLine();
      _builder.append("gar G ind = a1[1];");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getDefineDecl(), null, 
        IssueMessages.DEFINEDECL_CONTAINS_CYCLES);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testDefineArrayMixingTypes1() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("define a1[4] := {arr1[2], arr1[2] = 1, 4, false};");
      _builder.newLine();
      _builder.append("sys Int(0..8) ind;");
      _builder.newLine();
      _builder.append("gar G ind = a1[1];");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getDefineDecl(), null, 
        IssueMessages.DEFINEDECL_DIFFERENT_TYPES);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testDefineArrayMixingTypes2() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("define a1[2][2] := {{false, arr1[2] = 1}, {4, arr1[2]}};");
      _builder.newLine();
      _builder.append("sys Int(0..8) ind;");
      _builder.newLine();
      _builder.append("gar G ind = a1[1][1];");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getDefineDecl(), null, 
        IssueMessages.DEFINEDECL_DIFFERENT_TYPES);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testDefineInvalidDimensions() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("define bool_def := (i = 3);");
      _builder.newLine();
      _builder.append("define a1[bool_def] := {5,6,7,8};");
      _builder.newLine();
      _builder.append("sys Int(0..8) i;");
      _builder.newLine();
      _builder.append("gar G i = a1[1];");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getDefineDecl(), null, 
        IssueMessages.NON_INT_DEFINE_DIMEMSION);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testDefineArrayIntegers() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("define a0[3] := {1,2,3};");
      _builder.newLine();
      _builder.append("define a1[4][2] := {{5,4}, {0,6}, {1,8}, {8,4}};");
      _builder.newLine();
      _builder.append("sys Int(0..8)[4] arr1;");
      _builder.newLine();
      _builder.append("gar G arr1[0] = a0[2];");
      _builder.newLine();
      _builder.append("gar G arr1[0] = a1[1][0];");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertNoErrors(value);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testDefineArrayBooleans() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("define a0[3] := {true,arr1[0]=arr1[1],arr1[1]=4}; ");
      _builder.newLine();
      _builder.append("sys Int(0..8)[4] arr1;");
      _builder.newLine();
      _builder.append("gar G forall i in Int(0..2). a0[i];");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertNoErrors(value);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testDefineArrayIndexes1() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("define a1[4][2] := {{5,4}, {0,6}, {1,8}, {8,4}};");
      _builder.newLine();
      _builder.append("sys Int(0..8)[4] arr1;");
      _builder.newLine();
      _builder.append("gar G forall i in Int(0..3) . arr1[i] = a1[i][0];");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertNoErrors(value);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testDefineArrayIndexes2() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("define N := 5;");
      _builder.newLine();
      _builder.append("define a1[(N-1)] := {5,6,7,8};");
      _builder.newLine();
      _builder.append("sys Int(5..8) i;");
      _builder.newLine();
      _builder.append("gar G i = a1[i-5];");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertNoErrors(value);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testDefineArrayDefineDimensions() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("define N := 3;");
      _builder.newLine();
      _builder.append("define a1[N][N] := {{1,2,3},{4,5,6},{7,8,9}};");
      _builder.newLine();
      _builder.append("sys Int(0..8) ind;");
      _builder.newLine();
      _builder.append("gar G forall i in Int(0..2) . forall j in Int(0..2) . ind = a1[i][j];");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertNoErrors(value);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testRegexpDefineInsideToRepetitionRange() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("sys Int(0..7) x;");
      _builder.newLine();
      _builder.append("define myInt := 5; ");
      _builder.newLine();
      _builder.append("regexp myRegexp := [x>3]{0,myInt}; ");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertNoErrors(value);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testRegexpDefineInsideFromRepetitionRange() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("sys Int(0..7) x;");
      _builder.newLine();
      _builder.append("define myInt := 2; ");
      _builder.newLine();
      _builder.append("regexp myRegexp := [x>3]{myInt, 3}; ");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertNoErrors(value);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testRegexpDefineInsideFromAndToRepetitionRange() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("sys Int(0..7) x;");
      _builder.newLine();
      _builder.append("define fromInt := 2;");
      _builder.newLine();
      _builder.append("define toInt := 5; ");
      _builder.newLine();
      _builder.append("regexp myRegexp := [x>3]{fromInt, toInt}; ");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertNoErrors(value);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testRegexpDefineInsideRepetitionRangeBoolean() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("sys Int(0..7) x;");
      _builder.newLine();
      _builder.append("define myInt := true; ");
      _builder.newLine();
      _builder.append("regexp myRegexp := [x>3]{myInt, 3}; ");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getUnaryRegExp(), null, 
        IssueMessages.REGEXP_INVALID_RANGE_QUANTIFIER_NOT_A_NUMBER);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testRegexpDefineInsideRepetitionRangeNotAVar() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("module Moo");
      _builder.newLine();
      _builder.append("sys Int(0..7) x;");
      _builder.newLine();
      _builder.append("define myInt := x + 3; ");
      _builder.newLine();
      _builder.append("regexp myRegexp := [x>3]{myInt, 3}; ");
      _builder.newLine();
      final Model value = this._parseHelper.parse(_builder);
      this._validationTestHelper.assertError(value, SpectraPackage.eINSTANCE.getUnaryRegExp(), null, 
        IssueMessages.REGEXP_INVALID_RANGE_QUANTIFIER_NOT_A_NUMBER);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
}
