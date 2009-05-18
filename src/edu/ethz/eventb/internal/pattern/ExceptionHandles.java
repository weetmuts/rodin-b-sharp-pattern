package edu.ethz.eventb.internal.pattern;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.rodinp.core.RodinDBException;

public class ExceptionHandles {

	public static void handleGetProjectsException(RodinDBException e, Shell shell) {
		Throwable realException = e.getException();
		if (shell != null)
			MessageDialog
				.openError(shell, "Error when getting RODIN projects",
						realException.getMessage());
	}

	public static void handleGetMachinesException(RodinDBException e,
			Shell shell) {
		Throwable realException = e.getException();
		if (shell != null)
			MessageDialog
				.openError(shell, "Error when getting machine roots of a project",
						realException.getMessage());
	}
	
}
