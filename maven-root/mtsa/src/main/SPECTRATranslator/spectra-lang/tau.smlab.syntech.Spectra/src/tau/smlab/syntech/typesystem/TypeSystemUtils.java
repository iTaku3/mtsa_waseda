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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.EcoreUtil2;

import tau.smlab.syntech.spectra.Constant;
import tau.smlab.syntech.spectra.Counter;
import tau.smlab.syntech.spectra.DefineArray;
import tau.smlab.syntech.spectra.DefineDecl;
import tau.smlab.syntech.spectra.DefineRegExpDecl;
import tau.smlab.syntech.spectra.DomainVarDecl;
import tau.smlab.syntech.spectra.Monitor;
import tau.smlab.syntech.spectra.PatternParam;
import tau.smlab.syntech.spectra.SizeDefineDecl;
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
import tau.smlab.syntech.spectra.Var;
import tau.smlab.syntech.spectra.VarDecl;
import tau.smlab.syntech.spectra.VarType;

public class TypeSystemUtils {

	// TODO replace with grammarAccess (currently grammarAccess is null for some
	// reason)
	private final static String NEXT = "next";
	private final static String BOOLEAN = "boolean";
	private final static String MINUS = "-";
	private final static String MAX = "max";
	private final static String MIN = "min";
	private static final String NOT = "!";
	private static final String SYS = "SYS";
	private static final String AUX = "AUX";

	public static final String NUMERIC_ARRAY_MIN_OF = ".min";
	public static final String NUMERIC_ARRAY_MAX_OF = ".max";
	public static final String NUMERIC_ARRAY_SUM_OF = ".sum";
	public static final String NUMERIC_ARRAY_PROD_OF = ".prod";
	public static final String BOOL_ARRAY_AND_OF = ".all";
	public static final String BOOL_ARRAY_OR_OF = ".any";
	
	public static final List<String> NUMERIC_ARRAY_FUNCTIONS = Stream.of(NUMERIC_ARRAY_SUM_OF, NUMERIC_ARRAY_PROD_OF, NUMERIC_ARRAY_MIN_OF, NUMERIC_ARRAY_MAX_OF)
			.collect(Collectors.toList());
	public static final List<String> BOOLEAN_ARRAY_FUNCTIONS = Stream.of(BOOL_ARRAY_AND_OF, BOOL_ARRAY_OR_OF)
			.collect(Collectors.toList());
	public static final List<String> ARRAY_FUNCTIONS = Stream.of(NUMERIC_ARRAY_FUNCTIONS, BOOLEAN_ARRAY_FUNCTIONS)
			.flatMap(x -> x.stream()).collect(Collectors.toList());

	// @Inject static SpectraGrammarAccess grammarAccess;

