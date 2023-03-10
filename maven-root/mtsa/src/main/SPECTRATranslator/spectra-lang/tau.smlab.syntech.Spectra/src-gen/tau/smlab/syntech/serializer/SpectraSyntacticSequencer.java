/*
 * generated by Xtext 2.25.0
 */
package tau.smlab.syntech.serializer;

import com.google.inject.Inject;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.IGrammarAccess;
import org.eclipse.xtext.RuleCall;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.serializer.analysis.GrammarAlias.AbstractElementAlias;
import org.eclipse.xtext.serializer.analysis.GrammarAlias.AlternativeAlias;
import org.eclipse.xtext.serializer.analysis.GrammarAlias.TokenAlias;
import org.eclipse.xtext.serializer.analysis.ISyntacticSequencerPDAProvider.ISynNavigable;
import org.eclipse.xtext.serializer.analysis.ISyntacticSequencerPDAProvider.ISynTransition;
import org.eclipse.xtext.serializer.sequencer.AbstractSyntacticSequencer;
import tau.smlab.syntech.services.SpectraGrammarAccess;

@SuppressWarnings("all")
public class SpectraSyntacticSequencer extends AbstractSyntacticSequencer {

	protected SpectraGrammarAccess grammarAccess;
	protected AbstractElementAlias match_EXGar_GarKeyword_0_1_or_GuaranteeKeyword_0_0;
	protected AbstractElementAlias match_LTLAsm_AsmKeyword_0_1_or_AssumptionKeyword_0_0;
	protected AbstractElementAlias match_LTLAsm___IniKeyword_2_0_0_0_or_InitiallyKeyword_2_0_0_1__q;
	protected AbstractElementAlias match_LTLGar_GarKeyword_0_1_or_GuaranteeKeyword_0_0;
	protected AbstractElementAlias match_LTLGar___IniKeyword_2_0_0_0_or_InitiallyKeyword_2_0_0_1__q;
	protected AbstractElementAlias match_Model_ModuleKeyword_1_0_or_SpecKeyword_1_1;
	protected AbstractElementAlias match_Monitor_AlwKeyword_4_0_2_0_1_or_AlwaysKeyword_4_0_2_0_0;
	protected AbstractElementAlias match_Monitor_GKeyword_4_0_1_0_0_or_TransKeyword_4_0_1_0_1;
	protected AbstractElementAlias match_Monitor___IniKeyword_4_0_0_0_0_or_InitiallyKeyword_4_0_0_0_1__q;
	protected AbstractElementAlias match_Pattern_AlwEvKeyword_3_2_0_3_0_1_or_AlwaysEventuallyKeyword_3_2_0_3_0_2_or_GFKeyword_3_2_0_3_0_0;
	protected AbstractElementAlias match_Pattern_AlwKeyword_3_2_0_2_0_1_or_AlwaysKeyword_3_2_0_2_0_0;
	protected AbstractElementAlias match_Pattern_GKeyword_3_2_0_1_0_0_or_TransKeyword_3_2_0_1_0_1;
	protected AbstractElementAlias match_Pattern___IniKeyword_3_2_0_0_0_0_or_InitiallyKeyword_3_2_0_0_0_1__q;
	protected AbstractElementAlias match_PrimaryRegExp_LeftParenthesisKeyword_0_0_a;
	protected AbstractElementAlias match_PrimaryRegExp_LeftParenthesisKeyword_0_0_p;
	protected AbstractElementAlias match_TemporalPrimaryExpr_LeftParenthesisKeyword_1_0_a;
	protected AbstractElementAlias match_TemporalPrimaryExpr_LeftParenthesisKeyword_1_0_p;
	
