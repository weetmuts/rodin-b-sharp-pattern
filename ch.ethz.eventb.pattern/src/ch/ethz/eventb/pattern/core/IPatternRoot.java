package ch.ethz.eventb.pattern.core;

import org.eventb.core.ICommentedElement;
import org.eventb.core.IConfigurationElement;
import org.eventb.core.IEventBRoot;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

import ch.ethz.eventb.pattern.EventBPattern;



public interface IPatternRoot extends IEventBRoot, ICommentedElement, IConfigurationElement {
	
	IInternalElementType<IPatternRoot> ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPattern.PLUGIN_ID + ".patternRoot"); //$NON-NLS-1$
	
	public String getPatternMachine() throws RodinDBException;
	
	public void setPatternMachine(String name) throws RodinDBException;

	public String getProblemMachine() throws RodinDBException;
	
	public void setProblemMachine(String name) throws RodinDBException;
	
	public String getPatternProject() throws RodinDBException;
	
	public void setPatternProject(String name) throws RodinDBException;

	public String getProblemProject() throws RodinDBException;
	
	public void setProblemProject(String name) throws RodinDBException;
	
	public ICarrierSetMatching getCarrierSetMatching(String elementName);
	
	public ICarrierSetMatching[] getCarrierSetMatchings() throws RodinDBException;
	
	public IConstantMatching getConstantMatching(String elementName);
	
	public IConstantMatching[] getConstantMatchings() throws RodinDBException;
		
	public IVariableMatching getVariableMatching(String elementName);
	
	public IVariableMatching[] getVariableMatchings() throws RodinDBException;
	
	public IEventMatching getEventMatching(String elementName);
	
	public IEventMatching[] getEventMatchings() throws RodinDBException;
	

}
