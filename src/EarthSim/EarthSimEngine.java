package EarthSim;

import messaging.MessageListener;
import messaging.Publisher;
import messaging.events.ConsumeContinuousMessage;
import messaging.events.DisplayMessage;
import messaging.events.PauseMessage;
import messaging.events.ProduceContinuousMessage;
import messaging.events.ResumeMessage;
import messaging.events.StartMessage;
import messaging.events.StopMessage;
import simulation.Earth;
import view.EarthDisplayEngine;

import common.Buffer;
import common.BufferController;
import common.ComponentBase;
import common.IComponent;
import common.Initiative;

import concurrent.ThreadManager;

public final class EarthSimEngine extends ComponentBase {
	
	private static final int DEFAULT_BUFFER_SIZE = 1;
	
	private ThreadManager manager;
	private IComponent model, view;
	
	private Initiative initiative;
	private boolean isPaused, isStopped, isRunning;
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
		if (initiative == Initiative.PRES_THREAD) {
			publisher.subscribe(ProduceContinuousMessage.class, (MessageListener) model);
		} else if (initiative == Initiative.SIM_THREAD) {
			publisher.subscribe(ConsumeContinuousMessage.class, (MessageListener) view);
		} else Buffer.addCallback(new BufferController());
		
			
		manager = new ThreadManager();
			
		publisher.subscribe(StopMessage.class, (MessageListener) model);
		publisher.subscribe(StopMessage.class, (MessageListener) view);
			
		publisher.subscribe(PauseMessage.class, (MessageListener) view);
		publisher.subscribe(PauseMessage.class, (MessageListener) model);
		publisher.subscribe(ResumeMessage.class, (MessageListener) view);
		publisher.subscribe(ResumeMessage.class, (MessageListener) model);
			
		if (initiative == Initiative.SIM_THREAD) manager.add(model);
		if (initiative == Initiative.PRES_THREAD) manager.add(view);
		manager.add(this);
		
		this.isPaused = this.isStopped = this.isRunning = false;
		this.gs = this.timeStep = 0;
		this.presentationRate = 0;
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
		
		manager.start();
		
		this.dispatchMessage(new StartMessage());
		this.isRunning = true;
	}
	
	public void stop() {
		
		this.dispatchMessage(new StopMessage());	
		
		this.isStopped = true;
		this.isRunning = false;
		
		Thread.currentThread().interrupt();
	}
	
	public void pause() {
		this.dispatchMessage(new PauseMessage());
		this.isPaused = true;
	}
	
	public void restart() {
		this.dispatchMessage(new ResumeMessage());
		this.isPaused = false;
	}
	
	public boolean isPaused() {
		return this.isPaused;
	}
	
	public boolean isRunning() {
		return this.isRunning;
	}
	
	public boolean isStopped() {
		return this.isStopped;
	}

	@Override
	public void performAction() {
		
		while(this.isPaused) { /* block */ }
		
		if (initiative != Initiative.PRES_THREAD) model.performAction();
		if (initiative != Initiative.SIM_THREAD) view.performAction();
		
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
		
		this.stop();
		manager.stop();
		
		model.close();
		view.close();
		model = view = null;
				
		// remove subscriptions
		Publisher.unsubscribeAll();
	}
}