	@Inject
	protected void init(IGrammarAccess access) {
		grammarAccess = (SpectraGrammarAccess) access;
		match_EXGar_GarKeyword_0_1_or_GuaranteeKeyword_0_0 = new AlternativeAlias(false, false, new TokenAlias(false, false, grammarAccess.getEXGarAccess().getGarKeyword_0_1()), new TokenAlias(false, false, grammarAccess.getEXGarAccess().getGuaranteeKeyword_0_0()));
		match_LTLAsm_AsmKeyword_0_1_or_AssumptionKeyword_0_0 = new AlternativeAlias(false, false, new TokenAlias(false, false, grammarAccess.getLTLAsmAccess().getAsmKeyword_0_1()), new TokenAlias(false, false, grammarAccess.getLTLAsmAccess().getAssumptionKeyword_0_0()));
		match_LTLAsm___IniKeyword_2_0_0_0_or_InitiallyKeyword_2_0_0_1__q = new AlternativeAlias(false, true, new TokenAlias(false, false, grammarAccess.getLTLAsmAccess().getIniKeyword_2_0_0_0()), new TokenAlias(false, false, grammarAccess.getLTLAsmAccess().getInitiallyKeyword_2_0_0_1()));
		match_LTLGar_GarKeyword_0_1_or_GuaranteeKeyword_0_0 = new AlternativeAlias(false, false, new TokenAlias(false, false, grammarAccess.getLTLGarAccess().getGarKeyword_0_1()), new TokenAlias(false, false, grammarAccess.getLTLGarAccess().getGuaranteeKeyword_0_0()));
		match_LTLGar___IniKeyword_2_0_0_0_or_InitiallyKeyword_2_0_0_1__q = new AlternativeAlias(false, true, new TokenAlias(false, false, grammarAccess.getLTLGarAccess().getIniKeyword_2_0_0_0()), new TokenAlias(false, false, grammarAccess.getLTLGarAccess().getInitiallyKeyword_2_0_0_1()));
		match_Model_ModuleKeyword_1_0_or_SpecKeyword_1_1 = new AlternativeAlias(false, false, new TokenAlias(false, false, grammarAccess.getModelAccess().getModuleKeyword_1_0()), new TokenAlias(false, false, grammarAccess.getModelAccess().getSpecKeyword_1_1()));
		match_Monitor_AlwKeyword_4_0_2_0_1_or_AlwaysKeyword_4_0_2_0_0 = new AlternativeAlias(false, false, new TokenAlias(false, false, grammarAccess.getMonitorAccess().getAlwKeyword_4_0_2_0_1()), new TokenAlias(false, false, grammarAccess.getMonitorAccess().getAlwaysKeyword_4_0_2_0_0()));
		match_Monitor_GKeyword_4_0_1_0_0_or_TransKeyword_4_0_1_0_1 = new AlternativeAlias(false, false, new TokenAlias(false, false, grammarAccess.getMonitorAccess().getGKeyword_4_0_1_0_0()), new TokenAlias(false, false, grammarAccess.getMonitorAccess().getTransKeyword_4_0_1_0_1()));
		match_Monitor___IniKeyword_4_0_0_0_0_or_InitiallyKeyword_4_0_0_0_1__q = new AlternativeAlias(false, true, new TokenAlias(false, false, grammarAccess.getMonitorAccess().getIniKeyword_4_0_0_0_0()), new TokenAlias(false, false, grammarAccess.getMonitorAccess().getInitiallyKeyword_4_0_0_0_1()));
		match_Pattern_AlwEvKeyword_3_2_0_3_0_1_or_AlwaysEventuallyKeyword_3_2_0_3_0_2_or_GFKeyword_3_2_0_3_0_0 = new AlternativeAlias(false, false, new TokenAlias(false, false, grammarAccess.getPatternAccess().getAlwEvKeyword_3_2_0_3_0_1()), new TokenAlias(false, false, grammarAccess.getPatternAccess().getAlwaysEventuallyKeyword_3_2_0_3_0_2()), new TokenAlias(false, false, grammarAccess.getPatternAccess().getGFKeyword_3_2_0_3_0_0()));
		match_Pattern_AlwKeyword_3_2_0_2_0_1_or_AlwaysKeyword_3_2_0_2_0_0 = new AlternativeAlias(false, false, new TokenAlias(false, false, grammarAccess.getPatternAccess().getAlwKeyword_3_2_0_2_0_1()), new TokenAlias(false, false, grammarAccess.getPatternAccess().getAlwaysKeyword_3_2_0_2_0_0()));
		match_Pattern_GKeyword_3_2_0_1_0_0_or_TransKeyword_3_2_0_1_0_1 = new AlternativeAlias(false, false, new TokenAlias(false, false, grammarAccess.getPatternAccess().getGKeyword_3_2_0_1_0_0()), new TokenAlias(false, false, grammarAccess.getPatternAccess().getTransKeyword_3_2_0_1_0_1()));
		match_Pattern___IniKeyword_3_2_0_0_0_0_or_InitiallyKeyword_3_2_0_0_0_1__q = new AlternativeAlias(false, true, new TokenAlias(false, false, grammarAccess.getPatternAccess().getIniKeyword_3_2_0_0_0_0()), new TokenAlias(false, false, grammarAccess.getPatternAccess().getInitiallyKeyword_3_2_0_0_0_1()));
		match_PrimaryRegExp_LeftParenthesisKeyword_0_0_a = new TokenAlias(true, true, grammarAccess.getPrimaryRegExpAccess().getLeftParenthesisKeyword_0_0());
		match_PrimaryRegExp_LeftParenthesisKeyword_0_0_p = new TokenAlias(true, false, grammarAccess.getPrimaryRegExpAccess().getLeftParenthesisKeyword_0_0());
		match_TemporalPrimaryExpr_LeftParenthesisKeyword_1_0_a = new TokenAlias(true, true, grammarAccess.getTemporalPrimaryExprAccess().getLeftParenthesisKeyword_1_0());
		match_TemporalPrimaryExpr_LeftParenthesisKeyword_1_0_p = new TokenAlias(true, false, grammarAccess.getTemporalPrimaryExprAccess().getLeftParenthesisKeyword_1_0());
	}
	
