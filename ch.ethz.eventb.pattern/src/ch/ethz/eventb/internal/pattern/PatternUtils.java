/*******************************************************************************
 * Copyright (c) 2009 ETH Zurich
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package ch.ethz.eventb.internal.pattern;

import static org.eventb.internal.ui.EventBUtils.getFreeChildName;
import static org.eventb.internal.ui.EventBUtils.getImplicitChildren;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eventb.core.IAction;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEvent;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IGuard;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.IInvariant;
import org.eventb.core.ILabeledElement;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IRefinesEvent;
import org.eventb.core.IVariable;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.BecomesMemberOf;
import org.eventb.core.ast.BecomesSuchThat;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.LanguageVersion;
import org.eventb.core.ast.Predicate;
import org.eventb.ui.EventBUIPlugin;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

import ch.ethz.eventb.internal.pattern.wizards.ComplexMatching;
import ch.ethz.eventb.internal.pattern.wizards.IComplexMatching;
import ch.ethz.eventb.internal.pattern.wizards.Matching;
import ch.ethz.eventb.internal.pattern.wizards.MatchingMachine;
import ch.ethz.eventb.internal.pattern.wizards.Renaming;


/**
 * @author htson
 *         <p>
 *         Utility class for Event-B Pattern plug-in, including debugging.
 *         </p>
 */
/**
 * @author fuersta
 *
 */
public class PatternUtils {
	
	/**
	 * The debug flag. This is set by the option when the plug-in is started.
	 * Client should not try to reset this flag.
	 */
	public static boolean DEBUG = false;

	/**
	 * Debug prefix for debugging message.
	 */
	public static final String DEBUG_PREFIX = "*** Event-B Pattern ***";

	/**
	 * Utility method to print debugging message (including the debug prefix
	 * {@link #DEBUG_PREFIX}) to the console.
	 * 
	 * @param message
	 *            the debug message.
	 */
	public static void debug(String message) {
		System.out.println(DEBUG_PREFIX + " " + message);
	}

	/**
	 * Returns the Rodin projects in the workspace, or an empty array if there
	 * are none.
	 * 
	 * @return the Rodin projects in this workspace, or an empty array if there
	 *         are none
	 * @exception RodinDBException
	 *                if this request fails.
	 */
	public static IRodinProject[] getRodinProjects() throws RodinDBException {
		return RodinCore.getRodinDB().getRodinProjects();
	}

	/**
	 * Returns the Rodin project with the given name. This is a handle-only
	 * method. The project may or may not exist.
	 * 
	 * @param name
	 *            the name of the Rodin project
	 * @return the Rodin project with the given name
	 */
	public static IRodinProject getRodinProject(String name) {
		return RodinCore.getRodinDB().getRodinProject(name);
	}

	public static IEvent[] getRefinementEvents(IEvent event,
			IMachineRoot refMachine) throws RodinDBException {
		Collection<IEvent> result = new ArrayList<IEvent>();
		for (IEvent refEvent : refMachine.getEvents()) {
			IRefinesEvent[] refines = refEvent.getRefinesClauses();
			for (IRefinesEvent ref : refines) {
				if (ref.getAbstractEventLabel().equals(event.getLabel()))
					result.add(refEvent);
			}
		}
		return result.toArray(new IEvent[result.size()]);
	}
	
	
	
	public static <T extends IInternalElement> T getElementByLabel(IInternalElementType<T> childType, String label, IInternalElement parent) throws RodinDBException {
		for (T element : parent.getChildrenOfType(childType))
				if (element instanceof ILabeledElement && ((ILabeledElement)element).getLabel().equals(label))
					return element;
		return null;
	}

	public static <T extends IInternalElement> T getElementByElementName(IInternalElementType<T> childType, String elementName, IInternalElement parent) throws RodinDBException {
		for (T element : parent.getChildrenOfType(childType))
			if (element.getElementName().equals(elementName))
				return element;
		return null;
	}
	
	public static <T extends IInternalElement> T getElementByIdentifier(IInternalElementType<T> childType, String elementName, IInternalElement parent) throws RodinDBException {
		for (T element : parent.getChildrenOfType(childType))
			if (element instanceof IIdentifierElement && ((IIdentifierElement)element).getIdentifierString().equals(elementName))
				return element;
		return null;
	}
	
	public static <T extends IEventBRoot> T getRootByName(IInternalElementType<T> childType, String elementName, IRodinProject project) throws RodinDBException {
		for (T element : project.getRootElementsOfType(childType))
			if (element.getElementName().equals(elementName))
				return element;
		
		return null;
	}
	
	
	public static IComplexMatching<IEvent> getInitMatching(
			MatchingMachine matching) throws RodinDBException {
		for (IComplexMatching<IEvent> match : matching.getChildrenOfTypeEvent())
			if (match.getPatternElement().getLabel().equals(IEvent.INITIALISATION))
				return match;
		return null;
	}

