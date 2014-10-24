package common;

import java.util.concurrent.ConcurrentLinkedQueue;

import messaging.Message;
import messaging.MessageListener;

public abstract class ComponentBase implements MessageListener, Runnable {

	private final ConcurrentLinkedQueue<Message> msgQueue = new ConcurrentLinkedQueue<Message>();
	
	// can be set to signal thread run loop should exit
	protected Boolean stopThread = false; 
	protected Boolean paused = false;

	public void onMessage(Message msg) {

		// enque message to be processed later
		msgQueue.add(msg);
	}

	public void processFullMessageQueue() {
		while (!processMessageQueue()) {
			// Do nothing
		}
	}

	public Boolean processMessageQueue() {

		Boolean queueEmpty = false;
		if(!paused) {
			Message msg = msgQueue.poll();
			if (msg == null) {
				queueEmpty = true;
			} else {
				dispatchMessage(msg);
			}
		}
		
		return queueEmpty;
	}

	// This method dispatches a message to the appropriate processor
	public abstract void dispatchMessage(Message msg);

	@Override
	public void run() {

		try {
			while (!Thread.currentThread().isInterrupted() && !stopThread) {
				if(!paused) {
					runAutomaticActions();
				}
				if (processMessageQueue()) {
					Thread.yield();
				}
			}
		} catch (InterruptedException e) {
			// OK
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// Used to pause a component
	public void pause(Boolean pause) {
		this.paused = pause;
	}
	
	// override this method for actions to be ran automatically
	public void runAutomaticActions() throws Exception {
	};
}