	public static BooleanAndString isBooleanExpression(TemporalExpression temporalExpression,
			List<String> alreadySeenDefineDecls) {
		if (temporalExpression instanceof TemporalRemainderExpr) {
			return new BooleanAndString(false, "Modulo expression found");
		} else if (temporalExpression instanceof TemporalAdditiveExpr) {
			return new BooleanAndString(false, "Arithmetic expression found");
		} else if (temporalExpression instanceof TemporalMultiplicativeExpr) {
			return new BooleanAndString(false, "Arithmetic expression found");
		} else if (temporalExpression instanceof Constant) {
			Constant constant = (Constant) temporalExpression;
			if (constant.getBooleanValue() == null) {
				// it's an int
				return new BooleanAndString(false, "Integer found");
			}
		} else if (temporalExpression instanceof TemporalPrimaryExpr) {
			TemporalPrimaryExpr temporalPrimaryExp = (TemporalPrimaryExpr) temporalExpression;
			if (temporalPrimaryExp.getPointer() != null) {
				if (temporalPrimaryExp.getPointer() instanceof VarDecl) {
					VarDecl varDecl = (VarDecl) temporalPrimaryExp.getPointer();
					VarType varType = varDecl.getType();
					boolean isVarTypeBoolean = isVarTypeBoolean(varType);
					if (!isVarTypeBoolean) {
						return new BooleanAndString(false, "Non-boolean variable found");
					}
				} else if (temporalPrimaryExp.getPointer() instanceof Monitor) {
					Monitor mon = (Monitor) temporalPrimaryExp.getPointer();
					VarType varType = mon.getType();
					boolean isVarTypeBoolean = isVarTypeBoolean(varType);
					if (!isVarTypeBoolean) {
						return new BooleanAndString(false, "Non-numeric monitor found");
					}
				} else if (temporalPrimaryExp.getPointer() instanceof Counter) {
					return new BooleanAndString(false, "Non-boolean counter found");
				} else if (temporalPrimaryExp.getPointer() instanceof TypedParam) {
					TypedParam typedParam = (TypedParam) temporalPrimaryExp.getPointer();
					VarType varType = typedParam.getType();
					boolean isVarTypeBoolean = isVarTypeBoolean(varType);
					if (!isVarTypeBoolean) {
						return new BooleanAndString(false, "Non-boolean variable found");
					}
				} else if (temporalPrimaryExp.getPointer() instanceof TypeConstant) {
					return new BooleanAndString(false, "TypeConstant found");
				} else if (temporalPrimaryExp.getPointer() instanceof DefineDecl) {
					DefineDecl defineDecl = (DefineDecl) temporalPrimaryExp.getPointer();
					// cycles danger
					if (alreadySeenDefineDecls == null) {
						alreadySeenDefineDecls = new ArrayList<>();
					} else if (alreadySeenDefineDecls.contains(defineDecl.getName())) {
						return null; // to prevent stack overflow. we handle this error somewhere else so just return
						// "no problem" here.
					}
					alreadySeenDefineDecls.add(defineDecl.getName());
					TemporalExpression defineExpression;
					if (defineDecl.getSimpleExpr() != null) {
						defineExpression = defineDecl.getSimpleExpr();
					} else {
						// Check the first because mixing boolean and numeric in defines is already checked before
						DefineArray defArray = defineDecl.getInnerArray();
						while (defArray.getSimpleExprs() == null || defArray.getSimpleExprs().size() == 0) {
							defArray = defArray.getInnerArrays().get(0);
						}
						defineExpression = defArray.getSimpleExprs().get(0);
					}

					BooleanAndString isDefineExpressionBoolean = isBooleanExpression(defineExpression,
							alreadySeenDefineDecls); // go inside
					if (!isDefineExpressionBoolean.getBoolean()) {
						return new BooleanAndString(false, "Non-boolean define expression found");
					}
				} else if (temporalPrimaryExp.getPointer() instanceof DomainVarDecl) {
					DomainVarDecl domainVarDecl = (DomainVarDecl) temporalPrimaryExp.getPointer();
					VarType varType = domainVarDecl.getDomainType();
					boolean isVarTypeBoolean = isVarTypeBoolean(varType);
					if (!isVarTypeBoolean) {
						return new BooleanAndString(false, "Non-boolean variable found");
					}
				} else if (temporalPrimaryExp.getPointer() instanceof DefineRegExpDecl) {
					return new BooleanAndString(false, "Reference to a regular expression found");
				}
			}
			if (temporalPrimaryExp.getOperator() != null) {
				if (temporalPrimaryExp.getOperator().equals(MINUS)) {
					return new BooleanAndString(false, "An expression that starts with '-' found");
				}
				if (temporalPrimaryExp.getOperator().equals(MIN) || temporalPrimaryExp.getOperator().equals(MAX)) {
					return new BooleanAndString(false);
				}
				if (temporalPrimaryExp.getOperator().equals(NEXT)) {
					TemporalExpression temporalExpressionWithinNext = temporalPrimaryExp.getTemporalExpression();
					BooleanAndString isTemporalExpressionWithinNextBoolean = isBooleanExpression(
							temporalExpressionWithinNext, alreadySeenDefineDecls); // go inside
					if (!isTemporalExpressionWithinNextBoolean.getBoolean()) {
						return new BooleanAndString(false, "Non-boolean expression within 'next' found");
					}
				}
			}
		}

		return new BooleanAndString(true);
	}

	static boolean isVarTypeBoolean(VarType varType) {
		return isVarTypeBoolean(varType, null);
	}

	static boolean isVarTypeBoolean(VarType varType, List<String> alreadySeenTypeDefs) {
		if (varType.getName() != null && varType.getName().equals(BOOLEAN)) {
			return true;
		}
		if (varType.getType() != null) {
			if (alreadySeenTypeDefs == null) {
				alreadySeenTypeDefs = new ArrayList<>();
			} else if (alreadySeenTypeDefs.contains(varType.getType().getName())) {
				return false; // meaningless, just to prevent stack overflowing
			}
			alreadySeenTypeDefs.add(varType.getType().getName());
			VarType typeOfTypeDef = varType.getType().getType();
			return isVarTypeBoolean(typeOfTypeDef, alreadySeenTypeDefs);
		}
		return false;
	}

	private static boolean isSameDimensions(EList<SizeDefineDecl> dimensions1, EList<SizeDefineDecl> dimensions2) {
		if ((dimensions1 != null && dimensions2 == null) || (dimensions1 == null && dimensions2 != null)) {
			return false;
		}
		if (dimensions1.size() != dimensions2.size()) {
			return false;
		}
		for (int i = 0; i < dimensions1.size(); i++) {
			int dim1 = sizeDefineToInt(dimensions1.get(i));
			int dim2 = sizeDefineToInt(dimensions2.get(i));
			if (dim1 != dim2) {
				return false;
			}
		}

		return true;
	}

	private static boolean isSameConsts(EList<TypeConstant> const1, EList<TypeConstant> const2) {
		if ((const1 != null && const2 == null) || (const1 == null && const2 != null)) {
			return false;
		}
		if (const1.size() != const2.size()) {
			return false;
		}

		for (int i = 0; i < const1.size(); i++) {
			if (!(const1.get(i).getName()).equals(const2.get(i).getName())) {
				return false;
			}
		}

		return true;
	}

