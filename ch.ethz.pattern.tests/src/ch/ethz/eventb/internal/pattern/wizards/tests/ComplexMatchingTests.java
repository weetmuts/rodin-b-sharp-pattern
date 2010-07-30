/**
 * 
 */
package ch.ethz.eventb.internal.pattern.wizards.tests;

import java.util.Collection;

import org.eventb.core.IAction;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.junit.Test;
import org.rodinp.core.RodinDBException;

import ch.ethz.eventb.internal.pattern.PatternUtils;
import ch.ethz.eventb.internal.pattern.tests.EventBTests;
import ch.ethz.eventb.internal.pattern.wizards.ComplexMatching;
import ch.ethz.eventb.internal.pattern.wizards.Matching;

/**
 * @author fuersta
 *
 */
public class ComplexMatchingTests extends EventBTests {

	/**
	 * Test method for {@link ch.ethz.eventb.internal.pattern.wizards.ComplexMatching#ComplexMatching(org.rodinp.core.IInternalElement, org.rodinp.core.IInternalElement, org.rodinp.core.IInternalElementType, ch.ethz.eventb.internal.pattern.wizards.IComplexMatching)}.
	 */
	@Test
	public void testComplexMatchingIInternalElementIInternalElementIInternalElementTypeOfTIComplexMatchingOfQextendsIRodinElement() {
		try {
			match_1 = new ComplexMatching<IEvent>(init1_1, init1_2, IEvent.ELEMENT_TYPE, machineMatch);
		} catch (RodinDBException e) {
			e.printStackTrace();
			fail("There should be no exception throws");
			return;
		}
		
		assertEquals(init1_1, match_1.getPatternElement());
		assertEquals(init1_2, match_1.getProblemElement());
	}

	/**
	 * Test method for {@link ch.ethz.eventb.internal.pattern.wizards.ComplexMatching#ComplexMatching(java.lang.String, java.lang.String, org.rodinp.core.IInternalElementType, ch.ethz.eventb.internal.pattern.wizards.IComplexMatching, boolean)}.
	 */
	@Test
	public void testComplexMatchingStringStringIInternalElementTypeOfTIComplexMatchingOfQextendsIRodinElementBoolean() {
		try {
			match_1 = new ComplexMatching<IEvent>("evt1_1_1", "evt1_2_1", IEvent.ELEMENT_TYPE, machineMatch, true);
		} catch (RodinDBException e) {
			e.printStackTrace();
			fail("There should be no exception throws");
			return;
		}
		
		assertEquals(evt1_1_1, match_1.getPatternElement());
		assertEquals(evt1_2_1, match_1.getProblemElement());
	}

	/**
	 * Test method for {@link ch.ethz.eventb.internal.pattern.wizards.ComplexMatching#getChildrenOfType(org.rodinp.core.IElementType)}.
	 */
	@Test
	public void testGetChildrenOfType() {
		match_1.addMatching(grd1_1_2_1, grd1_2_2_1, IGuard.ELEMENT_TYPE);
		match_1.addMatching(act1_1_2_1, act1_2_2_1, IAction.ELEMENT_TYPE);
		
		Matching<IGuard>[] guards = match_1.getChildrenOfType(IGuard.ELEMENT_TYPE);
		assertTrue(guards.length == 1);
		assertEquals(grd1_1_2_1, guards[0].getPatternElement());
		assertEquals(grd1_2_2_1, guards[0].getProblemElement());
		
		Matching<IAction>[] actions = match_1.getChildrenOfType(IAction.ELEMENT_TYPE);
		assertTrue(actions.length == 1);
		assertEquals(act1_1_2_1, actions[0].getPatternElement());
		assertEquals(act1_2_2_1, actions[0].getProblemElement());
	}

