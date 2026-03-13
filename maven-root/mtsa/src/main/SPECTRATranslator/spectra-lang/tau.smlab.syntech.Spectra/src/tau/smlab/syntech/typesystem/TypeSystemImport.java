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

import tau.smlab.syntech.spectra.Import;
import tau.smlab.syntech.spectra.SpectraPackage;

public class TypeSystemImport{

	public static TypeCheckIssue typeCheck(Import importEobject) {
		String path = importEobject.getImportURI();
		int extensionBeginning = path.lastIndexOf(".");
		if (! path.substring(extensionBeginning).equals(".spectra"))
		{
			return new TypeCheckError(SpectraPackage.Literals.IMPORT__IMPORT_URI, IssueMessages.NOT_A_SPECTRA_FILE);
		}
//		
//		String[] pathFragments = path.split("/");
//
//		Model rootModel = EcoreUtil2.getContainerOfType(importEobject, Model.class);
//		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
//		IPath location = root.getLocation(); 
//		Resource rsrc = rootModel.eResource();
//		URI uri = rsrc.getURI();
//		String projectName = uri.segment(1);
//		
//		String fullPath = location + File.separator + projectName;
//		
//		int i = 0;
//		if (pathFragments != null && pathFragments.length > 0 && pathFragments[0].equals(".."))
//		{
//			i = 1;
//		}
//		
//		else
//		{
//			// relative path to the folder of rootModel
//			for (int j = 2; j < uri.segmentCount()-1 ; j++)
//			{
//				fullPath += File.separator + uri.segment(j + 0);
//			}
//		}
//		 
//		while (i < pathFragments.length)
//		{
//			fullPath += File.separator + pathFragments[i];
//			i++;
//		}
//		
//		File f = new File(fullPath);
//		if(! f.exists() || f.isDirectory()) { 
//			return new TypeCheckError(SpectraPackage.Literals.IMPORT__IMPORT_URI, IssueMessages.SPECTRA_FILE_NOT_FOUND);		    
//		}
		
		return null;
	}

}
