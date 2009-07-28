package ch.ethz.eventb.internal.pattern.tests;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eventb.core.IAction;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IVariable;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.junit.Test;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

import ch.ethz.eventb.internal.pattern.PatternUtils;
import ch.ethz.eventb.internal.pattern.wizards.ComplexMatching;
import ch.ethz.eventb.internal.pattern.wizards.IComplexMatching;
import ch.ethz.eventb.internal.pattern.wizards.Matching;

public class PatternUtilsTests extends EventBTests {
	

	/**
	 * Test method for {@link ch.ethz.eventb.internal.pattern.PatternUtils#getRodinProjects()}.
	 */
	@Test
	public void testGetRodinProjects() {
		IRodinProject[] projects;
		
		try {
			projects = PatternUtils.getRodinProjects();
		} catch (RodinDBException e) {
			e.printStackTrace();
			fail("There should be no exception throws");
			return;
		}
		assertTrue("There should be 4 projects", projects.length == 4);
		List<IRodinProject> list = new ArrayList<IRodinProject>();
		for(IRodinProject p : projects)
			list.add(p);
		
		assertTrue("P1 should be in the array", list.contains(P1.getRodinProject()));
		assertTrue("P2 should be in the array", list.contains(P2.getRodinProject()));
		assertTrue("P3 should be in the array", list.contains(P3.getRodinProject()));
		assertTrue("P4 should be in the array", list.contains(P4.getRodinProject()));
	}

	/**
	 * Test method for {@link ch.ethz.eventb.internal.pattern.PatternUtils#getRodinProject(java.lang.String)}.
	 */
	@Test
	public void testGetRodinProject() {
		IRodinProject project;

		// Test get project successfully.
		project = PatternUtils.getRodinProject("P1");
		
		assertNotNull("There should be a handle with the name P1", project);
		assertTrue("The project must exist", project.exists());
		assertEquals("Project should be P1", project, P1.getRodinProject());

		// Test get project fail.
		
		project =  PatternUtils.getRodinProject("P100");
		assertNotNull("There should be a handle with the name P100", project);
		assertFalse("The project must not exist", project.exists());
	}

	/**
	 * Test method for {@link ch.ethz.eventb.internal.pattern.PatternUtils#getRefinementEvents(org.eventb.core.IEvent, org.eventb.core.IMachineRoot)}.
	 */
	@Test
	public void testGetRefinementEvents() {
	
		IEvent[] events;
		
		try {
			events = PatternUtils.getRefinementEvents(evt1_1_1 , mch1_2);
		} catch (RodinDBException e) {
			e.printStackTrace();
			fail("There should be no exception throws");
			return;
		}
		
		assertTrue("There should be 4 projects", events.length == 2);
		
		List<IEvent> list = Arrays.asList(events);
				
		assertTrue("evt1_2_1 should be in the array", list.contains(evt1_2_1));
		assertTrue("evt1_2_1b should be in the array", list.contains(evt1_2_1b));
	}

	/**
	 * Test method for {@link ch.ethz.eventb.internal.pattern.PatternUtils#getElementByLabel(org.rodinp.core.IInternalElementType, java.lang.String, org.rodinp.core.IInternalElement)}.
	 */
	@Test
	public void testGetElementByLabel() {
		// Test with event 
		try {
			assertEquals(evt1_1_1, PatternUtils.getElementByLabel(IEvent.ELEMENT_TYPE, "evt1_1_1", mch1_1));
		} catch (RodinDBException e) {
			e.printStackTrace();
			fail("There should be no exception throws");
			return;
		}
			
		// Test with guard
		try {
			assertEquals(grd1_2_2_1, PatternUtils.getElementByLabel(IGuard.ELEMENT_TYPE, "grd1_2_2_1", evt1_2_2));
		} catch (RodinDBException e) {
			e.printStackTrace();
			fail("There should be no exception throws");
			return;
		}
			
		// Test with wrong type
		try {
			assertNull(PatternUtils.getElementByLabel(IAction.ELEMENT_TYPE, "grd1_2_2_1", evt1_2_2));
		} catch (RodinDBException e) {
			e.printStackTrace();
			fail("There should be no exception throws");
			return;
		}
		
		// Test with wrong parent
		try {
			assertNull(PatternUtils.getElementByLabel(IGuard.ELEMENT_TYPE, "grd1_2_2_1", evt1_2_1));
		} catch (RodinDBException e) {
			e.printStackTrace();
			fail("There should be no exception throws");
			return;
		}
		
		
	}

