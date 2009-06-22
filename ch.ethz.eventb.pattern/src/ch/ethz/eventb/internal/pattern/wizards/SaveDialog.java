package ch.ethz.eventb.internal.pattern.wizards;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eventb.core.IAction;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IParameter;
import org.eventb.core.IVariable;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

import ch.ethz.eventb.pattern.core.IActionMatching;
import ch.ethz.eventb.pattern.core.ICarrierSetMatching;
import ch.ethz.eventb.pattern.core.IConstantMatching;
import ch.ethz.eventb.pattern.core.IEventMatching;
import ch.ethz.eventb.pattern.core.IGuardMatching;
import ch.ethz.eventb.pattern.core.IParameterMatching;
import ch.ethz.eventb.pattern.core.IPatternRoot;
import ch.ethz.eventb.pattern.core.IVariableMatching;

public class SaveDialog extends Dialog {
	
    /**
     * The title of the dialog.
     */
    private String title;
    private MatchingWizardPage matchingPage;
    private MatchingMachine matching;
    private Renaming<ICarrierSet> carrierSetRenaming;
    private Renaming<IConstant> constantRenaming;    
	private Text textField;
	private Label error;
	private IRodinProject project;
    
    Button OKbutton;

    public SaveDialog(Shell parentShell, String dialogTitle, MatchingWizardPage matchingPage) {
        super(parentShell);
        this.title = dialogTitle;
        this.matchingPage = matchingPage;
        matching = matchingPage.getMatching();
        carrierSetRenaming = matchingPage.getCarrierSetRenaming();
        constantRenaming = matchingPage.getConstantRenaming();
        project = matching.getProblemProject();
    }
    
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        if (title != null) {
			shell.setText(title);
		}
    }
    
    protected void createButtonsForButtonBar(Composite parent) {
        // create OK and Cancel buttons by default
        OKbutton = createButton(parent, IDialogConstants.OK_ID,
                IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID,
                IDialogConstants.CANCEL_LABEL, false);

    }
    
    protected Control createDialogArea(Composite parent) {
        // create composite
        Composite composite = (Composite) super.createDialogArea(parent);
        GridLayout layout = new GridLayout();
        composite.setLayout(layout);
		layout.numColumns = 1;
		layout.verticalSpacing = 9;
		
		// Label
		Label label = new Label(composite, SWT.NULL);
		label.setText("The matching will be saved in project " + matchingPage.getMatching().getProblemProject() + ".\n" +
				"Please enter a name for the new file:");
		
		// Text field
		textField = new Text(composite, SWT.BORDER | SWT.SINGLE);
		textField.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				checkFilename();
			}
			
		});
		GridData gd = new GridData(SWT.FILL, SWT.NONE, true, false);
		textField.setLayoutData(gd);
		error = new Label(composite, SWT.NULL);
		
		
		applyDialogFont(composite);
        
        return composite;
    }

    private void checkFilename() {
    	String text = textField.getText();
		if (text.equals("")){
			error.setText("Name must not be empty.");
			OKbutton.setEnabled(false);
		}
		else if (project != null) {
			IRodinFile file = project.getRodinFile(text + ".pat");
			if (file != null && file.exists()){
				error.setText("File name " + text + ".pat already exists.");
				OKbutton.setEnabled(false);
			}
			else{
				error.setText("");
				OKbutton.setEnabled(true);
			}
		}
		error.pack();
    }
    
	@Override
	protected void okPressed() {
		try {
			IRodinFile file = matching.getProblemElement().getRodinProject().getRodinFile(textField.getText() + ".pat");
			file.create(false, null);
			IPatternRoot root = (IPatternRoot)file.getRoot();
			root.setPatternMachine(matching.getPatternID());
			root.setProblemMachine(matching.getProblemID());
			root.setPatternProject(matching.getPatternElement().getRodinProject().getElementName());
			root.setProblemProject(matching.getProblemElement().getRodinProject().getElementName());
			
			Assert.isTrue(root instanceof IPatternRoot);
			int it = 1;
			ICarrierSetMatching carMatch;
			for (String rename : carrierSetRenaming.getSourceList()) {
				carMatch = root.getCarrierSetMatching("matching " + it++);
				carMatch.create(null, null);
				carMatch.setPatternCarrierSet(rename);		
				carMatch.setProblemCarrierSet(carrierSetRenaming.getRenamingOfElement(rename));
			}
			IConstantMatching conMatch;
			for (String rename : constantRenaming.getSourceList()) {
				conMatch = root.getConstantMatching("matching " + it++);
				conMatch.create(null, null);
				conMatch.setPatternConstant(rename);		
				conMatch.setProblemConstant(constantRenaming.getRenamingOfElement(rename));
			}
			IVariableMatching varMatch;
			for (Matching<IVariable> match : matching.getChildrenOfType(IVariable.ELEMENT_TYPE)){
				varMatch = root.getVariableMatching("matching " + it++);
				varMatch.create(null, null);
				varMatch.setPatternVariable(match.getPatternID());		
				varMatch.setProblemVariable(match.getProblemID());
			}
			IEventMatching evtMatch;
			IParameterMatching parMatch;
			IGuardMatching grdMatch;
			IActionMatching actMatch;
			for (ComplexMatching<IEvent> match : matching.getChildrenOfTypeEvent()){
				evtMatch = root.getEventMatching("matching " + it++);
				evtMatch.create(null, null);
				evtMatch.setPatternEvent(match.getPatternID());		
				evtMatch.setProblemEvent(match.getProblemID());
				for (Matching<IParameter> submatch : match.getChildrenOfType(IParameter.ELEMENT_TYPE)) {
					parMatch = evtMatch.getParameterMatching("matching " + it++);
					parMatch.create(null, null);
					parMatch.setPatternParameter(submatch.getPatternID());		
					parMatch.setProblemParameter(submatch.getProblemID());
				}
				for (Matching<IGuard> submatch : match.getChildrenOfType(IGuard.ELEMENT_TYPE)) {
					grdMatch = evtMatch.getGuardMatching("matching " + it++);
					grdMatch.create(null, null);
					grdMatch.setPatternGuard(submatch.getPatternID());		
					grdMatch.setProblemGuard(submatch.getProblemID());
				}
				for (Matching<IAction> submatch : match.getChildrenOfType(IAction.ELEMENT_TYPE)) {
					actMatch = evtMatch.getActionMatching("matching " + it++);
					actMatch.create(null, null);
					actMatch.setPatternAction(submatch.getPatternID());		
					actMatch.setProblemAction(submatch.getProblemID());
				}
			}
			
			file.save(null, true, false);
		}
		catch (RodinDBException e) {
			return;
		}


		
		
		
		super.okPressed();
	}

	
	
	@Override
	protected void initializeBounds() {
		textField.setText(matching.getProblemID());
		super.initializeBounds();
	}

    

}
