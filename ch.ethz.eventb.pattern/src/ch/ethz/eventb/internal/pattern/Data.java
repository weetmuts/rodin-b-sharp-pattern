package ch.ethz.eventb.internal.pattern;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import org.eventb.core.IAction;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEvent;
import org.eventb.core.IEventBProject;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IParameter;
import org.eventb.core.IRefinesEvent;
import org.eventb.core.IRefinesMachine;
import org.eventb.core.ISeesContext;
import org.eventb.core.IVariable;
import org.eventb.core.IWitness;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.LanguageVersion;
import org.eventb.core.ast.Predicate;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

import ch.ethz.eventb.pattern.DataException;
import ch.ethz.eventb.pattern.IData;
import ch.ethz.eventb.pattern.IMachineGenerator;
import ch.ethz.eventb.pattern.core.IActionMatching;
import ch.ethz.eventb.pattern.core.ICarrierSetMatching;
import ch.ethz.eventb.pattern.core.IConstantMatching;
import ch.ethz.eventb.pattern.core.IEventMatching;
import ch.ethz.eventb.pattern.core.IGuardMatching;
import ch.ethz.eventb.pattern.core.IParameterMatching;
import ch.ethz.eventb.pattern.core.IPatternRoot;
import ch.ethz.eventb.pattern.core.IVariableMatching;

/**
 * Data class where all the necessary information for applying patterns is stored.
 * Some information is stored redundantly on purpose to speed up access. 
 * @author fuersta
 */
public class Data implements IData {
	
	private FormulaFactory ff;
	
	private Expression defaultExpression;
	
	// pattern
	private IMachineRoot patternAbstractMachine;
	
	private Collection<IEvent> allPatternEvents;
	
	private Collection<IEvent> matchedPatternEvents;
	
	private Collection<IEvent> notMatchedPatternEvents;
	
	private Collection<IVariable> allPatternVariables;
	
	private Collection<IVariable> matchedPatternVariables;
	
	private Collection<IVariable> notMatchedPatternVariables;
	
	private Collection<IVariable> disappearingPatternVariables;
	
	private Collection<IVariable> newPatternRefinementVariables;
	
	private Collection<IVariable> remainingPatternVariables;
	
	private HashMap<String, IVariable> variableRemainings;
	
	private HashMap<String, IVariable> variablesEntries;
		
	// pattern refinement
	private IMachineRoot patternRefinementMachine;
	
	private Collection<IEvent> allPatternRefinementEvents;
	
	private Collection<IEvent> newPatternRefinementEvents;
	
	private Collection<IEvent> refinedPatternRefinementEvents;
	
	private Collection<IEvent> mergedPatternRefinementEvents;
	
	private Collection<IEvent> notMergedPatternRefinementEvents;
	
	private Collection<IVariable> allPatternRefinementVariables;
	
	// problem
	private IMachineRoot problemMachine;
	
	private Collection<IEvent> allProblemEvents;
	
	private Collection<IEvent> matchedProblemEvents;
	
	private Collection<IEvent> notMatchedProblemEvents;
	
	private Collection<IEvent> mergedProblemEvents;
	
	private Collection<IEvent> notMergedProblemEvents;
	
	private Collection<IVariable> allProblemVariables;
	
	private Collection<IVariable> matchedProblemVariables;
	
	private Collection<IVariable> notMatchedProblemVariables;
	
	// inside the pattern
	private Collection<IMachineRoot> intermediatePatternMachines;
	
	private HashMap<IMachineRoot, Collection<IVariable>> intermediateNewVariables;
	
	private HashMap<IMachineRoot, Collection<IVariable>> intermediateDisappearingVariables;
	
	private HashMap<String, IVariable> variableAppearing;
	
	private HashMap<String, IVariable> variableDisappearing;
	
	private HashMap<IMachineRoot, IMachineRoot> refinement;
	
	private HashMap<IMachineRoot, IMachineRoot> abstraction;
	
	private HashMap<IEvent, Collection<IEvent>> eventRefinement;
	
	private HashMap<IEvent, Collection<IEvent>> eventAbstraction;
	
	private HashMap<IEvent, HashMap<IEvent,IEvent>> chainDown;
	
	private HashMap<IEvent, HashSet<IEvent>> allChains;
	
	private HashMap<IMachineRoot, Collection<IInvariant>> relevantInvariants;
	
	private Collection<IVariable> relatedVariables;
	
	private HashMap<IMachineRoot, Integer> machineRank;
	

	// parameters
	private HashMap<IEvent, Collection<IParameter>> allPatternParameters;
	
	private HashMap<IEvent,  Collection<IParameter>> allProblemParameters;
	
	private HashMap<IEvent,  Collection<IParameter>> allPatternRefinementParameters;
	
	private HashMap<IEvent, Collection<IParameter>> matchedParameters;
	
	private HashMap<IEvent, Collection<IParameter>> notMatchedParameters;
	
	// guards
	private HashMap<IEvent, Collection<IGuard>> allPatternGuards;
	
	private HashMap<IEvent, Collection<IGuard>> allProblemGuards;
	
	private HashMap<IEvent, Collection<IGuard>> allPatternRefinementGuards;
	
	private HashMap<IEvent, Collection<IGuard>> matchedGuards;
	
	private HashMap<IEvent, Collection<IGuard>> notMatchedGuards;
	
	// actions
	private HashMap<IEvent, Collection<IAction>> allPatternActions;
	
	private HashMap<IEvent, Collection<IAction>> allProblemActions;
	
	private HashMap<IEvent, Collection<IAction>> allPatternRefinementActions;
	
	private HashMap<IEvent, Collection<IAction>> matchedActions;
	
	private HashMap<IEvent, Collection<IAction>> notMatchedActions;
	
	// matchings
	private HashMap<IEvent, Collection<IEvent>> eventMatchings;
	
	private HashMap<IVariable, IVariable> variableMatchings;
	
	private HashMap<IParameter, IParameter> parameterMatchings;
	
	private HashMap<IGuard, IGuard> guardMatchings;
	
	private HashMap<IAction, IAction> actionMatchings;
	
	// renamings
	private HashMap<IVariable, String> variableRenaming;
	
	private HashMap<ICarrierSet, String> carrierSetRenaming;
	
	private HashMap<IConstant, String> constantRenaming;
	
	private HashMap<IEvent, String> mergedEventRenaming;
	
	private HashMap<IEvent, String> newEventRenaming;
	
	private HashMap<Pair<IEvent, IEvent>, String> matchedEventRenaming;
		
	private HashMap<IVariable, Collection<IVariable>> forwardDependencies;
	
	private HashMap<IVariable, Collection<IVariable>> backwardDependencies;
	
	private HashMap<IVariable, Expression> variableForwardReplacement;
	
	private HashMap<IVariable, Expression> variableBackwardReplacement;
	
	private HashMap<IWitness, Expression> parameterReplacement;
	
	// merging
	private HashMap<IEvent, Collection<IEvent>> eventMerging;
	
	
	/**
	 * Standard constructor. All fields are initialized.
	 */
	public Data() {
		
		ff = FormulaFactory.getDefault();
		defaultExpression = ff.makeFreeIdentifier("error", null);
		
		// initialize the fields
		
		allPatternEvents = new HashSet<IEvent>();
		matchedPatternEvents = new ArrayList<IEvent>();
		notMatchedPatternEvents = new ArrayList<IEvent>();
		allPatternVariables = new HashSet<IVariable>();
		matchedPatternVariables = new ArrayList<IVariable>();
		notMatchedPatternVariables = new ArrayList<IVariable>();
		disappearingPatternVariables = new HashSet<IVariable>();
		newPatternRefinementVariables = new HashSet<IVariable>();
		remainingPatternVariables = new HashSet<IVariable>();
		variableRemainings = new HashMap<String, IVariable>();
		variablesEntries = new HashMap<String, IVariable>();
		allPatternRefinementEvents = new HashSet<IEvent>();
		newPatternRefinementEvents = new HashSet<IEvent>();
		refinedPatternRefinementEvents = new HashSet<IEvent>();
		mergedPatternRefinementEvents = new ArrayList<IEvent>();
		notMergedPatternRefinementEvents =  new ArrayList<IEvent>();
		allPatternRefinementVariables = new HashSet<IVariable>();
		allProblemEvents = new HashSet<IEvent>();
		matchedProblemEvents = new ArrayList<IEvent>();
		notMatchedProblemEvents = new ArrayList<IEvent>();
		mergedProblemEvents = new ArrayList<IEvent>();
		notMergedProblemEvents = new ArrayList<IEvent>();
		allProblemVariables = new HashSet<IVariable>();
		matchedProblemVariables = new ArrayList<IVariable>();
		notMatchedProblemVariables = new ArrayList<IVariable>();
		intermediatePatternMachines = new HashSet<IMachineRoot>();
		intermediateNewVariables = new HashMap<IMachineRoot, Collection<IVariable>>();
		intermediateDisappearingVariables = new HashMap<IMachineRoot, Collection<IVariable>>();
		variableAppearing = new HashMap<String, IVariable>();
		variableDisappearing = new HashMap<String, IVariable>();
		refinement = new HashMap<IMachineRoot, IMachineRoot>();
		abstraction = new HashMap<IMachineRoot, IMachineRoot>();
		eventRefinement = new HashMap<IEvent, Collection<IEvent>>();
		eventAbstraction = new HashMap<IEvent, Collection<IEvent>>();
		chainDown = new HashMap<IEvent, HashMap<IEvent,IEvent>>();
		allChains = new HashMap<IEvent, HashSet<IEvent>>();
		relevantInvariants = new HashMap<IMachineRoot, Collection<IInvariant>>();
		relatedVariables = new HashSet<IVariable>();
		machineRank = new HashMap<IMachineRoot, Integer>();
		
		allPatternParameters = new HashMap<IEvent, Collection<IParameter>>();
		allProblemParameters = new HashMap<IEvent, Collection<IParameter>>();
		allPatternRefinementParameters = new HashMap<IEvent, Collection<IParameter>>();
		matchedParameters = new HashMap<IEvent, Collection<IParameter>>();
		notMatchedParameters = new HashMap<IEvent, Collection<IParameter>>();
		allPatternGuards = new HashMap<IEvent, Collection<IGuard>>();
		allProblemGuards = new HashMap<IEvent, Collection<IGuard>>();
		allPatternRefinementGuards = new HashMap<IEvent, Collection<IGuard>>();
		matchedGuards = new HashMap<IEvent, Collection<IGuard>>();
		notMatchedGuards = new HashMap<IEvent, Collection<IGuard>>();
		allPatternActions = new HashMap<IEvent, Collection<IAction>>();
		allProblemActions = new HashMap<IEvent, Collection<IAction>>();
		allPatternRefinementActions = new HashMap<IEvent, Collection<IAction>>();
		matchedActions = new HashMap<IEvent, Collection<IAction>>();
		notMatchedActions = new HashMap<IEvent, Collection<IAction>>();	
		eventMatchings = new HashMap<IEvent, Collection<IEvent>>();
		variableMatchings = new HashMap<IVariable, IVariable>();
		parameterMatchings = new HashMap<IParameter, IParameter>();
		guardMatchings = new HashMap<IGuard, IGuard>();
		actionMatchings = new HashMap<IAction, IAction>();
		variableRenaming = new HashMap<IVariable, String>();
		carrierSetRenaming = new HashMap<ICarrierSet, String>();
		constantRenaming = new HashMap<IConstant, String>();
		mergedEventRenaming = new HashMap<IEvent, String>();
		newEventRenaming = new HashMap<IEvent, String>();
		matchedEventRenaming = new HashMap<Pair<IEvent,IEvent>, String>();
		forwardDependencies = new HashMap<IVariable, Collection<IVariable>>();
		backwardDependencies = new HashMap<IVariable, Collection<IVariable>>();
		variableForwardReplacement = new HashMap<IVariable, Expression>();
		variableBackwardReplacement = new HashMap<IVariable, Expression>();
		parameterReplacement = new HashMap<IWitness, Expression>();
			
		eventMerging = new HashMap<IEvent, Collection<IEvent>>();
		
	}
	
	/**
	 * Set or change the abstract pattern machine. All existing linking data as matchings, renamings, etc.
	 * are deleted. If the problem machine is set, all the linking information is reinitialized. The pattern refinement machine is set to null.
	 * @param patternAbstractMachine that should be set locally
	 * @throws DataException if the input is not valid or a RodinDBException occurs
	 */
	public void changePatternAbstractMachine(IMachineRoot patternAbstractMachine) throws DataException {
		
		try {
			// check given abstract pattern machine
			if (patternAbstractMachine == null || !patternAbstractMachine.exists())
				throw new DataException("Given pattern machine was null or does not exist.");
			
			// set given machine locally
			this.patternAbstractMachine = patternAbstractMachine;
			
			machineRank.put(patternAbstractMachine, 0);
			
			
			// clear sub-matchings
			matchedParameters.clear();
			notMatchedParameters.clear();
			matchedGuards.clear();
			notMatchedGuards.clear();
			matchedActions.clear();
			notMatchedActions.clear();
			
			// PATTERN
			
			// initialize pattern events
			allPatternEvents.clear();
			for (IEvent event : patternAbstractMachine.getEvents())
				allPatternEvents.add(event);
			matchedPatternEvents.clear();
			notMatchedPatternEvents.clear();
			notMatchedPatternEvents.addAll(allPatternEvents);
			
			// initialize variables
			allPatternVariables.clear();
			for (IVariable variable : patternAbstractMachine.getVariables())
				allPatternVariables.add(variable);
			matchedPatternVariables.clear();
			notMatchedPatternVariables.clear();
			notMatchedPatternVariables.addAll(allPatternVariables);
			
			// initialize parameters of pattern events
			allPatternParameters.clear();
			calculateParameters(allPatternEvents, allPatternParameters);
			for (IEvent event : allPatternParameters.keySet()) {
				matchedParameters.put(event, new ArrayList<IParameter>());
				notMatchedParameters.put(event, getAllPatternParametersOf(event));
			}
						
			// initialize guards of pattern events
			allPatternGuards.clear();
			calculateGuards(allPatternEvents, allPatternGuards);
			for (IEvent event : allPatternGuards.keySet()) {
				matchedGuards.put(event, new ArrayList<IGuard>());
				notMatchedGuards.put(event, getAllPatternGuardsOf(event));
			}
			
			// initialize actions of pattern events
			allPatternActions.clear();
			calculateActions(allPatternEvents, allPatternActions);
			for (IEvent event : allPatternActions.keySet()) {
				matchedActions.put(event, new ArrayList<IAction>());
				notMatchedActions.put(event, getAllPatternActionsOf(event));
			}
			
			// clear dependent data
			disappearingPatternVariables.clear();
			newPatternRefinementVariables.clear();
			remainingPatternVariables.clear();
			variableRemainings.clear();
			
			
			variablesEntries.clear();
			variablesEntries.clear();
			for (IVariable variable : allPatternVariables)
				variablesEntries.put(variable.getIdentifierString(), variable);
			
			
			
			// PROBLEM
			
			// initialize if already set
			if (problemMachine != null) {
				
				// initialize problem events
				matchedProblemEvents.clear();
				notMatchedProblemEvents.clear();
				notMatchedProblemEvents.addAll(allProblemEvents);
				mergedProblemEvents.clear();
				notMergedProblemEvents.clear();
				notMergedProblemEvents.addAll(notMatchedProblemEvents);
				
				// initialize event matching
				eventMatchings.clear();
				
				// initialize problem variables
				matchedProblemVariables.clear();
				notMatchedProblemVariables.clear();
				notMatchedProblemVariables.addAll(allProblemVariables);
				
				// initialize variable matching
				variableMatchings.clear();
				
				// initialize sub-matching
				parameterMatchings.clear();
				guardMatchings.clear();
				actionMatchings.clear();
				
				
				// initialize parameters of problem events
				for (IEvent event : allProblemParameters.keySet()) {
					matchedParameters.put(event, new ArrayList<IParameter>());
					notMatchedParameters.put(event, getAllProblemParametersOf(event));
				}
				
				// initialize guards of problem events
				for (IEvent event : allProblemGuards.keySet()) {
					matchedGuards.put(event, new ArrayList<IGuard>());
					notMatchedGuards.put(event, getAllProblemGuardsOf(event));
				}
				
				// initialize actions of problem events
				for (IEvent event : allProblemActions.keySet()) {
					matchedActions.put(event, new ArrayList<IAction>());
					notMatchedActions.put(event, getAllProblemActionsOf(event));
				}
			}
			
			// PATTERN REFINEMENT
			
			// clear dependent data
			patternRefinementMachine = null;
			
			allPatternRefinementEvents.clear();
			newPatternRefinementEvents.clear();
			refinedPatternRefinementEvents.clear();
			mergedPatternRefinementEvents.clear();
			notMergedPatternRefinementEvents.clear();
			allPatternRefinementVariables.clear();
			
			refinement.clear();
			abstraction.clear();
			eventRefinement.clear();
			eventAbstraction.clear();
			
			// PATTERN INSIDE
			
			// clear dependent data
			intermediatePatternMachines.clear();
			intermediateNewVariables.clear();
			intermediateDisappearingVariables.clear();
			
			// CONTEXT
			
			// clear carrier sets
			carrierSetRenaming.clear();
			
			// clear constants
			constantRenaming.clear();
		}
		catch (RodinDBException e) {
			// error occurred
			this.patternAbstractMachine = null;
			throw new DataException("Error occurred during data manipulation");
		}
	
	}
	
