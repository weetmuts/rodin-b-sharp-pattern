package ch.ethz.eventb.pattern.core;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

import ch.ethz.eventb.pattern.EventBPattern;


public interface IConstantMatching extends IInternalElement{
	
	IInternalElementType<IConstantMatching> ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPattern.PLUGIN_ID + ".constantMatching"); //$NON-NLS-1$
	
	public String getPatternConstant() throws RodinDBException;
	
	public void setPatternConstant(String name) throws RodinDBException;

	public String getProblemConstant() throws RodinDBException;
	
	public void setProblemConstant(String name) throws RodinDBException;

}
