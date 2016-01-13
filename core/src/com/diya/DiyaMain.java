package com.diya;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.utils.viewport.*;
import com.diya.graph.Graph;

import diya.controller.handler.DiyaCommandProcessor;
import diya.controller.handler.DiyaController;
import diya.model.automata.Automaton;
import diya.model.automata.FiniteStateMachine;
import diya.model.automata.TuringMachine;
import diya.model.automata.events.*;
import diya.model.language.Alphabet;
import diya.model.language.AlphabetType;
import diya.view.DiyaViewInterface;

public class DiyaMain extends ApplicationAdapter implements DiyaViewInterface{
	
	private int worldWidth;
	private int worldHeight;

	private UIStage uiStage;
	private ConstructionStage constructionStage;
	private ConstructionMenu menu;
	private CameraWrapper camera;
	
	private DiyaController controller;
	private Automaton automaton;
	private Graph graph;

	boolean firstStart = true;
	
	@Override
	public void create () {

		worldWidth = 1024;
		worldHeight = 1024;

       	ShapeRenderer shapeRenderer = new ShapeRenderer();
       	shapeRenderer.setAutoShapeType(true);

       	createEmptyFiniteStateMachine(shapeRenderer);
		//createFiniteStateMachine(shapeRenderer);
       	//createTuringMachine(shapeRenderer);
       	
		Viewport uiViewport = new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		uiStage = new UIStage(uiViewport, this, controller);
       	camera = new CameraWrapper(worldWidth, worldHeight, uiViewport.getScreenWidth(), uiViewport.getScreenHeight());
		
       	Viewport constructionViewport = new ExtendViewport(uiViewport.getScreenWidth(), uiViewport.getScreenHeight(), camera.getCamera());
       	constructionStage = new ConstructionStage(camera, this, controller, shapeRenderer, constructionViewport);
       	
       	constructionStage.setMenu(menu);
       	constructionStage.addGraph(graph);

       	InputMultiplexer inputMultiplexer = new InputMultiplexer(uiStage, new GestureDetector(constructionStage), constructionStage);
		Gdx.input.setInputProcessor(inputMultiplexer);
	}
	
	private void createEmptyFiniteStateMachine(ShapeRenderer shapeRenderer){
		Alphabet alphabet = new Alphabet();
		alphabet.addSymbol("0");
		alphabet.addSymbol("1");
		automaton = new FiniteStateMachine(416, 100, alphabet);
		graph = new Graph(worldWidth, worldHeight, this, automaton.getMainInputTape(), shapeRenderer);
		controller = new DiyaCommandProcessor(automaton);
		automaton.addObserver(this);
		
		Alphabet[] transitionAlphabet = new Alphabet[1];
       	transitionAlphabet[0] = automaton.getAlphabet(AlphabetType.INPUT);
    	menu = new ConstructionMenu(alphabet, true, transitionAlphabet);
	}
	
	private void createFiniteStateMachine(ShapeRenderer shapeRenderer){		
		Alphabet alphabet = new Alphabet();
		alphabet.addSymbol("0");
		alphabet.addSymbol("1");
		automaton = new FiniteStateMachine(416, 100, alphabet);
		graph = new Graph(worldWidth, worldHeight, this, automaton.getMainInputTape(), shapeRenderer);
		controller = new DiyaCommandProcessor(automaton);
		automaton.addObserver(this);
		
		automaton.addState("t1", true, false, 200, 200);
		automaton.addState("t2", false, true, 400, 200);
		
		automaton.addTransition("t1", "t2", null);
		automaton.addTransition("t1", "t1", new String[] {"0"});
		automaton.addTransition("t2", "t2", new String[] {"1"});
		
		Alphabet[] transitionAlphabet = new Alphabet[1];
       	transitionAlphabet[0] = automaton.getAlphabet(AlphabetType.INPUT);
       	menu = new ConstructionMenu(alphabet, true, transitionAlphabet);
	}
	
	private void createTuringMachine(ShapeRenderer shapeRenderer){	
		Alphabet alphabet = new Alphabet();
		alphabet.addSymbol("0");
		alphabet.addSymbol("1");
		automaton = new TuringMachine(200, 300, alphabet, "#");
		graph = new Graph(worldWidth, worldHeight, this, automaton.getMainInputTape(), shapeRenderer);
		controller = new DiyaCommandProcessor(automaton);
		automaton.addObserver(this);
		
		automaton.addState("t1", true, false, 200, 200);
		automaton.addState("t2", false, false, 400, 200);
		automaton.addState("t3", false, true, 600, 200);
		
		automaton.addTransition("t1", "t1", new String[] {"0/R"});
		automaton.addTransition("t1", "t2", new String[] {"#/R"});
		automaton.addTransition("t2", "t3", new String[] {"#/0"});
		
		Alphabet[] transitionAlphabet = new Alphabet[2];
       	transitionAlphabet[0] = automaton.getAlphabet(AlphabetType.TAPE);
      	transitionAlphabet[1] = Alphabet.combine(automaton.getAlphabet(AlphabetType.TAPE), automaton.getAlphabet(AlphabetType.MOVEMENT));
       	menu = new ConstructionMenu(alphabet, false, transitionAlphabet);
	}

	@Override
	public void render () {
		if(firstStart){
			camera.moveCamera(worldWidth/2-Gdx.graphics.getWidth()/2, -100);
			firstStart = false;
		}
		
        constructionStage.act();
        uiStage.act();

        Gdx.gl.glLineWidth(2f);
        Gdx.gl.glClearColor(0.95f, 0.95f, 0.95f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        constructionStage.draw();
        uiStage.draw();
	}

    @Override
    public void resize(int width, int height) {
    	constructionStage.getViewport().update(width, height, true);
    	uiStage.getViewport().update(width, height, true);
    }

    @Override
    public void resume() {
    }

    @Override
    public void pause() {
    }
    
    @Override
    public void dispose() {
        uiStage.dispose();
        constructionStage.dispose();
    }
    
	@Override
	public void sendCommand(String text) {	
		if(text.startsWith("zoom")){
			camera.setZoom(Float.valueOf(text.substring(4, text.length())));
		}
		else{
			controller.actionPerformed(this, text);
		}
	}

	@Override
	public void message(String message) {
		uiStage.printMessage(">"+message);
	}

	@Override
	public void onNotify(AutomatonEvent event) {
		switch(event.getType()){
			case StateAdded : graph.addNode(((StateAddedEvent) event).getState()); 
				break;
			case StateRemoved : graph.removeNode(((StateRemovedEvent) event).getState());
				break;
			case TransitionAdded : graph.addEdge(((TransitionAddedEvent) event).getTransition());
				break;
			case TransitionRemoved : graph.removeEdge(((TransitionRemovedEvent) event).getTransition());
				break;
			case TransitionUpdated: graph.updateEdge(((TransitionUpdatedEvent) event).getTransition());
				break;
			case StateUpdated : graph.updateNode(((StateUpdatedEvent) event).getState());
				break;
			case StepDone : graph.animateTransition(((StepDoneEvent) event).getStates(), ((StepDoneEvent) event).getTransitions(), ((StepDoneEvent) event).getStepNumber());
				break;
			case RunFinished : graph.accepted(((RunFinishedEvent) event).hasAccepted());
				break;
			case AutomatonReset : graph.automatonReseted();
				break;
			case TapeUpdated : graph.tapeUpdated();
				break;
			default:
				break;
		}
	}
}
