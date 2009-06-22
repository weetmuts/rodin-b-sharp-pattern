package ch.ethz.eventb.internal.pattern.wizards;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eventb.core.IEvent;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IVariable;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

import ch.ethz.eventb.internal.pattern.Pair;
import ch.ethz.eventb.internal.pattern.PatternUtils;




/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (mpe).
 */

public class RenamingWizardPage extends WizardPage {

	private MatchingWizardPage matchingPage;

	private ElementChooserViewer<IMachineRoot> refMachineChooser;
	
	private IMachineRoot root;
	
	private Label projectLabel;
	
	private TableViewer variables;
	
	private TableViewer events;
	
	private Renaming<IVariable> renamedVariables;
	
	private Renaming<IEvent> renamedEvents;
	
	
	/**
	 * @author fuersta
	 *         <p>
	 *         A utility class for providing contents for the table viewer.
	 *         </p>
	 */
	private class VariableContentProvider implements IStructuredContentProvider {

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
			Assert.isLegal(inputElement instanceof IMachineRoot,
					"input must be a machine");
			// Stored the root element and return its children.
			root = (IMachineRoot)inputElement;
			try {
				return root.getVariables();
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				return null;
			}
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
	 * @author fuersta
	 *         <p>
	 *         A utility class for providing contents for the table viewer.
	 *         </p>
	 */
	private class EventContentProvider implements IStructuredContentProvider {

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
			Assert.isLegal(inputElement instanceof IMachineRoot,
					"input must be a machine");
			// Stored the root element and return its children.
			root = (IMachineRoot)inputElement;
			Collection<Pair<String,String>> result = new ArrayList<Pair<String,String>>();
			try {
				Set<String> refEvents = new HashSet<String>();
				for (IEvent evt : root.getEvents())
					if (!evt.isInitialisation())
						refEvents.add(evt.getLabel());
				
				for(Matching<IEvent> match : matchingPage.getMatching().getChildrenOfType(IEvent.ELEMENT_TYPE)) {
					for (IEvent evt : PatternUtils.getRefinementEvents(match.getPatternElement(), root)) {
						result.add(new Pair<String, String>(evt.getLabel(),match.getProblemID()));
						refEvents.remove(evt.getLabel());
					}
				}
				for (String evt : refEvents)
					result.add(new Pair<String, String>(evt,""));
			
			
				return result.toArray(new Pair[result.size()]);
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				return null;
			}
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
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param matchingPage
	 * 
	 * @param pageName
	 */
	public RenamingWizardPage(MatchingWizardPage matchingPage) {
		super("wizardPage");
		setTitle("Event-B Pattern. Step 3");
		setDescription("This step is for developers to choose the renaming before incorporating the pattern.");
		this.matchingPage = matchingPage;
	}

