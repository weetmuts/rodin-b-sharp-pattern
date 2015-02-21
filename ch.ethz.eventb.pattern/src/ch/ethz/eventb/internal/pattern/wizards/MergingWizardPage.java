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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eventb.core.IEvent;
import org.eventb.core.IMachineRoot;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

import ch.ethz.eventb.internal.pattern.ActionPerformer;
import ch.ethz.eventb.internal.pattern.Data;
import ch.ethz.eventb.internal.pattern.PatternUtils;




public class MergingWizardPage extends WizardPage {

	private MatchingWizardPage matchingPage;

	private ElementChooserViewer<IMachineRoot> refMachineChooser;
	
	// Event matching group
	private MergingGroup eventGroup;
	
	// The matching between problem and pattern machines.	
	private MatchingMachine merging;
	
	private Label projectLabel;
		
	private ActionPerformer pageChanged = new ActionPerformer();
		
	private Data data;
	
	private Collection<IRodinFile> openFiles;
	
	private class MergingContentProvider implements IStructuredContentProvider {

		
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
		 */
		public void dispose() {
			// Do nothing	
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse
		 * .jface.viewers.Viewer, java.lang.Object, java.lang.Object)
		 */
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// Do nothing
		}

		/**
		 * Method to return the list of objects for a given input.
		 * 
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
		 */
		public Object[] getElements(Object inputElement) {
			return ((Collection<IEvent>)inputElement).toArray(new IEvent[((Collection<IEvent>)inputElement).size()]);
		}
		
	}
	
		
	
	/**
	 * The constructor. Stored the current selection to set the initial value
	 * later.
	 * 
	 * @param selection
	 *            the current selection.
	 */
	public MergingWizardPage(MatchingWizardPage matchingPage, Collection<IRodinFile> openFiles, Data data) {
		super("wizardPage");
		setTitle("Event-B Pattern. Step 3");
		setDescription("This step is for developer to choose the merging of new events with not-matched ones.");
		this.matchingPage = matchingPage;
		this.openFiles = openFiles;
		this.data = data;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout gl = new GridLayout();
		gl.numColumns = 2;
		container.setLayout(gl);
	
		// Project Label
		Label label = new Label(container, SWT.NONE);
		label.setText("Project:");
		label.setLayoutData(new GridData());
		
		projectLabel = new Label(container, SWT.NONE);
		MachineChooserGroup patternGroup = matchingPage.getPatternGroup();
		final ElementChooserViewer<IRodinProject> projectChooser = patternGroup.getProjectChooser();
		projectChooser.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				projectChanged(projectChooser.getElement());
			}
			
		});
		projectLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// Machine Label
		label = new Label(container, SWT.NONE);
		label.setText("Pattern refinment machine");
		label.setLayoutData(new GridData());
		
		refMachineChooser = new ElementChooserViewer<IMachineRoot>(container,
				IMachineRoot.ELEMENT_TYPE);
		refMachineChooser.getControl().setLayoutData(
				new GridData(GridData.FILL_HORIZONTAL));
		refMachineChooser.addSelectionChangedListener(
				new ISelectionChangedListener() {

					/*
					 * (non-Javadoc)
					 * 
					 * @seeorg.eclipse.jface.viewers.ISelectionChangedListener#
					 * selectionChanged
					 * (org.eclipse.jface.viewers.SelectionChangedEvent)
					 */
					public void selectionChanged(SelectionChangedEvent event) {
						refMachineChooser.getElement();
						
						if (refMachineChooser.getElement() == null) {
							updateStatus("A refinement machine must be chosen");
							return;
						}
						else if (!refMachineChooser.getElement().getSCMachineRoot().exists()) {
							updateStatus("The refinement machine must be a checked model");
							return;
						}
						refMachineChanged();
					}

				});

		
		// Create the variable matching group.
		eventGroup = new MergingGroup(container, SWT.DEFAULT,
				IEvent.ELEMENT_TYPE, data);
		eventGroup.getGroup().setText("Merging new pattern events with not-matched problem events.");
		eventGroup.getPatternChooser().setContentProvider(new MergingContentProvider());
		eventGroup.getProblemChooser().setContentProvider(new MergingContentProvider());
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		eventGroup.getGroup().setLayoutData(gd);
		
		eventGroup.getActionPerformer().addListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				pageChanged.performAction();
				updateStatus(null);
			}
		});
		
		matchingPage.getEventGroup().getActionPerformer().addListener( new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				matchingChanged();
				eventGroup.setInput(null);
				updateStatus(null);
			}
			
		});
		
		// Initialise the widgets
		initialize();
		
		updateStatus(null);
			
		// Set the main control of the wizard.
		setControl(container);
				
	}


	/**
	 * Tests if the current workbench selection is a suitable container to use.
	 */

	private void initialize() {
		MachineChooserGroup patternGroup = matchingPage.getPatternGroup();
		ElementChooserViewer<IRodinProject> projectChooser = patternGroup.getProjectChooser();

		IRodinProject element = projectChooser.getElement();
		eventGroup.setInput(null);
		projectChanged(element);
		
	}
	
	private void matchingChanged () {
//		if (matchingPage.getMatching() != null && matching != null) {
//			
//			Collection<IEvent> events = PatternUtils.getNewPatternEvents(matchingPage.getMatching(), matching.getPatternElement());
//			newEventsOfPatternRefinement = events.toArray(new IEvent[events.size()]);
//	//		if (events == null){
//	//			matching.clearMatching();
//	//		}
//			
//			// generate list of not-matched events
//			events = PatternUtils.getNotMatchedProblemEvents(matchingPage.getMatching());
//			notMatchedEventsOfProblem = events.toArray(new IEvent[events.size()]);
//	//		if (events == null){
//				matching.clearMatching();
//	//		}
//		}
//		else {
//			newEventsOfPatternRefinement = null;
//			notMatchedEventsOfProblem = null;
//		}
	}



	/**
	 * Utility method to update the status message and also set the completeness
	 * of the page.
	 * 
	 * @param message
	 *            the error message or <code>null</code>.
	 */
	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}


	
	public ActionPerformer getActionPerformer() {
		return pageChanged;
	}
	
	protected void projectChanged(IRodinProject project) {
		if (project != null)
			projectLabel.setText(project.getElementName());
		else
			projectLabel.setText("-- UNDEFINED --");
		
		refMachineChooser.setInput(project);
		updateStatus("A refinement has to be chosen");
		matchingChanged();
		eventGroup.setInput(null);
		
		
	}
	
	private void refMachineChanged(){
		final IMachineRoot patternRefinementMachine = refMachineChooser.getElement();
		
		if (patternRefinementMachine != null && !openFiles.contains(patternRefinementMachine.getRodinFile())){
			openFiles.add(patternRefinementMachine.getRodinFile());
			// set all events to not extended
			try {
				new ProgressMonitorDialog(this.getShell()).run(true, false,
						new IRunnableWithProgress() {

							public void run(IProgressMonitor monitor)
									throws InvocationTargetException,
									InterruptedException {
								try{
									
									IEvent[] events = patternRefinementMachine.getEvents();
									monitor.beginTask("Set events to not extended", events.length);
									for (IEvent evt : patternRefinementMachine.getEvents()) {
										if (evt.isExtended())
											PatternUtils.unsetExtended(evt);
										monitor.worked(1);
									}
									
								} catch (RodinDBException e) {
								} finally {
									monitor.done();
								}
								
							}
					
				});
			} catch (InvocationTargetException e) {
			} catch (InterruptedException e) {
			}
		}
		
		try {
			merging = new MatchingMachine(patternRefinementMachine, matchingPage.getMatching().getProblemElement());
			data.changePatternRefinementMachine(patternRefinementMachine);
			matchingChanged();
			eventGroup.getPatternChooser().setInput(data.getNewPatternRefinementEvents());
			eventGroup.getProblemChooser().setInput(data.getNotMatchedProblemEvents());
			eventGroup.setInput(merging);
		} catch (Exception e) {
		}
		updateStatus(null);
	}
	
	public IMachineRoot getPatternRefinmentMachine() {
		return refMachineChooser.getElement();
	}

	public ElementChooserViewer<IMachineRoot> getRefinementChooser() {
		return refMachineChooser;
	}
	
	public MatchingMachine getMerging() {
		return merging;
	}
	
	public MergingGroup getEventGroup() {
		return eventGroup;
	}



}