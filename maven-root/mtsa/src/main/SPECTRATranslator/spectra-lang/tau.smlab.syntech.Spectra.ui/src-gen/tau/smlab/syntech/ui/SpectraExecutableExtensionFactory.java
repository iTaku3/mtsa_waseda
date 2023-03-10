/*
 * generated by Xtext 2.25.0
 */
package tau.smlab.syntech.ui;

import com.google.inject.Injector;
import org.eclipse.xtext.ui.guice.AbstractGuiceAwareExecutableExtensionFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import tau.smlab.syntech.Spectra.ui.internal.SpectraActivator;

/**
 * This class was generated. Customizations should only happen in a newly
 * introduced subclass. 
 */
public class SpectraExecutableExtensionFactory extends AbstractGuiceAwareExecutableExtensionFactory {

	@Override
	protected Bundle getBundle() {
		return FrameworkUtil.getBundle(SpectraActivator.class);
	}
	
	@Override
	protected Injector getInjector() {
		SpectraActivator activator = SpectraActivator.getInstance();
		return activator != null ? activator.getInjector(SpectraActivator.TAU_SMLAB_SYNTECH_SPECTRA) : null;
	}

}
