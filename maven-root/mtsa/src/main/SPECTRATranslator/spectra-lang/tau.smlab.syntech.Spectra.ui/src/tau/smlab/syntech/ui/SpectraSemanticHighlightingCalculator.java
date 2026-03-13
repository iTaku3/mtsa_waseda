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

package tau.smlab.syntech.ui;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.AbstractRule;
import org.eclipse.xtext.CrossReference;
import org.eclipse.xtext.RuleCall;
import org.eclipse.xtext.ide.editor.syntaxcoloring.IHighlightedPositionAcceptor;
import org.eclipse.xtext.ide.editor.syntaxcoloring.ISemanticHighlightingCalculator;
import org.eclipse.xtext.nodemodel.BidiTreeIterable;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.resource.EObjectAtOffsetHelper;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.util.CancelIndicator;

import com.google.inject.Inject;

import tau.smlab.syntech.services.SpectraGrammarAccess;
import tau.smlab.syntech.spectra.TypeConstant;

public class SpectraSemanticHighlightingCalculator implements ISemanticHighlightingCalculator {

	@Inject
	private SpectraGrammarAccess ga;

	@Inject
	private EObjectAtOffsetHelper helper;

	@Override
	public void provideHighlightingFor(XtextResource resource, IHighlightedPositionAcceptor acceptor,
			CancelIndicator cancelIndicator) {
		if (resource == null)
			return;
		IParseResult parseResult = resource.getParseResult();
		if (parseResult == null || parseResult.getRootNode() == null)
			return;
		BidiTreeIterable<INode> tree = parseResult.getRootNode().getAsTreeIterable();
		for (INode node : tree) {
			if (cancelIndicator.isCanceled()) {
				return;
			}
			EObject grammarElement = node.getGrammarElement();
			if (grammarElement instanceof RuleCall) {

				RuleCall rc = (RuleCall) grammarElement;
				AbstractRule r = rc.getRule();
				// EObject c = grammarElement.eContainer();

				if (r.getName().equals("TypeConstant")) {
					acceptor.addPosition(node.getOffset(), node.getLength(),
							SpectraHighlightingConfiguration.TYPE_CONSTANT_ID);

				}

			}

			if (node.getGrammarElement() instanceof CrossReference) {
				if (ga.getTemporalPrimaryExprAccess().getPointerReferrableCrossReference_2_1_2_0_0() == node.getGrammarElement()
						|| ga.getValueInRangeAccess().getConstTypeConstantCrossReference_0_0() == node.getGrammarElement()) {
					EObject target = helper.resolveElementAt(resource, node.getOffset());
					if (target instanceof TypeConstant) {
						acceptor.addPosition(node.getOffset(), node.getLength(),
								SpectraHighlightingConfiguration.TYPE_CONSTANT_ID);
					}
				}
				
		

//				if (ga.getPrimaryExprHelperAccess().getPointerReferrableCrossReference_2_0_0() == node.getGrammarElement())
//				{
//					EObject target = helper.resolveElementAt(resource, node.getOffset());
//					if (target instanceof TypeConstant) {
//						acceptor.addPosition(node.getOffset(), node.getLength(),
//								SpectraHighlightingConfiguration.TYPE_CONSTANT_ID);
//					}				
//				}
			}

		}

	}

}
