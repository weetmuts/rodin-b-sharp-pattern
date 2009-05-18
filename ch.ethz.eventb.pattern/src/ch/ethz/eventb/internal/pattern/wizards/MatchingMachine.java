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

import org.eventb.core.IEvent;
import org.eventb.core.IMachineRoot;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

import ch.ethz.eventb.internal.pattern.PatternUtils;


/**
 * @author htson
 *         <p>
 *         A concrete class for matching of two machine roots (i.e.
 *         {@link IMachineRoot})
 *         </p>
 */
public class MatchingMachine extends ComplexMatching<IMachineRoot> implements
		IComplexMatching<IMachineRoot> {
	
	private IRodinProject patternProject;
	
	private IRodinProject problemProject;
	
	
	/**
	 * The constructor. Create a complex matching {@link ComplexMatching} of
	 * type machine root.
	 * 
	 * @param problem
	 *            a problem machine root.
	 * @param pattern
	 *            a pattern machine root.
	 * @throws RodinDBException 
	 */
	public MatchingMachine(IMachineRoot problem, IMachineRoot pattern) throws RodinDBException {
		super(problem, pattern, IMachineRoot.ELEMENT_TYPE, null);
		problemProject = problem.getRodinProject();
		patternProject = pattern.getRodinProject();
	}
	

	public IMachineRoot getPatternElement() {
		try {
			return PatternUtils.getRootByName(IMachineRoot.ELEMENT_TYPE, pattern, patternProject);
		} catch (RodinDBException e) {
			return null;
		}
	}
	
	public IMachineRoot getProblemElement() {
		try {
			return PatternUtils.getRootByName(IMachineRoot.ELEMENT_TYPE, problem, problemProject);
		} catch (RodinDBException e) {
			return null;
		}
	}
	
}