	public static <T extends ILabeledElement> boolean isInMatchings(T pattern,
			T problem, Matching<T>[] matchings) throws RodinDBException {

		// matching is invalid
		if (matchings == null)
			return false;
		else {
			if (pattern == null) {
				if (problem == null) {
					// pattern and problem are null
					return false;
				} else {
					// problem is not null
					for (Matching<T> matching : matchings)
						if (matching.getProblemID().equals(problem.getLabel()))
							return true;
				}
			} else {
				if (problem == null) {
					// pattern is not null
					for (Matching<T> matching : matchings)
						if (matching.getPatternID().equals(pattern.getLabel()))
							return true;
				} else {
					// both are not null
					for (Matching<T> matching : matchings)
						if (matching.getPatternID().equals(pattern.getLabel())
								&& matching.getProblemID().equals(problem.getLabel()))
							return true;
				}
			}
		}
		return false;
	}
	
	public static <T extends IIdentifierElement> boolean isInMatchings(T pattern,
			T problem, Matching<T>[] matchings) throws RodinDBException {

		// matching is invalid
		if (matchings == null)
			return false;
		else {
			if (pattern == null) {
				if (problem == null) {
					// pattern and problem are null
					return false;
				} else {
					// problem is not null
					for (Matching<T> matching : matchings)
						if (matching.getProblemID().equals(problem.getIdentifierString()))
							return true;
				}
			} else {
				if (problem == null) {
					// pattern is not null
					for (Matching<T> matching : matchings)
						if (matching.getPatternID().equals(pattern.getIdentifierString()))
							return true;
				} else {
					// both are not null
					for (Matching<T> matching : matchings)
						if (matching.getPatternID().equals(pattern.getIdentifierString())
								&& matching.getProblemID().equals(problem.getIdentifierString()))
							return true;
				}
			}
		}
		return false;
	}
	
	public static boolean isInArray (String element, IVariable[] array) {
		
		for (IVariable variable : array)
			try {
				if (variable.getIdentifierString().equals(element))
					return true;
			} catch (RodinDBException e) {
			}
		return false;
	}
	
	
	public static <T extends IInternalElement> Matching<T> getMatching(T pattern,
			T problem, Matching<T>[] matchings) {

		// matching is invalid
		if (matchings == null)
			return null;
		else {
			if (pattern == null) {
				if (problem == null) {
					// pattern and problem are null
					return null;
				} else {
					// problem is not null
					for (Matching<T> matching : matchings)
						if (matching.getProblemElement().equals(problem))
							return matching;
				}
			} else {
				if (problem == null) {
					// pattern is not null
					for (Matching<T> matching : matchings)
						if (matching.getPatternElement().equals(pattern))
							return matching;
				} else {
					// both are not null
					for (Matching<T> matching : matchings)
						if (matching.getPatternElement().equals(pattern)
								&& matching.getProblemElement().equals(problem))
							return matching;
				}
			}
		}
		return null;
	}
	
	public static <T extends IInternalElement> Matching<T> getMatching(String pattern,
			String problem, Matching<T>[] matchings) {

		// matching is invalid
		if (matchings == null)
			return null;
		else {
			if (pattern == null) {
				if (problem == null) {
					// pattern and problem are null
					return null;
				} else {
					// problem is not null
					for (Matching<T> matching : matchings)
						if (matching.getProblemID().equals(problem))
							return matching;
				}
			} else {
				if (problem == null) {
					// pattern is not null
					for (Matching<T> matching : matchings)
						if (matching.getPatternID().equals(pattern))
							return matching;
				} else {
					// both are not null
					for (Matching<T> matching : matchings)
						if (matching.getPatternID().equals(pattern)
								&& matching.getProblemID().equals(problem))
							return matching;
				}
			}
		}
		return null;
	}
	
	public static ComplexMatching<IEvent> getMatching(IEvent pattern,
			IEvent problem, ComplexMatching<IEvent>[] matchings) throws RodinDBException {

		// matching is invalid
		if (matchings == null)
			return null;
		else {
			if (pattern == null) {
				if (problem == null) {
					// pattern and problem are null
					return null;
				} else {
					// problem is not null
					for (ComplexMatching<IEvent> matching : matchings)
						if (matching.getProblemID().equals(problem.getLabel()))
							return matching;
				}
			} else {
				if (problem == null) {
					// pattern is not null
					for (ComplexMatching<IEvent> matching : matchings)
						if (matching.getPatternID().equals(pattern.getLabel()))
							return matching;
				} else {
					// both are not null
					for (ComplexMatching<IEvent> matching : matchings)
						if (matching.getPatternID().equals(pattern.getLabel())
								&& matching.getProblemID().equals(problem.getLabel()))
							return matching;
				}
			}
		}
		return null;
	}
	

