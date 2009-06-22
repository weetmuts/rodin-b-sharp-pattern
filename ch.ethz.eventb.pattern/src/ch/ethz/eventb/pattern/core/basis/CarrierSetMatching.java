package ch.ethz.eventb.pattern.core.basis;

import org.eventb.core.basis.EventBElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

import ch.ethz.eventb.pattern.core.ICarrierSetMatching;


public class CarrierSetMatching extends EventBElement implements ICarrierSetMatching {

	public CarrierSetMatching(String name, IRodinElement parent) {
		super(name, parent);
		
	}

	@Override
	public IInternalElementType<ICarrierSetMatching> getElementType() {
		return ELEMENT_TYPE;
	}

	public String getPatternCarrierSet() throws RodinDBException {
		return getAttributeValue(PatternAttributes.PATTERN_ELEMENT);
	}
	
	public void setPatternCarrierSet(String name) throws RodinDBException {
		setAttributeValue(PatternAttributes.PATTERN_ELEMENT, name , null);
	}

	public String getProblemCarrierSet() throws RodinDBException {
		return getAttributeValue(PatternAttributes.PROBLEM_ELEMENT);
	}
	
	public void setProblemCarrierSet(String name) throws RodinDBException {
		setAttributeValue(PatternAttributes.PROBLEM_ELEMENT, name , null);
	}
}
