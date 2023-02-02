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

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.TerminalRule;
import org.eclipse.xtext.documentation.IEObjectDocumentationProvider;
import org.eclipse.xtext.documentation.IEObjectDocumentationProviderExtension;
import org.eclipse.xtext.documentation.impl.AbstractMultiLineCommentProvider;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.ILeafNode;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;

import com.google.inject.Inject;

public class SpectraMultiLineCommentDocumentationProvider extends AbstractMultiLineCommentProvider implements IEObjectDocumentationProvider, IEObjectDocumentationProviderExtension{


	/**
	 * @since 2.5
	 */
	protected Pattern commentStartTagRegex;

	protected String findComment(EObject o) {
		String returnValue = null;
		List<INode> documentationNodes = getDocumentationNodes(o);
		if (!documentationNodes.isEmpty()) {
			return documentationNodes.get(0).getText();
		}
		return returnValue;
	}
	
	/**
	 * Returns the nearest multi line comment node that precedes the given object. 
	 * @since 2.3
	 * @return a list with exactly one node or an empty list if the object is undocumented.
	 */
	/* @NonNull */
	@Override
	public List<INode> getDocumentationNodes(/* @NonNull */ EObject object) {
		ICompositeNode node = NodeModelUtils.getNode(object);
		List<INode> result = Collections.emptyList();
		if (node != null) {
			// get the last multi line comment before a non hidden leaf node
			for (ILeafNode leafNode : node.getLeafNodes()) {
				if (!leafNode.isHidden())
					break;
				if (leafNode.getGrammarElement() instanceof TerminalRule
						&& ruleName.equalsIgnoreCase(((TerminalRule) leafNode.getGrammarElement()).getName())) {
					String comment = leafNode.getText();
					if (commentStartTagRegex.matcher(comment).matches()) {
						result = Collections.<INode>singletonList(leafNode);
					}
				}
			}
		}
		return result;
	}
	
	
	
	@Override
	public String getDocumentation(EObject o) {
		String returnValue = findComment(o);
		String returnValueWithAnnotations = getAnnotatedDocumentation(returnValue);
		return getTextFromMultilineComment(returnValueWithAnnotations);
	}
	
	private String getAnnotatedDocumentation(String returnValue) {
	  if (returnValue == null) {return null;} 
	  boolean isFirstAnnotationFound = false;
	  StringBuilder result = new StringBuilder("");
    String[] splitted = returnValue.trim().split(" +");
    for (int i = 0; i < splitted.length; i++)
    {
      if (splitted[i].charAt(0) == '@')
      {
        if (! isFirstAnnotationFound)
        {
          result.append("<br><b>Parameters:</b>");
          isFirstAnnotationFound = true;
        }
        result.append("<br>"); //new line
        result.append("<b>"); //bold
        result.append(splitted[i].substring(1) + " "); // do not include "@"
        result.append(splitted[i+1] + " ");
        result.append("</b>");
        i++;
      }
      else
      {
        result.append(splitted[i] + " ");
      }
    }
    String resultString = result.toString();
    return resultString.substring(0, resultString.length()-1); // getting rid of the strange "/" in the end
  }

  /**
	 * @since 2.5
	 */
	@Override
	@Inject
	public void injectProperties(MultiLineCommentProviderProperties properties) {
		super.injectProperties(properties);
		this.commentStartTagRegex = Pattern.compile("(?s)" + startTag + ".*");
	}
	
}
