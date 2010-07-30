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
package ch.ethz.eventb.internal.pattern.wizards;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TreeItem;
import org.eventb.core.IAction;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IParameter;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.IEventBSharedImages;
import org.rodinp.core.IInternalElementType;

import ch.ethz.eventb.internal.pattern.ActionPerformer;
import ch.ethz.eventb.internal.pattern.Data;


/**
 * @author fuersta
 *         <p>
 *         A generic implementation for a matching group. This group provides
 *         two element choosers ({@link ElementChooserViewer}) for choosing the
 *         two matching element and a button add the matching to the list.
 *         </p>
 *         <p>
 *         The matchings are display in a TreeViewer with button to remove a
 *         mapping and to edit the details of the matching (e.g. matching of
 *         events).
 *         </p>
 * @param <T>
 *            the type of the matching elements.
 */
public class ExtendedMatchingGroup {

	// The main group widget
	private Group group;

	// The problem element chooser.
	private ElementChooserViewer<IEvent> problemChooser;

	// The pattern element chooser.
	private ElementChooserViewer<IEvent> patternChooser;

	// The tree viewer.
	TreeViewer viewer;

	// The add button.
	private Button addButton;
	
	private Button removeButton;

	// The root element of the matching.
	private IComplexMatching<?> root;

	// The element type of matchings.
	IInternalElementType<IEvent> type;

	private SubmatchingDialog dialog;
	
	private Data data;
	
	private ActionPerformer matchingChanged = new ActionPerformer();

	/**
	 * @author fuersta
	 *         <p>
	 *         A utility class for providing contents for the table viewer.
	 *         </p>
	 */
	private class ExtendedMatchingContentProvider implements ITreeContentProvider {


		public Object[] getElements(Object inputElement) {
			// The input element will be the root of the viewer and must be a
			// complex matching, i.e. having sub-matchings.
			Assert.isLegal(inputElement instanceof IComplexMatching<?>,
					"input must be a complex matching");
			// Stored the root element and return its children.
			root = (IComplexMatching<?>) inputElement;
			return root.getChildrenOfType(type);
		}

		public void dispose() {
			// Reset the root node to null.
			root = null;
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// Reset the root node to null.
			root = null;
			
		}

		public Object[] getChildren(Object parentElement) {
			
			Collection<Object> result = new ArrayList<Object>();
			if (parentElement instanceof ComplexMatching<?>){
				for (Matching<IParameter> matching : ((ComplexMatching<IEvent>) parentElement).getChildrenOfType(IParameter.ELEMENT_TYPE)) {
					result.add((Matching<IParameter>)matching);
				}
				for (Matching<IGuard> matching : ((ComplexMatching<IEvent>) parentElement).getChildrenOfType(IGuard.ELEMENT_TYPE)) {
					result.add((Matching<IGuard>)matching);
				}
				for (Matching<IAction> matching : ((ComplexMatching<IEvent>) parentElement).getChildrenOfType(IAction.ELEMENT_TYPE)) {
					result.add((Matching<IAction>)matching);
				}
			}
			return result.toArray();
						
		}

		public Object getParent(Object element) {
			return null;
		}

		public boolean hasChildren(Object element) {
			if(element instanceof ComplexMatching<?>)
				return true;
			return false;
		}



	}

	/**
	 * @author fuersta
	 *         <p>
	 *         A utility class for providing label for the table viewer.
	 *         </p>
	 */
	private class MatchingLabelProvider implements ILabelProvider {

		public Image getImage(Object element) {
			if (element instanceof Matching && ((Matching)element).getType().equals(IGuard.ELEMENT_TYPE))
				return EventBUIPlugin.getDefault().getImageRegistry().get(IEventBSharedImages.IMG_GUARD);
			else if (element instanceof Matching && ((Matching)element).getType().equals(IParameter.ELEMENT_TYPE))
				return EventBUIPlugin.getDefault().getImageRegistry().get(IEventBSharedImages.IMG_PARAMETER);
			else if (element instanceof Matching && ((Matching)element).getType().equals(IAction.ELEMENT_TYPE))
				return EventBUIPlugin.getDefault().getImageRegistry().get(IEventBSharedImages.IMG_ACTION);
			else if (element instanceof ComplexMatching && ((ComplexMatching)element).getType().equals(IEvent.ELEMENT_TYPE))
				return EventBUIPlugin.getDefault().getImageRegistry().get(IEventBSharedImages.IMG_EVENT);
			else
			
			
				return null;
		}

		public String getText(Object element) {
			// Element must be a matching
			//Assert.isLegal(element instanceof IMatching<?>,
			//		"Element should be a matching");
			// Return the pretty-print of the matching
			return element.toString();
		}

		public void addListener(ILabelProviderListener listener) {
			// Do nothing
		}

		public void dispose() {
			// Do nothing
		}

		public boolean isLabelProperty(Object element, String property) {
			// Ignore by returning false
			return false;
		}

		public void removeListener(ILabelProviderListener listener) {
			// Do nothing
		}



	}

	/**
	 * The constructor. Create the main group widget with the input type then
	 * create the content of the widget.
	 * 
	 * @param container
	 *            the composite parent of the main group widget.
	 * @param style
	 *            the style to create the main group widget.
	 * @param type
	 *            the element type of the matchings
	 */
	public ExtendedMatchingGroup(Composite container, int style, IInternalElementType<IEvent> type, Data data) {
		group = new Group(container, style);
		GridLayout gl = new GridLayout();
		gl.numColumns = 3;
		gl.verticalSpacing = 9;
		group.setLayout(gl);
		this.type = type;
		this.data = data;
		createContents();
	}

