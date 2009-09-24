package ch.ethz.eventb.internal.pattern;

import static org.eventb.core.IConfigurationElement.DEFAULT_CONFIGURATION;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eventb.core.IAction;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
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
import org.eventb.core.IConvergenceElement.Convergence;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.LanguageVersion;
import org.eventb.core.ast.Predicate;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

public class MachineGenerator {

	private Data data;
	
	private IMachineRoot generatedMachine;
	
	private final FormulaFactory ff = FormulaFactory.getDefault();
	
	private Map<FreeIdentifier, Expression> patternMap;
	
	private Map<FreeIdentifier, Expression> patternRefinementMap;

	private Map<FreeIdentifier, Expression> problemMap;

	private Map<FreeIdentifier, Expression> parameterMap;
	
	private Map<FreeIdentifier, Expression> intermediateMap;
	
	private final String VARIABLE_PREFIX = "internal_var";
	private int variableNumber;
	
	private final String PARAMETER_PREFIX = "internal_prm";
	private int parameterNumber;
	
	private final String INVARIANT_PREFIX = "internal_inv";
	private final String INVARIANT_LABEL = "inv";
	private final String THEOREM_LABEL = "thm";
	private int invariantNumber;
	
	private final String EVENT_PREFIX = "internal_evt";
	private int eventNumber;
	
	private final String WITNESS_PREFIX = "internal_wit";
	private int witnessNumber;
	
	private final String GUARD_PREFIX = "grd";
	private final String GUARD_LABEL = "grd";
	private int guardNumber;
	
	private final String ACTION_PREFIX = "internal_act";
	private final String ACTION_LABEL = "act";
	private int actionNumber;
	
	private final String REFINEMENT_PREFIX = "internal_refinement";
	
	public MachineGenerator(Data data) {
		this.data = data;
	}
	
	public IMachineRoot generateMachine(String name, boolean generatePO, IProgressMonitor monitor) throws Exception {
		
		monitor.beginTask("Generate machine", 8);
		
		unsetExtended(new SubProgressMonitor(monitor,1));
		calculateMaps(new SubProgressMonitor(monitor,1));
		createMachine(name, generatePO, new SubProgressMonitor(monitor,1));
		createComment(new SubProgressMonitor(monitor,1));
		createVariables(new SubProgressMonitor(monitor,1));
		createInvariants(new SubProgressMonitor(monitor,1));
		createEvents(new SubProgressMonitor(monitor,1));
		monitor.subTask("save File");
		saveFile();
		monitor.worked(1);
		return generatedMachine;
		
	}
	
	private void createMachine(String name, boolean generatePO, IProgressMonitor monitor) throws Exception {
		
		monitor.beginTask(null, 1);
		monitor.subTask("create machine");
		// get problem project
		IEventBProject eventBProject = data.getProblemEventBProject();
		// generate a new machine file
		IRodinFile generatedFile = eventBProject.getMachineFile(name);
		generatedFile.create(false, null);
		// get the machine root of the file
		generatedMachine = (IMachineRoot) generatedFile.getRoot();
		// get the problem machine
		IMachineRoot problemMachine = data.getProblemMachine();
		// set if POs should be generated
		if (generatePO)
			generatedMachine.setConfiguration(DEFAULT_CONFIGURATION, null);
		else
			generatedMachine.setConfiguration("org.eventb.core.mchBase", null);
		// set the new machine to be the refinement of the problem machine
		IRefinesMachine refinesClause = generatedMachine.getRefinesClause("internal_refineClause");
		refinesClause.create(null, null);
		refinesClause.setAbstractMachineName(problemMachine.getComponentName(), null);
		// copy all context clauses
		for(ISeesContext sees : problemMachine.getSeesClauses())
			sees.copy(generatedMachine, null, null, true, null);
		monitor.worked(1);
		monitor.done();
	}
	
