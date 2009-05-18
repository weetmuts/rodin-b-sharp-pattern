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

import java.util.Collection;

import org.eventb.core.IEvent;
import org.eventb.core.ISCEvent;
import org.rodinp.core.IElementType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;

/**
 * @author htson
 *         <p>
 *         An extension of the {@link IMatching} interface for complex matchings
 *         that contains sub-matchings. For example, a matching between two
 *         events contains matchings of their parameters, guards, actions, etc.
 *         </p>
 * @param <T>
 *            the type of the elements in matching.
 */
public interface IComplexMatching<T extends IInternalElement> extends IMatching<T> {

	/**
	 * Get the list of sub-mappings of certain type. For example, from the event
	 * matching to get the list of sub-mappings for parameters.
	 * 
	 * @param <S>
	 *            a type which is an extension of {@link IRodinElement}
	 * @param type
	 *            the element type corresponding to <S>
	 * @return the list of sub-mappings of the input type.
	 */
	public <S extends IInternalElement> Matching<S>[] getChildrenOfType(
			IElementType<S> type);
	
	
	public ComplexMatching<IEvent>[] getChildrenOfTypeEvent();


	/**
	 * Getting the list of problem sub-elements of certain type. For example,
	 * from the event matching to get the list of problem parameters get
	 * matched.
	 * 
	 * @param <S>
	 *            a type which is an extension of {@link IRodinElement}
	 * @param type
	 *            the element type corresponding to <S>
	 * @return the list of problem sub-elements of the input type.
	 */
	public <S extends IInternalElement> Collection<S> getProblemChildrenOfType(
			IElementType<S> type);
	
	/**
	 * Get the matched pattern sub-elements corresponding to input element.
	 * 
	 * @param <S>
	 *            a type which is an extension of {@link IRodinElement}
	 * @param element
	 *            an element of type <S>
	 * @return the matched pattern sub-element or an empty collection if there
	 *         are no matchings..
	 */
	public <S extends IInternalElement> Collection<S> getPatternElement(S element);

	/**
	 * Add a matching to the collection of sub-mappings.
	 * 
	 * @param <S>
	 *            a type which is an extension of {@link IRodinElement}
	 * @param matching
	 *            a matching of type <S>
	 */
	public <S extends IInternalElement> void addMatching(S patternElement, S problemElement, IInternalElementType<S> type);
	
	public <S extends IInternalElement> void addComplexMatching(S patternElement, S problemElement, IInternalElementType<S> type);
	
	/**
	 * Remove a matching from the collection of sub-mappings.
	 * 
	 * @param <S>
	 *            a type which is an extension of {@link IRodinElement}
	 * @param matching
	 *            a matching of type <S>
	 */
	public <S extends IInternalElement> void removeMatching(Matching<S> matching);

}