	@Override
	protected String getUnassignedRuleCallToken(EObject semanticObject, RuleCall ruleCall, INode node) {
		if (ruleCall.getRule() == grammarAccess.getTOK_SEMIRule())
			return getTOK_SEMIToken(semanticObject, ruleCall, node);
		return "";
	}
	
	/**
	 * TOK_SEMI:
	 * 	';';
	 */
	protected String getTOK_SEMIToken(EObject semanticObject, RuleCall ruleCall, INode node) {
		if (node != null)
			return getTokenText(node);
		return ";";
	}
	
	@Override
	protected void emitUnassignedTokens(EObject semanticObject, ISynTransition transition, INode fromNode, INode toNode) {
		if (transition.getAmbiguousSyntaxes().isEmpty()) return;
		List<INode> transitionNodes = collectNodes(fromNode, toNode);
		for (AbstractElementAlias syntax : transition.getAmbiguousSyntaxes()) {
			List<INode> syntaxNodes = getNodesFor(transitionNodes, syntax);
			if (match_EXGar_GarKeyword_0_1_or_GuaranteeKeyword_0_0.equals(syntax))
				emit_EXGar_GarKeyword_0_1_or_GuaranteeKeyword_0_0(semanticObject, getLastNavigableState(), syntaxNodes);
			else if (match_LTLAsm_AsmKeyword_0_1_or_AssumptionKeyword_0_0.equals(syntax))
				emit_LTLAsm_AsmKeyword_0_1_or_AssumptionKeyword_0_0(semanticObject, getLastNavigableState(), syntaxNodes);
			else if (match_LTLAsm___IniKeyword_2_0_0_0_or_InitiallyKeyword_2_0_0_1__q.equals(syntax))
				emit_LTLAsm___IniKeyword_2_0_0_0_or_InitiallyKeyword_2_0_0_1__q(semanticObject, getLastNavigableState(), syntaxNodes);
			else if (match_LTLGar_GarKeyword_0_1_or_GuaranteeKeyword_0_0.equals(syntax))
				emit_LTLGar_GarKeyword_0_1_or_GuaranteeKeyword_0_0(semanticObject, getLastNavigableState(), syntaxNodes);
			else if (match_LTLGar___IniKeyword_2_0_0_0_or_InitiallyKeyword_2_0_0_1__q.equals(syntax))
				emit_LTLGar___IniKeyword_2_0_0_0_or_InitiallyKeyword_2_0_0_1__q(semanticObject, getLastNavigableState(), syntaxNodes);
			else if (match_Model_ModuleKeyword_1_0_or_SpecKeyword_1_1.equals(syntax))
				emit_Model_ModuleKeyword_1_0_or_SpecKeyword_1_1(semanticObject, getLastNavigableState(), syntaxNodes);
			else if (match_Monitor_AlwKeyword_4_0_2_0_1_or_AlwaysKeyword_4_0_2_0_0.equals(syntax))
				emit_Monitor_AlwKeyword_4_0_2_0_1_or_AlwaysKeyword_4_0_2_0_0(semanticObject, getLastNavigableState(), syntaxNodes);
			else if (match_Monitor_GKeyword_4_0_1_0_0_or_TransKeyword_4_0_1_0_1.equals(syntax))
				emit_Monitor_GKeyword_4_0_1_0_0_or_TransKeyword_4_0_1_0_1(semanticObject, getLastNavigableState(), syntaxNodes);
			else if (match_Monitor___IniKeyword_4_0_0_0_0_or_InitiallyKeyword_4_0_0_0_1__q.equals(syntax))
				emit_Monitor___IniKeyword_4_0_0_0_0_or_InitiallyKeyword_4_0_0_0_1__q(semanticObject, getLastNavigableState(), syntaxNodes);
			else if (match_Pattern_AlwEvKeyword_3_2_0_3_0_1_or_AlwaysEventuallyKeyword_3_2_0_3_0_2_or_GFKeyword_3_2_0_3_0_0.equals(syntax))
				emit_Pattern_AlwEvKeyword_3_2_0_3_0_1_or_AlwaysEventuallyKeyword_3_2_0_3_0_2_or_GFKeyword_3_2_0_3_0_0(semanticObject, getLastNavigableState(), syntaxNodes);
			else if (match_Pattern_AlwKeyword_3_2_0_2_0_1_or_AlwaysKeyword_3_2_0_2_0_0.equals(syntax))
				emit_Pattern_AlwKeyword_3_2_0_2_0_1_or_AlwaysKeyword_3_2_0_2_0_0(semanticObject, getLastNavigableState(), syntaxNodes);
			else if (match_Pattern_GKeyword_3_2_0_1_0_0_or_TransKeyword_3_2_0_1_0_1.equals(syntax))
				emit_Pattern_GKeyword_3_2_0_1_0_0_or_TransKeyword_3_2_0_1_0_1(semanticObject, getLastNavigableState(), syntaxNodes);
			else if (match_Pattern___IniKeyword_3_2_0_0_0_0_or_InitiallyKeyword_3_2_0_0_0_1__q.equals(syntax))
				emit_Pattern___IniKeyword_3_2_0_0_0_0_or_InitiallyKeyword_3_2_0_0_0_1__q(semanticObject, getLastNavigableState(), syntaxNodes);
			else if (match_PrimaryRegExp_LeftParenthesisKeyword_0_0_a.equals(syntax))
				emit_PrimaryRegExp_LeftParenthesisKeyword_0_0_a(semanticObject, getLastNavigableState(), syntaxNodes);
			else if (match_PrimaryRegExp_LeftParenthesisKeyword_0_0_p.equals(syntax))
				emit_PrimaryRegExp_LeftParenthesisKeyword_0_0_p(semanticObject, getLastNavigableState(), syntaxNodes);
			else if (match_TemporalPrimaryExpr_LeftParenthesisKeyword_1_0_a.equals(syntax))
				emit_TemporalPrimaryExpr_LeftParenthesisKeyword_1_0_a(semanticObject, getLastNavigableState(), syntaxNodes);
			else if (match_TemporalPrimaryExpr_LeftParenthesisKeyword_1_0_p.equals(syntax))
				emit_TemporalPrimaryExpr_LeftParenthesisKeyword_1_0_p(semanticObject, getLastNavigableState(), syntaxNodes);
			else acceptNodes(getLastNavigableState(), syntaxNodes);
		}
	}

