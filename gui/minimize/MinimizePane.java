/* -- JFLAP 4.0 --
 *
 * Copyright information:
 *
 * Susan H. Rodger, Thomas Finley
 * Computer Science Department
 * Duke University
 * April 24, 2003
 * Supported by National Science Foundation DUE-9752583.
 *
 * Copyright (c) 2003
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms are permitted
 * provided that the above copyright notice and this paragraph are
 * duplicated in all such forms and that any documentation,
 * advertising materials, and other materials related to such
 * distribution and use acknowledge that the software was developed
 * by the author.  The name of the author may not be used to
 * endorse or promote products derived from this software without
 * specific prior written permission.
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 * WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 */
 
package gui.minimize;

import gui.tree.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.*;
import automata.*;
import automata.fsa.*;
import gui.viewer.*;
import gui.editor.*;
import gui.environment.Environment;
import gui.SplitPaneFactory;

/**
 * The <CODE>MinimizePane</CODE> is a view created to minimize a DFA
 * using some minimizing tree structure.
 * 
 * @author Thomas Finley
 */

public class MinimizePane extends JPanel {
    /**
     * Instantiates a <CODE>MinimizePane</CODE>.
     * @param dfa a DFA to minimize
     * @param environment the environment this minimize pane will be
     * added to
     */
    public MinimizePane(FiniteStateAutomaton dfa, Environment environment) {
	// Set up the minimizable automaton, and the minimize tree.
	minimizer = new Minimizer();
	minimizer.initializeMinimizer();
	dfa = (FiniteStateAutomaton) minimizer.getMinimizeableAutomaton(dfa);
	//minimizer.initializeMinimizer();
	TreeModel tree = minimizer.getInitializedTree(dfa);
	// Set up the drawers.
	automatonDrawer = new SelectionDrawer(dfa);
	treeDrawer = new SelectTreeDrawer(tree);
	// Set up the minimize node drawer.
	MinimizeNodeDrawer nodeDrawer = new MinimizeNodeDrawer();
	treeDrawer.setNodeDrawer(nodeDrawer);
	TreeNode[] groups = Trees.children((MinimizeTreeNode)tree.getRoot());
	for (int i=0; i<groups.length; i++) {
	    MinimizeTreeNode group = (MinimizeTreeNode) groups[i];
	    State[] states = (State[]) group.getUserObject();
	    if (states.length == 0) continue;
	    if (dfa.isFinalState(states[0]))
		nodeDrawer.setLabel(group, "Final");
	    else
		nodeDrawer.setLabel(group, "Nonfinal");
	}

	// Set up the controller object.
	controller = new MinimizeController(this, automatonDrawer, treeDrawer,
					    minimizer);
	JPanel right = new JPanel(new BorderLayout());
	right.add(initTreePane(), BorderLayout.CENTER);
	controlPanel = new ControlPanel(treeDrawer, controller);
	/*right.add(new ControlPanel(treeDrawer, controller),
	  BorderLayout.SOUTH);*/

	// Finally, initialize the view.
	split = SplitPaneFactory.createSplit(environment, true, 0.5,
					     initAutomatonPane(), right);
	setLayout(new BorderLayout());
	add(split, BorderLayout.CENTER);
	add(controlPanel, BorderLayout.NORTH);
	add(messageLabel, BorderLayout.SOUTH);
	split.setResizeWeight(0.5);
	controller.setEnabledness();
    }

    /**
     * Initializes the automaton pane.
     */
    public AutomatonPane initAutomatonPane() {
	AutomatonPane apane = new AutomatonPane(automatonDrawer);
	gui.SuperMouseAdapter a =
	    new ArrowMinimizeTool(apane, automatonDrawer);
	apane.addMouseListener(a);
	apane.addMouseMotionListener(a);
	return apane;
    }
    