	private static boolean isSameSubrange(Subrange subr1, Subrange subr2) {

		if ((subr1 != null && subr2 == null) || (subr1 == null && subr2 != null)) {
			return false;
		}

		int from1 = sizeDefineToInt(subr1.getFrom());
		int to1 = sizeDefineToInt(subr1.getTo());
		int from2 = sizeDefineToInt(subr2.getFrom());
		int to2 = sizeDefineToInt(subr2.getTo());

		if (from1 != from2 || to1 != to2) {
			return false;
		}

		return true;
	}

	/**
	 * 
	 * @return DomainVarDecl nested in TemporalPrimaryExpr. return null if no
	 *         VarDecl at all
	 */
	public static DomainVarDecl extractDomainVarDeclFromTemporalPrimaryExpr(TemporalPrimaryExpr temporalPrimaryExpr) {
		if (temporalPrimaryExpr.getPointer() != null) {
			// TODO: fix refactor of xtext!
			if (temporalPrimaryExpr.getPointer() instanceof DomainVarDecl) {
				return (DomainVarDecl) temporalPrimaryExpr.getPointer();
			}
		}
		if (temporalPrimaryExpr.getOperator() != null && temporalPrimaryExpr.getOperator().equals(NEXT)) {
			TemporalExpression temporalExpressionWithinNext = temporalPrimaryExpr.getTemporalExpression();
			if (temporalExpressionWithinNext instanceof TemporalPrimaryExpr) {
				return extractDomainVarDeclFromTemporalPrimaryExpr((TemporalPrimaryExpr) temporalExpressionWithinNext);
			}
		}
		return null;
	}

	/**
	 * 
	 * @return VarDecl nested in TemporalPrimaryExpr. return null if no VarDecl at
	 *         all
	 */
	public static VarDecl extractVarDeclFromTemporalPrimaryExpr(TemporalPrimaryExpr temporalPrimaryExpr) {
		if (temporalPrimaryExpr.getPointer() != null) {
			// TODO: fix refactor of xtext!
			if (temporalPrimaryExpr.getPointer() instanceof VarDecl) {
				return (VarDecl) temporalPrimaryExpr.getPointer();
			}
		}
		if (temporalPrimaryExpr.getOperator() != null && temporalPrimaryExpr.getOperator().equals(NEXT)) {
			TemporalExpression temporalExpressionWithinNext = temporalPrimaryExpr.getTemporalExpression();
			if (temporalExpressionWithinNext instanceof TemporalPrimaryExpr) {
				return extractVarDeclFromTemporalPrimaryExpr((TemporalPrimaryExpr) temporalExpressionWithinNext);
			}
		}
		return null;
	}

	/**
	 * 
	 * @return PatternParam nested in TemporalPrimaryExpr. return null if no
	 *         PatternParam at all
	 */
	public static PatternParam extractPatternParamFromTemporalPrimaryExpr(TemporalPrimaryExpr temporalPrimaryExpr) {
		if (temporalPrimaryExpr.getPointer() != null && temporalPrimaryExpr.getPointer() instanceof PatternParam) {
			return (PatternParam) temporalPrimaryExpr.getPointer();
		}
		if (temporalPrimaryExpr.getOperator() != null && temporalPrimaryExpr.getOperator().equals(NEXT)) {
			TemporalExpression temporalExpressionWithinNext = temporalPrimaryExpr.getTemporalExpression();
			if (temporalExpressionWithinNext instanceof TemporalPrimaryExpr) {
				return extractPatternParamFromTemporalPrimaryExpr((TemporalPrimaryExpr) temporalExpressionWithinNext);
			}
		}

		return null;
	}

	/**
	 * 
	 * @return TypedParam nested in TemporalPrimaryExpr. return null if no
	 *         TypedParam at all
	 */
	public static TypedParam extractTypedParamFromTemporalPrimaryExpr(TemporalPrimaryExpr temporalPrimaryExpr) {
		if (temporalPrimaryExpr.getPointer() != null && temporalPrimaryExpr.getPointer() instanceof TypedParam) {
			return (TypedParam) temporalPrimaryExpr.getPointer();
		}
		if (temporalPrimaryExpr.getOperator() != null && temporalPrimaryExpr.getOperator().equals(NEXT)) {
			TemporalExpression temporalExpressionWithinNext = temporalPrimaryExpr.getTemporalExpression();
			if (temporalExpressionWithinNext instanceof TemporalPrimaryExpr) {
				return extractTypedParamFromTemporalPrimaryExpr((TemporalPrimaryExpr) temporalExpressionWithinNext);
			}
		}

		return null;
	}

