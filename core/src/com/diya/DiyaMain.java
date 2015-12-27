package com.diya;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.diya.graph.Graph;

import diya.controller.handler.DiyaCommandProcessor;
import diya.controller.handler.DiyaController;
import diya.model.automata.Automaton;
import diya.model.automata.FiniteStateMachine;
import diya.model.automata.events.*;
import diya.model.language.Alphabet;
import diya.view.DiyaViewInterface;

public class DiyaMain extends ApplicationAdapter implements DiyaViewInterface{
	
	private int worldWidth;
	private int worldHeight;

	private UIStage uiStage;
	private ConstructionStage constructionStage;
	private Camera camera;
	
	private DiyaController controller;
	private Automaton automaton;
	private Graph graph;

	@Override
	public void create () {
		Alphabet alphabet = new Alphabet();
		alphabet.addSymbol("a");
		alphabet.addSymbol("b");
		automaton = new FiniteStateMachine(200, 400, alphabet);
		controller = new DiyaCommandProcessor(automaton);
		automaton.addObserver(this);

		Viewport viewport = new ExtendViewport(512, 512);
		uiStage = new UIStage(viewport, this, controller);
       	camera = new Camera(1024, 1024, viewport.getScreenWidth(), viewport.getScreenHeight());
       	constructionStage = new ConstructionStage(camera, this, controller);

        constructionStage.setDebugAll(true);
      
		graph = new Graph(worldWidth, worldHeight, this, automaton.getInputTape());
		automaton.addState("t1", true, false, 200, 200);
		//automaton.addState("t2", false, true, 400, 200);
//		automaton.addState("s1", false, false, 400, 200);
		//automaton.addTransition("t1", "t2", new String[] {"a"});
//		automaton.addTransition("t2", "s1", new String[] {"a"});
//		automaton.addTransition("s1", "t2", new String[] {"b"});
//		automaton.addTransition("t2", "t2", new String[] {"a"});
//		automaton.addTransition("t2", "t1", new String[] {"a"});
//		automaton.addTransition("s1", "t1", new String[] {"a"});
//		automaton.addTransition("t1", "s1", new String[] {"a"});
		/*graph.addNode(automaton.addState("s2", false, false, 300, 100));
		graph.addEdge(automaton.addTransition("s2", "s1", new String[] {"a"}));
		graph.addEdge(automaton.addTransition("s1", "test", new String[] {"a"}));*/
       	constructionStage.addActor(graph);
       	ConstructionMenu menu = new ConstructionMenu(new String[]{"a", "b"});
       	constructionStage.setMenu(menu);
       	graph.toBack();
		
       	InputMultiplexer inputMultiplexer = new InputMultiplexer(uiStage, new GestureDetector(constructionStage), constructionStage);
		Gdx.input.setInputProcessor(inputMultiplexer);
	}

	@Override
	public void render () {
        constructionStage.act();
        uiStage.act();

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
		text = text.toLowerCase();
		
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
			case StepDone : graph.setHighlightedStates(((StepDoneEvent) event).getStates());
				break;
			case RunFinished : graph.accepted(((RunFinishedEvent) event).hasAccepted());
				break;
			case AutomatonReset : graph.automatonReseted();
				break;
			default:
				break;
		}
	}
}
