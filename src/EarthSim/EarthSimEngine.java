package EarthSim;

import messaging.MessageListener;
import messaging.Publisher;
import messaging.events.CloseMessage;
import messaging.events.ContinuouslyConsumeMessage;
import messaging.events.ConsumeMessage;
import messaging.events.DisplayMessage;
import messaging.events.PauseMessage;
import messaging.events.ContinuouslyProduceMessage;
import messaging.events.ProduceMessage;
import messaging.events.ResumeMessage;
import messaging.events.StartMessage;
import messaging.events.StopMessage;
import simulation.Earth;
import view.EarthDisplayEngine;
import common.Buffer;
import common.BufferController;
import common.AbstractEngine;
import common.IEngine;
import common.Initiative;
import common.InitiativeHandler;
import concurrent.ThreadManager;

public final class EarthSimEngine extends AbstractEngine {
	
	private static final int DEFAULT_BUFFER_SIZE = 1;
	
	private InitiativeHandler handler;
	private ThreadManager manager;
	private IEngine model, view;
	
	private int gs, timeStep; 
	private long presentationRate;
	
	public EarthSimEngine(Initiative i, MessageListener model, MessageListener view, int b) {
		
		if (b <= 0) b = DEFAULT_BUFFER_SIZE;
		if (b >= Integer.MAX_VALUE)
			throw new IllegalArgumentException("Invalid buffer size");
		
		Publisher publisher = Publisher.getInstance();
		
		// TODO this needs to be done in GUI
//		model = new Earth();
//		view = new EarthDisplayEngine();
		
		publisher.subscribe(ProduceMessage.class, model);
		publisher.subscribe(ConsumeMessage.class, view);
		publisher.subscribe(DisplayMessage.class, view);
		
		if (i == Initiative.SIMULATION)
			handler.inject(new SimulationHandler());
		else if (i == Initiative.PRESENTATION)
			handler.inject(new ViewHandler());
		else
			handler.inject(new BufferHandler());
		
		Buffer.getBuffer().create(b);
		
		manager = ThreadManager.getManager();
			
		publisher.subscribe(StartMessage.class, manager);
		publisher.subscribe(PauseMessage.class, (MessageListener) manager);
		publisher.subscribe(StopMessage.class, (MessageListener) manager);
		publisher.subscribe(ResumeMessage.class, (MessageListener) manager);
		
		publisher.subscribe(CloseMessage.class, (MessageListener) view);
		publisher.subscribe(CloseMessage.class, (MessageListener) model);
		publisher.subscribe(CloseMessage.class, (MessageListener) manager);
			
		// TODO this needs to be done in GUI
//		if (simThreaded) manager.add((IEngine) model);
//		if (viewThreaded) manager.add((IEngine) view);
//		manager.add(this);
		
		this.gs = this.timeStep = 0;
		this.presentationRate = 0;
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			
			@Override
			public void run() {
				dispatchMessage(new CloseMessage());
			}
		});
	}
	
	// One way we could improve extensibility is to use a message packet to send values
	// Wrapper around base configure to add additional attribute setting
	public void configure(int gs, int timeStep, long presentationRate) {
		
		if (presentationRate <= 0 || presentationRate >= Long.MAX_VALUE)
			throw new IllegalArgumentException("Invalid Presentation Rate value");
		
		this.configure(gs, timeStep);
		this.presentationRate = presentationRate;
	}
	
	public void start() {
		
		model.configure(this.gs, this.timeStep);
		view.configure(this.gs, this.timeStep);
		
		this.dispatchMessage(new StartMessage());
	}
	
	public void stop() {
		this.dispatchMessage(new StopMessage());	
	}
	
	public void pause() {
		this.dispatchMessage(new PauseMessage());
	}
	
	public void resume() {
		this.dispatchMessage(new ResumeMessage());
	}

	@Override
	public void performAction() {

		// This will cause who ever has the initiative to do their stuff
		// If both engines are non threaded, this will block until the 
		handler.invoke();
		
		try {
			Thread.currentThread();
			Thread.sleep(this.presentationRate);
		} catch (InterruptedException e) {
			// do nothing
		}
		
		Publisher.getInstance().send(new DisplayMessage());
	}

	@Override
	public void generate() {
		// nothing to do
		return;
	}

	@Override
	public void configure(int gs, int timeStep) {
		
		if (gs <= 0 || gs >= Integer.MAX_VALUE)
			throw new IllegalArgumentException("Invalid Grid Spacing value");
		
		if (timeStep <= 0 || timeStep >= Integer.MAX_VALUE)
			throw new IllegalArgumentException("Invalid Time Step value");
		
		this.gs = gs;
		this.timeStep = timeStep;
	}

	@Override
	public void close() {
				
		// remove subscriptions
		Publisher.unsubscribeAll();
	}
}