	/**
	 * Set or change the pattern refinement machine. The abstract pattern machine has to be set before this
	 * method is called.
	 * @param patternRefinementMachine that should be set locally
	 * @throws DataException if input is invalid, abstract pattern machine is no set or a RodinDBException occurs
	 */
	public void changePatternRefinementMachine(IMachineRoot patternRefinementMachine) throws DataException {
		
		try {
			// check given refined pattern machine
			if (patternRefinementMachine == null || !patternRefinementMachine.exists())
				throw new DataException("Given pattern refinement machine was null or does not exist.");
			
			// check that abstract pattern machine is set, this is essential
			if (patternAbstractMachine == null)
				throw new DataException("Abstract pattern machine is not set.");
			
			// generate chain of refinement and check if given machine is a refinement of the abstract one
			intermediatePatternMachines.clear();
			refinement.clear();
			abstraction.clear();
			eventRefinement.clear();
			eventAbstraction.clear();
			
			// get abstract machine of given pattern refinement machine
			IMachineRoot currentMachine = patternRefinementMachine;
			IRefinesMachine[] refinesClauses = currentMachine.getRefinesClauses();
			if (refinesClauses == null || refinesClauses.length <= 0)
				throw new DataException("Given machine is not a refinement of the abstract pattern machine.");
			if (refinesClauses.length != 1)
				throw new DataException("Invalid number of refines clauses in the machine.");
			IMachineRoot abstractMachine = refinesClauses[0].getAbstractMachineRoot();
			refinement.put(abstractMachine, currentMachine);
			abstraction.put(currentMachine, abstractMachine);
			
			for (IEvent refinementEvent : currentMachine.getEvents()) {
				eventAbstraction.put(refinementEvent, new HashSet<IEvent>());
				if (refinementEvent.isInitialisation()) {
					IEvent abstractEvent = PatternUtils.getElementByLabel(IEvent.ELEMENT_TYPE, IEvent.INITIALISATION, abstractMachine);
					if (abstractEvent == null || !abstractEvent.exists())
						throw new DataException("Init event not found.");
					eventRefinement.put(abstractEvent, new HashSet<IEvent>());
					eventRefinement.get(abstractEvent).add(refinementEvent);
					eventAbstraction.get(refinementEvent).add(abstractEvent);
					continue;
				}
					
				IRefinesEvent[] eventRefinesClauses = refinementEvent.getRefinesClauses();
				if (eventRefinesClauses == null)
					throw new DataException("Current event returned invalid refines clauses.");
				for (IRefinesEvent refines : eventRefinesClauses) {
					String eventLabel = refines.getAbstractEventLabel();
					IEvent abstractEvent = PatternUtils.getElementByLabel(IEvent.ELEMENT_TYPE, eventLabel, abstractMachine);
					if (abstractEvent == null || !abstractEvent.exists())
						throw new DataException("Event abstraction is not valid.");
					if (!eventRefinement.containsKey(abstractEvent))
						eventRefinement.put(abstractEvent, new HashSet<IEvent>());
					eventRefinement.get(abstractEvent).add(refinementEvent);
					eventAbstraction.get(refinementEvent).add(abstractEvent);
				}
			}
					
			while (!abstractMachine.equals(patternAbstractMachine)) {
				currentMachine = abstractMachine;
				intermediatePatternMachines.add(currentMachine);
				refinesClauses = currentMachine.getRefinesClauses();
				if (refinesClauses == null || refinesClauses.length <= 0)
					throw new DataException("Given machine is not a refinement of the abstract pattern machine.");
				if (refinesClauses.length != 1)
					throw new DataException("Invalid number of refines clauses in the machine.");
				abstractMachine = refinesClauses[0].getAbstractMachineRoot();
				refinement.put(abstractMachine, currentMachine);
				abstraction.put(currentMachine, abstractMachine);
				
				for (IEvent refinementEvent : currentMachine.getEvents()) {
					if (refinementEvent.isExtended())
						PatternUtils.unsetExtended(refinementEvent);
					eventAbstraction.put(refinementEvent, new HashSet<IEvent>());
					if (refinementEvent.isInitialisation()) {
						IEvent abstractEvent = PatternUtils.getElementByLabel(IEvent.ELEMENT_TYPE, IEvent.INITIALISATION, abstractMachine);
						if (abstractEvent == null || !abstractEvent.exists())
							throw new DataException("Event abstraction is not valid.");
						eventRefinement.put(abstractEvent, new HashSet<IEvent>());
						eventRefinement.get(abstractEvent).add(refinementEvent);
						eventAbstraction.get(refinementEvent).add(abstractEvent);
						continue;
					}
					IRefinesEvent[] eventRefinesClauses = refinementEvent.getRefinesClauses();
					if (eventRefinesClauses == null)
						throw new DataException("Current event returned invalid refines clauses.");
					for (IRefinesEvent refines : eventRefinesClauses) {
						String eventLabel = refines.getAbstractEventLabel();
						IEvent abstractEvent = PatternUtils.getElementByLabel(IEvent.ELEMENT_TYPE, eventLabel, abstractMachine);
						if (abstractEvent == null || !abstractEvent.exists())
							throw new DataException("Event abstraction is not valid.");
						if (!eventRefinement.containsKey(abstractEvent))
							eventRefinement.put(abstractEvent, new HashSet<IEvent>());
						eventRefinement.get(abstractEvent).add(refinementEvent);
						eventAbstraction.get(refinementEvent).add(abstractEvent);
					}
				}
			}
								
			// set given machine locally
			this.patternRefinementMachine = patternRefinementMachine;
			
			// PATTERN REFINEMENT
			
			// initialize pattern refinement events
			allPatternRefinementEvents.clear();
			newPatternRefinementEvents.clear();
			refinedPatternRefinementEvents.clear();
			
			for (IEvent event : patternRefinementMachine.getEvents()){
				allPatternRefinementEvents.add(event);
				newPatternRefinementEvents.add(event);
			}
			IEvent[] events = PatternUtils.getRefinementsOfEvents(patternRefinementMachine, patternAbstractMachine.getEvents());
			if (events == null)
				throw new DataException("A problem occured during calulation of the pattern refinement events.");
			for (IEvent event : events) {
				newPatternRefinementEvents.remove(event);
				refinedPatternRefinementEvents.add(event);
			}
			
			mergedPatternRefinementEvents.clear();
			notMergedPatternRefinementEvents.clear();
			notMergedPatternRefinementEvents.addAll(newPatternRefinementEvents);
					
			// initialize pattern refinement variables
			allPatternRefinementVariables.clear();
			for (IVariable variable : patternRefinementMachine.getVariables())
				allPatternRefinementVariables.add(variable);
						
			// initialize parameters of pattern refinement events
			allPatternRefinementParameters.clear();
			calculateParameters(allPatternRefinementEvents, allPatternRefinementParameters);
	
			// initialize guards of pattern refinement events
			allPatternRefinementGuards.clear();
			calculateGuards(allPatternRefinementEvents, allPatternRefinementGuards);
			
			// initialize actions of pattern refinement events
			allPatternRefinementActions.clear();
			calculateActions(allPatternRefinementEvents, allPatternRefinementActions);
			
			// initialize renamings
			variableRenaming.clear();
			newEventRenaming.clear();
			mergedEventRenaming.clear();
			matchedEventRenaming.clear();
			
			// initialize replacements
			variableForwardReplacement.clear();
			variableBackwardReplacement.clear();
			forwardDependencies.clear();
			backwardDependencies.clear();
			parameterReplacement.clear();
			variableAppearing.clear();
			variableDisappearing.clear();
			
			
			// PATTERN
			
			// initialize disappearing pattern variables
			disappearingPatternVariables.clear();
			disappearingPatternVariables.addAll(allPatternVariables);
			for (IVariable patternRefinementVariable: allPatternRefinementVariables) {
				IVariable patternVariable = PatternUtils.getElementByIdentifier(IVariable.ELEMENT_TYPE, patternRefinementVariable.getIdentifierString(), patternAbstractMachine);
				disappearingPatternVariables.remove(patternVariable);
			}
			
			newPatternRefinementVariables.clear();
			newPatternRefinementVariables.addAll(allPatternRefinementVariables);
			for (IVariable patternVariable: allPatternVariables) {
				IVariable patternRefinementVariable = PatternUtils.getElementByIdentifier(IVariable.ELEMENT_TYPE, patternVariable.getIdentifierString(), patternRefinementMachine);
				newPatternRefinementVariables.remove(patternRefinementVariable);
			}
			
			remainingPatternVariables.clear();
			remainingPatternVariables.addAll(allPatternVariables);
			remainingPatternVariables.removeAll(disappearingPatternVariables);
			
			
			
			
			variableRemainings.clear();
			for (IVariable variable : allPatternRefinementVariables)
				variableRemainings.put(variable.getIdentifierString(), variable);
			
			// PROBLEM
			
			// initialize if already set
			if (patternRefinementMachine != null) {
				
				// initialize problem events
				mergedProblemEvents.clear();
				notMergedProblemEvents.clear();
				notMergedProblemEvents.addAll(notMatchedProblemEvents);
				
				// reset event merging
				eventMerging.clear();
			}
			
			// PATTERN INSIDE
			
			// calculate new and disappearing variables of intermediate machines
			currentMachine = patternAbstractMachine;
			// once for abstract pattern machine
			{
				Collection<IVariable> newVariables = new HashSet<IVariable>();
				intermediateNewVariables.put(currentMachine, newVariables);
				for (IVariable variable : currentMachine.getVariables())
					newVariables.add(variable);
				Collection<IVariable> disappearingVariables = new HashSet<IVariable>(newVariables);
				intermediateDisappearingVariables.put(currentMachine, disappearingVariables);
				for (IVariable refinementVariable: refinement.get(currentMachine).getVariables()) {
					IVariable currentVariable = PatternUtils.getElementByIdentifier(IVariable.ELEMENT_TYPE, refinementVariable.getIdentifierString(), currentMachine);
					disappearingVariables.remove(currentVariable);
				}
				
				for (IVariable variable : newVariables)
					variableAppearing.put(variable.getIdentifierString(), variable);
				for (IVariable variable : disappearingVariables)
					variableDisappearing.put(variable.getIdentifierString(), variable);
				
				currentMachine = refinement.get(currentMachine);
			}
			
			
			
			while (!currentMachine.equals(patternRefinementMachine)) {
				
				Collection<IVariable> newVariables = new HashSet<IVariable>();
				intermediateNewVariables.put(currentMachine, newVariables);
				for (IVariable variable : currentMachine.getVariables())
					newVariables.add(variable);
				Collection<IVariable> disappearingVariables = new HashSet<IVariable>(newVariables);
				for (IVariable abstractVariable: abstraction.get(currentMachine).getVariables()) {
					IVariable currentVariable = PatternUtils.getElementByIdentifier(IVariable.ELEMENT_TYPE, abstractVariable.getIdentifierString(), currentMachine);
					newVariables.remove(currentVariable);
				}
				
				intermediateDisappearingVariables.put(currentMachine, disappearingVariables);
				for (IVariable refinementVariable: refinement.get(currentMachine).getVariables()) {
					IVariable currentVariable = PatternUtils.getElementByIdentifier(IVariable.ELEMENT_TYPE, refinementVariable.getIdentifierString(), currentMachine);
					disappearingVariables.remove(currentVariable);
				}
				
				for (IVariable variable : newVariables)
					variableAppearing.put(variable.getIdentifierString(), variable);
				for (IVariable variable : disappearingVariables)
					variableDisappearing.put(variable.getIdentifierString(), variable);
				
				currentMachine = refinement.get(currentMachine);
				
			}
			
			// once for the pattern refinement machine
			{
				
				Collection<IVariable> newVariables = new HashSet<IVariable>();
				intermediateNewVariables.put(currentMachine, newVariables);
				for (IVariable variable : currentMachine.getVariables())
					newVariables.add(variable);
				Collection<IVariable> disappearingVariables = new HashSet<IVariable>(newVariables);
				for (IVariable abstractVariable: abstraction.get(currentMachine).getVariables()) {
					IVariable currentVariable = PatternUtils.getElementByIdentifier(IVariable.ELEMENT_TYPE, abstractVariable.getIdentifierString(), currentMachine);
					newVariables.remove(currentVariable);
				}
				
				intermediateDisappearingVariables.put(currentMachine, disappearingVariables);
				
				
				for (IVariable variable : newVariables)
					variableAppearing.put(variable.getIdentifierString(), variable);
				for (IVariable variable : disappearingVariables)
					variableDisappearing.put(variable.getIdentifierString(), variable);
				
								
			}
			
						
			HashMap<IEvent, Collection<IEvent>> reachable = new HashMap<IEvent, Collection<IEvent>>();
			HashMap<IEvent, Collection<IEvent>> usedFor = new HashMap<IEvent, Collection<IEvent>>();
			HashMap<IEvent, Integer> weight = new HashMap<IEvent, Integer>();
			Collection<IEvent> currentEvents = getRefinedPatternRefinementEvents();
			Collection<IEvent> abstractEvents = new HashSet<IEvent>();
			currentMachine = patternRefinementMachine;
			for (IEvent event : currentEvents){
				reachable.put(event, new HashSet<IEvent>());
				reachable.get(event).add(event);
				usedFor.put(event, new HashSet<IEvent>());
				usedFor.get(event).add(event);
			}
			while (!currentMachine.equals(patternAbstractMachine)) {
				for (IEvent event : currentEvents) {
					weight.put(event, 0);
					Collection<IEvent> currentReachable = reachable.get(event);
					for (IEvent abstractEvent : eventAbstraction.get(event)) {
						if (!abstractEvents.contains(abstractEvent)) {
							reachable.put(abstractEvent, new HashSet<IEvent>());
							abstractEvents.add(abstractEvent);
							usedFor.put(abstractEvent, new HashSet<IEvent>());
						}
						reachable.get(abstractEvent).addAll(currentReachable);
						weight.put(event,weight.get(event) + 1);
					}
				}
				currentEvents.clear();
				currentEvents.addAll(abstractEvents);
				abstractEvents.clear();
				currentMachine = abstraction.get(currentMachine);
			}
			
			for (IEvent refinement : getRefinedPatternRefinementEvents()) {
				// initialize
				HashMap<IEvent, IEvent> currentChainDown =  new HashMap<IEvent, IEvent>();
				chainDown.put(refinement, currentChainDown);
				Collection<IEvent> possibilities = new HashSet<IEvent>();
				Collection<IEvent> candidates = new HashSet<IEvent>();
				IEvent nextEvent;
				// get all matched pattern events that reach the current refinement
				Collection<IEvent> startEvents = new HashSet<IEvent>();
				for (IEvent patternEvent : matchedPatternEvents)
					if (reachable.get(patternEvent).contains(refinement))
						startEvents.add(patternEvent);
				for (IEvent currentEvent : startEvents) {
					
					while (!currentEvent.equals(refinement)) {
											
						// collect possible events
						possibilities.clear();
						candidates.clear();
						for (IEvent event : eventRefinement.get(currentEvent))
							// check if the refinement is reachable through this event
							if (reachable.get(event) != null && reachable.get(event).contains(refinement)) {
								// check if it was already used for this refinement
								if (usedFor.get(event).contains(refinement)) {
									candidates.add(event);
									break;
								}
								possibilities.add(event);
							}
						// if no candidate found yet
						if (candidates.size() != 1) {
							// find "heaviest" event among possibilities
							int max = 0;
							for (IEvent event : possibilities) {
								if (weight.get(event) > max) {
									candidates.clear();
									candidates.add(event);
								}
								else if (weight.get(event) == max)
									candidates.add(event);
							}
						}
						// if more than one candidate found
						if (candidates.size() != 1) {
							// find mostly used event among candidates
							possibilities.clear();
							possibilities.addAll(candidates);
							candidates.clear();
							int max = 0;
							for (IEvent event : possibilities) {
								if (usedFor.get(event).size() > max) {
									candidates.clear();
									candidates.add(event);
								}
								else if (usedFor.get(event).size() == max)
									candidates.add(event);
							}
						}
						// if more than one candidate found
						if (candidates.size() != 1) {
							// find the event with the most possibilities
							possibilities.clear();
							possibilities.addAll(candidates);
							candidates.clear();
							int max = 0;
							for (IEvent event : possibilities) {
								if (reachable.get(event).size() > max) {
									candidates.clear();
									candidates.add(event);
								}
								else if (reachable.get(event).size() == max)
									candidates.add(event);
							}
						}
						// set the next event
						nextEvent = candidates.iterator().next();
						// update the chain
						currentChainDown.put(currentEvent, nextEvent);
						// update allChains
						if (!allChains.containsKey(currentEvent))
							allChains.put(currentEvent, new HashSet<IEvent>());
						allChains.get(currentEvent).add(nextEvent);
						// mark event as used for this refinement
						usedFor.get(nextEvent).add(refinement);
						// prepare next round
						currentEvent = nextEvent;
					}
				}
			}
			
			// collect the relevant parameters
			for (IEvent patternEvent: getMatchedPatternEvents())
				collectRelevantParameters(patternEvent, getMatchedParametersOf(patternEvent));
		
			// collect relevant invariants
			relevantInvariants.clear();
			relatedVariables.clear();
			
			currentMachine = patternAbstractMachine;
			int rank = 1;
			while (!currentMachine.equals(patternRefinementMachine)) {
				abstractMachine = currentMachine;
				currentMachine = refinement.get(currentMachine);
				// set rank of current machine
				machineRank.put(currentMachine, rank++);
				
				Collection<IInvariant> relevantInvs = new HashSet<IInvariant>();
				relevantInvariants.put(currentMachine, relevantInvs);
				// collect all new variables of the current machine that still exist in the refinement machine
				Collection<IVariable> relevantVariables = new HashSet<IVariable>();
				Collection<IVariable> newVariables = getNewVariablesOf(currentMachine);
				for (IVariable variable : allPatternRefinementVariables) {
					IVariable var = PatternUtils.getElementByIdentifier(IVariable.ELEMENT_TYPE, variable.getIdentifierString(), currentMachine);
					if (var != null && newVariables.contains(var))
						relevantVariables.add(var);
				}
				// for all invariants of the current machine		
				for (IInvariant invariant : currentMachine.getInvariants()) {
					// check if it includes relevant variables of the current machine
					if (EventBUtils.isRelevant(invariant, relevantVariables)){
						relevantInvs.add(invariant);
						// check for variables
						for (FreeIdentifier identifier : 
							ff.parsePredicate(invariant.getPredicateString(), LanguageVersion.LATEST, null).getParsedPredicate().getFreeIdentifiers()) {
							IVariable variable = variableAppearing.get(identifier.getName());
							if (variable == null)
								throw new DataException("Variable in invariant does not exist.");
							if (variableRemainings.containsKey(identifier.getName()))
									continue;
							if (!variablesEntries.containsKey(identifier.getName()))
								relatedVariables.add(variable);
								
						}
					}
				}
				
			}
		}
		catch (RodinDBException e) {
			// error occurred
			this.patternRefinementMachine = null;
			throw new DataException("Error occurred during data manipulation");
		}
	
	}
	
