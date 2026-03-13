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

package tau.smlab.syntech.typesystem;

public interface IssueMessages {

  public final String NON_INT_VAR_DIMEMSION = "Dimensions of variable must be positive integers";
  public final String NON_INT_DEFINE_DIMEMSION = "Dimensions of define array must be positive integers";
  public final String NON_INT_VAR_INDEX = "Illegal array index. Index must be a positive integer";
  public final String IMPLIES_EXPR_ARGUMENTS_MUST_BE_BOOLEANS = "Arguments of IMPLIES expression must be booleans";
  public final String IFF_EXPR_ARGUMENTS_MUST_BE_BOOLEANS = "Arguments of IFF expression must be booleans";
  public final String OR_EXPR_ARGUMENTS_MUST_BE_BOOLEANS = "Arguments of OR expression must be booleans";
  public final String AND_EXPR_ARGUMENTS_MUST_BE_BOOLEANS = "Arguments of AND expression must be booleans";
  public final String PASTLTL_EXPR_ARGUMENTS_MUST_BE_BOOLEANS = "Arguments of PastLTL expression must be booleans";
  public final String ILLEGAL_SUBRANGE_FROM = "Illegal 'from' range (must be a legal size decleration)";
  public final String ILLEGAL_SUBRANGE_TO = "Illegal 'to' range (must be a legal size decleration)";
  public final String ILLEGAL_SUBRANGE = "Illegal int range (must be LEFT <= RIGHT)";
  public final String MOD_EXP_ARGUMENTS_MUST_BE_NUMERIC = "Arguments of MOD expression must be numeric";
  public final String ADDITIVE_EXP_ARGUMENTS_MUST_BE_NUMERIC = "Arguments of ADDITIVE expression must be numeric";
  public final String MULTIPLICATIVE_EXP_ARGUMENTS_MUST_BE_NUMERIC = "Arguments of MULTIPLICATIVE expression must be numeric";
  public final String DIMENSIONS_DONT_MATCH = "Number of dimensions doesn't match";
  public final String INDEX_OUT_OF_BOUNDS = "Index out of bounds";
  public final String INVALID_INDEX_EXP = "Invalid index expression";
  public final String NUM_ARGS_DOESNT_MATCH =  "Number of arguments doesn't match signature";
  public final String PATTERN_PARAMS_ARE_BOOLEAN = "Pattern should get boolean parameters";
  public final String PATTERN_MUST_BE_ALONE = "Pattern can only appear as an assumption or as a gurantee by itself";
  public final String WRONG_PARAM_TYPE = "Wrong parameter type";
  public final String NOT_EXPR_ARGUMENT_MUST_BE_BOOLEAN= "Argument of NOT expression must be boolean";
  public final String MINUS_EXPR_ARGUMENT_MUST_BE_NUMERIC = "Argument of MINUS expression must be numeric";
  public final String NOT_AN_ARRAY = "Not an array. Delete these tokens: '[]'.";
  public final String VARS_HAVE_DIFFERENT_DOMAINS = "These variables have different domains";
  public final String CANT_USE_CAPITAL_BOOLEAN_FOR_VARIABLES = "Can't use a capital boolean (TRUE/FALSE) for variables. Change to lower cases (true/false) instead";
  public final String ILLEGAL_COMPARISON_BETWEEN_VAR_AND_CONSTANT = "Illegal comparison between a variable and a constant";
  public final String TYPE_CONSTANTS_COMPARISON_EVALUATES_TO_FALSE = "Comparison always evaluates to false";
  public final String TYPE_CONSTANTS_COMPARISON_EVALUATES_TO_TRUE = "Comparison always evaluates to true";
  public final String VAR_DECL_AND_TYPE_CONSTANT_COME_FROM_DIFFERENT_ENUMS = "Variable doesn't have this TypeConstant in its enum";
  public final String COMPARISON_BETWEEN_TYPECONSTANTS = "Comparison between TypeConstants";
  public final String CANT_COMPARE_TYPE_CONSTANT_AND_CONSTANT = "Can't compare TypeConstant with a constant";
  public final String CANT_CALL_A_PATTERN_FROM_HERE = "Can't call a pattern from here";
  public final String GAR_INI_CANT_HAVE_PRIMED_VARS = "GAR INI can't have primed ('next') variables";
  public final String GAR_JUSTICE_CANT_HAVE_PRIMED_VARS = "GAR JUSTICE can't have primed ('next') variables";
  public final String ASM_SAFETY_CAN_ONLY_HAVE_PRIMED_ENV = "ASM SAFETY can have only primed ('next') ENV variables";
  public final String ASM_JUSTICE_CANT_HAVE_PRIMED_VARS = "ASM JUSTICE can't have primed ('next') variables";
  public final String ASM_INI_CANT_HAVE_PRIMED_VARS = "ASM INI can't have primed ('next') variables";
  public final String ASM_INI_CAN_ONLY_HAVE_ENV_VARS = "ASM INI can have only ENV variables";
  public final String VARDECL_NOT_USED = "Variable not used";
  public final String TYPECONSTANT_NOT_USED = "TypeConstant not used";
  public final String CANT_COMPARE_BOOLEAN_PATT_PARAM_TO_NON_BOOLEAN_EXPR = "Can't compare PatternParam (boolean) to a non-boolean expression";
  public final String DEFINEDECL_NOT_USED = "DefineDecl not used";
  public final String TYPEDEF_CONTAINS_CYCLES = "TypeDef contains a cycle";
  public final String DEFINEDECL_CONTAINS_CYCLES = "DefineDecl contains a cycle";
  public final String DEFINEDECL_NON_CONSTANT_INDEX = "DefineDecl can only have constant indexes in define arrays";
  public final String DEFINEDECL_INDEXES_DONT_MATCH = "DefineDecl dimensions don't match in definition";
  public final String DEFINEDECL_DIFFERENT_TYPES = "DefineDecl cannot mix numeric and boolean values";
  public final String VAR_ISNT_ENUM = "Variable isn't an enum";
  public final String CANT_COMPARE_BETWEEN_BOOLEAN_AND_NON_BOOLEAN = "Can't compare between boolean and non-boolean";
  public final String CANT_COMPARE_BETWEEN_NUMERIC_AND_NON_NUMERIC = "Can't compare between numeric and non-numeric";
  public final String CANT_PRIME_MORE_THAN_ONCE = "Double priming (using nested 'next's) is not allowed";
  public final String ILLEGAL_ACCESS_TO_ARRAY = "Illegal access to an array";
  public final String ILLEGAL_ACCESS_TO_DEFINE_ARRAY = "Illegal access to a define array";
  public final String PREDICATES_CYCLE = "Predicate contains a cycle";
  public final String PREDICATES_MUST_BE_BOOLEAN = "Predicate is non-boolean";
  public final String NOT_A_SPECTRA_FILE = "This is not a .spectra file";
  public final String SPECTRA_FILE_NOT_FOUND = "No such .spectra file";
  public final String DOMAIN_TYPE_IS_NOT_ARRAY = "Domain type must be scalar";
  public final String DOMAIN_TYPE_WRONG_FORM = "Domain type's form can't be - {Const1,Const2,...}";
  public final String PATTERN_CANT_BE_IN_QUANTIFIER_EXPR ="Pattern can't be called in quantifier expression";
  public final String EXISTENTIAL_GAR_MUST_CAN_ONLY_HAVE_BOOLEAN = "Existential guarantees can only have boolean expressions";
  public final String EXISTENTIAL_GAR_CANT_HAVE_PRIMED_VARS = "Existential guarantees can't have primed ('next') variables";
  public final String REGEXP_PREDICATES_MUST_BE_BOOLEAN = "Regular expressions can only have boolean predicate expressions";
  public final String REGEXP_IN_SET_PREDICATES_CANT_HAVE_ARRAYS = "Regular expressions can only have 'in value set' predicates that reference non-array variables";
  public final String IN_LEFT_VAR_DOM = "The value set can only contain values in the domain of the referenced variable";
  public final String IN_LEFT_INVALID_VAL_RANGE = "An empty range of integer values";
  public final String IN_LEFT_VAL_AT_MOST_ONCE = "The same value can appear at most once in the value set";
  public final String IN_LEFT_VAL_SET_EQUALS_DOM = "Too much values in the value set";
  public final String REGEXP_CANT_HAVE_PASTLTL_EXPR = "Regular expressions can't have PastLTL expressions";
  public final String REGEXP_CANT_HAVE_PRIMED_VARS = "Regular expressions can't have primed ('next') variables";
  public final String REGEXP_CANT_HAVE_NESTED_COMP = "Regular expressions can't have nested complementation (negation) operators";
  public final String REGEXP_INVALID_RANGE_QUANTIFIER = "Repetitions in range quantifier with an invalid range";
  public final String REGEXP_INVALID_RANGE_QUANTIFIER_NOT_A_NUMBER = "Repetitions in range quantifier must be a constant number";
  public final String GAR_MUST_BE_BOOLEAN = "GAR must be boolean";
  public final String ASM_MUST_BE_BOOLEAN = "ASM must be boolean";
  public final String FUNCTION_CAN_APPLY_ONlY_ON_BOOLEAN_ARRAY = "This function can only apply on boolean arrays";
  public final String FUNCTION_CANT_APPLY_ON_BOOLEAN_ARRAY = "This function can only apply on numeric arrays";
  public final String FUNCTION_APPLY_ONLY_ON_ARRAY = "This function can apply only on arrays";
}
