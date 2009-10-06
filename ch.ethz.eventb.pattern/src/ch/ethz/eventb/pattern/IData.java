package ch.ethz.eventb.pattern;

import org.eventb.core.IAction;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IParameter;
import org.eventb.core.IVariable;
import org.eventb.core.IWitness;

import ch.ethz.eventb.pattern.core.IPatternRoot;

public interface IData {

	/**
	 * Set or change the abstract pattern machine. All existing linking data as matchings, renamings, etc.
	 * are deleted. If the problem machine is set, all the linking information is reinitialized. The pattern refinement machine is set to null.
	 * @param patternAbstractMachine that should be set locally
	 * @throws DataException if the input is not valid or a RodinDBException occurs
	 */
	public void changePatternAbstractMachine(IMachineRoot patternAbstractMachine)
			throws DataException;

	/**
	 * Set or change the pattern refinement machine. The abstract pattern machine has to be set before this
	 * method is called.
	 * @param patternRefinementMachine that should be set locally
	 * @throws DataException if input is invalid, abstract pattern machine is no set or a RodinDBException occurs
	 */
	public void changePatternRefinementMachine(
			IMachineRoot patternRefinementMachine) throws DataException;

	/**
	 * Set or change the problem machine. All existing linking data as matchings are deleted.
	 * If the pattern refinement machine is set, all the linking information is reinitialized.
	 * @param problemMachine that should be set locally
	 * @throws DataException if the input is not valid or a RodinDBException occurs
	 */
	public void changeProblemMachine(IMachineRoot problemMachine)
			throws DataException;

	/**
	 * Add the matching of two events (pattern and problem). 
	 * @param pattern event
	 * @param problem event
	 * @throws DataException
	 */
	public void addMatching(IEvent pattern, IEvent problem)
			throws DataException;

	/**
	 * Add the matching of two variables (pattern and problem).
	 * @param pattern variable
	 * @param problem variable
	 * @throws DataException
	 */
	public void addMatching(IVariable pattern, IVariable problem)
			throws DataException;

	/**
	 * Add the matching of two parameters (pattern and problem). The corresponding events
	 * have to be matched.
	 * @param pattern parameter
	 * @param problem parameter
	 * @throws DataException
	 */
	public void addMatching(IParameter pattern, IParameter problem)
			throws DataException;

	/**
	 * Add the matching of two guards (pattern and problem). The corresponding events
	 * have to be matched.
	 * @param pattern guard
	 * @param problem guard
	 * @throws DataException
	 */
	public void addMatching(IGuard pattern, IGuard problem)
			throws DataException;

	/**
	 * Add the matching of two actions (pattern and problem). The corresponding events
	 * have to be matched.
	 * @param pattern action
	 * @param problem action
	 * @throws DataException
	 */
	public void addMatching(IAction pattern, IAction problem)
			throws DataException;

	/**
	 * Add the merging of two events. This defines which events should be combined in the
	 * generated machine.
	 * @param patternRefinement event
	 * @param problem event
	 * @throws DataException
	 */
	public void addMerging(IEvent patternRefinement, IEvent problem)
			throws DataException;

	/**
	 * Update the renaming of a variable of the pattern refinement machine.
	 * The renaming has to be a non-empty string.
	 * @param variable of the pattern refinement
	 * @param renaming of the variable
	 * @throws DataException
	 */
	public void updateRenaming(IVariable variable, String renaming)
			throws DataException;

	/**
	 * Update the matching of a carrier set with a string.
	 * @param carrierSet
	 * @param matching string
	 * @throws DataException
	 */
	public void updateMatching(ICarrierSet carrierSet, String matching)
			throws DataException;

	/**
	 * Update the matching of a constant with a string.
	 * @param constant
	 * @param matching string
	 * @throws DataException
	 */
	public void updateMatching(IConstant constant, String matching)
			throws DataException;

	/**
	 * Update the renaming of a new not-merged event of the pattern refinement machine.
	 * The renaming has to be a non-empty string.
	 * @param event of the pattern refinement
	 * @param renaming of the event
	 * @throws DataException
	 */
	public void updateRenamingOfNewEvent(IEvent event, String renaming)
			throws DataException;

	/**
	 * Update the renaming of a problem event that is merged with one or several new event of the pattern
	 * refinement machine. The renaming has to be a non-empty string.
	 * @param event of the problem
	 * @param renaming the event
	 * @throws DataException
	 */
	public void updateRenamingOfMergedEvent(IEvent event, String renaming)
			throws DataException;

	/**
	 * Update the renaming of a event of the pattern refinement machine that refines a matched pattern event.
	 * Since pattern events can be matched with more than one problem event it is necessary to provide also
	 * the problem event. The renaming has to be a non-empty string.
	 * @param patternRefinement event
	 * @param problem event
	 * @param renaming of the event
	 * @throws DataException
	 */
	public void updateRenamingOfMatchedEvent(IEvent patternRefinement,
			IEvent problem, String renaming) throws DataException;

	/**
	 * Update the replacement expression for a disappearing variable of the pattern machine
	 * in relation to the pattern refinement.
	 * The replacement has to be a non-empty string.
	 * @param variable
	 * @param replacement of the variable
	 * @throws DataException
	 */
	public void updateForwardReplacementOf(IVariable variable,
			String replacement) throws DataException;

	/**
	 * Update the replacement expression for a disappearing parameter of the pattern machine
	 * in relation to the pattern refinement.
	 * The replacement has to be a non-empty string.
	 * @param parameter
	 * @param replacement of the parameter
	 * @throws DataException
	 */
	public void updateReplacementOf(IWitness witness, String replacement)
			throws DataException;

	/**
	 * The given matching machine is fed into the data.
	 * @param matching
	 * @throws DataException
	 */
	public void loadMatching(IPatternRoot root) throws DataException;

	public IMachineGenerator createNewMachineGenerator();
	
	
}