	private void collectRelevantParameters(IEvent event, Collection<IParameter> relevantParameters) throws DataException, RodinDBException {
		
		Collection<IParameter> result = new HashSet<IParameter>(relevantParameters);
		// for all given parameters
		for (IParameter parameter : relevantParameters){
			// test if the parameter still exists in this event
			if(PatternUtils.getElementByIdentifier(IParameter.ELEMENT_TYPE, parameter.getIdentifierString(), event) == null) {
				
				// remove the disappearing parameter from the collection of relevant parameters
				result.remove(parameter);
				// search for a witness				
				IWitness witness = PatternUtils.getElementByLabel(IWitness.ELEMENT_TYPE, parameter.getIdentifierString(), event);
				if (witness == null)
					throw new DataException("No witness provided for disappearing parameter.");
				// add witness to parameterReplacement
				parameterReplacement.put(witness, ff.makeFreeIdentifier("replace", null));
				// get the predicate of the witness
				Predicate predicate = ff.parsePredicate(witness.getPredicateString(), LanguageVersion.LATEST, null).getParsedPredicate();
				if (predicate == null)
					throw new DataException("Error while parsing witness predicate.");
				// get all free identifiers
				for (FreeIdentifier identifier : predicate.getFreeIdentifiers()) {
					IParameter par = PatternUtils.getElementByIdentifier(IParameter.ELEMENT_TYPE, identifier.getName(), event);
					// if a free identifier is a parameter of the event add it to the collection of relevant parameters
					if (par != null)
						result.add(par);
				}
			}
		}
		// collect for all the refinements of the event (if there exist) the relevant parameters
		if (!result.isEmpty() && allChains.containsKey(event))
			for (IEvent refEvent : allChains.get(event))
				collectRelevantParameters(refEvent, result);
			
	}
	
	/**
	 * Set or change the problem machine. All existing linking data as matchings are deleted.
	 * If the pattern refinement machine is set, all the linking information is reinitialized.
	 * @param problemMachine that should be set locally
	 * @throws DataException if the input is not valid or a RodinDBException occurs
	 */
	public void changeProblemMachine(IMachineRoot problemMachine) throws DataException {
		
		try {
			// check given problem machine
			if (problemMachine == null || !problemMachine.exists())
				throw new DataException("Given problem machine was null or does not exist.");
			
			// set given machine locally
			this.problemMachine = problemMachine;
			
			// clear sub-matchings
			matchedParameters.clear();
			notMatchedParameters.clear();
			matchedGuards.clear();
			notMatchedGuards.clear();
			matchedActions.clear();
			notMatchedActions.clear();
			
			// PROBLEM
			
			// initialize problem events
			allProblemEvents.clear();
			for (IEvent event : problemMachine.getEvents()){
				allProblemEvents.add(event);
			}
			matchedProblemEvents.clear();
			notMatchedProblemEvents.clear();
			notMatchedProblemEvents.addAll(allProblemEvents);
			mergedProblemEvents.clear();
			notMergedProblemEvents.clear();
			notMergedProblemEvents.addAll(notMatchedProblemEvents);
			
			// initialize problem variables
			allProblemVariables.clear();
			for (IVariable variable : problemMachine.getVariables()){
				allProblemVariables.add(variable);
			}
			matchedProblemVariables.clear();
			notMatchedProblemVariables.clear();
			notMatchedProblemVariables.addAll(allProblemVariables);
			
			// initialize parameters of problem events
			allProblemParameters.clear();
			calculateParameters(allProblemEvents, allProblemParameters);
			for (IEvent event : allProblemParameters.keySet()) {
				matchedParameters.put(event, new ArrayList<IParameter>());
				notMatchedParameters.put(event, getAllProblemParametersOf(event));
			}
			
			// initialize guards of problem events
			allProblemGuards.clear();
			calculateGuards(allProblemEvents, allProblemGuards);
			for (IEvent event : allProblemGuards.keySet()) {
				matchedGuards.put(event, new ArrayList<IGuard>());
				notMatchedGuards.put(event, getAllProblemGuardsOf(event));
			}
			
			// initialize actions of problem events
			allProblemActions.clear();
			calculateActions(allProblemEvents, allProblemActions);
			for (IEvent event : allProblemActions.keySet()) {
				matchedActions.put(event, new ArrayList<IAction>());
				notMatchedActions.put(event, getAllProblemActionsOf(event));
			}
				
			// PATTERN
			
			// initialize if already set
			if (patternAbstractMachine != null) {
				
				// initialize pattern events
				matchedPatternEvents.clear();
				notMatchedPatternEvents.clear();
				notMatchedPatternEvents.addAll(allPatternEvents);
				
				// clear event matching
				eventMatchings.clear();
				
				// initialize pattern variables
				matchedPatternVariables.clear();
				notMatchedPatternVariables.clear();
				notMatchedPatternVariables.addAll(allPatternVariables);
				
				// clear variable matching
				variableMatchings.clear();
				
				// clear sub-matching
				parameterMatchings.clear();
				guardMatchings.clear();
				actionMatchings.clear();
				
				// initialize parameters of pattern events
				for (IEvent event : allPatternParameters.keySet()) {
					matchedParameters.put(event, new ArrayList<IParameter>());
					notMatchedParameters.put(event, getAllPatternParametersOf(event));
				}
				
				// initialize guards of problem events
				for (IEvent event : allPatternGuards.keySet()) {
					matchedGuards.put(event, new ArrayList<IGuard>());
					notMatchedGuards.put(event, getAllPatternGuardsOf(event));
				}
				
				// initialize actions of problem events
				for (IEvent event : allPatternActions.keySet()) {
					matchedActions.put(event, new ArrayList<IAction>());
					notMatchedActions.put(event, getAllPatternActionsOf(event));
				}
			}
			
			// PATTERN REFINEMENT
			
			// initialize if already set
			if (patternRefinementMachine != null) {
				
				// initialize pattern refinement events
				mergedPatternRefinementEvents.clear();
				notMergedPatternRefinementEvents.clear();
				notMergedPatternRefinementEvents.addAll(allPatternRefinementEvents);
				
				// clear event merging
				eventMerging.clear();
			}
		}
		catch (RodinDBException e) {
			// error occurred
			this.problemMachine = null;
			throw new DataException("Error occurred during data manipulation");
		}
	
	}
	
	/**
	 * Add the matching of two events (pattern and problem). 
	 * @param pattern event
	 * @param problem event
	 * @throws DataException
	 */
	public void addMatching(IEvent pattern, IEvent problem) throws DataException {
		
		// machines have to be initialized		
		if (patternAbstractMachine == null || problemMachine == null)
			throw new DataException("Machines not yet initialized.");
		
		// validate input
		if (pattern == null || problem == null)
			throw new DataException("One of the inputs is null.");
		
		// given pattern event checked
		if (!allPatternEvents.contains(pattern))
			throw new DataException("Given event is not a pattern event.");
		
		// given problem event checked
		if (!allProblemEvents.contains(problem))
			throw new DataException("Given event is not a problem event.");
		
		// move the pattern event
		matchedPatternEvents.add(pattern);
		notMatchedPatternEvents.remove(pattern);
		
		// move the problem event
		matchedProblemEvents.add(problem);
		notMatchedProblemEvents.remove(problem);
		
		// add the matching in pattern point of view
		if (!eventMatchings.containsKey(pattern))
			eventMatchings.put(pattern, new ArrayList<IEvent>());
		eventMatchings.get(pattern).add(problem);
		
		// add the matching in problem point of view
		if (!eventMatchings.containsKey(problem))
			eventMatchings.put(problem, new ArrayList<IEvent>());
		eventMatchings.get(problem).add(pattern);
	
	}	
	
	/**
	 * Add the matching of two variables (pattern and problem).
	 * @param pattern variable
	 * @param problem variable
	 * @throws DataException
	 */
	public void addMatching(IVariable pattern, IVariable problem) throws DataException {
		
		// machines have to be initialized
		if (patternAbstractMachine == null || problemMachine == null)
			throw new DataException("Machines not yet initialized.");
		
		// validate input
		if (pattern == null || problem == null)
			throw new DataException("One of the inputs is null.");
		
		// given pattern variable checked
		if (!allPatternVariables.contains(pattern))
			throw new DataException("Given variable is not a pattern variable.");
		
		// given problem variable checked
		if (!allProblemVariables.contains(problem))
			throw new DataException("Given variable is not a problem variable.");
		
		// move the pattern variable
		matchedPatternVariables.add(pattern);
		notMatchedPatternVariables.remove(pattern);
		
		// move the problem variable
		matchedProblemVariables.add(problem);
		notMatchedProblemVariables.remove(problem);
		
		// add the matching in pattern point of view
		variableMatchings.put(pattern, problem);
		
		// add the matching in problem point of view
		variableMatchings.put(problem, pattern);
	
	}
	
	/**
	 * Add the matching of two parameters (pattern and problem). The corresponding events
	 * have to be matched.
	 * @param pattern parameter
	 * @param problem parameter
	 * @throws DataException
	 */
	public void addMatching(IParameter pattern, IParameter problem) throws DataException {
		
		// machines have to be initialized
		if (patternAbstractMachine == null || problemMachine == null)
			throw new DataException("Machines not yet initialized.");
		
		// validate input
		if (pattern == null || problem == null)
			throw new DataException("One of the inputs is null.");
		
		// get the pattern parameters parent
		IRodinElement patternParent = pattern.getParent();
		
		// check that parent is an event and declare it as such
		IEvent patternEvent;
		if (patternParent instanceof IEvent)
			patternEvent = (IEvent) patternParent;
		else
			throw new DataException("Pattern parent is not an event.");
		
		// check that parent is matched
		if (!matchedPatternEvents.contains(patternEvent))
			throw new DataException("Pattern event is not an matched event.");
		
		// get the problem parameters parent
		IRodinElement problemParent = problem.getParent();
		
		//check that parent is an event and declare it as such
		IEvent problemEvent;
		if (problemParent instanceof IEvent)
			problemEvent = (IEvent) problemParent;
		else
			throw new DataException("Problem parent is not an event.");
		
		// check that parent is matched
		if (!matchedProblemEvents.contains(problemEvent))
			throw new DataException("Problem event is not an matched event.");
		
		// given pattern parameter checked
		if (!allPatternParameters.get(patternEvent).contains(pattern))
			throw new DataException("Given event is not a pattern event.");
		
		// given problem parameter checked
		if (!allProblemParameters.get(problemEvent).contains(problem))
			throw new DataException("Given event is not a problem event.");
		
		// move the pattern parameter
		matchedParameters.get(patternEvent).add(pattern);
		notMatchedParameters.get(patternEvent).remove(pattern);
		
		// move the problem parameter
		matchedParameters.get(problemEvent).add(problem);
		notMatchedParameters.get(problemEvent).remove(problem);
		
		// add the matching in pattern point of view
		parameterMatchings.put(pattern, problem);
		
		// add the matching in problem point of view
		parameterMatchings.put(problem, pattern);
	
	}
	
