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
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eventb.core.IAction;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IMachineRoot;
import org.eventb.core.ISeesContext;
import org.eventb.core.IVariable;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.LanguageVersion;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.keyboard.RodinKeyboardPlugin;

import ch.ethz.eventb.internal.pattern.ActionPerformer;
import ch.ethz.eventb.internal.pattern.MyComboBoxCellEditor;
import ch.ethz.eventb.internal.pattern.PatternUtils;



/**
 * @author htson
 *         <p>
 *         The wizard page for matching variables and events. The user needs to
 *         select both problem machine and pattern machine in order to continue.
 *         </p>
 */
public class MatchingWizardPage extends WizardPage {

	private MatchingWizardPage page;
	
	private ISelection selection;
	
	// Problem machine chooser group
	private MachineChooserGroup problemGroup; 
	
	// Pattern machine chooser group
	private MachineChooserGroup patternGroup; 

	// Context
	private TableViewer context;
	private ISeesContext[] root;
	
	// Variable matching group
	private MatchingGroup<IVariable> variableGroup;
	
	// Event matching group
	private ExtendedMatchingGroup eventGroup;
	
	// The matching between problem and pattern matchines.	
	private MatchingMachine matching;
	
	private Renaming<ICarrierSet> carrierSetRenaming;
	private Renaming<IConstant> constantRenaming;
	
	private MyComboBoxCellEditor combo;
	private ArrayList<String> comboContent;
	
	private Button confirmation;
	
//	private Button checking;
	
	private ActionPerformer pageChanged = new ActionPerformer();
	
	private Collection<IRodinFile> openFiles;
	
	private Dialog dialog;
	
		

