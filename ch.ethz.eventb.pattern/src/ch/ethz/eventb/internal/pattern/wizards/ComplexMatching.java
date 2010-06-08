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

import java.util.ArrayList;
import java.util.Collection;

import org.eventb.core.IEvent;
import org.rodinp.core.IElementType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         A concrete implementation of {@link IComplexMatching} by extending
 *         {@link Matching}.
 *         </p>
 * @param <T>
 *            the type of elements in matching.
 */
public class ComplexMatching<T extends IInternalElement> extends Matching<T>
		implements IComplexMatching<T> {

	// Collection of sub-matchings.
	private Collection<Matching<?>> matchings;

	/**
	 * The constructor. The collection of sub-matchings is initialised.
	 * @param pattern
	 *            the pattern element.
	 * @param problem
	 *            the problem element.
	 * @param type
	 *            the type of the matching.
	 * 
	 * @throws RodinDBException 
	 */
	public ComplexMatching(IInternalElement pattern, IInternalElement problem, IInternalElementType<T> type, IComplexMatching<? extends IRodinElement> parents) throws RodinDBException {
		super(pattern, problem, type, parents);
		matchings = new ArrayList<Matching<?>>();
	}
	
	public ComplexMatching(String pattern, String problem, IInternalElementType<T> type, IComplexMatching<? extends IRodinElement> parents, boolean hasLabel) throws RodinDBException {
		super(pattern, problem, type, parents, hasLabel);
		matchings = new ArrayList<Matching<?>>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.ethz.eventb.internal.pattern.wizards.IComplexMatching#getChildrenOfType
	 * (org.rodinp.core.IElementType)
	 */
	@SuppressWarnings("unchecked")
	public <S extends IInternalElement> Matching<S>[] getChildrenOfType(
			IElementType<S> type) {
		Collection<Matching<S>> result = new ArrayList<Matching<S>>();
		for (Matching<?> matching : matchings) {
			if (matching.getType().equals(type)) {
				result.add((Matching<S>) matching);
			}
		}
		return result.toArray(new Matching[result.size()]);
	}
	
	@SuppressWarnings("unchecked")
	public ComplexMatching<IEvent>[] getChildrenOfTypeEvent() {
		Collection<ComplexMatching<IEvent>> result = new ArrayList<ComplexMatching<IEvent>>();
		for (Matching<?> matching : matchings) {
			if (matching.getType().equals(IEvent.ELEMENT_TYPE)) {
				result.add((ComplexMatching<IEvent>) matching);
			}
		}
		return result.toArray(new ComplexMatching[result.size()]);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @seeedu.ethz.eventb.internal.pattern.wizards.IComplexMatching#
	 * getProblemChildrenOfType(org.rodinp.core.IElementType)
	 */
	@SuppressWarnings("unchecked")
	public <S extends IInternalElement> Collection<S> getProblemChildrenOfType(
			IElementType<S> type) {
		Collection<S> result = new ArrayList<S>();
		for (Matching<?> matching : matchings) {
			if (matching.getType().equals(type)) {
				result.add((S) (matching.getProblemElement()));
			}
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.ethz.eventb.internal.pattern.wizards.IComplexMatching#getPatternElement
	 * (org.rodinp.core.IRodinElement)
	 */
	@SuppressWarnings("unchecked")
	public <S extends IInternalElement> Collection<S> getPatternElement(S element) {
		Collection<S> result = new ArrayList<S>();
		for (Matching<?> matching : matchings) {
			if (matching.getProblemElement().equals(element)) {
				result.add((S) matching.getPatternElement());
			}
		}
		return result;
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.ethz.eventb.internal.pattern.wizards.IComplexMatching#addMatching
	 * (edu.ethz.eventb.internal.pattern.wizards.Matching)
	 */
	public <S extends IInternalElement> void addMatching(S patternElement, S problemElement, IInternalElementType<S> type) {
		try {
			matchings.add(new Matching<S>(patternElement, problemElement, type,this));
		} catch (RodinDBException e) {
		}
	}
	
	public <S extends IInternalElement> void addMatching(String patternElement, String problemElement, IInternalElementType<S> type, boolean hasLabel) {
		try {
			matchings.add(new Matching<S>(patternElement, problemElement, type, this, hasLabel));
		} catch (RodinDBException e) {
		}
	}
	
	public <S extends IInternalElement> void addComplexMatching(S patternElement, S problemElement, IInternalElementType<S> type) {
		try {
			matchings.add(new ComplexMatching<S>(patternElement, problemElement, type,this));
		} catch (RodinDBException e) {
		}
	}
	
	public <S extends IInternalElement> ComplexMatching<S> addComplexMatching(String patternElement, String problemElement, IInternalElementType<S> type, boolean hasLabel) {
		ComplexMatching<S> cplx = null;
		try {
			cplx = new ComplexMatching<S>(patternElement, problemElement, type, this, hasLabel);
			matchings.add(cplx);
		} catch (RodinDBException e) {
		}
		return cplx;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.ethz.eventb.internal.pattern.wizards.IComplexMatching#removeMatching
	 * (edu.ethz.eventb.internal.pattern.wizards.Matching)
	 */
	public <S extends IInternalElement> void removeMatching(Matching<S> matching) {
		if (matchings.contains(matching))
			matchings.remove(matching);
	}
	
	public void clearMatching() {
		matchings.clear();
	}
	
}