	private void createComment(IProgressMonitor monitor) throws RodinDBException, DataException {
		
		monitor.beginTask(null, 12);
		monitor.subTask("create machine");
		
		String comment = "Pattern applied: " + data.getPatternProject().getElementName() + "\n";
		comment = comment.concat("matched with model: "+ data.getPatternAbstractMachine().getElementName()+"\n");
		comment = comment.concat("incorporated model: "+ data.getPatternRefinementMachine().getElementName()+"\n");
		monitor.worked(1);
		
		Collection<ICarrierSet> renamedCarrierSets = data.getMatchedCarrierSets();
		if (renamedCarrierSets.size()>0)
			comment = comment.concat("\ncarrier set matchings:");
		for (ICarrierSet carrierSet : renamedCarrierSets)
			comment = comment.concat("\n   " + carrierSet.getIdentifierString() + " --> " + data.getMatchingOf(carrierSet));
		monitor.worked(1);
		
		Collection<IConstant> renamedConstants = data.getMatchedConstants();
		if (renamedConstants.size()>0)
			comment = comment.concat("\nconstant matchings:");
		for (IConstant constant : renamedConstants)
			comment = comment.concat("\n   " + constant.getIdentifierString() + " --> " + data.getMatchingOf(constant));		
		monitor.worked(1);
		
		comment = comment.concat("\nvariable matchings:");
		for (IVariable variable : data.getMatchedPatternVariables())
			comment = comment.concat("\n   " + variable.getIdentifierString() + " --> " + data.getMatchingOf(variable).getIdentifierString());
		monitor.worked(1);
		
		comment = comment.concat("\nevent matchings:");
		for (IEvent patternEvent : data.getMatchedPatternEvents())
			for (IEvent problemEvent : data.getMatchingsOf(patternEvent))
				comment = comment.concat("\n   " + patternEvent.getLabel() + " --> " + problemEvent.getLabel());
		monitor.worked(1);
		
		Collection<IEvent> mergedEvents = data.getMergedProblemEvents();
		if (mergedEvents.size()>0)
			comment = comment.concat("\nevent mergings:");
		for (IEvent problemEvent : mergedEvents) {
			comment = comment.concat("\n   " + problemEvent.getLabel() + " {");
			for (IEvent patternRefinementEvent : data.getMergingOf(problemEvent))
				comment = comment.concat("\n     " + patternRefinementEvent.getLabel());
			comment = comment.concat(" }");
		}
		monitor.worked(1);
		
		Collection<IVariable> renamedVariables = data.getRenamedVariables();
		if (renamedVariables.size()>0)
			comment = comment.concat("\nvariable renaming:");
		for (IVariable variable : renamedVariables)
			comment = comment.concat("\n   " + variable.getIdentifierString() + " --> " + data.getRenamingOf(variable));
		monitor.worked(1);
		
		Collection<Pair<IEvent,IEvent>> renamedEventPairs = data.getRenamedMatchedEvents();
		if (renamedEventPairs.size()>0)
			comment = comment.concat("\nmatched event renaming:");
		for (Pair<IEvent,IEvent> pair : renamedEventPairs) {
			IEvent patternRefinementEvent = pair.getFirst();
			IEvent problemEvent = pair.getSecond();
			comment = comment.concat("\n   " + patternRefinementEvent.getLabel() + 
					" (" + problemEvent.getLabel() + ") --> " + 
					data.getRenamingOfMatchedEvent(patternRefinementEvent, problemEvent));
		}
		monitor.worked(1);
		
		Collection<IEvent> renamedMergedEvents = data.getRenamedMergedEvents();
		if (renamedMergedEvents.size()>0)
			comment = comment.concat("\nmerged event renaming:");
		for (IEvent event : renamedMergedEvents)
			comment = comment.concat("\n   " + event.getLabel() + " --> " + data.getRenamingOfMergedEvent(event));
		monitor.worked(1);
		
		Collection<IEvent> renamedNewEvents = data.getRenamedNewEvents();
		if (renamedNewEvents.size()>0)
			comment = comment.concat("\nnew event renaming:");
		for (IEvent event : renamedNewEvents)
			comment = comment.concat("\n   " + event.getLabel() + " --> " + data.getRenamingOfNewEvent(event));
		monitor.worked(1);
		
		Collection<IVariable> replacedVariables = data.getReplacedVariables();
		if (replacedVariables.size()>0)		
			comment = comment.concat("\nvariable replacement:");
		for (IVariable variable : replacedVariables)
			comment = comment.concat("\n   " + variable.getIdentifierString() + " --> " + data.getForwardReplacementFor(variable));
		monitor.worked(1);
		
		generatedMachine.setComment(comment, null);
		monitor.worked(1);
		monitor.done();
	}
		
	
	
	private void createVariables(IProgressMonitor monitor) throws RodinDBException, DataException  {
		
		monitor.beginTask(null, 2);
		monitor.subTask("create variables");
				
		variableNumber = 1;
		// copy not-matched pattern variables
		copyNotMatchedPatternVariables(new SubProgressMonitor(monitor,1));
		// copy problem refinement variables
		copyPatternVariables(new SubProgressMonitor(monitor,1));
		
		monitor.done();
	}
	
	private int copyNotMatchedPatternVariables(IProgressMonitor monitor) throws RodinDBException, DataException {
				
		int i = 1;
		
		// get not-matched problem variables	
		Collection<IVariable> variables = data.getNotMatchedProblemVariables();
		// split task into sub-tasks
		monitor.beginTask(null, variables.size());
		for (IVariable var : variables) {
			IVariable variable = generatedMachine.getVariable(VARIABLE_PREFIX + variableNumber++);
			variable.create(null, null);
			variable.setIdentifierString(var.getIdentifierString(), null);
			variable.setComment("Original variable", null);
			monitor.worked(1);
		}
		monitor.done();
		return i;
	}
	