	/**
	 * 
	 * @return TypeConstant nested in TemporalPrimaryExpr. return null if no
	 *         TypeConstant at all
	 */
	public static TypeConstant extractTypeConstantFromTemporalPrimaryExpr(TemporalPrimaryExpr temporalPrimaryExpr) {
		if (temporalPrimaryExpr.getPointer() != null && temporalPrimaryExpr.getPointer() instanceof TypeConstant) {
			return (TypeConstant) temporalPrimaryExpr.getPointer();
		}

		return null;
	}

	static class Range {
		Subrange subrange = null;
		Integer value = null;
	}

	static class IllegalIndexException extends Exception {
		private static final long serialVersionUID = 1L;

	}

	public static BooleanAndString isNumericExpression(TemporalExpression temporalExpression,
			List<String> alreadySeenDefineDecls) {
		if (temporalExpression instanceof TemporalImpExpr) {
			return new BooleanAndString(false, "IMPLIES expression found");
		} else if (temporalExpression instanceof TemporalIffExpr) {
			return new BooleanAndString(false, "IFF expression found");
		} else if (temporalExpression instanceof TemporalOrExpr) {
			return new BooleanAndString(false, "OR expression found");
		} else if (temporalExpression instanceof TemporalAndExpr) {
			return new BooleanAndString(false, "AND expression found");
		} else if (temporalExpression instanceof TemporalRelationalExpr) {
			return new BooleanAndString(false, "Relational expression found");
		} else if (temporalExpression instanceof TemporalBinaryExpr) {
			return new BooleanAndString(false, "PastLTL expression found");
		} else if (temporalExpression instanceof TemporalUnaryExpr) {
			return new BooleanAndString(false, "PastLTL expression found");
		} else if (temporalExpression instanceof Constant) {
			Constant constant = (Constant) temporalExpression;
			if (constant.getBooleanValue() != null) {
				// it's not an int
				return new BooleanAndString(false, "Boolean constant (true/false) found");
			}
		} else if (temporalExpression instanceof TemporalPrimaryExpr) {
			TemporalPrimaryExpr temporalPrimaryExpr = (TemporalPrimaryExpr) temporalExpression;
			if (temporalPrimaryExpr.getPointer() != null) {
				if (temporalPrimaryExpr.getPointer() instanceof VarDecl) {
					VarDecl varDecl = (VarDecl) temporalPrimaryExpr.getPointer();
					VarType varType = varDecl.getType();
					boolean isVarTypeNumeric = isVarTypeNumeric(varType);
					if (!isVarTypeNumeric) {
						return new BooleanAndString(false, "Non-numeric variable found");
					}
				} else if (temporalPrimaryExpr.getPointer() instanceof Monitor) {
					Monitor mon = (Monitor) temporalPrimaryExpr.getPointer();
					VarType varType = mon.getType();
					boolean isVarTypeNumeric = isVarTypeNumeric(varType);
					if (!isVarTypeNumeric) {
						return new BooleanAndString(false, "Non-numeric monitor found");
					}
				} else if (temporalPrimaryExpr.getPointer() instanceof TypedParam) {
					TypedParam typedParam = (TypedParam) temporalPrimaryExpr.getPointer();
					VarType varType = typedParam.getType();
					boolean isVarTypeNumeric = isVarTypeNumeric(varType);
					if (!isVarTypeNumeric) {
						return new BooleanAndString(false, "Non-numeric variable found");
					}
				} else if (temporalPrimaryExpr.getPointer() instanceof PatternParam) {
					return new BooleanAndString(false, "PatternParam found");
				} else if (temporalPrimaryExpr.getPointer() instanceof TypeConstant) {
					return new BooleanAndString(false, "TypeConstant found");
				} else if (temporalPrimaryExpr.getPointer() instanceof DefineDecl) {
					DefineDecl defineDecl = (DefineDecl) temporalPrimaryExpr.getPointer();
					// cycles danger
					if (alreadySeenDefineDecls == null) {
						alreadySeenDefineDecls = new ArrayList<>();
					} else if (alreadySeenDefineDecls.contains(defineDecl.getName())) {
						return null; // to prevent stack overflow. we handle this error somewhere else so just return
						// "no problem" here.
					}
					alreadySeenDefineDecls.add(defineDecl.getName());
					BooleanAndString isDefineExpressionNumeric = isNumericExpression(defineDecl.getSimpleExpr(),
							alreadySeenDefineDecls); // go inside
					if (!isDefineExpressionNumeric.getBoolean()) {
						return new BooleanAndString(false, "Non-numeric define found");
					}
				} else if (temporalPrimaryExpr.getPointer() instanceof DomainVarDecl) {
					DomainVarDecl domainVarDecl = (DomainVarDecl) temporalPrimaryExpr.getPointer();
					VarType varType = domainVarDecl.getDomainType();
					if (!isVarTypeNumeric(varType)) {
						return new BooleanAndString(false, "Non-numeric variable found");
					}
				} else if (temporalPrimaryExpr.getPointer() instanceof DefineRegExpDecl) {
					return new BooleanAndString(false, "Reference to a regular expression found");
				}
			} else if (temporalPrimaryExpr.getPredPatt() != null) {
				return new BooleanAndString(false, "Reference to predicate/pattern found");
			} else if (temporalPrimaryExpr.getOperator() != null && temporalPrimaryExpr.getOperator().equals(NEXT)) {
				TemporalExpression temporalExpressionWithinNext = temporalPrimaryExpr.getTemporalExpression();
				BooleanAndString isTemporalExpressionWithinNextNumeric = isNumericExpression(
						temporalExpressionWithinNext, alreadySeenDefineDecls); // go inside
				if (!isTemporalExpressionWithinNextNumeric.getBoolean()) {
					return new BooleanAndString(false, "Non-numeric expression within 'next' found");
				}
			} else if (temporalPrimaryExpr.getOperator() != null && temporalPrimaryExpr.getOperator().equals(NOT)) {
				return new BooleanAndString(false, "'!' found");
			}
		}

		return new BooleanAndString(true);
	}

