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
package ch.ethz.eventb.internal.pattern.wizards;

import org.rodinp.core.IElementType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

/**
 * @author htson
 *         <p>
 *         Generic interface for a matching of two Rodin elements: a problem
 *         element and a pattern element. The two elements must of the same
 *         type.
 *         </p>
 * @param <T>
 *            the type of the Rodin elements in matching.
 */
public interface IMatching<T extends IInternalElement> {

	/**
	 * Get the problem element of the matching.
	 * 
	 * @return the problem element of the matching.
	 */
	public T getProblemElement();
	
	/**
	 * Get the pattern element of the matching.
	 * 
	 * @return the pattern element of the matching.
	 */
	public T getPatternElement();

	/**
	 * Get the type of the elements in matching.
	 * 
	 * @return the type of the elements in matching.
	 */
	public IElementType<T> getType();
}