	private void copyPatternVariables(IProgressMonitor monitor) throws RodinDBException, DataException {
		Collection<IVariable> variables = data.getAllPatternRefinementVariables();
		monitor.beginTask(null, variables.size());
		for (IVariable var : variables) {
			IVariable variable = generatedMachine.getVariable(VARIABLE_PREFIX + variableNumber++);
			variable.create(null, new NullProgressMonitor());
			String renaming = data.getRenamingOf(var);
			if (renaming == null) {
				variable.setIdentifierString(var.getIdentifierString(), null);
				variable.setComment(
						"Variable of pattern (without renaming)", null);
			}
			// with renaming
			else {
				variable.setIdentifierString(renaming, null);
				variable.setComment("Variable of pattern (renaming)", null);
			}
			monitor.worked(1);
		}
		monitor.done();
	}
	
	private void createInvariants(IProgressMonitor monitor) throws RodinDBException, DataException {
		
		invariantNumber = 1;
		
		Collection<IVariable> newVariables = data.getNewPatternRefinementVariables();
		Collection<IVariable> disappearingVariables = data.getDisappearingPatternVariables();
		Collection<IVariable> remainingVariables = data.getRemainingPatternVariables();
		
		monitor.beginTask(null , newVariables.size() + disappearingVariables.size() + remainingVariables.size());
		monitor.subTask("create invariants");
		
		// create invariant for new variables
		for (IVariable newVariable : newVariables) {
			IInvariant newInvariant = generatedMachine.getInvariant(INVARIANT_PREFIX + invariantNumber);
			newInvariant.create(null, null);
			newInvariant.setLabel(INVARIANT_LABEL + invariantNumber++, null);
			newInvariant.setTheorem(false, null);
			String predicate = EventBUtils.getTypingTheorem(data.getPatternRefinementMachine(), newVariable.getIdentifierString());
			predicate = PatternUtils.substitute(predicate, patternRefinementMap, ff);
			newInvariant.setPredicateString(predicate, null);
			newInvariant.setComment("Typing information for new variable", null);
			monitor.worked(1);
		}
		
		// create invariant for disappearing variables
		for (IVariable disappearingVariable : disappearingVariables) {
			String problemIdentifier = data.getMatchingOf(disappearingVariable).getIdentifierString();
			String patternRenaming = disappearingVariable.getIdentifierString();
			patternRenaming = PatternUtils.substitute(patternRenaming, intermediateMap, ff);
			patternRenaming = PatternUtils.substitute(patternRenaming, patternRefinementMap, ff);
			if (!problemIdentifier.equals(patternRenaming)) {
				IInvariant newInvariant = generatedMachine.getInvariant(INVARIANT_PREFIX + invariantNumber);
				newInvariant.create(null, null);
				newInvariant.setLabel(INVARIANT_LABEL + invariantNumber++, null);
				newInvariant.setTheorem(false, null);
				String predicate = problemIdentifier + " = " + patternRenaming;
				newInvariant.setPredicateString(predicate, null);
				newInvariant.setComment("Linking for disappeared variable", null);
			}
			monitor.worked(1);
		}
		
		// create invariant for remaning variables
		for (IVariable remainingVariable : remainingVariables) {
			String problemIdentifier = data.getMatchingOf(remainingVariable).getIdentifierString();
			String patternRenaming = remainingVariable.getIdentifierString();
			patternRenaming = PatternUtils.substitute(patternRenaming, patternRefinementMap, ff);
			if (!problemIdentifier.equals(patternRenaming)) {
				IInvariant newInvariant = generatedMachine.getInvariant(INVARIANT_PREFIX + invariantNumber);
				newInvariant.create(null, null);
				newInvariant.setLabel(INVARIANT_LABEL + invariantNumber++, null);
				newInvariant.setTheorem(false, null);
				String predicate = problemIdentifier + " = " + patternRenaming;
				newInvariant.setPredicateString(predicate, null);
				newInvariant.setComment("Linking for disappeared variable", null);
			}
			monitor.worked(1);
		}
		
		monitor.done();
	
	}
	
	private void createEvents(IProgressMonitor monitor) throws Exception, DataException {
		
		monitor.beginTask(null, 5);
		monitor.subTask("create events");
		eventNumber = 1;
		
		monitor.subTask("Create init event");
		// Create init event
		createInitEvent();
		monitor.worked(1);
				
		monitor.subTask("Copy not-matched problem events");
		// Copy the old events from problem
		copyNotMatchedProblemEvents(new SubProgressMonitor(monitor,1));
		
		monitor.subTask("Copy new pattern refinement events");
		// Copy new events from patternRef
		copyNewPatternRefinementEvents(new SubProgressMonitor(monitor,1));
		
		monitor.subTask("Merge new pattern refinement events");
		// Merge new events from patternRef with not-matched events from problem
		mergeNewPatternRefinementEvents(new SubProgressMonitor(monitor,1));
		
		monitor.subTask("Merge refined pattern refinement events");
		// Merge refined events from patternRef
		mergeRefinedPatternRefinementEvents(new SubProgressMonitor(monitor,1));
	}
	
