package ch.ethz.eventb.pattern.example.handlers;


import java.util.HashMap;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eventb.core.IAction;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IParameter;
import org.eventb.core.IVariable;
import org.eventb.core.IWitness;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;

import ch.ethz.eventb.pattern.DataFactory;
import ch.ethz.eventb.pattern.IData;
import ch.ethz.eventb.pattern.IMachineGenerator;
import ch.ethz.eventb.pattern.example.PatternUtils;


/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class GeneratorHandler extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public GeneratorHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
	
		
		try {
			
			// get the Rodin Database
			IRodinDB rodinDB = RodinCore.getRodinDB();
			
			// get the problem and pattern project 
			IRodinProject problemProject = rodinDB.getRodinProject("Negotiation-090223");
			IRodinProject patternProject = rodinDB.getRodinProject("PatternChannel-090220");
			
			// get a map of all problem machines 
			HashMap<String,IMachineRoot> problemMachines = new HashMap<String, IMachineRoot>();
		
			for (IRodinElement element : problemProject.getChildren()) {
				if (element instanceof IRodinFile) {
					IInternalElement root = ((IRodinFile) element).getRoot();
			        if (root instanceof IMachineRoot) {
			        	problemMachines.put(root.getElementName(), (IMachineRoot) root);
			        }
				}
			}
			
			// get a map of all pattern machines 
			HashMap<String,IMachineRoot> patternMachines = new HashMap<String, IMachineRoot>();
			for (IRodinElement element : patternProject.getChildren()) {
				if (element instanceof IRodinFile) {
					IInternalElement root = ((IRodinFile) element).getRoot();
			        if (root instanceof IMachineRoot) {
			        	patternMachines.put(root.getElementName(), (IMachineRoot) root);
			        }
				}
			}
							
						
			// get a data instance
			IData data = DataFactory.createData();
			
			// SET PROBLEM MACHINE
			IMachineRoot problemMachine = problemMachines.get("protocol");
			data.changeProblemMachine(problemMachine);
			
			
			// SET PATTERN ABSTRACT MACHINE
			IMachineRoot patternAbstractMachine = patternMachines.get("ChannelInterface");
			data.changePatternAbstractMachine(patternAbstractMachine);
			
			
			
			// get the pattern context
			IContextRoot patternContext = patternAbstractMachine.getSeesClauses()[0].getSeenContextRoot();
			
			
			// SET CONTEXT MATCHINGS
			
			// MESSAGE  -->  PROPOSAL
			ICarrierSet carrierSet = PatternUtils.getElementByIdentifier(ICarrierSet.ELEMENT_TYPE, "MESSAGE", patternContext);
			data.updateMatching(carrierSet, "PROPOSAL");
			
			// SET VARIABLE MATCHINGS
			
			// sent_count  -->  B_sent_proposal_count
			data.addMatching(PatternUtils.getElementByIdentifier(IVariable.ELEMENT_TYPE, "sent_count", patternAbstractMachine),
					PatternUtils.getElementByIdentifier(IVariable.ELEMENT_TYPE, "B_sent_proposal_count", problemMachine));
			
			// received_count  -->  S_received_proposal_count
			data.addMatching(PatternUtils.getElementByIdentifier(IVariable.ELEMENT_TYPE, "received_count", patternAbstractMachine),
					PatternUtils.getElementByIdentifier(IVariable.ELEMENT_TYPE, "S_received_proposal_count", problemMachine));
			
			// SET EVENT MATCHINGS
			IEvent patternEvent;
			IEvent problemEvent;
			
			// INITIALISATION  -->  INITIALISATION
			patternEvent = PatternUtils.getElementByLabel(IEvent.ELEMENT_TYPE, "INITIALISATION", patternAbstractMachine);
			problemEvent = PatternUtils.getElementByLabel(IEvent.ELEMENT_TYPE, "INITIALISATION", problemMachine);
			
			data.addMatching(patternEvent, problemEvent);
			{
			// SET PARAMETERS
				
			// SET GUARDS
				
			// SET ACTIONS
				
				// act1  -->  act5
				data.addMatching(PatternUtils.getElementByLabel(IAction.ELEMENT_TYPE, "act1", patternEvent),
						PatternUtils.getElementByLabel(IAction.ELEMENT_TYPE, "act5", problemEvent));
				
				// act2  -->  act6
				data.addMatching(PatternUtils.getElementByLabel(IAction.ELEMENT_TYPE, "act2", patternEvent),
						PatternUtils.getElementByLabel(IAction.ELEMENT_TYPE, "act6", problemEvent));
			}
			
			// sends  -->  B_sends_proposal
			patternEvent = PatternUtils.getElementByLabel(IEvent.ELEMENT_TYPE, "sends", patternAbstractMachine);
			problemEvent = PatternUtils.getElementByLabel(IEvent.ELEMENT_TYPE, "B_sends_proposal", problemMachine);
			
			data.addMatching(patternEvent, problemEvent);
			{
			// SET PARAMETERS
				
				// msg  -->  prop
				data.addMatching(PatternUtils.getElementByIdentifier(IParameter.ELEMENT_TYPE, "msg", patternEvent),
						PatternUtils.getElementByIdentifier(IParameter.ELEMENT_TYPE, "prop", problemEvent));
				
			// SET GUARDS
				
				// grd1  -->  grd1
				data.addMatching(PatternUtils.getElementByLabel(IGuard.ELEMENT_TYPE, "grd1", patternEvent),
						PatternUtils.getElementByLabel(IGuard.ELEMENT_TYPE, "grd1", problemEvent));
				
			// SET ACTIONS
				
				// act1  -->  act3
				data.addMatching(PatternUtils.getElementByLabel(IAction.ELEMENT_TYPE, "act1", patternEvent),
						PatternUtils.getElementByLabel(IAction.ELEMENT_TYPE, "act3", problemEvent));
				
			}
			
			// receives  -->  S_receives_proposal
			patternEvent = PatternUtils.getElementByLabel(IEvent.ELEMENT_TYPE, "receives", patternAbstractMachine);
			problemEvent = PatternUtils.getElementByLabel(IEvent.ELEMENT_TYPE, "S_receives_proposal", problemMachine);
			
			data.addMatching(patternEvent, problemEvent);
			{
			// SET PARAMETERS
				
				// msg  -->  prop
				data.addMatching(PatternUtils.getElementByIdentifier(IParameter.ELEMENT_TYPE, "msg", patternEvent),
						PatternUtils.getElementByIdentifier(IParameter.ELEMENT_TYPE, "prop", problemEvent));
				
			// SET GUARDS
				
				// grd1  -->  grd1
				data.addMatching(PatternUtils.getElementByLabel(IGuard.ELEMENT_TYPE, "grd1", patternEvent),
						PatternUtils.getElementByLabel(IGuard.ELEMENT_TYPE, "grd1", problemEvent));
				
			// SET ACTIONS
				
				// act1  -->  act3
				data.addMatching(PatternUtils.getElementByLabel(IAction.ELEMENT_TYPE, "act1", patternEvent),
						PatternUtils.getElementByLabel(IAction.ELEMENT_TYPE, "act3", problemEvent));
				
			}
			
			
			// SET PATTERN REFINEMENT MACHINE
			IMachineRoot patternRefinementMachine = patternMachines.get("EO");
			data.changePatternRefinementMachine(patternRefinementMachine);
			
			// RENAME THE VARIABLES
			
			// channel  -->  channel_proposals
			data.updateRenaming(PatternUtils.getElementByIdentifier(IVariable.ELEMENT_TYPE, "channel", patternRefinementMachine), "channel_proposals");
			
			// sent  -->  sent_proposals
			data.updateRenaming(PatternUtils.getElementByIdentifier(IVariable.ELEMENT_TYPE, "sent", patternRefinementMachine), "sent_proposals");
			
			// received  -->  received_proposals
			data.updateRenaming(PatternUtils.getElementByIdentifier(IVariable.ELEMENT_TYPE, "received", patternRefinementMachine), "received_proposals");
			
			// sent_count  -->  B_sent_proposal_count
			data.updateRenaming(PatternUtils.getElementByIdentifier(IVariable.ELEMENT_TYPE, "sent_count", patternRefinementMachine), "B_sent_proposal_count");
			
			// received_count  -->  S_received_proposal_count
			data.updateRenaming(PatternUtils.getElementByIdentifier(IVariable.ELEMENT_TYPE, "received_count", patternRefinementMachine), "S_received_proposal_count");
			
			
			
			// SET REPLACEMENT FOR WITNESSES
			
			// msg  -->  sent(i)  (in event receives)
			IEvent refinementEvent = PatternUtils.getElementByLabel(IEvent.ELEMENT_TYPE, "receives", patternRefinementMachine);
			data.updateReplacementOf(PatternUtils.getElementByLabel(IWitness.ELEMENT_TYPE, "msg", refinementEvent), "sent(i)");
			
			// GENERATE MACHINE
			IMachineGenerator generator = data.createNewMachineGenerator();
			
			problemMachine = generator.generateMachine("protocol_EO", false, null);
			
			
			
			
			
			//// SECOND TIME
			
			
			
			
			// SET PROBLEM MACHINE
			data.changeProblemMachine(problemMachine);
			
			// SET PATTERN ABSTRACT MACHINE
			// the same as before, nothing to change
			
			// get the pattern context
			// the same as before, nothing to change
			
			// SET CONTEXT MATCHINGS
			
			// MESSAGE  -->  PROPOSAL
			carrierSet = PatternUtils.getElementByIdentifier(ICarrierSet.ELEMENT_TYPE, "MESSAGE", patternContext);
			data.updateMatching(carrierSet, "BOOL");
			
			// SET VARIABLE MATCHINGS
			
			// sent_count  -->  B_sent_proposal_count
			data.addMatching(PatternUtils.getElementByIdentifier(IVariable.ELEMENT_TYPE, "sent_count", patternAbstractMachine),
					PatternUtils.getElementByIdentifier(IVariable.ELEMENT_TYPE, "S_sent_answer_count", problemMachine));
			
			// received_count  -->  S_received_proposal_count
			data.addMatching(PatternUtils.getElementByIdentifier(IVariable.ELEMENT_TYPE, "received_count", patternAbstractMachine),
					PatternUtils.getElementByIdentifier(IVariable.ELEMENT_TYPE, "B_received_answer_count", problemMachine));
			
			// SET EVENT MATCHINGS
						
			// INITIALISATION  -->  INITIALISATION
			patternEvent = PatternUtils.getElementByLabel(IEvent.ELEMENT_TYPE, "INITIALISATION", patternAbstractMachine);
			problemEvent = PatternUtils.getElementByLabel(IEvent.ELEMENT_TYPE, "INITIALISATION", problemMachine);
			
			data.addMatching(patternEvent, problemEvent);
			{
			// SET PARAMETERS
				
			// SET GUARDS
				
			// SET ACTIONS
				
				// act1  -->  act5
				data.addMatching(PatternUtils.getElementByLabel(IAction.ELEMENT_TYPE, "act1", patternEvent),
						PatternUtils.getElementByLabel(IAction.ELEMENT_TYPE, "act5", problemEvent));
				
				// act2  -->  act4
				data.addMatching(PatternUtils.getElementByLabel(IAction.ELEMENT_TYPE, "act2", patternEvent),
						PatternUtils.getElementByLabel(IAction.ELEMENT_TYPE, "act4", problemEvent));
			}
			
			// sends  -->  S_sends_acceptance
			patternEvent = PatternUtils.getElementByLabel(IEvent.ELEMENT_TYPE, "sends", patternAbstractMachine);
			problemEvent = PatternUtils.getElementByLabel(IEvent.ELEMENT_TYPE, "S_sends_acceptance", problemMachine);
			
			data.addMatching(patternEvent, problemEvent);
			{
			// SET PARAMETERS
				
				// msg  -->  answer
				data.addMatching(PatternUtils.getElementByIdentifier(IParameter.ELEMENT_TYPE, "msg", patternEvent),
						PatternUtils.getElementByIdentifier(IParameter.ELEMENT_TYPE, "answer", problemEvent));
				
			// SET GUARDS
			
			// SET ACTIONS
				
				// act1  -->  act1
				data.addMatching(PatternUtils.getElementByLabel(IAction.ELEMENT_TYPE, "act1", patternEvent),
						PatternUtils.getElementByLabel(IAction.ELEMENT_TYPE, "act1", problemEvent));
				
			}
			
			// sends  -->  S_sends_rejection
			patternEvent = PatternUtils.getElementByLabel(IEvent.ELEMENT_TYPE, "sends", patternAbstractMachine);
			problemEvent = PatternUtils.getElementByLabel(IEvent.ELEMENT_TYPE, "S_sends_rejection", problemMachine);
			
			data.addMatching(patternEvent, problemEvent);
			{
			// SET PARAMETERS
				
				// msg  -->  answer
				data.addMatching(PatternUtils.getElementByIdentifier(IParameter.ELEMENT_TYPE, "msg", patternEvent),
						PatternUtils.getElementByIdentifier(IParameter.ELEMENT_TYPE, "answer", problemEvent));
				
			// SET GUARDS
				
			// SET ACTIONS
				
				// act1  -->  act1
				data.addMatching(PatternUtils.getElementByLabel(IAction.ELEMENT_TYPE, "act1", patternEvent),
						PatternUtils.getElementByLabel(IAction.ELEMENT_TYPE, "act1", problemEvent));
				
			}
			
			// receives  -->  B_receives_acceptance_right
			patternEvent = PatternUtils.getElementByLabel(IEvent.ELEMENT_TYPE, "receives", patternAbstractMachine);
			problemEvent = PatternUtils.getElementByLabel(IEvent.ELEMENT_TYPE, "B_receives_acceptance_right", problemMachine);
			
			data.addMatching(patternEvent, problemEvent);
			{
			// SET PARAMETERS
				
				// msg  -->  answer
				data.addMatching(PatternUtils.getElementByIdentifier(IParameter.ELEMENT_TYPE, "msg", patternEvent),
						PatternUtils.getElementByIdentifier(IParameter.ELEMENT_TYPE, "answer", problemEvent));
				
			// SET GUARDS
				
			// SET ACTIONS
				
				// act1  -->  act1
				data.addMatching(PatternUtils.getElementByLabel(IAction.ELEMENT_TYPE, "act1", patternEvent),
						PatternUtils.getElementByLabel(IAction.ELEMENT_TYPE, "act1", problemEvent));
				
			}
			
			// receives  -->  B_receives_acceptance_wrong
			patternEvent = PatternUtils.getElementByLabel(IEvent.ELEMENT_TYPE, "receives", patternAbstractMachine);
			problemEvent = PatternUtils.getElementByLabel(IEvent.ELEMENT_TYPE, "B_receives_acceptance_wrong", problemMachine);
			
			data.addMatching(patternEvent, problemEvent);
			{
			// SET PARAMETERS
				
				// msg  -->  answer
				data.addMatching(PatternUtils.getElementByIdentifier(IParameter.ELEMENT_TYPE, "msg", patternEvent),
						PatternUtils.getElementByIdentifier(IParameter.ELEMENT_TYPE, "answer", problemEvent));
				
			// SET GUARDS
				
			// SET ACTIONS
				
				// act1  -->  act1
				data.addMatching(PatternUtils.getElementByLabel(IAction.ELEMENT_TYPE, "act1", patternEvent),
						PatternUtils.getElementByLabel(IAction.ELEMENT_TYPE, "act1", problemEvent));
				
			}
			
			// receives  -->  B_receives_rejection
			patternEvent = PatternUtils.getElementByLabel(IEvent.ELEMENT_TYPE, "receives", patternAbstractMachine);
			problemEvent = PatternUtils.getElementByLabel(IEvent.ELEMENT_TYPE, "B_receives_rejection", problemMachine);
			
			data.addMatching(patternEvent, problemEvent);
			{
			// SET PARAMETERS
				
				// msg  -->  answer
				data.addMatching(PatternUtils.getElementByIdentifier(IParameter.ELEMENT_TYPE, "msg", patternEvent),
						PatternUtils.getElementByIdentifier(IParameter.ELEMENT_TYPE, "answer", problemEvent));
				
			// SET GUARDS
				
			// SET ACTIONS
				
				// act1  -->  act1
				data.addMatching(PatternUtils.getElementByLabel(IAction.ELEMENT_TYPE, "act1", patternEvent),
						PatternUtils.getElementByLabel(IAction.ELEMENT_TYPE, "act1", problemEvent));
				
			}
			
			
			// SET PATTERN REFINEMENT MACHINE
			// the same as before, but has to be reset
			data.changePatternRefinementMachine(patternRefinementMachine);
			
			// RENAME THE VARIABLES
			
			// channel  -->  channel_proposals
			data.updateRenaming(PatternUtils.getElementByIdentifier(IVariable.ELEMENT_TYPE, "channel", patternRefinementMachine), "channel_answers");
			
			// sent  -->  sent_proposals
			data.updateRenaming(PatternUtils.getElementByIdentifier(IVariable.ELEMENT_TYPE, "sent", patternRefinementMachine), "sent_answers");
			
			// received  -->  received_proposals
			data.updateRenaming(PatternUtils.getElementByIdentifier(IVariable.ELEMENT_TYPE, "received", patternRefinementMachine), "received_answers");
			
			// sent_count  -->  S_sent_answer_count
			data.updateRenaming(PatternUtils.getElementByIdentifier(IVariable.ELEMENT_TYPE, "sent_count", patternRefinementMachine), "S_sent_answer_count");
			
			// received_count  -->  B_received_answer_count
			data.updateRenaming(PatternUtils.getElementByIdentifier(IVariable.ELEMENT_TYPE, "received_count", patternRefinementMachine), "B_received_answer_count");
			
			
			// SET REPLACEMENT FOR WITNESSES
			
			// msg  -->  sent(i)  (in event receives)
			refinementEvent = PatternUtils.getElementByLabel(IEvent.ELEMENT_TYPE, "receives", patternRefinementMachine);
			data.updateReplacementOf(PatternUtils.getElementByLabel(IWitness.ELEMENT_TYPE, "msg", refinementEvent), "sent(i)");
			
			// GENERATE MACHINE
			
			
			generator.generateMachine("protocol_EO_ref", false, null);
			
			
		} catch (Exception e) {
			System.err.println(e);
		}
			
		
		return null;
	}
	


}
