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

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
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
import org.eclipse.swt.widgets.TableItem;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinDBException;

import ch.ethz.eventb.internal.pattern.ActionPerformer;


/**
 * @author htson
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
public class MatchingGroup<T extends IInternalElement> {

	// The main group widget
	private Group group;

	// The problem element chooser.
	private ElementChooserViewer<T> problemChooser;

	// The pattern element chooser.
	private ElementChooserViewer<T> patternChooser;

	// The table viewer.
	TableViewer viewer;

	// The add button.
	private Button addButton;

	// The root element of the matching.
	private IComplexMatching<?> root;

	// The element type of matchings.
	IInternalElementType<T> type;
	
	private ActionPerformer matchingChanged = new ActionPerformer();


	/**
	 * @author htson
	 *         <p>
	 *         A utility class for providing contents for the table viewer.
	 *         </p>
	 */
	private class MatchingContentProvider implements IStructuredContentProvider {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.IStructuredContentProvider#getElements(
		 * java.lang.Object)
		 */
		public Object[] getElements(Object inputElement) {
			// The input element will be the root of the viewer and must be a
			// complex matching, i.e. having sub-matchings.
			Assert.isLegal(inputElement instanceof IComplexMatching<?>,
					"input must be a complex matching");
			// Stored the root element and return its children.
			root = (IComplexMatching<?>) inputElement;
			return root.getChildrenOfType(type);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
		 */
		public void dispose() {
			// Reset the root node to null.
			root = null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse
		 * .jface.viewers.Viewer, java.lang.Object, java.lang.Object)
		 */
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// Reset the root node to null.
			root = null;
		}
		
	}

	/**
	 * @author htson
	 *         <p>
	 *         A utility class for providing label for the table viewer.
	 *         </p>
	 */
	private class MatchingLabelProvider implements ITableLabelProvider {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java
		 * .lang.Object, int)
		 */
		public Image getColumnImage(Object element, int columnIndex) {
			// Ignore by return null
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.
		 * lang.Object, int)
		 */
		public String getColumnText(Object element, int columnIndex) {
			// Element must be a matching
			Assert.isLegal(element instanceof IMatching<?>,
					"Element should be a matching");
			// Return the pretty-print of the matching
			return element.toString();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse
		 * .jface.viewers.ILabelProviderListener)
		 */
		public void addListener(ILabelProviderListener listener) {
			// Do nothing
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
		 */
		public void dispose() {
			// Do nothing
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java
		 * .lang.Object, java.lang.String)
		 */
		public boolean isLabelProperty(Object element, String property) {
			// Ignore by returning false
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse
		 * .jface.viewers.ILabelProviderListener)
		 */
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
	public MatchingGroup(Composite container, int style, IInternalElementType<T> type) {
		group = new Group(container, style);
		GridLayout gl = new GridLayout();
		gl.numColumns = 3;
		gl.verticalSpacing = 9;
		group.setLayout(gl);
		this.type = type;
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
		patternChooser = new ElementChooserViewer<T>(group, type);
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
				try {
					addMatching();
				} catch (RodinDBException e1) {
				}
			}

		});
		addButton.setEnabled(false);

		// Problem element chooser
		problemChooser = new ElementChooserViewer<T>(group, type);
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

		// Table viewer of the matching
		viewer = new TableViewer(composite, SWT.BORDER);
		viewer.setContentProvider(new MatchingContentProvider());
		viewer.setLabelProvider(new MatchingLabelProvider());
		viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));

		// Remove button
		Button removeButton = new Button(composite, SWT.PUSH);
		removeButton.setText("Remove");
		removeButton.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {
				removeMatching();
			}

		});
		
	}

	/**
	 * Utility method to update the status of the add button. 
	 */
	protected void updateAddButton() {
		T problemElement = this.getProblemChooser().getElement();
		T patternElement = this.getPatternChooser().getElement();
		addButton.setEnabled(problemElement != null && patternElement != null);
	}

	/**
	 * Utility method to remove a matching.
	 */
	@SuppressWarnings("unchecked")
	protected void removeMatching() {
		TableItem[] table = viewer.getTable().getSelection();
		if (table.length == 1){
			Matching<T> item = (Matching<T>)table[0].getData();
			root.removeMatching(item);
		}
		viewer.setInput(root);
		matchingChanged.performAction();
	}

	/**
	 * Utility method to add a matching.
	 * @throws RodinDBException 
	 */
	protected void addMatching() throws RodinDBException {
		T problemElement = this.getProblemChooser().getElement();
		T patternElement = this.getPatternChooser().getElement();
		Assert.isNotNull(problemElement, "Problem element should not be null");
		Assert.isNotNull(patternElement, "Problem element should not be null");
		root.addMatching(patternElement, problemElement, type);
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
	public ElementChooserViewer<T> getProblemChooser() {
		return problemChooser;
	}

	/**
	 * Get the pattern element chooser.
	 * 
	 * @return the pattern element chooser.
	 */
	public ElementChooserViewer<T> getPatternChooser() {
		return patternChooser;
	}
	
	public TableViewer getTableViewer() {
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
	
	}
	
	public ActionPerformer getActionPerformer() {
		return matchingChanged;
	}
	
	public Matching<T>[] getMatchings(){
		if (root != null)
			return root.getChildrenOfType(type);
		else return new Matching[0];
	}
	
}