	/**
	 * Ambiguous syntax:
	 *     'guarantee' | 'gar'
	 *
	 * This ambiguous syntax occurs at:
	 *     (rule start) (ambiguity) 'GE' elements+=TemporalInExpr
	 *     (rule start) (ambiguity) 'GEF' regExp=RegExp
	 *     (rule start) (ambiguity) 'GEF' regExpPointer=[DefineRegExpDecl|ID]
	 *     (rule start) (ambiguity) name=ID
	 */
	protected void emit_EXGar_GarKeyword_0_1_or_GuaranteeKeyword_0_0(EObject semanticObject, ISynNavigable transition, List<INode> nodes) {
		acceptNodes(transition, nodes);
	}
	
	/**
	 * Ambiguous syntax:
	 *     'assumption' | 'asm'
	 *
	 * This ambiguous syntax occurs at:
	 *     (rule start) (ambiguity) ('ini' | 'initially')? temporalExpr=QuantifierExpr
	 *     (rule start) (ambiguity) justice='GF'
	 *     (rule start) (ambiguity) justice='alwEv'
	 *     (rule start) (ambiguity) justice='alwaysEventually'
	 *     (rule start) (ambiguity) name=ID
	 *     (rule start) (ambiguity) safety='G'
	 *     (rule start) (ambiguity) safety='trans'
	 *     (rule start) (ambiguity) stateInv='alw'
	 *     (rule start) (ambiguity) stateInv='always'
	 *     (rule start) (ambiguity) trig=Trigger
	 */
	protected void emit_LTLAsm_AsmKeyword_0_1_or_AssumptionKeyword_0_0(EObject semanticObject, ISynNavigable transition, List<INode> nodes) {
		acceptNodes(transition, nodes);
	}
	
