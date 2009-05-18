package ch.ethz.eventb.internal.pattern.wizards;

import static org.eventb.core.IConfigurationElement.DEFAULT_CONFIGURATION;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eventb.core.IAction;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConfigurationElement;
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
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

import ch.ethz.eventb.internal.pattern.PatternUtils;




/**
 * This is a sample new wizard. Its role is to create a new file 
 * resource in the provided container. If the container resource
 * (a folder or a project) is selected in the workspace 
 * when the wizard is opened, it will accept it as the target
 * container. The wizard creates one file with the extension
 * "mpe". If a sample multi-page editor (also available
 * as a template) is registered for the same extension, it will
 * be able to open it.
 */

public class EventBPatternWizard extends Wizard implements INewWizard {
	private MatchingWizardPage matchingPage;
	
	private SyntaxCheckingWizardPage syntaxCheckingPage;
	
	private RenamingWizardPage renamingPage;
	
	private IncorporatingWizardPage incorporatingPage;
	
	private ISelection selection;
	
	private Collection<IRodinFile> openFiles;
	
	private final FormulaFactory ff = FormulaFactory.getDefault();
	
	private Map<FreeIdentifier, Expression> specMap;
	
	private Map<FreeIdentifier, Expression> refMap;
	
	private Map<FreeIdentifier, Expression> probMap;
	
	private Map<FreeIdentifier, Expression> paraMap;
	
	
	/**
	 * Constructor for EventBPatternWizard.
	 */
	public EventBPatternWizard() {
		super();
		setNeedsProgressMonitor(true);
		openFiles = new ArrayList<IRodinFile>();
		specMap = new HashMap<FreeIdentifier, Expression>();
		refMap = new HashMap<FreeIdentifier, Expression>();
		probMap = new HashMap<FreeIdentifier, Expression>();
		paraMap = new HashMap<FreeIdentifier, Expression>();
	}
	
	/**
	 * Adding the page to the wizard.
	 */

