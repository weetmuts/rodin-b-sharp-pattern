package ch.ethz.eventb.internal.pattern.wizards;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eventb.core.IEvent;
import org.eventb.core.IEventBProject;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IParameter;
import org.eventb.core.IVariable;
import org.eventb.core.IWitness;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

import ch.ethz.eventb.internal.pattern.PatternUtils;


/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (mpe).
 */

public class IncorporatingWizardPage extends WizardPage {
	
	

	private MatchingWizardPage matchingPage;
	
	private RenamingWizardPage renamingPage;
	
	private Matching<IVariable>[] variableMatching;

	private Text textField;
	
	private TableViewer variables;
	
	private TableViewer invariants;
	
	private TableViewer witnesses;
	
	private MatchingGroup<IVariable> varGroup; 
	
	private Collection<IVariable> disappeard;
	
	private Collection<IWitness> allWitnesses;
	
	private Renaming<IVariable> variableExtraction;
	
	private Renaming<IWitness> witnessExtraction;
	
	private IEventBProject project;
	
	private Button generatePO;
	
	private boolean doGeneratePO;

	
	
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
			
			varGroup = (MatchingGroup<IVariable>)inputElement;
			IVariable[] refinementVariables = new IVariable[0];
			if (renamingPage.getPatternRefinmentMachine() != null) {
				try {
					refinementVariables = renamingPage.getPatternRefinmentMachine().getVariables();
				} catch (RodinDBException e) {
				}
			}
			Collection<IVariable> result = new ArrayList<IVariable>();
			if (varGroup != null) {
				for (Matching<IVariable> matching : varGroup.getMatchings()) 
					if (!PatternUtils.isInArray(matching.getPatternID(), refinementVariables))
						result.add(matching.getPatternElement());
			}
			disappeard = result;
			return result.toArray();
			}
	

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
		 */
		public void dispose() {

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse
		 * .jface.viewers.Viewer, java.lang.Object, java.lang.Object)
		 */
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

		}

	}
	
	/**
	 * @author fuersta
	 *         <p>
	 *         A utility class for providing contents for the table viewer.
	 *         </p>
	 */
	private class WitnessContentProvider implements IStructuredContentProvider {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.IStructuredContentProvider#getElements(
		 * java.lang.Object)
		 */
		public Object[] getElements(Object inputElement) {
			
			Assert.isTrue(inputElement instanceof IMachineRoot);
			Collection<IWitness> result = new ArrayList<IWitness>();
			for (ComplexMatching<IEvent> match : matchingPage.getEventGroup().getMatchings()) {
				try {
					for (IEvent refEvent : PatternUtils.getRefinementEvents(match.getPatternElement(), (IMachineRoot)inputElement)) {
						for (IWitness witness : refEvent.getWitnesses())
							if (!result.contains(witness))
								result.add(witness);
					}
				} catch (RodinDBException e) {}
			}
			
			allWitnesses = result;
			return result.toArray();
		}
		
	

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
		 */
		public void dispose() {
			// Reset the root node to null.
		
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
		
		}

	}
	
	/**
	 * @author fuersta
	 *         <p>
	 *         A utility class for providing contents for the table viewer.
	 *         </p>
	 */
	private class InvariantContentProvider implements IStructuredContentProvider {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.IStructuredContentProvider#getElements(
		 * java.lang.Object)
		 */
		public Object[] getElements(Object inputElement) {
			Assert.isTrue(inputElement instanceof IMachineRoot);
			try {
				return ((IMachineRoot)inputElement).getInvariants();
			} catch (RodinDBException e) {
			}
			return new Object[0];
		}


		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
		 */
		public void dispose() {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse
		 * .jface.viewers.Viewer, java.lang.Object, java.lang.Object)
		 */
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

	}

	
	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public IncorporatingWizardPage(MatchingWizardPage matchingPage, RenamingWizardPage renamingPage) {
		super("wizardPage");
		setTitle("Event-B Pattern. Step 4");
		setDescription("This step incorporate the pattern and the problem.");
		this.matchingPage = matchingPage;
		this.renamingPage = renamingPage;
		varGroup = matchingPage.getVariableGroup();
	}

	private String renameMatching(String predicate){
		
		for (Matching<IVariable> matching : variableMatching)
			predicate = predicate.replace(PatternUtils.getDisplayText(matching.getPatternElement()),PatternUtils.getDisplayText(matching.getProblemElement()));
			
		return predicate;
	}
	
	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout gl = new GridLayout();
		gl.numColumns = 2;
		gl.verticalSpacing = 9;
		container.setLayout(gl);
		
		
		// Label
		Label label = new Label(container, SWT.NULL);
		label.setText("New machine's name");
		
		// Text field
		textField = new Text(container, SWT.BORDER | SWT.SINGLE);
		textField.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				
				updateStatus(null);
			}
			
		});
		GridData gd = new GridData(SWT.FILL, SWT.NONE, true, false);
		textField.setLayoutData(gd);

		// Table viewer of the matching
		Group invariantGroup = new Group(container,SWT.NULL);
		invariantGroup.setText("Overview of the invariants");
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		invariantGroup.setLayoutData(gd);
		invariantGroup.setLayout(gl);				
		
		invariants = new TableViewer(invariantGroup, SWT.NULL);
		
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		invariants.getControl().setLayoutData(gd);
										
		invariants.setContentProvider(new InvariantContentProvider());
		
		
				
		renamingPage.getRefinementChooser().addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				invariants.setInput(renamingPage.getPatternRefinmentMachine());
				variables.setInput(matchingPage.getVariableGroup());
				witnesses.setInput(renamingPage.getPatternRefinmentMachine());
				variableExtraction = new Renaming<IVariable>();
				witnessExtraction = new Renaming<IWitness>();
				updateStatus(null);
				return;			
			}
		});
		
		TableViewerColumn inv = new TableViewerColumn(invariants,SWT.NONE);
		inv.getColumn().setWidth(100);
		inv.setLabelProvider(new CellLabelProvider(){
		    @Override
		    public void update(ViewerCell cell) {
		    		cell.setText(PatternUtils.getDisplayText(cell.getElement()));
		       
		    }

		});
	
		
		// Table viewer of the matching
		Group variableGroup = new Group(container,SWT.NULL);
		variableGroup.setText("Replacement for the disappeared variables");
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		variableGroup.setLayoutData(gd);
		variableGroup.setLayout(gl);
		
		variables = new TableViewer(variableGroup, SWT.NULL);
		
		variables.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
										
		variables.setContentProvider(new VariableContentProvider());
		
				
		final MatchingGroup<IVariable> varGroup = matchingPage.getVariableGroup();
		varGroup.getActionPerformer().addListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				variables.setInput(matchingPage.getVariableGroup());
				variableMatching = matchingPage.getMatching().getChildrenOfType(IVariable.ELEMENT_TYPE);
				project = matchingPage.getMatching().getProblemElement().getEventBProject();
				variableExtraction = new Renaming<IVariable>();
				updateStatus(null);
				return;
				
			}});
		
		matchingPage.getProblemGroup().getMachineChooser().addSelectionChangedListener(new ISelectionChangedListener(){

			public void selectionChanged(SelectionChangedEvent event) {
				textField.setText(matchingPage.getProblemGroup().getMachineChooser().getElement().getElementName());
				if(matchingPage.getMatching() != null)
					project = matchingPage.getMatching().getProblemElement().getEventBProject();
				updateStatus(null);
				return;
			}
		});

		
		TableViewerColumn originalVariables = new TableViewerColumn(variables,SWT.NONE);
		originalVariables.getColumn().setWidth(100);
		originalVariables.setLabelProvider(new CellLabelProvider(){
		    @Override
		    public void update(ViewerCell cell) {
		    	cell.setText(PatternUtils.getDisplayText(cell.getElement()));
		       
		    }

		});
		
		TableViewerColumn extractedVars = new TableViewerColumn(variables,SWT.NONE);
		extractedVars.getColumn().setWidth(100);
		extractedVars.setLabelProvider(new CellLabelProvider(){
		    @Override
		    public void update(ViewerCell cell) {
		    	cell.setText(variableExtraction.getRenamingOfElement((IVariable)cell.getElement()));
		    	updateStatus(null);
			}

		});
		
		extractedVars.setEditingSupport(new EditingSupport(variables) {

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
		        return variableExtraction.getRenamingOfElement((IVariable)element);
		    }

		    @Override
		    protected void setValue(Object element, Object value) {
		    	if (value.equals(""))
		    		variableExtraction.removePair((IVariable)element);
		    	else
		    		variableExtraction.addPair((IVariable)element, value.toString());
		    	variables.setInput(matchingPage.getVariableGroup());
		    }

		});
		
		
		// Table viewer of the matching
		Group witnessGroup = new Group(container,SWT.NULL);
		witnessGroup.setText("Replacement for the witnesses");
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		witnessGroup.setLayoutData(gd);
		witnessGroup.setLayout(gl);
		
		witnesses = new TableViewer(witnessGroup, SWT.NULL);
		
		witnesses.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
										
		witnesses.setContentProvider(new WitnessContentProvider());
		
				
		matchingPage.getActionPerformer().addListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				witnesses.setInput(renamingPage.getPatternRefinmentMachine());
				witnessExtraction = new Renaming<IWitness>();
				updateStatus(null);
				return;
			}
		
		
		});

		
		TableViewerColumn events = new TableViewerColumn(witnesses,SWT.NONE);
		events.getColumn().setWidth(100);
		events.setLabelProvider(new CellLabelProvider(){
		    @Override
		    public void update(ViewerCell cell) {
		    	cell.setText(PatternUtils.getDisplayText(((IWitness)cell.getElement()).getParent()));
		       
		    }

		});
		
		
		TableViewerColumn originalWitness = new TableViewerColumn(witnesses,SWT.NONE);
		originalWitness.getColumn().setWidth(100);
		originalWitness.setLabelProvider(new CellLabelProvider(){
		    @Override
		    public void update(ViewerCell cell) {
		    	try {
					cell.setText(PatternUtils.getDisplayText(((IWitness)cell.getElement()).getPredicateString()));
				} catch (RodinDBException e) {}
		       
		    }

		});
		
		TableViewerColumn witnessLabel = new TableViewerColumn(witnesses,SWT.NONE);
		witnessLabel.getColumn().setWidth(100);
		witnessLabel.setLabelProvider(new CellLabelProvider(){
		    @Override
		    public void update(ViewerCell cell) {
		    	try {
					cell.setText(((IWitness)cell.getElement()).getLabel() + " =");
				} catch (RodinDBException e) {}
		    }

		});
		
		TableViewerColumn extractedWitness = new TableViewerColumn(witnesses,SWT.NONE);
		extractedWitness.getColumn().setWidth(200);
		extractedWitness.setLabelProvider(new CellLabelProvider(){
		    @Override
		    public void update(ViewerCell cell) {
		    	cell.setText(witnessExtraction.getRenamingOfElement((IWitness)cell.getElement()));
		    	updateStatus(null);
			}

		});
		
		extractedWitness.setEditingSupport(new EditingSupport(witnesses) {

		    @Override
		    protected boolean canEdit(Object element) {
		     	return true;
		    }

		    @Override
		    protected CellEditor getCellEditor(Object element) {
		        return new TextCellEditor(witnesses.getTable());
		    }

		    @Override
		    protected Object getValue(Object element) {
		        return witnessExtraction.getRenamingOfElement((IWitness)element);
		    }

		    @Override
		    protected void setValue(Object element, Object value) {
		    	if (value.equals(""))
		    		witnessExtraction.removePair((IWitness)element);
		    	else
		    		witnessExtraction.addPair((IWitness)element, value.toString());
		    	witnesses.setInput(renamingPage.getPatternRefinmentMachine());
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

		
		// Create the problem machine chooser group.
		generatePO = new Button(group,SWT.CHECK);
		generatePO.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {
				doGeneratePO = generatePO.getSelection();
				dialogChanged();
			}

		});
			
		Label conftext = new Label(group,SWT.NONE);
		conftext.setText("generate Proof Obligations");
		
		
		
		
		initialize();
		dialogChanged();
		setControl(container);
	}

	/**
	 * Tests if the current workbench selection is a suitable container to use.
	 */

	private void initialize() {
		IMachineRoot problemMachine = matchingPage.getProblemGroup().getMachineChooser().getElement();
		if (problemMachine != null){
			textField.setText(problemMachine.getElementName());
			project = problemMachine.getEventBProject();
		}
		else
			textField.setText("");
		
		disappeard = new ArrayList<IVariable>();
		allWitnesses = new ArrayList<IWitness>();
		variableExtraction = new Renaming<IVariable>();
		witnessExtraction = new Renaming<IWitness>();
		if(matchingPage.getMatching() != null)
			variableMatching = matchingPage.getMatching().getChildrenOfType(IVariable.ELEMENT_TYPE);
		generatePO.setSelection(true);
	}

	/**
	 * Ensures that both text fields are set.
	 */

	private void dialogChanged() {
		String text = textField.getText();
		if (text.equals(""))
			updateStatus("Name must not be empty");
		updateStatus(null);
	}
	

	private void updateStatus(String message) {
		if (message == null)
			message = checkFilename();
		if (message == null)
			message = replacingComplete();
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	public String getMachineName() {
		return textField.getText();
	}
	
	public Renaming<IVariable> getVariableExtraction(){
		return variableExtraction;
	}
	
	public Renaming<IWitness> getWitnessExtraction(){
		return witnessExtraction;
	}
	
	private String replacingComplete() {
		for (IVariable variable : disappeard)
			if (variableExtraction.getRenamingOfElement(variable).equals(""))
					return "Not all replacements are set";
		for (IWitness witness : allWitnesses)
			if (witnessExtraction.getRenamingOfElement(witness).equals(""))
				return "Not all witnesses are set";
		return null;
	}
	
	private String checkFilename() {
		String text = textField.getText();
		if (text.equals(""))
			return "Name must not be empty";
		if (project != null) {
			IRodinFile file = project.getMachineFile(text);
			if (file != null && file.exists())
				return "File name " + text + " already exists";
			file = project.getContextFile(text);
			if (file != null && file.exists())
				return "File name " + text + " already exists";
			return null;
		}
		else
			return "No project defined yet";
		
	}
	
	public boolean generatePO() {
		return doGeneratePO;
	}

}