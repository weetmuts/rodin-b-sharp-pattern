/**
 * 
 */
package ch.ethz.eventb.internal.pattern.tests;

import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IEvent;
import org.eventb.core.IParameter;
import org.eventb.core.IVariable;
import org.junit.Test;

import ch.ethz.eventb.internal.pattern.Pair;



/**
 * @author fuersta
 *
 */
public class PairTests extends EventBTests {

	protected Pair<IEvent, IEvent> firstPair;
	
	protected Pair<IEvent, IEvent> secondPair;
	
	protected Pair<IVariable, IParameter> thirdPair;
	
	protected Pair<IConstant, ICarrierSet> fourthPair;
	
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		firstPair = new Pair<IEvent, IEvent>(evt1_1_1, evt1_2_1);
		
		secondPair =  new Pair<IEvent, IEvent>(evt1_1_2, evt1_2_2);
		
		thirdPair =  new Pair<IVariable, IParameter>(x, s);
		
		fourthPair = new Pair<IConstant, ICarrierSet>(a, S);
		
	}

	/**
	 * Test method for {@link ch.ethz.eventb.internal.pattern.Pair#hashCode()}.
	 */
	@Test
	public void testHashCode() {
		assertFalse(firstPair.hashCode() == secondPair.hashCode());
		assertTrue(thirdPair.hashCode() == thirdPair.hashCode());
	}

	/**
	 * Test method for {@link ch.ethz.eventb.internal.pattern.Pair#Pair(java.lang.Object, java.lang.Object)}.
	 */
	@Test
	public void testPair() {
		assertTrue(firstPair.fst == evt1_1_1);
		assertTrue(firstPair.snd == evt1_2_1);
	}

	/**
	 * Test method for {@link ch.ethz.eventb.internal.pattern.Pair#getFirst()}.
	 */
	@Test
	public void testGetFirst() {
		assertTrue(secondPair.getFirst() == secondPair.fst);
	}

	/**
	 * Test method for {@link ch.ethz.eventb.internal.pattern.Pair#getSecond()}.
	 */
	@Test
	public void testGetSecond() {
		assertTrue(thirdPair.getSecond() == thirdPair.getSecond());
	}

	/**
	 * Test method for {@link ch.ethz.eventb.internal.pattern.Pair#setFirst(java.lang.Object)}.
	 */
	@Test
	public void testSetFirst() {
		
		assertFalse(firstPair.fst == evt1_2_1b);
		firstPair.setFirst(evt1_2_1b);
		assertTrue(firstPair.fst == evt1_2_1b);
		
	}

	/**
	 * Test method for {@link ch.ethz.eventb.internal.pattern.Pair#setSecond(java.lang.Object)}.
	 */
	@Test
	public void testSetSecond() {
		
		assertFalse(secondPair.snd == evt1_3_4);
		secondPair.setSecond(evt1_3_4);
		assertTrue(secondPair.snd == evt1_3_4);
	}


	/**
	 * Test method for {@link ch.ethz.eventb.internal.pattern.Pair#equals(java.lang.Object)}.
	 */
	@Test
	public void testEqualsObject() {

		assertFalse(secondPair.equals(firstPair));
		
		secondPair.setFirst(firstPair.getFirst());
		secondPair.setSecond(firstPair.getSecond());
		
		assertTrue(secondPair.equals(firstPair));
		assertTrue(firstPair.equals(secondPair));

	}

	/**
	 * Test method for {@link ch.ethz.eventb.internal.pattern.Pair#of(java.lang.Object, java.lang.Object)}.
	 */
	@Test
	public void testOf() {
		assertFalse(Pair.of(x, s) == (thirdPair));
		assertTrue(Pair.of(x, s).equals(thirdPair));
	}

}
