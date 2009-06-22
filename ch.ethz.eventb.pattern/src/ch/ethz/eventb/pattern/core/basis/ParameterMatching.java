package ch.ethz.eventb.pattern.core.basis;

import org.eventb.core.basis.EventBElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

import ch.ethz.eventb.pattern.core.IParameterMatching;


public class ParameterMatching extends EventBElement implements IParameterMatching {

	public ParameterMatching(String name, IRodinElement parent) {
		super(name, parent);
		
	}

	@Override
	public IInternalElementType<IParameterMatching> getElementType() {
		return ELEMENT_TYPE;
	}

	public String getPatternParameter() throws RodinDBException {
		return getAttributeValue(PatternAttributes.PATTERN_ELEMENT);
	}
	
	public void setPatternParameter(String name) throws RodinDBException {
		setAttributeValue(PatternAttributes.PATTERN_ELEMENT, name , null);
	}

	public String getProblemParameter() throws RodinDBException {
		return getAttributeValue(PatternAttributes.PROBLEM_ELEMENT);
	}
	
	public void setProblemParameter(String name) throws RodinDBException {
		setAttributeValue(PatternAttributes.PROBLEM_ELEMENT, name , null);
	}
}