	/**
	 * @see IDialogPage#createControl(Composite)
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

		
		// Table viewer of the matching
		Group variableGroup = new Group(container,SWT.NULL);
		variableGroup.setText("Renaming of the variables");
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		variableGroup.setLayoutData(gd);
		variableGroup.setLayout(gl);
		
		variables = new TableViewer(variableGroup, SWT.NULL);
		
		variables.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		
								
		variables.setContentProvider(new VariableContentProvider());
		
		TableViewerColumn originalVariables = new TableViewerColumn(variables,SWT.NONE);
		originalVariables.getColumn().setWidth(200);
		originalVariables.getColumn().setResizable(true);
		originalVariables.setLabelProvider(new CellLabelProvider(){
		    @Override
		    public void update(ViewerCell cell) {
		    	cell.setText(PatternUtils.getDisplayText(cell.getElement()));
		       
		    }

		});
		
		TableViewerColumn renamedVars = new TableViewerColumn(variables,SWT.NONE);
		renamedVars.getColumn().setWidth(200);
		originalVariables.getColumn().setResizable(true);
		renamedVars.setLabelProvider(new CellLabelProvider(){
		    @Override
		    public void update(ViewerCell cell) {
		       cell.setText(renamedVariables.getRenamingOfElement((IVariable)cell.getElement()));
		    }

		});
		
		renamedVars.setEditingSupport(new EditingSupport(variables) {

		    @Override
		    protected boolean canEdit(Object element) {
		     	return true;
		    }

		    @Override
		    protected CellEditor getCellEditor(Object element) {
		        return new TextCellEditor(variables.getTable());
		    }

		    @Override
		    protected Object getValue(Object element) {
		        return renamedVariables.getRenamingOfElement((IVariable)element);
		    }

		    @Override
		    protected void setValue(Object element, Object value) {
		    	if (!value.toString().equals("")){
		    		renamedVariables.addPair((IVariable)element, value.toString());
		    		variables.setInput(root);
		    		updateStatus(null);
		    	}
		    }

		});
		
		
		matchingPage.getVariableGroup().getActionPerformer().addListener( new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				initializeVariables();
				variables.setInput(root);
				updateStatus(null);
			}
			
		});
		
		Group eventGroup = new Group(container,SWT.NULL);
		eventGroup.setText("Renaming of the events");
		eventGroup.setLayoutData(gd);
		eventGroup.setLayout(gl);
		
		
		events = new TableViewer(eventGroup, SWT.NONE);
	
				
		events.setContentProvider(new EventContentProvider());
		events.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));

		TableViewerColumn originalEvents = new TableViewerColumn(events,SWT.NONE);
		originalEvents.getColumn().setWidth(200);
		originalEvents.setLabelProvider(new CellLabelProvider(){
		    @Override
		    public void update(ViewerCell cell) {
		    	String label = ((Pair<String,String>)cell.getElement()).getFirst();
		    	String matched = ((Pair<String,String>)cell.getElement()).getSecond();
		    	if (!matched.equals(""))
		    		cell.setText(label+" ("+matched+")");
		    	else
		    		cell.setText(label);
		    }

		});
		
		TableViewerColumn renamedEvts = new TableViewerColumn(events,SWT.NONE);
		renamedEvts.getColumn().setWidth(200);
		renamedEvts.setLabelProvider(new CellLabelProvider(){
		    @Override
		    public void update(ViewerCell cell) {
		       cell.setText(renamedEvents.getRenamingOfElement(
		    		   ((Pair<String,String>)cell.getElement()).getFirst(),
		    		   ((Pair<String,String>)cell.getElement()).getSecond()));
		    }

		});
		
		renamedEvts.setEditingSupport(new EditingSupport(events) {

		    @Override
		    protected boolean canEdit(Object element) {
		    	if (((Pair<String,String>)element).getFirst().equals(IEvent.INITIALISATION))
					return false;
				return true;
		    }

		    @Override
		    protected CellEditor getCellEditor(Object element) {
		        return new TextCellEditor(events.getTable());
		    }

		    @Override
		    protected Object getValue(Object element) {
		        return renamedEvents.getRenamingOfElement(((Pair<String,String>)element).getFirst(),
		        		((Pair<String,String>)element).getSecond());
		    }

		    @Override
		    protected void setValue(Object element, Object value) {
		    	if (!value.toString().equals("")){
		    		renamedEvents.addPair(((Pair<String,String>)element).getFirst(),
		    				((Pair<String,String>)element).getSecond(),value.toString());
			    	events.setInput(root);
			    	updateStatus(null);
		    	}
		    		
		    	
		    }

		});
		
		matchingPage.getEventGroup().getActionPerformer().addListener( new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				initializeEvents();
				events.setInput(root);
				updateStatus(null);
			}
			
		});

		initialize();
		setControl(container);
	}
	
	
	
	
	private void refMachineChanged(){
		initializeVariables();
		initializeEvents();
		variables.setInput(refMachineChooser.getElement());
		events.setInput(refMachineChooser.getElement());
		updateStatus(null);
	}

	protected void projectChanged(IRodinProject project) {
		if (project != null)
			projectLabel.setText(project.getElementName());
		else
			projectLabel.setText("-- UNDEFINED --");
		
		refMachineChooser.setInput(project);
		updateStatus("A refinement has to be chosen");
		variables.setInput(null);
		events.setInput(null);
		
	}

	/**
	 * Tests if the current workbench selection is a suitable container to use.
	 */

	private void initialize() {
		MachineChooserGroup patternGroup = matchingPage.getPatternGroup();
		ElementChooserViewer<IRodinProject> projectChooser = patternGroup.getProjectChooser();

		IRodinProject element = projectChooser.getElement();
		variables.setInput(element);
		renamedVariables = new Renaming<IVariable>();
		events.setInput(element);
		renamedEvents = new Renaming<IEvent>();
		projectChanged(element);
		
	}

