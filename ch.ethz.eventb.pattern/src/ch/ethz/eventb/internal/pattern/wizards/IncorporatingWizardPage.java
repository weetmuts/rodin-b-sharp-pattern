package ch.ethz.eventb.internal.pattern.wizards;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
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
import org.eclipse.swt.widgets.TreeItem;
import org.eventb.core.IEvent;
import org.eventb.core.IEventBProject;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IParameter;
import org.eventb.core.IVariable;
import org.eventb.core.IWitness;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.internal.core.RodinDB;
import org.rodinp.keyboard.RodinKeyboardPlugin;

import ch.ethz.eventb.internal.pattern.Data;
import ch.ethz.eventb.internal.pattern.DataException;
import ch.ethz.eventb.internal.pattern.EventBUtils;
import ch.ethz.eventb.internal.pattern.PatternUtils;


/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (mpe).
 */

public class IncorporatingWizardPage extends WizardPage {
	
	

	private MatchingWizardPage matchingPage;
	
	private MergingWizardPage mergingPage;
	
	private Matching<IVariable>[] variableMatching;

	private Text textField;
	
	private TreeViewer variables;
	
//	private TreeViewer variables2;
	
	private TableViewer invariants;
	
	private TableViewer witnesses;
	
	private MatchingGroup<IVariable> varGroup; 
	
	private Collection<IVariable> disappeard;
	
	private Collection<IVariable> related;
	
	private Collection<IWitness> allWitnesses;
	
	private Renaming<IVariable> variableExtraction;
	
//	private Renaming<IVariable> variableExtraction2;
	
	private Renaming<IWitness> witnessExtraction;
	
	private IEventBProject project;
	
	private Button generatePO;
	
	private boolean doGeneratePO;

	private Data data;
	
	private RodinKeyboardPlugin keyboard = RodinKeyboardPlugin.getDefault();
	
	
	private class VariableContentProvider implements ITreeContentProvider {

		public Object[] getChildren(Object parentElement) {
			try {
				return data.getForwardDependentVariables((IVariable) parentElement).toArray();
			} catch (DataException e) {
				return new Object[0];
			}
		}

		public Object getParent(Object element) {
			return null;
		}

		public boolean hasChildren(Object element) {
			try {
				return data.getForwardDependentVariables((IVariable) element).size()>0;
			} catch (DataException e) {
				return false;
			}
		}

		public Object[] getElements(Object inputElement) {
			
			try {
				
				disappeard = data.getDisappearingPatternVariablesAtLocation();
				return disappeard.toArray();
			}
			catch (DataException e) {
				return new Object[0];
			}
			
		}

		public void dispose() {
			varGroup = null;
			
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			varGroup = null;
			
		}
		
	}
	
	private class VariableContentProvider2 implements ITreeContentProvider {

		public Object[] getChildren(Object parentElement) {
			try {
				return data.getBackwardDependentVariables((IVariable) parentElement).toArray();
			} catch (DataException e) {
				return new Object[0];
			}
		}

		public Object getParent(Object element) {
			return null;
		}

		public boolean hasChildren(Object element) {
			try {
				return data.getBackwardDependentVariables((IVariable) element).size()>0;
			} catch (DataException e) {
				return false;
			}
		}

		public Object[] getElements(Object inputElement) {
			
			try {
				
				related = data.getRelatedPatternVariables();
				return related.toArray();
			}
			catch (DataException e) {
				return new Object[0];
			}
			
		}

