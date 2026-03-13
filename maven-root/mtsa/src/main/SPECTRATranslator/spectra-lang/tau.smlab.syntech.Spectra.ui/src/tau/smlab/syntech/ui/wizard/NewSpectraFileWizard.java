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

package tau.smlab.syntech.ui.wizard;

import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.xtext.util.StringInputStream;

public class NewSpectraFileWizard extends Wizard implements INewWizard {
  private IStructuredSelection selection;
  private NewSpectraFileWizardPage newFileWizardPage;

  public NewSpectraFileWizard() {
    setWindowTitle("New Spectra file");
  }

  @Override
  public void addPages() {
    newFileWizardPage = new NewSpectraFileWizardPage(selection);
    addPage(newFileWizardPage);
  }

  @Override
  public boolean performFinish() {
    IFile file = newFileWizardPage.createNewFile();
    if (file != null) {
      String fileNameWithSpectraExtension = file.getName();
      int startOfExtension = fileNameWithSpectraExtension.lastIndexOf(".spectra");
      String fileNameWithoutSpectraExtension = fileNameWithSpectraExtension.substring(0, startOfExtension);
      // writing "module [name]" to the new created file      
      InputStream input = new StringInputStream("spec " + firstToUpper(fileNameWithoutSpectraExtension) + System.lineSeparator()
          + System.lineSeparator() + "gar" + System.lineSeparator() + "  alw TRUE;");
      try {
        file.setContents(input, IResource.FORCE, null);
      } catch (CoreException e) {
      }
      return true;
    } else {
      return false;
    }
  }

  private String firstToUpper(String s) {    
    String first = s.substring(0, 1).toUpperCase();    
    return first + s.substring(1, s.length());
  }

  public void init(IWorkbench workbench, IStructuredSelection selection) {
    this.selection = selection;
  }
}