	/**
	 * Test method for {@link ch.ethz.eventb.internal.pattern.wizards.ComplexMatching#getChildrenOfTypeEvent()}.
	 */
	@Test
	public void testGetChildrenOfTypeEvent() {
		ComplexMatching<IEvent>[] events = machineMatch.getChildrenOfTypeEvent();
		assertTrue(events.length == 3);
		try {
			assertTrue(PatternUtils.isInMatchings(init1_1, init1_2, events));
			assertTrue(PatternUtils.isInMatchings(evt1_1_2, evt1_2_2, events));
			assertTrue(PatternUtils.isInMatchings(evt1_1_3, evt1_2_3, events));
		} catch (RodinDBException e) {
			e.printStackTrace();
			fail("There should be no exception throws");
			return;
		}
		
	}

	/**
	 * Test method for {@link ch.ethz.eventb.internal.pattern.wizards.ComplexMatching#getProblemChildrenOfType(org.rodinp.core.IElementType)}.
	 */
	@Test
	public void testGetProblemChildrenOfType() {
		Collection<IEvent> events = machineMatch.getProblemChildrenOfType(IEvent.ELEMENT_TYPE);
		assertTrue(events.size() == 3);
		assertTrue(events.contains(init1_2));
		assertTrue(events.contains(evt1_2_2));
		assertTrue(events.contains(evt1_2_3));
		
	}

	/**
	 * Test method for {@link ch.ethz.eventb.internal.pattern.wizards.ComplexMatching#getPatternElement(org.rodinp.core.IInternalElement)}.
	 */
	@Test
	public void testGetPatternElementS() {
		assertEquals(evt1_1_2, match_1.getPatternElement());
		assertEquals(grd1_1_2_1, match_2.getPatternElement());
		assertEquals(act1_1_2_1, match_3.getPatternElement());
		assertEquals(x, match_4.getPatternElement());
	}

	/**
	 * Test method for {@link ch.ethz.eventb.internal.pattern.wizards.ComplexMatching#addMatching(org.rodinp.core.IInternalElement, org.rodinp.core.IInternalElement, org.rodinp.core.IInternalElementType)}.
	 */
	@Test
	public void testAddMatchingSSIInternalElementTypeOfS() {
		ComplexMatching<IEvent> matching;
		try {
			matching = new ComplexMatching<IEvent>(evt1_1_3, evt1_2_3, IEvent.ELEMENT_TYPE, machineMatch);
		} catch (RodinDBException e) {
			e.printStackTrace();
			fail("There should be no exception throws");
			return;
		}
		assertTrue(matching.getChildrenOfType(IGuard.ELEMENT_TYPE).length == 0);
		
		matching.addMatching(grd1_1_3_1, grd1_2_3_1, IGuard.ELEMENT_TYPE);
		
		assertTrue(matching.getChildrenOfType(IGuard.ELEMENT_TYPE).length == 1);
		
		matching.addMatching(grd1_1_3_1, grd1_2_3_1, IGuard.ELEMENT_TYPE);
		
		assertTrue(matching.getChildrenOfType(IGuard.ELEMENT_TYPE).length == 2);
		
				
	}

	/**
	 * Test method for {@link ch.ethz.eventb.internal.pattern.wizards.ComplexMatching#addMatching(java.lang.String, java.lang.String, org.rodinp.core.IInternalElementType, boolean)}.
	 */
	@Test
	public void testAddMatchingStringStringIInternalElementTypeOfSBoolean() {
		ComplexMatching<IEvent> matching;
		try {
			matching = new ComplexMatching<IEvent>(evt1_1_3, evt1_2_3, IEvent.ELEMENT_TYPE, machineMatch);
		} catch (RodinDBException e) {
			e.printStackTrace();
			fail("There should be no exception throws");
			return;
		}
		assertTrue(matching.getChildrenOfType(IGuard.ELEMENT_TYPE).length == 0);
		
		matching.addMatching("grd1_1_3_1", "grd1_2_3_1", IGuard.ELEMENT_TYPE, true);
		
		assertTrue(matching.getChildrenOfType(IGuard.ELEMENT_TYPE).length == 1);
		
		matching.addMatching("grd1_1_3_1", "grd1_2_3_1", IGuard.ELEMENT_TYPE, true);
		
		assertTrue(matching.getChildrenOfType(IGuard.ELEMENT_TYPE).length == 2);
		
	}

