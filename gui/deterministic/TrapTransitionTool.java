package gui.deterministic;

import gui.editor.TransitionTool;
import gui.regular.FSAToREController;
import gui.viewer.AutomatonDrawer;
import gui.viewer.AutomatonPane;

import java.awt.event.MouseEvent;

import automata.State;

/**
 * Useful in creating transitions to the trap state
 * @author Kyung Min (Jason) Lee
 *
 */
public class TrapTransitionTool extends TransitionTool{
	
	/** The adding trap state controller. */
	private AddTrapStateController myController;

	/**
	 * Instantiates a new transition tool.
	 * 
	 * @param view
	 *            the view where the automaton is drawn
	 * @param drawer
	 *            the object that draws the automaton
	 * @param controller
	 *            the controller object for the transition from an FSA to an RE
	 */
	public TrapTransitionTool(AutomatonPane view, AutomatonDrawer drawer,
			AddTrapStateController controller) {
		super(view, drawer);
		myController = controller;
	}

	/**
	 * When we release the mouse, a transition from the start state to this
	 * released state must be formed.
	 * 
	 * @param event
	 *            the mouse event
	 */
	public void mouseReleased(MouseEvent event) {
		// Did we even start at a state?
		if (first == null)
			return;
		State state = getDrawer().stateAtPoint(event.getPoint());
		if (state != null) {
			myController.transitionCreate(first, state);
		}
		first = null;
		getView().repaint();
	}
}