	/**
	 * Test method for {@link ch.ethz.eventb.internal.pattern.PatternUtils#getElementByIdentifier(org.rodinp.core.IInternalElementType, java.lang.String, org.rodinp.core.IInternalElement)}.
	 */
	@Test
	public void testGetElementByIdentifier() {
		// Test with variable 
		try {
			assertEquals(x, PatternUtils.getElementByIdentifier(IVariable.ELEMENT_TYPE, "x", mch1_1));
		} catch (RodinDBException e) {
			e.printStackTrace();
			fail("There should be no exception throws");
			return;
		}
			
		// Test with carrierSet
		try {
			assertEquals(U, PatternUtils.getElementByIdentifier(ICarrierSet.ELEMENT_TYPE, "U", ctx1_2));
		} catch (RodinDBException e) {
			e.printStackTrace();
			fail("There should be no exception throws");
			return;
		}
			
		// Test with wrong type
		try {
			assertNull(PatternUtils.getElementByIdentifier(IConstant.ELEMENT_TYPE, "U", ctx1_1));
		} catch (RodinDBException e) {
			e.printStackTrace();
			fail("There should be no exception throws");
			return;
		}
		
		// Test with wrong parent
		try {
			assertNull(PatternUtils.getElementByIdentifier(ICarrierSet.ELEMENT_TYPE, "U", ctx1_1));
		} catch (RodinDBException e) {
			e.printStackTrace();
			fail("There should be no exception throws");
			return;
		}
		
		
	}

	/**
	 * Test method for {@link ch.ethz.eventb.internal.pattern.PatternUtils#getRootByName(org.rodinp.core.IInternalElementType, java.lang.String, org.rodinp.core.IRodinProject)}.
	 */
	@Test
	public void testGetRootByName() {
		
		// IMachineRoot
		
		try {
			assertEquals(mch1_1, PatternUtils.getRootByName(IMachineRoot.ELEMENT_TYPE, "mch1_1", P1.getRodinProject()));
		} catch (RodinDBException e) {
			e.printStackTrace();
			fail("There should be no exception throws");
			return;
		}
		
		// IContextRoot
		
		try {
			assertEquals(ctx2_1, PatternUtils.getRootByName(IContextRoot.ELEMENT_TYPE, "ctx2_1", P2.getRodinProject()));
		} catch (RodinDBException e) {
			e.printStackTrace();
			fail("There should be no exception throws");
			return;
		}
	}

	/**
	 * Test method for {@link ch.ethz.eventb.internal.pattern.PatternUtils#getInitMatching(ch.ethz.eventb.internal.pattern.wizards.MatchingMachine)}.
	 */
	@Test
	public void testGetInitMatching() {
		IComplexMatching<IEvent> match;
		try {
			match = PatternUtils.getInitMatching(machineMatch);
		} catch (RodinDBException e) {
			e.printStackTrace();
			fail("There should be no exception throws");
			return;
		}
		assertEquals(init1_1, match.getPatternElement());
		assertEquals(init1_2, match.getProblemElement());
	}

	/**
	 * Test method for {@link ch.ethz.eventb.internal.pattern.PatternUtils#isInMatchings(org.eventb.core.IIdentifierElement, org.eventb.core.IIdentifierElement, ch.ethz.eventb.internal.pattern.wizards.Matching<T>[])}.
	 */
	@Test
	public void testIsInMatchings() {
		try {
			assertTrue(PatternUtils.isInMatchings(evt1_1_2, evt1_2_2, (Matching<IEvent>[])new Matching<?>[] {match_2, match_3, match_1}));
			assertFalse(PatternUtils.isInMatchings(evt1_1_2, evt1_2_2, (Matching<IEvent>[])new Matching<?>[] {match_2, match_3}));
			assertTrue(PatternUtils.isInMatchings(x, u, (Matching<IVariable>[])new Matching<?>[] {match_2, match_3, match_1, match_4}));
			assertFalse(PatternUtils.isInMatchings(x, u, (Matching<IVariable>[])new Matching<?>[] {match_2, match_3, match_1}));

		} catch (RodinDBException e) {
			e.printStackTrace();
			fail("There should be no exception throws");
			return;
		}
	}

