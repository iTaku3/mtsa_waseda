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

import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.xtext.generator.IFileSystemAccess2;
import com.google.inject.Inject;

public class SpectraProjectCreator2 extends SpectraProjectCreator {
  protected static final String DSL_PROJECT_NAME = "tau.smlab.syntech";

  @Inject
  private SpectraNewProjectWizardInitialContents initialContents;

  @Override
  protected List<String> getAllFolders() {
    return Collections.emptyList();
  }
  
  @Override
  protected String getModelFolderName() {
    return "src";
  }
  
  @Override
  protected IFile getModelFile(IProject project) throws CoreException {

    final String expectedExtension = getPrimaryModelFileExtension();
    final IFile[] result = new IFile[1];
    project.accept(new IResourceVisitor() {
      @Override
      public boolean visit(IResource resource) throws CoreException {
        if (IResource.FILE == resource.getType() && expectedExtension.equals(resource.getFileExtension())) {
          result[0] = (IFile) resource;
          return false;
        }
        return IResource.FOLDER == resource.getType();
      }
    });
    return result[0];
  }

  @Override
  protected void enhanceProject(final IProject project, final IProgressMonitor monitor) throws CoreException {
    IFileSystemAccess2 access = getFileSystemAccess(project, monitor);
    initialContents.generateInitialContents(access, project.getName());
    project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
  }

}
