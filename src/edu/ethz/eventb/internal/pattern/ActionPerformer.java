package edu.ethz.eventb.internal.pattern;

import java.awt.event.ActionListener;

import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.SafeRunner;


public class ActionPerformer {
	
	ListenerList listeners = new ListenerList();
	
	public void addListener(ActionListener l){
		listeners.add(l);
	}
	
	public void performAction(){
        
		SafeRunner.run(new ISafeRunnable() {
			public void run() throws Exception {
				Object[] listenerArray = listeners.getListeners();
		        for (int i = 0; i < listenerArray.length; ++i) {
		            final ActionListener l = (ActionListener) listenerArray[i];
		            	l.actionPerformed(null);
					
		        }
			}

			public void handleException(Throwable exception) {
			
			}
		});
	}
}
