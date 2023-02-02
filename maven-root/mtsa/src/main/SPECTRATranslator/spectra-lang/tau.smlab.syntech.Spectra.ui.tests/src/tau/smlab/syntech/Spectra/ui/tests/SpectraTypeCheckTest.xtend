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

package tau.smlab.syntech.Spectra.ui.tests

import com.google.inject.Inject
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.testing.util.ParseHelper
import org.junit.Test
import org.junit.runner.RunWith
import tau.smlab.syntech.ui.tests.SpectraUiInjectorProvider
import org.eclipse.xtext.testing.validation.ValidationTestHelper
import tau.smlab.syntech.spectra.Model
import tau.smlab.syntech.spectra.SpectraPackage
import tau.smlab.syntech.typesystem.IssueMessages

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(SpectraUiInjectorProvider))
class SpectraTypeCheckTest {

	@Inject extension ParseHelper<Model>
	@Inject extension ValidationTestHelper

	@Test
	def void testBooleanVsNumeric() {
		val value = '''
			module Moo
			gar
			5=true;
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.temporalRelationalExpr, null,
			IssueMessages.CANT_COMPARE_BETWEEN_BOOLEAN_AND_NON_BOOLEAN);
	}

	@Test
	def void testNestedPrimes() {
		val value = '''
			module Moo
			type FloorNum = Int(0..4);
			predicate pred(FloorNum counter):
			next(next(counter));
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.temporalPrimaryExpr, null, IssueMessages.CANT_PRIME_MORE_THAN_ONCE);
	}

	@Test
	def void testArithmetics1() {
		val value = '''
			module Moo
			gar 3+true;
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.temporalAdditiveExpr, null,
			IssueMessages.ADDITIVE_EXP_ARGUMENTS_MUST_BE_NUMERIC);
	}

	@Test
	def void testArithmetics2() {
		val value = '''
			module Moo
			gar 3-true;
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.temporalAdditiveExpr, null,
			IssueMessages.ADDITIVE_EXP_ARGUMENTS_MUST_BE_NUMERIC);
	}

	@Test
	def void testArithmetics3() {
		val value = '''
			module Moo
			gar 3*true;
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.temporalMultiplicativeExpr, null,
			IssueMessages.MULTIPLICATIVE_EXP_ARGUMENTS_MUST_BE_NUMERIC);
	}

	@Test
	def void testArithmetics4() {
		val value = '''
			module Moo
			gar 3/true;
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.temporalMultiplicativeExpr, null,
			IssueMessages.MULTIPLICATIVE_EXP_ARGUMENTS_MUST_BE_NUMERIC);
	}

	@Test
	def void testArithmetics5() {
		val value = '''
			module Moo
			gar 3 mod true;
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.temporalRemainderExpr, null,
			IssueMessages.MOD_EXP_ARGUMENTS_MUST_BE_NUMERIC);
	}

	@Test
	def void testImplies1() {
		val value = '''
			module Moo
			gar
			3 implies true;
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.temporalImpExpr, null,
			IssueMessages.IMPLIES_EXPR_ARGUMENTS_MUST_BE_BOOLEANS);
	}

	@Test
	def void testImplies2() {
		val value = '''
			module Moo
			gar
			3 -> true;
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.temporalImpExpr, null,
			IssueMessages.IMPLIES_EXPR_ARGUMENTS_MUST_BE_BOOLEANS);
	}

	@Test
	def void testIff1() {
		val value = '''
			module Moo
			gar
			3 iff true;
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.temporalIffExpr, null,
			IssueMessages.IFF_EXPR_ARGUMENTS_MUST_BE_BOOLEANS);
	}

	@Test
	def void testIff2() {
		val value = '''
			module Moo
			gar
			3 <-> true;
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.temporalIffExpr, null,
			IssueMessages.IFF_EXPR_ARGUMENTS_MUST_BE_BOOLEANS);
	}

	@Test
	def void testOr1() {
		val value = '''
			module Moo
			gar
			3 or true;
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.temporalOrExpr, null,
			IssueMessages.OR_EXPR_ARGUMENTS_MUST_BE_BOOLEANS);
	}

	@Test
	def void testOr2() {
		val value = '''
			module Moo
			gar
			3 xor true;
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.temporalOrExpr, null,
			IssueMessages.OR_EXPR_ARGUMENTS_MUST_BE_BOOLEANS);
	}

	@Test
	def void testOr3() {
		val value = '''
			module Moo
			gar
			3 | true;
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.temporalOrExpr, null,
			IssueMessages.OR_EXPR_ARGUMENTS_MUST_BE_BOOLEANS);
	}

	@Test
	def void testAnd1() {
		val value = '''
			module Moo
			gar
			3 and true;
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.temporalAndExpr, null,
			IssueMessages.AND_EXPR_ARGUMENTS_MUST_BE_BOOLEANS);
	}

	@Test
	def void testAnd2() {
		val value = '''
			module Moo
			gar
			3 & true;
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.temporalAndExpr, null,
			IssueMessages.AND_EXPR_ARGUMENTS_MUST_BE_BOOLEANS);
	}

	@Test
	def void testPatternParam() {
		val value = '''
			module Moo
			pattern patt(a) {
			a=5;  
			}
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.temporalRelationalExpr, null,
			IssueMessages.CANT_COMPARE_BOOLEAN_PATT_PARAM_TO_NON_BOOLEAN_EXPR);
	}

	@Test
	def void testNotOperator() {
		val value = '''
			module Moo
			gar
			!5;
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.temporalPrimaryExpr, null,
			IssueMessages.NOT_EXPR_ARGUMENT_MUST_BE_BOOLEAN);
	}

	@Test
	def void testMinusOperator() {
		val value = '''
			module Moo
			gar
			-false;
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.temporalPrimaryExpr, null,
			IssueMessages.MINUS_EXPR_ARGUMENT_MUST_BE_NUMERIC);
	}

	@Test
	def void testVarDeclVsTypeConstant() {
		val value = '''
			module Moo
			output {UP, DOWN, STOP} motor;
			output {UP2, DOWN2, STOP2} motor2;
			 gar
			  G (motor!=UP2);
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.temporalRelationalExpr, null,
			IssueMessages.VAR_DECL_AND_TYPE_CONSTANT_COME_FROM_DIFFERENT_ENUMS);
	}

	@Test
	def void testUnaryLTL1() {
		val value = '''
			module Moo
			gar
			PREV 5;
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.temporalUnaryExpr, null,
			IssueMessages.PASTLTL_EXPR_ARGUMENTS_MUST_BE_BOOLEANS);
	}

	@Test
	def void testUnaryLTL2() {
		val value = '''
			module Moo
			gar
			H 5;
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.temporalUnaryExpr, null,
			IssueMessages.PASTLTL_EXPR_ARGUMENTS_MUST_BE_BOOLEANS);
	}

	@Test
	def void testUnaryLTL3() {
		val value = '''
			module Moo
			gar
			ONCE 5;
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.temporalUnaryExpr, null,
			IssueMessages.PASTLTL_EXPR_ARGUMENTS_MUST_BE_BOOLEANS);
	}

	@Test
	def void testBinaryTL1() {
		val value = '''
			module Moo
			gar
			5 SINCE 3;
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.temporalBinaryExpr, null,
			IssueMessages.PASTLTL_EXPR_ARGUMENTS_MUST_BE_BOOLEANS);
	}

	@Test
	def void testBinaryTL2() {
		val value = '''
			module Moo
			gar
			false TRIGGERED 3;
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.temporalBinaryExpr, null,
			IssueMessages.PASTLTL_EXPR_ARGUMENTS_MUST_BE_BOOLEANS);
	}

	@Test
	def void testDomains1() {
		val value = '''
			module Moo
			output {UP, DOWN, STOP} motor;
			output {UP2, DOWN2, STOP2} motor2;
			 gar
			  G (motor!=motor2);
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.temporalRelationalExpr, null,
			IssueMessages.VARS_HAVE_DIFFERENT_DOMAINS);
	}

	@Test
	def void testDomains2() {
		val value = '''
			module Moo
			output boolean motor;
			output {UP2, DOWN2, STOP2} motor2;
			 gar
			  G (motor!=motor2);
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.temporalRelationalExpr, null,
			IssueMessages.VARS_HAVE_DIFFERENT_DOMAINS);
	}

	@Test
	def void testDomains3() {
		val value = '''
			module Moo
			output boolean motor;
			output Int(0..4) motor2;
			 gar
			  G (motor!=motor2);
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.temporalRelationalExpr, null,
			IssueMessages.VARS_HAVE_DIFFERENT_DOMAINS);
	}

	@Test
	def void testDomains4() {
		val value = '''
			module Moo
			output Int(6..8) motor;
			output Int(0..4) motor2;
			 gar
			  G (motor!=motor2);    
		'''.parse
		value.assertNoErrors
	}

	@Test
	def void testDomains5() {
		val value = '''
			module Moo
			type myType = Int(0..4);
			output myType motor;
			output boolean motor2;
			 gar
			  G (motor!=motor2); 
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.temporalRelationalExpr, null,
			IssueMessages.VARS_HAVE_DIFFERENT_DOMAINS);
	}

	@Test
	def void testDefineDeclCycles1() {
		val value = '''
			module Moo
			define a := a;
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.defineDecl, null, IssueMessages.DEFINEDECL_CONTAINS_CYCLES);
	}

	@Test
	def void testDefineDeclCycles2() {
		val value = '''
			module Moo
			define a := b;
			define b := a;
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.defineDecl, null, IssueMessages.DEFINEDECL_CONTAINS_CYCLES);
	}

	@Test
	def void testTypeDefCycles1() {
		val value = '''
			module Moo
			type a = a;
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.typeDef, null, IssueMessages.TYPEDEF_CONTAINS_CYCLES);
	}

	@Test
	def void testTypeDefCycles2() {
		val value = '''
			module Moo
			type a = b;
			type b = a;
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.typeDef, null, IssueMessages.TYPEDEF_CONTAINS_CYCLES);
	}

	@Test
	def void testPattParamsBooleans1() {
		val value = '''
			module Moo
			gar
			patt(5);
			
			pattern patt(a) {
			  G(a);
			}
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.temporalPrimaryExpr, null,
			IssueMessages.PATTERN_PARAMS_ARE_BOOLEAN);
	}

	@Test
	def void testPattParamsBooleans2() {
		val value = '''
			module Moo
			gar
			patt(false, 5);
			
			pattern patt(a, b) {
			  G(a);
			}
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.temporalPrimaryExpr, null,
			IssueMessages.PATTERN_PARAMS_ARE_BOOLEAN);
	}

	@Test
	def void testNumArgumentsDontMatch1() {
		val value = '''
			module Moo
			gar
			patt(false);
			
			pattern patt(a, b) {
			  G(a);
			}
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.temporalPrimaryExpr, null, IssueMessages.NUM_ARGS_DOESNT_MATCH);
	}

	@Test
	def void testNumArgumentsDontMatch2() {
		val value = '''
			module Moo
			gar
			patt(false);
			
			predicate patt(boolean a, Int(0..4) b) {
			  G(a);
			}
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.temporalPrimaryExpr, null, IssueMessages.NUM_ARGS_DOESNT_MATCH);
	}

	@Test
	def void testGarININoPrimes1() {
		val value = '''
			module Moo
			sysvar boolean x;
			gar
			next(x);
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.LTLGar, null, IssueMessages.GAR_INI_CANT_HAVE_PRIMED_VARS);
	}

	@Test
	def void testGarININoPrimes2() {
		val value = '''
			module Moo
			envvar boolean x;
			gar
			next(x);
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.LTLGar, null, IssueMessages.GAR_INI_CANT_HAVE_PRIMED_VARS);
	}

	@Test
	def void testGarININoPrimes3() {
		val value = '''
			module Moo
			auxvar boolean x;
			gar
			next(x);
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.LTLGar, null, IssueMessages.GAR_INI_CANT_HAVE_PRIMED_VARS);
	}

	@Test
	def void testGarJusticeNOPrimes1() {
		val value = '''
			module Moo
			sysvar boolean x;
			gar
			GF (next(x));
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.LTLGar, null, IssueMessages.GAR_JUSTICE_CANT_HAVE_PRIMED_VARS);
	}

	@Test
	def void testGarJusticeNOPrimes2() {
		val value = '''
			module Moo
			envvar boolean x;
			gar
			GF (next(x));
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.LTLGar, null, IssueMessages.GAR_JUSTICE_CANT_HAVE_PRIMED_VARS);
	}

	@Test
	def void testGarJusticeNOPrimes3() {
		val value = '''
			module Moo
			auxvar boolean x;
			gar
			GF (next(x));
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.LTLGar, null, IssueMessages.GAR_JUSTICE_CANT_HAVE_PRIMED_VARS);
	}

	@Test
	def void testAsmINIYesEnv() {
		val value = '''
			module Moo
			envvar boolean x;
			asm
			x;
		'''.parse
		value.assertNoErrors
	}

	@Test
	def void testAsmININoSys() {
		val value = '''
			module Moo
			sysvar boolean x;
			asm
			x;
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.LTLAsm, null, IssueMessages.ASM_INI_CAN_ONLY_HAVE_ENV_VARS);
	}

	@Test
	def void testAsmININoAUX() {
		val value = '''
			module Moo
			auxvar boolean x;
			asm
			x;
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.LTLAsm, null, IssueMessages.ASM_INI_CAN_ONLY_HAVE_ENV_VARS);
	}

	@Test
	def void testAsmININoPrimes1() {
		val value = '''
			module Moo
			envvar boolean x;
			asm
			next(x);
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.LTLAsm, null, IssueMessages.ASM_INI_CANT_HAVE_PRIMED_VARS);
	}

	@Test
	def void testAsmININoPrimes2() {
		val value = '''
			module Moo
			sysvar boolean x;
			asm
			next(x);
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.LTLAsm, null, IssueMessages.ASM_INI_CANT_HAVE_PRIMED_VARS);
	}

	@Test
	def void testAsmININoPrimes3() {
		val value = '''
			module Moo
			auxvar boolean x;
			asm
			next(x);
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.LTLAsm, null, IssueMessages.ASM_INI_CANT_HAVE_PRIMED_VARS);
	}

	@Test
	def void testAsmSafetyYesPrimedEnv() {
		val value = '''
			module Moo
			envvar boolean x;
			asm
			G (next(x));
		'''.parse
		value.assertNoErrors
	}

	@Test
	def void testAsmSafetyNoPrimedSys() {
		val value = '''
			module Moo
			sysvar boolean x;
			asm
			G (next(x));
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.LTLAsm, null, IssueMessages.ASM_SAFETY_CAN_ONLY_HAVE_PRIMED_ENV);
	}

	@Test
	def void testAsmSafetyNoPrimedAux() {
		val value = '''
			module Moo
			auxvar boolean x;
			asm
			G (next(x));
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.LTLAsm, null, IssueMessages.ASM_SAFETY_CAN_ONLY_HAVE_PRIMED_ENV);
	}

	@Test
	def void testAsmJusticeNoPrimes1() {
		val value = '''
			module Moo
			envvar boolean x;
			asm
			GF (next(x));
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.LTLAsm, null, IssueMessages.ASM_JUSTICE_CANT_HAVE_PRIMED_VARS);
	}

	@Test
	def void testAsmJusticeNoPrimes2() {
		val value = '''
			module Moo
			sysvar boolean x;
			asm
			GF (next(x));
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.LTLAsm, null, IssueMessages.ASM_JUSTICE_CANT_HAVE_PRIMED_VARS);
	}

	@Test
	def void testAsmJusticeNoPrimes3() {
		val value = '''
			module Moo
			auxvar boolean x;
			asm
			GF (next(x));
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.LTLAsm, null, IssueMessages.ASM_JUSTICE_CANT_HAVE_PRIMED_VARS);
	}

	@Test
	def void testTypeConstantVsNonEnumVarDecl() {
		val value = '''
			module Moo
			input {UP, DOWN, STOP} motor;
			auxvar boolean x;
			gar
			STOP=x;
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.temporalRelationalExpr, null, IssueMessages.VAR_ISNT_ENUM);
	}

	@Test
	def void testVarEnumVsVarEnum1() {
		val value = '''
			module Moo
			input {UP,DOWN} motor1;
			input {UP,DOWN} motor2;
			gar
			motor1=motor2;
		'''.parse
		value.assertNoErrors;
	}

	@Test
	def void testVarEnumVsVarEnum2() {
		val value = '''
			module Moo
			input {UP, DOWN} motor1;
			input {UP} motor2;
			gar
			motor1=motor2;
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.temporalRelationalExpr, null,
			IssueMessages.VARS_HAVE_DIFFERENT_DOMAINS);
	}

	@Test
	def void testVarEnumAsTypeVsVarEnumAsType1() {
		val value = '''
			module Moo
			type Sometype = {UP, DOWN};
			sysvar Sometype motor1;
			sysvar Sometype motor2;
			gar
			motor1=motor2;  	
		'''.parse
		value.assertNoErrors;
	}

	@Test
	def void testVarEnumAsTypeVsVarEnumAsType2() {
		val value = '''
			module Moo
			type Sometype1 = {UP, DOWN};
			type Sometype2 = {UP};
			sysvar Sometype1 motor1;
			sysvar Sometype2 motor2;
			gar
			motor1=motor2;  	
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.temporalRelationalExpr, null,
			IssueMessages.VARS_HAVE_DIFFERENT_DOMAINS);
	}

	@Test
	def void testVarEnumAsTypeVsVarEnum() {
		val value = '''
			module Moo
			type Sometype = {UP};
			sysvar {UP, DOWN} motor1;
			sysvar Sometype motor2;
			gar
			motor1=motor2;  	
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.temporalRelationalExpr, null,
			IssueMessages.VARS_HAVE_DIFFERENT_DOMAINS);
	}

	@Test
	def void testVarEnumAsTypeVsVarBoolean() {
		val value = '''
			module Moo
			type Sometype2 = boolean;
			sysvar {UP, DOWN} motor1;
			sysvar Sometype2 motor2;
			gar
			motor1=motor2;  	
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.temporalRelationalExpr, null,
			IssueMessages.VARS_HAVE_DIFFERENT_DOMAINS);
	}

	@Test
	def void testVarEnumAsTypeVsVarBooleanAsType() {
		val value = '''
			module Moo
			type Sometype1 = {UP, DOWN};
			type Sometype2 = boolean;
			sysvar Sometype1 motor1;
			sysvar Sometype2 motor2;
			gar
			motor1=motor2;  	
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.temporalRelationalExpr, null,
			IssueMessages.VARS_HAVE_DIFFERENT_DOMAINS);
	}

	@Test
	def void testIntVarComparedToIntArrayLocation() {
		val value = '''
			module Moo
			sysvar Int(3..5) x;
			sysvar Int(3..5)[3] arr;
			gar
			  G (arr[1] = x);
		'''.parse
		value.assertNoErrors;
	}

	@Test
	def void testComparisonWithNegativeValue() {
		val value = '''
			module Moo
			gar
			5 = -5;
		'''.parse
		value.assertNoErrors;
	}

	@Test
	def void testPredicateEnumParameterPassed() {
		val value = '''
			module Moo
			type Pos = {Left, Right};
			predicate allItems(Pos p):
			  TRUE;
			asm
			  allItems(Left);
		    '''.parse
		value.assertNoErrors;
	}

	@Test
	def void testDefineWithArithmeticExpression() {
		val value = '''
			module Moo
			define NUMBEROFDISKS := 5;
			define NUMBEROFDISKSMINUSONE := NUMBEROFDISKS - 1;
			sys Int(0..NUMBEROFDISKSMINUSONE) moveDiskNumber;			  
		'''.parse
		value.assertNoErrors;
	}

	@Test
	def void testSubrangeWithArithmeticExpression() {
		val value = '''
			module Moo
			sys Int(0..(3 + 8 * 2)) a;			  
		'''.parse
		value.assertNoErrors;
	}

	@Test
	def void testQuantifierInPredicate() {
		val value = '''
			module Moo
			define sizeOfIntArray := 7;
			
			sys Int(0..3)[sizeOfIntArray] intArray;
			
			predicate allGreaterThan (Int(0..3) number):
				forall i in Int(0..sizeOfIntArray) . intArray[i]>number;
		'''.parse
		value.assertNoErrors;
	}

	@Test
	def void testNestedQuantifierInPredicate() {
		val value = '''
			module Moo
			define sizeOfIntArray := 7;
			
			sys Int(0..3)[sizeOfIntArray] intArray;
			predicate allGreaterThan (Int(0..3) number):
				forall i in Int(0..(sizeOfIntArray-1)) . 
				exists j in Int(0..5) .
				 intArray[i] = j;
		'''.parse
		value.assertNoErrors;
	}
	
	@Test
	def void testQuantifierInDefine(){
		val value = '''
			module Moo
			define sizeOfBoolArray := 4;
			
			sys boolean[sizeOfBoolArray] boolArray;	
			define atLeastOneTrue := exists i in Int(0..(sizeOfBoolArray-1)) . boolArray[i];
		'''.parse
		value.assertNoErrors;
	}
	
	@Test
	def void testNestedQuantifierInDefine(){
		val value = '''
			module Moo
			define sizeOfBoolArray := 4;
			
			sys boolean[sizeOfBoolArray] boolArray;	
			define atLeastTwoTrue := exists i in Int(0..(sizeOfBoolArray-1)) . 
						exists j in Int(0..sizeOfBoolArray) . 
							j < i and boolArray[i] and boolArray[j];
		'''.parse
		value.assertNoErrors;
	}
	
	@Test
	def void testMinFunctionOnIntArray(){
		val value = '''
			module Moo
			sys Int(0..5)[8] arr;
			gar G arr.min < 10;
		'''.parse
		value.assertNoErrors;
	}
	
	@Test
	def void testMaxFunctionOnIntArray(){
		val value = '''
			module Moo
			sys Int(0..5)[8] arr;
			gar G arr.max < 10;
		'''.parse
		value.assertNoErrors;
	}
	
	@Test
	def void testMinFunctionOnBooleanArray(){
		val value = '''
			module Moo
			sys boolean[8] arr;
			gar G arr.min;
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.temporalPrimaryExpr, null,
			IssueMessages.FUNCTION_CANT_APPLY_ON_BOOLEAN_ARRAY);
	}
	
	@Test
	def void testMaxFunctionOnBooleanArray(){
		val value = '''
			module Moo
			sys boolean[8] arr;
			gar G arr.max;
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.temporalPrimaryExpr, null,
			IssueMessages.FUNCTION_CANT_APPLY_ON_BOOLEAN_ARRAY);
	}

	
	@Test
	def void testParameterizedGar(){
		val value = '''
			module Moo
			
			sys boolean a;
			gar b{Int(0..2) i}:
				(i = 0 or i = 1 or i =2) and a; 
		'''.parse
		value.assertNoErrors;
	}
	
		
	@Test
	def void testParameterizedAsm(){
		val value = '''
			module Moo
			
			env boolean a;
			asm b{Int(0..2) i}:
				(i = 0 or i = 1 or i =2) and a; 
		'''.parse
		value.assertNoErrors;
	}
	
	@Test
	def void testTwoParameters(){
		val value = '''
			module Moo
			
			sys boolean a;
			gar b{Int(0..2) i, boolean c}:
				i = 0 or c; 
		'''.parse
		value.assertNoErrors;
	}
	
	@Test
	def void testDefineArrayNonConstantIndex() {
		val value = '''
			module Moo
			define a1[4] := {0, 1, a1[ind], 2};
			sys Int(0..8) ind;
			gar G forall i in Int(0..3) . ind = a1[i];
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.defineDecl, null,
			IssueMessages.DEFINEDECL_NON_CONSTANT_INDEX);
	}
	
	@Test
	def void testDefineArrayBadSize1() {
		val value = '''
			module Moo
			define N := 4;
			define a1[N] := {0, 2};
			sys Int(0..8) ind;
			gar G forall i in Int(0..3) . ind = a1[i];
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.defineDecl, null,
			IssueMessages.DEFINEDECL_INDEXES_DONT_MATCH);
	}
	
	@Test
	def void testDefineArrayBadSize2() {
		val value = '''
			module Moo
			define a1[4][2] := {{5,4}, {0,6,7,8}, {1,8}, {{1,2},{3,4}}};
			sys Int(0..8) ind;
			gar G forall i in Int(0..3) . ind = a1[i][i];
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.defineDecl, null,
			IssueMessages.DEFINEDECL_INDEXES_DONT_MATCH);
	}
	
	@Test
	def void testDefineArrayBadInnerDefine1() {
		val value = '''
			module Moo
			define a1[2] := {0, a1[0][2]};
			sys Int(0..8) ind;
			gar G forall i in Int(0..3) . ind = a1[i];
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.defineDecl, null,
			IssueMessages.DIMENSIONS_DONT_MATCH);
	}
	
	@Test
	def void testDefineArrayBadInnerDefine2() {
		val value = '''
			module Moo
			define a1[2] := {0, a1};
			sys Int(0..8) ind;
			gar G ind = a1[0];
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.temporalPrimaryExpr, null,
			IssueMessages.ILLEGAL_ACCESS_TO_DEFINE_ARRAY);
	}
	
	@Test
	def void testDefineArrayInnerOutOfBounds() {
		val value = '''
			module Moo
			define a1[2] := {0, a1[10]};
			sys Int(0..8) ind;
			gar G forall i in Int(0..3) . ind = a1[i];
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.defineDecl, null,
			IssueMessages.INDEX_OUT_OF_BOUNDS);
	}
	
	@Test
	def void testDefineArrayBadDimensions1() {
		val value = '''
			module Moo
			define a1[2] := {0, 2};
			sys Int(0..8) ind;
			gar G forall i in Int(0..3) . ind = a1[i][0][2];
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.temporalPrimaryExpr, null,
			IssueMessages.DIMENSIONS_DONT_MATCH);
	}
	
	@Test
	def void testDefineArrayBadDimensions2() {
		val value = '''
			module Moo
			define a1[2] := {0, 2};
			sys Int(0..8) ind;
			gar G ind = a1;
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.temporalPrimaryExpr, null,
			IssueMessages.ILLEGAL_ACCESS_TO_DEFINE_ARRAY);
	}
	
	@Test
	def void testDefineArrayOutOfBounds() {
		val value = '''
			module Moo
			define a1[2] := {0, a1[0]};
			sys Int(0..8) ind;
			gar G ind = a1[6];
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.temporalPrimaryExpr, null,
			IssueMessages.INDEX_OUT_OF_BOUNDS);
	}
	
	@Test
	def void testDefineArrayCycle1() {
		val value = '''
			module Moo
			define a1[4] := {0, 1, a1[2], 2};
			sys Int(0..8) ind;
			gar G ind = a1[1];
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.defineDecl, null,
			IssueMessages.DEFINEDECL_CONTAINS_CYCLES);
	}
	
	@Test
	def void testDefineArrayCycle2() {
		val value = '''
			module Moo
			define a0 := a2[0][0];
			define a1[4] := {a0, 1, a1[1], 2};
			define a2[2][2] := {{a1[0],a1[2]}, {2,4}, {ind,ind-2}};
			sys Int(0..8) ind;
			gar G ind = a1[1];
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.defineDecl, null,
			IssueMessages.DEFINEDECL_CONTAINS_CYCLES);
	}
	
	@Test
	def void testDefineArrayMixingTypes1() {
		val value = '''
			module Moo
			define a1[4] := {arr1[2], arr1[2] = 1, 4, false};
			sys Int(0..8) ind;
			gar G ind = a1[1];
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.defineDecl, null,
			IssueMessages.DEFINEDECL_DIFFERENT_TYPES);
	}
	
	@Test
	def void testDefineArrayMixingTypes2() {
		val value = '''
			module Moo
			define a1[2][2] := {{false, arr1[2] = 1}, {4, arr1[2]}};
			sys Int(0..8) ind;
			gar G ind = a1[1][1];
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.defineDecl, null,
			IssueMessages.DEFINEDECL_DIFFERENT_TYPES);
	}
	
	@Test
	def void testDefineInvalidDimensions() {
		val value = '''
			module Moo
			define bool_def := (i = 3);
			define a1[bool_def] := {5,6,7,8};
			sys Int(0..8) i;
			gar G i = a1[1];
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.defineDecl, null,
			IssueMessages.NON_INT_DEFINE_DIMEMSION);
	}
	
	@Test
	def void testDefineArrayIntegers() {
		val value = '''
			module Moo
			define a0[3] := {1,2,3};
			define a1[4][2] := {{5,4}, {0,6}, {1,8}, {8,4}};
			sys Int(0..8)[4] arr1;
			gar G arr1[0] = a0[2];
			gar G arr1[0] = a1[1][0];
		'''.parse
		value.assertNoErrors;
	}
	
	@Test
	def void testDefineArrayBooleans() {
		val value = '''
			module Moo
			define a0[3] := {true,arr1[0]=arr1[1],arr1[1]=4}; 
			sys Int(0..8)[4] arr1;
			gar G forall i in Int(0..2). a0[i];
		'''.parse
		value.assertNoErrors;
	}
	
	@Test
	def void testDefineArrayIndexes1() {
		val value = '''
			module Moo
			define a1[4][2] := {{5,4}, {0,6}, {1,8}, {8,4}};
			sys Int(0..8)[4] arr1;
			gar G forall i in Int(0..3) . arr1[i] = a1[i][0];
		'''.parse
		value.assertNoErrors;
	}
	
	@Test
	def void testDefineArrayIndexes2() {
		val value = '''
			module Moo
			define N := 5;
			define a1[(N-1)] := {5,6,7,8};
			sys Int(5..8) i;
			gar G i = a1[i-5];
		'''.parse
		value.assertNoErrors;
	}
	
	@Test
	def void testDefineArrayDefineDimensions() {
		val value = '''
			module Moo
			define N := 3;
			define a1[N][N] := {{1,2,3},{4,5,6},{7,8,9}};
			sys Int(0..8) ind;
			gar G forall i in Int(0..2) . forall j in Int(0..2) . ind = a1[i][j];
		'''.parse
		value.assertNoErrors;
	}
	
	@Test
	def void testRegexpDefineInsideToRepetitionRange() {
		val value = '''
			module Moo
			sys Int(0..7) x;
			define myInt := 5; 
			regexp myRegexp := [x>3]{0,myInt}; 
		'''.parse
		value.assertNoErrors;
	}
	
	@Test
	def void testRegexpDefineInsideFromRepetitionRange() {
		val value = '''
			module Moo
			sys Int(0..7) x;
			define myInt := 2; 
			regexp myRegexp := [x>3]{myInt, 3}; 
		'''.parse
		value.assertNoErrors;
	}
	
	@Test
	def void testRegexpDefineInsideFromAndToRepetitionRange() {
		val value = '''
			module Moo
			sys Int(0..7) x;
			define fromInt := 2;
			define toInt := 5; 
			regexp myRegexp := [x>3]{fromInt, toInt}; 
		'''.parse
		value.assertNoErrors;
	}
	
	@Test
	def void testRegexpDefineInsideRepetitionRangeBoolean() {
		val value = '''
			module Moo
			sys Int(0..7) x;
			define myInt := true; 
			regexp myRegexp := [x>3]{myInt, 3}; 
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.unaryRegExp, null,
			IssueMessages.REGEXP_INVALID_RANGE_QUANTIFIER_NOT_A_NUMBER);
	}
	
	@Test
	def void testRegexpDefineInsideRepetitionRangeNotAVar() {
		val value = '''
			module Moo
			sys Int(0..7) x;
			define myInt := x + 3; 
			regexp myRegexp := [x>3]{myInt, 3}; 
		'''.parse
		value.assertError(SpectraPackage::eINSTANCE.unaryRegExp, null,
			IssueMessages.REGEXP_INVALID_RANGE_QUANTIFIER_NOT_A_NUMBER);
	}
} 