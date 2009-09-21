package ch.ethz.eventb.internal.pattern.wizards;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eventb.core.IAction;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IParameter;
import org.rodinp.core.RodinDBException;

import ch.ethz.eventb.internal.pattern.Data;

/**
 * A simple input dialog for soliciting an input string from the user.
 * <p>
 * This concrete dialog class can be instantiated as is, or further subclassed as
 * required.
 * </p>
 */
public class SubmatchingDialog extends Dialog {
    /**
     * The title of the dialog.
     */
    private String title;

    // Parameter matching group
    private MatchingGroup<IParameter> parameterGroup;
    
    // Guard matching group
	private MatchingGroup<IGuard> guardGroup;
	
	// Action matching group
	private MatchingGroup<IAction> actionGroup;
	
	// The matching between problem and pattern event	
	private ComplexMatching<IEvent> matching;
	
	private Data data;


    public SubmatchingDialog(Shell parentShell, String dialogTitle, ComplexMatching<IEvent> matching, Data data) {
        super(parentShell);
        this.title = dialogTitle;
        this.matching = matching;
        this.data = data;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        if (title != null) {
			shell.setText(title);
		}
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    protected void createButtonsForButtonBar(Composite parent) {
        // create OK and Cancel buttons by default
        createButton(parent, IDialogConstants.OK_ID,
                IDialogConstants.OK_LABEL, true);
        //createButton(parent, IDialogConstants.CANCEL_ID,
        //        IDialogConstants.CANCEL_LABEL, false);

    }

    /*
     * (non-Javadoc) Method declared on Dialog.
     */
    protected Control createDialogArea(Composite parent) {
        // create composite
        Composite composite = (Composite) super.createDialogArea(parent);
        GridLayout layout = new GridLayout();
        composite.setLayout(layout);
		layout.numColumns = 2;
		layout.verticalSpacing = 9;
		GridData gd;
			
		// Create the parameter matching group.
		parameterGroup = new MatchingGroup<IParameter>(composite, SWT.DEFAULT,
				IParameter.ELEMENT_TYPE, data);
		parameterGroup.getGroup().setText("Matching parameter");
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		parameterGroup.getGroup().setLayoutData(gd);
		boolean hasParameters;
		try {
			hasParameters = matching.getPatternElement().getParameters().length > 0;
		} catch (RodinDBException e) {
			hasParameters = true;
		}
		if (hasParameters) {
			parameterGroup.setInput(matching);
			parameterGroup.getProblemChooser().setInput(matching.getProblemElement());
			parameterGroup.getPatternChooser().setInput(matching.getPatternElement());
		} else
			parameterGroup.getGroup().setEnabled(false);
	
		// Create the variable matching group.
		guardGroup = new MatchingGroup<IGuard>(composite, SWT.DEFAULT,
				IGuard.ELEMENT_TYPE, data);
		guardGroup.getGroup().setText("Matching guard");
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		guardGroup.getGroup().setLayoutData(gd);
		boolean hasGuards;
		try {
			hasGuards = matching.getPatternElement().getGuards().length > 0;
		} catch (RodinDBException e) {
			hasGuards = true;
		}
		if (hasGuards) {
			guardGroup.setInput(matching);
			guardGroup.getProblemChooser().setInput(matching.getProblemElement());
			guardGroup.getPatternChooser().setInput(matching.getPatternElement());
		} else
			guardGroup.getGroup().setEnabled(false);
		
		// Create the event matching group
		actionGroup = new MatchingGroup<IAction>(composite, SWT.DEFAULT,
				IAction.ELEMENT_TYPE, data);
		actionGroup.getGroup().setText("Matching action");
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		actionGroup.getGroup().setLayoutData(gd);
		actionGroup.setInput(matching);
		actionGroup.getProblemChooser().setInput(matching.getProblemElement());
		actionGroup.getPatternChooser().setInput(matching.getPatternElement());
		
    		
  
     
        applyDialogFont(composite);
        return composite;
        
        
        
        
        

		
	
				
//		// Initialise the widgets
//		initialize();
//		
//		// Set the main control of the wizard.
//		setControl(container);
        
        
     
        
        
        
    }



	/**
	 * Returns the style bits that should be used for the input text field.
	 * Defaults to a single line entry. Subclasses may override.
	 * 
	 * @return the integer style bits that should be used when creating the
	 *         input text
	 * 
	 * @since 3.4
	 */
	protected int getInputTextStyle() {
		return SWT.SINGLE | SWT.BORDER;
	}
}