		public void dispose() {
			varGroup = null;
			
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			varGroup = null;
			
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
			
//			Assert.isTrue(inputElement instanceof IMachineRoot);
//			Collection<IWitness> result = new ArrayList<IWitness>();
//			for (ComplexMatching<IEvent> match : matchingPage.getEventGroup().getMatchings()) {
//				try {
//					for (IEvent refEvent : PatternUtils.getRefinementChainOfEvent((IMachineRoot)inputElement, match.getPatternElement())) {
//						for (IWitness witness : refEvent.getWitnesses())
//							if (!result.contains(witness))
//								result.add(witness);
//					}
//				} catch (RodinDBException e) {}
//			}
//			
			try {
				allWitnesses = data.getRelevantWitnesses();
				return allWitnesses.toArray();
			}
			catch (DataException e) {
				return new Object[0];
			}
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
			
			Assert.isTrue(inputElement instanceof IVariable);
			
			IMachineRoot machine;
			Collection<IInvariant> result = new ArrayList<IInvariant>();
			
			try {
				machine = data.getMachineAfterDisappearingOf(((IVariable)inputElement).getIdentifierString());
				for (IInvariant invariant : machine.getInvariants())
					if (EventBUtils.isRelevant(invariant, (IVariable) inputElement))
						result.add(invariant);
			}
			catch (Exception e) {}
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
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public IncorporatingWizardPage(MatchingWizardPage matchingPage, MergingWizardPage mergingPage, Data data) {
		super("wizardPage");
		setTitle("Event-B Pattern. Step 5");
		setDescription("This step incorporate the pattern and the problem.");
		this.matchingPage = matchingPage;
		this.mergingPage = mergingPage;
		varGroup = matchingPage.getVariableGroup();
		this.data = data;
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
		
		
				
		mergingPage.getRefinementChooser().addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				variables.setInput(matchingPage.getVariableGroup());
//				variables2.setInput(matchingPage.getVariableGroup());
				witnesses.setInput(mergingPage.getPatternRefinmentMachine());
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
		
		variables = new TreeViewer(variableGroup, SWT.NULL);
		
		variables.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
										
		variables.setContentProvider(new VariableContentProvider());
		
		variables.setAutoExpandLevel(TreeViewer.ALL_LEVELS);
		
		variables.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				
				TreeItem[] sel = variables.getTree().getSelection();
				if (sel.length == 1)
					invariants.setInput(sel[0].getData());
			}
			
		});
		
				
		
		matchingPage.getProblemGroup().getMachineChooser().addSelectionChangedListener(new ISelectionChangedListener(){

			public void selectionChanged(SelectionChangedEvent event) {
				textField.setText(matchingPage.getProblemGroup().getMachineChooser().getElement().getElementName());
				if(matchingPage.getMatching() != null)
					project = matchingPage.getMatching().getProblemElement().getEventBProject();
				updateStatus(null);
				return;
			}
		});

		
		final TreeViewerColumn originalVariables = new TreeViewerColumn(variables,SWT.NONE);
		originalVariables.getColumn().setWidth(100);
		originalVariables.setLabelProvider(new CellLabelProvider(){
		    @Override
		    public void update(ViewerCell cell) {
		    	cell.setText(PatternUtils.getDisplayText(cell.getElement()));
		       
		    }

		});
		
		final TreeViewerColumn extractedVars = new TreeViewerColumn(variables,SWT.NONE);
		extractedVars.getColumn().setWidth(100);
		extractedVars.setLabelProvider(new CellLabelProvider(){
		    @Override
		    public void update(ViewerCell cell) {
		    	try {
		    		cell.setText(data.getForwardReplacementFor((IVariable)cell.getElement()).toString());
				} catch (Exception e) {
					cell.setText("error");
				}
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
		        return new TextCellEditor(variables.getTree());
		    }

		    @Override
		    protected Object getValue(Object element) {
		    	try {
		    		return data.getForwardReplacementFor((IVariable)element).toString();
				} catch (Exception e) {
					return "";
				}
		    }

		    @Override
		    protected void setValue(Object element, Object value) {
		    	String translateStr = keyboard.translate(value.toString());
				if (!value.toString().equals(translateStr))
					value = translateStr;
		    	if (value.equals(""))
		    		variableExtraction.removePair((IVariable)element);
		    	else 
		    		variableExtraction.addPair((IVariable)element, value.toString());
		    	try {
					data.updateForwardReplacementOf((IVariable)element, value.toString());
				} catch (Exception e) {
					MessageDialog.openError(getShell(), "Error", e.getMessage());
				}
		    	variables.setInput(matchingPage.getVariableGroup());
		    }

		});
		