	/**
	 * Ambiguous syntax:
	 *     ('ini' | 'initially')?
	 *
	 * This ambiguous syntax occurs at:
	 *     (rule start) ('assumption' | 'asm') (ambiguity) temporalExpr=QuantifierExpr
	 *     name=ID ':' (ambiguity) temporalExpr=QuantifierExpr
	 *     params=TypedParamList '}' ':' (ambiguity) temporalExpr=QuantifierExpr
	 */
	protected void emit_LTLAsm___IniKeyword_2_0_0_0_or_InitiallyKeyword_2_0_0_1__q(EObject semanticObject, ISynNavigable transition, List<INode> nodes) {
		acceptNodes(transition, nodes);
	}
	
	/**
	 * Ambiguous syntax:
	 *     'guarantee' | 'gar'
	 *
	 * This ambiguous syntax occurs at:
	 *     (rule start) (ambiguity) ('ini' | 'initially')? temporalExpr=QuantifierExpr
	 *     (rule start) (ambiguity) justice='GF'
	 *     (rule start) (ambiguity) justice='alwEv'
	 *     (rule start) (ambiguity) justice='alwaysEventually'
	 *     (rule start) (ambiguity) name=ID
	 *     (rule start) (ambiguity) safety='G'
	 *     (rule start) (ambiguity) safety='trans'
	 *     (rule start) (ambiguity) stateInv='alw'
	 *     (rule start) (ambiguity) stateInv='always'
	 *     (rule start) (ambiguity) trig=Trigger
	 */
	protected void emit_LTLGar_GarKeyword_0_1_or_GuaranteeKeyword_0_0(EObject semanticObject, ISynNavigable transition, List<INode> nodes) {
		acceptNodes(transition, nodes);
	}
	
	/**
	 * Ambiguous syntax:
	 *     ('ini' | 'initially')?
	 *
	 * This ambiguous syntax occurs at:
	 *     (rule start) ('guarantee' | 'gar') (ambiguity) temporalExpr=QuantifierExpr
	 *     name=ID ':' (ambiguity) temporalExpr=QuantifierExpr
	 *     params=TypedParamList '}' ':' (ambiguity) temporalExpr=QuantifierExpr
	 */
	protected void emit_LTLGar___IniKeyword_2_0_0_0_or_InitiallyKeyword_2_0_0_1__q(EObject semanticObject, ISynNavigable transition, List<INode> nodes) {
		acceptNodes(transition, nodes);
	}
	
	/**
	 * Ambiguous syntax:
	 *     'module' | 'spec'
	 *
	 * This ambiguous syntax occurs at:
	 *     (rule start) (ambiguity) name=ID
	 *     imports+=Import (ambiguity) name=ID
	 */
	protected void emit_Model_ModuleKeyword_1_0_or_SpecKeyword_1_1(EObject semanticObject, ISynNavigable transition, List<INode> nodes) {
		acceptNodes(transition, nodes);
	}
	
