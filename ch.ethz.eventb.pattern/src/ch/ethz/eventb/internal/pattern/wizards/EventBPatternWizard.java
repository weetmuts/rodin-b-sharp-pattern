package ch.ethz.eventb.internal.pattern.wizards;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eventb.core.IMachineRoot;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

import ch.ethz.eventb.internal.pattern.Data;
import ch.ethz.eventb.internal.pattern.MachineGenerator;
import ch.ethz.eventb.internal.pattern.PatternUtils;




/**
 * This is a sample new wizard. Its role is to create a new file 
 * resource in the provided container. If the container resource
 * (a folder or a project) is selected in the workspace 
 * when the wizard is opened, it will accept it as the target
 * container. The wizard creates one file with the extension
 * "mpe". If a sample multi-page editor (also available
 * as a template) is registered for the same extension, it will
 * be able to open it.
 */

public class EventBPatternWizard extends Wizard implements INewWizard {
	private MatchingWizardPage matchingPage;
	
	private SyntaxCheckingWizardPage syntaxCheckingPage;
	
	private MergingWizardPage mergingPage;
	
	private RenamingWizardPage renamingPage;
	
	private IncorporatingWizardPage incorporatingPage;
	
	private ISelection selection;
	
	private Collection<IRodinFile> openFiles;
		
	private Data data;
	
	private IMachineRoot generatedMachine;
	
	/**
	 * Constructor for EventBPatternWizard.
	 */
	public EventBPatternWizard() {
		super();
		setNeedsProgressMonitor(true);
		
			
		openFiles = new ArrayList<IRodinFile>();
		
		data = new Data();
	}
	
	/**
	 * Adding the pages to the wizard.
	 */
	public void addPages() {
		matchingPage = new MatchingWizardPage(selection, openFiles, data);
		addPage(matchingPage);
		syntaxCheckingPage = new SyntaxCheckingWizardPage(matchingPage);
		addPage(syntaxCheckingPage);
		mergingPage = new MergingWizardPage(matchingPage, openFiles, data);
		addPage(mergingPage);		
		renamingPage = new RenamingWizardPage(matchingPage, mergingPage, data);
		addPage(renamingPage);
		incorporatingPage = new IncorporatingWizardPage(matchingPage, mergingPage, data);
		addPage(incorporatingPage);
	}

	 
	/**
	 * This method is called when 'Finish' button is pressed in
	 * the wizard. We will create an operation and run it
	 * using wizard as execution context.
	 */
	public boolean performFinish() {
		final String machineRefName = incorporatingPage.getMachineName();
		final boolean generatePOs = incorporatingPage.generatePO();
		final boolean copyInvariants = incorporatingPage.copyInvariants();
		
		final MachineGenerator generator = new MachineGenerator(data);
		
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException {
				try {
					if (copyInvariants)
						generator.doCopyInvariants();
					generatedMachine = generator.generateMachine(machineRefName, generatePOs, monitor);
					monitor.subTask("Opening file for editing...");
					getShell().getDisplay().asyncExec(new Runnable() {
						public void run() {
							PatternUtils.openWithDefaultEditor(generatedMachine);
						}
					});
				} catch (Exception e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.subTask("Cleanup");
					
					for (IRodinFile file : openFiles) {
						try {
							if (file != null && !file.isConsistent())
								file.revert();
						}
						catch (RodinDBException e) {}
					}
					openFiles.clear();
					monitor.worked(1);
					monitor.done();	


				}
			}
		};
		try {
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException.getMessage());
			return false;
		}
		return true;
	}
	
	
	
	@Override
	public boolean performCancel() {
			
		for (IRodinFile file : openFiles) {
			try {
				if (file != null && !file.isConsistent())
					file.revert();
			}
			catch (RodinDBException e) {}
		}
		openFiles.clear();
		return super.performCancel();
	}

			
	/**
	 * We will accept the selection in the workbench to see if
	 * we can initialize from it.
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}
	
	
}