	/**
	 * Test method for {@link ch.ethz.eventb.internal.pattern.PatternUtils#isInArray(java.lang.String, org.eventb.core.IVariable[])}.
	 */
	@Test
	public void testIsInArray() {
		assertTrue(PatternUtils.isInArray("u", new IVariable[] {x,y,u,v,p}));
		assertFalse(PatternUtils.isInArray("x", new IVariable[] {y,u,v,p}));
	}

	/**
	 * Test method for {@link ch.ethz.eventb.internal.pattern.PatternUtils#getMatching(org.rodinp.core.IInternalElement, org.rodinp.core.IInternalElement, ch.ethz.eventb.internal.pattern.wizards.Matching<T>[])}.
	 */
	@Test
	public void testGetMatchingTTMatchingOfTArray() {
		assertEquals(match_1, PatternUtils.getMatching(evt1_1_2, evt1_2_2, (Matching<IEvent>[])new Matching<?>[] {match_2, match_3, match_1}));
		assertNull(PatternUtils.getMatching(evt1_1_2, evt1_2_2, (Matching<IEvent>[])new Matching<?>[] {match_2, match_3}));
		assertEquals(match_4, PatternUtils.getMatching(x, u, (Matching<IVariable>[])new Matching<?>[] {match_2, match_3, match_1, match_4}));
		assertNull(PatternUtils.getMatching(x, u, (Matching<IVariable>[])new Matching<?>[] {match_2, match_3, match_1}));

	}

	/**
	 * Test method for {@link ch.ethz.eventb.internal.pattern.PatternUtils#getMatching(java.lang.String, java.lang.String, ch.ethz.eventb.internal.pattern.wizards.Matching<T>[])}.
	 */
	@Test
	public void testGetMatchingStringStringMatchingOfTArray() {
		assertEquals(match_1, PatternUtils.getMatching("evt1_1_2", "evt1_2_2", (Matching<IEvent>[])new Matching<?>[] {match_2, match_3, match_1}));
		assertNull(PatternUtils.getMatching("evt1_1_2", "evt1_2_2", (Matching<IEvent>[])new Matching<?>[] {match_2, match_3}));
		assertEquals(match_4, PatternUtils.getMatching("x", "u", (Matching<IVariable>[])new Matching<?>[] {match_2, match_3, match_1, match_4}));
		assertNull(PatternUtils.getMatching("x", "u", (Matching<IVariable>[])new Matching<?>[] {match_2, match_3, match_1}));

	}

	/**
	 * Test method for {@link ch.ethz.eventb.internal.pattern.PatternUtils#getMatching(org.eventb.core.IEvent, org.eventb.core.IEvent, ch.ethz.eventb.internal.pattern.wizards.ComplexMatching<org.eventb.core.IEvent>[])}.
	 */
	@Test
	public void testGetMatchingIEventIEventComplexMatchingOfIEventArray() {
		assertEquals(match_1, PatternUtils.getMatching("evt1_1_2", "evt1_2_2", (ComplexMatching<IEvent>[])new ComplexMatching<?>[] {machineMatch, match_1}));
		assertNull(PatternUtils.getMatching("evt1_1_2", "evt1_2_2", (ComplexMatching<IEvent>[])new ComplexMatching<?>[] {machineMatch}));
		
	}

	/**
	 * Test method for {@link ch.ethz.eventb.internal.pattern.PatternUtils#getDisplayText(java.lang.Object)}.
	 */
	@Test
	public void testGetDisplayText() {
		
		// test predicate element (guard)
		assertEquals("y ≠ 0", PatternUtils.getDisplayText(grd1_1_2_1));
		// test assignment element (action)
		assertEquals("x :∈ {a, b}", PatternUtils.getDisplayText(act1_1_1_1));
		// test identifier element (constant)
		assertEquals("c", PatternUtils.getDisplayText(c));
		// test labeled element (event)
		assertEquals("evt1_1_3", PatternUtils.getDisplayText(evt1_1_3));
		// test rodin element (context root)
		assertEquals("ctx1_1", PatternUtils.getDisplayText(ctx1_1));

	}