	private void initializeVariables () {
		IMachineRoot refMachine = refMachineChooser.getElement();
		if (refMachine != null) {
			renamedVariables = new Renaming<IVariable>();
			Matching<IVariable>[] variableMatchings = matchingPage.getMatching().getChildrenOfType(IVariable.ELEMENT_TYPE);
			try {
				for (IVariable var : refMachine.getVariables()) {
					Matching<IVariable> match = PatternUtils.getMatching(var.getIdentifierString(), null, variableMatchings);
					if (match != null)
						renamedVariables.addPair(match.getPatternID(), match.getProblemID());
					else
						renamedVariables.addPair(var.getIdentifierString(), var.getIdentifierString());
				}
			} catch (RodinDBException e) {}
		}
	}

	private void initializeEvents () {
		IMachineRoot refMachine = refMachineChooser.getElement();
		if (refMachine != null) {
			renamedEvents = new Renaming<IEvent>();
			try {
				Set<String> refEvents = new HashSet<String>();
				for (IEvent evt : refMachine.getEvents())
					if (!evt.isInitialisation())
						refEvents.add(evt.getLabel());
				for(Matching<IEvent> match : matchingPage.getMatching().getChildrenOfType(IEvent.ELEMENT_TYPE)) {
					for (IEvent evt : PatternUtils.getRefinementEvents(match.getPatternElement(), refMachine)) {
						renamedEvents.addPair(evt.getLabel(), match.getProblemID(), match.getProblemID());
						refEvents.remove(evt.getLabel());
					}
				}
				for (String evt : refEvents)
					renamedEvents.addPair(evt, "", evt);
			}catch (RodinDBException e) {}
		}
	}
		
	private String checkRenaming() {
		try {
			// check for duplicate variable renaming
			List<String> renamings = renamedVariables.getRenameList();
			int size = renamings.size();
			String element;
			for (int i=0 ; i<size; i++) {
				element = renamings.get(i);
				for (int j=i+1; j<size; j++)
					if (element.equals(renamings.get(j)))
						return "Duplicate variable renaming: " + element;
			}
				
			// check for variable renaming that already exists in problem machine
			Set<String> notMatchedVariables = new HashSet<String>();
			for (IVariable var : matchingPage.getMatching().getProblemElement().getVariables())
				if (!PatternUtils.isInMatchings(null, var, matchingPage.getMatching().getChildrenOfType(IVariable.ELEMENT_TYPE)))
					notMatchedVariables.add(var.getIdentifierString());
			for (String renaming : renamedVariables.getRenameList())
				if (notMatchedVariables.contains(renaming))
					return "Variable " + renaming + " already exists in problem machine";
			
			// check for duplicate event renaming
			renamings = renamedEvents.getRenameList();
			size = renamings.size();
			for (int i=0 ; i<size; i++) {
				element = renamings.get(i);
				for (int j=i+1; j<size; j++)
					if (element.equals(renamings.get(j)))
						return "Duplicate event renaming: " + element;
			}
			
			// check for event renaming that already exists in problem machine
			Set<String> notMatchedEvents = new HashSet<String>();
			for (IEvent evt : matchingPage.getMatching().getProblemElement().getEvents())
				if (!PatternUtils.isInMatchings(null, evt, matchingPage.getMatching().getChildrenOfType(IEvent.ELEMENT_TYPE)))
					notMatchedEvents.add(evt.getLabel());
			for (String renaming : renamedEvents.getRenameList())
				if (notMatchedEvents.contains(renaming))
					return "Event " + renaming + " already exists in problem machine";
			
		} catch (RodinDBException e) {
		}
		return null;
			
	}

	private void updateStatus(String message) {
		if (message == null)
			message = checkRenaming();
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	public Renaming<IVariable> getRenamingVariables() {
		return renamedVariables;
	}

	public IMachineRoot getPatternRefinmentMachine() {
		return refMachineChooser.getElement();
	}

	public Renaming<IEvent> getRenamingEvents() {
		return renamedEvents;
	}
	
	public ElementChooserViewer<IMachineRoot> getRefinementChooser() {
		return refMachineChooser;
	}

}