	/**
	 * Ambiguous syntax:
	 *     'always' | 'alw'
	 *
	 * This ambiguous syntax occurs at:
	 *     initial+=TemporalInExpr TOK_SEMI (ambiguity) stateInv+=TemporalInExpr
	 *     name=ID '{' (ambiguity) stateInv+=TemporalInExpr
	 *     safety+=TemporalInExpr TOK_SEMI (ambiguity) stateInv+=TemporalInExpr
	 *     stateInv+=TemporalInExpr TOK_SEMI (ambiguity) stateInv+=TemporalInExpr
	 */
	protected void emit_Monitor_AlwKeyword_4_0_2_0_1_or_AlwaysKeyword_4_0_2_0_0(EObject semanticObject, ISynNavigable transition, List<INode> nodes) {
		acceptNodes(transition, nodes);
	}
	
	/**
	 * Ambiguous syntax:
	 *     'G' | 'trans'
	 *
	 * This ambiguous syntax occurs at:
	 *     initial+=TemporalInExpr TOK_SEMI (ambiguity) safety+=TemporalInExpr
	 *     name=ID '{' (ambiguity) safety+=TemporalInExpr
	 *     safety+=TemporalInExpr TOK_SEMI (ambiguity) safety+=TemporalInExpr
	 *     stateInv+=TemporalInExpr TOK_SEMI (ambiguity) safety+=TemporalInExpr
	 */
	protected void emit_Monitor_GKeyword_4_0_1_0_0_or_TransKeyword_4_0_1_0_1(EObject semanticObject, ISynNavigable transition, List<INode> nodes) {
		acceptNodes(transition, nodes);
	}
	
	/**
	 * Ambiguous syntax:
	 *     ('ini' | 'initially')?
	 *
	 * This ambiguous syntax occurs at:
	 *     initial+=TemporalInExpr TOK_SEMI (ambiguity) initial+=TemporalInExpr
	 *     name=ID '{' (ambiguity) initial+=TemporalInExpr
	 *     safety+=TemporalInExpr TOK_SEMI (ambiguity) initial+=TemporalInExpr
	 *     stateInv+=TemporalInExpr TOK_SEMI (ambiguity) initial+=TemporalInExpr
	 */
	protected void emit_Monitor___IniKeyword_4_0_0_0_0_or_InitiallyKeyword_4_0_0_0_1__q(EObject semanticObject, ISynNavigable transition, List<INode> nodes) {
		acceptNodes(transition, nodes);
	}
	
	/**
	 * Ambiguous syntax:
	 *     'GF' | 'alwEv' | 'alwaysEventually'
	 *
	 * This ambiguous syntax occurs at:
	 *     initial+=TemporalInExpr TOK_SEMI (ambiguity) justice+=TemporalInExpr
	 *     justice+=TemporalInExpr TOK_SEMI (ambiguity) justice+=TemporalInExpr
	 *     name=ID '{' (ambiguity) justice+=TemporalInExpr
	 *     params=PatternParamList ')' '{' (ambiguity) justice+=TemporalInExpr
	 *     safety+=TemporalInExpr TOK_SEMI (ambiguity) justice+=TemporalInExpr
	 *     stateInv+=TemporalInExpr TOK_SEMI (ambiguity) justice+=TemporalInExpr
	 *     varDeclList+=VarDecl (ambiguity) justice+=TemporalInExpr
	 */
	protected void emit_Pattern_AlwEvKeyword_3_2_0_3_0_1_or_AlwaysEventuallyKeyword_3_2_0_3_0_2_or_GFKeyword_3_2_0_3_0_0(EObject semanticObject, ISynNavigable transition, List<INode> nodes) {
		acceptNodes(transition, nodes);
	}
	
