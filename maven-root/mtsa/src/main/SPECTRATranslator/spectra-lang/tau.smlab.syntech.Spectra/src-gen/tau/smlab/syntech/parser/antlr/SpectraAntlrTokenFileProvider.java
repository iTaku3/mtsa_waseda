/*
 * generated by Xtext 2.25.0
 */
package tau.smlab.syntech.parser.antlr;

import java.io.InputStream;
import org.eclipse.xtext.parser.antlr.IAntlrTokenFileProvider;

public class SpectraAntlrTokenFileProvider implements IAntlrTokenFileProvider {

	@Override
	public InputStream getAntlrTokenFile() {
		ClassLoader classLoader = getClass().getClassLoader();
		return classLoader.getResourceAsStream("tau/smlab/syntech/parser/antlr/internal/InternalSpectra.tokens");
	}
}
