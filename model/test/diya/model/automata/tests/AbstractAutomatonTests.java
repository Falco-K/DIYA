package diya.model.automata.tests;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import diya.model.automata.Automaton;
import diya.model.automata.FiniteStateMachine;
import diya.model.language.Alphabet;
import diya.model.language.AlphabetType;

public class AbstractAutomatonTests {
	
	@Test
	public void correctAutomatonHasBeenCreated(){
		Alphabet alphabet = new Alphabet();
		alphabet.addSymbol("TestSymbol");
		Automaton testAutomaton = new FiniteStateMachine(0, 0, alphabet);
		
		assertEquals("Alphabet must contain TestSymbol", "TestSymbol", testAutomaton.getAlphabets().get(AlphabetType.INPUT).getAsStrings()[0]);
	}
}