	/**
	 * Add the matching of two guards (pattern and problem). The corresponding events
	 * have to be matched.
	 * @param pattern guard
	 * @param problem guard
	 * @throws DataException
	 */
	public void addMatching(IGuard pattern, IGuard problem) throws DataException {
		
		// machines have to be initialized
		if (patternAbstractMachine == null || problemMachine == null)
			throw new DataException("Machines not yet initialized.");
		
		// validate input
		if (pattern == null || problem == null)
			throw new DataException("One of the inputs is null.");
		
		// get the pattern guard parent
		IRodinElement patternParent = pattern.getParent();
		
		// check that parent is an event and declare it as such
		IEvent patternEvent;
		if (patternParent instanceof IEvent)
			patternEvent = (IEvent) patternParent;
		else
			throw new DataException("Pattern parent is not an event.");
		
		// check that parent is matched
		if (!matchedPatternEvents.contains(patternEvent))
			throw new DataException("Pattern event is not an matched event.");
		
		// get the problem guard parent
		IRodinElement problemParent = problem.getParent();
		
		// check that parent is an event and declare it as such
		IEvent problemEvent;
		if (problemParent instanceof IEvent)
			problemEvent = (IEvent) problemParent;
		else
			throw new DataException("Problem parent is not an event.");
		
		// check that parent is matched
		if (!matchedProblemEvents.contains(problemEvent))
			throw new DataException("Problem event is not an matched event.");
		
		// given pattern guard checked
		if (!allPatternGuards.get(patternEvent).contains(pattern))
			throw new DataException("Given event is not a pattern event.");
		
		// given problem guard checked
		if (!allProblemGuards.get(problemEvent).contains(problem))
			throw new DataException("Given event is not a problem event.");
		
		// move the pattern guard
		matchedGuards.get(patternEvent).add(pattern);
		notMatchedGuards.get(patternEvent).remove(pattern);
		
		// move the problem guard
		matchedGuards.get(problemEvent).add(problem);
		notMatchedGuards.get(problemEvent).remove(problem);
		
		// add the matching in pattern point of view
		guardMatchings.put(pattern, problem);
		
		// add the matching in problem point of view
		guardMatchings.put(problem, pattern);
	
	}
	
	/**
	 * Add the matching of two actions (pattern and problem). The corresponding events
	 * have to be matched.
	 * @param pattern action
	 * @param problem action
	 * @throws DataException
	 */
	public void addMatching(IAction pattern, IAction problem) throws DataException {
		
		// machines have to be initialized
		if (patternAbstractMachine == null || problemMachine == null)
			throw new DataException("Machines not yet initialized.");
		
		// validate input
		if (pattern == null || problem == null)
			throw new DataException("One of the inputs is null.");
		
		// get the pattern action parent
		IRodinElement patternParent = pattern.getParent();
		
		// check that parent is an event and declare it as such
		IEvent patternEvent;
		if (patternParent instanceof IEvent)
			patternEvent = (IEvent) patternParent;
		else
			throw new DataException("Pattern parent is not an event.");
		
		// check that parent is matched
		if (!matchedPatternEvents.contains(patternEvent))
			throw new DataException("Pattern event is not an matched event.");
		
		// get the problem action parent
		IRodinElement problemParent = problem.getParent();
		
		// check that parent is an event and declare it as such
		IEvent problemEvent;
		if (problemParent instanceof IEvent)
			problemEvent = (IEvent) problemParent;
		else
			throw new DataException("Problem parent is not an event.");
		
		// check that parent is matched
		if (!matchedProblemEvents.contains(problemEvent))
			throw new DataException("Problem event is not an matched event.");
		
		// given pattern action checked
		if (!allPatternActions.get(patternEvent).contains(pattern))
			throw new DataException("Given event is not a pattern event.");
		
		// given pattern action checked
		if (!allProblemActions.get(problemEvent).contains(problem))
			throw new DataException("Given event is not a problem event.");
		
		// move the pattern action
		matchedActions.get(patternEvent).add(pattern);
		notMatchedActions.get(patternEvent).remove(pattern);
		
		// move the problem action
		matchedActions.get(problemEvent).add(problem);
		notMatchedActions.get(problemEvent).remove(problem);
		
		// add the matching in pattern point of view
		actionMatchings.put(pattern, problem);
		
		// add the matching in problem point of view
		actionMatchings.put(problem, pattern);
	
	}
	
	/**
	 * Remove the matching of the two given events. All sub-matchings are removed implicitly.
	 * @param pattern event
	 * @param problem event
	 * @throws DataException
	 */
	public void removeMatching(IEvent pattern, IEvent problem) throws DataException {
		
		// machines have to be initialized
		if (patternAbstractMachine == null || problemMachine == null)
			throw new DataException("Machines not yet initialized.");
		
		// validate input
		if (pattern == null || problem == null)
			throw new DataException("One of the inputs is null.");
		
		// given pattern event checked
		if (!matchedPatternEvents.contains(pattern))
			throw new DataException("Given pattern event is not matched.");
		
		// given problem event checked
		if (!matchedProblemEvents.contains(problem))
			throw new DataException("Given problem event is not matched.");
		
		// remove submatchings
		for (IParameter parameter : getMatchedParametersOf(problem))
			removeMatching(getMatchingOf(parameter), parameter);
		for (IGuard guard : getMatchedGuardsOf(problem))
			removeMatching(getMatchingOf(guard), guard);
		for (IAction action : getMatchedActionsOf(problem))
			removeMatching(getMatchingOf(action), action);		
		
		// move the pattern event
		matchedPatternEvents.remove(pattern);
		if (!matchedPatternEvents.contains(pattern))
			notMatchedPatternEvents.add(pattern);
		
		// move the problem event
		matchedProblemEvents.remove(problem);
		if (!matchedProblemEvents.contains(problem))
			notMatchedProblemEvents.add(problem);
		
		// remove the matching in pattern point of view
		eventMatchings.get(pattern).remove(problem);
		if (eventMatchings.get(pattern).isEmpty())
			eventMatchings.remove(pattern);
		
		// remove the matching in problem point of view
		eventMatchings.get(problem).remove(pattern);
		if (eventMatchings.get(problem).isEmpty())
			eventMatchings.remove(problem);
		
	}
	
	/**
	 * Remove the matching of the two given variables.
	 * @param pattern variable
	 * @param problem variable
	 * @throws DataException
	 */
	public void removeMatching(IVariable pattern, IVariable problem) throws DataException {

		// machines have to be initialized
		if (patternAbstractMachine == null || problemMachine == null)
			throw new DataException("Machines not yet initialized.");
		
		// validate input
		if (pattern == null || problem == null)
			throw new DataException("One of the inputs is null.");
		
		// given pattern variable checked
		if (!matchedPatternVariables.contains(pattern))
			throw new DataException("Given pattern variable is not matched.");
		
		// given problem variable checked
		if (!matchedProblemVariables.contains(problem))
			throw new DataException("Given problem variable is not matched.");
		
		// move the pattern variable
		matchedPatternVariables.remove(pattern);
		if (!matchedPatternVariables.contains(pattern))
			notMatchedPatternVariables.add(pattern);
		
		// move the problem variable
		matchedProblemVariables.remove(problem);
		if (!matchedProblemVariables.contains(problem))
			notMatchedProblemVariables.add(problem);
		
		// remove the matching in pattern point of view
		variableMatchings.remove(pattern);
		
		// remove the matching in problem point of view
		variableMatchings.remove(problem);
		
	}
	
	/**
	 * Remove the matching of the two given parameters.
	 * @param pattern parameter
	 * @param problem parameter
	 * @throws DataException
	 */
	public void removeMatching(IParameter pattern, IParameter problem) throws DataException {

		// machines have to be initialized
		if (patternAbstractMachine == null || problemMachine == null)
			throw new DataException("Machines not yet initialized.");
		
		// validate input
		if (pattern == null || problem == null)
			throw new DataException("One of the inputs is null.");
		
		// get the pattern parameter parent
		IRodinElement patternParent = pattern.getParent();
		
		// check that parent is an event and declare it as such	
		IEvent patternEvent;
		if (patternParent instanceof IEvent)
			patternEvent = (IEvent) patternParent;
		else
			throw new DataException("Pattern parent is not an event.");
		
		// get the problem parameter parent
		IRodinElement problemParent = problem.getParent();
		
		// check that parent is an event and declare it as such	
		IEvent problemEvent;
		if (problemParent instanceof IEvent)
			problemEvent = (IEvent) problemParent;
		else
			throw new DataException("Problem parent is not an event.");
		
		// given pattern parameter checked
		Collection<IParameter> matchedPatternParameters = matchedParameters.get(patternEvent);
		if (!matchedPatternParameters.contains(pattern))
			throw new DataException("Given pattern parameter is not matched.");
		
		// given problem parameter checked
		Collection<IParameter> matchedProblemParameters = matchedParameters.get(problemEvent);
		if (!matchedProblemParameters.contains(problem))
			throw new DataException("Given problem parameter is not matched.");
		
		// move the pattern parameter
		matchedPatternParameters.remove(pattern);
		if (!matchedPatternParameters.contains(pattern))
			notMatchedParameters.get(patternEvent).add(pattern);
		
		// move the problem parameter
		matchedProblemParameters.remove(problem);
		if (!matchedProblemParameters.contains(problem))
			notMatchedParameters.get(problemEvent).add(problem);
		
		// remove the matching in pattern point of view
		parameterMatchings.remove(pattern);
		
		// remove the matching in problem point of view
		parameterMatchings.remove(problem);
	
	}
	
	/**
	 * Remove the matching of the two given guards.
	 * @param pattern guard
	 * @param problem guard
	 * @throws DataException
	 */
	public void removeMatching(IGuard pattern, IGuard problem) throws DataException {

		// machines have to be initialized
		if (patternAbstractMachine == null || problemMachine == null)
			throw new DataException("Machines not yet initialized.");
		
		// validate input
		if (pattern == null || problem == null)
			throw new DataException("One of the inputs is null.");
		
		// get the pattern guard parent
		IRodinElement patternParent = pattern.getParent();
		
		// check that parent is an event and declare it as such	
		IEvent patternEvent;
		if (patternParent instanceof IEvent)
			patternEvent = (IEvent) patternParent;
		else
			throw new DataException("Pattern parent is not an event.");
		
		// get the problem guard parent
		IRodinElement problemParent = problem.getParent();
		
		// check that parent is an event and declare it as such	
		IEvent problemEvent;
		if (problemParent instanceof IEvent)
			problemEvent = (IEvent) problemParent;
		else
			throw new DataException("Problem parent is not an event.");
		
		// given pattern guard checked
		Collection<IGuard> matchedPatternGuards = matchedGuards.get(patternEvent);
		if (!matchedPatternGuards.contains(pattern))
			throw new DataException("Given pattern guard is not matched.");
		
		// given problem guard checked
		Collection<IGuard> matchedProblemGuards = matchedGuards.get(problemEvent);
		if (!matchedProblemGuards.contains(problem))
			throw new DataException("Given problem guard is not matched.");
		
		// move the pattern guard
		matchedPatternGuards.remove(pattern);
		if (!matchedPatternGuards.contains(pattern))
			notMatchedGuards.get(patternEvent).add(pattern);
		
		// move the problem guard
		matchedProblemGuards.remove(problem);
		if (!matchedProblemGuards.contains(problem))
			notMatchedGuards.get(problemEvent).add(problem);
		
		// remove the matching in pattern point of view
		guardMatchings.remove(pattern);
		
		// remove the matching in problem point of view
		guardMatchings.remove(problem);
		
	}
	
	/**
	 * Remove the matching of the two given actions.
	 * @param pattern action
	 * @param problem action
	 * @throws DataException
	 */
	public void removeMatching(IAction pattern, IAction problem) throws DataException {

		// machines have to be initialized
		if (patternAbstractMachine == null || problemMachine == null)
			throw new DataException("Machines not yet initialized.");
		
		// validate input
		if (pattern == null || problem == null)
			throw new DataException("One of the inputs is null.");
		
		// get the pattern action parent
		IRodinElement patternParent = pattern.getParent();
		
		// check that parent is an event and declare it as such	
		IEvent patternEvent;
		if (patternParent instanceof IEvent)
			patternEvent = (IEvent) patternParent;
		else
			throw new DataException("Pattern parent is not an event.");
		
		// get the problem action parent
		IRodinElement problemParent = problem.getParent();
		
		// check that parent is an event and declare it as such	
		IEvent problemEvent;
		if (problemParent instanceof IEvent)
			problemEvent = (IEvent) problemParent;
		else
			throw new DataException("Problem parent is not an event.");
		
		// given pattern action checked
		Collection<IAction> matchedPatternActions = matchedActions.get(patternEvent);
		if (!matchedPatternActions.contains(pattern))
			throw new DataException("Given pattern action is not matched.");
		
		// given problem action checked
		Collection<IAction> matchedProblemActions = matchedActions.get(problemEvent);
		if (!matchedProblemActions.contains(problem))
			throw new DataException("Given problem action is not matched.");
		
		// move the pattern action
		matchedPatternActions.remove(pattern);
		if (!matchedPatternActions.contains(pattern))
			notMatchedActions.get(patternEvent).add(pattern);
		
		// move the problem action
		matchedProblemActions.remove(problem);
		if (!matchedProblemActions.contains(problem))
			notMatchedActions.get(problemEvent).add(problem);
		
		// remove the matching in pattern point of view
		actionMatchings.remove(pattern);
		
		// remove the matching in problem point of view
		actionMatchings.remove(problem);
		
	}
	
	/**
	 * Add the merging of two events. This defines which events should be combined in the
	 * generated machine.
	 * @param patternRefinement event
	 * @param problem event
	 * @throws DataException
	 */
	public void addMerging(IEvent patternRefinement, IEvent problem) throws DataException {
		
		// machines have to be initialized
		if (patternRefinementMachine == null || problemMachine == null)
			throw new DataException("Machines not yet initialized.");
		
		// validate input
		if (patternRefinement == null || problem == null)
			throw new DataException("One of the inputs is null.");
		
		// given pattern refinement event checked
		if (!newPatternRefinementEvents.contains(patternRefinement))
			throw new DataException("Given event is not a new pattern refinement event.");
		
		// given problem event checked
		if (!notMatchedProblemEvents.contains(problem))
			throw new DataException("Given event is not a not-matched problem event.");
		
		// move the pattern refinement event
		mergedPatternRefinementEvents.add(patternRefinement);
		notMergedPatternRefinementEvents.remove(patternRefinement);
		
		// move the problem event
		mergedProblemEvents.add(problem);
		notMergedProblemEvents.remove(problem);
		
		// add the merging in pattern point of view
		if (!eventMerging.containsKey(patternRefinement))
			eventMerging.put(patternRefinement, new ArrayList<IEvent>());
		eventMerging.get(patternRefinement).add(problem);
		
		// add the merging in problem point of view
		if (!eventMerging.containsKey(problem))
			eventMerging.put(problem, new ArrayList<IEvent>());
		eventMerging.get(problem).add(patternRefinement);
	
	}
	
	/**
	 * Remove the merging of the given events.
	 * @param patternRefinement event
	 * @param problem event
	 * @throws DataException
	 */
	public void removeMerging(IEvent patternRefinement, IEvent problem) throws DataException {

		// machines have to be initialized
		if (patternRefinementMachine == null || problemMachine == null)
			throw new DataException("Machines not yet initialized.");
		
		// validate input
		if (patternRefinement == null || problem == null)
			throw new DataException("One of the inputs is null.");
		
		// given pattern refinement event checked
		if (!mergedPatternRefinementEvents.contains(patternRefinement))
			throw new DataException("Given pattern refinement event is not merged.");
		
		// given problem event checked
		if (!mergedProblemEvents.contains(problem))
			throw new DataException("Given problem event is not merged.");
		
		// move the pattern refinement event
		mergedPatternRefinementEvents.remove(patternRefinement);
		if (!mergedPatternRefinementEvents.contains(patternRefinement))
			notMergedPatternRefinementEvents.add(patternRefinement);
		
		// move the problem event
		mergedProblemEvents.remove(problem);
		if (!mergedProblemEvents.contains(problem))
			notMergedProblemEvents.add(problem);
		
		// remove the merging in pattern refinement point of view
		eventMerging.get(patternRefinement).remove(problem);
		if (eventMerging.get(patternRefinement).isEmpty())
			eventMerging.remove(patternRefinement);
		
		// remove the merging in problem point of view
		eventMerging.get(problem).remove(patternRefinement);
		if (eventMerging.get(problem).isEmpty())
			eventMerging.remove(problem);
	
	}
	