	static boolean isVarTypeNumeric(VarType varType) {
		return isVarTypeNumeric(varType, null);
	}

	static boolean isVarTypeNumeric(VarType varType, List<String> alreadySeenTypeDefs) {
		if (varType.getSubr() != null) {
			return true;
		}
		if (varType.getType() != null) {
			if (alreadySeenTypeDefs == null) {
				alreadySeenTypeDefs = new ArrayList<>();
			} else if (alreadySeenTypeDefs.contains(varType.getType().getName())) {
				return false; // meaningless, just to prevent stack overflowing
			}
			alreadySeenTypeDefs.add(varType.getType().getName());
			VarType typeOfTypeDef = varType.getType().getType();
			return isVarTypeNumeric(typeOfTypeDef, alreadySeenTypeDefs);
		}
		return false;
	}

	public static List<VarDecl> getNonPrimedVarDecls(List<TemporalPrimaryExpr> temporalPrimaryExprsList) {
		List<VarDecl> nonPrimedVarDecls = new ArrayList<>();

		for (TemporalPrimaryExpr temporalPrimaryExpr : temporalPrimaryExprsList) {
			if (temporalPrimaryExpr.getPointer() != null && temporalPrimaryExpr.getPointer() instanceof VarDecl) {
				nonPrimedVarDecls.add(((VarDecl) temporalPrimaryExpr.getPointer()));
			}
		}
		return nonPrimedVarDecls;
	}

	public static List<VarDecl> getPrimedVarDecls(List<TemporalPrimaryExpr> temporalPrimaryExprsList) {
		List<VarDecl> primedVarDecls = new ArrayList<>();
		for (TemporalPrimaryExpr temporalPrimaryExpr : temporalPrimaryExprsList) {
			if (temporalPrimaryExpr.getOperator() != null && temporalPrimaryExpr.getOperator().equals(NEXT)) {
				TemporalExpression nestedTemporalExpression = temporalPrimaryExpr.getTemporalExpression();
				if (nestedTemporalExpression instanceof TemporalPrimaryExpr) {
					TemporalPrimaryExpr nestedTemporalPrimaryExpr = (TemporalPrimaryExpr) nestedTemporalExpression;
					if (nestedTemporalPrimaryExpr.getPointer() != null
							&& nestedTemporalPrimaryExpr.getPointer() instanceof VarDecl) {
						primedVarDecls.add(((VarDecl) nestedTemporalPrimaryExpr.getPointer()));
					}
				}
			}
		}
		return primedVarDecls;
	}

	public static boolean isSysVarDecl(VarDecl varDecl) {
		Var parentVar = EcoreUtil2.getContainerOfType(varDecl, Var.class);
		if (parentVar.getKind().getName().equals(SYS)) {
			return true;
		}
		return false;
	}

	public static boolean isAuxVarDecl(VarDecl varDecl) {
		Var parentVar = EcoreUtil2.getContainerOfType(varDecl, Var.class);
		if (parentVar.getKind().getName().equals(AUX)) {
			return true;
		}
		return false;
	}

	public static BooleanAndString isBooleanExpression(TemporalExpression temporalExpression) {
		return isBooleanExpression(temporalExpression, null);
	}

	public static BooleanAndString isNumericExpression(TemporalExpression temporalExpression) {
		return isNumericExpression(temporalExpression, null);
	}

	public static boolean isSameVarType(VarType type1, VarType type2) {
		type1 = getVarType(type1);
		type2 = getVarType(type2);

		String name1 = type1.getName();
		String name2 = type2.getName();

		if ((name1 != null && name2 == null) || (name2 != null && name1 == null)) {
			return false;
		} else if (name1 != null && name2 != null && !name1.equals(name2)) {
			return false;
		}
		if ((type1.getSubr() != null || type2.getSubr() != null) && !isSameSubrange(type1.getSubr(), type2.getSubr())) {
			return false;
		}
		if ((type1.getConst() != null || type2.getConst() != null)
				&& !isSameConsts(type1.getConst(), type2.getConst())) {
			return false;
		}

		if ((type1.getDimensions() != null || type2.getDimensions() != null)
				&& !isSameDimensions(type1.getDimensions(), type2.getDimensions())) {
			return false;
		}

		return true;
	}

