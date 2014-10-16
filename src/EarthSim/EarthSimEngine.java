package EarthSim;

import messaging.MessageListener;
import messaging.Publisher;
import messaging.events.CloseMessage;
import messaging.events.ConsumeContinuousMessage;
import messaging.events.ConsumeMessage;
import messaging.events.DisplayMessage;
import messaging.events.PauseMessage;
import messaging.events.ProduceContinuousMessage;
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
import concurrent.ThreadManager;

public final class EarthSimEngine extends AbstractEngine {
	
	private static final int DEFAULT_BUFFER_SIZE = 1;
	
	private ThreadManager manager;
	private IEngine model, view;
	
	private Initiative initiative;
	private int gs, timeStep; 
	private long presentationRate;
	
	public EarthSimEngine(boolean s, boolean p, Initiative i, int b) {
		
		if (b <= 0) b = DEFAULT_BUFFER_SIZE;
		if (b >= Integer.MAX_VALUE)
			throw new IllegalArgumentException("Invalid buffer size");
		
		initiative = i;
		
		Publisher publisher = Publisher.getInstance();
		model = new Earth();
		view = new EarthDisplayEngine();
		
		Buffer.getBuffer().create(b);
		
		// TODO more extensible possible?
		if (initiative == Initiative.PRES_THREAD)
			publisher.subscribe(ProduceContinuousMessage.class, (MessageListener) model);
		else if (initiative == Initiative.SIM_THREAD)
			publisher.subscribe(ConsumeContinuousMessage.class, (MessageListener) view);
		else {
			publisher.subscribe(ProduceMessage.class, (MessageListener) model);
			publisher.subscribe(ConsumeMessage.class, (MessageListener) view);
		}
		
		manager = ThreadManager.getManager();
			
		publisher.subscribe(StartMessage.class, (MessageListener) manager);
		publisher.subscribe(PauseMessage.class, (MessageListener) manager);
		publisher.subscribe(StopMessage.class, (MessageListener) manager);
		publisher.subscribe(ResumeMessage.class, (MessageListener) manager);
		
		publisher.subscribe(CloseMessage.class, (MessageListener) view);
		publisher.subscribe(CloseMessage.class, (MessageListener) model);
		publisher.subscribe(CloseMessage.class, (MessageListener) manager);
			
		if (initiative == Initiative.SIM_THREAD) manager.add(model);
		if (initiative == Initiative.PRES_THREAD) manager.add(view);
		manager.add(this);
		
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
	
	public void restart() {
		this.dispatchMessage(new ResumeMessage());
	}

	@Override
	public void performAction() {
		
		if (initiative != Initiative.PRES_THREAD) model.processQueue();
		else if (initiative != Initiative.SIM_THREAD) view.processQueue();
		else BufferController.getController().invoke();
		
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