	public void addPages() {
		matchingPage = new MatchingWizardPage(selection, openFiles);
		addPage(matchingPage);
		syntaxCheckingPage = new SyntaxCheckingWizardPage(matchingPage);
		addPage(syntaxCheckingPage);
		renamingPage = new RenamingWizardPage(matchingPage);
		addPage(renamingPage);
		incorporatingPage = new IncorporatingWizardPage(matchingPage, renamingPage);
		addPage(incorporatingPage);
	}

	 
	/**
	 * This method is called when 'Finish' button is pressed in
	 * the wizard. We will create an operation and run it
	 * using wizard as execution context.
	 */
	public boolean performFinish() {
		final MatchingMachine matching = matchingPage.getMatching();
//		final IMachineRoot problemMachine = matchingPage.getProblemMachine();
//		final IMachineRoot patternSpec = matchingPage.getPatternMachine();
		final Matching<IVariable>[] matchingVariables = matchingPage.getVariableGroup().getMatchings();
		final ComplexMatching<IEvent>[] matchingEvents = matchingPage.getEventGroup().getMatchings();
//		final Collection<ComplexMatching<IEvent>> eventCollection = matchingPage.getEventGroup().getMatchingCollection();
		
		
		final IMachineRoot patternRef = renamingPage.getPatternRefinmentMachine();
		final Renaming<IVariable> renamingVariables = renamingPage.getRenamingVariables();
		final Renaming<IEvent> renamingEvents = renamingPage.getRenamingEvents();
		final Renaming<IVariable> extractionVariables = incorporatingPage.getVariableExtraction();
		final Renaming<IWitness> extractionWitnesses = incorporatingPage.getWitnessExtraction();
		final Renaming<ICarrierSet> renamingCarrierSets = matchingPage.getCarrierSetRenaming();
		final Renaming<IConstant> renamingConstants = matchingPage.getConstantRenaming();
	

		final String machineRefName = incorporatingPage.getMachineName();
		
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException {
				try {
					doFinish(matching,
							patternRef,
							machineRefName,
							renamingVariables,
							renamingEvents,
							renamingCarrierSets,
							renamingConstants,
							extractionVariables,
							extractionWitnesses,
							matchingVariables,
							matchingEvents,
							monitor);

				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.subTask("Cleanup");
					
					for (IRodinFile file : openFiles) {
						try {
							if (file != null && !file.isConsistent())
								file.revert();
						}
						catch (RodinDBException e) {}
					}
					openFiles.clear();
					monitor.worked(1);
					monitor.done();	


				}
			}
		};
		try {
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException.getMessage());
			return false;
		}
		return true;
	}
	
	
	
	@Override
	public boolean performCancel() {
		for (IRodinFile file : openFiles) {
			try {
				if (file != null && !file.isConsistent())
					file.revert();
			}
			catch (RodinDBException e) {}
		}
		openFiles.clear();
		return super.performCancel();
	}

	protected void doFinish(MatchingMachine matching,
			IMachineRoot patternRef,
			String machineRefName,
			Renaming<IVariable> renamingVariables,
			Renaming<IEvent> renamingEvents, 
			Renaming<ICarrierSet> renamingCarrierSets,
			Renaming<IConstant> renamingConstants,
			Renaming<IVariable> extractionVariables,
			Renaming<IWitness> extractionWitnesses,						
			Matching<IVariable>[] matchingVariables,
			ComplexMatching<IEvent>[] matchingEvents,
			IProgressMonitor monitor) throws CoreException {
		
		
		
		monitor.beginTask("Generate machine", 8);
		SubProgressMonitor subprom = new SubProgressMonitor(monitor,1);
		
		
		// create the refinement machine.
		monitor.subTask("Creating " + machineRefName);
		subprom.beginTask("", 2);
		final IMachineRoot refMachine = createNewRefinmentMachine(matching
				.getProblemElement(), machineRefName);
		subprom.worked(1);
		
		// create comment
		monitor.subTask("Generate comment");
		String comment = "Pattern applied: " + matching.getPatternElement().getEventBProject().getRodinProject().getElementName() + "\n";
		comment = comment.concat("matched with model: "+matching.getPatternElement().getElementName()+"\n");
		comment = comment.concat("incorporated model: "+patternRef.getElementName()+"\n");
		if (renamingCarrierSets.size()>0)
			comment = comment.concat("carrier set matchings:\n");
		for (String rename : renamingCarrierSets.getSourceList())
			comment = comment.concat("   " + rename + " --> " + renamingCarrierSets.getRenamingOfElement(rename) + "\n");
		if (renamingConstants.size()>0)
			comment = comment.concat("constant matchings:\n");
		for (String rename : renamingConstants.getSourceList())
			comment = comment.concat("   " + rename + " --> " + renamingConstants.getRenamingOfElement(rename) + "\n");		
		comment = comment.concat("variable matchings:\n");
		for (Matching<IVariable> match : matchingVariables)
			comment = comment.concat("   "+match + "\n");
		comment = comment.concat("event matchings:\n");
		for (Matching<IEvent> match : matchingEvents)
			comment = comment.concat("   "+match + "\n");
		comment = comment.concat("variable renaming:\n");
		for (String rename : renamingVariables.getSourceList())
			comment = comment.concat("   " + rename + " --> " + renamingVariables.getRenamingOfElement(rename) + "\n");
		comment = comment.concat("event renaming:\n");
		List<String> source = renamingEvents.getSourceList();
		List<String> problem = renamingEvents.getParentList();
		List<String> renaming = renamingEvents.getRenameList();
		for (int i=0; i< renamingEvents.size(); i++)
			comment = comment.concat("   " + source.get(i)
					+ " ("+problem.get(i)+") --> " + 
					renaming.get(i) + "\n");
		comment = comment.concat("variable replacement:\n");
		for (String rename : extractionVariables.getSourceList())
			comment = comment.concat("   " + rename + " --> " + extractionVariables.getRenamingOfElement(rename) + "\n");
		refMachine.setComment(comment, null);
		subprom.worked(1);
		subprom.done();
				
		subprom = new SubProgressMonitor(monitor,1);
		monitor.subTask("Prepare machines");
		subprom.beginTask(null, 9);
		
		subprom.subTask("Collect not-matched pattern variables");
		final IVariable[] notMatchedProblemVariables;
		Collection<IVariable> vars = new ArrayList<IVariable>();
		for (IVariable var : matching.getProblemElement().getVariables())
			if (!PatternUtils.isInMatchings(null, var, matchingVariables))
				vars.add(var);
		notMatchedProblemVariables = vars.toArray(new IVariable[vars.size()]);
		subprom.worked(1);
		
		IMachineRoot patternSpec = matching.getPatternElement();
		IMachineRoot problemMachine = matching.getProblemElement();
		
		subprom.subTask("Unextend events of pattern refinement");
		// set all events of the pattern refinement to not-extended
		if(!openFiles.contains(patternRef.getRodinFile())){
			openFiles.add(patternRef.getRodinFile());
			for (IEvent evt : patternRef.getEvents()) {
				if (evt.isExtended())
					PatternUtils.unsetExtended(evt);
			}
		}
		subprom.worked(1);
		
		subprom.subTask("Unextend events of pattern specification");
		// set all events of the pattern specification to not-extended
		if(!openFiles.contains(patternSpec.getRodinFile())){
			openFiles.add(patternSpec.getRodinFile());
			for (IEvent evt : patternSpec.getEvents()) {
				if (evt.isExtended())
					PatternUtils.unsetExtended(evt);
			}
		}
		subprom.worked(1);
		
		subprom.subTask("Unextend events of problem machine");
		// set all events of the problem machine to not-extended
		if(!openFiles.contains(problemMachine.getRodinFile())){
			openFiles.add(problemMachine.getRodinFile());
			for (IEvent evt : problemMachine.getEvents()) {
				if (evt.isExtended())
					PatternUtils.unsetExtended(evt);
			}
		}
		subprom.worked(1);
		
		subprom.subTask("Create renaming map of pattern refinement");
		// do the renaming in the pattern refinement and save the machine
		// renaming of the pattern refinement variables
		for (String variable : renamingVariables.getSourceList()) {
			FreeIdentifier oldVar = ff.makeFreeIdentifier(variable, null);
			FreeIdentifier newVar = ff.makeFreeIdentifier(renamingVariables.getRenamingOfElement(variable), null);
			refMap.put(oldVar, newVar);
			refMap.put(oldVar.withPrime(ff), newVar.withPrime(ff));
		}
		
		// renaming of the pattern refinement carrier sets
		for (String carrierSet : renamingCarrierSets.getSourceList()) {
			refMap.put(ff.makeFreeIdentifier(carrierSet, null), ff.makeFreeIdentifier(renamingCarrierSets.getRenamingOfElement(carrierSet), null));
		}
		
		// renaming of the pattern refinement constants
		for (String constant : renamingConstants.getSourceList()) {
			refMap.put(ff.makeFreeIdentifier(constant, null), ff.makeFreeIdentifier(renamingConstants.getRenamingOfElement(constant), null));
		}
		subprom.worked(1);
		
		subprom.subTask("Create renaming map of pattern specification");			
		// renaming of the pattern specification variables that disappear
		for (Matching<IVariable> match : matchingVariables) {
			String patternVariable = match.getPatternID();
			boolean disappeared = true;
			for (IVariable var : patternRef.getVariables())
				if (var.getIdentifierString().equals(patternVariable)){
					disappeared = false;
					break;
				}
			if (disappeared){
				String problemVariable = match.getProblemID();
				specMap.put(ff.makeFreeIdentifier(patternVariable, null), ff.makeFreeIdentifier(problemVariable, null));
			}
		}
		subprom.worked(1);
				
		subprom.subTask("Rename extracted variables");
		for (String var : extractionVariables.getSourceList())
			extractionVariables.setRenamingOfElement(var, PatternUtils.substitute(extractionVariables.getRenamingOfElement(var), refMap, ff));
		subprom.worked(1);
		
		subprom.subTask("Rename extracted witnesses");
		List<String> sources = extractionWitnesses.getSourceList();
		List<String> parents = extractionWitnesses.getParentList();
		for (int i=0 ; i<extractionWitnesses.size() ; i++)
			extractionWitnesses.setRenamingOfElement(sources.get(i), parents.get(i), PatternUtils.substitute(extractionWitnesses.getRenamingOfElement(sources.get(i), parents.get(i)), refMap, ff));
		subprom.worked(1);

		subprom.subTask("Create renaming map of problem machine");	
		// renaming of the problem variables
		for (Matching<IVariable> match : matchingVariables) {
			String patternVariable = match.getPatternID();
			String problemVariable = match.getProblemID();
			String newName = extractionVariables.getRenamingOfElement(patternVariable);
			if (newName.equals(""))
				newName = renamingVariables.getRenamingOfElement(patternVariable);
			if (newName.equals(""))
				newName = patternVariable;
			FreeIdentifier oldVar = ff.makeFreeIdentifier(problemVariable, null);
			FreeIdentifier newVar = ff.makeFreeIdentifier(newName, null);
			probMap.put(oldVar, newVar);
			probMap.put(oldVar.withPrime(ff), newVar.withPrime(ff));
			
		}
		subprom.worked(1);
		

		
		subprom.done();
		
	
		monitor.subTask("Creating variables");
		createVariables(matching, patternRef, refMachine, notMatchedProblemVariables, renamingVariables, new SubProgressMonitor(monitor, 1));
				
		monitor.subTask("Creating invariants");
		createInvariants(matching, patternRef, refMachine, renamingVariables, renamingCarrierSets, renamingConstants, new SubProgressMonitor(monitor,1));
		
		monitor.subTask("Creating events");
		createEvents(matching, patternRef, refMachine, renamingVariables, renamingEvents, renamingCarrierSets, renamingConstants, extractionVariables, extractionWitnesses, new SubProgressMonitor(monitor,1));
		
		monitor.subTask("Saving " + machineRefName);
		// Save the resulting file.
		try {
			refMachine.getRodinFile().save(new NullProgressMonitor(), false, true);
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		monitor.worked(1);
		
		
		
		monitor.subTask("Opening file for editing...");
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				PatternUtils.openWithDefaultEditor(refMachine);
			}
		});
		monitor.worked(1);
	}

	private IMachineRoot createNewRefinmentMachine(IMachineRoot problemMachine,
			String name) throws RodinDBException {
				
		IEventBProject eventBProject = problemMachine.getEventBProject();

		IRodinFile machineFile = eventBProject.getMachineFile(name);
		machineFile.create(false, null);
		final IInternalElement rodinRoot = machineFile.getRoot();
		if (incorporatingPage.generatePO())
			((IConfigurationElement) rodinRoot).setConfiguration(
				DEFAULT_CONFIGURATION, null);
		else
			((IConfigurationElement) rodinRoot).setConfiguration(
					"org.eventb.core.mchBase", null);
		IMachineRoot machineRoot = (IMachineRoot) machineFile.getRoot();
		IRefinesMachine refinesClause = machineRoot
				.getRefinesClause("internal_refineClause");
		refinesClause.create(null, null);
		refinesClause.setAbstractMachineName(problemMachine.getComponentName(),
				null);
		for(ISeesContext sees : problemMachine.getSeesClauses())
			sees.copy(machineRoot, null, null, true, null);
		machineFile.save(null, true);
		return machineRoot;
	}

	private void createVariables(
			MatchingMachine matching,
			IMachineRoot patternRef,
			IMachineRoot refMachine,
			IVariable[] notMatchedProblemVariables,
			Renaming<IVariable> renamingVariables,
			IProgressMonitor monitor) {
		
		monitor.beginTask(null, 2);
		int i = copyOriginalVariables(
				notMatchedProblemVariables,
				refMachine,
				new SubProgressMonitor(monitor,1));
		
		copyPatternVariables(
				patternRef,
				renamingVariables, 
				refMachine,
				new SubProgressMonitor(monitor,1),
				i);

	}

	private int copyOriginalVariables(
			IVariable[] notMatchedProblemVariables,
			IMachineRoot refMachine,
			IProgressMonitor monitor) {
				
		int i = 1;
		try {
			monitor.beginTask(null, notMatchedProblemVariables.length);
			for (IVariable var : notMatchedProblemVariables) {
				IVariable variable = refMachine
						.getVariable("internal_variable" + (i++));
				variable.create(null, null);
				variable.setIdentifierString(var.getIdentifierString(), null);
				variable.setComment("Original variable", null);
				monitor.worked(1);
			}
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			monitor.done();
		}
		return i;
	}
	
	private void copyPatternVariables(
			IMachineRoot patternRef,
			Renaming<IVariable> renamingVariables, 
			IMachineRoot refMachine,
			IProgressMonitor monitor,
			int i) {
		// Copy the pattern refinement variables with renaming
		IVariable[] refVariables;		
		try {
			refVariables = patternRef.getVariables();
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		monitor.beginTask(null, refVariables.length);
		List<String> sourceList = renamingVariables.getSourceList();
		List<String> renameList = renamingVariables.getRenameList();
		for (IVariable var : refVariables) {
			int indexOf;
			try {
				indexOf = sourceList.indexOf(var.getIdentifierString());
			} catch (RodinDBException e1) {
				indexOf = -1;
			}
			// no renaming
			if (indexOf == -1) {
				try {
					IVariable variable = refMachine
							.getVariable("internal_variable" + (i++));
					variable.create(null, new NullProgressMonitor());
					variable.setIdentifierString(var.getIdentifierString(), null);
					variable.setComment(
							"Variable of pattern (without renaming)", null);
				} catch (RodinDBException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// with renaming
			else {
				try {
					IVariable variable = refMachine
							.getVariable("internal_variable" + (i++));
					variable.create(null, new NullProgressMonitor());
					variable.setIdentifierString(renameList.get(indexOf), null);
					variable.setComment("Variable of pattern (renaming)", null);
				} catch (RodinDBException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}
			monitor.worked(1);
		}
		monitor.done();
	}

	
	
	private void createInvariants(
			MatchingMachine matching,
			IMachineRoot patternRef, 
			IMachineRoot refMachine,
			Renaming<IVariable> renamingVariables,
			Renaming<ICarrierSet> renamingCarrierSets,
			Renaming<IConstant> renamingConstants,
			IProgressMonitor monitor) {
				
//		// create array of variable matchings concerning 
//		Matching<IVariable>[] variableMatchings = matching.getChildrenOfType(IVariable.ELEMENT_TYPE);
//		Collection<Matching<IVariable>> modifiedMatchings = new ArrayList<Matching<IVariable>>();
//		for (Matching<IVariable> match : variableMatchings)
//			try {
//				if (!PatternUtils.isInArray(match.getPatternID(), patternRef.getVariables()))
//					modifiedMatchings.add(match);
//			} catch (RodinDBException e1) {
//			}
//		
		
		
		int i = 1;
		String prefix = "inv"; // TODO Make change-able
		IInvariant[] invariants;
		
		// copy abstract invariants
//		try {
//			invariants = matching.getPatternElement().getInvariants();
//		} catch (RodinDBException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return;
//		}
//		for (IInvariant invariant : invariants) {
//			IInvariant newInvariant = refMachine.getInvariant("internal_invariant" + i);
//			try {
//				newInvariant.create(null, monitor);
//				newInvariant.setLabel(prefix + (i++), monitor);
//				newInvariant.setPredicateString(renameMatching(invariant.getPredicateString(), matchingVariables), monitor);
//				newInvariant.setComment("Copy from pattern specification", monitor);
//			} catch (RodinDBException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		
		// copy concrete invariants
		try {
			invariants = patternRef.getInvariants();
			monitor.beginTask(null , invariants.length);
			for (IInvariant invariant : invariants) {
				IInvariant newInvariant = refMachine.getInvariant("internal_invariant" + i);
				newInvariant.create(null, null);
				newInvariant.setLabel(prefix + (i++), null);
				newInvariant.setTheorem(invariant.isTheorem(), null);
				String predicate = invariant.getPredicateString();
				predicate = PatternUtils.substitute(predicate, specMap, ff);
				predicate = PatternUtils.substitute(predicate, refMap, ff);
				newInvariant.setPredicateString(predicate, null);
				newInvariant.setComment("Copy from pattern refinement", null);
				monitor.worked(1);
			}
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		finally {
			monitor.done();
		}
			
		
	}
	
	
	private void createEvents(MatchingMachine matching,
			IMachineRoot patternRef, IMachineRoot refMachine,
			Renaming<IVariable> renamingVariables,
			Renaming<IEvent> renamingEvents, 
			Renaming<ICarrierSet> renamingCarrierSets,
			Renaming<IConstant> renamingConstants,
			Renaming<IVariable> extractionVariables,
			Renaming<IWitness> extractionWitnesses,
			IProgressMonitor monitor)
			throws RodinDBException {
		
		monitor.beginTask(null, 4);
		
		monitor.subTask("Create init event");
		// Create init event
		int i = createInitEvent(matching, patternRef, refMachine, renamingVariables, renamingCarrierSets, renamingConstants, extractionVariables);
		monitor.worked(1);
		
		monitor.subTask("Copy old problem events");
		// Copy the old events from problem
		i = copyOldEvents(matching, refMachine, extractionVariables, renamingVariables, new SubProgressMonitor(monitor,1), i);
		
		monitor.subTask("Copy new  pattern events");
		// Copy new events from patternRef
		i = copyNewEvents(patternRef, refMachine, renamingVariables, renamingEvents, renamingCarrierSets, renamingConstants, new SubProgressMonitor(monitor,1), i);
		
		monitor.subTask("Merge matched events");
		// Merge refined events from patternRef
		mergeRefinedEvents(matching, patternRef, refMachine, renamingVariables, renamingEvents, renamingCarrierSets, renamingConstants,
				extractionVariables, extractionWitnesses, new SubProgressMonitor(monitor,1),i);

	}
	
	private int createInitEvent(
			MatchingMachine matching,
			IMachineRoot patternRef,
			IMachineRoot refMachine,
			Renaming<IVariable> renamingVariables,
			Renaming<ICarrierSet> renamingCarrierSets,
			Renaming<IConstant> renamingConstants,
			Renaming<IVariable> extraction)
			throws RodinDBException {
		
		IEvent oldInit = PatternUtils.getElementByLabel(IEvent.ELEMENT_TYPE, IEvent.INITIALISATION, matching.getProblemElement());
		IEvent refInit = PatternUtils.getElementByLabel(IEvent.ELEMENT_TYPE, IEvent.INITIALISATION, patternRef);
			
		IComplexMatching<IEvent> initMatching = PatternUtils.getInitMatching(matching);
				
		//INITIALISATION
		if (oldInit != null && refInit != null && initMatching != null){
				
			// create a new event in the refinement
			IEvent evt = refMachine.getEvent("internal_evt1");
			evt.create(null, null);
			// copy the name (rename if necessary)
			evt.setLabel(IEvent.INITIALISATION, null);
			// set event to not-extended
			evt.setExtended(false, null);
			// set the convergence status
			evt.setConvergence(Convergence.ORDINARY, null);
			// copy all not-matched actions from old refinement
			Collection<IAction> matchedActions = new ArrayList<IAction>();
			for (IAction act : initMatching.getProblemChildrenOfType(IAction.ELEMENT_TYPE)) 
				matchedActions.add(PatternUtils.getElementByLabel(IAction.ELEMENT_TYPE, act.getLabel(), oldInit));
			int j = 1; 
		
			for (IAction action : oldInit.getActions()) {
				
				if (!matchedActions.contains(action)) {
					IAction act = evt.getAction("internal_act" + j);
					act.create(null, null);
					act.setLabel("act" + (j++), null);
					String assignment = action.getAssignmentString();
					assignment = PatternUtils.substitute(assignment, probMap, ff);
					act.setAssignmentString(assignment, null);
					act.setComment("Extra action", null);
				}
			}
			// copy actions from refinement
			for (IAction action : refInit.getActions()){
				IAction act = evt.getAction("internal_act" + j);
				act.create(null, null);
				act.setLabel("act" + (j++), null);
				String assignment = action.getAssignmentString();
				assignment = PatternUtils.substitute(assignment, refMap, ff);
				act.setAssignmentString(assignment, null);
				act.setComment("Copy from pattern refinement", null);
			}
		}
		return 2;
	}
	
	

	private int copyOldEvents(MatchingMachine matching,
			IMachineRoot refMachine, 
			Renaming<IVariable> extraction,
			Renaming<IVariable> renamingVariables,
			IProgressMonitor monitor,
			int i)
			throws RodinDBException {
		
		IMachineRoot problemMachine = matching.getProblemElement();
		IEvent[] events = problemMachine.getEvents();
		ComplexMatching<IEvent>[] matchedEvents = matching.getChildrenOfTypeEvent();
		monitor.beginTask(null, events.length);
		for (IEvent event : events) {
			// Not the INITIALISATION
			if (event.isInitialisation()){
				monitor.worked(1);
				continue;
			}
			// Every not-matched event
			if (!PatternUtils.isInMatchings(null, event, matchedEvents)) {
				// create a new event in the refinement
				IEvent evt = refMachine.getEvent("internal_evt" + (i++));
				evt.create(null, null);
				// copy the name
				evt.setLabel(event.getLabel(), null);
				// set the comment
				evt.setComment("Original Event", null);
				// the new event refines the old one
				IRefinesEvent ref = evt.getRefinesClause("internal_element1");
				ref.create(null, null);
				ref.setAbstractEventLabel(evt.getLabel(), null);
				// set event to not-extended because there are variables that have to be replaced
				evt.setExtended(false, null);
				// set the convergence status to the old one
				evt.setConvergence(event.getConvergence(), null);
				// copy all parameters
				int j = 1;
				for (IParameter parameter : event.getParameters()){
					IParameter par = evt.getParameter("internal_var" + (j++));
					par.create(null, null);
					par.setIdentifierString(parameter.getIdentifierString(), null);
				}
				// copy all guards
				j = 1;
				for (IGuard guard : event.getGuards()){
					IGuard grd = evt.getGuard("internal_grd" + j);
					grd.create(null, null);
					grd.setLabel("grd" + (j++), null);
					String predicate = guard.getPredicateString();
					predicate = PatternUtils.substitute(predicate, probMap, ff);
					grd.setPredicateString(predicate, null);
				}
				// copy all actions
				j = 1;
				for (IAction action : event.getActions()){
					IAction act = evt.getAction("internal_act" + j);
					act.create(null, null);
					act.setLabel("act" + (j++), null);
					String assignment = action.getAssignmentString();
					assignment = PatternUtils.substitute(assignment, probMap, ff);
					act.setAssignmentString(assignment, null);
				}
				monitor.worked(1);
			}
			monitor.worked(1);
		}
		monitor.done();
		return i;
	}

	private int copyNewEvents(IMachineRoot patternRef, IMachineRoot refMachine,
			Renaming<IVariable> renamingVariables,
			Renaming<IEvent> renamingEvents,
			Renaming<ICarrierSet> renamingCarrierSets,
			Renaming<IConstant> renamingConstants,
			IProgressMonitor monitor,
			int i)
			throws RodinDBException {
		
		IEvent[] events = patternRef.getEvents();
		monitor.beginTask(null, events.length);
		for (IEvent event : events) {
			// Not the INITIALISATION
			if (event.isInitialisation()){
				monitor.worked(1);
				continue;
			}
			// Every new event
			IRefinesEvent[] refinesClauses = event.getRefinesClauses();
			if (refinesClauses.length == 0) {
				// create a new event in the refinement
				IEvent evt = refMachine.getEvent("internal_evt" + (i++));
				evt.create(null, null);
				// copy the name (rename if necessary)
				evt.setLabel(PatternUtils.renameLabel(event.getLabel(), renamingEvents), null);
				// set the comment
				evt.setComment("New pattern event", null);
				// set event to not-extended because its a new one
				evt.setExtended(false, null);
				// copy the convergence status
				evt.setConvergence(event.getConvergence(), null);
				// copy all parameters
				int j = 1;
				for (IParameter parameter : event.getParameters()){
					IParameter par = evt.getParameter("internal_prm" + (j++));
					par.create(null, null);
					par.setIdentifierString(parameter.getIdentifierString(), null);
				}
				// copy all witnesses (although shouldn't have any)
				j = 1;
				for (IWitness witness : event.getWitnesses()){
					IWitness wit = evt.getWitness("wit" + (j++));
					wit.create(null, null);
					wit.setLabel(witness.getLabel(),null);
					String predicate = witness.getPredicateString();
					predicate = PatternUtils.substitute(predicate, refMap, ff);
					wit.setPredicateString(predicate, null);
				}			
				
				// copy all guards
				j = 1;
				for (IGuard guard : event.getGuards()){
					IGuard grd = evt.getGuard("internal_grd" + j);
					grd.create(null, null);
					grd.setLabel("grd" + (j++), null);
					String predicate = guard.getPredicateString();
					predicate = PatternUtils.substitute(predicate, refMap, ff);
					grd.setPredicateString(predicate, null);
				}
				// copy all actions
				j = 1;
				for (IAction action : event.getActions()){
					IAction act = evt.getAction("internal_act" + j);
					act.create(null, null);
					act.setLabel("act" + (j++), null);
					String assignment = action.getAssignmentString();
					assignment = PatternUtils.substitute(assignment, refMap, ff);
					act.setAssignmentString(assignment, null);
				}
				monitor.worked(1);
			}
			monitor.worked(1);
		}
		monitor.done();
		return i;
	}
		
	private void mergeRefinedEvents(
			MatchingMachine matching,
			IMachineRoot patternRef,
			IMachineRoot refMachine,
			Renaming<IVariable> renamingVariables,
			Renaming<IEvent> renamingEvents, 
			Renaming<ICarrierSet> renamingCarrierSets,
			Renaming<IConstant> renamingConstants,
			Renaming<IVariable> extractionVariables,
			Renaming<IWitness> extractionWitnesses,
			IProgressMonitor monitor,
			int i)
			throws RodinDBException {
		
		monitor.beginTask(null, matching.getChildrenOfTypeEvent().length);
		for (ComplexMatching<IEvent> match : matching.getChildrenOfTypeEvent()) {
			IEvent problemEvent = match.getProblemElement();
			IEvent patternEvent = match.getPatternElement();
			// Not the INITIALISATION
			if (patternEvent.isInitialisation()){
				monitor.worked(1);
				continue;
			}
			IEvent[] refEvents = PatternUtils.getRefinementEvents(patternEvent, patternRef);
			SubProgressMonitor sub = new SubProgressMonitor(monitor, 1);
			sub.beginTask(null, refEvents.length);
			for (IEvent refEvent : refEvents) {
				// create a new event in the refinement
				IEvent evt = refMachine.getEvent("internal_evt" + (i++));
				evt.create(null, null);
				// copy the name (rename if necessary)
				evt.setLabel(renamingEvents.getRenamingOfElement(refEvent.getLabel(), problemEvent.getLabel()), null);
				// set the comment
				evt.setComment("Merged event", null);
				// the pattern event refines the problem event
				IRefinesEvent ref = evt.getRefinesClause("internal_element1");
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
				
				int j = 1;
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
					IParameter prm = evt.getParameter("internal_prm" + j);
					prm.create(null, null);
					prm.setIdentifierString(parameter.getIdentifierString(), null);
					prm.setComment("Copy from pattern refinement", null);
				}
				// create parameter renaming map
				paraMap.clear();
				for (Matching<IParameter> matchedParameter : match.getChildrenOfType(IParameter.ELEMENT_TYPE)) {
					String oldPar = matchedParameter.getProblemID();
					String newPar = extractionWitnesses.getRenamingOfElement(matchedParameter.getPatternID(), match.getPatternID());
					if (newPar.equals(""))
						newPar = matchedParameter.getPatternID();
					paraMap.put(ff.makeFreeIdentifier(oldPar, null), ff.makeFreeIdentifier(newPar, null));
				}
				
				j = 1;
				for (IParameter parameter : patternEvent.getParameters()){
					IWitness witness = null;
					IWitness wit = evt.getWitness("internal_wit" + (j++));
					wit.create(null, null);
					for (IWitness refWit : refEvent.getWitnesses()) {
						if(refWit.getLabel().equals(parameter.getIdentifierString())) {
							witness = refWit;
							break;
						}
					}
					Matching<IParameter>[] matchedParameters = match.getChildrenOfType(IParameter.ELEMENT_TYPE);
					Matching<IParameter> parameterMatching = PatternUtils.getMatching(parameter, null, matchedParameters);
					if (parameterMatching != null) {
						String label = parameterMatching.getProblemID();
						wit.setLabel(label, null);
						String predicate;
						if (witness != null)
							predicate = label + " = " + extractionWitnesses.getRenamingOfElement(witness);
						else
							predicate = label + " = " + parameter.getIdentifierString();
						wit.setPredicateString(predicate, null);
					}
						
				}
				// copy all extra guards from problem event
				Collection<IGuard> matchedGuards = new ArrayList<IGuard>();
				for (IGuard grd : match.getProblemChildrenOfType(IGuard.ELEMENT_TYPE))
					matchedGuards.add(PatternUtils.getElementByLabel(IGuard.ELEMENT_TYPE, grd.getLabel(), problemEvent));
				
				j = 1;
				for (IGuard guard : problemEvent.getGuards()) {
					if (!matchedGuards.contains(guard)) {
						IGuard grd = evt.getGuard("internal_grd" + j);
						grd.create(null, null);
						grd.setLabel("grd" + (j++), null);
						String predicate = guard.getPredicateString();
						predicate = PatternUtils.substitute(predicate, probMap, ff);
						predicate = PatternUtils.substitute(predicate, paraMap, ff);
						grd.setPredicateString(predicate, null);
						grd.setComment("Extra guard", null);
					}
				}
				// copy guards from refinement
				for (IGuard guard : refEvent.getGuards()){
					IGuard grd = evt.getGuard("internal_grd" + j);
					grd.create(null, null);
					grd.setLabel("grd" + (j++), null);
					String predicate = guard.getPredicateString();
					predicate = PatternUtils.substitute(predicate, refMap, ff);
					grd.setPredicateString(predicate, null);
					grd.setComment("Copy from pattern refinement", null);
				}
				// copy all extra action from problem event
				Collection<IAction> matchedActions = new ArrayList<IAction>();
				for (IAction act : match.getProblemChildrenOfType(IAction.ELEMENT_TYPE))
					matchedActions.add(PatternUtils.getElementByLabel(IAction.ELEMENT_TYPE, act.getLabel(), problemEvent));
				
				j = 1;
				for (IAction action : problemEvent.getActions()) {
					if (!matchedActions.contains(action)) {
						IAction act = evt.getAction("internal_act" + j);
						act.create(null, null);
						act.setLabel("act" + (j++), null);
						String assignment = action.getAssignmentString();
						assignment = PatternUtils.substitute(assignment, probMap, ff);
						assignment = PatternUtils.substitute(assignment, paraMap, ff);
						act.setAssignmentString(assignment, null);
						act.setComment("Extra action", null);
					}
				}
				// copy actions from refinement
				for (IAction action : refEvent.getActions()){
					IAction act = evt.getAction("internal_act" + j);
					act.create(null, null);
					act.setLabel("act" + (j++), null);
					String assignment = action.getAssignmentString();
					assignment = PatternUtils.substitute(assignment, refMap, ff);
					act.setAssignmentString(assignment, null);
					act.setComment("Copy from pattern refinement", null);
				}
				sub.worked(1);
			}
			sub.done();
		}
		monitor.done();
	}
		
	/**
	 * We will accept the selection in the workbench to see if
	 * we can initialize from it.
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}
	
	
}