	private void createInitEvent() throws RodinDBException, DataException {
		
		
		IEvent problemInit = PatternUtils.getElementByLabel(IEvent.ELEMENT_TYPE, IEvent.INITIALISATION, data.getProblemMachine());
		IEvent patternRefinementInit = PatternUtils.getElementByLabel(IEvent.ELEMENT_TYPE, IEvent.INITIALISATION, data.getPatternRefinementMachine());
			
		//INITIALISATION
		if (problemInit != null && patternRefinementInit != null){
				
			// create a new event in the refinement
			IEvent evt = generatedMachine.getEvent(EVENT_PREFIX + eventNumber++);
			evt.create(null, null);
			// set the name
			evt.setLabel(IEvent.INITIALISATION, null);
			// set event to not-extended
			evt.setExtended(false, null);
			// set the convergence status
			evt.setConvergence(Convergence.ORDINARY, null);
			
			actionNumber = 1;
			// copy all not-matched actions from old init
			for (IAction action : data.getNotMatchedActionsOf(problemInit)) {
				IAction act = evt.getAction(ACTION_PREFIX + actionNumber);
				act.create(null, null);
				act.setLabel(ACTION_LABEL + actionNumber++, null);
				String assignment = action.getAssignmentString();
				assignment = PatternUtils.substitute(assignment, problemMap, ff);
				act.setAssignmentString(assignment, null);
				act.setComment("Extra action", null);
			}
			// copy actions from refinement init
			for (IAction action : data.getAllPatternRefinementActionsOf(patternRefinementInit)){
				IAction act = evt.getAction(ACTION_PREFIX + actionNumber);
				act.create(null, null);
				act.setLabel(ACTION_LABEL + actionNumber++, null);
				String assignment = action.getAssignmentString();
				assignment = PatternUtils.substitute(assignment, patternRefinementMap, ff);
				act.setAssignmentString(assignment, null);
				act.setComment("Copy from pattern refinement", null);
			}
		}
	}
	
	private void copyNotMatchedProblemEvents(IProgressMonitor monitor) throws RodinDBException, DataException {
		
		// Not-matched problem events
		Collection<IEvent> events = data.getNotMatchedProblemEvents();
		// Not the merged events
		events.removeAll(data.getMergedProblemEvents());
		
				
		monitor.beginTask(null, events.size());
		for (IEvent event : events) {
			// Not the INITIALISATION
			if (event.isInitialisation()){
				monitor.worked(1);
				continue;
			}
			// create a new event in the refinement
			IEvent evt = generatedMachine.getEvent(EVENT_PREFIX + eventNumber++);
			evt.create(null, null);
			// copy the name
			evt.setLabel(event.getLabel(), null);
			// set the comment
			evt.setComment("Original Event", null);
			// the new event refines the old one
			IRefinesEvent ref = evt.getRefinesClause(REFINEMENT_PREFIX);
			ref.create(null, null);
			ref.setAbstractEventLabel(evt.getLabel(), null);
			// set event to not-extended because there are variables that have to be replaced
			evt.setExtended(false, null);
			// set the convergence status to the old one
			evt.setConvergence(event.getConvergence(), null);
			// copy all parameters
			parameterNumber = 1;
			for (IParameter parameter : event.getParameters()){
				IParameter par = evt.getParameter(PARAMETER_PREFIX + parameterNumber++);
				par.create(null, null);
				par.setIdentifierString(parameter.getIdentifierString(), null);
			}
			// copy all guards
			guardNumber = 1;
			for (IGuard guard : event.getGuards()){
				IGuard grd = evt.getGuard(GUARD_PREFIX + guardNumber);
				grd.create(null, null);
				grd.setLabel(GUARD_LABEL + guardNumber++, null);
				grd.setTheorem(guard.isTheorem(), null);
				String predicate = guard.getPredicateString();
				predicate = PatternUtils.substitute(predicate, problemMap, ff);
				grd.setPredicateString(predicate, null);
			}
			// copy all actions
			actionNumber = 1;
			for (IAction action : event.getActions()){
				IAction act = evt.getAction(ACTION_PREFIX + actionNumber);
				act.create(null, null);
				act.setLabel(ACTION_LABEL + actionNumber++, null);
				String assignment = action.getAssignmentString();
				assignment = PatternUtils.substitute(assignment, problemMap, ff);
				act.setAssignmentString(assignment, null);
			}
			monitor.worked(1);
		}
		monitor.done();
	}

