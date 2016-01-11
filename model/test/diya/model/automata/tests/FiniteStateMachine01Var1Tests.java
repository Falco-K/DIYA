package diya.model.automata.tests;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import diya.model.automata.Automaton;
import diya.model.automata.FiniteStateMachine;
import diya.model.language.Alphabet;
import diya.model.language.Word;

public class FiniteStateMachine01Var1Tests {
	
	static Automaton finiteStateMachine01;
	static Alphabet alphabet01;
	
	@BeforeClass
	public static void createAutomata(){
		alphabet01 = new Alphabet();
		alphabet01.addSymbol("0");
		alphabet01.addSymbol("1");
		
		finiteStateMachine01 = new FiniteStateMachine(0,0,alphabet01);
		finiteStateMachine01.addState("s0", true, false, 0, 0);
		finiteStateMachine01.addState("s1", false, true, 0, 0);
		finiteStateMachine01.addTransition("s0", "s1", null);
		finiteStateMachine01.addTransition("s0", "s0", new String[] {"0"});
		finiteStateMachine01.addTransition("s1", "s1", new String[] {"1"});
	}
	
	@Test
	public void testFiniteAutomaton01Var1WithEmptyWord(){
		testWord(finiteStateMachine01, alphabet01, "", 1);
	}
	
	@Test
	public void testFiniteAutomaton01Var1With0(){
		testWord(finiteStateMachine01, alphabet01, "0", 2);
	}
	
	@Test
	public void testFiniteAutomaton01Var1With1(){
		testWord(finiteStateMachine01, alphabet01, "1", 2);
	}
	
	@Test
	public void testFiniteAutomaton01Var1With01(){
		testWord(finiteStateMachine01, alphabet01, "0 1", 3);
	}
	
	@Test
	public void testFiniteAutomaton01Var1With0011(){
		testWord(finiteStateMachine01, alphabet01, "0 0 1 1", 5);
	}
	
	@Test
	public void testFiniteAutomaton01Var1With000111(){
		testWord(finiteStateMachine01, alphabet01, "0 0 0 1 1 1", 7);
	}
	
	@Test
	public void testFiniteAutomaton01Var1With0111(){
		testWord(finiteStateMachine01, alphabet01, "0 1 1 1", 5);
	}
	
	@Test
	public void testFiniteAutomaton01Var1With000000011111(){
		testWord(finiteStateMachine01, alphabet01, "0 0 0 0 0 0 0 1 1 1 1 1", 13);
	}
	
	public void testWord(Automaton automaton, Alphabet alphabet, String word, int expectedSteps){
		assertTrue("Automaton should accept '"+word+"'", automaton.run(new Word(word, alphabet)));
		assertEquals("Stepcount for '"+word+"' should be "+expectedSteps, expectedSteps, automaton.getCurrentStepCount());
	}
}