		variableGroup.addControlListener(new ControlAdapter() {

			@Override
			public void controlResized(ControlEvent e) {
				int width = variables.getTree().getSize().x;
				originalVariables.getColumn().setWidth(width/2);
				extractedVars.getColumn().setWidth(width/2);
				super.controlResized(e);
			}
			
		});
		
		
//		// Table viewer of the matching
//		Group variableGroup2 = new Group(container,SWT.NULL);
//		variableGroup2.setText("Replacement for the new variables");
//		gd = new GridData(GridData.FILL_BOTH);
//		gd.horizontalSpan = 2;
//		variableGroup2.setLayoutData(gd);
//		variableGroup2.setLayout(gl);
//		
//		variables2 = new TreeViewer(variableGroup2, SWT.NULL);
//		
//		variables2.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
//										
//		variables2.setContentProvider(new VariableContentProvider2());
//		
//		variables2.setAutoExpandLevel(TreeViewer.ALL_LEVELS);
//		
//		final TreeViewerColumn originalVariables2 = new TreeViewerColumn(variables2,SWT.NONE);
//		originalVariables2.getColumn().setWidth(100);
//		originalVariables2.setLabelProvider(new CellLabelProvider(){
//		    @Override
//		    public void update(ViewerCell cell) {
//		    	cell.setText(PatternUtils.getDisplayText(cell.getElement()));
//		       
//		    }
//
//		});
//		
//		final TreeViewerColumn extractedVars2 = new TreeViewerColumn(variables2,SWT.NONE);
//		extractedVars2.getColumn().setWidth(100);
//		extractedVars2.setLabelProvider(new CellLabelProvider(){
//		    @Override
//		    public void update(ViewerCell cell) {
//		    	try {
//		    		cell.setText(data.getBackwardReplacementFor((IVariable)cell.getElement()).toString());
//				} catch (Exception e) {
//					cell.setText("error");
//				}
//		    	updateStatus(null);
//			}
//
//		});
//		
//		extractedVars2.setEditingSupport(new EditingSupport(variables2) {
//
//		    @Override
//		    protected boolean canEdit(Object element) {
//		     	return true;
//		    }
//
//		    @Override
//		    protected CellEditor getCellEditor(Object element) {
//		        return new TextCellEditor(variables2.getTree());
//		    }
//
//		    @Override
//		    protected Object getValue(Object element) {
//		    	try {
//		    		return data.getBackwardReplacementFor((IVariable)element).toString();
//				} catch (Exception e) {
//					return "";
//				}
//		    }
//
//		    @Override
//		    protected void setValue(Object element, Object value) {
//		    	String translateStr = keyboard.translate(value.toString());
//				if (!value.toString().equals(translateStr))
//					value = translateStr;
//		    	try {
//		    		data.updateBackwardReplacementOf((IVariable)element, value.toString());
//				} catch (Exception e) {
//					MessageDialog.openError(getShell(), "Error", e.getMessage());
//				}
//		    	variables2.setInput(matchingPage.getVariableGroup());
//		    }
//
//		});
//		
//		variableGroup2.addControlListener(new ControlAdapter() {
//
//			@Override
//			public void controlResized(ControlEvent e) {
//				int width = variables2.getTree().getSize().x;
//				originalVariables2.getColumn().setWidth(width/2);
//				extractedVars2.getColumn().setWidth(width/2);
//				super.controlResized(e);
//			}
//			
//		});
//		
//		final MatchingGroup<IVariable> varGroup = matchingPage.getVariableGroup();
//		varGroup.getActionPerformer().addListener(new ActionListener(){
//
//			public void actionPerformed(ActionEvent e) {
//				variables.setInput(matchingPage.getVariableGroup());
//				variables2.setInput(matchingPage.getVariableGroup());
//				variableMatching = matchingPage.getMatching().getChildrenOfType(IVariable.ELEMENT_TYPE);
//				project = matchingPage.getMatching().getProblemElement().getEventBProject();
//				variableExtraction = new Renaming<IVariable>();
//				updateStatus(null);
//				return;
//				
//			}});
//		
		
		
		// Table viewer of the matching
		Group witnessGroup = new Group(container,SWT.NULL);
		witnessGroup.setText("Typing for the witnesses");
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		witnessGroup.setLayoutData(gd);
		witnessGroup.setLayout(gl);
		
