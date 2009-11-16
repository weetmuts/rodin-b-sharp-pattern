package ch.ethz.eventb.pattern.core;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

import ch.ethz.eventb.pattern.EventBPattern;


public interface IVariableMatching extends IInternalElement{
	
	IInternalElementType<IVariableMatching> ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPattern.PLUGIN_ID + ".variableMatching"); //$NON-NLS-1$
	
	public String getPatternVariable() throws RodinDBException;
	
	public void setPatternVariable(String name) throws RodinDBException;

	public String getProblemVariable() throws RodinDBException;
	
	public void setProblemVariable(String name) throws RodinDBException;

}
