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

package tau.smlab.syntech.ui.contentassist;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.xtext.ui.editor.contentassist.CompletionProposalComputer;

import tau.smlab.syntech.spectra.TypeConstant;
import tau.smlab.syntech.spectra.VarDecl;
import tau.smlab.syntech.spectra.VarType;

public class SpectraProposalProviderFilter {

	/**
	 * 
	 * @param vd
	 * @return possible TypeConstant values for vd
	 */
	public static List<TypeConstant> getValidProposals(VarDecl vd) {
		List<TypeConstant> possibleTypeConstants = new LinkedList<>();

		VarType vt = vd.getType();
		if (vt != null) {
			possibleTypeConstants.addAll(vt.getConst());
		}
		return possibleTypeConstants;
	}

	/**
	 * Accessing propospals field though it's private
	 */
	@SuppressWarnings("unchecked")
	public static Collection<ICompletionProposal> getProposals(CompletionProposalComputer computer) {
		Field f;
		try {
			f = computer.getClass().getDeclaredField("proposals");
		} catch (NoSuchFieldException e) {
			return null;
		} catch (SecurityException e) {
			return null;
		}
		f.setAccessible(true);
		Collection<ICompletionProposal> proposals = null;
		try {
			proposals = (Collection<ICompletionProposal>) f.get(computer);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			return null;
		} //IllegalAccessException
		return proposals;
	}


	
}
