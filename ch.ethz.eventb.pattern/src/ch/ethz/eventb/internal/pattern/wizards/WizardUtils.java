package ch.ethz.eventb.internal.pattern.wizards;


public class WizardUtils {

	/**
	 * The debug flag. This is set by the option when the plug-in is started.
	 * Client should not try to reset this flag.
	 */
	public static boolean DEBUG = false;
	
	public static final String DEBUG_PREFIX = "*** Event-B Pattern Wizard ***";
	
	public static void debug(String message) {
		System.out.println(DEBUG_PREFIX + " " + message);
	}
	
}