	/**
	 * Update the renaming of a variable of the pattern refinement machine.
	 * The renaming has to be a non-empty string.
	 * @param variable of the pattern refinement
	 * @param renaming of the variable
	 * @throws DataException
	 */
	public void updateRenaming(IVariable variable, String renaming) throws DataException {
		
		// machine has to be initialized
		if (patternRefinementMachine == null)
			throw new DataException("Machine not yet initialized.");
		
		// validate input
		if (variable == null || renaming == null || renaming.equals(""))
			throw new DataException("One of the inputs is invalid.");
		
		// only rename variables of pattern refinement
		if (!allPatternRefinementVariables.contains(variable))
			throw new DataException("Variable has to be of the pattern refinement.");
		
		// get original string
		String original;
		try {
			original = variable.getIdentifierString();
		}
		catch (RodinDBException e) {
			throw new DataException("Error while accessing variable");
		}
		
		// remove entry if renaming equals the original else add it
		if (original.equals(renaming))
			variableRenaming.remove(variable);
		else
			variableRenaming.put(variable, renaming);
		
	}
	
	/**
	 * Update the matching of a carrier set with a string.
	 * @param carrierSet
	 * @param matching string
	 * @throws DataException
	 */
	public void updateMatching(ICarrierSet carrierSet, String matching) throws DataException {

		// machines have to be initialized
		if (patternAbstractMachine == null || problemMachine == null)
			throw new DataException("Machines not yet initialized.");
		
		// validate input
		if (carrierSet == null || matching == null || matching.equals(""))
			throw new DataException("One of the inputs is invalid.");
		
		// get original string
		String original;
		try {
			original = carrierSet.getIdentifierString();
		}
		catch (RodinDBException e) {
			throw new DataException("Error while accessing carrier set");
		}
		
		// remove entry if renaming equals the original else add it
		if (original.equals(matching))
			carrierSetRenaming.remove(carrierSet);
		else
			carrierSetRenaming.put(carrierSet, matching);
		
	}
	
	/**
	 * Update the matching of a constant with a string.
	 * @param constant
	 * @param matching string
	 * @throws DataException
	 */
	public void updateMatching(IConstant constant, String matching) throws DataException {

		// machines have to be initialized
		if (patternAbstractMachine == null || problemMachine == null)
			throw new DataException("Machines not yet initialized.");
		
		// validate input
		if (constant == null || matching == null || matching.equals(""))
			throw new DataException("One of the inputs is invalid.");
		
		// get original string
		String original;
		try {
			original = constant.getIdentifierString();
		}
		catch (RodinDBException e) {
			throw new DataException("Error while accessing constant");
		}
		
		// remove entry if renaming equals the original else add it
		if (original.equals(matching))
			constantRenaming.remove(constant);
		else
			constantRenaming.put(constant, matching);
		
	}
	
	/**
	 * Update the renaming of a new not-merged event of the pattern refinement machine.
	 * The renaming has to be a non-empty string.
	 * @param event of the pattern refinement
	 * @param renaming of the event
	 * @throws DataException
	 */
	public void updateRenamingOfNewEvent(IEvent event, String renaming) throws DataException {

		// machine has to be initialized
		if (patternRefinementMachine == null)
			throw new DataException("Machine not yet initialized.");
		
		// validate input
		if (event == null || renaming == null || renaming.equals(""))
			throw new DataException("One of the inputs is invalid.");
		
		// only rename new not-merged pattern events
		if (!notMergedPatternRefinementEvents.contains(event))
			throw new DataException("Event has to be a new not-merged pattern refinement event.");
		
		// get original string
		String original;
		try {
			original = event.getLabel();
		}
		catch (RodinDBException e) {
			throw new DataException("Error while accessing event");
		}
		
		// remove entry if renaming equals the original else add it
		if (original.equals(renaming))
			newEventRenaming.remove(event);
		else
			newEventRenaming.put(event, renaming);
		
	}
	
	/**
	 * Update the renaming of a problem event that is merged with one or several new event of the pattern
	 * refinement machine. The renaming has to be a non-empty string.
	 * @param event of the problem
	 * @param renaming the event
	 * @throws DataException
	 */
	public void updateRenamingOfMergedEvent(IEvent event, String renaming) throws DataException {

		// machine has to be initialized
		if (patternRefinementMachine == null)
			throw new DataException("Machine not yet initialized.");
		
		// validate input
		if (event == null || renaming == null || renaming.equals(""))
			throw new DataException("One of the inputs is invalid.");
		
		// only rename merged problem events
		if (!mergedProblemEvents.contains(event))
			throw new DataException("Event has to be a merged problem event.");
		
		// get original string
		String original;
		try {
			original = event.getLabel();
		}
		catch (RodinDBException e) {
			throw new DataException("Error while accessing event");
		}
		
		// remove entry if renaming equals the original else add it
		if (original.equals(renaming))
			mergedEventRenaming.remove(event);
		else
			mergedEventRenaming.put(event, renaming);
					
	}
	
	/**
	 * Update the renaming of a event of the pattern refinement machine that refines a matched pattern event.
	 * Since pattern events can be matched with more than one problem event it is necessary to provide also
	 * the problem event. The renaming has to be a non-empty string.
	 * @param patternRefinement event
	 * @param problem event
	 * @param renaming of the event
	 * @throws DataException
	 */
	public void updateRenamingOfMatchedEvent(IEvent patternRefinement, IEvent problem, String renaming) throws DataException {

		// machines have to be initialized
		if (patternAbstractMachine == null || problemMachine == null)
			throw new DataException("Machines not yet initialized.");
		
		// validate input
		if (patternRefinement == null || problem == null || renaming == null || renaming.equals(""))
			throw new DataException("One of the inputs is invalid.");
		
		// only rename refinements of matched pattern events
		if (!refinedPatternRefinementEvents.contains(patternRefinement))
			throw new DataException("First event has to be a refinement of a matched pattern event.");
		if (!matchedProblemEvents.contains(problem))
			throw new DataException("Second event has to be a matched problem event.");
		
		// get original string
		String original;
		try {
			original = problem.getLabel();
		}
		catch (RodinDBException e) {
			throw new DataException("Error while accessing event");
		}
		
		// remove entry if renaming equals the original else add it
		if (original.equals(renaming))
			matchedEventRenaming.remove(new Pair<IEvent,IEvent>(patternRefinement, problem));
		else
			matchedEventRenaming.put(new Pair<IEvent,IEvent>(patternRefinement, problem), renaming);
		
	}
	
	/**
	 * Update the replacement expression for a disappearing variable of the pattern machine
	 * in relation to the pattern refinement.
	 * The replacement has to be a non-empty string.
	 * @param variable
	 * @param replacement of the variable
	 * @throws DataException
	 */
	public void updateForwardReplacementOf(IVariable variable, String replacement) throws DataException {

		// machine has to be initialized
		if (patternRefinementMachine == null)
			throw new DataException("Machine not yet initialized.");
		
		// validate input
		if (variable == null || replacement == null || replacement.equals(""))
			throw new DataException("One of the inputs is invalid.");
		
		// only replace new pattern variables
		if (!disappearingPatternVariables.contains(variable)){
			IMachineRoot parent = (IMachineRoot)variable.getParent();
			Collection<IVariable> disappearingVariables = intermediateDisappearingVariables.get(parent);
			if (disappearingVariables == null || !disappearingVariables.contains(variable))
				throw new DataException("Variable has to be a disappearing pattern variable.");
		}
		
		// get variable at disappearing location
		try {
			variable = variableDisappearing.get(variable.getIdentifierString());
		} catch (RodinDBException e) {
			throw new DataException("Error occurred while getting variable at disappearing location.");
		}
		
		// parse expression
		Expression expression = ff.parseExpression(replacement, LanguageVersion.LATEST, null).getParsedExpression();
		if (expression == null)
			throw new DataException("Given replacement is not an expression.");
		if(!forwardDependencies.containsKey(variable))
			forwardDependencies.put(variable, new HashSet<IVariable>());
		Collection<IVariable> dependencies = forwardDependencies.get(variable);
		dependencies.clear();
		for (FreeIdentifier identifier : expression.getFreeIdentifiers()) {
			IVariable var = variableDisappearing.get(identifier.getName());
			if (var == null)
				throw new DataException("Identifier in given replacement does not exist in further refinements.");
			if (!isNewerThan(variable, var))
				throw new DataException("Identifier in given replacement appears before or within the current machine.");
			if (!variableRemainings.containsKey(identifier.getName()))
				dependencies.add(var);
				
		}
		variableForwardReplacement.put(variable, expression);
		
		
	}
	
	public void updateBackwardReplacementOf(IVariable variable, String replacement) throws DataException {

		// machine has to be initialized
		if (patternRefinementMachine == null)
			throw new DataException("Machine not yet initialized.");
		
		// validate input
		if (variable == null || replacement == null || replacement.equals(""))
			throw new DataException("One of the inputs is invalid.");
		
		// only replace disappearing pattern variables
		if (!newPatternRefinementVariables.contains(variable)){
			IMachineRoot parent = (IMachineRoot)variable.getParent();
			Collection<IVariable> newVariables = intermediateNewVariables.get(parent);
			if (newVariables == null || !newVariables.contains(variable))
				throw new DataException("Variable has to be a new pattern refinement variable.");
		}
		
		// parse expression
		Expression expression = ff.parseExpression(replacement, LanguageVersion.LATEST, null).getParsedExpression();
		
		if (expression == null)
			throw new DataException("Given replacement is not an expression.");
		if(!backwardDependencies.containsKey(variable))
			backwardDependencies.put(variable, new HashSet<IVariable>());
		Collection<IVariable> dependencies = backwardDependencies.get(variable);
		dependencies.clear();
		for (FreeIdentifier identifier : expression.getFreeIdentifiers()) {
			IVariable var = variableAppearing.get(identifier.getName());
			if (var == null)
				throw new DataException("Identifier in given replacement does not exist in further refinements.");
			if (!isNewerThan(var, variable))
				throw new DataException("Identifier in given replacement appears before or within the current machine.");
			if (!variablesEntries.containsKey(identifier.getName()))
				dependencies.add(var);
		}
		variableBackwardReplacement.put(variable, expression);
		
		
	}
	
	/**
	 * Update the replacement expression for a disappearing parameter of the pattern machine
	 * in relation to the pattern refinement.
	 * The replacement has to be a non-empty string.
	 * @param parameter
	 * @param replacement of the parameter
	 * @throws DataException
	 */
	public void updateReplacementOf(IWitness witness, String replacement) throws DataException {

		// machine has to be initialized
		if (patternRefinementMachine == null)
			throw new DataException("Machine not yet initialized.");
		
		// validate input
		if (witness == null || replacement == null || replacement.equals(""))
			throw new DataException("One of the inputs is invalid.");
		
		// only replace pattern parameters
		if (!parameterReplacement.containsKey(witness))
			throw new DataException("Witness not for a relevant parameter.");
		
		// parse expression
		Expression expression = ff.parseExpression(replacement, LanguageVersion.LATEST, null).getParsedExpression();
		if (expression == null)
			throw new DataException("Given replacement is not an expression.");
			
		parameterReplacement.put(witness, expression);
		
	}
	
	/**
	 * Calculates the map of parameter for a given collection of events.
	 * @param events collection
	 * @param parameterMap that will contain the parameters grouped by event
	 * @throws RodinDBException
	 */
	private void calculateParameters (Collection<IEvent> events, HashMap<IEvent, Collection<IParameter>> parameterMap) throws RodinDBException {
		for (IEvent event : events) {
			Collection<IParameter> parameters = new ArrayList<IParameter>();
			for (IParameter parameter : event.getParameters())
				parameters.add(parameter);
			parameterMap.put(event, parameters);
		}
	}
	
	/**
	 * Calculates the map of guards for a given collection of events.
	 * @param events collection
	 * @param guardMap that will contain the guards grouped by event
	 * @throws RodinDBException
	 */
	private void calculateGuards (Collection<IEvent> events, HashMap<IEvent, Collection<IGuard>> guardMap) throws RodinDBException {
		for (IEvent event : events) {
			Collection<IGuard> guards = new ArrayList<IGuard>();
			for (IGuard guard : event.getGuards())
				guards.add(guard);
			guardMap.put(event, guards);
		}
	}
	
	/**
	 * Calculates the map of actions for a given collection of events.
	 * @param events collection
	 * @param actionMap that will contain the action grouped by event
	 * @throws RodinDBException
	 */
	private void calculateActions (Collection<IEvent> events, HashMap<IEvent, Collection<IAction>> actionMap) throws RodinDBException {
		for (IEvent event : events) {
			Collection<IAction> actions = new ArrayList<IAction>();
			for (IAction action : event.getActions())
				actions.add(action);
			actionMap.put(event, actions);
		}
	}
	
	/**
	 * The given matching machine is fed into the data.
	 * @param matching
	 * @throws DataException
	 */
	public void loadMatching(IPatternRoot root) throws DataException {
		
		// input validation
		if (root == null || !root.exists())
			throw new DataException("Input is null or does not exist.");
		IMachineRoot loadPatternMachine;
		IMachineRoot loadProblemMachine;
		
		// get pattern machine 
		try {
			IRodinProject patternProject = RodinCore.getRodinDB().getRodinProject(root.getPatternProject());
			loadPatternMachine = (IMachineRoot)patternProject.getRodinFile(root.getPatternMachine()+".bum").getRoot();
		}
		catch (RodinDBException e) {
			throw new DataException("Error while loading pattern machine.");
		}
		// set pattern abstract machine
		changePatternAbstractMachine(loadPatternMachine);
		// get problem machine
		try {
			IRodinProject problemProject = RodinCore.getRodinDB().getRodinProject(root.getProblemProject());
			loadProblemMachine = (IMachineRoot)problemProject.getRodinFile(root.getProblemMachine()+".bum").getRoot();
		}
		catch (RodinDBException e) {
			throw new DataException("Error while loading problem machine.");
		}
		// set problem machine
		changeProblemMachine(loadProblemMachine);
		
		// get variable matchings
		try {
			for (IVariableMatching matching : root.getVariableMatchings())
				// add variable matching
				addMatching(
						PatternUtils.getElementByIdentifier(IVariable.ELEMENT_TYPE, matching.getPatternVariable(), loadPatternMachine),
						PatternUtils.getElementByIdentifier(IVariable.ELEMENT_TYPE, matching.getProblemVariable(), loadProblemMachine));
		}
		catch (RodinDBException e) {
			throw new DataException("Error while loading variable matching.");
		}
		
		// get event matchings
		try {
			for (IEventMatching matching : root.getEventMatchings()){
				// get matched events
				IEvent patternEvent = PatternUtils.getElementByLabel(IEvent.ELEMENT_TYPE, matching.getPatternEvent(), loadPatternMachine);
				IEvent problemEvent = PatternUtils.getElementByLabel(IEvent.ELEMENT_TYPE, matching.getProblemEvent(), loadProblemMachine);
				// add event matching
				addMatching(patternEvent, problemEvent);
				
				// get parameter matchings
				for (IParameterMatching match : matching.getParameterMatchings())
					// add parameter matching
					addMatching(
							PatternUtils.getElementByIdentifier(IParameter.ELEMENT_TYPE, match.getPatternParameter(), patternEvent),
							PatternUtils.getElementByIdentifier(IParameter.ELEMENT_TYPE, match.getProblemParameter(), problemEvent));
							
				// get guard matchings
				for (IGuardMatching match : matching.getGuardMatchings())
					// add guard matching
					addMatching(
							PatternUtils.getElementByLabel(IGuard.ELEMENT_TYPE, match.getPatternGuard(), patternEvent),
							PatternUtils.getElementByLabel(IGuard.ELEMENT_TYPE, match.getPatternGuard(), problemEvent));
							
				// get action matchings
				for (IActionMatching match : matching.getActionMatchings())
					// add action matching
					addMatching(
							PatternUtils.getElementByLabel(IAction.ELEMENT_TYPE, match.getPatternAction(), patternEvent),
							PatternUtils.getElementByLabel(IAction.ELEMENT_TYPE, match.getProblemAction(), problemEvent));
				}
		}
		catch (RodinDBException e) {
			throw new DataException("Error while loading event matching.");
		}
		// collect seen carrierSets and constants
		HashMap<String, ICarrierSet> carrierSets = new HashMap<String, ICarrierSet>();
		HashMap<String, IConstant> constants = new HashMap<String, IConstant>();
		try {
			for (ISeesContext context : loadPatternMachine.getSeesClauses()) {
				IContextRoot contextRoot = context.getSeenContextRoot();
				for (ICarrierSet carrierSet : contextRoot.getCarrierSets())
					carrierSets.put(carrierSet.getIdentifierString(), carrierSet);
				for (IConstant constant : contextRoot.getConstants())
					constants.put(constant.getIdentifierString(), constant);
			}
		}
		catch (RodinDBException e) {
			throw new DataException("Error while collecting seen context.");
		}
		// get carrierSet matchings
		try {
			for (ICarrierSetMatching matching : root.getCarrierSetMatchings())
				// add carrierSet matching
				updateMatching(carrierSets.get(matching.getPatternCarrierSet()), matching.getProblemCarrierSet());
		}
		catch (RodinDBException e) {
			throw new DataException("Error while loading carrierSet matching.");
		}
		// get constant matchings
		try {
			for (IConstantMatching matching : root.getConstantMatchings())
				// add constant matching
				updateMatching(constants.get(matching.getPatternConstant()), matching.getProblemConstant());
		}
		catch (RodinDBException e) {
			throw new DataException("Error while loading constant matching.");
		}
	}
	