	/**
	 * Utility method for creating the actual contents of the group widget. This
	 * is called from the constructor of the class.
	 */
	private void createContents() {
		
		ISelectionChangedListener listener = new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				updateAddButton();
			}
		};
		
		// Pattern element chooser
		patternChooser = new ElementChooserViewer<IEvent>(group, type);
		patternChooser.getControl().setLayoutData(
				new GridData(GridData.FILL_HORIZONTAL));
		patternChooser.addSelectionChangedListener(listener);

		// Add Matching button
		addButton = new Button(group, SWT.PUSH);
		addButton.setText("Add Matching");
		addButton.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {
				addMatching();
			}

		});
		addButton.setEnabled(false);
		
		// Problem element chooser
		problemChooser = new ElementChooserViewer<IEvent>(group, type);
		problemChooser.getControl().setLayoutData(
				new GridData(GridData.FILL_HORIZONTAL));
		problemChooser.addSelectionChangedListener(listener);

		// Create a table viewer with button
		Composite composite = new Composite(group, SWT.NONE);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 3;
		composite.setLayoutData(gd);
		GridLayout gl = new GridLayout();
		gl.numColumns = 2;
		gl.horizontalSpacing = 9;
		composite.setLayout(gl);

		// Tree viewer of the matching
		viewer = new TreeViewer(composite, SWT.BORDER);
		viewer.setContentProvider(new ExtendedMatchingContentProvider());
		viewer.setLabelProvider(new MatchingLabelProvider());
		viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		
		viewer.addDoubleClickListener(new IDoubleClickListener(){
				
				
				
				
			public void doubleClick(DoubleClickEvent event) {
			
			TreeItem[] sel = viewer.getTree().getSelection();
			if (sel.length == 1){
				if (sel[0].getData() instanceof ComplexMatching<?>)
					addSubMatching(viewer.getSelection(),(ComplexMatching<IEvent>)sel[0].getData());					
			}
		}

		
		});
		
		viewer.addSelectionChangedListener(new ISelectionChangedListener(){

			public void selectionChanged(SelectionChangedEvent event) {
				TreeItem[] sel = viewer.getTree().getSelection();
				if (sel.length == 1)
					removeButton.setEnabled(sel[0].getData() instanceof ComplexMatching<?> && 
								!((ComplexMatching<IEvent>)sel[0].getData()).getPatternID().equals(IEvent.INITIALISATION));
			}
			
		});
		

		
		// Remove button
		removeButton = new Button(composite, SWT.PUSH);
		removeButton.setText("Remove");
		removeButton.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {
				removeMatching();
			}

		});
		

		
		Label submatching = new Label(composite,SWT.NULL);
		submatching.setText("Double click on matching to enter/change submatchings");
		
		
	}

	/**
	 * Utility method to update the status of the add button. 
	 */
	protected void updateAddButton() {
		IEvent problemElement = this.getProblemChooser().getElement();
		IEvent patternElement = this.getPatternChooser().getElement();
		addButton.setEnabled(problemElement != null && patternElement != null);
	}

	/**
	 * Utility method to remove a matching.
	 */
	@SuppressWarnings("unchecked")
	protected void removeMatching() {
		TreeItem[] sel = viewer.getTree().getSelection();
		if (sel.length == 1){
			ComplexMatching<IEvent> item = (ComplexMatching<IEvent>)sel[0].getData();
			root.removeMatching(item);
			try {
				data.removeMatching(item.getPatternElement(), item.getProblemElement());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		viewer.setInput(root);
		matchingChanged.performAction();
	}

	/**
	 * Utility method to add a matching.
	 */
	protected void addMatching() {
		IEvent problemElement = this.getProblemChooser().getElement();
		IEvent patternElement = this.getPatternChooser().getElement();
		Assert.isNotNull(problemElement, "Problem element should not be null");
		Assert.isNotNull(patternElement, "Problem element should not be null");
		root.addComplexMatching(patternElement, problemElement, IEvent.ELEMENT_TYPE);
		try {
			data.addMatching(patternElement, problemElement);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		viewer.setInput(root);
		matchingChanged.performAction();
	}
	
	/**
	 * Utility method to add a sub-matching.
	 */
	protected void addSubMatching(ISelection selection, ComplexMatching<IEvent> matching ) {
		
		dialog = new SubmatchingDialog(group.getShell(), "Sub-matching", matching, data);
		dialog.open();
		viewer.setInput(root);
		matchingChanged.performAction();		
	}
	

	

	/**
	 * Get the main control (the group widget).
	 * 
	 * @return the main group widget.
	 */
	public Group getGroup() {
		return group;
	}

	/**
	 * Get the problem element chooser.
	 * 
	 * @return the problem element chooser.
	 */
	public ElementChooserViewer<IEvent> getProblemChooser() {
		return problemChooser;
	}

	/**
	 * Get the pattern element chooser.
	 * 
	 * @return the pattern element chooser.
	 */
	public ElementChooserViewer<IEvent> getPatternChooser() {
		return patternChooser;
	}

	public TreeViewer getTreeViewer() {
		return viewer;
	}

	
	/**
	 * Set the input of the viewer.
	 * 
	 * @param matching
	 *            a complex matching input.
	 */
	public void setInput(IComplexMatching<?> matching) {
		viewer.setInput(matching);
		matchingChanged.performAction();
	}

	public ActionPerformer getActionPerformer(){
		return matchingChanged;
	}
	
	
	public ComplexMatching<IEvent>[] getMatchings(){
		if (root != null)
			return root.getChildrenOfTypeEvent();
		else return new ComplexMatching[0];
	}

}