	private void copyNewPatternRefinementEvents(IProgressMonitor monitor) throws Exception, DataException {
		
		// Every new event
		Collection<IEvent> events = data.getNewPatternRefinementEvents();
		// Not the merged events
		events.removeAll(data.getMergedPatternRefinementEvents());
		
		
		monitor.beginTask(null, events.size());
		for (IEvent event : events) {
			// Not the INITIALISATION
			if (event.isInitialisation()){
				monitor.worked(1);
				continue;
			}
			// create a new event in the refinement
			IEvent evt = generatedMachine.getEvent(EVENT_PREFIX + eventNumber++);
			evt.create(null, null);
			// copy the name (rename if necessary)
			String rename = data.getRenamingOfNewEvent(event);
			if (rename == null)
				evt.setLabel(event.getLabel(), null);
			else
				evt.setLabel(rename, null);
			// set the comment
			evt.setComment("New pattern event", null);
			// set event to not-extended because its a new one
			evt.setExtended(false, null);
			// copy the convergence status
			evt.setConvergence(event.getConvergence(), null);
			// copy all parameters
			parameterNumber = 1;
			for (IParameter parameter : event.getParameters()){
				IParameter par = evt.getParameter(PARAMETER_PREFIX + parameterNumber++);
				par.create(null, null);
				par.setIdentifierString(parameter.getIdentifierString(), null);
			}
			// copy all witnesses (although shouldn't have any)
			 //TODO: extract the relationship of the parameters
//			j = 1;
//			for (IWitness witness : event.getWitnesses()){
//				IWitness wit = evt.getWitness("wit" + (j++));
//				wit.create(null, null);
//				wit.setLabel(witness.getLabel(),null);
//				String predicate = witness.getPredicateString();
//				predicate = PatternUtils.substitute(predicate, refMap, ff);
//				wit.setPredicateString(predicate, null);
//			}			
//			
			// copy all guards
			guardNumber = 1;
			for (IGuard guard : event.getGuards()){
				IGuard grd = evt.getGuard(GUARD_PREFIX + guardNumber);
				grd.create(null, null);
				grd.setLabel(GUARD_LABEL + guardNumber++, null);
				grd.setTheorem(guard.isTheorem(), null);
				String predicate = guard.getPredicateString();
				predicate = PatternUtils.substitute(predicate, patternRefinementMap, ff);
				grd.setPredicateString(predicate, null);
			}
			// copy all actions
			actionNumber = 1;
			for (IAction action : event.getActions()){
				IAction act = evt.getAction(ACTION_PREFIX + actionNumber);
				act.create(null, null);
				act.setLabel(ACTION_LABEL + actionNumber++, null);
				String assignment = action.getAssignmentString();
				assignment = PatternUtils.substitute(assignment, patternRefinementMap, ff);
				act.setAssignmentString(assignment, null);
			}
			monitor.worked(1);
		
		}
		monitor.done();
	}
	
	private void mergeNewPatternRefinementEvents(IProgressMonitor monitor) throws RodinDBException, DataException {
		
		Collection<IEvent> events = data.getMergedProblemEvents();
				
		monitor.beginTask(null, events.size());
		for (IEvent problemEvent : events) {
			Collection<IEvent> patternEvents = data.getMergingOf(problemEvent);
						
			// create a new event in the refinement
			IEvent evt = generatedMachine.getEvent(EVENT_PREFIX + eventNumber++);
			evt.create(null, null);
			// copy the name (rename if necessary)
			String rename = data.getRenamingOfMergedEvent(problemEvent);
			if (rename == null)
				evt.setLabel(problemEvent.getLabel(), null);
			else
				evt.setLabel(rename, null);
			// set the comment
			evt.setComment("Merged event", null);
			// the pattern event refines the problem event
			IRefinesEvent ref = evt.getRefinesClause(REFINEMENT_PREFIX);
			ref.create(null, null);
			ref.setAbstractEventLabel(problemEvent.getLabel(), null);
			// set event to not-extended because its a new one
			evt.setExtended(false, null);
			// copy the convergence status
			evt.setConvergence(problemEvent.getConvergence(), null);
			
			// copy parameters from problem event
			parameterNumber = 1;
			for (IParameter parameter : problemEvent.getParameters()) {
				IParameter prm = evt.getParameter(PARAMETER_PREFIX + parameterNumber++);
				String identifer = parameter.getIdentifierString();
				prm.create(null, null);
				prm.setIdentifierString(identifer, null);
				prm.setComment("problem parameter", null);
			}
			// copy all guards from problem event
			guardNumber = 1;
			for (IGuard guard : problemEvent.getGuards()) {
				IGuard grd = evt.getGuard(GUARD_PREFIX + guardNumber);
				grd.create(null, null);
				grd.setLabel(GUARD_LABEL + guardNumber++, null);
				grd.setTheorem(guard.isTheorem(), null);
				String predicate = guard.getPredicateString();
				predicate = PatternUtils.substitute(predicate, problemMap, ff);
				grd.setPredicateString(predicate, null);
				grd.setComment("problem guard", null);
			}
			// copy actions from problem event
			actionNumber = 1;
			for (IAction action : problemEvent.getActions()) {
				IAction act = evt.getAction(ACTION_PREFIX + actionNumber);
				act.create(null, null);
				act.setLabel(ACTION_LABEL + actionNumber++, null);
				String assignment = action.getAssignmentString();
				assignment = PatternUtils.substitute(assignment, problemMap, ff);
				act.setAssignmentString(assignment, null);
				act.setComment("problem action", null);
			}
			int p = 1;
			Map<FreeIdentifier, Expression> temporaryMap = new HashMap<FreeIdentifier, Expression>();
			
			for (IEvent patternEvent : patternEvents) {
				temporaryMap.clear();
				// copy parameters from pattern refinement
				for (IParameter parameter : patternEvent.getParameters()){
					IParameter prm = evt.getParameter(PARAMETER_PREFIX + parameterNumber++);
					String identifer = parameter.getIdentifierString();
					prm.create(null, null);
					prm.setIdentifierString("pat_" + p + "_" + identifer, null);
					temporaryMap.put(ff.makeFreeIdentifier(identifer, null), ff.makeFreeIdentifier("pat_" + p + "_" + identifer, null));
					prm.setComment("pattern parameter", null);
				}
				// copy guards from pattern refinement
				for (IGuard guard : patternEvent.getGuards()){
					IGuard grd = evt.getGuard(GUARD_PREFIX + guardNumber);
					grd.create(null, null);
					grd.setLabel(GUARD_LABEL + guardNumber++, null);
					grd.setTheorem(guard.isTheorem(), null);
					String predicate = guard.getPredicateString();
					predicate = PatternUtils.substitute(predicate, patternRefinementMap, ff);
					predicate = PatternUtils.substitute(predicate, temporaryMap, ff);
					grd.setPredicateString(predicate, null);
					grd.setComment("pattern guard", null);
				}
				// copy actions from pattern refinement
				for (IAction action : patternEvent.getActions()){
					IAction act = evt.getAction(ACTION_PREFIX + actionNumber);
					act.create(null, null);
					act.setLabel(ACTION_LABEL + actionNumber++, null);
					String assignment = action.getAssignmentString();
					assignment = PatternUtils.substitute(assignment, patternRefinementMap, ff);
					assignment = PatternUtils.substitute(assignment, temporaryMap, ff);
					act.setAssignmentString(assignment, null);
					act.setComment("pattern action", null);
				}
				p++;
			}
			monitor.worked(1);
		}
		monitor.done();
	}
	
