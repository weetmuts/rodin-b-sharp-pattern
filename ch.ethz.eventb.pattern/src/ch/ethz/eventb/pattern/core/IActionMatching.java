package ch.ethz.eventb.pattern.core;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

import ch.ethz.eventb.pattern.EventBPattern;


public interface IActionMatching extends IInternalElement{
	
	IInternalElementType<IActionMatching> ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPattern.PLUGIN_ID + ".actionMatching"); //$NON-NLS-1$
	
	public String getPatternAction() throws RodinDBException;
	
	public void setPatternAction(String name) throws RodinDBException;

	public String getProblemAction() throws RodinDBException;
	
	public void setProblemAction(String name) throws RodinDBException;

}