	/**
	 * @return all events of the abstract pattern machine.
	 * @throws DataException
	 */
	public Collection<IEvent> getAllPatternEvents() throws DataException {
		
		// check initialization
		if (patternAbstractMachine == null)
			throw new DataException("Pattern machine not yet initialized");
		return new HashSet<IEvent>(allPatternEvents);
			
	}
	
	/**
	 * @return all events of the pattern refinement machine.
	 * @throws DataException
	 */
	public Collection<IEvent> getAllPatternRefinementEvents() throws DataException {
		
		// check initialization
		if (patternRefinementMachine == null)
			throw new DataException("Pattern refinement machine not yet initialized");
		return new HashSet<IEvent>(allPatternRefinementEvents);
			
	}
	
	/**
	 * @return all events of the problem machine.
	 * @throws DataException
	 */
	public Collection<IEvent> getAllProblemEvents() throws DataException {
		
		// check initialization
		if (problemMachine == null)
			throw new DataException("Problem machine not yet initialized");
		return new HashSet<IEvent>(allProblemEvents);
			
	}
	
	/**
	 * @return all events of the pattern refinement machine that do not refine a matched event.
	 * @throws DataException
	 */
	public Collection<IEvent> getNewPatternRefinementEvents() throws DataException {
		
		// check initialization
		if (patternRefinementMachine == null)
			throw new DataException("Pattern refinement machine not yet initialized");
		return new HashSet<IEvent>(newPatternRefinementEvents);
			
	}
	
	/**
	 * @return all events of the pattern refinement machine that do refine a matched event.
	 * @throws DataException
	 */
	public Collection<IEvent> getRefinedPatternRefinementEvents() throws DataException {
		
		// check initialization
		if (patternRefinementMachine == null)
			throw new DataException("Pattern refinement machine not yet initialized");
		return new HashSet<IEvent>(refinedPatternRefinementEvents);
		
	}
	
	/**
	 * @return all events of the pattern refinement machine that will be merged with a problem event.
	 * @throws DataException
	 */
	public Collection<IEvent> getMergedPatternRefinementEvents() throws DataException {

		// check initialization
		if (patternRefinementMachine == null || problemMachine == null)
			throw new DataException("Pattern refinement machine or problem machine not yet initialized");
		return new HashSet<IEvent>(mergedPatternRefinementEvents);
		
	}
	
	/**
	 * @return all events of the pattern refinement machine that won't be merged with a problem event.
	 * @throws DataException
	 */
	public Collection<IEvent> getNotMergedPatternRefinementEvents() throws DataException {
		
		// check initialization
		if (patternRefinementMachine == null || problemMachine == null)
			throw new DataException("Pattern refinement machine or problem machine not yet initialized");
		return new HashSet<IEvent>(notMergedPatternRefinementEvents);
		
	}
	
	/**
	 * @return all events of the problem machine that will be merged with one or more pattern refinement events.
	 * @throws DataException
	 */
	public Collection<IEvent> getMergedProblemEvents() throws DataException {

		// check initialization
		if (patternRefinementMachine == null || problemMachine == null)
			throw new DataException("Pattern refinement machine or problem machine not yet initialized");
		return new HashSet<IEvent>(mergedProblemEvents);
	
	}
	
	/**
	 * @return all events of the problem machine that won't be merged with any pattern refinement event.
	 * @throws DataException
	 */
	public Collection<IEvent> getNotMergedProblemEvents() throws DataException {
		
		// check initialization
		if (patternRefinementMachine == null || problemMachine == null)
			throw new DataException("Pattern refinement machine or problem machine not yet initialized");
		return new HashSet<IEvent>(notMergedProblemEvents);
		
	}
	
	/**
	 * @return all events of the pattern machine that are matched with a problem event.
	 * @throws DataException
	 */
	public Collection<IEvent> getMatchedPatternEvents() throws DataException {
		
		// check initialization
		if (patternAbstractMachine == null || problemMachine == null)
			throw new DataException("Pattern machine or problem machine not yet initialized");
		return new HashSet<IEvent>(matchedPatternEvents);
		
	}
	
	/**
	 * @return all events of the pattern machine that are not matched with any problem event.
	 * @throws DataException
	 */
	public Collection<IEvent> getNotMatchedPatternEvents() throws DataException {

		// check initialization
		if (patternAbstractMachine == null || problemMachine == null)
			throw new DataException("Pattern machine or problem machine not yet initialized");
		return new HashSet<IEvent>(notMatchedPatternEvents);
		
	}
	
	/**
	 * @return all events of the problem machine that are matched with one or several pattern events. 
	 * @throws DataException
	 */
	public Collection<IEvent> getMatchedProblemEvents() throws DataException {

		// check initialization
		if (patternAbstractMachine == null || problemMachine == null)
			throw new DataException("Pattern machine or problem machine not yet initialized");
		return new HashSet<IEvent>(matchedProblemEvents);
		
	}
	
	/**
	 * @return all events of the problem machine that are not matched with any pattern event.
	 * @throws DataException
	 */
	public Collection<IEvent> getNotMatchedProblemEvents() throws DataException {

		// check initialization
		if (patternAbstractMachine == null || problemMachine == null)
			throw new DataException("Pattern machine or problem machine not yet initialized");
		return new HashSet<IEvent>(notMatchedProblemEvents);
		
	}
	
	/**
	 * @return all variables of the pattern machine.
	 * @throws DataException
	 */
	public Collection<IVariable> getAllPatternVariables() throws DataException {

		// check initialization
		if (patternAbstractMachine == null)
			throw new DataException("Pattern machine not yet initialized");
		return new HashSet<IVariable>(allPatternVariables);
		
	}
	
	/**
	 * @return all variables of the pattern machine that are matched with a problem variable.
	 * @throws DataException
	 */
	public Collection<IVariable> getMatchedPatternVariables() throws DataException {

		// check initialization
		if (patternAbstractMachine == null || problemMachine == null)
			throw new DataException("Pattern machine or problem machine not yet initialized");
		return new HashSet<IVariable>(matchedPatternVariables);
		
	}
	
	/**
	 * @return all variables of the pattern machine that are not matched with any problem variable.
	 * @throws DataException
	 */
	public Collection<IVariable> getNotMatchedPatternVariables() throws DataException {

		// check initialization
		if (patternAbstractMachine == null || problemMachine == null)
			throw new DataException("Pattern machine or problem machine not yet initialized");
		return new HashSet<IVariable>(notMatchedPatternVariables);
		
	}
	
	/**
	 * @return all variables of the pattern machine that do not exist anymore in the pattern refinement machine.
	 * @throws DataException
	 */
	public Collection<IVariable> getDisappearingPatternVariables() throws DataException {

		// check initialization
		if (patternRefinementMachine == null)
			throw new DataException("Pattern refinement machine not yet initialized");
		return new HashSet<IVariable>(disappearingPatternVariables);
		
	}
	
	public Collection<IVariable> getDisappearingPatternVariablesAtLocation() throws DataException {

		// check initialization
		if (patternRefinementMachine == null)
			throw new DataException("Pattern refinement machine not yet initialized");
		Collection<IVariable> result = new HashSet<IVariable>();
		for (IVariable variable : disappearingPatternVariables)
			try {
				result.add(variableDisappearing.get(variable.getIdentifierString()));
			} catch (RodinDBException e) {
				throw new DataException("Error occurred while getting variable at disappearing location.");
			}
		return result;
		
	}
	
	public Collection<IVariable> getNewPatternRefinementVariables() throws DataException {

		// check initialization
		if (patternRefinementMachine == null)
			throw new DataException("Pattern refinement machine not yet initialized");
		return new HashSet<IVariable>(newPatternRefinementVariables);
		
	}
	
	public Collection<IVariable> getRelatedPatternVariables() throws DataException {

		// check initialization
		if (patternRefinementMachine == null)
			throw new DataException("Pattern refinement machine not yet initialized");
		return new HashSet<IVariable>(relatedVariables);
		
	}
	
	/**
	 * @return all variables of the pattern machine that still exist in the pattern refinement machine.
	 * @throws DataException
	 */
	public Collection<IVariable> getRemainingPatternVariables() throws DataException {

		// check initialization
		if (patternRefinementMachine == null)
			throw new DataException("Pattern refinement machine not yet initialized");
		return new HashSet<IVariable>(remainingPatternVariables);
		
	}
	
	/**
	 * @return all variables of the pattern refinement machine.
	 * @throws DataException
	 */
	public Collection<IVariable> getAllPatternRefinementVariables() throws DataException {

		// check initialization
		if (patternRefinementMachine == null)
			throw new DataException("Pattern refinement machine not yet initialized");
		return new HashSet<IVariable>(allPatternRefinementVariables);
		
	}
	
	/**
	 * @return all variables of the problem machine.
	 * @throws DataException
	 */
	public Collection<IVariable> getAllProblemVariables() throws DataException {

		// check initialization
		if (problemMachine == null)
			throw new DataException("Problem machine not yet initialized");
		return new HashSet<IVariable>(allProblemVariables);
		
	}
	
	/**
	 * @return all variables of the problem machine that are matched with one or several pattern variables.
	 * @throws DataException
	 */
	public Collection<IVariable> getMatchedProblemVariables() throws DataException {

		// check initialization
		if (patternAbstractMachine == null || problemMachine == null)
			throw new DataException("Pattern machine or problem machine not yet initialized");
		return new HashSet<IVariable>(matchedProblemVariables);
		
	}
	
	/**
	 * @return all variables of the problem machine that are not matched with any pattern variable.
	 * @throws DataException
	 */
	public Collection<IVariable> getNotMatchedProblemVariables() throws DataException {

		// check initialization
		if (patternAbstractMachine == null || problemMachine == null)
			throw new DataException("Pattern machine or problem machine not yet initialized");
		return new HashSet<IVariable>(notMatchedProblemVariables);
		
	}
	
	/**
	 * @param event of the pattern machine
	 * @return all parameters of the given pattern event.
	 * @throws DataException
	 */
	private Collection<IParameter> getAllPatternParametersOf(IEvent event) throws DataException {
		
		// input validation
		if (event == null || !event.exists())
			throw new DataException("Input is null or does not exist.");
		// check initialization
		if (patternAbstractMachine == null)
			throw new DataException("Pattern machine not yet initialized");
		// check input restrictions
		if (!allPatternEvents.contains(event))
			throw new DataException("Event has to be a pattern event.");
		return new HashSet<IParameter>(allPatternParameters.get(event));
		
	}
	
	/**
	 * @param event of the problem machine
	 * @return all parameters of the given problem event.
	 * @throws DataException
	 */
	private Collection<IParameter> getAllProblemParametersOf(IEvent event) throws DataException {

		// input validation
		if (event == null || !event.exists())
			throw new DataException("Input is null or does not exist.");
		// check initialization
		if (problemMachine == null)
			throw new DataException("Problem machine not yet initialized");
		// check input restrictions
		if (!allProblemEvents.contains(event))
			throw new DataException("Event has to be a problem event.");
		return new HashSet<IParameter>(allProblemParameters.get(event));
		
	}
	
	/**
	 * @param event of the pattern refinement machine
	 * @return all parameters of the given pattern refinement event.
	 * @throws DataException
	 */
	public Collection<IParameter> getAllPatternRefinementParametersOf(IEvent event) throws DataException {

		// input validation
		if (event == null || !event.exists())
			throw new DataException("Input is null or does not exist.");
		// check initialization
		if (patternRefinementMachine == null)
			throw new DataException("Pattern refinement machine not yet initialized");
		// check input restrictions
		if (!allPatternRefinementEvents.contains(event))
			throw new DataException("Event has to be a pattern refinement event.");
		return new HashSet<IParameter>(allPatternRefinementParameters.get(event));
		
	}
	
	/**
	 * @param event of the pattern or problem machine that is matched
	 * @return all matched parameters of the given event.
	 * @throws DataException
	 */
	public Collection<IParameter> getMatchedParametersOf(IEvent event) throws DataException {
		
		// input validation
		if (event == null || !event.exists())
			throw new DataException("Input is null or does not exist.");
		// check initialization
		if (patternAbstractMachine == null || problemMachine == null)
			throw new DataException("Pattern machine or problem machine not yet initialized");
		// check input restrictions
		if (!matchedPatternEvents.contains(event) && ! matchedProblemEvents.contains(event))
			throw new DataException("Event has to be a matched pattern event or a matched problem event.");
		return new HashSet<IParameter>(matchedParameters.get(event));
		
	}
	
	/**
	 * @param event of the pattern or problem machine that is matched
	 * @return all not-matched parameters of the given event.
	 * @throws DataException
	 */
	public Collection<IParameter> getNotMatchedParametersOf(IEvent event) throws DataException {

		// input validation
		if (event == null || !event.exists())
			throw new DataException("Input is null or does not exist.");
		// check initialization
		if (patternAbstractMachine == null || problemMachine == null)
			throw new DataException("Pattern machine or problem machine not yet initialized");
		// check input restrictions
		if (!matchedPatternEvents.contains(event) && ! matchedProblemEvents.contains(event))
			throw new DataException("Event has to be a matched pattern event or a matched problem event.");
		return new HashSet<IParameter>(notMatchedParameters.get(event));
		
	}
	
	/**
	 * @param event of the pattern machine
	 * @return all guards of the given pattern event.
	 * @throws DataException
	 */
	private Collection<IGuard> getAllPatternGuardsOf(IEvent event) throws DataException {
		
		// input validation
		if (event == null || !event.exists())
			throw new DataException("Input is null or does not exist.");
		// check initialization
		if (patternAbstractMachine == null)
			throw new DataException("Pattern machine not yet initialized");
		// check input restrictions
		if (!allPatternEvents.contains(event))
			throw new DataException("Event has to be a pattern event.");
		return new HashSet<IGuard>(allPatternGuards.get(event));
		
	}
	
	/**
	 * @param event of the problem event
	 * @return all guards of the given problem event.
	 * @throws DataException
	 */
	private Collection<IGuard> getAllProblemGuardsOf(IEvent event) throws DataException {

		// input validation
		if (event == null || !event.exists())
			throw new DataException("Input is null or does not exist.");
		// check initialization
		if (problemMachine == null)
			throw new DataException("Problem machine not yet initialized");
		// check input restrictions
		if (!allProblemEvents.contains(event))
			throw new DataException("Event has to be a problem event.");
		return new HashSet<IGuard>(allProblemGuards.get(event));
		
	}
	
