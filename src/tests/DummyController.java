package tests;

import java.util.concurrent.ArrayBlockingQueue;

import common.AbstractEngine;
import messaging.ContinuouslyConsumeCommand;
import messaging.ContinuouslyProduceCommand;
import messaging.Message;
import messaging.Publisher;
import messaging.events.DisplayMessage;
import messaging.events.ProduceMessage;

public class DummyController extends AbstractEngine {
	
	private Boolean running = false;
	private Boolean paused = false;
	private Boolean simThreaded;
	private Boolean viewThreaded;
	private InitiativeSetting initiative;
	private ArrayBlockingQueue<Integer> q;
	private DummyModel model;
	private DummyView view;
	private Thread modelThread;
	private Thread viewThread;
	private Publisher pub = Publisher.getInstance();
	private int bufferSize;

	private int debugCnt = 0;
	
	public DummyController(Boolean simThreaded, Boolean viewThreaded, InitiativeSetting initiative, int bufferSize) {
		this.simThreaded = simThreaded;
		this.viewThreaded = viewThreaded;
		this.initiative = initiative;
		this.bufferSize = bufferSize;
	}
	
	public void start() {
		// Make GUI changes:
		// - Disable start button (can't press again until stopped)
		// - lock sim parameter settings
		// - enable stop/pause/restart?
		
		// Instance model/view
		q = new ArrayBlockingQueue<Integer>(bufferSize);
		model = new DummyModel(q);
		view = new DummyView(q);
		
		// setup message subscriptions per initiative settings
		switch (initiative) {
		case MODEL:
			pub.subscribe(ContinuouslyProduceCommand.class, model);
			// kickstart message to the model.  After first message it will 
			// continue to provide the message to itself and fill buffer.
			pub.send(new ContinuouslyProduceCommand());
			break;

		case VIEW:
			pub.subscribe(ContinuouslyConsumeCommand.class, model);
			// the view will produce the above message any time the queue is 
			// empty.  When the model sees the event it will produce a single
			// simulation output for the view to display.
			break;

		case THIRD_PARTY:
			// This currently functions by telling the model to produce a single
			// sim result.  The controller then waits for the view to signal
			// it has displayed the data, before requesting the model produce
			// another sim result.
			pub.subscribe(ProduceMessage.class, model);
			// NOTE: no need to subscribe controller to display message since
			//       that is done in all cases for debug display counting.
			//       The process method for DisplayMessage will send all
			//       ProduceMessage's after the first below.
			pub.send(new ProduceMessage());
			break;
		}
		
		// subscribe to count presented results for debug purposes
		pub.subscribe(DisplayMessage.class, this);

		
		// Kick off threads as appropriate
		if(simThreaded) {
			modelThread = new Thread(model);
			modelThread.start();
		}
		if(viewThreaded) {
			viewThread = new Thread(view);
			viewThread.start();
		}
		
		// Kick off run loop
		run();
	}
	
	public void stop() {
		// End run loop
		running = false;
		paused = false;
		
		// Stop threads
		if(simThreaded) {
			modelThread.interrupt();
			try {
				modelThread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(viewThreaded) {
			viewThread.interrupt();
			try {
				viewThread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// remove subscriptions
		Publisher.unsubscribeAll();
		
		// destroy model/view
		model.close();
		model = null;
		view.close();
		view = null;
		
		// Make GUI changes
		
	}
	
	public void pause() {
		// make GUI updates
		// set variable to skip run loop contents
		paused = true;
	}
	
	public void restart() {
		// make GUI updates
		// set variable to NOT skip run loop contents
		paused = false;
	}
	
	public void run() {
		
		running = true;
		paused = false;
		while (running) {
			if(debugCnt >= 2) {
				running = false;
			}
			
			// Allow non-threaded components to process event queues
			if(!simThreaded) {
				model.performAction();
			}
			
			if(!viewThreaded) {
				view.performAction();
			}
			
			// Do any orchestration required for current initiative setting
			//TODO: work this out between here and in message handlers...

			while (paused) {
				// yield execution thread if nothing to process (save cpu)
				Thread.yield();
			}
		}
		
		stop();
	}

	public void process(DisplayMessage msg) {
		debugCnt++;
		// If we're in third party mode and a display was just finished, it's
		// time to request another sim output.
		if(initiative == InitiativeSetting.THIRD_PARTY) {
			pub.send(new ProduceMessage());
		}
	}

	@Override
	public void generate() {
		return;
	}

	@Override
	public <T extends Message> void dispatchMessage(T msg) {
		
		if (msg instanceof DisplayMessage) {
			process((DisplayMessage) msg);
		} else {
			System.err.printf("WARNING: No processor specified in class %s for message %s\n",
					this.getClass().getName(), msg.getClass().getName());
		}	
	}

	@Override
	public void configure(int gs, int timeStep) {
		return;
	}

	@Override
	public void close() {
		return;
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}
}
