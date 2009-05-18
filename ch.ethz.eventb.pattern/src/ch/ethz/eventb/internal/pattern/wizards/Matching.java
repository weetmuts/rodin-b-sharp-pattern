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

import org.eclipse.core.runtime.Assert;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.ILabeledElement;
import org.eventb.core.IMachineRoot;
import org.eventb.core.ISCIdentifierElement;
import org.eventb.core.basis.SCIdentifierElement;
import org.rodinp.core.IElementType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;

import ch.ethz.eventb.internal.pattern.PatternUtils;


/**
 * @author htson
 *         <p>
 *         A concrete implementation of {@link IMatching}.
 *         </p>
 * @param <T>
 *            the type of elements in matching.
 */
public class Matching<T extends IInternalElement> implements IMatching<T> {

	// The problem element
	protected String problem;
	
	// The pattern element
	protected String pattern;
	
	private IMatching parents;
	
	// The type of the elements in matching.
	private IInternalElementType<T> type;
	
	private boolean hasLabel;
	
	/**
	 * The constructor. The input elements must have the correct type.
	 * 
	 * @param problem
	 *            a problem element
	 * @param pattern
	 *            a pattern element
	 * @param type
	 *            the type of the input elements.
	 * @throws RodinDBException 
	 */
	public Matching(IInternalElement problem, IInternalElement pattern, IInternalElementType<T> type, IComplexMatching<? extends IRodinElement> parents) throws RodinDBException {
//		Assert.isLegal(problem.getElementType() == type,
//				"Incorrect elemet type");
//		Assert.isLegal(pattern.getElementType() == type,
//				"Incorrect elemet type");
		if (problem instanceof ISCIdentifierElement){
			this.problem = ((ISCIdentifierElement)problem).getIdentifierString();
			this.pattern = ((ISCIdentifierElement)pattern).getIdentifierString();
			hasLabel = false;
		}
		else if (problem instanceof IIdentifierElement){
			this.problem = ((IIdentifierElement)problem).getIdentifierString();
			this.pattern = ((IIdentifierElement)pattern).getIdentifierString();
			hasLabel = false;
		}
		else if (problem instanceof ILabeledElement){
			this.problem = ((ILabeledElement)problem).getLabel();
			this.pattern = ((ILabeledElement)pattern).getLabel();
			hasLabel = true;
		}
		else if (problem instanceof IEventBRoot){
			this.problem = ((IEventBRoot)problem).getElementName();
			this.pattern = ((IEventBRoot)pattern).getElementName();
			hasLabel = false;
		}
		this.type = type;
		this.parents = parents;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.ethz.eventb.internal.pattern.wizards.IMatching#getPatternElement()
	 */
	public T getPatternElement() {
		try {
			if (hasLabel)
				return PatternUtils.getElementByLabel(type, pattern, parents.getPatternElement());
			return PatternUtils.getElementByIdentifier(type, pattern, parents.getPatternElement());
		} catch (RodinDBException e) {
			return null;
		}
	}
	
	public String getPatternID() {
		return pattern;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.ethz.eventb.internal.pattern.wizards.IMatching#getProblemElement()
	 */
	public T getProblemElement() {
		try {
			if (hasLabel)
				return PatternUtils.getElementByLabel(type, problem, parents.getProblemElement());
			return PatternUtils.getElementByIdentifier(type, problem, parents.getProblemElement());
		} catch (RodinDBException e) {
			return null;
		}
	}
	
	public String getProblemID() {
		return problem;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.ethz.eventb.internal.pattern.wizards.IMatching#getType()
	 */
	public IElementType<T> getType() {
		return type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return PatternUtils.getDisplayText(getPatternElement()) + " --> "
						+ PatternUtils.getDisplayText(getProblemElement());
		
	}

}