	/**
	 * @author fuersta
	 *         <p>
	 *         A utility class for providing contents for the table viewer.
	 *         </p>
	 */
	private class ContextContentProvider implements IStructuredContentProvider {

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
			Assert.isLegal(inputElement instanceof MatchingMachine,
					"input must be a machine");
			ArrayList<Object> result = new ArrayList<Object>();
			try {
				// Stored the root element and return its children.
				root = PatternUtils.getRootByName(IMachineRoot.ELEMENT_TYPE, ((MatchingMachine)inputElement).getPatternElement().getElementName(), patternGroup.getProjectChooser().getElement()).getSeesClauses();
								
				for(ISeesContext context : root) {
					IContextRoot contextRoot = PatternUtils.getRodinContext(((MatchingMachine)inputElement).getPatternElement().getRodinProject(), context.getSeenContextName());
					if (contextRoot != null) {
						if(!openFiles.contains(contextRoot.getRodinFile()))
							openFiles.add(contextRoot.getRodinFile());
						for (ICarrierSet carrierset : contextRoot.getCarrierSets())
							result.add(carrierset);
						for (IConstant constant : contextRoot.getConstants())
							result.add(constant);
					}
				}
				// get problem context
				for (ISeesContext context : ((MatchingMachine)inputElement).getProblemElement().getSeesClauses()) {
					IContextRoot contextRoot = PatternUtils.getRodinContext(((MatchingMachine)inputElement).getProblemElement().getRodinProject(), context.getSeenContextName());
					if (contextRoot != null) {
						if(!openFiles.contains(contextRoot.getRodinFile()))
							openFiles.add(contextRoot.getRodinFile());
						comboContent.clear();
						for (ICarrierSet carrierset : contextRoot.getCarrierSets())
							comboContent.add(PatternUtils.getDisplayText(carrierset));
						for (IConstant constant : contextRoot.getConstants())
							comboContent.add(PatternUtils.getDisplayText(constant));
					}
				}
					
				
			} catch (RodinDBException e) {
				
			}
			return result.toArray();
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
	 * The constructor. Stored the current selection to set the initial value
	 * later.
	 * 
	 * @param selection
	 *            the current selection.
	 */
	public MatchingWizardPage(ISelection selection, Collection<IRodinFile> openFiles) {
		super("wizardPage");
		setTitle("Event-B Pattern. Step 1");
		setDescription("This step is for developer to choose the matching of variables and events");
		this.selection = selection;
		this.openFiles = openFiles;
		page = this;
	}
	
	
	public void loadMatchingMachine(MatchingMachine matching, 
			Renaming<ICarrierSet> carrierSetRenaming,
			Renaming<IConstant> constantRenaming){
		
		patternGroup.getProjectChooser().setElement(matching.getPatternProject());
		patternGroup.getMachineChooser().setElement(matching.getPatternElement());
		patternMachineChanged();
		problemGroup.getProjectChooser().setElement(matching.getProblemProject());
		problemGroup.getMachineChooser().setElement(matching.getProblemElement());
		problemMachineChanged();
		this.matching = matching;
		variableGroup.setInput(matching);
		eventGroup.setInput(matching);
		this.carrierSetRenaming = carrierSetRenaming;
		this.constantRenaming = constantRenaming;
		context.setInput(matching);
		combo.setItems(comboContent.toArray(new String[comboContent.size()]));
		pageChanged.performAction();
	}
	
	


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	public void createControl(Composite parent) {
		// Create the main composite
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;
		layout.verticalSpacing = 9;
		
		// Create the pattern machine chooser group.
		patternGroup = new MachineChooserGroup(container, SWT.DEFAULT);
		patternGroup.getGroup().setText("Pattern machine");
		patternGroup.getGroup().setLayoutData(
			new GridData(GridData.FILL_HORIZONTAL));
		patternGroup.getActionPerformer().addListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				patternMachineChanged();
				pageChanged.performAction();
			}
		});

		// Create the problem machine chooser group.
		problemGroup = new MachineChooserGroup(container, SWT.DEFAULT);
		problemGroup.getGroup().setText("Problem machine");
		problemGroup.getGroup().setLayoutData(
				new GridData(GridData.FILL_HORIZONTAL));
		problemGroup.getActionPerformer().addListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				problemMachineChanged();
				pageChanged.performAction();
			}
		});
		
		// Create the context matching group.
		GridLayout gl = new GridLayout();
		gl.numColumns = 2;
		container.setLayout(gl);
		
		Group contextGroup = new Group(container,SWT.NULL);
		contextGroup.setText("Matching context");
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		contextGroup.setLayoutData(gd);
		contextGroup.setLayout(gl);
		
		context = new TableViewer(contextGroup, SWT.BORDER);
		
		context.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		
								
		context.setContentProvider(new ContextContentProvider());
		
		TableViewerColumn patternContext = new TableViewerColumn(context,SWT.NONE);
		patternContext.getColumn().setWidth(100);
		patternContext.setLabelProvider(new CellLabelProvider(){
		    @Override
		    public void update(ViewerCell cell) {
		    	cell.setText(PatternUtils.getDisplayText(cell.getElement()));
		       
		    }

		});
		
	
		TableViewerColumn problemContext = new TableViewerColumn(context,SWT.NONE);
		problemContext.getColumn().setWidth(100);
		problemContext.setLabelProvider(new CellLabelProvider(){
		    @Override
		    public void update(ViewerCell cell) {
		    	Object element = cell.getElement();
		    	Assert.isTrue(element instanceof ICarrierSet || element instanceof IConstant,
    			"The element has to be an instance of ICarrierSet or IConstant");
		    	if (element instanceof ICarrierSet)
		    		cell.setText(carrierSetRenaming.getRenamingOfElement((ICarrierSet)element));
		    	else 
		    		cell.setText(constantRenaming.getRenamingOfElement((IConstant)element));
		    }

		});
		
		combo = new MyComboBoxCellEditor();
		combo.setStyle(SWT.NONE);
		combo.create(context.getTable());
		comboContent = new ArrayList<String>();
		combo.setItems(comboContent.toArray(new String[comboContent.size()]));
		
		
		problemContext.setEditingSupport(new EditingSupport(context) {

		    @Override
		    protected boolean canEdit(Object element) {
		     	return true;
		    }

		    @Override
		    protected CellEditor getCellEditor(Object element) {
		        return combo;
		    }

		    @Override
		    protected Object getValue(Object element) {
		    	return combo.getValue();
		    }
		    		    
		 
		    @Override
		    protected void setValue(Object element, Object value) {
		    	Assert.isTrue(element instanceof ICarrierSet || element instanceof IConstant,
	    			"The element has to be an instance of ICarrierSet or IConstant");
		    	Assert.isTrue(value instanceof Integer,
    				"The value has to be an instance of Integer");
//		    		if (element instanceof ICarrierSet)
//		    			carrierSetRenaming.removePair((ICarrierSet)element);
//		    		else
//		    			constantRenaming.removePair((IConstant)element);
//		    	}
//		    	else {
		    	if ((Integer)value >= 0) {
		    		if (element instanceof ICarrierSet)
		    			carrierSetRenaming.addPair((ICarrierSet)element, PatternUtils.getDisplayText(comboContent.get((Integer)value)));
		    		else
		    			constantRenaming.addPair((IConstant)element, PatternUtils.getDisplayText(comboContent.get((Integer)value)));
		    	}
		    	else if (!combo.getText().equals("")) {
					String translateStr = RodinKeyboardPlugin.getDefault().translate(combo.getText());
					if (!combo.getText().equals(translateStr))
						combo.setText(translateStr);
		    		if (element instanceof ICarrierSet)
		    			carrierSetRenaming.addPair((ICarrierSet)element, combo.getText());
		    		else
		    			constantRenaming.addPair((IConstant)element, combo.getText());	
		    	}
//		    	}
		    		context.setInput(matching);
		    }

		});
		
		// Create the variable matching group.
		variableGroup = new MatchingGroup<IVariable>(container, SWT.DEFAULT,
				IVariable.ELEMENT_TYPE);
		variableGroup.getGroup().setText("Matching variable");
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		variableGroup.getGroup().setLayoutData(gd);

		variableGroup.getActionPerformer().addListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				variableGroup.setInput(matching);
				pageChanged.performAction();
				updateStatus(null);
			}
		});
		
		// Create the event matching group
		eventGroup = new ExtendedMatchingGroup(container, SWT.DEFAULT,
				IEvent.ELEMENT_TYPE);
		eventGroup.getGroup().setText("Matching event");
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		eventGroup.getGroup().setLayoutData(gd);
		eventGroup.getTreeViewer().setAutoExpandLevel(2);
		
		eventGroup.getActionPerformer().addListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				eventGroup.setInput(matching);
				pageChanged.performAction();
				updateStatus(null);
			}
		});
		
		
		Group group = new Group(container, SWT.DEFAULT);
		gl = new GridLayout();
		gl.numColumns = 2;
		gl.verticalSpacing = 9;
		group.setLayout(gl);
		group.setText("Options");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		group.setLayoutData(gd);

		
		
		confirmation = new Button(group,SWT.CHECK);
		confirmation.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {
				updateStatus(null);
			}

		});
			
		Label conftext = new Label(group,SWT.NONE);
		conftext.setText("All elements of the pattern have to be matched.");
		
		
		final Group filegroup = new Group(container, SWT.DEFAULT);
		gl = new GridLayout();
		gl.numColumns = 2;
		gl.verticalSpacing = 9;
		filegroup.setLayout(gl);
		filegroup.setText("Matching file");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		filegroup.setLayoutData(gd);

		
		
		Button savefile = new Button(filegroup,SWT.PUSH);
		savefile.setText("Save Matching");
		savefile.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {
				if (matching != null){
					dialog = new SaveDialog(filegroup.getShell(), "Save Matching", page);
					dialog.open();
					updateStatus(null);
				}
			}

		});
		
		Button loadfile = new Button(filegroup,SWT.PUSH);
		loadfile.setText("Load Matching");
		loadfile.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {
				dialog = new LoadDialog(filegroup.getShell(), "Load Matching", page);
				dialog.open();
				updateStatus(null);
			}

		});
			
		
		