	public static VarType getVarType(VarType varType) {
		return getVarType(varType, new HashSet<Integer>(), new ArrayList<SizeDefineDecl>(), false);
	}

	public static VarType getVarType(VarType varType, Set<Integer> seenTypeNames, List<SizeDefineDecl> dimensions,
			boolean isRecursiveCallOccuredAtLeastOnce) {
		int hashCode = varType.hashCode();
		if (seenTypeNames.contains(hashCode)) {
			// in case of cycles, prevent stack overflow (cycle handled somewhere else)
			return null;
		}

		if (varType.getDimensions() != null) {
			// so we don't lose this information in the recursive calls.
			dimensions.addAll(varType.getDimensions());
		}
		if (varType.getType() == null) {
			// no defined type

			if (!isRecursiveCallOccuredAtLeastOnce || dimensions.size() == 0) {
				// save some time in case this is the first call (and we have dim.) or
				// if it's not the first call, but anyway no dimensions met during the calls
				return varType;
			} else {
				return new CustomizedVarType(varType, dimensions);
			}
		}
		TypeDef typeDef = varType.getType();
		seenTypeNames.add(hashCode);
		return getVarType(typeDef.getType(), seenTypeNames, dimensions, true);
	}

	public static TypeCheckIssue typeCheckCorrectWayOfAccessingArray(TemporalPrimaryExpr temporalPrimaryExpr,
			EReference eReference) {
		if (temporalPrimaryExpr.getPointer() != null
				&& (temporalPrimaryExpr.getIndex() == null || temporalPrimaryExpr.getIndex().size() == 0)) {
			if (temporalPrimaryExpr.getPointer() instanceof VarDecl) {
				VarDecl varDecl = (VarDecl) temporalPrimaryExpr.getPointer();
				VarType varType = getVarType(varDecl.getType());
				if (varType.getDimensions() != null && varType.getDimensions().size() > 0
						&& !ARRAY_FUNCTIONS.contains(temporalPrimaryExpr.getOperator())) {
					return new TypeCheckError(eReference,
							IssueMessages.ILLEGAL_ACCESS_TO_ARRAY + " '" + varDecl.getName() + "'");
				}
			} else if (temporalPrimaryExpr.getPointer() instanceof TypedParam) {
				TypedParam typedParam = (TypedParam) temporalPrimaryExpr.getPointer();
				VarType varType = getVarType(typedParam.getType());
				if (varType.getDimensions() != null && varType.getDimensions().size() > 0
						&& !ARRAY_FUNCTIONS.contains(temporalPrimaryExpr.getOperator())) {
					return new TypeCheckError(eReference,
							IssueMessages.ILLEGAL_ACCESS_TO_ARRAY + " '" + typedParam.getName() + "'");
				}
			} else if (temporalPrimaryExpr.getPointer() instanceof DefineDecl) {
				DefineDecl defineDecl = (DefineDecl) temporalPrimaryExpr.getPointer();
				if (defineDecl.getDimensions() != null && defineDecl.getDimensions().size() > 0
						&& !ARRAY_FUNCTIONS.contains(temporalPrimaryExpr.getOperator())) {
					return new TypeCheckError(eReference,
							IssueMessages.ILLEGAL_ACCESS_TO_DEFINE_ARRAY + " '" + defineDecl.getName() + "'");
				}
			}
		}
		return null;
	}

	private static TypeCheckIssue typeCheckCorrectWayOfAccessingArray(TemporalPrimaryExpr temporalPrimaryExpr,
			String side, EAttribute eAttribute) {
		if (temporalPrimaryExpr.getPointer() != null
				&& (temporalPrimaryExpr.getIndex() == null || temporalPrimaryExpr.getIndex().size() == 0)) {
			if (temporalPrimaryExpr.getPointer() instanceof VarDecl) {
				VarDecl varDecl = (VarDecl) temporalPrimaryExpr.getPointer();
				VarType varType = getVarType(varDecl.getType());
				if (varType.getDimensions() != null && varType.getDimensions().size() > 0
						&& !ARRAY_FUNCTIONS.contains(temporalPrimaryExpr.getOperator())) {
					return new TypeCheckError(eAttribute,
							IssueMessages.ILLEGAL_ACCESS_TO_ARRAY + " '" + varDecl.getName() + "' (" + side + " side)");
				}
			} else if (temporalPrimaryExpr.getPointer() instanceof TypedParam) {
				TypedParam typedParam = (TypedParam) temporalPrimaryExpr.getPointer();
				VarType varType = getVarType(typedParam.getType());
				if (varType.getDimensions() != null && varType.getDimensions().size() > 0
						&& !ARRAY_FUNCTIONS.contains(temporalPrimaryExpr.getOperator())) {
					return new TypeCheckError(eAttribute, IssueMessages.ILLEGAL_ACCESS_TO_ARRAY + " '"
							+ typedParam.getName() + "' (" + side + " side)");
				}
			}
		}
		return null;
	}