	/**
	 * Ambiguous syntax:
	 *     'always' | 'alw'
	 *
	 * This ambiguous syntax occurs at:
	 *     initial+=TemporalInExpr TOK_SEMI (ambiguity) stateInv+=TemporalInExpr
	 *     justice+=TemporalInExpr TOK_SEMI (ambiguity) stateInv+=TemporalInExpr
	 *     name=ID '{' (ambiguity) stateInv+=TemporalInExpr
	 *     params=PatternParamList ')' '{' (ambiguity) stateInv+=TemporalInExpr
	 *     safety+=TemporalInExpr TOK_SEMI (ambiguity) stateInv+=TemporalInExpr
	 *     stateInv+=TemporalInExpr TOK_SEMI (ambiguity) stateInv+=TemporalInExpr
	 *     varDeclList+=VarDecl (ambiguity) stateInv+=TemporalInExpr
	 */
	protected void emit_Pattern_AlwKeyword_3_2_0_2_0_1_or_AlwaysKeyword_3_2_0_2_0_0(EObject semanticObject, ISynNavigable transition, List<INode> nodes) {
		acceptNodes(transition, nodes);
	}
	
	/**
	 * Ambiguous syntax:
	 *     'G' | 'trans'
	 *
	 * This ambiguous syntax occurs at:
	 *     initial+=TemporalInExpr TOK_SEMI (ambiguity) safety+=TemporalInExpr
	 *     justice+=TemporalInExpr TOK_SEMI (ambiguity) safety+=TemporalInExpr
	 *     name=ID '{' (ambiguity) safety+=TemporalInExpr
	 *     params=PatternParamList ')' '{' (ambiguity) safety+=TemporalInExpr
	 *     safety+=TemporalInExpr TOK_SEMI (ambiguity) safety+=TemporalInExpr
	 *     stateInv+=TemporalInExpr TOK_SEMI (ambiguity) safety+=TemporalInExpr
	 *     varDeclList+=VarDecl (ambiguity) safety+=TemporalInExpr
	 */
	protected void emit_Pattern_GKeyword_3_2_0_1_0_0_or_TransKeyword_3_2_0_1_0_1(EObject semanticObject, ISynNavigable transition, List<INode> nodes) {
		acceptNodes(transition, nodes);
	}
	
	/**
	 * Ambiguous syntax:
	 *     ('ini' | 'initially')?
	 *
	 * This ambiguous syntax occurs at:
	 *     initial+=TemporalInExpr TOK_SEMI (ambiguity) initial+=TemporalInExpr
	 *     justice+=TemporalInExpr TOK_SEMI (ambiguity) initial+=TemporalInExpr
	 *     name=ID '{' (ambiguity) initial+=TemporalInExpr
	 *     params=PatternParamList ')' '{' (ambiguity) initial+=TemporalInExpr
	 *     safety+=TemporalInExpr TOK_SEMI (ambiguity) initial+=TemporalInExpr
	 *     stateInv+=TemporalInExpr TOK_SEMI (ambiguity) initial+=TemporalInExpr
	 *     varDeclList+=VarDecl (ambiguity) initial+=TemporalInExpr
	 */
	protected void emit_Pattern___IniKeyword_3_2_0_0_0_0_or_InitiallyKeyword_3_2_0_0_0_1__q(EObject semanticObject, ISynNavigable transition, List<INode> nodes) {
		acceptNodes(transition, nodes);
	}
	
	/**
	 * Ambiguous syntax:
	 *     '('*
	 *
	 * This ambiguous syntax occurs at:
	 *     (rule start) (ambiguity) assrt=BooleanTerm
	 *     (rule start) (ambiguity) comp='~'
	 *     (rule start) (ambiguity) empty?='()'
	 *     (rule start) (ambiguity) val='FALSE'
	 *     (rule start) (ambiguity) val='TRUE'
	 *     (rule start) (ambiguity) val='false'
	 *     (rule start) (ambiguity) val='true'
	 *     (rule start) (ambiguity) {BinaryRegExp.left=}
	 *     (rule start) (ambiguity) {UnaryRegExp.left=}
	 */
	protected void emit_PrimaryRegExp_LeftParenthesisKeyword_0_0_a(EObject semanticObject, ISynNavigable transition, List<INode> nodes) {
		acceptNodes(transition, nodes);
	}
	
	/**
	 * Ambiguous syntax:
	 *     '('+
	 *
	 * This ambiguous syntax occurs at:
	 *     (rule start) (ambiguity) comp='~'
	 *     (rule start) (ambiguity) {BinaryRegExp.left=}
	 *     (rule start) (ambiguity) {UnaryRegExp.left=}
	 */
	protected void emit_PrimaryRegExp_LeftParenthesisKeyword_0_0_p(EObject semanticObject, ISynNavigable transition, List<INode> nodes) {
		acceptNodes(transition, nodes);
	}
	
