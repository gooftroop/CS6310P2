package EarthSim;

import messaging.MessageListener;
import messaging.Publisher;
import messaging.events.CloseMessage;
import messaging.events.ConsumeMessage;
import messaging.events.DisplayMessage;
import messaging.events.PauseMessage;
import messaging.events.ProduceMessage;
import messaging.events.ResumeMessage;
import messaging.events.StartMessage;
import messaging.events.StopMessage;
import messaging.events.UpdatedMessage;
import simulation.Earth;
import view.EarthDisplayEngine;
import common.AbstractEngine;
import common.Buffer;
import common.BufferController;
import common.IEngine;
import common.State;
import common.InitiativeHandler;
import common.SimulationHandler;
import common.ViewHandler;
import concurrent.ProcessManager;

public final class EarthSimEngine extends AbstractEngine {
	
	private static final int DEFAULT_BUFFER_SIZE = 1;
	
	private InitiativeHandler handler;
	private ProcessManager manager;
	private Publisher publisher;
	private IEngine model, view; // TODO is IEngine useful?? maybe seperate this stuff better
	
	// TODO I want to refactor this - this needs to be done cause this is ugly
	private boolean simThreaded;
	private State i;
	 
	private long presentationRate;
	
	public EarthSimEngine(State i, boolean simThreaded, boolean viewThreaded, int b) {
		
		if (b <= 0) b = DEFAULT_BUFFER_SIZE;
		if (b >= Integer.MAX_VALUE)
			throw new IllegalArgumentException("Invalid buffer size");
		
		this.simThreaded = simThreaded; // see above note
		this.i = i; // see above note
		
		Buffer.getBuffer().create(b);
		
		model = new Earth(simThreaded);
		view = new EarthDisplayEngine(viewThreaded);
		
		manager = ProcessManager.getManager();
		
		publisher = Publisher.getInstance();
		publisher.subscribe(ProduceMessage.class, (MessageListener) model); // TODO casting is bad
		publisher.subscribe(ConsumeMessage.class, (MessageListener) view);
		publisher.subscribe(DisplayMessage.class, (MessageListener) view);
			
		publisher.subscribe(StartMessage.class, manager);
		publisher.subscribe(PauseMessage.class, (MessageListener) manager);
		publisher.subscribe(StopMessage.class, (MessageListener) manager);
		publisher.subscribe(ResumeMessage.class, (MessageListener) manager);
		
		publisher.subscribe(CloseMessage.class, (MessageListener) view);
		publisher.subscribe(CloseMessage.class, (MessageListener) model);
		publisher.subscribe(CloseMessage.class, (MessageListener) manager);
		
		if (i == State.SIMULATION)
			handler = new InitiativeHandler(new SimulationHandler((Class<? extends MessageListener>) model.getClass()));
		else if (i == State.PRESENTATION)
			handler = new InitiativeHandler(new ViewHandler((Class<? extends MessageListener>) view.getClass()));
		else
			handler = new InitiativeHandler(new BufferController());
		
		publisher.subscribe(UpdatedMessage.class, handler);
		publisher.subscribe(StartMessage.class, handler);
			
		if (simThreaded) manager.add((IEngine) model);
		if (viewThreaded) manager.add((IEngine) view);
		manager.add(this);
		
		this.presentationRate = 0;
	}
	
	// One way we could improve extensibility is to use a message packet to send values
	// Wrapper around base configure to add additional attribute setting
	public void configure(int gs, int timeStep, long presentationRate) {
		
		if (presentationRate <= 0 || presentationRate >= Long.MAX_VALUE)
			throw new IllegalArgumentException("Invalid Presentation Rate value");
		
		if (presentationRate < 1000)
			this.presentationRate =  presentationRate * 1000;
		else
			this.presentationRate = presentationRate;
		
		this.configure(gs, timeStep);
	}
	
	@Override
	public void configure(int gs, int timeStep) {
		
		if (gs <= 0 || gs >= Integer.MAX_VALUE)
			throw new IllegalArgumentException("Invalid Grid Spacing value");
		
		if (timeStep <= 0 || timeStep >= Integer.MAX_VALUE)
			throw new IllegalArgumentException("Invalid Time Step value");
		
		model.configure(gs, timeStep);
		view.configure(gs, timeStep);
	}

	@Override
	public void performAction() {

		if (!simThreaded && i == State.SIMULATION)
			publisher.send(new ProduceMessage());
		
		if (i == State.MASTER)
			publisher.send(new UpdatedMessage(null)); // There has got be a way of not using classes
		
		try {
			Thread.currentThread();
			Thread.sleep(this.presentationRate);
		} catch (InterruptedException e) {
			// do nothing
		}
		
		publisher.send(new DisplayMessage());
	}

	@Override
	public void generate() {
		// nothing to do
		return;
	}

	@Override
	public void close() {
		handler = null;
	}
}