	public static TypeCheckIssue typeCheckCorrectWayOfAccessingArray(TemporalExpression leftTemporalExpression,
			TemporalExpression rightTemporalExpression, EAttribute eAttribute) {
		if (leftTemporalExpression instanceof TemporalPrimaryExpr) {
			TemporalPrimaryExpr temporalPrimaryExpr = (TemporalPrimaryExpr) leftTemporalExpression;
			TypeCheckIssue issue = TypeSystemUtils.typeCheckCorrectWayOfAccessingArray(temporalPrimaryExpr, "Left",
					eAttribute);
			if (issue != null) {
				return issue;
			}
		}

		if (rightTemporalExpression instanceof TemporalPrimaryExpr) {
			TemporalPrimaryExpr temporalPrimaryExpr = (TemporalPrimaryExpr) rightTemporalExpression;
			TypeCheckIssue issue = TypeSystemUtils.typeCheckCorrectWayOfAccessingArray(temporalPrimaryExpr, "Right",
					eAttribute);
			if (issue != null) {
				return issue;
			}
		}

		return null;
	}

	public static List<TemporalPrimaryExpr> getAllTemporaryPrimaryExprs(TemporalExpression temporalExpresison) {
		return getAllTemporaryPrimaryExprs(temporalExpresison, false);
	}

	public static List<TemporalPrimaryExpr> getAllTemporaryPrimaryExprs(TemporalExpression temporalExpression,
			boolean getOnlyTemporalPrimaryExprWithPointers) {
		List<TemporalPrimaryExpr> tpesList = new ArrayList<>();
		if (temporalExpression instanceof TemporalPrimaryExpr) {
			tpesList.add((TemporalPrimaryExpr) temporalExpression);
		} else {
			tpesList.addAll(EcoreUtil2.getAllContentsOfType(temporalExpression, TemporalPrimaryExpr.class));
		}
		if (getOnlyTemporalPrimaryExprWithPointers) {
			List<TemporalPrimaryExpr> filterdList = new ArrayList<>();
			for (TemporalPrimaryExpr tpe : tpesList) {
				if (tpe.getPointer() != null) {
					filterdList.add(tpe);
				}
			}
			return filterdList;
		} else {
			return tpesList;
		}
	}

	// returns the integer value of the input object
	public static int sizeDefineToInt(SizeDefineDecl dd) {
		int dim = -1;
		if (dd.getName() instanceof DefineDecl) {
			try {
				dim = calcArithmeticExpression(((DefineDecl) dd.getName()).getSimpleExpr());
			} catch (NotArithmeticExpressionException ignored) {
			}
		} else if (dd.getArithExp() != null) {
			try {
				dim = calcArithmeticExpression(dd.getArithExp());
			} catch (NotArithmeticExpressionException ignored) {
			}
		} else {
			dim = dd.getValue();
		}
		return dim;
	}

	// An exception that indicates the expression is not arithmetic
	static class NotArithmeticExpressionException extends Exception {
		private static final long serialVersionUID = 2L;
	}

	public static List<List<Integer>> calcArithmeticExpressions(List<TemporalExpression> arithmeticExprList)
			throws NotArithmeticExpressionException {

		List<List<Integer>> out = new ArrayList<>();
		for (TemporalExpression arithmeticExpr : arithmeticExprList) {
			try {
				out.add(Arrays.asList(calcArithmeticExpression(arithmeticExpr)));
			} catch (NotArithmeticExpressionException e) {
				if ((arithmeticExpr instanceof TemporalPrimaryExpr)
						&& (((TemporalPrimaryExpr) arithmeticExpr).getPointer() instanceof VarDecl)) {

					// we have an index which is a variable reference
					TemporalPrimaryExpr temporalPrimaryExpr = (TemporalPrimaryExpr) arithmeticExpr;
					VarDecl varDecl = (VarDecl) temporalPrimaryExpr.getPointer();
					VarType varType = TypeSystemUtils.getVarType(varDecl.getType());

					// if(!isVarTypeNumeric(varDecl.getType())) {throw e;}
					if (varType.getSubr() == null) { // Non-Integer typed variable (non-numeric, invalid index)
						throw e;
					}

					// add domain interval to the list
					out.add(Arrays.asList(TypeSystemUtils.sizeDefineToInt(varType.getSubr().getFrom()),
							TypeSystemUtils.sizeDefineToInt(varType.getSubr().getTo())));
				} else {
					throw e;
				}
			}
		}
		return out;
	}