	/**
	 * Ambiguous syntax:
	 *     '('*
	 *
	 * This ambiguous syntax occurs at:
	 *     (rule start) (ambiguity) booleanValue='FALSE'
	 *     (rule start) (ambiguity) booleanValue='TRUE'
	 *     (rule start) (ambiguity) booleanValue='false'
	 *     (rule start) (ambiguity) booleanValue='true'
	 *     (rule start) (ambiguity) integerValue=INT
	 *     (rule start) (ambiguity) kinds='H'
	 *     (rule start) (ambiguity) kinds='HISTORICALLY'
	 *     (rule start) (ambiguity) kinds='O'
	 *     (rule start) (ambiguity) kinds='ONCE'
	 *     (rule start) (ambiguity) kinds='PREV'
	 *     (rule start) (ambiguity) kinds='Y'
	 *     (rule start) (ambiguity) operator='!'
	 *     (rule start) (ambiguity) operator='-'
	 *     (rule start) (ambiguity) operator='exists'
	 *     (rule start) (ambiguity) operator='forall'
	 *     (rule start) (ambiguity) operator='next'
	 *     (rule start) (ambiguity) operator='regexp'
	 *     (rule start) (ambiguity) pointer=[Referrable|ID]
	 *     (rule start) (ambiguity) predPatt=[PredicateOrPatternReferrable|ID]
	 *     (rule start) (ambiguity) {TemporalAdditiveExpr.elements+=}
	 *     (rule start) (ambiguity) {TemporalAndExpr.elements+=}
	 *     (rule start) (ambiguity) {TemporalBinaryExpr.elements+=}
	 *     (rule start) (ambiguity) {TemporalIffExpr.elements+=}
	 *     (rule start) (ambiguity) {TemporalImpExpr.left=}
	 *     (rule start) (ambiguity) {TemporalInExpr.left=}
	 *     (rule start) (ambiguity) {TemporalMultiplicativeExpr.elements+=}
	 *     (rule start) (ambiguity) {TemporalOrExpr.elements+=}
	 *     (rule start) (ambiguity) {TemporalRelationalExpr.left=}
	 *     (rule start) (ambiguity) {TemporalRemainderExpr.left=}
	 */
	protected void emit_TemporalPrimaryExpr_LeftParenthesisKeyword_1_0_a(EObject semanticObject, ISynNavigable transition, List<INode> nodes) {
		acceptNodes(transition, nodes);
	}
	
	/**
	 * Ambiguous syntax:
	 *     '('+
	 *
	 * This ambiguous syntax occurs at:
	 *     (rule start) (ambiguity) kinds='H'
	 *     (rule start) (ambiguity) kinds='HISTORICALLY'
	 *     (rule start) (ambiguity) kinds='O'
	 *     (rule start) (ambiguity) kinds='ONCE'
	 *     (rule start) (ambiguity) kinds='PREV'
	 *     (rule start) (ambiguity) kinds='Y'
	 *     (rule start) (ambiguity) operator='exists'
	 *     (rule start) (ambiguity) operator='forall'
	 *     (rule start) (ambiguity) {TemporalAdditiveExpr.elements+=}
	 *     (rule start) (ambiguity) {TemporalAndExpr.elements+=}
	 *     (rule start) (ambiguity) {TemporalBinaryExpr.elements+=}
	 *     (rule start) (ambiguity) {TemporalIffExpr.elements+=}
	 *     (rule start) (ambiguity) {TemporalImpExpr.left=}
	 *     (rule start) (ambiguity) {TemporalInExpr.left=}
	 *     (rule start) (ambiguity) {TemporalMultiplicativeExpr.elements+=}
	 *     (rule start) (ambiguity) {TemporalOrExpr.elements+=}
	 *     (rule start) (ambiguity) {TemporalRelationalExpr.left=}
	 *     (rule start) (ambiguity) {TemporalRemainderExpr.left=}
	 */
	protected void emit_TemporalPrimaryExpr_LeftParenthesisKeyword_1_0_p(EObject semanticObject, ISynNavigable transition, List<INode> nodes) {
		acceptNodes(transition, nodes);
	}
	
}
