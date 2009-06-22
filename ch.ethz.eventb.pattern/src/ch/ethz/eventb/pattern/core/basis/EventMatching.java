package ch.ethz.eventb.pattern.core.basis;

import org.eventb.core.basis.EventBElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

import ch.ethz.eventb.pattern.core.IActionMatching;
import ch.ethz.eventb.pattern.core.IEventMatching;
import ch.ethz.eventb.pattern.core.IGuardMatching;
import ch.ethz.eventb.pattern.core.IParameterMatching;


public class EventMatching extends EventBElement implements IEventMatching {

	public EventMatching(String name, IRodinElement parent) {
		super(name, parent);
		
	}

	@Override
	public IInternalElementType<IEventMatching> getElementType() {
		return ELEMENT_TYPE;
	}

	public String getPatternEvent() throws RodinDBException {
		return getAttributeValue(PatternAttributes.PATTERN_ELEMENT);
	}
	
	public void setPatternEvent(String name) throws RodinDBException {
		setAttributeValue(PatternAttributes.PATTERN_ELEMENT, name , null);
	}

	public String getProblemEvent() throws RodinDBException {
		return getAttributeValue(PatternAttributes.PROBLEM_ELEMENT);
	}
	
	public void setProblemEvent(String name) throws RodinDBException {
		setAttributeValue(PatternAttributes.PROBLEM_ELEMENT, name , null);
	}
	
	public IParameterMatching getParameterMatching(String elementName) {
		return getInternalElement(IParameterMatching.ELEMENT_TYPE, elementName);
	}

	public IParameterMatching[] getParameterMatchings() throws RodinDBException {
		return getChildrenOfType(IParameterMatching.ELEMENT_TYPE);
	}

	public IGuardMatching getGuardMatching(String elementName) {
		return getInternalElement(IGuardMatching.ELEMENT_TYPE, elementName);
	}

	public IGuardMatching[] getGuardMatchings() throws RodinDBException {
		return getChildrenOfType(IGuardMatching.ELEMENT_TYPE);
	}
	
	public IActionMatching getActionMatching(String elementName) {
		return getInternalElement(IActionMatching.ELEMENT_TYPE, elementName);
	}

	public IActionMatching[] getActionMatchings() throws RodinDBException {
		return getChildrenOfType(IActionMatching.ELEMENT_TYPE);
	}

}