	/**
	 * Open a machine root with the default editor.
	 * <p>
	 * 
	 * @param machineRoot
	 *            a machine root
	 */
	public static void openWithDefaultEditor(IMachineRoot machineRoot) {
		IRodinFile rodinFile = machineRoot.getRodinFile();
		if (rodinFile == null)
			return;
		try {
			IEditorDescriptor desc = PlatformUI.getWorkbench()
					.getEditorRegistry().getDefaultEditor(
							rodinFile.getCorrespondingResource().getName());

			EventBPattern.getActivePage().openEditor(
					new FileEditorInput(rodinFile.getResource()), desc.getId());
		} catch (PartInitException e) {
			String errorMsg = "Error open Editor";
			MessageDialog.openError(null, null, errorMsg);
			EventBPattern.getDefault().getLog().log(
					new Status(IStatus.ERROR, EventBUIPlugin.PLUGIN_ID,
							errorMsg, e));
		}
	}

	public static String getDisplayText(Object element) {

		// If the element is a Guard element then return the predicate of the
		// element.
		if (element instanceof IGuard) {
			try {
				return ((IGuard) element).getPredicateString();
			} catch (RodinDBException e) {
				return "";
			}
		}
		
		// If the element is an Action element then return the assignment of the
		// element.
		if (element instanceof IAction) {
			try {
				return ((IAction) element).getAssignmentString();
			} catch (RodinDBException e) {
				return "";
			}
		}
		
		// If the element is an Invariant element then return the predicate of the
		// element.
		if (element instanceof IInvariant) {
			try {
				return ((IInvariant) element).getPredicateString();
			} catch (RodinDBException e) {
				return "";
			}
		}

		// If the element has label then return the label.
		if (element instanceof ILabeledElement) {
			try {
				return ((ILabeledElement) element).getLabel();
			} catch (RodinDBException e) {
				return "";
			}
		}

		// If the element has identifier string then return it.
		if (element instanceof IIdentifierElement) {
			try {
				return ((IIdentifierElement) element).getIdentifierString();
			} catch (RodinDBException e) {
				return "";
			}
		}

		// If the element is a Rodin element then return the name of the
		// element.
		if (element instanceof IRodinElement) {
			return ((IRodinElement) element).getElementName();
		}

		// Otherwise return the string corresponding to the element by
		// toString() method.
		return element.toString();
	}
	
	public static String[] toStringArray (ArrayList<IRodinElement> array) {
		ArrayList<String> result = new ArrayList<String>();
		for (IRodinElement element : array)
			result.add(PatternUtils.getDisplayText(element));
		return result.toArray(new String[result.size()]);
	}

	public static IContextRoot getRodinContext(IRodinProject project,
			String context) {
		try {
			for (IContextRoot contextRoot : project.getRootElementsOfType(IContextRoot.ELEMENT_TYPE))
				if (contextRoot.getElementName().equals(context))
					return contextRoot;
		} catch (RodinDBException e) {
		}
		return null;
	}
	

	public static <T extends ILabeledElement> String renameLabel(String label, Renaming<T> renaming) throws RodinDBException {
		
		List<String> src = renaming.getSourceList();
		List<String> ren = renaming.getRenameList();
		for (int i = 0;  i < renaming.size() ; i++)
			if (src.get(i).equals(label))
				return ren.get(i);
		return label;
	}
	
