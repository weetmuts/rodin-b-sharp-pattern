package ch.ethz.eventb.pattern.core.basis;

import org.rodinp.core.IAttributeType;
import org.rodinp.core.RodinCore;

import ch.ethz.eventb.internal.pattern.EventBPattern;


public final class PatternAttributes {
		
	public PatternAttributes() {
		}
	
	public static IAttributeType.String PATTERN_ELEMENT =
		RodinCore.getStringAttrType(EventBPattern.PLUGIN_ID + ".patternElement");
	
	public static IAttributeType.String PROBLEM_ELEMENT =
		RodinCore.getStringAttrType(EventBPattern.PLUGIN_ID + ".problemElement");

	public static IAttributeType.String PATTERN_PROJECT =
		RodinCore.getStringAttrType(EventBPattern.PLUGIN_ID + ".patternProject");
	
	public static IAttributeType.String PROBLEM_PROJECT =
		RodinCore.getStringAttrType(EventBPattern.PLUGIN_ID + ".problemProject");
		

}
