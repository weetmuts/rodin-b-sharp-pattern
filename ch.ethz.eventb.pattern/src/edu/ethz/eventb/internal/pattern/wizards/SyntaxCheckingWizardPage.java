package edu.ethz.eventb.internal.pattern.wizards;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eventb.core.IAction;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IParameter;
import org.eventb.core.ISCAction;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCGuard;
import org.eventb.core.ISCParameter;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.IEventBSharedImages;


/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (mpe).
 */

public class SyntaxCheckingWizardPage extends WizardPage {

	private MatchingWizardPage matchingPage;
	
	private TreeViewer viewer;
	
	private Button confirmation;
	
	private IComplexMatching<IEvent> root;
	
	
	/**
	 * @author fuersta
	 *         <p>
	 *         A utility class for providing contents for the table viewer.
	 *         </p>
	 */
	private class ContentProvider implements ITreeContentProvider {


		public Object[] getElements(Object inputElement) {
		// The input element will be the root of the viewer and must be a
		// complex matching, i.e. having sub-matchings.
		Assert.isLegal(inputElement instanceof ComplexMatching<?>,
				"input must be a complex matching");
		// Stored the root element and return its children.
		Collection<Matching<IEvent>> result = new ArrayList<Matching<IEvent>>();
		root = (IComplexMatching<IEvent>) inputElement;
		for (Matching<IEvent> matching : root.getChildrenOfType(IEvent.ELEMENT_TYPE)) {
			result.add((Matching<IEvent>)matching);
		}
			return result.toArray();
		}

		public void dispose() {
			// Reset the root node to null.
			root = null;
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// Reset the root node to null.
			root = null;
			
		}

		public Object[] getChildren(Object parentElement) {
			
			Collection<Object> result = new ArrayList<Object>();
			if (parentElement instanceof ComplexMatching<?>){
				for (Matching<IParameter> matching : ((ComplexMatching<IEvent>) parentElement).getChildrenOfType(IParameter.ELEMENT_TYPE)) {
					result.add((Matching<IParameter>)matching);
				}
				for (Matching<IGuard> matching : ((ComplexMatching<IEvent>) parentElement).getChildrenOfType(IGuard.ELEMENT_TYPE)) {
					result.add((Matching<IGuard>)matching);
				}
				for (Matching<IAction> matching : ((ComplexMatching<IEvent>) parentElement).getChildrenOfType(IAction.ELEMENT_TYPE)) {
					result.add((Matching<IAction>)matching);
				}
			}
			return result.toArray();
						
		}

		public Object getParent(Object element) {
			return null;
		}

		public boolean hasChildren(Object element) {
			if(element instanceof ComplexMatching<?>)
				return true;
			return false;
		}



	}

	/**
	 * @author fuersta
	 *         <p>
	 *         A utility class for providing label for the table viewer.
	 *         </p>
	 */
	private class LabelProvider implements ILabelProvider {

		public Image getImage(Object element) {
			if (element instanceof Matching && ((Matching)element).getType().equals(IGuard.ELEMENT_TYPE))
				return EventBUIPlugin.getDefault().getImageRegistry().get(IEventBSharedImages.IMG_VARIABLE);
			else if (element instanceof Matching && ((Matching)element).getType().equals(IParameter.ELEMENT_TYPE))
				return EventBUIPlugin.getDefault().getImageRegistry().get(IEventBSharedImages.IMG_CARRIER_SET);
			else if (element instanceof Matching && ((Matching)element).getType().equals(IAction.ELEMENT_TYPE))
				return EventBUIPlugin.getDefault().getImageRegistry().get(IEventBSharedImages.IMG_AXIOM);
			else if (element instanceof ComplexMatching && ((ComplexMatching)element).getType().equals(IEvent.ELEMENT_TYPE))
				return EventBUIPlugin.getDefault().getImageRegistry().get(IEventBSharedImages.IMG_EVENT);
			else
			
			
				return null;
		}

		public String getText(Object element) {
			// Element must be a matching
			//Assert.isLegal(element instanceof IMatching<?>,
			//		"Element should be a matching");
			// Return the pretty-print of the matching
			return element.toString();
		}

		public void addListener(ILabelProviderListener listener) {
			// Do nothing
		}

		public void dispose() {
			// Do nothing
		}

		public boolean isLabelProperty(Object element, String property) {
			// Ignore by returning false
			return false;
		}

		public void removeListener(ILabelProviderListener listener) {
			// Do nothing
		}



	}
	
	
	
	
	
	

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public SyntaxCheckingWizardPage(MatchingWizardPage matchingPage) {
		super("wizardPage");
		setTitle("Event-B Pattern. Step 2");
		setDescription("This step is to verify the input in the matching step.");
		this.matchingPage = matchingPage;
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		// Create the main composite
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;

		
				
		// Table viewer of the matching
		viewer = new TreeViewer(container, SWT.BORDER);
		viewer.setContentProvider(new ContentProvider());
		viewer.setLabelProvider(new LabelProvider());
		viewer.setAutoExpandLevel(AbstractTreeViewer.ALL_LEVELS);
		
		// Create the main composite
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		viewer.getControl().setLayoutData(gd);
		
		Group group = new Group(container, SWT.DEFAULT);
		GridLayout gl = new GridLayout();
		gl.numColumns = 2;
		gl.verticalSpacing = 9;
		group.setLayout(gl);
		group.setText("Confirmation");
		
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		group.setLayoutData(gd);
		
		// Create the problem machine chooser group.
		confirmation = new Button(group,SWT.CHECK);
		confirmation.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {
				dialogChanged();
			}

		});
		
		gd = new GridData(GridData.BEGINNING);
		confirmation.setLayoutData(gd);
		
		
		matchingPage.getActionPerformer().addListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				confirmation.setSelection(false);
				updateStatus("The guard and action matching has to be confirmed");
				viewer.setInput(matchingPage.getMatching());				
			}
			
		});
		
			
		
		Label conftext = new Label(group,SWT.NONE);
		conftext.setText("The above stated guard and action matchings are syntactically the same");
		
		
		initialize();
		dialogChanged();
		setControl(container);
	}

	/**
	 * Tests if the current workbench selection is a suitable container to use.
	 */

	private void initialize() {
		
		viewer.setInput(matchingPage.getMatching());
				
//		if (selection != null && selection.isEmpty() == false
//				&& selection instanceof IStructuredSelection) {
//			IStructuredSelection ssel = (IStructuredSelection) selection;
//			if (ssel.size() > 1)
//				return;
//			Object obj = ssel.getFirstElement();
//			if (obj instanceof IResource) {
//				IContainer container;
//				if (obj instanceof IContainer)
//					container = (IContainer) obj;
//				else
//					container = ((IResource) obj).getParent();
////				containerText.setText(container.getFullPath().toString());
//			}
//		}
////		fileText.setText("new_file.mpe");
	}

	/**
	 * Ensures that both text fields are set.
	 */

	private void dialogChanged() {
		viewer.setInput(matchingPage.getMatching());
		if (!confirmation.getSelection()){
			updateStatus("The guard and action matching has to be confirmed");
			return;
		}
		updateStatus(null);
	}

	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
		this.getContainer().updateButtons();
	}

}