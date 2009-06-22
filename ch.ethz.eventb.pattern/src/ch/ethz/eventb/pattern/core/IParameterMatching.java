package ch.ethz.eventb.pattern.core;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

import ch.ethz.eventb.internal.pattern.EventBPattern;


public interface IParameterMatching extends IInternalElement{
	
	IInternalElementType<IParameterMatching> ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPattern.PLUGIN_ID + ".parameterMatching"); //$NON-NLS-1$
	
	public String getPatternParameter() throws RodinDBException;
	
	public void setPatternParameter(String name) throws RodinDBException;

	public String getProblemParameter() throws RodinDBException;
	
	public void setProblemParameter(String name) throws RodinDBException;

}
