package tests;

import java.util.concurrent.ArrayBlockingQueue;

import common.ComponentBase;

import messaging.Message;
import messaging.Publisher;
import messaging.events.DisplayMessage;
import messaging.events.NeedDisplayDataMessage;
import messaging.events.ProduceContinuousMessage;
import messaging.events.ProduceMessage;

public class DummyController extends ComponentBase {
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
			pub.subscribe(ProduceContinuousMessage.class, model);
			// kickstart message to the model.  After first message it will 
			// continue to provide the message to itself and fill buffer.
			pub.send(new ProduceContinuousMessage());
			break;

		case VIEW:
			pub.subscribe(NeedDisplayDataMessage.class, model);
			// the view will produce the above message anytime the queue is 
			// empty.  When the model sees the event it will produce a single
			// simulation output for the view to display.
			break;

		case THIRD_PARTY:
//			pub.subscribe(ProduceMessage.class, model);
//			pub.subscribe(ProduceMessage.class, model);
			// TODO: need work here, subscribe controller to produced message from model
			//       and then controller can send consume message to view.
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
	
	public void stop() throws InterruptedException {
		// End run loop
		running = false;
		paused = false;
		
		// Stop threads
		if(simThreaded) {
			modelThread.interrupt();
			modelThread.join();
		}
		if(viewThreaded) {
			viewThread.interrupt();
			viewThread.join();
		}
		
		// remove subscriptions
		
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
		Boolean queueEmpty;
		running = true;
		paused = false;
		while (running) {
			if(debugCnt >= 2) {
				running = false;
			}
			
			// Allow non-threaded components to process event queues
			if(!simThreaded) {
				model.runAutomaticActions();
				model.processMessageQueue();
			}
			if(!viewThreaded) {
				view.runAutomaticActions();
				view.processMessageQueue();
			}
			
			// Do any orchestration required for current initiative setting
			//TODO: work this out between here and in message handlers...
			

			queueEmpty = processMessageQueue();
			if (queueEmpty || paused) {
				// yield execution thread if nothing to process (save cpu)
				Thread.yield();
			}
		}
		
		try {
			stop();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void dispatchMessage(Message msg) {
		if (msg instanceof DisplayMessage) {
			process((DisplayMessage) msg);
		} else {
			System.err.printf("WARNING: No processor specified in class %s for message %s\n",
					this.getClass().getName(), msg.getClass().getName());
		}
	}

	public void process(DisplayMessage msg) {
		debugCnt++;
	}
}
