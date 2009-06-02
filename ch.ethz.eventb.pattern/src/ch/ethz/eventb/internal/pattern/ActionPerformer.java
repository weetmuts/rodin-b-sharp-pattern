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
package ch.ethz.eventb.internal.pattern;

import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;


/**
 * This class contains a list of ActionListeners, that are notified
 * when the method performAction is called. 
 * 
 * @author fuersta
 *
 */
public class ActionPerformer {
	
	Set<ActionListener> listeners = new HashSet<ActionListener>();
	
	/**
	 * Method to add a ActionListener to the list.
	 * 
	 * @param l
	 */
	public void addListener(ActionListener l){
		listeners.add(l);
	}
	
	/**
	 * This method notifies all ActionListeners in the list of listeners by
	 * calling their actionPerformed method.
	 */
	public void performAction(){
     	SafeRunner.run(new ISafeRunnable() {
			public void run() throws Exception {
				for (ActionListener l : listeners)
					l.actionPerformed(null);
			}

			public void handleException(Throwable exception) {
			
			}
		});
	}
}
