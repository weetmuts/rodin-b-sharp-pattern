package ch.ethz.eventb.pattern.core.basis;

import org.eventb.core.basis.EventBElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

import ch.ethz.eventb.pattern.core.IGuardMatching;


public class GuardMatching extends EventBElement implements IGuardMatching {

	public GuardMatching(String name, IRodinElement parent) {
		super(name, parent);
		
	}

	@Override
	public IInternalElementType<IGuardMatching> getElementType() {
		return ELEMENT_TYPE;
	}

	public String getPatternGuard() throws RodinDBException {
		return getAttributeValue(PatternAttributes.PATTERN_ELEMENT);
	}
	
	public void setPatternGuard(String name) throws RodinDBException {
		setAttributeValue(PatternAttributes.PATTERN_ELEMENT, name , null);
	}

	public String getProblemGuard() throws RodinDBException {
		return getAttributeValue(PatternAttributes.PROBLEM_ELEMENT);
	}
	
	public void setProblemGuard(String name) throws RodinDBException {
		setAttributeValue(PatternAttributes.PROBLEM_ELEMENT, name , null);
	}
}
