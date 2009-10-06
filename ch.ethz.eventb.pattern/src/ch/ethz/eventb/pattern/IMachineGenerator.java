package ch.ethz.eventb.pattern;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IMachineRoot;

public interface IMachineGenerator {

	public IMachineRoot generateMachine(String name, boolean generatePO,
			IProgressMonitor monitor) throws Exception;

}