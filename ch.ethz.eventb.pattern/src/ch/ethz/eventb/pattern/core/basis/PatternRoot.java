package ch.ethz.eventb.pattern.core.basis;

import org.eventb.core.basis.EventBRoot;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

import ch.ethz.eventb.pattern.core.ICarrierSetMatching;
import ch.ethz.eventb.pattern.core.IConstantMatching;
import ch.ethz.eventb.pattern.core.IEventMatching;
import ch.ethz.eventb.pattern.core.IPatternRoot;
import ch.ethz.eventb.pattern.core.IVariableMatching;


public class PatternRoot extends EventBRoot implements IPatternRoot {

	public PatternRoot(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType<IPatternRoot> getElementType() {
		return ELEMENT_TYPE;
	}
	
	public String getPatternMachine() throws RodinDBException {
		return getAttributeValue(PatternAttributes.PATTERN_ELEMENT);
	}
	
	public void setPatternMachine(String name) throws RodinDBException {
		setAttributeValue(PatternAttributes.PATTERN_ELEMENT, name , null);
	}

	public String getProblemMachine() throws RodinDBException {
		return getAttributeValue(PatternAttributes.PROBLEM_ELEMENT);
	}
	
	public void setProblemMachine(String name) throws RodinDBException {
		setAttributeValue(PatternAttributes.PROBLEM_ELEMENT, name , null);
	}
	
	public String getPatternProject() throws RodinDBException {
		return getAttributeValue(PatternAttributes.PATTERN_PROJECT);
	}
	
	public void setPatternProject(String name) throws RodinDBException {
		setAttributeValue(PatternAttributes.PATTERN_PROJECT, name , null);
	}

	public String getProblemProject() throws RodinDBException {
		return getAttributeValue(PatternAttributes.PROBLEM_PROJECT);
	}
	
	public void setProblemProject(String name) throws RodinDBException {
		setAttributeValue(PatternAttributes.PROBLEM_PROJECT, name , null);
	}
	
	public IVariableMatching getVariableMatching(String elementName) {
		return getInternalElement(IVariableMatching.ELEMENT_TYPE, elementName);
	}

	public IVariableMatching[] getVariableMatchings() throws RodinDBException {
		return getChildrenOfType(IVariableMatching.ELEMENT_TYPE);
	}
	
	public IEventMatching getEventMatching(String elementName) {
		return getInternalElement(IEventMatching.ELEMENT_TYPE, elementName);
	}

	public IEventMatching[] getEventMatchings() throws RodinDBException {
		return getChildrenOfType(IEventMatching.ELEMENT_TYPE);
	}

	public ICarrierSetMatching getCarrierSetMatching(String elementName) {
		return getInternalElement(ICarrierSetMatching.ELEMENT_TYPE, elementName);
	}

	public ICarrierSetMatching[] getCarrierSetMatchings() throws RodinDBException {
		return getChildrenOfType(ICarrierSetMatching.ELEMENT_TYPE);
	}

	public IConstantMatching getConstantMatching(String elementName) {
		return getInternalElement(IConstantMatching.ELEMENT_TYPE, elementName);
	}

	public IConstantMatching[] getConstantMatchings() throws RodinDBException {
		return getChildrenOfType(IConstantMatching.ELEMENT_TYPE);
	}


}