	public static <T extends IIdentifierElement> String renameIdentifier(T element, Matching<T>[] matchings, boolean fromPatterntoProblem) throws RodinDBException {
		if (fromPatterntoProblem) {
			for (Matching<T> matching : matchings)
				if (matching.getPatternElement() == element)
					return matching.getProblemElement().getIdentifierString();
		}
		else {
			for (Matching<T> matching : matchings)
				if (matching.getProblemElement() == element)
					return matching.getPatternElement().getIdentifierString();
		}
		return element.getIdentifierString();
	}

	
	

	
	public static String substitute(String string, String oldName, String newName, FormulaFactory ff) throws RodinDBException{
		Map<FreeIdentifier, Expression> map = new HashMap<FreeIdentifier, Expression>();
		map.put(ff.makeFreeIdentifier(oldName, null), ff.makeFreeIdentifier(newName, null));
		return substitute(string, map, ff);
	}
	
	
	public static String substitute(String string, Map<FreeIdentifier, Expression> map, FormulaFactory ff) throws RodinDBException{
		
		Expression expression;
		Predicate predicate;
		Assignment assignment;
		
		// try to parse the string as an expression 
		expression = ff.parseExpression(string, LanguageVersion.LATEST, null).getParsedExpression();
		// if the string is not an expression try to parse it as a predicate
		if (expression == null) {
			predicate = ff.parsePredicate(string, LanguageVersion.LATEST, null).getParsedPredicate();
			// if the string is not a predicate try to parse it as an assignment
			if (predicate == null) {
				assignment = ff.parseAssignment(string, LanguageVersion.LATEST, null).getParsedAssignment();
				// string is not valid
				if (assignment == null)
					return string;
				
				Collection<FreeIdentifier> leftCollection = new ArrayList<FreeIdentifier>();
				for(FreeIdentifier free : assignment.getAssignedIdentifiers())
					leftCollection.add(ff.makeFreeIdentifier(free.substituteFreeIdents(map, ff).toString(), null));
				FreeIdentifier[] left = leftCollection.toArray(new FreeIdentifier[leftCollection.size()]);
				if (assignment instanceof BecomesEqualTo) {
					Collection<Expression> right = new ArrayList<Expression>();
					Expression[] assRight = ((BecomesEqualTo)assignment).getExpressions();
					for (Expression expr : assRight)
						right.add(expr.substituteFreeIdents(map, ff));
					assignment = ff.makeBecomesEqualTo(left, right.toArray(new Expression[right.size()]), null);
				}
				else if (assignment instanceof BecomesMemberOf) {
					Expression assRight = ((BecomesMemberOf)assignment).getSet();
					assignment = ff.makeBecomesMemberOf(left[0], assRight.substituteFreeIdents(map, ff), assignment.getSourceLocation());
				}
				else if (assignment instanceof BecomesSuchThat) {
					Collection<BoundIdentDecl> primedCollection = new ArrayList<BoundIdentDecl>();
					for (FreeIdentifier l : left)
						primedCollection.add(l.asPrimedDecl(ff));
					BoundIdentDecl[] primed = primedCollection.toArray(new BoundIdentDecl[primedCollection.size()]);
					Predicate assRight = ((BecomesSuchThat)assignment).getCondition();
					assignment = ff.makeBecomesSuchThat(left, primed, assRight.substituteFreeIdents(map, ff), assignment.getSourceLocation());
				}
				return assignment.toString();
			}
			predicate = predicate.substituteFreeIdents(map, ff);
			return predicate.toString();
		}
		expression = expression.substituteFreeIdents(map, ff);
		return expression.toString();
	}
	
	public static boolean backupFile(IMachineRoot machine, IProgressMonitor monitor) {
		Assert.isNotNull(machine, "machine must not be null");
		IFile file = machine.getRodinFile().getResource();
		IProject project = file.getProject();
		try {
			IPath backupPath = new Path(file.getProjectRelativePath()+"_tmp");
			IFile backup = project.getFile(backupPath);
			if (backup.exists())
				backup.delete(true, monitor);
			file.copy(backupPath, true, monitor);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	public static boolean restoreFile(IMachineRoot machine, IProgressMonitor monitor) {
		Assert.isNotNull(machine, "machine must not be null");
		IFile file = machine.getRodinFile().getResource();
		IProject project = file.getProject();
		try {
			IPath backupPath = new Path(file.getProjectRelativePath()+"_tmp");
			IFile backup = project.getFile(backupPath);
			if (file.exists())
				file.delete(true, monitor);
			backup.copy(file.getProjectRelativePath(), true, monitor);
		} catch (CoreException e) {
			return false;
		}
		return true;
	}
	
	public static void unsetExtended(IEvent event) throws RodinDBException {
		new UnsetExtended(event).run(null);
	}
	
	private static final class UnsetExtended implements IWorkspaceRunnable {

		private final IEvent event;

		public UnsetExtended(IEvent event) {
			this.event = event;
		}

		public void run(IProgressMonitor pMonitor) throws RodinDBException {
			final IInternalElement[] implicitChildren = getImplicitChildren(event);
			event.setExtended(false, pMonitor);
			if (implicitChildren.length == 0) {
				return;
			}
			insertImplicitChildren(implicitChildren);
		}

		private void insertImplicitChildren(
				final IInternalElement[] implicitChildren)
				throws RodinDBException {
			final IRodinElement firstChild = getFirstChild();
			for (IInternalElement implicit : implicitChildren) {
				final String name = getFreshName(implicit);
				implicit.copy(event, firstChild, name, false, null);
			}
		}

		private IRodinElement getFirstChild() throws RodinDBException {
			for (IRodinElement child : event.getChildren()) {
				if (child.getElementType() != IRefinesEvent.ELEMENT_TYPE) {
					return child;
				}
			}
			return null;
		}

		private String getFreshName(IInternalElement implicit)
				throws RodinDBException {
			final IInternalElementType<?> type = implicit.getElementType();
			final String name = implicit.getElementName();
			if (event.getInternalElement(type, name).exists()) {
				return getFreeChildName(event, type, "internal"); //$NON-NLS-1$
			}
			return name;
		}
	}
}
