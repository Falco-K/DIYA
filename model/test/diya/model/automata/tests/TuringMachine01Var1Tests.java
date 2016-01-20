package diya.model.automata.tests;

import org.junit.BeforeClass;
import org.junit.Test;

import diya.model.automata.Automaton;
import diya.model.automata.FiniteStateMachine;
import diya.model.automata.TuringMachine;
import diya.model.language.Alphabet;

public class TuringMachine01Var1Tests {

	static Automaton turingMachine01;
	static Alphabet alphabet01;
	
	@BeforeClass
	public static void createAutomata(){
		alphabet01 = new Alphabet();
		alphabet01.addSymbol("0");
		alphabet01.addSymbol("1");
		
		turingMachine01 = new TuringMachine(0, 0, alphabet01, alphabet01, "X");
		
		turingMachine01.addState("s0", true, false, 0, 0);
		turingMachine01.addState("s1", false, false, 0, 0);
		turingMachine01.addState("s2", false, true, 0, 0);
		
		turingMachine01.addTransition("s0", "s0", new String[] {"0/R"});
		
		turingMachine01.addTransition("s0", "s1", new String[] {"X/R"});
		turingMachine01.addTransition("s1", "s2", new String[] {"X/0"});
	}
	
	@Test
	public void testTuringMachine01Var1With0(){
		TestHelper.testTape(turingMachine01, alphabet01, "0", "0 X 0", 4, true);
	}
	
	@Test
	public void testTuringMachine01Var1With000(){
		TestHelper.testTape(turingMachine01, alphabet01, "0 0 0", "0 0 0 X 0", 6, true);
	}
	
	@Test
	public void testTuringMachine01Var1With1(){
		TestHelper.testTape(turingMachine01, alphabet01, "1", "1", 1, false);
	}
	
	@Test
	public void testTuringMachine01Var1With111(){
		TestHelper.testTape(turingMachine01, alphabet01, "1 1 1", "1 1 1", 1, false);
	}
}
