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
 
package gui.regular;

import automata.fsa.FiniteStateAutomaton;
import gui.editor.*;
import gui.environment.AutomatonEnvironment;
import gui.environment.Universe;
import gui.viewer.AutomatonDrawer;
import gui.viewer.AutomatonPane;
import gui.viewer.SelectionDrawer;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.*;
import javax.swing.*;

/**
 * This is the pane that holds the tools necessary for the conversion
 * of a finite state automaton to a regular expression.
 * 
 * @author Thomas Finley
 */

public class ConvertPane extends JPanel {
    /**
     * Creates a new conversion pane for the conversion of an
     * automaton to a regular expression.
     * @param environment the environment that this convert pane will
     * be a part of
     */
    public ConvertPane(AutomatonEnvironment environment) {
	this.environment = environment;
	automaton = (FiniteStateAutomaton) environment.getAutomaton().clone();
	JFrame frame = Universe.frameForEnvironment(environment);
	
	setLayout(new BorderLayout());
	
	JPanel labels = new JPanel(new BorderLayout());
	JLabel mainLabel = new JLabel();
	JLabel detailLabel = new JLabel();
	labels.add(mainLabel, BorderLayout.NORTH);
	labels.add(detailLabel, BorderLayout.SOUTH);
	
	add(labels, BorderLayout.NORTH);
	SelectionDrawer automatonDrawer = new SelectionDrawer(automaton);

	final FSAToREController controller =
	    new FSAToREController(automaton, automatonDrawer, mainLabel,
				  detailLabel, frame);

	gui.editor.EditorPane ep = new gui.editor.EditorPane
	    (automatonDrawer, new ToolBox() {
		public List tools
		    (AutomatonPane view, AutomatonDrawer drawer) {
		    LinkedList tools = new LinkedList();
		    tools.add(new ArrowNontransitionTool(view, drawer));
		    tools.add(new RegularStateTool(view, drawer, controller));
		    tools.add(new RegularTransitionTool
			      (view, drawer, controller));
		    tools.add(new CollapseTool(view, drawer, controller));
		    tools.add(new StateCollapseTool(view, drawer, controller));
		    return tools;
		}
	    });

	JToolBar bar = ep.getToolBar();
	bar.addSeparator();
	bar.add(new JButton(new AbstractAction("Do It") {
		public void actionPerformed(ActionEvent e) {
		    controller.moveNextStep();
		} }));
	bar.add(new JButton(new AbstractAction("Export") {
		public void actionPerformed(ActionEvent e) {
		    controller.export();
		} }));
	/*bar.add(new JButton(new AbstractAction("Export Automaton") {
		public void actionPerformed(ActionEvent e) {
		    controller.exportAutomaton();
		    } }));*/

	add(ep, BorderLayout.CENTER);
    }
    
    /** The environment that holds the automaton.  The automaton from
     * the environment is itself not modified. */
    AutomatonEnvironment environment;
    /** The copy of the original automaton, which will be modified
     * throughout this process. */
    private FiniteStateAutomaton automaton;
}
