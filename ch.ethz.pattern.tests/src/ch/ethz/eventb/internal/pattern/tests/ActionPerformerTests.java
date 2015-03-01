/**
 * 
 */
package ch.ethz.eventb.internal.pattern.tests;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import junit.framework.TestCase;

import org.junit.Test;

import ch.ethz.eventb.internal.pattern.ActionPerformer;

/**
 * @author fuersta
 *
 */
public class ActionPerformerTests extends TestCase {

	protected ActionListener listener_1;
	
	boolean actionPerformed_1;
	
	protected ActionListener listener_2;
	
	boolean actionPerformed_2;
	
	protected ActionPerformer performer;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		listener_1 = new ActionListener() {
		
			public void actionPerformed(ActionEvent e) {
					actionPerformed_1 = true;
			}
		};
		
		listener_2 = new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
					actionPerformed_2 = true;
			}
		};
		
		actionPerformed_1 = false;
		actionPerformed_2 = false;
	}

	/**
	 * Test method for {@link ch.ethz.eventb.internal.pattern.ActionPerformer#performAction()}.
	 */
	@Test
	public void testPerformAction() {

		performer = new ActionPerformer();
		
		performer.addListener(listener_1);
		performer.addListener(listener_2);
		
		assertFalse(actionPerformed_1);
		assertFalse(actionPerformed_2);
		
		performer.performAction();
		
		assertTrue(actionPerformed_1);
		assertTrue(actionPerformed_2);
	}

}