	/**
	 * Test method for {@link ch.ethz.eventb.internal.pattern.PatternUtils#toStringArray(java.util.ArrayList)}.
	 */
	@Test
	public void testToStringArray() {
		ArrayList<IRodinElement> list = new ArrayList<IRodinElement>();
		list.add(a);
		list.add(b);
		list.add(c);
		list.add(d);
		
		assertTrue(PatternUtils.toStringArray(list).length == 4);
		
		assertEquals("a", PatternUtils.toStringArray(list)[0]);
		assertEquals("b", PatternUtils.toStringArray(list)[1]);
		assertEquals("c", PatternUtils.toStringArray(list)[2]);
		assertEquals("d", PatternUtils.toStringArray(list)[3]);
	}

	/**
	 * Test method for {@link ch.ethz.eventb.internal.pattern.PatternUtils#getRodinContext(org.rodinp.core.IRodinProject, java.lang.String)}.
	 */
	@Test
	public void testGetRodinContext() {
		assertEquals(ctx1_1, PatternUtils.getRodinContext(P1.getRodinProject(), "ctx1_1"));
		assertNull(PatternUtils.getRodinContext(P3.getRodinProject(), "ctx2_2"));
	}

	/**
	 * Test method for {@link ch.ethz.eventb.internal.pattern.PatternUtils#renameLabel(java.lang.String, ch.ethz.eventb.internal.pattern.wizards.Renaming)}.
	 */
	@Test
	public void testRenameLabel() {
		
		// test events
		try {
			assertEquals("evt1_1_3_renamed", PatternUtils.renameLabel("evt1_1_3", eventRenaming));
			assertEquals("evt1_3_1_renamed", PatternUtils.renameLabel("evt1_3_1", eventRenaming));
			assertEquals("testdefault", PatternUtils.renameLabel("testdefault", eventRenaming));
		} catch (RodinDBException e) {
			e.printStackTrace();
			fail("There should be no exception throws");
			return;
		}
	}


	/**
	 * Test method for {@link ch.ethz.eventb.internal.pattern.PatternUtils#substitute(java.lang.String, java.lang.String, java.lang.String, org.eventb.core.ast.FormulaFactory)}.
	 */
	@Test
	public void testSubstituteStringStringStringFormulaFactory() {
		try {
			assertEquals("x ≔ b", PatternUtils.substitute("x ≔ a", "a", "b", FormulaFactory.getDefault()));
			assertEquals("y=TRUE⇔(∀z·z∈ℕ⇒z≠1)", PatternUtils.substitute("z = TRUE ⇔ (∀z·z∈ℕ ⇒ z ≠ 1)", "z", "y", FormulaFactory.getDefault()));
			
		} catch (RodinDBException e) {
			e.printStackTrace();
			fail("There should be no exception throws");
			return;
		}
	}

	/**
	 * Test method for {@link ch.ethz.eventb.internal.pattern.PatternUtils#substitute(java.lang.String, java.util.Map, org.eventb.core.ast.FormulaFactory)}.
	 */
	@Test
	public void testSubstituteStringMapOfFreeIdentifierExpressionFormulaFactory() {
		try {
			FormulaFactory ff = FormulaFactory.getDefault();
			Map<FreeIdentifier, Expression> map = new HashMap<FreeIdentifier, Expression>();
			map.put(ff.makeFreeIdentifier("a", null), ff.makeFreeIdentifier("b", null));
			map.put(ff.makeFreeIdentifier("z", null), ff.makeFreeIdentifier("y", null));
			assertEquals("x ≔ b", PatternUtils.substitute("x ≔ a", map, ff));
			assertEquals("y=TRUE⇔(∀z·z∈ℕ⇒z≠1)", PatternUtils.substitute("z = TRUE ⇔ (∀z·z∈ℕ ⇒ z ≠ 1)", map, ff));
			
		} catch (RodinDBException e) {
			e.printStackTrace();
			fail("There should be no exception throws");
			return;
		}
	}



	/**
	 * Test method for {@link ch.ethz.eventb.internal.pattern.PatternUtils#unsetExtended(org.eventb.core.IEvent)}.
	 */
	@Test
	public void testUnsetExtended() {
		try {
			// extended event becomes unextended
			assertTrue(evt1_2_2.isExtended());
			PatternUtils.unsetExtended(evt1_2_2);
			assertFalse(evt1_2_2.isExtended());
			
			// unextended event remains unextended
			assertFalse(evt1_2_1.isExtended());
			PatternUtils.unsetExtended(evt1_2_1);
			assertFalse(evt1_2_1.isExtended());
		} catch (RodinDBException e) {
			e.printStackTrace();
			fail("There should be no exception throws");
			return;
		}
	}

}
