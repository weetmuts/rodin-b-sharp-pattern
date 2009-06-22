package ch.ethz.eventb.pattern.core.basis;

import org.eventb.core.basis.EventBElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

import ch.ethz.eventb.pattern.core.IConstantMatching;


public class ConstantMatching extends EventBElement implements IConstantMatching {

	public ConstantMatching(String name, IRodinElement parent) {
		super(name, parent);
		
	}

	@Override
	public IInternalElementType<IConstantMatching> getElementType() {
		return ELEMENT_TYPE;
	}

	public String getPatternConstant() throws RodinDBException {
		return getAttributeValue(PatternAttributes.PATTERN_ELEMENT);
	}
	
	public void setPatternConstant(String name) throws RodinDBException {
		setAttributeValue(PatternAttributes.PATTERN_ELEMENT, name , null);
	}

	public String getProblemConstant() throws RodinDBException {
		return getAttributeValue(PatternAttributes.PROBLEM_ELEMENT);
	}
	
	public void setProblemConstant(String name) throws RodinDBException {
		setAttributeValue(PatternAttributes.PROBLEM_ELEMENT, name , null);
	}
}
