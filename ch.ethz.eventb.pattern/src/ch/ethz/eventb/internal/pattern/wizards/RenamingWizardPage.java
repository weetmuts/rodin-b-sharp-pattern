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
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eventb.core.IEvent;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IParameter;
import org.eventb.core.IVariable;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

import ch.ethz.eventb.internal.pattern.Data;
import ch.ethz.eventb.internal.pattern.Pair;
import ch.ethz.eventb.internal.pattern.PatternUtils;




/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (mpe).
 */

public class RenamingWizardPage extends WizardPage {

	private MatchingWizardPage matchingPage;
	
	private MergingWizardPage mergingPage;

	private IMachineRoot root;
	
	private TableViewer variables;
	
	private TableViewer events;
	
	private Renaming<IVariable> renamedVariables;
	
	private Renaming<IEvent> renamedEvents;
	
	private Data data;
	
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
					for (IEvent evt : PatternUtils.getRefinementsOfEvent(root, match.getPatternElement())) {
						result.add(new Pair<String, String>(evt.getLabel(),match.getProblemID()));
						refEvents.remove(evt.getLabel());
					}
				}
				Collection<IEvent> problemEvents = new ArrayList<IEvent>();
				for (Matching<IEvent> match : mergingPage.getMerging().getChildrenOfType(IEvent.ELEMENT_TYPE)) {
					IEvent problemEvent = match.getProblemElement();
					if (!problemEvents.contains(problemEvent))
						problemEvents.add(problemEvent);
				}
				for (IEvent problemEvent : problemEvents) {
					Collection<IEvent> patternEvents = new ArrayList<IEvent>();
					for (Matching<IEvent> match : mergingPage.getMerging().getChildrenOfType(IEvent.ELEMENT_TYPE)) {
						if (match.getProblemElement().equals(problemEvent)) {
							IEvent patternEvent = match.getPatternElement();
							refEvents.remove(patternEvent.getLabel());
							if (!patternEvents.contains(patternEvent))
								patternEvents.add(patternEvent);
						}
					}
					String pattern = "{\n";
					for (IEvent patternEvent : patternEvents) 
						pattern += patternEvent.getLabel()+"\n";
					pattern += "}";
					result.add(new Pair<String, String>(problemEvent.getLabel(), pattern));
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
	public RenamingWizardPage(MatchingWizardPage matchingPage, MergingWizardPage mergingPage, Data data) {
		super("wizardPage");
		setTitle("Event-B Pattern. Step 4");
		setDescription("This step is for developers to choose the renaming before incorporating the pattern.");
		this.matchingPage = matchingPage;
		this.mergingPage = mergingPage;
		this.data = data;
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout gl = new GridLayout();
		gl.numColumns = 2;
		container.setLayout(gl);
	
		
		// Table viewer of the matching
		Group variableGroup = new Group(container,SWT.NULL);
		variableGroup.setText("Renaming of the variables");
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		variableGroup.setLayoutData(gd);
		variableGroup.setLayout(gl);
		
		variables = new TableViewer(variableGroup, SWT.NONE);
		
		variables.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		
								
		variables.setContentProvider(new VariableContentProvider());
		
		final TableViewerColumn originalVariables = new TableViewerColumn(variables,SWT.NONE);
		originalVariables.getColumn().setWidth(200);
		originalVariables.setLabelProvider(new CellLabelProvider(){
		    @Override
		    public void update(ViewerCell cell) {
		    	cell.setText(PatternUtils.getDisplayText(cell.getElement()));
		       
		    }

		});
		
		final TableViewerColumn renamedVars = new TableViewerColumn(variables,SWT.NONE);
		renamedVars.getColumn().setWidth(200);
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
		    		try {
						data.updateRenaming((IVariable)element, value.toString());
					} catch (Exception e) {}
		    		updateStatus(null);
		    	}
		    }

		});
		
		
		variableGroup.addControlListener(new ControlAdapter() {

			@Override
			public void controlResized(ControlEvent e) {
				int width = variables.getTable().getSize().x;
				originalVariables.getColumn().setWidth(width/2);
				renamedVars.getColumn().setWidth(width/2);
				super.controlResized(e);
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

		final TableViewerColumn originalEvents = new TableViewerColumn(events,SWT.NONE);
		originalEvents.getColumn().setWidth(200);
		originalEvents.setLabelProvider(new CellLabelProvider(){
		    @Override
		    public void update(ViewerCell cell) {
		    	String label = ((Pair<String,String>)cell.getElement()).getFirst();
		    	String matched = ((Pair<String,String>)cell.getElement()).getSecond();
		    	if (!matched.equals("")) {
		    		if (matched.charAt(0) == '{')
		    			cell.setText(label+matched);
		    		else
		    			cell.setText(label+" ("+matched+")");
		    	}
		    	else
		    		cell.setText(label);
		    }

		});
		
		final TableViewerColumn renamedEvts = new TableViewerColumn(events,SWT.NONE);
		renamedEvts.getColumn().setWidth(200);
		renamedEvts.setLabelProvider(new CellLabelProvider(){
		    @Override
		    public void update(ViewerCell cell) {
		    	String first = ((Pair<String,String>)cell.getElement()).getFirst();
		    	String second = ((Pair<String,String>)cell.getElement()).getSecond();
		    	if (!second.equals("") && second.charAt(0) == '{')
		    		cell.setText(renamedEvents.getRenamingOfElement(first,""));
		    	else
		    		cell.setText(renamedEvents.getRenamingOfElement(first, second));
		    			
		    	
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
		    	String first = ((Pair<String,String>)element).getFirst();
		    	String second = ((Pair<String,String>)element).getSecond();
		    	if (!second.equals("") && second.charAt(0) == '{')
		    		return renamedEvents.getRenamingOfElement(first, "");
		    	else
		    		return renamedEvents.getRenamingOfElement(first, second);
		        
		    }

		    @Override
		    protected void setValue(Object element, Object value) {
		    	if (!value.toString().equals("")){
		    		String first = ((Pair<String,String>)element).getFirst();
		    		String second = ((Pair<String,String>)element).getSecond();
			    	if (!second.equals("")) {
			    		if (second.charAt(0) == '{'){
			    			renamedEvents.addPair(first, "", value.toString());
			    			try {
				    			IEvent problemEvent = PatternUtils.getElementByLabel(IEvent.ELEMENT_TYPE, first, data.getProblemMachine());
				    			assert problemEvent.exists();
			    				data.updateRenamingOfMergedEvent(problemEvent, value.toString());
							} catch (Exception e) {}
			    		}
			    		else {
			    			renamedEvents.addPair(first, second, value.toString());
			    			try {
				    			IEvent patternRefinementEvent = PatternUtils.getElementByLabel(IEvent.ELEMENT_TYPE, first, data.getPatternRefinementMachine());
				    			IEvent problemEvent = PatternUtils.getElementByLabel(IEvent.ELEMENT_TYPE, second, data.getProblemMachine());
				    			assert patternRefinementEvent.exists() && problemEvent.exists();
			    				data.updateRenamingOfMatchedEvent(patternRefinementEvent, problemEvent, value.toString());
							} catch (Exception e) {}
			    		}
			    	}
			    	else {
			    		renamedEvents.addPair(first, second, value.toString());
			    		try {
				    		IEvent patternRefinementEvent = PatternUtils.getElementByLabel(IEvent.ELEMENT_TYPE, first, data.getPatternRefinementMachine());
				    		assert patternRefinementEvent.exists();
			    			data.updateRenamingOfNewEvent(patternRefinementEvent, value.toString());
						} catch (Exception e) {}
			    	}
			    	events.setInput(root);
			    	updateStatus(null);
		    	}
		    		
		    	
		    }

		});
		
		eventGroup.addControlListener(new ControlAdapter() {

			@Override
			public void controlResized(ControlEvent e) {
				int width = events.getTable().getSize().x;
				originalEvents.getColumn().setWidth(width/2);
				renamedEvts.getColumn().setWidth(width/2);
				super.controlResized(e);
			}
			
		});
		
		
		matchingPage.getEventGroup().getActionPerformer().addListener( new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				initializeEvents();
				events.setInput(root);
				updateStatus(null);
			}
			
		});
		
		mergingPage.getRefinementChooser().addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				initializeVariables();
				initializeEvents();
				variables.setInput(mergingPage.getPatternRefinmentMachine());
				events.setInput(mergingPage.getPatternRefinmentMachine());
				updateStatus(null);;			
			}
		});
		
		mergingPage.getEventGroup().getActionPerformer().addListener( new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				initializeEvents();
				events.setInput(root);
				updateStatus(null);
			}
			
		});
		
		initialize();
		setControl(container);
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
	}

	private void initializeVariables () {
		IMachineRoot refMachine = mergingPage.getPatternRefinmentMachine();
		if (refMachine != null) {
			renamedVariables = new Renaming<IVariable>();
			Matching<IVariable>[] variableMatchings = matchingPage.getMatching().getChildrenOfType(IVariable.ELEMENT_TYPE);
			try {
				for (IVariable var : refMachine.getVariables()) {
					Matching<IVariable> match = PatternUtils.getMatching(var.getIdentifierString(), null, variableMatchings);
					if (match != null) {
						renamedVariables.addPair(match.getPatternID(), match.getProblemID());
						data.updateRenaming(var, match.getProblemID());
					} 
					else
						renamedVariables.addPair(var.getIdentifierString(), var.getIdentifierString());
				}
			} catch (Exception e) {}
		}
	}

	private void initializeEvents () {
		IMachineRoot refMachine = mergingPage.getPatternRefinmentMachine();
		if (refMachine != null) {
			renamedEvents = new Renaming<IEvent>();
			try {
				Set<String> refEvents = new HashSet<String>();
				for (IEvent evt : refMachine.getEvents())
					if (!evt.isInitialisation())
						refEvents.add(evt.getLabel());
				for(Matching<IEvent> match : matchingPage.getMatching().getChildrenOfType(IEvent.ELEMENT_TYPE)) {
					for (IEvent evt : PatternUtils.getRefinementsOfEvent(refMachine, match.getPatternElement())) {
						renamedEvents.addPair(evt.getLabel(), match.getProblemID(), match.getProblemID());
						refEvents.remove(evt.getLabel());
					}
				}
				Collection<IEvent> problemEvents = new ArrayList<IEvent>();
				for (Matching<IEvent> match : mergingPage.getMerging().getChildrenOfType(IEvent.ELEMENT_TYPE)) {
					refEvents.remove(match.getPatternID());
					IEvent problemEvent = match.getProblemElement();
					if (!problemEvents.contains(problemEvent)){
						problemEvents.add(problemEvent);
						renamedEvents.addPair(problemEvent.getLabel(),"",problemEvent.getLabel());
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
			MatchingMachine matching = matchingPage.getMatching();
			if (matching != null) {
				// collect all names of not matched variables
				for (IVariable var : matching.getProblemElement().getVariables())
					if (!PatternUtils.isInMatchings(null, var, matching.getChildrenOfType(IVariable.ELEMENT_TYPE)))
						notMatchedVariables.add(var.getIdentifierString());
				// check if renaming contains a collected name
				for (String renaming : renamedVariables.getRenameList())
					if (notMatchedVariables.contains(renaming))
						return "Variable " + renaming + " already exists in problem machine";
				
				Set<String> parameters = new HashSet<String>();
				// collect all names of parameters
				for (IEvent evt : matching.getProblemElement().getEvents())
					for (IParameter par : evt.getParameters())
						parameters.add(par.getIdentifierString());
				// check if renaming contains a collected name
				for (String renaming : renamedVariables.getRenameList())
					if (parameters.contains(renaming))
						return "Parameter " + renaming + " already exists in a problem event";
				
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
				List<String> source = renamedEvents.getSourceList();
				List<String> renaming = renamedEvents.getRenameList();
				for (int i = 1; i< renaming.size(); i++){
					String rename = renaming.get(i);
					if (notMatchedEvents.contains(rename) && !source.get(i).equals(rename))
						return "Event " + rename + " already exists in problem machine";
				}
			}
			
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

	
	public Renaming<IEvent> getRenamingEvents() {
		return renamedEvents;
	}
	
	
}