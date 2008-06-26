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

package automata;

/**
 * This simulator factory returns the simulator for the type of automaton passed
 * in.
 * 
 * @author Thomas Finley
 */

public class SimulatorFactory {
	/**
	 * Returns the automaton simulator for this type of automaton.
	 * 
	 * @param automaton
	 *            the automaton to get the simulator for
	 * @return the appropriate automaton simulator for this automaton, or <CODE>null</CODE>
	 *         if there is no automaton simulator known for this type of
	 *         automaton
	 */
	public static AutomatonSimulator getSimulator(Automaton automaton) {
		if (automaton instanceof automata.fsa.FiniteStateAutomaton)
			return new automata.fsa.FSAStepWithClosureSimulator(automaton);
		else if (automaton instanceof automata.pda.PushdownAutomaton)
			return new automata.pda.PDAStepWithClosureSimulator(automaton);
		else if (automaton instanceof automata.turing.TuringMachine)
			return new automata.turing.TMSimulator(automaton);
        /*
         * Check for Moore must take place before check for Mealy because Moore
         * is a subclass of Mealy.
         */
        else if(automaton instanceof automata.mealy.MooreMachine)
            return new automata.mealy.MooreStepByStateSimulator(automaton);
        else if(automaton instanceof automata.mealy.MealyMachine)
            return new automata.mealy.MealyStepByStateSimulator(automaton);
		return null;
	}
}
