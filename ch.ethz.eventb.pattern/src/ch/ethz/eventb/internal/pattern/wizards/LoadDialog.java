package ch.ethz.eventb.internal.pattern.wizards;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eventb.core.IAction;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IParameter;
import org.eventb.core.IVariable;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

import ch.ethz.eventb.pattern.core.IActionMatching;
import ch.ethz.eventb.pattern.core.ICarrierSetMatching;
import ch.ethz.eventb.pattern.core.IConstantMatching;
import ch.ethz.eventb.pattern.core.IEventMatching;
import ch.ethz.eventb.pattern.core.IGuardMatching;
import ch.ethz.eventb.pattern.core.IParameterMatching;
import ch.ethz.eventb.pattern.core.IPatternRoot;
import ch.ethz.eventb.pattern.core.IVariableMatching;

public class LoadDialog extends Dialog {
	
    /**
     * The title of the dialog.
     */
    private String title;
    private MatchingWizardPage matchingPage;
    MatchingChooserGroup chooser;
    
    Button OKbutton;

    public LoadDialog(Shell parentShell, String dialogTitle, MatchingWizardPage matchingPage) {
        super(parentShell);
        this.title = dialogTitle;
        this.matchingPage = matchingPage;
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
		OKbutton.setEnabled(!chooser.getMatchingChooser().getSelection().isEmpty());

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
		
		chooser = new MatchingChooserGroup(composite, SWT.DEFAULT);
		chooser.getProjectChooser().setInput(RodinCore.getRodinDB());
		
		chooser.getActionPerformer().addListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				OKbutton.setEnabled(!chooser.getMatchingChooser().getSelection().isEmpty());
			}
		});
		
        applyDialogFont(composite);
        
        return composite;
    }

    
    
	@Override
	protected void okPressed() {
		IPatternRoot root = chooser.getMatchingChooser().getElement();
		MatchingMachine newMatching = null;
		Renaming<ICarrierSet> carrierSetRenaming = null;
		Renaming<IConstant> constantRenaming = null;
		
		try {
			IRodinProject problemProject = RodinCore.getRodinDB().getRodinProject(root.getProblemProject());
			IRodinProject patternProject = RodinCore.getRodinDB().getRodinProject(root.getPatternProject());
			IMachineRoot problemMachine = (IMachineRoot)problemProject.getRodinFile(root.getProblemMachine()+".bum").getRoot();
			IMachineRoot patternMachine = (IMachineRoot)patternProject.getRodinFile(root.getPatternMachine()+".bum").getRoot();
			newMatching = new MatchingMachine(problemMachine, patternMachine);
		
			for (IVariableMatching varMatch : root.getVariableMatchings())
				newMatching.addMatching(varMatch.getPatternVariable(), varMatch.getProblemVariable(), IVariable.ELEMENT_TYPE, false);
			for (IEventMatching evtMatch : root.getEventMatchings()) {
				ComplexMatching<IEvent> complex = newMatching.addComplexMatching(evtMatch.getPatternEvent(), evtMatch.getProblemEvent(), IEvent.ELEMENT_TYPE, true);
				for (IParameterMatching parMatch : evtMatch.getParameterMatchings())
					complex.addMatching(parMatch.getPatternParameter(), parMatch.getProblemParameter(), IParameter.ELEMENT_TYPE, false);
				for (IGuardMatching grdMatch : evtMatch.getGuardMatchings())
					complex.addMatching(grdMatch.getPatternGuard(), grdMatch.getProblemGuard(), IGuard.ELEMENT_TYPE, true);
				for (IActionMatching actMatch : evtMatch.getActionMatchings())
					complex.addMatching(actMatch.getPatternAction(), actMatch.getProblemAction(), IAction.ELEMENT_TYPE, true);
			}
			carrierSetRenaming = new Renaming<ICarrierSet>();
			for (ICarrierSetMatching carMatch : root.getCarrierSetMatchings())
				carrierSetRenaming.addPair(carMatch.getPatternCarrierSet(), carMatch.getProblemCarrierSet());
			constantRenaming = new Renaming<IConstant>();
			for (IConstantMatching conMatch : root.getConstantMatchings())
				constantRenaming.addPair(conMatch.getPatternConstant(), conMatch.getProblemConstant());
		} catch (RodinDBException e) {
			return;
		}
			
		matchingPage.loadMatchingMachine(newMatching, carrierSetRenaming, constantRenaming);
		
		
		super.okPressed();
	}
    
    

}
