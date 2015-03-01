package ch.ethz.eventb.pattern.tests;

import junit.framework.Assert;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

import ch.ethz.eventb.pattern.core.IActionMatching;
import ch.ethz.eventb.pattern.core.ICarrierSetMatching;
import ch.ethz.eventb.pattern.core.IConstantMatching;
import ch.ethz.eventb.pattern.core.IEventMatching;
import ch.ethz.eventb.pattern.core.IGuardMatching;
import ch.ethz.eventb.pattern.core.IParameterMatching;
import ch.ethz.eventb.pattern.core.IPatternRoot;
import ch.ethz.eventb.pattern.core.IVariableMatching;
import ch.ethz.eventb.utils.tests.ChannelSetupTests;

public class PatternFileTests extends ChannelSetupTests {

	public void testCreatePatternFile() {
		IRodinFile channelPatternFile = channelPrj.getRodinProject()
				.getRodinFile("Channel.pat");
		try {
			channelPatternFile.create(false, new NullProgressMonitor());
		} catch (RodinDBException e) {
			e.printStackTrace();
			Assert.fail("Create pattern file \"Channel.pat\" fails");
		}
		IInternalElement root = channelPatternFile.getRoot();
		Assert.assertTrue("Root must be an instance of IPatternRoot",
				root instanceof IPatternRoot);
		IPatternRoot patternRoot = (IPatternRoot) root;

		// Test set/get pattern project/machine
		try {
			patternRoot.setPatternProject("PatternProject");
			patternRoot.setPatternMachine("PatternMachine");
			Assert.assertEquals("Incorrect pattern project name",
					"PatternProject", patternRoot.getPatternProject());
			Assert.assertEquals("Incorrect pattern machine name",
					"PatternMachine", patternRoot.getPatternMachine());
		} catch (RodinDBException e) {
			e.printStackTrace();
			Assert.fail("Set/get pattern project/machine fails");
		}

		// Test set/get problem project/machine
		try {
			patternRoot.setProblemProject("ProblemProject");
			patternRoot.setProblemMachine("ProblemMachine");
			Assert.assertEquals("Incorrect problem project name",
					"ProblemProject", patternRoot.getProblemProject());
			Assert.assertEquals("Incorrect problem machine name",
					"ProblemMachine", patternRoot.getProblemMachine());
		} catch (RodinDBException e) {
			e.printStackTrace();
			Assert.fail("Set/get problem project/machine fails");
		}
		
		// Test set/get carrier set matchings
		ICarrierSetMatching setMatching = patternRoot
				.getCarrierSetMatching("internalSetMatching");
		try {
			setMatching.create(null, new NullProgressMonitor());
			setMatching.setPatternCarrierSet("S");
			setMatching.setProblemCarrierSet("X");
			Assert.assertEquals("Incorrect pattern carrier set", "S",
					setMatching.getPatternCarrierSet());
			Assert.assertEquals("Incorrect problem carrier set", "X",
					setMatching.getProblemCarrierSet());
		} catch (RodinDBException e) {
			e.printStackTrace();
			Assert.fail("Set/get carrier set matchings fails");
		}
		
		// Test set/get constant matchings
		IConstantMatching cstMatching = patternRoot
				.getConstantMatching("interalCstMatching");
		try {
			cstMatching.create(null, new NullProgressMonitor());
			cstMatching.setPatternConstant("c");
			cstMatching.setProblemConstant("a");
			Assert.assertEquals("Incorrect pattern constant", "c",
					cstMatching.getPatternConstant());
			Assert.assertEquals("Incorrect problem constant", "a",
					cstMatching.getProblemConstant());
		} catch (RodinDBException e) {
			e.printStackTrace();
			Assert.fail("Set/get constant matchings fails");
		}
		
		
		// Test set/get variable matchings
		IVariableMatching varMatching = patternRoot
				.getVariableMatching("internalVarMatching");
		try {
			varMatching.create(null, new NullProgressMonitor());
			varMatching.setPatternVariable("v");
			varMatching.setProblemVariable("w");
			Assert.assertEquals("Incorrect pattern variable", "v",
					varMatching.getPatternVariable());
			Assert.assertEquals("Incorrect problem variable", "w",
					varMatching.getProblemVariable());			
		} catch (RodinDBException e) {
			e.printStackTrace();
			Assert.fail("Set/get variable matchings fails");
		}
		
		// Test set/get event matchings
		IEventMatching evtMatching = patternRoot
				.getEventMatching("internalEvtMatching");
		try {
			evtMatching.create(null, new NullProgressMonitor());
			evtMatching.setPatternEvent("e");
			evtMatching.setProblemEvent("f");
			Assert.assertEquals("Incorrect pattern event", "e",
					evtMatching.getPatternEvent());
			Assert.assertEquals("Incorrect problem event", "f",
					evtMatching.getProblemEvent());
			
			IParameterMatching parMatching = evtMatching
					.getParameterMatching("internalParMatching");
			parMatching.create(null, new NullProgressMonitor());
			parMatching.setPatternParameter("p");
			parMatching.setProblemParameter("q");
			Assert.assertEquals("Incorrect pattern parameter", "p",
					parMatching.getPatternParameter());
			Assert.assertEquals("Incorrect problem parameter", "q",
					parMatching.getProblemParameter());
			
			IGuardMatching grdMatching = evtMatching
					.getGuardMatching("internalGrdMatching");
			grdMatching.create(null, new NullProgressMonitor());
			grdMatching.setPatternGuard("g");
			grdMatching.setProblemGuard("h");
			Assert.assertEquals("Incorrect pattern guard", "g",
					grdMatching.getPatternGuard());
			Assert.assertEquals("Incorrect problem guard", "h",
					grdMatching.getProblemGuard());
			
			IActionMatching actMatching = evtMatching
					.getActionMatching("internalActMatching");
			actMatching.create(null, new NullProgressMonitor());
			actMatching.setPatternAction("a");
			actMatching.setProblemAction("b");
			Assert.assertEquals("Incorrect pattern action", "a",
					actMatching.getPatternAction());
			Assert.assertEquals("Incorrect problem action", "b",
					actMatching.getProblemAction());
			
		} catch (RodinDBException e) {
			e.printStackTrace();
			Assert.fail("Set/get event matchings fails");
		}
	}
}
