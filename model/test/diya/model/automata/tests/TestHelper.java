package diya.model.automata.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import diya.model.automata.Automaton;
import diya.model.language.Alphabet;
import diya.model.language.Word;

public final class TestHelper {
	
	public static void testWord(Automaton automaton, Alphabet alphabet, String word, int expectedSteps, boolean expectedResult){
		if(expectedResult){
			assertTrue("Automaton should accept '"+word+"'", automaton.run(new Word(word, alphabet)));
		}else{
			assertFalse("Automaton should not accept '"+word+"'", automaton.run(new Word(word, alphabet)));
		}

		assertEquals("Stepcount for '"+word+"' should be "+expectedSteps, expectedSteps, automaton.getCurrentStepCount());
	}
	
	public static void testWord(Automaton automaton, Alphabet alphabet, String word, int expectedSteps){
		TestHelper.testWord(automaton, alphabet, word, expectedSteps, true);
	}
	
	public static void testTape(Automaton automaton, Alphabet alphabet, String inputWord, String expectedTapeContent, int expectedSteps, boolean expectedResult){
		TestHelper.testWord(automaton, alphabet, inputWord, expectedSteps, expectedResult);
		
		String output = automaton.getMainInputTapeContent();
		assertEquals("Expected '"+expectedTapeContent+"' instead of '"+output+"'", expectedTapeContent, output);
	}
}