	private void mergeRefinedPatternRefinementEvents(IProgressMonitor monitor) throws Exception, DataException {
		
		Collection<IEvent> events = data.getMatchedProblemEvents();
				
		monitor.beginTask(null, events.size());
		for (IEvent problemEvent : events) {
			// Not the INITIALISATION
			if (problemEvent.isInitialisation()){
				monitor.worked(1);
				continue;
			}
			Collection<IEvent> patternEvents = data.getMatchingsOf(problemEvent);
			if (patternEvents.size() != 1)
				throw new Exception("Problem event matched with more than one pattern event.");
			IEvent patternEvent = patternEvents.iterator().next();
			IEvent[] refEvents = PatternUtils.getRefinementsOfEvent(data.getPatternRefinementMachine(), patternEvent);
			SubProgressMonitor sub = new SubProgressMonitor(monitor, 1);
			sub.beginTask(null, refEvents.length);
			for (IEvent refEvent : refEvents) {
				// create a new event in the refinement
				IEvent evt = generatedMachine.getEvent(EVENT_PREFIX + eventNumber++);
				evt.create(null, null);
				// copy the name (rename if necessary)
				String rename = data.getRenamingOfMatchedEvent(refEvent, problemEvent);
				if (rename == null)
					evt.setLabel(problemEvent.getLabel(), null);
				else
					evt.setLabel(rename, null);
				// set the comment
				evt.setComment("Matched event", null);
				// the pattern event refines the problem event
				IRefinesEvent ref = evt.getRefinesClause(REFINEMENT_PREFIX);
				ref.create(null, null);
				ref.setAbstractEventLabel(problemEvent.getLabel(), null);
				// set event to not-extended because its a new one
				evt.setExtended(false, null);
				// copy the convergence status
				evt.setConvergence(refEvent.getConvergence(), null);
				// copy all extra parameters from problem event
//				Collection<IParameter> matchedParameters = new ArrayList<IParameter>();
//				for (IParameter prm : match.getProblemChildrenOfType(IParameter.ELEMENT_TYPE))
//					matchedParameters.add(PatternUtils.getElementByIdentifier(IParameter.ELEMENT_TYPE, prm.getIdentifierString(), problemEvent));
				
				parameterNumber = 1;
//				for (IParameter parameter : problemEvent.getParameters()) {
//					if (!matchedParameters.contains(parameter)) {
//						IParameter prm = evt.getParameter("internal_prm" + j);
//						prm.create(null, null);
//						prm.setIdentifierString(parameter.getIdentifierString(), null);
//						prm.setComment("Extra parameter", null);
//					}
//				}
									
				// copy parameters from refinement
				for (IParameter parameter : refEvent.getParameters()){
					IParameter prm = evt.getParameter(PARAMETER_PREFIX + parameterNumber++);
					prm.create(null, null);
					prm.setIdentifierString(parameter.getIdentifierString(), null);
					prm.setComment("Copy from pattern refinement", null);
				}
				// create parameter renaming map
				calculateParameterMap(refEvent, problemEvent);
				// create witnesses
				witnessNumber = 1;
				for (IParameter problemParameter : data.getMatchedParametersOf(problemEvent)){
					IWitness wit = evt.getWitness(WITNESS_PREFIX + witnessNumber++);
					wit.create(null, null);
					String label = problemParameter.getIdentifierString();
					wit.setLabel(label, null);
					// determine the corresponding parameter of the pattern event
					String replacement = PatternUtils.substitute(label, parameterMap, ff);
					replacement = PatternUtils.substitute(replacement, patternRefinementMap, ff);
					wit.setPredicateString(label + " = " + replacement, null);
				}
				// copy all extra guards from problem event
				guardNumber = 1;
				for (IGuard guard : data.getNotMatchedGuardsOf(problemEvent)) {
					IGuard grd = evt.getGuard(GUARD_PREFIX + guardNumber);
					grd.create(null, null);
					grd.setLabel(GUARD_LABEL + guardNumber++, null);
					String predicate = guard.getPredicateString();
					predicate = PatternUtils.substitute(predicate, problemMap, ff);
					predicate = PatternUtils.substitute(predicate, parameterMap, ff);
					grd.setPredicateString(predicate, null);
					grd.setComment("Extra guard", null);
				}
				// copy guards from refinement
				for (IGuard guard : refEvent.getGuards()){
					IGuard grd = evt.getGuard(GUARD_PREFIX + guardNumber);
					grd.create(null, null);
					grd.setLabel(GUARD_LABEL + guardNumber++, null);
					String predicate = guard.getPredicateString();
					predicate = PatternUtils.substitute(predicate, patternRefinementMap, ff);
					grd.setPredicateString(predicate, null);
					grd.setComment("Copy from pattern refinement", null);
				}
				// copy all extra action from problem event
				actionNumber = 1;
				for (IAction action : data.getNotMatchedActionsOf(problemEvent)) {
					IAction act = evt.getAction(ACTION_PREFIX + actionNumber);
					act.create(null, null);
					act.setLabel(ACTION_LABEL + actionNumber++, null);
					String assignment = action.getAssignmentString();
					assignment = PatternUtils.substitute(assignment, problemMap, ff);
					assignment = PatternUtils.substitute(assignment, parameterMap, ff);
					act.setAssignmentString(assignment, null);
					act.setComment("Extra action", null);
				}
				// copy actions from refinement
				for (IAction action : refEvent.getActions()){
					IAction act = evt.getAction(ACTION_PREFIX + actionNumber);
					act.create(null, null);
					act.setLabel(ACTION_LABEL + actionNumber++, null);
					String assignment = action.getAssignmentString();
					assignment = PatternUtils.substitute(assignment, patternRefinementMap, ff);
					act.setAssignmentString(assignment, null);
					act.setComment("Copy from pattern refinement", null);
				}
				sub.worked(1);
			}
			sub.done();
		}
		monitor.done();
	}
	
	
	
	
	private void saveFile() throws RodinDBException {
		// save the problem machine
		generatedMachine.getRodinFile().save(new NullProgressMonitor(), false, true);
	}
	
