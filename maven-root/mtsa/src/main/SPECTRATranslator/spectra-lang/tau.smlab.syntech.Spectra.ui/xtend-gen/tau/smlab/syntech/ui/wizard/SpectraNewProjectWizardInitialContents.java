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
package tau.smlab.syntech.ui.wizard;

import com.google.inject.Inject;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.generator.IFileSystemAccess2;
import org.eclipse.xtext.resource.FileExtensionProvider;

@SuppressWarnings("all")
public class SpectraNewProjectWizardInitialContents {
  @Inject
  private FileExtensionProvider fileExtensionProvider;
  
  public void generateInitialContents(final IFileSystemAccess2 fsa, final String projectName) {
    String _primaryFileExtension = this.fileExtensionProvider.getPrimaryFileExtension();
    String _plus = ((projectName + ".") + _primaryFileExtension);
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("/*");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("* Auto-generated Spectra file");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("*/");
    _builder.newLine();
    _builder.append("spec ");
    String _plus_1 = (_builder.toString() + projectName);
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("\t\t\t");
    _builder_1.newLine();
    _builder_1.append("\t\t\t");
    _builder_1.newLine();
    _builder_1.append("gar ");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.append("alw TRUE;");
    _builder_1.newLine();
    String _plus_2 = (_plus_1 + _builder_1);
    fsa.generateFile(_plus, _plus_2);
  }
  
  public Object generateInitialContents(final IFileSystemAccess2 fsa) {
    return null;
  }
}
