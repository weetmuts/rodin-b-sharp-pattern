/*******************************************************************************
 * Copyright (c) 2009 ETH Zurich
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package ch.ethz.eventb.pattern;


import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import ch.ethz.eventb.internal.pattern.PatternUtils;
import ch.ethz.eventb.internal.pattern.wizards.WizardUtils;


/**
 * @author htson
 * <p>
 * The activator class controls the plug-in life cycle
 * </p>
 */
public class EventBPattern extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "ch.ethz.eventb.pattern";

	// The shared instance
	private static EventBPattern plugin;
	
	/**
	 * The empty constructor.
	 */
	public EventBPattern() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		// Start the plug-in
		super.start(context);
		plugin = this;
		
		// Configure debug options.
		configureDebugOptions();
	}

	// Trace Options
	private static final String GLOBAL_TRACE = PLUGIN_ID + "/debug"; //$NON-NLS-1$

	private static final String WIZARD_TRACE = GLOBAL_TRACE + "/wizard"; //$NON-NLS-1$

	/**
	 * Utility method for configuring various debug options.
	 */
	private void configureDebugOptions() {
		if (isDebugging()) {
			
			// Global plugin debug
			String option = Platform.getDebugOption(GLOBAL_TRACE);
			if (option != null)
				PatternUtils.DEBUG = option.equalsIgnoreCase("true"); //$NON-NLS-1$

			// Wizard debug
			option = Platform.getDebugOption(WIZARD_TRACE);
			if (option != null)
				WizardUtils.DEBUG = option.equalsIgnoreCase("true"); //$NON-NLS-1$
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static EventBPattern getDefault() {
		return plugin;
	}

	/**
	 * Get the active workbench page.
	 * <p>
	 * 
	 * @return current active workbench page
	 */
	public static IWorkbenchPage getActivePage() {
		return getDefault().internalGetActivePage();
	}

	/**
	 * Getting the current active page from the active workbench window.
	 * <p>
	 * 
	 * @return current active workbench page
	 */
	private IWorkbenchPage internalGetActivePage() {
		return getWorkbench().getActiveWorkbenchWindow().getActivePage();
	}

}