	/**
	 * @param event of the pattern refinement machine
	 * @return all guards of the given pattern refinement event.
	 * @throws DataException
	 */
	public Collection<IGuard> getAllPatternRefinementGuardsOf(IEvent event) throws DataException {

		// input validation
		if (event == null || !event.exists())
			throw new DataException("Input is null or does not exist.");
		// check initialization
		if (patternRefinementMachine == null)
			throw new DataException("Pattern refinement machine not yet initialized");
		// check input restrictions
		if (!allPatternRefinementEvents.contains(event))
			throw new DataException("Event has to be a pattern refinement event.");
		return new HashSet<IGuard>(allPatternRefinementGuards.get(event));
		
	}
	
	/**
	 * @param event of the pattern or problem machine that is matched
	 * @return all matched guards of the given event.
	 * @throws DataException
	 */
	public Collection<IGuard> getMatchedGuardsOf(IEvent event) throws DataException {

		// input validation
		if (event == null || !event.exists())
			throw new DataException("Input is null or does not exist.");
		// check initialization
		if (patternAbstractMachine == null || problemMachine == null)
			throw new DataException("Pattern machine or problem machine not yet initialized");
		// check input restrictions
		if (!matchedPatternEvents.contains(event) && ! matchedProblemEvents.contains(event))
			throw new DataException("Event has to be a matched pattern event or a matched problem event.");
		return new HashSet<IGuard>(matchedGuards.get(event));
		
	}
	
	/**
	 * @param event of the pattern or problem machine that is matched
	 * @return all not-matched guards of the given event.
	 * @throws DataException
	 */
	public Collection<IGuard> getNotMatchedGuardsOf(IEvent event) throws DataException {
		
		// input validation
		if (event == null || !event.exists())
			throw new DataException("Input is null or does not exist.");
		// check initialization
		if (patternAbstractMachine == null || problemMachine == null)
			throw new DataException("Pattern machine or problem machine not yet initialized");
		// check input restrictions
		if (!matchedPatternEvents.contains(event) && ! matchedProblemEvents.contains(event))
			throw new DataException("Event has to be a matched pattern event or a matched problem event.");
		return new HashSet<IGuard>(notMatchedGuards.get(event));
		
	}
	
	/**
	 * @param event of the pattern machine
	 * @return all actions of the given pattern event.
	 * @throws DataException
	 */
	private Collection<IAction> getAllPatternActionsOf(IEvent event) throws DataException {

		// input validation
		if (event == null || !event.exists())
			throw new DataException("Input is null or does not exist.");
		// check initialization
		if (patternAbstractMachine == null)
			throw new DataException("Pattern machine not yet initialized");
		// check input restrictions
		if (!allPatternEvents.contains(event))
			throw new DataException("Event has to be a pattern event.");
		return new HashSet<IAction>(allPatternActions.get(event));
		
	}
	
	/**
	 * @param event of the problem machine
	 * @return all actions of the given problem event.
	 * @throws DataException
	 */
	private Collection<IAction> getAllProblemActionsOf(IEvent event) throws DataException {

		// input validation
		if (event == null || !event.exists())
			throw new DataException("Input is null or does not exist.");
		// check initialization
		if (problemMachine == null)
			throw new DataException("Problem machine not yet initialized");
		// check input restrictions
		if (!allProblemEvents.contains(event))
			throw new DataException("Event has to be a problem event.");
		return new HashSet<IAction>(allProblemActions.get(event));
		
	}
	
	/**
	 * @param event of the pattern refinement machine
	 * @return all actions of the given pattern refinement event.
	 * @throws DataException
	 */
	public Collection<IAction> getAllPatternRefinementActionsOf(IEvent event) throws DataException {

		// input validation
		if (event == null || !event.exists())
			throw new DataException("Input is null or does not exist.");
		// check initialization
		if (patternRefinementMachine == null)
			throw new DataException("Pattern refinement machine not yet initialized");
		// check input restrictions
		if (!allPatternRefinementEvents.contains(event))
			throw new DataException("Event has to be a pattern refinement event.");
		return new HashSet<IAction>(allPatternRefinementActions.get(event));
		
	}
	
	/**
	 * @param event of the pattern or problem machine that is matched
	 * @return all matched actions of the given event.
	 * @throws DataException
	 */
	public Collection<IAction> getMatchedActionsOf(IEvent event) throws DataException {

		// input validation
		if (event == null || !event.exists())
			throw new DataException("Input is null or does not exist.");
		// check initialization
		if (patternAbstractMachine == null || problemMachine == null)
			throw new DataException("Pattern machine or problem machine not yet initialized");
		// check input restrictions
		if (!matchedPatternEvents.contains(event) && ! matchedProblemEvents.contains(event))
			throw new DataException("Event has to be a matched pattern event or a matched problem event.");
		return new HashSet<IAction>(matchedActions.get(event));
		
	}
	
	/**
	 * @param event of the pattern or problem machine that is matched
	 * @return all not-matched actions of the given event. 
	 * @throws DataException
	 */
	public Collection<IAction> getNotMatchedActionsOf(IEvent event) throws DataException {

		// input validation
		if (event == null || !event.exists())
			throw new DataException("Input is null or does not exist.");
		// check initialization
		if (patternAbstractMachine == null || problemMachine == null)
			throw new DataException("Pattern machine or problem machine not yet initialized");
		// check input restrictions
		if (!matchedPatternEvents.contains(event) && ! matchedProblemEvents.contains(event))
			throw new DataException("Event has to be a matched pattern event or a matched problem event.");
		return new HashSet<IAction>(notMatchedActions.get(event));
		
	}
	
	/**
	 * @param event of the pattern or problem machine that is matched
	 * @return all the events that are matched with the given event.
	 * @throws DataException
	 */
	public Collection<IEvent> getMatchingsOf(IEvent event) throws DataException {

		// input validation
		if (event == null || !event.exists())
			throw new DataException("Input is null or does not exist.");
		// check initialization
		if (patternAbstractMachine == null || problemMachine == null)
			throw new DataException("Pattern machine or problem machine not yet initialized");
		if (!matchedPatternEvents.contains(event) && ! matchedProblemEvents.contains(event))
			throw new DataException("Event has to be a matched pattern event or a matched problem event.");
		return new HashSet<IEvent>(eventMatchings.get(event));
		
	}
	
	/**
	 * @param variable of the pattern or problem machine that is matched
	 * @return the variable that is matched with the given variable.
	 * @throws DataException
	 */
	public IVariable getMatchingOf(IVariable variable) throws DataException {
		
		// input validation
		if (variable == null || !variable.exists())
			throw new DataException("Input is null or does not exist.");
		// check initialization
		if (patternAbstractMachine == null || problemMachine == null)
			throw new DataException("Pattern machine or problem machine not yet initialized");
		// check input restrictions
		if (!matchedPatternVariables.contains(variable) && ! matchedProblemVariables.contains(variable))
			throw new DataException("Variable has to be a matched pattern variable or a matched problem variable.");
		return variableMatchings.get(variable);
		
	}
	
	/**
	 * @param parameter of the pattern or problem machine that is matched
	 * @return the parameter that is matched with the given parameter.
	 * @throws DataException
	 */
	public IParameter getMatchingOf(IParameter parameter) throws DataException {
		
		// input validation
		if (parameter == null || !parameter.exists())
			throw new DataException("Input is null or does not exist.");
		// check initialization
		if (patternAbstractMachine == null || problemMachine == null)
			throw new DataException("Pattern machine or problem machine not yet initialized");
		// check input restrictions
		if (!parameterMatchings.containsKey(parameter))
			throw new DataException("Parameter has to be a matched pattern parameter or a matched problem parameter.");
		return parameterMatchings.get(parameter);
		
	}
	
	/**
	 * @param guard of the pattern or problem machine that is matched
	 * @return the guard that is matched with the given guard.
	 * @throws DataException
	 */
	public IGuard getMatchingOf(IGuard guard) throws DataException {
		
		// input validation
		if (guard == null || !guard.exists())
			throw new DataException("Input is null or does not exist.");
		// check initialization
		if (patternAbstractMachine == null || problemMachine == null)
			throw new DataException("Pattern machine or problem machine not yet initialized");
		// check input restrictions
		if (!guardMatchings.containsKey(guard))
			throw new DataException("Guard has to be a matched pattern guard or a matched problem guard.");
		return guardMatchings.get(guard);
		
	}
	
	/**
	 * @param action of the pattern or problem machine that is matched
	 * @return the action that is matched with the give action.
	 * @throws DataException
	 */
	public IAction getMatchingOf(IAction action) throws DataException {
		
		// input validation
		if (action == null || !action.exists())
			throw new DataException("Input is null or does not exist.");
		// check initialization
		if (patternAbstractMachine == null || problemMachine == null)
			throw new DataException("Pattern machine or problem machine not yet initialized");
		// check input restrictions
		if (!actionMatchings.containsKey(action))
			throw new DataException("Action has to be a matched pattern action or a matched problem action.");
		return actionMatchings.get(action);
		
	}
	
	/**
	 * @param variable of the pattern refinement machine
	 * @return the renaming of the given variable (null if there is no renaming).
	 * @throws DataException
	 */
	public String getRenamingOf(IVariable variable) throws DataException {

		// input validation
		if (variable == null || !variable.exists())
			throw new DataException("Input is null or does not exist.");
		// check initialization
		if (patternRefinementMachine == null)
			throw new DataException("Pattern refinement machine not yet initialized");
		// check input restrictions
		if (!allPatternRefinementVariables.contains(variable))
			throw new DataException("Variable has to be a pattern refinement variable.");
		return variableRenaming.get(variable);
				
	}
	
	/**
	 * @return all variables for which a renaming is defined.
	 * @throws DataException
	 */
	public Collection<IVariable> getRenamedVariables() throws DataException {
	
		// check initialization
		if (patternRefinementMachine == null)
			throw new DataException("Pattern refinement machine not yet initialized");
		return new HashSet<IVariable>(variableRenaming.keySet());
		
	}
	
	/**
	 * @param carrierSet
	 * @return matching of the given carrier set in form of a string.
	 * @throws DataException
	 */
	public String getMatchingOf(ICarrierSet carrierSet) throws DataException {

		// input validation
		if (carrierSet == null || !carrierSet.exists())
			throw new DataException("Input is null or does not exist.");
		// check initialization
		if (patternRefinementMachine == null)
			throw new DataException("Pattern refinement machine not yet initialized");
		return carrierSetRenaming.get(carrierSet);
		
	}
	
	/**
	 * @return all carrier sets that are matched.
	 * @throws DataException
	 */
	public Collection<ICarrierSet> getMatchedCarrierSets() throws DataException {

		// check initialization
		if (patternRefinementMachine == null)
			throw new DataException("Pattern refinement machine not yet initialized");
		return new HashSet<ICarrierSet>(carrierSetRenaming.keySet());
		
	}	
	
	/**
	 * @param constant
	 * @return matching of the given constant in form of a string.
	 * @throws DataException
	 */
	public String getMatchingOf(IConstant constant) throws DataException {

		// input validation
		if (constant == null || !constant.exists())
			throw new DataException("Input is null or does not exist.");
		// check initialization
		if (patternRefinementMachine == null)
			throw new DataException("Pattern refinement machine not yet initialized");
		return constantRenaming.get(constant);
		
	}
	
	/**
	 * @return all matched constants.
	 * @throws DataException
	 */
	public Collection<IConstant> getMatchedConstants() throws DataException {

		// check initialization
		if (patternRefinementMachine == null)
			throw new DataException("Pattern refinement machine not yet initialized");
		return new HashSet<IConstant>(constantRenaming.keySet());
		
	}	
	
	/**
	 * @param event of the pattern refinement machine that is new and not merged
	 * @return the renaming of the given event (null if there is no renaming).
	 * @throws DataException
	 */
	public String getRenamingOfNewEvent(IEvent event) throws DataException {

		// input validation
		if (event == null || !event.exists())
			throw new DataException("Input is null or does not exist.");
		// check initialization
		if (patternRefinementMachine == null)
			throw new DataException("Pattern refinement machine not yet initialized");
		// check input restrictions
		if (!newPatternRefinementEvents.contains(event))
			throw new DataException("Event has to be a new pattern refinement event.");
		return newEventRenaming.get(event);
		
	}
	
	/**
	 * @return all new not-merged events for which a renaming is defined.
	 * @throws DataException
	 */
	public Collection<IEvent> getRenamedNewEvents() throws DataException {

		// check initialization
		if (patternRefinementMachine == null)
			throw new DataException("Pattern refinement machine not yet initialized");
		return new HashSet<IEvent>(newEventRenaming.keySet());
		
	}
	
	/**
	 * @param event of the pattern refinement machine that is new but merged
	 * @return the renaming of the given event (null if there is no renaming).
	 * @throws DataException
	 */
	public String getRenamingOfMergedEvent(IEvent event) throws DataException {

		// input validation
		if (event == null || !event.exists())
			throw new DataException("Input is null or does not exist.");
		// check initialization
		if (patternRefinementMachine == null)
			throw new DataException("Pattern refinement machine not yet initialized");
		// check input restrictions
		if (!mergedProblemEvents.contains(event))
			throw new DataException("Event has to be a merged pattern refinement event.");
		return mergedEventRenaming.get(event);
		
	}
	
	/**
	 * @return all new but merged events for which a renaming is defined.
	 * @throws DataException
	 */
	public Collection<IEvent> getRenamedMergedEvents() throws DataException {

		// check initialization
		if (patternRefinementMachine == null)
			throw new DataException("Pattern refinement machine not yet initialized");
		return new HashSet<IEvent>(mergedEventRenaming.keySet());
		
	}	
	
	/**
	 * @param patternRefinement event that is matched
	 * @param problem event that is matched
	 * @return the renaming of the given event matching (null if there is no renaming).
	 * @throws DataException
	 */
	public String getRenamingOfMatchedEvent(IEvent patternRefinement, IEvent problem) throws DataException {

		// input validation
		if (patternRefinement == null || !patternRefinement.exists())
			throw new DataException("Input is null or does not exist.");
		if (problem == null || !problem.exists())
			throw new DataException("Input is null or does not exist.");
		// check initialization
		if (patternRefinementMachine == null)
			throw new DataException("Pattern refinement machine not yet initialized");
		// check input restrictions
		if (!refinedPatternRefinementEvents.contains(patternRefinement) || !matchedProblemEvents.contains(problem))
			throw new DataException("Events have to be a refinement of a matched pattern event or a matched problem event.");
		return matchedEventRenaming.get(new Pair<IEvent, IEvent>(patternRefinement, problem));
		
	}
	
	/**
	 * @return all event pairs (pattern refinement event, problem event) for which a renaming is defined.
	 * @throws DataException
	 */
	public Collection<Pair<IEvent,IEvent>> getRenamedMatchedEvents() throws DataException {

		// check initialization
		if (patternRefinementMachine == null)
			throw new DataException("Pattern refinement machine not yet initialized");
		return new HashSet<Pair<IEvent,IEvent>>(matchedEventRenaming.keySet());
		
	}
	
	/**
	 * @param variable
	 * @return the replacement expression for the given variable.
	 * @throws DataException
	 */
	public Expression getForwardReplacementFor(IVariable variable) throws DataException {

		// input validation
		if (variable == null || !variable.exists())
			throw new DataException("Input is null or does not exist.");
		// check initialization
		if (patternRefinementMachine == null)
			throw new DataException("Pattern refinement machine not yet initialized");
		Expression result = variableForwardReplacement.get(variable);
		if (result  == null)
			return defaultExpression;
		return result;
		
	}
	
