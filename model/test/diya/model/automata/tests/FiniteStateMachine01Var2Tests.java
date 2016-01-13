package diya.model.automata.tests;

import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

import diya.model.automata.Automaton;
import diya.model.automata.FiniteStateMachine;
import diya.model.language.Alphabet;
import diya.model.language.Word;

public class FiniteStateMachine01Var2Tests {
	
	static Automaton finiteStateMachine01;
	static Alphabet alphabet01;
	
	@BeforeClass
	public static void createAutomata(){
		alphabet01 = new Alphabet();
		alphabet01.addSymbol("0");
		alphabet01.addSymbol("1");
		
		finiteStateMachine01 = new FiniteStateMachine(0,0,alphabet01);
		finiteStateMachine01.addState("s0", true, false, 0, 0);
		finiteStateMachine01.addState("s1", false, false, 0, 0);
		finiteStateMachine01.addState("s2", false, true, 0, 0);
		
		
		finiteStateMachine01.addTransition("s0", "s1", new String[] {"1"});
		finiteStateMachine01.addTransition("s1", "s2", new String[] {"0"});
		finiteStateMachine01.addTransition("s2", "s0", new String[] {"0", "1"});
		
		finiteStateMachine01.addTransition("s0", "s0", new String[] {"0"});
		finiteStateMachine01.addTransition("s1", "s1", new String[] {"1"});
	}
	
	@Test
	public void testFiniteAutomaton01Var2WithEmptyWord(){
		TestHelper.testWord(finiteStateMachine01, alphabet01, "", 1, false);
	}
	
	@Test
	public void testFiniteAutomaton01Var2With1(){
		TestHelper.testWord(finiteStateMachine01, alphabet01, "1", 2, false);
	}
	
	@Test
	public void testFiniteAutomaton01Var2With0(){
		TestHelper.testWord(finiteStateMachine01, alphabet01, "0", 2, false);
	}
	
	@Test
	public void testFiniteAutomaton01Var2With111(){
		TestHelper.testWord(finiteStateMachine01, alphabet01, "1 1 1", 4, false);
	}
	
	@Test
	public void testFiniteAutomaton01Var2With00111(){
		TestHelper.testWord(finiteStateMachine01, alphabet01, "0 0 1 1 1", 6, false);
	}
	
	@Test
	public void testFiniteAutomaton01Var2With10(){
		TestHelper.testWord(finiteStateMachine01, alphabet01, "1 0", 3, true);
	}
	
	@Test
	public void testFiniteAutomaton01Var2With1010(){
		TestHelper.testWord(finiteStateMachine01, alphabet01, "1 0 1 0", 5, false);
	}
	
	@Test
	public void testFiniteAutomaton01Var2With01110110(){
		TestHelper.testWord(finiteStateMachine01, alphabet01, "0 1 1 1 0 1 1 0", 9, true);
	}
	
	@Test
	public void testFiniteAutomaton01Var2With0111011011(){
		TestHelper.testWord(finiteStateMachine01, alphabet01, "0 1 1 1 0 1 1 0 1 1", 11, false);
	}
}