//		checking = new Button(group,SWT.CHECK);
//		checking.addSelectionListener(new SelectionListener() {
//
//			public void widgetDefaultSelected(SelectionEvent e) {
//				widgetSelected(e);
//			}
//
//			public void widgetSelected(SelectionEvent e) {
//				updateStatus(null);
//			}
//
//		});
//			
//		Label checkingtext = new Label(group,SWT.NONE);
//		checkingtext.setText("Check that not-matched events do not alter matched variables.");
		
		
		// Initialise the widgets
		initialize();
		
		updateStatus(null);
			
		// Set the main control of the wizard.
		setControl(container);
				
	}


	/**
	 * Utility method to be called when the pattern machine changed.
	 * <ul>
	 * <li>Reset the input for the pattern variable matching.</li>
	 * <li>Reset the input for the pattern event matching.</li>
	 * </ul>
	 */
	protected void patternMachineChanged() {
		if (checkPatternMachine()) {
			final IMachineRoot patternMachine = patternGroup.getMachineChooser().getElement();
			variableGroup.getPatternChooser().setInput(patternMachine);
			eventGroup.getPatternChooser().setInput(patternMachine);
			if (!openFiles.contains(patternMachine.getRodinFile())){
				openFiles.add(patternMachine.getRodinFile());
				// set all events to not extended
				try {
					new ProgressMonitorDialog(patternGroup.getGroup().getShell()).run(true, false,
							new IRunnableWithProgress() {

								public void run(IProgressMonitor monitor)
										throws InvocationTargetException,
										InterruptedException {
									try{
										
										IEvent[] events = patternMachine.getEvents();
										monitor.beginTask("Set events to not extended", events.length);
										for (IEvent evt : patternMachine.getEvents()) {
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
			matchingChanged();
		}
		else {
			variableGroup.getPatternChooser().setInput(null);
			eventGroup.getPatternChooser().setInput(null);
			context.setInput(null);
		}
		updateStatus(null);
	}

	/**
	 * Utility method to be called when the problem machine changed.
	 * <ul>
	 * <li>Reset the input for the problem variable matching.</li>
	 * <li>Reset the input for the problem event matching.</li>
	 */
	protected void problemMachineChanged() {
		if (checkProblemMachine()) {
			final IMachineRoot problemMachine = problemGroup.getMachineChooser().getElement();
			variableGroup.getProblemChooser().setInput(problemMachine);
			eventGroup.getProblemChooser().setInput(problemMachine);
			if (!openFiles.contains(problemMachine.getRodinFile())){
				openFiles.add(problemMachine.getRodinFile());
				// set all events to not extended
				try {
					new ProgressMonitorDialog(patternGroup.getGroup().getShell()).run(true, false,
							new IRunnableWithProgress() {

								public void run(IProgressMonitor monitor)
										throws InvocationTargetException,
										InterruptedException {
									try{
										
										IEvent[] events = problemMachine.getEvents();
										monitor.beginTask("Set events to not extended", events.length);
										for (IEvent evt : problemMachine.getEvents()) {
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
			matchingChanged();
		}
		else {
			variableGroup.getProblemChooser().setInput(null);
			eventGroup.getProblemChooser().setInput(null);
			context.setInput(null);
		}
		updateStatus(null);
	}

	/**
	 * Utility method to initialise the widgets according to the current selection.
	 * <ul>
	 * <li>The input to the problem machine chooser group is the RodinDB.</li>
	 * <li>The input to the problem machine chooser group is the RodinDB.</li>
	 * </ul>
	 */
	private void initialize() {
		IRodinDB rodinDB = RodinCore.getRodinDB();
		problemGroup.getProjectChooser().setInput(rodinDB);
		patternGroup.getProjectChooser().setInput(rodinDB);
		variableGroup.setInput(matching);
		eventGroup.setInput(matching);
		context.setInput(matching);
		combo.setItems(comboContent.toArray(new String[comboContent.size()]));
		carrierSetRenaming = new Renaming<ICarrierSet>();
		constantRenaming = new Renaming<IConstant>();
		confirmation.setSelection(true);
//		checking.setSelection(true);
						
		if (selection != null && selection.isEmpty() == false
				&& selection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) selection;
			if (ssel.size() > 1) {
				return;
			}
			Object element = ssel.getFirstElement();
			if (element instanceof IMachineRoot) {
				IRodinProject project = ((IMachineRoot) element).getRodinProject();
				problemGroup.getProjectChooser().setSelection(new StructuredSelection(project), true);
				problemGroup.getMachineChooser().setSelection(new StructuredSelection(element), true);
				return;
			}
			if (element instanceof IContextRoot) {
				IRodinProject project = ((IContextRoot) element).getRodinProject();
				problemGroup.getProjectChooser().setSelection(new StructuredSelection(project), true);
				return;
			}
			if (element instanceof IProject) {
				IRodinProject project = rodinDB.getRodinProject(((IProject) element).getName());
				problemGroup.getProjectChooser().setSelection(new StructuredSelection(project), true);
				return;
			}
			if (element instanceof IFile) {
				IRodinProject project = rodinDB.getRodinProject(((IFile) element).getProject().getName());
				problemGroup.getProjectChooser().setSelection(new StructuredSelection(project), true);
				if (project != null) {
					IRodinFile file = project.getRodinFile(((IFile)element).getName());
					if (file != null) {
						IInternalElement root = file.getRoot();
						if (root instanceof IMachineRoot)
							problemGroup.getMachineChooser().setSelection(new StructuredSelection(root), true);
					}
				}
				return;
			}
			patternMachineChanged();
			problemMachineChanged();
		}
	}

	private boolean checkProblemMachine() {
		IMachineRoot problemMachine = problemGroup.getMachineChooser().getElement();
		if (problemMachine == null)
			return false;
		else if (!problemMachine.getSCMachineRoot().exists())
			return false;
		return true;
	}
	
	private boolean checkPatternMachine() {
		IMachineRoot patternMachine = patternGroup.getMachineChooser().getElement();
		if (patternMachine == null)
			return false;
		else if (!patternMachine.getSCMachineRoot().exists())
			return false;
		return true;
	}
	
	private void matchingChanged() {
		IMachineRoot problemMachine = problemGroup.getMachineChooser().getElement();
		IMachineRoot patternMachine = patternGroup.getMachineChooser().getElement();
		
		if (problemMachine != null && patternMachine != null) {
			try {
				matching = new MatchingMachine(patternMachine, problemMachine);
				matching.addComplexMatching(PatternUtils.getElementByLabel(IEvent.ELEMENT_TYPE, IEvent.INITIALISATION, problemMachine),
						PatternUtils.getElementByLabel(IEvent.ELEMENT_TYPE, IEvent.INITIALISATION, patternMachine),
						IEvent.ELEMENT_TYPE);
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			variableGroup.setInput(matching);
			eventGroup.setInput(matching);
			context.setInput(matching);
			combo.setItems(comboContent.toArray(new String[comboContent.size()]));
			carrierSetRenaming = new Renaming<ICarrierSet>();
			constantRenaming = new Renaming<IConstant>();
		}
	}
	

	/**
	 * Utility method to update the status message and also set the completeness
	 * of the page.
	 * 
	 * @param message
	 *            the error message or <code>null</code>.
	 */
	private void updateStatus(String message) {
		if (message == null) {
			IMachineRoot problemMachine = problemGroup.getMachineChooser().getElement();
			if (problemMachine == null)
				message = "A problem machine must be chosen";
			else if (!problemMachine.getSCMachineRoot().exists())
				message = "The problem machine must be a checked model";
		}
		if (message == null) {
			IMachineRoot patternMachine = patternGroup.getMachineChooser().getElement();
			if (patternMachine == null)
				message = "A pattern machine must be chosen";
			else if (!patternMachine.getSCMachineRoot().exists())
				message = "The pattern machine must be a checked model";
		}
		if (message == null && confirmation.getSelection() && !matchingComplete())
			message = "Not all elements of the pattern are matched";
		if (message == null)
			message = checkNotMatchedEvents();
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	/**
	 * Get the matching between two machines.
	 * 
	 * @return the matching between two machines.
	 */
	public MatchingMachine getMatching() {
		return matching;
	}
	
	/**
	 * Return the pattern machine chooser group. This is used by the Renaming
	 * page {@link RenamingWizardPage}.
	 * 
	 * @return the pattern machine chooser group.
	 */
	public MachineChooserGroup getPatternGroup() {
		return patternGroup;
	}

	public MachineChooserGroup getProblemGroup() {
		return problemGroup;
	}
	
	public ExtendedMatchingGroup getEventGroup() {
		return eventGroup;
	}
	
	public MatchingGroup<IVariable> getVariableGroup() {
		return variableGroup;
	}
	
	public Renaming<ICarrierSet> getCarrierSetRenaming() {
		return carrierSetRenaming;
	}
	
	public Renaming<IConstant> getConstantRenaming() {
		return constantRenaming;
	}
	
	public ActionPerformer getActionPerformer() {
		return pageChanged;
	}
	
	private boolean matchingComplete() {
		try {
			for (IVariable variable : patternGroup.getMachineChooser().getElement().getVariables())
				if (!PatternUtils.isInMatchings(variable, null, variableGroup.getMatchings()))
					return false;
			for (IEvent event : patternGroup.getMachineChooser().getElement().getEvents()){
				ComplexMatching<IEvent> eventMatching = 
					PatternUtils.getMatching(event, null, eventGroup.getMatchings());
				if (eventMatching == null)
					return false;
				for (IGuard guard : event.getGuards())
					if (!PatternUtils.isInMatchings(guard, null, eventMatching.getChildrenOfType(IGuard.ELEMENT_TYPE)))
						return false;
				for (IAction action : event.getActions())
					if (!PatternUtils.isInMatchings(action, null, eventMatching.getChildrenOfType(IAction.ELEMENT_TYPE)))
						return false;
			}
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	private String checkNotMatchedEvents() {
		try {
			Assignment assignment;
			Collection<String> variables = new ArrayList<String>();
			for (Matching<IVariable> variable : variableGroup.getMatchings())
				variables.add(variable.getProblemID());
			FormulaFactory ff = FormulaFactory.getDefault();
			for (IEvent event : problemGroup.getMachineChooser().getElement().getEvents()) {
				if (!PatternUtils.isInMatchings(null, event, eventGroup.getMatchings())){
					for (IAction action : event.getActions()){
						assignment = ff.parseAssignment(action.getAssignmentString(), LanguageVersion.LATEST, null).getParsedAssignment();
						for (FreeIdentifier free : assignment.getAssignedIdentifiers())
							if (variables.contains(free.getName()))
								return "The not-matched event '" + event.getLabel() + "' alters the matched variable '" + free.getName() + "'!";
					}
				}
			}
		} catch (RodinDBException e) {
		}
		return null;
		
	}

}