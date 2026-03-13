package tau.smlab.syntech.parser.antlr.internal;

import org.eclipse.xtext.*;
import org.eclipse.xtext.parser.*;
import org.eclipse.xtext.parser.impl.*;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.xtext.parser.antlr.AbstractInternalAntlrParser;
import org.eclipse.xtext.parser.antlr.XtextTokenStream;
import org.eclipse.xtext.parser.antlr.XtextTokenStream.HiddenTokens;
import org.eclipse.xtext.parser.antlr.AntlrDatatypeRuleToken;
import tau.smlab.syntech.services.SpectraGrammarAccess;



import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class InternalSpectraParser extends AbstractInternalAntlrParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "RULE_ID", "RULE_STRING", "RULE_INT", "RULE_SL_COMMENT", "RULE_ML_COMMENT", "RULE_WS", "RULE_ANY_OTHER", "'module'", "'spec'", "'import'", "'weight'", "':'", "'-'", "'type'", "'='", "'define'", "':='", "'['", "']'", "'{'", "','", "'}'", "'regexp'", "'counter'", "'('", "')'", "'reset:'", "'inc:'", "'dec:'", "'overflow:'", "'underflow:'", "'monitor'", "'ini'", "'initially'", "'G'", "'trans'", "'always'", "'alw'", "'pattern'", "'var'", "'GF'", "'alwEv'", "'alwaysEventually'", "'predicate'", "'()'", "'boolean'", "'Int'", "'guarantee'", "'gar'", "'assumption'", "'asm'", "'GE'", "'GEF'", "'not'", "'in'", "'->'", "'implies'", "'<->'", "'iff'", "'|'", "'or'", "'xor'", "'&'", "'and'", "'!='", "'<'", "'>'", "'<='", "'>='", "'mod'", "'%'", "'+'", "'*'", "'/'", "'S'", "'SINCE'", "'T'", "'TRIGGERED'", "'Y'", "'PREV'", "'H'", "'HISTORICALLY'", "'O'", "'ONCE'", "'!'", "'next'", "'.all'", "'.any'", "'.prod'", "'.sum'", "'.min'", "'.max'", "'..'", "'FALSE'", "'false'", "'TRUE'", "'true'", "'trig'", "'|=>'", "'?'", "'~'", "'forall'", "'exists'", "'.'", "';'", "'output'", "'out'", "'sysvar'", "'sys'", "'input'", "'envvar'", "'env'", "'auxvar'", "'aux'", "'keep'", "'modulo'"
    };
    public static final int T__50=50;
    public static final int T__59=59;
    public static final int T__55=55;
    public static final int T__56=56;
    public static final int T__57=57;
    public static final int T__58=58;
    public static final int T__51=51;
    public static final int T__52=52;
    public static final int T__53=53;
    public static final int T__54=54;
    public static final int T__60=60;
    public static final int T__61=61;
    public static final int RULE_ID=4;
    public static final int RULE_INT=6;
    public static final int T__66=66;
    public static final int RULE_ML_COMMENT=8;
    public static final int T__67=67;
    public static final int T__68=68;
    public static final int T__69=69;
    public static final int T__62=62;
    public static final int T__63=63;
    public static final int T__64=64;
    public static final int T__65=65;
    public static final int T__37=37;
    public static final int T__38=38;
    public static final int T__39=39;
    public static final int T__33=33;
    public static final int T__34=34;
    public static final int T__35=35;
    public static final int T__36=36;
    public static final int T__30=30;
    public static final int T__31=31;
    public static final int T__32=32;
    public static final int T__48=48;
    public static final int T__49=49;
    public static final int T__44=44;
    public static final int T__45=45;
    public static final int T__46=46;
    public static final int T__47=47;
    public static final int T__40=40;
    public static final int T__41=41;
    public static final int T__42=42;
    public static final int T__43=43;
    public static final int T__91=91;
    public static final int T__100=100;
    public static final int T__92=92;
    public static final int T__93=93;
    public static final int T__102=102;
    public static final int T__94=94;
    public static final int T__101=101;
    public static final int T__90=90;
    public static final int T__19=19;
    public static final int T__15=15;
    public static final int T__16=16;
    public static final int T__17=17;
    public static final int T__18=18;
    public static final int T__11=11;
    public static final int T__99=99;
    public static final int T__12=12;
    public static final int T__13=13;
    public static final int T__14=14;
    public static final int T__95=95;
    public static final int T__96=96;
    public static final int T__97=97;
    public static final int T__98=98;
    public static final int T__26=26;
    public static final int T__27=27;
    public static final int T__28=28;
    public static final int T__29=29;
    public static final int T__22=22;
    public static final int T__23=23;
    public static final int T__24=24;
    public static final int T__25=25;
    public static final int T__20=20;
    public static final int T__21=21;
    public static final int T__70=70;
    public static final int T__71=71;
    public static final int T__72=72;
    public static final int RULE_STRING=5;
    public static final int RULE_SL_COMMENT=7;
    public static final int T__77=77;
    public static final int T__119=119;
    public static final int T__78=78;
    public static final int T__118=118;
    public static final int T__79=79;
    public static final int T__73=73;
    public static final int T__115=115;
    public static final int EOF=-1;
    public static final int T__74=74;
    public static final int T__114=114;
    public static final int T__75=75;
    public static final int T__117=117;
    public static final int T__76=76;
    public static final int T__116=116;
    public static final int T__80=80;
    public static final int T__111=111;
    public static final int T__81=81;
    public static final int T__110=110;
    public static final int T__82=82;
    public static final int T__113=113;
    public static final int T__83=83;
    public static final int T__112=112;
    public static final int RULE_WS=9;
    public static final int RULE_ANY_OTHER=10;
    public static final int T__88=88;
    public static final int T__108=108;
    public static final int T__89=89;
    public static final int T__107=107;
    public static final int T__109=109;
    public static final int T__84=84;
    public static final int T__104=104;
    public static final int T__85=85;
    public static final int T__103=103;
    public static final int T__86=86;
    public static final int T__106=106;
    public static final int T__87=87;
    public static final int T__105=105;

    // delegates
    // delegators


        public InternalSpectraParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public InternalSpectraParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return InternalSpectraParser.tokenNames; }
    public String getGrammarFileName() { return "InternalSpectra.g"; }



     	private SpectraGrammarAccess grammarAccess;

        public InternalSpectraParser(TokenStream input, SpectraGrammarAccess grammarAccess) {
            this(input);
            this.grammarAccess = grammarAccess;
            registerRules(grammarAccess.getGrammar());
        }

        @Override
        protected String getFirstRuleName() {
        	return "Model";
       	}

       	@Override
       	protected SpectraGrammarAccess getGrammarAccess() {
       		return grammarAccess;
       	}




    // $ANTLR start "entryRuleModel"
    // InternalSpectra.g:65:1: entryRuleModel returns [EObject current=null] : iv_ruleModel= ruleModel EOF ;
    public final EObject entryRuleModel() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleModel = null;


        try {
            // InternalSpectra.g:65:46: (iv_ruleModel= ruleModel EOF )
            // InternalSpectra.g:66:2: iv_ruleModel= ruleModel EOF
            {
             newCompositeNode(grammarAccess.getModelRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleModel=ruleModel();

            state._fsp--;

             current =iv_ruleModel; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleModel"


    // $ANTLR start "ruleModel"
    // InternalSpectra.g:72:1: ruleModel returns [EObject current=null] : ( ( (lv_imports_0_0= ruleImport ) )* (otherlv_1= 'module' | otherlv_2= 'spec' ) ( (lv_name_3_0= RULE_ID ) ) ( (lv_elements_4_0= ruleDecl ) )+ ) ;
    public final EObject ruleModel() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_2=null;
        Token lv_name_3_0=null;
        EObject lv_imports_0_0 = null;

        EObject lv_elements_4_0 = null;



        	enterRule();

        try {
            // InternalSpectra.g:78:2: ( ( ( (lv_imports_0_0= ruleImport ) )* (otherlv_1= 'module' | otherlv_2= 'spec' ) ( (lv_name_3_0= RULE_ID ) ) ( (lv_elements_4_0= ruleDecl ) )+ ) )
            // InternalSpectra.g:79:2: ( ( (lv_imports_0_0= ruleImport ) )* (otherlv_1= 'module' | otherlv_2= 'spec' ) ( (lv_name_3_0= RULE_ID ) ) ( (lv_elements_4_0= ruleDecl ) )+ )
            {
            // InternalSpectra.g:79:2: ( ( (lv_imports_0_0= ruleImport ) )* (otherlv_1= 'module' | otherlv_2= 'spec' ) ( (lv_name_3_0= RULE_ID ) ) ( (lv_elements_4_0= ruleDecl ) )+ )
            // InternalSpectra.g:80:3: ( (lv_imports_0_0= ruleImport ) )* (otherlv_1= 'module' | otherlv_2= 'spec' ) ( (lv_name_3_0= RULE_ID ) ) ( (lv_elements_4_0= ruleDecl ) )+
            {
            // InternalSpectra.g:80:3: ( (lv_imports_0_0= ruleImport ) )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==13) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // InternalSpectra.g:81:4: (lv_imports_0_0= ruleImport )
            	    {
            	    // InternalSpectra.g:81:4: (lv_imports_0_0= ruleImport )
            	    // InternalSpectra.g:82:5: lv_imports_0_0= ruleImport
            	    {

            	    					newCompositeNode(grammarAccess.getModelAccess().getImportsImportParserRuleCall_0_0());
            	    				
            	    pushFollow(FOLLOW_3);
            	    lv_imports_0_0=ruleImport();

            	    state._fsp--;


            	    					if (current==null) {
            	    						current = createModelElementForParent(grammarAccess.getModelRule());
            	    					}
            	    					add(
            	    						current,
            	    						"imports",
            	    						lv_imports_0_0,
            	    						"tau.smlab.syntech.Spectra.Import");
            	    					afterParserOrEnumRuleCall();
            	    				

            	    }


            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);

            // InternalSpectra.g:99:3: (otherlv_1= 'module' | otherlv_2= 'spec' )
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==11) ) {
                alt2=1;
            }
            else if ( (LA2_0==12) ) {
                alt2=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // InternalSpectra.g:100:4: otherlv_1= 'module'
                    {
                    otherlv_1=(Token)match(input,11,FOLLOW_4); 

                    				newLeafNode(otherlv_1, grammarAccess.getModelAccess().getModuleKeyword_1_0());
                    			

                    }
                    break;
                case 2 :
                    // InternalSpectra.g:105:4: otherlv_2= 'spec'
                    {
                    otherlv_2=(Token)match(input,12,FOLLOW_4); 

                    				newLeafNode(otherlv_2, grammarAccess.getModelAccess().getSpecKeyword_1_1());
                    			

                    }
                    break;

            }

            // InternalSpectra.g:110:3: ( (lv_name_3_0= RULE_ID ) )
            // InternalSpectra.g:111:4: (lv_name_3_0= RULE_ID )
            {
            // InternalSpectra.g:111:4: (lv_name_3_0= RULE_ID )
            // InternalSpectra.g:112:5: lv_name_3_0= RULE_ID
            {
            lv_name_3_0=(Token)match(input,RULE_ID,FOLLOW_5); 

            					newLeafNode(lv_name_3_0, grammarAccess.getModelAccess().getNameIDTerminalRuleCall_2_0());
            				

            					if (current==null) {
            						current = createModelElement(grammarAccess.getModelRule());
            					}
            					setWithLastConsumed(
            						current,
            						"name",
            						lv_name_3_0,
            						"org.eclipse.xtext.common.Terminals.ID");
            				

            }


            }

            // InternalSpectra.g:128:3: ( (lv_elements_4_0= ruleDecl ) )+
            int cnt3=0;
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==14||LA3_0==17||LA3_0==19||(LA3_0>=26 && LA3_0<=27)||LA3_0==35||LA3_0==42||LA3_0==47||(LA3_0>=51 && LA3_0<=54)||LA3_0==58||(LA3_0>=109 && LA3_0<=117)) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // InternalSpectra.g:129:4: (lv_elements_4_0= ruleDecl )
            	    {
            	    // InternalSpectra.g:129:4: (lv_elements_4_0= ruleDecl )
            	    // InternalSpectra.g:130:5: lv_elements_4_0= ruleDecl
            	    {

            	    					newCompositeNode(grammarAccess.getModelAccess().getElementsDeclParserRuleCall_3_0());
            	    				
            	    pushFollow(FOLLOW_6);
            	    lv_elements_4_0=ruleDecl();

            	    state._fsp--;


            	    					if (current==null) {
            	    						current = createModelElementForParent(grammarAccess.getModelRule());
            	    					}
            	    					add(
            	    						current,
            	    						"elements",
            	    						lv_elements_4_0,
            	    						"tau.smlab.syntech.Spectra.Decl");
            	    					afterParserOrEnumRuleCall();
            	    				

            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt3 >= 1 ) break loop3;
                        EarlyExitException eee =
                            new EarlyExitException(3, input);
                        throw eee;
                }
                cnt3++;
            } while (true);


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleModel"


    // $ANTLR start "entryRuleImport"
    // InternalSpectra.g:151:1: entryRuleImport returns [EObject current=null] : iv_ruleImport= ruleImport EOF ;
    public final EObject entryRuleImport() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleImport = null;


        try {
            // InternalSpectra.g:151:47: (iv_ruleImport= ruleImport EOF )
            // InternalSpectra.g:152:2: iv_ruleImport= ruleImport EOF
            {
             newCompositeNode(grammarAccess.getImportRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleImport=ruleImport();

            state._fsp--;

             current =iv_ruleImport; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleImport"


    // $ANTLR start "ruleImport"
    // InternalSpectra.g:158:1: ruleImport returns [EObject current=null] : (otherlv_0= 'import' ( (lv_importURI_1_0= RULE_STRING ) ) ) ;
    public final EObject ruleImport() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_importURI_1_0=null;


        	enterRule();

        try {
            // InternalSpectra.g:164:2: ( (otherlv_0= 'import' ( (lv_importURI_1_0= RULE_STRING ) ) ) )
            // InternalSpectra.g:165:2: (otherlv_0= 'import' ( (lv_importURI_1_0= RULE_STRING ) ) )
            {
            // InternalSpectra.g:165:2: (otherlv_0= 'import' ( (lv_importURI_1_0= RULE_STRING ) ) )
            // InternalSpectra.g:166:3: otherlv_0= 'import' ( (lv_importURI_1_0= RULE_STRING ) )
            {
            otherlv_0=(Token)match(input,13,FOLLOW_7); 

            			newLeafNode(otherlv_0, grammarAccess.getImportAccess().getImportKeyword_0());
            		
            // InternalSpectra.g:170:3: ( (lv_importURI_1_0= RULE_STRING ) )
            // InternalSpectra.g:171:4: (lv_importURI_1_0= RULE_STRING )
            {
            // InternalSpectra.g:171:4: (lv_importURI_1_0= RULE_STRING )
            // InternalSpectra.g:172:5: lv_importURI_1_0= RULE_STRING
            {
            lv_importURI_1_0=(Token)match(input,RULE_STRING,FOLLOW_2); 

            					newLeafNode(lv_importURI_1_0, grammarAccess.getImportAccess().getImportURISTRINGTerminalRuleCall_1_0());
            				

            					if (current==null) {
            						current = createModelElement(grammarAccess.getImportRule());
            					}
            					setWithLastConsumed(
            						current,
            						"importURI",
            						lv_importURI_1_0,
            						"org.eclipse.xtext.common.Terminals.STRING");
            				

            }


            }


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleImport"


    // $ANTLR start "entryRuleDecl"
    // InternalSpectra.g:192:1: entryRuleDecl returns [EObject current=null] : iv_ruleDecl= ruleDecl EOF ;
    public final EObject entryRuleDecl() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDecl = null;


        try {
            // InternalSpectra.g:192:45: (iv_ruleDecl= ruleDecl EOF )
            // InternalSpectra.g:193:2: iv_ruleDecl= ruleDecl EOF
            {
             newCompositeNode(grammarAccess.getDeclRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleDecl=ruleDecl();

            state._fsp--;

             current =iv_ruleDecl; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleDecl"


    // $ANTLR start "ruleDecl"
    // InternalSpectra.g:199:1: ruleDecl returns [EObject current=null] : (this_Var_0= ruleVar | this_TypeDef_1= ruleTypeDef | this_Define_2= ruleDefine | this_Predicate_3= rulePredicate | this_Pattern_4= rulePattern | this_Monitor_5= ruleMonitor | this_WeightDef_6= ruleWeightDef | this_LTLGar_7= ruleLTLGar | this_LTLAsm_8= ruleLTLAsm | this_EXGar_9= ruleEXGar | this_Counter_10= ruleCounter | this_DefineRegExp_11= ruleDefineRegExp ) ;
    public final EObject ruleDecl() throws RecognitionException {
        EObject current = null;

        EObject this_Var_0 = null;

        EObject this_TypeDef_1 = null;

        EObject this_Define_2 = null;

        EObject this_Predicate_3 = null;

        EObject this_Pattern_4 = null;

        EObject this_Monitor_5 = null;

        EObject this_WeightDef_6 = null;

        EObject this_LTLGar_7 = null;

        EObject this_LTLAsm_8 = null;

        EObject this_EXGar_9 = null;

        EObject this_Counter_10 = null;

        EObject this_DefineRegExp_11 = null;



        	enterRule();

        try {
            // InternalSpectra.g:205:2: ( (this_Var_0= ruleVar | this_TypeDef_1= ruleTypeDef | this_Define_2= ruleDefine | this_Predicate_3= rulePredicate | this_Pattern_4= rulePattern | this_Monitor_5= ruleMonitor | this_WeightDef_6= ruleWeightDef | this_LTLGar_7= ruleLTLGar | this_LTLAsm_8= ruleLTLAsm | this_EXGar_9= ruleEXGar | this_Counter_10= ruleCounter | this_DefineRegExp_11= ruleDefineRegExp ) )
            // InternalSpectra.g:206:2: (this_Var_0= ruleVar | this_TypeDef_1= ruleTypeDef | this_Define_2= ruleDefine | this_Predicate_3= rulePredicate | this_Pattern_4= rulePattern | this_Monitor_5= ruleMonitor | this_WeightDef_6= ruleWeightDef | this_LTLGar_7= ruleLTLGar | this_LTLAsm_8= ruleLTLAsm | this_EXGar_9= ruleEXGar | this_Counter_10= ruleCounter | this_DefineRegExp_11= ruleDefineRegExp )
            {
            // InternalSpectra.g:206:2: (this_Var_0= ruleVar | this_TypeDef_1= ruleTypeDef | this_Define_2= ruleDefine | this_Predicate_3= rulePredicate | this_Pattern_4= rulePattern | this_Monitor_5= ruleMonitor | this_WeightDef_6= ruleWeightDef | this_LTLGar_7= ruleLTLGar | this_LTLAsm_8= ruleLTLAsm | this_EXGar_9= ruleEXGar | this_Counter_10= ruleCounter | this_DefineRegExp_11= ruleDefineRegExp )
            int alt4=12;
            alt4 = dfa4.predict(input);
            switch (alt4) {
                case 1 :
                    // InternalSpectra.g:207:3: this_Var_0= ruleVar
                    {

                    			newCompositeNode(grammarAccess.getDeclAccess().getVarParserRuleCall_0());
                    		
                    pushFollow(FOLLOW_2);
                    this_Var_0=ruleVar();

                    state._fsp--;


                    			current = this_Var_0;
                    			afterParserOrEnumRuleCall();
                    		

                    }
                    break;
                case 2 :
                    // InternalSpectra.g:216:3: this_TypeDef_1= ruleTypeDef
                    {

                    			newCompositeNode(grammarAccess.getDeclAccess().getTypeDefParserRuleCall_1());
                    		
                    pushFollow(FOLLOW_2);
                    this_TypeDef_1=ruleTypeDef();

                    state._fsp--;


                    			current = this_TypeDef_1;
                    			afterParserOrEnumRuleCall();
                    		

                    }
                    break;
                case 3 :
                    // InternalSpectra.g:225:3: this_Define_2= ruleDefine
                    {

                    			newCompositeNode(grammarAccess.getDeclAccess().getDefineParserRuleCall_2());
                    		
                    pushFollow(FOLLOW_2);
                    this_Define_2=ruleDefine();

                    state._fsp--;


                    			current = this_Define_2;
                    			afterParserOrEnumRuleCall();
                    		

                    }
                    break;
                case 4 :
                    // InternalSpectra.g:234:3: this_Predicate_3= rulePredicate
                    {

                    			newCompositeNode(grammarAccess.getDeclAccess().getPredicateParserRuleCall_3());
                    		
                    pushFollow(FOLLOW_2);
                    this_Predicate_3=rulePredicate();

                    state._fsp--;


                    			current = this_Predicate_3;
                    			afterParserOrEnumRuleCall();
                    		

                    }
                    break;
                case 5 :
                    // InternalSpectra.g:243:3: this_Pattern_4= rulePattern
                    {

                    			newCompositeNode(grammarAccess.getDeclAccess().getPatternParserRuleCall_4());
                    		
                    pushFollow(FOLLOW_2);
                    this_Pattern_4=rulePattern();

                    state._fsp--;


                    			current = this_Pattern_4;
                    			afterParserOrEnumRuleCall();
                    		

                    }
                    break;
                case 6 :
                    // InternalSpectra.g:252:3: this_Monitor_5= ruleMonitor
                    {

                    			newCompositeNode(grammarAccess.getDeclAccess().getMonitorParserRuleCall_5());
                    		
                    pushFollow(FOLLOW_2);
                    this_Monitor_5=ruleMonitor();

                    state._fsp--;


                    			current = this_Monitor_5;
                    			afterParserOrEnumRuleCall();
                    		

                    }
                    break;
                case 7 :
                    // InternalSpectra.g:261:3: this_WeightDef_6= ruleWeightDef
                    {

                    			newCompositeNode(grammarAccess.getDeclAccess().getWeightDefParserRuleCall_6());
                    		
                    pushFollow(FOLLOW_2);
                    this_WeightDef_6=ruleWeightDef();

                    state._fsp--;


                    			current = this_WeightDef_6;
                    			afterParserOrEnumRuleCall();
                    		

                    }
                    break;
                case 8 :
                    // InternalSpectra.g:270:3: this_LTLGar_7= ruleLTLGar
                    {

                    			newCompositeNode(grammarAccess.getDeclAccess().getLTLGarParserRuleCall_7());
                    		
                    pushFollow(FOLLOW_2);
                    this_LTLGar_7=ruleLTLGar();

                    state._fsp--;


                    			current = this_LTLGar_7;
                    			afterParserOrEnumRuleCall();
                    		

                    }
                    break;
                case 9 :
                    // InternalSpectra.g:279:3: this_LTLAsm_8= ruleLTLAsm
                    {

                    			newCompositeNode(grammarAccess.getDeclAccess().getLTLAsmParserRuleCall_8());
                    		
                    pushFollow(FOLLOW_2);
                    this_LTLAsm_8=ruleLTLAsm();

                    state._fsp--;


                    			current = this_LTLAsm_8;
                    			afterParserOrEnumRuleCall();
                    		

                    }
                    break;
                case 10 :
                    // InternalSpectra.g:288:3: this_EXGar_9= ruleEXGar
                    {

                    			newCompositeNode(grammarAccess.getDeclAccess().getEXGarParserRuleCall_9());
                    		
                    pushFollow(FOLLOW_2);
                    this_EXGar_9=ruleEXGar();

                    state._fsp--;


                    			current = this_EXGar_9;
                    			afterParserOrEnumRuleCall();
                    		

                    }
                    break;
                case 11 :
                    // InternalSpectra.g:297:3: this_Counter_10= ruleCounter
                    {

                    			newCompositeNode(grammarAccess.getDeclAccess().getCounterParserRuleCall_10());
                    		
                    pushFollow(FOLLOW_2);
                    this_Counter_10=ruleCounter();

                    state._fsp--;


                    			current = this_Counter_10;
                    			afterParserOrEnumRuleCall();
                    		

                    }
                    break;
                case 12 :
                    // InternalSpectra.g:306:3: this_DefineRegExp_11= ruleDefineRegExp
                    {

                    			newCompositeNode(grammarAccess.getDeclAccess().getDefineRegExpParserRuleCall_11());
                    		
                    pushFollow(FOLLOW_2);
                    this_DefineRegExp_11=ruleDefineRegExp();

                    state._fsp--;


                    			current = this_DefineRegExp_11;
                    			afterParserOrEnumRuleCall();
                    		

                    }
                    break;

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleDecl"


    // $ANTLR start "entryRuleWeightDef"
    // InternalSpectra.g:318:1: entryRuleWeightDef returns [EObject current=null] : iv_ruleWeightDef= ruleWeightDef EOF ;
    public final EObject entryRuleWeightDef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleWeightDef = null;


        try {
            // InternalSpectra.g:318:50: (iv_ruleWeightDef= ruleWeightDef EOF )
            // InternalSpectra.g:319:2: iv_ruleWeightDef= ruleWeightDef EOF
            {
             newCompositeNode(grammarAccess.getWeightDefRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleWeightDef=ruleWeightDef();

            state._fsp--;

             current =iv_ruleWeightDef; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleWeightDef"


    // $ANTLR start "ruleWeightDef"
    // InternalSpectra.g:325:1: ruleWeightDef returns [EObject current=null] : (otherlv_0= 'weight' ( ( (lv_name_1_0= RULE_ID ) ) otherlv_2= ':' )? ( (lv_negative_3_0= '-' ) )? ( (lv_value_4_0= RULE_INT ) ) ( (lv_definition_5_0= ruleTemporalInExpr ) ) ruleTOK_SEMI ) ;
    public final EObject ruleWeightDef() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_name_1_0=null;
        Token otherlv_2=null;
        Token lv_negative_3_0=null;
        Token lv_value_4_0=null;
        EObject lv_definition_5_0 = null;



        	enterRule();

        try {
            // InternalSpectra.g:331:2: ( (otherlv_0= 'weight' ( ( (lv_name_1_0= RULE_ID ) ) otherlv_2= ':' )? ( (lv_negative_3_0= '-' ) )? ( (lv_value_4_0= RULE_INT ) ) ( (lv_definition_5_0= ruleTemporalInExpr ) ) ruleTOK_SEMI ) )
            // InternalSpectra.g:332:2: (otherlv_0= 'weight' ( ( (lv_name_1_0= RULE_ID ) ) otherlv_2= ':' )? ( (lv_negative_3_0= '-' ) )? ( (lv_value_4_0= RULE_INT ) ) ( (lv_definition_5_0= ruleTemporalInExpr ) ) ruleTOK_SEMI )
            {
            // InternalSpectra.g:332:2: (otherlv_0= 'weight' ( ( (lv_name_1_0= RULE_ID ) ) otherlv_2= ':' )? ( (lv_negative_3_0= '-' ) )? ( (lv_value_4_0= RULE_INT ) ) ( (lv_definition_5_0= ruleTemporalInExpr ) ) ruleTOK_SEMI )
            // InternalSpectra.g:333:3: otherlv_0= 'weight' ( ( (lv_name_1_0= RULE_ID ) ) otherlv_2= ':' )? ( (lv_negative_3_0= '-' ) )? ( (lv_value_4_0= RULE_INT ) ) ( (lv_definition_5_0= ruleTemporalInExpr ) ) ruleTOK_SEMI
            {
            otherlv_0=(Token)match(input,14,FOLLOW_8); 

            			newLeafNode(otherlv_0, grammarAccess.getWeightDefAccess().getWeightKeyword_0());
            		
            // InternalSpectra.g:337:3: ( ( (lv_name_1_0= RULE_ID ) ) otherlv_2= ':' )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==RULE_ID) ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // InternalSpectra.g:338:4: ( (lv_name_1_0= RULE_ID ) ) otherlv_2= ':'
                    {
                    // InternalSpectra.g:338:4: ( (lv_name_1_0= RULE_ID ) )
                    // InternalSpectra.g:339:5: (lv_name_1_0= RULE_ID )
                    {
                    // InternalSpectra.g:339:5: (lv_name_1_0= RULE_ID )
                    // InternalSpectra.g:340:6: lv_name_1_0= RULE_ID
                    {
                    lv_name_1_0=(Token)match(input,RULE_ID,FOLLOW_9); 

                    						newLeafNode(lv_name_1_0, grammarAccess.getWeightDefAccess().getNameIDTerminalRuleCall_1_0_0());
                    					

                    						if (current==null) {
                    							current = createModelElement(grammarAccess.getWeightDefRule());
                    						}
                    						setWithLastConsumed(
                    							current,
                    							"name",
                    							lv_name_1_0,
                    							"org.eclipse.xtext.common.Terminals.ID");
                    					

                    }


                    }

                    otherlv_2=(Token)match(input,15,FOLLOW_10); 

                    				newLeafNode(otherlv_2, grammarAccess.getWeightDefAccess().getColonKeyword_1_1());
                    			

                    }
                    break;

            }

            // InternalSpectra.g:361:3: ( (lv_negative_3_0= '-' ) )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==16) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // InternalSpectra.g:362:4: (lv_negative_3_0= '-' )
                    {
                    // InternalSpectra.g:362:4: (lv_negative_3_0= '-' )
                    // InternalSpectra.g:363:5: lv_negative_3_0= '-'
                    {
                    lv_negative_3_0=(Token)match(input,16,FOLLOW_11); 

                    					newLeafNode(lv_negative_3_0, grammarAccess.getWeightDefAccess().getNegativeHyphenMinusKeyword_2_0());
                    				

                    					if (current==null) {
                    						current = createModelElement(grammarAccess.getWeightDefRule());
                    					}
                    					setWithLastConsumed(current, "negative", lv_negative_3_0, "-");
                    				

                    }


                    }
                    break;

            }

            // InternalSpectra.g:375:3: ( (lv_value_4_0= RULE_INT ) )
            // InternalSpectra.g:376:4: (lv_value_4_0= RULE_INT )
            {
            // InternalSpectra.g:376:4: (lv_value_4_0= RULE_INT )
            // InternalSpectra.g:377:5: lv_value_4_0= RULE_INT
            {
            lv_value_4_0=(Token)match(input,RULE_INT,FOLLOW_12); 

            					newLeafNode(lv_value_4_0, grammarAccess.getWeightDefAccess().getValueINTTerminalRuleCall_3_0());
            				

            					if (current==null) {
            						current = createModelElement(grammarAccess.getWeightDefRule());
            					}
            					setWithLastConsumed(
            						current,
            						"value",
            						lv_value_4_0,
            						"org.eclipse.xtext.common.Terminals.INT");
            				

            }


            }

            // InternalSpectra.g:393:3: ( (lv_definition_5_0= ruleTemporalInExpr ) )
            // InternalSpectra.g:394:4: (lv_definition_5_0= ruleTemporalInExpr )
            {
            // InternalSpectra.g:394:4: (lv_definition_5_0= ruleTemporalInExpr )
            // InternalSpectra.g:395:5: lv_definition_5_0= ruleTemporalInExpr
            {

            					newCompositeNode(grammarAccess.getWeightDefAccess().getDefinitionTemporalInExprParserRuleCall_4_0());
            				
            pushFollow(FOLLOW_13);
            lv_definition_5_0=ruleTemporalInExpr();

            state._fsp--;


            					if (current==null) {
            						current = createModelElementForParent(grammarAccess.getWeightDefRule());
            					}
            					set(
            						current,
            						"definition",
            						lv_definition_5_0,
            						"tau.smlab.syntech.Spectra.TemporalInExpr");
            					afterParserOrEnumRuleCall();
            				

            }


            }


            			newCompositeNode(grammarAccess.getWeightDefAccess().getTOK_SEMIParserRuleCall_5());
            		
            pushFollow(FOLLOW_2);
            ruleTOK_SEMI();

            state._fsp--;


            			afterParserOrEnumRuleCall();
            		

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleWeightDef"


    // $ANTLR start "entryRuleVar"
    // InternalSpectra.g:423:1: entryRuleVar returns [EObject current=null] : iv_ruleVar= ruleVar EOF ;
    public final EObject entryRuleVar() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleVar = null;


        try {
            // InternalSpectra.g:423:44: (iv_ruleVar= ruleVar EOF )
            // InternalSpectra.g:424:2: iv_ruleVar= ruleVar EOF
            {
             newCompositeNode(grammarAccess.getVarRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleVar=ruleVar();

            state._fsp--;

             current =iv_ruleVar; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleVar"


    // $ANTLR start "ruleVar"
    // InternalSpectra.g:430:1: ruleVar returns [EObject current=null] : ( ( (lv_kind_0_0= ruleVarOwner ) ) ( (lv_var_1_0= ruleVarDecl ) ) ) ;
    public final EObject ruleVar() throws RecognitionException {
        EObject current = null;

        Enumerator lv_kind_0_0 = null;

        EObject lv_var_1_0 = null;



        	enterRule();

        try {
            // InternalSpectra.g:436:2: ( ( ( (lv_kind_0_0= ruleVarOwner ) ) ( (lv_var_1_0= ruleVarDecl ) ) ) )
            // InternalSpectra.g:437:2: ( ( (lv_kind_0_0= ruleVarOwner ) ) ( (lv_var_1_0= ruleVarDecl ) ) )
            {
            // InternalSpectra.g:437:2: ( ( (lv_kind_0_0= ruleVarOwner ) ) ( (lv_var_1_0= ruleVarDecl ) ) )
            // InternalSpectra.g:438:3: ( (lv_kind_0_0= ruleVarOwner ) ) ( (lv_var_1_0= ruleVarDecl ) )
            {
            // InternalSpectra.g:438:3: ( (lv_kind_0_0= ruleVarOwner ) )
            // InternalSpectra.g:439:4: (lv_kind_0_0= ruleVarOwner )
            {
            // InternalSpectra.g:439:4: (lv_kind_0_0= ruleVarOwner )
            // InternalSpectra.g:440:5: lv_kind_0_0= ruleVarOwner
            {

            					newCompositeNode(grammarAccess.getVarAccess().getKindVarOwnerEnumRuleCall_0_0());
            				
            pushFollow(FOLLOW_14);
            lv_kind_0_0=ruleVarOwner();

            state._fsp--;


            					if (current==null) {
            						current = createModelElementForParent(grammarAccess.getVarRule());
            					}
            					set(
            						current,
            						"kind",
            						lv_kind_0_0,
            						"tau.smlab.syntech.Spectra.VarOwner");
            					afterParserOrEnumRuleCall();
            				

            }


            }

            // InternalSpectra.g:457:3: ( (lv_var_1_0= ruleVarDecl ) )
            // InternalSpectra.g:458:4: (lv_var_1_0= ruleVarDecl )
            {
            // InternalSpectra.g:458:4: (lv_var_1_0= ruleVarDecl )
            // InternalSpectra.g:459:5: lv_var_1_0= ruleVarDecl
            {

            					newCompositeNode(grammarAccess.getVarAccess().getVarVarDeclParserRuleCall_1_0());
            				
            pushFollow(FOLLOW_2);
            lv_var_1_0=ruleVarDecl();

            state._fsp--;


            					if (current==null) {
            						current = createModelElementForParent(grammarAccess.getVarRule());
            					}
            					set(
            						current,
            						"var",
            						lv_var_1_0,
            						"tau.smlab.syntech.Spectra.VarDecl");
            					afterParserOrEnumRuleCall();
            				

            }


            }


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleVar"


    // $ANTLR start "entryRuleTypeDef"
    // InternalSpectra.g:480:1: entryRuleTypeDef returns [EObject current=null] : iv_ruleTypeDef= ruleTypeDef EOF ;
    public final EObject entryRuleTypeDef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleTypeDef = null;


        try {
            // InternalSpectra.g:480:48: (iv_ruleTypeDef= ruleTypeDef EOF )
            // InternalSpectra.g:481:2: iv_ruleTypeDef= ruleTypeDef EOF
            {
             newCompositeNode(grammarAccess.getTypeDefRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleTypeDef=ruleTypeDef();

            state._fsp--;

             current =iv_ruleTypeDef; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleTypeDef"


    // $ANTLR start "ruleTypeDef"
    // InternalSpectra.g:487:1: ruleTypeDef returns [EObject current=null] : (otherlv_0= 'type' ( (lv_name_1_0= RULE_ID ) ) otherlv_2= '=' ( (lv_type_3_0= ruleVarType ) ) ruleTOK_SEMI ) ;
    public final EObject ruleTypeDef() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_name_1_0=null;
        Token otherlv_2=null;
        EObject lv_type_3_0 = null;



        	enterRule();

        try {
            // InternalSpectra.g:493:2: ( (otherlv_0= 'type' ( (lv_name_1_0= RULE_ID ) ) otherlv_2= '=' ( (lv_type_3_0= ruleVarType ) ) ruleTOK_SEMI ) )
            // InternalSpectra.g:494:2: (otherlv_0= 'type' ( (lv_name_1_0= RULE_ID ) ) otherlv_2= '=' ( (lv_type_3_0= ruleVarType ) ) ruleTOK_SEMI )
            {
            // InternalSpectra.g:494:2: (otherlv_0= 'type' ( (lv_name_1_0= RULE_ID ) ) otherlv_2= '=' ( (lv_type_3_0= ruleVarType ) ) ruleTOK_SEMI )
            // InternalSpectra.g:495:3: otherlv_0= 'type' ( (lv_name_1_0= RULE_ID ) ) otherlv_2= '=' ( (lv_type_3_0= ruleVarType ) ) ruleTOK_SEMI
            {
            otherlv_0=(Token)match(input,17,FOLLOW_4); 

            			newLeafNode(otherlv_0, grammarAccess.getTypeDefAccess().getTypeKeyword_0());
            		
            // InternalSpectra.g:499:3: ( (lv_name_1_0= RULE_ID ) )
            // InternalSpectra.g:500:4: (lv_name_1_0= RULE_ID )
            {
            // InternalSpectra.g:500:4: (lv_name_1_0= RULE_ID )
            // InternalSpectra.g:501:5: lv_name_1_0= RULE_ID
            {
            lv_name_1_0=(Token)match(input,RULE_ID,FOLLOW_15); 

            					newLeafNode(lv_name_1_0, grammarAccess.getTypeDefAccess().getNameIDTerminalRuleCall_1_0());
            				

            					if (current==null) {
            						current = createModelElement(grammarAccess.getTypeDefRule());
            					}
            					setWithLastConsumed(
            						current,
            						"name",
            						lv_name_1_0,
            						"org.eclipse.xtext.common.Terminals.ID");
            				

            }


            }

            otherlv_2=(Token)match(input,18,FOLLOW_14); 

            			newLeafNode(otherlv_2, grammarAccess.getTypeDefAccess().getEqualsSignKeyword_2());
            		
            // InternalSpectra.g:521:3: ( (lv_type_3_0= ruleVarType ) )
            // InternalSpectra.g:522:4: (lv_type_3_0= ruleVarType )
            {
            // InternalSpectra.g:522:4: (lv_type_3_0= ruleVarType )
            // InternalSpectra.g:523:5: lv_type_3_0= ruleVarType
            {

            					newCompositeNode(grammarAccess.getTypeDefAccess().getTypeVarTypeParserRuleCall_3_0());
            				
            pushFollow(FOLLOW_13);
            lv_type_3_0=ruleVarType();

            state._fsp--;


            					if (current==null) {
            						current = createModelElementForParent(grammarAccess.getTypeDefRule());
            					}
            					set(
            						current,
            						"type",
            						lv_type_3_0,
            						"tau.smlab.syntech.Spectra.VarType");
            					afterParserOrEnumRuleCall();
            				

            }


            }


            			newCompositeNode(grammarAccess.getTypeDefAccess().getTOK_SEMIParserRuleCall_4());
            		
            pushFollow(FOLLOW_2);
            ruleTOK_SEMI();

            state._fsp--;


            			afterParserOrEnumRuleCall();
            		

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleTypeDef"


    // $ANTLR start "entryRuleVarDecl"
    // InternalSpectra.g:551:1: entryRuleVarDecl returns [EObject current=null] : iv_ruleVarDecl= ruleVarDecl EOF ;
    public final EObject entryRuleVarDecl() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleVarDecl = null;


        try {
            // InternalSpectra.g:551:48: (iv_ruleVarDecl= ruleVarDecl EOF )
            // InternalSpectra.g:552:2: iv_ruleVarDecl= ruleVarDecl EOF
            {
             newCompositeNode(grammarAccess.getVarDeclRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleVarDecl=ruleVarDecl();

            state._fsp--;

             current =iv_ruleVarDecl; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleVarDecl"


    // $ANTLR start "ruleVarDecl"
    // InternalSpectra.g:558:1: ruleVarDecl returns [EObject current=null] : ( ( (lv_type_0_0= ruleVarType ) ) ( (lv_name_1_0= RULE_ID ) ) ruleTOK_SEMI ) ;
    public final EObject ruleVarDecl() throws RecognitionException {
        EObject current = null;

        Token lv_name_1_0=null;
        EObject lv_type_0_0 = null;



        	enterRule();

        try {
            // InternalSpectra.g:564:2: ( ( ( (lv_type_0_0= ruleVarType ) ) ( (lv_name_1_0= RULE_ID ) ) ruleTOK_SEMI ) )
            // InternalSpectra.g:565:2: ( ( (lv_type_0_0= ruleVarType ) ) ( (lv_name_1_0= RULE_ID ) ) ruleTOK_SEMI )
            {
            // InternalSpectra.g:565:2: ( ( (lv_type_0_0= ruleVarType ) ) ( (lv_name_1_0= RULE_ID ) ) ruleTOK_SEMI )
            // InternalSpectra.g:566:3: ( (lv_type_0_0= ruleVarType ) ) ( (lv_name_1_0= RULE_ID ) ) ruleTOK_SEMI
            {
            // InternalSpectra.g:566:3: ( (lv_type_0_0= ruleVarType ) )
            // InternalSpectra.g:567:4: (lv_type_0_0= ruleVarType )
            {
            // InternalSpectra.g:567:4: (lv_type_0_0= ruleVarType )
            // InternalSpectra.g:568:5: lv_type_0_0= ruleVarType
            {

            					newCompositeNode(grammarAccess.getVarDeclAccess().getTypeVarTypeParserRuleCall_0_0());
            				
            pushFollow(FOLLOW_4);
            lv_type_0_0=ruleVarType();

            state._fsp--;


            					if (current==null) {
            						current = createModelElementForParent(grammarAccess.getVarDeclRule());
            					}
            					set(
            						current,
            						"type",
            						lv_type_0_0,
            						"tau.smlab.syntech.Spectra.VarType");
            					afterParserOrEnumRuleCall();
            				

            }


            }

            // InternalSpectra.g:585:3: ( (lv_name_1_0= RULE_ID ) )
            // InternalSpectra.g:586:4: (lv_name_1_0= RULE_ID )
            {
            // InternalSpectra.g:586:4: (lv_name_1_0= RULE_ID )
            // InternalSpectra.g:587:5: lv_name_1_0= RULE_ID
            {
            lv_name_1_0=(Token)match(input,RULE_ID,FOLLOW_13); 

            					newLeafNode(lv_name_1_0, grammarAccess.getVarDeclAccess().getNameIDTerminalRuleCall_1_0());
            				

            					if (current==null) {
            						current = createModelElement(grammarAccess.getVarDeclRule());
            					}
            					setWithLastConsumed(
            						current,
            						"name",
            						lv_name_1_0,
            						"org.eclipse.xtext.common.Terminals.ID");
            				

            }


            }


            			newCompositeNode(grammarAccess.getVarDeclAccess().getTOK_SEMIParserRuleCall_2());
            		
            pushFollow(FOLLOW_2);
            ruleTOK_SEMI();

            state._fsp--;


            			afterParserOrEnumRuleCall();
            		

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleVarDecl"


    // $ANTLR start "entryRuleTypeConstant"
    // InternalSpectra.g:614:1: entryRuleTypeConstant returns [EObject current=null] : iv_ruleTypeConstant= ruleTypeConstant EOF ;
    public final EObject entryRuleTypeConstant() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleTypeConstant = null;


        try {
            // InternalSpectra.g:614:53: (iv_ruleTypeConstant= ruleTypeConstant EOF )
            // InternalSpectra.g:615:2: iv_ruleTypeConstant= ruleTypeConstant EOF
            {
             newCompositeNode(grammarAccess.getTypeConstantRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleTypeConstant=ruleTypeConstant();

            state._fsp--;

             current =iv_ruleTypeConstant; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleTypeConstant"


    // $ANTLR start "ruleTypeConstant"
    // InternalSpectra.g:621:1: ruleTypeConstant returns [EObject current=null] : ( (lv_name_0_0= ruleTypeConstantLiteral ) ) ;
    public final EObject ruleTypeConstant() throws RecognitionException {
        EObject current = null;

        AntlrDatatypeRuleToken lv_name_0_0 = null;



        	enterRule();

        try {
            // InternalSpectra.g:627:2: ( ( (lv_name_0_0= ruleTypeConstantLiteral ) ) )
            // InternalSpectra.g:628:2: ( (lv_name_0_0= ruleTypeConstantLiteral ) )
            {
            // InternalSpectra.g:628:2: ( (lv_name_0_0= ruleTypeConstantLiteral ) )
            // InternalSpectra.g:629:3: (lv_name_0_0= ruleTypeConstantLiteral )
            {
            // InternalSpectra.g:629:3: (lv_name_0_0= ruleTypeConstantLiteral )
            // InternalSpectra.g:630:4: lv_name_0_0= ruleTypeConstantLiteral
            {

            				newCompositeNode(grammarAccess.getTypeConstantAccess().getNameTypeConstantLiteralParserRuleCall_0());
            			
            pushFollow(FOLLOW_2);
            lv_name_0_0=ruleTypeConstantLiteral();

            state._fsp--;


            				if (current==null) {
            					current = createModelElementForParent(grammarAccess.getTypeConstantRule());
            				}
            				set(
            					current,
            					"name",
            					lv_name_0_0,
            					"tau.smlab.syntech.Spectra.TypeConstantLiteral");
            				afterParserOrEnumRuleCall();
            			

            }


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleTypeConstant"


    // $ANTLR start "entryRuleTypeConstantLiteral"
    // InternalSpectra.g:650:1: entryRuleTypeConstantLiteral returns [String current=null] : iv_ruleTypeConstantLiteral= ruleTypeConstantLiteral EOF ;
    public final String entryRuleTypeConstantLiteral() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleTypeConstantLiteral = null;


        try {
            // InternalSpectra.g:650:59: (iv_ruleTypeConstantLiteral= ruleTypeConstantLiteral EOF )
            // InternalSpectra.g:651:2: iv_ruleTypeConstantLiteral= ruleTypeConstantLiteral EOF
            {
             newCompositeNode(grammarAccess.getTypeConstantLiteralRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleTypeConstantLiteral=ruleTypeConstantLiteral();

            state._fsp--;

             current =iv_ruleTypeConstantLiteral.getText(); 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleTypeConstantLiteral"


    // $ANTLR start "ruleTypeConstantLiteral"
    // InternalSpectra.g:657:1: ruleTypeConstantLiteral returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_ID_0= RULE_ID | this_INT_1= RULE_INT ) ;
    public final AntlrDatatypeRuleToken ruleTypeConstantLiteral() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_ID_0=null;
        Token this_INT_1=null;


        	enterRule();

        try {
            // InternalSpectra.g:663:2: ( (this_ID_0= RULE_ID | this_INT_1= RULE_INT ) )
            // InternalSpectra.g:664:2: (this_ID_0= RULE_ID | this_INT_1= RULE_INT )
            {
            // InternalSpectra.g:664:2: (this_ID_0= RULE_ID | this_INT_1= RULE_INT )
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==RULE_ID) ) {
                alt7=1;
            }
            else if ( (LA7_0==RULE_INT) ) {
                alt7=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 7, 0, input);

                throw nvae;
            }
            switch (alt7) {
                case 1 :
                    // InternalSpectra.g:665:3: this_ID_0= RULE_ID
                    {
                    this_ID_0=(Token)match(input,RULE_ID,FOLLOW_2); 

                    			current.merge(this_ID_0);
                    		

                    			newLeafNode(this_ID_0, grammarAccess.getTypeConstantLiteralAccess().getIDTerminalRuleCall_0());
                    		

                    }
                    break;
                case 2 :
                    // InternalSpectra.g:673:3: this_INT_1= RULE_INT
                    {
                    this_INT_1=(Token)match(input,RULE_INT,FOLLOW_2); 

                    			current.merge(this_INT_1);
                    		

                    			newLeafNode(this_INT_1, grammarAccess.getTypeConstantLiteralAccess().getINTTerminalRuleCall_1());
                    		

                    }
                    break;

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleTypeConstantLiteral"


    // $ANTLR start "entryRuleDefine"
    // InternalSpectra.g:684:1: entryRuleDefine returns [EObject current=null] : iv_ruleDefine= ruleDefine EOF ;
    public final EObject entryRuleDefine() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDefine = null;


        try {
            // InternalSpectra.g:684:47: (iv_ruleDefine= ruleDefine EOF )
            // InternalSpectra.g:685:2: iv_ruleDefine= ruleDefine EOF
            {
             newCompositeNode(grammarAccess.getDefineRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleDefine=ruleDefine();

            state._fsp--;

             current =iv_ruleDefine; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleDefine"


    // $ANTLR start "ruleDefine"
    // InternalSpectra.g:691:1: ruleDefine returns [EObject current=null] : (otherlv_0= 'define' ( (lv_defineList_1_0= ruleDefineDecl ) )+ ) ;
    public final EObject ruleDefine() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        EObject lv_defineList_1_0 = null;



        	enterRule();

        try {
            // InternalSpectra.g:697:2: ( (otherlv_0= 'define' ( (lv_defineList_1_0= ruleDefineDecl ) )+ ) )
            // InternalSpectra.g:698:2: (otherlv_0= 'define' ( (lv_defineList_1_0= ruleDefineDecl ) )+ )
            {
            // InternalSpectra.g:698:2: (otherlv_0= 'define' ( (lv_defineList_1_0= ruleDefineDecl ) )+ )
            // InternalSpectra.g:699:3: otherlv_0= 'define' ( (lv_defineList_1_0= ruleDefineDecl ) )+
            {
            otherlv_0=(Token)match(input,19,FOLLOW_4); 

            			newLeafNode(otherlv_0, grammarAccess.getDefineAccess().getDefineKeyword_0());
            		
            // InternalSpectra.g:703:3: ( (lv_defineList_1_0= ruleDefineDecl ) )+
            int cnt8=0;
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==RULE_ID) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // InternalSpectra.g:704:4: (lv_defineList_1_0= ruleDefineDecl )
            	    {
            	    // InternalSpectra.g:704:4: (lv_defineList_1_0= ruleDefineDecl )
            	    // InternalSpectra.g:705:5: lv_defineList_1_0= ruleDefineDecl
            	    {

            	    					newCompositeNode(grammarAccess.getDefineAccess().getDefineListDefineDeclParserRuleCall_1_0());
            	    				
            	    pushFollow(FOLLOW_16);
            	    lv_defineList_1_0=ruleDefineDecl();

            	    state._fsp--;


            	    					if (current==null) {
            	    						current = createModelElementForParent(grammarAccess.getDefineRule());
            	    					}
            	    					add(
            	    						current,
            	    						"defineList",
            	    						lv_defineList_1_0,
            	    						"tau.smlab.syntech.Spectra.DefineDecl");
            	    					afterParserOrEnumRuleCall();
            	    				

            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt8 >= 1 ) break loop8;
                        EarlyExitException eee =
                            new EarlyExitException(8, input);
                        throw eee;
                }
                cnt8++;
            } while (true);


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleDefine"


    // $ANTLR start "entryRuleDefineDecl"
    // InternalSpectra.g:726:1: entryRuleDefineDecl returns [EObject current=null] : iv_ruleDefineDecl= ruleDefineDecl EOF ;
    public final EObject entryRuleDefineDecl() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDefineDecl = null;


        try {
            // InternalSpectra.g:726:51: (iv_ruleDefineDecl= ruleDefineDecl EOF )
            // InternalSpectra.g:727:2: iv_ruleDefineDecl= ruleDefineDecl EOF
            {
             newCompositeNode(grammarAccess.getDefineDeclRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleDefineDecl=ruleDefineDecl();

            state._fsp--;

             current =iv_ruleDefineDecl; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleDefineDecl"


    // $ANTLR start "ruleDefineDecl"
    // InternalSpectra.g:733:1: ruleDefineDecl returns [EObject current=null] : ( ( ( ( (lv_name_0_0= RULE_ID ) ) otherlv_1= ':=' ( (lv_simpleExpr_2_0= ruleTemporalExpression ) ) ) | ( ( (lv_name_3_0= RULE_ID ) ) (otherlv_4= '[' ( (lv_dimensions_5_0= ruleSizeDefineDecl ) ) otherlv_6= ']' )+ otherlv_7= ':=' ( (lv_innerArray_8_0= ruleDefineArray ) ) ) ) ruleTOK_SEMI ) ;
    public final EObject ruleDefineDecl() throws RecognitionException {
        EObject current = null;

        Token lv_name_0_0=null;
        Token otherlv_1=null;
        Token lv_name_3_0=null;
        Token otherlv_4=null;
        Token otherlv_6=null;
        Token otherlv_7=null;
        EObject lv_simpleExpr_2_0 = null;

        EObject lv_dimensions_5_0 = null;

        EObject lv_innerArray_8_0 = null;



        	enterRule();

        try {
            // InternalSpectra.g:739:2: ( ( ( ( ( (lv_name_0_0= RULE_ID ) ) otherlv_1= ':=' ( (lv_simpleExpr_2_0= ruleTemporalExpression ) ) ) | ( ( (lv_name_3_0= RULE_ID ) ) (otherlv_4= '[' ( (lv_dimensions_5_0= ruleSizeDefineDecl ) ) otherlv_6= ']' )+ otherlv_7= ':=' ( (lv_innerArray_8_0= ruleDefineArray ) ) ) ) ruleTOK_SEMI ) )
            // InternalSpectra.g:740:2: ( ( ( ( (lv_name_0_0= RULE_ID ) ) otherlv_1= ':=' ( (lv_simpleExpr_2_0= ruleTemporalExpression ) ) ) | ( ( (lv_name_3_0= RULE_ID ) ) (otherlv_4= '[' ( (lv_dimensions_5_0= ruleSizeDefineDecl ) ) otherlv_6= ']' )+ otherlv_7= ':=' ( (lv_innerArray_8_0= ruleDefineArray ) ) ) ) ruleTOK_SEMI )
            {
            // InternalSpectra.g:740:2: ( ( ( ( (lv_name_0_0= RULE_ID ) ) otherlv_1= ':=' ( (lv_simpleExpr_2_0= ruleTemporalExpression ) ) ) | ( ( (lv_name_3_0= RULE_ID ) ) (otherlv_4= '[' ( (lv_dimensions_5_0= ruleSizeDefineDecl ) ) otherlv_6= ']' )+ otherlv_7= ':=' ( (lv_innerArray_8_0= ruleDefineArray ) ) ) ) ruleTOK_SEMI )
            // InternalSpectra.g:741:3: ( ( ( (lv_name_0_0= RULE_ID ) ) otherlv_1= ':=' ( (lv_simpleExpr_2_0= ruleTemporalExpression ) ) ) | ( ( (lv_name_3_0= RULE_ID ) ) (otherlv_4= '[' ( (lv_dimensions_5_0= ruleSizeDefineDecl ) ) otherlv_6= ']' )+ otherlv_7= ':=' ( (lv_innerArray_8_0= ruleDefineArray ) ) ) ) ruleTOK_SEMI
            {
            // InternalSpectra.g:741:3: ( ( ( (lv_name_0_0= RULE_ID ) ) otherlv_1= ':=' ( (lv_simpleExpr_2_0= ruleTemporalExpression ) ) ) | ( ( (lv_name_3_0= RULE_ID ) ) (otherlv_4= '[' ( (lv_dimensions_5_0= ruleSizeDefineDecl ) ) otherlv_6= ']' )+ otherlv_7= ':=' ( (lv_innerArray_8_0= ruleDefineArray ) ) ) )
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==RULE_ID) ) {
                int LA10_1 = input.LA(2);

                if ( (LA10_1==21) ) {
                    alt10=2;
                }
                else if ( (LA10_1==20) ) {
                    alt10=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 10, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // InternalSpectra.g:742:4: ( ( (lv_name_0_0= RULE_ID ) ) otherlv_1= ':=' ( (lv_simpleExpr_2_0= ruleTemporalExpression ) ) )
                    {
                    // InternalSpectra.g:742:4: ( ( (lv_name_0_0= RULE_ID ) ) otherlv_1= ':=' ( (lv_simpleExpr_2_0= ruleTemporalExpression ) ) )
                    // InternalSpectra.g:743:5: ( (lv_name_0_0= RULE_ID ) ) otherlv_1= ':=' ( (lv_simpleExpr_2_0= ruleTemporalExpression ) )
                    {
                    // InternalSpectra.g:743:5: ( (lv_name_0_0= RULE_ID ) )
                    // InternalSpectra.g:744:6: (lv_name_0_0= RULE_ID )
                    {
                    // InternalSpectra.g:744:6: (lv_name_0_0= RULE_ID )
                    // InternalSpectra.g:745:7: lv_name_0_0= RULE_ID
                    {
                    lv_name_0_0=(Token)match(input,RULE_ID,FOLLOW_17); 

                    							newLeafNode(lv_name_0_0, grammarAccess.getDefineDeclAccess().getNameIDTerminalRuleCall_0_0_0_0());
                    						

                    							if (current==null) {
                    								current = createModelElement(grammarAccess.getDefineDeclRule());
                    							}
                    							setWithLastConsumed(
                    								current,
                    								"name",
                    								lv_name_0_0,
                    								"org.eclipse.xtext.common.Terminals.ID");
                    						

                    }


                    }

                    otherlv_1=(Token)match(input,20,FOLLOW_18); 

                    					newLeafNode(otherlv_1, grammarAccess.getDefineDeclAccess().getColonEqualsSignKeyword_0_0_1());
                    				
                    // InternalSpectra.g:765:5: ( (lv_simpleExpr_2_0= ruleTemporalExpression ) )
                    // InternalSpectra.g:766:6: (lv_simpleExpr_2_0= ruleTemporalExpression )
                    {
                    // InternalSpectra.g:766:6: (lv_simpleExpr_2_0= ruleTemporalExpression )
                    // InternalSpectra.g:767:7: lv_simpleExpr_2_0= ruleTemporalExpression
                    {

                    							newCompositeNode(grammarAccess.getDefineDeclAccess().getSimpleExprTemporalExpressionParserRuleCall_0_0_2_0());
                    						
                    pushFollow(FOLLOW_13);
                    lv_simpleExpr_2_0=ruleTemporalExpression();

                    state._fsp--;


                    							if (current==null) {
                    								current = createModelElementForParent(grammarAccess.getDefineDeclRule());
                    							}
                    							set(
                    								current,
                    								"simpleExpr",
                    								lv_simpleExpr_2_0,
                    								"tau.smlab.syntech.Spectra.TemporalExpression");
                    							afterParserOrEnumRuleCall();
                    						

                    }


                    }


                    }


                    }
                    break;
                case 2 :
                    // InternalSpectra.g:786:4: ( ( (lv_name_3_0= RULE_ID ) ) (otherlv_4= '[' ( (lv_dimensions_5_0= ruleSizeDefineDecl ) ) otherlv_6= ']' )+ otherlv_7= ':=' ( (lv_innerArray_8_0= ruleDefineArray ) ) )
                    {
                    // InternalSpectra.g:786:4: ( ( (lv_name_3_0= RULE_ID ) ) (otherlv_4= '[' ( (lv_dimensions_5_0= ruleSizeDefineDecl ) ) otherlv_6= ']' )+ otherlv_7= ':=' ( (lv_innerArray_8_0= ruleDefineArray ) ) )
                    // InternalSpectra.g:787:5: ( (lv_name_3_0= RULE_ID ) ) (otherlv_4= '[' ( (lv_dimensions_5_0= ruleSizeDefineDecl ) ) otherlv_6= ']' )+ otherlv_7= ':=' ( (lv_innerArray_8_0= ruleDefineArray ) )
                    {
                    // InternalSpectra.g:787:5: ( (lv_name_3_0= RULE_ID ) )
                    // InternalSpectra.g:788:6: (lv_name_3_0= RULE_ID )
                    {
                    // InternalSpectra.g:788:6: (lv_name_3_0= RULE_ID )
                    // InternalSpectra.g:789:7: lv_name_3_0= RULE_ID
                    {
                    lv_name_3_0=(Token)match(input,RULE_ID,FOLLOW_19); 

                    							newLeafNode(lv_name_3_0, grammarAccess.getDefineDeclAccess().getNameIDTerminalRuleCall_0_1_0_0());
                    						

                    							if (current==null) {
                    								current = createModelElement(grammarAccess.getDefineDeclRule());
                    							}
                    							setWithLastConsumed(
                    								current,
                    								"name",
                    								lv_name_3_0,
                    								"org.eclipse.xtext.common.Terminals.ID");
                    						

                    }


                    }

                    // InternalSpectra.g:805:5: (otherlv_4= '[' ( (lv_dimensions_5_0= ruleSizeDefineDecl ) ) otherlv_6= ']' )+
                    int cnt9=0;
                    loop9:
                    do {
                        int alt9=2;
                        int LA9_0 = input.LA(1);

                        if ( (LA9_0==21) ) {
                            alt9=1;
                        }


                        switch (alt9) {
                    	case 1 :
                    	    // InternalSpectra.g:806:6: otherlv_4= '[' ( (lv_dimensions_5_0= ruleSizeDefineDecl ) ) otherlv_6= ']'
                    	    {
                    	    otherlv_4=(Token)match(input,21,FOLLOW_20); 

                    	    						newLeafNode(otherlv_4, grammarAccess.getDefineDeclAccess().getLeftSquareBracketKeyword_0_1_1_0());
                    	    					
                    	    // InternalSpectra.g:810:6: ( (lv_dimensions_5_0= ruleSizeDefineDecl ) )
                    	    // InternalSpectra.g:811:7: (lv_dimensions_5_0= ruleSizeDefineDecl )
                    	    {
                    	    // InternalSpectra.g:811:7: (lv_dimensions_5_0= ruleSizeDefineDecl )
                    	    // InternalSpectra.g:812:8: lv_dimensions_5_0= ruleSizeDefineDecl
                    	    {

                    	    								newCompositeNode(grammarAccess.getDefineDeclAccess().getDimensionsSizeDefineDeclParserRuleCall_0_1_1_1_0());
                    	    							
                    	    pushFollow(FOLLOW_21);
                    	    lv_dimensions_5_0=ruleSizeDefineDecl();

                    	    state._fsp--;


                    	    								if (current==null) {
                    	    									current = createModelElementForParent(grammarAccess.getDefineDeclRule());
                    	    								}
                    	    								add(
                    	    									current,
                    	    									"dimensions",
                    	    									lv_dimensions_5_0,
                    	    									"tau.smlab.syntech.Spectra.SizeDefineDecl");
                    	    								afterParserOrEnumRuleCall();
                    	    							

                    	    }


                    	    }

                    	    otherlv_6=(Token)match(input,22,FOLLOW_22); 

                    	    						newLeafNode(otherlv_6, grammarAccess.getDefineDeclAccess().getRightSquareBracketKeyword_0_1_1_2());
                    	    					

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt9 >= 1 ) break loop9;
                                EarlyExitException eee =
                                    new EarlyExitException(9, input);
                                throw eee;
                        }
                        cnt9++;
                    } while (true);

                    otherlv_7=(Token)match(input,20,FOLLOW_23); 

                    					newLeafNode(otherlv_7, grammarAccess.getDefineDeclAccess().getColonEqualsSignKeyword_0_1_2());
                    				
                    // InternalSpectra.g:838:5: ( (lv_innerArray_8_0= ruleDefineArray ) )
                    // InternalSpectra.g:839:6: (lv_innerArray_8_0= ruleDefineArray )
                    {
                    // InternalSpectra.g:839:6: (lv_innerArray_8_0= ruleDefineArray )
                    // InternalSpectra.g:840:7: lv_innerArray_8_0= ruleDefineArray
                    {

                    							newCompositeNode(grammarAccess.getDefineDeclAccess().getInnerArrayDefineArrayParserRuleCall_0_1_3_0());
                    						
                    pushFollow(FOLLOW_13);
                    lv_innerArray_8_0=ruleDefineArray();

                    state._fsp--;


                    							if (current==null) {
                    								current = createModelElementForParent(grammarAccess.getDefineDeclRule());
                    							}
                    							set(
                    								current,
                    								"innerArray",
                    								lv_innerArray_8_0,
                    								"tau.smlab.syntech.Spectra.DefineArray");
                    							afterParserOrEnumRuleCall();
                    						

                    }


                    }


                    }


                    }
                    break;

            }


            			newCompositeNode(grammarAccess.getDefineDeclAccess().getTOK_SEMIParserRuleCall_1());
            		
            pushFollow(FOLLOW_2);
            ruleTOK_SEMI();

            state._fsp--;


            			afterParserOrEnumRuleCall();
            		

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleDefineDecl"


    // $ANTLR start "entryRuleDefineArray"
    // InternalSpectra.g:870:1: entryRuleDefineArray returns [EObject current=null] : iv_ruleDefineArray= ruleDefineArray EOF ;
    public final EObject entryRuleDefineArray() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDefineArray = null;


        try {
            // InternalSpectra.g:870:52: (iv_ruleDefineArray= ruleDefineArray EOF )
            // InternalSpectra.g:871:2: iv_ruleDefineArray= ruleDefineArray EOF
            {
             newCompositeNode(grammarAccess.getDefineArrayRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleDefineArray=ruleDefineArray();

            state._fsp--;

             current =iv_ruleDefineArray; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleDefineArray"


    // $ANTLR start "ruleDefineArray"
    // InternalSpectra.g:877:1: ruleDefineArray returns [EObject current=null] : ( (otherlv_0= '{' ( ( (lv_simpleExprs_1_0= ruleTemporalExpression ) ) (otherlv_2= ',' ( (lv_simpleExprs_3_0= ruleTemporalExpression ) ) )* ) otherlv_4= '}' ) | (otherlv_5= '{' ( ( (lv_innerArrays_6_0= ruleDefineArray ) ) (otherlv_7= ',' ( (lv_innerArrays_8_0= ruleDefineArray ) ) )* ) otherlv_9= '}' ) ) ;
    public final EObject ruleDefineArray() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_2=null;
        Token otherlv_4=null;
        Token otherlv_5=null;
        Token otherlv_7=null;
        Token otherlv_9=null;
        EObject lv_simpleExprs_1_0 = null;

        EObject lv_simpleExprs_3_0 = null;

        EObject lv_innerArrays_6_0 = null;

        EObject lv_innerArrays_8_0 = null;



        	enterRule();

        try {
            // InternalSpectra.g:883:2: ( ( (otherlv_0= '{' ( ( (lv_simpleExprs_1_0= ruleTemporalExpression ) ) (otherlv_2= ',' ( (lv_simpleExprs_3_0= ruleTemporalExpression ) ) )* ) otherlv_4= '}' ) | (otherlv_5= '{' ( ( (lv_innerArrays_6_0= ruleDefineArray ) ) (otherlv_7= ',' ( (lv_innerArrays_8_0= ruleDefineArray ) ) )* ) otherlv_9= '}' ) ) )
            // InternalSpectra.g:884:2: ( (otherlv_0= '{' ( ( (lv_simpleExprs_1_0= ruleTemporalExpression ) ) (otherlv_2= ',' ( (lv_simpleExprs_3_0= ruleTemporalExpression ) ) )* ) otherlv_4= '}' ) | (otherlv_5= '{' ( ( (lv_innerArrays_6_0= ruleDefineArray ) ) (otherlv_7= ',' ( (lv_innerArrays_8_0= ruleDefineArray ) ) )* ) otherlv_9= '}' ) )
            {
            // InternalSpectra.g:884:2: ( (otherlv_0= '{' ( ( (lv_simpleExprs_1_0= ruleTemporalExpression ) ) (otherlv_2= ',' ( (lv_simpleExprs_3_0= ruleTemporalExpression ) ) )* ) otherlv_4= '}' ) | (otherlv_5= '{' ( ( (lv_innerArrays_6_0= ruleDefineArray ) ) (otherlv_7= ',' ( (lv_innerArrays_8_0= ruleDefineArray ) ) )* ) otherlv_9= '}' ) )
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==23) ) {
                int LA13_1 = input.LA(2);

                if ( (LA13_1==RULE_ID||LA13_1==RULE_INT||LA13_1==16||LA13_1==26||LA13_1==28||(LA13_1>=82 && LA13_1<=89)||(LA13_1>=97 && LA13_1<=100)||(LA13_1>=105 && LA13_1<=106)) ) {
                    alt13=1;
                }
                else if ( (LA13_1==23) ) {
                    alt13=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 13, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 13, 0, input);

                throw nvae;
            }
            switch (alt13) {
                case 1 :
                    // InternalSpectra.g:885:3: (otherlv_0= '{' ( ( (lv_simpleExprs_1_0= ruleTemporalExpression ) ) (otherlv_2= ',' ( (lv_simpleExprs_3_0= ruleTemporalExpression ) ) )* ) otherlv_4= '}' )
                    {
                    // InternalSpectra.g:885:3: (otherlv_0= '{' ( ( (lv_simpleExprs_1_0= ruleTemporalExpression ) ) (otherlv_2= ',' ( (lv_simpleExprs_3_0= ruleTemporalExpression ) ) )* ) otherlv_4= '}' )
                    // InternalSpectra.g:886:4: otherlv_0= '{' ( ( (lv_simpleExprs_1_0= ruleTemporalExpression ) ) (otherlv_2= ',' ( (lv_simpleExprs_3_0= ruleTemporalExpression ) ) )* ) otherlv_4= '}'
                    {
                    otherlv_0=(Token)match(input,23,FOLLOW_18); 

                    				newLeafNode(otherlv_0, grammarAccess.getDefineArrayAccess().getLeftCurlyBracketKeyword_0_0());
                    			
                    // InternalSpectra.g:890:4: ( ( (lv_simpleExprs_1_0= ruleTemporalExpression ) ) (otherlv_2= ',' ( (lv_simpleExprs_3_0= ruleTemporalExpression ) ) )* )
                    // InternalSpectra.g:891:5: ( (lv_simpleExprs_1_0= ruleTemporalExpression ) ) (otherlv_2= ',' ( (lv_simpleExprs_3_0= ruleTemporalExpression ) ) )*
                    {
                    // InternalSpectra.g:891:5: ( (lv_simpleExprs_1_0= ruleTemporalExpression ) )
                    // InternalSpectra.g:892:6: (lv_simpleExprs_1_0= ruleTemporalExpression )
                    {
                    // InternalSpectra.g:892:6: (lv_simpleExprs_1_0= ruleTemporalExpression )
                    // InternalSpectra.g:893:7: lv_simpleExprs_1_0= ruleTemporalExpression
                    {

                    							newCompositeNode(grammarAccess.getDefineArrayAccess().getSimpleExprsTemporalExpressionParserRuleCall_0_1_0_0());
                    						
                    pushFollow(FOLLOW_24);
                    lv_simpleExprs_1_0=ruleTemporalExpression();

                    state._fsp--;


                    							if (current==null) {
                    								current = createModelElementForParent(grammarAccess.getDefineArrayRule());
                    							}
                    							add(
                    								current,
                    								"simpleExprs",
                    								lv_simpleExprs_1_0,
                    								"tau.smlab.syntech.Spectra.TemporalExpression");
                    							afterParserOrEnumRuleCall();
                    						

                    }


                    }

                    // InternalSpectra.g:910:5: (otherlv_2= ',' ( (lv_simpleExprs_3_0= ruleTemporalExpression ) ) )*
                    loop11:
                    do {
                        int alt11=2;
                        int LA11_0 = input.LA(1);

                        if ( (LA11_0==24) ) {
                            alt11=1;
                        }


                        switch (alt11) {
                    	case 1 :
                    	    // InternalSpectra.g:911:6: otherlv_2= ',' ( (lv_simpleExprs_3_0= ruleTemporalExpression ) )
                    	    {
                    	    otherlv_2=(Token)match(input,24,FOLLOW_18); 

                    	    						newLeafNode(otherlv_2, grammarAccess.getDefineArrayAccess().getCommaKeyword_0_1_1_0());
                    	    					
                    	    // InternalSpectra.g:915:6: ( (lv_simpleExprs_3_0= ruleTemporalExpression ) )
                    	    // InternalSpectra.g:916:7: (lv_simpleExprs_3_0= ruleTemporalExpression )
                    	    {
                    	    // InternalSpectra.g:916:7: (lv_simpleExprs_3_0= ruleTemporalExpression )
                    	    // InternalSpectra.g:917:8: lv_simpleExprs_3_0= ruleTemporalExpression
                    	    {

                    	    								newCompositeNode(grammarAccess.getDefineArrayAccess().getSimpleExprsTemporalExpressionParserRuleCall_0_1_1_1_0());
                    	    							
                    	    pushFollow(FOLLOW_24);
                    	    lv_simpleExprs_3_0=ruleTemporalExpression();

                    	    state._fsp--;


                    	    								if (current==null) {
                    	    									current = createModelElementForParent(grammarAccess.getDefineArrayRule());
                    	    								}
                    	    								add(
                    	    									current,
                    	    									"simpleExprs",
                    	    									lv_simpleExprs_3_0,
                    	    									"tau.smlab.syntech.Spectra.TemporalExpression");
                    	    								afterParserOrEnumRuleCall();
                    	    							

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop11;
                        }
                    } while (true);


                    }

                    otherlv_4=(Token)match(input,25,FOLLOW_2); 

                    				newLeafNode(otherlv_4, grammarAccess.getDefineArrayAccess().getRightCurlyBracketKeyword_0_2());
                    			

                    }


                    }
                    break;
                case 2 :
                    // InternalSpectra.g:942:3: (otherlv_5= '{' ( ( (lv_innerArrays_6_0= ruleDefineArray ) ) (otherlv_7= ',' ( (lv_innerArrays_8_0= ruleDefineArray ) ) )* ) otherlv_9= '}' )
                    {
                    // InternalSpectra.g:942:3: (otherlv_5= '{' ( ( (lv_innerArrays_6_0= ruleDefineArray ) ) (otherlv_7= ',' ( (lv_innerArrays_8_0= ruleDefineArray ) ) )* ) otherlv_9= '}' )
                    // InternalSpectra.g:943:4: otherlv_5= '{' ( ( (lv_innerArrays_6_0= ruleDefineArray ) ) (otherlv_7= ',' ( (lv_innerArrays_8_0= ruleDefineArray ) ) )* ) otherlv_9= '}'
                    {
                    otherlv_5=(Token)match(input,23,FOLLOW_23); 

                    				newLeafNode(otherlv_5, grammarAccess.getDefineArrayAccess().getLeftCurlyBracketKeyword_1_0());
                    			
                    // InternalSpectra.g:947:4: ( ( (lv_innerArrays_6_0= ruleDefineArray ) ) (otherlv_7= ',' ( (lv_innerArrays_8_0= ruleDefineArray ) ) )* )
                    // InternalSpectra.g:948:5: ( (lv_innerArrays_6_0= ruleDefineArray ) ) (otherlv_7= ',' ( (lv_innerArrays_8_0= ruleDefineArray ) ) )*
                    {
                    // InternalSpectra.g:948:5: ( (lv_innerArrays_6_0= ruleDefineArray ) )
                    // InternalSpectra.g:949:6: (lv_innerArrays_6_0= ruleDefineArray )
                    {
                    // InternalSpectra.g:949:6: (lv_innerArrays_6_0= ruleDefineArray )
                    // InternalSpectra.g:950:7: lv_innerArrays_6_0= ruleDefineArray
                    {

                    							newCompositeNode(grammarAccess.getDefineArrayAccess().getInnerArraysDefineArrayParserRuleCall_1_1_0_0());
                    						
                    pushFollow(FOLLOW_24);
                    lv_innerArrays_6_0=ruleDefineArray();

                    state._fsp--;


                    							if (current==null) {
                    								current = createModelElementForParent(grammarAccess.getDefineArrayRule());
                    							}
                    							add(
                    								current,
                    								"innerArrays",
                    								lv_innerArrays_6_0,
                    								"tau.smlab.syntech.Spectra.DefineArray");
                    							afterParserOrEnumRuleCall();
                    						

                    }


                    }

                    // InternalSpectra.g:967:5: (otherlv_7= ',' ( (lv_innerArrays_8_0= ruleDefineArray ) ) )*
                    loop12:
                    do {
                        int alt12=2;
                        int LA12_0 = input.LA(1);

                        if ( (LA12_0==24) ) {
                            alt12=1;
                        }


                        switch (alt12) {
                    	case 1 :
                    	    // InternalSpectra.g:968:6: otherlv_7= ',' ( (lv_innerArrays_8_0= ruleDefineArray ) )
                    	    {
                    	    otherlv_7=(Token)match(input,24,FOLLOW_23); 

                    	    						newLeafNode(otherlv_7, grammarAccess.getDefineArrayAccess().getCommaKeyword_1_1_1_0());
                    	    					
                    	    // InternalSpectra.g:972:6: ( (lv_innerArrays_8_0= ruleDefineArray ) )
                    	    // InternalSpectra.g:973:7: (lv_innerArrays_8_0= ruleDefineArray )
                    	    {
                    	    // InternalSpectra.g:973:7: (lv_innerArrays_8_0= ruleDefineArray )
                    	    // InternalSpectra.g:974:8: lv_innerArrays_8_0= ruleDefineArray
                    	    {

                    	    								newCompositeNode(grammarAccess.getDefineArrayAccess().getInnerArraysDefineArrayParserRuleCall_1_1_1_1_0());
                    	    							
                    	    pushFollow(FOLLOW_24);
                    	    lv_innerArrays_8_0=ruleDefineArray();

                    	    state._fsp--;


                    	    								if (current==null) {
                    	    									current = createModelElementForParent(grammarAccess.getDefineArrayRule());
                    	    								}
                    	    								add(
                    	    									current,
                    	    									"innerArrays",
                    	    									lv_innerArrays_8_0,
                    	    									"tau.smlab.syntech.Spectra.DefineArray");
                    	    								afterParserOrEnumRuleCall();
                    	    							

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop12;
                        }
                    } while (true);


                    }

                    otherlv_9=(Token)match(input,25,FOLLOW_2); 

                    				newLeafNode(otherlv_9, grammarAccess.getDefineArrayAccess().getRightCurlyBracketKeyword_1_2());
                    			

                    }


                    }
                    break;

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleDefineArray"


    // $ANTLR start "entryRuleDefineRegExp"
    // InternalSpectra.g:1002:1: entryRuleDefineRegExp returns [EObject current=null] : iv_ruleDefineRegExp= ruleDefineRegExp EOF ;
    public final EObject entryRuleDefineRegExp() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDefineRegExp = null;


        try {
            // InternalSpectra.g:1002:53: (iv_ruleDefineRegExp= ruleDefineRegExp EOF )
            // InternalSpectra.g:1003:2: iv_ruleDefineRegExp= ruleDefineRegExp EOF
            {
             newCompositeNode(grammarAccess.getDefineRegExpRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleDefineRegExp=ruleDefineRegExp();

            state._fsp--;

             current =iv_ruleDefineRegExp; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleDefineRegExp"


    // $ANTLR start "ruleDefineRegExp"
    // InternalSpectra.g:1009:1: ruleDefineRegExp returns [EObject current=null] : (otherlv_0= 'regexp' ( (lv_defineRegsList_1_0= ruleDefineRegExpDecl ) )+ ) ;
    public final EObject ruleDefineRegExp() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        EObject lv_defineRegsList_1_0 = null;



        	enterRule();

        try {
            // InternalSpectra.g:1015:2: ( (otherlv_0= 'regexp' ( (lv_defineRegsList_1_0= ruleDefineRegExpDecl ) )+ ) )
            // InternalSpectra.g:1016:2: (otherlv_0= 'regexp' ( (lv_defineRegsList_1_0= ruleDefineRegExpDecl ) )+ )
            {
            // InternalSpectra.g:1016:2: (otherlv_0= 'regexp' ( (lv_defineRegsList_1_0= ruleDefineRegExpDecl ) )+ )
            // InternalSpectra.g:1017:3: otherlv_0= 'regexp' ( (lv_defineRegsList_1_0= ruleDefineRegExpDecl ) )+
            {
            otherlv_0=(Token)match(input,26,FOLLOW_4); 

            			newLeafNode(otherlv_0, grammarAccess.getDefineRegExpAccess().getRegexpKeyword_0());
            		
            // InternalSpectra.g:1021:3: ( (lv_defineRegsList_1_0= ruleDefineRegExpDecl ) )+
            int cnt14=0;
            loop14:
            do {
                int alt14=2;
                int LA14_0 = input.LA(1);

                if ( (LA14_0==RULE_ID) ) {
                    alt14=1;
                }


                switch (alt14) {
            	case 1 :
            	    // InternalSpectra.g:1022:4: (lv_defineRegsList_1_0= ruleDefineRegExpDecl )
            	    {
            	    // InternalSpectra.g:1022:4: (lv_defineRegsList_1_0= ruleDefineRegExpDecl )
            	    // InternalSpectra.g:1023:5: lv_defineRegsList_1_0= ruleDefineRegExpDecl
            	    {

            	    					newCompositeNode(grammarAccess.getDefineRegExpAccess().getDefineRegsListDefineRegExpDeclParserRuleCall_1_0());
            	    				
            	    pushFollow(FOLLOW_16);
            	    lv_defineRegsList_1_0=ruleDefineRegExpDecl();

            	    state._fsp--;


            	    					if (current==null) {
            	    						current = createModelElementForParent(grammarAccess.getDefineRegExpRule());
            	    					}
            	    					add(
            	    						current,
            	    						"defineRegsList",
            	    						lv_defineRegsList_1_0,
            	    						"tau.smlab.syntech.Spectra.DefineRegExpDecl");
            	    					afterParserOrEnumRuleCall();
            	    				

            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt14 >= 1 ) break loop14;
                        EarlyExitException eee =
                            new EarlyExitException(14, input);
                        throw eee;
                }
                cnt14++;
            } while (true);


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleDefineRegExp"


    // $ANTLR start "entryRuleDefineRegExpDecl"
    // InternalSpectra.g:1044:1: entryRuleDefineRegExpDecl returns [EObject current=null] : iv_ruleDefineRegExpDecl= ruleDefineRegExpDecl EOF ;
    public final EObject entryRuleDefineRegExpDecl() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDefineRegExpDecl = null;


        try {
            // InternalSpectra.g:1044:57: (iv_ruleDefineRegExpDecl= ruleDefineRegExpDecl EOF )
            // InternalSpectra.g:1045:2: iv_ruleDefineRegExpDecl= ruleDefineRegExpDecl EOF
            {
             newCompositeNode(grammarAccess.getDefineRegExpDeclRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleDefineRegExpDecl=ruleDefineRegExpDecl();

            state._fsp--;

             current =iv_ruleDefineRegExpDecl; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleDefineRegExpDecl"


    // $ANTLR start "ruleDefineRegExpDecl"
    // InternalSpectra.g:1051:1: ruleDefineRegExpDecl returns [EObject current=null] : ( ( (lv_name_0_0= RULE_ID ) ) otherlv_1= ':=' ( (lv_exp_2_0= ruleRegExp ) ) ruleTOK_SEMI ) ;
    public final EObject ruleDefineRegExpDecl() throws RecognitionException {
        EObject current = null;

        Token lv_name_0_0=null;
        Token otherlv_1=null;
        EObject lv_exp_2_0 = null;



        	enterRule();

        try {
            // InternalSpectra.g:1057:2: ( ( ( (lv_name_0_0= RULE_ID ) ) otherlv_1= ':=' ( (lv_exp_2_0= ruleRegExp ) ) ruleTOK_SEMI ) )
            // InternalSpectra.g:1058:2: ( ( (lv_name_0_0= RULE_ID ) ) otherlv_1= ':=' ( (lv_exp_2_0= ruleRegExp ) ) ruleTOK_SEMI )
            {
            // InternalSpectra.g:1058:2: ( ( (lv_name_0_0= RULE_ID ) ) otherlv_1= ':=' ( (lv_exp_2_0= ruleRegExp ) ) ruleTOK_SEMI )
            // InternalSpectra.g:1059:3: ( (lv_name_0_0= RULE_ID ) ) otherlv_1= ':=' ( (lv_exp_2_0= ruleRegExp ) ) ruleTOK_SEMI
            {
            // InternalSpectra.g:1059:3: ( (lv_name_0_0= RULE_ID ) )
            // InternalSpectra.g:1060:4: (lv_name_0_0= RULE_ID )
            {
            // InternalSpectra.g:1060:4: (lv_name_0_0= RULE_ID )
            // InternalSpectra.g:1061:5: lv_name_0_0= RULE_ID
            {
            lv_name_0_0=(Token)match(input,RULE_ID,FOLLOW_17); 

            					newLeafNode(lv_name_0_0, grammarAccess.getDefineRegExpDeclAccess().getNameIDTerminalRuleCall_0_0());
            				

            					if (current==null) {
            						current = createModelElement(grammarAccess.getDefineRegExpDeclRule());
            					}
            					setWithLastConsumed(
            						current,
            						"name",
            						lv_name_0_0,
            						"org.eclipse.xtext.common.Terminals.ID");
            				

            }


            }

            otherlv_1=(Token)match(input,20,FOLLOW_25); 

            			newLeafNode(otherlv_1, grammarAccess.getDefineRegExpDeclAccess().getColonEqualsSignKeyword_1());
            		
            // InternalSpectra.g:1081:3: ( (lv_exp_2_0= ruleRegExp ) )
            // InternalSpectra.g:1082:4: (lv_exp_2_0= ruleRegExp )
            {
            // InternalSpectra.g:1082:4: (lv_exp_2_0= ruleRegExp )
            // InternalSpectra.g:1083:5: lv_exp_2_0= ruleRegExp
            {

            					newCompositeNode(grammarAccess.getDefineRegExpDeclAccess().getExpRegExpParserRuleCall_2_0());
            				
            pushFollow(FOLLOW_13);
            lv_exp_2_0=ruleRegExp();

            state._fsp--;


            					if (current==null) {
            						current = createModelElementForParent(grammarAccess.getDefineRegExpDeclRule());
            					}
            					set(
            						current,
            						"exp",
            						lv_exp_2_0,
            						"tau.smlab.syntech.Spectra.RegExp");
            					afterParserOrEnumRuleCall();
            				

            }


            }


            			newCompositeNode(grammarAccess.getDefineRegExpDeclAccess().getTOK_SEMIParserRuleCall_3());
            		
            pushFollow(FOLLOW_2);
            ruleTOK_SEMI();

            state._fsp--;


            			afterParserOrEnumRuleCall();
            		

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleDefineRegExpDecl"


    // $ANTLR start "entryRulePatternParamList"
    // InternalSpectra.g:1111:1: entryRulePatternParamList returns [EObject current=null] : iv_rulePatternParamList= rulePatternParamList EOF ;
    public final EObject entryRulePatternParamList() throws RecognitionException {
        EObject current = null;

        EObject iv_rulePatternParamList = null;


        try {
            // InternalSpectra.g:1111:57: (iv_rulePatternParamList= rulePatternParamList EOF )
            // InternalSpectra.g:1112:2: iv_rulePatternParamList= rulePatternParamList EOF
            {
             newCompositeNode(grammarAccess.getPatternParamListRule()); 
            pushFollow(FOLLOW_1);
            iv_rulePatternParamList=rulePatternParamList();

            state._fsp--;

             current =iv_rulePatternParamList; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRulePatternParamList"


    // $ANTLR start "rulePatternParamList"
    // InternalSpectra.g:1118:1: rulePatternParamList returns [EObject current=null] : ( ( (lv_params_0_0= rulePatternParam ) ) (otherlv_1= ',' ( (lv_params_2_0= rulePatternParam ) ) )* ) ;
    public final EObject rulePatternParamList() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        EObject lv_params_0_0 = null;

        EObject lv_params_2_0 = null;



        	enterRule();

        try {
            // InternalSpectra.g:1124:2: ( ( ( (lv_params_0_0= rulePatternParam ) ) (otherlv_1= ',' ( (lv_params_2_0= rulePatternParam ) ) )* ) )
            // InternalSpectra.g:1125:2: ( ( (lv_params_0_0= rulePatternParam ) ) (otherlv_1= ',' ( (lv_params_2_0= rulePatternParam ) ) )* )
            {
            // InternalSpectra.g:1125:2: ( ( (lv_params_0_0= rulePatternParam ) ) (otherlv_1= ',' ( (lv_params_2_0= rulePatternParam ) ) )* )
            // InternalSpectra.g:1126:3: ( (lv_params_0_0= rulePatternParam ) ) (otherlv_1= ',' ( (lv_params_2_0= rulePatternParam ) ) )*
            {
            // InternalSpectra.g:1126:3: ( (lv_params_0_0= rulePatternParam ) )
            // InternalSpectra.g:1127:4: (lv_params_0_0= rulePatternParam )
            {
            // InternalSpectra.g:1127:4: (lv_params_0_0= rulePatternParam )
            // InternalSpectra.g:1128:5: lv_params_0_0= rulePatternParam
            {

            					newCompositeNode(grammarAccess.getPatternParamListAccess().getParamsPatternParamParserRuleCall_0_0());
            				
            pushFollow(FOLLOW_26);
            lv_params_0_0=rulePatternParam();

            state._fsp--;


            					if (current==null) {
            						current = createModelElementForParent(grammarAccess.getPatternParamListRule());
            					}
            					add(
            						current,
            						"params",
            						lv_params_0_0,
            						"tau.smlab.syntech.Spectra.PatternParam");
            					afterParserOrEnumRuleCall();
            				

            }


            }

            // InternalSpectra.g:1145:3: (otherlv_1= ',' ( (lv_params_2_0= rulePatternParam ) ) )*
            loop15:
            do {
                int alt15=2;
                int LA15_0 = input.LA(1);

                if ( (LA15_0==24) ) {
                    alt15=1;
                }


                switch (alt15) {
            	case 1 :
            	    // InternalSpectra.g:1146:4: otherlv_1= ',' ( (lv_params_2_0= rulePatternParam ) )
            	    {
            	    otherlv_1=(Token)match(input,24,FOLLOW_4); 

            	    				newLeafNode(otherlv_1, grammarAccess.getPatternParamListAccess().getCommaKeyword_1_0());
            	    			
            	    // InternalSpectra.g:1150:4: ( (lv_params_2_0= rulePatternParam ) )
            	    // InternalSpectra.g:1151:5: (lv_params_2_0= rulePatternParam )
            	    {
            	    // InternalSpectra.g:1151:5: (lv_params_2_0= rulePatternParam )
            	    // InternalSpectra.g:1152:6: lv_params_2_0= rulePatternParam
            	    {

            	    						newCompositeNode(grammarAccess.getPatternParamListAccess().getParamsPatternParamParserRuleCall_1_1_0());
            	    					
            	    pushFollow(FOLLOW_26);
            	    lv_params_2_0=rulePatternParam();

            	    state._fsp--;


            	    						if (current==null) {
            	    							current = createModelElementForParent(grammarAccess.getPatternParamListRule());
            	    						}
            	    						add(
            	    							current,
            	    							"params",
            	    							lv_params_2_0,
            	    							"tau.smlab.syntech.Spectra.PatternParam");
            	    						afterParserOrEnumRuleCall();
            	    					

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop15;
                }
            } while (true);


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "rulePatternParamList"


    // $ANTLR start "entryRulePatternParam"
    // InternalSpectra.g:1174:1: entryRulePatternParam returns [EObject current=null] : iv_rulePatternParam= rulePatternParam EOF ;
    public final EObject entryRulePatternParam() throws RecognitionException {
        EObject current = null;

        EObject iv_rulePatternParam = null;


        try {
            // InternalSpectra.g:1174:53: (iv_rulePatternParam= rulePatternParam EOF )
            // InternalSpectra.g:1175:2: iv_rulePatternParam= rulePatternParam EOF
            {
             newCompositeNode(grammarAccess.getPatternParamRule()); 
            pushFollow(FOLLOW_1);
            iv_rulePatternParam=rulePatternParam();

            state._fsp--;

             current =iv_rulePatternParam; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRulePatternParam"


    // $ANTLR start "rulePatternParam"
    // InternalSpectra.g:1181:1: rulePatternParam returns [EObject current=null] : ( (lv_name_0_0= RULE_ID ) ) ;
    public final EObject rulePatternParam() throws RecognitionException {
        EObject current = null;

        Token lv_name_0_0=null;


        	enterRule();

        try {
            // InternalSpectra.g:1187:2: ( ( (lv_name_0_0= RULE_ID ) ) )
            // InternalSpectra.g:1188:2: ( (lv_name_0_0= RULE_ID ) )
            {
            // InternalSpectra.g:1188:2: ( (lv_name_0_0= RULE_ID ) )
            // InternalSpectra.g:1189:3: (lv_name_0_0= RULE_ID )
            {
            // InternalSpectra.g:1189:3: (lv_name_0_0= RULE_ID )
            // InternalSpectra.g:1190:4: lv_name_0_0= RULE_ID
            {
            lv_name_0_0=(Token)match(input,RULE_ID,FOLLOW_2); 

            				newLeafNode(lv_name_0_0, grammarAccess.getPatternParamAccess().getNameIDTerminalRuleCall_0());
            			

            				if (current==null) {
            					current = createModelElement(grammarAccess.getPatternParamRule());
            				}
            				setWithLastConsumed(
            					current,
            					"name",
            					lv_name_0_0,
            					"org.eclipse.xtext.common.Terminals.ID");
            			

            }


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "rulePatternParam"


    // $ANTLR start "entryRuleTypedParamList"
    // InternalSpectra.g:1209:1: entryRuleTypedParamList returns [EObject current=null] : iv_ruleTypedParamList= ruleTypedParamList EOF ;
    public final EObject entryRuleTypedParamList() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleTypedParamList = null;


        try {
            // InternalSpectra.g:1209:55: (iv_ruleTypedParamList= ruleTypedParamList EOF )
            // InternalSpectra.g:1210:2: iv_ruleTypedParamList= ruleTypedParamList EOF
            {
             newCompositeNode(grammarAccess.getTypedParamListRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleTypedParamList=ruleTypedParamList();

            state._fsp--;

             current =iv_ruleTypedParamList; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleTypedParamList"


    // $ANTLR start "ruleTypedParamList"
    // InternalSpectra.g:1216:1: ruleTypedParamList returns [EObject current=null] : ( ( (lv_params_0_0= ruleTypedParam ) ) (otherlv_1= ',' ( (lv_params_2_0= ruleTypedParam ) ) )* ) ;
    public final EObject ruleTypedParamList() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        EObject lv_params_0_0 = null;

        EObject lv_params_2_0 = null;



        	enterRule();

        try {
            // InternalSpectra.g:1222:2: ( ( ( (lv_params_0_0= ruleTypedParam ) ) (otherlv_1= ',' ( (lv_params_2_0= ruleTypedParam ) ) )* ) )
            // InternalSpectra.g:1223:2: ( ( (lv_params_0_0= ruleTypedParam ) ) (otherlv_1= ',' ( (lv_params_2_0= ruleTypedParam ) ) )* )
            {
            // InternalSpectra.g:1223:2: ( ( (lv_params_0_0= ruleTypedParam ) ) (otherlv_1= ',' ( (lv_params_2_0= ruleTypedParam ) ) )* )
            // InternalSpectra.g:1224:3: ( (lv_params_0_0= ruleTypedParam ) ) (otherlv_1= ',' ( (lv_params_2_0= ruleTypedParam ) ) )*
            {
            // InternalSpectra.g:1224:3: ( (lv_params_0_0= ruleTypedParam ) )
            // InternalSpectra.g:1225:4: (lv_params_0_0= ruleTypedParam )
            {
            // InternalSpectra.g:1225:4: (lv_params_0_0= ruleTypedParam )
            // InternalSpectra.g:1226:5: lv_params_0_0= ruleTypedParam
            {

            					newCompositeNode(grammarAccess.getTypedParamListAccess().getParamsTypedParamParserRuleCall_0_0());
            				
            pushFollow(FOLLOW_26);
            lv_params_0_0=ruleTypedParam();

            state._fsp--;


            					if (current==null) {
            						current = createModelElementForParent(grammarAccess.getTypedParamListRule());
            					}
            					add(
            						current,
            						"params",
            						lv_params_0_0,
            						"tau.smlab.syntech.Spectra.TypedParam");
            					afterParserOrEnumRuleCall();
            				

            }


            }

            // InternalSpectra.g:1243:3: (otherlv_1= ',' ( (lv_params_2_0= ruleTypedParam ) ) )*
            loop16:
            do {
                int alt16=2;
                int LA16_0 = input.LA(1);

                if ( (LA16_0==24) ) {
                    alt16=1;
                }


                switch (alt16) {
            	case 1 :
            	    // InternalSpectra.g:1244:4: otherlv_1= ',' ( (lv_params_2_0= ruleTypedParam ) )
            	    {
            	    otherlv_1=(Token)match(input,24,FOLLOW_14); 

            	    				newLeafNode(otherlv_1, grammarAccess.getTypedParamListAccess().getCommaKeyword_1_0());
            	    			
            	    // InternalSpectra.g:1248:4: ( (lv_params_2_0= ruleTypedParam ) )
            	    // InternalSpectra.g:1249:5: (lv_params_2_0= ruleTypedParam )
            	    {
            	    // InternalSpectra.g:1249:5: (lv_params_2_0= ruleTypedParam )
            	    // InternalSpectra.g:1250:6: lv_params_2_0= ruleTypedParam
            	    {

            	    						newCompositeNode(grammarAccess.getTypedParamListAccess().getParamsTypedParamParserRuleCall_1_1_0());
            	    					
            	    pushFollow(FOLLOW_26);
            	    lv_params_2_0=ruleTypedParam();

            	    state._fsp--;


            	    						if (current==null) {
            	    							current = createModelElementForParent(grammarAccess.getTypedParamListRule());
            	    						}
            	    						add(
            	    							current,
            	    							"params",
            	    							lv_params_2_0,
            	    							"tau.smlab.syntech.Spectra.TypedParam");
            	    						afterParserOrEnumRuleCall();
            	    					

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop16;
                }
            } while (true);


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleTypedParamList"


    // $ANTLR start "entryRuleTypedParam"
    // InternalSpectra.g:1272:1: entryRuleTypedParam returns [EObject current=null] : iv_ruleTypedParam= ruleTypedParam EOF ;
    public final EObject entryRuleTypedParam() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleTypedParam = null;


        try {
            // InternalSpectra.g:1272:51: (iv_ruleTypedParam= ruleTypedParam EOF )
            // InternalSpectra.g:1273:2: iv_ruleTypedParam= ruleTypedParam EOF
            {
             newCompositeNode(grammarAccess.getTypedParamRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleTypedParam=ruleTypedParam();

            state._fsp--;

             current =iv_ruleTypedParam; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleTypedParam"


    // $ANTLR start "ruleTypedParam"
    // InternalSpectra.g:1279:1: ruleTypedParam returns [EObject current=null] : ( ( (lv_type_0_0= ruleVarType ) ) ( (lv_name_1_0= RULE_ID ) ) ) ;
    public final EObject ruleTypedParam() throws RecognitionException {
        EObject current = null;

        Token lv_name_1_0=null;
        EObject lv_type_0_0 = null;



        	enterRule();

        try {
            // InternalSpectra.g:1285:2: ( ( ( (lv_type_0_0= ruleVarType ) ) ( (lv_name_1_0= RULE_ID ) ) ) )
            // InternalSpectra.g:1286:2: ( ( (lv_type_0_0= ruleVarType ) ) ( (lv_name_1_0= RULE_ID ) ) )
            {
            // InternalSpectra.g:1286:2: ( ( (lv_type_0_0= ruleVarType ) ) ( (lv_name_1_0= RULE_ID ) ) )
            // InternalSpectra.g:1287:3: ( (lv_type_0_0= ruleVarType ) ) ( (lv_name_1_0= RULE_ID ) )
            {
            // InternalSpectra.g:1287:3: ( (lv_type_0_0= ruleVarType ) )
            // InternalSpectra.g:1288:4: (lv_type_0_0= ruleVarType )
            {
            // InternalSpectra.g:1288:4: (lv_type_0_0= ruleVarType )
            // InternalSpectra.g:1289:5: lv_type_0_0= ruleVarType
            {

            					newCompositeNode(grammarAccess.getTypedParamAccess().getTypeVarTypeParserRuleCall_0_0());
            				
            pushFollow(FOLLOW_4);
            lv_type_0_0=ruleVarType();

            state._fsp--;


            					if (current==null) {
            						current = createModelElementForParent(grammarAccess.getTypedParamRule());
            					}
            					set(
            						current,
            						"type",
            						lv_type_0_0,
            						"tau.smlab.syntech.Spectra.VarType");
            					afterParserOrEnumRuleCall();
            				

            }


            }

            // InternalSpectra.g:1306:3: ( (lv_name_1_0= RULE_ID ) )
            // InternalSpectra.g:1307:4: (lv_name_1_0= RULE_ID )
            {
            // InternalSpectra.g:1307:4: (lv_name_1_0= RULE_ID )
            // InternalSpectra.g:1308:5: lv_name_1_0= RULE_ID
            {
            lv_name_1_0=(Token)match(input,RULE_ID,FOLLOW_2); 

            					newLeafNode(lv_name_1_0, grammarAccess.getTypedParamAccess().getNameIDTerminalRuleCall_1_0());
            				

            					if (current==null) {
            						current = createModelElement(grammarAccess.getTypedParamRule());
            					}
            					setWithLastConsumed(
            						current,
            						"name",
            						lv_name_1_0,
            						"org.eclipse.xtext.common.Terminals.ID");
            				

            }


            }


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleTypedParam"


    // $ANTLR start "entryRuleCounter"
    // InternalSpectra.g:1328:1: entryRuleCounter returns [EObject current=null] : iv_ruleCounter= ruleCounter EOF ;
    public final EObject entryRuleCounter() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleCounter = null;


        try {
            // InternalSpectra.g:1328:48: (iv_ruleCounter= ruleCounter EOF )
            // InternalSpectra.g:1329:2: iv_ruleCounter= ruleCounter EOF
            {
             newCompositeNode(grammarAccess.getCounterRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleCounter=ruleCounter();

            state._fsp--;

             current =iv_ruleCounter; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleCounter"


    // $ANTLR start "ruleCounter"
    // InternalSpectra.g:1335:1: ruleCounter returns [EObject current=null] : (otherlv_0= 'counter' ( (lv_name_1_0= RULE_ID ) ) (otherlv_2= '(' ( (lv_range_3_0= ruleSubrange ) ) otherlv_4= ')' ) otherlv_5= '{' ( ( ( (lv_initial_6_0= ruleTemporalInExpr ) ) | (otherlv_7= 'reset:' ( (lv_resetPred_8_0= ruleTemporalInExpr ) ) ) | (otherlv_9= 'inc:' ( (lv_incPred_10_0= ruleTemporalExpression ) ) ) | (otherlv_11= 'dec:' ( (lv_decPred_12_0= ruleTemporalExpression ) ) ) | (otherlv_13= 'overflow:' ( (lv_overflowMethod_14_0= ruleOverflowMethod ) ) ) | (otherlv_15= 'underflow:' ( (lv_underflowMethod_16_0= ruleOverflowMethod ) ) ) ) ruleTOK_SEMI )* otherlv_18= '}' ) ;
    public final EObject ruleCounter() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_name_1_0=null;
        Token otherlv_2=null;
        Token otherlv_4=null;
        Token otherlv_5=null;
        Token otherlv_7=null;
        Token otherlv_9=null;
        Token otherlv_11=null;
        Token otherlv_13=null;
        Token otherlv_15=null;
        Token otherlv_18=null;
        EObject lv_range_3_0 = null;

        EObject lv_initial_6_0 = null;

        EObject lv_resetPred_8_0 = null;

        EObject lv_incPred_10_0 = null;

        EObject lv_decPred_12_0 = null;

        Enumerator lv_overflowMethod_14_0 = null;

        Enumerator lv_underflowMethod_16_0 = null;



        	enterRule();

        try {
            // InternalSpectra.g:1341:2: ( (otherlv_0= 'counter' ( (lv_name_1_0= RULE_ID ) ) (otherlv_2= '(' ( (lv_range_3_0= ruleSubrange ) ) otherlv_4= ')' ) otherlv_5= '{' ( ( ( (lv_initial_6_0= ruleTemporalInExpr ) ) | (otherlv_7= 'reset:' ( (lv_resetPred_8_0= ruleTemporalInExpr ) ) ) | (otherlv_9= 'inc:' ( (lv_incPred_10_0= ruleTemporalExpression ) ) ) | (otherlv_11= 'dec:' ( (lv_decPred_12_0= ruleTemporalExpression ) ) ) | (otherlv_13= 'overflow:' ( (lv_overflowMethod_14_0= ruleOverflowMethod ) ) ) | (otherlv_15= 'underflow:' ( (lv_underflowMethod_16_0= ruleOverflowMethod ) ) ) ) ruleTOK_SEMI )* otherlv_18= '}' ) )
            // InternalSpectra.g:1342:2: (otherlv_0= 'counter' ( (lv_name_1_0= RULE_ID ) ) (otherlv_2= '(' ( (lv_range_3_0= ruleSubrange ) ) otherlv_4= ')' ) otherlv_5= '{' ( ( ( (lv_initial_6_0= ruleTemporalInExpr ) ) | (otherlv_7= 'reset:' ( (lv_resetPred_8_0= ruleTemporalInExpr ) ) ) | (otherlv_9= 'inc:' ( (lv_incPred_10_0= ruleTemporalExpression ) ) ) | (otherlv_11= 'dec:' ( (lv_decPred_12_0= ruleTemporalExpression ) ) ) | (otherlv_13= 'overflow:' ( (lv_overflowMethod_14_0= ruleOverflowMethod ) ) ) | (otherlv_15= 'underflow:' ( (lv_underflowMethod_16_0= ruleOverflowMethod ) ) ) ) ruleTOK_SEMI )* otherlv_18= '}' )
            {
            // InternalSpectra.g:1342:2: (otherlv_0= 'counter' ( (lv_name_1_0= RULE_ID ) ) (otherlv_2= '(' ( (lv_range_3_0= ruleSubrange ) ) otherlv_4= ')' ) otherlv_5= '{' ( ( ( (lv_initial_6_0= ruleTemporalInExpr ) ) | (otherlv_7= 'reset:' ( (lv_resetPred_8_0= ruleTemporalInExpr ) ) ) | (otherlv_9= 'inc:' ( (lv_incPred_10_0= ruleTemporalExpression ) ) ) | (otherlv_11= 'dec:' ( (lv_decPred_12_0= ruleTemporalExpression ) ) ) | (otherlv_13= 'overflow:' ( (lv_overflowMethod_14_0= ruleOverflowMethod ) ) ) | (otherlv_15= 'underflow:' ( (lv_underflowMethod_16_0= ruleOverflowMethod ) ) ) ) ruleTOK_SEMI )* otherlv_18= '}' )
            // InternalSpectra.g:1343:3: otherlv_0= 'counter' ( (lv_name_1_0= RULE_ID ) ) (otherlv_2= '(' ( (lv_range_3_0= ruleSubrange ) ) otherlv_4= ')' ) otherlv_5= '{' ( ( ( (lv_initial_6_0= ruleTemporalInExpr ) ) | (otherlv_7= 'reset:' ( (lv_resetPred_8_0= ruleTemporalInExpr ) ) ) | (otherlv_9= 'inc:' ( (lv_incPred_10_0= ruleTemporalExpression ) ) ) | (otherlv_11= 'dec:' ( (lv_decPred_12_0= ruleTemporalExpression ) ) ) | (otherlv_13= 'overflow:' ( (lv_overflowMethod_14_0= ruleOverflowMethod ) ) ) | (otherlv_15= 'underflow:' ( (lv_underflowMethod_16_0= ruleOverflowMethod ) ) ) ) ruleTOK_SEMI )* otherlv_18= '}'
            {
            otherlv_0=(Token)match(input,27,FOLLOW_4); 

            			newLeafNode(otherlv_0, grammarAccess.getCounterAccess().getCounterKeyword_0());
            		
            // InternalSpectra.g:1347:3: ( (lv_name_1_0= RULE_ID ) )
            // InternalSpectra.g:1348:4: (lv_name_1_0= RULE_ID )
            {
            // InternalSpectra.g:1348:4: (lv_name_1_0= RULE_ID )
            // InternalSpectra.g:1349:5: lv_name_1_0= RULE_ID
            {
            lv_name_1_0=(Token)match(input,RULE_ID,FOLLOW_27); 

            					newLeafNode(lv_name_1_0, grammarAccess.getCounterAccess().getNameIDTerminalRuleCall_1_0());
            				

            					if (current==null) {
            						current = createModelElement(grammarAccess.getCounterRule());
            					}
            					setWithLastConsumed(
            						current,
            						"name",
            						lv_name_1_0,
            						"org.eclipse.xtext.common.Terminals.ID");
            				

            }


            }

            // InternalSpectra.g:1365:3: (otherlv_2= '(' ( (lv_range_3_0= ruleSubrange ) ) otherlv_4= ')' )
            // InternalSpectra.g:1366:4: otherlv_2= '(' ( (lv_range_3_0= ruleSubrange ) ) otherlv_4= ')'
            {
            otherlv_2=(Token)match(input,28,FOLLOW_20); 

            				newLeafNode(otherlv_2, grammarAccess.getCounterAccess().getLeftParenthesisKeyword_2_0());
            			
            // InternalSpectra.g:1370:4: ( (lv_range_3_0= ruleSubrange ) )
            // InternalSpectra.g:1371:5: (lv_range_3_0= ruleSubrange )
            {
            // InternalSpectra.g:1371:5: (lv_range_3_0= ruleSubrange )
            // InternalSpectra.g:1372:6: lv_range_3_0= ruleSubrange
            {

            						newCompositeNode(grammarAccess.getCounterAccess().getRangeSubrangeParserRuleCall_2_1_0());
            					
            pushFollow(FOLLOW_28);
            lv_range_3_0=ruleSubrange();

            state._fsp--;


            						if (current==null) {
            							current = createModelElementForParent(grammarAccess.getCounterRule());
            						}
            						set(
            							current,
            							"range",
            							lv_range_3_0,
            							"tau.smlab.syntech.Spectra.Subrange");
            						afterParserOrEnumRuleCall();
            					

            }


            }

            otherlv_4=(Token)match(input,29,FOLLOW_23); 

            				newLeafNode(otherlv_4, grammarAccess.getCounterAccess().getRightParenthesisKeyword_2_2());
            			

            }

            otherlv_5=(Token)match(input,23,FOLLOW_29); 

            			newLeafNode(otherlv_5, grammarAccess.getCounterAccess().getLeftCurlyBracketKeyword_3());
            		
            // InternalSpectra.g:1398:3: ( ( ( (lv_initial_6_0= ruleTemporalInExpr ) ) | (otherlv_7= 'reset:' ( (lv_resetPred_8_0= ruleTemporalInExpr ) ) ) | (otherlv_9= 'inc:' ( (lv_incPred_10_0= ruleTemporalExpression ) ) ) | (otherlv_11= 'dec:' ( (lv_decPred_12_0= ruleTemporalExpression ) ) ) | (otherlv_13= 'overflow:' ( (lv_overflowMethod_14_0= ruleOverflowMethod ) ) ) | (otherlv_15= 'underflow:' ( (lv_underflowMethod_16_0= ruleOverflowMethod ) ) ) ) ruleTOK_SEMI )*
            loop18:
            do {
                int alt18=2;
                int LA18_0 = input.LA(1);

                if ( (LA18_0==RULE_ID||LA18_0==RULE_INT||LA18_0==16||LA18_0==26||LA18_0==28||(LA18_0>=30 && LA18_0<=34)||(LA18_0>=82 && LA18_0<=89)||(LA18_0>=97 && LA18_0<=100)) ) {
                    alt18=1;
                }


                switch (alt18) {
            	case 1 :
            	    // InternalSpectra.g:1399:4: ( ( (lv_initial_6_0= ruleTemporalInExpr ) ) | (otherlv_7= 'reset:' ( (lv_resetPred_8_0= ruleTemporalInExpr ) ) ) | (otherlv_9= 'inc:' ( (lv_incPred_10_0= ruleTemporalExpression ) ) ) | (otherlv_11= 'dec:' ( (lv_decPred_12_0= ruleTemporalExpression ) ) ) | (otherlv_13= 'overflow:' ( (lv_overflowMethod_14_0= ruleOverflowMethod ) ) ) | (otherlv_15= 'underflow:' ( (lv_underflowMethod_16_0= ruleOverflowMethod ) ) ) ) ruleTOK_SEMI
            	    {
            	    // InternalSpectra.g:1399:4: ( ( (lv_initial_6_0= ruleTemporalInExpr ) ) | (otherlv_7= 'reset:' ( (lv_resetPred_8_0= ruleTemporalInExpr ) ) ) | (otherlv_9= 'inc:' ( (lv_incPred_10_0= ruleTemporalExpression ) ) ) | (otherlv_11= 'dec:' ( (lv_decPred_12_0= ruleTemporalExpression ) ) ) | (otherlv_13= 'overflow:' ( (lv_overflowMethod_14_0= ruleOverflowMethod ) ) ) | (otherlv_15= 'underflow:' ( (lv_underflowMethod_16_0= ruleOverflowMethod ) ) ) )
            	    int alt17=6;
            	    switch ( input.LA(1) ) {
            	    case RULE_ID:
            	    case RULE_INT:
            	    case 16:
            	    case 26:
            	    case 28:
            	    case 82:
            	    case 83:
            	    case 84:
            	    case 85:
            	    case 86:
            	    case 87:
            	    case 88:
            	    case 89:
            	    case 97:
            	    case 98:
            	    case 99:
            	    case 100:
            	        {
            	        alt17=1;
            	        }
            	        break;
            	    case 30:
            	        {
            	        alt17=2;
            	        }
            	        break;
            	    case 31:
            	        {
            	        alt17=3;
            	        }
            	        break;
            	    case 32:
            	        {
            	        alt17=4;
            	        }
            	        break;
            	    case 33:
            	        {
            	        alt17=5;
            	        }
            	        break;
            	    case 34:
            	        {
            	        alt17=6;
            	        }
            	        break;
            	    default:
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 17, 0, input);

            	        throw nvae;
            	    }

            	    switch (alt17) {
            	        case 1 :
            	            // InternalSpectra.g:1400:5: ( (lv_initial_6_0= ruleTemporalInExpr ) )
            	            {
            	            // InternalSpectra.g:1400:5: ( (lv_initial_6_0= ruleTemporalInExpr ) )
            	            // InternalSpectra.g:1401:6: (lv_initial_6_0= ruleTemporalInExpr )
            	            {
            	            // InternalSpectra.g:1401:6: (lv_initial_6_0= ruleTemporalInExpr )
            	            // InternalSpectra.g:1402:7: lv_initial_6_0= ruleTemporalInExpr
            	            {

            	            							newCompositeNode(grammarAccess.getCounterAccess().getInitialTemporalInExprParserRuleCall_4_0_0_0());
            	            						
            	            pushFollow(FOLLOW_13);
            	            lv_initial_6_0=ruleTemporalInExpr();

            	            state._fsp--;


            	            							if (current==null) {
            	            								current = createModelElementForParent(grammarAccess.getCounterRule());
            	            							}
            	            							add(
            	            								current,
            	            								"initial",
            	            								lv_initial_6_0,
            	            								"tau.smlab.syntech.Spectra.TemporalInExpr");
            	            							afterParserOrEnumRuleCall();
            	            						

            	            }


            	            }


            	            }
            	            break;
            	        case 2 :
            	            // InternalSpectra.g:1420:5: (otherlv_7= 'reset:' ( (lv_resetPred_8_0= ruleTemporalInExpr ) ) )
            	            {
            	            // InternalSpectra.g:1420:5: (otherlv_7= 'reset:' ( (lv_resetPred_8_0= ruleTemporalInExpr ) ) )
            	            // InternalSpectra.g:1421:6: otherlv_7= 'reset:' ( (lv_resetPred_8_0= ruleTemporalInExpr ) )
            	            {
            	            otherlv_7=(Token)match(input,30,FOLLOW_12); 

            	            						newLeafNode(otherlv_7, grammarAccess.getCounterAccess().getResetKeyword_4_0_1_0());
            	            					
            	            // InternalSpectra.g:1425:6: ( (lv_resetPred_8_0= ruleTemporalInExpr ) )
            	            // InternalSpectra.g:1426:7: (lv_resetPred_8_0= ruleTemporalInExpr )
            	            {
            	            // InternalSpectra.g:1426:7: (lv_resetPred_8_0= ruleTemporalInExpr )
            	            // InternalSpectra.g:1427:8: lv_resetPred_8_0= ruleTemporalInExpr
            	            {

            	            								newCompositeNode(grammarAccess.getCounterAccess().getResetPredTemporalInExprParserRuleCall_4_0_1_1_0());
            	            							
            	            pushFollow(FOLLOW_13);
            	            lv_resetPred_8_0=ruleTemporalInExpr();

            	            state._fsp--;


            	            								if (current==null) {
            	            									current = createModelElementForParent(grammarAccess.getCounterRule());
            	            								}
            	            								add(
            	            									current,
            	            									"resetPred",
            	            									lv_resetPred_8_0,
            	            									"tau.smlab.syntech.Spectra.TemporalInExpr");
            	            								afterParserOrEnumRuleCall();
            	            							

            	            }


            	            }


            	            }


            	            }
            	            break;
            	        case 3 :
            	            // InternalSpectra.g:1446:5: (otherlv_9= 'inc:' ( (lv_incPred_10_0= ruleTemporalExpression ) ) )
            	            {
            	            // InternalSpectra.g:1446:5: (otherlv_9= 'inc:' ( (lv_incPred_10_0= ruleTemporalExpression ) ) )
            	            // InternalSpectra.g:1447:6: otherlv_9= 'inc:' ( (lv_incPred_10_0= ruleTemporalExpression ) )
            	            {
            	            otherlv_9=(Token)match(input,31,FOLLOW_18); 

            	            						newLeafNode(otherlv_9, grammarAccess.getCounterAccess().getIncKeyword_4_0_2_0());
            	            					
            	            // InternalSpectra.g:1451:6: ( (lv_incPred_10_0= ruleTemporalExpression ) )
            	            // InternalSpectra.g:1452:7: (lv_incPred_10_0= ruleTemporalExpression )
            	            {
            	            // InternalSpectra.g:1452:7: (lv_incPred_10_0= ruleTemporalExpression )
            	            // InternalSpectra.g:1453:8: lv_incPred_10_0= ruleTemporalExpression
            	            {

            	            								newCompositeNode(grammarAccess.getCounterAccess().getIncPredTemporalExpressionParserRuleCall_4_0_2_1_0());
            	            							
            	            pushFollow(FOLLOW_13);
            	            lv_incPred_10_0=ruleTemporalExpression();

            	            state._fsp--;


            	            								if (current==null) {
            	            									current = createModelElementForParent(grammarAccess.getCounterRule());
            	            								}
            	            								add(
            	            									current,
            	            									"incPred",
            	            									lv_incPred_10_0,
            	            									"tau.smlab.syntech.Spectra.TemporalExpression");
            	            								afterParserOrEnumRuleCall();
            	            							

            	            }


            	            }


            	            }


            	            }
            	            break;
            	        case 4 :
            	            // InternalSpectra.g:1472:5: (otherlv_11= 'dec:' ( (lv_decPred_12_0= ruleTemporalExpression ) ) )
            	            {
            	            // InternalSpectra.g:1472:5: (otherlv_11= 'dec:' ( (lv_decPred_12_0= ruleTemporalExpression ) ) )
            	            // InternalSpectra.g:1473:6: otherlv_11= 'dec:' ( (lv_decPred_12_0= ruleTemporalExpression ) )
            	            {
            	            otherlv_11=(Token)match(input,32,FOLLOW_18); 

            	            						newLeafNode(otherlv_11, grammarAccess.getCounterAccess().getDecKeyword_4_0_3_0());
            	            					
            	            // InternalSpectra.g:1477:6: ( (lv_decPred_12_0= ruleTemporalExpression ) )
            	            // InternalSpectra.g:1478:7: (lv_decPred_12_0= ruleTemporalExpression )
            	            {
            	            // InternalSpectra.g:1478:7: (lv_decPred_12_0= ruleTemporalExpression )
            	            // InternalSpectra.g:1479:8: lv_decPred_12_0= ruleTemporalExpression
            	            {

            	            								newCompositeNode(grammarAccess.getCounterAccess().getDecPredTemporalExpressionParserRuleCall_4_0_3_1_0());
            	            							
            	            pushFollow(FOLLOW_13);
            	            lv_decPred_12_0=ruleTemporalExpression();

            	            state._fsp--;


            	            								if (current==null) {
            	            									current = createModelElementForParent(grammarAccess.getCounterRule());
            	            								}
            	            								add(
            	            									current,
            	            									"decPred",
            	            									lv_decPred_12_0,
            	            									"tau.smlab.syntech.Spectra.TemporalExpression");
            	            								afterParserOrEnumRuleCall();
            	            							

            	            }


            	            }


            	            }


            	            }
            	            break;
            	        case 5 :
            	            // InternalSpectra.g:1498:5: (otherlv_13= 'overflow:' ( (lv_overflowMethod_14_0= ruleOverflowMethod ) ) )
            	            {
            	            // InternalSpectra.g:1498:5: (otherlv_13= 'overflow:' ( (lv_overflowMethod_14_0= ruleOverflowMethod ) ) )
            	            // InternalSpectra.g:1499:6: otherlv_13= 'overflow:' ( (lv_overflowMethod_14_0= ruleOverflowMethod ) )
            	            {
            	            otherlv_13=(Token)match(input,33,FOLLOW_30); 

            	            						newLeafNode(otherlv_13, grammarAccess.getCounterAccess().getOverflowKeyword_4_0_4_0());
            	            					
            	            // InternalSpectra.g:1503:6: ( (lv_overflowMethod_14_0= ruleOverflowMethod ) )
            	            // InternalSpectra.g:1504:7: (lv_overflowMethod_14_0= ruleOverflowMethod )
            	            {
            	            // InternalSpectra.g:1504:7: (lv_overflowMethod_14_0= ruleOverflowMethod )
            	            // InternalSpectra.g:1505:8: lv_overflowMethod_14_0= ruleOverflowMethod
            	            {

            	            								newCompositeNode(grammarAccess.getCounterAccess().getOverflowMethodOverflowMethodEnumRuleCall_4_0_4_1_0());
            	            							
            	            pushFollow(FOLLOW_13);
            	            lv_overflowMethod_14_0=ruleOverflowMethod();

            	            state._fsp--;


            	            								if (current==null) {
            	            									current = createModelElementForParent(grammarAccess.getCounterRule());
            	            								}
            	            								add(
            	            									current,
            	            									"overflowMethod",
            	            									lv_overflowMethod_14_0,
            	            									"tau.smlab.syntech.Spectra.OverflowMethod");
            	            								afterParserOrEnumRuleCall();
            	            							

            	            }


            	            }


            	            }


            	            }
            	            break;
            	        case 6 :
            	            // InternalSpectra.g:1524:5: (otherlv_15= 'underflow:' ( (lv_underflowMethod_16_0= ruleOverflowMethod ) ) )
            	            {
            	            // InternalSpectra.g:1524:5: (otherlv_15= 'underflow:' ( (lv_underflowMethod_16_0= ruleOverflowMethod ) ) )
            	            // InternalSpectra.g:1525:6: otherlv_15= 'underflow:' ( (lv_underflowMethod_16_0= ruleOverflowMethod ) )
            	            {
            	            otherlv_15=(Token)match(input,34,FOLLOW_30); 

            	            						newLeafNode(otherlv_15, grammarAccess.getCounterAccess().getUnderflowKeyword_4_0_5_0());
            	            					
            	            // InternalSpectra.g:1529:6: ( (lv_underflowMethod_16_0= ruleOverflowMethod ) )
            	            // InternalSpectra.g:1530:7: (lv_underflowMethod_16_0= ruleOverflowMethod )
            	            {
            	            // InternalSpectra.g:1530:7: (lv_underflowMethod_16_0= ruleOverflowMethod )
            	            // InternalSpectra.g:1531:8: lv_underflowMethod_16_0= ruleOverflowMethod
            	            {

            	            								newCompositeNode(grammarAccess.getCounterAccess().getUnderflowMethodOverflowMethodEnumRuleCall_4_0_5_1_0());
            	            							
            	            pushFollow(FOLLOW_13);
            	            lv_underflowMethod_16_0=ruleOverflowMethod();

            	            state._fsp--;


            	            								if (current==null) {
            	            									current = createModelElementForParent(grammarAccess.getCounterRule());
            	            								}
            	            								add(
            	            									current,
            	            									"underflowMethod",
            	            									lv_underflowMethod_16_0,
            	            									"tau.smlab.syntech.Spectra.OverflowMethod");
            	            								afterParserOrEnumRuleCall();
            	            							

            	            }


            	            }


            	            }


            	            }
            	            break;

            	    }


            	    				newCompositeNode(grammarAccess.getCounterAccess().getTOK_SEMIParserRuleCall_4_1());
            	    			
            	    pushFollow(FOLLOW_29);
            	    ruleTOK_SEMI();

            	    state._fsp--;


            	    				afterParserOrEnumRuleCall();
            	    			

            	    }
            	    break;

            	default :
            	    break loop18;
                }
            } while (true);

            otherlv_18=(Token)match(input,25,FOLLOW_2); 

            			newLeafNode(otherlv_18, grammarAccess.getCounterAccess().getRightCurlyBracketKeyword_5());
            		

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleCounter"


    // $ANTLR start "entryRuleMonitor"
    // InternalSpectra.g:1566:1: entryRuleMonitor returns [EObject current=null] : iv_ruleMonitor= ruleMonitor EOF ;
    public final EObject entryRuleMonitor() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleMonitor = null;


        try {
            // InternalSpectra.g:1566:48: (iv_ruleMonitor= ruleMonitor EOF )
            // InternalSpectra.g:1567:2: iv_ruleMonitor= ruleMonitor EOF
            {
             newCompositeNode(grammarAccess.getMonitorRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleMonitor=ruleMonitor();

            state._fsp--;

             current =iv_ruleMonitor; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleMonitor"


    // $ANTLR start "ruleMonitor"
    // InternalSpectra.g:1573:1: ruleMonitor returns [EObject current=null] : (otherlv_0= 'monitor' ( (lv_type_1_0= ruleVarType ) ) ( (lv_name_2_0= RULE_ID ) ) otherlv_3= '{' ( ( ( (otherlv_4= 'ini' | otherlv_5= 'initially' )? ( (lv_initial_6_0= ruleTemporalInExpr ) ) ) | ( (otherlv_7= 'G' | otherlv_8= 'trans' ) ( (lv_safety_9_0= ruleTemporalInExpr ) ) ) | ( (otherlv_10= 'always' | otherlv_11= 'alw' ) ( (lv_stateInv_12_0= ruleTemporalInExpr ) ) ) ) ruleTOK_SEMI )* otherlv_14= '}' ) ;
    public final EObject ruleMonitor() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_name_2_0=null;
        Token otherlv_3=null;
        Token otherlv_4=null;
        Token otherlv_5=null;
        Token otherlv_7=null;
        Token otherlv_8=null;
        Token otherlv_10=null;
        Token otherlv_11=null;
        Token otherlv_14=null;
        EObject lv_type_1_0 = null;

        EObject lv_initial_6_0 = null;

        EObject lv_safety_9_0 = null;

        EObject lv_stateInv_12_0 = null;



        	enterRule();

        try {
            // InternalSpectra.g:1579:2: ( (otherlv_0= 'monitor' ( (lv_type_1_0= ruleVarType ) ) ( (lv_name_2_0= RULE_ID ) ) otherlv_3= '{' ( ( ( (otherlv_4= 'ini' | otherlv_5= 'initially' )? ( (lv_initial_6_0= ruleTemporalInExpr ) ) ) | ( (otherlv_7= 'G' | otherlv_8= 'trans' ) ( (lv_safety_9_0= ruleTemporalInExpr ) ) ) | ( (otherlv_10= 'always' | otherlv_11= 'alw' ) ( (lv_stateInv_12_0= ruleTemporalInExpr ) ) ) ) ruleTOK_SEMI )* otherlv_14= '}' ) )
            // InternalSpectra.g:1580:2: (otherlv_0= 'monitor' ( (lv_type_1_0= ruleVarType ) ) ( (lv_name_2_0= RULE_ID ) ) otherlv_3= '{' ( ( ( (otherlv_4= 'ini' | otherlv_5= 'initially' )? ( (lv_initial_6_0= ruleTemporalInExpr ) ) ) | ( (otherlv_7= 'G' | otherlv_8= 'trans' ) ( (lv_safety_9_0= ruleTemporalInExpr ) ) ) | ( (otherlv_10= 'always' | otherlv_11= 'alw' ) ( (lv_stateInv_12_0= ruleTemporalInExpr ) ) ) ) ruleTOK_SEMI )* otherlv_14= '}' )
            {
            // InternalSpectra.g:1580:2: (otherlv_0= 'monitor' ( (lv_type_1_0= ruleVarType ) ) ( (lv_name_2_0= RULE_ID ) ) otherlv_3= '{' ( ( ( (otherlv_4= 'ini' | otherlv_5= 'initially' )? ( (lv_initial_6_0= ruleTemporalInExpr ) ) ) | ( (otherlv_7= 'G' | otherlv_8= 'trans' ) ( (lv_safety_9_0= ruleTemporalInExpr ) ) ) | ( (otherlv_10= 'always' | otherlv_11= 'alw' ) ( (lv_stateInv_12_0= ruleTemporalInExpr ) ) ) ) ruleTOK_SEMI )* otherlv_14= '}' )
            // InternalSpectra.g:1581:3: otherlv_0= 'monitor' ( (lv_type_1_0= ruleVarType ) ) ( (lv_name_2_0= RULE_ID ) ) otherlv_3= '{' ( ( ( (otherlv_4= 'ini' | otherlv_5= 'initially' )? ( (lv_initial_6_0= ruleTemporalInExpr ) ) ) | ( (otherlv_7= 'G' | otherlv_8= 'trans' ) ( (lv_safety_9_0= ruleTemporalInExpr ) ) ) | ( (otherlv_10= 'always' | otherlv_11= 'alw' ) ( (lv_stateInv_12_0= ruleTemporalInExpr ) ) ) ) ruleTOK_SEMI )* otherlv_14= '}'
            {
            otherlv_0=(Token)match(input,35,FOLLOW_14); 

            			newLeafNode(otherlv_0, grammarAccess.getMonitorAccess().getMonitorKeyword_0());
            		
            // InternalSpectra.g:1585:3: ( (lv_type_1_0= ruleVarType ) )
            // InternalSpectra.g:1586:4: (lv_type_1_0= ruleVarType )
            {
            // InternalSpectra.g:1586:4: (lv_type_1_0= ruleVarType )
            // InternalSpectra.g:1587:5: lv_type_1_0= ruleVarType
            {

            					newCompositeNode(grammarAccess.getMonitorAccess().getTypeVarTypeParserRuleCall_1_0());
            				
            pushFollow(FOLLOW_4);
            lv_type_1_0=ruleVarType();

            state._fsp--;


            					if (current==null) {
            						current = createModelElementForParent(grammarAccess.getMonitorRule());
            					}
            					set(
            						current,
            						"type",
            						lv_type_1_0,
            						"tau.smlab.syntech.Spectra.VarType");
            					afterParserOrEnumRuleCall();
            				

            }


            }

            // InternalSpectra.g:1604:3: ( (lv_name_2_0= RULE_ID ) )
            // InternalSpectra.g:1605:4: (lv_name_2_0= RULE_ID )
            {
            // InternalSpectra.g:1605:4: (lv_name_2_0= RULE_ID )
            // InternalSpectra.g:1606:5: lv_name_2_0= RULE_ID
            {
            lv_name_2_0=(Token)match(input,RULE_ID,FOLLOW_23); 

            					newLeafNode(lv_name_2_0, grammarAccess.getMonitorAccess().getNameIDTerminalRuleCall_2_0());
            				

            					if (current==null) {
            						current = createModelElement(grammarAccess.getMonitorRule());
            					}
            					setWithLastConsumed(
            						current,
            						"name",
            						lv_name_2_0,
            						"org.eclipse.xtext.common.Terminals.ID");
            				

            }


            }

            otherlv_3=(Token)match(input,23,FOLLOW_31); 

            			newLeafNode(otherlv_3, grammarAccess.getMonitorAccess().getLeftCurlyBracketKeyword_3());
            		
            // InternalSpectra.g:1626:3: ( ( ( (otherlv_4= 'ini' | otherlv_5= 'initially' )? ( (lv_initial_6_0= ruleTemporalInExpr ) ) ) | ( (otherlv_7= 'G' | otherlv_8= 'trans' ) ( (lv_safety_9_0= ruleTemporalInExpr ) ) ) | ( (otherlv_10= 'always' | otherlv_11= 'alw' ) ( (lv_stateInv_12_0= ruleTemporalInExpr ) ) ) ) ruleTOK_SEMI )*
            loop23:
            do {
                int alt23=2;
                int LA23_0 = input.LA(1);

                if ( (LA23_0==RULE_ID||LA23_0==RULE_INT||LA23_0==16||LA23_0==26||LA23_0==28||(LA23_0>=36 && LA23_0<=41)||(LA23_0>=82 && LA23_0<=89)||(LA23_0>=97 && LA23_0<=100)) ) {
                    alt23=1;
                }


                switch (alt23) {
            	case 1 :
            	    // InternalSpectra.g:1627:4: ( ( (otherlv_4= 'ini' | otherlv_5= 'initially' )? ( (lv_initial_6_0= ruleTemporalInExpr ) ) ) | ( (otherlv_7= 'G' | otherlv_8= 'trans' ) ( (lv_safety_9_0= ruleTemporalInExpr ) ) ) | ( (otherlv_10= 'always' | otherlv_11= 'alw' ) ( (lv_stateInv_12_0= ruleTemporalInExpr ) ) ) ) ruleTOK_SEMI
            	    {
            	    // InternalSpectra.g:1627:4: ( ( (otherlv_4= 'ini' | otherlv_5= 'initially' )? ( (lv_initial_6_0= ruleTemporalInExpr ) ) ) | ( (otherlv_7= 'G' | otherlv_8= 'trans' ) ( (lv_safety_9_0= ruleTemporalInExpr ) ) ) | ( (otherlv_10= 'always' | otherlv_11= 'alw' ) ( (lv_stateInv_12_0= ruleTemporalInExpr ) ) ) )
            	    int alt22=3;
            	    switch ( input.LA(1) ) {
            	    case RULE_ID:
            	    case RULE_INT:
            	    case 16:
            	    case 26:
            	    case 28:
            	    case 36:
            	    case 37:
            	    case 82:
            	    case 83:
            	    case 84:
            	    case 85:
            	    case 86:
            	    case 87:
            	    case 88:
            	    case 89:
            	    case 97:
            	    case 98:
            	    case 99:
            	    case 100:
            	        {
            	        alt22=1;
            	        }
            	        break;
            	    case 38:
            	    case 39:
            	        {
            	        alt22=2;
            	        }
            	        break;
            	    case 40:
            	    case 41:
            	        {
            	        alt22=3;
            	        }
            	        break;
            	    default:
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 22, 0, input);

            	        throw nvae;
            	    }

            	    switch (alt22) {
            	        case 1 :
            	            // InternalSpectra.g:1628:5: ( (otherlv_4= 'ini' | otherlv_5= 'initially' )? ( (lv_initial_6_0= ruleTemporalInExpr ) ) )
            	            {
            	            // InternalSpectra.g:1628:5: ( (otherlv_4= 'ini' | otherlv_5= 'initially' )? ( (lv_initial_6_0= ruleTemporalInExpr ) ) )
            	            // InternalSpectra.g:1629:6: (otherlv_4= 'ini' | otherlv_5= 'initially' )? ( (lv_initial_6_0= ruleTemporalInExpr ) )
            	            {
            	            // InternalSpectra.g:1629:6: (otherlv_4= 'ini' | otherlv_5= 'initially' )?
            	            int alt19=3;
            	            int LA19_0 = input.LA(1);

            	            if ( (LA19_0==36) ) {
            	                alt19=1;
            	            }
            	            else if ( (LA19_0==37) ) {
            	                alt19=2;
            	            }
            	            switch (alt19) {
            	                case 1 :
            	                    // InternalSpectra.g:1630:7: otherlv_4= 'ini'
            	                    {
            	                    otherlv_4=(Token)match(input,36,FOLLOW_12); 

            	                    							newLeafNode(otherlv_4, grammarAccess.getMonitorAccess().getIniKeyword_4_0_0_0_0());
            	                    						

            	                    }
            	                    break;
            	                case 2 :
            	                    // InternalSpectra.g:1635:7: otherlv_5= 'initially'
            	                    {
            	                    otherlv_5=(Token)match(input,37,FOLLOW_12); 

            	                    							newLeafNode(otherlv_5, grammarAccess.getMonitorAccess().getInitiallyKeyword_4_0_0_0_1());
            	                    						

            	                    }
            	                    break;

            	            }

            	            // InternalSpectra.g:1640:6: ( (lv_initial_6_0= ruleTemporalInExpr ) )
            	            // InternalSpectra.g:1641:7: (lv_initial_6_0= ruleTemporalInExpr )
            	            {
            	            // InternalSpectra.g:1641:7: (lv_initial_6_0= ruleTemporalInExpr )
            	            // InternalSpectra.g:1642:8: lv_initial_6_0= ruleTemporalInExpr
            	            {

            	            								newCompositeNode(grammarAccess.getMonitorAccess().getInitialTemporalInExprParserRuleCall_4_0_0_1_0());
            	            							
            	            pushFollow(FOLLOW_13);
            	            lv_initial_6_0=ruleTemporalInExpr();

            	            state._fsp--;


            	            								if (current==null) {
            	            									current = createModelElementForParent(grammarAccess.getMonitorRule());
            	            								}
            	            								add(
            	            									current,
            	            									"initial",
            	            									lv_initial_6_0,
            	            									"tau.smlab.syntech.Spectra.TemporalInExpr");
            	            								afterParserOrEnumRuleCall();
            	            							

            	            }


            	            }


            	            }


            	            }
            	            break;
            	        case 2 :
            	            // InternalSpectra.g:1661:5: ( (otherlv_7= 'G' | otherlv_8= 'trans' ) ( (lv_safety_9_0= ruleTemporalInExpr ) ) )
            	            {
            	            // InternalSpectra.g:1661:5: ( (otherlv_7= 'G' | otherlv_8= 'trans' ) ( (lv_safety_9_0= ruleTemporalInExpr ) ) )
            	            // InternalSpectra.g:1662:6: (otherlv_7= 'G' | otherlv_8= 'trans' ) ( (lv_safety_9_0= ruleTemporalInExpr ) )
            	            {
            	            // InternalSpectra.g:1662:6: (otherlv_7= 'G' | otherlv_8= 'trans' )
            	            int alt20=2;
            	            int LA20_0 = input.LA(1);

            	            if ( (LA20_0==38) ) {
            	                alt20=1;
            	            }
            	            else if ( (LA20_0==39) ) {
            	                alt20=2;
            	            }
            	            else {
            	                NoViableAltException nvae =
            	                    new NoViableAltException("", 20, 0, input);

            	                throw nvae;
            	            }
            	            switch (alt20) {
            	                case 1 :
            	                    // InternalSpectra.g:1663:7: otherlv_7= 'G'
            	                    {
            	                    otherlv_7=(Token)match(input,38,FOLLOW_12); 

            	                    							newLeafNode(otherlv_7, grammarAccess.getMonitorAccess().getGKeyword_4_0_1_0_0());
            	                    						

            	                    }
            	                    break;
            	                case 2 :
            	                    // InternalSpectra.g:1668:7: otherlv_8= 'trans'
            	                    {
            	                    otherlv_8=(Token)match(input,39,FOLLOW_12); 

            	                    							newLeafNode(otherlv_8, grammarAccess.getMonitorAccess().getTransKeyword_4_0_1_0_1());
            	                    						

            	                    }
            	                    break;

            	            }

            	            // InternalSpectra.g:1673:6: ( (lv_safety_9_0= ruleTemporalInExpr ) )
            	            // InternalSpectra.g:1674:7: (lv_safety_9_0= ruleTemporalInExpr )
            	            {
            	            // InternalSpectra.g:1674:7: (lv_safety_9_0= ruleTemporalInExpr )
            	            // InternalSpectra.g:1675:8: lv_safety_9_0= ruleTemporalInExpr
            	            {

            	            								newCompositeNode(grammarAccess.getMonitorAccess().getSafetyTemporalInExprParserRuleCall_4_0_1_1_0());
            	            							
            	            pushFollow(FOLLOW_13);
            	            lv_safety_9_0=ruleTemporalInExpr();

            	            state._fsp--;


            	            								if (current==null) {
            	            									current = createModelElementForParent(grammarAccess.getMonitorRule());
            	            								}
            	            								add(
            	            									current,
            	            									"safety",
            	            									lv_safety_9_0,
            	            									"tau.smlab.syntech.Spectra.TemporalInExpr");
            	            								afterParserOrEnumRuleCall();
            	            							

            	            }


            	            }


            	            }


            	            }
            	            break;
            	        case 3 :
            	            // InternalSpectra.g:1694:5: ( (otherlv_10= 'always' | otherlv_11= 'alw' ) ( (lv_stateInv_12_0= ruleTemporalInExpr ) ) )
            	            {
            	            // InternalSpectra.g:1694:5: ( (otherlv_10= 'always' | otherlv_11= 'alw' ) ( (lv_stateInv_12_0= ruleTemporalInExpr ) ) )
            	            // InternalSpectra.g:1695:6: (otherlv_10= 'always' | otherlv_11= 'alw' ) ( (lv_stateInv_12_0= ruleTemporalInExpr ) )
            	            {
            	            // InternalSpectra.g:1695:6: (otherlv_10= 'always' | otherlv_11= 'alw' )
            	            int alt21=2;
            	            int LA21_0 = input.LA(1);

            	            if ( (LA21_0==40) ) {
            	                alt21=1;
            	            }
            	            else if ( (LA21_0==41) ) {
            	                alt21=2;
            	            }
            	            else {
            	                NoViableAltException nvae =
            	                    new NoViableAltException("", 21, 0, input);

            	                throw nvae;
            	            }
            	            switch (alt21) {
            	                case 1 :
            	                    // InternalSpectra.g:1696:7: otherlv_10= 'always'
            	                    {
            	                    otherlv_10=(Token)match(input,40,FOLLOW_12); 

            	                    							newLeafNode(otherlv_10, grammarAccess.getMonitorAccess().getAlwaysKeyword_4_0_2_0_0());
            	                    						

            	                    }
            	                    break;
            	                case 2 :
            	                    // InternalSpectra.g:1701:7: otherlv_11= 'alw'
            	                    {
            	                    otherlv_11=(Token)match(input,41,FOLLOW_12); 

            	                    							newLeafNode(otherlv_11, grammarAccess.getMonitorAccess().getAlwKeyword_4_0_2_0_1());
            	                    						

            	                    }
            	                    break;

            	            }

            	            // InternalSpectra.g:1706:6: ( (lv_stateInv_12_0= ruleTemporalInExpr ) )
            	            // InternalSpectra.g:1707:7: (lv_stateInv_12_0= ruleTemporalInExpr )
            	            {
            	            // InternalSpectra.g:1707:7: (lv_stateInv_12_0= ruleTemporalInExpr )
            	            // InternalSpectra.g:1708:8: lv_stateInv_12_0= ruleTemporalInExpr
            	            {

            	            								newCompositeNode(grammarAccess.getMonitorAccess().getStateInvTemporalInExprParserRuleCall_4_0_2_1_0());
            	            							
            	            pushFollow(FOLLOW_13);
            	            lv_stateInv_12_0=ruleTemporalInExpr();

            	            state._fsp--;


            	            								if (current==null) {
            	            									current = createModelElementForParent(grammarAccess.getMonitorRule());
            	            								}
            	            								add(
            	            									current,
            	            									"stateInv",
            	            									lv_stateInv_12_0,
            	            									"tau.smlab.syntech.Spectra.TemporalInExpr");
            	            								afterParserOrEnumRuleCall();
            	            							

            	            }


            	            }


            	            }


            	            }
            	            break;

            	    }


            	    				newCompositeNode(grammarAccess.getMonitorAccess().getTOK_SEMIParserRuleCall_4_1());
            	    			
            	    pushFollow(FOLLOW_31);
            	    ruleTOK_SEMI();

            	    state._fsp--;


            	    				afterParserOrEnumRuleCall();
            	    			

            	    }
            	    break;

            	default :
            	    break loop23;
                }
            } while (true);

            otherlv_14=(Token)match(input,25,FOLLOW_2); 

            			newLeafNode(otherlv_14, grammarAccess.getMonitorAccess().getRightCurlyBracketKeyword_5());
            		

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleMonitor"


    // $ANTLR start "entryRulePattern"
    // InternalSpectra.g:1743:1: entryRulePattern returns [EObject current=null] : iv_rulePattern= rulePattern EOF ;
    public final EObject entryRulePattern() throws RecognitionException {
        EObject current = null;

        EObject iv_rulePattern = null;


        try {
            // InternalSpectra.g:1743:48: (iv_rulePattern= rulePattern EOF )
            // InternalSpectra.g:1744:2: iv_rulePattern= rulePattern EOF
            {
             newCompositeNode(grammarAccess.getPatternRule()); 
            pushFollow(FOLLOW_1);
            iv_rulePattern=rulePattern();

            state._fsp--;

             current =iv_rulePattern; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRulePattern"


    // $ANTLR start "rulePattern"
    // InternalSpectra.g:1750:1: rulePattern returns [EObject current=null] : (otherlv_0= 'pattern' ( (lv_name_1_0= RULE_ID ) ) (otherlv_2= '(' ( (lv_params_3_0= rulePatternParamList ) ) otherlv_4= ')' )? (otherlv_5= '{' (otherlv_6= 'var' ( (lv_varDeclList_7_0= ruleVarDecl ) ) )* ( ( ( (otherlv_8= 'ini' | otherlv_9= 'initially' )? ( (lv_initial_10_0= ruleTemporalInExpr ) ) ) | ( (otherlv_11= 'G' | otherlv_12= 'trans' ) ( (lv_safety_13_0= ruleTemporalInExpr ) ) ) | ( (otherlv_14= 'always' | otherlv_15= 'alw' ) ( (lv_stateInv_16_0= ruleTemporalInExpr ) ) ) | ( (otherlv_17= 'GF' | otherlv_18= 'alwEv' | otherlv_19= 'alwaysEventually' ) ( (lv_justice_20_0= ruleTemporalInExpr ) ) ) ) ruleTOK_SEMI )+ otherlv_22= '}' ) ) ;
    public final EObject rulePattern() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_name_1_0=null;
        Token otherlv_2=null;
        Token otherlv_4=null;
        Token otherlv_5=null;
        Token otherlv_6=null;
        Token otherlv_8=null;
        Token otherlv_9=null;
        Token otherlv_11=null;
        Token otherlv_12=null;
        Token otherlv_14=null;
        Token otherlv_15=null;
        Token otherlv_17=null;
        Token otherlv_18=null;
        Token otherlv_19=null;
        Token otherlv_22=null;
        EObject lv_params_3_0 = null;

        EObject lv_varDeclList_7_0 = null;

        EObject lv_initial_10_0 = null;

        EObject lv_safety_13_0 = null;

        EObject lv_stateInv_16_0 = null;

        EObject lv_justice_20_0 = null;



        	enterRule();

        try {
            // InternalSpectra.g:1756:2: ( (otherlv_0= 'pattern' ( (lv_name_1_0= RULE_ID ) ) (otherlv_2= '(' ( (lv_params_3_0= rulePatternParamList ) ) otherlv_4= ')' )? (otherlv_5= '{' (otherlv_6= 'var' ( (lv_varDeclList_7_0= ruleVarDecl ) ) )* ( ( ( (otherlv_8= 'ini' | otherlv_9= 'initially' )? ( (lv_initial_10_0= ruleTemporalInExpr ) ) ) | ( (otherlv_11= 'G' | otherlv_12= 'trans' ) ( (lv_safety_13_0= ruleTemporalInExpr ) ) ) | ( (otherlv_14= 'always' | otherlv_15= 'alw' ) ( (lv_stateInv_16_0= ruleTemporalInExpr ) ) ) | ( (otherlv_17= 'GF' | otherlv_18= 'alwEv' | otherlv_19= 'alwaysEventually' ) ( (lv_justice_20_0= ruleTemporalInExpr ) ) ) ) ruleTOK_SEMI )+ otherlv_22= '}' ) ) )
            // InternalSpectra.g:1757:2: (otherlv_0= 'pattern' ( (lv_name_1_0= RULE_ID ) ) (otherlv_2= '(' ( (lv_params_3_0= rulePatternParamList ) ) otherlv_4= ')' )? (otherlv_5= '{' (otherlv_6= 'var' ( (lv_varDeclList_7_0= ruleVarDecl ) ) )* ( ( ( (otherlv_8= 'ini' | otherlv_9= 'initially' )? ( (lv_initial_10_0= ruleTemporalInExpr ) ) ) | ( (otherlv_11= 'G' | otherlv_12= 'trans' ) ( (lv_safety_13_0= ruleTemporalInExpr ) ) ) | ( (otherlv_14= 'always' | otherlv_15= 'alw' ) ( (lv_stateInv_16_0= ruleTemporalInExpr ) ) ) | ( (otherlv_17= 'GF' | otherlv_18= 'alwEv' | otherlv_19= 'alwaysEventually' ) ( (lv_justice_20_0= ruleTemporalInExpr ) ) ) ) ruleTOK_SEMI )+ otherlv_22= '}' ) )
            {
            // InternalSpectra.g:1757:2: (otherlv_0= 'pattern' ( (lv_name_1_0= RULE_ID ) ) (otherlv_2= '(' ( (lv_params_3_0= rulePatternParamList ) ) otherlv_4= ')' )? (otherlv_5= '{' (otherlv_6= 'var' ( (lv_varDeclList_7_0= ruleVarDecl ) ) )* ( ( ( (otherlv_8= 'ini' | otherlv_9= 'initially' )? ( (lv_initial_10_0= ruleTemporalInExpr ) ) ) | ( (otherlv_11= 'G' | otherlv_12= 'trans' ) ( (lv_safety_13_0= ruleTemporalInExpr ) ) ) | ( (otherlv_14= 'always' | otherlv_15= 'alw' ) ( (lv_stateInv_16_0= ruleTemporalInExpr ) ) ) | ( (otherlv_17= 'GF' | otherlv_18= 'alwEv' | otherlv_19= 'alwaysEventually' ) ( (lv_justice_20_0= ruleTemporalInExpr ) ) ) ) ruleTOK_SEMI )+ otherlv_22= '}' ) )
            // InternalSpectra.g:1758:3: otherlv_0= 'pattern' ( (lv_name_1_0= RULE_ID ) ) (otherlv_2= '(' ( (lv_params_3_0= rulePatternParamList ) ) otherlv_4= ')' )? (otherlv_5= '{' (otherlv_6= 'var' ( (lv_varDeclList_7_0= ruleVarDecl ) ) )* ( ( ( (otherlv_8= 'ini' | otherlv_9= 'initially' )? ( (lv_initial_10_0= ruleTemporalInExpr ) ) ) | ( (otherlv_11= 'G' | otherlv_12= 'trans' ) ( (lv_safety_13_0= ruleTemporalInExpr ) ) ) | ( (otherlv_14= 'always' | otherlv_15= 'alw' ) ( (lv_stateInv_16_0= ruleTemporalInExpr ) ) ) | ( (otherlv_17= 'GF' | otherlv_18= 'alwEv' | otherlv_19= 'alwaysEventually' ) ( (lv_justice_20_0= ruleTemporalInExpr ) ) ) ) ruleTOK_SEMI )+ otherlv_22= '}' )
            {
            otherlv_0=(Token)match(input,42,FOLLOW_4); 

            			newLeafNode(otherlv_0, grammarAccess.getPatternAccess().getPatternKeyword_0());
            		
            // InternalSpectra.g:1762:3: ( (lv_name_1_0= RULE_ID ) )
            // InternalSpectra.g:1763:4: (lv_name_1_0= RULE_ID )
            {
            // InternalSpectra.g:1763:4: (lv_name_1_0= RULE_ID )
            // InternalSpectra.g:1764:5: lv_name_1_0= RULE_ID
            {
            lv_name_1_0=(Token)match(input,RULE_ID,FOLLOW_32); 

            					newLeafNode(lv_name_1_0, grammarAccess.getPatternAccess().getNameIDTerminalRuleCall_1_0());
            				

            					if (current==null) {
            						current = createModelElement(grammarAccess.getPatternRule());
            					}
            					setWithLastConsumed(
            						current,
            						"name",
            						lv_name_1_0,
            						"org.eclipse.xtext.common.Terminals.ID");
            				

            }


            }

            // InternalSpectra.g:1780:3: (otherlv_2= '(' ( (lv_params_3_0= rulePatternParamList ) ) otherlv_4= ')' )?
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( (LA24_0==28) ) {
                alt24=1;
            }
            switch (alt24) {
                case 1 :
                    // InternalSpectra.g:1781:4: otherlv_2= '(' ( (lv_params_3_0= rulePatternParamList ) ) otherlv_4= ')'
                    {
                    otherlv_2=(Token)match(input,28,FOLLOW_4); 

                    				newLeafNode(otherlv_2, grammarAccess.getPatternAccess().getLeftParenthesisKeyword_2_0());
                    			
                    // InternalSpectra.g:1785:4: ( (lv_params_3_0= rulePatternParamList ) )
                    // InternalSpectra.g:1786:5: (lv_params_3_0= rulePatternParamList )
                    {
                    // InternalSpectra.g:1786:5: (lv_params_3_0= rulePatternParamList )
                    // InternalSpectra.g:1787:6: lv_params_3_0= rulePatternParamList
                    {

                    						newCompositeNode(grammarAccess.getPatternAccess().getParamsPatternParamListParserRuleCall_2_1_0());
                    					
                    pushFollow(FOLLOW_28);
                    lv_params_3_0=rulePatternParamList();

                    state._fsp--;


                    						if (current==null) {
                    							current = createModelElementForParent(grammarAccess.getPatternRule());
                    						}
                    						set(
                    							current,
                    							"params",
                    							lv_params_3_0,
                    							"tau.smlab.syntech.Spectra.PatternParamList");
                    						afterParserOrEnumRuleCall();
                    					

                    }


                    }

                    otherlv_4=(Token)match(input,29,FOLLOW_23); 

                    				newLeafNode(otherlv_4, grammarAccess.getPatternAccess().getRightParenthesisKeyword_2_2());
                    			

                    }
                    break;

            }

            // InternalSpectra.g:1809:3: (otherlv_5= '{' (otherlv_6= 'var' ( (lv_varDeclList_7_0= ruleVarDecl ) ) )* ( ( ( (otherlv_8= 'ini' | otherlv_9= 'initially' )? ( (lv_initial_10_0= ruleTemporalInExpr ) ) ) | ( (otherlv_11= 'G' | otherlv_12= 'trans' ) ( (lv_safety_13_0= ruleTemporalInExpr ) ) ) | ( (otherlv_14= 'always' | otherlv_15= 'alw' ) ( (lv_stateInv_16_0= ruleTemporalInExpr ) ) ) | ( (otherlv_17= 'GF' | otherlv_18= 'alwEv' | otherlv_19= 'alwaysEventually' ) ( (lv_justice_20_0= ruleTemporalInExpr ) ) ) ) ruleTOK_SEMI )+ otherlv_22= '}' )
            // InternalSpectra.g:1810:4: otherlv_5= '{' (otherlv_6= 'var' ( (lv_varDeclList_7_0= ruleVarDecl ) ) )* ( ( ( (otherlv_8= 'ini' | otherlv_9= 'initially' )? ( (lv_initial_10_0= ruleTemporalInExpr ) ) ) | ( (otherlv_11= 'G' | otherlv_12= 'trans' ) ( (lv_safety_13_0= ruleTemporalInExpr ) ) ) | ( (otherlv_14= 'always' | otherlv_15= 'alw' ) ( (lv_stateInv_16_0= ruleTemporalInExpr ) ) ) | ( (otherlv_17= 'GF' | otherlv_18= 'alwEv' | otherlv_19= 'alwaysEventually' ) ( (lv_justice_20_0= ruleTemporalInExpr ) ) ) ) ruleTOK_SEMI )+ otherlv_22= '}'
            {
            otherlv_5=(Token)match(input,23,FOLLOW_33); 

            				newLeafNode(otherlv_5, grammarAccess.getPatternAccess().getLeftCurlyBracketKeyword_3_0());
            			
            // InternalSpectra.g:1814:4: (otherlv_6= 'var' ( (lv_varDeclList_7_0= ruleVarDecl ) ) )*
            loop25:
            do {
                int alt25=2;
                int LA25_0 = input.LA(1);

                if ( (LA25_0==43) ) {
                    alt25=1;
                }


                switch (alt25) {
            	case 1 :
            	    // InternalSpectra.g:1815:5: otherlv_6= 'var' ( (lv_varDeclList_7_0= ruleVarDecl ) )
            	    {
            	    otherlv_6=(Token)match(input,43,FOLLOW_14); 

            	    					newLeafNode(otherlv_6, grammarAccess.getPatternAccess().getVarKeyword_3_1_0());
            	    				
            	    // InternalSpectra.g:1819:5: ( (lv_varDeclList_7_0= ruleVarDecl ) )
            	    // InternalSpectra.g:1820:6: (lv_varDeclList_7_0= ruleVarDecl )
            	    {
            	    // InternalSpectra.g:1820:6: (lv_varDeclList_7_0= ruleVarDecl )
            	    // InternalSpectra.g:1821:7: lv_varDeclList_7_0= ruleVarDecl
            	    {

            	    							newCompositeNode(grammarAccess.getPatternAccess().getVarDeclListVarDeclParserRuleCall_3_1_1_0());
            	    						
            	    pushFollow(FOLLOW_33);
            	    lv_varDeclList_7_0=ruleVarDecl();

            	    state._fsp--;


            	    							if (current==null) {
            	    								current = createModelElementForParent(grammarAccess.getPatternRule());
            	    							}
            	    							add(
            	    								current,
            	    								"varDeclList",
            	    								lv_varDeclList_7_0,
            	    								"tau.smlab.syntech.Spectra.VarDecl");
            	    							afterParserOrEnumRuleCall();
            	    						

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop25;
                }
            } while (true);

            // InternalSpectra.g:1839:4: ( ( ( (otherlv_8= 'ini' | otherlv_9= 'initially' )? ( (lv_initial_10_0= ruleTemporalInExpr ) ) ) | ( (otherlv_11= 'G' | otherlv_12= 'trans' ) ( (lv_safety_13_0= ruleTemporalInExpr ) ) ) | ( (otherlv_14= 'always' | otherlv_15= 'alw' ) ( (lv_stateInv_16_0= ruleTemporalInExpr ) ) ) | ( (otherlv_17= 'GF' | otherlv_18= 'alwEv' | otherlv_19= 'alwaysEventually' ) ( (lv_justice_20_0= ruleTemporalInExpr ) ) ) ) ruleTOK_SEMI )+
            int cnt31=0;
            loop31:
            do {
                int alt31=2;
                int LA31_0 = input.LA(1);

                if ( (LA31_0==RULE_ID||LA31_0==RULE_INT||LA31_0==16||LA31_0==26||LA31_0==28||(LA31_0>=36 && LA31_0<=41)||(LA31_0>=44 && LA31_0<=46)||(LA31_0>=82 && LA31_0<=89)||(LA31_0>=97 && LA31_0<=100)) ) {
                    alt31=1;
                }


                switch (alt31) {
            	case 1 :
            	    // InternalSpectra.g:1840:5: ( ( (otherlv_8= 'ini' | otherlv_9= 'initially' )? ( (lv_initial_10_0= ruleTemporalInExpr ) ) ) | ( (otherlv_11= 'G' | otherlv_12= 'trans' ) ( (lv_safety_13_0= ruleTemporalInExpr ) ) ) | ( (otherlv_14= 'always' | otherlv_15= 'alw' ) ( (lv_stateInv_16_0= ruleTemporalInExpr ) ) ) | ( (otherlv_17= 'GF' | otherlv_18= 'alwEv' | otherlv_19= 'alwaysEventually' ) ( (lv_justice_20_0= ruleTemporalInExpr ) ) ) ) ruleTOK_SEMI
            	    {
            	    // InternalSpectra.g:1840:5: ( ( (otherlv_8= 'ini' | otherlv_9= 'initially' )? ( (lv_initial_10_0= ruleTemporalInExpr ) ) ) | ( (otherlv_11= 'G' | otherlv_12= 'trans' ) ( (lv_safety_13_0= ruleTemporalInExpr ) ) ) | ( (otherlv_14= 'always' | otherlv_15= 'alw' ) ( (lv_stateInv_16_0= ruleTemporalInExpr ) ) ) | ( (otherlv_17= 'GF' | otherlv_18= 'alwEv' | otherlv_19= 'alwaysEventually' ) ( (lv_justice_20_0= ruleTemporalInExpr ) ) ) )
            	    int alt30=4;
            	    switch ( input.LA(1) ) {
            	    case RULE_ID:
            	    case RULE_INT:
            	    case 16:
            	    case 26:
            	    case 28:
            	    case 36:
            	    case 37:
            	    case 82:
            	    case 83:
            	    case 84:
            	    case 85:
            	    case 86:
            	    case 87:
            	    case 88:
            	    case 89:
            	    case 97:
            	    case 98:
            	    case 99:
            	    case 100:
            	        {
            	        alt30=1;
            	        }
            	        break;
            	    case 38:
            	    case 39:
            	        {
            	        alt30=2;
            	        }
            	        break;
            	    case 40:
            	    case 41:
            	        {
            	        alt30=3;
            	        }
            	        break;
            	    case 44:
            	    case 45:
            	    case 46:
            	        {
            	        alt30=4;
            	        }
            	        break;
            	    default:
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 30, 0, input);

            	        throw nvae;
            	    }

            	    switch (alt30) {
            	        case 1 :
            	            // InternalSpectra.g:1841:6: ( (otherlv_8= 'ini' | otherlv_9= 'initially' )? ( (lv_initial_10_0= ruleTemporalInExpr ) ) )
            	            {
            	            // InternalSpectra.g:1841:6: ( (otherlv_8= 'ini' | otherlv_9= 'initially' )? ( (lv_initial_10_0= ruleTemporalInExpr ) ) )
            	            // InternalSpectra.g:1842:7: (otherlv_8= 'ini' | otherlv_9= 'initially' )? ( (lv_initial_10_0= ruleTemporalInExpr ) )
            	            {
            	            // InternalSpectra.g:1842:7: (otherlv_8= 'ini' | otherlv_9= 'initially' )?
            	            int alt26=3;
            	            int LA26_0 = input.LA(1);

            	            if ( (LA26_0==36) ) {
            	                alt26=1;
            	            }
            	            else if ( (LA26_0==37) ) {
            	                alt26=2;
            	            }
            	            switch (alt26) {
            	                case 1 :
            	                    // InternalSpectra.g:1843:8: otherlv_8= 'ini'
            	                    {
            	                    otherlv_8=(Token)match(input,36,FOLLOW_12); 

            	                    								newLeafNode(otherlv_8, grammarAccess.getPatternAccess().getIniKeyword_3_2_0_0_0_0());
            	                    							

            	                    }
            	                    break;
            	                case 2 :
            	                    // InternalSpectra.g:1848:8: otherlv_9= 'initially'
            	                    {
            	                    otherlv_9=(Token)match(input,37,FOLLOW_12); 

            	                    								newLeafNode(otherlv_9, grammarAccess.getPatternAccess().getInitiallyKeyword_3_2_0_0_0_1());
            	                    							

            	                    }
            	                    break;

            	            }

            	            // InternalSpectra.g:1853:7: ( (lv_initial_10_0= ruleTemporalInExpr ) )
            	            // InternalSpectra.g:1854:8: (lv_initial_10_0= ruleTemporalInExpr )
            	            {
            	            // InternalSpectra.g:1854:8: (lv_initial_10_0= ruleTemporalInExpr )
            	            // InternalSpectra.g:1855:9: lv_initial_10_0= ruleTemporalInExpr
            	            {

            	            									newCompositeNode(grammarAccess.getPatternAccess().getInitialTemporalInExprParserRuleCall_3_2_0_0_1_0());
            	            								
            	            pushFollow(FOLLOW_13);
            	            lv_initial_10_0=ruleTemporalInExpr();

            	            state._fsp--;


            	            									if (current==null) {
            	            										current = createModelElementForParent(grammarAccess.getPatternRule());
            	            									}
            	            									add(
            	            										current,
            	            										"initial",
            	            										lv_initial_10_0,
            	            										"tau.smlab.syntech.Spectra.TemporalInExpr");
            	            									afterParserOrEnumRuleCall();
            	            								

            	            }


            	            }


            	            }


            	            }
            	            break;
            	        case 2 :
            	            // InternalSpectra.g:1874:6: ( (otherlv_11= 'G' | otherlv_12= 'trans' ) ( (lv_safety_13_0= ruleTemporalInExpr ) ) )
            	            {
            	            // InternalSpectra.g:1874:6: ( (otherlv_11= 'G' | otherlv_12= 'trans' ) ( (lv_safety_13_0= ruleTemporalInExpr ) ) )
            	            // InternalSpectra.g:1875:7: (otherlv_11= 'G' | otherlv_12= 'trans' ) ( (lv_safety_13_0= ruleTemporalInExpr ) )
            	            {
            	            // InternalSpectra.g:1875:7: (otherlv_11= 'G' | otherlv_12= 'trans' )
            	            int alt27=2;
            	            int LA27_0 = input.LA(1);

            	            if ( (LA27_0==38) ) {
            	                alt27=1;
            	            }
            	            else if ( (LA27_0==39) ) {
            	                alt27=2;
            	            }
            	            else {
            	                NoViableAltException nvae =
            	                    new NoViableAltException("", 27, 0, input);

            	                throw nvae;
            	            }
            	            switch (alt27) {
            	                case 1 :
            	                    // InternalSpectra.g:1876:8: otherlv_11= 'G'
            	                    {
            	                    otherlv_11=(Token)match(input,38,FOLLOW_12); 

            	                    								newLeafNode(otherlv_11, grammarAccess.getPatternAccess().getGKeyword_3_2_0_1_0_0());
            	                    							

            	                    }
            	                    break;
            	                case 2 :
            	                    // InternalSpectra.g:1881:8: otherlv_12= 'trans'
            	                    {
            	                    otherlv_12=(Token)match(input,39,FOLLOW_12); 

            	                    								newLeafNode(otherlv_12, grammarAccess.getPatternAccess().getTransKeyword_3_2_0_1_0_1());
            	                    							

            	                    }
            	                    break;

            	            }

            	            // InternalSpectra.g:1886:7: ( (lv_safety_13_0= ruleTemporalInExpr ) )
            	            // InternalSpectra.g:1887:8: (lv_safety_13_0= ruleTemporalInExpr )
            	            {
            	            // InternalSpectra.g:1887:8: (lv_safety_13_0= ruleTemporalInExpr )
            	            // InternalSpectra.g:1888:9: lv_safety_13_0= ruleTemporalInExpr
            	            {

            	            									newCompositeNode(grammarAccess.getPatternAccess().getSafetyTemporalInExprParserRuleCall_3_2_0_1_1_0());
            	            								
            	            pushFollow(FOLLOW_13);
            	            lv_safety_13_0=ruleTemporalInExpr();

            	            state._fsp--;


            	            									if (current==null) {
            	            										current = createModelElementForParent(grammarAccess.getPatternRule());
            	            									}
            	            									add(
            	            										current,
            	            										"safety",
            	            										lv_safety_13_0,
            	            										"tau.smlab.syntech.Spectra.TemporalInExpr");
            	            									afterParserOrEnumRuleCall();
            	            								

            	            }


            	            }


            	            }


            	            }
            	            break;
            	        case 3 :
            	            // InternalSpectra.g:1907:6: ( (otherlv_14= 'always' | otherlv_15= 'alw' ) ( (lv_stateInv_16_0= ruleTemporalInExpr ) ) )
            	            {
            	            // InternalSpectra.g:1907:6: ( (otherlv_14= 'always' | otherlv_15= 'alw' ) ( (lv_stateInv_16_0= ruleTemporalInExpr ) ) )
            	            // InternalSpectra.g:1908:7: (otherlv_14= 'always' | otherlv_15= 'alw' ) ( (lv_stateInv_16_0= ruleTemporalInExpr ) )
            	            {
            	            // InternalSpectra.g:1908:7: (otherlv_14= 'always' | otherlv_15= 'alw' )
            	            int alt28=2;
            	            int LA28_0 = input.LA(1);

            	            if ( (LA28_0==40) ) {
            	                alt28=1;
            	            }
            	            else if ( (LA28_0==41) ) {
            	                alt28=2;
            	            }
            	            else {
            	                NoViableAltException nvae =
            	                    new NoViableAltException("", 28, 0, input);

            	                throw nvae;
            	            }
            	            switch (alt28) {
            	                case 1 :
            	                    // InternalSpectra.g:1909:8: otherlv_14= 'always'
            	                    {
            	                    otherlv_14=(Token)match(input,40,FOLLOW_12); 

            	                    								newLeafNode(otherlv_14, grammarAccess.getPatternAccess().getAlwaysKeyword_3_2_0_2_0_0());
            	                    							

            	                    }
            	                    break;
            	                case 2 :
            	                    // InternalSpectra.g:1914:8: otherlv_15= 'alw'
            	                    {
            	                    otherlv_15=(Token)match(input,41,FOLLOW_12); 

            	                    								newLeafNode(otherlv_15, grammarAccess.getPatternAccess().getAlwKeyword_3_2_0_2_0_1());
            	                    							

            	                    }
            	                    break;

            	            }

            	            // InternalSpectra.g:1919:7: ( (lv_stateInv_16_0= ruleTemporalInExpr ) )
            	            // InternalSpectra.g:1920:8: (lv_stateInv_16_0= ruleTemporalInExpr )
            	            {
            	            // InternalSpectra.g:1920:8: (lv_stateInv_16_0= ruleTemporalInExpr )
            	            // InternalSpectra.g:1921:9: lv_stateInv_16_0= ruleTemporalInExpr
            	            {

            	            									newCompositeNode(grammarAccess.getPatternAccess().getStateInvTemporalInExprParserRuleCall_3_2_0_2_1_0());
            	            								
            	            pushFollow(FOLLOW_13);
            	            lv_stateInv_16_0=ruleTemporalInExpr();

            	            state._fsp--;


            	            									if (current==null) {
            	            										current = createModelElementForParent(grammarAccess.getPatternRule());
            	            									}
            	            									add(
            	            										current,
            	            										"stateInv",
            	            										lv_stateInv_16_0,
            	            										"tau.smlab.syntech.Spectra.TemporalInExpr");
            	            									afterParserOrEnumRuleCall();
            	            								

            	            }


            	            }


            	            }


            	            }
            	            break;
            	        case 4 :
            	            // InternalSpectra.g:1940:6: ( (otherlv_17= 'GF' | otherlv_18= 'alwEv' | otherlv_19= 'alwaysEventually' ) ( (lv_justice_20_0= ruleTemporalInExpr ) ) )
            	            {
            	            // InternalSpectra.g:1940:6: ( (otherlv_17= 'GF' | otherlv_18= 'alwEv' | otherlv_19= 'alwaysEventually' ) ( (lv_justice_20_0= ruleTemporalInExpr ) ) )
            	            // InternalSpectra.g:1941:7: (otherlv_17= 'GF' | otherlv_18= 'alwEv' | otherlv_19= 'alwaysEventually' ) ( (lv_justice_20_0= ruleTemporalInExpr ) )
            	            {
            	            // InternalSpectra.g:1941:7: (otherlv_17= 'GF' | otherlv_18= 'alwEv' | otherlv_19= 'alwaysEventually' )
            	            int alt29=3;
            	            switch ( input.LA(1) ) {
            	            case 44:
            	                {
            	                alt29=1;
            	                }
            	                break;
            	            case 45:
            	                {
            	                alt29=2;
            	                }
            	                break;
            	            case 46:
            	                {
            	                alt29=3;
            	                }
            	                break;
            	            default:
            	                NoViableAltException nvae =
            	                    new NoViableAltException("", 29, 0, input);

            	                throw nvae;
            	            }

            	            switch (alt29) {
            	                case 1 :
            	                    // InternalSpectra.g:1942:8: otherlv_17= 'GF'
            	                    {
            	                    otherlv_17=(Token)match(input,44,FOLLOW_12); 

            	                    								newLeafNode(otherlv_17, grammarAccess.getPatternAccess().getGFKeyword_3_2_0_3_0_0());
            	                    							

            	                    }
            	                    break;
            	                case 2 :
            	                    // InternalSpectra.g:1947:8: otherlv_18= 'alwEv'
            	                    {
            	                    otherlv_18=(Token)match(input,45,FOLLOW_12); 

            	                    								newLeafNode(otherlv_18, grammarAccess.getPatternAccess().getAlwEvKeyword_3_2_0_3_0_1());
            	                    							

            	                    }
            	                    break;
            	                case 3 :
            	                    // InternalSpectra.g:1952:8: otherlv_19= 'alwaysEventually'
            	                    {
            	                    otherlv_19=(Token)match(input,46,FOLLOW_12); 

            	                    								newLeafNode(otherlv_19, grammarAccess.getPatternAccess().getAlwaysEventuallyKeyword_3_2_0_3_0_2());
            	                    							

            	                    }
            	                    break;

            	            }

            	            // InternalSpectra.g:1957:7: ( (lv_justice_20_0= ruleTemporalInExpr ) )
            	            // InternalSpectra.g:1958:8: (lv_justice_20_0= ruleTemporalInExpr )
            	            {
            	            // InternalSpectra.g:1958:8: (lv_justice_20_0= ruleTemporalInExpr )
            	            // InternalSpectra.g:1959:9: lv_justice_20_0= ruleTemporalInExpr
            	            {

            	            									newCompositeNode(grammarAccess.getPatternAccess().getJusticeTemporalInExprParserRuleCall_3_2_0_3_1_0());
            	            								
            	            pushFollow(FOLLOW_13);
            	            lv_justice_20_0=ruleTemporalInExpr();

            	            state._fsp--;


            	            									if (current==null) {
            	            										current = createModelElementForParent(grammarAccess.getPatternRule());
            	            									}
            	            									add(
            	            										current,
            	            										"justice",
            	            										lv_justice_20_0,
            	            										"tau.smlab.syntech.Spectra.TemporalInExpr");
            	            									afterParserOrEnumRuleCall();
            	            								

            	            }


            	            }


            	            }


            	            }
            	            break;

            	    }


            	    					newCompositeNode(grammarAccess.getPatternAccess().getTOK_SEMIParserRuleCall_3_2_1());
            	    				
            	    pushFollow(FOLLOW_34);
            	    ruleTOK_SEMI();

            	    state._fsp--;


            	    					afterParserOrEnumRuleCall();
            	    				

            	    }
            	    break;

            	default :
            	    if ( cnt31 >= 1 ) break loop31;
                        EarlyExitException eee =
                            new EarlyExitException(31, input);
                        throw eee;
                }
                cnt31++;
            } while (true);

            otherlv_22=(Token)match(input,25,FOLLOW_2); 

            				newLeafNode(otherlv_22, grammarAccess.getPatternAccess().getRightCurlyBracketKeyword_3_3());
            			

            }


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "rulePattern"


    // $ANTLR start "entryRulePredicate"
    // InternalSpectra.g:1995:1: entryRulePredicate returns [EObject current=null] : iv_rulePredicate= rulePredicate EOF ;
    public final EObject entryRulePredicate() throws RecognitionException {
        EObject current = null;

        EObject iv_rulePredicate = null;


        try {
            // InternalSpectra.g:1995:50: (iv_rulePredicate= rulePredicate EOF )
            // InternalSpectra.g:1996:2: iv_rulePredicate= rulePredicate EOF
            {
             newCompositeNode(grammarAccess.getPredicateRule()); 
            pushFollow(FOLLOW_1);
            iv_rulePredicate=rulePredicate();

            state._fsp--;

             current =iv_rulePredicate; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRulePredicate"


    // $ANTLR start "rulePredicate"
    // InternalSpectra.g:2002:1: rulePredicate returns [EObject current=null] : (otherlv_0= 'predicate' ( (lv_name_1_0= RULE_ID ) ) ( (otherlv_2= '(' ( (lv_params_3_0= ruleTypedParamList ) ) otherlv_4= ')' ) | otherlv_5= '()' ) ( (otherlv_6= ':' ( (lv_body_7_0= ruleTemporalExpression ) ) ruleTOK_SEMI ) | (otherlv_9= '{' ( (lv_body_10_0= ruleTemporalExpression ) ) otherlv_11= '}' ) ) ) ;
    public final EObject rulePredicate() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_name_1_0=null;
        Token otherlv_2=null;
        Token otherlv_4=null;
        Token otherlv_5=null;
        Token otherlv_6=null;
        Token otherlv_9=null;
        Token otherlv_11=null;
        EObject lv_params_3_0 = null;

        EObject lv_body_7_0 = null;

        EObject lv_body_10_0 = null;



        	enterRule();

        try {
            // InternalSpectra.g:2008:2: ( (otherlv_0= 'predicate' ( (lv_name_1_0= RULE_ID ) ) ( (otherlv_2= '(' ( (lv_params_3_0= ruleTypedParamList ) ) otherlv_4= ')' ) | otherlv_5= '()' ) ( (otherlv_6= ':' ( (lv_body_7_0= ruleTemporalExpression ) ) ruleTOK_SEMI ) | (otherlv_9= '{' ( (lv_body_10_0= ruleTemporalExpression ) ) otherlv_11= '}' ) ) ) )
            // InternalSpectra.g:2009:2: (otherlv_0= 'predicate' ( (lv_name_1_0= RULE_ID ) ) ( (otherlv_2= '(' ( (lv_params_3_0= ruleTypedParamList ) ) otherlv_4= ')' ) | otherlv_5= '()' ) ( (otherlv_6= ':' ( (lv_body_7_0= ruleTemporalExpression ) ) ruleTOK_SEMI ) | (otherlv_9= '{' ( (lv_body_10_0= ruleTemporalExpression ) ) otherlv_11= '}' ) ) )
            {
            // InternalSpectra.g:2009:2: (otherlv_0= 'predicate' ( (lv_name_1_0= RULE_ID ) ) ( (otherlv_2= '(' ( (lv_params_3_0= ruleTypedParamList ) ) otherlv_4= ')' ) | otherlv_5= '()' ) ( (otherlv_6= ':' ( (lv_body_7_0= ruleTemporalExpression ) ) ruleTOK_SEMI ) | (otherlv_9= '{' ( (lv_body_10_0= ruleTemporalExpression ) ) otherlv_11= '}' ) ) )
            // InternalSpectra.g:2010:3: otherlv_0= 'predicate' ( (lv_name_1_0= RULE_ID ) ) ( (otherlv_2= '(' ( (lv_params_3_0= ruleTypedParamList ) ) otherlv_4= ')' ) | otherlv_5= '()' ) ( (otherlv_6= ':' ( (lv_body_7_0= ruleTemporalExpression ) ) ruleTOK_SEMI ) | (otherlv_9= '{' ( (lv_body_10_0= ruleTemporalExpression ) ) otherlv_11= '}' ) )
            {
            otherlv_0=(Token)match(input,47,FOLLOW_4); 

            			newLeafNode(otherlv_0, grammarAccess.getPredicateAccess().getPredicateKeyword_0());
            		
            // InternalSpectra.g:2014:3: ( (lv_name_1_0= RULE_ID ) )
            // InternalSpectra.g:2015:4: (lv_name_1_0= RULE_ID )
            {
            // InternalSpectra.g:2015:4: (lv_name_1_0= RULE_ID )
            // InternalSpectra.g:2016:5: lv_name_1_0= RULE_ID
            {
            lv_name_1_0=(Token)match(input,RULE_ID,FOLLOW_35); 

            					newLeafNode(lv_name_1_0, grammarAccess.getPredicateAccess().getNameIDTerminalRuleCall_1_0());
            				

            					if (current==null) {
            						current = createModelElement(grammarAccess.getPredicateRule());
            					}
            					setWithLastConsumed(
            						current,
            						"name",
            						lv_name_1_0,
            						"org.eclipse.xtext.common.Terminals.ID");
            				

            }


            }

            // InternalSpectra.g:2032:3: ( (otherlv_2= '(' ( (lv_params_3_0= ruleTypedParamList ) ) otherlv_4= ')' ) | otherlv_5= '()' )
            int alt32=2;
            int LA32_0 = input.LA(1);

            if ( (LA32_0==28) ) {
                alt32=1;
            }
            else if ( (LA32_0==48) ) {
                alt32=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 32, 0, input);

                throw nvae;
            }
            switch (alt32) {
                case 1 :
                    // InternalSpectra.g:2033:4: (otherlv_2= '(' ( (lv_params_3_0= ruleTypedParamList ) ) otherlv_4= ')' )
                    {
                    // InternalSpectra.g:2033:4: (otherlv_2= '(' ( (lv_params_3_0= ruleTypedParamList ) ) otherlv_4= ')' )
                    // InternalSpectra.g:2034:5: otherlv_2= '(' ( (lv_params_3_0= ruleTypedParamList ) ) otherlv_4= ')'
                    {
                    otherlv_2=(Token)match(input,28,FOLLOW_14); 

                    					newLeafNode(otherlv_2, grammarAccess.getPredicateAccess().getLeftParenthesisKeyword_2_0_0());
                    				
                    // InternalSpectra.g:2038:5: ( (lv_params_3_0= ruleTypedParamList ) )
                    // InternalSpectra.g:2039:6: (lv_params_3_0= ruleTypedParamList )
                    {
                    // InternalSpectra.g:2039:6: (lv_params_3_0= ruleTypedParamList )
                    // InternalSpectra.g:2040:7: lv_params_3_0= ruleTypedParamList
                    {

                    							newCompositeNode(grammarAccess.getPredicateAccess().getParamsTypedParamListParserRuleCall_2_0_1_0());
                    						
                    pushFollow(FOLLOW_28);
                    lv_params_3_0=ruleTypedParamList();

                    state._fsp--;


                    							if (current==null) {
                    								current = createModelElementForParent(grammarAccess.getPredicateRule());
                    							}
                    							set(
                    								current,
                    								"params",
                    								lv_params_3_0,
                    								"tau.smlab.syntech.Spectra.TypedParamList");
                    							afterParserOrEnumRuleCall();
                    						

                    }


                    }

                    otherlv_4=(Token)match(input,29,FOLLOW_36); 

                    					newLeafNode(otherlv_4, grammarAccess.getPredicateAccess().getRightParenthesisKeyword_2_0_2());
                    				

                    }


                    }
                    break;
                case 2 :
                    // InternalSpectra.g:2063:4: otherlv_5= '()'
                    {
                    otherlv_5=(Token)match(input,48,FOLLOW_36); 

                    				newLeafNode(otherlv_5, grammarAccess.getPredicateAccess().getLeftParenthesisRightParenthesisKeyword_2_1());
                    			

                    }
                    break;

            }

            // InternalSpectra.g:2068:3: ( (otherlv_6= ':' ( (lv_body_7_0= ruleTemporalExpression ) ) ruleTOK_SEMI ) | (otherlv_9= '{' ( (lv_body_10_0= ruleTemporalExpression ) ) otherlv_11= '}' ) )
            int alt33=2;
            int LA33_0 = input.LA(1);

            if ( (LA33_0==15) ) {
                alt33=1;
            }
            else if ( (LA33_0==23) ) {
                alt33=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 33, 0, input);

                throw nvae;
            }
            switch (alt33) {
                case 1 :
                    // InternalSpectra.g:2069:4: (otherlv_6= ':' ( (lv_body_7_0= ruleTemporalExpression ) ) ruleTOK_SEMI )
                    {
                    // InternalSpectra.g:2069:4: (otherlv_6= ':' ( (lv_body_7_0= ruleTemporalExpression ) ) ruleTOK_SEMI )
                    // InternalSpectra.g:2070:5: otherlv_6= ':' ( (lv_body_7_0= ruleTemporalExpression ) ) ruleTOK_SEMI
                    {
                    otherlv_6=(Token)match(input,15,FOLLOW_18); 

                    					newLeafNode(otherlv_6, grammarAccess.getPredicateAccess().getColonKeyword_3_0_0());
                    				
                    // InternalSpectra.g:2074:5: ( (lv_body_7_0= ruleTemporalExpression ) )
                    // InternalSpectra.g:2075:6: (lv_body_7_0= ruleTemporalExpression )
                    {
                    // InternalSpectra.g:2075:6: (lv_body_7_0= ruleTemporalExpression )
                    // InternalSpectra.g:2076:7: lv_body_7_0= ruleTemporalExpression
                    {

                    							newCompositeNode(grammarAccess.getPredicateAccess().getBodyTemporalExpressionParserRuleCall_3_0_1_0());
                    						
                    pushFollow(FOLLOW_13);
                    lv_body_7_0=ruleTemporalExpression();

                    state._fsp--;


                    							if (current==null) {
                    								current = createModelElementForParent(grammarAccess.getPredicateRule());
                    							}
                    							set(
                    								current,
                    								"body",
                    								lv_body_7_0,
                    								"tau.smlab.syntech.Spectra.TemporalExpression");
                    							afterParserOrEnumRuleCall();
                    						

                    }


                    }


                    					newCompositeNode(grammarAccess.getPredicateAccess().getTOK_SEMIParserRuleCall_3_0_2());
                    				
                    pushFollow(FOLLOW_2);
                    ruleTOK_SEMI();

                    state._fsp--;


                    					afterParserOrEnumRuleCall();
                    				

                    }


                    }
                    break;
                case 2 :
                    // InternalSpectra.g:2102:4: (otherlv_9= '{' ( (lv_body_10_0= ruleTemporalExpression ) ) otherlv_11= '}' )
                    {
                    // InternalSpectra.g:2102:4: (otherlv_9= '{' ( (lv_body_10_0= ruleTemporalExpression ) ) otherlv_11= '}' )
                    // InternalSpectra.g:2103:5: otherlv_9= '{' ( (lv_body_10_0= ruleTemporalExpression ) ) otherlv_11= '}'
                    {
                    otherlv_9=(Token)match(input,23,FOLLOW_18); 

                    					newLeafNode(otherlv_9, grammarAccess.getPredicateAccess().getLeftCurlyBracketKeyword_3_1_0());
                    				
                    // InternalSpectra.g:2107:5: ( (lv_body_10_0= ruleTemporalExpression ) )
                    // InternalSpectra.g:2108:6: (lv_body_10_0= ruleTemporalExpression )
                    {
                    // InternalSpectra.g:2108:6: (lv_body_10_0= ruleTemporalExpression )
                    // InternalSpectra.g:2109:7: lv_body_10_0= ruleTemporalExpression
                    {

                    							newCompositeNode(grammarAccess.getPredicateAccess().getBodyTemporalExpressionParserRuleCall_3_1_1_0());
                    						
                    pushFollow(FOLLOW_37);
                    lv_body_10_0=ruleTemporalExpression();

                    state._fsp--;


                    							if (current==null) {
                    								current = createModelElementForParent(grammarAccess.getPredicateRule());
                    							}
                    							set(
                    								current,
                    								"body",
                    								lv_body_10_0,
                    								"tau.smlab.syntech.Spectra.TemporalExpression");
                    							afterParserOrEnumRuleCall();
                    						

                    }


                    }

                    otherlv_11=(Token)match(input,25,FOLLOW_2); 

                    					newLeafNode(otherlv_11, grammarAccess.getPredicateAccess().getRightCurlyBracketKeyword_3_1_2());
                    				

                    }


                    }
                    break;

            }


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "rulePredicate"


    // $ANTLR start "entryRuleVarType"
    // InternalSpectra.g:2136:1: entryRuleVarType returns [EObject current=null] : iv_ruleVarType= ruleVarType EOF ;
    public final EObject entryRuleVarType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleVarType = null;


        try {
            // InternalSpectra.g:2136:48: (iv_ruleVarType= ruleVarType EOF )
            // InternalSpectra.g:2137:2: iv_ruleVarType= ruleVarType EOF
            {
             newCompositeNode(grammarAccess.getVarTypeRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleVarType=ruleVarType();

            state._fsp--;

             current =iv_ruleVarType; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleVarType"


    // $ANTLR start "ruleVarType"
    // InternalSpectra.g:2143:1: ruleVarType returns [EObject current=null] : ( ( ( (lv_name_0_0= 'boolean' ) ) | (otherlv_1= 'Int' otherlv_2= '(' ( (lv_subr_3_0= ruleSubrange ) ) otherlv_4= ')' ) | (otherlv_5= '{' ( (lv_const_6_0= ruleTypeConstant ) ) (otherlv_7= ',' ( (lv_const_8_0= ruleTypeConstant ) ) )* otherlv_9= '}' ) | ( (otherlv_10= RULE_ID ) ) ) (otherlv_11= '[' ( (lv_dimensions_12_0= ruleSizeDefineDecl ) ) otherlv_13= ']' )* ) ;
    public final EObject ruleVarType() throws RecognitionException {
        EObject current = null;

        Token lv_name_0_0=null;
        Token otherlv_1=null;
        Token otherlv_2=null;
        Token otherlv_4=null;
        Token otherlv_5=null;
        Token otherlv_7=null;
        Token otherlv_9=null;
        Token otherlv_10=null;
        Token otherlv_11=null;
        Token otherlv_13=null;
        EObject lv_subr_3_0 = null;

        EObject lv_const_6_0 = null;

        EObject lv_const_8_0 = null;

        EObject lv_dimensions_12_0 = null;



        	enterRule();

        try {
            // InternalSpectra.g:2149:2: ( ( ( ( (lv_name_0_0= 'boolean' ) ) | (otherlv_1= 'Int' otherlv_2= '(' ( (lv_subr_3_0= ruleSubrange ) ) otherlv_4= ')' ) | (otherlv_5= '{' ( (lv_const_6_0= ruleTypeConstant ) ) (otherlv_7= ',' ( (lv_const_8_0= ruleTypeConstant ) ) )* otherlv_9= '}' ) | ( (otherlv_10= RULE_ID ) ) ) (otherlv_11= '[' ( (lv_dimensions_12_0= ruleSizeDefineDecl ) ) otherlv_13= ']' )* ) )
            // InternalSpectra.g:2150:2: ( ( ( (lv_name_0_0= 'boolean' ) ) | (otherlv_1= 'Int' otherlv_2= '(' ( (lv_subr_3_0= ruleSubrange ) ) otherlv_4= ')' ) | (otherlv_5= '{' ( (lv_const_6_0= ruleTypeConstant ) ) (otherlv_7= ',' ( (lv_const_8_0= ruleTypeConstant ) ) )* otherlv_9= '}' ) | ( (otherlv_10= RULE_ID ) ) ) (otherlv_11= '[' ( (lv_dimensions_12_0= ruleSizeDefineDecl ) ) otherlv_13= ']' )* )
            {
            // InternalSpectra.g:2150:2: ( ( ( (lv_name_0_0= 'boolean' ) ) | (otherlv_1= 'Int' otherlv_2= '(' ( (lv_subr_3_0= ruleSubrange ) ) otherlv_4= ')' ) | (otherlv_5= '{' ( (lv_const_6_0= ruleTypeConstant ) ) (otherlv_7= ',' ( (lv_const_8_0= ruleTypeConstant ) ) )* otherlv_9= '}' ) | ( (otherlv_10= RULE_ID ) ) ) (otherlv_11= '[' ( (lv_dimensions_12_0= ruleSizeDefineDecl ) ) otherlv_13= ']' )* )
            // InternalSpectra.g:2151:3: ( ( (lv_name_0_0= 'boolean' ) ) | (otherlv_1= 'Int' otherlv_2= '(' ( (lv_subr_3_0= ruleSubrange ) ) otherlv_4= ')' ) | (otherlv_5= '{' ( (lv_const_6_0= ruleTypeConstant ) ) (otherlv_7= ',' ( (lv_const_8_0= ruleTypeConstant ) ) )* otherlv_9= '}' ) | ( (otherlv_10= RULE_ID ) ) ) (otherlv_11= '[' ( (lv_dimensions_12_0= ruleSizeDefineDecl ) ) otherlv_13= ']' )*
            {
            // InternalSpectra.g:2151:3: ( ( (lv_name_0_0= 'boolean' ) ) | (otherlv_1= 'Int' otherlv_2= '(' ( (lv_subr_3_0= ruleSubrange ) ) otherlv_4= ')' ) | (otherlv_5= '{' ( (lv_const_6_0= ruleTypeConstant ) ) (otherlv_7= ',' ( (lv_const_8_0= ruleTypeConstant ) ) )* otherlv_9= '}' ) | ( (otherlv_10= RULE_ID ) ) )
            int alt35=4;
            switch ( input.LA(1) ) {
            case 49:
                {
                alt35=1;
                }
                break;
            case 50:
                {
                alt35=2;
                }
                break;
            case 23:
                {
                alt35=3;
                }
                break;
            case RULE_ID:
                {
                alt35=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 35, 0, input);

                throw nvae;
            }

            switch (alt35) {
                case 1 :
                    // InternalSpectra.g:2152:4: ( (lv_name_0_0= 'boolean' ) )
                    {
                    // InternalSpectra.g:2152:4: ( (lv_name_0_0= 'boolean' ) )
                    // InternalSpectra.g:2153:5: (lv_name_0_0= 'boolean' )
                    {
                    // InternalSpectra.g:2153:5: (lv_name_0_0= 'boolean' )
                    // InternalSpectra.g:2154:6: lv_name_0_0= 'boolean'
                    {
                    lv_name_0_0=(Token)match(input,49,FOLLOW_38); 

                    						newLeafNode(lv_name_0_0, grammarAccess.getVarTypeAccess().getNameBooleanKeyword_0_0_0());
                    					

                    						if (current==null) {
                    							current = createModelElement(grammarAccess.getVarTypeRule());
                    						}
                    						setWithLastConsumed(current, "name", lv_name_0_0, "boolean");
                    					

                    }


                    }


                    }
                    break;
                case 2 :
                    // InternalSpectra.g:2167:4: (otherlv_1= 'Int' otherlv_2= '(' ( (lv_subr_3_0= ruleSubrange ) ) otherlv_4= ')' )
                    {
                    // InternalSpectra.g:2167:4: (otherlv_1= 'Int' otherlv_2= '(' ( (lv_subr_3_0= ruleSubrange ) ) otherlv_4= ')' )
                    // InternalSpectra.g:2168:5: otherlv_1= 'Int' otherlv_2= '(' ( (lv_subr_3_0= ruleSubrange ) ) otherlv_4= ')'
                    {
                    otherlv_1=(Token)match(input,50,FOLLOW_27); 

                    					newLeafNode(otherlv_1, grammarAccess.getVarTypeAccess().getIntKeyword_0_1_0());
                    				
                    otherlv_2=(Token)match(input,28,FOLLOW_20); 

                    					newLeafNode(otherlv_2, grammarAccess.getVarTypeAccess().getLeftParenthesisKeyword_0_1_1());
                    				
                    // InternalSpectra.g:2176:5: ( (lv_subr_3_0= ruleSubrange ) )
                    // InternalSpectra.g:2177:6: (lv_subr_3_0= ruleSubrange )
                    {
                    // InternalSpectra.g:2177:6: (lv_subr_3_0= ruleSubrange )
                    // InternalSpectra.g:2178:7: lv_subr_3_0= ruleSubrange
                    {

                    							newCompositeNode(grammarAccess.getVarTypeAccess().getSubrSubrangeParserRuleCall_0_1_2_0());
                    						
                    pushFollow(FOLLOW_28);
                    lv_subr_3_0=ruleSubrange();

                    state._fsp--;


                    							if (current==null) {
                    								current = createModelElementForParent(grammarAccess.getVarTypeRule());
                    							}
                    							set(
                    								current,
                    								"subr",
                    								lv_subr_3_0,
                    								"tau.smlab.syntech.Spectra.Subrange");
                    							afterParserOrEnumRuleCall();
                    						

                    }


                    }

                    otherlv_4=(Token)match(input,29,FOLLOW_38); 

                    					newLeafNode(otherlv_4, grammarAccess.getVarTypeAccess().getRightParenthesisKeyword_0_1_3());
                    				

                    }


                    }
                    break;
                case 3 :
                    // InternalSpectra.g:2201:4: (otherlv_5= '{' ( (lv_const_6_0= ruleTypeConstant ) ) (otherlv_7= ',' ( (lv_const_8_0= ruleTypeConstant ) ) )* otherlv_9= '}' )
                    {
                    // InternalSpectra.g:2201:4: (otherlv_5= '{' ( (lv_const_6_0= ruleTypeConstant ) ) (otherlv_7= ',' ( (lv_const_8_0= ruleTypeConstant ) ) )* otherlv_9= '}' )
                    // InternalSpectra.g:2202:5: otherlv_5= '{' ( (lv_const_6_0= ruleTypeConstant ) ) (otherlv_7= ',' ( (lv_const_8_0= ruleTypeConstant ) ) )* otherlv_9= '}'
                    {
                    otherlv_5=(Token)match(input,23,FOLLOW_39); 

                    					newLeafNode(otherlv_5, grammarAccess.getVarTypeAccess().getLeftCurlyBracketKeyword_0_2_0());
                    				
                    // InternalSpectra.g:2206:5: ( (lv_const_6_0= ruleTypeConstant ) )
                    // InternalSpectra.g:2207:6: (lv_const_6_0= ruleTypeConstant )
                    {
                    // InternalSpectra.g:2207:6: (lv_const_6_0= ruleTypeConstant )
                    // InternalSpectra.g:2208:7: lv_const_6_0= ruleTypeConstant
                    {

                    							newCompositeNode(grammarAccess.getVarTypeAccess().getConstTypeConstantParserRuleCall_0_2_1_0());
                    						
                    pushFollow(FOLLOW_24);
                    lv_const_6_0=ruleTypeConstant();

                    state._fsp--;


                    							if (current==null) {
                    								current = createModelElementForParent(grammarAccess.getVarTypeRule());
                    							}
                    							add(
                    								current,
                    								"const",
                    								lv_const_6_0,
                    								"tau.smlab.syntech.Spectra.TypeConstant");
                    							afterParserOrEnumRuleCall();
                    						

                    }


                    }

                    // InternalSpectra.g:2225:5: (otherlv_7= ',' ( (lv_const_8_0= ruleTypeConstant ) ) )*
                    loop34:
                    do {
                        int alt34=2;
                        int LA34_0 = input.LA(1);

                        if ( (LA34_0==24) ) {
                            alt34=1;
                        }


                        switch (alt34) {
                    	case 1 :
                    	    // InternalSpectra.g:2226:6: otherlv_7= ',' ( (lv_const_8_0= ruleTypeConstant ) )
                    	    {
                    	    otherlv_7=(Token)match(input,24,FOLLOW_39); 

                    	    						newLeafNode(otherlv_7, grammarAccess.getVarTypeAccess().getCommaKeyword_0_2_2_0());
                    	    					
                    	    // InternalSpectra.g:2230:6: ( (lv_const_8_0= ruleTypeConstant ) )
                    	    // InternalSpectra.g:2231:7: (lv_const_8_0= ruleTypeConstant )
                    	    {
                    	    // InternalSpectra.g:2231:7: (lv_const_8_0= ruleTypeConstant )
                    	    // InternalSpectra.g:2232:8: lv_const_8_0= ruleTypeConstant
                    	    {

                    	    								newCompositeNode(grammarAccess.getVarTypeAccess().getConstTypeConstantParserRuleCall_0_2_2_1_0());
                    	    							
                    	    pushFollow(FOLLOW_24);
                    	    lv_const_8_0=ruleTypeConstant();

                    	    state._fsp--;


                    	    								if (current==null) {
                    	    									current = createModelElementForParent(grammarAccess.getVarTypeRule());
                    	    								}
                    	    								add(
                    	    									current,
                    	    									"const",
                    	    									lv_const_8_0,
                    	    									"tau.smlab.syntech.Spectra.TypeConstant");
                    	    								afterParserOrEnumRuleCall();
                    	    							

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop34;
                        }
                    } while (true);

                    otherlv_9=(Token)match(input,25,FOLLOW_38); 

                    					newLeafNode(otherlv_9, grammarAccess.getVarTypeAccess().getRightCurlyBracketKeyword_0_2_3());
                    				

                    }


                    }
                    break;
                case 4 :
                    // InternalSpectra.g:2256:4: ( (otherlv_10= RULE_ID ) )
                    {
                    // InternalSpectra.g:2256:4: ( (otherlv_10= RULE_ID ) )
                    // InternalSpectra.g:2257:5: (otherlv_10= RULE_ID )
                    {
                    // InternalSpectra.g:2257:5: (otherlv_10= RULE_ID )
                    // InternalSpectra.g:2258:6: otherlv_10= RULE_ID
                    {

                    						if (current==null) {
                    							current = createModelElement(grammarAccess.getVarTypeRule());
                    						}
                    					
                    otherlv_10=(Token)match(input,RULE_ID,FOLLOW_38); 

                    						newLeafNode(otherlv_10, grammarAccess.getVarTypeAccess().getTypeTypeDefCrossReference_0_3_0());
                    					

                    }


                    }


                    }
                    break;

            }

            // InternalSpectra.g:2270:3: (otherlv_11= '[' ( (lv_dimensions_12_0= ruleSizeDefineDecl ) ) otherlv_13= ']' )*
            loop36:
            do {
                int alt36=2;
                int LA36_0 = input.LA(1);

                if ( (LA36_0==21) ) {
                    alt36=1;
                }


                switch (alt36) {
            	case 1 :
            	    // InternalSpectra.g:2271:4: otherlv_11= '[' ( (lv_dimensions_12_0= ruleSizeDefineDecl ) ) otherlv_13= ']'
            	    {
            	    otherlv_11=(Token)match(input,21,FOLLOW_20); 

            	    				newLeafNode(otherlv_11, grammarAccess.getVarTypeAccess().getLeftSquareBracketKeyword_1_0());
            	    			
            	    // InternalSpectra.g:2275:4: ( (lv_dimensions_12_0= ruleSizeDefineDecl ) )
            	    // InternalSpectra.g:2276:5: (lv_dimensions_12_0= ruleSizeDefineDecl )
            	    {
            	    // InternalSpectra.g:2276:5: (lv_dimensions_12_0= ruleSizeDefineDecl )
            	    // InternalSpectra.g:2277:6: lv_dimensions_12_0= ruleSizeDefineDecl
            	    {

            	    						newCompositeNode(grammarAccess.getVarTypeAccess().getDimensionsSizeDefineDeclParserRuleCall_1_1_0());
            	    					
            	    pushFollow(FOLLOW_21);
            	    lv_dimensions_12_0=ruleSizeDefineDecl();

            	    state._fsp--;


            	    						if (current==null) {
            	    							current = createModelElementForParent(grammarAccess.getVarTypeRule());
            	    						}
            	    						add(
            	    							current,
            	    							"dimensions",
            	    							lv_dimensions_12_0,
            	    							"tau.smlab.syntech.Spectra.SizeDefineDecl");
            	    						afterParserOrEnumRuleCall();
            	    					

            	    }


            	    }

            	    otherlv_13=(Token)match(input,22,FOLLOW_38); 

            	    				newLeafNode(otherlv_13, grammarAccess.getVarTypeAccess().getRightSquareBracketKeyword_1_2());
            	    			

            	    }
            	    break;

            	default :
            	    break loop36;
                }
            } while (true);


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleVarType"


    // $ANTLR start "entryRuleLTLGar"
    // InternalSpectra.g:2303:1: entryRuleLTLGar returns [EObject current=null] : iv_ruleLTLGar= ruleLTLGar EOF ;
    public final EObject entryRuleLTLGar() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleLTLGar = null;


        try {
            // InternalSpectra.g:2303:47: (iv_ruleLTLGar= ruleLTLGar EOF )
            // InternalSpectra.g:2304:2: iv_ruleLTLGar= ruleLTLGar EOF
            {
             newCompositeNode(grammarAccess.getLTLGarRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleLTLGar=ruleLTLGar();

            state._fsp--;

             current =iv_ruleLTLGar; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleLTLGar"


    // $ANTLR start "ruleLTLGar"
    // InternalSpectra.g:2310:1: ruleLTLGar returns [EObject current=null] : ( (otherlv_0= 'guarantee' | otherlv_1= 'gar' ) ( ( (lv_name_2_0= RULE_ID ) ) (otherlv_3= '{' ( (lv_params_4_0= ruleTypedParamList ) ) otherlv_5= '}' )? otherlv_6= ':' )? ( ( (otherlv_7= 'ini' | otherlv_8= 'initially' | ( ( (lv_safety_9_1= 'G' | lv_safety_9_2= 'trans' ) ) ) | ( ( (lv_stateInv_10_1= 'always' | lv_stateInv_10_2= 'alw' ) ) ) | ( ( (lv_justice_11_1= 'GF' | lv_justice_11_2= 'alwEv' | lv_justice_11_3= 'alwaysEventually' ) ) ) )? ( (lv_temporalExpr_12_0= ruleQuantifierExpr ) ) ) | ( (lv_trig_13_0= ruleTrigger ) ) ) ruleTOK_SEMI ) ;
    public final EObject ruleLTLGar() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_1=null;
        Token lv_name_2_0=null;
        Token otherlv_3=null;
        Token otherlv_5=null;
        Token otherlv_6=null;
        Token otherlv_7=null;
        Token otherlv_8=null;
        Token lv_safety_9_1=null;
        Token lv_safety_9_2=null;
        Token lv_stateInv_10_1=null;
        Token lv_stateInv_10_2=null;
        Token lv_justice_11_1=null;
        Token lv_justice_11_2=null;
        Token lv_justice_11_3=null;
        EObject lv_params_4_0 = null;

        EObject lv_temporalExpr_12_0 = null;

        EObject lv_trig_13_0 = null;



        	enterRule();

        try {
            // InternalSpectra.g:2316:2: ( ( (otherlv_0= 'guarantee' | otherlv_1= 'gar' ) ( ( (lv_name_2_0= RULE_ID ) ) (otherlv_3= '{' ( (lv_params_4_0= ruleTypedParamList ) ) otherlv_5= '}' )? otherlv_6= ':' )? ( ( (otherlv_7= 'ini' | otherlv_8= 'initially' | ( ( (lv_safety_9_1= 'G' | lv_safety_9_2= 'trans' ) ) ) | ( ( (lv_stateInv_10_1= 'always' | lv_stateInv_10_2= 'alw' ) ) ) | ( ( (lv_justice_11_1= 'GF' | lv_justice_11_2= 'alwEv' | lv_justice_11_3= 'alwaysEventually' ) ) ) )? ( (lv_temporalExpr_12_0= ruleQuantifierExpr ) ) ) | ( (lv_trig_13_0= ruleTrigger ) ) ) ruleTOK_SEMI ) )
            // InternalSpectra.g:2317:2: ( (otherlv_0= 'guarantee' | otherlv_1= 'gar' ) ( ( (lv_name_2_0= RULE_ID ) ) (otherlv_3= '{' ( (lv_params_4_0= ruleTypedParamList ) ) otherlv_5= '}' )? otherlv_6= ':' )? ( ( (otherlv_7= 'ini' | otherlv_8= 'initially' | ( ( (lv_safety_9_1= 'G' | lv_safety_9_2= 'trans' ) ) ) | ( ( (lv_stateInv_10_1= 'always' | lv_stateInv_10_2= 'alw' ) ) ) | ( ( (lv_justice_11_1= 'GF' | lv_justice_11_2= 'alwEv' | lv_justice_11_3= 'alwaysEventually' ) ) ) )? ( (lv_temporalExpr_12_0= ruleQuantifierExpr ) ) ) | ( (lv_trig_13_0= ruleTrigger ) ) ) ruleTOK_SEMI )
            {
            // InternalSpectra.g:2317:2: ( (otherlv_0= 'guarantee' | otherlv_1= 'gar' ) ( ( (lv_name_2_0= RULE_ID ) ) (otherlv_3= '{' ( (lv_params_4_0= ruleTypedParamList ) ) otherlv_5= '}' )? otherlv_6= ':' )? ( ( (otherlv_7= 'ini' | otherlv_8= 'initially' | ( ( (lv_safety_9_1= 'G' | lv_safety_9_2= 'trans' ) ) ) | ( ( (lv_stateInv_10_1= 'always' | lv_stateInv_10_2= 'alw' ) ) ) | ( ( (lv_justice_11_1= 'GF' | lv_justice_11_2= 'alwEv' | lv_justice_11_3= 'alwaysEventually' ) ) ) )? ( (lv_temporalExpr_12_0= ruleQuantifierExpr ) ) ) | ( (lv_trig_13_0= ruleTrigger ) ) ) ruleTOK_SEMI )
            // InternalSpectra.g:2318:3: (otherlv_0= 'guarantee' | otherlv_1= 'gar' ) ( ( (lv_name_2_0= RULE_ID ) ) (otherlv_3= '{' ( (lv_params_4_0= ruleTypedParamList ) ) otherlv_5= '}' )? otherlv_6= ':' )? ( ( (otherlv_7= 'ini' | otherlv_8= 'initially' | ( ( (lv_safety_9_1= 'G' | lv_safety_9_2= 'trans' ) ) ) | ( ( (lv_stateInv_10_1= 'always' | lv_stateInv_10_2= 'alw' ) ) ) | ( ( (lv_justice_11_1= 'GF' | lv_justice_11_2= 'alwEv' | lv_justice_11_3= 'alwaysEventually' ) ) ) )? ( (lv_temporalExpr_12_0= ruleQuantifierExpr ) ) ) | ( (lv_trig_13_0= ruleTrigger ) ) ) ruleTOK_SEMI
            {
            // InternalSpectra.g:2318:3: (otherlv_0= 'guarantee' | otherlv_1= 'gar' )
            int alt37=2;
            int LA37_0 = input.LA(1);

            if ( (LA37_0==51) ) {
                alt37=1;
            }
            else if ( (LA37_0==52) ) {
                alt37=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 37, 0, input);

                throw nvae;
            }
            switch (alt37) {
                case 1 :
                    // InternalSpectra.g:2319:4: otherlv_0= 'guarantee'
                    {
                    otherlv_0=(Token)match(input,51,FOLLOW_40); 

                    				newLeafNode(otherlv_0, grammarAccess.getLTLGarAccess().getGuaranteeKeyword_0_0());
                    			

                    }
                    break;
                case 2 :
                    // InternalSpectra.g:2324:4: otherlv_1= 'gar'
                    {
                    otherlv_1=(Token)match(input,52,FOLLOW_40); 

                    				newLeafNode(otherlv_1, grammarAccess.getLTLGarAccess().getGarKeyword_0_1());
                    			

                    }
                    break;

            }

            // InternalSpectra.g:2329:3: ( ( (lv_name_2_0= RULE_ID ) ) (otherlv_3= '{' ( (lv_params_4_0= ruleTypedParamList ) ) otherlv_5= '}' )? otherlv_6= ':' )?
            int alt39=2;
            int LA39_0 = input.LA(1);

            if ( (LA39_0==RULE_ID) ) {
                int LA39_1 = input.LA(2);

                if ( (LA39_1==15||LA39_1==23) ) {
                    alt39=1;
                }
            }
            switch (alt39) {
                case 1 :
                    // InternalSpectra.g:2330:4: ( (lv_name_2_0= RULE_ID ) ) (otherlv_3= '{' ( (lv_params_4_0= ruleTypedParamList ) ) otherlv_5= '}' )? otherlv_6= ':'
                    {
                    // InternalSpectra.g:2330:4: ( (lv_name_2_0= RULE_ID ) )
                    // InternalSpectra.g:2331:5: (lv_name_2_0= RULE_ID )
                    {
                    // InternalSpectra.g:2331:5: (lv_name_2_0= RULE_ID )
                    // InternalSpectra.g:2332:6: lv_name_2_0= RULE_ID
                    {
                    lv_name_2_0=(Token)match(input,RULE_ID,FOLLOW_36); 

                    						newLeafNode(lv_name_2_0, grammarAccess.getLTLGarAccess().getNameIDTerminalRuleCall_1_0_0());
                    					

                    						if (current==null) {
                    							current = createModelElement(grammarAccess.getLTLGarRule());
                    						}
                    						setWithLastConsumed(
                    							current,
                    							"name",
                    							lv_name_2_0,
                    							"org.eclipse.xtext.common.Terminals.ID");
                    					

                    }


                    }

                    // InternalSpectra.g:2348:4: (otherlv_3= '{' ( (lv_params_4_0= ruleTypedParamList ) ) otherlv_5= '}' )?
                    int alt38=2;
                    int LA38_0 = input.LA(1);

                    if ( (LA38_0==23) ) {
                        alt38=1;
                    }
                    switch (alt38) {
                        case 1 :
                            // InternalSpectra.g:2349:5: otherlv_3= '{' ( (lv_params_4_0= ruleTypedParamList ) ) otherlv_5= '}'
                            {
                            otherlv_3=(Token)match(input,23,FOLLOW_14); 

                            					newLeafNode(otherlv_3, grammarAccess.getLTLGarAccess().getLeftCurlyBracketKeyword_1_1_0());
                            				
                            // InternalSpectra.g:2353:5: ( (lv_params_4_0= ruleTypedParamList ) )
                            // InternalSpectra.g:2354:6: (lv_params_4_0= ruleTypedParamList )
                            {
                            // InternalSpectra.g:2354:6: (lv_params_4_0= ruleTypedParamList )
                            // InternalSpectra.g:2355:7: lv_params_4_0= ruleTypedParamList
                            {

                            							newCompositeNode(grammarAccess.getLTLGarAccess().getParamsTypedParamListParserRuleCall_1_1_1_0());
                            						
                            pushFollow(FOLLOW_37);
                            lv_params_4_0=ruleTypedParamList();

                            state._fsp--;


                            							if (current==null) {
                            								current = createModelElementForParent(grammarAccess.getLTLGarRule());
                            							}
                            							set(
                            								current,
                            								"params",
                            								lv_params_4_0,
                            								"tau.smlab.syntech.Spectra.TypedParamList");
                            							afterParserOrEnumRuleCall();
                            						

                            }


                            }

                            otherlv_5=(Token)match(input,25,FOLLOW_9); 

                            					newLeafNode(otherlv_5, grammarAccess.getLTLGarAccess().getRightCurlyBracketKeyword_1_1_2());
                            				

                            }
                            break;

                    }

                    otherlv_6=(Token)match(input,15,FOLLOW_40); 

                    				newLeafNode(otherlv_6, grammarAccess.getLTLGarAccess().getColonKeyword_1_2());
                    			

                    }
                    break;

            }

            // InternalSpectra.g:2382:3: ( ( (otherlv_7= 'ini' | otherlv_8= 'initially' | ( ( (lv_safety_9_1= 'G' | lv_safety_9_2= 'trans' ) ) ) | ( ( (lv_stateInv_10_1= 'always' | lv_stateInv_10_2= 'alw' ) ) ) | ( ( (lv_justice_11_1= 'GF' | lv_justice_11_2= 'alwEv' | lv_justice_11_3= 'alwaysEventually' ) ) ) )? ( (lv_temporalExpr_12_0= ruleQuantifierExpr ) ) ) | ( (lv_trig_13_0= ruleTrigger ) ) )
            int alt44=2;
            int LA44_0 = input.LA(1);

            if ( (LA44_0==RULE_ID||LA44_0==RULE_INT||LA44_0==16||LA44_0==26||LA44_0==28||(LA44_0>=36 && LA44_0<=41)||(LA44_0>=44 && LA44_0<=46)||(LA44_0>=82 && LA44_0<=89)||(LA44_0>=97 && LA44_0<=100)||(LA44_0>=105 && LA44_0<=106)) ) {
                alt44=1;
            }
            else if ( (LA44_0==101) ) {
                alt44=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 44, 0, input);

                throw nvae;
            }
            switch (alt44) {
                case 1 :
                    // InternalSpectra.g:2383:4: ( (otherlv_7= 'ini' | otherlv_8= 'initially' | ( ( (lv_safety_9_1= 'G' | lv_safety_9_2= 'trans' ) ) ) | ( ( (lv_stateInv_10_1= 'always' | lv_stateInv_10_2= 'alw' ) ) ) | ( ( (lv_justice_11_1= 'GF' | lv_justice_11_2= 'alwEv' | lv_justice_11_3= 'alwaysEventually' ) ) ) )? ( (lv_temporalExpr_12_0= ruleQuantifierExpr ) ) )
                    {
                    // InternalSpectra.g:2383:4: ( (otherlv_7= 'ini' | otherlv_8= 'initially' | ( ( (lv_safety_9_1= 'G' | lv_safety_9_2= 'trans' ) ) ) | ( ( (lv_stateInv_10_1= 'always' | lv_stateInv_10_2= 'alw' ) ) ) | ( ( (lv_justice_11_1= 'GF' | lv_justice_11_2= 'alwEv' | lv_justice_11_3= 'alwaysEventually' ) ) ) )? ( (lv_temporalExpr_12_0= ruleQuantifierExpr ) ) )
                    // InternalSpectra.g:2384:5: (otherlv_7= 'ini' | otherlv_8= 'initially' | ( ( (lv_safety_9_1= 'G' | lv_safety_9_2= 'trans' ) ) ) | ( ( (lv_stateInv_10_1= 'always' | lv_stateInv_10_2= 'alw' ) ) ) | ( ( (lv_justice_11_1= 'GF' | lv_justice_11_2= 'alwEv' | lv_justice_11_3= 'alwaysEventually' ) ) ) )? ( (lv_temporalExpr_12_0= ruleQuantifierExpr ) )
                    {
                    // InternalSpectra.g:2384:5: (otherlv_7= 'ini' | otherlv_8= 'initially' | ( ( (lv_safety_9_1= 'G' | lv_safety_9_2= 'trans' ) ) ) | ( ( (lv_stateInv_10_1= 'always' | lv_stateInv_10_2= 'alw' ) ) ) | ( ( (lv_justice_11_1= 'GF' | lv_justice_11_2= 'alwEv' | lv_justice_11_3= 'alwaysEventually' ) ) ) )?
                    int alt43=6;
                    switch ( input.LA(1) ) {
                        case 36:
                            {
                            alt43=1;
                            }
                            break;
                        case 37:
                            {
                            alt43=2;
                            }
                            break;
                        case 38:
                        case 39:
                            {
                            alt43=3;
                            }
                            break;
                        case 40:
                        case 41:
                            {
                            alt43=4;
                            }
                            break;
                        case 44:
                        case 45:
                        case 46:
                            {
                            alt43=5;
                            }
                            break;
                    }

                    switch (alt43) {
                        case 1 :
                            // InternalSpectra.g:2385:6: otherlv_7= 'ini'
                            {
                            otherlv_7=(Token)match(input,36,FOLLOW_18); 

                            						newLeafNode(otherlv_7, grammarAccess.getLTLGarAccess().getIniKeyword_2_0_0_0());
                            					

                            }
                            break;
                        case 2 :
                            // InternalSpectra.g:2390:6: otherlv_8= 'initially'
                            {
                            otherlv_8=(Token)match(input,37,FOLLOW_18); 

                            						newLeafNode(otherlv_8, grammarAccess.getLTLGarAccess().getInitiallyKeyword_2_0_0_1());
                            					

                            }
                            break;
                        case 3 :
                            // InternalSpectra.g:2395:6: ( ( (lv_safety_9_1= 'G' | lv_safety_9_2= 'trans' ) ) )
                            {
                            // InternalSpectra.g:2395:6: ( ( (lv_safety_9_1= 'G' | lv_safety_9_2= 'trans' ) ) )
                            // InternalSpectra.g:2396:7: ( (lv_safety_9_1= 'G' | lv_safety_9_2= 'trans' ) )
                            {
                            // InternalSpectra.g:2396:7: ( (lv_safety_9_1= 'G' | lv_safety_9_2= 'trans' ) )
                            // InternalSpectra.g:2397:8: (lv_safety_9_1= 'G' | lv_safety_9_2= 'trans' )
                            {
                            // InternalSpectra.g:2397:8: (lv_safety_9_1= 'G' | lv_safety_9_2= 'trans' )
                            int alt40=2;
                            int LA40_0 = input.LA(1);

                            if ( (LA40_0==38) ) {
                                alt40=1;
                            }
                            else if ( (LA40_0==39) ) {
                                alt40=2;
                            }
                            else {
                                NoViableAltException nvae =
                                    new NoViableAltException("", 40, 0, input);

                                throw nvae;
                            }
                            switch (alt40) {
                                case 1 :
                                    // InternalSpectra.g:2398:9: lv_safety_9_1= 'G'
                                    {
                                    lv_safety_9_1=(Token)match(input,38,FOLLOW_18); 

                                    									newLeafNode(lv_safety_9_1, grammarAccess.getLTLGarAccess().getSafetyGKeyword_2_0_0_2_0_0());
                                    								

                                    									if (current==null) {
                                    										current = createModelElement(grammarAccess.getLTLGarRule());
                                    									}
                                    									setWithLastConsumed(current, "safety", lv_safety_9_1, null);
                                    								

                                    }
                                    break;
                                case 2 :
                                    // InternalSpectra.g:2409:9: lv_safety_9_2= 'trans'
                                    {
                                    lv_safety_9_2=(Token)match(input,39,FOLLOW_18); 

                                    									newLeafNode(lv_safety_9_2, grammarAccess.getLTLGarAccess().getSafetyTransKeyword_2_0_0_2_0_1());
                                    								

                                    									if (current==null) {
                                    										current = createModelElement(grammarAccess.getLTLGarRule());
                                    									}
                                    									setWithLastConsumed(current, "safety", lv_safety_9_2, null);
                                    								

                                    }
                                    break;

                            }


                            }


                            }


                            }
                            break;
                        case 4 :
                            // InternalSpectra.g:2423:6: ( ( (lv_stateInv_10_1= 'always' | lv_stateInv_10_2= 'alw' ) ) )
                            {
                            // InternalSpectra.g:2423:6: ( ( (lv_stateInv_10_1= 'always' | lv_stateInv_10_2= 'alw' ) ) )
                            // InternalSpectra.g:2424:7: ( (lv_stateInv_10_1= 'always' | lv_stateInv_10_2= 'alw' ) )
                            {
                            // InternalSpectra.g:2424:7: ( (lv_stateInv_10_1= 'always' | lv_stateInv_10_2= 'alw' ) )
                            // InternalSpectra.g:2425:8: (lv_stateInv_10_1= 'always' | lv_stateInv_10_2= 'alw' )
                            {
                            // InternalSpectra.g:2425:8: (lv_stateInv_10_1= 'always' | lv_stateInv_10_2= 'alw' )
                            int alt41=2;
                            int LA41_0 = input.LA(1);

                            if ( (LA41_0==40) ) {
                                alt41=1;
                            }
                            else if ( (LA41_0==41) ) {
                                alt41=2;
                            }
                            else {
                                NoViableAltException nvae =
                                    new NoViableAltException("", 41, 0, input);

                                throw nvae;
                            }
                            switch (alt41) {
                                case 1 :
                                    // InternalSpectra.g:2426:9: lv_stateInv_10_1= 'always'
                                    {
                                    lv_stateInv_10_1=(Token)match(input,40,FOLLOW_18); 

                                    									newLeafNode(lv_stateInv_10_1, grammarAccess.getLTLGarAccess().getStateInvAlwaysKeyword_2_0_0_3_0_0());
                                    								

                                    									if (current==null) {
                                    										current = createModelElement(grammarAccess.getLTLGarRule());
                                    									}
                                    									setWithLastConsumed(current, "stateInv", lv_stateInv_10_1, null);
                                    								

                                    }
                                    break;
                                case 2 :
                                    // InternalSpectra.g:2437:9: lv_stateInv_10_2= 'alw'
                                    {
                                    lv_stateInv_10_2=(Token)match(input,41,FOLLOW_18); 

                                    									newLeafNode(lv_stateInv_10_2, grammarAccess.getLTLGarAccess().getStateInvAlwKeyword_2_0_0_3_0_1());
                                    								

                                    									if (current==null) {
                                    										current = createModelElement(grammarAccess.getLTLGarRule());
                                    									}
                                    									setWithLastConsumed(current, "stateInv", lv_stateInv_10_2, null);
                                    								

                                    }
                                    break;

                            }


                            }


                            }


                            }
                            break;
                        case 5 :
                            // InternalSpectra.g:2451:6: ( ( (lv_justice_11_1= 'GF' | lv_justice_11_2= 'alwEv' | lv_justice_11_3= 'alwaysEventually' ) ) )
                            {
                            // InternalSpectra.g:2451:6: ( ( (lv_justice_11_1= 'GF' | lv_justice_11_2= 'alwEv' | lv_justice_11_3= 'alwaysEventually' ) ) )
                            // InternalSpectra.g:2452:7: ( (lv_justice_11_1= 'GF' | lv_justice_11_2= 'alwEv' | lv_justice_11_3= 'alwaysEventually' ) )
                            {
                            // InternalSpectra.g:2452:7: ( (lv_justice_11_1= 'GF' | lv_justice_11_2= 'alwEv' | lv_justice_11_3= 'alwaysEventually' ) )
                            // InternalSpectra.g:2453:8: (lv_justice_11_1= 'GF' | lv_justice_11_2= 'alwEv' | lv_justice_11_3= 'alwaysEventually' )
                            {
                            // InternalSpectra.g:2453:8: (lv_justice_11_1= 'GF' | lv_justice_11_2= 'alwEv' | lv_justice_11_3= 'alwaysEventually' )
                            int alt42=3;
                            switch ( input.LA(1) ) {
                            case 44:
                                {
                                alt42=1;
                                }
                                break;
                            case 45:
                                {
                                alt42=2;
                                }
                                break;
                            case 46:
                                {
                                alt42=3;
                                }
                                break;
                            default:
                                NoViableAltException nvae =
                                    new NoViableAltException("", 42, 0, input);

                                throw nvae;
                            }

                            switch (alt42) {
                                case 1 :
                                    // InternalSpectra.g:2454:9: lv_justice_11_1= 'GF'
                                    {
                                    lv_justice_11_1=(Token)match(input,44,FOLLOW_18); 

                                    									newLeafNode(lv_justice_11_1, grammarAccess.getLTLGarAccess().getJusticeGFKeyword_2_0_0_4_0_0());
                                    								

                                    									if (current==null) {
                                    										current = createModelElement(grammarAccess.getLTLGarRule());
                                    									}
                                    									setWithLastConsumed(current, "justice", lv_justice_11_1, null);
                                    								

                                    }
                                    break;
                                case 2 :
                                    // InternalSpectra.g:2465:9: lv_justice_11_2= 'alwEv'
                                    {
                                    lv_justice_11_2=(Token)match(input,45,FOLLOW_18); 

                                    									newLeafNode(lv_justice_11_2, grammarAccess.getLTLGarAccess().getJusticeAlwEvKeyword_2_0_0_4_0_1());
                                    								

                                    									if (current==null) {
                                    										current = createModelElement(grammarAccess.getLTLGarRule());
                                    									}
                                    									setWithLastConsumed(current, "justice", lv_justice_11_2, null);
                                    								

                                    }
                                    break;
                                case 3 :
                                    // InternalSpectra.g:2476:9: lv_justice_11_3= 'alwaysEventually'
                                    {
                                    lv_justice_11_3=(Token)match(input,46,FOLLOW_18); 

                                    									newLeafNode(lv_justice_11_3, grammarAccess.getLTLGarAccess().getJusticeAlwaysEventuallyKeyword_2_0_0_4_0_2());
                                    								

                                    									if (current==null) {
                                    										current = createModelElement(grammarAccess.getLTLGarRule());
                                    									}
                                    									setWithLastConsumed(current, "justice", lv_justice_11_3, null);
                                    								

                                    }
                                    break;

                            }


                            }


                            }


                            }
                            break;

                    }

                    // InternalSpectra.g:2490:5: ( (lv_temporalExpr_12_0= ruleQuantifierExpr ) )
                    // InternalSpectra.g:2491:6: (lv_temporalExpr_12_0= ruleQuantifierExpr )
                    {
                    // InternalSpectra.g:2491:6: (lv_temporalExpr_12_0= ruleQuantifierExpr )
                    // InternalSpectra.g:2492:7: lv_temporalExpr_12_0= ruleQuantifierExpr
                    {

                    							newCompositeNode(grammarAccess.getLTLGarAccess().getTemporalExprQuantifierExprParserRuleCall_2_0_1_0());
                    						
                    pushFollow(FOLLOW_13);
                    lv_temporalExpr_12_0=ruleQuantifierExpr();

                    state._fsp--;


                    							if (current==null) {
                    								current = createModelElementForParent(grammarAccess.getLTLGarRule());
                    							}
                    							set(
                    								current,
                    								"temporalExpr",
                    								lv_temporalExpr_12_0,
                    								"tau.smlab.syntech.Spectra.QuantifierExpr");
                    							afterParserOrEnumRuleCall();
                    						

                    }


                    }


                    }


                    }
                    break;
                case 2 :
                    // InternalSpectra.g:2511:4: ( (lv_trig_13_0= ruleTrigger ) )
                    {
                    // InternalSpectra.g:2511:4: ( (lv_trig_13_0= ruleTrigger ) )
                    // InternalSpectra.g:2512:5: (lv_trig_13_0= ruleTrigger )
                    {
                    // InternalSpectra.g:2512:5: (lv_trig_13_0= ruleTrigger )
                    // InternalSpectra.g:2513:6: lv_trig_13_0= ruleTrigger
                    {

                    						newCompositeNode(grammarAccess.getLTLGarAccess().getTrigTriggerParserRuleCall_2_1_0());
                    					
                    pushFollow(FOLLOW_13);
                    lv_trig_13_0=ruleTrigger();

                    state._fsp--;


                    						if (current==null) {
                    							current = createModelElementForParent(grammarAccess.getLTLGarRule());
                    						}
                    						set(
                    							current,
                    							"trig",
                    							lv_trig_13_0,
                    							"tau.smlab.syntech.Spectra.Trigger");
                    						afterParserOrEnumRuleCall();
                    					

                    }


                    }


                    }
                    break;

            }


            			newCompositeNode(grammarAccess.getLTLGarAccess().getTOK_SEMIParserRuleCall_3());
            		
            pushFollow(FOLLOW_2);
            ruleTOK_SEMI();

            state._fsp--;


            			afterParserOrEnumRuleCall();
            		

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleLTLGar"


    // $ANTLR start "entryRuleLTLAsm"
    // InternalSpectra.g:2542:1: entryRuleLTLAsm returns [EObject current=null] : iv_ruleLTLAsm= ruleLTLAsm EOF ;
    public final EObject entryRuleLTLAsm() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleLTLAsm = null;


        try {
            // InternalSpectra.g:2542:47: (iv_ruleLTLAsm= ruleLTLAsm EOF )
            // InternalSpectra.g:2543:2: iv_ruleLTLAsm= ruleLTLAsm EOF
            {
             newCompositeNode(grammarAccess.getLTLAsmRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleLTLAsm=ruleLTLAsm();

            state._fsp--;

             current =iv_ruleLTLAsm; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleLTLAsm"


    // $ANTLR start "ruleLTLAsm"
    // InternalSpectra.g:2549:1: ruleLTLAsm returns [EObject current=null] : ( (otherlv_0= 'assumption' | otherlv_1= 'asm' ) ( ( (lv_name_2_0= RULE_ID ) ) (otherlv_3= '{' ( (lv_params_4_0= ruleTypedParamList ) ) otherlv_5= '}' )? otherlv_6= ':' )? ( ( (otherlv_7= 'ini' | otherlv_8= 'initially' | ( ( (lv_safety_9_1= 'G' | lv_safety_9_2= 'trans' ) ) ) | ( ( (lv_stateInv_10_1= 'always' | lv_stateInv_10_2= 'alw' ) ) ) | ( ( (lv_justice_11_1= 'GF' | lv_justice_11_2= 'alwEv' | lv_justice_11_3= 'alwaysEventually' ) ) ) )? ( (lv_temporalExpr_12_0= ruleQuantifierExpr ) ) ) | ( (lv_trig_13_0= ruleTrigger ) ) ) ruleTOK_SEMI ) ;
    public final EObject ruleLTLAsm() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_1=null;
        Token lv_name_2_0=null;
        Token otherlv_3=null;
        Token otherlv_5=null;
        Token otherlv_6=null;
        Token otherlv_7=null;
        Token otherlv_8=null;
        Token lv_safety_9_1=null;
        Token lv_safety_9_2=null;
        Token lv_stateInv_10_1=null;
        Token lv_stateInv_10_2=null;
        Token lv_justice_11_1=null;
        Token lv_justice_11_2=null;
        Token lv_justice_11_3=null;
        EObject lv_params_4_0 = null;

        EObject lv_temporalExpr_12_0 = null;

        EObject lv_trig_13_0 = null;



        	enterRule();

        try {
            // InternalSpectra.g:2555:2: ( ( (otherlv_0= 'assumption' | otherlv_1= 'asm' ) ( ( (lv_name_2_0= RULE_ID ) ) (otherlv_3= '{' ( (lv_params_4_0= ruleTypedParamList ) ) otherlv_5= '}' )? otherlv_6= ':' )? ( ( (otherlv_7= 'ini' | otherlv_8= 'initially' | ( ( (lv_safety_9_1= 'G' | lv_safety_9_2= 'trans' ) ) ) | ( ( (lv_stateInv_10_1= 'always' | lv_stateInv_10_2= 'alw' ) ) ) | ( ( (lv_justice_11_1= 'GF' | lv_justice_11_2= 'alwEv' | lv_justice_11_3= 'alwaysEventually' ) ) ) )? ( (lv_temporalExpr_12_0= ruleQuantifierExpr ) ) ) | ( (lv_trig_13_0= ruleTrigger ) ) ) ruleTOK_SEMI ) )
            // InternalSpectra.g:2556:2: ( (otherlv_0= 'assumption' | otherlv_1= 'asm' ) ( ( (lv_name_2_0= RULE_ID ) ) (otherlv_3= '{' ( (lv_params_4_0= ruleTypedParamList ) ) otherlv_5= '}' )? otherlv_6= ':' )? ( ( (otherlv_7= 'ini' | otherlv_8= 'initially' | ( ( (lv_safety_9_1= 'G' | lv_safety_9_2= 'trans' ) ) ) | ( ( (lv_stateInv_10_1= 'always' | lv_stateInv_10_2= 'alw' ) ) ) | ( ( (lv_justice_11_1= 'GF' | lv_justice_11_2= 'alwEv' | lv_justice_11_3= 'alwaysEventually' ) ) ) )? ( (lv_temporalExpr_12_0= ruleQuantifierExpr ) ) ) | ( (lv_trig_13_0= ruleTrigger ) ) ) ruleTOK_SEMI )
            {
            // InternalSpectra.g:2556:2: ( (otherlv_0= 'assumption' | otherlv_1= 'asm' ) ( ( (lv_name_2_0= RULE_ID ) ) (otherlv_3= '{' ( (lv_params_4_0= ruleTypedParamList ) ) otherlv_5= '}' )? otherlv_6= ':' )? ( ( (otherlv_7= 'ini' | otherlv_8= 'initially' | ( ( (lv_safety_9_1= 'G' | lv_safety_9_2= 'trans' ) ) ) | ( ( (lv_stateInv_10_1= 'always' | lv_stateInv_10_2= 'alw' ) ) ) | ( ( (lv_justice_11_1= 'GF' | lv_justice_11_2= 'alwEv' | lv_justice_11_3= 'alwaysEventually' ) ) ) )? ( (lv_temporalExpr_12_0= ruleQuantifierExpr ) ) ) | ( (lv_trig_13_0= ruleTrigger ) ) ) ruleTOK_SEMI )
            // InternalSpectra.g:2557:3: (otherlv_0= 'assumption' | otherlv_1= 'asm' ) ( ( (lv_name_2_0= RULE_ID ) ) (otherlv_3= '{' ( (lv_params_4_0= ruleTypedParamList ) ) otherlv_5= '}' )? otherlv_6= ':' )? ( ( (otherlv_7= 'ini' | otherlv_8= 'initially' | ( ( (lv_safety_9_1= 'G' | lv_safety_9_2= 'trans' ) ) ) | ( ( (lv_stateInv_10_1= 'always' | lv_stateInv_10_2= 'alw' ) ) ) | ( ( (lv_justice_11_1= 'GF' | lv_justice_11_2= 'alwEv' | lv_justice_11_3= 'alwaysEventually' ) ) ) )? ( (lv_temporalExpr_12_0= ruleQuantifierExpr ) ) ) | ( (lv_trig_13_0= ruleTrigger ) ) ) ruleTOK_SEMI
            {
            // InternalSpectra.g:2557:3: (otherlv_0= 'assumption' | otherlv_1= 'asm' )
            int alt45=2;
            int LA45_0 = input.LA(1);

            if ( (LA45_0==53) ) {
                alt45=1;
            }
            else if ( (LA45_0==54) ) {
                alt45=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 45, 0, input);

                throw nvae;
            }
            switch (alt45) {
                case 1 :
                    // InternalSpectra.g:2558:4: otherlv_0= 'assumption'
                    {
                    otherlv_0=(Token)match(input,53,FOLLOW_40); 

                    				newLeafNode(otherlv_0, grammarAccess.getLTLAsmAccess().getAssumptionKeyword_0_0());
                    			

                    }
                    break;
                case 2 :
                    // InternalSpectra.g:2563:4: otherlv_1= 'asm'
                    {
                    otherlv_1=(Token)match(input,54,FOLLOW_40); 

                    				newLeafNode(otherlv_1, grammarAccess.getLTLAsmAccess().getAsmKeyword_0_1());
                    			

                    }
                    break;

            }

            // InternalSpectra.g:2568:3: ( ( (lv_name_2_0= RULE_ID ) ) (otherlv_3= '{' ( (lv_params_4_0= ruleTypedParamList ) ) otherlv_5= '}' )? otherlv_6= ':' )?
            int alt47=2;
            int LA47_0 = input.LA(1);

            if ( (LA47_0==RULE_ID) ) {
                int LA47_1 = input.LA(2);

                if ( (LA47_1==15||LA47_1==23) ) {
                    alt47=1;
                }
            }
            switch (alt47) {
                case 1 :
                    // InternalSpectra.g:2569:4: ( (lv_name_2_0= RULE_ID ) ) (otherlv_3= '{' ( (lv_params_4_0= ruleTypedParamList ) ) otherlv_5= '}' )? otherlv_6= ':'
                    {
                    // InternalSpectra.g:2569:4: ( (lv_name_2_0= RULE_ID ) )
                    // InternalSpectra.g:2570:5: (lv_name_2_0= RULE_ID )
                    {
                    // InternalSpectra.g:2570:5: (lv_name_2_0= RULE_ID )
                    // InternalSpectra.g:2571:6: lv_name_2_0= RULE_ID
                    {
                    lv_name_2_0=(Token)match(input,RULE_ID,FOLLOW_36); 

                    						newLeafNode(lv_name_2_0, grammarAccess.getLTLAsmAccess().getNameIDTerminalRuleCall_1_0_0());
                    					

                    						if (current==null) {
                    							current = createModelElement(grammarAccess.getLTLAsmRule());
                    						}
                    						setWithLastConsumed(
                    							current,
                    							"name",
                    							lv_name_2_0,
                    							"org.eclipse.xtext.common.Terminals.ID");
                    					

                    }


                    }

                    // InternalSpectra.g:2587:4: (otherlv_3= '{' ( (lv_params_4_0= ruleTypedParamList ) ) otherlv_5= '}' )?
                    int alt46=2;
                    int LA46_0 = input.LA(1);

                    if ( (LA46_0==23) ) {
                        alt46=1;
                    }
                    switch (alt46) {
                        case 1 :
                            // InternalSpectra.g:2588:5: otherlv_3= '{' ( (lv_params_4_0= ruleTypedParamList ) ) otherlv_5= '}'
                            {
                            otherlv_3=(Token)match(input,23,FOLLOW_14); 

                            					newLeafNode(otherlv_3, grammarAccess.getLTLAsmAccess().getLeftCurlyBracketKeyword_1_1_0());
                            				
                            // InternalSpectra.g:2592:5: ( (lv_params_4_0= ruleTypedParamList ) )
                            // InternalSpectra.g:2593:6: (lv_params_4_0= ruleTypedParamList )
                            {
                            // InternalSpectra.g:2593:6: (lv_params_4_0= ruleTypedParamList )
                            // InternalSpectra.g:2594:7: lv_params_4_0= ruleTypedParamList
                            {

                            							newCompositeNode(grammarAccess.getLTLAsmAccess().getParamsTypedParamListParserRuleCall_1_1_1_0());
                            						
                            pushFollow(FOLLOW_37);
                            lv_params_4_0=ruleTypedParamList();

                            state._fsp--;


                            							if (current==null) {
                            								current = createModelElementForParent(grammarAccess.getLTLAsmRule());
                            							}
                            							set(
                            								current,
                            								"params",
                            								lv_params_4_0,
                            								"tau.smlab.syntech.Spectra.TypedParamList");
                            							afterParserOrEnumRuleCall();
                            						

                            }


                            }

                            otherlv_5=(Token)match(input,25,FOLLOW_9); 

                            					newLeafNode(otherlv_5, grammarAccess.getLTLAsmAccess().getRightCurlyBracketKeyword_1_1_2());
                            				

                            }
                            break;

                    }

                    otherlv_6=(Token)match(input,15,FOLLOW_40); 

                    				newLeafNode(otherlv_6, grammarAccess.getLTLAsmAccess().getColonKeyword_1_2());
                    			

                    }
                    break;

            }

            // InternalSpectra.g:2621:3: ( ( (otherlv_7= 'ini' | otherlv_8= 'initially' | ( ( (lv_safety_9_1= 'G' | lv_safety_9_2= 'trans' ) ) ) | ( ( (lv_stateInv_10_1= 'always' | lv_stateInv_10_2= 'alw' ) ) ) | ( ( (lv_justice_11_1= 'GF' | lv_justice_11_2= 'alwEv' | lv_justice_11_3= 'alwaysEventually' ) ) ) )? ( (lv_temporalExpr_12_0= ruleQuantifierExpr ) ) ) | ( (lv_trig_13_0= ruleTrigger ) ) )
            int alt52=2;
            int LA52_0 = input.LA(1);

            if ( (LA52_0==RULE_ID||LA52_0==RULE_INT||LA52_0==16||LA52_0==26||LA52_0==28||(LA52_0>=36 && LA52_0<=41)||(LA52_0>=44 && LA52_0<=46)||(LA52_0>=82 && LA52_0<=89)||(LA52_0>=97 && LA52_0<=100)||(LA52_0>=105 && LA52_0<=106)) ) {
                alt52=1;
            }
            else if ( (LA52_0==101) ) {
                alt52=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 52, 0, input);

                throw nvae;
            }
            switch (alt52) {
                case 1 :
                    // InternalSpectra.g:2622:4: ( (otherlv_7= 'ini' | otherlv_8= 'initially' | ( ( (lv_safety_9_1= 'G' | lv_safety_9_2= 'trans' ) ) ) | ( ( (lv_stateInv_10_1= 'always' | lv_stateInv_10_2= 'alw' ) ) ) | ( ( (lv_justice_11_1= 'GF' | lv_justice_11_2= 'alwEv' | lv_justice_11_3= 'alwaysEventually' ) ) ) )? ( (lv_temporalExpr_12_0= ruleQuantifierExpr ) ) )
                    {
                    // InternalSpectra.g:2622:4: ( (otherlv_7= 'ini' | otherlv_8= 'initially' | ( ( (lv_safety_9_1= 'G' | lv_safety_9_2= 'trans' ) ) ) | ( ( (lv_stateInv_10_1= 'always' | lv_stateInv_10_2= 'alw' ) ) ) | ( ( (lv_justice_11_1= 'GF' | lv_justice_11_2= 'alwEv' | lv_justice_11_3= 'alwaysEventually' ) ) ) )? ( (lv_temporalExpr_12_0= ruleQuantifierExpr ) ) )
                    // InternalSpectra.g:2623:5: (otherlv_7= 'ini' | otherlv_8= 'initially' | ( ( (lv_safety_9_1= 'G' | lv_safety_9_2= 'trans' ) ) ) | ( ( (lv_stateInv_10_1= 'always' | lv_stateInv_10_2= 'alw' ) ) ) | ( ( (lv_justice_11_1= 'GF' | lv_justice_11_2= 'alwEv' | lv_justice_11_3= 'alwaysEventually' ) ) ) )? ( (lv_temporalExpr_12_0= ruleQuantifierExpr ) )
                    {
                    // InternalSpectra.g:2623:5: (otherlv_7= 'ini' | otherlv_8= 'initially' | ( ( (lv_safety_9_1= 'G' | lv_safety_9_2= 'trans' ) ) ) | ( ( (lv_stateInv_10_1= 'always' | lv_stateInv_10_2= 'alw' ) ) ) | ( ( (lv_justice_11_1= 'GF' | lv_justice_11_2= 'alwEv' | lv_justice_11_3= 'alwaysEventually' ) ) ) )?
                    int alt51=6;
                    switch ( input.LA(1) ) {
                        case 36:
                            {
                            alt51=1;
                            }
                            break;
                        case 37:
                            {
                            alt51=2;
                            }
                            break;
                        case 38:
                        case 39:
                            {
                            alt51=3;
                            }
                            break;
                        case 40:
                        case 41:
                            {
                            alt51=4;
                            }
                            break;
                        case 44:
                        case 45:
                        case 46:
                            {
                            alt51=5;
                            }
                            break;
                    }

                    switch (alt51) {
                        case 1 :
                            // InternalSpectra.g:2624:6: otherlv_7= 'ini'
                            {
                            otherlv_7=(Token)match(input,36,FOLLOW_18); 

                            						newLeafNode(otherlv_7, grammarAccess.getLTLAsmAccess().getIniKeyword_2_0_0_0());
                            					

                            }
                            break;
                        case 2 :
                            // InternalSpectra.g:2629:6: otherlv_8= 'initially'
                            {
                            otherlv_8=(Token)match(input,37,FOLLOW_18); 

                            						newLeafNode(otherlv_8, grammarAccess.getLTLAsmAccess().getInitiallyKeyword_2_0_0_1());
                            					

                            }
                            break;
                        case 3 :
                            // InternalSpectra.g:2634:6: ( ( (lv_safety_9_1= 'G' | lv_safety_9_2= 'trans' ) ) )
                            {
                            // InternalSpectra.g:2634:6: ( ( (lv_safety_9_1= 'G' | lv_safety_9_2= 'trans' ) ) )
                            // InternalSpectra.g:2635:7: ( (lv_safety_9_1= 'G' | lv_safety_9_2= 'trans' ) )
                            {
                            // InternalSpectra.g:2635:7: ( (lv_safety_9_1= 'G' | lv_safety_9_2= 'trans' ) )
                            // InternalSpectra.g:2636:8: (lv_safety_9_1= 'G' | lv_safety_9_2= 'trans' )
                            {
                            // InternalSpectra.g:2636:8: (lv_safety_9_1= 'G' | lv_safety_9_2= 'trans' )
                            int alt48=2;
                            int LA48_0 = input.LA(1);

                            if ( (LA48_0==38) ) {
                                alt48=1;
                            }
                            else if ( (LA48_0==39) ) {
                                alt48=2;
                            }
                            else {
                                NoViableAltException nvae =
                                    new NoViableAltException("", 48, 0, input);

                                throw nvae;
                            }
                            switch (alt48) {
                                case 1 :
                                    // InternalSpectra.g:2637:9: lv_safety_9_1= 'G'
                                    {
                                    lv_safety_9_1=(Token)match(input,38,FOLLOW_18); 

                                    									newLeafNode(lv_safety_9_1, grammarAccess.getLTLAsmAccess().getSafetyGKeyword_2_0_0_2_0_0());
                                    								

                                    									if (current==null) {
                                    										current = createModelElement(grammarAccess.getLTLAsmRule());
                                    									}
                                    									setWithLastConsumed(current, "safety", lv_safety_9_1, null);
                                    								

                                    }
                                    break;
                                case 2 :
                                    // InternalSpectra.g:2648:9: lv_safety_9_2= 'trans'
                                    {
                                    lv_safety_9_2=(Token)match(input,39,FOLLOW_18); 

                                    									newLeafNode(lv_safety_9_2, grammarAccess.getLTLAsmAccess().getSafetyTransKeyword_2_0_0_2_0_1());
                                    								

                                    									if (current==null) {
                                    										current = createModelElement(grammarAccess.getLTLAsmRule());
                                    									}
                                    									setWithLastConsumed(current, "safety", lv_safety_9_2, null);
                                    								

                                    }
                                    break;

                            }


                            }


                            }


                            }
                            break;
                        case 4 :
                            // InternalSpectra.g:2662:6: ( ( (lv_stateInv_10_1= 'always' | lv_stateInv_10_2= 'alw' ) ) )
                            {
                            // InternalSpectra.g:2662:6: ( ( (lv_stateInv_10_1= 'always' | lv_stateInv_10_2= 'alw' ) ) )
                            // InternalSpectra.g:2663:7: ( (lv_stateInv_10_1= 'always' | lv_stateInv_10_2= 'alw' ) )
                            {
                            // InternalSpectra.g:2663:7: ( (lv_stateInv_10_1= 'always' | lv_stateInv_10_2= 'alw' ) )
                            // InternalSpectra.g:2664:8: (lv_stateInv_10_1= 'always' | lv_stateInv_10_2= 'alw' )
                            {
                            // InternalSpectra.g:2664:8: (lv_stateInv_10_1= 'always' | lv_stateInv_10_2= 'alw' )
                            int alt49=2;
                            int LA49_0 = input.LA(1);

                            if ( (LA49_0==40) ) {
                                alt49=1;
                            }
                            else if ( (LA49_0==41) ) {
                                alt49=2;
                            }
                            else {
                                NoViableAltException nvae =
                                    new NoViableAltException("", 49, 0, input);

                                throw nvae;
                            }
                            switch (alt49) {
                                case 1 :
                                    // InternalSpectra.g:2665:9: lv_stateInv_10_1= 'always'
                                    {
                                    lv_stateInv_10_1=(Token)match(input,40,FOLLOW_18); 

                                    									newLeafNode(lv_stateInv_10_1, grammarAccess.getLTLAsmAccess().getStateInvAlwaysKeyword_2_0_0_3_0_0());
                                    								

                                    									if (current==null) {
                                    										current = createModelElement(grammarAccess.getLTLAsmRule());
                                    									}
                                    									setWithLastConsumed(current, "stateInv", lv_stateInv_10_1, null);
                                    								

                                    }
                                    break;
                                case 2 :
                                    // InternalSpectra.g:2676:9: lv_stateInv_10_2= 'alw'
                                    {
                                    lv_stateInv_10_2=(Token)match(input,41,FOLLOW_18); 

                                    									newLeafNode(lv_stateInv_10_2, grammarAccess.getLTLAsmAccess().getStateInvAlwKeyword_2_0_0_3_0_1());
                                    								

                                    									if (current==null) {
                                    										current = createModelElement(grammarAccess.getLTLAsmRule());
                                    									}
                                    									setWithLastConsumed(current, "stateInv", lv_stateInv_10_2, null);
                                    								

                                    }
                                    break;

                            }


                            }


                            }


                            }
                            break;
                        case 5 :
                            // InternalSpectra.g:2690:6: ( ( (lv_justice_11_1= 'GF' | lv_justice_11_2= 'alwEv' | lv_justice_11_3= 'alwaysEventually' ) ) )
                            {
                            // InternalSpectra.g:2690:6: ( ( (lv_justice_11_1= 'GF' | lv_justice_11_2= 'alwEv' | lv_justice_11_3= 'alwaysEventually' ) ) )
                            // InternalSpectra.g:2691:7: ( (lv_justice_11_1= 'GF' | lv_justice_11_2= 'alwEv' | lv_justice_11_3= 'alwaysEventually' ) )
                            {
                            // InternalSpectra.g:2691:7: ( (lv_justice_11_1= 'GF' | lv_justice_11_2= 'alwEv' | lv_justice_11_3= 'alwaysEventually' ) )
                            // InternalSpectra.g:2692:8: (lv_justice_11_1= 'GF' | lv_justice_11_2= 'alwEv' | lv_justice_11_3= 'alwaysEventually' )
                            {
                            // InternalSpectra.g:2692:8: (lv_justice_11_1= 'GF' | lv_justice_11_2= 'alwEv' | lv_justice_11_3= 'alwaysEventually' )
                            int alt50=3;
                            switch ( input.LA(1) ) {
                            case 44:
                                {
                                alt50=1;
                                }
                                break;
                            case 45:
                                {
                                alt50=2;
                                }
                                break;
                            case 46:
                                {
                                alt50=3;
                                }
                                break;
                            default:
                                NoViableAltException nvae =
                                    new NoViableAltException("", 50, 0, input);

                                throw nvae;
                            }

                            switch (alt50) {
                                case 1 :
                                    // InternalSpectra.g:2693:9: lv_justice_11_1= 'GF'
                                    {
                                    lv_justice_11_1=(Token)match(input,44,FOLLOW_18); 

                                    									newLeafNode(lv_justice_11_1, grammarAccess.getLTLAsmAccess().getJusticeGFKeyword_2_0_0_4_0_0());
                                    								

                                    									if (current==null) {
                                    										current = createModelElement(grammarAccess.getLTLAsmRule());
                                    									}
                                    									setWithLastConsumed(current, "justice", lv_justice_11_1, null);
                                    								

                                    }
                                    break;
                                case 2 :
                                    // InternalSpectra.g:2704:9: lv_justice_11_2= 'alwEv'
                                    {
                                    lv_justice_11_2=(Token)match(input,45,FOLLOW_18); 

                                    									newLeafNode(lv_justice_11_2, grammarAccess.getLTLAsmAccess().getJusticeAlwEvKeyword_2_0_0_4_0_1());
                                    								

                                    									if (current==null) {
                                    										current = createModelElement(grammarAccess.getLTLAsmRule());
                                    									}
                                    									setWithLastConsumed(current, "justice", lv_justice_11_2, null);
                                    								

                                    }
                                    break;
                                case 3 :
                                    // InternalSpectra.g:2715:9: lv_justice_11_3= 'alwaysEventually'
                                    {
                                    lv_justice_11_3=(Token)match(input,46,FOLLOW_18); 

                                    									newLeafNode(lv_justice_11_3, grammarAccess.getLTLAsmAccess().getJusticeAlwaysEventuallyKeyword_2_0_0_4_0_2());
                                    								

                                    									if (current==null) {
                                    										current = createModelElement(grammarAccess.getLTLAsmRule());
                                    									}
                                    									setWithLastConsumed(current, "justice", lv_justice_11_3, null);
                                    								

                                    }
                                    break;

                            }


                            }


                            }


                            }
                            break;

                    }

                    // InternalSpectra.g:2729:5: ( (lv_temporalExpr_12_0= ruleQuantifierExpr ) )
                    // InternalSpectra.g:2730:6: (lv_temporalExpr_12_0= ruleQuantifierExpr )
                    {
                    // InternalSpectra.g:2730:6: (lv_temporalExpr_12_0= ruleQuantifierExpr )
                    // InternalSpectra.g:2731:7: lv_temporalExpr_12_0= ruleQuantifierExpr
                    {

                    							newCompositeNode(grammarAccess.getLTLAsmAccess().getTemporalExprQuantifierExprParserRuleCall_2_0_1_0());
                    						
                    pushFollow(FOLLOW_13);
                    lv_temporalExpr_12_0=ruleQuantifierExpr();

                    state._fsp--;


                    							if (current==null) {
                    								current = createModelElementForParent(grammarAccess.getLTLAsmRule());
                    							}
                    							set(
                    								current,
                    								"temporalExpr",
                    								lv_temporalExpr_12_0,
                    								"tau.smlab.syntech.Spectra.QuantifierExpr");
                    							afterParserOrEnumRuleCall();
                    						

                    }


                    }


                    }


                    }
                    break;
                case 2 :
                    // InternalSpectra.g:2750:4: ( (lv_trig_13_0= ruleTrigger ) )
                    {
                    // InternalSpectra.g:2750:4: ( (lv_trig_13_0= ruleTrigger ) )
                    // InternalSpectra.g:2751:5: (lv_trig_13_0= ruleTrigger )
                    {
                    // InternalSpectra.g:2751:5: (lv_trig_13_0= ruleTrigger )
                    // InternalSpectra.g:2752:6: lv_trig_13_0= ruleTrigger
                    {

                    						newCompositeNode(grammarAccess.getLTLAsmAccess().getTrigTriggerParserRuleCall_2_1_0());
                    					
                    pushFollow(FOLLOW_13);
                    lv_trig_13_0=ruleTrigger();

                    state._fsp--;


                    						if (current==null) {
                    							current = createModelElementForParent(grammarAccess.getLTLAsmRule());
                    						}
                    						set(
                    							current,
                    							"trig",
                    							lv_trig_13_0,
                    							"tau.smlab.syntech.Spectra.Trigger");
                    						afterParserOrEnumRuleCall();
                    					

                    }


                    }


                    }
                    break;

            }


            			newCompositeNode(grammarAccess.getLTLAsmAccess().getTOK_SEMIParserRuleCall_3());
            		
            pushFollow(FOLLOW_2);
            ruleTOK_SEMI();

            state._fsp--;


            			afterParserOrEnumRuleCall();
            		

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleLTLAsm"


    // $ANTLR start "entryRuleEXGar"
    // InternalSpectra.g:2781:1: entryRuleEXGar returns [EObject current=null] : iv_ruleEXGar= ruleEXGar EOF ;
    public final EObject entryRuleEXGar() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleEXGar = null;


        try {
            // InternalSpectra.g:2781:46: (iv_ruleEXGar= ruleEXGar EOF )
            // InternalSpectra.g:2782:2: iv_ruleEXGar= ruleEXGar EOF
            {
             newCompositeNode(grammarAccess.getEXGarRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleEXGar=ruleEXGar();

            state._fsp--;

             current =iv_ruleEXGar; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleEXGar"


    // $ANTLR start "ruleEXGar"
    // InternalSpectra.g:2788:1: ruleEXGar returns [EObject current=null] : ( (otherlv_0= 'guarantee' | otherlv_1= 'gar' ) ( ( (lv_name_2_0= RULE_ID ) ) otherlv_3= ':' )? ( (otherlv_4= 'GE' ( (lv_elements_5_0= ruleTemporalInExpr ) ) (otherlv_6= ',' ( (lv_elements_7_0= ruleTemporalInExpr ) ) )* ) | (otherlv_8= 'GEF' ( (otherlv_9= RULE_ID ) ) ) | (otherlv_10= 'GEF' ( (lv_regExp_11_0= ruleRegExp ) ) ) ) ruleTOK_SEMI ) ;
    public final EObject ruleEXGar() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_1=null;
        Token lv_name_2_0=null;
        Token otherlv_3=null;
        Token otherlv_4=null;
        Token otherlv_6=null;
        Token otherlv_8=null;
        Token otherlv_9=null;
        Token otherlv_10=null;
        EObject lv_elements_5_0 = null;

        EObject lv_elements_7_0 = null;

        EObject lv_regExp_11_0 = null;



        	enterRule();

        try {
            // InternalSpectra.g:2794:2: ( ( (otherlv_0= 'guarantee' | otherlv_1= 'gar' ) ( ( (lv_name_2_0= RULE_ID ) ) otherlv_3= ':' )? ( (otherlv_4= 'GE' ( (lv_elements_5_0= ruleTemporalInExpr ) ) (otherlv_6= ',' ( (lv_elements_7_0= ruleTemporalInExpr ) ) )* ) | (otherlv_8= 'GEF' ( (otherlv_9= RULE_ID ) ) ) | (otherlv_10= 'GEF' ( (lv_regExp_11_0= ruleRegExp ) ) ) ) ruleTOK_SEMI ) )
            // InternalSpectra.g:2795:2: ( (otherlv_0= 'guarantee' | otherlv_1= 'gar' ) ( ( (lv_name_2_0= RULE_ID ) ) otherlv_3= ':' )? ( (otherlv_4= 'GE' ( (lv_elements_5_0= ruleTemporalInExpr ) ) (otherlv_6= ',' ( (lv_elements_7_0= ruleTemporalInExpr ) ) )* ) | (otherlv_8= 'GEF' ( (otherlv_9= RULE_ID ) ) ) | (otherlv_10= 'GEF' ( (lv_regExp_11_0= ruleRegExp ) ) ) ) ruleTOK_SEMI )
            {
            // InternalSpectra.g:2795:2: ( (otherlv_0= 'guarantee' | otherlv_1= 'gar' ) ( ( (lv_name_2_0= RULE_ID ) ) otherlv_3= ':' )? ( (otherlv_4= 'GE' ( (lv_elements_5_0= ruleTemporalInExpr ) ) (otherlv_6= ',' ( (lv_elements_7_0= ruleTemporalInExpr ) ) )* ) | (otherlv_8= 'GEF' ( (otherlv_9= RULE_ID ) ) ) | (otherlv_10= 'GEF' ( (lv_regExp_11_0= ruleRegExp ) ) ) ) ruleTOK_SEMI )
            // InternalSpectra.g:2796:3: (otherlv_0= 'guarantee' | otherlv_1= 'gar' ) ( ( (lv_name_2_0= RULE_ID ) ) otherlv_3= ':' )? ( (otherlv_4= 'GE' ( (lv_elements_5_0= ruleTemporalInExpr ) ) (otherlv_6= ',' ( (lv_elements_7_0= ruleTemporalInExpr ) ) )* ) | (otherlv_8= 'GEF' ( (otherlv_9= RULE_ID ) ) ) | (otherlv_10= 'GEF' ( (lv_regExp_11_0= ruleRegExp ) ) ) ) ruleTOK_SEMI
            {
            // InternalSpectra.g:2796:3: (otherlv_0= 'guarantee' | otherlv_1= 'gar' )
            int alt53=2;
            int LA53_0 = input.LA(1);

            if ( (LA53_0==51) ) {
                alt53=1;
            }
            else if ( (LA53_0==52) ) {
                alt53=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 53, 0, input);

                throw nvae;
            }
            switch (alt53) {
                case 1 :
                    // InternalSpectra.g:2797:4: otherlv_0= 'guarantee'
                    {
                    otherlv_0=(Token)match(input,51,FOLLOW_41); 

                    				newLeafNode(otherlv_0, grammarAccess.getEXGarAccess().getGuaranteeKeyword_0_0());
                    			

                    }
                    break;
                case 2 :
                    // InternalSpectra.g:2802:4: otherlv_1= 'gar'
                    {
                    otherlv_1=(Token)match(input,52,FOLLOW_41); 

                    				newLeafNode(otherlv_1, grammarAccess.getEXGarAccess().getGarKeyword_0_1());
                    			

                    }
                    break;

            }

            // InternalSpectra.g:2807:3: ( ( (lv_name_2_0= RULE_ID ) ) otherlv_3= ':' )?
            int alt54=2;
            int LA54_0 = input.LA(1);

            if ( (LA54_0==RULE_ID) ) {
                alt54=1;
            }
            switch (alt54) {
                case 1 :
                    // InternalSpectra.g:2808:4: ( (lv_name_2_0= RULE_ID ) ) otherlv_3= ':'
                    {
                    // InternalSpectra.g:2808:4: ( (lv_name_2_0= RULE_ID ) )
                    // InternalSpectra.g:2809:5: (lv_name_2_0= RULE_ID )
                    {
                    // InternalSpectra.g:2809:5: (lv_name_2_0= RULE_ID )
                    // InternalSpectra.g:2810:6: lv_name_2_0= RULE_ID
                    {
                    lv_name_2_0=(Token)match(input,RULE_ID,FOLLOW_9); 

                    						newLeafNode(lv_name_2_0, grammarAccess.getEXGarAccess().getNameIDTerminalRuleCall_1_0_0());
                    					

                    						if (current==null) {
                    							current = createModelElement(grammarAccess.getEXGarRule());
                    						}
                    						setWithLastConsumed(
                    							current,
                    							"name",
                    							lv_name_2_0,
                    							"org.eclipse.xtext.common.Terminals.ID");
                    					

                    }


                    }

                    otherlv_3=(Token)match(input,15,FOLLOW_42); 

                    				newLeafNode(otherlv_3, grammarAccess.getEXGarAccess().getColonKeyword_1_1());
                    			

                    }
                    break;

            }

            // InternalSpectra.g:2831:3: ( (otherlv_4= 'GE' ( (lv_elements_5_0= ruleTemporalInExpr ) ) (otherlv_6= ',' ( (lv_elements_7_0= ruleTemporalInExpr ) ) )* ) | (otherlv_8= 'GEF' ( (otherlv_9= RULE_ID ) ) ) | (otherlv_10= 'GEF' ( (lv_regExp_11_0= ruleRegExp ) ) ) )
            int alt56=3;
            int LA56_0 = input.LA(1);

            if ( (LA56_0==55) ) {
                alt56=1;
            }
            else if ( (LA56_0==56) ) {
                int LA56_2 = input.LA(2);

                if ( (LA56_2==21||LA56_2==28||LA56_2==48||(LA56_2>=97 && LA56_2<=100)||LA56_2==104) ) {
                    alt56=3;
                }
                else if ( (LA56_2==RULE_ID) ) {
                    alt56=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 56, 2, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 56, 0, input);

                throw nvae;
            }
            switch (alt56) {
                case 1 :
                    // InternalSpectra.g:2832:4: (otherlv_4= 'GE' ( (lv_elements_5_0= ruleTemporalInExpr ) ) (otherlv_6= ',' ( (lv_elements_7_0= ruleTemporalInExpr ) ) )* )
                    {
                    // InternalSpectra.g:2832:4: (otherlv_4= 'GE' ( (lv_elements_5_0= ruleTemporalInExpr ) ) (otherlv_6= ',' ( (lv_elements_7_0= ruleTemporalInExpr ) ) )* )
                    // InternalSpectra.g:2833:5: otherlv_4= 'GE' ( (lv_elements_5_0= ruleTemporalInExpr ) ) (otherlv_6= ',' ( (lv_elements_7_0= ruleTemporalInExpr ) ) )*
                    {
                    otherlv_4=(Token)match(input,55,FOLLOW_12); 

                    					newLeafNode(otherlv_4, grammarAccess.getEXGarAccess().getGEKeyword_2_0_0());
                    				
                    // InternalSpectra.g:2837:5: ( (lv_elements_5_0= ruleTemporalInExpr ) )
                    // InternalSpectra.g:2838:6: (lv_elements_5_0= ruleTemporalInExpr )
                    {
                    // InternalSpectra.g:2838:6: (lv_elements_5_0= ruleTemporalInExpr )
                    // InternalSpectra.g:2839:7: lv_elements_5_0= ruleTemporalInExpr
                    {

                    							newCompositeNode(grammarAccess.getEXGarAccess().getElementsTemporalInExprParserRuleCall_2_0_1_0());
                    						
                    pushFollow(FOLLOW_43);
                    lv_elements_5_0=ruleTemporalInExpr();

                    state._fsp--;


                    							if (current==null) {
                    								current = createModelElementForParent(grammarAccess.getEXGarRule());
                    							}
                    							add(
                    								current,
                    								"elements",
                    								lv_elements_5_0,
                    								"tau.smlab.syntech.Spectra.TemporalInExpr");
                    							afterParserOrEnumRuleCall();
                    						

                    }


                    }

                    // InternalSpectra.g:2856:5: (otherlv_6= ',' ( (lv_elements_7_0= ruleTemporalInExpr ) ) )*
                    loop55:
                    do {
                        int alt55=2;
                        int LA55_0 = input.LA(1);

                        if ( (LA55_0==24) ) {
                            alt55=1;
                        }


                        switch (alt55) {
                    	case 1 :
                    	    // InternalSpectra.g:2857:6: otherlv_6= ',' ( (lv_elements_7_0= ruleTemporalInExpr ) )
                    	    {
                    	    otherlv_6=(Token)match(input,24,FOLLOW_12); 

                    	    						newLeafNode(otherlv_6, grammarAccess.getEXGarAccess().getCommaKeyword_2_0_2_0());
                    	    					
                    	    // InternalSpectra.g:2861:6: ( (lv_elements_7_0= ruleTemporalInExpr ) )
                    	    // InternalSpectra.g:2862:7: (lv_elements_7_0= ruleTemporalInExpr )
                    	    {
                    	    // InternalSpectra.g:2862:7: (lv_elements_7_0= ruleTemporalInExpr )
                    	    // InternalSpectra.g:2863:8: lv_elements_7_0= ruleTemporalInExpr
                    	    {

                    	    								newCompositeNode(grammarAccess.getEXGarAccess().getElementsTemporalInExprParserRuleCall_2_0_2_1_0());
                    	    							
                    	    pushFollow(FOLLOW_43);
                    	    lv_elements_7_0=ruleTemporalInExpr();

                    	    state._fsp--;


                    	    								if (current==null) {
                    	    									current = createModelElementForParent(grammarAccess.getEXGarRule());
                    	    								}
                    	    								add(
                    	    									current,
                    	    									"elements",
                    	    									lv_elements_7_0,
                    	    									"tau.smlab.syntech.Spectra.TemporalInExpr");
                    	    								afterParserOrEnumRuleCall();
                    	    							

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop55;
                        }
                    } while (true);


                    }


                    }
                    break;
                case 2 :
                    // InternalSpectra.g:2883:4: (otherlv_8= 'GEF' ( (otherlv_9= RULE_ID ) ) )
                    {
                    // InternalSpectra.g:2883:4: (otherlv_8= 'GEF' ( (otherlv_9= RULE_ID ) ) )
                    // InternalSpectra.g:2884:5: otherlv_8= 'GEF' ( (otherlv_9= RULE_ID ) )
                    {
                    otherlv_8=(Token)match(input,56,FOLLOW_4); 

                    					newLeafNode(otherlv_8, grammarAccess.getEXGarAccess().getGEFKeyword_2_1_0());
                    				
                    // InternalSpectra.g:2888:5: ( (otherlv_9= RULE_ID ) )
                    // InternalSpectra.g:2889:6: (otherlv_9= RULE_ID )
                    {
                    // InternalSpectra.g:2889:6: (otherlv_9= RULE_ID )
                    // InternalSpectra.g:2890:7: otherlv_9= RULE_ID
                    {

                    							if (current==null) {
                    								current = createModelElement(grammarAccess.getEXGarRule());
                    							}
                    						
                    otherlv_9=(Token)match(input,RULE_ID,FOLLOW_13); 

                    							newLeafNode(otherlv_9, grammarAccess.getEXGarAccess().getRegExpPointerDefineRegExpDeclCrossReference_2_1_1_0());
                    						

                    }


                    }


                    }


                    }
                    break;
                case 3 :
                    // InternalSpectra.g:2903:4: (otherlv_10= 'GEF' ( (lv_regExp_11_0= ruleRegExp ) ) )
                    {
                    // InternalSpectra.g:2903:4: (otherlv_10= 'GEF' ( (lv_regExp_11_0= ruleRegExp ) ) )
                    // InternalSpectra.g:2904:5: otherlv_10= 'GEF' ( (lv_regExp_11_0= ruleRegExp ) )
                    {
                    otherlv_10=(Token)match(input,56,FOLLOW_25); 

                    					newLeafNode(otherlv_10, grammarAccess.getEXGarAccess().getGEFKeyword_2_2_0());
                    				
                    // InternalSpectra.g:2908:5: ( (lv_regExp_11_0= ruleRegExp ) )
                    // InternalSpectra.g:2909:6: (lv_regExp_11_0= ruleRegExp )
                    {
                    // InternalSpectra.g:2909:6: (lv_regExp_11_0= ruleRegExp )
                    // InternalSpectra.g:2910:7: lv_regExp_11_0= ruleRegExp
                    {

                    							newCompositeNode(grammarAccess.getEXGarAccess().getRegExpRegExpParserRuleCall_2_2_1_0());
                    						
                    pushFollow(FOLLOW_13);
                    lv_regExp_11_0=ruleRegExp();

                    state._fsp--;


                    							if (current==null) {
                    								current = createModelElementForParent(grammarAccess.getEXGarRule());
                    							}
                    							set(
                    								current,
                    								"regExp",
                    								lv_regExp_11_0,
                    								"tau.smlab.syntech.Spectra.RegExp");
                    							afterParserOrEnumRuleCall();
                    						

                    }


                    }


                    }


                    }
                    break;

            }


            			newCompositeNode(grammarAccess.getEXGarAccess().getTOK_SEMIParserRuleCall_3());
            		
            pushFollow(FOLLOW_2);
            ruleTOK_SEMI();

            state._fsp--;


            			afterParserOrEnumRuleCall();
            		

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleEXGar"


    // $ANTLR start "entryRuleTemporalExpression"
    // InternalSpectra.g:2940:1: entryRuleTemporalExpression returns [EObject current=null] : iv_ruleTemporalExpression= ruleTemporalExpression EOF ;
    public final EObject entryRuleTemporalExpression() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleTemporalExpression = null;


        try {
            // InternalSpectra.g:2940:59: (iv_ruleTemporalExpression= ruleTemporalExpression EOF )
            // InternalSpectra.g:2941:2: iv_ruleTemporalExpression= ruleTemporalExpression EOF
            {
             newCompositeNode(grammarAccess.getTemporalExpressionRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleTemporalExpression=ruleTemporalExpression();

            state._fsp--;

             current =iv_ruleTemporalExpression; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleTemporalExpression"


    // $ANTLR start "ruleTemporalExpression"
    // InternalSpectra.g:2947:1: ruleTemporalExpression returns [EObject current=null] : this_QuantifierExpr_0= ruleQuantifierExpr ;
    public final EObject ruleTemporalExpression() throws RecognitionException {
        EObject current = null;

        EObject this_QuantifierExpr_0 = null;



        	enterRule();

        try {
            // InternalSpectra.g:2953:2: (this_QuantifierExpr_0= ruleQuantifierExpr )
            // InternalSpectra.g:2954:2: this_QuantifierExpr_0= ruleQuantifierExpr
            {

            		newCompositeNode(grammarAccess.getTemporalExpressionAccess().getQuantifierExprParserRuleCall());
            	
            pushFollow(FOLLOW_2);
            this_QuantifierExpr_0=ruleQuantifierExpr();

            state._fsp--;


            		current = this_QuantifierExpr_0;
            		afterParserOrEnumRuleCall();
            	

            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleTemporalExpression"


    // $ANTLR start "entryRuleTemporalInExpr"
    // InternalSpectra.g:2965:1: entryRuleTemporalInExpr returns [EObject current=null] : iv_ruleTemporalInExpr= ruleTemporalInExpr EOF ;
    public final EObject entryRuleTemporalInExpr() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleTemporalInExpr = null;


        try {
            // InternalSpectra.g:2965:55: (iv_ruleTemporalInExpr= ruleTemporalInExpr EOF )
            // InternalSpectra.g:2966:2: iv_ruleTemporalInExpr= ruleTemporalInExpr EOF
            {
             newCompositeNode(grammarAccess.getTemporalInExprRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleTemporalInExpr=ruleTemporalInExpr();

            state._fsp--;

             current =iv_ruleTemporalInExpr; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleTemporalInExpr"


    // $ANTLR start "ruleTemporalInExpr"
    // InternalSpectra.g:2972:1: ruleTemporalInExpr returns [EObject current=null] : (this_TemporalImpExpr_0= ruleTemporalImpExpr ( () ( (lv_not_2_0= 'not' ) )? ( (lv_operator_3_0= 'in' ) ) otherlv_4= '{' ( (lv_values_5_0= ruleValueInRange ) ) (otherlv_6= ',' ( (lv_values_7_0= ruleValueInRange ) ) )* otherlv_8= '}' )? ) ;
    public final EObject ruleTemporalInExpr() throws RecognitionException {
        EObject current = null;

        Token lv_not_2_0=null;
        Token lv_operator_3_0=null;
        Token otherlv_4=null;
        Token otherlv_6=null;
        Token otherlv_8=null;
        EObject this_TemporalImpExpr_0 = null;

        EObject lv_values_5_0 = null;

        EObject lv_values_7_0 = null;



        	enterRule();

        try {
            // InternalSpectra.g:2978:2: ( (this_TemporalImpExpr_0= ruleTemporalImpExpr ( () ( (lv_not_2_0= 'not' ) )? ( (lv_operator_3_0= 'in' ) ) otherlv_4= '{' ( (lv_values_5_0= ruleValueInRange ) ) (otherlv_6= ',' ( (lv_values_7_0= ruleValueInRange ) ) )* otherlv_8= '}' )? ) )
            // InternalSpectra.g:2979:2: (this_TemporalImpExpr_0= ruleTemporalImpExpr ( () ( (lv_not_2_0= 'not' ) )? ( (lv_operator_3_0= 'in' ) ) otherlv_4= '{' ( (lv_values_5_0= ruleValueInRange ) ) (otherlv_6= ',' ( (lv_values_7_0= ruleValueInRange ) ) )* otherlv_8= '}' )? )
            {
            // InternalSpectra.g:2979:2: (this_TemporalImpExpr_0= ruleTemporalImpExpr ( () ( (lv_not_2_0= 'not' ) )? ( (lv_operator_3_0= 'in' ) ) otherlv_4= '{' ( (lv_values_5_0= ruleValueInRange ) ) (otherlv_6= ',' ( (lv_values_7_0= ruleValueInRange ) ) )* otherlv_8= '}' )? )
            // InternalSpectra.g:2980:3: this_TemporalImpExpr_0= ruleTemporalImpExpr ( () ( (lv_not_2_0= 'not' ) )? ( (lv_operator_3_0= 'in' ) ) otherlv_4= '{' ( (lv_values_5_0= ruleValueInRange ) ) (otherlv_6= ',' ( (lv_values_7_0= ruleValueInRange ) ) )* otherlv_8= '}' )?
            {

            			newCompositeNode(grammarAccess.getTemporalInExprAccess().getTemporalImpExprParserRuleCall_0());
            		
            pushFollow(FOLLOW_44);
            this_TemporalImpExpr_0=ruleTemporalImpExpr();

            state._fsp--;


            			current = this_TemporalImpExpr_0;
            			afterParserOrEnumRuleCall();
            		
            // InternalSpectra.g:2988:3: ( () ( (lv_not_2_0= 'not' ) )? ( (lv_operator_3_0= 'in' ) ) otherlv_4= '{' ( (lv_values_5_0= ruleValueInRange ) ) (otherlv_6= ',' ( (lv_values_7_0= ruleValueInRange ) ) )* otherlv_8= '}' )?
            int alt59=2;
            int LA59_0 = input.LA(1);

            if ( ((LA59_0>=57 && LA59_0<=58)) ) {
                alt59=1;
            }
            switch (alt59) {
                case 1 :
                    // InternalSpectra.g:2989:4: () ( (lv_not_2_0= 'not' ) )? ( (lv_operator_3_0= 'in' ) ) otherlv_4= '{' ( (lv_values_5_0= ruleValueInRange ) ) (otherlv_6= ',' ( (lv_values_7_0= ruleValueInRange ) ) )* otherlv_8= '}'
                    {
                    // InternalSpectra.g:2989:4: ()
                    // InternalSpectra.g:2990:5: 
                    {

                    					current = forceCreateModelElementAndSet(
                    						grammarAccess.getTemporalInExprAccess().getTemporalInExprLeftAction_1_0(),
                    						current);
                    				

                    }

                    // InternalSpectra.g:2996:4: ( (lv_not_2_0= 'not' ) )?
                    int alt57=2;
                    int LA57_0 = input.LA(1);

                    if ( (LA57_0==57) ) {
                        alt57=1;
                    }
                    switch (alt57) {
                        case 1 :
                            // InternalSpectra.g:2997:5: (lv_not_2_0= 'not' )
                            {
                            // InternalSpectra.g:2997:5: (lv_not_2_0= 'not' )
                            // InternalSpectra.g:2998:6: lv_not_2_0= 'not'
                            {
                            lv_not_2_0=(Token)match(input,57,FOLLOW_45); 

                            						newLeafNode(lv_not_2_0, grammarAccess.getTemporalInExprAccess().getNotNotKeyword_1_1_0());
                            					

                            						if (current==null) {
                            							current = createModelElement(grammarAccess.getTemporalInExprRule());
                            						}
                            						setWithLastConsumed(current, "not", lv_not_2_0 != null, "not");
                            					

                            }


                            }
                            break;

                    }

                    // InternalSpectra.g:3010:4: ( (lv_operator_3_0= 'in' ) )
                    // InternalSpectra.g:3011:5: (lv_operator_3_0= 'in' )
                    {
                    // InternalSpectra.g:3011:5: (lv_operator_3_0= 'in' )
                    // InternalSpectra.g:3012:6: lv_operator_3_0= 'in'
                    {
                    lv_operator_3_0=(Token)match(input,58,FOLLOW_23); 

                    						newLeafNode(lv_operator_3_0, grammarAccess.getTemporalInExprAccess().getOperatorInKeyword_1_2_0());
                    					

                    						if (current==null) {
                    							current = createModelElement(grammarAccess.getTemporalInExprRule());
                    						}
                    						setWithLastConsumed(current, "operator", lv_operator_3_0, "in");
                    					

                    }


                    }

                    otherlv_4=(Token)match(input,23,FOLLOW_46); 

                    				newLeafNode(otherlv_4, grammarAccess.getTemporalInExprAccess().getLeftCurlyBracketKeyword_1_3());
                    			
                    // InternalSpectra.g:3028:4: ( (lv_values_5_0= ruleValueInRange ) )
                    // InternalSpectra.g:3029:5: (lv_values_5_0= ruleValueInRange )
                    {
                    // InternalSpectra.g:3029:5: (lv_values_5_0= ruleValueInRange )
                    // InternalSpectra.g:3030:6: lv_values_5_0= ruleValueInRange
                    {

                    						newCompositeNode(grammarAccess.getTemporalInExprAccess().getValuesValueInRangeParserRuleCall_1_4_0());
                    					
                    pushFollow(FOLLOW_24);
                    lv_values_5_0=ruleValueInRange();

                    state._fsp--;


                    						if (current==null) {
                    							current = createModelElementForParent(grammarAccess.getTemporalInExprRule());
                    						}
                    						add(
                    							current,
                    							"values",
                    							lv_values_5_0,
                    							"tau.smlab.syntech.Spectra.ValueInRange");
                    						afterParserOrEnumRuleCall();
                    					

                    }


                    }

                    // InternalSpectra.g:3047:4: (otherlv_6= ',' ( (lv_values_7_0= ruleValueInRange ) ) )*
                    loop58:
                    do {
                        int alt58=2;
                        int LA58_0 = input.LA(1);

                        if ( (LA58_0==24) ) {
                            alt58=1;
                        }


                        switch (alt58) {
                    	case 1 :
                    	    // InternalSpectra.g:3048:5: otherlv_6= ',' ( (lv_values_7_0= ruleValueInRange ) )
                    	    {
                    	    otherlv_6=(Token)match(input,24,FOLLOW_46); 

                    	    					newLeafNode(otherlv_6, grammarAccess.getTemporalInExprAccess().getCommaKeyword_1_5_0());
                    	    				
                    	    // InternalSpectra.g:3052:5: ( (lv_values_7_0= ruleValueInRange ) )
                    	    // InternalSpectra.g:3053:6: (lv_values_7_0= ruleValueInRange )
                    	    {
                    	    // InternalSpectra.g:3053:6: (lv_values_7_0= ruleValueInRange )
                    	    // InternalSpectra.g:3054:7: lv_values_7_0= ruleValueInRange
                    	    {

                    	    							newCompositeNode(grammarAccess.getTemporalInExprAccess().getValuesValueInRangeParserRuleCall_1_5_1_0());
                    	    						
                    	    pushFollow(FOLLOW_24);
                    	    lv_values_7_0=ruleValueInRange();

                    	    state._fsp--;


                    	    							if (current==null) {
                    	    								current = createModelElementForParent(grammarAccess.getTemporalInExprRule());
                    	    							}
                    	    							add(
                    	    								current,
                    	    								"values",
                    	    								lv_values_7_0,
                    	    								"tau.smlab.syntech.Spectra.ValueInRange");
                    	    							afterParserOrEnumRuleCall();
                    	    						

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop58;
                        }
                    } while (true);

                    otherlv_8=(Token)match(input,25,FOLLOW_2); 

                    				newLeafNode(otherlv_8, grammarAccess.getTemporalInExprAccess().getRightCurlyBracketKeyword_1_6());
                    			

                    }
                    break;

            }


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleTemporalInExpr"


    // $ANTLR start "entryRuleTemporalImpExpr"
    // InternalSpectra.g:3081:1: entryRuleTemporalImpExpr returns [EObject current=null] : iv_ruleTemporalImpExpr= ruleTemporalImpExpr EOF ;
    public final EObject entryRuleTemporalImpExpr() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleTemporalImpExpr = null;


        try {
            // InternalSpectra.g:3081:56: (iv_ruleTemporalImpExpr= ruleTemporalImpExpr EOF )
            // InternalSpectra.g:3082:2: iv_ruleTemporalImpExpr= ruleTemporalImpExpr EOF
            {
             newCompositeNode(grammarAccess.getTemporalImpExprRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleTemporalImpExpr=ruleTemporalImpExpr();

            state._fsp--;

             current =iv_ruleTemporalImpExpr; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleTemporalImpExpr"


    // $ANTLR start "ruleTemporalImpExpr"
    // InternalSpectra.g:3088:1: ruleTemporalImpExpr returns [EObject current=null] : (this_TemporalIffExpr_0= ruleTemporalIffExpr ( () ( ( (lv_operator_2_1= '->' | lv_operator_2_2= 'implies' ) ) ) ( (lv_implication_3_0= ruleTemporalImpExpr ) ) )? ) ;
    public final EObject ruleTemporalImpExpr() throws RecognitionException {
        EObject current = null;

        Token lv_operator_2_1=null;
        Token lv_operator_2_2=null;
        EObject this_TemporalIffExpr_0 = null;

        EObject lv_implication_3_0 = null;



        	enterRule();

        try {
            // InternalSpectra.g:3094:2: ( (this_TemporalIffExpr_0= ruleTemporalIffExpr ( () ( ( (lv_operator_2_1= '->' | lv_operator_2_2= 'implies' ) ) ) ( (lv_implication_3_0= ruleTemporalImpExpr ) ) )? ) )
            // InternalSpectra.g:3095:2: (this_TemporalIffExpr_0= ruleTemporalIffExpr ( () ( ( (lv_operator_2_1= '->' | lv_operator_2_2= 'implies' ) ) ) ( (lv_implication_3_0= ruleTemporalImpExpr ) ) )? )
            {
            // InternalSpectra.g:3095:2: (this_TemporalIffExpr_0= ruleTemporalIffExpr ( () ( ( (lv_operator_2_1= '->' | lv_operator_2_2= 'implies' ) ) ) ( (lv_implication_3_0= ruleTemporalImpExpr ) ) )? )
            // InternalSpectra.g:3096:3: this_TemporalIffExpr_0= ruleTemporalIffExpr ( () ( ( (lv_operator_2_1= '->' | lv_operator_2_2= 'implies' ) ) ) ( (lv_implication_3_0= ruleTemporalImpExpr ) ) )?
            {

            			newCompositeNode(grammarAccess.getTemporalImpExprAccess().getTemporalIffExprParserRuleCall_0());
            		
            pushFollow(FOLLOW_47);
            this_TemporalIffExpr_0=ruleTemporalIffExpr();

            state._fsp--;


            			current = this_TemporalIffExpr_0;
            			afterParserOrEnumRuleCall();
            		
            // InternalSpectra.g:3104:3: ( () ( ( (lv_operator_2_1= '->' | lv_operator_2_2= 'implies' ) ) ) ( (lv_implication_3_0= ruleTemporalImpExpr ) ) )?
            int alt61=2;
            int LA61_0 = input.LA(1);

            if ( ((LA61_0>=59 && LA61_0<=60)) ) {
                alt61=1;
            }
            switch (alt61) {
                case 1 :
                    // InternalSpectra.g:3105:4: () ( ( (lv_operator_2_1= '->' | lv_operator_2_2= 'implies' ) ) ) ( (lv_implication_3_0= ruleTemporalImpExpr ) )
                    {
                    // InternalSpectra.g:3105:4: ()
                    // InternalSpectra.g:3106:5: 
                    {

                    					current = forceCreateModelElementAndSet(
                    						grammarAccess.getTemporalImpExprAccess().getTemporalImpExprLeftAction_1_0(),
                    						current);
                    				

                    }

                    // InternalSpectra.g:3112:4: ( ( (lv_operator_2_1= '->' | lv_operator_2_2= 'implies' ) ) )
                    // InternalSpectra.g:3113:5: ( (lv_operator_2_1= '->' | lv_operator_2_2= 'implies' ) )
                    {
                    // InternalSpectra.g:3113:5: ( (lv_operator_2_1= '->' | lv_operator_2_2= 'implies' ) )
                    // InternalSpectra.g:3114:6: (lv_operator_2_1= '->' | lv_operator_2_2= 'implies' )
                    {
                    // InternalSpectra.g:3114:6: (lv_operator_2_1= '->' | lv_operator_2_2= 'implies' )
                    int alt60=2;
                    int LA60_0 = input.LA(1);

                    if ( (LA60_0==59) ) {
                        alt60=1;
                    }
                    else if ( (LA60_0==60) ) {
                        alt60=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 60, 0, input);

                        throw nvae;
                    }
                    switch (alt60) {
                        case 1 :
                            // InternalSpectra.g:3115:7: lv_operator_2_1= '->'
                            {
                            lv_operator_2_1=(Token)match(input,59,FOLLOW_12); 

                            							newLeafNode(lv_operator_2_1, grammarAccess.getTemporalImpExprAccess().getOperatorHyphenMinusGreaterThanSignKeyword_1_1_0_0());
                            						

                            							if (current==null) {
                            								current = createModelElement(grammarAccess.getTemporalImpExprRule());
                            							}
                            							setWithLastConsumed(current, "operator", lv_operator_2_1, null);
                            						

                            }
                            break;
                        case 2 :
                            // InternalSpectra.g:3126:7: lv_operator_2_2= 'implies'
                            {
                            lv_operator_2_2=(Token)match(input,60,FOLLOW_12); 

                            							newLeafNode(lv_operator_2_2, grammarAccess.getTemporalImpExprAccess().getOperatorImpliesKeyword_1_1_0_1());
                            						

                            							if (current==null) {
                            								current = createModelElement(grammarAccess.getTemporalImpExprRule());
                            							}
                            							setWithLastConsumed(current, "operator", lv_operator_2_2, null);
                            						

                            }
                            break;

                    }


                    }


                    }

                    // InternalSpectra.g:3139:4: ( (lv_implication_3_0= ruleTemporalImpExpr ) )
                    // InternalSpectra.g:3140:5: (lv_implication_3_0= ruleTemporalImpExpr )
                    {
                    // InternalSpectra.g:3140:5: (lv_implication_3_0= ruleTemporalImpExpr )
                    // InternalSpectra.g:3141:6: lv_implication_3_0= ruleTemporalImpExpr
                    {

                    						newCompositeNode(grammarAccess.getTemporalImpExprAccess().getImplicationTemporalImpExprParserRuleCall_1_2_0());
                    					
                    pushFollow(FOLLOW_2);
                    lv_implication_3_0=ruleTemporalImpExpr();

                    state._fsp--;


                    						if (current==null) {
                    							current = createModelElementForParent(grammarAccess.getTemporalImpExprRule());
                    						}
                    						set(
                    							current,
                    							"implication",
                    							lv_implication_3_0,
                    							"tau.smlab.syntech.Spectra.TemporalImpExpr");
                    						afterParserOrEnumRuleCall();
                    					

                    }


                    }


                    }
                    break;

            }


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleTemporalImpExpr"


    // $ANTLR start "entryRuleTemporalIffExpr"
    // InternalSpectra.g:3163:1: entryRuleTemporalIffExpr returns [EObject current=null] : iv_ruleTemporalIffExpr= ruleTemporalIffExpr EOF ;
    public final EObject entryRuleTemporalIffExpr() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleTemporalIffExpr = null;


        try {
            // InternalSpectra.g:3163:56: (iv_ruleTemporalIffExpr= ruleTemporalIffExpr EOF )
            // InternalSpectra.g:3164:2: iv_ruleTemporalIffExpr= ruleTemporalIffExpr EOF
            {
             newCompositeNode(grammarAccess.getTemporalIffExprRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleTemporalIffExpr=ruleTemporalIffExpr();

            state._fsp--;

             current =iv_ruleTemporalIffExpr; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleTemporalIffExpr"


    // $ANTLR start "ruleTemporalIffExpr"
    // InternalSpectra.g:3170:1: ruleTemporalIffExpr returns [EObject current=null] : (this_TemporalOrExpr_0= ruleTemporalOrExpr ( () ( ( (lv_operator_2_1= '<->' | lv_operator_2_2= 'iff' ) ) ) ( (lv_elements_3_0= ruleTemporalOrExpr ) ) )* ) ;
    public final EObject ruleTemporalIffExpr() throws RecognitionException {
        EObject current = null;

        Token lv_operator_2_1=null;
        Token lv_operator_2_2=null;
        EObject this_TemporalOrExpr_0 = null;

        EObject lv_elements_3_0 = null;



        	enterRule();

        try {
            // InternalSpectra.g:3176:2: ( (this_TemporalOrExpr_0= ruleTemporalOrExpr ( () ( ( (lv_operator_2_1= '<->' | lv_operator_2_2= 'iff' ) ) ) ( (lv_elements_3_0= ruleTemporalOrExpr ) ) )* ) )
            // InternalSpectra.g:3177:2: (this_TemporalOrExpr_0= ruleTemporalOrExpr ( () ( ( (lv_operator_2_1= '<->' | lv_operator_2_2= 'iff' ) ) ) ( (lv_elements_3_0= ruleTemporalOrExpr ) ) )* )
            {
            // InternalSpectra.g:3177:2: (this_TemporalOrExpr_0= ruleTemporalOrExpr ( () ( ( (lv_operator_2_1= '<->' | lv_operator_2_2= 'iff' ) ) ) ( (lv_elements_3_0= ruleTemporalOrExpr ) ) )* )
            // InternalSpectra.g:3178:3: this_TemporalOrExpr_0= ruleTemporalOrExpr ( () ( ( (lv_operator_2_1= '<->' | lv_operator_2_2= 'iff' ) ) ) ( (lv_elements_3_0= ruleTemporalOrExpr ) ) )*
            {

            			newCompositeNode(grammarAccess.getTemporalIffExprAccess().getTemporalOrExprParserRuleCall_0());
            		
            pushFollow(FOLLOW_48);
            this_TemporalOrExpr_0=ruleTemporalOrExpr();

            state._fsp--;


            			current = this_TemporalOrExpr_0;
            			afterParserOrEnumRuleCall();
            		
            // InternalSpectra.g:3186:3: ( () ( ( (lv_operator_2_1= '<->' | lv_operator_2_2= 'iff' ) ) ) ( (lv_elements_3_0= ruleTemporalOrExpr ) ) )*
            loop63:
            do {
                int alt63=2;
                int LA63_0 = input.LA(1);

                if ( ((LA63_0>=61 && LA63_0<=62)) ) {
                    alt63=1;
                }


                switch (alt63) {
            	case 1 :
            	    // InternalSpectra.g:3187:4: () ( ( (lv_operator_2_1= '<->' | lv_operator_2_2= 'iff' ) ) ) ( (lv_elements_3_0= ruleTemporalOrExpr ) )
            	    {
            	    // InternalSpectra.g:3187:4: ()
            	    // InternalSpectra.g:3188:5: 
            	    {

            	    					current = forceCreateModelElementAndAdd(
            	    						grammarAccess.getTemporalIffExprAccess().getTemporalIffExprElementsAction_1_0(),
            	    						current);
            	    				

            	    }

            	    // InternalSpectra.g:3194:4: ( ( (lv_operator_2_1= '<->' | lv_operator_2_2= 'iff' ) ) )
            	    // InternalSpectra.g:3195:5: ( (lv_operator_2_1= '<->' | lv_operator_2_2= 'iff' ) )
            	    {
            	    // InternalSpectra.g:3195:5: ( (lv_operator_2_1= '<->' | lv_operator_2_2= 'iff' ) )
            	    // InternalSpectra.g:3196:6: (lv_operator_2_1= '<->' | lv_operator_2_2= 'iff' )
            	    {
            	    // InternalSpectra.g:3196:6: (lv_operator_2_1= '<->' | lv_operator_2_2= 'iff' )
            	    int alt62=2;
            	    int LA62_0 = input.LA(1);

            	    if ( (LA62_0==61) ) {
            	        alt62=1;
            	    }
            	    else if ( (LA62_0==62) ) {
            	        alt62=2;
            	    }
            	    else {
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 62, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt62) {
            	        case 1 :
            	            // InternalSpectra.g:3197:7: lv_operator_2_1= '<->'
            	            {
            	            lv_operator_2_1=(Token)match(input,61,FOLLOW_12); 

            	            							newLeafNode(lv_operator_2_1, grammarAccess.getTemporalIffExprAccess().getOperatorLessThanSignHyphenMinusGreaterThanSignKeyword_1_1_0_0());
            	            						

            	            							if (current==null) {
            	            								current = createModelElement(grammarAccess.getTemporalIffExprRule());
            	            							}
            	            							setWithLastConsumed(current, "operator", lv_operator_2_1, null);
            	            						

            	            }
            	            break;
            	        case 2 :
            	            // InternalSpectra.g:3208:7: lv_operator_2_2= 'iff'
            	            {
            	            lv_operator_2_2=(Token)match(input,62,FOLLOW_12); 

            	            							newLeafNode(lv_operator_2_2, grammarAccess.getTemporalIffExprAccess().getOperatorIffKeyword_1_1_0_1());
            	            						

            	            							if (current==null) {
            	            								current = createModelElement(grammarAccess.getTemporalIffExprRule());
            	            							}
            	            							setWithLastConsumed(current, "operator", lv_operator_2_2, null);
            	            						

            	            }
            	            break;

            	    }


            	    }


            	    }

            	    // InternalSpectra.g:3221:4: ( (lv_elements_3_0= ruleTemporalOrExpr ) )
            	    // InternalSpectra.g:3222:5: (lv_elements_3_0= ruleTemporalOrExpr )
            	    {
            	    // InternalSpectra.g:3222:5: (lv_elements_3_0= ruleTemporalOrExpr )
            	    // InternalSpectra.g:3223:6: lv_elements_3_0= ruleTemporalOrExpr
            	    {

            	    						newCompositeNode(grammarAccess.getTemporalIffExprAccess().getElementsTemporalOrExprParserRuleCall_1_2_0());
            	    					
            	    pushFollow(FOLLOW_48);
            	    lv_elements_3_0=ruleTemporalOrExpr();

            	    state._fsp--;


            	    						if (current==null) {
            	    							current = createModelElementForParent(grammarAccess.getTemporalIffExprRule());
            	    						}
            	    						add(
            	    							current,
            	    							"elements",
            	    							lv_elements_3_0,
            	    							"tau.smlab.syntech.Spectra.TemporalOrExpr");
            	    						afterParserOrEnumRuleCall();
            	    					

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop63;
                }
            } while (true);


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleTemporalIffExpr"


    // $ANTLR start "entryRuleTemporalOrExpr"
    // InternalSpectra.g:3245:1: entryRuleTemporalOrExpr returns [EObject current=null] : iv_ruleTemporalOrExpr= ruleTemporalOrExpr EOF ;
    public final EObject entryRuleTemporalOrExpr() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleTemporalOrExpr = null;


        try {
            // InternalSpectra.g:3245:55: (iv_ruleTemporalOrExpr= ruleTemporalOrExpr EOF )
            // InternalSpectra.g:3246:2: iv_ruleTemporalOrExpr= ruleTemporalOrExpr EOF
            {
             newCompositeNode(grammarAccess.getTemporalOrExprRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleTemporalOrExpr=ruleTemporalOrExpr();

            state._fsp--;

             current =iv_ruleTemporalOrExpr; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleTemporalOrExpr"


    // $ANTLR start "ruleTemporalOrExpr"
    // InternalSpectra.g:3252:1: ruleTemporalOrExpr returns [EObject current=null] : (this_TemporalAndExpr_0= ruleTemporalAndExpr ( () ( ( (lv_operator_2_1= '|' | lv_operator_2_2= 'or' | lv_operator_2_3= 'xor' ) ) ) ( (lv_elements_3_0= ruleTemporalAndExpr ) ) )* ) ;
    public final EObject ruleTemporalOrExpr() throws RecognitionException {
        EObject current = null;

        Token lv_operator_2_1=null;
        Token lv_operator_2_2=null;
        Token lv_operator_2_3=null;
        EObject this_TemporalAndExpr_0 = null;

        EObject lv_elements_3_0 = null;



        	enterRule();

        try {
            // InternalSpectra.g:3258:2: ( (this_TemporalAndExpr_0= ruleTemporalAndExpr ( () ( ( (lv_operator_2_1= '|' | lv_operator_2_2= 'or' | lv_operator_2_3= 'xor' ) ) ) ( (lv_elements_3_0= ruleTemporalAndExpr ) ) )* ) )
            // InternalSpectra.g:3259:2: (this_TemporalAndExpr_0= ruleTemporalAndExpr ( () ( ( (lv_operator_2_1= '|' | lv_operator_2_2= 'or' | lv_operator_2_3= 'xor' ) ) ) ( (lv_elements_3_0= ruleTemporalAndExpr ) ) )* )
            {
            // InternalSpectra.g:3259:2: (this_TemporalAndExpr_0= ruleTemporalAndExpr ( () ( ( (lv_operator_2_1= '|' | lv_operator_2_2= 'or' | lv_operator_2_3= 'xor' ) ) ) ( (lv_elements_3_0= ruleTemporalAndExpr ) ) )* )
            // InternalSpectra.g:3260:3: this_TemporalAndExpr_0= ruleTemporalAndExpr ( () ( ( (lv_operator_2_1= '|' | lv_operator_2_2= 'or' | lv_operator_2_3= 'xor' ) ) ) ( (lv_elements_3_0= ruleTemporalAndExpr ) ) )*
            {

            			newCompositeNode(grammarAccess.getTemporalOrExprAccess().getTemporalAndExprParserRuleCall_0());
            		
            pushFollow(FOLLOW_49);
            this_TemporalAndExpr_0=ruleTemporalAndExpr();

            state._fsp--;


            			current = this_TemporalAndExpr_0;
            			afterParserOrEnumRuleCall();
            		
            // InternalSpectra.g:3268:3: ( () ( ( (lv_operator_2_1= '|' | lv_operator_2_2= 'or' | lv_operator_2_3= 'xor' ) ) ) ( (lv_elements_3_0= ruleTemporalAndExpr ) ) )*
            loop65:
            do {
                int alt65=2;
                int LA65_0 = input.LA(1);

                if ( ((LA65_0>=63 && LA65_0<=65)) ) {
                    alt65=1;
                }


                switch (alt65) {
            	case 1 :
            	    // InternalSpectra.g:3269:4: () ( ( (lv_operator_2_1= '|' | lv_operator_2_2= 'or' | lv_operator_2_3= 'xor' ) ) ) ( (lv_elements_3_0= ruleTemporalAndExpr ) )
            	    {
            	    // InternalSpectra.g:3269:4: ()
            	    // InternalSpectra.g:3270:5: 
            	    {

            	    					current = forceCreateModelElementAndAdd(
            	    						grammarAccess.getTemporalOrExprAccess().getTemporalOrExprElementsAction_1_0(),
            	    						current);
            	    				

            	    }

            	    // InternalSpectra.g:3276:4: ( ( (lv_operator_2_1= '|' | lv_operator_2_2= 'or' | lv_operator_2_3= 'xor' ) ) )
            	    // InternalSpectra.g:3277:5: ( (lv_operator_2_1= '|' | lv_operator_2_2= 'or' | lv_operator_2_3= 'xor' ) )
            	    {
            	    // InternalSpectra.g:3277:5: ( (lv_operator_2_1= '|' | lv_operator_2_2= 'or' | lv_operator_2_3= 'xor' ) )
            	    // InternalSpectra.g:3278:6: (lv_operator_2_1= '|' | lv_operator_2_2= 'or' | lv_operator_2_3= 'xor' )
            	    {
            	    // InternalSpectra.g:3278:6: (lv_operator_2_1= '|' | lv_operator_2_2= 'or' | lv_operator_2_3= 'xor' )
            	    int alt64=3;
            	    switch ( input.LA(1) ) {
            	    case 63:
            	        {
            	        alt64=1;
            	        }
            	        break;
            	    case 64:
            	        {
            	        alt64=2;
            	        }
            	        break;
            	    case 65:
            	        {
            	        alt64=3;
            	        }
            	        break;
            	    default:
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 64, 0, input);

            	        throw nvae;
            	    }

            	    switch (alt64) {
            	        case 1 :
            	            // InternalSpectra.g:3279:7: lv_operator_2_1= '|'
            	            {
            	            lv_operator_2_1=(Token)match(input,63,FOLLOW_12); 

            	            							newLeafNode(lv_operator_2_1, grammarAccess.getTemporalOrExprAccess().getOperatorVerticalLineKeyword_1_1_0_0());
            	            						

            	            							if (current==null) {
            	            								current = createModelElement(grammarAccess.getTemporalOrExprRule());
            	            							}
            	            							addWithLastConsumed(current, "operator", lv_operator_2_1, null);
            	            						

            	            }
            	            break;
            	        case 2 :
            	            // InternalSpectra.g:3290:7: lv_operator_2_2= 'or'
            	            {
            	            lv_operator_2_2=(Token)match(input,64,FOLLOW_12); 

            	            							newLeafNode(lv_operator_2_2, grammarAccess.getTemporalOrExprAccess().getOperatorOrKeyword_1_1_0_1());
            	            						

            	            							if (current==null) {
            	            								current = createModelElement(grammarAccess.getTemporalOrExprRule());
            	            							}
            	            							addWithLastConsumed(current, "operator", lv_operator_2_2, null);
            	            						

            	            }
            	            break;
            	        case 3 :
            	            // InternalSpectra.g:3301:7: lv_operator_2_3= 'xor'
            	            {
            	            lv_operator_2_3=(Token)match(input,65,FOLLOW_12); 

            	            							newLeafNode(lv_operator_2_3, grammarAccess.getTemporalOrExprAccess().getOperatorXorKeyword_1_1_0_2());
            	            						

            	            							if (current==null) {
            	            								current = createModelElement(grammarAccess.getTemporalOrExprRule());
            	            							}
            	            							addWithLastConsumed(current, "operator", lv_operator_2_3, null);
            	            						

            	            }
            	            break;

            	    }


            	    }


            	    }

            	    // InternalSpectra.g:3314:4: ( (lv_elements_3_0= ruleTemporalAndExpr ) )
            	    // InternalSpectra.g:3315:5: (lv_elements_3_0= ruleTemporalAndExpr )
            	    {
            	    // InternalSpectra.g:3315:5: (lv_elements_3_0= ruleTemporalAndExpr )
            	    // InternalSpectra.g:3316:6: lv_elements_3_0= ruleTemporalAndExpr
            	    {

            	    						newCompositeNode(grammarAccess.getTemporalOrExprAccess().getElementsTemporalAndExprParserRuleCall_1_2_0());
            	    					
            	    pushFollow(FOLLOW_49);
            	    lv_elements_3_0=ruleTemporalAndExpr();

            	    state._fsp--;


            	    						if (current==null) {
            	    							current = createModelElementForParent(grammarAccess.getTemporalOrExprRule());
            	    						}
            	    						add(
            	    							current,
            	    							"elements",
            	    							lv_elements_3_0,
            	    							"tau.smlab.syntech.Spectra.TemporalAndExpr");
            	    						afterParserOrEnumRuleCall();
            	    					

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop65;
                }
            } while (true);


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleTemporalOrExpr"


    // $ANTLR start "entryRuleTemporalAndExpr"
    // InternalSpectra.g:3338:1: entryRuleTemporalAndExpr returns [EObject current=null] : iv_ruleTemporalAndExpr= ruleTemporalAndExpr EOF ;
    public final EObject entryRuleTemporalAndExpr() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleTemporalAndExpr = null;


        try {
            // InternalSpectra.g:3338:56: (iv_ruleTemporalAndExpr= ruleTemporalAndExpr EOF )
            // InternalSpectra.g:3339:2: iv_ruleTemporalAndExpr= ruleTemporalAndExpr EOF
            {
             newCompositeNode(grammarAccess.getTemporalAndExprRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleTemporalAndExpr=ruleTemporalAndExpr();

            state._fsp--;

             current =iv_ruleTemporalAndExpr; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleTemporalAndExpr"


    // $ANTLR start "ruleTemporalAndExpr"
    // InternalSpectra.g:3345:1: ruleTemporalAndExpr returns [EObject current=null] : (this_TemporalRelationalExpr_0= ruleTemporalRelationalExpr ( () ( ( (lv_operator_2_1= '&' | lv_operator_2_2= 'and' ) ) ) ( (lv_elements_3_0= ruleTemporalRelationalExpr ) ) )* ) ;
    public final EObject ruleTemporalAndExpr() throws RecognitionException {
        EObject current = null;

        Token lv_operator_2_1=null;
        Token lv_operator_2_2=null;
        EObject this_TemporalRelationalExpr_0 = null;

        EObject lv_elements_3_0 = null;



        	enterRule();

        try {
            // InternalSpectra.g:3351:2: ( (this_TemporalRelationalExpr_0= ruleTemporalRelationalExpr ( () ( ( (lv_operator_2_1= '&' | lv_operator_2_2= 'and' ) ) ) ( (lv_elements_3_0= ruleTemporalRelationalExpr ) ) )* ) )
            // InternalSpectra.g:3352:2: (this_TemporalRelationalExpr_0= ruleTemporalRelationalExpr ( () ( ( (lv_operator_2_1= '&' | lv_operator_2_2= 'and' ) ) ) ( (lv_elements_3_0= ruleTemporalRelationalExpr ) ) )* )
            {
            // InternalSpectra.g:3352:2: (this_TemporalRelationalExpr_0= ruleTemporalRelationalExpr ( () ( ( (lv_operator_2_1= '&' | lv_operator_2_2= 'and' ) ) ) ( (lv_elements_3_0= ruleTemporalRelationalExpr ) ) )* )
            // InternalSpectra.g:3353:3: this_TemporalRelationalExpr_0= ruleTemporalRelationalExpr ( () ( ( (lv_operator_2_1= '&' | lv_operator_2_2= 'and' ) ) ) ( (lv_elements_3_0= ruleTemporalRelationalExpr ) ) )*
            {

            			newCompositeNode(grammarAccess.getTemporalAndExprAccess().getTemporalRelationalExprParserRuleCall_0());
            		
            pushFollow(FOLLOW_50);
            this_TemporalRelationalExpr_0=ruleTemporalRelationalExpr();

            state._fsp--;


            			current = this_TemporalRelationalExpr_0;
            			afterParserOrEnumRuleCall();
            		
            // InternalSpectra.g:3361:3: ( () ( ( (lv_operator_2_1= '&' | lv_operator_2_2= 'and' ) ) ) ( (lv_elements_3_0= ruleTemporalRelationalExpr ) ) )*
            loop67:
            do {
                int alt67=2;
                int LA67_0 = input.LA(1);

                if ( ((LA67_0>=66 && LA67_0<=67)) ) {
                    alt67=1;
                }


                switch (alt67) {
            	case 1 :
            	    // InternalSpectra.g:3362:4: () ( ( (lv_operator_2_1= '&' | lv_operator_2_2= 'and' ) ) ) ( (lv_elements_3_0= ruleTemporalRelationalExpr ) )
            	    {
            	    // InternalSpectra.g:3362:4: ()
            	    // InternalSpectra.g:3363:5: 
            	    {

            	    					current = forceCreateModelElementAndAdd(
            	    						grammarAccess.getTemporalAndExprAccess().getTemporalAndExprElementsAction_1_0(),
            	    						current);
            	    				

            	    }

            	    // InternalSpectra.g:3369:4: ( ( (lv_operator_2_1= '&' | lv_operator_2_2= 'and' ) ) )
            	    // InternalSpectra.g:3370:5: ( (lv_operator_2_1= '&' | lv_operator_2_2= 'and' ) )
            	    {
            	    // InternalSpectra.g:3370:5: ( (lv_operator_2_1= '&' | lv_operator_2_2= 'and' ) )
            	    // InternalSpectra.g:3371:6: (lv_operator_2_1= '&' | lv_operator_2_2= 'and' )
            	    {
            	    // InternalSpectra.g:3371:6: (lv_operator_2_1= '&' | lv_operator_2_2= 'and' )
            	    int alt66=2;
            	    int LA66_0 = input.LA(1);

            	    if ( (LA66_0==66) ) {
            	        alt66=1;
            	    }
            	    else if ( (LA66_0==67) ) {
            	        alt66=2;
            	    }
            	    else {
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 66, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt66) {
            	        case 1 :
            	            // InternalSpectra.g:3372:7: lv_operator_2_1= '&'
            	            {
            	            lv_operator_2_1=(Token)match(input,66,FOLLOW_12); 

            	            							newLeafNode(lv_operator_2_1, grammarAccess.getTemporalAndExprAccess().getOperatorAmpersandKeyword_1_1_0_0());
            	            						

            	            							if (current==null) {
            	            								current = createModelElement(grammarAccess.getTemporalAndExprRule());
            	            							}
            	            							addWithLastConsumed(current, "operator", lv_operator_2_1, null);
            	            						

            	            }
            	            break;
            	        case 2 :
            	            // InternalSpectra.g:3383:7: lv_operator_2_2= 'and'
            	            {
            	            lv_operator_2_2=(Token)match(input,67,FOLLOW_12); 

            	            							newLeafNode(lv_operator_2_2, grammarAccess.getTemporalAndExprAccess().getOperatorAndKeyword_1_1_0_1());
            	            						

            	            							if (current==null) {
            	            								current = createModelElement(grammarAccess.getTemporalAndExprRule());
            	            							}
            	            							addWithLastConsumed(current, "operator", lv_operator_2_2, null);
            	            						

            	            }
            	            break;

            	    }


            	    }


            	    }

            	    // InternalSpectra.g:3396:4: ( (lv_elements_3_0= ruleTemporalRelationalExpr ) )
            	    // InternalSpectra.g:3397:5: (lv_elements_3_0= ruleTemporalRelationalExpr )
            	    {
            	    // InternalSpectra.g:3397:5: (lv_elements_3_0= ruleTemporalRelationalExpr )
            	    // InternalSpectra.g:3398:6: lv_elements_3_0= ruleTemporalRelationalExpr
            	    {

            	    						newCompositeNode(grammarAccess.getTemporalAndExprAccess().getElementsTemporalRelationalExprParserRuleCall_1_2_0());
            	    					
            	    pushFollow(FOLLOW_50);
            	    lv_elements_3_0=ruleTemporalRelationalExpr();

            	    state._fsp--;


            	    						if (current==null) {
            	    							current = createModelElementForParent(grammarAccess.getTemporalAndExprRule());
            	    						}
            	    						add(
            	    							current,
            	    							"elements",
            	    							lv_elements_3_0,
            	    							"tau.smlab.syntech.Spectra.TemporalRelationalExpr");
            	    						afterParserOrEnumRuleCall();
            	    					

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop67;
                }
            } while (true);


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleTemporalAndExpr"


    // $ANTLR start "entryRuleTemporalRelationalExpr"
    // InternalSpectra.g:3420:1: entryRuleTemporalRelationalExpr returns [EObject current=null] : iv_ruleTemporalRelationalExpr= ruleTemporalRelationalExpr EOF ;
    public final EObject entryRuleTemporalRelationalExpr() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleTemporalRelationalExpr = null;


        try {
            // InternalSpectra.g:3420:63: (iv_ruleTemporalRelationalExpr= ruleTemporalRelationalExpr EOF )
            // InternalSpectra.g:3421:2: iv_ruleTemporalRelationalExpr= ruleTemporalRelationalExpr EOF
            {
             newCompositeNode(grammarAccess.getTemporalRelationalExprRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleTemporalRelationalExpr=ruleTemporalRelationalExpr();

            state._fsp--;

             current =iv_ruleTemporalRelationalExpr; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleTemporalRelationalExpr"


    // $ANTLR start "ruleTemporalRelationalExpr"
    // InternalSpectra.g:3427:1: ruleTemporalRelationalExpr returns [EObject current=null] : (this_TemporalRemainderExpr_0= ruleTemporalRemainderExpr ( () ( ( (lv_operator_2_1= '=' | lv_operator_2_2= '!=' | lv_operator_2_3= '<' | lv_operator_2_4= '>' | lv_operator_2_5= '<=' | lv_operator_2_6= '>=' ) ) ) ( (lv_right_3_0= ruleTemporalRemainderExpr ) ) )? ) ;
    public final EObject ruleTemporalRelationalExpr() throws RecognitionException {
        EObject current = null;

        Token lv_operator_2_1=null;
        Token lv_operator_2_2=null;
        Token lv_operator_2_3=null;
        Token lv_operator_2_4=null;
        Token lv_operator_2_5=null;
        Token lv_operator_2_6=null;
        EObject this_TemporalRemainderExpr_0 = null;

        EObject lv_right_3_0 = null;



        	enterRule();

        try {
            // InternalSpectra.g:3433:2: ( (this_TemporalRemainderExpr_0= ruleTemporalRemainderExpr ( () ( ( (lv_operator_2_1= '=' | lv_operator_2_2= '!=' | lv_operator_2_3= '<' | lv_operator_2_4= '>' | lv_operator_2_5= '<=' | lv_operator_2_6= '>=' ) ) ) ( (lv_right_3_0= ruleTemporalRemainderExpr ) ) )? ) )
            // InternalSpectra.g:3434:2: (this_TemporalRemainderExpr_0= ruleTemporalRemainderExpr ( () ( ( (lv_operator_2_1= '=' | lv_operator_2_2= '!=' | lv_operator_2_3= '<' | lv_operator_2_4= '>' | lv_operator_2_5= '<=' | lv_operator_2_6= '>=' ) ) ) ( (lv_right_3_0= ruleTemporalRemainderExpr ) ) )? )
            {
            // InternalSpectra.g:3434:2: (this_TemporalRemainderExpr_0= ruleTemporalRemainderExpr ( () ( ( (lv_operator_2_1= '=' | lv_operator_2_2= '!=' | lv_operator_2_3= '<' | lv_operator_2_4= '>' | lv_operator_2_5= '<=' | lv_operator_2_6= '>=' ) ) ) ( (lv_right_3_0= ruleTemporalRemainderExpr ) ) )? )
            // InternalSpectra.g:3435:3: this_TemporalRemainderExpr_0= ruleTemporalRemainderExpr ( () ( ( (lv_operator_2_1= '=' | lv_operator_2_2= '!=' | lv_operator_2_3= '<' | lv_operator_2_4= '>' | lv_operator_2_5= '<=' | lv_operator_2_6= '>=' ) ) ) ( (lv_right_3_0= ruleTemporalRemainderExpr ) ) )?
            {

            			newCompositeNode(grammarAccess.getTemporalRelationalExprAccess().getTemporalRemainderExprParserRuleCall_0());
            		
            pushFollow(FOLLOW_51);
            this_TemporalRemainderExpr_0=ruleTemporalRemainderExpr();

            state._fsp--;


            			current = this_TemporalRemainderExpr_0;
            			afterParserOrEnumRuleCall();
            		
            // InternalSpectra.g:3443:3: ( () ( ( (lv_operator_2_1= '=' | lv_operator_2_2= '!=' | lv_operator_2_3= '<' | lv_operator_2_4= '>' | lv_operator_2_5= '<=' | lv_operator_2_6= '>=' ) ) ) ( (lv_right_3_0= ruleTemporalRemainderExpr ) ) )?
            int alt69=2;
            int LA69_0 = input.LA(1);

            if ( (LA69_0==18||(LA69_0>=68 && LA69_0<=72)) ) {
                alt69=1;
            }
            switch (alt69) {
                case 1 :
                    // InternalSpectra.g:3444:4: () ( ( (lv_operator_2_1= '=' | lv_operator_2_2= '!=' | lv_operator_2_3= '<' | lv_operator_2_4= '>' | lv_operator_2_5= '<=' | lv_operator_2_6= '>=' ) ) ) ( (lv_right_3_0= ruleTemporalRemainderExpr ) )
                    {
                    // InternalSpectra.g:3444:4: ()
                    // InternalSpectra.g:3445:5: 
                    {

                    					current = forceCreateModelElementAndSet(
                    						grammarAccess.getTemporalRelationalExprAccess().getTemporalRelationalExprLeftAction_1_0(),
                    						current);
                    				

                    }

                    // InternalSpectra.g:3451:4: ( ( (lv_operator_2_1= '=' | lv_operator_2_2= '!=' | lv_operator_2_3= '<' | lv_operator_2_4= '>' | lv_operator_2_5= '<=' | lv_operator_2_6= '>=' ) ) )
                    // InternalSpectra.g:3452:5: ( (lv_operator_2_1= '=' | lv_operator_2_2= '!=' | lv_operator_2_3= '<' | lv_operator_2_4= '>' | lv_operator_2_5= '<=' | lv_operator_2_6= '>=' ) )
                    {
                    // InternalSpectra.g:3452:5: ( (lv_operator_2_1= '=' | lv_operator_2_2= '!=' | lv_operator_2_3= '<' | lv_operator_2_4= '>' | lv_operator_2_5= '<=' | lv_operator_2_6= '>=' ) )
                    // InternalSpectra.g:3453:6: (lv_operator_2_1= '=' | lv_operator_2_2= '!=' | lv_operator_2_3= '<' | lv_operator_2_4= '>' | lv_operator_2_5= '<=' | lv_operator_2_6= '>=' )
                    {
                    // InternalSpectra.g:3453:6: (lv_operator_2_1= '=' | lv_operator_2_2= '!=' | lv_operator_2_3= '<' | lv_operator_2_4= '>' | lv_operator_2_5= '<=' | lv_operator_2_6= '>=' )
                    int alt68=6;
                    switch ( input.LA(1) ) {
                    case 18:
                        {
                        alt68=1;
                        }
                        break;
                    case 68:
                        {
                        alt68=2;
                        }
                        break;
                    case 69:
                        {
                        alt68=3;
                        }
                        break;
                    case 70:
                        {
                        alt68=4;
                        }
                        break;
                    case 71:
                        {
                        alt68=5;
                        }
                        break;
                    case 72:
                        {
                        alt68=6;
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("", 68, 0, input);

                        throw nvae;
                    }

                    switch (alt68) {
                        case 1 :
                            // InternalSpectra.g:3454:7: lv_operator_2_1= '='
                            {
                            lv_operator_2_1=(Token)match(input,18,FOLLOW_12); 

                            							newLeafNode(lv_operator_2_1, grammarAccess.getTemporalRelationalExprAccess().getOperatorEqualsSignKeyword_1_1_0_0());
                            						

                            							if (current==null) {
                            								current = createModelElement(grammarAccess.getTemporalRelationalExprRule());
                            							}
                            							setWithLastConsumed(current, "operator", lv_operator_2_1, null);
                            						

                            }
                            break;
                        case 2 :
                            // InternalSpectra.g:3465:7: lv_operator_2_2= '!='
                            {
                            lv_operator_2_2=(Token)match(input,68,FOLLOW_12); 

                            							newLeafNode(lv_operator_2_2, grammarAccess.getTemporalRelationalExprAccess().getOperatorExclamationMarkEqualsSignKeyword_1_1_0_1());
                            						

                            							if (current==null) {
                            								current = createModelElement(grammarAccess.getTemporalRelationalExprRule());
                            							}
                            							setWithLastConsumed(current, "operator", lv_operator_2_2, null);
                            						

                            }
                            break;
                        case 3 :
                            // InternalSpectra.g:3476:7: lv_operator_2_3= '<'
                            {
                            lv_operator_2_3=(Token)match(input,69,FOLLOW_12); 

                            							newLeafNode(lv_operator_2_3, grammarAccess.getTemporalRelationalExprAccess().getOperatorLessThanSignKeyword_1_1_0_2());
                            						

                            							if (current==null) {
                            								current = createModelElement(grammarAccess.getTemporalRelationalExprRule());
                            							}
                            							setWithLastConsumed(current, "operator", lv_operator_2_3, null);
                            						

                            }
                            break;
                        case 4 :
                            // InternalSpectra.g:3487:7: lv_operator_2_4= '>'
                            {
                            lv_operator_2_4=(Token)match(input,70,FOLLOW_12); 

                            							newLeafNode(lv_operator_2_4, grammarAccess.getTemporalRelationalExprAccess().getOperatorGreaterThanSignKeyword_1_1_0_3());
                            						

                            							if (current==null) {
                            								current = createModelElement(grammarAccess.getTemporalRelationalExprRule());
                            							}
                            							setWithLastConsumed(current, "operator", lv_operator_2_4, null);
                            						

                            }
                            break;
                        case 5 :
                            // InternalSpectra.g:3498:7: lv_operator_2_5= '<='
                            {
                            lv_operator_2_5=(Token)match(input,71,FOLLOW_12); 

                            							newLeafNode(lv_operator_2_5, grammarAccess.getTemporalRelationalExprAccess().getOperatorLessThanSignEqualsSignKeyword_1_1_0_4());
                            						

                            							if (current==null) {
                            								current = createModelElement(grammarAccess.getTemporalRelationalExprRule());
                            							}
                            							setWithLastConsumed(current, "operator", lv_operator_2_5, null);
                            						

                            }
                            break;
                        case 6 :
                            // InternalSpectra.g:3509:7: lv_operator_2_6= '>='
                            {
                            lv_operator_2_6=(Token)match(input,72,FOLLOW_12); 

                            							newLeafNode(lv_operator_2_6, grammarAccess.getTemporalRelationalExprAccess().getOperatorGreaterThanSignEqualsSignKeyword_1_1_0_5());
                            						

                            							if (current==null) {
                            								current = createModelElement(grammarAccess.getTemporalRelationalExprRule());
                            							}
                            							setWithLastConsumed(current, "operator", lv_operator_2_6, null);
                            						

                            }
                            break;

                    }


                    }


                    }

                    // InternalSpectra.g:3522:4: ( (lv_right_3_0= ruleTemporalRemainderExpr ) )
                    // InternalSpectra.g:3523:5: (lv_right_3_0= ruleTemporalRemainderExpr )
                    {
                    // InternalSpectra.g:3523:5: (lv_right_3_0= ruleTemporalRemainderExpr )
                    // InternalSpectra.g:3524:6: lv_right_3_0= ruleTemporalRemainderExpr
                    {

                    						newCompositeNode(grammarAccess.getTemporalRelationalExprAccess().getRightTemporalRemainderExprParserRuleCall_1_2_0());
                    					
                    pushFollow(FOLLOW_2);
                    lv_right_3_0=ruleTemporalRemainderExpr();

                    state._fsp--;


                    						if (current==null) {
                    							current = createModelElementForParent(grammarAccess.getTemporalRelationalExprRule());
                    						}
                    						set(
                    							current,
                    							"right",
                    							lv_right_3_0,
                    							"tau.smlab.syntech.Spectra.TemporalRemainderExpr");
                    						afterParserOrEnumRuleCall();
                    					

                    }


                    }


                    }
                    break;

            }


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleTemporalRelationalExpr"


    // $ANTLR start "entryRuleTemporalRemainderExpr"
    // InternalSpectra.g:3546:1: entryRuleTemporalRemainderExpr returns [EObject current=null] : iv_ruleTemporalRemainderExpr= ruleTemporalRemainderExpr EOF ;
    public final EObject entryRuleTemporalRemainderExpr() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleTemporalRemainderExpr = null;


        try {
            // InternalSpectra.g:3546:62: (iv_ruleTemporalRemainderExpr= ruleTemporalRemainderExpr EOF )
            // InternalSpectra.g:3547:2: iv_ruleTemporalRemainderExpr= ruleTemporalRemainderExpr EOF
            {
             newCompositeNode(grammarAccess.getTemporalRemainderExprRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleTemporalRemainderExpr=ruleTemporalRemainderExpr();

            state._fsp--;

             current =iv_ruleTemporalRemainderExpr; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleTemporalRemainderExpr"


    // $ANTLR start "ruleTemporalRemainderExpr"
    // InternalSpectra.g:3553:1: ruleTemporalRemainderExpr returns [EObject current=null] : (this_TemporalAdditiveExpr_0= ruleTemporalAdditiveExpr ( () ( ( (lv_operator_2_1= 'mod' | lv_operator_2_2= '%' ) ) ) ( (lv_right_3_0= ruleTemporalAdditiveExpr ) ) )? ) ;
    public final EObject ruleTemporalRemainderExpr() throws RecognitionException {
        EObject current = null;

        Token lv_operator_2_1=null;
        Token lv_operator_2_2=null;
        EObject this_TemporalAdditiveExpr_0 = null;

        EObject lv_right_3_0 = null;



        	enterRule();

        try {
            // InternalSpectra.g:3559:2: ( (this_TemporalAdditiveExpr_0= ruleTemporalAdditiveExpr ( () ( ( (lv_operator_2_1= 'mod' | lv_operator_2_2= '%' ) ) ) ( (lv_right_3_0= ruleTemporalAdditiveExpr ) ) )? ) )
            // InternalSpectra.g:3560:2: (this_TemporalAdditiveExpr_0= ruleTemporalAdditiveExpr ( () ( ( (lv_operator_2_1= 'mod' | lv_operator_2_2= '%' ) ) ) ( (lv_right_3_0= ruleTemporalAdditiveExpr ) ) )? )
            {
            // InternalSpectra.g:3560:2: (this_TemporalAdditiveExpr_0= ruleTemporalAdditiveExpr ( () ( ( (lv_operator_2_1= 'mod' | lv_operator_2_2= '%' ) ) ) ( (lv_right_3_0= ruleTemporalAdditiveExpr ) ) )? )
            // InternalSpectra.g:3561:3: this_TemporalAdditiveExpr_0= ruleTemporalAdditiveExpr ( () ( ( (lv_operator_2_1= 'mod' | lv_operator_2_2= '%' ) ) ) ( (lv_right_3_0= ruleTemporalAdditiveExpr ) ) )?
            {

            			newCompositeNode(grammarAccess.getTemporalRemainderExprAccess().getTemporalAdditiveExprParserRuleCall_0());
            		
            pushFollow(FOLLOW_52);
            this_TemporalAdditiveExpr_0=ruleTemporalAdditiveExpr();

            state._fsp--;


            			current = this_TemporalAdditiveExpr_0;
            			afterParserOrEnumRuleCall();
            		
            // InternalSpectra.g:3569:3: ( () ( ( (lv_operator_2_1= 'mod' | lv_operator_2_2= '%' ) ) ) ( (lv_right_3_0= ruleTemporalAdditiveExpr ) ) )?
            int alt71=2;
            int LA71_0 = input.LA(1);

            if ( ((LA71_0>=73 && LA71_0<=74)) ) {
                alt71=1;
            }
            switch (alt71) {
                case 1 :
                    // InternalSpectra.g:3570:4: () ( ( (lv_operator_2_1= 'mod' | lv_operator_2_2= '%' ) ) ) ( (lv_right_3_0= ruleTemporalAdditiveExpr ) )
                    {
                    // InternalSpectra.g:3570:4: ()
                    // InternalSpectra.g:3571:5: 
                    {

                    					current = forceCreateModelElementAndSet(
                    						grammarAccess.getTemporalRemainderExprAccess().getTemporalRemainderExprLeftAction_1_0(),
                    						current);
                    				

                    }

                    // InternalSpectra.g:3577:4: ( ( (lv_operator_2_1= 'mod' | lv_operator_2_2= '%' ) ) )
                    // InternalSpectra.g:3578:5: ( (lv_operator_2_1= 'mod' | lv_operator_2_2= '%' ) )
                    {
                    // InternalSpectra.g:3578:5: ( (lv_operator_2_1= 'mod' | lv_operator_2_2= '%' ) )
                    // InternalSpectra.g:3579:6: (lv_operator_2_1= 'mod' | lv_operator_2_2= '%' )
                    {
                    // InternalSpectra.g:3579:6: (lv_operator_2_1= 'mod' | lv_operator_2_2= '%' )
                    int alt70=2;
                    int LA70_0 = input.LA(1);

                    if ( (LA70_0==73) ) {
                        alt70=1;
                    }
                    else if ( (LA70_0==74) ) {
                        alt70=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 70, 0, input);

                        throw nvae;
                    }
                    switch (alt70) {
                        case 1 :
                            // InternalSpectra.g:3580:7: lv_operator_2_1= 'mod'
                            {
                            lv_operator_2_1=(Token)match(input,73,FOLLOW_12); 

                            							newLeafNode(lv_operator_2_1, grammarAccess.getTemporalRemainderExprAccess().getOperatorModKeyword_1_1_0_0());
                            						

                            							if (current==null) {
                            								current = createModelElement(grammarAccess.getTemporalRemainderExprRule());
                            							}
                            							setWithLastConsumed(current, "operator", lv_operator_2_1, null);
                            						

                            }
                            break;
                        case 2 :
                            // InternalSpectra.g:3591:7: lv_operator_2_2= '%'
                            {
                            lv_operator_2_2=(Token)match(input,74,FOLLOW_12); 

                            							newLeafNode(lv_operator_2_2, grammarAccess.getTemporalRemainderExprAccess().getOperatorPercentSignKeyword_1_1_0_1());
                            						

                            							if (current==null) {
                            								current = createModelElement(grammarAccess.getTemporalRemainderExprRule());
                            							}
                            							setWithLastConsumed(current, "operator", lv_operator_2_2, null);
                            						

                            }
                            break;

                    }


                    }


                    }

                    // InternalSpectra.g:3604:4: ( (lv_right_3_0= ruleTemporalAdditiveExpr ) )
                    // InternalSpectra.g:3605:5: (lv_right_3_0= ruleTemporalAdditiveExpr )
                    {
                    // InternalSpectra.g:3605:5: (lv_right_3_0= ruleTemporalAdditiveExpr )
                    // InternalSpectra.g:3606:6: lv_right_3_0= ruleTemporalAdditiveExpr
                    {

                    						newCompositeNode(grammarAccess.getTemporalRemainderExprAccess().getRightTemporalAdditiveExprParserRuleCall_1_2_0());
                    					
                    pushFollow(FOLLOW_2);
                    lv_right_3_0=ruleTemporalAdditiveExpr();

                    state._fsp--;


                    						if (current==null) {
                    							current = createModelElementForParent(grammarAccess.getTemporalRemainderExprRule());
                    						}
                    						set(
                    							current,
                    							"right",
                    							lv_right_3_0,
                    							"tau.smlab.syntech.Spectra.TemporalAdditiveExpr");
                    						afterParserOrEnumRuleCall();
                    					

                    }


                    }


                    }
                    break;

            }


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleTemporalRemainderExpr"


    // $ANTLR start "entryRuleTemporalAdditiveExpr"
    // InternalSpectra.g:3628:1: entryRuleTemporalAdditiveExpr returns [EObject current=null] : iv_ruleTemporalAdditiveExpr= ruleTemporalAdditiveExpr EOF ;
    public final EObject entryRuleTemporalAdditiveExpr() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleTemporalAdditiveExpr = null;


        try {
            // InternalSpectra.g:3628:61: (iv_ruleTemporalAdditiveExpr= ruleTemporalAdditiveExpr EOF )
            // InternalSpectra.g:3629:2: iv_ruleTemporalAdditiveExpr= ruleTemporalAdditiveExpr EOF
            {
             newCompositeNode(grammarAccess.getTemporalAdditiveExprRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleTemporalAdditiveExpr=ruleTemporalAdditiveExpr();

            state._fsp--;

             current =iv_ruleTemporalAdditiveExpr; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleTemporalAdditiveExpr"


    // $ANTLR start "ruleTemporalAdditiveExpr"
    // InternalSpectra.g:3635:1: ruleTemporalAdditiveExpr returns [EObject current=null] : (this_TemporalMultiplicativeExpr_0= ruleTemporalMultiplicativeExpr ( () ( ( (lv_operator_2_1= '+' | lv_operator_2_2= '-' ) ) ) ( (lv_elements_3_0= ruleTemporalMultiplicativeExpr ) ) )* ) ;
    public final EObject ruleTemporalAdditiveExpr() throws RecognitionException {
        EObject current = null;

        Token lv_operator_2_1=null;
        Token lv_operator_2_2=null;
        EObject this_TemporalMultiplicativeExpr_0 = null;

        EObject lv_elements_3_0 = null;



        	enterRule();

        try {
            // InternalSpectra.g:3641:2: ( (this_TemporalMultiplicativeExpr_0= ruleTemporalMultiplicativeExpr ( () ( ( (lv_operator_2_1= '+' | lv_operator_2_2= '-' ) ) ) ( (lv_elements_3_0= ruleTemporalMultiplicativeExpr ) ) )* ) )
            // InternalSpectra.g:3642:2: (this_TemporalMultiplicativeExpr_0= ruleTemporalMultiplicativeExpr ( () ( ( (lv_operator_2_1= '+' | lv_operator_2_2= '-' ) ) ) ( (lv_elements_3_0= ruleTemporalMultiplicativeExpr ) ) )* )
            {
            // InternalSpectra.g:3642:2: (this_TemporalMultiplicativeExpr_0= ruleTemporalMultiplicativeExpr ( () ( ( (lv_operator_2_1= '+' | lv_operator_2_2= '-' ) ) ) ( (lv_elements_3_0= ruleTemporalMultiplicativeExpr ) ) )* )
            // InternalSpectra.g:3643:3: this_TemporalMultiplicativeExpr_0= ruleTemporalMultiplicativeExpr ( () ( ( (lv_operator_2_1= '+' | lv_operator_2_2= '-' ) ) ) ( (lv_elements_3_0= ruleTemporalMultiplicativeExpr ) ) )*
            {

            			newCompositeNode(grammarAccess.getTemporalAdditiveExprAccess().getTemporalMultiplicativeExprParserRuleCall_0());
            		
            pushFollow(FOLLOW_53);
            this_TemporalMultiplicativeExpr_0=ruleTemporalMultiplicativeExpr();

            state._fsp--;


            			current = this_TemporalMultiplicativeExpr_0;
            			afterParserOrEnumRuleCall();
            		
            // InternalSpectra.g:3651:3: ( () ( ( (lv_operator_2_1= '+' | lv_operator_2_2= '-' ) ) ) ( (lv_elements_3_0= ruleTemporalMultiplicativeExpr ) ) )*
            loop73:
            do {
                int alt73=2;
                int LA73_0 = input.LA(1);

                if ( (LA73_0==16||LA73_0==75) ) {
                    alt73=1;
                }


                switch (alt73) {
            	case 1 :
            	    // InternalSpectra.g:3652:4: () ( ( (lv_operator_2_1= '+' | lv_operator_2_2= '-' ) ) ) ( (lv_elements_3_0= ruleTemporalMultiplicativeExpr ) )
            	    {
            	    // InternalSpectra.g:3652:4: ()
            	    // InternalSpectra.g:3653:5: 
            	    {

            	    					current = forceCreateModelElementAndAdd(
            	    						grammarAccess.getTemporalAdditiveExprAccess().getTemporalAdditiveExprElementsAction_1_0(),
            	    						current);
            	    				

            	    }

            	    // InternalSpectra.g:3659:4: ( ( (lv_operator_2_1= '+' | lv_operator_2_2= '-' ) ) )
            	    // InternalSpectra.g:3660:5: ( (lv_operator_2_1= '+' | lv_operator_2_2= '-' ) )
            	    {
            	    // InternalSpectra.g:3660:5: ( (lv_operator_2_1= '+' | lv_operator_2_2= '-' ) )
            	    // InternalSpectra.g:3661:6: (lv_operator_2_1= '+' | lv_operator_2_2= '-' )
            	    {
            	    // InternalSpectra.g:3661:6: (lv_operator_2_1= '+' | lv_operator_2_2= '-' )
            	    int alt72=2;
            	    int LA72_0 = input.LA(1);

            	    if ( (LA72_0==75) ) {
            	        alt72=1;
            	    }
            	    else if ( (LA72_0==16) ) {
            	        alt72=2;
            	    }
            	    else {
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 72, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt72) {
            	        case 1 :
            	            // InternalSpectra.g:3662:7: lv_operator_2_1= '+'
            	            {
            	            lv_operator_2_1=(Token)match(input,75,FOLLOW_12); 

            	            							newLeafNode(lv_operator_2_1, grammarAccess.getTemporalAdditiveExprAccess().getOperatorPlusSignKeyword_1_1_0_0());
            	            						

            	            							if (current==null) {
            	            								current = createModelElement(grammarAccess.getTemporalAdditiveExprRule());
            	            							}
            	            							addWithLastConsumed(current, "operator", lv_operator_2_1, null);
            	            						

            	            }
            	            break;
            	        case 2 :
            	            // InternalSpectra.g:3673:7: lv_operator_2_2= '-'
            	            {
            	            lv_operator_2_2=(Token)match(input,16,FOLLOW_12); 

            	            							newLeafNode(lv_operator_2_2, grammarAccess.getTemporalAdditiveExprAccess().getOperatorHyphenMinusKeyword_1_1_0_1());
            	            						

            	            							if (current==null) {
            	            								current = createModelElement(grammarAccess.getTemporalAdditiveExprRule());
            	            							}
            	            							addWithLastConsumed(current, "operator", lv_operator_2_2, null);
            	            						

            	            }
            	            break;

            	    }


            	    }


            	    }

            	    // InternalSpectra.g:3686:4: ( (lv_elements_3_0= ruleTemporalMultiplicativeExpr ) )
            	    // InternalSpectra.g:3687:5: (lv_elements_3_0= ruleTemporalMultiplicativeExpr )
            	    {
            	    // InternalSpectra.g:3687:5: (lv_elements_3_0= ruleTemporalMultiplicativeExpr )
            	    // InternalSpectra.g:3688:6: lv_elements_3_0= ruleTemporalMultiplicativeExpr
            	    {

            	    						newCompositeNode(grammarAccess.getTemporalAdditiveExprAccess().getElementsTemporalMultiplicativeExprParserRuleCall_1_2_0());
            	    					
            	    pushFollow(FOLLOW_53);
            	    lv_elements_3_0=ruleTemporalMultiplicativeExpr();

            	    state._fsp--;


            	    						if (current==null) {
            	    							current = createModelElementForParent(grammarAccess.getTemporalAdditiveExprRule());
            	    						}
            	    						add(
            	    							current,
            	    							"elements",
            	    							lv_elements_3_0,
            	    							"tau.smlab.syntech.Spectra.TemporalMultiplicativeExpr");
            	    						afterParserOrEnumRuleCall();
            	    					

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop73;
                }
            } while (true);


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleTemporalAdditiveExpr"


    // $ANTLR start "entryRuleTemporalMultiplicativeExpr"
    // InternalSpectra.g:3710:1: entryRuleTemporalMultiplicativeExpr returns [EObject current=null] : iv_ruleTemporalMultiplicativeExpr= ruleTemporalMultiplicativeExpr EOF ;
    public final EObject entryRuleTemporalMultiplicativeExpr() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleTemporalMultiplicativeExpr = null;


        try {
            // InternalSpectra.g:3710:67: (iv_ruleTemporalMultiplicativeExpr= ruleTemporalMultiplicativeExpr EOF )
            // InternalSpectra.g:3711:2: iv_ruleTemporalMultiplicativeExpr= ruleTemporalMultiplicativeExpr EOF
            {
             newCompositeNode(grammarAccess.getTemporalMultiplicativeExprRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleTemporalMultiplicativeExpr=ruleTemporalMultiplicativeExpr();

            state._fsp--;

             current =iv_ruleTemporalMultiplicativeExpr; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleTemporalMultiplicativeExpr"


    // $ANTLR start "ruleTemporalMultiplicativeExpr"
    // InternalSpectra.g:3717:1: ruleTemporalMultiplicativeExpr returns [EObject current=null] : (this_TemporalBinaryExpr_0= ruleTemporalBinaryExpr ( () ( ( (lv_operator_2_1= '*' | lv_operator_2_2= '/' ) ) ) ( (lv_elements_3_0= ruleTemporalBinaryExpr ) ) )* ) ;
    public final EObject ruleTemporalMultiplicativeExpr() throws RecognitionException {
        EObject current = null;

        Token lv_operator_2_1=null;
        Token lv_operator_2_2=null;
        EObject this_TemporalBinaryExpr_0 = null;

        EObject lv_elements_3_0 = null;



        	enterRule();

        try {
            // InternalSpectra.g:3723:2: ( (this_TemporalBinaryExpr_0= ruleTemporalBinaryExpr ( () ( ( (lv_operator_2_1= '*' | lv_operator_2_2= '/' ) ) ) ( (lv_elements_3_0= ruleTemporalBinaryExpr ) ) )* ) )
            // InternalSpectra.g:3724:2: (this_TemporalBinaryExpr_0= ruleTemporalBinaryExpr ( () ( ( (lv_operator_2_1= '*' | lv_operator_2_2= '/' ) ) ) ( (lv_elements_3_0= ruleTemporalBinaryExpr ) ) )* )
            {
            // InternalSpectra.g:3724:2: (this_TemporalBinaryExpr_0= ruleTemporalBinaryExpr ( () ( ( (lv_operator_2_1= '*' | lv_operator_2_2= '/' ) ) ) ( (lv_elements_3_0= ruleTemporalBinaryExpr ) ) )* )
            // InternalSpectra.g:3725:3: this_TemporalBinaryExpr_0= ruleTemporalBinaryExpr ( () ( ( (lv_operator_2_1= '*' | lv_operator_2_2= '/' ) ) ) ( (lv_elements_3_0= ruleTemporalBinaryExpr ) ) )*
            {

            			newCompositeNode(grammarAccess.getTemporalMultiplicativeExprAccess().getTemporalBinaryExprParserRuleCall_0());
            		
            pushFollow(FOLLOW_54);
            this_TemporalBinaryExpr_0=ruleTemporalBinaryExpr();

            state._fsp--;


            			current = this_TemporalBinaryExpr_0;
            			afterParserOrEnumRuleCall();
            		
            // InternalSpectra.g:3733:3: ( () ( ( (lv_operator_2_1= '*' | lv_operator_2_2= '/' ) ) ) ( (lv_elements_3_0= ruleTemporalBinaryExpr ) ) )*
            loop75:
            do {
                int alt75=2;
                int LA75_0 = input.LA(1);

                if ( ((LA75_0>=76 && LA75_0<=77)) ) {
                    alt75=1;
                }


                switch (alt75) {
            	case 1 :
            	    // InternalSpectra.g:3734:4: () ( ( (lv_operator_2_1= '*' | lv_operator_2_2= '/' ) ) ) ( (lv_elements_3_0= ruleTemporalBinaryExpr ) )
            	    {
            	    // InternalSpectra.g:3734:4: ()
            	    // InternalSpectra.g:3735:5: 
            	    {

            	    					current = forceCreateModelElementAndAdd(
            	    						grammarAccess.getTemporalMultiplicativeExprAccess().getTemporalMultiplicativeExprElementsAction_1_0(),
            	    						current);
            	    				

            	    }

            	    // InternalSpectra.g:3741:4: ( ( (lv_operator_2_1= '*' | lv_operator_2_2= '/' ) ) )
            	    // InternalSpectra.g:3742:5: ( (lv_operator_2_1= '*' | lv_operator_2_2= '/' ) )
            	    {
            	    // InternalSpectra.g:3742:5: ( (lv_operator_2_1= '*' | lv_operator_2_2= '/' ) )
            	    // InternalSpectra.g:3743:6: (lv_operator_2_1= '*' | lv_operator_2_2= '/' )
            	    {
            	    // InternalSpectra.g:3743:6: (lv_operator_2_1= '*' | lv_operator_2_2= '/' )
            	    int alt74=2;
            	    int LA74_0 = input.LA(1);

            	    if ( (LA74_0==76) ) {
            	        alt74=1;
            	    }
            	    else if ( (LA74_0==77) ) {
            	        alt74=2;
            	    }
            	    else {
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 74, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt74) {
            	        case 1 :
            	            // InternalSpectra.g:3744:7: lv_operator_2_1= '*'
            	            {
            	            lv_operator_2_1=(Token)match(input,76,FOLLOW_12); 

            	            							newLeafNode(lv_operator_2_1, grammarAccess.getTemporalMultiplicativeExprAccess().getOperatorAsteriskKeyword_1_1_0_0());
            	            						

            	            							if (current==null) {
            	            								current = createModelElement(grammarAccess.getTemporalMultiplicativeExprRule());
            	            							}
            	            							addWithLastConsumed(current, "operator", lv_operator_2_1, null);
            	            						

            	            }
            	            break;
            	        case 2 :
            	            // InternalSpectra.g:3755:7: lv_operator_2_2= '/'
            	            {
            	            lv_operator_2_2=(Token)match(input,77,FOLLOW_12); 

            	            							newLeafNode(lv_operator_2_2, grammarAccess.getTemporalMultiplicativeExprAccess().getOperatorSolidusKeyword_1_1_0_1());
            	            						

            	            							if (current==null) {
            	            								current = createModelElement(grammarAccess.getTemporalMultiplicativeExprRule());
            	            							}
            	            							addWithLastConsumed(current, "operator", lv_operator_2_2, null);
            	            						

            	            }
            	            break;

            	    }


            	    }


            	    }

            	    // InternalSpectra.g:3768:4: ( (lv_elements_3_0= ruleTemporalBinaryExpr ) )
            	    // InternalSpectra.g:3769:5: (lv_elements_3_0= ruleTemporalBinaryExpr )
            	    {
            	    // InternalSpectra.g:3769:5: (lv_elements_3_0= ruleTemporalBinaryExpr )
            	    // InternalSpectra.g:3770:6: lv_elements_3_0= ruleTemporalBinaryExpr
            	    {

            	    						newCompositeNode(grammarAccess.getTemporalMultiplicativeExprAccess().getElementsTemporalBinaryExprParserRuleCall_1_2_0());
            	    					
            	    pushFollow(FOLLOW_54);
            	    lv_elements_3_0=ruleTemporalBinaryExpr();

            	    state._fsp--;


            	    						if (current==null) {
            	    							current = createModelElementForParent(grammarAccess.getTemporalMultiplicativeExprRule());
            	    						}
            	    						add(
            	    							current,
            	    							"elements",
            	    							lv_elements_3_0,
            	    							"tau.smlab.syntech.Spectra.TemporalBinaryExpr");
            	    						afterParserOrEnumRuleCall();
            	    					

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop75;
                }
            } while (true);


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleTemporalMultiplicativeExpr"


    // $ANTLR start "entryRuleTemporalBinaryExpr"
    // InternalSpectra.g:3792:1: entryRuleTemporalBinaryExpr returns [EObject current=null] : iv_ruleTemporalBinaryExpr= ruleTemporalBinaryExpr EOF ;
    public final EObject entryRuleTemporalBinaryExpr() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleTemporalBinaryExpr = null;


        try {
            // InternalSpectra.g:3792:59: (iv_ruleTemporalBinaryExpr= ruleTemporalBinaryExpr EOF )
            // InternalSpectra.g:3793:2: iv_ruleTemporalBinaryExpr= ruleTemporalBinaryExpr EOF
            {
             newCompositeNode(grammarAccess.getTemporalBinaryExprRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleTemporalBinaryExpr=ruleTemporalBinaryExpr();

            state._fsp--;

             current =iv_ruleTemporalBinaryExpr; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleTemporalBinaryExpr"


    // $ANTLR start "ruleTemporalBinaryExpr"
    // InternalSpectra.g:3799:1: ruleTemporalBinaryExpr returns [EObject current=null] : (this_TemporalUnaryExpr_0= ruleTemporalUnaryExpr ( () ( ( (lv_operator_2_1= 'S' | lv_operator_2_2= 'SINCE' | lv_operator_2_3= 'T' | lv_operator_2_4= 'TRIGGERED' ) ) ) ( (lv_elements_3_0= ruleTemporalUnaryExpr ) ) )* ) ;
    public final EObject ruleTemporalBinaryExpr() throws RecognitionException {
        EObject current = null;

        Token lv_operator_2_1=null;
        Token lv_operator_2_2=null;
        Token lv_operator_2_3=null;
        Token lv_operator_2_4=null;
        EObject this_TemporalUnaryExpr_0 = null;

        EObject lv_elements_3_0 = null;



        	enterRule();

        try {
            // InternalSpectra.g:3805:2: ( (this_TemporalUnaryExpr_0= ruleTemporalUnaryExpr ( () ( ( (lv_operator_2_1= 'S' | lv_operator_2_2= 'SINCE' | lv_operator_2_3= 'T' | lv_operator_2_4= 'TRIGGERED' ) ) ) ( (lv_elements_3_0= ruleTemporalUnaryExpr ) ) )* ) )
            // InternalSpectra.g:3806:2: (this_TemporalUnaryExpr_0= ruleTemporalUnaryExpr ( () ( ( (lv_operator_2_1= 'S' | lv_operator_2_2= 'SINCE' | lv_operator_2_3= 'T' | lv_operator_2_4= 'TRIGGERED' ) ) ) ( (lv_elements_3_0= ruleTemporalUnaryExpr ) ) )* )
            {
            // InternalSpectra.g:3806:2: (this_TemporalUnaryExpr_0= ruleTemporalUnaryExpr ( () ( ( (lv_operator_2_1= 'S' | lv_operator_2_2= 'SINCE' | lv_operator_2_3= 'T' | lv_operator_2_4= 'TRIGGERED' ) ) ) ( (lv_elements_3_0= ruleTemporalUnaryExpr ) ) )* )
            // InternalSpectra.g:3807:3: this_TemporalUnaryExpr_0= ruleTemporalUnaryExpr ( () ( ( (lv_operator_2_1= 'S' | lv_operator_2_2= 'SINCE' | lv_operator_2_3= 'T' | lv_operator_2_4= 'TRIGGERED' ) ) ) ( (lv_elements_3_0= ruleTemporalUnaryExpr ) ) )*
            {

            			newCompositeNode(grammarAccess.getTemporalBinaryExprAccess().getTemporalUnaryExprParserRuleCall_0());
            		
            pushFollow(FOLLOW_55);
            this_TemporalUnaryExpr_0=ruleTemporalUnaryExpr();

            state._fsp--;


            			current = this_TemporalUnaryExpr_0;
            			afterParserOrEnumRuleCall();
            		
            // InternalSpectra.g:3815:3: ( () ( ( (lv_operator_2_1= 'S' | lv_operator_2_2= 'SINCE' | lv_operator_2_3= 'T' | lv_operator_2_4= 'TRIGGERED' ) ) ) ( (lv_elements_3_0= ruleTemporalUnaryExpr ) ) )*
            loop77:
            do {
                int alt77=2;
                int LA77_0 = input.LA(1);

                if ( ((LA77_0>=78 && LA77_0<=81)) ) {
                    alt77=1;
                }


                switch (alt77) {
            	case 1 :
            	    // InternalSpectra.g:3816:4: () ( ( (lv_operator_2_1= 'S' | lv_operator_2_2= 'SINCE' | lv_operator_2_3= 'T' | lv_operator_2_4= 'TRIGGERED' ) ) ) ( (lv_elements_3_0= ruleTemporalUnaryExpr ) )
            	    {
            	    // InternalSpectra.g:3816:4: ()
            	    // InternalSpectra.g:3817:5: 
            	    {

            	    					current = forceCreateModelElementAndAdd(
            	    						grammarAccess.getTemporalBinaryExprAccess().getTemporalBinaryExprElementsAction_1_0(),
            	    						current);
            	    				

            	    }

            	    // InternalSpectra.g:3823:4: ( ( (lv_operator_2_1= 'S' | lv_operator_2_2= 'SINCE' | lv_operator_2_3= 'T' | lv_operator_2_4= 'TRIGGERED' ) ) )
            	    // InternalSpectra.g:3824:5: ( (lv_operator_2_1= 'S' | lv_operator_2_2= 'SINCE' | lv_operator_2_3= 'T' | lv_operator_2_4= 'TRIGGERED' ) )
            	    {
            	    // InternalSpectra.g:3824:5: ( (lv_operator_2_1= 'S' | lv_operator_2_2= 'SINCE' | lv_operator_2_3= 'T' | lv_operator_2_4= 'TRIGGERED' ) )
            	    // InternalSpectra.g:3825:6: (lv_operator_2_1= 'S' | lv_operator_2_2= 'SINCE' | lv_operator_2_3= 'T' | lv_operator_2_4= 'TRIGGERED' )
            	    {
            	    // InternalSpectra.g:3825:6: (lv_operator_2_1= 'S' | lv_operator_2_2= 'SINCE' | lv_operator_2_3= 'T' | lv_operator_2_4= 'TRIGGERED' )
            	    int alt76=4;
            	    switch ( input.LA(1) ) {
            	    case 78:
            	        {
            	        alt76=1;
            	        }
            	        break;
            	    case 79:
            	        {
            	        alt76=2;
            	        }
            	        break;
            	    case 80:
            	        {
            	        alt76=3;
            	        }
            	        break;
            	    case 81:
            	        {
            	        alt76=4;
            	        }
            	        break;
            	    default:
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 76, 0, input);

            	        throw nvae;
            	    }

            	    switch (alt76) {
            	        case 1 :
            	            // InternalSpectra.g:3826:7: lv_operator_2_1= 'S'
            	            {
            	            lv_operator_2_1=(Token)match(input,78,FOLLOW_12); 

            	            							newLeafNode(lv_operator_2_1, grammarAccess.getTemporalBinaryExprAccess().getOperatorSKeyword_1_1_0_0());
            	            						

            	            							if (current==null) {
            	            								current = createModelElement(grammarAccess.getTemporalBinaryExprRule());
            	            							}
            	            							addWithLastConsumed(current, "operator", lv_operator_2_1, null);
            	            						

            	            }
            	            break;
            	        case 2 :
            	            // InternalSpectra.g:3837:7: lv_operator_2_2= 'SINCE'
            	            {
            	            lv_operator_2_2=(Token)match(input,79,FOLLOW_12); 

            	            							newLeafNode(lv_operator_2_2, grammarAccess.getTemporalBinaryExprAccess().getOperatorSINCEKeyword_1_1_0_1());
            	            						

            	            							if (current==null) {
            	            								current = createModelElement(grammarAccess.getTemporalBinaryExprRule());
            	            							}
            	            							addWithLastConsumed(current, "operator", lv_operator_2_2, null);
            	            						

            	            }
            	            break;
            	        case 3 :
            	            // InternalSpectra.g:3848:7: lv_operator_2_3= 'T'
            	            {
            	            lv_operator_2_3=(Token)match(input,80,FOLLOW_12); 

            	            							newLeafNode(lv_operator_2_3, grammarAccess.getTemporalBinaryExprAccess().getOperatorTKeyword_1_1_0_2());
            	            						

            	            							if (current==null) {
            	            								current = createModelElement(grammarAccess.getTemporalBinaryExprRule());
            	            							}
            	            							addWithLastConsumed(current, "operator", lv_operator_2_3, null);
            	            						

            	            }
            	            break;
            	        case 4 :
            	            // InternalSpectra.g:3859:7: lv_operator_2_4= 'TRIGGERED'
            	            {
            	            lv_operator_2_4=(Token)match(input,81,FOLLOW_12); 

            	            							newLeafNode(lv_operator_2_4, grammarAccess.getTemporalBinaryExprAccess().getOperatorTRIGGEREDKeyword_1_1_0_3());
            	            						

            	            							if (current==null) {
            	            								current = createModelElement(grammarAccess.getTemporalBinaryExprRule());
            	            							}
            	            							addWithLastConsumed(current, "operator", lv_operator_2_4, null);
            	            						

            	            }
            	            break;

            	    }


            	    }


            	    }

            	    // InternalSpectra.g:3872:4: ( (lv_elements_3_0= ruleTemporalUnaryExpr ) )
            	    // InternalSpectra.g:3873:5: (lv_elements_3_0= ruleTemporalUnaryExpr )
            	    {
            	    // InternalSpectra.g:3873:5: (lv_elements_3_0= ruleTemporalUnaryExpr )
            	    // InternalSpectra.g:3874:6: lv_elements_3_0= ruleTemporalUnaryExpr
            	    {

            	    						newCompositeNode(grammarAccess.getTemporalBinaryExprAccess().getElementsTemporalUnaryExprParserRuleCall_1_2_0());
            	    					
            	    pushFollow(FOLLOW_55);
            	    lv_elements_3_0=ruleTemporalUnaryExpr();

            	    state._fsp--;


            	    						if (current==null) {
            	    							current = createModelElementForParent(grammarAccess.getTemporalBinaryExprRule());
            	    						}
            	    						add(
            	    							current,
            	    							"elements",
            	    							lv_elements_3_0,
            	    							"tau.smlab.syntech.Spectra.TemporalUnaryExpr");
            	    						afterParserOrEnumRuleCall();
            	    					

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop77;
                }
            } while (true);


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleTemporalBinaryExpr"


    // $ANTLR start "entryRuleTemporalUnaryExpr"
    // InternalSpectra.g:3896:1: entryRuleTemporalUnaryExpr returns [EObject current=null] : iv_ruleTemporalUnaryExpr= ruleTemporalUnaryExpr EOF ;
    public final EObject entryRuleTemporalUnaryExpr() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleTemporalUnaryExpr = null;


        try {
            // InternalSpectra.g:3896:58: (iv_ruleTemporalUnaryExpr= ruleTemporalUnaryExpr EOF )
            // InternalSpectra.g:3897:2: iv_ruleTemporalUnaryExpr= ruleTemporalUnaryExpr EOF
            {
             newCompositeNode(grammarAccess.getTemporalUnaryExprRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleTemporalUnaryExpr=ruleTemporalUnaryExpr();

            state._fsp--;

             current =iv_ruleTemporalUnaryExpr; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleTemporalUnaryExpr"


    // $ANTLR start "ruleTemporalUnaryExpr"
    // InternalSpectra.g:3903:1: ruleTemporalUnaryExpr returns [EObject current=null] : (this_TemporalPrimaryExpr_0= ruleTemporalPrimaryExpr | ( () ( ( ( ( (lv_kinds_2_1= 'Y' | lv_kinds_2_2= 'PREV' ) ) ) ( (lv_tue_3_0= ruleTemporalUnaryExpr ) ) ) | ( ( ( (lv_kinds_4_1= 'H' | lv_kinds_4_2= 'HISTORICALLY' ) ) ) ( (lv_tue_5_0= ruleTemporalUnaryExpr ) ) ) | ( ( ( (lv_kinds_6_1= 'O' | lv_kinds_6_2= 'ONCE' ) ) ) ( (lv_tue_7_0= ruleTemporalUnaryExpr ) ) ) ) ) ) ;
    public final EObject ruleTemporalUnaryExpr() throws RecognitionException {
        EObject current = null;

        Token lv_kinds_2_1=null;
        Token lv_kinds_2_2=null;
        Token lv_kinds_4_1=null;
        Token lv_kinds_4_2=null;
        Token lv_kinds_6_1=null;
        Token lv_kinds_6_2=null;
        EObject this_TemporalPrimaryExpr_0 = null;

        EObject lv_tue_3_0 = null;

        EObject lv_tue_5_0 = null;

        EObject lv_tue_7_0 = null;



        	enterRule();

        try {
            // InternalSpectra.g:3909:2: ( (this_TemporalPrimaryExpr_0= ruleTemporalPrimaryExpr | ( () ( ( ( ( (lv_kinds_2_1= 'Y' | lv_kinds_2_2= 'PREV' ) ) ) ( (lv_tue_3_0= ruleTemporalUnaryExpr ) ) ) | ( ( ( (lv_kinds_4_1= 'H' | lv_kinds_4_2= 'HISTORICALLY' ) ) ) ( (lv_tue_5_0= ruleTemporalUnaryExpr ) ) ) | ( ( ( (lv_kinds_6_1= 'O' | lv_kinds_6_2= 'ONCE' ) ) ) ( (lv_tue_7_0= ruleTemporalUnaryExpr ) ) ) ) ) ) )
            // InternalSpectra.g:3910:2: (this_TemporalPrimaryExpr_0= ruleTemporalPrimaryExpr | ( () ( ( ( ( (lv_kinds_2_1= 'Y' | lv_kinds_2_2= 'PREV' ) ) ) ( (lv_tue_3_0= ruleTemporalUnaryExpr ) ) ) | ( ( ( (lv_kinds_4_1= 'H' | lv_kinds_4_2= 'HISTORICALLY' ) ) ) ( (lv_tue_5_0= ruleTemporalUnaryExpr ) ) ) | ( ( ( (lv_kinds_6_1= 'O' | lv_kinds_6_2= 'ONCE' ) ) ) ( (lv_tue_7_0= ruleTemporalUnaryExpr ) ) ) ) ) )
            {
            // InternalSpectra.g:3910:2: (this_TemporalPrimaryExpr_0= ruleTemporalPrimaryExpr | ( () ( ( ( ( (lv_kinds_2_1= 'Y' | lv_kinds_2_2= 'PREV' ) ) ) ( (lv_tue_3_0= ruleTemporalUnaryExpr ) ) ) | ( ( ( (lv_kinds_4_1= 'H' | lv_kinds_4_2= 'HISTORICALLY' ) ) ) ( (lv_tue_5_0= ruleTemporalUnaryExpr ) ) ) | ( ( ( (lv_kinds_6_1= 'O' | lv_kinds_6_2= 'ONCE' ) ) ) ( (lv_tue_7_0= ruleTemporalUnaryExpr ) ) ) ) ) )
            int alt82=2;
            int LA82_0 = input.LA(1);

            if ( (LA82_0==RULE_ID||LA82_0==RULE_INT||LA82_0==16||LA82_0==26||LA82_0==28||(LA82_0>=88 && LA82_0<=89)||(LA82_0>=97 && LA82_0<=100)) ) {
                alt82=1;
            }
            else if ( ((LA82_0>=82 && LA82_0<=87)) ) {
                alt82=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 82, 0, input);

                throw nvae;
            }
            switch (alt82) {
                case 1 :
                    // InternalSpectra.g:3911:3: this_TemporalPrimaryExpr_0= ruleTemporalPrimaryExpr
                    {

                    			newCompositeNode(grammarAccess.getTemporalUnaryExprAccess().getTemporalPrimaryExprParserRuleCall_0());
                    		
                    pushFollow(FOLLOW_2);
                    this_TemporalPrimaryExpr_0=ruleTemporalPrimaryExpr();

                    state._fsp--;


                    			current = this_TemporalPrimaryExpr_0;
                    			afterParserOrEnumRuleCall();
                    		

                    }
                    break;
                case 2 :
                    // InternalSpectra.g:3920:3: ( () ( ( ( ( (lv_kinds_2_1= 'Y' | lv_kinds_2_2= 'PREV' ) ) ) ( (lv_tue_3_0= ruleTemporalUnaryExpr ) ) ) | ( ( ( (lv_kinds_4_1= 'H' | lv_kinds_4_2= 'HISTORICALLY' ) ) ) ( (lv_tue_5_0= ruleTemporalUnaryExpr ) ) ) | ( ( ( (lv_kinds_6_1= 'O' | lv_kinds_6_2= 'ONCE' ) ) ) ( (lv_tue_7_0= ruleTemporalUnaryExpr ) ) ) ) )
                    {
                    // InternalSpectra.g:3920:3: ( () ( ( ( ( (lv_kinds_2_1= 'Y' | lv_kinds_2_2= 'PREV' ) ) ) ( (lv_tue_3_0= ruleTemporalUnaryExpr ) ) ) | ( ( ( (lv_kinds_4_1= 'H' | lv_kinds_4_2= 'HISTORICALLY' ) ) ) ( (lv_tue_5_0= ruleTemporalUnaryExpr ) ) ) | ( ( ( (lv_kinds_6_1= 'O' | lv_kinds_6_2= 'ONCE' ) ) ) ( (lv_tue_7_0= ruleTemporalUnaryExpr ) ) ) ) )
                    // InternalSpectra.g:3921:4: () ( ( ( ( (lv_kinds_2_1= 'Y' | lv_kinds_2_2= 'PREV' ) ) ) ( (lv_tue_3_0= ruleTemporalUnaryExpr ) ) ) | ( ( ( (lv_kinds_4_1= 'H' | lv_kinds_4_2= 'HISTORICALLY' ) ) ) ( (lv_tue_5_0= ruleTemporalUnaryExpr ) ) ) | ( ( ( (lv_kinds_6_1= 'O' | lv_kinds_6_2= 'ONCE' ) ) ) ( (lv_tue_7_0= ruleTemporalUnaryExpr ) ) ) )
                    {
                    // InternalSpectra.g:3921:4: ()
                    // InternalSpectra.g:3922:5: 
                    {

                    					current = forceCreateModelElement(
                    						grammarAccess.getTemporalUnaryExprAccess().getTemporalUnaryExprAction_1_0(),
                    						current);
                    				

                    }

                    // InternalSpectra.g:3928:4: ( ( ( ( (lv_kinds_2_1= 'Y' | lv_kinds_2_2= 'PREV' ) ) ) ( (lv_tue_3_0= ruleTemporalUnaryExpr ) ) ) | ( ( ( (lv_kinds_4_1= 'H' | lv_kinds_4_2= 'HISTORICALLY' ) ) ) ( (lv_tue_5_0= ruleTemporalUnaryExpr ) ) ) | ( ( ( (lv_kinds_6_1= 'O' | lv_kinds_6_2= 'ONCE' ) ) ) ( (lv_tue_7_0= ruleTemporalUnaryExpr ) ) ) )
                    int alt81=3;
                    switch ( input.LA(1) ) {
                    case 82:
                    case 83:
                        {
                        alt81=1;
                        }
                        break;
                    case 84:
                    case 85:
                        {
                        alt81=2;
                        }
                        break;
                    case 86:
                    case 87:
                        {
                        alt81=3;
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("", 81, 0, input);

                        throw nvae;
                    }

                    switch (alt81) {
                        case 1 :
                            // InternalSpectra.g:3929:5: ( ( ( (lv_kinds_2_1= 'Y' | lv_kinds_2_2= 'PREV' ) ) ) ( (lv_tue_3_0= ruleTemporalUnaryExpr ) ) )
                            {
                            // InternalSpectra.g:3929:5: ( ( ( (lv_kinds_2_1= 'Y' | lv_kinds_2_2= 'PREV' ) ) ) ( (lv_tue_3_0= ruleTemporalUnaryExpr ) ) )
                            // InternalSpectra.g:3930:6: ( ( (lv_kinds_2_1= 'Y' | lv_kinds_2_2= 'PREV' ) ) ) ( (lv_tue_3_0= ruleTemporalUnaryExpr ) )
                            {
                            // InternalSpectra.g:3930:6: ( ( (lv_kinds_2_1= 'Y' | lv_kinds_2_2= 'PREV' ) ) )
                            // InternalSpectra.g:3931:7: ( (lv_kinds_2_1= 'Y' | lv_kinds_2_2= 'PREV' ) )
                            {
                            // InternalSpectra.g:3931:7: ( (lv_kinds_2_1= 'Y' | lv_kinds_2_2= 'PREV' ) )
                            // InternalSpectra.g:3932:8: (lv_kinds_2_1= 'Y' | lv_kinds_2_2= 'PREV' )
                            {
                            // InternalSpectra.g:3932:8: (lv_kinds_2_1= 'Y' | lv_kinds_2_2= 'PREV' )
                            int alt78=2;
                            int LA78_0 = input.LA(1);

                            if ( (LA78_0==82) ) {
                                alt78=1;
                            }
                            else if ( (LA78_0==83) ) {
                                alt78=2;
                            }
                            else {
                                NoViableAltException nvae =
                                    new NoViableAltException("", 78, 0, input);

                                throw nvae;
                            }
                            switch (alt78) {
                                case 1 :
                                    // InternalSpectra.g:3933:9: lv_kinds_2_1= 'Y'
                                    {
                                    lv_kinds_2_1=(Token)match(input,82,FOLLOW_12); 

                                    									newLeafNode(lv_kinds_2_1, grammarAccess.getTemporalUnaryExprAccess().getKindsYKeyword_1_1_0_0_0_0());
                                    								

                                    									if (current==null) {
                                    										current = createModelElement(grammarAccess.getTemporalUnaryExprRule());
                                    									}
                                    									setWithLastConsumed(current, "kinds", lv_kinds_2_1, null);
                                    								

                                    }
                                    break;
                                case 2 :
                                    // InternalSpectra.g:3944:9: lv_kinds_2_2= 'PREV'
                                    {
                                    lv_kinds_2_2=(Token)match(input,83,FOLLOW_12); 

                                    									newLeafNode(lv_kinds_2_2, grammarAccess.getTemporalUnaryExprAccess().getKindsPREVKeyword_1_1_0_0_0_1());
                                    								

                                    									if (current==null) {
                                    										current = createModelElement(grammarAccess.getTemporalUnaryExprRule());
                                    									}
                                    									setWithLastConsumed(current, "kinds", lv_kinds_2_2, null);
                                    								

                                    }
                                    break;

                            }


                            }


                            }

                            // InternalSpectra.g:3957:6: ( (lv_tue_3_0= ruleTemporalUnaryExpr ) )
                            // InternalSpectra.g:3958:7: (lv_tue_3_0= ruleTemporalUnaryExpr )
                            {
                            // InternalSpectra.g:3958:7: (lv_tue_3_0= ruleTemporalUnaryExpr )
                            // InternalSpectra.g:3959:8: lv_tue_3_0= ruleTemporalUnaryExpr
                            {

                            								newCompositeNode(grammarAccess.getTemporalUnaryExprAccess().getTueTemporalUnaryExprParserRuleCall_1_1_0_1_0());
                            							
                            pushFollow(FOLLOW_2);
                            lv_tue_3_0=ruleTemporalUnaryExpr();

                            state._fsp--;


                            								if (current==null) {
                            									current = createModelElementForParent(grammarAccess.getTemporalUnaryExprRule());
                            								}
                            								set(
                            									current,
                            									"tue",
                            									lv_tue_3_0,
                            									"tau.smlab.syntech.Spectra.TemporalUnaryExpr");
                            								afterParserOrEnumRuleCall();
                            							

                            }


                            }


                            }


                            }
                            break;
                        case 2 :
                            // InternalSpectra.g:3978:5: ( ( ( (lv_kinds_4_1= 'H' | lv_kinds_4_2= 'HISTORICALLY' ) ) ) ( (lv_tue_5_0= ruleTemporalUnaryExpr ) ) )
                            {
                            // InternalSpectra.g:3978:5: ( ( ( (lv_kinds_4_1= 'H' | lv_kinds_4_2= 'HISTORICALLY' ) ) ) ( (lv_tue_5_0= ruleTemporalUnaryExpr ) ) )
                            // InternalSpectra.g:3979:6: ( ( (lv_kinds_4_1= 'H' | lv_kinds_4_2= 'HISTORICALLY' ) ) ) ( (lv_tue_5_0= ruleTemporalUnaryExpr ) )
                            {
                            // InternalSpectra.g:3979:6: ( ( (lv_kinds_4_1= 'H' | lv_kinds_4_2= 'HISTORICALLY' ) ) )
                            // InternalSpectra.g:3980:7: ( (lv_kinds_4_1= 'H' | lv_kinds_4_2= 'HISTORICALLY' ) )
                            {
                            // InternalSpectra.g:3980:7: ( (lv_kinds_4_1= 'H' | lv_kinds_4_2= 'HISTORICALLY' ) )
                            // InternalSpectra.g:3981:8: (lv_kinds_4_1= 'H' | lv_kinds_4_2= 'HISTORICALLY' )
                            {
                            // InternalSpectra.g:3981:8: (lv_kinds_4_1= 'H' | lv_kinds_4_2= 'HISTORICALLY' )
                            int alt79=2;
                            int LA79_0 = input.LA(1);

                            if ( (LA79_0==84) ) {
                                alt79=1;
                            }
                            else if ( (LA79_0==85) ) {
                                alt79=2;
                            }
                            else {
                                NoViableAltException nvae =
                                    new NoViableAltException("", 79, 0, input);

                                throw nvae;
                            }
                            switch (alt79) {
                                case 1 :
                                    // InternalSpectra.g:3982:9: lv_kinds_4_1= 'H'
                                    {
                                    lv_kinds_4_1=(Token)match(input,84,FOLLOW_12); 

                                    									newLeafNode(lv_kinds_4_1, grammarAccess.getTemporalUnaryExprAccess().getKindsHKeyword_1_1_1_0_0_0());
                                    								

                                    									if (current==null) {
                                    										current = createModelElement(grammarAccess.getTemporalUnaryExprRule());
                                    									}
                                    									setWithLastConsumed(current, "kinds", lv_kinds_4_1, null);
                                    								

                                    }
                                    break;
                                case 2 :
                                    // InternalSpectra.g:3993:9: lv_kinds_4_2= 'HISTORICALLY'
                                    {
                                    lv_kinds_4_2=(Token)match(input,85,FOLLOW_12); 

                                    									newLeafNode(lv_kinds_4_2, grammarAccess.getTemporalUnaryExprAccess().getKindsHISTORICALLYKeyword_1_1_1_0_0_1());
                                    								

                                    									if (current==null) {
                                    										current = createModelElement(grammarAccess.getTemporalUnaryExprRule());
                                    									}
                                    									setWithLastConsumed(current, "kinds", lv_kinds_4_2, null);
                                    								

                                    }
                                    break;

                            }


                            }


                            }

                            // InternalSpectra.g:4006:6: ( (lv_tue_5_0= ruleTemporalUnaryExpr ) )
                            // InternalSpectra.g:4007:7: (lv_tue_5_0= ruleTemporalUnaryExpr )
                            {
                            // InternalSpectra.g:4007:7: (lv_tue_5_0= ruleTemporalUnaryExpr )
                            // InternalSpectra.g:4008:8: lv_tue_5_0= ruleTemporalUnaryExpr
                            {

                            								newCompositeNode(grammarAccess.getTemporalUnaryExprAccess().getTueTemporalUnaryExprParserRuleCall_1_1_1_1_0());
                            							
                            pushFollow(FOLLOW_2);
                            lv_tue_5_0=ruleTemporalUnaryExpr();

                            state._fsp--;


                            								if (current==null) {
                            									current = createModelElementForParent(grammarAccess.getTemporalUnaryExprRule());
                            								}
                            								set(
                            									current,
                            									"tue",
                            									lv_tue_5_0,
                            									"tau.smlab.syntech.Spectra.TemporalUnaryExpr");
                            								afterParserOrEnumRuleCall();
                            							

                            }


                            }


                            }


                            }
                            break;
                        case 3 :
                            // InternalSpectra.g:4027:5: ( ( ( (lv_kinds_6_1= 'O' | lv_kinds_6_2= 'ONCE' ) ) ) ( (lv_tue_7_0= ruleTemporalUnaryExpr ) ) )
                            {
                            // InternalSpectra.g:4027:5: ( ( ( (lv_kinds_6_1= 'O' | lv_kinds_6_2= 'ONCE' ) ) ) ( (lv_tue_7_0= ruleTemporalUnaryExpr ) ) )
                            // InternalSpectra.g:4028:6: ( ( (lv_kinds_6_1= 'O' | lv_kinds_6_2= 'ONCE' ) ) ) ( (lv_tue_7_0= ruleTemporalUnaryExpr ) )
                            {
                            // InternalSpectra.g:4028:6: ( ( (lv_kinds_6_1= 'O' | lv_kinds_6_2= 'ONCE' ) ) )
                            // InternalSpectra.g:4029:7: ( (lv_kinds_6_1= 'O' | lv_kinds_6_2= 'ONCE' ) )
                            {
                            // InternalSpectra.g:4029:7: ( (lv_kinds_6_1= 'O' | lv_kinds_6_2= 'ONCE' ) )
                            // InternalSpectra.g:4030:8: (lv_kinds_6_1= 'O' | lv_kinds_6_2= 'ONCE' )
                            {
                            // InternalSpectra.g:4030:8: (lv_kinds_6_1= 'O' | lv_kinds_6_2= 'ONCE' )
                            int alt80=2;
                            int LA80_0 = input.LA(1);

                            if ( (LA80_0==86) ) {
                                alt80=1;
                            }
                            else if ( (LA80_0==87) ) {
                                alt80=2;
                            }
                            else {
                                NoViableAltException nvae =
                                    new NoViableAltException("", 80, 0, input);

                                throw nvae;
                            }
                            switch (alt80) {
                                case 1 :
                                    // InternalSpectra.g:4031:9: lv_kinds_6_1= 'O'
                                    {
                                    lv_kinds_6_1=(Token)match(input,86,FOLLOW_12); 

                                    									newLeafNode(lv_kinds_6_1, grammarAccess.getTemporalUnaryExprAccess().getKindsOKeyword_1_1_2_0_0_0());
                                    								

                                    									if (current==null) {
                                    										current = createModelElement(grammarAccess.getTemporalUnaryExprRule());
                                    									}
                                    									setWithLastConsumed(current, "kinds", lv_kinds_6_1, null);
                                    								

                                    }
                                    break;
                                case 2 :
                                    // InternalSpectra.g:4042:9: lv_kinds_6_2= 'ONCE'
                                    {
                                    lv_kinds_6_2=(Token)match(input,87,FOLLOW_12); 

                                    									newLeafNode(lv_kinds_6_2, grammarAccess.getTemporalUnaryExprAccess().getKindsONCEKeyword_1_1_2_0_0_1());
                                    								

                                    									if (current==null) {
                                    										current = createModelElement(grammarAccess.getTemporalUnaryExprRule());
                                    									}
                                    									setWithLastConsumed(current, "kinds", lv_kinds_6_2, null);
                                    								

                                    }
                                    break;

                            }


                            }


                            }

                            // InternalSpectra.g:4055:6: ( (lv_tue_7_0= ruleTemporalUnaryExpr ) )
                            // InternalSpectra.g:4056:7: (lv_tue_7_0= ruleTemporalUnaryExpr )
                            {
                            // InternalSpectra.g:4056:7: (lv_tue_7_0= ruleTemporalUnaryExpr )
                            // InternalSpectra.g:4057:8: lv_tue_7_0= ruleTemporalUnaryExpr
                            {

                            								newCompositeNode(grammarAccess.getTemporalUnaryExprAccess().getTueTemporalUnaryExprParserRuleCall_1_1_2_1_0());
                            							
                            pushFollow(FOLLOW_2);
                            lv_tue_7_0=ruleTemporalUnaryExpr();

                            state._fsp--;


                            								if (current==null) {
                            									current = createModelElementForParent(grammarAccess.getTemporalUnaryExprRule());
                            								}
                            								set(
                            									current,
                            									"tue",
                            									lv_tue_7_0,
                            									"tau.smlab.syntech.Spectra.TemporalUnaryExpr");
                            								afterParserOrEnumRuleCall();
                            							

                            }


                            }


                            }


                            }
                            break;

                    }


                    }


                    }
                    break;

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleTemporalUnaryExpr"


    // $ANTLR start "entryRuleTemporalPrimaryExpr"
    // InternalSpectra.g:4081:1: entryRuleTemporalPrimaryExpr returns [EObject current=null] : iv_ruleTemporalPrimaryExpr= ruleTemporalPrimaryExpr EOF ;
    public final EObject entryRuleTemporalPrimaryExpr() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleTemporalPrimaryExpr = null;


        try {
            // InternalSpectra.g:4081:60: (iv_ruleTemporalPrimaryExpr= ruleTemporalPrimaryExpr EOF )
            // InternalSpectra.g:4082:2: iv_ruleTemporalPrimaryExpr= ruleTemporalPrimaryExpr EOF
            {
             newCompositeNode(grammarAccess.getTemporalPrimaryExprRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleTemporalPrimaryExpr=ruleTemporalPrimaryExpr();

            state._fsp--;

             current =iv_ruleTemporalPrimaryExpr; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleTemporalPrimaryExpr"


    // $ANTLR start "ruleTemporalPrimaryExpr"
    // InternalSpectra.g:4088:1: ruleTemporalPrimaryExpr returns [EObject current=null] : (this_Constant_0= ruleConstant | (otherlv_1= '(' this_QuantifierExpr_2= ruleQuantifierExpr otherlv_3= ')' ) | ( () ( ( ( (otherlv_5= RULE_ID ) ) ( (otherlv_6= '(' ( (lv_predPattParams_7_0= ruleTemporalInExpr ) ) (otherlv_8= ',' ( (lv_predPattParams_9_0= ruleTemporalInExpr ) ) )* otherlv_10= ')' ) | otherlv_11= '()' ) ) | ( ( ( (lv_operator_12_1= '-' | lv_operator_12_2= '!' ) ) ) ( (lv_tpe_13_0= ruleTemporalPrimaryExpr ) ) ) | ( ( (otherlv_14= RULE_ID ) ) (otherlv_15= '[' ( (lv_index_16_0= ruleTemporalInExpr ) ) otherlv_17= ']' )* ) | ( ( (lv_operator_18_0= 'next' ) ) otherlv_19= '(' ( (lv_temporalExpression_20_0= ruleTemporalInExpr ) ) otherlv_21= ')' ) | ( ( (lv_operator_22_0= 'regexp' ) ) otherlv_23= '(' ( ( (lv_regexp_24_0= ruleRegExp ) ) | ( (otherlv_25= RULE_ID ) ) ) otherlv_26= ')' ) | ( ( (otherlv_27= RULE_ID ) ) ( (lv_operator_28_0= '.all' ) ) ) | ( ( (otherlv_29= RULE_ID ) ) ( (lv_operator_30_0= '.any' ) ) ) | ( ( (otherlv_31= RULE_ID ) ) ( (lv_operator_32_0= '.prod' ) ) ) | ( ( (otherlv_33= RULE_ID ) ) ( (lv_operator_34_0= '.sum' ) ) ) | ( ( (otherlv_35= RULE_ID ) ) ( (lv_operator_36_0= '.min' ) ) ) | ( ( (otherlv_37= RULE_ID ) ) ( (lv_operator_38_0= '.max' ) ) ) ) ) ) ;
    public final EObject ruleTemporalPrimaryExpr() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_3=null;
        Token otherlv_5=null;
        Token otherlv_6=null;
        Token otherlv_8=null;
        Token otherlv_10=null;
        Token otherlv_11=null;
        Token lv_operator_12_1=null;
        Token lv_operator_12_2=null;
        Token otherlv_14=null;
        Token otherlv_15=null;
        Token otherlv_17=null;
        Token lv_operator_18_0=null;
        Token otherlv_19=null;
        Token otherlv_21=null;
        Token lv_operator_22_0=null;
        Token otherlv_23=null;
        Token otherlv_25=null;
        Token otherlv_26=null;
        Token otherlv_27=null;
        Token lv_operator_28_0=null;
        Token otherlv_29=null;
        Token lv_operator_30_0=null;
        Token otherlv_31=null;
        Token lv_operator_32_0=null;
        Token otherlv_33=null;
        Token lv_operator_34_0=null;
        Token otherlv_35=null;
        Token lv_operator_36_0=null;
        Token otherlv_37=null;
        Token lv_operator_38_0=null;
        EObject this_Constant_0 = null;

        EObject this_QuantifierExpr_2 = null;

        EObject lv_predPattParams_7_0 = null;

        EObject lv_predPattParams_9_0 = null;

        EObject lv_tpe_13_0 = null;

        EObject lv_index_16_0 = null;

        EObject lv_temporalExpression_20_0 = null;

        EObject lv_regexp_24_0 = null;



        	enterRule();

        try {
            // InternalSpectra.g:4094:2: ( (this_Constant_0= ruleConstant | (otherlv_1= '(' this_QuantifierExpr_2= ruleQuantifierExpr otherlv_3= ')' ) | ( () ( ( ( (otherlv_5= RULE_ID ) ) ( (otherlv_6= '(' ( (lv_predPattParams_7_0= ruleTemporalInExpr ) ) (otherlv_8= ',' ( (lv_predPattParams_9_0= ruleTemporalInExpr ) ) )* otherlv_10= ')' ) | otherlv_11= '()' ) ) | ( ( ( (lv_operator_12_1= '-' | lv_operator_12_2= '!' ) ) ) ( (lv_tpe_13_0= ruleTemporalPrimaryExpr ) ) ) | ( ( (otherlv_14= RULE_ID ) ) (otherlv_15= '[' ( (lv_index_16_0= ruleTemporalInExpr ) ) otherlv_17= ']' )* ) | ( ( (lv_operator_18_0= 'next' ) ) otherlv_19= '(' ( (lv_temporalExpression_20_0= ruleTemporalInExpr ) ) otherlv_21= ')' ) | ( ( (lv_operator_22_0= 'regexp' ) ) otherlv_23= '(' ( ( (lv_regexp_24_0= ruleRegExp ) ) | ( (otherlv_25= RULE_ID ) ) ) otherlv_26= ')' ) | ( ( (otherlv_27= RULE_ID ) ) ( (lv_operator_28_0= '.all' ) ) ) | ( ( (otherlv_29= RULE_ID ) ) ( (lv_operator_30_0= '.any' ) ) ) | ( ( (otherlv_31= RULE_ID ) ) ( (lv_operator_32_0= '.prod' ) ) ) | ( ( (otherlv_33= RULE_ID ) ) ( (lv_operator_34_0= '.sum' ) ) ) | ( ( (otherlv_35= RULE_ID ) ) ( (lv_operator_36_0= '.min' ) ) ) | ( ( (otherlv_37= RULE_ID ) ) ( (lv_operator_38_0= '.max' ) ) ) ) ) ) )
            // InternalSpectra.g:4095:2: (this_Constant_0= ruleConstant | (otherlv_1= '(' this_QuantifierExpr_2= ruleQuantifierExpr otherlv_3= ')' ) | ( () ( ( ( (otherlv_5= RULE_ID ) ) ( (otherlv_6= '(' ( (lv_predPattParams_7_0= ruleTemporalInExpr ) ) (otherlv_8= ',' ( (lv_predPattParams_9_0= ruleTemporalInExpr ) ) )* otherlv_10= ')' ) | otherlv_11= '()' ) ) | ( ( ( (lv_operator_12_1= '-' | lv_operator_12_2= '!' ) ) ) ( (lv_tpe_13_0= ruleTemporalPrimaryExpr ) ) ) | ( ( (otherlv_14= RULE_ID ) ) (otherlv_15= '[' ( (lv_index_16_0= ruleTemporalInExpr ) ) otherlv_17= ']' )* ) | ( ( (lv_operator_18_0= 'next' ) ) otherlv_19= '(' ( (lv_temporalExpression_20_0= ruleTemporalInExpr ) ) otherlv_21= ')' ) | ( ( (lv_operator_22_0= 'regexp' ) ) otherlv_23= '(' ( ( (lv_regexp_24_0= ruleRegExp ) ) | ( (otherlv_25= RULE_ID ) ) ) otherlv_26= ')' ) | ( ( (otherlv_27= RULE_ID ) ) ( (lv_operator_28_0= '.all' ) ) ) | ( ( (otherlv_29= RULE_ID ) ) ( (lv_operator_30_0= '.any' ) ) ) | ( ( (otherlv_31= RULE_ID ) ) ( (lv_operator_32_0= '.prod' ) ) ) | ( ( (otherlv_33= RULE_ID ) ) ( (lv_operator_34_0= '.sum' ) ) ) | ( ( (otherlv_35= RULE_ID ) ) ( (lv_operator_36_0= '.min' ) ) ) | ( ( (otherlv_37= RULE_ID ) ) ( (lv_operator_38_0= '.max' ) ) ) ) ) )
            {
            // InternalSpectra.g:4095:2: (this_Constant_0= ruleConstant | (otherlv_1= '(' this_QuantifierExpr_2= ruleQuantifierExpr otherlv_3= ')' ) | ( () ( ( ( (otherlv_5= RULE_ID ) ) ( (otherlv_6= '(' ( (lv_predPattParams_7_0= ruleTemporalInExpr ) ) (otherlv_8= ',' ( (lv_predPattParams_9_0= ruleTemporalInExpr ) ) )* otherlv_10= ')' ) | otherlv_11= '()' ) ) | ( ( ( (lv_operator_12_1= '-' | lv_operator_12_2= '!' ) ) ) ( (lv_tpe_13_0= ruleTemporalPrimaryExpr ) ) ) | ( ( (otherlv_14= RULE_ID ) ) (otherlv_15= '[' ( (lv_index_16_0= ruleTemporalInExpr ) ) otherlv_17= ']' )* ) | ( ( (lv_operator_18_0= 'next' ) ) otherlv_19= '(' ( (lv_temporalExpression_20_0= ruleTemporalInExpr ) ) otherlv_21= ')' ) | ( ( (lv_operator_22_0= 'regexp' ) ) otherlv_23= '(' ( ( (lv_regexp_24_0= ruleRegExp ) ) | ( (otherlv_25= RULE_ID ) ) ) otherlv_26= ')' ) | ( ( (otherlv_27= RULE_ID ) ) ( (lv_operator_28_0= '.all' ) ) ) | ( ( (otherlv_29= RULE_ID ) ) ( (lv_operator_30_0= '.any' ) ) ) | ( ( (otherlv_31= RULE_ID ) ) ( (lv_operator_32_0= '.prod' ) ) ) | ( ( (otherlv_33= RULE_ID ) ) ( (lv_operator_34_0= '.sum' ) ) ) | ( ( (otherlv_35= RULE_ID ) ) ( (lv_operator_36_0= '.min' ) ) ) | ( ( (otherlv_37= RULE_ID ) ) ( (lv_operator_38_0= '.max' ) ) ) ) ) )
            int alt89=3;
            switch ( input.LA(1) ) {
            case RULE_INT:
            case 97:
            case 98:
            case 99:
            case 100:
                {
                alt89=1;
                }
                break;
            case 28:
                {
                alt89=2;
                }
                break;
            case RULE_ID:
            case 16:
            case 26:
            case 88:
            case 89:
                {
                alt89=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 89, 0, input);

                throw nvae;
            }

            switch (alt89) {
                case 1 :
                    // InternalSpectra.g:4096:3: this_Constant_0= ruleConstant
                    {

                    			newCompositeNode(grammarAccess.getTemporalPrimaryExprAccess().getConstantParserRuleCall_0());
                    		
                    pushFollow(FOLLOW_2);
                    this_Constant_0=ruleConstant();

                    state._fsp--;


                    			current = this_Constant_0;
                    			afterParserOrEnumRuleCall();
                    		

                    }
                    break;
                case 2 :
                    // InternalSpectra.g:4105:3: (otherlv_1= '(' this_QuantifierExpr_2= ruleQuantifierExpr otherlv_3= ')' )
                    {
                    // InternalSpectra.g:4105:3: (otherlv_1= '(' this_QuantifierExpr_2= ruleQuantifierExpr otherlv_3= ')' )
                    // InternalSpectra.g:4106:4: otherlv_1= '(' this_QuantifierExpr_2= ruleQuantifierExpr otherlv_3= ')'
                    {
                    otherlv_1=(Token)match(input,28,FOLLOW_18); 

                    				newLeafNode(otherlv_1, grammarAccess.getTemporalPrimaryExprAccess().getLeftParenthesisKeyword_1_0());
                    			

                    				newCompositeNode(grammarAccess.getTemporalPrimaryExprAccess().getQuantifierExprParserRuleCall_1_1());
                    			
                    pushFollow(FOLLOW_28);
                    this_QuantifierExpr_2=ruleQuantifierExpr();

                    state._fsp--;


                    				current = this_QuantifierExpr_2;
                    				afterParserOrEnumRuleCall();
                    			
                    otherlv_3=(Token)match(input,29,FOLLOW_2); 

                    				newLeafNode(otherlv_3, grammarAccess.getTemporalPrimaryExprAccess().getRightParenthesisKeyword_1_2());
                    			

                    }


                    }
                    break;
                case 3 :
                    // InternalSpectra.g:4124:3: ( () ( ( ( (otherlv_5= RULE_ID ) ) ( (otherlv_6= '(' ( (lv_predPattParams_7_0= ruleTemporalInExpr ) ) (otherlv_8= ',' ( (lv_predPattParams_9_0= ruleTemporalInExpr ) ) )* otherlv_10= ')' ) | otherlv_11= '()' ) ) | ( ( ( (lv_operator_12_1= '-' | lv_operator_12_2= '!' ) ) ) ( (lv_tpe_13_0= ruleTemporalPrimaryExpr ) ) ) | ( ( (otherlv_14= RULE_ID ) ) (otherlv_15= '[' ( (lv_index_16_0= ruleTemporalInExpr ) ) otherlv_17= ']' )* ) | ( ( (lv_operator_18_0= 'next' ) ) otherlv_19= '(' ( (lv_temporalExpression_20_0= ruleTemporalInExpr ) ) otherlv_21= ')' ) | ( ( (lv_operator_22_0= 'regexp' ) ) otherlv_23= '(' ( ( (lv_regexp_24_0= ruleRegExp ) ) | ( (otherlv_25= RULE_ID ) ) ) otherlv_26= ')' ) | ( ( (otherlv_27= RULE_ID ) ) ( (lv_operator_28_0= '.all' ) ) ) | ( ( (otherlv_29= RULE_ID ) ) ( (lv_operator_30_0= '.any' ) ) ) | ( ( (otherlv_31= RULE_ID ) ) ( (lv_operator_32_0= '.prod' ) ) ) | ( ( (otherlv_33= RULE_ID ) ) ( (lv_operator_34_0= '.sum' ) ) ) | ( ( (otherlv_35= RULE_ID ) ) ( (lv_operator_36_0= '.min' ) ) ) | ( ( (otherlv_37= RULE_ID ) ) ( (lv_operator_38_0= '.max' ) ) ) ) )
                    {
                    // InternalSpectra.g:4124:3: ( () ( ( ( (otherlv_5= RULE_ID ) ) ( (otherlv_6= '(' ( (lv_predPattParams_7_0= ruleTemporalInExpr ) ) (otherlv_8= ',' ( (lv_predPattParams_9_0= ruleTemporalInExpr ) ) )* otherlv_10= ')' ) | otherlv_11= '()' ) ) | ( ( ( (lv_operator_12_1= '-' | lv_operator_12_2= '!' ) ) ) ( (lv_tpe_13_0= ruleTemporalPrimaryExpr ) ) ) | ( ( (otherlv_14= RULE_ID ) ) (otherlv_15= '[' ( (lv_index_16_0= ruleTemporalInExpr ) ) otherlv_17= ']' )* ) | ( ( (lv_operator_18_0= 'next' ) ) otherlv_19= '(' ( (lv_temporalExpression_20_0= ruleTemporalInExpr ) ) otherlv_21= ')' ) | ( ( (lv_operator_22_0= 'regexp' ) ) otherlv_23= '(' ( ( (lv_regexp_24_0= ruleRegExp ) ) | ( (otherlv_25= RULE_ID ) ) ) otherlv_26= ')' ) | ( ( (otherlv_27= RULE_ID ) ) ( (lv_operator_28_0= '.all' ) ) ) | ( ( (otherlv_29= RULE_ID ) ) ( (lv_operator_30_0= '.any' ) ) ) | ( ( (otherlv_31= RULE_ID ) ) ( (lv_operator_32_0= '.prod' ) ) ) | ( ( (otherlv_33= RULE_ID ) ) ( (lv_operator_34_0= '.sum' ) ) ) | ( ( (otherlv_35= RULE_ID ) ) ( (lv_operator_36_0= '.min' ) ) ) | ( ( (otherlv_37= RULE_ID ) ) ( (lv_operator_38_0= '.max' ) ) ) ) )
                    // InternalSpectra.g:4125:4: () ( ( ( (otherlv_5= RULE_ID ) ) ( (otherlv_6= '(' ( (lv_predPattParams_7_0= ruleTemporalInExpr ) ) (otherlv_8= ',' ( (lv_predPattParams_9_0= ruleTemporalInExpr ) ) )* otherlv_10= ')' ) | otherlv_11= '()' ) ) | ( ( ( (lv_operator_12_1= '-' | lv_operator_12_2= '!' ) ) ) ( (lv_tpe_13_0= ruleTemporalPrimaryExpr ) ) ) | ( ( (otherlv_14= RULE_ID ) ) (otherlv_15= '[' ( (lv_index_16_0= ruleTemporalInExpr ) ) otherlv_17= ']' )* ) | ( ( (lv_operator_18_0= 'next' ) ) otherlv_19= '(' ( (lv_temporalExpression_20_0= ruleTemporalInExpr ) ) otherlv_21= ')' ) | ( ( (lv_operator_22_0= 'regexp' ) ) otherlv_23= '(' ( ( (lv_regexp_24_0= ruleRegExp ) ) | ( (otherlv_25= RULE_ID ) ) ) otherlv_26= ')' ) | ( ( (otherlv_27= RULE_ID ) ) ( (lv_operator_28_0= '.all' ) ) ) | ( ( (otherlv_29= RULE_ID ) ) ( (lv_operator_30_0= '.any' ) ) ) | ( ( (otherlv_31= RULE_ID ) ) ( (lv_operator_32_0= '.prod' ) ) ) | ( ( (otherlv_33= RULE_ID ) ) ( (lv_operator_34_0= '.sum' ) ) ) | ( ( (otherlv_35= RULE_ID ) ) ( (lv_operator_36_0= '.min' ) ) ) | ( ( (otherlv_37= RULE_ID ) ) ( (lv_operator_38_0= '.max' ) ) ) )
                    {
                    // InternalSpectra.g:4125:4: ()
                    // InternalSpectra.g:4126:5: 
                    {

                    					current = forceCreateModelElement(
                    						grammarAccess.getTemporalPrimaryExprAccess().getTemporalPrimaryExprAction_2_0(),
                    						current);
                    				

                    }

                    // InternalSpectra.g:4132:4: ( ( ( (otherlv_5= RULE_ID ) ) ( (otherlv_6= '(' ( (lv_predPattParams_7_0= ruleTemporalInExpr ) ) (otherlv_8= ',' ( (lv_predPattParams_9_0= ruleTemporalInExpr ) ) )* otherlv_10= ')' ) | otherlv_11= '()' ) ) | ( ( ( (lv_operator_12_1= '-' | lv_operator_12_2= '!' ) ) ) ( (lv_tpe_13_0= ruleTemporalPrimaryExpr ) ) ) | ( ( (otherlv_14= RULE_ID ) ) (otherlv_15= '[' ( (lv_index_16_0= ruleTemporalInExpr ) ) otherlv_17= ']' )* ) | ( ( (lv_operator_18_0= 'next' ) ) otherlv_19= '(' ( (lv_temporalExpression_20_0= ruleTemporalInExpr ) ) otherlv_21= ')' ) | ( ( (lv_operator_22_0= 'regexp' ) ) otherlv_23= '(' ( ( (lv_regexp_24_0= ruleRegExp ) ) | ( (otherlv_25= RULE_ID ) ) ) otherlv_26= ')' ) | ( ( (otherlv_27= RULE_ID ) ) ( (lv_operator_28_0= '.all' ) ) ) | ( ( (otherlv_29= RULE_ID ) ) ( (lv_operator_30_0= '.any' ) ) ) | ( ( (otherlv_31= RULE_ID ) ) ( (lv_operator_32_0= '.prod' ) ) ) | ( ( (otherlv_33= RULE_ID ) ) ( (lv_operator_34_0= '.sum' ) ) ) | ( ( (otherlv_35= RULE_ID ) ) ( (lv_operator_36_0= '.min' ) ) ) | ( ( (otherlv_37= RULE_ID ) ) ( (lv_operator_38_0= '.max' ) ) ) )
                    int alt88=11;
                    alt88 = dfa88.predict(input);
                    switch (alt88) {
                        case 1 :
                            // InternalSpectra.g:4133:5: ( ( (otherlv_5= RULE_ID ) ) ( (otherlv_6= '(' ( (lv_predPattParams_7_0= ruleTemporalInExpr ) ) (otherlv_8= ',' ( (lv_predPattParams_9_0= ruleTemporalInExpr ) ) )* otherlv_10= ')' ) | otherlv_11= '()' ) )
                            {
                            // InternalSpectra.g:4133:5: ( ( (otherlv_5= RULE_ID ) ) ( (otherlv_6= '(' ( (lv_predPattParams_7_0= ruleTemporalInExpr ) ) (otherlv_8= ',' ( (lv_predPattParams_9_0= ruleTemporalInExpr ) ) )* otherlv_10= ')' ) | otherlv_11= '()' ) )
                            // InternalSpectra.g:4134:6: ( (otherlv_5= RULE_ID ) ) ( (otherlv_6= '(' ( (lv_predPattParams_7_0= ruleTemporalInExpr ) ) (otherlv_8= ',' ( (lv_predPattParams_9_0= ruleTemporalInExpr ) ) )* otherlv_10= ')' ) | otherlv_11= '()' )
                            {
                            // InternalSpectra.g:4134:6: ( (otherlv_5= RULE_ID ) )
                            // InternalSpectra.g:4135:7: (otherlv_5= RULE_ID )
                            {
                            // InternalSpectra.g:4135:7: (otherlv_5= RULE_ID )
                            // InternalSpectra.g:4136:8: otherlv_5= RULE_ID
                            {

                            								if (current==null) {
                            									current = createModelElement(grammarAccess.getTemporalPrimaryExprRule());
                            								}
                            							
                            otherlv_5=(Token)match(input,RULE_ID,FOLLOW_35); 

                            								newLeafNode(otherlv_5, grammarAccess.getTemporalPrimaryExprAccess().getPredPattPredicateOrPatternReferrableCrossReference_2_1_0_0_0());
                            							

                            }


                            }

                            // InternalSpectra.g:4147:6: ( (otherlv_6= '(' ( (lv_predPattParams_7_0= ruleTemporalInExpr ) ) (otherlv_8= ',' ( (lv_predPattParams_9_0= ruleTemporalInExpr ) ) )* otherlv_10= ')' ) | otherlv_11= '()' )
                            int alt84=2;
                            int LA84_0 = input.LA(1);

                            if ( (LA84_0==28) ) {
                                alt84=1;
                            }
                            else if ( (LA84_0==48) ) {
                                alt84=2;
                            }
                            else {
                                NoViableAltException nvae =
                                    new NoViableAltException("", 84, 0, input);

                                throw nvae;
                            }
                            switch (alt84) {
                                case 1 :
                                    // InternalSpectra.g:4148:7: (otherlv_6= '(' ( (lv_predPattParams_7_0= ruleTemporalInExpr ) ) (otherlv_8= ',' ( (lv_predPattParams_9_0= ruleTemporalInExpr ) ) )* otherlv_10= ')' )
                                    {
                                    // InternalSpectra.g:4148:7: (otherlv_6= '(' ( (lv_predPattParams_7_0= ruleTemporalInExpr ) ) (otherlv_8= ',' ( (lv_predPattParams_9_0= ruleTemporalInExpr ) ) )* otherlv_10= ')' )
                                    // InternalSpectra.g:4149:8: otherlv_6= '(' ( (lv_predPattParams_7_0= ruleTemporalInExpr ) ) (otherlv_8= ',' ( (lv_predPattParams_9_0= ruleTemporalInExpr ) ) )* otherlv_10= ')'
                                    {
                                    otherlv_6=(Token)match(input,28,FOLLOW_12); 

                                    								newLeafNode(otherlv_6, grammarAccess.getTemporalPrimaryExprAccess().getLeftParenthesisKeyword_2_1_0_1_0_0());
                                    							
                                    // InternalSpectra.g:4153:8: ( (lv_predPattParams_7_0= ruleTemporalInExpr ) )
                                    // InternalSpectra.g:4154:9: (lv_predPattParams_7_0= ruleTemporalInExpr )
                                    {
                                    // InternalSpectra.g:4154:9: (lv_predPattParams_7_0= ruleTemporalInExpr )
                                    // InternalSpectra.g:4155:10: lv_predPattParams_7_0= ruleTemporalInExpr
                                    {

                                    										newCompositeNode(grammarAccess.getTemporalPrimaryExprAccess().getPredPattParamsTemporalInExprParserRuleCall_2_1_0_1_0_1_0());
                                    									
                                    pushFollow(FOLLOW_56);
                                    lv_predPattParams_7_0=ruleTemporalInExpr();

                                    state._fsp--;


                                    										if (current==null) {
                                    											current = createModelElementForParent(grammarAccess.getTemporalPrimaryExprRule());
                                    										}
                                    										add(
                                    											current,
                                    											"predPattParams",
                                    											lv_predPattParams_7_0,
                                    											"tau.smlab.syntech.Spectra.TemporalInExpr");
                                    										afterParserOrEnumRuleCall();
                                    									

                                    }


                                    }

                                    // InternalSpectra.g:4172:8: (otherlv_8= ',' ( (lv_predPattParams_9_0= ruleTemporalInExpr ) ) )*
                                    loop83:
                                    do {
                                        int alt83=2;
                                        int LA83_0 = input.LA(1);

                                        if ( (LA83_0==24) ) {
                                            alt83=1;
                                        }


                                        switch (alt83) {
                                    	case 1 :
                                    	    // InternalSpectra.g:4173:9: otherlv_8= ',' ( (lv_predPattParams_9_0= ruleTemporalInExpr ) )
                                    	    {
                                    	    otherlv_8=(Token)match(input,24,FOLLOW_12); 

                                    	    									newLeafNode(otherlv_8, grammarAccess.getTemporalPrimaryExprAccess().getCommaKeyword_2_1_0_1_0_2_0());
                                    	    								
                                    	    // InternalSpectra.g:4177:9: ( (lv_predPattParams_9_0= ruleTemporalInExpr ) )
                                    	    // InternalSpectra.g:4178:10: (lv_predPattParams_9_0= ruleTemporalInExpr )
                                    	    {
                                    	    // InternalSpectra.g:4178:10: (lv_predPattParams_9_0= ruleTemporalInExpr )
                                    	    // InternalSpectra.g:4179:11: lv_predPattParams_9_0= ruleTemporalInExpr
                                    	    {

                                    	    											newCompositeNode(grammarAccess.getTemporalPrimaryExprAccess().getPredPattParamsTemporalInExprParserRuleCall_2_1_0_1_0_2_1_0());
                                    	    										
                                    	    pushFollow(FOLLOW_56);
                                    	    lv_predPattParams_9_0=ruleTemporalInExpr();

                                    	    state._fsp--;


                                    	    											if (current==null) {
                                    	    												current = createModelElementForParent(grammarAccess.getTemporalPrimaryExprRule());
                                    	    											}
                                    	    											add(
                                    	    												current,
                                    	    												"predPattParams",
                                    	    												lv_predPattParams_9_0,
                                    	    												"tau.smlab.syntech.Spectra.TemporalInExpr");
                                    	    											afterParserOrEnumRuleCall();
                                    	    										

                                    	    }


                                    	    }


                                    	    }
                                    	    break;

                                    	default :
                                    	    break loop83;
                                        }
                                    } while (true);

                                    otherlv_10=(Token)match(input,29,FOLLOW_2); 

                                    								newLeafNode(otherlv_10, grammarAccess.getTemporalPrimaryExprAccess().getRightParenthesisKeyword_2_1_0_1_0_3());
                                    							

                                    }


                                    }
                                    break;
                                case 2 :
                                    // InternalSpectra.g:4203:7: otherlv_11= '()'
                                    {
                                    otherlv_11=(Token)match(input,48,FOLLOW_2); 

                                    							newLeafNode(otherlv_11, grammarAccess.getTemporalPrimaryExprAccess().getLeftParenthesisRightParenthesisKeyword_2_1_0_1_1());
                                    						

                                    }
                                    break;

                            }


                            }


                            }
                            break;
                        case 2 :
                            // InternalSpectra.g:4210:5: ( ( ( (lv_operator_12_1= '-' | lv_operator_12_2= '!' ) ) ) ( (lv_tpe_13_0= ruleTemporalPrimaryExpr ) ) )
                            {
                            // InternalSpectra.g:4210:5: ( ( ( (lv_operator_12_1= '-' | lv_operator_12_2= '!' ) ) ) ( (lv_tpe_13_0= ruleTemporalPrimaryExpr ) ) )
                            // InternalSpectra.g:4211:6: ( ( (lv_operator_12_1= '-' | lv_operator_12_2= '!' ) ) ) ( (lv_tpe_13_0= ruleTemporalPrimaryExpr ) )
                            {
                            // InternalSpectra.g:4211:6: ( ( (lv_operator_12_1= '-' | lv_operator_12_2= '!' ) ) )
                            // InternalSpectra.g:4212:7: ( (lv_operator_12_1= '-' | lv_operator_12_2= '!' ) )
                            {
                            // InternalSpectra.g:4212:7: ( (lv_operator_12_1= '-' | lv_operator_12_2= '!' ) )
                            // InternalSpectra.g:4213:8: (lv_operator_12_1= '-' | lv_operator_12_2= '!' )
                            {
                            // InternalSpectra.g:4213:8: (lv_operator_12_1= '-' | lv_operator_12_2= '!' )
                            int alt85=2;
                            int LA85_0 = input.LA(1);

                            if ( (LA85_0==16) ) {
                                alt85=1;
                            }
                            else if ( (LA85_0==88) ) {
                                alt85=2;
                            }
                            else {
                                NoViableAltException nvae =
                                    new NoViableAltException("", 85, 0, input);

                                throw nvae;
                            }
                            switch (alt85) {
                                case 1 :
                                    // InternalSpectra.g:4214:9: lv_operator_12_1= '-'
                                    {
                                    lv_operator_12_1=(Token)match(input,16,FOLLOW_57); 

                                    									newLeafNode(lv_operator_12_1, grammarAccess.getTemporalPrimaryExprAccess().getOperatorHyphenMinusKeyword_2_1_1_0_0_0());
                                    								

                                    									if (current==null) {
                                    										current = createModelElement(grammarAccess.getTemporalPrimaryExprRule());
                                    									}
                                    									setWithLastConsumed(current, "operator", lv_operator_12_1, null);
                                    								

                                    }
                                    break;
                                case 2 :
                                    // InternalSpectra.g:4225:9: lv_operator_12_2= '!'
                                    {
                                    lv_operator_12_2=(Token)match(input,88,FOLLOW_57); 

                                    									newLeafNode(lv_operator_12_2, grammarAccess.getTemporalPrimaryExprAccess().getOperatorExclamationMarkKeyword_2_1_1_0_0_1());
                                    								

                                    									if (current==null) {
                                    										current = createModelElement(grammarAccess.getTemporalPrimaryExprRule());
                                    									}
                                    									setWithLastConsumed(current, "operator", lv_operator_12_2, null);
                                    								

                                    }
                                    break;

                            }


                            }


                            }

                            // InternalSpectra.g:4238:6: ( (lv_tpe_13_0= ruleTemporalPrimaryExpr ) )
                            // InternalSpectra.g:4239:7: (lv_tpe_13_0= ruleTemporalPrimaryExpr )
                            {
                            // InternalSpectra.g:4239:7: (lv_tpe_13_0= ruleTemporalPrimaryExpr )
                            // InternalSpectra.g:4240:8: lv_tpe_13_0= ruleTemporalPrimaryExpr
                            {

                            								newCompositeNode(grammarAccess.getTemporalPrimaryExprAccess().getTpeTemporalPrimaryExprParserRuleCall_2_1_1_1_0());
                            							
                            pushFollow(FOLLOW_2);
                            lv_tpe_13_0=ruleTemporalPrimaryExpr();

                            state._fsp--;


                            								if (current==null) {
                            									current = createModelElementForParent(grammarAccess.getTemporalPrimaryExprRule());
                            								}
                            								set(
                            									current,
                            									"tpe",
                            									lv_tpe_13_0,
                            									"tau.smlab.syntech.Spectra.TemporalPrimaryExpr");
                            								afterParserOrEnumRuleCall();
                            							

                            }


                            }


                            }


                            }
                            break;
                        case 3 :
                            // InternalSpectra.g:4259:5: ( ( (otherlv_14= RULE_ID ) ) (otherlv_15= '[' ( (lv_index_16_0= ruleTemporalInExpr ) ) otherlv_17= ']' )* )
                            {
                            // InternalSpectra.g:4259:5: ( ( (otherlv_14= RULE_ID ) ) (otherlv_15= '[' ( (lv_index_16_0= ruleTemporalInExpr ) ) otherlv_17= ']' )* )
                            // InternalSpectra.g:4260:6: ( (otherlv_14= RULE_ID ) ) (otherlv_15= '[' ( (lv_index_16_0= ruleTemporalInExpr ) ) otherlv_17= ']' )*
                            {
                            // InternalSpectra.g:4260:6: ( (otherlv_14= RULE_ID ) )
                            // InternalSpectra.g:4261:7: (otherlv_14= RULE_ID )
                            {
                            // InternalSpectra.g:4261:7: (otherlv_14= RULE_ID )
                            // InternalSpectra.g:4262:8: otherlv_14= RULE_ID
                            {

                            								if (current==null) {
                            									current = createModelElement(grammarAccess.getTemporalPrimaryExprRule());
                            								}
                            							
                            otherlv_14=(Token)match(input,RULE_ID,FOLLOW_38); 

                            								newLeafNode(otherlv_14, grammarAccess.getTemporalPrimaryExprAccess().getPointerReferrableCrossReference_2_1_2_0_0());
                            							

                            }


                            }

                            // InternalSpectra.g:4273:6: (otherlv_15= '[' ( (lv_index_16_0= ruleTemporalInExpr ) ) otherlv_17= ']' )*
                            loop86:
                            do {
                                int alt86=2;
                                int LA86_0 = input.LA(1);

                                if ( (LA86_0==21) ) {
                                    alt86=1;
                                }


                                switch (alt86) {
                            	case 1 :
                            	    // InternalSpectra.g:4274:7: otherlv_15= '[' ( (lv_index_16_0= ruleTemporalInExpr ) ) otherlv_17= ']'
                            	    {
                            	    otherlv_15=(Token)match(input,21,FOLLOW_12); 

                            	    							newLeafNode(otherlv_15, grammarAccess.getTemporalPrimaryExprAccess().getLeftSquareBracketKeyword_2_1_2_1_0());
                            	    						
                            	    // InternalSpectra.g:4278:7: ( (lv_index_16_0= ruleTemporalInExpr ) )
                            	    // InternalSpectra.g:4279:8: (lv_index_16_0= ruleTemporalInExpr )
                            	    {
                            	    // InternalSpectra.g:4279:8: (lv_index_16_0= ruleTemporalInExpr )
                            	    // InternalSpectra.g:4280:9: lv_index_16_0= ruleTemporalInExpr
                            	    {

                            	    									newCompositeNode(grammarAccess.getTemporalPrimaryExprAccess().getIndexTemporalInExprParserRuleCall_2_1_2_1_1_0());
                            	    								
                            	    pushFollow(FOLLOW_21);
                            	    lv_index_16_0=ruleTemporalInExpr();

                            	    state._fsp--;


                            	    									if (current==null) {
                            	    										current = createModelElementForParent(grammarAccess.getTemporalPrimaryExprRule());
                            	    									}
                            	    									add(
                            	    										current,
                            	    										"index",
                            	    										lv_index_16_0,
                            	    										"tau.smlab.syntech.Spectra.TemporalInExpr");
                            	    									afterParserOrEnumRuleCall();
                            	    								

                            	    }


                            	    }

                            	    otherlv_17=(Token)match(input,22,FOLLOW_38); 

                            	    							newLeafNode(otherlv_17, grammarAccess.getTemporalPrimaryExprAccess().getRightSquareBracketKeyword_2_1_2_1_2());
                            	    						

                            	    }
                            	    break;

                            	default :
                            	    break loop86;
                                }
                            } while (true);


                            }


                            }
                            break;
                        case 4 :
                            // InternalSpectra.g:4304:5: ( ( (lv_operator_18_0= 'next' ) ) otherlv_19= '(' ( (lv_temporalExpression_20_0= ruleTemporalInExpr ) ) otherlv_21= ')' )
                            {
                            // InternalSpectra.g:4304:5: ( ( (lv_operator_18_0= 'next' ) ) otherlv_19= '(' ( (lv_temporalExpression_20_0= ruleTemporalInExpr ) ) otherlv_21= ')' )
                            // InternalSpectra.g:4305:6: ( (lv_operator_18_0= 'next' ) ) otherlv_19= '(' ( (lv_temporalExpression_20_0= ruleTemporalInExpr ) ) otherlv_21= ')'
                            {
                            // InternalSpectra.g:4305:6: ( (lv_operator_18_0= 'next' ) )
                            // InternalSpectra.g:4306:7: (lv_operator_18_0= 'next' )
                            {
                            // InternalSpectra.g:4306:7: (lv_operator_18_0= 'next' )
                            // InternalSpectra.g:4307:8: lv_operator_18_0= 'next'
                            {
                            lv_operator_18_0=(Token)match(input,89,FOLLOW_27); 

                            								newLeafNode(lv_operator_18_0, grammarAccess.getTemporalPrimaryExprAccess().getOperatorNextKeyword_2_1_3_0_0());
                            							

                            								if (current==null) {
                            									current = createModelElement(grammarAccess.getTemporalPrimaryExprRule());
                            								}
                            								setWithLastConsumed(current, "operator", lv_operator_18_0, "next");
                            							

                            }


                            }

                            otherlv_19=(Token)match(input,28,FOLLOW_12); 

                            						newLeafNode(otherlv_19, grammarAccess.getTemporalPrimaryExprAccess().getLeftParenthesisKeyword_2_1_3_1());
                            					
                            // InternalSpectra.g:4323:6: ( (lv_temporalExpression_20_0= ruleTemporalInExpr ) )
                            // InternalSpectra.g:4324:7: (lv_temporalExpression_20_0= ruleTemporalInExpr )
                            {
                            // InternalSpectra.g:4324:7: (lv_temporalExpression_20_0= ruleTemporalInExpr )
                            // InternalSpectra.g:4325:8: lv_temporalExpression_20_0= ruleTemporalInExpr
                            {

                            								newCompositeNode(grammarAccess.getTemporalPrimaryExprAccess().getTemporalExpressionTemporalInExprParserRuleCall_2_1_3_2_0());
                            							
                            pushFollow(FOLLOW_28);
                            lv_temporalExpression_20_0=ruleTemporalInExpr();

                            state._fsp--;


                            								if (current==null) {
                            									current = createModelElementForParent(grammarAccess.getTemporalPrimaryExprRule());
                            								}
                            								set(
                            									current,
                            									"temporalExpression",
                            									lv_temporalExpression_20_0,
                            									"tau.smlab.syntech.Spectra.TemporalInExpr");
                            								afterParserOrEnumRuleCall();
                            							

                            }


                            }

                            otherlv_21=(Token)match(input,29,FOLLOW_2); 

                            						newLeafNode(otherlv_21, grammarAccess.getTemporalPrimaryExprAccess().getRightParenthesisKeyword_2_1_3_3());
                            					

                            }


                            }
                            break;
                        case 5 :
                            // InternalSpectra.g:4348:5: ( ( (lv_operator_22_0= 'regexp' ) ) otherlv_23= '(' ( ( (lv_regexp_24_0= ruleRegExp ) ) | ( (otherlv_25= RULE_ID ) ) ) otherlv_26= ')' )
                            {
                            // InternalSpectra.g:4348:5: ( ( (lv_operator_22_0= 'regexp' ) ) otherlv_23= '(' ( ( (lv_regexp_24_0= ruleRegExp ) ) | ( (otherlv_25= RULE_ID ) ) ) otherlv_26= ')' )
                            // InternalSpectra.g:4349:6: ( (lv_operator_22_0= 'regexp' ) ) otherlv_23= '(' ( ( (lv_regexp_24_0= ruleRegExp ) ) | ( (otherlv_25= RULE_ID ) ) ) otherlv_26= ')'
                            {
                            // InternalSpectra.g:4349:6: ( (lv_operator_22_0= 'regexp' ) )
                            // InternalSpectra.g:4350:7: (lv_operator_22_0= 'regexp' )
                            {
                            // InternalSpectra.g:4350:7: (lv_operator_22_0= 'regexp' )
                            // InternalSpectra.g:4351:8: lv_operator_22_0= 'regexp'
                            {
                            lv_operator_22_0=(Token)match(input,26,FOLLOW_27); 

                            								newLeafNode(lv_operator_22_0, grammarAccess.getTemporalPrimaryExprAccess().getOperatorRegexpKeyword_2_1_4_0_0());
                            							

                            								if (current==null) {
                            									current = createModelElement(grammarAccess.getTemporalPrimaryExprRule());
                            								}
                            								setWithLastConsumed(current, "operator", lv_operator_22_0, "regexp");
                            							

                            }


                            }

                            otherlv_23=(Token)match(input,28,FOLLOW_58); 

                            						newLeafNode(otherlv_23, grammarAccess.getTemporalPrimaryExprAccess().getLeftParenthesisKeyword_2_1_4_1());
                            					
                            // InternalSpectra.g:4367:6: ( ( (lv_regexp_24_0= ruleRegExp ) ) | ( (otherlv_25= RULE_ID ) ) )
                            int alt87=2;
                            int LA87_0 = input.LA(1);

                            if ( (LA87_0==21||LA87_0==28||LA87_0==48||(LA87_0>=97 && LA87_0<=100)||LA87_0==104) ) {
                                alt87=1;
                            }
                            else if ( (LA87_0==RULE_ID) ) {
                                alt87=2;
                            }
                            else {
                                NoViableAltException nvae =
                                    new NoViableAltException("", 87, 0, input);

                                throw nvae;
                            }
                            switch (alt87) {
                                case 1 :
                                    // InternalSpectra.g:4368:7: ( (lv_regexp_24_0= ruleRegExp ) )
                                    {
                                    // InternalSpectra.g:4368:7: ( (lv_regexp_24_0= ruleRegExp ) )
                                    // InternalSpectra.g:4369:8: (lv_regexp_24_0= ruleRegExp )
                                    {
                                    // InternalSpectra.g:4369:8: (lv_regexp_24_0= ruleRegExp )
                                    // InternalSpectra.g:4370:9: lv_regexp_24_0= ruleRegExp
                                    {

                                    									newCompositeNode(grammarAccess.getTemporalPrimaryExprAccess().getRegexpRegExpParserRuleCall_2_1_4_2_0_0());
                                    								
                                    pushFollow(FOLLOW_28);
                                    lv_regexp_24_0=ruleRegExp();

                                    state._fsp--;


                                    									if (current==null) {
                                    										current = createModelElementForParent(grammarAccess.getTemporalPrimaryExprRule());
                                    									}
                                    									set(
                                    										current,
                                    										"regexp",
                                    										lv_regexp_24_0,
                                    										"tau.smlab.syntech.Spectra.RegExp");
                                    									afterParserOrEnumRuleCall();
                                    								

                                    }


                                    }


                                    }
                                    break;
                                case 2 :
                                    // InternalSpectra.g:4388:7: ( (otherlv_25= RULE_ID ) )
                                    {
                                    // InternalSpectra.g:4388:7: ( (otherlv_25= RULE_ID ) )
                                    // InternalSpectra.g:4389:8: (otherlv_25= RULE_ID )
                                    {
                                    // InternalSpectra.g:4389:8: (otherlv_25= RULE_ID )
                                    // InternalSpectra.g:4390:9: otherlv_25= RULE_ID
                                    {

                                    									if (current==null) {
                                    										current = createModelElement(grammarAccess.getTemporalPrimaryExprRule());
                                    									}
                                    								
                                    otherlv_25=(Token)match(input,RULE_ID,FOLLOW_28); 

                                    									newLeafNode(otherlv_25, grammarAccess.getTemporalPrimaryExprAccess().getRegexpPointerDefineRegExpDeclCrossReference_2_1_4_2_1_0());
                                    								

                                    }


                                    }


                                    }
                                    break;

                            }

                            otherlv_26=(Token)match(input,29,FOLLOW_2); 

                            						newLeafNode(otherlv_26, grammarAccess.getTemporalPrimaryExprAccess().getRightParenthesisKeyword_2_1_4_3());
                            					

                            }


                            }
                            break;
                        case 6 :
                            // InternalSpectra.g:4408:5: ( ( (otherlv_27= RULE_ID ) ) ( (lv_operator_28_0= '.all' ) ) )
                            {
                            // InternalSpectra.g:4408:5: ( ( (otherlv_27= RULE_ID ) ) ( (lv_operator_28_0= '.all' ) ) )
                            // InternalSpectra.g:4409:6: ( (otherlv_27= RULE_ID ) ) ( (lv_operator_28_0= '.all' ) )
                            {
                            // InternalSpectra.g:4409:6: ( (otherlv_27= RULE_ID ) )
                            // InternalSpectra.g:4410:7: (otherlv_27= RULE_ID )
                            {
                            // InternalSpectra.g:4410:7: (otherlv_27= RULE_ID )
                            // InternalSpectra.g:4411:8: otherlv_27= RULE_ID
                            {

                            								if (current==null) {
                            									current = createModelElement(grammarAccess.getTemporalPrimaryExprRule());
                            								}
                            							
                            otherlv_27=(Token)match(input,RULE_ID,FOLLOW_59); 

                            								newLeafNode(otherlv_27, grammarAccess.getTemporalPrimaryExprAccess().getPointerReferrableCrossReference_2_1_5_0_0());
                            							

                            }


                            }

                            // InternalSpectra.g:4422:6: ( (lv_operator_28_0= '.all' ) )
                            // InternalSpectra.g:4423:7: (lv_operator_28_0= '.all' )
                            {
                            // InternalSpectra.g:4423:7: (lv_operator_28_0= '.all' )
                            // InternalSpectra.g:4424:8: lv_operator_28_0= '.all'
                            {
                            lv_operator_28_0=(Token)match(input,90,FOLLOW_2); 

                            								newLeafNode(lv_operator_28_0, grammarAccess.getTemporalPrimaryExprAccess().getOperatorAllKeyword_2_1_5_1_0());
                            							

                            								if (current==null) {
                            									current = createModelElement(grammarAccess.getTemporalPrimaryExprRule());
                            								}
                            								setWithLastConsumed(current, "operator", lv_operator_28_0, ".all");
                            							

                            }


                            }


                            }


                            }
                            break;
                        case 7 :
                            // InternalSpectra.g:4438:5: ( ( (otherlv_29= RULE_ID ) ) ( (lv_operator_30_0= '.any' ) ) )
                            {
                            // InternalSpectra.g:4438:5: ( ( (otherlv_29= RULE_ID ) ) ( (lv_operator_30_0= '.any' ) ) )
                            // InternalSpectra.g:4439:6: ( (otherlv_29= RULE_ID ) ) ( (lv_operator_30_0= '.any' ) )
                            {
                            // InternalSpectra.g:4439:6: ( (otherlv_29= RULE_ID ) )
                            // InternalSpectra.g:4440:7: (otherlv_29= RULE_ID )
                            {
                            // InternalSpectra.g:4440:7: (otherlv_29= RULE_ID )
                            // InternalSpectra.g:4441:8: otherlv_29= RULE_ID
                            {

                            								if (current==null) {
                            									current = createModelElement(grammarAccess.getTemporalPrimaryExprRule());
                            								}
                            							
                            otherlv_29=(Token)match(input,RULE_ID,FOLLOW_60); 

                            								newLeafNode(otherlv_29, grammarAccess.getTemporalPrimaryExprAccess().getPointerReferrableCrossReference_2_1_6_0_0());
                            							

                            }


                            }

                            // InternalSpectra.g:4452:6: ( (lv_operator_30_0= '.any' ) )
                            // InternalSpectra.g:4453:7: (lv_operator_30_0= '.any' )
                            {
                            // InternalSpectra.g:4453:7: (lv_operator_30_0= '.any' )
                            // InternalSpectra.g:4454:8: lv_operator_30_0= '.any'
                            {
                            lv_operator_30_0=(Token)match(input,91,FOLLOW_2); 

                            								newLeafNode(lv_operator_30_0, grammarAccess.getTemporalPrimaryExprAccess().getOperatorAnyKeyword_2_1_6_1_0());
                            							

                            								if (current==null) {
                            									current = createModelElement(grammarAccess.getTemporalPrimaryExprRule());
                            								}
                            								setWithLastConsumed(current, "operator", lv_operator_30_0, ".any");
                            							

                            }


                            }


                            }


                            }
                            break;
                        case 8 :
                            // InternalSpectra.g:4468:5: ( ( (otherlv_31= RULE_ID ) ) ( (lv_operator_32_0= '.prod' ) ) )
                            {
                            // InternalSpectra.g:4468:5: ( ( (otherlv_31= RULE_ID ) ) ( (lv_operator_32_0= '.prod' ) ) )
                            // InternalSpectra.g:4469:6: ( (otherlv_31= RULE_ID ) ) ( (lv_operator_32_0= '.prod' ) )
                            {
                            // InternalSpectra.g:4469:6: ( (otherlv_31= RULE_ID ) )
                            // InternalSpectra.g:4470:7: (otherlv_31= RULE_ID )
                            {
                            // InternalSpectra.g:4470:7: (otherlv_31= RULE_ID )
                            // InternalSpectra.g:4471:8: otherlv_31= RULE_ID
                            {

                            								if (current==null) {
                            									current = createModelElement(grammarAccess.getTemporalPrimaryExprRule());
                            								}
                            							
                            otherlv_31=(Token)match(input,RULE_ID,FOLLOW_61); 

                            								newLeafNode(otherlv_31, grammarAccess.getTemporalPrimaryExprAccess().getPointerReferrableCrossReference_2_1_7_0_0());
                            							

                            }


                            }

                            // InternalSpectra.g:4482:6: ( (lv_operator_32_0= '.prod' ) )
                            // InternalSpectra.g:4483:7: (lv_operator_32_0= '.prod' )
                            {
                            // InternalSpectra.g:4483:7: (lv_operator_32_0= '.prod' )
                            // InternalSpectra.g:4484:8: lv_operator_32_0= '.prod'
                            {
                            lv_operator_32_0=(Token)match(input,92,FOLLOW_2); 

                            								newLeafNode(lv_operator_32_0, grammarAccess.getTemporalPrimaryExprAccess().getOperatorProdKeyword_2_1_7_1_0());
                            							

                            								if (current==null) {
                            									current = createModelElement(grammarAccess.getTemporalPrimaryExprRule());
                            								}
                            								setWithLastConsumed(current, "operator", lv_operator_32_0, ".prod");
                            							

                            }


                            }


                            }


                            }
                            break;
                        case 9 :
                            // InternalSpectra.g:4498:5: ( ( (otherlv_33= RULE_ID ) ) ( (lv_operator_34_0= '.sum' ) ) )
                            {
                            // InternalSpectra.g:4498:5: ( ( (otherlv_33= RULE_ID ) ) ( (lv_operator_34_0= '.sum' ) ) )
                            // InternalSpectra.g:4499:6: ( (otherlv_33= RULE_ID ) ) ( (lv_operator_34_0= '.sum' ) )
                            {
                            // InternalSpectra.g:4499:6: ( (otherlv_33= RULE_ID ) )
                            // InternalSpectra.g:4500:7: (otherlv_33= RULE_ID )
                            {
                            // InternalSpectra.g:4500:7: (otherlv_33= RULE_ID )
                            // InternalSpectra.g:4501:8: otherlv_33= RULE_ID
                            {

                            								if (current==null) {
                            									current = createModelElement(grammarAccess.getTemporalPrimaryExprRule());
                            								}
                            							
                            otherlv_33=(Token)match(input,RULE_ID,FOLLOW_62); 

                            								newLeafNode(otherlv_33, grammarAccess.getTemporalPrimaryExprAccess().getPointerReferrableCrossReference_2_1_8_0_0());
                            							

                            }


                            }

                            // InternalSpectra.g:4512:6: ( (lv_operator_34_0= '.sum' ) )
                            // InternalSpectra.g:4513:7: (lv_operator_34_0= '.sum' )
                            {
                            // InternalSpectra.g:4513:7: (lv_operator_34_0= '.sum' )
                            // InternalSpectra.g:4514:8: lv_operator_34_0= '.sum'
                            {
                            lv_operator_34_0=(Token)match(input,93,FOLLOW_2); 

                            								newLeafNode(lv_operator_34_0, grammarAccess.getTemporalPrimaryExprAccess().getOperatorSumKeyword_2_1_8_1_0());
                            							

                            								if (current==null) {
                            									current = createModelElement(grammarAccess.getTemporalPrimaryExprRule());
                            								}
                            								setWithLastConsumed(current, "operator", lv_operator_34_0, ".sum");
                            							

                            }


                            }


                            }


                            }
                            break;
                        case 10 :
                            // InternalSpectra.g:4528:5: ( ( (otherlv_35= RULE_ID ) ) ( (lv_operator_36_0= '.min' ) ) )
                            {
                            // InternalSpectra.g:4528:5: ( ( (otherlv_35= RULE_ID ) ) ( (lv_operator_36_0= '.min' ) ) )
                            // InternalSpectra.g:4529:6: ( (otherlv_35= RULE_ID ) ) ( (lv_operator_36_0= '.min' ) )
                            {
                            // InternalSpectra.g:4529:6: ( (otherlv_35= RULE_ID ) )
                            // InternalSpectra.g:4530:7: (otherlv_35= RULE_ID )
                            {
                            // InternalSpectra.g:4530:7: (otherlv_35= RULE_ID )
                            // InternalSpectra.g:4531:8: otherlv_35= RULE_ID
                            {

                            								if (current==null) {
                            									current = createModelElement(grammarAccess.getTemporalPrimaryExprRule());
                            								}
                            							
                            otherlv_35=(Token)match(input,RULE_ID,FOLLOW_63); 

                            								newLeafNode(otherlv_35, grammarAccess.getTemporalPrimaryExprAccess().getPointerReferrableCrossReference_2_1_9_0_0());
                            							

                            }


                            }

                            // InternalSpectra.g:4542:6: ( (lv_operator_36_0= '.min' ) )
                            // InternalSpectra.g:4543:7: (lv_operator_36_0= '.min' )
                            {
                            // InternalSpectra.g:4543:7: (lv_operator_36_0= '.min' )
                            // InternalSpectra.g:4544:8: lv_operator_36_0= '.min'
                            {
                            lv_operator_36_0=(Token)match(input,94,FOLLOW_2); 

                            								newLeafNode(lv_operator_36_0, grammarAccess.getTemporalPrimaryExprAccess().getOperatorMinKeyword_2_1_9_1_0());
                            							

                            								if (current==null) {
                            									current = createModelElement(grammarAccess.getTemporalPrimaryExprRule());
                            								}
                            								setWithLastConsumed(current, "operator", lv_operator_36_0, ".min");
                            							

                            }


                            }


                            }


                            }
                            break;
                        case 11 :
                            // InternalSpectra.g:4558:5: ( ( (otherlv_37= RULE_ID ) ) ( (lv_operator_38_0= '.max' ) ) )
                            {
                            // InternalSpectra.g:4558:5: ( ( (otherlv_37= RULE_ID ) ) ( (lv_operator_38_0= '.max' ) ) )
                            // InternalSpectra.g:4559:6: ( (otherlv_37= RULE_ID ) ) ( (lv_operator_38_0= '.max' ) )
                            {
                            // InternalSpectra.g:4559:6: ( (otherlv_37= RULE_ID ) )
                            // InternalSpectra.g:4560:7: (otherlv_37= RULE_ID )
                            {
                            // InternalSpectra.g:4560:7: (otherlv_37= RULE_ID )
                            // InternalSpectra.g:4561:8: otherlv_37= RULE_ID
                            {

                            								if (current==null) {
                            									current = createModelElement(grammarAccess.getTemporalPrimaryExprRule());
                            								}
                            							
                            otherlv_37=(Token)match(input,RULE_ID,FOLLOW_64); 

                            								newLeafNode(otherlv_37, grammarAccess.getTemporalPrimaryExprAccess().getPointerReferrableCrossReference_2_1_10_0_0());
                            							

                            }


                            }

                            // InternalSpectra.g:4572:6: ( (lv_operator_38_0= '.max' ) )
                            // InternalSpectra.g:4573:7: (lv_operator_38_0= '.max' )
                            {
                            // InternalSpectra.g:4573:7: (lv_operator_38_0= '.max' )
                            // InternalSpectra.g:4574:8: lv_operator_38_0= '.max'
                            {
                            lv_operator_38_0=(Token)match(input,95,FOLLOW_2); 

                            								newLeafNode(lv_operator_38_0, grammarAccess.getTemporalPrimaryExprAccess().getOperatorMaxKeyword_2_1_10_1_0());
                            							

                            								if (current==null) {
                            									current = createModelElement(grammarAccess.getTemporalPrimaryExprRule());
                            								}
                            								setWithLastConsumed(current, "operator", lv_operator_38_0, ".max");
                            							

                            }


                            }


                            }


                            }
                            break;

                    }


                    }


                    }
                    break;

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleTemporalPrimaryExpr"


    // $ANTLR start "entryRuleSubrange"
    // InternalSpectra.g:4593:1: entryRuleSubrange returns [EObject current=null] : iv_ruleSubrange= ruleSubrange EOF ;
    public final EObject entryRuleSubrange() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleSubrange = null;


        try {
            // InternalSpectra.g:4593:49: (iv_ruleSubrange= ruleSubrange EOF )
            // InternalSpectra.g:4594:2: iv_ruleSubrange= ruleSubrange EOF
            {
             newCompositeNode(grammarAccess.getSubrangeRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleSubrange=ruleSubrange();

            state._fsp--;

             current =iv_ruleSubrange; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleSubrange"


    // $ANTLR start "ruleSubrange"
    // InternalSpectra.g:4600:1: ruleSubrange returns [EObject current=null] : ( ( (lv_from_0_0= ruleSizeDefineDecl ) ) otherlv_1= '..' ( (lv_to_2_0= ruleSizeDefineDecl ) ) ) ;
    public final EObject ruleSubrange() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        EObject lv_from_0_0 = null;

        EObject lv_to_2_0 = null;



        	enterRule();

        try {
            // InternalSpectra.g:4606:2: ( ( ( (lv_from_0_0= ruleSizeDefineDecl ) ) otherlv_1= '..' ( (lv_to_2_0= ruleSizeDefineDecl ) ) ) )
            // InternalSpectra.g:4607:2: ( ( (lv_from_0_0= ruleSizeDefineDecl ) ) otherlv_1= '..' ( (lv_to_2_0= ruleSizeDefineDecl ) ) )
            {
            // InternalSpectra.g:4607:2: ( ( (lv_from_0_0= ruleSizeDefineDecl ) ) otherlv_1= '..' ( (lv_to_2_0= ruleSizeDefineDecl ) ) )
            // InternalSpectra.g:4608:3: ( (lv_from_0_0= ruleSizeDefineDecl ) ) otherlv_1= '..' ( (lv_to_2_0= ruleSizeDefineDecl ) )
            {
            // InternalSpectra.g:4608:3: ( (lv_from_0_0= ruleSizeDefineDecl ) )
            // InternalSpectra.g:4609:4: (lv_from_0_0= ruleSizeDefineDecl )
            {
            // InternalSpectra.g:4609:4: (lv_from_0_0= ruleSizeDefineDecl )
            // InternalSpectra.g:4610:5: lv_from_0_0= ruleSizeDefineDecl
            {

            					newCompositeNode(grammarAccess.getSubrangeAccess().getFromSizeDefineDeclParserRuleCall_0_0());
            				
            pushFollow(FOLLOW_65);
            lv_from_0_0=ruleSizeDefineDecl();

            state._fsp--;


            					if (current==null) {
            						current = createModelElementForParent(grammarAccess.getSubrangeRule());
            					}
            					set(
            						current,
            						"from",
            						lv_from_0_0,
            						"tau.smlab.syntech.Spectra.SizeDefineDecl");
            					afterParserOrEnumRuleCall();
            				

            }


            }

            otherlv_1=(Token)match(input,96,FOLLOW_20); 

            			newLeafNode(otherlv_1, grammarAccess.getSubrangeAccess().getFullStopFullStopKeyword_1());
            		
            // InternalSpectra.g:4631:3: ( (lv_to_2_0= ruleSizeDefineDecl ) )
            // InternalSpectra.g:4632:4: (lv_to_2_0= ruleSizeDefineDecl )
            {
            // InternalSpectra.g:4632:4: (lv_to_2_0= ruleSizeDefineDecl )
            // InternalSpectra.g:4633:5: lv_to_2_0= ruleSizeDefineDecl
            {

            					newCompositeNode(grammarAccess.getSubrangeAccess().getToSizeDefineDeclParserRuleCall_2_0());
            				
            pushFollow(FOLLOW_2);
            lv_to_2_0=ruleSizeDefineDecl();

            state._fsp--;


            					if (current==null) {
            						current = createModelElementForParent(grammarAccess.getSubrangeRule());
            					}
            					set(
            						current,
            						"to",
            						lv_to_2_0,
            						"tau.smlab.syntech.Spectra.SizeDefineDecl");
            					afterParserOrEnumRuleCall();
            				

            }


            }


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleSubrange"


    // $ANTLR start "entryRuleConstant"
    // InternalSpectra.g:4654:1: entryRuleConstant returns [EObject current=null] : iv_ruleConstant= ruleConstant EOF ;
    public final EObject entryRuleConstant() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleConstant = null;


        try {
            // InternalSpectra.g:4654:49: (iv_ruleConstant= ruleConstant EOF )
            // InternalSpectra.g:4655:2: iv_ruleConstant= ruleConstant EOF
            {
             newCompositeNode(grammarAccess.getConstantRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleConstant=ruleConstant();

            state._fsp--;

             current =iv_ruleConstant; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleConstant"


    // $ANTLR start "ruleConstant"
    // InternalSpectra.g:4661:1: ruleConstant returns [EObject current=null] : ( () ( ( (lv_booleanValue_1_0= 'FALSE' ) ) | ( (lv_booleanValue_2_0= 'false' ) ) | ( (lv_booleanValue_3_0= 'TRUE' ) ) | ( (lv_booleanValue_4_0= 'true' ) ) | ( (lv_integerValue_5_0= RULE_INT ) ) ) ) ;
    public final EObject ruleConstant() throws RecognitionException {
        EObject current = null;

        Token lv_booleanValue_1_0=null;
        Token lv_booleanValue_2_0=null;
        Token lv_booleanValue_3_0=null;
        Token lv_booleanValue_4_0=null;
        Token lv_integerValue_5_0=null;


        	enterRule();

        try {
            // InternalSpectra.g:4667:2: ( ( () ( ( (lv_booleanValue_1_0= 'FALSE' ) ) | ( (lv_booleanValue_2_0= 'false' ) ) | ( (lv_booleanValue_3_0= 'TRUE' ) ) | ( (lv_booleanValue_4_0= 'true' ) ) | ( (lv_integerValue_5_0= RULE_INT ) ) ) ) )
            // InternalSpectra.g:4668:2: ( () ( ( (lv_booleanValue_1_0= 'FALSE' ) ) | ( (lv_booleanValue_2_0= 'false' ) ) | ( (lv_booleanValue_3_0= 'TRUE' ) ) | ( (lv_booleanValue_4_0= 'true' ) ) | ( (lv_integerValue_5_0= RULE_INT ) ) ) )
            {
            // InternalSpectra.g:4668:2: ( () ( ( (lv_booleanValue_1_0= 'FALSE' ) ) | ( (lv_booleanValue_2_0= 'false' ) ) | ( (lv_booleanValue_3_0= 'TRUE' ) ) | ( (lv_booleanValue_4_0= 'true' ) ) | ( (lv_integerValue_5_0= RULE_INT ) ) ) )
            // InternalSpectra.g:4669:3: () ( ( (lv_booleanValue_1_0= 'FALSE' ) ) | ( (lv_booleanValue_2_0= 'false' ) ) | ( (lv_booleanValue_3_0= 'TRUE' ) ) | ( (lv_booleanValue_4_0= 'true' ) ) | ( (lv_integerValue_5_0= RULE_INT ) ) )
            {
            // InternalSpectra.g:4669:3: ()
            // InternalSpectra.g:4670:4: 
            {

            				current = forceCreateModelElement(
            					grammarAccess.getConstantAccess().getConstantAction_0(),
            					current);
            			

            }

            // InternalSpectra.g:4676:3: ( ( (lv_booleanValue_1_0= 'FALSE' ) ) | ( (lv_booleanValue_2_0= 'false' ) ) | ( (lv_booleanValue_3_0= 'TRUE' ) ) | ( (lv_booleanValue_4_0= 'true' ) ) | ( (lv_integerValue_5_0= RULE_INT ) ) )
            int alt90=5;
            switch ( input.LA(1) ) {
            case 97:
                {
                alt90=1;
                }
                break;
            case 98:
                {
                alt90=2;
                }
                break;
            case 99:
                {
                alt90=3;
                }
                break;
            case 100:
                {
                alt90=4;
                }
                break;
            case RULE_INT:
                {
                alt90=5;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 90, 0, input);

                throw nvae;
            }

            switch (alt90) {
                case 1 :
                    // InternalSpectra.g:4677:4: ( (lv_booleanValue_1_0= 'FALSE' ) )
                    {
                    // InternalSpectra.g:4677:4: ( (lv_booleanValue_1_0= 'FALSE' ) )
                    // InternalSpectra.g:4678:5: (lv_booleanValue_1_0= 'FALSE' )
                    {
                    // InternalSpectra.g:4678:5: (lv_booleanValue_1_0= 'FALSE' )
                    // InternalSpectra.g:4679:6: lv_booleanValue_1_0= 'FALSE'
                    {
                    lv_booleanValue_1_0=(Token)match(input,97,FOLLOW_2); 

                    						newLeafNode(lv_booleanValue_1_0, grammarAccess.getConstantAccess().getBooleanValueFALSEKeyword_1_0_0());
                    					

                    						if (current==null) {
                    							current = createModelElement(grammarAccess.getConstantRule());
                    						}
                    						setWithLastConsumed(current, "booleanValue", lv_booleanValue_1_0, "FALSE");
                    					

                    }


                    }


                    }
                    break;
                case 2 :
                    // InternalSpectra.g:4692:4: ( (lv_booleanValue_2_0= 'false' ) )
                    {
                    // InternalSpectra.g:4692:4: ( (lv_booleanValue_2_0= 'false' ) )
                    // InternalSpectra.g:4693:5: (lv_booleanValue_2_0= 'false' )
                    {
                    // InternalSpectra.g:4693:5: (lv_booleanValue_2_0= 'false' )
                    // InternalSpectra.g:4694:6: lv_booleanValue_2_0= 'false'
                    {
                    lv_booleanValue_2_0=(Token)match(input,98,FOLLOW_2); 

                    						newLeafNode(lv_booleanValue_2_0, grammarAccess.getConstantAccess().getBooleanValueFalseKeyword_1_1_0());
                    					

                    						if (current==null) {
                    							current = createModelElement(grammarAccess.getConstantRule());
                    						}
                    						setWithLastConsumed(current, "booleanValue", lv_booleanValue_2_0, "false");
                    					

                    }


                    }


                    }
                    break;
                case 3 :
                    // InternalSpectra.g:4707:4: ( (lv_booleanValue_3_0= 'TRUE' ) )
                    {
                    // InternalSpectra.g:4707:4: ( (lv_booleanValue_3_0= 'TRUE' ) )
                    // InternalSpectra.g:4708:5: (lv_booleanValue_3_0= 'TRUE' )
                    {
                    // InternalSpectra.g:4708:5: (lv_booleanValue_3_0= 'TRUE' )
                    // InternalSpectra.g:4709:6: lv_booleanValue_3_0= 'TRUE'
                    {
                    lv_booleanValue_3_0=(Token)match(input,99,FOLLOW_2); 

                    						newLeafNode(lv_booleanValue_3_0, grammarAccess.getConstantAccess().getBooleanValueTRUEKeyword_1_2_0());
                    					

                    						if (current==null) {
                    							current = createModelElement(grammarAccess.getConstantRule());
                    						}
                    						setWithLastConsumed(current, "booleanValue", lv_booleanValue_3_0, "TRUE");
                    					

                    }


                    }


                    }
                    break;
                case 4 :
                    // InternalSpectra.g:4722:4: ( (lv_booleanValue_4_0= 'true' ) )
                    {
                    // InternalSpectra.g:4722:4: ( (lv_booleanValue_4_0= 'true' ) )
                    // InternalSpectra.g:4723:5: (lv_booleanValue_4_0= 'true' )
                    {
                    // InternalSpectra.g:4723:5: (lv_booleanValue_4_0= 'true' )
                    // InternalSpectra.g:4724:6: lv_booleanValue_4_0= 'true'
                    {
                    lv_booleanValue_4_0=(Token)match(input,100,FOLLOW_2); 

                    						newLeafNode(lv_booleanValue_4_0, grammarAccess.getConstantAccess().getBooleanValueTrueKeyword_1_3_0());
                    					

                    						if (current==null) {
                    							current = createModelElement(grammarAccess.getConstantRule());
                    						}
                    						setWithLastConsumed(current, "booleanValue", lv_booleanValue_4_0, "true");
                    					

                    }


                    }


                    }
                    break;
                case 5 :
                    // InternalSpectra.g:4737:4: ( (lv_integerValue_5_0= RULE_INT ) )
                    {
                    // InternalSpectra.g:4737:4: ( (lv_integerValue_5_0= RULE_INT ) )
                    // InternalSpectra.g:4738:5: (lv_integerValue_5_0= RULE_INT )
                    {
                    // InternalSpectra.g:4738:5: (lv_integerValue_5_0= RULE_INT )
                    // InternalSpectra.g:4739:6: lv_integerValue_5_0= RULE_INT
                    {
                    lv_integerValue_5_0=(Token)match(input,RULE_INT,FOLLOW_2); 

                    						newLeafNode(lv_integerValue_5_0, grammarAccess.getConstantAccess().getIntegerValueINTTerminalRuleCall_1_4_0());
                    					

                    						if (current==null) {
                    							current = createModelElement(grammarAccess.getConstantRule());
                    						}
                    						setWithLastConsumed(
                    							current,
                    							"integerValue",
                    							lv_integerValue_5_0,
                    							"org.eclipse.xtext.common.Terminals.INT");
                    					

                    }


                    }


                    }
                    break;

            }


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleConstant"


    // $ANTLR start "entryRuleTrigger"
    // InternalSpectra.g:4760:1: entryRuleTrigger returns [EObject current=null] : iv_ruleTrigger= ruleTrigger EOF ;
    public final EObject entryRuleTrigger() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleTrigger = null;


        try {
            // InternalSpectra.g:4760:48: (iv_ruleTrigger= ruleTrigger EOF )
            // InternalSpectra.g:4761:2: iv_ruleTrigger= ruleTrigger EOF
            {
             newCompositeNode(grammarAccess.getTriggerRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleTrigger=ruleTrigger();

            state._fsp--;

             current =iv_ruleTrigger; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleTrigger"


    // $ANTLR start "ruleTrigger"
    // InternalSpectra.g:4767:1: ruleTrigger returns [EObject current=null] : ( () otherlv_1= 'trig' ( ( (otherlv_2= RULE_ID ) ) | ( (lv_initRegExp_3_0= ruleRegExp ) ) ) otherlv_4= '|=>' ( ( (otherlv_5= RULE_ID ) ) | ( (lv_effectRegExp_6_0= ruleRegExp ) ) ) ) ;
    public final EObject ruleTrigger() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_2=null;
        Token otherlv_4=null;
        Token otherlv_5=null;
        EObject lv_initRegExp_3_0 = null;

        EObject lv_effectRegExp_6_0 = null;



        	enterRule();

        try {
            // InternalSpectra.g:4773:2: ( ( () otherlv_1= 'trig' ( ( (otherlv_2= RULE_ID ) ) | ( (lv_initRegExp_3_0= ruleRegExp ) ) ) otherlv_4= '|=>' ( ( (otherlv_5= RULE_ID ) ) | ( (lv_effectRegExp_6_0= ruleRegExp ) ) ) ) )
            // InternalSpectra.g:4774:2: ( () otherlv_1= 'trig' ( ( (otherlv_2= RULE_ID ) ) | ( (lv_initRegExp_3_0= ruleRegExp ) ) ) otherlv_4= '|=>' ( ( (otherlv_5= RULE_ID ) ) | ( (lv_effectRegExp_6_0= ruleRegExp ) ) ) )
            {
            // InternalSpectra.g:4774:2: ( () otherlv_1= 'trig' ( ( (otherlv_2= RULE_ID ) ) | ( (lv_initRegExp_3_0= ruleRegExp ) ) ) otherlv_4= '|=>' ( ( (otherlv_5= RULE_ID ) ) | ( (lv_effectRegExp_6_0= ruleRegExp ) ) ) )
            // InternalSpectra.g:4775:3: () otherlv_1= 'trig' ( ( (otherlv_2= RULE_ID ) ) | ( (lv_initRegExp_3_0= ruleRegExp ) ) ) otherlv_4= '|=>' ( ( (otherlv_5= RULE_ID ) ) | ( (lv_effectRegExp_6_0= ruleRegExp ) ) )
            {
            // InternalSpectra.g:4775:3: ()
            // InternalSpectra.g:4776:4: 
            {

            				current = forceCreateModelElement(
            					grammarAccess.getTriggerAccess().getTriggerAction_0(),
            					current);
            			

            }

            otherlv_1=(Token)match(input,101,FOLLOW_58); 

            			newLeafNode(otherlv_1, grammarAccess.getTriggerAccess().getTrigKeyword_1());
            		
            // InternalSpectra.g:4786:3: ( ( (otherlv_2= RULE_ID ) ) | ( (lv_initRegExp_3_0= ruleRegExp ) ) )
            int alt91=2;
            int LA91_0 = input.LA(1);

            if ( (LA91_0==RULE_ID) ) {
                alt91=1;
            }
            else if ( (LA91_0==21||LA91_0==28||LA91_0==48||(LA91_0>=97 && LA91_0<=100)||LA91_0==104) ) {
                alt91=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 91, 0, input);

                throw nvae;
            }
            switch (alt91) {
                case 1 :
                    // InternalSpectra.g:4787:4: ( (otherlv_2= RULE_ID ) )
                    {
                    // InternalSpectra.g:4787:4: ( (otherlv_2= RULE_ID ) )
                    // InternalSpectra.g:4788:5: (otherlv_2= RULE_ID )
                    {
                    // InternalSpectra.g:4788:5: (otherlv_2= RULE_ID )
                    // InternalSpectra.g:4789:6: otherlv_2= RULE_ID
                    {

                    						if (current==null) {
                    							current = createModelElement(grammarAccess.getTriggerRule());
                    						}
                    					
                    otherlv_2=(Token)match(input,RULE_ID,FOLLOW_66); 

                    						newLeafNode(otherlv_2, grammarAccess.getTriggerAccess().getInitPointerDefineRegExpDeclCrossReference_2_0_0());
                    					

                    }


                    }


                    }
                    break;
                case 2 :
                    // InternalSpectra.g:4801:4: ( (lv_initRegExp_3_0= ruleRegExp ) )
                    {
                    // InternalSpectra.g:4801:4: ( (lv_initRegExp_3_0= ruleRegExp ) )
                    // InternalSpectra.g:4802:5: (lv_initRegExp_3_0= ruleRegExp )
                    {
                    // InternalSpectra.g:4802:5: (lv_initRegExp_3_0= ruleRegExp )
                    // InternalSpectra.g:4803:6: lv_initRegExp_3_0= ruleRegExp
                    {

                    						newCompositeNode(grammarAccess.getTriggerAccess().getInitRegExpRegExpParserRuleCall_2_1_0());
                    					
                    pushFollow(FOLLOW_66);
                    lv_initRegExp_3_0=ruleRegExp();

                    state._fsp--;


                    						if (current==null) {
                    							current = createModelElementForParent(grammarAccess.getTriggerRule());
                    						}
                    						set(
                    							current,
                    							"initRegExp",
                    							lv_initRegExp_3_0,
                    							"tau.smlab.syntech.Spectra.RegExp");
                    						afterParserOrEnumRuleCall();
                    					

                    }


                    }


                    }
                    break;

            }

            otherlv_4=(Token)match(input,102,FOLLOW_58); 

            			newLeafNode(otherlv_4, grammarAccess.getTriggerAccess().getVerticalLineEqualsSignGreaterThanSignKeyword_3());
            		
            // InternalSpectra.g:4825:3: ( ( (otherlv_5= RULE_ID ) ) | ( (lv_effectRegExp_6_0= ruleRegExp ) ) )
            int alt92=2;
            int LA92_0 = input.LA(1);

            if ( (LA92_0==RULE_ID) ) {
                alt92=1;
            }
            else if ( (LA92_0==21||LA92_0==28||LA92_0==48||(LA92_0>=97 && LA92_0<=100)||LA92_0==104) ) {
                alt92=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 92, 0, input);

                throw nvae;
            }
            switch (alt92) {
                case 1 :
                    // InternalSpectra.g:4826:4: ( (otherlv_5= RULE_ID ) )
                    {
                    // InternalSpectra.g:4826:4: ( (otherlv_5= RULE_ID ) )
                    // InternalSpectra.g:4827:5: (otherlv_5= RULE_ID )
                    {
                    // InternalSpectra.g:4827:5: (otherlv_5= RULE_ID )
                    // InternalSpectra.g:4828:6: otherlv_5= RULE_ID
                    {

                    						if (current==null) {
                    							current = createModelElement(grammarAccess.getTriggerRule());
                    						}
                    					
                    otherlv_5=(Token)match(input,RULE_ID,FOLLOW_2); 

                    						newLeafNode(otherlv_5, grammarAccess.getTriggerAccess().getEffectPointerDefineRegExpDeclCrossReference_4_0_0());
                    					

                    }


                    }


                    }
                    break;
                case 2 :
                    // InternalSpectra.g:4840:4: ( (lv_effectRegExp_6_0= ruleRegExp ) )
                    {
                    // InternalSpectra.g:4840:4: ( (lv_effectRegExp_6_0= ruleRegExp ) )
                    // InternalSpectra.g:4841:5: (lv_effectRegExp_6_0= ruleRegExp )
                    {
                    // InternalSpectra.g:4841:5: (lv_effectRegExp_6_0= ruleRegExp )
                    // InternalSpectra.g:4842:6: lv_effectRegExp_6_0= ruleRegExp
                    {

                    						newCompositeNode(grammarAccess.getTriggerAccess().getEffectRegExpRegExpParserRuleCall_4_1_0());
                    					
                    pushFollow(FOLLOW_2);
                    lv_effectRegExp_6_0=ruleRegExp();

                    state._fsp--;


                    						if (current==null) {
                    							current = createModelElementForParent(grammarAccess.getTriggerRule());
                    						}
                    						set(
                    							current,
                    							"effectRegExp",
                    							lv_effectRegExp_6_0,
                    							"tau.smlab.syntech.Spectra.RegExp");
                    						afterParserOrEnumRuleCall();
                    					

                    }


                    }


                    }
                    break;

            }


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleTrigger"


    // $ANTLR start "entryRuleRegExp"
    // InternalSpectra.g:4864:1: entryRuleRegExp returns [EObject current=null] : iv_ruleRegExp= ruleRegExp EOF ;
    public final EObject entryRuleRegExp() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleRegExp = null;


        try {
            // InternalSpectra.g:4864:47: (iv_ruleRegExp= ruleRegExp EOF )
            // InternalSpectra.g:4865:2: iv_ruleRegExp= ruleRegExp EOF
            {
             newCompositeNode(grammarAccess.getRegExpRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleRegExp=ruleRegExp();

            state._fsp--;

             current =iv_ruleRegExp; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleRegExp"


    // $ANTLR start "ruleRegExp"
    // InternalSpectra.g:4871:1: ruleRegExp returns [EObject current=null] : this_BinaryRegExp_0= ruleBinaryRegExp ;
    public final EObject ruleRegExp() throws RecognitionException {
        EObject current = null;

        EObject this_BinaryRegExp_0 = null;



        	enterRule();

        try {
            // InternalSpectra.g:4877:2: (this_BinaryRegExp_0= ruleBinaryRegExp )
            // InternalSpectra.g:4878:2: this_BinaryRegExp_0= ruleBinaryRegExp
            {

            		newCompositeNode(grammarAccess.getRegExpAccess().getBinaryRegExpParserRuleCall());
            	
            pushFollow(FOLLOW_2);
            this_BinaryRegExp_0=ruleBinaryRegExp();

            state._fsp--;


            		current = this_BinaryRegExp_0;
            		afterParserOrEnumRuleCall();
            	

            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleRegExp"


    // $ANTLR start "entryRuleBinaryRegExp"
    // InternalSpectra.g:4889:1: entryRuleBinaryRegExp returns [EObject current=null] : iv_ruleBinaryRegExp= ruleBinaryRegExp EOF ;
    public final EObject entryRuleBinaryRegExp() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleBinaryRegExp = null;


        try {
            // InternalSpectra.g:4889:53: (iv_ruleBinaryRegExp= ruleBinaryRegExp EOF )
            // InternalSpectra.g:4890:2: iv_ruleBinaryRegExp= ruleBinaryRegExp EOF
            {
             newCompositeNode(grammarAccess.getBinaryRegExpRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleBinaryRegExp=ruleBinaryRegExp();

            state._fsp--;

             current =iv_ruleBinaryRegExp; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleBinaryRegExp"


    // $ANTLR start "ruleBinaryRegExp"
    // InternalSpectra.g:4896:1: ruleBinaryRegExp returns [EObject current=null] : (this_UnaryRegExp_0= ruleUnaryRegExp ( () ( ( (lv_op_2_1= '&' | lv_op_2_2= '|' ) ) )? ( (lv_right_3_0= ruleUnaryRegExp ) ) )* ) ;
    public final EObject ruleBinaryRegExp() throws RecognitionException {
        EObject current = null;

        Token lv_op_2_1=null;
        Token lv_op_2_2=null;
        EObject this_UnaryRegExp_0 = null;

        EObject lv_right_3_0 = null;



        	enterRule();

        try {
            // InternalSpectra.g:4902:2: ( (this_UnaryRegExp_0= ruleUnaryRegExp ( () ( ( (lv_op_2_1= '&' | lv_op_2_2= '|' ) ) )? ( (lv_right_3_0= ruleUnaryRegExp ) ) )* ) )
            // InternalSpectra.g:4903:2: (this_UnaryRegExp_0= ruleUnaryRegExp ( () ( ( (lv_op_2_1= '&' | lv_op_2_2= '|' ) ) )? ( (lv_right_3_0= ruleUnaryRegExp ) ) )* )
            {
            // InternalSpectra.g:4903:2: (this_UnaryRegExp_0= ruleUnaryRegExp ( () ( ( (lv_op_2_1= '&' | lv_op_2_2= '|' ) ) )? ( (lv_right_3_0= ruleUnaryRegExp ) ) )* )
            // InternalSpectra.g:4904:3: this_UnaryRegExp_0= ruleUnaryRegExp ( () ( ( (lv_op_2_1= '&' | lv_op_2_2= '|' ) ) )? ( (lv_right_3_0= ruleUnaryRegExp ) ) )*
            {

            			newCompositeNode(grammarAccess.getBinaryRegExpAccess().getUnaryRegExpParserRuleCall_0());
            		
            pushFollow(FOLLOW_67);
            this_UnaryRegExp_0=ruleUnaryRegExp();

            state._fsp--;


            			current = this_UnaryRegExp_0;
            			afterParserOrEnumRuleCall();
            		
            // InternalSpectra.g:4912:3: ( () ( ( (lv_op_2_1= '&' | lv_op_2_2= '|' ) ) )? ( (lv_right_3_0= ruleUnaryRegExp ) ) )*
            loop95:
            do {
                int alt95=2;
                int LA95_0 = input.LA(1);

                if ( (LA95_0==21||LA95_0==28||LA95_0==48||LA95_0==63||LA95_0==66||(LA95_0>=97 && LA95_0<=100)||LA95_0==104) ) {
                    alt95=1;
                }


                switch (alt95) {
            	case 1 :
            	    // InternalSpectra.g:4913:4: () ( ( (lv_op_2_1= '&' | lv_op_2_2= '|' ) ) )? ( (lv_right_3_0= ruleUnaryRegExp ) )
            	    {
            	    // InternalSpectra.g:4913:4: ()
            	    // InternalSpectra.g:4914:5: 
            	    {

            	    					current = forceCreateModelElementAndSet(
            	    						grammarAccess.getBinaryRegExpAccess().getBinaryRegExpLeftAction_1_0(),
            	    						current);
            	    				

            	    }

            	    // InternalSpectra.g:4920:4: ( ( (lv_op_2_1= '&' | lv_op_2_2= '|' ) ) )?
            	    int alt94=2;
            	    int LA94_0 = input.LA(1);

            	    if ( (LA94_0==63||LA94_0==66) ) {
            	        alt94=1;
            	    }
            	    switch (alt94) {
            	        case 1 :
            	            // InternalSpectra.g:4921:5: ( (lv_op_2_1= '&' | lv_op_2_2= '|' ) )
            	            {
            	            // InternalSpectra.g:4921:5: ( (lv_op_2_1= '&' | lv_op_2_2= '|' ) )
            	            // InternalSpectra.g:4922:6: (lv_op_2_1= '&' | lv_op_2_2= '|' )
            	            {
            	            // InternalSpectra.g:4922:6: (lv_op_2_1= '&' | lv_op_2_2= '|' )
            	            int alt93=2;
            	            int LA93_0 = input.LA(1);

            	            if ( (LA93_0==66) ) {
            	                alt93=1;
            	            }
            	            else if ( (LA93_0==63) ) {
            	                alt93=2;
            	            }
            	            else {
            	                NoViableAltException nvae =
            	                    new NoViableAltException("", 93, 0, input);

            	                throw nvae;
            	            }
            	            switch (alt93) {
            	                case 1 :
            	                    // InternalSpectra.g:4923:7: lv_op_2_1= '&'
            	                    {
            	                    lv_op_2_1=(Token)match(input,66,FOLLOW_25); 

            	                    							newLeafNode(lv_op_2_1, grammarAccess.getBinaryRegExpAccess().getOpAmpersandKeyword_1_1_0_0());
            	                    						

            	                    							if (current==null) {
            	                    								current = createModelElement(grammarAccess.getBinaryRegExpRule());
            	                    							}
            	                    							setWithLastConsumed(current, "op", lv_op_2_1, null);
            	                    						

            	                    }
            	                    break;
            	                case 2 :
            	                    // InternalSpectra.g:4934:7: lv_op_2_2= '|'
            	                    {
            	                    lv_op_2_2=(Token)match(input,63,FOLLOW_25); 

            	                    							newLeafNode(lv_op_2_2, grammarAccess.getBinaryRegExpAccess().getOpVerticalLineKeyword_1_1_0_1());
            	                    						

            	                    							if (current==null) {
            	                    								current = createModelElement(grammarAccess.getBinaryRegExpRule());
            	                    							}
            	                    							setWithLastConsumed(current, "op", lv_op_2_2, null);
            	                    						

            	                    }
            	                    break;

            	            }


            	            }


            	            }
            	            break;

            	    }

            	    // InternalSpectra.g:4947:4: ( (lv_right_3_0= ruleUnaryRegExp ) )
            	    // InternalSpectra.g:4948:5: (lv_right_3_0= ruleUnaryRegExp )
            	    {
            	    // InternalSpectra.g:4948:5: (lv_right_3_0= ruleUnaryRegExp )
            	    // InternalSpectra.g:4949:6: lv_right_3_0= ruleUnaryRegExp
            	    {

            	    						newCompositeNode(grammarAccess.getBinaryRegExpAccess().getRightUnaryRegExpParserRuleCall_1_2_0());
            	    					
            	    pushFollow(FOLLOW_67);
            	    lv_right_3_0=ruleUnaryRegExp();

            	    state._fsp--;


            	    						if (current==null) {
            	    							current = createModelElementForParent(grammarAccess.getBinaryRegExpRule());
            	    						}
            	    						set(
            	    							current,
            	    							"right",
            	    							lv_right_3_0,
            	    							"tau.smlab.syntech.Spectra.UnaryRegExp");
            	    						afterParserOrEnumRuleCall();
            	    					

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop95;
                }
            } while (true);


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleBinaryRegExp"


    // $ANTLR start "entryRuleUnaryRegExp"
    // InternalSpectra.g:4971:1: entryRuleUnaryRegExp returns [EObject current=null] : iv_ruleUnaryRegExp= ruleUnaryRegExp EOF ;
    public final EObject entryRuleUnaryRegExp() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleUnaryRegExp = null;


        try {
            // InternalSpectra.g:4971:52: (iv_ruleUnaryRegExp= ruleUnaryRegExp EOF )
            // InternalSpectra.g:4972:2: iv_ruleUnaryRegExp= ruleUnaryRegExp EOF
            {
             newCompositeNode(grammarAccess.getUnaryRegExpRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleUnaryRegExp=ruleUnaryRegExp();

            state._fsp--;

             current =iv_ruleUnaryRegExp; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleUnaryRegExp"


    // $ANTLR start "ruleUnaryRegExp"
    // InternalSpectra.g:4978:1: ruleUnaryRegExp returns [EObject current=null] : (this_CompRegExp_0= ruleCompRegExp ( () ( ( (lv_kleened_2_0= '*' ) ) | ( (lv_questionMark_3_0= '?' ) ) | ( (lv_plus_4_0= '+' ) ) | ( ( (lv_haveExactRepetition_5_0= '{' ) ) ( (lv_exactRepetition_6_0= RULE_INT ) ) otherlv_7= '}' ) | ( ( (lv_haveAtLeast_8_0= '{' ) ) ( (lv_atLeast_9_0= RULE_INT ) ) otherlv_10= ',' otherlv_11= '}' ) | ( ( (lv_haveRange_12_0= '{' ) ) ( ( (lv_from_13_0= RULE_INT ) ) | ( (otherlv_14= RULE_ID ) ) ) otherlv_15= ',' ( ( (lv_to_16_0= RULE_INT ) ) | ( (otherlv_17= RULE_ID ) ) ) otherlv_18= '}' ) ) )? ) ;
    public final EObject ruleUnaryRegExp() throws RecognitionException {
        EObject current = null;

        Token lv_kleened_2_0=null;
        Token lv_questionMark_3_0=null;
        Token lv_plus_4_0=null;
        Token lv_haveExactRepetition_5_0=null;
        Token lv_exactRepetition_6_0=null;
        Token otherlv_7=null;
        Token lv_haveAtLeast_8_0=null;
        Token lv_atLeast_9_0=null;
        Token otherlv_10=null;
        Token otherlv_11=null;
        Token lv_haveRange_12_0=null;
        Token lv_from_13_0=null;
        Token otherlv_14=null;
        Token otherlv_15=null;
        Token lv_to_16_0=null;
        Token otherlv_17=null;
        Token otherlv_18=null;
        EObject this_CompRegExp_0 = null;



        	enterRule();

        try {
            // InternalSpectra.g:4984:2: ( (this_CompRegExp_0= ruleCompRegExp ( () ( ( (lv_kleened_2_0= '*' ) ) | ( (lv_questionMark_3_0= '?' ) ) | ( (lv_plus_4_0= '+' ) ) | ( ( (lv_haveExactRepetition_5_0= '{' ) ) ( (lv_exactRepetition_6_0= RULE_INT ) ) otherlv_7= '}' ) | ( ( (lv_haveAtLeast_8_0= '{' ) ) ( (lv_atLeast_9_0= RULE_INT ) ) otherlv_10= ',' otherlv_11= '}' ) | ( ( (lv_haveRange_12_0= '{' ) ) ( ( (lv_from_13_0= RULE_INT ) ) | ( (otherlv_14= RULE_ID ) ) ) otherlv_15= ',' ( ( (lv_to_16_0= RULE_INT ) ) | ( (otherlv_17= RULE_ID ) ) ) otherlv_18= '}' ) ) )? ) )
            // InternalSpectra.g:4985:2: (this_CompRegExp_0= ruleCompRegExp ( () ( ( (lv_kleened_2_0= '*' ) ) | ( (lv_questionMark_3_0= '?' ) ) | ( (lv_plus_4_0= '+' ) ) | ( ( (lv_haveExactRepetition_5_0= '{' ) ) ( (lv_exactRepetition_6_0= RULE_INT ) ) otherlv_7= '}' ) | ( ( (lv_haveAtLeast_8_0= '{' ) ) ( (lv_atLeast_9_0= RULE_INT ) ) otherlv_10= ',' otherlv_11= '}' ) | ( ( (lv_haveRange_12_0= '{' ) ) ( ( (lv_from_13_0= RULE_INT ) ) | ( (otherlv_14= RULE_ID ) ) ) otherlv_15= ',' ( ( (lv_to_16_0= RULE_INT ) ) | ( (otherlv_17= RULE_ID ) ) ) otherlv_18= '}' ) ) )? )
            {
            // InternalSpectra.g:4985:2: (this_CompRegExp_0= ruleCompRegExp ( () ( ( (lv_kleened_2_0= '*' ) ) | ( (lv_questionMark_3_0= '?' ) ) | ( (lv_plus_4_0= '+' ) ) | ( ( (lv_haveExactRepetition_5_0= '{' ) ) ( (lv_exactRepetition_6_0= RULE_INT ) ) otherlv_7= '}' ) | ( ( (lv_haveAtLeast_8_0= '{' ) ) ( (lv_atLeast_9_0= RULE_INT ) ) otherlv_10= ',' otherlv_11= '}' ) | ( ( (lv_haveRange_12_0= '{' ) ) ( ( (lv_from_13_0= RULE_INT ) ) | ( (otherlv_14= RULE_ID ) ) ) otherlv_15= ',' ( ( (lv_to_16_0= RULE_INT ) ) | ( (otherlv_17= RULE_ID ) ) ) otherlv_18= '}' ) ) )? )
            // InternalSpectra.g:4986:3: this_CompRegExp_0= ruleCompRegExp ( () ( ( (lv_kleened_2_0= '*' ) ) | ( (lv_questionMark_3_0= '?' ) ) | ( (lv_plus_4_0= '+' ) ) | ( ( (lv_haveExactRepetition_5_0= '{' ) ) ( (lv_exactRepetition_6_0= RULE_INT ) ) otherlv_7= '}' ) | ( ( (lv_haveAtLeast_8_0= '{' ) ) ( (lv_atLeast_9_0= RULE_INT ) ) otherlv_10= ',' otherlv_11= '}' ) | ( ( (lv_haveRange_12_0= '{' ) ) ( ( (lv_from_13_0= RULE_INT ) ) | ( (otherlv_14= RULE_ID ) ) ) otherlv_15= ',' ( ( (lv_to_16_0= RULE_INT ) ) | ( (otherlv_17= RULE_ID ) ) ) otherlv_18= '}' ) ) )?
            {

            			newCompositeNode(grammarAccess.getUnaryRegExpAccess().getCompRegExpParserRuleCall_0());
            		
            pushFollow(FOLLOW_68);
            this_CompRegExp_0=ruleCompRegExp();

            state._fsp--;


            			current = this_CompRegExp_0;
            			afterParserOrEnumRuleCall();
            		
            // InternalSpectra.g:4994:3: ( () ( ( (lv_kleened_2_0= '*' ) ) | ( (lv_questionMark_3_0= '?' ) ) | ( (lv_plus_4_0= '+' ) ) | ( ( (lv_haveExactRepetition_5_0= '{' ) ) ( (lv_exactRepetition_6_0= RULE_INT ) ) otherlv_7= '}' ) | ( ( (lv_haveAtLeast_8_0= '{' ) ) ( (lv_atLeast_9_0= RULE_INT ) ) otherlv_10= ',' otherlv_11= '}' ) | ( ( (lv_haveRange_12_0= '{' ) ) ( ( (lv_from_13_0= RULE_INT ) ) | ( (otherlv_14= RULE_ID ) ) ) otherlv_15= ',' ( ( (lv_to_16_0= RULE_INT ) ) | ( (otherlv_17= RULE_ID ) ) ) otherlv_18= '}' ) ) )?
            int alt99=2;
            int LA99_0 = input.LA(1);

            if ( (LA99_0==23||(LA99_0>=75 && LA99_0<=76)||LA99_0==103) ) {
                alt99=1;
            }
            switch (alt99) {
                case 1 :
                    // InternalSpectra.g:4995:4: () ( ( (lv_kleened_2_0= '*' ) ) | ( (lv_questionMark_3_0= '?' ) ) | ( (lv_plus_4_0= '+' ) ) | ( ( (lv_haveExactRepetition_5_0= '{' ) ) ( (lv_exactRepetition_6_0= RULE_INT ) ) otherlv_7= '}' ) | ( ( (lv_haveAtLeast_8_0= '{' ) ) ( (lv_atLeast_9_0= RULE_INT ) ) otherlv_10= ',' otherlv_11= '}' ) | ( ( (lv_haveRange_12_0= '{' ) ) ( ( (lv_from_13_0= RULE_INT ) ) | ( (otherlv_14= RULE_ID ) ) ) otherlv_15= ',' ( ( (lv_to_16_0= RULE_INT ) ) | ( (otherlv_17= RULE_ID ) ) ) otherlv_18= '}' ) )
                    {
                    // InternalSpectra.g:4995:4: ()
                    // InternalSpectra.g:4996:5: 
                    {

                    					current = forceCreateModelElementAndSet(
                    						grammarAccess.getUnaryRegExpAccess().getUnaryRegExpLeftAction_1_0(),
                    						current);
                    				

                    }

                    // InternalSpectra.g:5002:4: ( ( (lv_kleened_2_0= '*' ) ) | ( (lv_questionMark_3_0= '?' ) ) | ( (lv_plus_4_0= '+' ) ) | ( ( (lv_haveExactRepetition_5_0= '{' ) ) ( (lv_exactRepetition_6_0= RULE_INT ) ) otherlv_7= '}' ) | ( ( (lv_haveAtLeast_8_0= '{' ) ) ( (lv_atLeast_9_0= RULE_INT ) ) otherlv_10= ',' otherlv_11= '}' ) | ( ( (lv_haveRange_12_0= '{' ) ) ( ( (lv_from_13_0= RULE_INT ) ) | ( (otherlv_14= RULE_ID ) ) ) otherlv_15= ',' ( ( (lv_to_16_0= RULE_INT ) ) | ( (otherlv_17= RULE_ID ) ) ) otherlv_18= '}' ) )
                    int alt98=6;
                    alt98 = dfa98.predict(input);
                    switch (alt98) {
                        case 1 :
                            // InternalSpectra.g:5003:5: ( (lv_kleened_2_0= '*' ) )
                            {
                            // InternalSpectra.g:5003:5: ( (lv_kleened_2_0= '*' ) )
                            // InternalSpectra.g:5004:6: (lv_kleened_2_0= '*' )
                            {
                            // InternalSpectra.g:5004:6: (lv_kleened_2_0= '*' )
                            // InternalSpectra.g:5005:7: lv_kleened_2_0= '*'
                            {
                            lv_kleened_2_0=(Token)match(input,76,FOLLOW_2); 

                            							newLeafNode(lv_kleened_2_0, grammarAccess.getUnaryRegExpAccess().getKleenedAsteriskKeyword_1_1_0_0());
                            						

                            							if (current==null) {
                            								current = createModelElement(grammarAccess.getUnaryRegExpRule());
                            							}
                            							setWithLastConsumed(current, "kleened", lv_kleened_2_0 != null, "*");
                            						

                            }


                            }


                            }
                            break;
                        case 2 :
                            // InternalSpectra.g:5018:5: ( (lv_questionMark_3_0= '?' ) )
                            {
                            // InternalSpectra.g:5018:5: ( (lv_questionMark_3_0= '?' ) )
                            // InternalSpectra.g:5019:6: (lv_questionMark_3_0= '?' )
                            {
                            // InternalSpectra.g:5019:6: (lv_questionMark_3_0= '?' )
                            // InternalSpectra.g:5020:7: lv_questionMark_3_0= '?'
                            {
                            lv_questionMark_3_0=(Token)match(input,103,FOLLOW_2); 

                            							newLeafNode(lv_questionMark_3_0, grammarAccess.getUnaryRegExpAccess().getQuestionMarkQuestionMarkKeyword_1_1_1_0());
                            						

                            							if (current==null) {
                            								current = createModelElement(grammarAccess.getUnaryRegExpRule());
                            							}
                            							setWithLastConsumed(current, "questionMark", lv_questionMark_3_0 != null, "?");
                            						

                            }


                            }


                            }
                            break;
                        case 3 :
                            // InternalSpectra.g:5033:5: ( (lv_plus_4_0= '+' ) )
                            {
                            // InternalSpectra.g:5033:5: ( (lv_plus_4_0= '+' ) )
                            // InternalSpectra.g:5034:6: (lv_plus_4_0= '+' )
                            {
                            // InternalSpectra.g:5034:6: (lv_plus_4_0= '+' )
                            // InternalSpectra.g:5035:7: lv_plus_4_0= '+'
                            {
                            lv_plus_4_0=(Token)match(input,75,FOLLOW_2); 

                            							newLeafNode(lv_plus_4_0, grammarAccess.getUnaryRegExpAccess().getPlusPlusSignKeyword_1_1_2_0());
                            						

                            							if (current==null) {
                            								current = createModelElement(grammarAccess.getUnaryRegExpRule());
                            							}
                            							setWithLastConsumed(current, "plus", lv_plus_4_0 != null, "+");
                            						

                            }


                            }


                            }
                            break;
                        case 4 :
                            // InternalSpectra.g:5048:5: ( ( (lv_haveExactRepetition_5_0= '{' ) ) ( (lv_exactRepetition_6_0= RULE_INT ) ) otherlv_7= '}' )
                            {
                            // InternalSpectra.g:5048:5: ( ( (lv_haveExactRepetition_5_0= '{' ) ) ( (lv_exactRepetition_6_0= RULE_INT ) ) otherlv_7= '}' )
                            // InternalSpectra.g:5049:6: ( (lv_haveExactRepetition_5_0= '{' ) ) ( (lv_exactRepetition_6_0= RULE_INT ) ) otherlv_7= '}'
                            {
                            // InternalSpectra.g:5049:6: ( (lv_haveExactRepetition_5_0= '{' ) )
                            // InternalSpectra.g:5050:7: (lv_haveExactRepetition_5_0= '{' )
                            {
                            // InternalSpectra.g:5050:7: (lv_haveExactRepetition_5_0= '{' )
                            // InternalSpectra.g:5051:8: lv_haveExactRepetition_5_0= '{'
                            {
                            lv_haveExactRepetition_5_0=(Token)match(input,23,FOLLOW_11); 

                            								newLeafNode(lv_haveExactRepetition_5_0, grammarAccess.getUnaryRegExpAccess().getHaveExactRepetitionLeftCurlyBracketKeyword_1_1_3_0_0());
                            							

                            								if (current==null) {
                            									current = createModelElement(grammarAccess.getUnaryRegExpRule());
                            								}
                            								setWithLastConsumed(current, "haveExactRepetition", lv_haveExactRepetition_5_0 != null, "{");
                            							

                            }


                            }

                            // InternalSpectra.g:5063:6: ( (lv_exactRepetition_6_0= RULE_INT ) )
                            // InternalSpectra.g:5064:7: (lv_exactRepetition_6_0= RULE_INT )
                            {
                            // InternalSpectra.g:5064:7: (lv_exactRepetition_6_0= RULE_INT )
                            // InternalSpectra.g:5065:8: lv_exactRepetition_6_0= RULE_INT
                            {
                            lv_exactRepetition_6_0=(Token)match(input,RULE_INT,FOLLOW_37); 

                            								newLeafNode(lv_exactRepetition_6_0, grammarAccess.getUnaryRegExpAccess().getExactRepetitionINTTerminalRuleCall_1_1_3_1_0());
                            							

                            								if (current==null) {
                            									current = createModelElement(grammarAccess.getUnaryRegExpRule());
                            								}
                            								setWithLastConsumed(
                            									current,
                            									"exactRepetition",
                            									lv_exactRepetition_6_0,
                            									"org.eclipse.xtext.common.Terminals.INT");
                            							

                            }


                            }

                            otherlv_7=(Token)match(input,25,FOLLOW_2); 

                            						newLeafNode(otherlv_7, grammarAccess.getUnaryRegExpAccess().getRightCurlyBracketKeyword_1_1_3_2());
                            					

                            }


                            }
                            break;
                        case 5 :
                            // InternalSpectra.g:5087:5: ( ( (lv_haveAtLeast_8_0= '{' ) ) ( (lv_atLeast_9_0= RULE_INT ) ) otherlv_10= ',' otherlv_11= '}' )
                            {
                            // InternalSpectra.g:5087:5: ( ( (lv_haveAtLeast_8_0= '{' ) ) ( (lv_atLeast_9_0= RULE_INT ) ) otherlv_10= ',' otherlv_11= '}' )
                            // InternalSpectra.g:5088:6: ( (lv_haveAtLeast_8_0= '{' ) ) ( (lv_atLeast_9_0= RULE_INT ) ) otherlv_10= ',' otherlv_11= '}'
                            {
                            // InternalSpectra.g:5088:6: ( (lv_haveAtLeast_8_0= '{' ) )
                            // InternalSpectra.g:5089:7: (lv_haveAtLeast_8_0= '{' )
                            {
                            // InternalSpectra.g:5089:7: (lv_haveAtLeast_8_0= '{' )
                            // InternalSpectra.g:5090:8: lv_haveAtLeast_8_0= '{'
                            {
                            lv_haveAtLeast_8_0=(Token)match(input,23,FOLLOW_11); 

                            								newLeafNode(lv_haveAtLeast_8_0, grammarAccess.getUnaryRegExpAccess().getHaveAtLeastLeftCurlyBracketKeyword_1_1_4_0_0());
                            							

                            								if (current==null) {
                            									current = createModelElement(grammarAccess.getUnaryRegExpRule());
                            								}
                            								setWithLastConsumed(current, "haveAtLeast", lv_haveAtLeast_8_0 != null, "{");
                            							

                            }


                            }

                            // InternalSpectra.g:5102:6: ( (lv_atLeast_9_0= RULE_INT ) )
                            // InternalSpectra.g:5103:7: (lv_atLeast_9_0= RULE_INT )
                            {
                            // InternalSpectra.g:5103:7: (lv_atLeast_9_0= RULE_INT )
                            // InternalSpectra.g:5104:8: lv_atLeast_9_0= RULE_INT
                            {
                            lv_atLeast_9_0=(Token)match(input,RULE_INT,FOLLOW_69); 

                            								newLeafNode(lv_atLeast_9_0, grammarAccess.getUnaryRegExpAccess().getAtLeastINTTerminalRuleCall_1_1_4_1_0());
                            							

                            								if (current==null) {
                            									current = createModelElement(grammarAccess.getUnaryRegExpRule());
                            								}
                            								setWithLastConsumed(
                            									current,
                            									"atLeast",
                            									lv_atLeast_9_0,
                            									"org.eclipse.xtext.common.Terminals.INT");
                            							

                            }


                            }

                            otherlv_10=(Token)match(input,24,FOLLOW_37); 

                            						newLeafNode(otherlv_10, grammarAccess.getUnaryRegExpAccess().getCommaKeyword_1_1_4_2());
                            					
                            otherlv_11=(Token)match(input,25,FOLLOW_2); 

                            						newLeafNode(otherlv_11, grammarAccess.getUnaryRegExpAccess().getRightCurlyBracketKeyword_1_1_4_3());
                            					

                            }


                            }
                            break;
                        case 6 :
                            // InternalSpectra.g:5130:5: ( ( (lv_haveRange_12_0= '{' ) ) ( ( (lv_from_13_0= RULE_INT ) ) | ( (otherlv_14= RULE_ID ) ) ) otherlv_15= ',' ( ( (lv_to_16_0= RULE_INT ) ) | ( (otherlv_17= RULE_ID ) ) ) otherlv_18= '}' )
                            {
                            // InternalSpectra.g:5130:5: ( ( (lv_haveRange_12_0= '{' ) ) ( ( (lv_from_13_0= RULE_INT ) ) | ( (otherlv_14= RULE_ID ) ) ) otherlv_15= ',' ( ( (lv_to_16_0= RULE_INT ) ) | ( (otherlv_17= RULE_ID ) ) ) otherlv_18= '}' )
                            // InternalSpectra.g:5131:6: ( (lv_haveRange_12_0= '{' ) ) ( ( (lv_from_13_0= RULE_INT ) ) | ( (otherlv_14= RULE_ID ) ) ) otherlv_15= ',' ( ( (lv_to_16_0= RULE_INT ) ) | ( (otherlv_17= RULE_ID ) ) ) otherlv_18= '}'
                            {
                            // InternalSpectra.g:5131:6: ( (lv_haveRange_12_0= '{' ) )
                            // InternalSpectra.g:5132:7: (lv_haveRange_12_0= '{' )
                            {
                            // InternalSpectra.g:5132:7: (lv_haveRange_12_0= '{' )
                            // InternalSpectra.g:5133:8: lv_haveRange_12_0= '{'
                            {
                            lv_haveRange_12_0=(Token)match(input,23,FOLLOW_39); 

                            								newLeafNode(lv_haveRange_12_0, grammarAccess.getUnaryRegExpAccess().getHaveRangeLeftCurlyBracketKeyword_1_1_5_0_0());
                            							

                            								if (current==null) {
                            									current = createModelElement(grammarAccess.getUnaryRegExpRule());
                            								}
                            								setWithLastConsumed(current, "haveRange", lv_haveRange_12_0 != null, "{");
                            							

                            }


                            }

                            // InternalSpectra.g:5145:6: ( ( (lv_from_13_0= RULE_INT ) ) | ( (otherlv_14= RULE_ID ) ) )
                            int alt96=2;
                            int LA96_0 = input.LA(1);

                            if ( (LA96_0==RULE_INT) ) {
                                alt96=1;
                            }
                            else if ( (LA96_0==RULE_ID) ) {
                                alt96=2;
                            }
                            else {
                                NoViableAltException nvae =
                                    new NoViableAltException("", 96, 0, input);

                                throw nvae;
                            }
                            switch (alt96) {
                                case 1 :
                                    // InternalSpectra.g:5146:7: ( (lv_from_13_0= RULE_INT ) )
                                    {
                                    // InternalSpectra.g:5146:7: ( (lv_from_13_0= RULE_INT ) )
                                    // InternalSpectra.g:5147:8: (lv_from_13_0= RULE_INT )
                                    {
                                    // InternalSpectra.g:5147:8: (lv_from_13_0= RULE_INT )
                                    // InternalSpectra.g:5148:9: lv_from_13_0= RULE_INT
                                    {
                                    lv_from_13_0=(Token)match(input,RULE_INT,FOLLOW_69); 

                                    									newLeafNode(lv_from_13_0, grammarAccess.getUnaryRegExpAccess().getFromINTTerminalRuleCall_1_1_5_1_0_0());
                                    								

                                    									if (current==null) {
                                    										current = createModelElement(grammarAccess.getUnaryRegExpRule());
                                    									}
                                    									setWithLastConsumed(
                                    										current,
                                    										"from",
                                    										lv_from_13_0,
                                    										"org.eclipse.xtext.common.Terminals.INT");
                                    								

                                    }


                                    }


                                    }
                                    break;
                                case 2 :
                                    // InternalSpectra.g:5165:7: ( (otherlv_14= RULE_ID ) )
                                    {
                                    // InternalSpectra.g:5165:7: ( (otherlv_14= RULE_ID ) )
                                    // InternalSpectra.g:5166:8: (otherlv_14= RULE_ID )
                                    {
                                    // InternalSpectra.g:5166:8: (otherlv_14= RULE_ID )
                                    // InternalSpectra.g:5167:9: otherlv_14= RULE_ID
                                    {

                                    									if (current==null) {
                                    										current = createModelElement(grammarAccess.getUnaryRegExpRule());
                                    									}
                                    								
                                    otherlv_14=(Token)match(input,RULE_ID,FOLLOW_69); 

                                    									newLeafNode(otherlv_14, grammarAccess.getUnaryRegExpAccess().getFromDefineDefineDeclCrossReference_1_1_5_1_1_0());
                                    								

                                    }


                                    }


                                    }
                                    break;

                            }

                            otherlv_15=(Token)match(input,24,FOLLOW_39); 

                            						newLeafNode(otherlv_15, grammarAccess.getUnaryRegExpAccess().getCommaKeyword_1_1_5_2());
                            					
                            // InternalSpectra.g:5183:6: ( ( (lv_to_16_0= RULE_INT ) ) | ( (otherlv_17= RULE_ID ) ) )
                            int alt97=2;
                            int LA97_0 = input.LA(1);

                            if ( (LA97_0==RULE_INT) ) {
                                alt97=1;
                            }
                            else if ( (LA97_0==RULE_ID) ) {
                                alt97=2;
                            }
                            else {
                                NoViableAltException nvae =
                                    new NoViableAltException("", 97, 0, input);

                                throw nvae;
                            }
                            switch (alt97) {
                                case 1 :
                                    // InternalSpectra.g:5184:7: ( (lv_to_16_0= RULE_INT ) )
                                    {
                                    // InternalSpectra.g:5184:7: ( (lv_to_16_0= RULE_INT ) )
                                    // InternalSpectra.g:5185:8: (lv_to_16_0= RULE_INT )
                                    {
                                    // InternalSpectra.g:5185:8: (lv_to_16_0= RULE_INT )
                                    // InternalSpectra.g:5186:9: lv_to_16_0= RULE_INT
                                    {
                                    lv_to_16_0=(Token)match(input,RULE_INT,FOLLOW_37); 

                                    									newLeafNode(lv_to_16_0, grammarAccess.getUnaryRegExpAccess().getToINTTerminalRuleCall_1_1_5_3_0_0());
                                    								

                                    									if (current==null) {
                                    										current = createModelElement(grammarAccess.getUnaryRegExpRule());
                                    									}
                                    									setWithLastConsumed(
                                    										current,
                                    										"to",
                                    										lv_to_16_0,
                                    										"org.eclipse.xtext.common.Terminals.INT");
                                    								

                                    }


                                    }


                                    }
                                    break;
                                case 2 :
                                    // InternalSpectra.g:5203:7: ( (otherlv_17= RULE_ID ) )
                                    {
                                    // InternalSpectra.g:5203:7: ( (otherlv_17= RULE_ID ) )
                                    // InternalSpectra.g:5204:8: (otherlv_17= RULE_ID )
                                    {
                                    // InternalSpectra.g:5204:8: (otherlv_17= RULE_ID )
                                    // InternalSpectra.g:5205:9: otherlv_17= RULE_ID
                                    {

                                    									if (current==null) {
                                    										current = createModelElement(grammarAccess.getUnaryRegExpRule());
                                    									}
                                    								
                                    otherlv_17=(Token)match(input,RULE_ID,FOLLOW_37); 

                                    									newLeafNode(otherlv_17, grammarAccess.getUnaryRegExpAccess().getToDefineDefineDeclCrossReference_1_1_5_3_1_0());
                                    								

                                    }


                                    }


                                    }
                                    break;

                            }

                            otherlv_18=(Token)match(input,25,FOLLOW_2); 

                            						newLeafNode(otherlv_18, grammarAccess.getUnaryRegExpAccess().getRightCurlyBracketKeyword_1_1_5_4());
                            					

                            }


                            }
                            break;

                    }


                    }
                    break;

            }


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleUnaryRegExp"


    // $ANTLR start "entryRuleCompRegExp"
    // InternalSpectra.g:5228:1: entryRuleCompRegExp returns [EObject current=null] : iv_ruleCompRegExp= ruleCompRegExp EOF ;
    public final EObject entryRuleCompRegExp() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleCompRegExp = null;


        try {
            // InternalSpectra.g:5228:51: (iv_ruleCompRegExp= ruleCompRegExp EOF )
            // InternalSpectra.g:5229:2: iv_ruleCompRegExp= ruleCompRegExp EOF
            {
             newCompositeNode(grammarAccess.getCompRegExpRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleCompRegExp=ruleCompRegExp();

            state._fsp--;

             current =iv_ruleCompRegExp; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleCompRegExp"


    // $ANTLR start "ruleCompRegExp"
    // InternalSpectra.g:5235:1: ruleCompRegExp returns [EObject current=null] : (this_PrimaryRegExp_0= rulePrimaryRegExp | ( () ( (lv_comp_2_0= '~' ) ) ( (lv_left_3_0= ruleCompRegExp ) ) ) ) ;
    public final EObject ruleCompRegExp() throws RecognitionException {
        EObject current = null;

        Token lv_comp_2_0=null;
        EObject this_PrimaryRegExp_0 = null;

        EObject lv_left_3_0 = null;



        	enterRule();

        try {
            // InternalSpectra.g:5241:2: ( (this_PrimaryRegExp_0= rulePrimaryRegExp | ( () ( (lv_comp_2_0= '~' ) ) ( (lv_left_3_0= ruleCompRegExp ) ) ) ) )
            // InternalSpectra.g:5242:2: (this_PrimaryRegExp_0= rulePrimaryRegExp | ( () ( (lv_comp_2_0= '~' ) ) ( (lv_left_3_0= ruleCompRegExp ) ) ) )
            {
            // InternalSpectra.g:5242:2: (this_PrimaryRegExp_0= rulePrimaryRegExp | ( () ( (lv_comp_2_0= '~' ) ) ( (lv_left_3_0= ruleCompRegExp ) ) ) )
            int alt100=2;
            int LA100_0 = input.LA(1);

            if ( (LA100_0==21||LA100_0==28||LA100_0==48||(LA100_0>=97 && LA100_0<=100)) ) {
                alt100=1;
            }
            else if ( (LA100_0==104) ) {
                alt100=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 100, 0, input);

                throw nvae;
            }
            switch (alt100) {
                case 1 :
                    // InternalSpectra.g:5243:3: this_PrimaryRegExp_0= rulePrimaryRegExp
                    {

                    			newCompositeNode(grammarAccess.getCompRegExpAccess().getPrimaryRegExpParserRuleCall_0());
                    		
                    pushFollow(FOLLOW_2);
                    this_PrimaryRegExp_0=rulePrimaryRegExp();

                    state._fsp--;


                    			current = this_PrimaryRegExp_0;
                    			afterParserOrEnumRuleCall();
                    		

                    }
                    break;
                case 2 :
                    // InternalSpectra.g:5252:3: ( () ( (lv_comp_2_0= '~' ) ) ( (lv_left_3_0= ruleCompRegExp ) ) )
                    {
                    // InternalSpectra.g:5252:3: ( () ( (lv_comp_2_0= '~' ) ) ( (lv_left_3_0= ruleCompRegExp ) ) )
                    // InternalSpectra.g:5253:4: () ( (lv_comp_2_0= '~' ) ) ( (lv_left_3_0= ruleCompRegExp ) )
                    {
                    // InternalSpectra.g:5253:4: ()
                    // InternalSpectra.g:5254:5: 
                    {

                    					current = forceCreateModelElement(
                    						grammarAccess.getCompRegExpAccess().getCompRegExpAction_1_0(),
                    						current);
                    				

                    }

                    // InternalSpectra.g:5260:4: ( (lv_comp_2_0= '~' ) )
                    // InternalSpectra.g:5261:5: (lv_comp_2_0= '~' )
                    {
                    // InternalSpectra.g:5261:5: (lv_comp_2_0= '~' )
                    // InternalSpectra.g:5262:6: lv_comp_2_0= '~'
                    {
                    lv_comp_2_0=(Token)match(input,104,FOLLOW_25); 

                    						newLeafNode(lv_comp_2_0, grammarAccess.getCompRegExpAccess().getCompTildeKeyword_1_1_0());
                    					

                    						if (current==null) {
                    							current = createModelElement(grammarAccess.getCompRegExpRule());
                    						}
                    						setWithLastConsumed(current, "comp", lv_comp_2_0, "~");
                    					

                    }


                    }

                    // InternalSpectra.g:5274:4: ( (lv_left_3_0= ruleCompRegExp ) )
                    // InternalSpectra.g:5275:5: (lv_left_3_0= ruleCompRegExp )
                    {
                    // InternalSpectra.g:5275:5: (lv_left_3_0= ruleCompRegExp )
                    // InternalSpectra.g:5276:6: lv_left_3_0= ruleCompRegExp
                    {

                    						newCompositeNode(grammarAccess.getCompRegExpAccess().getLeftCompRegExpParserRuleCall_1_2_0());
                    					
                    pushFollow(FOLLOW_2);
                    lv_left_3_0=ruleCompRegExp();

                    state._fsp--;


                    						if (current==null) {
                    							current = createModelElementForParent(grammarAccess.getCompRegExpRule());
                    						}
                    						set(
                    							current,
                    							"left",
                    							lv_left_3_0,
                    							"tau.smlab.syntech.Spectra.CompRegExp");
                    						afterParserOrEnumRuleCall();
                    					

                    }


                    }


                    }


                    }
                    break;

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleCompRegExp"


    // $ANTLR start "entryRulePrimaryRegExp"
    // InternalSpectra.g:5298:1: entryRulePrimaryRegExp returns [EObject current=null] : iv_rulePrimaryRegExp= rulePrimaryRegExp EOF ;
    public final EObject entryRulePrimaryRegExp() throws RecognitionException {
        EObject current = null;

        EObject iv_rulePrimaryRegExp = null;


        try {
            // InternalSpectra.g:5298:54: (iv_rulePrimaryRegExp= rulePrimaryRegExp EOF )
            // InternalSpectra.g:5299:2: iv_rulePrimaryRegExp= rulePrimaryRegExp EOF
            {
             newCompositeNode(grammarAccess.getPrimaryRegExpRule()); 
            pushFollow(FOLLOW_1);
            iv_rulePrimaryRegExp=rulePrimaryRegExp();

            state._fsp--;

             current =iv_rulePrimaryRegExp; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRulePrimaryRegExp"


    // $ANTLR start "rulePrimaryRegExp"
    // InternalSpectra.g:5305:1: rulePrimaryRegExp returns [EObject current=null] : ( (otherlv_0= '(' this_RegExp_1= ruleRegExp otherlv_2= ')' ) | ( ( (lv_val_3_1= 'TRUE' | lv_val_3_2= 'FALSE' | lv_val_3_3= 'true' | lv_val_3_4= 'false' ) ) ) | ( (lv_assrt_4_0= ruleBooleanTerm ) ) | ( (lv_empty_5_0= '()' ) ) ) ;
    public final EObject rulePrimaryRegExp() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_2=null;
        Token lv_val_3_1=null;
        Token lv_val_3_2=null;
        Token lv_val_3_3=null;
        Token lv_val_3_4=null;
        Token lv_empty_5_0=null;
        EObject this_RegExp_1 = null;

        EObject lv_assrt_4_0 = null;



        	enterRule();

        try {
            // InternalSpectra.g:5311:2: ( ( (otherlv_0= '(' this_RegExp_1= ruleRegExp otherlv_2= ')' ) | ( ( (lv_val_3_1= 'TRUE' | lv_val_3_2= 'FALSE' | lv_val_3_3= 'true' | lv_val_3_4= 'false' ) ) ) | ( (lv_assrt_4_0= ruleBooleanTerm ) ) | ( (lv_empty_5_0= '()' ) ) ) )
            // InternalSpectra.g:5312:2: ( (otherlv_0= '(' this_RegExp_1= ruleRegExp otherlv_2= ')' ) | ( ( (lv_val_3_1= 'TRUE' | lv_val_3_2= 'FALSE' | lv_val_3_3= 'true' | lv_val_3_4= 'false' ) ) ) | ( (lv_assrt_4_0= ruleBooleanTerm ) ) | ( (lv_empty_5_0= '()' ) ) )
            {
            // InternalSpectra.g:5312:2: ( (otherlv_0= '(' this_RegExp_1= ruleRegExp otherlv_2= ')' ) | ( ( (lv_val_3_1= 'TRUE' | lv_val_3_2= 'FALSE' | lv_val_3_3= 'true' | lv_val_3_4= 'false' ) ) ) | ( (lv_assrt_4_0= ruleBooleanTerm ) ) | ( (lv_empty_5_0= '()' ) ) )
            int alt102=4;
            switch ( input.LA(1) ) {
            case 28:
                {
                alt102=1;
                }
                break;
            case 97:
            case 98:
            case 99:
            case 100:
                {
                alt102=2;
                }
                break;
            case 21:
                {
                alt102=3;
                }
                break;
            case 48:
                {
                alt102=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 102, 0, input);

                throw nvae;
            }

            switch (alt102) {
                case 1 :
                    // InternalSpectra.g:5313:3: (otherlv_0= '(' this_RegExp_1= ruleRegExp otherlv_2= ')' )
                    {
                    // InternalSpectra.g:5313:3: (otherlv_0= '(' this_RegExp_1= ruleRegExp otherlv_2= ')' )
                    // InternalSpectra.g:5314:4: otherlv_0= '(' this_RegExp_1= ruleRegExp otherlv_2= ')'
                    {
                    otherlv_0=(Token)match(input,28,FOLLOW_25); 

                    				newLeafNode(otherlv_0, grammarAccess.getPrimaryRegExpAccess().getLeftParenthesisKeyword_0_0());
                    			

                    				newCompositeNode(grammarAccess.getPrimaryRegExpAccess().getRegExpParserRuleCall_0_1());
                    			
                    pushFollow(FOLLOW_28);
                    this_RegExp_1=ruleRegExp();

                    state._fsp--;


                    				current = this_RegExp_1;
                    				afterParserOrEnumRuleCall();
                    			
                    otherlv_2=(Token)match(input,29,FOLLOW_2); 

                    				newLeafNode(otherlv_2, grammarAccess.getPrimaryRegExpAccess().getRightParenthesisKeyword_0_2());
                    			

                    }


                    }
                    break;
                case 2 :
                    // InternalSpectra.g:5332:3: ( ( (lv_val_3_1= 'TRUE' | lv_val_3_2= 'FALSE' | lv_val_3_3= 'true' | lv_val_3_4= 'false' ) ) )
                    {
                    // InternalSpectra.g:5332:3: ( ( (lv_val_3_1= 'TRUE' | lv_val_3_2= 'FALSE' | lv_val_3_3= 'true' | lv_val_3_4= 'false' ) ) )
                    // InternalSpectra.g:5333:4: ( (lv_val_3_1= 'TRUE' | lv_val_3_2= 'FALSE' | lv_val_3_3= 'true' | lv_val_3_4= 'false' ) )
                    {
                    // InternalSpectra.g:5333:4: ( (lv_val_3_1= 'TRUE' | lv_val_3_2= 'FALSE' | lv_val_3_3= 'true' | lv_val_3_4= 'false' ) )
                    // InternalSpectra.g:5334:5: (lv_val_3_1= 'TRUE' | lv_val_3_2= 'FALSE' | lv_val_3_3= 'true' | lv_val_3_4= 'false' )
                    {
                    // InternalSpectra.g:5334:5: (lv_val_3_1= 'TRUE' | lv_val_3_2= 'FALSE' | lv_val_3_3= 'true' | lv_val_3_4= 'false' )
                    int alt101=4;
                    switch ( input.LA(1) ) {
                    case 99:
                        {
                        alt101=1;
                        }
                        break;
                    case 97:
                        {
                        alt101=2;
                        }
                        break;
                    case 100:
                        {
                        alt101=3;
                        }
                        break;
                    case 98:
                        {
                        alt101=4;
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("", 101, 0, input);

                        throw nvae;
                    }

                    switch (alt101) {
                        case 1 :
                            // InternalSpectra.g:5335:6: lv_val_3_1= 'TRUE'
                            {
                            lv_val_3_1=(Token)match(input,99,FOLLOW_2); 

                            						newLeafNode(lv_val_3_1, grammarAccess.getPrimaryRegExpAccess().getValTRUEKeyword_1_0_0());
                            					

                            						if (current==null) {
                            							current = createModelElement(grammarAccess.getPrimaryRegExpRule());
                            						}
                            						setWithLastConsumed(current, "val", lv_val_3_1, null);
                            					

                            }
                            break;
                        case 2 :
                            // InternalSpectra.g:5346:6: lv_val_3_2= 'FALSE'
                            {
                            lv_val_3_2=(Token)match(input,97,FOLLOW_2); 

                            						newLeafNode(lv_val_3_2, grammarAccess.getPrimaryRegExpAccess().getValFALSEKeyword_1_0_1());
                            					

                            						if (current==null) {
                            							current = createModelElement(grammarAccess.getPrimaryRegExpRule());
                            						}
                            						setWithLastConsumed(current, "val", lv_val_3_2, null);
                            					

                            }
                            break;
                        case 3 :
                            // InternalSpectra.g:5357:6: lv_val_3_3= 'true'
                            {
                            lv_val_3_3=(Token)match(input,100,FOLLOW_2); 

                            						newLeafNode(lv_val_3_3, grammarAccess.getPrimaryRegExpAccess().getValTrueKeyword_1_0_2());
                            					

                            						if (current==null) {
                            							current = createModelElement(grammarAccess.getPrimaryRegExpRule());
                            						}
                            						setWithLastConsumed(current, "val", lv_val_3_3, null);
                            					

                            }
                            break;
                        case 4 :
                            // InternalSpectra.g:5368:6: lv_val_3_4= 'false'
                            {
                            lv_val_3_4=(Token)match(input,98,FOLLOW_2); 

                            						newLeafNode(lv_val_3_4, grammarAccess.getPrimaryRegExpAccess().getValFalseKeyword_1_0_3());
                            					

                            						if (current==null) {
                            							current = createModelElement(grammarAccess.getPrimaryRegExpRule());
                            						}
                            						setWithLastConsumed(current, "val", lv_val_3_4, null);
                            					

                            }
                            break;

                    }


                    }


                    }


                    }
                    break;
                case 3 :
                    // InternalSpectra.g:5382:3: ( (lv_assrt_4_0= ruleBooleanTerm ) )
                    {
                    // InternalSpectra.g:5382:3: ( (lv_assrt_4_0= ruleBooleanTerm ) )
                    // InternalSpectra.g:5383:4: (lv_assrt_4_0= ruleBooleanTerm )
                    {
                    // InternalSpectra.g:5383:4: (lv_assrt_4_0= ruleBooleanTerm )
                    // InternalSpectra.g:5384:5: lv_assrt_4_0= ruleBooleanTerm
                    {

                    					newCompositeNode(grammarAccess.getPrimaryRegExpAccess().getAssrtBooleanTermParserRuleCall_2_0());
                    				
                    pushFollow(FOLLOW_2);
                    lv_assrt_4_0=ruleBooleanTerm();

                    state._fsp--;


                    					if (current==null) {
                    						current = createModelElementForParent(grammarAccess.getPrimaryRegExpRule());
                    					}
                    					set(
                    						current,
                    						"assrt",
                    						lv_assrt_4_0,
                    						"tau.smlab.syntech.Spectra.BooleanTerm");
                    					afterParserOrEnumRuleCall();
                    				

                    }


                    }


                    }
                    break;
                case 4 :
                    // InternalSpectra.g:5402:3: ( (lv_empty_5_0= '()' ) )
                    {
                    // InternalSpectra.g:5402:3: ( (lv_empty_5_0= '()' ) )
                    // InternalSpectra.g:5403:4: (lv_empty_5_0= '()' )
                    {
                    // InternalSpectra.g:5403:4: (lv_empty_5_0= '()' )
                    // InternalSpectra.g:5404:5: lv_empty_5_0= '()'
                    {
                    lv_empty_5_0=(Token)match(input,48,FOLLOW_2); 

                    					newLeafNode(lv_empty_5_0, grammarAccess.getPrimaryRegExpAccess().getEmptyLeftParenthesisRightParenthesisKeyword_3_0());
                    				

                    					if (current==null) {
                    						current = createModelElement(grammarAccess.getPrimaryRegExpRule());
                    					}
                    					setWithLastConsumed(current, "empty", lv_empty_5_0 != null, "()");
                    				

                    }


                    }


                    }
                    break;

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "rulePrimaryRegExp"


    // $ANTLR start "entryRuleBooleanTerm"
    // InternalSpectra.g:5420:1: entryRuleBooleanTerm returns [EObject current=null] : iv_ruleBooleanTerm= ruleBooleanTerm EOF ;
    public final EObject entryRuleBooleanTerm() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleBooleanTerm = null;


        try {
            // InternalSpectra.g:5420:52: (iv_ruleBooleanTerm= ruleBooleanTerm EOF )
            // InternalSpectra.g:5421:2: iv_ruleBooleanTerm= ruleBooleanTerm EOF
            {
             newCompositeNode(grammarAccess.getBooleanTermRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleBooleanTerm=ruleBooleanTerm();

            state._fsp--;

             current =iv_ruleBooleanTerm; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleBooleanTerm"


    // $ANTLR start "ruleBooleanTerm"
    // InternalSpectra.g:5427:1: ruleBooleanTerm returns [EObject current=null] : ( () otherlv_1= '[' ( (lv_relExpr_2_0= ruleTemporalInExpr ) ) otherlv_3= ']' ) ;
    public final EObject ruleBooleanTerm() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_3=null;
        EObject lv_relExpr_2_0 = null;



        	enterRule();

        try {
            // InternalSpectra.g:5433:2: ( ( () otherlv_1= '[' ( (lv_relExpr_2_0= ruleTemporalInExpr ) ) otherlv_3= ']' ) )
            // InternalSpectra.g:5434:2: ( () otherlv_1= '[' ( (lv_relExpr_2_0= ruleTemporalInExpr ) ) otherlv_3= ']' )
            {
            // InternalSpectra.g:5434:2: ( () otherlv_1= '[' ( (lv_relExpr_2_0= ruleTemporalInExpr ) ) otherlv_3= ']' )
            // InternalSpectra.g:5435:3: () otherlv_1= '[' ( (lv_relExpr_2_0= ruleTemporalInExpr ) ) otherlv_3= ']'
            {
            // InternalSpectra.g:5435:3: ()
            // InternalSpectra.g:5436:4: 
            {

            				current = forceCreateModelElement(
            					grammarAccess.getBooleanTermAccess().getBooleanTermAction_0(),
            					current);
            			

            }

            otherlv_1=(Token)match(input,21,FOLLOW_12); 

            			newLeafNode(otherlv_1, grammarAccess.getBooleanTermAccess().getLeftSquareBracketKeyword_1());
            		
            // InternalSpectra.g:5446:3: ( (lv_relExpr_2_0= ruleTemporalInExpr ) )
            // InternalSpectra.g:5447:4: (lv_relExpr_2_0= ruleTemporalInExpr )
            {
            // InternalSpectra.g:5447:4: (lv_relExpr_2_0= ruleTemporalInExpr )
            // InternalSpectra.g:5448:5: lv_relExpr_2_0= ruleTemporalInExpr
            {

            					newCompositeNode(grammarAccess.getBooleanTermAccess().getRelExprTemporalInExprParserRuleCall_2_0());
            				
            pushFollow(FOLLOW_21);
            lv_relExpr_2_0=ruleTemporalInExpr();

            state._fsp--;


            					if (current==null) {
            						current = createModelElementForParent(grammarAccess.getBooleanTermRule());
            					}
            					set(
            						current,
            						"relExpr",
            						lv_relExpr_2_0,
            						"tau.smlab.syntech.Spectra.TemporalInExpr");
            					afterParserOrEnumRuleCall();
            				

            }


            }

            otherlv_3=(Token)match(input,22,FOLLOW_2); 

            			newLeafNode(otherlv_3, grammarAccess.getBooleanTermAccess().getRightSquareBracketKeyword_3());
            		

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleBooleanTerm"


    // $ANTLR start "entryRuleValueInRange"
    // InternalSpectra.g:5473:1: entryRuleValueInRange returns [EObject current=null] : iv_ruleValueInRange= ruleValueInRange EOF ;
    public final EObject entryRuleValueInRange() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleValueInRange = null;


        try {
            // InternalSpectra.g:5473:53: (iv_ruleValueInRange= ruleValueInRange EOF )
            // InternalSpectra.g:5474:2: iv_ruleValueInRange= ruleValueInRange EOF
            {
             newCompositeNode(grammarAccess.getValueInRangeRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleValueInRange=ruleValueInRange();

            state._fsp--;

             current =iv_ruleValueInRange; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleValueInRange"


    // $ANTLR start "ruleValueInRange"
    // InternalSpectra.g:5480:1: ruleValueInRange returns [EObject current=null] : ( ( (otherlv_0= RULE_ID ) ) | ( (lv_int_1_0= RULE_INT ) ) | ( ( (lv_from_2_0= RULE_INT ) ) ( (lv_multi_3_0= '-' ) ) ( (lv_to_4_0= RULE_INT ) ) ) | ( ( (lv_booleanValue_5_1= 'TRUE' | lv_booleanValue_5_2= 'FALSE' | lv_booleanValue_5_3= 'true' | lv_booleanValue_5_4= 'false' ) ) ) ) ;
    public final EObject ruleValueInRange() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_int_1_0=null;
        Token lv_from_2_0=null;
        Token lv_multi_3_0=null;
        Token lv_to_4_0=null;
        Token lv_booleanValue_5_1=null;
        Token lv_booleanValue_5_2=null;
        Token lv_booleanValue_5_3=null;
        Token lv_booleanValue_5_4=null;


        	enterRule();

        try {
            // InternalSpectra.g:5486:2: ( ( ( (otherlv_0= RULE_ID ) ) | ( (lv_int_1_0= RULE_INT ) ) | ( ( (lv_from_2_0= RULE_INT ) ) ( (lv_multi_3_0= '-' ) ) ( (lv_to_4_0= RULE_INT ) ) ) | ( ( (lv_booleanValue_5_1= 'TRUE' | lv_booleanValue_5_2= 'FALSE' | lv_booleanValue_5_3= 'true' | lv_booleanValue_5_4= 'false' ) ) ) ) )
            // InternalSpectra.g:5487:2: ( ( (otherlv_0= RULE_ID ) ) | ( (lv_int_1_0= RULE_INT ) ) | ( ( (lv_from_2_0= RULE_INT ) ) ( (lv_multi_3_0= '-' ) ) ( (lv_to_4_0= RULE_INT ) ) ) | ( ( (lv_booleanValue_5_1= 'TRUE' | lv_booleanValue_5_2= 'FALSE' | lv_booleanValue_5_3= 'true' | lv_booleanValue_5_4= 'false' ) ) ) )
            {
            // InternalSpectra.g:5487:2: ( ( (otherlv_0= RULE_ID ) ) | ( (lv_int_1_0= RULE_INT ) ) | ( ( (lv_from_2_0= RULE_INT ) ) ( (lv_multi_3_0= '-' ) ) ( (lv_to_4_0= RULE_INT ) ) ) | ( ( (lv_booleanValue_5_1= 'TRUE' | lv_booleanValue_5_2= 'FALSE' | lv_booleanValue_5_3= 'true' | lv_booleanValue_5_4= 'false' ) ) ) )
            int alt104=4;
            switch ( input.LA(1) ) {
            case RULE_ID:
                {
                alt104=1;
                }
                break;
            case RULE_INT:
                {
                int LA104_2 = input.LA(2);

                if ( (LA104_2==EOF||(LA104_2>=24 && LA104_2<=25)) ) {
                    alt104=2;
                }
                else if ( (LA104_2==16) ) {
                    alt104=3;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 104, 2, input);

                    throw nvae;
                }
                }
                break;
            case 97:
            case 98:
            case 99:
            case 100:
                {
                alt104=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 104, 0, input);

                throw nvae;
            }

            switch (alt104) {
                case 1 :
                    // InternalSpectra.g:5488:3: ( (otherlv_0= RULE_ID ) )
                    {
                    // InternalSpectra.g:5488:3: ( (otherlv_0= RULE_ID ) )
                    // InternalSpectra.g:5489:4: (otherlv_0= RULE_ID )
                    {
                    // InternalSpectra.g:5489:4: (otherlv_0= RULE_ID )
                    // InternalSpectra.g:5490:5: otherlv_0= RULE_ID
                    {

                    					if (current==null) {
                    						current = createModelElement(grammarAccess.getValueInRangeRule());
                    					}
                    				
                    otherlv_0=(Token)match(input,RULE_ID,FOLLOW_2); 

                    					newLeafNode(otherlv_0, grammarAccess.getValueInRangeAccess().getConstTypeConstantCrossReference_0_0());
                    				

                    }


                    }


                    }
                    break;
                case 2 :
                    // InternalSpectra.g:5502:3: ( (lv_int_1_0= RULE_INT ) )
                    {
                    // InternalSpectra.g:5502:3: ( (lv_int_1_0= RULE_INT ) )
                    // InternalSpectra.g:5503:4: (lv_int_1_0= RULE_INT )
                    {
                    // InternalSpectra.g:5503:4: (lv_int_1_0= RULE_INT )
                    // InternalSpectra.g:5504:5: lv_int_1_0= RULE_INT
                    {
                    lv_int_1_0=(Token)match(input,RULE_INT,FOLLOW_2); 

                    					newLeafNode(lv_int_1_0, grammarAccess.getValueInRangeAccess().getIntINTTerminalRuleCall_1_0());
                    				

                    					if (current==null) {
                    						current = createModelElement(grammarAccess.getValueInRangeRule());
                    					}
                    					setWithLastConsumed(
                    						current,
                    						"int",
                    						lv_int_1_0,
                    						"org.eclipse.xtext.common.Terminals.INT");
                    				

                    }


                    }


                    }
                    break;
                case 3 :
                    // InternalSpectra.g:5521:3: ( ( (lv_from_2_0= RULE_INT ) ) ( (lv_multi_3_0= '-' ) ) ( (lv_to_4_0= RULE_INT ) ) )
                    {
                    // InternalSpectra.g:5521:3: ( ( (lv_from_2_0= RULE_INT ) ) ( (lv_multi_3_0= '-' ) ) ( (lv_to_4_0= RULE_INT ) ) )
                    // InternalSpectra.g:5522:4: ( (lv_from_2_0= RULE_INT ) ) ( (lv_multi_3_0= '-' ) ) ( (lv_to_4_0= RULE_INT ) )
                    {
                    // InternalSpectra.g:5522:4: ( (lv_from_2_0= RULE_INT ) )
                    // InternalSpectra.g:5523:5: (lv_from_2_0= RULE_INT )
                    {
                    // InternalSpectra.g:5523:5: (lv_from_2_0= RULE_INT )
                    // InternalSpectra.g:5524:6: lv_from_2_0= RULE_INT
                    {
                    lv_from_2_0=(Token)match(input,RULE_INT,FOLLOW_70); 

                    						newLeafNode(lv_from_2_0, grammarAccess.getValueInRangeAccess().getFromINTTerminalRuleCall_2_0_0());
                    					

                    						if (current==null) {
                    							current = createModelElement(grammarAccess.getValueInRangeRule());
                    						}
                    						setWithLastConsumed(
                    							current,
                    							"from",
                    							lv_from_2_0,
                    							"org.eclipse.xtext.common.Terminals.INT");
                    					

                    }


                    }

                    // InternalSpectra.g:5540:4: ( (lv_multi_3_0= '-' ) )
                    // InternalSpectra.g:5541:5: (lv_multi_3_0= '-' )
                    {
                    // InternalSpectra.g:5541:5: (lv_multi_3_0= '-' )
                    // InternalSpectra.g:5542:6: lv_multi_3_0= '-'
                    {
                    lv_multi_3_0=(Token)match(input,16,FOLLOW_11); 

                    						newLeafNode(lv_multi_3_0, grammarAccess.getValueInRangeAccess().getMultiHyphenMinusKeyword_2_1_0());
                    					

                    						if (current==null) {
                    							current = createModelElement(grammarAccess.getValueInRangeRule());
                    						}
                    						setWithLastConsumed(current, "multi", lv_multi_3_0 != null, "-");
                    					

                    }


                    }

                    // InternalSpectra.g:5554:4: ( (lv_to_4_0= RULE_INT ) )
                    // InternalSpectra.g:5555:5: (lv_to_4_0= RULE_INT )
                    {
                    // InternalSpectra.g:5555:5: (lv_to_4_0= RULE_INT )
                    // InternalSpectra.g:5556:6: lv_to_4_0= RULE_INT
                    {
                    lv_to_4_0=(Token)match(input,RULE_INT,FOLLOW_2); 

                    						newLeafNode(lv_to_4_0, grammarAccess.getValueInRangeAccess().getToINTTerminalRuleCall_2_2_0());
                    					

                    						if (current==null) {
                    							current = createModelElement(grammarAccess.getValueInRangeRule());
                    						}
                    						setWithLastConsumed(
                    							current,
                    							"to",
                    							lv_to_4_0,
                    							"org.eclipse.xtext.common.Terminals.INT");
                    					

                    }


                    }


                    }


                    }
                    break;
                case 4 :
                    // InternalSpectra.g:5574:3: ( ( (lv_booleanValue_5_1= 'TRUE' | lv_booleanValue_5_2= 'FALSE' | lv_booleanValue_5_3= 'true' | lv_booleanValue_5_4= 'false' ) ) )
                    {
                    // InternalSpectra.g:5574:3: ( ( (lv_booleanValue_5_1= 'TRUE' | lv_booleanValue_5_2= 'FALSE' | lv_booleanValue_5_3= 'true' | lv_booleanValue_5_4= 'false' ) ) )
                    // InternalSpectra.g:5575:4: ( (lv_booleanValue_5_1= 'TRUE' | lv_booleanValue_5_2= 'FALSE' | lv_booleanValue_5_3= 'true' | lv_booleanValue_5_4= 'false' ) )
                    {
                    // InternalSpectra.g:5575:4: ( (lv_booleanValue_5_1= 'TRUE' | lv_booleanValue_5_2= 'FALSE' | lv_booleanValue_5_3= 'true' | lv_booleanValue_5_4= 'false' ) )
                    // InternalSpectra.g:5576:5: (lv_booleanValue_5_1= 'TRUE' | lv_booleanValue_5_2= 'FALSE' | lv_booleanValue_5_3= 'true' | lv_booleanValue_5_4= 'false' )
                    {
                    // InternalSpectra.g:5576:5: (lv_booleanValue_5_1= 'TRUE' | lv_booleanValue_5_2= 'FALSE' | lv_booleanValue_5_3= 'true' | lv_booleanValue_5_4= 'false' )
                    int alt103=4;
                    switch ( input.LA(1) ) {
                    case 99:
                        {
                        alt103=1;
                        }
                        break;
                    case 97:
                        {
                        alt103=2;
                        }
                        break;
                    case 100:
                        {
                        alt103=3;
                        }
                        break;
                    case 98:
                        {
                        alt103=4;
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("", 103, 0, input);

                        throw nvae;
                    }

                    switch (alt103) {
                        case 1 :
                            // InternalSpectra.g:5577:6: lv_booleanValue_5_1= 'TRUE'
                            {
                            lv_booleanValue_5_1=(Token)match(input,99,FOLLOW_2); 

                            						newLeafNode(lv_booleanValue_5_1, grammarAccess.getValueInRangeAccess().getBooleanValueTRUEKeyword_3_0_0());
                            					

                            						if (current==null) {
                            							current = createModelElement(grammarAccess.getValueInRangeRule());
                            						}
                            						setWithLastConsumed(current, "booleanValue", lv_booleanValue_5_1, null);
                            					

                            }
                            break;
                        case 2 :
                            // InternalSpectra.g:5588:6: lv_booleanValue_5_2= 'FALSE'
                            {
                            lv_booleanValue_5_2=(Token)match(input,97,FOLLOW_2); 

                            						newLeafNode(lv_booleanValue_5_2, grammarAccess.getValueInRangeAccess().getBooleanValueFALSEKeyword_3_0_1());
                            					

                            						if (current==null) {
                            							current = createModelElement(grammarAccess.getValueInRangeRule());
                            						}
                            						setWithLastConsumed(current, "booleanValue", lv_booleanValue_5_2, null);
                            					

                            }
                            break;
                        case 3 :
                            // InternalSpectra.g:5599:6: lv_booleanValue_5_3= 'true'
                            {
                            lv_booleanValue_5_3=(Token)match(input,100,FOLLOW_2); 

                            						newLeafNode(lv_booleanValue_5_3, grammarAccess.getValueInRangeAccess().getBooleanValueTrueKeyword_3_0_2());
                            					

                            						if (current==null) {
                            							current = createModelElement(grammarAccess.getValueInRangeRule());
                            						}
                            						setWithLastConsumed(current, "booleanValue", lv_booleanValue_5_3, null);
                            					

                            }
                            break;
                        case 4 :
                            // InternalSpectra.g:5610:6: lv_booleanValue_5_4= 'false'
                            {
                            lv_booleanValue_5_4=(Token)match(input,98,FOLLOW_2); 

                            						newLeafNode(lv_booleanValue_5_4, grammarAccess.getValueInRangeAccess().getBooleanValueFalseKeyword_3_0_3());
                            					

                            						if (current==null) {
                            							current = createModelElement(grammarAccess.getValueInRangeRule());
                            						}
                            						setWithLastConsumed(current, "booleanValue", lv_booleanValue_5_4, null);
                            					

                            }
                            break;

                    }


                    }


                    }


                    }
                    break;

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleValueInRange"


    // $ANTLR start "entryRuleQuantifierExpr"
    // InternalSpectra.g:5627:1: entryRuleQuantifierExpr returns [EObject current=null] : iv_ruleQuantifierExpr= ruleQuantifierExpr EOF ;
    public final EObject entryRuleQuantifierExpr() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleQuantifierExpr = null;


        try {
            // InternalSpectra.g:5627:55: (iv_ruleQuantifierExpr= ruleQuantifierExpr EOF )
            // InternalSpectra.g:5628:2: iv_ruleQuantifierExpr= ruleQuantifierExpr EOF
            {
             newCompositeNode(grammarAccess.getQuantifierExprRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleQuantifierExpr=ruleQuantifierExpr();

            state._fsp--;

             current =iv_ruleQuantifierExpr; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleQuantifierExpr"


    // $ANTLR start "ruleQuantifierExpr"
    // InternalSpectra.g:5634:1: ruleQuantifierExpr returns [EObject current=null] : (this_TemporalInExpr_0= ruleTemporalInExpr | ( () ( ( ( (lv_operator_2_1= 'forall' | lv_operator_2_2= 'exists' ) ) ) ( (lv_domainVar_3_0= ruleDomainVarDecl ) ) otherlv_4= '.' ( (lv_temporalExpr_5_0= ruleQuantifierExpr ) ) ) ) ) ;
    public final EObject ruleQuantifierExpr() throws RecognitionException {
        EObject current = null;

        Token lv_operator_2_1=null;
        Token lv_operator_2_2=null;
        Token otherlv_4=null;
        EObject this_TemporalInExpr_0 = null;

        EObject lv_domainVar_3_0 = null;

        EObject lv_temporalExpr_5_0 = null;



        	enterRule();

        try {
            // InternalSpectra.g:5640:2: ( (this_TemporalInExpr_0= ruleTemporalInExpr | ( () ( ( ( (lv_operator_2_1= 'forall' | lv_operator_2_2= 'exists' ) ) ) ( (lv_domainVar_3_0= ruleDomainVarDecl ) ) otherlv_4= '.' ( (lv_temporalExpr_5_0= ruleQuantifierExpr ) ) ) ) ) )
            // InternalSpectra.g:5641:2: (this_TemporalInExpr_0= ruleTemporalInExpr | ( () ( ( ( (lv_operator_2_1= 'forall' | lv_operator_2_2= 'exists' ) ) ) ( (lv_domainVar_3_0= ruleDomainVarDecl ) ) otherlv_4= '.' ( (lv_temporalExpr_5_0= ruleQuantifierExpr ) ) ) ) )
            {
            // InternalSpectra.g:5641:2: (this_TemporalInExpr_0= ruleTemporalInExpr | ( () ( ( ( (lv_operator_2_1= 'forall' | lv_operator_2_2= 'exists' ) ) ) ( (lv_domainVar_3_0= ruleDomainVarDecl ) ) otherlv_4= '.' ( (lv_temporalExpr_5_0= ruleQuantifierExpr ) ) ) ) )
            int alt106=2;
            int LA106_0 = input.LA(1);

            if ( (LA106_0==RULE_ID||LA106_0==RULE_INT||LA106_0==16||LA106_0==26||LA106_0==28||(LA106_0>=82 && LA106_0<=89)||(LA106_0>=97 && LA106_0<=100)) ) {
                alt106=1;
            }
            else if ( ((LA106_0>=105 && LA106_0<=106)) ) {
                alt106=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 106, 0, input);

                throw nvae;
            }
            switch (alt106) {
                case 1 :
                    // InternalSpectra.g:5642:3: this_TemporalInExpr_0= ruleTemporalInExpr
                    {

                    			newCompositeNode(grammarAccess.getQuantifierExprAccess().getTemporalInExprParserRuleCall_0());
                    		
                    pushFollow(FOLLOW_2);
                    this_TemporalInExpr_0=ruleTemporalInExpr();

                    state._fsp--;


                    			current = this_TemporalInExpr_0;
                    			afterParserOrEnumRuleCall();
                    		

                    }
                    break;
                case 2 :
                    // InternalSpectra.g:5651:3: ( () ( ( ( (lv_operator_2_1= 'forall' | lv_operator_2_2= 'exists' ) ) ) ( (lv_domainVar_3_0= ruleDomainVarDecl ) ) otherlv_4= '.' ( (lv_temporalExpr_5_0= ruleQuantifierExpr ) ) ) )
                    {
                    // InternalSpectra.g:5651:3: ( () ( ( ( (lv_operator_2_1= 'forall' | lv_operator_2_2= 'exists' ) ) ) ( (lv_domainVar_3_0= ruleDomainVarDecl ) ) otherlv_4= '.' ( (lv_temporalExpr_5_0= ruleQuantifierExpr ) ) ) )
                    // InternalSpectra.g:5652:4: () ( ( ( (lv_operator_2_1= 'forall' | lv_operator_2_2= 'exists' ) ) ) ( (lv_domainVar_3_0= ruleDomainVarDecl ) ) otherlv_4= '.' ( (lv_temporalExpr_5_0= ruleQuantifierExpr ) ) )
                    {
                    // InternalSpectra.g:5652:4: ()
                    // InternalSpectra.g:5653:5: 
                    {

                    					current = forceCreateModelElement(
                    						grammarAccess.getQuantifierExprAccess().getQuantifierExprAction_1_0(),
                    						current);
                    				

                    }

                    // InternalSpectra.g:5659:4: ( ( ( (lv_operator_2_1= 'forall' | lv_operator_2_2= 'exists' ) ) ) ( (lv_domainVar_3_0= ruleDomainVarDecl ) ) otherlv_4= '.' ( (lv_temporalExpr_5_0= ruleQuantifierExpr ) ) )
                    // InternalSpectra.g:5660:5: ( ( (lv_operator_2_1= 'forall' | lv_operator_2_2= 'exists' ) ) ) ( (lv_domainVar_3_0= ruleDomainVarDecl ) ) otherlv_4= '.' ( (lv_temporalExpr_5_0= ruleQuantifierExpr ) )
                    {
                    // InternalSpectra.g:5660:5: ( ( (lv_operator_2_1= 'forall' | lv_operator_2_2= 'exists' ) ) )
                    // InternalSpectra.g:5661:6: ( (lv_operator_2_1= 'forall' | lv_operator_2_2= 'exists' ) )
                    {
                    // InternalSpectra.g:5661:6: ( (lv_operator_2_1= 'forall' | lv_operator_2_2= 'exists' ) )
                    // InternalSpectra.g:5662:7: (lv_operator_2_1= 'forall' | lv_operator_2_2= 'exists' )
                    {
                    // InternalSpectra.g:5662:7: (lv_operator_2_1= 'forall' | lv_operator_2_2= 'exists' )
                    int alt105=2;
                    int LA105_0 = input.LA(1);

                    if ( (LA105_0==105) ) {
                        alt105=1;
                    }
                    else if ( (LA105_0==106) ) {
                        alt105=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 105, 0, input);

                        throw nvae;
                    }
                    switch (alt105) {
                        case 1 :
                            // InternalSpectra.g:5663:8: lv_operator_2_1= 'forall'
                            {
                            lv_operator_2_1=(Token)match(input,105,FOLLOW_4); 

                            								newLeafNode(lv_operator_2_1, grammarAccess.getQuantifierExprAccess().getOperatorForallKeyword_1_1_0_0_0());
                            							

                            								if (current==null) {
                            									current = createModelElement(grammarAccess.getQuantifierExprRule());
                            								}
                            								setWithLastConsumed(current, "operator", lv_operator_2_1, null);
                            							

                            }
                            break;
                        case 2 :
                            // InternalSpectra.g:5674:8: lv_operator_2_2= 'exists'
                            {
                            lv_operator_2_2=(Token)match(input,106,FOLLOW_4); 

                            								newLeafNode(lv_operator_2_2, grammarAccess.getQuantifierExprAccess().getOperatorExistsKeyword_1_1_0_0_1());
                            							

                            								if (current==null) {
                            									current = createModelElement(grammarAccess.getQuantifierExprRule());
                            								}
                            								setWithLastConsumed(current, "operator", lv_operator_2_2, null);
                            							

                            }
                            break;

                    }


                    }


                    }

                    // InternalSpectra.g:5687:5: ( (lv_domainVar_3_0= ruleDomainVarDecl ) )
                    // InternalSpectra.g:5688:6: (lv_domainVar_3_0= ruleDomainVarDecl )
                    {
                    // InternalSpectra.g:5688:6: (lv_domainVar_3_0= ruleDomainVarDecl )
                    // InternalSpectra.g:5689:7: lv_domainVar_3_0= ruleDomainVarDecl
                    {

                    							newCompositeNode(grammarAccess.getQuantifierExprAccess().getDomainVarDomainVarDeclParserRuleCall_1_1_1_0());
                    						
                    pushFollow(FOLLOW_71);
                    lv_domainVar_3_0=ruleDomainVarDecl();

                    state._fsp--;


                    							if (current==null) {
                    								current = createModelElementForParent(grammarAccess.getQuantifierExprRule());
                    							}
                    							set(
                    								current,
                    								"domainVar",
                    								lv_domainVar_3_0,
                    								"tau.smlab.syntech.Spectra.DomainVarDecl");
                    							afterParserOrEnumRuleCall();
                    						

                    }


                    }

                    otherlv_4=(Token)match(input,107,FOLLOW_18); 

                    					newLeafNode(otherlv_4, grammarAccess.getQuantifierExprAccess().getFullStopKeyword_1_1_2());
                    				
                    // InternalSpectra.g:5710:5: ( (lv_temporalExpr_5_0= ruleQuantifierExpr ) )
                    // InternalSpectra.g:5711:6: (lv_temporalExpr_5_0= ruleQuantifierExpr )
                    {
                    // InternalSpectra.g:5711:6: (lv_temporalExpr_5_0= ruleQuantifierExpr )
                    // InternalSpectra.g:5712:7: lv_temporalExpr_5_0= ruleQuantifierExpr
                    {

                    							newCompositeNode(grammarAccess.getQuantifierExprAccess().getTemporalExprQuantifierExprParserRuleCall_1_1_3_0());
                    						
                    pushFollow(FOLLOW_2);
                    lv_temporalExpr_5_0=ruleQuantifierExpr();

                    state._fsp--;


                    							if (current==null) {
                    								current = createModelElementForParent(grammarAccess.getQuantifierExprRule());
                    							}
                    							set(
                    								current,
                    								"temporalExpr",
                    								lv_temporalExpr_5_0,
                    								"tau.smlab.syntech.Spectra.QuantifierExpr");
                    							afterParserOrEnumRuleCall();
                    						

                    }


                    }


                    }


                    }


                    }
                    break;

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleQuantifierExpr"


    // $ANTLR start "entryRuleDomainVarDecl"
    // InternalSpectra.g:5735:1: entryRuleDomainVarDecl returns [EObject current=null] : iv_ruleDomainVarDecl= ruleDomainVarDecl EOF ;
    public final EObject entryRuleDomainVarDecl() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDomainVarDecl = null;


        try {
            // InternalSpectra.g:5735:54: (iv_ruleDomainVarDecl= ruleDomainVarDecl EOF )
            // InternalSpectra.g:5736:2: iv_ruleDomainVarDecl= ruleDomainVarDecl EOF
            {
             newCompositeNode(grammarAccess.getDomainVarDeclRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleDomainVarDecl=ruleDomainVarDecl();

            state._fsp--;

             current =iv_ruleDomainVarDecl; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleDomainVarDecl"


    // $ANTLR start "ruleDomainVarDecl"
    // InternalSpectra.g:5742:1: ruleDomainVarDecl returns [EObject current=null] : ( ( (lv_name_0_0= RULE_ID ) ) otherlv_1= 'in' ( (lv_domainType_2_0= ruleVarType ) ) ) ;
    public final EObject ruleDomainVarDecl() throws RecognitionException {
        EObject current = null;

        Token lv_name_0_0=null;
        Token otherlv_1=null;
        EObject lv_domainType_2_0 = null;



        	enterRule();

        try {
            // InternalSpectra.g:5748:2: ( ( ( (lv_name_0_0= RULE_ID ) ) otherlv_1= 'in' ( (lv_domainType_2_0= ruleVarType ) ) ) )
            // InternalSpectra.g:5749:2: ( ( (lv_name_0_0= RULE_ID ) ) otherlv_1= 'in' ( (lv_domainType_2_0= ruleVarType ) ) )
            {
            // InternalSpectra.g:5749:2: ( ( (lv_name_0_0= RULE_ID ) ) otherlv_1= 'in' ( (lv_domainType_2_0= ruleVarType ) ) )
            // InternalSpectra.g:5750:3: ( (lv_name_0_0= RULE_ID ) ) otherlv_1= 'in' ( (lv_domainType_2_0= ruleVarType ) )
            {
            // InternalSpectra.g:5750:3: ( (lv_name_0_0= RULE_ID ) )
            // InternalSpectra.g:5751:4: (lv_name_0_0= RULE_ID )
            {
            // InternalSpectra.g:5751:4: (lv_name_0_0= RULE_ID )
            // InternalSpectra.g:5752:5: lv_name_0_0= RULE_ID
            {
            lv_name_0_0=(Token)match(input,RULE_ID,FOLLOW_45); 

            					newLeafNode(lv_name_0_0, grammarAccess.getDomainVarDeclAccess().getNameIDTerminalRuleCall_0_0());
            				

            					if (current==null) {
            						current = createModelElement(grammarAccess.getDomainVarDeclRule());
            					}
            					setWithLastConsumed(
            						current,
            						"name",
            						lv_name_0_0,
            						"org.eclipse.xtext.common.Terminals.ID");
            				

            }


            }

            otherlv_1=(Token)match(input,58,FOLLOW_14); 

            			newLeafNode(otherlv_1, grammarAccess.getDomainVarDeclAccess().getInKeyword_1());
            		
            // InternalSpectra.g:5772:3: ( (lv_domainType_2_0= ruleVarType ) )
            // InternalSpectra.g:5773:4: (lv_domainType_2_0= ruleVarType )
            {
            // InternalSpectra.g:5773:4: (lv_domainType_2_0= ruleVarType )
            // InternalSpectra.g:5774:5: lv_domainType_2_0= ruleVarType
            {

            					newCompositeNode(grammarAccess.getDomainVarDeclAccess().getDomainTypeVarTypeParserRuleCall_2_0());
            				
            pushFollow(FOLLOW_2);
            lv_domainType_2_0=ruleVarType();

            state._fsp--;


            					if (current==null) {
            						current = createModelElementForParent(grammarAccess.getDomainVarDeclRule());
            					}
            					set(
            						current,
            						"domainType",
            						lv_domainType_2_0,
            						"tau.smlab.syntech.Spectra.VarType");
            					afterParserOrEnumRuleCall();
            				

            }


            }


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleDomainVarDecl"


    // $ANTLR start "entryRuleSizeDefineDecl"
    // InternalSpectra.g:5795:1: entryRuleSizeDefineDecl returns [EObject current=null] : iv_ruleSizeDefineDecl= ruleSizeDefineDecl EOF ;
    public final EObject entryRuleSizeDefineDecl() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleSizeDefineDecl = null;


        try {
            // InternalSpectra.g:5795:55: (iv_ruleSizeDefineDecl= ruleSizeDefineDecl EOF )
            // InternalSpectra.g:5796:2: iv_ruleSizeDefineDecl= ruleSizeDefineDecl EOF
            {
             newCompositeNode(grammarAccess.getSizeDefineDeclRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleSizeDefineDecl=ruleSizeDefineDecl();

            state._fsp--;

             current =iv_ruleSizeDefineDecl; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleSizeDefineDecl"


    // $ANTLR start "ruleSizeDefineDecl"
    // InternalSpectra.g:5802:1: ruleSizeDefineDecl returns [EObject current=null] : ( ( (lv_value_0_0= RULE_INT ) ) | ( (otherlv_1= RULE_ID ) ) | (otherlv_2= '(' ( (lv_arithExp_3_0= ruleTemporalExpression ) ) otherlv_4= ')' ) ) ;
    public final EObject ruleSizeDefineDecl() throws RecognitionException {
        EObject current = null;

        Token lv_value_0_0=null;
        Token otherlv_1=null;
        Token otherlv_2=null;
        Token otherlv_4=null;
        EObject lv_arithExp_3_0 = null;



        	enterRule();

        try {
            // InternalSpectra.g:5808:2: ( ( ( (lv_value_0_0= RULE_INT ) ) | ( (otherlv_1= RULE_ID ) ) | (otherlv_2= '(' ( (lv_arithExp_3_0= ruleTemporalExpression ) ) otherlv_4= ')' ) ) )
            // InternalSpectra.g:5809:2: ( ( (lv_value_0_0= RULE_INT ) ) | ( (otherlv_1= RULE_ID ) ) | (otherlv_2= '(' ( (lv_arithExp_3_0= ruleTemporalExpression ) ) otherlv_4= ')' ) )
            {
            // InternalSpectra.g:5809:2: ( ( (lv_value_0_0= RULE_INT ) ) | ( (otherlv_1= RULE_ID ) ) | (otherlv_2= '(' ( (lv_arithExp_3_0= ruleTemporalExpression ) ) otherlv_4= ')' ) )
            int alt107=3;
            switch ( input.LA(1) ) {
            case RULE_INT:
                {
                alt107=1;
                }
                break;
            case RULE_ID:
                {
                alt107=2;
                }
                break;
            case 28:
                {
                alt107=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 107, 0, input);

                throw nvae;
            }

            switch (alt107) {
                case 1 :
                    // InternalSpectra.g:5810:3: ( (lv_value_0_0= RULE_INT ) )
                    {
                    // InternalSpectra.g:5810:3: ( (lv_value_0_0= RULE_INT ) )
                    // InternalSpectra.g:5811:4: (lv_value_0_0= RULE_INT )
                    {
                    // InternalSpectra.g:5811:4: (lv_value_0_0= RULE_INT )
                    // InternalSpectra.g:5812:5: lv_value_0_0= RULE_INT
                    {
                    lv_value_0_0=(Token)match(input,RULE_INT,FOLLOW_2); 

                    					newLeafNode(lv_value_0_0, grammarAccess.getSizeDefineDeclAccess().getValueINTTerminalRuleCall_0_0());
                    				

                    					if (current==null) {
                    						current = createModelElement(grammarAccess.getSizeDefineDeclRule());
                    					}
                    					setWithLastConsumed(
                    						current,
                    						"value",
                    						lv_value_0_0,
                    						"org.eclipse.xtext.common.Terminals.INT");
                    				

                    }


                    }


                    }
                    break;
                case 2 :
                    // InternalSpectra.g:5829:3: ( (otherlv_1= RULE_ID ) )
                    {
                    // InternalSpectra.g:5829:3: ( (otherlv_1= RULE_ID ) )
                    // InternalSpectra.g:5830:4: (otherlv_1= RULE_ID )
                    {
                    // InternalSpectra.g:5830:4: (otherlv_1= RULE_ID )
                    // InternalSpectra.g:5831:5: otherlv_1= RULE_ID
                    {

                    					if (current==null) {
                    						current = createModelElement(grammarAccess.getSizeDefineDeclRule());
                    					}
                    				
                    otherlv_1=(Token)match(input,RULE_ID,FOLLOW_2); 

                    					newLeafNode(otherlv_1, grammarAccess.getSizeDefineDeclAccess().getNameDefineDeclCrossReference_1_0());
                    				

                    }


                    }


                    }
                    break;
                case 3 :
                    // InternalSpectra.g:5843:3: (otherlv_2= '(' ( (lv_arithExp_3_0= ruleTemporalExpression ) ) otherlv_4= ')' )
                    {
                    // InternalSpectra.g:5843:3: (otherlv_2= '(' ( (lv_arithExp_3_0= ruleTemporalExpression ) ) otherlv_4= ')' )
                    // InternalSpectra.g:5844:4: otherlv_2= '(' ( (lv_arithExp_3_0= ruleTemporalExpression ) ) otherlv_4= ')'
                    {
                    otherlv_2=(Token)match(input,28,FOLLOW_18); 

                    				newLeafNode(otherlv_2, grammarAccess.getSizeDefineDeclAccess().getLeftParenthesisKeyword_2_0());
                    			
                    // InternalSpectra.g:5848:4: ( (lv_arithExp_3_0= ruleTemporalExpression ) )
                    // InternalSpectra.g:5849:5: (lv_arithExp_3_0= ruleTemporalExpression )
                    {
                    // InternalSpectra.g:5849:5: (lv_arithExp_3_0= ruleTemporalExpression )
                    // InternalSpectra.g:5850:6: lv_arithExp_3_0= ruleTemporalExpression
                    {

                    						newCompositeNode(grammarAccess.getSizeDefineDeclAccess().getArithExpTemporalExpressionParserRuleCall_2_1_0());
                    					
                    pushFollow(FOLLOW_28);
                    lv_arithExp_3_0=ruleTemporalExpression();

                    state._fsp--;


                    						if (current==null) {
                    							current = createModelElementForParent(grammarAccess.getSizeDefineDeclRule());
                    						}
                    						set(
                    							current,
                    							"arithExp",
                    							lv_arithExp_3_0,
                    							"tau.smlab.syntech.Spectra.TemporalExpression");
                    						afterParserOrEnumRuleCall();
                    					

                    }


                    }

                    otherlv_4=(Token)match(input,29,FOLLOW_2); 

                    				newLeafNode(otherlv_4, grammarAccess.getSizeDefineDeclAccess().getRightParenthesisKeyword_2_2());
                    			

                    }


                    }
                    break;

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleSizeDefineDecl"


    // $ANTLR start "entryRuleTOK_SEMI"
    // InternalSpectra.g:5876:1: entryRuleTOK_SEMI returns [String current=null] : iv_ruleTOK_SEMI= ruleTOK_SEMI EOF ;
    public final String entryRuleTOK_SEMI() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleTOK_SEMI = null;


        try {
            // InternalSpectra.g:5876:48: (iv_ruleTOK_SEMI= ruleTOK_SEMI EOF )
            // InternalSpectra.g:5877:2: iv_ruleTOK_SEMI= ruleTOK_SEMI EOF
            {
             newCompositeNode(grammarAccess.getTOK_SEMIRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleTOK_SEMI=ruleTOK_SEMI();

            state._fsp--;

             current =iv_ruleTOK_SEMI.getText(); 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleTOK_SEMI"


    // $ANTLR start "ruleTOK_SEMI"
    // InternalSpectra.g:5883:1: ruleTOK_SEMI returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : kw= ';' ;
    public final AntlrDatatypeRuleToken ruleTOK_SEMI() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;


        	enterRule();

        try {
            // InternalSpectra.g:5889:2: (kw= ';' )
            // InternalSpectra.g:5890:2: kw= ';'
            {
            kw=(Token)match(input,108,FOLLOW_2); 

            		current.merge(kw);
            		newLeafNode(kw, grammarAccess.getTOK_SEMIAccess().getSemicolonKeyword());
            	

            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleTOK_SEMI"


    // $ANTLR start "ruleVarOwner"
    // InternalSpectra.g:5898:1: ruleVarOwner returns [Enumerator current=null] : ( (enumLiteral_0= 'output' ) | (enumLiteral_1= 'out' ) | (enumLiteral_2= 'sysvar' ) | (enumLiteral_3= 'sys' ) | (enumLiteral_4= 'input' ) | (enumLiteral_5= 'in' ) | (enumLiteral_6= 'envvar' ) | (enumLiteral_7= 'env' ) | (enumLiteral_8= 'auxvar' ) | (enumLiteral_9= 'aux' ) ) ;
    public final Enumerator ruleVarOwner() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;
        Token enumLiteral_1=null;
        Token enumLiteral_2=null;
        Token enumLiteral_3=null;
        Token enumLiteral_4=null;
        Token enumLiteral_5=null;
        Token enumLiteral_6=null;
        Token enumLiteral_7=null;
        Token enumLiteral_8=null;
        Token enumLiteral_9=null;


        	enterRule();

        try {
            // InternalSpectra.g:5904:2: ( ( (enumLiteral_0= 'output' ) | (enumLiteral_1= 'out' ) | (enumLiteral_2= 'sysvar' ) | (enumLiteral_3= 'sys' ) | (enumLiteral_4= 'input' ) | (enumLiteral_5= 'in' ) | (enumLiteral_6= 'envvar' ) | (enumLiteral_7= 'env' ) | (enumLiteral_8= 'auxvar' ) | (enumLiteral_9= 'aux' ) ) )
            // InternalSpectra.g:5905:2: ( (enumLiteral_0= 'output' ) | (enumLiteral_1= 'out' ) | (enumLiteral_2= 'sysvar' ) | (enumLiteral_3= 'sys' ) | (enumLiteral_4= 'input' ) | (enumLiteral_5= 'in' ) | (enumLiteral_6= 'envvar' ) | (enumLiteral_7= 'env' ) | (enumLiteral_8= 'auxvar' ) | (enumLiteral_9= 'aux' ) )
            {
            // InternalSpectra.g:5905:2: ( (enumLiteral_0= 'output' ) | (enumLiteral_1= 'out' ) | (enumLiteral_2= 'sysvar' ) | (enumLiteral_3= 'sys' ) | (enumLiteral_4= 'input' ) | (enumLiteral_5= 'in' ) | (enumLiteral_6= 'envvar' ) | (enumLiteral_7= 'env' ) | (enumLiteral_8= 'auxvar' ) | (enumLiteral_9= 'aux' ) )
            int alt108=10;
            switch ( input.LA(1) ) {
            case 109:
                {
                alt108=1;
                }
                break;
            case 110:
                {
                alt108=2;
                }
                break;
            case 111:
                {
                alt108=3;
                }
                break;
            case 112:
                {
                alt108=4;
                }
                break;
            case 113:
                {
                alt108=5;
                }
                break;
            case 58:
                {
                alt108=6;
                }
                break;
            case 114:
                {
                alt108=7;
                }
                break;
            case 115:
                {
                alt108=8;
                }
                break;
            case 116:
                {
                alt108=9;
                }
                break;
            case 117:
                {
                alt108=10;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 108, 0, input);

                throw nvae;
            }

            switch (alt108) {
                case 1 :
                    // InternalSpectra.g:5906:3: (enumLiteral_0= 'output' )
                    {
                    // InternalSpectra.g:5906:3: (enumLiteral_0= 'output' )
                    // InternalSpectra.g:5907:4: enumLiteral_0= 'output'
                    {
                    enumLiteral_0=(Token)match(input,109,FOLLOW_2); 

                    				current = grammarAccess.getVarOwnerAccess().getSYSEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                    				newLeafNode(enumLiteral_0, grammarAccess.getVarOwnerAccess().getSYSEnumLiteralDeclaration_0());
                    			

                    }


                    }
                    break;
                case 2 :
                    // InternalSpectra.g:5914:3: (enumLiteral_1= 'out' )
                    {
                    // InternalSpectra.g:5914:3: (enumLiteral_1= 'out' )
                    // InternalSpectra.g:5915:4: enumLiteral_1= 'out'
                    {
                    enumLiteral_1=(Token)match(input,110,FOLLOW_2); 

                    				current = grammarAccess.getVarOwnerAccess().getSYSEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                    				newLeafNode(enumLiteral_1, grammarAccess.getVarOwnerAccess().getSYSEnumLiteralDeclaration_1());
                    			

                    }


                    }
                    break;
                case 3 :
                    // InternalSpectra.g:5922:3: (enumLiteral_2= 'sysvar' )
                    {
                    // InternalSpectra.g:5922:3: (enumLiteral_2= 'sysvar' )
                    // InternalSpectra.g:5923:4: enumLiteral_2= 'sysvar'
                    {
                    enumLiteral_2=(Token)match(input,111,FOLLOW_2); 

                    				current = grammarAccess.getVarOwnerAccess().getSYSEnumLiteralDeclaration_2().getEnumLiteral().getInstance();
                    				newLeafNode(enumLiteral_2, grammarAccess.getVarOwnerAccess().getSYSEnumLiteralDeclaration_2());
                    			

                    }


                    }
                    break;
                case 4 :
                    // InternalSpectra.g:5930:3: (enumLiteral_3= 'sys' )
                    {
                    // InternalSpectra.g:5930:3: (enumLiteral_3= 'sys' )
                    // InternalSpectra.g:5931:4: enumLiteral_3= 'sys'
                    {
                    enumLiteral_3=(Token)match(input,112,FOLLOW_2); 

                    				current = grammarAccess.getVarOwnerAccess().getSYSEnumLiteralDeclaration_3().getEnumLiteral().getInstance();
                    				newLeafNode(enumLiteral_3, grammarAccess.getVarOwnerAccess().getSYSEnumLiteralDeclaration_3());
                    			

                    }


                    }
                    break;
                case 5 :
                    // InternalSpectra.g:5938:3: (enumLiteral_4= 'input' )
                    {
                    // InternalSpectra.g:5938:3: (enumLiteral_4= 'input' )
                    // InternalSpectra.g:5939:4: enumLiteral_4= 'input'
                    {
                    enumLiteral_4=(Token)match(input,113,FOLLOW_2); 

                    				current = grammarAccess.getVarOwnerAccess().getENVEnumLiteralDeclaration_4().getEnumLiteral().getInstance();
                    				newLeafNode(enumLiteral_4, grammarAccess.getVarOwnerAccess().getENVEnumLiteralDeclaration_4());
                    			

                    }


                    }
                    break;
                case 6 :
                    // InternalSpectra.g:5946:3: (enumLiteral_5= 'in' )
                    {
                    // InternalSpectra.g:5946:3: (enumLiteral_5= 'in' )
                    // InternalSpectra.g:5947:4: enumLiteral_5= 'in'
                    {
                    enumLiteral_5=(Token)match(input,58,FOLLOW_2); 

                    				current = grammarAccess.getVarOwnerAccess().getENVEnumLiteralDeclaration_5().getEnumLiteral().getInstance();
                    				newLeafNode(enumLiteral_5, grammarAccess.getVarOwnerAccess().getENVEnumLiteralDeclaration_5());
                    			

                    }


                    }
                    break;
                case 7 :
                    // InternalSpectra.g:5954:3: (enumLiteral_6= 'envvar' )
                    {
                    // InternalSpectra.g:5954:3: (enumLiteral_6= 'envvar' )
                    // InternalSpectra.g:5955:4: enumLiteral_6= 'envvar'
                    {
                    enumLiteral_6=(Token)match(input,114,FOLLOW_2); 

                    				current = grammarAccess.getVarOwnerAccess().getENVEnumLiteralDeclaration_6().getEnumLiteral().getInstance();
                    				newLeafNode(enumLiteral_6, grammarAccess.getVarOwnerAccess().getENVEnumLiteralDeclaration_6());
                    			

                    }


                    }
                    break;
                case 8 :
                    // InternalSpectra.g:5962:3: (enumLiteral_7= 'env' )
                    {
                    // InternalSpectra.g:5962:3: (enumLiteral_7= 'env' )
                    // InternalSpectra.g:5963:4: enumLiteral_7= 'env'
                    {
                    enumLiteral_7=(Token)match(input,115,FOLLOW_2); 

                    				current = grammarAccess.getVarOwnerAccess().getENVEnumLiteralDeclaration_7().getEnumLiteral().getInstance();
                    				newLeafNode(enumLiteral_7, grammarAccess.getVarOwnerAccess().getENVEnumLiteralDeclaration_7());
                    			

                    }


                    }
                    break;
                case 9 :
                    // InternalSpectra.g:5970:3: (enumLiteral_8= 'auxvar' )
                    {
                    // InternalSpectra.g:5970:3: (enumLiteral_8= 'auxvar' )
                    // InternalSpectra.g:5971:4: enumLiteral_8= 'auxvar'
                    {
                    enumLiteral_8=(Token)match(input,116,FOLLOW_2); 

                    				current = grammarAccess.getVarOwnerAccess().getAUXEnumLiteralDeclaration_8().getEnumLiteral().getInstance();
                    				newLeafNode(enumLiteral_8, grammarAccess.getVarOwnerAccess().getAUXEnumLiteralDeclaration_8());
                    			

                    }


                    }
                    break;
                case 10 :
                    // InternalSpectra.g:5978:3: (enumLiteral_9= 'aux' )
                    {
                    // InternalSpectra.g:5978:3: (enumLiteral_9= 'aux' )
                    // InternalSpectra.g:5979:4: enumLiteral_9= 'aux'
                    {
                    enumLiteral_9=(Token)match(input,117,FOLLOW_2); 

                    				current = grammarAccess.getVarOwnerAccess().getAUXEnumLiteralDeclaration_9().getEnumLiteral().getInstance();
                    				newLeafNode(enumLiteral_9, grammarAccess.getVarOwnerAccess().getAUXEnumLiteralDeclaration_9());
                    			

                    }


                    }
                    break;

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleVarOwner"


    // $ANTLR start "ruleOverflowMethod"
    // InternalSpectra.g:5989:1: ruleOverflowMethod returns [Enumerator current=null] : ( (enumLiteral_0= 'keep' ) | (enumLiteral_1= 'false' ) | (enumLiteral_2= 'modulo' ) ) ;
    public final Enumerator ruleOverflowMethod() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;
        Token enumLiteral_1=null;
        Token enumLiteral_2=null;


        	enterRule();

        try {
            // InternalSpectra.g:5995:2: ( ( (enumLiteral_0= 'keep' ) | (enumLiteral_1= 'false' ) | (enumLiteral_2= 'modulo' ) ) )
            // InternalSpectra.g:5996:2: ( (enumLiteral_0= 'keep' ) | (enumLiteral_1= 'false' ) | (enumLiteral_2= 'modulo' ) )
            {
            // InternalSpectra.g:5996:2: ( (enumLiteral_0= 'keep' ) | (enumLiteral_1= 'false' ) | (enumLiteral_2= 'modulo' ) )
            int alt109=3;
            switch ( input.LA(1) ) {
            case 118:
                {
                alt109=1;
                }
                break;
            case 98:
                {
                alt109=2;
                }
                break;
            case 119:
                {
                alt109=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 109, 0, input);

                throw nvae;
            }

            switch (alt109) {
                case 1 :
                    // InternalSpectra.g:5997:3: (enumLiteral_0= 'keep' )
                    {
                    // InternalSpectra.g:5997:3: (enumLiteral_0= 'keep' )
                    // InternalSpectra.g:5998:4: enumLiteral_0= 'keep'
                    {
                    enumLiteral_0=(Token)match(input,118,FOLLOW_2); 

                    				current = grammarAccess.getOverflowMethodAccess().getKEEPEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                    				newLeafNode(enumLiteral_0, grammarAccess.getOverflowMethodAccess().getKEEPEnumLiteralDeclaration_0());
                    			

                    }


                    }
                    break;
                case 2 :
                    // InternalSpectra.g:6005:3: (enumLiteral_1= 'false' )
                    {
                    // InternalSpectra.g:6005:3: (enumLiteral_1= 'false' )
                    // InternalSpectra.g:6006:4: enumLiteral_1= 'false'
                    {
                    enumLiteral_1=(Token)match(input,98,FOLLOW_2); 

                    				current = grammarAccess.getOverflowMethodAccess().getFALSEEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                    				newLeafNode(enumLiteral_1, grammarAccess.getOverflowMethodAccess().getFALSEEnumLiteralDeclaration_1());
                    			

                    }


                    }
                    break;
                case 3 :
                    // InternalSpectra.g:6013:3: (enumLiteral_2= 'modulo' )
                    {
                    // InternalSpectra.g:6013:3: (enumLiteral_2= 'modulo' )
                    // InternalSpectra.g:6014:4: enumLiteral_2= 'modulo'
                    {
                    enumLiteral_2=(Token)match(input,119,FOLLOW_2); 

                    				current = grammarAccess.getOverflowMethodAccess().getMODULOEnumLiteralDeclaration_2().getEnumLiteral().getInstance();
                    				newLeafNode(enumLiteral_2, grammarAccess.getOverflowMethodAccess().getMODULOEnumLiteralDeclaration_2());
                    			

                    }


                    }
                    break;

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleOverflowMethod"

    // Delegated rules


    protected DFA4 dfa4 = new DFA4(this);
    protected DFA88 dfa88 = new DFA88(this);
    protected DFA98 dfa98 = new DFA98(this);
    static final String dfa_1s = "\21\uffff";
    static final String dfa_2s = "\1\16\7\uffff\2\4\3\uffff\1\17\2\uffff\1\4";
    static final String dfa_3s = "\1\165\7\uffff\2\152\3\uffff\1\154\2\uffff\1\152";
    static final String dfa_4s = "\1\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\2\uffff\1\11\1\13\1\14\1\uffff\1\10\1\12\1\uffff";
    static final String dfa_5s = "\21\uffff}>";
    static final String[] dfa_6s = {
            "\1\7\2\uffff\1\2\1\uffff\1\3\6\uffff\1\14\1\13\7\uffff\1\6\6\uffff\1\5\4\uffff\1\4\3\uffff\1\10\1\11\2\12\3\uffff\1\1\62\uffff\11\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\15\1\uffff\1\16\11\uffff\1\16\11\uffff\1\16\1\uffff\1\16\7\uffff\6\16\2\uffff\3\16\10\uffff\2\17\31\uffff\10\16\7\uffff\5\16\3\uffff\2\16",
            "\1\15\1\uffff\1\16\11\uffff\1\16\11\uffff\1\16\1\uffff\1\16\7\uffff\6\16\2\uffff\3\16\10\uffff\2\17\31\uffff\10\16\7\uffff\5\16\3\uffff\2\16",
            "",
            "",
            "",
            "\1\20\1\16\1\uffff\1\16\2\uffff\1\16\1\uffff\1\16\4\uffff\1\16\23\uffff\1\16\10\uffff\31\16\10\uffff\6\16\14\uffff\1\16",
            "",
            "",
            "\1\16\1\uffff\1\16\11\uffff\1\16\11\uffff\1\16\1\uffff\1\16\7\uffff\6\16\2\uffff\3\16\10\uffff\2\17\31\uffff\10\16\7\uffff\5\16\3\uffff\2\16"
    };

    static final short[] dfa_1 = DFA.unpackEncodedString(dfa_1s);
    static final char[] dfa_2 = DFA.unpackEncodedStringToUnsignedChars(dfa_2s);
    static final char[] dfa_3 = DFA.unpackEncodedStringToUnsignedChars(dfa_3s);
    static final short[] dfa_4 = DFA.unpackEncodedString(dfa_4s);
    static final short[] dfa_5 = DFA.unpackEncodedString(dfa_5s);
    static final short[][] dfa_6 = unpackEncodedStringArray(dfa_6s);

    class DFA4 extends DFA {

        public DFA4(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 4;
            this.eot = dfa_1;
            this.eof = dfa_1;
            this.min = dfa_2;
            this.max = dfa_3;
            this.accept = dfa_4;
            this.special = dfa_5;
            this.transition = dfa_6;
        }
        public String getDescription() {
            return "206:2: (this_Var_0= ruleVar | this_TypeDef_1= ruleTypeDef | this_Define_2= ruleDefine | this_Predicate_3= rulePredicate | this_Pattern_4= rulePattern | this_Monitor_5= ruleMonitor | this_WeightDef_6= ruleWeightDef | this_LTLGar_7= ruleLTLGar | this_LTLAsm_8= ruleLTLAsm | this_EXGar_9= ruleEXGar | this_Counter_10= ruleCounter | this_DefineRegExp_11= ruleDefineRegExp )";
        }
    }
    static final String dfa_7s = "\15\uffff";
    static final String dfa_8s = "\1\uffff\1\11\13\uffff";
    static final String dfa_9s = "\1\4\1\20\13\uffff";
    static final String dfa_10s = "\1\131\1\154\13\uffff";
    static final String dfa_11s = "\2\uffff\1\2\1\4\1\5\1\11\1\12\1\13\1\1\1\3\1\6\1\7\1\10";
    static final String dfa_12s = "\15\uffff}>";
    static final String[] dfa_13s = {
            "\1\1\13\uffff\1\2\11\uffff\1\4\75\uffff\1\2\1\3",
            "\1\11\1\uffff\1\11\2\uffff\2\11\1\uffff\2\11\2\uffff\1\10\1\11\22\uffff\1\10\10\uffff\31\11\10\uffff\1\12\1\13\1\14\1\5\1\6\1\7\14\uffff\1\11",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] dfa_7 = DFA.unpackEncodedString(dfa_7s);
    static final short[] dfa_8 = DFA.unpackEncodedString(dfa_8s);
    static final char[] dfa_9 = DFA.unpackEncodedStringToUnsignedChars(dfa_9s);
    static final char[] dfa_10 = DFA.unpackEncodedStringToUnsignedChars(dfa_10s);
    static final short[] dfa_11 = DFA.unpackEncodedString(dfa_11s);
    static final short[] dfa_12 = DFA.unpackEncodedString(dfa_12s);
    static final short[][] dfa_13 = unpackEncodedStringArray(dfa_13s);

    class DFA88 extends DFA {

        public DFA88(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 88;
            this.eot = dfa_7;
            this.eof = dfa_8;
            this.min = dfa_9;
            this.max = dfa_10;
            this.accept = dfa_11;
            this.special = dfa_12;
            this.transition = dfa_13;
        }
        public String getDescription() {
            return "4132:4: ( ( ( (otherlv_5= RULE_ID ) ) ( (otherlv_6= '(' ( (lv_predPattParams_7_0= ruleTemporalInExpr ) ) (otherlv_8= ',' ( (lv_predPattParams_9_0= ruleTemporalInExpr ) ) )* otherlv_10= ')' ) | otherlv_11= '()' ) ) | ( ( ( (lv_operator_12_1= '-' | lv_operator_12_2= '!' ) ) ) ( (lv_tpe_13_0= ruleTemporalPrimaryExpr ) ) ) | ( ( (otherlv_14= RULE_ID ) ) (otherlv_15= '[' ( (lv_index_16_0= ruleTemporalInExpr ) ) otherlv_17= ']' )* ) | ( ( (lv_operator_18_0= 'next' ) ) otherlv_19= '(' ( (lv_temporalExpression_20_0= ruleTemporalInExpr ) ) otherlv_21= ')' ) | ( ( (lv_operator_22_0= 'regexp' ) ) otherlv_23= '(' ( ( (lv_regexp_24_0= ruleRegExp ) ) | ( (otherlv_25= RULE_ID ) ) ) otherlv_26= ')' ) | ( ( (otherlv_27= RULE_ID ) ) ( (lv_operator_28_0= '.all' ) ) ) | ( ( (otherlv_29= RULE_ID ) ) ( (lv_operator_30_0= '.any' ) ) ) | ( ( (otherlv_31= RULE_ID ) ) ( (lv_operator_32_0= '.prod' ) ) ) | ( ( (otherlv_33= RULE_ID ) ) ( (lv_operator_34_0= '.sum' ) ) ) | ( ( (otherlv_35= RULE_ID ) ) ( (lv_operator_36_0= '.min' ) ) ) | ( ( (otherlv_37= RULE_ID ) ) ( (lv_operator_38_0= '.max' ) ) ) )";
        }
    }
    static final String dfa_14s = "\12\uffff";
    static final String dfa_15s = "\1\27\3\uffff\1\4\1\30\2\uffff\1\4\1\uffff";
    static final String dfa_16s = "\1\147\3\uffff\1\6\1\31\2\uffff\1\31\1\uffff";
    static final String dfa_17s = "\1\uffff\1\1\1\2\1\3\2\uffff\1\6\1\4\1\uffff\1\5";
    static final String dfa_18s = "\12\uffff}>";
    static final String[] dfa_19s = {
            "\1\4\63\uffff\1\3\1\1\32\uffff\1\2",
            "",
            "",
            "",
            "\1\6\1\uffff\1\5",
            "\1\10\1\7",
            "",
            "",
            "\1\6\1\uffff\1\6\22\uffff\1\11",
            ""
    };

    static final short[] dfa_14 = DFA.unpackEncodedString(dfa_14s);
    static final char[] dfa_15 = DFA.unpackEncodedStringToUnsignedChars(dfa_15s);
    static final char[] dfa_16 = DFA.unpackEncodedStringToUnsignedChars(dfa_16s);
    static final short[] dfa_17 = DFA.unpackEncodedString(dfa_17s);
    static final short[] dfa_18 = DFA.unpackEncodedString(dfa_18s);
    static final short[][] dfa_19 = unpackEncodedStringArray(dfa_19s);

    class DFA98 extends DFA {

        public DFA98(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 98;
            this.eot = dfa_14;
            this.eof = dfa_14;
            this.min = dfa_15;
            this.max = dfa_16;
            this.accept = dfa_17;
            this.special = dfa_18;
            this.transition = dfa_19;
        }
        public String getDescription() {
            return "5002:4: ( ( (lv_kleened_2_0= '*' ) ) | ( (lv_questionMark_3_0= '?' ) ) | ( (lv_plus_4_0= '+' ) ) | ( ( (lv_haveExactRepetition_5_0= '{' ) ) ( (lv_exactRepetition_6_0= RULE_INT ) ) otherlv_7= '}' ) | ( ( (lv_haveAtLeast_8_0= '{' ) ) ( (lv_atLeast_9_0= RULE_INT ) ) otherlv_10= ',' otherlv_11= '}' ) | ( ( (lv_haveRange_12_0= '{' ) ) ( ( (lv_from_13_0= RULE_INT ) ) | ( (otherlv_14= RULE_ID ) ) ) otherlv_15= ',' ( ( (lv_to_16_0= RULE_INT ) ) | ( (otherlv_17= RULE_ID ) ) ) otherlv_18= '}' ) )";
        }
    }
 

    public static final BitSet FOLLOW_1 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_2 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_3 = new BitSet(new long[]{0x0000000000003800L});
    public static final BitSet FOLLOW_4 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_5 = new BitSet(new long[]{0x047884080C0A4000L,0x003FE00000000000L});
    public static final BitSet FOLLOW_6 = new BitSet(new long[]{0x047884080C0A4002L,0x003FE00000000000L});
    public static final BitSet FOLLOW_7 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_8 = new BitSet(new long[]{0x0000000000010050L});
    public static final BitSet FOLLOW_9 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_10 = new BitSet(new long[]{0x0000000000010040L});
    public static final BitSet FOLLOW_11 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_12 = new BitSet(new long[]{0x0000000014010050L,0x0000001E03FC0000L});
    public static final BitSet FOLLOW_13 = new BitSet(new long[]{0x0000000000000000L,0x0000100000000000L});
    public static final BitSet FOLLOW_14 = new BitSet(new long[]{0x0006000000800010L});
    public static final BitSet FOLLOW_15 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_16 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_17 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_18 = new BitSet(new long[]{0x0000000014010050L,0x0000061E03FC0000L});
    public static final BitSet FOLLOW_19 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_20 = new BitSet(new long[]{0x0000000010000050L});
    public static final BitSet FOLLOW_21 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_22 = new BitSet(new long[]{0x0000000000300000L});
    public static final BitSet FOLLOW_23 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_24 = new BitSet(new long[]{0x0000000003000000L});
    public static final BitSet FOLLOW_25 = new BitSet(new long[]{0x0001000010200000L,0x0000011E00000000L});
    public static final BitSet FOLLOW_26 = new BitSet(new long[]{0x0000000001000002L});
    public static final BitSet FOLLOW_27 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_28 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_29 = new BitSet(new long[]{0x00000007D6010050L,0x0000001E03FC0000L});
    public static final BitSet FOLLOW_30 = new BitSet(new long[]{0x0000000000000000L,0x00C0000400000000L});
    public static final BitSet FOLLOW_31 = new BitSet(new long[]{0x000003F016010050L,0x0000001E03FC0000L});
    public static final BitSet FOLLOW_32 = new BitSet(new long[]{0x0000000010800000L});
    public static final BitSet FOLLOW_33 = new BitSet(new long[]{0x00007BF014010050L,0x0000001E03FC0000L});
    public static final BitSet FOLLOW_34 = new BitSet(new long[]{0x000073F016010050L,0x0000001E03FC0000L});
    public static final BitSet FOLLOW_35 = new BitSet(new long[]{0x0001000010000000L});
    public static final BitSet FOLLOW_36 = new BitSet(new long[]{0x0000000000808000L});
    public static final BitSet FOLLOW_37 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_38 = new BitSet(new long[]{0x0000000000200002L});
    public static final BitSet FOLLOW_39 = new BitSet(new long[]{0x0000000000000050L});
    public static final BitSet FOLLOW_40 = new BitSet(new long[]{0x000073F014010050L,0x0000063E03FC0000L});
    public static final BitSet FOLLOW_41 = new BitSet(new long[]{0x0180000000000010L});
    public static final BitSet FOLLOW_42 = new BitSet(new long[]{0x0180000000000000L});
    public static final BitSet FOLLOW_43 = new BitSet(new long[]{0x0000000001000000L,0x0000100000000000L});
    public static final BitSet FOLLOW_44 = new BitSet(new long[]{0x0600000000000002L});
    public static final BitSet FOLLOW_45 = new BitSet(new long[]{0x0400000000000000L});
    public static final BitSet FOLLOW_46 = new BitSet(new long[]{0x0000000000000050L,0x0000001E00000000L});
    public static final BitSet FOLLOW_47 = new BitSet(new long[]{0x1800000000000002L});
    public static final BitSet FOLLOW_48 = new BitSet(new long[]{0x6000000000000002L});
    public static final BitSet FOLLOW_49 = new BitSet(new long[]{0x8000000000000002L,0x0000000000000003L});
    public static final BitSet FOLLOW_50 = new BitSet(new long[]{0x0000000000000002L,0x000000000000000CL});
    public static final BitSet FOLLOW_51 = new BitSet(new long[]{0x0000000000040002L,0x00000000000001F0L});
    public static final BitSet FOLLOW_52 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000600L});
    public static final BitSet FOLLOW_53 = new BitSet(new long[]{0x0000000000010002L,0x0000000000000800L});
    public static final BitSet FOLLOW_54 = new BitSet(new long[]{0x0000000000000002L,0x0000000000003000L});
    public static final BitSet FOLLOW_55 = new BitSet(new long[]{0x0000000000000002L,0x000000000003C000L});
    public static final BitSet FOLLOW_56 = new BitSet(new long[]{0x0000000021000000L});
    public static final BitSet FOLLOW_57 = new BitSet(new long[]{0x0000000014010050L,0x0000001E03000000L});
    public static final BitSet FOLLOW_58 = new BitSet(new long[]{0x0001000010200010L,0x0000011E00000000L});
    public static final BitSet FOLLOW_59 = new BitSet(new long[]{0x0000000000000000L,0x0000000004000000L});
    public static final BitSet FOLLOW_60 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000000L});
    public static final BitSet FOLLOW_61 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
    public static final BitSet FOLLOW_62 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_63 = new BitSet(new long[]{0x0000000000000000L,0x0000000040000000L});
    public static final BitSet FOLLOW_64 = new BitSet(new long[]{0x0000000000000000L,0x0000000080000000L});
    public static final BitSet FOLLOW_65 = new BitSet(new long[]{0x0000000000000000L,0x0000000100000000L});
    public static final BitSet FOLLOW_66 = new BitSet(new long[]{0x0000000000000000L,0x0000004000000000L});
    public static final BitSet FOLLOW_67 = new BitSet(new long[]{0x8001000010200002L,0x0000011E00000004L});
    public static final BitSet FOLLOW_68 = new BitSet(new long[]{0x0000000000800002L,0x0000008000001800L});
    public static final BitSet FOLLOW_69 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_70 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_71 = new BitSet(new long[]{0x0000000000000000L,0x0000080000000000L});

}