	/**
	 * @param variable
	 * @return the replacement expression for the given variable.
	 * @throws DataException
	 */
	public Expression getBackwardReplacementFor(IVariable variable) throws DataException {

		// input validation
		if (variable == null || !variable.exists())
			throw new DataException("Input is null or does not exist.");
		// check initialization
		if (patternRefinementMachine == null)
			throw new DataException("Pattern refinement machine not yet initialized");
		return variableBackwardReplacement.get(variable);
		
	}
	
	/**
	 * @return all variables for which a replacement expression is defined.
	 * @throws DataException
	 */
	public Collection<IVariable> getReplacedVariables() throws DataException {

		// check initialization
		if (patternRefinementMachine == null)
			throw new DataException("Pattern refinement machine not yet initialized");
		return new HashSet<IVariable>(variableForwardReplacement.keySet());
		
	}
	
	/**
	 * @param parameter
	 * @return the replacement expression for the given parameter
	 * @throws DataException
	 */
	public Expression getReplacementFor(IParameter parameter) throws DataException {

		// input validation
		if (parameter == null || !parameter.exists())
			throw new DataException("Input is null or does not exist.");
		// check initialization
		if (patternRefinementMachine == null)
			throw new DataException("Pattern refinement machine not yet initialized");
		// get the parameter parent
		IRodinElement parent = parameter.getParent();
		// check that parent is an event and declare it as such
		IEvent event;
		if (parent instanceof IEvent)
			event = (IEvent) parent;
		else
			throw new DataException("Parent is not an event.");
		// find witness
		IWitness correspondingWitness;
		outer: try {
			for (IWitness witness : parameterReplacement.keySet()) {
				if (witness.getParent().equals(event) && witness.getLabel().equals(parameter.getIdentifierString())) {
					correspondingWitness = witness;
					break outer;
				}
			}
			throw new DataException("RodinDBException while seatching for witness.");
		}
		catch (RodinDBException e) {
			throw new DataException("RodinDBException while seatching for witness.");
		}
		return parameterReplacement.get(correspondingWitness);
		
	}
	
	public Expression getReplacementFor(IWitness witness) throws DataException {

		// input validation
		if (witness == null || !witness.exists())
			throw new DataException("Input is null or does not exist.");
		// check initialization
		if (patternRefinementMachine == null)
			throw new DataException("Pattern refinement machine not yet initialized");
		// check input restrictions
		if (!parameterReplacement.containsKey(witness))
			throw new DataException("Witness not for a relevant parameter.");
		return parameterReplacement.get(witness);
		
	}
		
	/**
	 * @param event of the pattern refinement or problem machine that is merged
	 * @return all the events that are merged with the given event.
	 * @throws DataException
	 */
	public Collection<IEvent> getMergingOf(IEvent event) throws DataException {
		
		// input validation
		if (event == null || !event.exists())
			throw new DataException("Input is null or does not exist.");
		// check initialization
		if (patternRefinementMachine == null || problemMachine == null)
			throw new DataException("Pattern refinement machine or problem machine not yet initialized");
		// check input restrictions
		if (!mergedPatternRefinementEvents.contains(event) && ! mergedProblemEvents.contains(event))
			throw new DataException("Event has to be a merged pattern refinement event or a merged problem event.");
		return new HashSet<IEvent>(eventMerging.get(event));
		
	}

	/**
	 * @return the project of the pattern machine.
	 * @throws DataException
	 */
	public IRodinProject getPatternProject () throws DataException {
		
		// check initialization
		if (patternAbstractMachine == null)
			throw new DataException("Pattern machine not yet initialized");
		return patternAbstractMachine.getRodinProject();
		
	}
	
	/**
	 * @return the abstract pattern machine.
	 * @throws DataException
	 */
	public IMachineRoot getPatternAbstractMachine () throws DataException {
		
		// check initialization
		if (patternAbstractMachine == null)
			throw new DataException("Pattern machine not yet initialized");
		return patternAbstractMachine;
	}
	
	/**
	 * @return the pattern refinement machine.
	 * @throws DataException
	 */
	public IMachineRoot getPatternRefinementMachine () throws DataException {
		
		// check initialization
		if (patternRefinementMachine == null)
			throw new DataException("Pattern refinement machine not yet initialized");
		return patternRefinementMachine;
	}
	
	/**
	 * @return the project of the problem machine.
	 * @throws DataException
	 */
	public IEventBProject getProblemEventBProject () throws DataException {
		
		// check initialization
		if (problemMachine == null)
			throw new DataException("Problem machine not yet initialized");
		return problemMachine.getEventBProject();
	}
	
	/**
	 * @return the problem machine.
	 * @throws DataException
	 */
	public IMachineRoot getProblemMachine() throws DataException {
		
		// check initialization
		if (problemMachine == null)
			throw new DataException("Problem machine not yet initialized");
		return problemMachine;
	}
	
	/**
	 * @return all intermediate machines between abstract pattern machine and pattern refinement machine
	 * @throws DataException
	 */
	public Collection<IMachineRoot> getIntermediateMachines() throws DataException {
		
		// check initialization
		if (patternRefinementMachine == null)
			throw new DataException("Pattern refinement machine not yet initialized");
		return new HashSet<IMachineRoot>(intermediatePatternMachines);
		
	}
	
	/**
	 * @param machine within the refinement chain of the pattern
	 * @return the abstraction of the given one.
	 * @throws DataException
	 */
	public IMachineRoot getAbstractMachineOf(IMachineRoot machine) throws DataException {
		
		// input validation
		if (machine == null || !machine.exists())
			throw new DataException("Input is null or does not exist.");
		// check initialization
		if (patternRefinementMachine == null)
			throw new DataException("Pattern refinement machine not yet initialized");
		// check input restrictions
		if (!intermediatePatternMachines.contains(machine) && ! patternRefinementMachine.equals(machine))
			throw new DataException("Machine has to be in the refinement chain of the pattern.");
		return abstraction.get(machine);
		
	}
	
	/**
	 * @param machine within the refinement chain of the pattern
	 * @return the refinement of the given machine.
	 * @throws DataException
	 */
	public IMachineRoot getRefinedMachineOf(IMachineRoot machine) throws DataException {
		
		// input validation
		if (machine == null || !machine.exists())
			throw new DataException("Input is null or does not exist.");
		// check initialization
		if (patternRefinementMachine == null)
			throw new DataException("Pattern refinement machine not yet initialized");
		// check input restrictions
		if (!intermediatePatternMachines.contains(machine) && ! patternAbstractMachine.equals(machine))
			throw new DataException("Machine has to be in the refinement chain of the pattern.");
		return refinement.get(machine);
		
	}
	
	/**
	 * @param event within the refinement chain of the pattern
	 * @return the abstractions of the given event.
	 * @throws DataException
	 */
	public Collection<IEvent> getAbstractEventsOf(IEvent event) throws DataException {
		
		// input validation
		if (event == null || !event.exists())
			throw new DataException("Input is null or does not exist.");
		// check initialization
		if (patternRefinementMachine == null)
			throw new DataException("Pattern refinement machine not yet initialized");
		// check input restrictions
		if (!eventAbstraction.containsKey(event))
			throw new DataException("Event has to be in the refinement chain of the pattern.");
		return new HashSet<IEvent>(eventAbstraction.get(event));
		
	}
	
	/**
	 * @param event within the refinement chain of the pattern
	 * @return the refinements of the given event.
	 * @throws DataException
	 */
	public Collection<IEvent> getRefinedEventsOf(IEvent event) throws DataException {
		
		// input validation
		if (event == null || !event.exists())
			throw new DataException("Input is null or does not exist.");
		// check initialization
		if (patternRefinementMachine == null)
			throw new DataException("Pattern refinement machine not yet initialized");
		// check input restrictions
		if (!eventRefinement.containsKey(event))
			throw new DataException("Event has to be in the refinement chain of the pattern.");
		return new HashSet<IEvent>(eventRefinement.get(event));
		
	}
	
	/**
	 * @param machine within the refinement chain of the pattern
	 * @return all new variables of the given intermediate machine.
	 * @throws DataException
	 */
	public Collection<IVariable> getNewVariablesOf(IMachineRoot machine) throws DataException {
		
		// input validation
		if (machine == null || !machine.exists())
			throw new DataException("Input is null or does not exist.");
		// check initialization
		if (patternRefinementMachine == null)
			throw new DataException("Pattern refinement machine not yet initialized");
		// check input restrictions
		if (!intermediatePatternMachines.contains(machine))
			throw new DataException("Machine has to be in the refinement chain of the pattern.");
		return new HashSet<IVariable>(intermediateNewVariables.get(machine));
		
	}
	
	/**
	 * @param machine within the refinement chain of the pattern
	 * @return all disappearing variables of the given intermediate machine.
	 * @throws DataException
	 */
	public Collection<IVariable> getDisappearingVariablesOf(IMachineRoot machine) throws DataException {
		
		// input validation
		if (machine == null || !machine.exists())
			throw new DataException("Input is null or does not exist.");
		// check initialization
		if (patternRefinementMachine == null)
			throw new DataException("Pattern refinement machine not yet initialized");
		// check input restrictions
		if (!intermediatePatternMachines.contains(machine) && !machine.equals(patternAbstractMachine))
			throw new DataException("Machine has to be in the refinement chain of the pattern.");
		return new HashSet<IVariable>(intermediateDisappearingVariables.get(machine));
		
	}
	
	/**
	 * @return all disappearing variables of all machines between pattern abstraction and pattern refinement.
	 * @throws DataException
	 */
	public Collection<IVariable> getDisappearingVariablesOfAllIntermediateMachines() throws DataException {
		
		// check initialization
		if (patternRefinementMachine == null)
			throw new DataException("Pattern refinement machine not yet initialized");
		// collect variables
		Collection<IVariable> result = new HashSet<IVariable>();
		for (IMachineRoot machine : intermediatePatternMachines)
			result.addAll(intermediateDisappearingVariables.get(machine));
		return result;
			
	}
	
	/**
	 * @return all witnesses containing parameters that need to be replaced.
	 * @throws DataException
	 */
	public Collection<IWitness> getRelevantWitnesses() throws DataException {
		
		// check initialization
		if (patternRefinementMachine == null)
			throw new DataException("Pattern refinement machine not yet initialized");
		return new HashSet<IWitness>(parameterReplacement.keySet());
	}
	
	/**
	 * @param event of the pattern refinement machine
	 * @return the map containing all refinement chains leading to the given event. 
	 * @throws DataException
	 */
	public HashMap<IEvent, IEvent> getChainFor(IEvent refinementEvent) throws DataException {
		
		// input validation
		if (refinementEvent == null || !refinementEvent.exists())
			throw new DataException("Input is null or does not exist.");
		// check initialization
		if (patternRefinementMachine == null)
			throw new DataException("Pattern refinement machine not yet initialized");
		// check input restrictions
		if (!refinedPatternRefinementEvents.contains(refinementEvent))
			throw new DataException("Event has to be a refinement of a matched event.");
		return new HashMap<IEvent, IEvent>(chainDown.get(refinementEvent));
	}
	
	/**
	 * @param witness within the refinement chain
	 * @return if the parameter in the given witness needs to be replaced
	 * @throws DataException
	 */
	public boolean isRelevantWitness(IWitness witness) throws DataException {
		
		// input validation
		if (witness == null || !witness.exists())
			throw new DataException("Input is null or does not exist.");
		// check initialization
		if (patternRefinementMachine == null)
			throw new DataException("Pattern refinement machine not yet initialized");
		return parameterReplacement.containsKey(witness);
	}
	
	/**
	 * @return the number of refinement steps
	 * <p>
	 * The refinement chain m0, m1, m2 would return 2.
	 * @throws DataException
	 */
	public int getNumberOfRefinements() throws DataException {
		
		// check initialization
		if (patternRefinementMachine == null)
			throw new DataException("Pattern refinement machine not yet initialized");
		return refinement.size();
		
	}
	
	/**
	 * @param machine within the refinement chain of the pattern
	 * @return all relevant invariants of the given machine. A Invariant is relevant if its predicate
	 * contains a disappearing or a new variable.
	 * @throws DataException
	 */
	public Collection<IInvariant> getRelevantInvariantsOf(IMachineRoot machine) throws DataException {
		
		// input validation
		if (machine == null || !machine.exists())
			throw new DataException("Input is null or does not exist.");
		// check initialization
		if (patternRefinementMachine == null)
			throw new DataException("Pattern refinement machine not yet initialized");
		// check input restrictions
		if (!intermediatePatternMachines.contains(machine) && !patternRefinementMachine.equals(machine))
			throw new DataException("Machine has to be in the refinement chain of the pattern.");
		return new HashSet<IInvariant>(relevantInvariants.get(machine));
		
	}
	
	public Collection<IVariable> getForwardDependentVariables(IVariable variable) throws DataException {
		
		// input validation
		if (variable == null || !variable.exists())
			throw new DataException("Input is null or does not exist.");
		// check initialization
		if (patternRefinementMachine == null)
			throw new DataException("Pattern refinement machine not yet initialized");
		// check input restrictions
		if (!forwardDependencies.containsKey(variable))
			throw new DataException("Variable has has no dependencies.");
		return new HashSet<IVariable>(forwardDependencies.get(variable));
		
	}
	
	public Collection<IVariable> getBackwardDependentVariables(IVariable variable) throws DataException {
		
		// input validation
		if (variable == null || !variable.exists())
			throw new DataException("Input is null or does not exist.");
		// check initialization
		if (patternRefinementMachine == null)
			throw new DataException("Pattern refinement machine not yet initialized");
		// check input restrictions
		if (!backwardDependencies.containsKey(variable))
			throw new DataException("Variable has has no dependencies.");
		return new HashSet<IVariable>(backwardDependencies.get(variable));
				
	}
	
	public IMachineRoot getMachineAfterDisappearingOf(String variable) throws DataException {
		
		// input validation
		if (variable == null)
			throw new DataException("Input is null");
		// check initialization
		if (patternRefinementMachine == null)
			throw new DataException("Pattern refinement machine not yet initialized");
		// check input restrictions
		if (!variableDisappearing.containsKey(variable))
			throw new DataException("Variable does not disappear.");
		
		IRodinElement parent = variableDisappearing.get(variable).getParent();
		if (parent == null || !parent.exists() || !(parent instanceof IMachineRoot))
			throw new DataException("Variable has corrupt parent.");
		return refinement.get((IMachineRoot)parent);

	}
	
	public boolean isNewerThan(IMachineRoot oldMachine, IMachineRoot newMachine) throws DataException {
		
		// input validation
		if (oldMachine == null || !oldMachine.exists() || newMachine == null || !newMachine.exists())
			throw new DataException("One of the input is null or does not exist.");
		// check initialization
		if (patternRefinementMachine == null)
			throw new DataException("Pattern refinement machine not yet initialized");
		return machineRank.get(newMachine)>machineRank.get(oldMachine);
	}
	
	public boolean isNewerThan(IVariable oldVariable, IVariable newVariable) throws DataException {
		
		// input validation
		if (oldVariable == null || !oldVariable.exists() || oldVariable == null || !oldVariable.exists())
			throw new DataException("One of the input is null or does not exist.");
		// check initialization
		if (patternRefinementMachine == null)
			throw new DataException("Pattern refinement machine not yet initialized");
		
		// get the old variable parent
		IRodinElement oldParent = oldVariable.getParent();
		
		// check that parent is a machine and declare it as such	
		IMachineRoot oldMachine;
		if (oldParent instanceof IMachineRoot)
			oldMachine = (IMachineRoot) oldParent;
		else
			throw new DataException("Old variable parent is not a machine.");
		
		// get the new variable parent
		IRodinElement newParent = newVariable.getParent();
		
		// check that parent is a machine and declare it as such	
		IMachineRoot newMachine;
		if (newParent instanceof IMachineRoot)
			newMachine = (IMachineRoot) newParent;
		else
			throw new DataException("New variable parent is not a machine.");
		
		return machineRank.get(newMachine)>machineRank.get(oldMachine);
	}

	public IMachineGenerator createNewMachineGenerator() {
		return new MachineGenerator(this);
	}
	
	
}