		witnesses = new TableViewer(witnessGroup, SWT.NULL);
		
		witnesses.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
										
		witnesses.setContentProvider(new WitnessContentProvider());
		
				
		matchingPage.getActionPerformer().addListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				witnesses.setInput(mergingPage.getPatternRefinmentMachine());
				witnessExtraction = new Renaming<IWitness>();
				updateStatus(null);
				return;
			}
		
		
		});

		
		final TableViewerColumn events = new TableViewerColumn(witnesses,SWT.NONE);
		events.getColumn().setWidth(100);
		events.setLabelProvider(new CellLabelProvider(){
		    @Override
		    public void update(ViewerCell cell) {
		    	cell.setText(PatternUtils.getDisplayText(((IWitness)cell.getElement()).getParent().getParent()) + "." +
		    			PatternUtils.getDisplayText(((IWitness)cell.getElement()).getParent()));
		       
		    }

		});
		
		
		final TableViewerColumn originalWitness = new TableViewerColumn(witnesses,SWT.NONE);
		originalWitness.getColumn().setWidth(100);
		originalWitness.setLabelProvider(new CellLabelProvider(){
		    @Override
		    public void update(ViewerCell cell) {
		    	try {
					cell.setText(PatternUtils.getDisplayText(((IWitness)cell.getElement()).getPredicateString()));
				} catch (RodinDBException e) {}
		       
		    }

		});
		
		final TableViewerColumn witnessLabel = new TableViewerColumn(witnesses,SWT.NONE);
		witnessLabel.getColumn().setWidth(100);
		witnessLabel.setLabelProvider(new CellLabelProvider(){
		    @Override
		    public void update(ViewerCell cell) {
		    	try {
					cell.setText(((IWitness)cell.getElement()).getLabel() + " =");
				} catch (RodinDBException e) {}
		    }

		});
		
		final TableViewerColumn extractedWitness = new TableViewerColumn(witnesses,SWT.NONE);
		extractedWitness.getColumn().setWidth(200);
		extractedWitness.setLabelProvider(new CellLabelProvider(){
		    @Override
		    public void update(ViewerCell cell) {
		    	try {
		    		cell.setText(data.getReplacementFor((IWitness)cell.getElement()).toString());
				} catch (Exception e) {
					cell.setText("error");
				}
		    	
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
		    	try {
		    		return data.getReplacementFor((IWitness)element).toString();
				} catch (Exception e) {
					return "";
				}
		    	
		        
//		        witnessExtraction.getRenamingOfElement((IWitness)element);
		    }

		    @Override
		    protected void setValue(Object element, Object value) {
		    	String translateStr = keyboard.translate(value.toString());
				if (!value.toString().equals(translateStr))
					value = translateStr;
		    	if (value.equals(""))
		    		witnessExtraction.removePair((IWitness)element);
		    	else
		    		witnessExtraction.addPair((IWitness)element, value.toString());
		    	try {
		    		data.updateReplacementOf((IWitness)element, value.toString());
				} catch (Exception e) {}
		    	witnesses.setInput(mergingPage.getPatternRefinmentMachine());
		    }

		});
		
		witnessGroup.addControlListener(new ControlAdapter() {

			@Override
			public void controlResized(ControlEvent e) {
				int width = witnesses.getTable().getSize().x;
				events.getColumn().setWidth(width/4);
				originalWitness.getColumn().setWidth(width/4);
				witnessLabel.getColumn().setWidth(width/4);
				extractedWitness.getColumn().setWidth(width/4);
				super.controlResized(e);
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
		doGeneratePO = true;
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