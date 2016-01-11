package diya.model.automata.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import diya.model.automata.Automaton;
import diya.model.automata.FiniteStateMachine;
import diya.model.language.Alphabet;
import diya.model.language.Word;

public class FiniteStateMachine01Var3Tests {
	static Automaton finiteStateMachine01;
	static Alphabet alphabet01;
	
	@BeforeClass
	public static void createAutomata(){
		alphabet01 = new Alphabet();
		alphabet01.addSymbol("0");
		alphabet01.addSymbol("1");
		
		finiteStateMachine01 = new FiniteStateMachine(0,0,alphabet01);
		createAutomaton();
		editAutomaton();
	}
	
	public static void createAutomaton(){
		finiteStateMachine01.addState("s0", true, false, 0, 0);
		finiteStateMachine01.addState("s1", false, false, 0, 0);
		finiteStateMachine01.addState("s2", false, true, 0, 0);
		
		finiteStateMachine01.addTransition("s0", "s1", new String[] {"1"});
		finiteStateMachine01.addTransition("s1", "s2", new String[] {"0"});
		finiteStateMachine01.addTransition("s2", "s0", new String[] {"0", "1"});
		
		finiteStateMachine01.addTransition("s0", "s0", new String[] {"0"});
		finiteStateMachine01.addTransition("s1", "s1", new String[] {"1"});
	}
	
	public static void editAutomaton(){
		finiteStateMachine01.updateState("s0", true, true, 0, 0);
		finiteStateMachine01.updateTransition("s0", "s0", new String[] {"1"});
		finiteStateMachine01.updateTransition("s0", "s1", new String[] {"0"});
		
		finiteStateMachine01.removeTransition("s2", "s0");
		
		finiteStateMachine01.addState("s3", false, false, 0, 0);
		finiteStateMachine01.addTransition("s2", "s3", new String[] {"0", "1"});
		finiteStateMachine01.addTransition("s3", "s3", new String[] {"0", "1"});
	}
	
	@Test
	public void testFiniteAutomaton01Var3WithEmptyWord(){
		testWord(finiteStateMachine01, alphabet01, "", 1, true);
	}
	
	@Test
	public void testFiniteAutomaton01Var3With1(){
		testWord(finiteStateMachine01, alphabet01, "1", 2, true);
	}
	
	@Test
	public void testFiniteAutomaton01Var3With0(){
		testWord(finiteStateMachine01, alphabet01, "0", 2, false);
	}
	
	@Test
	public void testFiniteAutomaton01Var3With111(){
		testWord(finiteStateMachine01, alphabet01, "1 1 1", 4, true);
	}
	
	@Test
	public void testFiniteAutomaton01Var3With00111(){
		testWord(finiteStateMachine01, alphabet01, "0 0 1 1 1", 6, false);
	}
	
	@Test
	public void testFiniteAutomaton01Var3With10(){
		testWord(finiteStateMachine01, alphabet01, "1 0", 3, false);
	}
	
	@Test
	public void testFiniteAutomaton01Var3With1010(){
		testWord(finiteStateMachine01, alphabet01, "1 0 1 0", 5, true);
	}
	
	@Test
	public void testFiniteAutomaton01Var3With11101110(){
		testWord(finiteStateMachine01, alphabet01, "1 1 1 0 1 1 1 0", 9, true);
	}
	
	@Test
	public void testFiniteAutomaton01Var3With01110110(){
		testWord(finiteStateMachine01, alphabet01, "0 1 1 1 0 1 1 0", 9, false);
	}
	
	@Test
	public void testFiniteAutomaton01Var3With0111011011(){
		testWord(finiteStateMachine01, alphabet01, "0 1 1 1 0 1 1 0 1 1", 11, false);
	}
	
	public void testWord(Automaton automaton, Alphabet alphabet, String word, int expectedSteps, boolean expectedResult){
		if(expectedResult){
			assertTrue("Automaton should accept '"+word+"'", automaton.run(new Word(word, alphabet)));
		}else{
			assertFalse("Automaton should not accept '"+word+"'", automaton.run(new Word(word, alphabet)));
		}

		assertEquals("Stepcount for '"+word+"' should be "+expectedSteps, expectedSteps, automaton.getCurrentStepCount());
	}
}