	private void calculateMaps (IProgressMonitor monitor) throws RodinDBException, DataException {
		
		monitor.beginTask(null, 6);
		monitor.subTask("calculate Maps");
				
		patternRefinementMap = new HashMap<FreeIdentifier, Expression>();
		// renaming of the pattern refinement variables
		for (IVariable variable : data.getRenamedVariables()) {
			FreeIdentifier oldVar = ff.makeFreeIdentifier(variable.getIdentifierString(), null);
			FreeIdentifier newVar = ff.makeFreeIdentifier(data.getRenamingOf(variable), null);
			
			patternRefinementMap.put(oldVar, newVar);
			patternRefinementMap.put(oldVar.withPrime(ff), newVar.withPrime(ff));
		}
		monitor.worked(1);
		
		// renaming of the pattern refinement carrier sets
		for (ICarrierSet carrierSet : data.getMatchedCarrierSets()) {
			patternRefinementMap.put(ff.makeFreeIdentifier(carrierSet.getIdentifierString(), null),
					ff.makeFreeIdentifier(data.getMatchingOf(carrierSet), null));
		}
		monitor.worked(1);
		
		// renaming of the pattern refinement constants
		for (IConstant constant : data.getMatchedConstants()) {
			patternRefinementMap.put(ff.makeFreeIdentifier(constant.getIdentifierString(), null),
					ff.makeFreeIdentifier(data.getMatchingOf(constant), null));
		}
		monitor.worked(1);
		
		patternMap = new HashMap<FreeIdentifier, Expression>();
		// renaming of the pattern specification variables that disappear
		for (IVariable variable : data.getDisappearingPatternVariables()) {
			IVariable problemVariable = data.getMatchingOf(variable);
			if (problemVariable != null)
				patternMap.put(ff.makeFreeIdentifier(variable.getIdentifierString(), null),
						ff.makeFreeIdentifier(problemVariable.getIdentifierString(), null));
		}
		monitor.worked(1);
		
		
		intermediateMap = new HashMap<FreeIdentifier, Expression>();
		HashMap<FreeIdentifier, Expression> tempMap = new HashMap<FreeIdentifier, Expression>();
		for (IVariable patternVariable : data.getDisappearingPatternVariables())
			intermediateMap.put(ff.makeFreeIdentifier(patternVariable.getIdentifierString(), null),
					ff.makeFreeIdentifier(patternVariable.getIdentifierString(),null));
		
		IMachineRoot refinement = data.getRefinedMachineOf(data.getPatternAbstractMachine());
		while (!refinement.equals(data.getPatternRefinementMachine())) {
			tempMap.clear();
			for (IVariable variable : data.getDisappearingVariablesOf(data.getAbstractMachineOf(refinement)))
					tempMap.put(ff.makeFreeIdentifier(variable.getIdentifierString(), null),
					data.getForwardReplacementFor(variable));
			for (FreeIdentifier var : intermediateMap.keySet())
				intermediateMap.put(var, intermediateMap.get(var).substituteFreeIdents(tempMap, ff));
			refinement = data.getRefinedMachineOf(refinement);
		}
		// once again for the pattern refinement machine
		tempMap.clear();
		for (IVariable variable : data.getDisappearingVariablesOf(data.getAbstractMachineOf(refinement)))
			tempMap.put(ff.makeFreeIdentifier(variable.getIdentifierString(), null),
				data.getForwardReplacementFor(variable));
		for (FreeIdentifier var : intermediateMap.keySet())
			intermediateMap.put(var, intermediateMap.get(var).substituteFreeIdents(tempMap, ff));
		//monitor.worked(1);
		
		
		problemMap = new HashMap<FreeIdentifier, Expression>();
		Collection<IVariable> problemVariables = data.getMatchedProblemVariables();
		for (IVariable problemVariable : problemVariables) {
			FreeIdentifier patternVariable = ff.makeFreeIdentifier(problemVariable.getIdentifierString(), null);
			Expression replacement = ff.makeFreeIdentifier(data.getMatchingOf(problemVariable).getIdentifierString(), null);
			replacement = replacement.substituteFreeIdents(intermediateMap, ff);
			replacement = replacement.substituteFreeIdents(patternRefinementMap, ff);
			problemMap.put(patternVariable, replacement);
		}
		monitor.worked(1);
		monitor.done();
	}
	
