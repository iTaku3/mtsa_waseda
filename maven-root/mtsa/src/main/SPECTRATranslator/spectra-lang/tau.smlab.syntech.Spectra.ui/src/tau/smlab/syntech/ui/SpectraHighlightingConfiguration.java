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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.xtext.ui.editor.syntaxcoloring.DefaultHighlightingConfiguration;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightingConfiguration;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightingConfigurationAcceptor;
import org.eclipse.xtext.ui.editor.utils.TextStyle;

public class SpectraHighlightingConfiguration  extends DefaultHighlightingConfiguration implements IHighlightingConfiguration{
	public static final String ASSUMPTION_ID = "assumption";
	public static final String TYPE_CONSTANT_ID = "typeContant";
	public static final String GUARANTEE_ID = "guarantee";
	public static final String WEIGHT_ID = "weight";
	public static final String REGEXP_ID = "regexp";
	
	@Override
	public void configure(IHighlightingConfigurationAcceptor acceptor) {
		super.configure(acceptor);
		acceptor.acceptDefaultHighlighting(ASSUMPTION_ID, "Assumption", assumptionTextStyle());
		acceptor.acceptDefaultHighlighting(TYPE_CONSTANT_ID, "TypeConstant", typeConstantTextStyle());
		acceptor.acceptDefaultHighlighting(GUARANTEE_ID, "Guarantee", guaranteeTextStyle());
		acceptor.acceptDefaultHighlighting(WEIGHT_ID, "Weight", weightTextStyle());
		acceptor.acceptDefaultHighlighting(REGEXP_ID, "RegExp", defineRegExpTextStyle());
	}

	private TextStyle weightTextStyle() {
		TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setColor(new RGB(255, 20, 147));
		textStyle.setStyle(SWT.BOLD);
		return textStyle;
	}

	private TextStyle guaranteeTextStyle() {
		TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setColor(new RGB(0, 153, 153));
		textStyle.setStyle(SWT.BOLD);
		return textStyle;
	}

	private TextStyle typeConstantTextStyle() {
		TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setColor(new RGB(76, 0, 153));
		textStyle.setStyle(SWT.ITALIC);
		return textStyle;
	}

	private TextStyle assumptionTextStyle() {
		TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setColor(new RGB(0, 153, 0));
		textStyle.setStyle(SWT.BOLD);
		return textStyle;
	}
	
	private TextStyle defineRegExpTextStyle() {
		TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setColor(new RGB(255, 20, 147));
		textStyle.setStyle(SWT.BOLD);
		return textStyle;
	}

	public TextStyle defaultTextStyle() {
		TextStyle textStyle = new TextStyle();
		return textStyle;
	}
}