	// Calculate the int value of an arithmetic expression
	public static int calcArithmeticExpression(TemporalExpression arithmeticExpr)
			throws NotArithmeticExpressionException {

		if (!isNumericExpression(arithmeticExpr).getBoolean()) {
			throw new NotArithmeticExpressionException();
		}

		if (arithmeticExpr instanceof Constant) {
			return ((Constant) arithmeticExpr).getIntegerValue();
		} else if (arithmeticExpr instanceof TemporalRemainderExpr) {
			TemporalRemainderExpr remainderExpr = (TemporalRemainderExpr) arithmeticExpr;
			return calcArithmeticExpression(remainderExpr.getLeft())
					% calcArithmeticExpression(remainderExpr.getRight());

		} else if (arithmeticExpr instanceof TemporalAdditiveExpr) {
			TemporalAdditiveExpr additiveExpr = (TemporalAdditiveExpr) arithmeticExpr;
			return calcAdditiveOrMultiplicativeExpression(additiveExpr.getElements(), additiveExpr.getOperator());
		} else if (arithmeticExpr instanceof TemporalMultiplicativeExpr) {
			TemporalMultiplicativeExpr multiplicativeExpr = (TemporalMultiplicativeExpr) arithmeticExpr;
			return calcAdditiveOrMultiplicativeExpression(multiplicativeExpr.getElements(),
					multiplicativeExpr.getOperator());

		} else if (arithmeticExpr instanceof TemporalPrimaryExpr) {
			TemporalPrimaryExpr temporalPrimaryExpr = (TemporalPrimaryExpr) arithmeticExpr;
			if (temporalPrimaryExpr.getPointer() != null && temporalPrimaryExpr.getPointer() instanceof DefineDecl) {
				DefineDecl defineDecl = (DefineDecl) temporalPrimaryExpr.getPointer();
				return calcArithmeticExpression(defineDecl.getSimpleExpr());
			}
		}

		throw new NotArithmeticExpressionException();
	}

	// Calculate the result of an additive or multiplicative expression, given it's
	// elements and operators
	private static int calcAdditiveOrMultiplicativeExpression(EList<TemporalExpression> elements,
			EList<String> operators) throws NotArithmeticExpressionException {

		int value = calcArithmeticExpression(elements.get(0));
		for (int i = 1; i < elements.size(); i++) {
			String operator = operators.get(i - 1);
			int elementValue = calcArithmeticExpression(elements.get(i));
			if (operator.equals("+")) {
				value += elementValue;
			} else if (operator.equals("-")) {
				value -= elementValue;
			} else if (operator.equals("*")) {
				value *= elementValue;
			} else {
				value /= elementValue;
			}
		}
		return value;
	}

	// gets a SizeDefineDecl list
	// returns a list that contsins the integer values of the objects in the list
	public static List<Integer> sizeDefineToInt(List<SizeDefineDecl> ll) {
		List<Integer> out = new ArrayList<Integer>();
		for (SizeDefineDecl dd : ll) {

			out.add(sizeDefineToInt(dd));
		}

		return out;
	}

	// i.e. if arr[2][4] is accessed as arr[0], dimensions need to be {4} and not
	// {2,4}
	// arr[2] , accessed as arr[0] should be {}
	public static VarType updateDimensionsAccordingToTheWayArrIsAccessed(VarType varType,
			EList<TemporalExpression> indexes) {
		if (varType.getDimensions() == null || varType.getDimensions().size() == 0) {
			return varType;
		}
		int numDimensionAccessed = indexes.size();
		List<SizeDefineDecl> updatedDimensions = new ArrayList<>();

		if (numDimensionAccessed == varType.getDimensions().size()) {
			return new CustomizedVarType(varType, new ArrayList<SizeDefineDecl>());
		} else {
			for (int i = numDimensionAccessed; i < varType.getDimensions().size(); i++) {
				updatedDimensions.add(varType.getDimensions().get(i));
			}
			return new CustomizedVarType(varType, updatedDimensions);
		}

	}

	public static List<String> getPermittedTypeConstants(VarType varType) {
		return getPermittedTypeConstants(varType, null);
	}

	private static List<String> getPermittedTypeConstants(VarType varType, List<String> alreadySeenTypeDefs) {

		if (varType.getConst() != null && varType.getConst().size() > 0) {
			List<String> permittedTypeconstants = new ArrayList<>();
			for (TypeConstant typeConstant : varType.getConst()) {
				permittedTypeconstants.add(typeConstant.getName());
			}
			return permittedTypeconstants;
		}
		if (varType.getType() != null) {
			if (alreadySeenTypeDefs == null) {
				alreadySeenTypeDefs = new ArrayList<>();
			} else if (alreadySeenTypeDefs.contains(varType.getType().getName())) {
				return null; // meaningless, just to prevent stackoverflow
			}
			alreadySeenTypeDefs.add(varType.getType().getName());
			VarType varTypeOfTypeDef = varType.getType().getType();
			return getPermittedTypeConstants(varTypeOfTypeDef, alreadySeenTypeDefs);
		}
		return null;
	}
}
