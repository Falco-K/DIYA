package diya.model.automata.events;

public enum AutomatonEventType {
	StateAdded,
	StateRemoved,
	StateUpdated,
	TransitionAdded,
	TransitionRemoved,
	TransitionUpdated,
	StepDone,
	RunFinished,
	AutomatonReset,
	InvalidAutomaton,
	TapeUpdated;
}
