package ch.ethz.eventb.pattern.core;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

import ch.ethz.eventb.internal.pattern.EventBPattern;


public interface IEventMatching extends IInternalElement{
	
	IInternalElementType<IEventMatching> ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPattern.PLUGIN_ID + ".eventMatching"); //$NON-NLS-1$
	
	public String getPatternEvent() throws RodinDBException;
	
	public void setPatternEvent(String name) throws RodinDBException;

	public String getProblemEvent() throws RodinDBException;
	
	public void setProblemEvent(String name) throws RodinDBException;
	
	public IParameterMatching getParameterMatching(String elementName);
	
	public IParameterMatching[] getParameterMatchings() throws RodinDBException;
	
	public IGuardMatching getGuardMatching(String elementName);
	
	public IGuardMatching[] getGuardMatchings() throws RodinDBException;
	
	public IActionMatching getActionMatching(String elementName);
	
	public IActionMatching[] getActionMatchings() throws RodinDBException;

}