	private void calculateParameterMap(IEvent patternRefinementEvent, IEvent problemEvent) throws RodinDBException, DataException {
		
		// setup fresh map
		parameterMap = new HashMap<FreeIdentifier, Expression>();
		// make sure the given event is a matched problem event
		assert data.getMatchedProblemEvents().contains(problemEvent);
		assert data.getRefinedPatternRefinementEvents().contains(patternRefinementEvent);
		// get matching of problem event
		IEvent patternEvent = data.getMatchingsOf(problemEvent).iterator().next();
		HashMap<FreeIdentifier, Expression> tempMap = new HashMap<FreeIdentifier, Expression>();
		// get all the matched parameters of the problem event
		for (IParameter problemParameter : data.getMatchedParametersOf(problemEvent)) {
			// determine the corresponding parameter of the pattern event
			IParameter patternParameter = data.getMatchingOf(problemParameter);
			parameterMap.put(ff.makeFreeIdentifier(problemParameter.getIdentifierString(), null),
					ff.makeFreeIdentifier(patternParameter.getIdentifierString(),null));
		}
		HashMap<IEvent, IEvent> chain = data.getChainFor(patternRefinementEvent);
		
		IEvent current = patternEvent;
		while (chain.containsKey(current)) {
			current = chain.get(current);
			tempMap.clear();
			for (IWitness witness : current.getWitnesses())
				if (data.isRelevantWitness(witness))
					tempMap.put(ff.makeFreeIdentifier(witness.getLabel(), null),
					data.getReplacementFor(witness));
			for (FreeIdentifier par : parameterMap.keySet())
				parameterMap.put(par, parameterMap.get(par).substituteFreeIdents(tempMap, ff));
		}
		
	}
	
	private void unsetExtended(IProgressMonitor monitor) throws DataException {
		
		monitor.beginTask(null, 3);
		monitor.subTask("Set events to not extended");
		try {
			for (IEvent evt : data.getAllPatternEvents()) {
				if (evt.isExtended())
					PatternUtils.unsetExtended(evt);
			}
			monitor.worked(1);
			for (IEvent evt : data.getAllPatternRefinementEvents()) {
				if (evt.isExtended())
					PatternUtils.unsetExtended(evt);
			}
			monitor.worked(1);
			for (IEvent evt : data.getAllProblemEvents()) {
				if (evt.isExtended())
					PatternUtils.unsetExtended(evt);
			}
			monitor.worked(1);
		}
		catch (RodinDBException e) {
			// error occurred
			throw new DataException("Error occurred while setting events to not extended");
		}
		finally {
			monitor.done();
		}
		
	}
}