    /**
     * Initializes the tree pane.
     */
    public TreePanel initTreePane() {
	final TreePanel tpane = new TreePanel(treeDrawer);
	gui.SuperMouseAdapter a = new gui.SuperMouseAdapter() {
		public void mouseClicked(MouseEvent event) {
		    TreeNode n = tpane.nodeAtPoint(event.getPoint());
		    controller.nodeClicked((MinimizeTreeNode) n, event);
		}
		public void mousePressed(MouseEvent event) {
		    TreeNode n = tpane.nodeAtPoint(event.getPoint());
		    controller.nodeDown((MinimizeTreeNode) n, event);
		}
	    };
	tpane.addMouseListener(a);
	tpane.addMouseMotionListener(a);
	return tpane;
    }

    /**
     * Tells the pane to replace the tree pane with a pane to build
     * the minimized automaton.  This should be called once the tree
     * is completed and the user has elected to move on to the
     * building of the minimized automaton.
     * @param dfa the finite state automaton we're minimizing
     * @param tree the completed minimized tree; results will be
     * unpredictable if this tree is not truly minimized
     */
    public void beginMinimizedAutomaton(FiniteStateAutomaton dfa,
					DefaultTreeModel tree) {
	// Create the new view.
	remove(controlPanel);
	FiniteStateAutomaton newAutomaton = new FiniteStateAutomaton();
	minimizer.createStatesForMinimumDfa(dfa, newAutomaton, tree);
	SelectionDrawer drawer = new SelectionDrawer(newAutomaton);
	EditorPane ep = new EditorPane(drawer, new ToolBox() {
		public java.util.List tools
		    (AutomatonPane view, AutomatonDrawer drawer) {
		    java.util.List tools = new java.util.LinkedList();
		    tools.add(new ArrowMinimizeTool(view, drawer));
		    tools.add(new TransitionTool(view, drawer));
		    return tools;
		}
	    });
	// Remove all selected stuff.
	automatonDrawer.clearSelected();
	// Set up the controller device.
	builderController =
	    new BuilderController(dfa, newAutomaton, drawer,
				  minimizer, tree, split);
	// Set the view in the right hand side.
	JPanel right = new JPanel(new BorderLayout());
	right.add(ep, BorderLayout.CENTER);
	/*right.add(new BuilderControlPanel(builderController),
	  BorderLayout.SOUTH);*/
	ep.getToolBar().addSeparator();
	BuilderControlPanel.initView(ep.getToolBar(), builderController);
	split.setRightComponent(right);
	invalidate();
	repaint();
    }

    /**
     * This extension of the arrow tool does not allow the editing of
     * transitions.
     */
    private class ArrowMinimizeTool extends gui.editor.ArrowNontransitionTool {
	/**
	 * Instantiates a new <CODE>ArrowMinimizeTool</CODE>.
	 * @param view the view the automaton is drawn in
	 * @param drawer the automaton drawer
	 */
	public ArrowMinimizeTool(AutomatonPane view, AutomatonDrawer drawer) {
	    super(view, drawer);
	}

	/**
	 * On a mouse click, this simply returns, 
	 * @param event the mouse event
	 */
	public void mouseClicked(java.awt.event.MouseEvent event) {
	    super.mouseClicked(event);
	    State s = automatonDrawer.stateAtPoint(event.getPoint());
	    // If we're still building the tree...
	    if (builderController == null)
		controller.stateDown(s, event);
	}
    }
    
    /** The object that handles the grit of the minimization. */
    Minimizer minimizer;
    /** The drawer for the original automaton. */
    SelectionDrawer automatonDrawer;
    /** The drawer for the tree. */
    SelectTreeDrawer treeDrawer;
    /** The minimize controller. */
    MinimizeController controller;
    /** The minimum automaton builder controller. */
    BuilderController builderController = null;
    /** The view for this pane. */
    JSplitPane split;
    /** The toolbar. */
    ControlPanel controlPanel;
    /** The message label. */
    JLabel messageLabel = new JLabel(" ");
}
