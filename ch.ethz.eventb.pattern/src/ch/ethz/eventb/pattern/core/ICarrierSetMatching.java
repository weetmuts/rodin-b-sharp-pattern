package ch.ethz.eventb.pattern.core;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

import ch.ethz.eventb.internal.pattern.EventBPattern;


public interface ICarrierSetMatching extends IInternalElement{
	
	IInternalElementType<ICarrierSetMatching> ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPattern.PLUGIN_ID + ".carrierSetMatching"); //$NON-NLS-1$
	
	public String getPatternCarrierSet() throws RodinDBException;
	
	public void setPatternCarrierSet(String name) throws RodinDBException;

	public String getProblemCarrierSet() throws RodinDBException;
	
	public void setProblemCarrierSet(String name) throws RodinDBException;

}