	/**
	 * Test method for {@link ch.ethz.eventb.internal.pattern.wizards.ComplexMatching#addComplexMatching(org.rodinp.core.IInternalElement, org.rodinp.core.IInternalElement, org.rodinp.core.IInternalElementType)}.
	 */
	@Test
	public void testAddComplexMatchingSSIInternalElementTypeOfS() {
		ComplexMatching<IEvent> matching;
		try {
			matching = new ComplexMatching<IEvent>(evt1_1_3, evt1_2_3, IEvent.ELEMENT_TYPE, machineMatch);
		} catch (RodinDBException e) {
			e.printStackTrace();
			fail("There should be no exception throws");
			return;
		}
		assertTrue(matching.getChildrenOfType(IGuard.ELEMENT_TYPE).length == 0);
		
		matching.addComplexMatching(grd1_1_3_1, grd1_2_3_1, IGuard.ELEMENT_TYPE);
		
		assertTrue(matching.getChildrenOfType(IGuard.ELEMENT_TYPE).length == 1);
		
		matching.addComplexMatching(grd1_1_3_1, grd1_2_3_1, IGuard.ELEMENT_TYPE);
		
		assertTrue(matching.getChildrenOfType(IGuard.ELEMENT_TYPE).length == 2);
	}

	/**
	 * Test method for {@link ch.ethz.eventb.internal.pattern.wizards.ComplexMatching#addComplexMatching(java.lang.String, java.lang.String, org.rodinp.core.IInternalElementType, boolean)}.
	 */
	@Test
	public void testAddComplexMatchingStringStringIInternalElementTypeOfSBoolean() {
		ComplexMatching<IEvent> matching;
		try {
			matching = new ComplexMatching<IEvent>(evt1_1_3, evt1_2_3, IEvent.ELEMENT_TYPE, machineMatch);
		} catch (RodinDBException e) {
			e.printStackTrace();
			fail("There should be no exception throws");
			return;
		}
		assertTrue(matching.getChildrenOfType(IGuard.ELEMENT_TYPE).length == 0);
		
		matching.addComplexMatching("grd1_1_3_1", "grd1_2_3_1", IGuard.ELEMENT_TYPE, true);
		
		assertTrue(matching.getChildrenOfType(IGuard.ELEMENT_TYPE).length == 1);
		
		matching.addComplexMatching("grd1_1_3_1", "grd1_2_3_1", IGuard.ELEMENT_TYPE, true);
		
		assertTrue(matching.getChildrenOfType(IGuard.ELEMENT_TYPE).length == 2);
	}

	/**
	 * Test method for {@link ch.ethz.eventb.internal.pattern.wizards.ComplexMatching#removeMatching(ch.ethz.eventb.internal.pattern.wizards.Matching)}.
	 */
	@Test
	public void testRemoveMatching() {
		ComplexMatching<IEvent>[] matchings = machineMatch.getChildrenOfTypeEvent();
		assertTrue(matchings.length == 3);
		
		machineMatch.removeMatching(matchings[0]);
		assertTrue(machineMatch.getChildrenOfTypeEvent().length == 2);
		try {
			assertFalse(PatternUtils.isInMatchings(matchings[0].getPatternElement(), matchings[0].getProblemElement(), machineMatch.getChildrenOfTypeEvent()));
		} catch (RodinDBException e) {
			e.printStackTrace();
			fail("There should be no exception throws");
			return;
		}
		
		machineMatch.removeMatching(matchings[1]);
		assertTrue(machineMatch.getChildrenOfTypeEvent().length == 1);
		try {
			assertFalse(PatternUtils.isInMatchings(matchings[1].getPatternElement(), matchings[1].getProblemElement(), machineMatch.getChildrenOfTypeEvent()));
		} catch (RodinDBException e) {
			e.printStackTrace();
			fail("There should be no exception throws");
			return;
		}
		
		machineMatch.removeMatching(matchings[2]);
		assertTrue(machineMatch.getChildrenOfTypeEvent().length == 0);
		
	}

}
