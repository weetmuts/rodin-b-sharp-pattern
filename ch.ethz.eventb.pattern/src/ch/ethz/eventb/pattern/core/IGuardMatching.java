package ch.ethz.eventb.pattern.core;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

import ch.ethz.eventb.internal.pattern.EventBPattern;


public interface IGuardMatching extends IInternalElement{
	
	IInternalElementType<IGuardMatching> ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPattern.PLUGIN_ID + ".guardMatching"); //$NON-NLS-1$
	
	public String getPatternGuard() throws RodinDBException;
	
	public void setPatternGuard(String name) throws RodinDBException;

	public String getProblemGuard() throws RodinDBException;
	
	public void setProblemGuard(String name) throws RodinDBException;

}
