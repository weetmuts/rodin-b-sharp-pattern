package ch.ethz.eventb.internal.pattern.wizards.tests;

import org.eventb.core.IAction;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IVariable;
import org.junit.Test;
import org.rodinp.core.RodinDBException;

import ch.ethz.eventb.internal.pattern.tests.EventBTests;
import ch.ethz.eventb.internal.pattern.wizards.ComplexMatching;
import ch.ethz.eventb.internal.pattern.wizards.Matching;

/**
 * @author fuersta
 *
 */
public class MatchingTests extends EventBTests{

	/**
	 * Test method for {@link ch.ethz.eventb.internal.pattern.wizards.Matching#Matching(org.rodinp.core.IInternalElement, org.rodinp.core.IInternalElement, org.rodinp.core.IInternalElementType, ch.ethz.eventb.internal.pattern.wizards.IComplexMatching)}.
	 */
	@Test
	public void testMatchingIInternalElementIInternalElementIInternalElementTypeOfTIComplexMatchingOfQextendsIRodinElement() {
		
		try {
			ComplexMatching<IEvent> parents = new ComplexMatching<IEvent>(evt1_1_3, evt1_2_3, IEvent.ELEMENT_TYPE, machineMatch);
			match_2 = new Matching<IGuard>(grd1_1_3_1, grd1_2_3_1, IGuard.ELEMENT_TYPE, parents);
		} catch (RodinDBException e) {
			e.printStackTrace();
			fail("There should be no exception throws");
			return;
		}
		
		assertEquals(grd1_1_3_1, match_2.getPatternElement());
		assertEquals(grd1_2_3_1, match_2.getProblemElement());
	}

	/**
	 * Test method for {@link ch.ethz.eventb.internal.pattern.wizards.Matching#Matching(java.lang.String, java.lang.String, org.rodinp.core.IInternalElementType, ch.ethz.eventb.internal.pattern.wizards.IComplexMatching, boolean)}.
	 */
	@Test
	public void testMatchingStringStringIInternalElementTypeOfTIComplexMatchingOfQextendsIRodinElementBoolean() {
		try {
			ComplexMatching<IEvent> parents = new ComplexMatching<IEvent>(evt1_1_3, evt1_2_3, IEvent.ELEMENT_TYPE, machineMatch);
			match_2 = new Matching<IGuard>("grd1_1_3_1", "grd1_2_3_1", IGuard.ELEMENT_TYPE, parents, true);
		} catch (RodinDBException e) {
			e.printStackTrace();
			fail("There should be no exception throws");
			return;
		}
		
		assertEquals(grd1_1_3_1, match_2.getPatternElement());
		assertEquals(grd1_2_3_1, match_2.getProblemElement());
	}

	/**
	 * Test method for {@link ch.ethz.eventb.internal.pattern.wizards.Matching#getPatternElement()}.
	 */
	@Test
	public void testGetPatternElement() {
		assertEquals(grd1_1_2_1, match_2.getPatternElement());
		assertEquals(act1_1_2_1, match_3.getPatternElement());
		assertEquals(x, match_4.getPatternElement());
	}

	/**
	 * Test method for {@link ch.ethz.eventb.internal.pattern.wizards.Matching#getPatternID()}.
	 */
	@Test
	public void testGetPatternID() {
		assertEquals("grd1_1_2_1", match_2.getPatternID());
		assertEquals("act1_1_2_1", match_3.getPatternID());
		assertEquals("x", match_4.getPatternID());
	}

	/**
	 * Test method for {@link ch.ethz.eventb.internal.pattern.wizards.Matching#getProblemElement()}.
	 */
	@Test
	public void testGetProblemElement() {
		assertEquals(grd1_2_2_1, match_2.getProblemElement());
		assertEquals(act1_2_2_1, match_3.getProblemElement());
		assertEquals(u, match_4.getProblemElement());
	}

	/**
	 * Test method for {@link ch.ethz.eventb.internal.pattern.wizards.Matching#getProblemID()}.
	 */
	@Test
	public void testGetProblemID() {
		assertEquals("grd1_2_2_1", match_2.getProblemID());
		assertEquals("act1_2_2_1", match_3.getProblemID());
		assertEquals("u", match_4.getProblemID());
	}

	/**
	 * Test method for {@link ch.ethz.eventb.internal.pattern.wizards.Matching#getType()}.
	 */
	@Test
	public void testGetType() {
		assertEquals(IGuard.ELEMENT_TYPE, match_2.getType());
		assertEquals(IAction.ELEMENT_TYPE, match_3.getType());
		assertEquals(IVariable.ELEMENT_TYPE, match_4.getType());
	}


}
