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
package tau.smlab.syntech.ui;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtend.lib.annotations.FinalFieldsConstructor;
import org.eclipse.xtext.documentation.IEObjectDocumentationProvider;
import org.eclipse.xtext.documentation.IEObjectDocumentationProviderExtension;
import org.eclipse.xtext.ide.editor.syntaxcoloring.AbstractAntlrTokenToAttributeIdMapper;
import org.eclipse.xtext.ide.editor.syntaxcoloring.ISemanticHighlightingCalculator;
import org.eclipse.xtext.ui.editor.XtextSourceViewerConfiguration;
import org.eclipse.xtext.ui.editor.autoedit.DefaultAutoEditStrategyProvider;
import org.eclipse.xtext.ui.editor.hover.IEObjectHoverProvider;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightingConfiguration;
import tau.smlab.syntech.ui.autoedit.SpectraAutoEditStrategyProvider;
import tau.smlab.syntech.ui.hover.SpectraEObjectHoverProvider;
import tau.smlab.syntech.ui.sourceviewer.SpectraSourceViewerConfiguration;
import tau.smlab.syntech.ui.wizard.SpectraProjectCreator;
import tau.smlab.syntech.ui.wizard.SpectraProjectCreator2;

/**
 * Use this class to register components to be used within the Eclipse IDE.
 */
@FinalFieldsConstructor
@SuppressWarnings("all")
public class SpectraUiModule extends AbstractSpectraUiModule {
  public Class<? extends AbstractAntlrTokenToAttributeIdMapper> bindAbstractAntlrTokenToAttributeIdMapper() {
    return SpectraAntlrTokenToAttributeIdMapper.class;
  }
  
  public Class<? extends ISemanticHighlightingCalculator> bindISemanticHighlightingCalculator() {
    return SpectraSemanticHighlightingCalculator.class;
  }
  
  public Class<? extends IHighlightingConfiguration> bindIHighlightingConfiguration() {
    return SpectraHighlightingConfiguration.class;
  }
  
  public Class<? extends IEObjectDocumentationProvider> bindIEObjectDocumentationProvider() {
    return SpectraMultiLineCommentDocumentationProvider.class;
  }
  
  public Class<? extends IEObjectDocumentationProviderExtension> bindIEObjectDocumentationProviderExtension() {
    return SpectraMultiLineCommentDocumentationProvider.class;
  }
  
  public Class<? extends IEObjectHoverProvider> bindIEObjectHoverProvider() {
    return SpectraEObjectHoverProvider.class;
  }
  
  public Class<? extends XtextSourceViewerConfiguration> bindXtextSourceViewerConfiguration() {
    return SpectraSourceViewerConfiguration.class;
  }
  
  public Class<? extends SpectraProjectCreator> bindSpectraProjectCreator() {
    return SpectraProjectCreator2.class;
  }
  
  public Class<? extends DefaultAutoEditStrategyProvider> bindDefaultAutoEditStrategyProvider() {
    return SpectraAutoEditStrategyProvider.class;
  }
  
  public SpectraUiModule(final AbstractUIPlugin plugin) {
    super(plugin);
  }
}
